package com.deameamo.swingx

import java.awt.event.MouseEvent
import javax.swing.{BorderFactory, JLabel, SwingConstants}

import com.deameamo.event.{CanFireActionEvent, MouseAdapter}

class DockedItem(var dockable: Dockable, var snapshot: ProportionalCellSnapshot)
  extends ResizableBox(ResizableBox.VERTICAL, false) with Relocable with CanFireActionEvent {
  
  setBorder(BorderFactory.createRaisedBevelBorder())
  addHandle(this)
  
  setBorderLength(0)
  val label = new JLabel
  label.setHorizontalAlignment(SwingConstants.CENTER)
    label.setText(dockable.title)
  val icon = new Icon
  icon.setImage(dockable.bigIcon)
  addRigidItem(label, 16)
  addItem(icon)
  
  var dragged = false
  
  override def mousePressed(e: MouseEvent): Unit = {
    super.mousePressed(e)
    setBorder(BorderFactory.createLoweredBevelBorder())
    dragged = false
  }
  
  override def mouseReleased(e: MouseEvent): Unit = {
    super.mouseReleased(e)
    setBorder(BorderFactory.createRaisedBevelBorder())
    if(!dragged)
      fireActionEvent(DockedItem.DOCKED_ITEM_CLICKED)
  }
  
  override def mouseDragged(e: MouseEvent): Unit = {
    super.mouseDragged(e)
    dragged = true
  }
  
  override def mouseEntered(e: MouseEvent): Unit = {
    super.mouseEntered(e)
//    setBorder(BorderFactory.createRaisedBevelBorder())
  }
  
  override def mouseExited(e: MouseEvent): Unit = {
    super.mouseExited(e)
//    setBorder(BorderFactory.createEtchedBorder())
  }
}

object DockedItem {
  val DOCKED_ITEM_CLICKED = "DOCKED_ITEM_CLICKED"
}