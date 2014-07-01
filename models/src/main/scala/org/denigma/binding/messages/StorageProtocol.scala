package org.denigma.binding.messages

import org.scalax.semweb.messages.StorageMessage


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

trait ModelStorage {

  trait ModelMessage extends StorageMessage
}
