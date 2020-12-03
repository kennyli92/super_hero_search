package com.example.superherosearch.extensions

import android.view.View
import com.example.superherosearch.dialog.SnackbarViewModel
import com.google.android.material.snackbar.Snackbar

fun View.showSnackBar(snackbarViewModel: SnackbarViewModel) {
  val snackBar = if (snackbarViewModel.messageResId != 0) {
    Snackbar.make(this, snackbarViewModel.messageResId, snackbarViewModel.duration)
  } else {
    Snackbar.make(this, snackbarViewModel.message, snackbarViewModel.duration)
  }

  val action: (View) -> Unit = { _: View -> snackbarViewModel.action }
  if (snackbarViewModel.actionTextResId != 0) {
    snackBar.setAction(snackbarViewModel.actionTextResId, action)
  } else {
    snackBar.setAction(snackbarViewModel.actionText, action)
  }

  snackBar.show()
}