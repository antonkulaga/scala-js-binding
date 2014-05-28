package importedjs.CodeMirror

import scala.scalajs.js
import org.scalajs.dom.{Event, HTMLTextAreaElement, HTMLElement}
import scala.scalajs.js.annotation.JSName
import scala.scalajs.js._


trait Editor extends js.Object {
  def hasFocus(): js.Boolean = ???
  def findPosH(start: Position, amount: js.Number, unit: js.String, visually: js.Boolean): js.Any = ???
  def findPosV(start: Position, amount: js.Number, unit: js.String): js.Any = ???
  def setOption(option: js.String, value: js.Any): Unit = ???
  def getOption(option: js.String): js.Dynamic = ???
  def addKeyMap(map: js.Any, bottom: js.Boolean = ???): Unit = ???
  def removeKeyMap(map: js.Any): Unit = ???
  def addOverlay(mode: js.Any, options: js.Any = ???): Unit = ???
  def removeOverlay(mode: js.Any): Unit = ???
  def getDoc(): Doc = ???
  def swapDoc(doc: Doc): Doc = ???
  def setGutterMarker(line: js.Any, gutterID: js.String, value: HTMLElement): LineHandle = ???
  def clearGutter(gutterID: js.String): Unit = ???
  def addLineClass(line: js.Any, where: js.String, _clazz: js.String): LineHandle = ???
  def removeLineClass(line: js.Any, where: js.String, clazz: js.String): LineHandle = ???
  def lineInfo(line: js.Any): js.Any = ???
  def addWidget(pos: Position, node: HTMLElement, scrollIntoView: js.Boolean): Unit = ???
  def addLineWidget(line: js.Any, node: HTMLElement, options: js.Any = ???): LineWidget = ???
  def setSize(width: js.Any, height: js.Any): Unit = ???
  def scrollTo(x: js.Number, y: js.Number): Unit = ???
  def getScrollInfo(): js.Any = ???
  def scrollIntoView(pos: Position, margin: js.Number = ???): Unit = ???
  def cursorCoords(where: js.Boolean, mode: js.String): js.Any = ???
  def charCoords(pos: Position, mode: js.String): js.Any = ???
  def coordsChar(`object`: js.Any, mode: js.String = ???): Position = ???
  def defaultTextHeight(): js.Number = ???
  def defaultCharWidth(): js.Number = ???
  def getViewport(): js.Any = ???
  def refresh(): Unit = ???
  def getTokenAt(pos: Position): js.Any = ???
  def getStateAfter(line: js.Number = ???): js.Dynamic = ???
  def operation[T](fn: js.Function0[T]): T = ???
  def indentLine(line: js.Number, dir: js.String = ???): Unit = ???
  def focus(): Unit = ???
  def getInputField(): HTMLTextAreaElement = ???
  def getWrapperElement(): HTMLElement = ???
  def getScrollerElement(): HTMLElement = ???
  def getGutterElement(): HTMLElement = ???
  def on(eventName: js.String, handler: js.Function1[Editor, Unit]): Unit = ???
  def off(eventName: js.String, handler: js.Function1[Editor, Unit]): Unit = ???
}

@JSName("Doc")
class Doc protected () extends js.Object {
  def this(text: js.String, mode: js.Any = ???, firstLineNumber: js.Number = ???) = this()
  def getValue(seperator: js.String = ???): js.String = ???
  def setValue(content: js.String): Unit = ???
  def getRange(from: Position, to: Position, seperator: js.String = ???): js.String = ???
  def replaceRange(replacement: js.String, from: Position, to: Position): Unit = ???
  def getLine(n: js.Number): js.String = ???
  def setLine(n: js.Number, text: js.String): Unit = ???
  def removeLine(n: js.Number): Unit = ???
  def lineCount(): js.Number = ???
  def firstLine(): js.Number = ???
  def lastLine(): js.Number = ???
  def getLineHandle(num: js.Number): LineHandle = ???
  def getLineNumber(handle: LineHandle): js.Number = ???
  def eachLine(f: js.Function1[LineHandle, Unit]): Unit = ???
  def eachLine(start: js.Number, end: js.Number, f: js.Function1[LineHandle, Unit]): Unit = ???
  def markClean(): Unit = ???
  def isClean(): js.Boolean = ???
  def getSelection(): js.String = ???
  def replaceSelection(replacement: js.String, collapse: js.String = ???): Unit = ???
  def getCursor(start: js.String = ???): Position = ???
  def somethingSelected(): js.Boolean = ???
  def setCursor(pos: Position): Unit = ???
  def setSelection(anchor: Position, head: Position): Unit = ???
  def extendSelection(from: Position, to: Position = ???): Unit = ???
  def setExtending(value: js.Boolean): Unit = ???
  def getEditor(): Editor = ???
  def copy(copyHistory: js.Boolean): Doc = ???
  def linkedDoc(options: js.Any): Doc = ???
  def unlinkDoc(doc: Doc): Unit = ???
  def iterLinkedDocs(fn: js.Function2[Doc, js.Boolean, Unit]): Unit = ???
  def undo(): Unit = ???
  def redo(): Unit = ???
  def historySize(): js.Any = ???
  def clearHistory(): Unit = ???
  def getHistory(): js.Dynamic = ???
  def setHistory(history: js.Any): Unit = ???
  def markText(from: Position, to: Position, options: TextMarkerOptions = ???): TextMarker = ???
  def setBookmark(pos: Position, options: js.Any = ???): TextMarker = ???
  def findMarksAt(pos: Position): js.Array[TextMarker] = ???
  def getAllMarks(): js.Array[TextMarker] = ???
  def getMode(): js.Dynamic = ???
  def posFromIndex(index: js.Number): Position = ???
  def indexFromPos(`object`: Position): js.Number = ???
}

