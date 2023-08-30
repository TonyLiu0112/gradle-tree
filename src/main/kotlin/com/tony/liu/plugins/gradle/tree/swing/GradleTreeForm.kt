package com.tony.liu.plugins.gradle.tree.swing

import com.intellij.openapi.externalSystem.importing.ImportSpecBuilder
import com.intellij.openapi.externalSystem.model.ProjectSystemId
import com.intellij.openapi.externalSystem.util.ExternalSystemUtil
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.tony.liu.plugins.gradle.tree.context.FileContext
import com.tony.liu.plugins.gradle.tree.services.PsiGradleService
import com.tony.liu.plugins.gradle.tree.static.SwingResource
import com.tony.liu.plugins.gradle.tree.utils.NodeTextUtils
import com.tony.liu.plugins.gradle.tree.utils.ObjUtils
import org.apache.commons.lang3.StringUtils.*
import java.awt.Component
import java.awt.Desktop
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.net.URI
import javax.swing.*
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.DefaultTreeModel
import javax.swing.tree.TreeNode
import javax.swing.tree.TreePath


class GradleTreeForm {
    var basePanel: JPanel? = null

    var topPanel: JPanel? = null
    private var refreshUIBtn: JButton? = null
    private var reimport: JButton? = null
    private var moneyBtn: JButton? = null

    var secondPanel: JPanel? = null
    private var searchInput: JTextField? = null
    private var filter: JCheckBox? = null

    var thirdPanel: JPanel? = null
    private var radioGroup: ButtonGroup? = null
    private var showGroupCheckBox: JCheckBox? = null
    private var expand: JRadioButton? = null
    private var collapse: JRadioButton? = null

    var lastPanel: JPanel? = null
    private var jSplitPane: JSplitPane? = null
    private var leftScrollPane: JScrollPane? = null
    private var leftTree: JTree? = null
    private var rightScrollPane: JScrollPane? = null
    private var rightTree: JTree? = null

    private var mouseClickListener: MouseClickListener? = null

    private var project: Project? = null
    private var virtualFile: VirtualFile? = null
    private var fileType: Int = 0

    init {

        leftTree!!.model = null
        rightTree!!.model = null

        radioGroup = ButtonGroup()
        radioGroup!!.add(expand)
        radioGroup!!.add(collapse)

        expand!!.isSelected = true

        moneyBtn!!.icon = SwingResource.giveCashIcon

        bindExpandBtnClick()
        bindCollapseBtnClick()
        bindReimportBtnClick()
        bindDonateBtnClick()
    }

