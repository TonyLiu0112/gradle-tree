package com.tony.liu.plugins.gradle.tree.utils

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream

class ObjUtils {

    companion object {

        fun deepCopy(obj: Any): Any {
            val byteArrayOutputStream = ByteArrayOutputStream()
            val objectOutputStream = ObjectOutputStream(byteArrayOutputStream)
            objectOutputStream.writeObject(obj)

            val byteArrayInputStream = ByteArrayInputStream(byteArrayOutputStream.toByteArray())
            val objectInputStream = ObjectInputStream(byteArrayInputStream)
            return objectInputStream.readObject()
        }
    }

}