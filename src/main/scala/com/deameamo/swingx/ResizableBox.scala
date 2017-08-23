package com.deameamo.swingx

import java.awt._
import java.awt.event.MouseEvent
import javax.swing.JPanel

import com.deameamo.event.MouseAdapter
import com.deameamo.util.ArrayList

import scala.collection.mutable

class ResizableBox(
    protected var alignment: Int, 
    protected var resizable: Boolean)
    extends JPanel {

  def this() = this(ResizableBox.HORIZONTAL, true)
  def this(alignment: Int) = this(alignment, true)
  
  private var minCellRatio = 0.0
  private var maxCellRatio = 1.0
  private var minCellLength = 10
  private var maxCellLength = 0
  private var defaultCellRatio = 0.1
  private var borderLength = 3
  private var refLength = 100
  private var availableLength = 0
  
  val cells = new ArrayList[Cell]
  protected val borders = new ArrayList[Border]
  private var borderColor: Color = _
  
  var paddings = new Paddings(0, 0, 0, 0)
  
  setLayout(new BoxLayout)
  
// Setters===================================================================== 
  def setMinCellRatio(ratio: Double) {minCellRatio = ratio}
  def setMaxCellRatio(ratio: Double) {maxCellRatio = ratio}
  def setMinCellLength(length: Int) {minCellLength = length}
  def setMaxCellLength(length: Int) {maxCellLength = length}
  def setDefaultCellRatio(ratio: Double) {defaultCellRatio = ratio}
  def setBorderLength(length: Int) {borderLength = length}
  def setBorderColor(color: Color) {borderColor = color}
  def setReferenceLength(length: Int) {refLength = length}
  
  def setAlignment(alignment: Int): Unit = {
    if(this.alignment != alignment) {
      this.alignment = alignment
      validate()
      repaint()
    }
  }
  
  def setResizable(resizable: Boolean) {
    if(this.resizable != resizable) {
      this.resizable = resizable
      borders.foreach(_.setResizable(resizable))
      validate()
      repaint()
    }
  }
  
  def setPaddings(padding: Int) { setPaddings(padding, padding, padding, padding) }
  
  def setPaddings(left: Int, right: Int, top: Int, bottom: Int) {
    paddings.left = left
    paddings.right = right
    paddings.top = top
    paddings.bottom = bottom
  }
  
  def setCellDisplayLength(index: Int, displayLength: Int) {
    val cell = cells.get(index).get
    if(cell.rigid)
      cell.displayLength = displayLength
  }
  
// add & remove ===============================================================
  def addItem(item: Component, initRatio: Double, minRatio: Double, maxRatio: Double) {
    addCell(CellFactory.createCell(item, initRatio, minRatio, maxRatio))
  }
  
  def addItem(item: Component) {
    addItem(item, defaultCellRatio, minCellRatio, maxCellRatio)
  }
  
  def addItem(item: Component, initRatio: Double) {
    addItem(item, initRatio, minCellRatio, maxCellRatio)
  }
  
  def addItem(item: Component, initLength: Int, minLength: Int, maxLength: Int) {
    addCell(CellFactory.createCell(item, initLength.toDouble / refLength.toDouble, minLength, maxLength))
  }
  
  def addItem(item: Component, initLength: Int) {
    addItem(item, initLength.toDouble / refLength.toDouble, minCellRatio, maxCellRatio)
  }
  
  def addRigidItem(item: Component, displayLength: Int) {
    addCell(CellFactory.createRigidCell(item, displayLength))
  }
  
  private def insertCell(index: Int, cell: Cell) {
    if(index == -1) {
      cells += cell
      add(cell.item)
    }
    else {
      if(index < cells.size - 1) {
        borders.get(index).get.prevCell = cell
        val border = BorderFactory.create(cells.get(index).get, cell)
        borders.insertBefore(index, border)
        add(border)
      }
      else {
        val border = BorderFactory.create(cells.last, cell)
        borders += border
        add(border)
      }
      cells.insertAfter(index, cell)
      add(cell.item)
    }
  }
  
  def addCell(cell: Cell) {insertCell(cells.size - 1, cell)}
  
  def addCellDynamically(snapshot: ProportionalCellSnapshot) {
    normalize(1.0 - snapshot.displayRatio)
    addCell(CellFactory.createCell(snapshot))
  }
  
  def removeItem(item: Component) {removeItem(item, autoValidate = true)}
  
  def removeItem(item: Component, autoValidate: Boolean) {
    val index = getCellIndex(item)
    if(index == -1) return
    if(cells.size > 1) {
      if(index == 0) {
        removeBorder(0)
      }
      else if(index == cells.size - 1) {
        removeBorder(borders.size - 1)
      }
      else {
        borders.get(index).get.prevCell = borders.get(index - 1).get.prevCell
        removeBorder(index - 1)
      }
    }
    
    val target = cells.get(index).get
    cells.removeAtIndex(index)
    normalize(1.0 - target.displayRatio)
    if(autoValidate) validate()
    remove(target.item)
    CellFactory.recycle(target)
  }
  
  def removeAllItems() {
    for(_ <- cells.indices)
      removeItem(cells.head.item, autoValidate = false)
    validate()
  }
  
  def makeSnapshots: mutable.MutableList[ProportionalCellSnapshot] = { for(cell <- cells) yield cell.makeSnapshot }
  
  def replaceAllCells(snapshots: mutable.MutableList[ProportionalCellSnapshot]) {
    removeAllItems()
    for(snapshot <- snapshots) {
      addCell(CellFactory.createCell(snapshot))
    }
    validate()
  }
  
  private def removeBorder(index: Int) {
    remove(borders.get(index).get)
    BorderFactory.recycle(borders.get(index).get)
    borders.removeAtIndex(index)
  }
  
// Helpers ====================================================================
  def getCellIndex(item: Component): Int = cells.getElementIndexIf(_.item == item)
  
  def getCell(item: Component): Cell = cells.getElementIf(_.item == item).orNull
  
// layout =====================================================================
  def layoutBox() {
    if(cells.isEmpty) {
      repaint()
      return
    }
    if(getWidth == 0 || getHeight == 0)
      return
    
    val insets = getInsets
    val containerLength = {
      if(alignment == ResizableBox.HORIZONTAL)
        getWidth - insets.left - insets.right - paddings.left - paddings.right
      else
        getHeight - insets.top - insets.bottom - paddings.top - paddings.bottom
    }
    var accBorderLength = 0
    borders.foreach { border => accBorderLength += border.length }
    var accRigidCellLength = 0
    cells.foreach(cell => {
      if(cell.rigid) accRigidCellLength += cell.displayLength
    })
    availableLength = containerLength - accBorderLength - accRigidCellLength
    normalize(1.0)
    cells.foreach(cell => {
      cell.calculateDisplayLength()
    })
    
    if(alignment == ResizableBox.HORIZONTAL)
      layoutContainerHorizontally()
    else
      layoutContainerVertically()
  }
  
  def layoutContainerHorizontally() {
    val insets = getInsets
    var x = insets.left + paddings.left
    val y = insets.top + paddings.top
    val height = getHeight - insets.top - insets.bottom - paddings.top - paddings.bottom
    
    cells.head.item.setBounds(x, y, cells.head.displayLength, height)
    cells.head.offset = x
    x += cells.head.displayLength
    var i = 1
    while(i < cells.size) {
      val border = borders.get(i - 1).get
      border.setBounds(x, y, border.length, height)
      x += border.length
      val cell = cells.get(i).get
      cell.item.setBounds(x, y, cell.displayLength, height)
      cell.offset = x
      x += cell.displayLength
      i += 1
    }
  }
  
  def layoutContainerVertically() {
    val insets = getInsets
    val x = insets.left + paddings.left
    var y = insets.top + paddings.top
    val width = getWidth - insets.left - insets.right - paddings.left - paddings.right
    
    cells.head.item.setBounds(x, y, width, cells.head.displayLength)
    cells.head.offset = y
    y += cells.head.displayLength
    var i = 1
    while(i < cells.size) {
      val border = borders.get(i - 1).get
      border.setBounds(x, y, width, border.length)
      y += border.length
      val cell = cells.get(i).get
      cell.item.setBounds(x, y, width, cell.displayLength)
      cell.offset = y
      y += cell.displayLength
      i += 1
    }
  }
  
  private def normalize(availableRatio: Double) {
    var accRatio = 0.0
    cells.foreach(cell => {
      if(!cell.rigid)
        accRatio += cell.displayRatio
    })
    val proportion = availableRatio / accRatio
    cells.foreach(cell => {
      if(!cell.rigid)
        cell.displayRatio = cell.displayRatio * proportion
    })
  }
  
// Inner classes ==============================================================
  class Cell {
    var item: Component = _
    var rigid = false
    var displayRatio = 0.0
    var minRatio = 0.0
    var maxRatio = 0.0
    var minLength = 0
    var maxLength = 0
    var offset = 0
    var displayLength = 0
    var displayMinLength = 0
    var displayMaxLength = 0
    
    def makeSnapshot: ProportionalCellSnapshot = ProportionalCellSnapshot(item, rigid, displayRatio, minRatio, maxRatio, minLength, maxLength,
        offset, displayLength, displayMinLength, displayMaxLength)
    
    def takeSnapshot(snapshot: ProportionalCellSnapshot) {
      item = snapshot.item
      rigid = snapshot.rigid
      displayRatio = snapshot.displayRatio
      minRatio = snapshot.minRatio
      maxRatio = snapshot.maxRatio
      minLength = snapshot.minLength
      maxLength = snapshot.maxLength
      offset = snapshot.offset
      displayLength = snapshot.displayLength
      displayMinLength = snapshot.displayMinLength
      displayMaxLength = snapshot.displayMaxLength
    }
    
    def calculateDisplayLength() {
      if(rigid)
        return
      displayLength = (displayRatio * availableLength).toInt
      displayMinLength = {
        if(minLength != 0) minLength
        else if(minRatio != 0.0) (minRatio * availableLength).toInt
        else if(minCellLength != 0) minCellLength
        else (minCellRatio * availableLength).toInt
      }
      displayMaxLength = {
        if(maxLength != 0) maxLength
        else if(maxRatio != 0.0) (maxRatio * availableLength).toInt
        else if(maxCellLength != 0) maxCellLength
        else (maxCellRatio * availableLength).toInt
      }
      if(displayLength < displayMinLength) displayLength = displayMinLength
      if(displayLength > displayMaxLength) displayLength = displayMaxLength
    }
    
    def canUpdate(lengthDiff: Int): Boolean = {
      if(lengthDiff < 0) displayLength + lengthDiff > displayMinLength
      else displayLength + lengthDiff < displayMaxLength
    }
    
    def update(offsetDiff: Int, lengthDiff: Int) {
      offset += offsetDiff
      displayLength += lengthDiff
      displayRatio = displayLength.toDouble / availableLength.toDouble
      val bounds = item.getBounds()
      if(alignment == ResizableBox.HORIZONTAL) {
        bounds.x = offset
        bounds.width = displayLength
      }
      else {
        bounds.y = offset
        bounds.height = displayLength
      }
      item.setBounds(bounds)
      item.validate()
    }
    
    def isPointInPreHalf(p: Int): Boolean = (p >= offset) && (p <= offset + displayLength / 2)
    
    def isPointInPostHalf(p: Int): Boolean = (p >= offset + displayLength / 2) && (p <= offset + displayLength)
  }
  
  object CellFactory {
    var cache = new mutable.Queue[Cell]
    
    val defaultSnapshot = ProportionalCellSnapshot(null, rigid = false, defaultCellRatio, minCellRatio, maxCellRatio, minCellLength, maxCellLength, 0, 0, 0, 0)
    
    def createCell(snapshot: ProportionalCellSnapshot): Cell = {
      val cell = createEmptyCell
      cell.takeSnapshot(snapshot)
      cell
    }
    
    def createCell(item: Component, currRatio: Double, minRatio: Double, maxRatio: Double): Cell = {
      val cell = createEmptyCell
      cell.item = item
      cell.rigid = false
      cell.displayRatio = currRatio
      cell.minRatio = minRatio
      cell.maxRatio = maxRatio
      cell
    }
    
    def createCell(item: Component, currRatio: Double, minLength: Int, maxLength: Int): Cell = {
      val cell = createEmptyCell
      cell.item = item
      cell.rigid = false
      cell.displayRatio = currRatio
      cell.minLength = minLength
      cell.maxLength = maxLength
      cell
    }
    
    def createRigidCell(item: Component, displayLength: Int): Cell = {
      val cell = createEmptyCell
      cell.item = item
      cell.rigid = true
      cell.displayLength = displayLength
      cell
    }
    
    def recycle(cell: Cell) {cache.enqueue(cell)}
    
    private def createEmptyCell: Cell = {
      val cell = { if(cache.isEmpty) new Cell else cache.dequeue }
      cell.takeSnapshot(defaultSnapshot)
      cell
    }
  }
  
  class Border(var prevCell: Cell, var nextCell: Cell, var length: Int) extends JPanel with MouseAdapter {
    
    var originalX: Int = -1
    var originalY: Int = -1
    var listenerAdded = false

    def setResizable(resizable: Boolean) {
      if(resizable) {
        if(!listenerAdded) {
          listenerAdded = true
          if(alignment == ResizableBox.HORIZONTAL)
            setCursor(ResizableBox.HORIZONTAL_CURSOR)
          else
            setCursor(ResizableBox.VERTICAL_CURSOR)
          addMouseListener(this)
        }
      }
      else {
        if(listenerAdded) {
          listenerAdded = false
          setCursor(ResizableBox.DEFAULT_CURSOR)
          removeMouseListener(this)
        }
      }
    }

    override def mousePressed(e: MouseEvent): Unit = {
      originalX = e.getX
      originalY = e.getY
      addMouseMotionListener(this)
    }

    override def mouseReleased(e: MouseEvent): Unit = {
      removeMouseMotionListener(this)
    }

    override def mouseDragged(e: MouseEvent): Unit = {
      if(alignment == ResizableBox.HORIZONTAL) {
        val diff = e.getX - originalX
        if(prevCell.canUpdate(diff) && nextCell.canUpdate(-diff)) {
          val borderBounds = getBounds()
          borderBounds.x += diff
          setBounds(borderBounds)
          prevCell.update(0, diff)
          nextCell.update(diff, -diff)
        }
      }
      else {
        val diff = e.getY - originalY
        if(prevCell.canUpdate(diff) && nextCell.canUpdate(-diff)) {
          val borderBounds = getBounds()
          borderBounds.y += diff
          setBounds(borderBounds)
          prevCell.update(0, diff)
          nextCell.update(diff, -diff)
        }
      }
    }
  }
  
  private object BorderFactory {
    var cache = new mutable.Queue[Border]
    
    def create(prevCell: Cell, nextCell: Cell): Border = {
      if(cache.isEmpty) {
        val border = new Border(prevCell, nextCell, borderLength)
        border.setResizable(resizable)
        border.setBackground(borderColor)
        border
      }
      else {
        val border = cache.dequeue()
        border.prevCell = prevCell
        border.nextCell = nextCell
        border.length = borderLength
        border.setResizable(resizable)
        border.setBackground(borderColor)
        border
      }
    }
    
    def recycle(border: Border) {
      if(resizable)
        border.setResizable(false)
      cache.enqueue(border)
    }
  }
  
  class BoxLayout extends LayoutManager {
    def layoutContainer(container: Container): Unit = layoutBox()
    def addLayoutComponent(name: String, component: Component): Unit = {}
    def removeLayoutComponent(component: Component): Unit = {}
    def minimumLayoutSize(container: Container): Dimension = { null }
    def preferredLayoutSize(container: Container): Dimension = { null }
  }
}

case class ProportionalCellSnapshot(
    var item: Component,
    var rigid: Boolean,
    var displayRatio: Double,
    var minRatio: Double,
    var maxRatio: Double,
    var minLength: Int,
    var maxLength: Int,
    var offset: Int,
    var displayLength: Int,
    var displayMinLength: Int,
    var displayMaxLength: Int)

class Paddings(var left: Int, var right: Int, var top: Int, var bottom: Int)

object ResizableBox {
  val HORIZONTAL = 0
  val VERTICAL = 1
  
  val HORIZONTAL_CURSOR = new Cursor(Cursor.E_RESIZE_CURSOR)
  val VERTICAL_CURSOR = new Cursor(Cursor.N_RESIZE_CURSOR)
  val DEFAULT_CURSOR = new Cursor(Cursor.DEFAULT_CURSOR)
}