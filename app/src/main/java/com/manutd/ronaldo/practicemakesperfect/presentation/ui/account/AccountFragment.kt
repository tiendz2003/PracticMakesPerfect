package com.manutd.ronaldo.practicemakesperfect.presentation.ui.account

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import coil.load
import com.manutd.ronaldo.practicemakesperfect.R
import com.manutd.ronaldo.practicemakesperfect.databinding.FragmentAccountBinding
import dagger.hilt.android.AndroidEntryPoint
import androidx.core.net.toUri


@AndroidEntryPoint
class AccountFragment : Fragment() {
    private var _binding: FragmentAccountBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAccountBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupResultListener()
        setupListener()
    }

    private fun setupListener() {
        with(binding) {
            btnCamera.setOnClickListener {
                val action = AccountFragmentDirections.actionAccountFragmentToCameraFragment()
                findNavController().navigate(action)
            }
        }
    }
    private fun setupResultListener() {
        val navBackStackEntry = findNavController().currentBackStackEntry

        navBackStackEntry?.savedStateHandle
            ?.getLiveData<String>("captured_image_uri")
            ?.observe(viewLifecycleOwner) { uriString ->
                if (uriString != null) {
                    val imageUri = uriString.toUri()
                    Log.d("AccountFragment", "Received URI: $imageUri")
                    binding.shapeableImageView.load(imageUri) {
                        crossfade(true)
                        placeholder(R.drawable.ic_launcher_foreground)
                        error(R.drawable.ic_launcher_foreground)
                        listener(
                            onError = { _, throwable ->
                                Log.e("AccountFragment", "Load image failed: ${throwable.throwable.message}")
                            }
                        )
                    }

                    navBackStackEntry.savedStateHandle.remove<String>("captured_image_uri")
                }
            }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}