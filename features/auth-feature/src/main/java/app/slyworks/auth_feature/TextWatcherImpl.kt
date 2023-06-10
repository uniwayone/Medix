package app.slyworks.auth_feature

import android.text.Editable
import android.text.TextWatcher

class TextWatcherImpl
    constructor(private var onTextChangedFunc:(String) -> Unit): TextWatcher {
    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
    override fun afterTextChanged(s: Editable?) { }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
          val _s:String =
              if (s.isNullOrBlank())
                  ""
              else
                  s.toString()
          onTextChangedFunc(_s)
    }
}