package com.arsvechkarev.test

import org.junit.Assert

fun assert(block: () -> Boolean) {
  Assert.assertTrue(block())
}