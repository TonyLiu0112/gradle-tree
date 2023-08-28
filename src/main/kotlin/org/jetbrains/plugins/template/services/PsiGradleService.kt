package org.jetbrains.plugins.template.services

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.findDocument
import com.intellij.psi.PsiManager
import com.intellij.psi.util.PsiTreeUtil
import com.tony.support.model.TreeNode
import org.apache.commons.lang3.StringUtils
import org.apache.commons.lang3.StringUtils.contains
import org.jetbrains.plugins.groovy.lang.psi.api.statements.expressions.GrMethodCall
import org.jetbrains.plugins.template.context.FileContext
import java.io.File


@Service(Service.Level.PROJECT)
class PsiGradleService {

    fun exclude(project: Project, virtualFile: VirtualFile, selectNode: String) {
        var text = virtualFile.findDocument()!!.text

        var lines = text.split("\n")

        var newText = ""
        val metadata = getGroupIdArtifactId(File(virtualFile.path).parent, selectNode)
        val groupArtifact = metadata.groupId + ":" + metadata.artifactId
        for (element in lines) {
            var line = element

            if (contains(line, groupArtifact)) {
                line += " exclude(group: '" + metadata.excludeGroupId + "', module: '" + metadata.excludeArtifactId + "')"
            }

            newText += line + "\n"
        }

        ApplicationManager.getApplication().runWriteAction {
            virtualFile.findDocument()!!.setText(newText)
        }

        val psiFile = PsiManager.getInstance(project).findFile(virtualFile) ?: return

        val elements = PsiTreeUtil.collectElements(psiFile) { element ->
            println(element)
            element is GrMethodCall
        }

        println(elements)

    }

    private fun getGroupIdArtifactId(fileDir: String, selectNode: String): Metadata {
        var groupArtifact = StringUtils.substringBefore(selectNode, " [").trim()
        var artifactId: String
        if (StringUtils.countMatches(groupArtifact, ":") == 1) {
            artifactId = groupArtifact.split(":")[0].trim()
        } else {
            artifactId = groupArtifact.split(":")[1].trim()
        }

        val treeNode = FileContext.FILE_CONTEXT_MAP[fileDir]!!.TREE_METADATA[artifactId]
        val rootNode = getRootNode(treeNode)

        val fullText = FileContext.FILE_CONTEXT_MAP[fileDir]!!.ARTIFACT_TEXT_MAP[artifactId]
        val items = fullText!!.trim().split(":")

        return Metadata(rootNode.groupId, rootNode.artifactId, items[0], artifactId)
    }

    private fun getRootNode(treeNode: TreeNode?): TreeNode {
        if (treeNode!!.parent != null && treeNode.parent.deep != -1) {
            return getRootNode(treeNode.parent)
        }
        return treeNode
    }

    private data class Metadata(
        val groupId: String,
        val artifactId: String,
        val excludeGroupId: String,
        val excludeArtifactId: String
    )

}