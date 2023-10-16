package com.tony.liu.plugins.gradle.tree.swing

import com.intellij.ui.JBColor
import com.intellij.util.ui.StartupUiUtil.isUnderDarcula
import com.intellij.util.ui.UIUtil.isUnderDarcula
import com.tony.liu.plugins.gradle.tree.context.FileContext
import com.tony.liu.plugins.gradle.tree.utils.NodeTextUtils
import org.apache.commons.lang3.StringUtils
import java.awt.Color
import java.awt.Component
import javax.swing.JTree
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.DefaultTreeCellRenderer


private const val OMITTED = "omitted with"

private val greenScope: Array<String> = arrayOf("[testCompileOnly]", "[testImplementation]", "[testRuntimeOnly]")
private val purpleScope: Array<String> = arrayOf("[runtimeOnly]")
private val yellowScope: Array<String> = arrayOf("[compileOnly]", "[developmentOnly]")

private data class ScopeColor(val color: Color, val scops: Array<String>)

private val transparent: Color = Color(0, 0, 0, 0)
private val filterSelectColor_Dark: Color = Color(52, 116, 118)
private val filterSelectColor_Light: Color = Color(148, 250, 254)

private val scopeColorList_Dark: Array<ScopeColor> =
    arrayOf(
        ScopeColor(Color(114, 159, 123), greenScope),
        ScopeColor(Color(195, 170, 222), purpleScope),
        ScopeColor(Color(194, 190, 23), yellowScope)
    )

private val scopeColorList_Light: Array<ScopeColor> =
    arrayOf(
        ScopeColor(Color(49, 109, 25), greenScope),
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
        try {
            super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus)
        } catch (_: Exception) {

        }
        icon = null

        val node = value as DefaultMutableTreeNode
        val nodeText = node.toString()

        if (nodeText == "virtualRoot") {
            return this
        }

        val cleanScopeNodeText = cleanScope(nodeText)

        if (StringUtils.isNotBlank(searchKey)
            && (StringUtils.contains(cleanScopeNodeText, searchKey) || groupContainsKeyword(
                nodeText,
                cleanScopeNodeText,
                searchKey
            ))
        ) {
            // 搜索渲染
            setBackgroundSelectionColor(DefaultTreeCellRenderer().backgroundSelectionColor)

            if (isUnderDarcula()) {
                setBackgroundNonSelectionColor(filterSelectColor_Dark)
                setBackground(filterSelectColor_Dark)
            } else {
                setBackgroundNonSelectionColor(filterSelectColor_Light)
                setBackground(filterSelectColor_Light)
            }

        } else {
            // 普通渲染
            val defaultTreeCellRenderer = DefaultTreeCellRenderer()
            setBackgroundSelectionColor(defaultTreeCellRenderer.backgroundSelectionColor)
            setBackgroundNonSelectionColor(transparent)
            setBackground(transparent)
        }

        // 主题色渲染
        if (isUnderDarcula) {
            setForeground(foreground)
            setBorderSelectionColor(null)
        } else {
            setForeground(foreground)
            setBorderSelectionColor(null)
        }

        // 焦点渲染
        if (sel && !hasFocus) {
            backgroundSelectionColor = null
        }

        // 编译范围渲染
        if (setForegroundByScope(nodeText)) {
            return this
        } else if (StringUtils.contains(nodeText, OMITTED)) {
            setForeground(JBColor.RED)
        }

        return this
    }

    private fun setForegroundByScope(nodeText: String): Boolean {
        val scopeColors = if (isUnderDarcula) scopeColorList_Dark else scopeColorList_Light

        for (scopeColor in scopeColors) {
            for (scope in scopeColor.scops) {
                if (StringUtils.contains(nodeText, scope)) {
                    setForeground(scopeColor.color)
                    return true
                }
            }
        }
        return false
    }

    private fun groupContainsKeyword(nodeText: String, cleanScopeNodeText: String, searchKey: String): Boolean {
        val artifactId = NodeTextUtils.getArtifactId(cleanScopeNodeText)
        val scope = NodeTextUtils.getScope(nodeText)
        val fullText = FileContext.FILE_CONTEXT_MAP[dir]!!.ARTIFACT_TEXT_MAP.getOrDefault("$artifactId-$scope", "")
        return StringUtils.contains(fullText, searchKey)
    }

    private fun cleanScope(nodeText: String): String {
        return StringUtils.substringBefore(nodeText, " [")
    }
}