    private fun bindDonateBtnClick() {
        moneyBtn!!.addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent?) {
                Desktop.getDesktop().browse(URI("https://github.com/TonyLiu0112/donate/wiki/Donate"));
            }
        })
    }

    fun initFile(project: Project, virtualFile: VirtualFile) {
        this.project = project
        this.virtualFile = virtualFile
        fileType = if (virtualFile.name.endsWith(".kts")) {
            1
        } else {
            0
        }
    }

    fun markRefreshUIChanged() {
        refreshUIBtn!!.icon = SwingResource.waringIcon
    }

    fun bindFilterSelected(listener: () -> Unit) {
        filter!!.addMouseListener(object : MouseAdapter() {

            override fun mouseClicked(e: MouseEvent?) {
                if (isNotBlank(searchInput!!.text)) {
                    listener.invoke()
                }
            }

        })
    }

    fun bindRefreshBtnClick(listener: () -> Unit) {
        refreshUIBtn!!.addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent?) {
                leftTree!!.model = null
                rightTree!!.model = null
                listener.invoke()
                refreshUIBtn!!.icon = null
            }
        })
    }

    private fun bindReimportBtnClick() {
        reimport!!.addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent?) {
                ExternalSystemUtil.refreshProjects(ImportSpecBuilder(project!!, ProjectSystemId("GRADLE")))
            }
        })
    }

    fun bindSearchInputChangeEvent(listener: () -> Unit) {

        searchInput!!.document.addDocumentListener(object : DocumentListener {

            override fun insertUpdate(e: DocumentEvent?) {
                listener.invoke()
            }

            override fun removeUpdate(e: DocumentEvent?) {
                listener.invoke()
            }

            override fun changedUpdate(e: DocumentEvent?) {
                listener.invoke()
            }

        })
    }

    fun bindShowGroupClick(listener: () -> Unit) {
        showGroupCheckBox!!.addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent?) {
                listener.invoke()
            }
        })
    }

    private fun bindExpandBtnClick() {
        expand!!.addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent?) {
                if (expand!!.isSelected) {
                    val root = leftTree!!.model.root
                    expandAllNodes(leftTree!!, TreePath(root))
                }
            }
        })
    }

    private fun bindCollapseBtnClick() {
        collapse!!.addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent?) {
                if (collapse!!.isSelected) {
                    val root = leftTree!!.model.root
                    collapseAllNodes(leftTree!!, TreePath(root))
                }
            }
        })
    }

    fun flushLeftTree(dir: String, virtualRoot: DefaultMutableTreeNode) {
        leftTree!!.cellRenderer = LeftTreeCellRenderer(searchInput!!.text, dir)

        var rootNode = ObjUtils.deepCopy(virtualRoot) as DefaultMutableTreeNode

        if (filter!!.isSelected && isNotBlank(searchInput!!.text)) {
            rootNode = cleanUnSearchNode(rootNode)
        }

        // hide virtualRoot
        leftTree!!.setRootVisible(false)
        leftTree!!.setModel(DefaultTreeModel(rootNode))

        if (rootNode.childCount == 0) {
            return
        }

        if (expand!!.isSelected) {
            expandAllNodes(leftTree!!, TreePath(rootNode))
        } else {
            collapseAllNodes(leftTree!!, TreePath(rootNode))
        }

        if (mouseClickListener == null) {
            mouseClickListener = MouseClickListener(this, leftTree!!, dir)
        }

        leftTree!!.removeMouseListener(mouseClickListener)
        leftTree!!.addMouseListener(mouseClickListener)

    }

    fun flushRightTree(nodes: MutableList<com.tony.support.model.TreeNode>) {
        rightTree!!.cellRenderer = RightTreeCellRenderer()

        val virtualRoot = DefaultMutableTreeNode("virtualRoot")

        nodes.forEach { treeNode ->
            val rootNode =
                DefaultMutableTreeNode(treeNode.definitionVersion + " [" + treeNode.scope + "]" + markOmitted(treeNode))
            if (treeNode.parent != null) {
                createTreeView(rootNode, treeNode.parent)
            }
            virtualRoot.add(rootNode)
        }

        // hide virtualRoot
        rightTree!!.setRootVisible(false)
        rightTree!!.setModel(DefaultTreeModel(virtualRoot))

        expandAllNodes(rightTree!!, TreePath(virtualRoot))
    }

    private fun markOmitted(node: com.tony.support.model.TreeNode): String {
        if (node.version == node.definitionVersion) {
            return ""
        }
        return " (omitted)"
    }

    private fun cleanUnSearchNode(virtualRoot: DefaultMutableTreeNode): DefaultMutableTreeNode {
        val newVirtualRoot = DefaultMutableTreeNode("virtualRoot")

        var i = 0
        var c = virtualRoot.childCount
        while (i < c) {
            val rootNode = virtualRoot.getChildAt(i) as DefaultMutableTreeNode

            if (anyMatchSearchKey(rootNode.userObject.toString())) {
                newVirtualRoot.add(rootNode)
                if (i > 0) {
                    i--
                }
                c--
                continue
            }
            val hasKeyword = childNodeHasSearchKey(rootNode)
            if (hasKeyword) {
                newVirtualRoot.add(rootNode)
                if (i > 0) {
                    i--
                }
                c--
            }

            i++
        }


        for (n in 0 until virtualRoot.childCount) {
            if (n >= virtualRoot.childCount) {
                continue
            }
            val rootNode = virtualRoot.getChildAt(n) as DefaultMutableTreeNode

            if (contains(rootNode.userObject.toString(), searchInput!!.text)) {
                newVirtualRoot.add(rootNode)
                continue
            }
            val hasKeyword = childNodeHasSearchKey(rootNode)
            if (hasKeyword) {
                newVirtualRoot.add(rootNode)
            }
        }
        return newVirtualRoot
    }

    private fun childNodeHasSearchKey(node: DefaultMutableTreeNode): Boolean {
        if (node.childCount == 0) {
            return anyMatchSearchKey(node.userObject.toString())
        }

        if (anyMatchSearchKey(node.userObject.toString())) {
            return true
        }

        for (i in 0 until node.childCount) {
            val childNode = node.getChildAt(i) as DefaultMutableTreeNode
            if (childNodeHasSearchKey(childNode)) {
                return true
            }
        }

        return false
    }

    private fun anyMatchSearchKey(nodeText: String): Boolean {
        return contains(substringBefore(nodeText, " ["), searchInput!!.text)
    }

    private fun expandAllNodes(tree: JTree, parentPath: TreePath) {
        val parentNode: TreeNode = parentPath.lastPathComponent as TreeNode
        showGroup(parentNode as DefaultMutableTreeNode)
        for (i in 0 until parentNode.childCount) {
            val childNode: TreeNode = parentNode.getChildAt(i)
            val childPath: TreePath = parentPath.pathByAddingChild(childNode)
            expandAllNodes(tree, childPath)
        }
        tree.expandPath(parentPath)
    }

    private fun collapseAllNodes(tree: JTree, parentPath: TreePath) {
        val parentNode: TreeNode = parentPath.lastPathComponent as TreeNode
        showGroup(parentNode as DefaultMutableTreeNode)
        for (i in 0 until parentNode.childCount) {
            val childNode: TreeNode = parentNode.getChildAt(i)
            val childPath: TreePath = parentPath.pathByAddingChild(childNode)
            collapseAllNodes(tree, childPath)
        }
        if (parentPath.pathCount > 1) {
            tree.collapsePath(parentPath)
        }
    }

    private fun showGroup(node: DefaultMutableTreeNode) {
        if (showGroupCheckBox!!.isSelected) {
            return
        }

        var text = node.userObject.toString()
        if (countMatches(text, " : ") == 2) {
            text = substringAfter(text, " : ")
        }

        node.userObject = text
    }

    private fun createTreeView(rootNode: DefaultMutableTreeNode, parent: com.tony.support.model.TreeNode) {
        if (isBlank(parent.artifactId)) {
            return
        }

        val childNode =
            DefaultMutableTreeNode(parent.groupId + " : " + parent.artifactId + " : " + parent.definitionVersion)
        rootNode.add(childNode)

        if (parent.parent != null && isNotBlank(parent.parent.artifactId)) {
            createTreeView(childNode, parent.parent)
        }
    }

    private class MouseClickListener(val uiForm: GradleTreeForm, val leftTree: JTree, val dir: String) :
        MouseAdapter() {

        override fun mouseClicked(e: MouseEvent?) {
            val selectionPath = leftTree.selectionModel.selectionPath ?: return

            if (SwingUtilities.isRightMouseButton(e)) {
                // 右键选中
                val row: Int = leftTree.getClosestRowForLocation(e!!.x, e.y)
                leftTree.setSelectionRow(row)

                // 功能菜单
                val selectedNode = selectionPath.lastPathComponent
                if (selectedNode != null) {
                    uiForm.showRightClientMenu(e.component, e.x, e.y, selectedNode as DefaultMutableTreeNode)
                }
            } else {
                val selectedNode = selectionPath.lastPathComponent
                if (selectedNode != null) {
                    val nodeText = (selectedNode as DefaultMutableTreeNode).userObject.toString()
                    val artifactId = NodeTextUtils.getArtifactId(nodeText)
                    val nodes = FileContext.FILE_CONTEXT_MAP[dir]!!.ARTIFACT_NODES_MAP.get(artifactId)
                    if (nodes.size > 0) {
                        uiForm.flushRightTree(nodes)
                    }
                }
            }
        }

    }

    private fun showRightClientMenu(component: Component, x: Int, y: Int, selectedNode: DefaultMutableTreeNode) {
        val popupMenu = JPopupMenu()
        val menuItem = JMenuItem("Exclude")
        menuItem.addActionListener {
            val psiGradleService = project!!.getService(PsiGradleService::class.java)
            val success = psiGradleService.exclude(project!!, virtualFile!!, selectedNode, fileType)
            if (success) {
                deleteNodeAndChildren(leftTree!!, TreePath(selectedNode))
            }
        }
        popupMenu.add(menuItem)
        popupMenu.show(component, x, y)
    }

    private fun deleteNodeAndChildren(tree: JTree, nodePath: TreePath) {
        val model = tree.model as DefaultTreeModel
        val node = nodePath.lastPathComponent as DefaultMutableTreeNode

        // Remove the node from its parent
        val parent = node.parent as DefaultMutableTreeNode
        parent.getIndex(node)
        model.removeNodeFromParent(node)

        // Recursively delete children
        for (i in node.childCount - 1 downTo 0) {
            val child = node.getChildAt(i) as DefaultMutableTreeNode
            model.removeNodeFromParent(child)
        }
    }
}
