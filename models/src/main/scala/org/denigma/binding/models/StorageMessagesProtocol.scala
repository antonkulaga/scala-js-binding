package org.denigma.binding.models


trait ExtendedStorageProtocol extends StorageProtocol{

  type SelectMessage <:CommonMessage
}

/**
 * Just come constrains to keep CRUD protocols in order
 */
trait StorageProtocol {

  type CommonMessage

  type CreateMessage<:CommonMessage
  type ReadMessage<:CommonMessage
  type UpdateMessage<:CommonMessage
  type DeleteMessage<:CommonMessage

}
