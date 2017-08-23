package com.deameamo.util

import java.io._

import scala.collection.mutable

object FileUtil {
  
  def main(args: Array[String]) {
    val file = new File("test/111")
    file.createNewFile
  }
  
  val DELETE_NEW_FILE = 1
  val DELETE_OLD_FILE = 0
  
  def getReader(filePath: String): BufferedReader = getReader(filePath, "utf-8")
  
  def getReader(filePath: String, charset: String): BufferedReader = {
    try {
      val in = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), charset))
      in
    } catch {
      case e: UnsupportedEncodingException => e.printStackTrace(); null
      case e: FileNotFoundException => e.printStackTrace(); null
    }
  }
  
  def getReader(file: File, charset: String = "utf-8"): BufferedReader = {
    try {
      val in = new BufferedReader(new InputStreamReader(new FileInputStream(file), charset))
      in
    } catch {
      case e: UnsupportedEncodingException => e.printStackTrace(); null
      case e: FileNotFoundException => e.printStackTrace(); null
    }
  }
  
  def getWriter(filePath: String): PrintWriter = getWriter(filePath, "utf-8")
  
  def getWriter(filePath: String, appending: Boolean): PrintWriter = getWriter(filePath, "utf-8", appending)
  
  def getWriter(filePath: String, charset: String): PrintWriter = getWriter(filePath, charset)
  
  def getWriter(filePath: String, charset: String = "utf-8", appending: Boolean = false): PrintWriter = {
    try {
      val out = new PrintWriter(new OutputStreamWriter(new FileOutputStream(filePath, appending), charset))
      out
    } catch {
      case e: UnsupportedEncodingException => e.printStackTrace(); null
      case e: FileNotFoundException => e.printStackTrace(); null
    }
  }

  def readFile(filePath: String): mutable.MutableList[String] = readFile(new File(filePath))
  
  def readFile(file: File): mutable.MutableList[String] = {
    val buffer = new mutable.MutableList[String]
    val in = getReader(file)
    var line = in.readLine
    while(line != null) {
      buffer += line
      line = in.readLine
    }
    in.close()
    buffer
  }

  def copy(srcPath: String, targetPath: String) {
    try {
      val inBuff = new BufferedInputStream(new FileInputStream(srcPath))
      val outBuff = new BufferedOutputStream(new FileOutputStream(targetPath))
      val buf = new Array[Byte](1024 * 5)
      var len = inBuff.read(buf)
      
      while(len != -1) {
        outBuff.write(buf, 0, len)
        len = inBuff.read(buf)
      }
      outBuff.flush()
      if(inBuff != null) {
        inBuff.close()
      }
      if(outBuff != null) {
        outBuff.close()
      }
    } catch {
      case e: FileNotFoundException => e.printStackTrace()
      case e: IOException => e.printStackTrace()
    }
  }
  
  def merge(part1Path: String, part2Path: String, targetPath: String) {
    FileUtil.copy(part1Path, targetPath)
    try {
      val inBuff = new BufferedInputStream(new FileInputStream(part2Path))
      val outBuff = new BufferedOutputStream(new FileOutputStream(targetPath, true))
      val buf = new Array[Byte](1024 * 5)
      var len = inBuff.read(buf)
      while(len != -1) {
        outBuff.write(buf, 0, len)
        len = inBuff.read(buf)
      }
      outBuff.flush()
      if(inBuff != null) {
        inBuff.close()
      }
      if(outBuff != null) {
        outBuff.close()
      }
    } catch {
      case e: FileNotFoundException => e.printStackTrace()
      case e: IOException => e.printStackTrace()
    }
  }
  
  def closeWriter(out: PrintWriter) {
    out.flush()
    out.close()
  }
  
  def saveAsFile(text: String, filePath: String, charset: String) {
    try {
      val out = new PrintWriter(new OutputStreamWriter(new FileOutputStream(filePath), charset))
      out.print(text)
      out.flush()
      out.close()
    } catch {
      case e: UnsupportedEncodingException => e.printStackTrace()
      case e: FileNotFoundException => e.printStackTrace()
    }
  }
  
  def makeDir(dirPath: String) {
    val dir = new File(dirPath)
    dir.mkdir
  }
  
  def makeFile(filePath: String) {
    val file = new File(filePath)
    file.createNewFile
  }
  
  def printFirstLines(filePath: String, charset: String, number: Int) {
    val in = getReader(filePath, charset)
    var line = in.readLine
    var i = 0
    try {
      var done = false
      while(line != null && !done) {
        if(i < number) {
          println(line)
          i += 1
        }
        else {
          done = true
        }
        line = in.readLine
      }
    } catch {
      case e: IOException => e.printStackTrace()
    }
  }
  
  def printFirstLines(inFilePath: String, outFilePath: String, charset: String, number: Int) {
    val in = getReader(inFilePath, charset)
    val out = getWriter(outFilePath, charset)
    var line = in.readLine
    var i = 0
    try {
      var done = false
      while(line != null && !done) {
        if(i < number) {
          out.println(line)
          i += 1
        }
        else {
          done = true
        }
        line = in.readLine()
      }
      out.flush()
      out.close()
    } catch {
      case e: IOException => e.printStackTrace()
    }
  }
  
  def rename(oldPath: String, newPath: String) {
    if(!oldPath.equals(newPath)) {
      val file = new File(oldPath)
      file.renameTo(new File(newPath))
    }
  }
  
  def renameWithDeletion(oldPath: String, newPath: String, mode: Int) {
    if(!oldPath.equals(newPath)) {
      val newFile = new File(newPath)
      val oldFile = new File(oldPath)
      if(newFile.exists) {
        if(mode == DELETE_NEW_FILE) {
          newFile.delete()
          oldFile.renameTo(newFile)
        }
        else if(mode == DELETE_OLD_FILE) {
          oldFile.delete()
        }
      }
      else {
        oldFile.renameTo(newFile)
      }
    }
  }
  
  def exists(filePath: String): Boolean = {
    val file = new File(filePath)
    file.exists
  }
  
  def delete(filePath: String) {
    val file = new File(filePath)
    println(file.delete())
  }
  
  def splitFileName(name: String): FileInfo = {
    if(name.contains(".")) {
      val neck = name.indexOf('.')
      FileInfo(name.substring(0, neck), name.substring(neck + 1))
    }
    else
      FileInfo(name, "")
  }
  
  case class FileInfo(main: String, ext: String)
}

