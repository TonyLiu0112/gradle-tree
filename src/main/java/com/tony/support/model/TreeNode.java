package com.tony.support.model;

import java.util.List;

public class TreeNode {

    private int deep;

    private String groupId;

    private String artifactId;

    private String definitionVersion;

    private String version;

    private String scope;

    private TreeNode parent;

    private List<TreeNode> children;

    public TreeNode() {
    }

    public TreeNode(int deep, String groupId, String artifactId, String definitionVersion, String version, TreeNode parent, List<TreeNode> children) {
        this.deep = deep;
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.definitionVersion = definitionVersion;
        this.version = version;
        this.parent = parent;
        this.children = children;
    }

    public int getDeep() {
        return deep;
    }

    public void setDeep(int deep) {
        this.deep = deep;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getArtifactId() {
        return artifactId;
    }

    public void setArtifactId(String artifactId) {
        this.artifactId = artifactId;
    }

    public String getVersion() {
        return version;
    }

    public String getDefinitionVersion() {
        return definitionVersion;
    }

    public void setDefinitionVersion(String definitionVersion) {
        this.definitionVersion = definitionVersion;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public TreeNode getParent() {
        return parent;
    }

    public void setParent(TreeNode parent) {
        this.parent = parent;
    }

    public List<TreeNode> getChildren() {
        return children;
    }

    public void setChildren(List<TreeNode> children) {
        this.children = children;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }
}
