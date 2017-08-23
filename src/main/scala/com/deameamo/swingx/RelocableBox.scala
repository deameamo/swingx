package com.deameamo.swingx

import java.awt._
import java.awt.image.BufferedImage
import javax.swing.JPanel

import com.deameamo.util.ArrayList

class RelocableBox(alignment: Int, resizable: Boolean)
    extends ResizableBox(alignment: Int, resizable: Boolean) with RelocableMotionListener {
  
  def this() = this(ResizableBox.HORIZONTAL, true)
  def this(alignment: Int) = this(alignment, true)
  
  var relocable: Boolean = true
  
  var phantom: DefaultPhantom = _
  
  var originalX: Int = -1
  var originalY: Int = -1
  var originalPhantomBounds: Rectangle = _
  
  val hiddenCells = new ArrayList[Cell]
  
  setPhantom(new DefaultPhantom(this))
  
  def setRelocable(relocable: Boolean) { this.relocable = relocable }
  
  def setPhantom(newPhantom: DefaultPhantom) {
    if(phantom != null)
      remove(phantom)
    phantom = newPhantom
    phantom.disappear()
    add(phantom)
  }
  
  def addItem(item: Relocable) {
    super.addItem(item.asInstanceOf[Component])
    item.addRelocableMotionListener(this)
  }
  
  def addItem(item: Relocable, initRatio: Double) {
    super.addItem(item.asInstanceOf[Component], initRatio)
    item.addRelocableMotionListener(this)
  }
  
  def addItem(item: Relocable, initLength: Int) {
    super.addItem(item.asInstanceOf[Component], initLength)
    item.addRelocableMotionListener(this)
  }
  
  def removeItem(item: Relocable, autoValidate: Boolean) {
    super.removeItem(item.asInstanceOf[Component], autoValidate)
    item.removeRelocableMotionListener(this)
  }
  
  def switchCell(aIndex: Int, bIndex: Int) {
    if(aIndex == bIndex)
      return
    if(aIndex > bIndex) {
      switchCell(bIndex, aIndex)
      return
    }
    val aCell = cells.get(aIndex).get
    val bCell = cells.get(bIndex).get
    borders.get(aIndex).get.prevCell = bCell
    if(aIndex > 0)
      borders.get(aIndex - 1).get.nextCell = bCell
    borders.get(bIndex - 1).get.nextCell = aCell
    if(bIndex < borders.size)
      borders.get(bIndex).get.prevCell = aCell
    cells.update(aIndex, bCell)
    cells.update(bIndex, aCell)
    validate()
  }
  
  def relocablePressed(event: RelocableMotionEvent) {
    if(!relocable || cells.size == 1) return
    if(alignment == ResizableBox.HORIZONTAL)
      originalX = event.mouseEvent.getX
    else
      originalY = event.mouseEvent.getY
    phantom.mimic(event.item)
    originalPhantomBounds = event.item.getBounds()
  }
  
  def relocableDragged(event: RelocableMotionEvent) {
    if(!relocable || cells.size == 1) return
    phantom.appear()
    val bounds = phantom.getBounds()
    if(alignment == ResizableBox.HORIZONTAL)
      bounds.x = originalPhantomBounds.x + event.mouseEvent.getX - originalX
    else
      bounds.y = originalPhantomBounds.y + event.mouseEvent.getY - originalY
    phantom.setBounds(bounds)
    
    if(alignment == ResizableBox.HORIZONTAL) {
      val x = originalPhantomBounds.x + event.mouseEvent.getX - originalX
      var i = 0
      var found = false
      if(x < originalPhantomBounds.x) {
        while(i < cells.size && !found) {
          if(cells.get(i).get.isPointInPreHalf(x))
            found = true
          else
            i += 1
        }
      }
      else {
        while(i < cells.size && !found) {
          if(cells.get(i).get.isPointInPostHalf(x + originalPhantomBounds.width))
            found = true
          else
            i += 1
        }
      }
      if(found) {
        switchCell(getCellIndex(event.item), i)
        originalPhantomBounds = event.item.getBounds()
      }
    }
    else {
      val y = originalPhantomBounds.y + event.mouseEvent.getY - originalY
      var i = 0
      var found = false
      if(y < originalPhantomBounds.y) {
        while(i < cells.size && !found) {
          if(cells.get(i).get.isPointInPreHalf(y))
            found = true
          else
            i += 1
        }
      }
      else {
        while(i < cells.size && !found) {
          if(cells.get(i).get.isPointInPostHalf(y + originalPhantomBounds.height))
            found = true
          else
            i += 1
        }
      }
      if(found) {
        switchCell(getCellIndex(event.item), i)
        originalPhantomBounds = event.item.getBounds()
      }
    }
    
  }
  
  def relocableReleased(event: RelocableMotionEvent) {
    if(!relocable || cells.size == 1) return
    phantom.disappear()
  }
}

class DefaultPhantom(val box: RelocableBox) extends JPanel {
  
  var phantom: BufferedImage = _
  
  val composite: AlphaComposite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f)
  
  override def paintComponent(g: Graphics) {
    if(phantom == null) return
    val g2 = g.asInstanceOf[Graphics2D]
    g2.setComposite(composite)
    g2.drawImage(phantom, 0, 0, null)
  }
  
  def mimic(item: Component) {
    phantom = new BufferedImage(item.getWidth, item.getHeight, BufferedImage.TYPE_INT_ARGB)
    item.paint(phantom.getGraphics)
    setBounds(item.getBounds())
  }
  
  def appear() {
    setVisible(true)
  }
  
  def disappear() {
    setVisible(false)
  }
}
