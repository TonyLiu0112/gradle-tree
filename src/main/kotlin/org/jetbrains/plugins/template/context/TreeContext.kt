package org.jetbrains.plugins.template.context

import com.google.common.collect.ArrayListMultimap
import com.tony.support.model.TreeNode
import org.jetbrains.plugins.template.model.TreeMetadata
import javax.swing.tree.DefaultMutableTreeNode

class TreeContext {

    // key: scope, value: tree node of root
    val TREE_NODE = mutableMapOf<String, TreeNode>()

    // key: tree content, value: metadata of node
    val TREE_METADATA = mutableMapOf<String, TreeNode>()

    // swing obj.
    var TREE_VIEW: DefaultMutableTreeNode? = null

    // key: artifactId, value: treeNode list
    val ARTIFACT_NODES_MAP: ArrayListMultimap<String, TreeNode> = ArrayListMultimap.create()

    // key: artifactId, value: full text.
    val ARTIFACT_TEXT_MAP = mutableMapOf<String, String>()

}