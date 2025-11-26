package com.manutd.ronaldo.practicemakesperfect.presentation

import android.R.attr.visibility
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.manutd.ronaldo.practicemakesperfect.R
import com.manutd.ronaldo.practicemakesperfect.databinding.MainActivityBinding
import com.manutd.ronaldo.practicemakesperfect.presentation.ui.SharedViewModel
import com.manutd.ronaldo.practicemakesperfect.presentation.ui.home.UiState
import com.manutd.ronaldo.practicemakesperfect.presentation.ui.movies.MoviesViewModel
import com.skydoves.transformationlayout.onTransformationStartContainer
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val binding by lazy(LazyThreadSafetyMode.NONE) {
        MainActivityBinding.inflate(layoutInflater)
    }
    private lateinit var navController: NavController
    private val viewModels: MoviesViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onTransformationStartContainer()
        setContentView(binding.root)
        binding.btnEnableKeyboard.setOnClickListener {
            // Open input method settings
            startActivity(Intent(Settings.ACTION_INPUT_METHOD_SETTINGS))
        }

        binding.btnSelectKeyboard.setOnClickListener {
            // Open input method picker
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showInputMethodPicker()
        }
    }

   /* private fun observeViewModel() {
        lifecycleScope.launch {
            viewModels.uiState.flowWithLifecycle(
                lifecycle,
                Lifecycle.State.STARTED
            ).collect { state ->
                Log.d("MainActivity", "observeViewModel:$state ")
                if (state is UiState.Loading) {
                    binding.progressBar.visibility = View.VISIBLE
                } else {
                    binding.progressBar.visibility = View.GONE
                }
            }
        }
    }*/

   /* fun setupBottomBar() {
        val topLevelDestinations = setOf(
            R.id.homeFragment,
            R.id.accountFragment,
        )
        val navHost =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHost.navController
        binding.bottomNavView.setupWithNavController(navController)
        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id in topLevelDestinations) {
                binding.bottomNavView.visibility = View.VISIBLE
                binding.mainToolbar.root.visibility = View.VISIBLE
            } else {
                binding.bottomNavView.visibility = View.GONE
                binding.mainToolbar.root.visibility = View.GONE
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }*/
}