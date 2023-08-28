package org.jetbrains.plugins.template.editor

import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.editor.event.DocumentListener
import com.intellij.openapi.editor.ex.EditorEx
import com.intellij.openapi.fileEditor.FileEditor
import com.intellij.openapi.fileEditor.FileEditorState
import com.intellij.openapi.fileEditor.FileEditorStateLevel
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Key
import com.intellij.openapi.util.UserDataHolderBase
import com.intellij.openapi.vfs.VirtualFile
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jetbrains.plugins.template.context.FileContext
import org.jetbrains.plugins.template.editWindow.GradleTreeForm
import java.beans.PropertyChangeListener
import java.io.File
import javax.swing.JComponent
import javax.swing.JPanel


class GradleTreeEditor(
    private val project: Project,
    private val file: VirtualFile,
    document: Document
) : FileEditor {

    private var editor: Editor
    private var panel: JPanel
    private var gradleTreeForm: GradleTreeForm
    private var dir: String = File(file.path).parent
    private val myUserDataHolder = UserDataHolderBase()

    init {
        editor = createEditorComponent()
        gradleTreeForm = GradleTreeForm()
        panel = createPanel()
        document.addDocumentListener(DocumentChangeListener(gradleTreeForm))
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
    override fun getName(): String = "Gradle Tree"
    override fun getState(level: FileEditorStateLevel): FileEditorState = FileEditorState.INSTANCE
    override fun setState(state: FileEditorState) {}
    override fun isModified(): Boolean = false

    private fun createEditorComponent(): Editor {
        val editorFactory: EditorFactory = EditorFactory.getInstance()
        val document: Document = editorFactory.createDocument(String(file.contentsToByteArray()))
        return editorFactory.createEditor(document, project) as EditorEx
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun createPanel(): JPanel {
        GlobalScope.launch {
            initTreeData()
        }

        gradleTreeForm.bindRefreshBtnClick {
            forceRefreshLeftTree()
        }

        gradleTreeForm.binSearchInputChangeEvent {
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
        FileContext.refresh(dir)
        initTreeData()
    }

    private fun initTreeData() {
        val treeContext = FileContext.FILE_CONTEXT_MAP[dir]
        if (treeContext?.TREE_VIEW == null) {
            FileContext.init(dir)
        }
        val leftTreeNode = FileContext.FILE_CONTEXT_MAP[dir]!!.TREE_VIEW
        if (leftTreeNode != null) {
            gradleTreeForm.flushLeftTree(dir, leftTreeNode)
        }
    }

    class DocumentChangeListener(private val gradleTreeForm: GradleTreeForm) : DocumentListener {

        override fun documentChanged(event: com.intellij.openapi.editor.event.DocumentEvent) {
            gradleTreeForm.markRefreshUIChanged()
        }

    }
}