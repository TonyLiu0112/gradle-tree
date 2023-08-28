package org.jetbrains.plugins.template

import com.tony.support.GradleTreeParser
import com.tony.support.model.TreeNode
import org.apache.commons.lang3.StringUtils
import org.gradle.tooling.internal.consumer.ConnectorServices
import java.io.ByteArrayOutputStream
import java.io.File

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

fun main() {
    // 解析树
    execGradleCommand()

    println(map)
}

val map = mutableMapOf<String, TreeNode>()

private fun execGradleCommand() {
    val connection = ConnectorServices.createConnector()
        .forProjectDirectory(File("/Users/tony/Downloads/hahahehe")) // 您的插件所在的项目目录
        .connect()

    val build = connection.newBuild()
    build.forTasks("dependencies")

    val outputStream = ByteArrayOutputStream()

    // 设置标准输出和错误输出
    build.setStandardOutput(outputStream)
    build.setStandardError(System.err)
    build.run()

    val result = String(outputStream.toByteArray())

    var splitItem = result.split("\n")

    var inFragement = false
    var fragments = mutableListOf<String>()
    var fragmentName = ""

    splitItem.forEach { line ->
        println(line)

        if (inFragement) {
            if (StringUtils.isEmpty(line)) {

                val gradleTreeParser = GradleTreeParser()
                val treeNode = gradleTreeParser.convert2Tree(fragments)
                map[fragmentName] = treeNode

                inFragement = false
                fragments = mutableListOf()
                fragmentName = ""
            } else {
                fragments.add(line)
            }
        }

        if (line.startsWith(compileClasspath)) {
            inFragement = true
            fragmentName = compileClasspath
        } else if (line.startsWith("testCompileClasspath")) {
            inFragement = true
            fragmentName = "testCompileClasspath"
        }

    }

}
