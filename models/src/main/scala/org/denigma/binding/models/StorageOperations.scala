package org.denigma.binding.models

import org.scalax.semweb.rdf.Res

object StorageOperations {


  case object ReadAll

  case class Update[ModelType<:Model](value:ModelType)

  case class Read(id:Res)

  case class Delete(id:Res)

  case class Create[ModelType<:Model](value:ModelType)


}


