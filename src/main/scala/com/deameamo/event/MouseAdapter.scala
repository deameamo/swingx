package com.deameamo.event

import java.awt.event._

trait MouseAdapter extends MouseListener with MouseMotionListener with MouseWheelListener {

  def mousePressed(e: MouseEvent): Unit = {}
  def mouseReleased(e: MouseEvent): Unit = {}
  def mouseClicked(e: MouseEvent): Unit = {}
  def mouseEntered(e: MouseEvent): Unit = {}
  def mouseExited(e: MouseEvent): Unit = {}
  def mouseDragged(e: MouseEvent): Unit = {}
  def mouseMoved(e: MouseEvent): Unit = {}
  def mouseWheelMoved(e: MouseWheelEvent): Unit = {}
}