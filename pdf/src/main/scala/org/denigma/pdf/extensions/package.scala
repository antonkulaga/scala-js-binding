package org.denigma.pdf

/**
  * Created by antonkulaga on 02/06/16.
  */
package object extensions {
  implicit def toExtendedPDFPromise[T](promise: PDFPromise[T]): ExtendedPDFPromise[T] = {
    new ExtendedPDFPromise[T](promise)
  }
}
