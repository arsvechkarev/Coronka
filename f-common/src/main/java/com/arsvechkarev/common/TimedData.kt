package com.arsvechkarev.common

import datetime.DateTime

class TimedData<T>(
  val data: T,
  val lastUpdateTime: DateTime
)