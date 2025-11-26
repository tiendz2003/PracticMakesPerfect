package com.manutd.ronaldo.practicemakesperfect.presentation.ui.keyboard

import android.R.attr.height
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import com.manutd.ronaldo.practicemakesperfect.R
import com.manutd.ronaldo.practicemakesperfect.data.model.Key
import com.manutd.ronaldo.practicemakesperfect.data.model.KeyType



class KeyView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    // Key data
    var key: Key? = null
        set(value) {
            field = value
            updateAppearance()
        }

    // Paint objects
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textAlign = Paint.Align.CENTER
        typeface = Typeface.DEFAULT
    }

    // State
    private var isKeyPressed = false
    private var isLongPressTriggered = false

    // Backgrounds
    private var normalBackground: Drawable? = null
    private var pressedBackground: Drawable? = null

    // Colors
    private var textColor: Int = Color.WHITE
    private var iconTint: Int = Color.WHITE

    // Handlers
    private val longPressHandler = Handler(Looper.getMainLooper())
    private val repeatHandler = Handler(Looper.getMainLooper())

    private var keyActionListener: OnKeyActionListener? = null

    // Timing
    private val longPressDelay = 300L
    private val repeatDelay = 50L
    private val repeatInitialDelay = 400L

    interface OnKeyActionListener {
        fun onPress(key: Key)
        fun onRelease(key: Key)
        fun onLongPress(key: Key)
        fun onRepeat(key: Key)
    }

    fun setOnKeyActionListener(listener: OnKeyActionListener) {
        this.keyActionListener = listener
    }

    init {
        isClickable = true
        isFocusable = true
    }

    private fun updateAppearance() {
        val key = this.key ?: return

        // Set text size based on key type
        textPaint.textSize = when (key.type) {
            KeyType.LETTER -> resources.getDimension(R.dimen.key_text_size_letter)
            KeyType.NUMBER -> resources.getDimension(R.dimen.key_text_size_number)
            KeyType.SPACE -> resources.getDimension(R.dimen.key_text_size_space)
            else -> resources.getDimension(R.dimen.key_text_size_symbol)
        }

        // Update background based on key type
        normalBackground = when (key.type) {
            KeyType.SHIFT, KeyType.BACKSPACE, KeyType.MODE_SYMBOL, KeyType.MODE_ALPHABET -> {
                ContextCompat.getDrawable(context, R.drawable.bg_key_function)
            }
            KeyType.ENTER -> {
                ContextCompat.getDrawable(context, R.drawable.bg_key_enter)
            }
            KeyType.SPACE -> {
                ContextCompat.getDrawable(context, R.drawable.bg_key_space)
            }
            else -> {
                ContextCompat.getDrawable(context, R.drawable.bg_key_normal)
            }
        }

        pressedBackground = ContextCompat.getDrawable(context, R.drawable.bg_key_pressed)

        // Set text color
        textColor = when (key.type) {
            KeyType.ENTER -> ContextCompat.getColor(context, R.color.key_text_enter)
            else -> ContextCompat.getColor(context, R.color.key_text_normal)
        }

        iconTint = textColor

        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val key = this.key ?: return

        // Draw background
        val bg = if (isKeyPressed) pressedBackground else normalBackground
        bg?.let { drawable ->
            drawable.setBounds(0, 0, width, height)
            drawable.draw(canvas)
        }

        // Draw icon or text
        if (key.icon != null) {
            drawIcon(canvas, key.icon)
        } else if (key.label.isNotEmpty()) {
            drawText(canvas, key.label)
        }
    }

    private fun drawText(canvas: Canvas, text: String) {
        textPaint.color = textColor

        val x = width / 2f
        val y = height / 2f - (textPaint.descent() + textPaint.ascent()) / 2

        canvas.drawText(text, x, y, textPaint)
    }

    private fun drawIcon(canvas: Canvas, iconRes: Int) {
        val drawable = ContextCompat.getDrawable(context, iconRes) ?: return

        val iconSize = (minOf(width, height) * 0.4f).toInt()
        val left = (width - iconSize) / 2
        val top = (height - iconSize) / 2

        drawable.setBounds(left, top, left + iconSize, top + iconSize)
        drawable.setTint(iconTint)
        drawable.draw(canvas)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val key = this.key ?: return false

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                isKeyPressed = true
                isLongPressTriggered = false
                invalidate()

                // Notify press
                keyActionListener?.onPress(key)

                // Schedule long press
                longPressHandler.postDelayed({
                    if (isKeyPressed && !isLongPressTriggered) {
                        isLongPressTriggered = true
                        keyActionListener?.onLongPress(key)

                        // Start repeat if repeatable
                        if (key.isRepeatable) {
                            startRepeat(key)
                        }
                    }
                }, longPressDelay)

                return true
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                val wasPressed = isKeyPressed
                isKeyPressed = false
                invalidate()

                // Cancel handlers
                longPressHandler.removeCallbacksAndMessages(null)
                repeatHandler.removeCallbacksAndMessages(null)

                // Notify release only if was pressed and not long press triggered (for non-repeatable keys)
                if (wasPressed && (!isLongPressTriggered || key.isRepeatable)) {
                    keyActionListener?.onRelease(key)
                }

                isLongPressTriggered = false

                return true
            }

            MotionEvent.ACTION_MOVE -> {
                // Check if finger moved outside the key
                if (!isPointInsideView(event.x, event.y)) {
                    if (isKeyPressed) {
                        isKeyPressed = false
                        invalidate()
                        longPressHandler.removeCallbacksAndMessages(null)
                        repeatHandler.removeCallbacksAndMessages(null)
                    }
                }
                return true
            }
        }

        return super.onTouchEvent(event)
    }

    private fun startRepeat(key: Key) {
        repeatHandler.postDelayed(object : Runnable {
            override fun run() {
                if (isKeyPressed) {
                    keyActionListener?.onRepeat(key)
                    repeatHandler.postDelayed(this, repeatDelay)
                }
            }
        }, repeatInitialDelay)
    }

    private fun isPointInsideView(x: Float, y: Float): Boolean {
        return x >= 0 && x <= width && y >= 0 && y <= height
    }

    fun updateShiftState(isShifted: Boolean, isCapsLock: Boolean) {
        val key = this.key ?: return
        if (key.type != KeyType.SHIFT) return

        // Update background for caps lock
        normalBackground = when {
            isCapsLock -> ContextCompat.getDrawable(context, R.drawable.bg_key_caps_lock)
            isShifted -> ContextCompat.getDrawable(context, R.drawable.bg_key_shift_on)
            else -> ContextCompat.getDrawable(context, R.drawable.bg_key_function)
        }

        invalidate()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        longPressHandler.removeCallbacksAndMessages(null)
        repeatHandler.removeCallbacksAndMessages(null)
    }
}