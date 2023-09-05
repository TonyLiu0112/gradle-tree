package com.tony.liu.plugins.gradle.tree.editor

import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.editor.ex.EditorEx
import com.intellij.openapi.editor.impl.DocumentImpl
import com.intellij.openapi.editor.impl.event.EditorEventMulticasterImpl
import com.intellij.openapi.fileEditor.FileEditor
import com.intellij.openapi.fileEditor.FileEditorState
import com.intellij.openapi.fileEditor.FileEditorStateLevel
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Key
import com.intellij.openapi.util.UserDataHolderBase
import com.intellij.openapi.vfs.VirtualFile
import com.tony.liu.plugins.gradle.tree.context.FileContext
import com.tony.liu.plugins.gradle.tree.listeners.DocumentChangeListener
import com.tony.liu.plugins.gradle.tree.swing.GradleTreeForm
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.beans.PropertyChangeListener
import java.io.File
import javax.swing.JComponent
import javax.swing.JPanel


class GradleTreeEditor(
    private val project: Project,
    private val file: VirtualFile,
    private val document: Document
) : FileEditor {

    private lateinit var editor: Editor
    private lateinit var panel: JPanel
    private lateinit var gradleTreeForm: GradleTreeForm
    private var dir: String = File(file.path).parent
    private val myUserDataHolder = UserDataHolderBase()
    private var inited: Boolean = false

    private fun doInit() {
        editor = createEditorComponent()
        gradleTreeForm = GradleTreeForm()
        gradleTreeForm.initFile(project, file)


        document.addDocumentListener(DocumentChangeListener(gradleTreeForm))
        panel = createPanel()

        inited = true
    }

    override fun <T : Any?> getUserData(key: Key<T>): T? {
        return myUserDataHolder.getUserData(key)
    }

    override fun <T : Any?> putUserData(key: Key<T>, value: T?) {
        myUserDataHolder.putUserData(key, value)
    }

    override fun dispose() {
    }

    override fun getComponent(): JComponent {
        if (!inited) {
            doInit()
        }
        return panel
    }

    override fun getPreferredFocusedComponent(): JComponent {
        return editor.contentComponent
    }

    override fun addPropertyChangeListener(listener: PropertyChangeListener) {
    }

    override fun removePropertyChangeListener(listener: PropertyChangeListener) {
    }

    override fun getFile(): VirtualFile = file
    override fun isValid(): Boolean = true
    override fun getName(): String = "Dependency Analysis"
    override fun getState(level: FileEditorStateLevel): FileEditorState = FileEditorState.INSTANCE
    override fun setState(state: FileEditorState) {}
    override fun isModified(): Boolean = false

    private fun createEditorComponent(): Editor {
        val editorFactory: EditorFactory = EditorFactory.getInstance()
        val document = DocumentImpl(String(file.contentsToByteArray()), true, false)
        (editorFactory.eventMulticaster as EditorEventMulticasterImpl).registerDocument(document)
        return editorFactory.createEditor(document, project) as EditorEx
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun createPanel(): JPanel {
        GlobalScope.launch {
            initTreeData()
        }

        gradleTreeForm.bindRefreshBtnClick {
            GlobalScope.launch {
                forceRefreshLeftTree()
                gradleTreeForm.enableRefreshBtn()
            }
        }

        gradleTreeForm.bindSearchInputChangeEvent {
            refreshLeftTree()
        }

        gradleTreeForm.bindFilterSelected {
            refreshLeftTree()
        }

        gradleTreeForm.bindShowGroupClick {
            refreshLeftTree()
        }

        return gradleTreeForm.basePanel!!
    }

    private fun refreshLeftTree() {
        val leftTreeNode = FileContext.FILE_CONTEXT_MAP[dir]!!.TREE_VIEW
        if (leftTreeNode != null) {
            gradleTreeForm.flushLeftTree(dir, leftTreeNode)
        }
    }

    private fun forceRefreshLeftTree() {
        FileContext.refresh(dir, file.detectedLineSeparator!!)
        initTreeData()
    }

    private fun initTreeData() {
        val treeContext = FileContext.FILE_CONTEXT_MAP[dir]
        if (treeContext?.TREE_VIEW == null) {
            FileContext.init(dir, file.detectedLineSeparator!!)
        }
        val leftTreeNode = FileContext.FILE_CONTEXT_MAP[dir]!!.TREE_VIEW
        if (leftTreeNode != null) {
            gradleTreeForm.flushLeftTree(dir, leftTreeNode)
        }
    }

}