package com.manutd.ronaldo.practicemakesperfect.presentation.ui.keyboard


import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup


import com.manutd.ronaldo.practicemakesperfect.R
import com.manutd.ronaldo.practicemakesperfect.data.model.Key
import com.manutd.ronaldo.practicemakesperfect.data.model.KeyType
import com.manutd.ronaldo.practicemakesperfect.data.model.Keyboard


class KeyboardView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ViewGroup(context, attrs, defStyleAttr) {

    // Keyboard data
    private var keyboard: Keyboard? = null
    private val keyViews = mutableListOf<KeyView>()

    // Dimensions
    private var keyWidth: Float = 0f
    private var keyHeight: Float = 0f
    private val keyPadding: Float = 2 * resources.displayMetrics.density

    // Listener
    var keyboardActionListener: KeyboardActionListener? = null

    interface KeyboardActionListener {
        fun onKeyPress(key: Key)
        fun onKeyRelease(key: Key)
        fun onKeyLongPress(key: Key)
        fun onKeyRepeat(key: Key)
    }

    fun setKeyboard(keyboard: Keyboard) {
        this.keyboard = keyboard
        buildKeyViews()
        requestLayout()
    }

    fun getKeyboard(): Keyboard? = keyboard

    private fun buildKeyViews() {
        removeAllViews()
        keyViews.clear()

        keyboard?.rows?.forEach { row ->
            row.keys.forEach { key ->
                val keyView = KeyView(context).apply {
                    this.key = key
                    setOnKeyActionListener(object : KeyView.OnKeyActionListener {
                        override fun onPress(key: Key) {
                            keyboardActionListener?.onKeyPress(key)
                        }

                        override fun onRelease(key: Key) {
                            keyboardActionListener?.onKeyRelease(key)
                        }

                        override fun onLongPress(key: Key) {
                            keyboardActionListener?.onKeyLongPress(key)
                        }

                        override fun onRepeat(key: Key) {
                            keyboardActionListener?.onKeyRepeat(key)
                        }
                    })
                }
                keyViews.add(keyView)
                addView(keyView)
            }
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val keyboard = this.keyboard ?: run {
            setMeasuredDimension(width, 0)
            return
        }

        val rowCount = keyboard.rows.size
        val standardKeyHeight = resources.getDimension(R.dimen.key_height)
        val totalHeight = (standardKeyHeight * rowCount).toInt()

        // Calculate key dimensions
        val maxKeysInRow = keyboard.rows.maxOfOrNull { row ->
            row.keys.sumOf { it.width.toDouble() }
        }?.toFloat() ?: 10f

        keyWidth = (width - paddingLeft - paddingRight) / maxKeysInRow
        keyHeight = standardKeyHeight

        setMeasuredDimension(width, totalHeight + paddingTop + paddingBottom)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val keyboard = this.keyboard ?: return

        var keyIndex = 0
        var currentY = paddingTop.toFloat()

        keyboard.rows.forEach { row ->
            var currentX = paddingLeft.toFloat()
            val rowHeight = keyHeight * row.rowHeight

            // Center the row if it doesn't fill the width
            val rowWidth = row.keys.sumOf { (keyWidth * it.width).toInt() }
            val totalWidth = width - paddingLeft - paddingRight
            if (rowWidth < totalWidth) {
                currentX += (totalWidth - rowWidth) / 2f
            }

            row.keys.forEach { key ->
                val keyView = keyViews.getOrNull(keyIndex) ?: return@forEach
                val keyWidthPx = (keyWidth * key.width).toInt()

                keyView.layout(
                    (currentX + keyPadding).toInt(),
                    (currentY + keyPadding).toInt(),
                    (currentX + keyWidthPx - keyPadding).toInt(),
                    (currentY + rowHeight - keyPadding).toInt()
                )

                currentX += keyWidthPx
                keyIndex++
            }

            currentY += rowHeight
        }
    }

    fun setShiftState(isShifted: Boolean, isCapsLock: Boolean) {
        // Update shift key appearance
        keyViews.forEach { keyView ->
            if (keyView.key?.type == KeyType.SHIFT) {
                keyView.updateShiftState(isShifted, isCapsLock)
            }
        }
    }

    fun findKeyViewByKey(key: Key): KeyView? {
        return keyViews.find { it.key?.code == key.code }
    }
}