package core.di

import core.FailureReasonToMessageConverter

interface FailureReasonToMessageModule {
  
  val failureReasonToMessageConverter: FailureReasonToMessageConverter
}