package com.deameamo.swingx

import javax.swing.ImageIcon

import com.deameamo.event.CanFireActionEvent

trait Dockable extends CanFireActionEvent {
  
  val title: String
  
  val smallIcon: ImageIcon
  
  val bigIcon: ImageIcon
  
  def switchButton()
  
  def minimizeButtonClicked()
  
  def maximizeButtonClicked()
  
  def restoreButtonClicked()
}

object WindowAction {
  val MINIMIZE = "WINDOW_MINIMIZE"
  val MAXIMIZE = "WINDOW_MAXIMIZE"
  val CLOSE = "WINDOW_CLOSE"
  val RESTORE = "WINDOW_RESTORE"
}