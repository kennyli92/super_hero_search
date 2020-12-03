package com.example.superherosearch.extensions

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

infix operator fun CompositeDisposable.plusAssign(disposable: Disposable) {
  add(disposable)
}