package com.manutd.ronaldo.practicemakesperfect.service

import android.graphics.Color
import android.inputmethodservice.InputMethodService
import android.media.AudioManager
import android.view.HapticFeedbackConstants
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.FrameLayout
import androidx.appcompat.view.ContextThemeWrapper
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.manutd.ronaldo.practicemakesperfect.R
import com.manutd.ronaldo.practicemakesperfect.data.model.Key
import com.manutd.ronaldo.practicemakesperfect.data.model.KeyType
import com.manutd.ronaldo.practicemakesperfect.data.model.KeyboardMode
import com.manutd.ronaldo.practicemakesperfect.manager.KeyboardLayoutManager
import com.manutd.ronaldo.practicemakesperfect.presentation.ui.keyboard.AnimationOverlay
import com.manutd.ronaldo.practicemakesperfect.presentation.ui.keyboard.KeyboardState
import com.manutd.ronaldo.practicemakesperfect.presentation.ui.keyboard.KeyboardView
import com.manutd.ronaldo.practicemakesperfect.presentation.ui.keyboard.KeyboardViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class EmojiKeyboardService : InputMethodService(),
    LifecycleOwner,
    ViewModelStoreOwner,
    SavedStateRegistryOwner {
    private val lifecycleRegistry = LifecycleRegistry(this)
    private val store = ViewModelStore()
    private val savedStateRegistryController = SavedStateRegistryController.create(this)
    private var animationOverlay: ComposeView? = null
    // Coroutine scope for state observation
    private val serviceScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    // ===== ViewModel =====
    private val viewModel: KeyboardViewModel by lazy {
        ViewModelProvider(this)[KeyboardViewModel::class.java]
    }

    // ===== Views =====
    private var rootLayout: FrameLayout? = null
    private var keyboardView: KeyboardView? = null

    // ===== Audio =====
    private var audioManager: AudioManager? = null

    // ===== Lifecycle Owner Implementation =====
    override val lifecycle: Lifecycle
        get() = lifecycleRegistry

    override val viewModelStore: ViewModelStore
        get() = store

    override val savedStateRegistry: SavedStateRegistry
        get() = savedStateRegistryController.savedStateRegistry

    // ===== Service Lifecycle =====
    override fun onCreate() {
        super.onCreate()
        savedStateRegistryController.performRestore(null)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
        audioManager = getSystemService(AUDIO_SERVICE) as? AudioManager

        // Start observing state
        observeKeyboardState()
    }

    private fun observeKeyboardState() {
        serviceScope.launch {
            viewModel.keyboardState.collect { state ->
                updateKeyboardMode(state.mode, state.isShifted, state.isCapsLock)
            }
        }
    }

    override fun onStartInputView(info: EditorInfo?, restarting: Boolean) {
        super.onStartInputView(info, restarting)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)

        viewModel.setInputConnection(currentInputConnection, info)
        viewModel.switchToAlphabetMode()
    }

    override fun onFinishInputView(finishingInput: Boolean) {
        super.onFinishInputView(finishingInput)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE)
        viewModel.setInputConnection(null, null)
    }



    // ===== Create Input View =====
    private var overlayView: ComposeView? = null

    override fun onCreateInputView(): View {
        window?.window?.decorView?.let { decorView ->
            decorView.setViewTreeLifecycleOwner(this)
            decorView.setViewTreeViewModelStoreOwner(this)
            decorView.setViewTreeSavedStateRegistryOwner(this)
        }

        // Keyboard container - KHÔNG chứa overlay
        rootLayout = FrameLayout(this).apply {
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
            )
            setBackgroundResource(R.color.keyboard_background)
            clipChildren = false
            clipToPadding = false
        }

        keyboardView = KeyboardView(this).apply {
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
            )
        }
        setupKeyboardListener()
        rootLayout?.addView(keyboardView)

        // ⭐ Thêm overlay TRONG rootLayout nhưng với cách measure đặc biệt
        animationOverlay = ComposeView(this).apply {
            layoutParams = object : FrameLayout.LayoutParams(
                MATCH_PARENT,
                MATCH_PARENT
            ) {
                // Override để không ảnh hưởng measure của parent
            }

            setBackgroundColor(Color.TRANSPARENT)
            isClickable = false
            isFocusable = false

            setViewCompositionStrategy(
                ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed
            )

            setContent {
                AnimationOverlay(animationEvents = viewModel.animationEvents)
            }
        }

        rootLayout?.viewTreeObserver?.addOnGlobalLayoutListener(
            object : android.view.ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    rootLayout?.viewTreeObserver?.removeOnGlobalLayoutListener(this)

                    // Set height của overlay = height của keyboard
                    val keyboardHeight = rootLayout?.height ?: 0
                    animationOverlay?.layoutParams = FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        keyboardHeight
                    )
                    rootLayout?.addView(animationOverlay)
                }
            }
        )

        keyboardView?.setKeyboard(KeyboardLayoutManager.createQwertyLayout())

        return rootLayout!!
    }




    override fun onDestroy() {

        (window?.window?.decorView as? FrameLayout)?.let { decorView ->
            for (i in decorView.childCount - 1 downTo 0) {
                val child = decorView.getChildAt(i)
                if (child is ComposeView) {
                    decorView.removeView(child)
                }
            }
        }

        super.onDestroy()
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_STOP)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        store.clear()
        serviceScope.cancel()
        rootLayout = null
        keyboardView = null
        audioManager = null
        animationOverlay = null
    }

    private fun setupKeyboardListener() {
        keyboardView?.keyboardActionListener = object : KeyboardView.KeyboardActionListener {
            override fun onKeyPress(key: Key) {
                playKeySound(key)
                playHapticFeedback()
            }

            override fun onKeyRelease(key: Key) {
                val keyView = keyboardView?.findKeyViewByKey(key)

                if (keyView != null) {
                    // Lấy vị trí của key trên màn hình
                    val keyLocation = IntArray(2)
                    keyView.getLocationOnScreen(keyLocation)

                    // Lấy vị trí của rootLayout (keyboard container) trên màn hình
                    val rootLocation = IntArray(2)
                    rootLayout?.getLocationOnScreen(rootLocation)

                    // ⭐ Tính tọa độ tương đối so với rootLayout
                    val x = (keyLocation[0] - rootLocation[0]) + keyView.width / 2f
                    val y = (keyLocation[1] - rootLocation[1]) + keyView.height / 2f

                    viewModel.onKeyPress(key, x, y)
                } else {
                    viewModel.onKeyPress(key, 0f, 0f)
                }
            }

            override fun onKeyLongPress(key: Key) {
                key.popupCharacters?.let { showPopupKeyboard(key, it) }
            }

            override fun onKeyRepeat(key: Key) {
                viewModel.onKeyRepeat(key)
            }
        }
    }

    private fun updateKeyboardMode(mode: KeyboardMode, isShifted: Boolean, isCapsLock: Boolean) {
        when (mode) {
            KeyboardMode.ALPHABET -> {
                keyboardView?.setKeyboard(KeyboardLayoutManager.createQwertyLayout(isShifted))
                keyboardView?.setShiftState(isShifted, isCapsLock)
            }
            KeyboardMode.SYMBOL -> {
                keyboardView?.setKeyboard(KeyboardLayoutManager.createSymbolLayout())
            }
            KeyboardMode.EMOJI -> { /* TODO */ }
            else -> {}
        }
    }

    private fun playKeySound(key: Key) {
        val soundType = when (key.type) {
            KeyType.BACKSPACE -> AudioManager.FX_KEYPRESS_DELETE
            KeyType.ENTER -> AudioManager.FX_KEYPRESS_RETURN
            KeyType.SPACE -> AudioManager.FX_KEYPRESS_SPACEBAR
            else -> AudioManager.FX_KEYPRESS_STANDARD
        }
        audioManager?.playSoundEffect(soundType, 0.5f)
    }

    private fun playHapticFeedback() {
        keyboardView?.performHapticFeedback(
            HapticFeedbackConstants.KEYBOARD_TAP,
            HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING
        )
    }

    private fun showPopupKeyboard(key: Key, characters: String) {
        // TODO
    }
}