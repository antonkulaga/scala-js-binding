package org.denigma.preview


import java.io.{File => JFile}

import akka.actor.ActorSystem
import akka.http.scaladsl.{Http, HttpExt}
import akka.stream.ActorMaterializer
import better.files.File
import com.typesafe.config.Config
import net.ceedubs.ficus.Ficus._
import better.files._
import scala.Seq
import scala.collection.immutable._


class FileManager(val root: File) {

  def remove(name: String) = {
    val path: File = root / name
    path.delete()
  }

  def readBytes(relativePath: String): Option[Array[Byte]] = {
    val file = (root / relativePath)
    if(file.exists && file.isRegularFile) {
      Some(file.loadBytes)
    } else None
  }
  def read(relativePath: String): String = (root / relativePath).contentAsString

  def cd(relativePath: String): FileManager = new FileManager(root / relativePath)

}