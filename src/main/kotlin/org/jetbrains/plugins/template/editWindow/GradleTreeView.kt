package org.jetbrains.plugins.template.editWindow

import net.miginfocom.swing.MigLayout
import javax.swing.*

class GradleTreeView {

    val basicPanel: JPanel = JPanel()

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    private var button1:JButton? = null
    private var button2:JButton? = null
    private var button3:JButton? = null
    private var textField1:JTextField? = null
    private var splitPane1:JSplitPane? = null
    private var scrollPane1:JScrollPane? = null
    private var tree1:JTree? = null
    private var scrollPane2:JScrollPane? = null
    private var tree2:JTree? = null

    init {
        initComponents()
    }

    private fun initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
        button1 = JButton()
        button2 = JButton()
        button3 = JButton()
        textField1 = JTextField()
        splitPane1 = JSplitPane()
        scrollPane1 = JScrollPane()
        tree1 = JTree()
        scrollPane2 = JScrollPane()
        tree2 = JTree()

        //======== this ========
        basicPanel.setLayout(MigLayout(
            "hidemode 3",  // columns
            "[fill]" +
                    "[fill]" +
                    "[fill]" +
                    "[fill]" +
                    "[fill]" +
                    "[fill]" +
                    "[fill]" +
                    "[fill]" +
                    "[fill]" +
                    "[fill]" +
                    "[fill]",  // rows
            "[]" +
                    "[]" +
                    "[]" +
                    "[]" +
                    "[]" +
                    "[]" +
                    "[]" +
                    "[]" +
                    "[]" +
                    "[]"))

        //---- button1 ----
        button1!!.setText("text")
        basicPanel.add(button1, "cell 1 0")

        //---- button2 ----
        button2!!.setText("text")
        basicPanel.add(button2, "cell 2 0")

        //---- button3 ----
        button3!!.setText("text")
        basicPanel.add(button3, "cell 10 0")
        basicPanel.add(textField1, "cell 1 1 2 1")

        //======== splitPane1 ========
        run{
            splitPane1!!.setResizeWeight(0.5)
            splitPane1!!.setDividerSize(2)

            //======== scrollPane1 ========
            run{scrollPane1!!.setViewportView(tree1)}
            splitPane1!!.leftComponent = scrollPane1

            //======== scrollPane2 ========
            run{scrollPane2!!.setViewportView(tree2)}
            splitPane1!!.rightComponent = scrollPane2
        }
        basicPanel.add(splitPane1, "cell 1 2 10 8,grow")
        // JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on
    }
}