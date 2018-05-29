package net.divlight.qiita.extension.arch

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.content.Context

val AndroidViewModel.context: Context get() = getApplication<Application>().applicationContext

fun AndroidViewModel.getString(resId: Int): String = context.getString(resId)

fun AndroidViewModel.getString(resId: Int, vararg formatArgs: Any): String = context.getString(
    resId,
    formatArgs
)
