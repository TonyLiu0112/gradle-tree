package com.tony.liu.plugins.gradle.tree.swing

import org.apache.commons.lang3.StringUtils
import java.awt.Color
import java.awt.Component
import javax.swing.JTree
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.DefaultTreeCellRenderer


private val transparent: Color = Color(0, 0, 0, 0)

class RightTreeCellRenderer : DefaultTreeCellRenderer() {

    override fun getTreeCellRendererComponent(
        tree: JTree?, value: Any?, sel: Boolean, expanded: Boolean, leaf: Boolean, row: Int, hasFocus: Boolean
    ): Component {
        super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus)
        icon = null

        val node = value as DefaultMutableTreeNode
        val nodeText = node.toString()

        setBackgroundNonSelectionColor(transparent)
        setBackground(transparent)
        setBackgroundSelectionColor(transparent)
        setBorderSelectionColor(null)

        if (StringUtils.contains(nodeText, "omitted")) {
            setForeground(Color.red)
        }
        return this
    }

}