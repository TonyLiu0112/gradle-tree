package com.tony.liu.plugins.gradle.tree.static

import com.tony.liu.plugins.gradle.tree.swing.GradleTreeForm
import java.io.IOException
import java.io.InputStream
import javax.swing.ImageIcon

class SwingResource {

    companion object {
        val waringIcon: ImageIcon? = createImageIcon("static/images/warning.png")
        val giveCashIcon: ImageIcon? = createImageIcon("static/images/GiveCash.png")

        private fun createImageIcon(fileName: String): ImageIcon? {
            return try {
                val classLoader: ClassLoader = GradleTreeForm::class.java.getClassLoader()
                val inputStream: InputStream? = classLoader.getResourceAsStream(fileName)
                if (inputStream != null) {
                    val buffer: ByteArray = inputStream.readAllBytes()
                    val imageIcon = ImageIcon(buffer)
                    inputStream.close()
                    imageIcon
                } else {
                    null
                }
            } catch (e: IOException) {
                e.printStackTrace()
                null
            }
        }
    }

}