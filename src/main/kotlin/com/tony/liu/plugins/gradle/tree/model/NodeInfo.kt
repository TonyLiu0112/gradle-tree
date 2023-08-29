package com.tony.liu.plugins.gradle.tree.model

data class NodeInfo(
    val deep: Int,
    val hasSameLevel: Boolean,
    val group: String,
    val artifactId: String,
    val scope: String,
    val version: String,
    val dVersion: String,
    val list: MutableList<NodeInfo>
)