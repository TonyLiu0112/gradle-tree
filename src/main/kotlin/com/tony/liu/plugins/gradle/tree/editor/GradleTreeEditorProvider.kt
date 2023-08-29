package com.tony.liu.plugins.gradle.tree.editor

import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.fileEditor.FileEditor
import com.intellij.openapi.fileEditor.FileEditorPolicy
import com.intellij.openapi.fileEditor.FileEditorProvider
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile


class GradleTreeEditorProvider : FileEditorProvider {

    override fun accept(project: Project, file: VirtualFile): Boolean {
        val fileName = file.name
        val fileItem = fileName.split(".")
        if (fileItem.isEmpty() || fileItem.size <= 1) {
            return false
        }

        return fileItem[1] == "gradle"
    }

    override fun createEditor(project: Project, file: VirtualFile): FileEditor {
        val document = FileDocumentManager.getInstance().getDocument(file)
        return GradleTreeEditor(project, file, document!!)
    }

    override fun getEditorTypeId(): String {
        return "Gradle editor provider"
    }

    override fun getPolicy(): FileEditorPolicy {
        return FileEditorPolicy.PLACE_AFTER_DEFAULT_EDITOR
    }
}