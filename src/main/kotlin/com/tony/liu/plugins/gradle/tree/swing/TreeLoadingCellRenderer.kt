package com.tony.liu.plugins.gradle.tree.swing

import java.awt.Color
import java.awt.Component
import java.awt.Font
import javax.swing.JTree
import javax.swing.tree.DefaultTreeCellRenderer

private val transparent: Color = Color(0, 0, 0, 0)

class TreeLoadingCellRenderer : DefaultTreeCellRenderer() {

    override fun getTreeCellRendererComponent(
        tree: JTree?, value: Any?, sel: Boolean, expanded: Boolean, leaf: Boolean, row: Int, hasFocus: Boolean
    ): Component {
        val component =
            super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus)
        icon = null

        setBackgroundNonSelectionColor(transparent)
        setBackground(transparent)
        setBackgroundSelectionColor(transparent)
        setBorderSelectionColor(null)

        val font: Font = component.font
        val boldFont = Font(font.name, Font.BOLD, font.size)
        component.setFont(boldFont)

        return this
    }

}