package com.deameamo.event

import java.awt.event.{ActionEvent, ActionListener}

import com.deameamo.util.ArrayList

trait CanFireActionEvent {
  
  val listeners = new ArrayList[ActionListener]
  
  def addActionListener(l: ActionListener) {
    listeners += l
  }
  
  def removeActionListener(l: ActionListener) {
    listeners -= l
  }
  
  def fireActionEvent(command: String) {
    fireActionEvent(new ActionEvent(this, 0, command))
  }
  
  def fireActionEvent(e: ActionEvent) {
    listeners.foreach(_.actionPerformed(e))
  }
}