trait LineHandle extends js.Object {
  var text: js.String = ???
}

trait TextMarker extends js.Object {
  def clear(): Unit = ???
  def find(): Position = ???
  def getOptions(copyWidget: js.Boolean): TextMarkerOptions = ???
}

trait LineWidget extends js.Object {
  def clear(): Unit = ???
  def changed(): Unit = ???
}

trait EditorChange extends js.Object {
  var from: Position = ???
  var to: Position = ???
  var text: js.Array[js.String] = ???
  var removed: js.String = ???
}

trait EditorChangeLinkedList extends EditorChange {
  var next: EditorChangeLinkedList = ???
}

trait EditorChangeCancellable extends EditorChange {
  def update(from: Position = ???, to: Position = ???, text: js.String = ???): Unit = ???
  def cancel(): Unit = ???
}

trait Position extends js.Object {
  var ch: js.Number = ???
  var line: js.Number = ???
}

trait EditorConfiguration extends js.Object {
  var value: js.Any = ???
  var mode: js.Any = ???
  var theme: js.String = ???
  var indentUnit: js.Number = ???
  var smartIndent: js.Boolean = ???
  var tabSize: js.Number = ???
  var indentWithTabs: js.Boolean = ???
  var electricChars: js.Boolean = ???
  var rtlMoveVisually: js.Boolean = ???
  var keyMap: js.String = ???
  var extraKeys: js.Any = ???
  var lineWrapping: js.Boolean = ???
  var lineNumbers: js.Boolean = ???
  var firstLineNumber: js.Number = ???
  var lineNumberFormatter: js.Function1[js.Number, js.String] = ???
  var gutters: js.Array[js.String] = ???
  var fixedGutter: js.Boolean = ???
  var readOnly: js.Any = ???
  var showCursorWhenSelecting: js.Boolean = ???
  var undoDepth: js.Number = ???
  var historyEventDelay: js.Number = ???
  var tabindex: js.Number = ???
  var autofocus: js.Boolean = ???
  var dragDrop: js.Boolean = ???
  var onDragEvent: js.Function2[Editor, Event, js.Boolean] = ???
  var onKeyEvent: js.Function2[Editor, Event, js.Boolean] = ???
  var cursorBlinkRate: js.Number = ???
  var cursorHeight: js.Number = ???
  var workTime: js.Number = ???
  var workDelay: js.Number = ???
  var pollInterval: js.Number = ???
  var flattenSpans: js.Boolean = ???
  var maxHighlightLength: js.Number = ???
  var viewportMargin: js.Number = ???
}

trait TextMarkerOptions extends js.Object {
  var className: js.String = ???
  var inclusiveLeft: js.Boolean = ???
  var inclusiveRight: js.Boolean = ???
  var atomic: js.Boolean = ???
  var collapsed: js.Boolean = ???
  var clearOnEnter: js.Boolean = ???
  var replacedWith: HTMLElement = ???
  var readOnly: js.Boolean = ???
  var addToHistory: js.Boolean = ???
  var startStyle: js.String = ???
  var endStyle: js.String = ???
  var shared: js.Boolean = ???
}
import js.annotation._

@JSName("CodeMirror")
object CodeMirror extends js.Object {
  var Pass: js.Any = ???
  def fromTextArea(host: HTMLTextAreaElement, options: EditorConfiguration = ???): Editor = ???
  //def fromTextArea(host: HTMLTextAreaElement, options: js.Any): Editor = ???

  var version: js.String = ???
  def defineExtension(name: js.String, value: js.Any): Unit = ???
  def defineDocExtension(name: js.String, value: js.Any): Unit = ???
  def defineOption(name: js.String, default: js.Any, updateFunc: js.Function): Unit = ???
  def defineInitHook(func: js.Function): Unit = ???
  def on(element: js.Any, eventName: js.String, handler: js.Function): Unit = ???
  def off(element: js.Any, eventName: js.String, handler: js.Function): Unit = ???
}