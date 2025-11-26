package com.manutd.ronaldo.practicemakesperfect.data.model

// ===== Key.kt =====
data class Key(
    val code: Int,              // KeyCode hoặc Unicode
    val label: String,          // Hiển thị trên phím
    val type: KeyType,
    val width: Float = 1f,      // Relative width (1 = standard)
    val icon: Int? = null,      // Resource ID nếu là icon
    val popupCharacters: String? = null,  // Long press options
    val isRepeatable: Boolean = false     // Backspace, etc.
)

// ===== KeyType.kt =====
enum class KeyType {
    // Character keys
    LETTER,
    NUMBER,
    SYMBOL,

    // Functional keys
    SHIFT,
    CAPS_LOCK,
    BACKSPACE,
    ENTER,
    SPACE,
    TAB,

    // Mode switch
    MODE_SYMBOL,      // ?123
    MODE_ALPHABET,    // ABC
    MODE_EMOJI,       // 😊

    // Special
    LANGUAGE_SWITCH,
    COMMA,
    PERIOD,
    ACTION            // Search, Send, Done, etc.
}

// ===== KeyCode.kt =====
object KeyCode {
    const val SHIFT = -1
    const val MODE_SYMBOL = -2
    const val ENTER = -3
    const val BACKSPACE = -4
    const val SPACE = 32
    const val MODE_EMOJI = -5
    const val LANGUAGE_SWITCH = -6
    const val MODE_ALPHABET = -9
    const val SETTINGS = -7
    const val CAPS_LOCK = -8

    // Check if it's a special key
    fun isSpecialKey(code: Int): Boolean = code < 0
}

// ===== Keyboard.kt =====
data class Keyboard(
    val rows: List<KeyboardRow>,
    val mode: KeyboardMode = KeyboardMode.ALPHABET,
    val isShifted: Boolean = false,
    val isCapsLock: Boolean = false
)

data class KeyboardRow(
    val keys: List<Key>,
    val rowHeight: Float = 1f  // Relative height
)

enum class KeyboardMode {
    ALPHABET,
    SYMBOL,
    SYMBOL_MORE,
    NUMBER,
    EMOJI
}