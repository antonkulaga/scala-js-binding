package org.denigma.binding.binders

import org.denigma.binding.macroses._
import org.denigma.binding.views.BindableView
import rx.{Rx, Var}

import scala.collection.immutable._


class MapItemsBinder[View<:BindableView](view: View, reactiveMap: Map[String, Var[String]])
                                        (implicit
                                         mpMap: MapRxMap[View], mpTag: TagRxMap[View],
                                         mpString: StringRxMap[View],  mpBool: BooleanRxMap[View],
                                         mpDouble: DoubleRxMap[View],  mpInt: IntRxMap[View],
                                         mpEvent: EventMap[View],  mpMouse: MouseEventMap[View],
                                         mpText: TextEventMap[View], mpKey: KeyEventMap[View],
                                         mpUI: UIEventMap[View], mpWheel: WheelEventMap[View], mpFocus: FocusEventMap[View]
                                          )
  extends GeneralBinder(view)(mpMap, mpTag, mpString, mpBool, mpDouble, mpInt, mpEvent, mpMouse, mpText, mpKey, mpUI, mpWheel, mpFocus) {

  override lazy val strings: Map[String, Rx[String]] = mpString.asStringRxMap(view) ++ reactiveMap

}
