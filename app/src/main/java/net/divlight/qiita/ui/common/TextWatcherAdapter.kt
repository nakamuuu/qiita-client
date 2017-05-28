package net.divlight.qiita.ui.common

import android.text.Editable
import android.text.TextWatcher

abstract class TextWatcherAdapter: TextWatcher {
    override fun afterTextChanged(s: Editable?) {
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
    }
}
