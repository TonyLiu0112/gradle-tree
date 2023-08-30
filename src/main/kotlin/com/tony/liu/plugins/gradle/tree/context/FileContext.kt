package com.tony.liu.plugins.gradle.tree.context

import com.tony.support.GradleTreeParser
import com.tony.support.model.TreeNode
import org.apache.commons.lang3.StringUtils
import org.apache.commons.lang3.StringUtils.contains
import org.apache.commons.lang3.StringUtils.isEmpty
import org.gradle.tooling.internal.consumer.ConnectorServices
import java.io.ByteArrayOutputStream
import java.io.File
import javax.swing.tree.DefaultMutableTreeNode

private const val startTag: String = "+---"
private const val endTag: String = "\\---"
private const val arrow: String = "->";
private const val configDependency: String = "(c)"
private const val transitiveDependency: String = "(*)"
private const val space: String = "    "

private const val compileClasspath: String = "compileClasspath"
private const val compileOnly: String = "compileOnly"
private const val default: String = "default"
private const val developmentOnly: String = "developmentOnly"
private const val implementation: String = "implementation"
private const val mainSourceElements: String = "mainSourceElements"

private val all_element: Array<String> = arrayOf(
    "compileClasspath",
    "testCompileClasspath",
    "compileOnly",
    "developmentOnly",
    "implementation",
    "runtimeOnly",
    "testCompileOnly",
    "testImplementation",
    "testRuntimeOnly"
)
private val classpaths: Array<String> = arrayOf("compileClasspath", "testCompileClasspath")
private val scopes: Array<String> = arrayOf(
    "compileOnly",
    "developmentOnly",
    "implementation",
    "runtimeOnly",
    "testCompileOnly",
    "testImplementation",
    "testRuntimeOnly"
)

class FileContext {

    companion object NodeContext {

        val newLineSymbol: String = createNewLineSymbol()

        private fun createNewLineSymbol(): String {
            if (System.getProperties().getProperty("os.name").toUpperCase().contains("WINDOWS")) {
                return "\r\n"
            }  else {
                return "\n"
            }
        }

        val FILE_CONTEXT_MAP = mutableMapOf<String, TreeContext>()

        fun init(filePath: String) {
            if (FILE_CONTEXT_MAP[filePath] != null) {
                return
            }

            refresh(filePath)
        }

        fun refresh(filePath: String) {
            val treeContext = TreeContext()
            execGradleCommand(filePath, treeContext)

            val virtualRoot = DefaultMutableTreeNode("virtualRoot")

            for (classpath in classpaths) {
                createTreeView(
                    treeContext, treeContext.TREE_NODE[classpath]!!, virtualRoot, ""
                )
            }

            treeContext.TREE_VIEW = virtualRoot
            FILE_CONTEXT_MAP[filePath] = treeContext
        }

        private fun createTreeView(
            treeContext: TreeContext, node: TreeNode, rootNode: DefaultMutableTreeNode, scope: String
        ): DefaultMutableTreeNode {
            return if (node.deep == -1) {

                for (child in node.children) {
                    if (isProcessedArtifactId(rootNode, child)) {
                        continue
                    }
                    child.scope = getScope(treeContext, child)
                    createTreeView(treeContext, child, rootNode, child.scope)
                }
                rootNode
            } else {
                val treeNode = DefaultMutableTreeNode(
                    node.groupId + " : " + node.artifactId + " : " + node.definitionVersion + " [" + scope + "] " + omittedWith(
                        node
                    )
                )
                treeContext.ARTIFACT_TEXT_MAP[node.artifactId] = node.groupId + " : " + node.artifactId
                treeContext.ARTIFACT_NODES_MAP.put(node.artifactId, node)
                treeContext.TREE_METADATA[node.artifactId] = node
                rootNode.add(treeNode)
                for (child in node.children) {
                    child.scope = scope
                    createTreeView(treeContext, child, treeNode, scope)
                }
                treeNode
            }
        }

        private fun isProcessedArtifactId(rootNode: DefaultMutableTreeNode, child: TreeNode): Boolean {
            for (i in 0 until rootNode.childCount) {
                if (contains(
                        (rootNode.getChildAt(i) as DefaultMutableTreeNode).userObject.toString(),
                        child.artifactId
                    )
                ) {
                    return true
                }
            }
            return false
        }

        private fun getScope(treeContext: TreeContext, child: TreeNode): String {
            for (scope in scopes) {
                val treeNode = treeContext.TREE_NODE[scope]
                if (treeNode == null || treeNode.children.isEmpty()) {
                    continue
                }

                for (scopeNode in treeNode.children) {
                    if (contains(scopeNode.artifactId, child.artifactId)) {
                        return scope
                    }
                }
            }
            return "unknown"
        }

        private fun omittedWith(node: TreeNode): String {
            if (node.version == node.definitionVersion) {
                return ""
            }

            return " (omitted with: " + node.version + ")"
        }

        private fun execGradleCommand(filePath: String, treeContext: TreeContext) {
            val connection = ConnectorServices.createConnector().forProjectDirectory(File(filePath)).connect()

            val build = connection.newBuild()
            build.forTasks("dependencies")

            val outputStream = ByteArrayOutputStream()

            build.setStandardOutput(outputStream)
            build.setStandardError(System.err)
            build.run()

            val result = String(outputStream.toByteArray())

            val splitItem = result.split(newLineSymbol)

            var inFragement = false
            var fragments = mutableListOf<String>()
            var fragmentName = ""

            splitItem.forEach { line ->
                if (inFragement) {
                    if (isEmpty(line)) {

                        val gradleTreeParser = GradleTreeParser()
                        val treeNode = gradleTreeParser.convert2Tree(fragments)
                        treeContext.TREE_NODE[fragmentName] = treeNode

                        inFragement = false
                        fragments = mutableListOf()
                        fragmentName = ""
                    } else {
                        fragments.add(line)
                    }
                } else {
                    for (fragment in all_element) {
                        if (line.startsWith(fragment)) {
                            inFragement = true
                            fragmentName = fragment
                            break
                        }
                    }
                }
            }

        }

    }

}