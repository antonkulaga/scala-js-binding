package org.denigma.preview
object Mode {
  import com.typesafe.config.ConfigFactory
  lazy val config = ConfigFactory.load()

  lazy val current = sys.env.get("APP_MODE").map(mode2file).getOrElse("-fastopt.js")

  protected def mode2file(mode: String) = mode match {
    case str if str.startsWith("prod") => "-opt.js"
    case _ => "-fastopt.js"
  }

}