package com.deameamo.swingx

import java.awt.Component
import java.awt.event.MouseEvent

import com.deameamo.event.MouseAdapter
import com.deameamo.util.ArrayList

trait Relocable extends MouseAdapter {
  
  private val listeners = new ArrayList[RelocableMotionListener]
  
  def addHandle(handle: Component) {
    handle.addMouseListener(this)
  }
  
  def addRelocableMotionListener(l: RelocableMotionListener) { listeners += l }
  
  def removeRelocableMotionListener(l: RelocableMotionListener) { listeners -= l }
  
  override def mousePressed(e: MouseEvent): Unit = {
    listeners.foreach { l => l.relocablePressed(RelocableMotionEvent(this.asInstanceOf[Component], e)) }
    e.getSource.asInstanceOf[Component].addMouseMotionListener(this)
  }
  
  override def mouseReleased(e: MouseEvent): Unit = {
    listeners.foreach { l => l.relocableReleased(RelocableMotionEvent(this.asInstanceOf[Component], e)) }
    e.getSource.asInstanceOf[Component].removeMouseMotionListener(this)
  }
  
  override def mouseDragged(e: MouseEvent): Unit = {
    listeners.foreach { l => l.relocableDragged(RelocableMotionEvent(this.asInstanceOf[Component], e)) }
  }
}

trait RelocableMotionListener {
  
  def relocablePressed(event: RelocableMotionEvent)
  
  def relocableDragged(event: RelocableMotionEvent)
  
  def relocableReleased(event: RelocableMotionEvent)
}

case class RelocableMotionEvent(item: Component, mouseEvent: MouseEvent)