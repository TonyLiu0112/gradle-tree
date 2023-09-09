package com.tony.liu.plugins.gradle.tree.swing

import java.awt.*
import java.awt.geom.RoundRectangle2D
import javax.swing.plaf.basic.BasicTreeUI
import javax.swing.tree.TreePath


class MyTreeUi: BasicTreeUI() {

    override fun paintRow(
        g: Graphics?,
        clipBounds: Rectangle?,
        insets: Insets?,
        bounds: Rectangle?,
        path: TreePath?,
        row: Int,
        isExpanded: Boolean,
        hasBeenExpanded: Boolean,
        isLeaf: Boolean
    ) {
        // TODO 尝试调整UI样式?
//        if (tree != null && tree.selectionModel.isRowSelected(row)) {
//            g!!.color = tree.background
//            val cornerRadius = 10 // 圆角半径
//
//            val g2d = g as Graphics2D
//            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
//            val roundRect: RoundRectangle2D = RoundRectangle2D.Float(
//                bounds!!.x.toFloat(),
//                bounds.y.toFloat(),
//                bounds.width.toFloat(),
//                bounds.height.toFloat(),
//                cornerRadius.toFloat(),
//                cornerRadius.toFloat() + 100
//            )
//            g2d.fill(roundRect)
//        }
        super.paintRow(g, clipBounds, insets, bounds, path, row, isExpanded, hasBeenExpanded, isLeaf)
    }

}