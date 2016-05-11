package org.denigma.preview.communication

import akka.actor.{Actor, ActorLogging, ActorRef}

class RoomActor(channel: String) extends Actor with ActorLogging{

  var participants: Map[String, ActorRef] = Map.empty[String, ActorRef]

  override def receive: Receive = {
    case SocketMessages.UserJoined(name, _, actorRef, time) =>
      participants += name -> actorRef
      //broadcast(SystemMessage(s"User $name joined channel..."))
      //self ! RoomMessages.Broadcast(_, message, name, true)
      log.info(s"User $name joined channel[$channel]")
      //actorRef ! Connected(name, channel, Nil)


    case SocketMessages.UserLeft(name, _, time) =>
      //broadcast(SystemMessage(s"User $name left channel[$roomId]"))
      participants.get(name) match {
        case Some(ref)=>
          //ref ! Disconnected(name, channel)
          participants -= name
          log.info(s"User $name left channel[$channel]")

        case None=>
          log.error(s"Non existing participant $name left the channel $channel")
      }


    case msg @ SocketMessages.IncomingMessage(_, username, message, time) =>
      participants.get(username) match
      {
        case Some(user) => user ! msg
        case None =>
          log.error(s"message for nonexistent participant $username")
      }

    case msg @ RoomMessages.Broadcast(_, message, senderName, includeSender) =>
      val to = if(includeSender) participants else participants.filterNot(_._1==senderName)
      to.values.foreach(_ ! message)
    //broadcast(msg) //TODO: fix
  }

}
