package com.example.superherosearch.log

import timber.log.Timber

class Logging {
  companion object {
    fun logError(): (Throwable) -> Unit {
      return {
        Timber.e(it)
      }
    }

    fun logErrorAndThrow(): (Throwable) -> Unit {
      return {
        Timber.e(it)
        throw RuntimeException(it)
      }
    }

    fun ignoreError(): (Any) -> Unit {
      return {
        // noop
      }
    }
  }
}