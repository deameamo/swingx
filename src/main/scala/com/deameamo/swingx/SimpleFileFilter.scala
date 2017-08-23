package com.deameamo.swingx

import java.io.File
import javax.swing.filechooser.FileFilter

class SimpleFileFilter(extList: Array[String], desc: String) extends FileFilter {

  val extensions: Array[String] = for(ext <- extList) yield ext.toLowerCase
  val description: String = if(desc == null) s"${extensions(0)} files" else desc

  def this(ext: String) { this(Array(ext), null) }
  
  def this(ext: String, desc: String) { this(Array(ext), desc) }

  def accept(file: File): Boolean = {
    if(file.isDirectory)
      true
    else {
      var accepted = false
      var i = 0
      while(!accepted && i < extensions.length) {
        accepted = file.getName.toLowerCase.endsWith(extensions(i))
        i += 1
      }
      accepted
    }
  }
  
  def getDescription: String = description
  
}