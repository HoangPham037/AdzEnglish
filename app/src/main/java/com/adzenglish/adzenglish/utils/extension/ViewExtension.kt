package com.adzenglish.adzenglish.utils.extension

import android.app.Activity
import android.content.Context
import android.os.SystemClock
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.animation.Animation
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast

class SafeClickListener(
    private var defaultInterval: Long = 1000, private val onSafeCLick: (View) -> Unit
) : View.OnClickListener {
    private var lastTimeClicked: Long = 0
    override fun onClick(v: View) {
        if (SystemClock.elapsedRealtime() - lastTimeClicked < defaultInterval) {
            return
        }
        lastTimeClicked = SystemClock.elapsedRealtime()
        onSafeCLick(v)
    }
}

fun View.hideKeyboard(activity: Activity) {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
    imm?.hideSoftInputFromWindow(activity.currentFocus?.windowToken, 0)
}

fun View.click(onSafeClick: (View) -> Unit) {
    val safeClickListener = SafeClickListener {
        onSafeClick(it)
    }
    setOnClickListener(safeClickListener)
}
fun View.goneIf(goneIf: Boolean) {
    visibility = if (goneIf) View.GONE else View.VISIBLE
}
fun View.visible() {
    visibility = View.VISIBLE
}
fun View.gone() {
    visibility = View.GONE
}
fun View.invisible() {
    visibility = View.INVISIBLE
}

fun View.setInVisibility(isShow: Boolean) {
    visibility = if (isShow)View.VISIBLE else View.INVISIBLE
}

fun EditText.onTextChanged(onTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            // do nothing
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            onTextChanged(s.toString())
        }

        override fun afterTextChanged(s: Editable?) {
            // do nothing
        }
    })
}
fun Animation.onAnimationEnd(onAnimationEnd: (Animation) -> Unit) {
    this.setAnimationListener(object : Animation.AnimationListener{
        override fun onAnimationStart(p0: Animation?) {
            //do nothing
        }

        override fun onAnimationEnd(p0: Animation?) {
            if (p0 != null) {
                onAnimationEnd.invoke(p0)
            }
        }

        override fun onAnimationRepeat(p0: Animation?) {
            //do nothing
        }

    })
}
fun Context.toast(text: String) {
    Toast.makeText(this, text, Toast.LENGTH_LONG).show()
}