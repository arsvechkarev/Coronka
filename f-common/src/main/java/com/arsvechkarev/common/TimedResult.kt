package com.arsvechkarev.common

import datetime.DateTime

class TimedResult<T>(
  val result: T,
  val lastUpdateTime: DateTime
)