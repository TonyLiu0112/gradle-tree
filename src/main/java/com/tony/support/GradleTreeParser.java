package com.tony.support;

import com.tony.support.model.TreeNode;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GradleTreeParser {

    public static void main(String[] args) throws IOException {
        GradleTreeParser test = new GradleTreeParser();
        test.test();
    }

    public TreeNode test() throws IOException {
        List<String> lines = FileUtils.readLines(new File("/Users/tony/Downloads/789"), "UTF-8");

        TreeNode pn = new TreeNode();
        pn.setDeep(-1);
        pn.setChildren(new ArrayList<>());

        processLine(lines, 0, pn);

        return pn;
    }

    public TreeNode convert2Tree(List<String> lines) {
        TreeNode pn = new TreeNode();
        pn.setDeep(-1);
        pn.setChildren(new ArrayList<>());

        processLine(lines, 0, pn);
        return pn;
    }

    public TreeNode processLine(List<String> lines, int currentOffset, TreeNode pn) {
        if (currentOffset == lines.size()) {
            return null;
        }

        if (pn == null) {
            // 构建虚拟节点, deep为 -1
            pn = new TreeNode();
            pn.setDeep(-1);
            pn.setChildren(new ArrayList<>());

        }

        String line = lines.get(currentOffset++);
        if (StringUtils.contains(line, "No dependencies")) {
            return null;
        }
        TreeNode cn = buildNodeByPn(line, pn);
        cn.setParent(pn);
        pn.getChildren().add(cn);

        if (currentOffset == lines.size()) {
            return null;
        }

        if (nextIsChild(lines, currentOffset, cn)) {
            return processLine(lines, currentOffset, cn);
        }

        TreeNode pNode = getpNode(lines, currentOffset, cn);

        return processLine(lines, currentOffset, pNode);
    }

    @NotNull
    private TreeNode getpNode(List<String> lines, int currentOffset, TreeNode cn) {
        String nextLine = lines.get(currentOffset);
        int nextLineDeep = getDeep(nextLine);
        TreeNode pNode = cn.getParent();
        while (pNode.getDeep() >= nextLineDeep) {
            pNode = pNode.getParent();
        }
        return pNode;
    }

    private TreeNode buildNodeByPn(String line, TreeNode pn) {
        TreeNode nodeInfo = this.buildNode(line);
        nodeInfo.setDeep(pn.getDeep() + 1);
        return nodeInfo;
    }

    private TreeNode buildNode(String line) {
        String coordinate = line.split("--- ")[1];

        String definitionVersion;
        String version;
        String groupId;
        String artifactId;
        if (StringUtils.contains(coordinate, " -> ")) {
            String[] item = StringUtils.splitByWholeSeparator(coordinate, " -> ");
            version = item[1];
            if (StringUtils.countMatches(item[0], ":") == 1) {
                definitionVersion = version;
            } else {
                definitionVersion = StringUtils.substringAfterLast(item[0], ":");
            }

            if (StringUtils.isBlank(definitionVersion)) {
                definitionVersion = version;
            }

            String[] gaItem = StringUtils.splitByWholeSeparator(item[0], ":");
            groupId = gaItem[0];
            artifactId = gaItem[1];
        } else {
            String[] gaItem = StringUtils.splitByWholeSeparator(coordinate, ":");
            groupId = gaItem[0];
            artifactId = gaItem[1];
            if (gaItem.length < 3) {
                definitionVersion = "";
                version = "";
            } else {
                definitionVersion = gaItem[2];
                version = gaItem[2];
            }
        }

        version = replaceFlag(version);
        definitionVersion = replaceFlag(definitionVersion);

        return new TreeNode(getDeep(line), groupId, artifactId, definitionVersion, version, null, new ArrayList<>());
    }

    @NotNull
    private static String replaceFlag(String version) {
        return version.replace(" (*)", "").replace(" (c)", "");
    }

    private boolean nextIsBrother(List<String> lines, int offset, TreeNode cn) {
        int deep = getDeep(lines.get(offset));
        return deep == cn.getDeep();
    }

    private int getDeep(String line) {
        String tabs = StringUtils.splitByWholeSeparator(line, "--- ")[0];
        return (tabs.length() - 1) / 5;
    }

    private boolean nextIsChild(List<String> lines, int offset, TreeNode cn) {
        int deep = getDeep(lines.get(offset));
        return deep > cn.getDeep();
    }


}
