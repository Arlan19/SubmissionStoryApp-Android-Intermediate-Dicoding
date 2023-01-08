package com.arlanallacsta.submissionstoryapp.customview

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.os.Build
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.text.method.PasswordTransformationMethod
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.arlanallacsta.submissionstoryapp.R

class EditTextPassword : AppCompatEditText, View.OnTouchListener {

    private lateinit var clearInput: Drawable

    private fun init() {
        clearInput = ContextCompat.getDrawable(context, R.drawable.ic_baseline_close_24) as Drawable
        setOnTouchListener(this)
        inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD

        setHint(R.string.input_password)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            setAutofillHints(AUTOFILL_HINT_EMAIL_ADDRESS)
        }

        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!s.isNullOrEmpty() && s.length < 6) {
                    error = context.getString(R.string.wrong_password)
                    showClearInput()
                } else {
                    hideClearInput()
                }

            }

            override fun afterTextChanged(s: Editable?) {}

        })
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        transformationMethod = PasswordTransformationMethod.getInstance()
    }

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        if (compoundDrawables[2] != null) {

            val isClearButtonClicked = false
            if (isClearButtonClicked) {
                when (event!!.action) {
                    MotionEvent.ACTION_DOWN -> {
                        clearInput = ContextCompat.getDrawable(
                            context,
                            R.drawable.ic_baseline_close_24
                        ) as Drawable
                        showClearInput()
                        return true
                    }
                    MotionEvent.ACTION_UP -> {
                        clearInput = ContextCompat.getDrawable(
                            context,
                            R.drawable.ic_baseline_close_24
                        ) as Drawable
                        when {
                            text != null -> text?.clear()
                        }
                        hideClearInput()
                        return true
                    }
                    else -> return false
                }
            } else return false
        }
        return false
    }

    private fun setButtonDrawables(
        startOfTheText: Drawable? = null,
        topOfTheText: Drawable? = null,
        endOfTheText: Drawable? = null,
        bottomOfTheText: Drawable? = null
    ) {
        setCompoundDrawablesWithIntrinsicBounds(
            startOfTheText,
            topOfTheText,
            endOfTheText,
            bottomOfTheText
        )
    }

    private fun showClearInput() {
        setButtonDrawables(endOfTheText = clearInput)
    }

    private fun hideClearInput() {
        setButtonDrawables()
    }
}
