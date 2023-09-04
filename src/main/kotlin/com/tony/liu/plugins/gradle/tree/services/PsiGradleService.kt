package com.tony.liu.plugins.gradle.tree.services

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.findDocument
import com.tony.liu.plugins.gradle.tree.context.FileContext
import org.apache.commons.lang3.StringUtils
import org.apache.commons.lang3.StringUtils.contains
import org.apache.commons.lang3.StringUtils.isNotBlank
import java.io.File
import javax.swing.tree.DefaultMutableTreeNode


private const val virtualNode: String = "virtualRoot"

@Service(Service.Level.PROJECT)
class PsiGradleService {

    fun exclude(
        project: Project,
        virtualFile: VirtualFile,
        selectNode: DefaultMutableTreeNode,
        fileType: Int
    ): Boolean {
        val parent: javax.swing.tree.TreeNode = selectNode.parent ?: return false

        val parentNode = parent as DefaultMutableTreeNode
        if (virtualNode == parentNode.userObject.toString()) {
            return false
        }

        val rootNode = getRootNode(selectNode)

        val fileDir = File(virtualFile.path).parent
        val selectMetadata = getNodeArtifactId(selectNode, fileDir)
        val rootMetadata = getNodeArtifactId(rootNode, fileDir)

        val text = virtualFile.findDocument()!!.text

        val lines = text.split("\n")

        var newText = ""
        val groupArtifact = rootMetadata.groupId + ":" + rootMetadata.artifactId
        var changed = false
        val skipLines: ArrayList<Int> = arrayListOf()

        for (i in lines.indices) {
            if (skipLines.contains(i)) {
                continue
            }

            val line = lines[i]

            var newLine = line

            if (contains(newLine, groupArtifact) && contains(newLine, rootMetadata.scope)) {
                if (fileType == 0) {
                    // groovy format
                    newLine += " exclude(group: '" + selectMetadata.groupId + "', module: '" + selectMetadata.artifactId + "')"
                } else {
                    // kotlin format
                    val ktsExcludeBlock = getKtsExcludeBlock(lines, i, selectMetadata)
                    skipLines.addAll(ktsExcludeBlock.skipLines)
                    if (isNotBlank(ktsExcludeBlock.str)) {
                        newLine = ktsExcludeBlock.str
                    }
                }
                changed = true
            }

            if (i != lines.size - 1) {
                newText += newLine + "\n"
            }
        }

        if (changed) {
            ApplicationManager.getApplication().runWriteAction {
                virtualFile.findDocument()!!.setText(newText)
            }
        }

        return changed
    }

    private fun getKtsExcludeBlock(lines: List<String>, lineNum: Int, selectMetadata: Metadata): LineMeta {
        var newLine = ""
        val skipLines: ArrayList<Int> = arrayListOf()
        val currentLine = lines[lineNum]
        val space = currentLine.takeWhile { it == ' ' }
        if (!currentLine.contains("{") && !lines[lineNum + 1].contains("{")) {
            newLine += "$currentLine {\n"
            newLine += space + "    exclude(\"" + selectMetadata.groupId + "\", \"" + selectMetadata.artifactId + "\")\n"
            newLine += "$space}"
        } else {
            // 获取已存在exclude
            val lineMeta = getExcludes(space, lines, lineNum)
            skipLines.addAll(lineMeta.skipLines)
            var excludeBlockLine = lineMeta.str
            excludeBlockLine += space + "    exclude(\"" + selectMetadata.groupId + "\", \"" + selectMetadata.artifactId + "\")\n"
            newLine += StringUtils.substringBefore(currentLine, "{") + " {\n"
            newLine += excludeBlockLine
            newLine += "$space}"
        }

        return LineMeta(newLine, skipLines)
    }

    private fun getExcludes(space: String, lines: List<String>, lineNum: Int): LineMeta {
        var exists = ""
        val skipLines: ArrayList<Int> = arrayListOf()
        for (i in lineNum until lines.size) {
            skipLines.add(i)

            val line = lines[i]

            if (line.contains("}")) {
                break
            }

            val excludeList = StringUtils.substringsBetween(line, "exclude(", ")")
            if (excludeList == null || excludeList.isEmpty()) {
                continue
            }

            for (s in excludeList) {
                exists += "$space    exclude($s)\n"
            }

        }
        return LineMeta(exists, skipLines)
    }

    private fun getNodeArtifactId(node: DefaultMutableTreeNode, fileDir: String): Metadata {
        val groupArtifact = StringUtils.substringBefore(node.userObject.toString(), " [").trim()
        val artifactId: String =
            if (StringUtils.countMatches(groupArtifact, ":") == 1) {
                groupArtifact.split(":")[0].trim()
            } else {
                groupArtifact.split(":")[1].trim()
            }
        val treeNode = FileContext.FILE_CONTEXT_MAP[fileDir]!!.TREE_METADATA[artifactId]
        return Metadata(treeNode!!.groupId, treeNode.artifactId, treeNode.scope)
    }

    private fun getRootNode(treeNode: DefaultMutableTreeNode): DefaultMutableTreeNode {
        val parentNode = treeNode.parent as DefaultMutableTreeNode
        if (virtualNode == parentNode.userObject.toString()) {
            return treeNode
        }
        return getRootNode(parentNode)
    }

    private data class Metadata(
        val groupId: String,
        val artifactId: String,
        val scope: String
    )

    private data class LineMeta(val str: String, val skipLines: List<Int>)

}