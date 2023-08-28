package org.jetbrains.plugins.template.editWindow

import com.intellij.ui.JBColor
import org.apache.commons.lang3.StringUtils
import org.jetbrains.plugins.template.context.FileContext
import org.jetbrains.plugins.template.utils.NodeTextUtils
import java.awt.Color
import java.awt.Component
import javax.swing.JTree
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.DefaultTreeCellRenderer

private const val OMITTED = "omitted with"

private val greenScope: Array<String> = arrayOf("[testCompileOnly]", "[testImplementation]", "[testRuntimeOnly]")
private val purpleScope: Array<String> = arrayOf("[runtimeOnly]")
private val yellowScope: Array<String> = arrayOf("[compileOnly]", "[developmentOnly]")

private val transparent: Color = Color(0, 0, 0, 0)

private data class ScopeColor(val color: Color, val scops: Array<String>)

private val scopeColorList: Array<ScopeColor> =
    arrayOf(
        ScopeColor(Color(30, 157, 30), greenScope),
        ScopeColor(Color(195, 170, 222), purpleScope),
        ScopeColor(Color(194, 190, 23), yellowScope)
    )

class LeftTreeCellRenderer(private val searchKey: String, private val dir: String) : DefaultTreeCellRenderer() {

    override fun getTreeCellRendererComponent(
        tree: JTree?,
        value: Any?,
        sel: Boolean,
        expanded: Boolean,
        leaf: Boolean,
        row: Int,
        hasFocus: Boolean
    ): Component {
        super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus)
        icon = null

        val node = value as DefaultMutableTreeNode
        val nodeText = node.toString()

        val cleanScopeNodeText = cleanScope(nodeText)

        if (StringUtils.isNotBlank(searchKey)
            && (StringUtils.contains(cleanScopeNodeText, searchKey) || groupContainsKeyword(
                cleanScopeNodeText,
                searchKey
            ))
        ) {
            setBackgroundSelectionColor(DefaultTreeCellRenderer().backgroundSelectionColor)
            setBackgroundNonSelectionColor(JBColor.CYAN)
            setBackground(JBColor.CYAN)
            setForeground(getTextSelectionColor());
        } else {
            val defaultTreeCellRenderer = DefaultTreeCellRenderer()
            setBackgroundSelectionColor(defaultTreeCellRenderer.backgroundSelectionColor)
            setForeground(getTextSelectionColor());
            setBackgroundNonSelectionColor(transparent)
            setBackground(transparent)
        }

        if (setForegroundByScope(nodeText)) {
            return this
        } else if (StringUtils.contains(nodeText, OMITTED)) {
            setForeground(JBColor.RED)
        }

        return this
    }

    private fun setForegroundByScope(nodeText: String): Boolean {
        for (scopeColor in scopeColorList) {
            for (scope in scopeColor.scops) {
                if (StringUtils.contains(nodeText, scope)) {
                    setForeground(scopeColor.color)
                    return true
                }
            }
        }
        return false
    }

    private fun groupContainsKeyword(nodeText: String, searchKey: String): Boolean {
        val artifactId = NodeTextUtils.getArtifactId(nodeText)
        val fullText = FileContext.FILE_CONTEXT_MAP[dir]!!.ARTIFACT_TEXT_MAP.getOrDefault(artifactId, "")
        return StringUtils.contains(fullText, searchKey)
    }

    private fun cleanScope(nodeText: String): String {
        return StringUtils.substringBefore(nodeText, " [")
    }
}