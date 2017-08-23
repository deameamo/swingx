package com.deameamo.event

import java.awt.event.{MouseEvent, MouseMotionListener}

trait MouseMotionAdapter extends MouseMotionListener {

  def mouseDragged(e: MouseEvent): Unit = {}
  def mouseMoved(e: MouseEvent): Unit = {}
}