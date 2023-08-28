package org.jetbrains.plugins.template.utils

import org.apache.commons.lang3.StringUtils

class NodeTextUtils {

    companion object {

        fun getArtifactId(nodeText: String): String {
            return if (StringUtils.countMatches(nodeText, " : ") == 2) {
                nodeText.split(" : ")[1]
            } else {
                nodeText.split(" : ")[0]
            }
        }
    }

}