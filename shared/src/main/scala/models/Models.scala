package models


//NOTE: not yet used anywhere
//case class User(nick: String, gravatarHash: String)

case class User(nick:String,avatar:String="")

//object User {
//  val System = User("<system>", "cd2aba324ee144fbe4066e0e2ee9966a") // Scala.js' gravatar
//  val Nobody = User("<nobody>", "") // better than null
//}
case class Message(user: User, text: String, timestamp: Long = System.currentTimeMillis())

case class Room(name: String)
