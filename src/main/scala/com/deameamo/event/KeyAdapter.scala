package com.deameamo.event

import java.awt.event.{KeyEvent, KeyListener}

trait KeyAdapter extends KeyListener {

  def keyReleased(e: KeyEvent): Unit = {}
  def keyPressed(e: KeyEvent): Unit = {}
  def keyTyped(e: KeyEvent): Unit = {}
}