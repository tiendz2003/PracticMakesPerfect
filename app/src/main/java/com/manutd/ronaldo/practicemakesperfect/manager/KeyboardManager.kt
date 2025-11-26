package com.manutd.ronaldo.practicemakesperfect.manager

import com.manutd.ronaldo.practicemakesperfect.R
import com.manutd.ronaldo.practicemakesperfect.data.model.Key
import com.manutd.ronaldo.practicemakesperfect.data.model.KeyCode
import com.manutd.ronaldo.practicemakesperfect.data.model.KeyType
import com.manutd.ronaldo.practicemakesperfect.data.model.Keyboard
import com.manutd.ronaldo.practicemakesperfect.data.model.KeyboardMode
import com.manutd.ronaldo.practicemakesperfect.data.model.KeyboardRow


object KeyboardLayoutManager {

    fun createQwertyLayout(isShifted: Boolean = false): Keyboard {
        return Keyboard(
            rows = listOf(
                createNumberRow(),
                createQwertyRow1(isShifted),
                createQwertyRow2(isShifted),
                createQwertyRow3(isShifted),
                createBottomRow()
            ),
            isShifted = isShifted
        )
    }

    private fun createNumberRow(): KeyboardRow {
        return KeyboardRow(
            keys = "1234567890".map { char ->
                Key(
                    code = char.code,
                    label = char.toString(),
                    type = KeyType.NUMBER,
                    popupCharacters = getNumberPopup(char)
                )
            }
        )
    }

    private fun createQwertyRow1(isShifted: Boolean): KeyboardRow {
        val chars = "qwertyuiop"
        return KeyboardRow(
            keys = chars.map { char ->
                Key(
                    code = char.code,
                    label = if (isShifted) char.uppercase() else char.toString(),
                    type = KeyType.LETTER,
                    popupCharacters = getLetterPopup(char)
                )
            }
        )
    }

    private fun createQwertyRow2(isShifted: Boolean): KeyboardRow {
        val chars = "asdfghjkl"
        return KeyboardRow(
            keys = chars.map { char ->
                Key(
                    code = char.code,
                    label = if (isShifted) char.uppercase() else char.toString(),
                    type = KeyType.LETTER,
                    popupCharacters = getLetterPopup(char)
                )
            }
        )
    }

    private fun createQwertyRow3(isShifted: Boolean): KeyboardRow {
        val chars = "zxcvbnm"
        return KeyboardRow(
            keys = buildList {
                // Shift key
                add(
                    Key(
                        code = KeyCode.SHIFT,
                        label = "",
                        type = KeyType.SHIFT,
                        width = 1.5f,
                        icon = R.drawable.ic_play
                    )
                )

                // Letter keys
                chars.forEach { char ->
                    add(
                        Key(
                            code = char.code,
                            label = if (isShifted) char.uppercase() else char.toString(),
                            type = KeyType.LETTER,
                            popupCharacters = getLetterPopup(char)
                        )
                    )
                }

                // Backspace key
                add(
                    Key(
                        code = KeyCode.BACKSPACE,
                        label = "",
                        type = KeyType.BACKSPACE,
                        width = 1.5f,
                        icon = R.drawable.ic_play,
                        isRepeatable = true
                    )
                )
            }
        )
    }

    private fun createBottomRow(): KeyboardRow {
        return KeyboardRow(
            keys = listOf(
                Key(
                    code = KeyCode.MODE_SYMBOL,
                    label = "?123",
                    type = KeyType.MODE_SYMBOL,
                    width = 1.2f
                ),
                Key(
                    code = KeyCode.MODE_EMOJI,
                    label = "",
                    type = KeyType.MODE_EMOJI,
                    icon = R.drawable.ic_launcher_foreground
                ),
                Key(
                    code = KeyCode.LANGUAGE_SWITCH,
                    label = "",
                    type = KeyType.LANGUAGE_SWITCH,
                    icon = R.drawable.ic_home
                ),
                Key(
                    code = KeyCode.SPACE,
                    label = "English",
                    type = KeyType.SPACE,
                    width = 4f
                ),
                Key(
                    code = '.'.code,
                    label = ".",
                    type = KeyType.PERIOD,
                    popupCharacters = ".,?!:;\"'"
                ),
                Key(
                    code = KeyCode.ENTER,
                    label = "",
                    type = KeyType.ENTER,
                    width = 1.5f,
                    icon = R.drawable.ic_person
                )
            )
        )
    }

