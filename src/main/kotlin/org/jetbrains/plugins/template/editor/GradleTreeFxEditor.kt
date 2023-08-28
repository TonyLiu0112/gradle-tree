//package org.jetbrains.plugins.template.editor
//
//import com.intellij.openapi.editor.Document
//import com.intellij.openapi.editor.Editor
//import com.intellij.openapi.editor.EditorFactory
//import com.intellij.openapi.editor.ex.EditorEx
//import com.intellij.openapi.fileEditor.FileEditor
//import com.intellij.openapi.fileEditor.FileEditorState
//import com.intellij.openapi.fileEditor.FileEditorStateLevel
//import com.intellij.openapi.project.Project
//import com.intellij.openapi.util.Key
//import com.intellij.openapi.util.Ref
//import com.intellij.openapi.vfs.VirtualFile
//import com.intellij.util.keyFMap.KeyFMap
//import javafx.application.Platform
//import javafx.embed.swing.JFXPanel
//import javafx.fxml.FXMLLoader
//import javafx.scene.Parent
//import javafx.scene.Scene
//import org.jetbrains.plugins.template.editWindow.GradleTreeForm
//import java.beans.PropertyChangeListener
//import java.io.IOException
//import javax.swing.JComponent
//import javax.swing.JPanel
//
//
//class GradleTreeFxEditor(private val project: Project, private val file: VirtualFile) : FileEditor {
//
//    private val myUserData: Ref<KeyFMap> = Ref.create(KeyFMap.EMPTY_MAP)
//    private var editor: Editor
//
//    //    private var panel: JPanel
//    private var fxPanel: JFXPanel? = null
//
//    init {
//        editor = createEditorComponent()
//    }
//
//    private fun initializeJavaFXContent() {
//        val fxmlLoader = FXMLLoader(this.javaClass.getResource("fxml/gradleTree.fxml"))
//        var root: Parent? = null
//        try {
//            fxmlLoader.classLoader = this.javaClass.classLoader
//            root = fxmlLoader.load()
//        } catch (e: IOException) {
//            e.printStackTrace()
//        }
//
//        fxPanel!!.scene = Scene(root)
//    }
//
//    override fun <T : Any?> getUserData(key: Key<T>): T? {
//        return myUserData.get().get(key)
//    }
//
//    override fun <T : Any?> putUserData(key: Key<T>, value: T?) {
//        val map = myUserData.get()
//        myUserData.set(if (value == null) map.minus(key) else map.plus(key, value))
//    }
//
//    override fun dispose() {
//    }
//
//    override fun getComponent(): JComponent {
//        if (fxPanel == null) {
//            fxPanel = JFXPanel()
//
//            Platform.runLater { initializeJavaFXContent() }
//        }
//        return fxPanel!!
//    }
//
//    override fun getPreferredFocusedComponent(): JComponent {
//        return editor.contentComponent
//    }
//
//    override fun addPropertyChangeListener(listener: PropertyChangeListener) {
//    }
//
//    override fun removePropertyChangeListener(listener: PropertyChangeListener) {
//    }
//
//    override fun getFile(): VirtualFile = file
//    override fun isValid(): Boolean = true
//    override fun getName(): String = "Gradle Tree"
//    override fun getState(level: FileEditorStateLevel): FileEditorState = FileEditorState.INSTANCE
//    override fun setState(state: FileEditorState) {}
//    override fun isModified(): Boolean = false
//
//    private fun createEditorComponent(): Editor {
//        val editorFactory: EditorFactory = EditorFactory.getInstance()
//        val document: Document = editorFactory.createDocument(String(file.contentsToByteArray()))
//        return editorFactory.createEditor(document, project) as EditorEx
//    }
//
//    private fun createPanel(): JPanel {
////        val customPanel = JPanel(BorderLayout())
////        customPanel.add(editor.component, BorderLayout.CENTER)
////        customPanel.setBorder(BorderFactory.createLineBorder(JBColor.GRAY))
////        return customPanel
//
//        val gradleTreeForm = GradleTreeForm()
//        return gradleTreeForm.basePanel!!
//    }
//}