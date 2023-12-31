package com.tony.liu.plugins.gradle.tree.context

import com.google.common.collect.ArrayListMultimap
import com.tony.support.model.TreeNode
import com.tony.liu.plugins.gradle.tree.model.TreeMetadata
import javax.swing.tree.DefaultMutableTreeNode

class TreeContext {

    // key: scope, value: tree node of root
    val TREE_NODE = mutableMapOf<String, TreeNode>()

    // key: artifactId-scope, value: metadata of node
    val TREE_METADATA = mutableMapOf<String, TreeNode>()

    // swing obj.
    var TREE_VIEW: DefaultMutableTreeNode? = null

    // key: artifactId-scope, value: treeNode list
    val ARTIFACT_NODES_MAP: ArrayListMultimap<String, TreeNode> = ArrayListMultimap.create()

    // key: artifactId-scope, value: full text.
    val ARTIFACT_TEXT_MAP = mutableMapOf<String, String>()

}