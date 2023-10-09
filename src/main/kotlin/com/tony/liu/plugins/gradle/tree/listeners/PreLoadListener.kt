//package com.tony.liu.plugins.gradle.tree.listeners
//
//import com.intellij.openapi.project.Project
//import com.intellij.openapi.project.ProjectManagerListener
//import com.tony.liu.plugins.gradle.tree.context.FileContext
//import kotlinx.coroutines.DelicateCoroutinesApi
//import kotlinx.coroutines.GlobalScope
//import kotlinx.coroutines.launch
//import org.apache.commons.io.FileUtils
//import org.apache.commons.io.filefilter.SuffixFileFilter
//import java.io.File
//
//@Suppress("DEPRECATION")
//class PreLoadListener : ProjectManagerListener {
//
//    @Deprecated("Deprecated in Java")
//    @OptIn(DelicateCoroutinesApi::class)
//    @Suppress("removal")
//    override fun projectOpened(project: Project) {
//        super.projectOpened(project)
//
//        val basePath = project.basePath
//
//        val iterate = FileUtils.iterateFiles(File(basePath!!), SuffixFileFilter("gradle", "kts"), null)
//
//        while (iterate.hasNext()) {
//            val file = iterate.next()
//            if (file.name.startsWith("settings")) {
//                continue
//            }
//
//            print(file.name)
//
//            GlobalScope.launch {
//                val gradleFile = basePath + File.separator + file.name
//                FileContext.init(gradleFile, "")
//            }
//
//            break
//        }
//
//    }
//
//}