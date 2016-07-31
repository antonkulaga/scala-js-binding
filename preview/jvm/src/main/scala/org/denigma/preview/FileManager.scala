package org.denigma.preview


import java.io.{File => JFile}

import akka.event.LoggingAdapter
import better.files.File

object FileManager {
  val FILE_NOT_EXIST = "file does not exist"
}


class FileManager(val root: File, log: LoggingAdapter) {

  def remove(name: String) = {
    val path = root / name
    if(path.notExists) log.error("")
    path.delete()
  }

  def readBytes(relativePath: String): Option[Array[Byte]] = {
    val file = root / relativePath
    if(file.exists && file.isRegularFile) {
      Some(file.loadBytes)
    } else None
  }

  def read(relativePath: String): String = (root / relativePath).contentAsString

  def cd(relativePath: String): FileManager = new FileManager(root.sibling(relativePath), log)

}