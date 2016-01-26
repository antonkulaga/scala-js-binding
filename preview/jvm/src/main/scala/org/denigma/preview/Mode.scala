package org.denigma.preview

object Mode {
  import com.typesafe.config.ConfigFactory
  lazy val config = ConfigFactory.load()
  lazy val current = if (config.hasPath("app.mode")) config.getString("app.mode") match {
    case "production" => "-opt.js"
    case _ => "-fastopt.js"
  } else  "-fastopt.js" //"-opt.js"

}