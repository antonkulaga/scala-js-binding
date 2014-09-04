package org.denigma.binding

/**
 * Useful implicit classes
 */
package object macroses {
  def asMap[T: ClassToMap](t: T) =  implicitly[ClassToMap[T]].asMap(t)
  def asStringRxMap[T: StringRxMap](t: T) =  implicitly[StringRxMap[T]].asStringRxMap(t)
  def asTagRxMap[T: TagRxMap](t: T) =  implicitly[TagRxMap[T]].asTagRxMap(t)
  def asListRxMap[T: ListRxMap](t: T) =  implicitly[ListRxMap[T]].asListRxMap(t)
  def asBooleanRxMap[T: BooleanRxMap](t: T) =  implicitly[BooleanRxMap[T]].asBooleanRxMap(t)

  def asEventMap[T: EventMap](t: T) =  implicitly[EventMap[T]].asEventMap(t)
  def asMouseEventMap[T: MouseEventMap](t: T) =  implicitly[MouseEventMap[T]].asMouseEventMap(t)
  def asTextEventMap[T: TextEventMap](t: T) =  implicitly[TextEventMap[T]].asTextEventMap(t)
  def asKeyEventMap[T: KeyEventMap](t: T) =  implicitly[KeyEventMap[T]].asKeyEventMap(t)
  def asUIEventMap[T: UIEventMap](t: T) =  implicitly[UIEventMap[T]].asUIEventMap(t)
  def asWheelEventMap[T: WheelEventMap](t: T) =  implicitly[WheelEventMap[T]].asWheelEventMap(t)
  def asFocusEventMap[T: FocusEventMap](t: T) =  implicitly[FocusEventMap[T]].asFocusEventMap(t)






}
