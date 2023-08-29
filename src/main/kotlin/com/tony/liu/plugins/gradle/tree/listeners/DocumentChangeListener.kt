package com.tony.liu.plugins.gradle.tree.listeners

import com.intellij.openapi.editor.event.DocumentListener
import com.tony.liu.plugins.gradle.tree.swing.GradleTreeForm

class DocumentChangeListener(private val gradleTreeForm: GradleTreeForm) : DocumentListener {

    override fun documentChanged(event: com.intellij.openapi.editor.event.DocumentEvent) {
        gradleTreeForm.markRefreshUIChanged()
    }

}