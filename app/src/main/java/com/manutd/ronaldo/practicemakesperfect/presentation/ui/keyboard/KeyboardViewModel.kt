package com.manutd.ronaldo.practicemakesperfect.presentation.ui.keyboard

import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.manutd.ronaldo.practicemakesperfect.data.model.Key
import com.manutd.ronaldo.practicemakesperfect.data.model.KeyCode
import com.manutd.ronaldo.practicemakesperfect.data.model.KeyType
import com.manutd.ronaldo.practicemakesperfect.data.model.KeyboardMode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject
import kotlin.random.Random


@HiltViewModel
class KeyboardViewModel @Inject constructor() : ViewModel() {

    // ===== State =====
    private val _keyboardState = MutableStateFlow(KeyboardState())
    val keyboardState: StateFlow<KeyboardState> = _keyboardState.asStateFlow()

    // ===== Animation Events =====
    private val _animationEvents = MutableSharedFlow<AnimationEvent>(
        extraBufferCapacity = 64,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val animationEvents: SharedFlow<AnimationEvent> = _animationEvents.asSharedFlow()

    // Current input connection (set by Service)
    private var inputConnection: InputConnection? = null
    private var currentEditorInfo: EditorInfo? = null

    fun setInputConnection(ic: InputConnection?, editorInfo: EditorInfo?) {
        inputConnection = ic
        currentEditorInfo = editorInfo
    }

    // ===== Key Handling =====
    fun onKeyPress(key: Key, x: Float, y: Float) {
        // Trigger animation for character keys
        if (!KeyCode.isSpecialKey(key.code)) {
            triggerKeyAnimation(x, y)
        }

        when {
            KeyCode.isSpecialKey(key.code) -> handleSpecialKey(key)
            else -> handleCharacterKey(key)
        }
    }

    private fun triggerKeyAnimation(x: Float, y: Float) {
        // Random particle count: 1 (60%), 2 (30%), 3 (10%)
        val particleCount = when (Random.nextInt(100)) {
            in 0..59 -> 1
            in 60..89 -> 2
            else -> 3
        }

        _animationEvents.tryEmit(
            AnimationEvent(
                x = x,
                y = y,
                particleCount = particleCount,
                effectType = EffectType.HEART
            )
        )
    }

    private fun handleCharacterKey(key: Key) {
        val char = key.code.toChar()
        val state = _keyboardState.value

        val finalChar = if (state.isShifted || state.isCapsLock) {
            char.uppercaseChar()
        } else {
            char
        }

        // Commit text
        inputConnection?.commitText(finalChar.toString(), 1)

        // Reset shift (not caps lock)
        if (state.isShifted && !state.isCapsLock) {
            _keyboardState.update { it.copy(isShifted = false) }
        }
    }

    private fun handleSpecialKey(key: Key) {
        when (key.code) {
            KeyCode.SHIFT -> handleShift()
            KeyCode.BACKSPACE -> handleBackspace()
            KeyCode.ENTER -> handleEnter()
            KeyCode.SPACE -> handleSpace()
            KeyCode.MODE_SYMBOL -> switchToSymbolMode()
            KeyCode.MODE_ALPHABET -> switchToAlphabetMode()
            KeyCode.MODE_EMOJI -> switchToEmojiMode()
            KeyCode.LANGUAGE_SWITCH -> switchLanguage()
        }
    }

    private fun handleShift() {
        _keyboardState.update { state ->
            when {
                state.isCapsLock -> state.copy(isShifted = false, isCapsLock = false)
                state.isShifted -> state.copy(isCapsLock = true)
                else -> state.copy(isShifted = true)
            }
        }
    }

    private fun handleBackspace() {
        inputConnection?.let { ic ->
            val selected = ic.getSelectedText(0)
            if (selected != null && selected.isNotEmpty()) {
                ic.commitText("", 1)
            } else {
                ic.deleteSurroundingText(1, 0)
            }
        }
    }

    fun onKeyRepeat(key: Key) {
        if (key.code == KeyCode.BACKSPACE) {
            handleBackspace()
        }
    }

    private fun handleEnter() {
        inputConnection?.let { ic ->
            val editorInfo = currentEditorInfo

            if (editorInfo != null) {
                val action = editorInfo.imeOptions and EditorInfo.IME_MASK_ACTION
                val actionHandled = when (action) {
                    EditorInfo.IME_ACTION_SEARCH -> {
                        ic.performEditorAction(EditorInfo.IME_ACTION_SEARCH)
                        true
                    }
                    EditorInfo.IME_ACTION_SEND -> {
                        ic.performEditorAction(EditorInfo.IME_ACTION_SEND)
                        true
                    }
                    EditorInfo.IME_ACTION_GO -> {
                        ic.performEditorAction(EditorInfo.IME_ACTION_GO)
                        true
                    }
                    EditorInfo.IME_ACTION_DONE -> {
                        ic.performEditorAction(EditorInfo.IME_ACTION_DONE)
                        true
                    }
                    EditorInfo.IME_ACTION_NEXT -> {
                        ic.performEditorAction(EditorInfo.IME_ACTION_NEXT)
                        true
                    }
                    else -> false
                }

                if (!actionHandled) {
                    ic.commitText("\n", 1)
                }
            } else {
                ic.commitText("\n", 1)
            }
        }
    }

    private fun handleSpace() {
        inputConnection?.commitText(" ", 1)
    }

    // ===== Mode Switching =====
    private fun switchToSymbolMode() {
        _keyboardState.update { it.copy(mode = KeyboardMode.SYMBOL) }
    }

    fun switchToAlphabetMode() {
        _keyboardState.update {
            it.copy(mode = KeyboardMode.ALPHABET, isShifted = false, isCapsLock = false)
        }
    }

    private fun switchToEmojiMode() {
        _keyboardState.update { it.copy(mode = KeyboardMode.EMOJI) }
    }

    private fun switchLanguage() {
        // TODO: Implement later
    }

    fun onPopupKeyPress(char: Char) {
        inputConnection?.commitText(char.toString(), 1)

        val state = _keyboardState.value
        if (state.isShifted && !state.isCapsLock) {
            _keyboardState.update { it.copy(isShifted = false) }
        }
    }
}

// ===== KeyboardState.kt =====
data class KeyboardState(
    val mode: KeyboardMode = KeyboardMode.ALPHABET,
    val isShifted: Boolean = false,
    val isCapsLock: Boolean = false,
    //val currentLanguage: String = "en"
)


