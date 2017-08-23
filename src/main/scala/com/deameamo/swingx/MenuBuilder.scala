package com.deameamo.swingx

import java.awt.Event
import java.awt.event.ActionListener
import java.util.ResourceBundle
import javax.swing.JMenu
import javax.swing.JMenuItem
import javax.swing.KeyStroke

class MenuBuilder(bundle: ResourceBundle) {
  
  def this(bundleName: String) = this(ResourceBundle.getBundle(bundleName))
  
  val NULL_CHAR: Char = 0
  val DEFAULT_MASK: Int = Event.CTRL_MASK
  
  def createMenu(key: String, mnemonic: Char): JMenu = {
    val menu = new JMenu(bundle.getString(key))
    if(mnemonic != NULL_CHAR) {
      menu.setMnemonic(mnemonic)
    }
    menu
  }
  
  def createMenu(key: String): JMenu = createMenu(key, NULL_CHAR)
  
  def createMenuItem(textKey: String, listener: ActionListener, command: String, acceleratorKey: String, mask: Int, mnemonic: Char): JMenuItem = {
    val menuItem = new JMenuItem()
    menuItem.setText(bundle.getString(textKey))
    menuItem.setActionCommand(command)
    menuItem.addActionListener(listener)
    if(mnemonic != NULL_CHAR) {
      menuItem.setMnemonic(mnemonic)
    }
    if(acceleratorKey != null) {
      menuItem.setAccelerator(KeyStroke.getKeyStroke(bundle.getString(acceleratorKey).charAt(0), mask, false))
    }
    menuItem
  }
  
  def createMenuItem(textKey: String, listener: ActionListener, command: String, acceleratorKey: String, mask: Int): JMenuItem = 
    createMenuItem(textKey, listener, command, acceleratorKey, mask, NULL_CHAR)
  
  def createMenuItem(textKey: String, listener: ActionListener, command: String, acceleratorKey: String): JMenuItem = 
    createMenuItem(textKey, listener, command, acceleratorKey, DEFAULT_MASK, NULL_CHAR)
  
  def createMenuItem(textKey: String, listener: ActionListener, command: String, mnemonic: Char): JMenuItem = 
    createMenuItem(textKey, listener, command, null, -1, mnemonic)
  
  def createMenuItem(textKey: String, listener: ActionListener, command: String): JMenuItem = 
    createMenuItem(textKey, listener, command, null, -1, NULL_CHAR)
  
  def createMenuItem(textKey: String, listener: ActionListener): JMenuItem = 
    createMenuItem(textKey, listener, null, null, -1, NULL_CHAR)
  
  def getString(key: String): String = bundle.getString(key)
}