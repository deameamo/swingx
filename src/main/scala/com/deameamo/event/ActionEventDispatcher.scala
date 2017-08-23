package com.deameamo.event

import java.awt.event.{ActionEvent, ActionListener}

import scala.collection.mutable

object ActionEventDispatcher extends ActionListener {
  
  val map = new mutable.HashMap[String, mutable.MutableList[ActionListener]]
  
  def actionPerformed(event: ActionEvent): Unit = {
    val command = event.getActionCommand
    if(map.contains(command)) {
      val listeners = map(command)
      for(listener <- listeners) {
        listener.actionPerformed(event)
      }
    }
  }
  
  def fireActionEvent(action: String) {
    fireActionEvent(new ActionEvent(this, 0, action))
  }
  
  def fireActionEvent(event: ActionEvent) {
    if(map.contains(event.getActionCommand)) {
      val listeners = map(event.getActionCommand)
      for(listener <- listeners) {
        listener.actionPerformed(event)
      }
    }
  }
  
  def addActionListener(listener: ActionListener, command: String) {
    if(!map.contains(command)) {
      val listeners = new mutable.MutableList[ActionListener]
      listeners += listener
      map += ((command, listeners))
    }
    else {
      val listeners = map(command)
      listeners += listener
    }
  }
}
