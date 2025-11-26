package com.manutd.ronaldo.practicemakesperfect.service


import android.view.View
import androidx.lifecycle.*
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import android.inputmethodservice.InputMethodService
import androidx.savedstate.setViewTreeSavedStateRegistryOwner

abstract class BaseLifecycleInputMethodService : InputMethodService(),
    LifecycleOwner,
    ViewModelStoreOwner,
    SavedStateRegistryOwner {
    // 1. Quản lý Lifecycle (Vòng đời)
    private val lifecycleRegistry: LifecycleRegistry by lazy { LifecycleRegistry(this) }

    // 2. Quản lý ViewModelStore (Nơi chứa ViewModel)
    private val store: ViewModelStore by lazy { ViewModelStore() }

    // 3. Quản lý SavedState (Lưu trạng thái khi bị kill)
    private val savedStateRegistryController: SavedStateRegistryController by lazy {
        SavedStateRegistryController.create(this)
    }

    override fun onCreate() {
        super.onCreate()
        savedStateRegistryController.performRestore(null)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START)
    }

    override fun onCreateInputView(): View? {
        val view = onCreateInputViewLayout().apply {
            setViewTreeLifecycleOwner(this@BaseLifecycleInputMethodService)
            setViewTreeViewModelStoreOwner(this@BaseLifecycleInputMethodService)
            setViewTreeSavedStateRegistryOwner(this@BaseLifecycleInputMethodService)
        }

        return view
    }

    abstract fun onCreateInputViewLayout(): View
    override val lifecycle: Lifecycle get() = lifecycleRegistry
    override val viewModelStore: ViewModelStore get() = store
    override val savedStateRegistry: SavedStateRegistry get() = savedStateRegistryController.savedStateRegistry
    override fun onDestroy() {
        super.onDestroy()
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_STOP)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        store.clear()

    }
}