package org.denigma.binding.commons

import org.denigma.binding.binders.{GeneralBinder, _}
import org.denigma.binding.extensions._
import org.denigma.binding.views.BindableView
import org.scalajs.dom
import org.scalajs.dom._
import org.scalajs.dom.html.Input
import org.scalajs.dom.raw.{Element, FileReader}
import rx._
import org.denigma.binding.extensions._
import scala.annotation.tailrec
import scala.concurrent.{Future, Promise}
import scala.util._
import scalajs.concurrent.JSExecutionContext.Implicits.queue

trait Uploader {

  protected def uploadHandler(ev: Event)(onComplete: Try[(File, String)] => Unit): Unit = {
    if (ev.target == ev.currentTarget){
      ev.preventDefault()
      ev.target match {
        case input: Input =>
          val files: List[File] = input.files
          for(f <- files) {
            val reader = new FileReader()
            reader.readAsText(f)
            val fut = readText(f)
            fut.onComplete(onComplete)
          }
        case null => println("null file input")
        case _ => dom.console.error("not a file input")
      }
    }
  }

  @tailrec final def filesToList(f: FileList, acc: List[File] = Nil, num: Int = 0): List[File] = {
    if (f.length <= num) acc.reverse else filesToList(f, f.item(num)::acc, num + 1)
  }

  implicit def filesAsList(f: FileList): List[File] = filesToList(f, Nil, 0)

  protected def readText(f: File): Future[(File, String)] = {
    val result = Promise[(File, String)]
    val reader = new FileReader()
    def onLoadEnd(ev: ProgressEvent): Any = {
      result.success((f, reader.result.toString))
    }
    def onErrorEnd(ev: Event): Any = {
      result.failure(new Exception("READING FAILURE " + ev.toString))
    }
    reader.onloadend = onLoadEnd _
    reader.onerror = onErrorEnd _
    reader.readAsText(f)
    result.future
  }
}