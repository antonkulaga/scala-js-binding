package org.denigma.semantic.styles

import scalacss.Defaults._

trait Colors {
  self: StyleSheet.Standalone =>

  import dsl._

  val bindingYellow = rgba(255,255,0,0.25)
  val bindingGreen = rgba(84,223,0,0.46)
  val bindingBlue = rgba(150,225,255,0.26)
  val bindingViolet = rgba(150,90,120,0.26)



  /*---  Colors  ---*/
  val red              = c"#DB2828"
  val orange           = c"#F2711C"
  val yellow           = c"#FBBD08"
  val olive            = c"#B5CC18"
  val green            = c"#21BA45"
  val teal             = c"#00B5AD"
  val blue             = c"#2185D0"
  val violet           = c"#6435C9"
  val purple           = c"#A333C8"
  val pink             = c"#E03997"
  val brown            = c"#A5673F"
  val grey             = c"#767676"
  val black            = c"#1B1C1D"

  /*---  Light Colors  ---*/
  val lightRed         = c"#FF695E"
  val lightOrange      = c"#FF851B"
  val lightYellow      = c"#FFE21F"
  val lightOlive       = c"#D9E778"
  val lightGreen       = c"#2ECC40"
  val lightTeal        = c"#6DFFFF"
  val lightBlue        = c"#54C8FF"
  val lightViolet      = c"#A291FB"
  val lightPurple      = c"#DC73FF"
  val lightPink        = c"#FF8EDF"
  val lightBrown       = c"#D67C1C"
  val lightGrey        = c"#DCDDDE"
  val lightBlack       = c"#545454"

  /*---   Neutrals  ---*/
  val fullBlack        = c"#000000"
  val offWhite         = c"#F9FAFB"
  val darkWhite        = c"#F3F4F5"
  val midWhite         = c"#DCDDDE"
  val white            = c"#FFFFFF"

  /*--- Colored Backgrounds ---*/
  val redBackground    = c"#FFE8E6"
  val orangeBackground = c"#FFEDDE"
  val yellowBackground = c"#FFF8DB"
  val oliveBackground  = c"#FBFDEF"
  val greenBackground  = c"#E5F9E7"
  val tealBackground   = c"#E1F7F7"
  val blueBackground   = c"#DFF0FF"
  val violetBackground = c"#EAE7FF"
  val purpleBackground = c"#F6E7FF"
  val pinkBackground   = c"#FFE3FB"
  val brownBackground  = c"#F1E2D3"

  /*--- Colored Text ---*/
  val redTextColor    =  red
  val orangeTextColor =  orange
  val yellowTextColor = c"#B58105" // Yellow text is difficult to read
  val oliveTextColor  = c"#8ABC1E" // Olive is difficult to read
  val greenTextColor  = c"#1EBC30" // Green is difficult to read
  val tealTextColor   = c"#10A3A3" // Teal text is difficult to read
  val blueTextColor   = blue
  val violetTextColor = violet
  val purpleTextColor = purple
  val pinkTextColor   = pink
  val brownTextColor  = brown


  /*******************************
           Power-User
    *******************************/


  /*-------------------
      Emotive Colors
  --------------------*/

  /* Positive */
  val positiveColor           = green
  val positiveBackgroundColor = c"#FCFFF5"
  val positiveBorderColor     = c"#A3C293"
  val positiveHeaderColor     = c"#1A531B"
  val positiveTextColor       = c"#2C662D"

  /* Negative */
  val negativeColor           = red
  val negativeBackgroundColor = c"#FFF6F6"
  val negativeBorderColor     = c"#E0B4B4"
  val negativeHeaderColor     = c"#912D2B"
  val negativeTextColor       = c"#9F3A38"

  /* Info */
  val infoColor              = c"#31CCEC"
  val infoBackgroundColor    = c"#F8FFFF"
  val infoBorderColor        = c"#A9D5DE"
  val infoHeaderColor        = c"#0E566C"
  val infoTextColor          = c"#276F86"

  /* Warning */
  val warningColor           = c"#F2C037"
  val warningBorderColor     = c"#C9BA9B"
  val warningBackgroundColor = c"#FFFAF3"
  val warningHeaderColor     = c"#794B02"
  val warningTextColor       = c"#573A08"

}