    // Popup characters cho các phím
    private fun getLetterPopup(char: Char): String? {
        return when (char) {
            'a' -> "àáâãäåæ"
            'e' -> "èéêë"
            'i' -> "ìíîï"
            'o' -> "òóôõöø"
            'u' -> "ùúûü"
            'n' -> "ñ"
            's' -> "ß"
            'c' -> "ç"
            else -> null
        }
    }

    private fun getNumberPopup(char: Char): String? {
        return when (char) {
            '1' -> "¹½⅓¼"
            '2' -> "²⅔"
            '3' -> "³¾"
            '0' -> "°∅"
            else -> null
        }
    }

    // Symbol layout
    fun createSymbolLayout(): Keyboard {
        return Keyboard(
            rows = listOf(
                createSymbolRow1(),
                createSymbolRow2(),
                createSymbolRow3(),
                createSymbolRow4(),
                createSymbolBottomRow()
            ),
            mode = KeyboardMode.SYMBOL
        )
    }

    private fun createSymbolRow1(): KeyboardRow {
        return KeyboardRow(
            keys = "1234567890".map { char ->
                Key(code = char.code, label = char.toString(), type = KeyType.NUMBER)
            }
        )
    }

    private fun createSymbolRow2(): KeyboardRow {
        return KeyboardRow(
            keys = "@#\$%&-+()*".map { char ->
                Key(code = char.code, label = char.toString(), type = KeyType.SYMBOL)
            }
        )
    }

    private fun createSymbolRow3(): KeyboardRow {
        return KeyboardRow(
            keys = "\"'/:;!?_=".map { char ->
                Key(code = char.code, label = char.toString(), type = KeyType.SYMBOL)
            }
        )
    }

    private fun createSymbolRow4(): KeyboardRow {
        return KeyboardRow(
            keys = buildList {
                add(
                    Key(
                        code = KeyCode.MODE_SYMBOL, // Switch to more symbols
                        label = "=\\<",
                        type = KeyType.MODE_SYMBOL,
                        width = 1.5f
                    )
                )

                ",.?!".forEach { char ->
                    add(Key(code = char.code, label = char.toString(), type = KeyType.SYMBOL))
                }

                add(
                    Key(
                        code = KeyCode.BACKSPACE,
                        label = "",
                        type = KeyType.BACKSPACE,
                        width = 1.5f,
                        icon = R.drawable.ic_setting,
                        isRepeatable = true
                    )
                )
            }
        )
    }

    private fun createSymbolBottomRow(): KeyboardRow {
        return KeyboardRow(
            keys = listOf(
                Key(
                    code = KeyCode.MODE_SYMBOL,
                    label = "ABC",
                    type = KeyType.MODE_ALPHABET,
                    width = 1.2f
                ),
                Key(
                    code = KeyCode.MODE_EMOJI,
                    label = "",
                    type = KeyType.MODE_EMOJI,
                    icon = R.drawable.ic_person
                ),
                Key(code = ','.code, label = ",", type = KeyType.COMMA),
                Key(code = KeyCode.SPACE, label = "", type = KeyType.SPACE, width = 4f),
                Key(code = '.'.code, label = ".", type = KeyType.PERIOD),
                Key(
                    code = KeyCode.ENTER,
                    label = "",
                    type = KeyType.ENTER,
                    width = 1.5f,
                    icon = R.drawable.ic_person
                )
            )
        )
    }
}