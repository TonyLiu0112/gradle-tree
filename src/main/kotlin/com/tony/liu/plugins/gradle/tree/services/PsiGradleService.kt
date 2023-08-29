package com.tony.liu.plugins.gradle.tree.services

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.findDocument
import com.tony.liu.plugins.gradle.tree.context.FileContext
import org.apache.commons.lang3.StringUtils
import org.apache.commons.lang3.StringUtils.contains
import java.io.File
import javax.swing.tree.DefaultMutableTreeNode


private const val virtualNode: String = "virtualRoot"

@Service(Service.Level.PROJECT)
class PsiGradleService {

    fun exclude(project: Project, virtualFile: VirtualFile, selectNode: DefaultMutableTreeNode) {
        var parent: javax.swing.tree.TreeNode? = selectNode.parent ?: return

        val parentNode = parent as DefaultMutableTreeNode
        if (virtualNode == parentNode.userObject.toString()) {
            return
        }

        var rootNode = getRootNode(selectNode)

        val fileDir = File(virtualFile.path).parent
        val selectMetadata = getNodeArtifactId(selectNode, fileDir)
        val rootMetadata = getNodeArtifactId(rootNode, fileDir)

        var text = virtualFile.findDocument()!!.text

        var lines = text.split("\n")

        var newText = ""
        val groupArtifact = rootMetadata.groupId + ":" + rootMetadata.artifactId
        for (element in lines) {
            var line = element

            if (contains(line, groupArtifact)) {
                line += " exclude(group: '" + selectMetadata.groupId + "', module: '" + selectMetadata.artifactId + "')"
            }

            newText += line + "\n"
        }

        ApplicationManager.getApplication().runWriteAction {
            virtualFile.findDocument()!!.setText(newText)
        }

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
        return Metadata(treeNode!!.groupId, treeNode!!.artifactId)
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
        val artifactId: String
    )

}