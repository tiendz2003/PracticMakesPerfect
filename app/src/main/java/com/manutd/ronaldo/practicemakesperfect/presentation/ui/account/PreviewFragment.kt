package com.manutd.ronaldo.practicemakesperfect.presentation.ui.account


import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import coil.load
import com.manutd.ronaldo.practicemakesperfect.R
import com.manutd.ronaldo.practicemakesperfect.databinding.FragmentPreviewBinding
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PreviewFragment : Fragment() {
    private var _binding: FragmentPreviewBinding? = null
    private val binding get() = _binding!!
    private val viewModels: CameraViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPreviewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModels.imageUri?.let { uri ->
            setupView(uri)
            setupListener()
        }
        handleSaveSuccess()
        handleSaveError()
    }

    private fun setupView(uri: Uri) {
        binding.ivPreview.load(uri)
    }

    private fun setupListener() {
        with(binding) {

            buttonRetake.setOnClickListener {
                findNavController().popBackStack()
            }
            buttonSave.setOnClickListener {
                saveImageToGallery()
            }
        }
    }

    private fun saveImageToGallery() {
        showLoading(true)

        //todo:Nên cho vào viewmodel
        lifecycleScope.launch {
            viewModels.saveImage()
        }
    }

    private fun handleSaveSuccess() {
        showLoading(false)
        viewModels.imageSaveResult.observe(viewLifecycleOwner) { savedUri ->
            if (savedUri !== null) {

                Toast.makeText(requireContext(), "Đã lưu ảnh!", Toast.LENGTH_SHORT).show()

                // 5. Gửi kết quả (URI của ảnh đã lưu) về cho AccountFragment
                val accountBackStackEntry =
                    findNavController().getBackStackEntry(R.id.accountFragment)
                accountBackStackEntry.savedStateHandle["captured_image_uri"] = savedUri.toString()


                // 6. Quay về AccountFragment, đồng thời xóa CameraFragment và PreviewFragment khỏi back stack
                findNavController().popBackStack(R.id.accountFragment, false)
            } else {
                handleSaveError()
            }
        }
    }


    private fun handleSaveError() {
        showLoading(false)
        Toast.makeText(requireContext(), "Lưu ảnh thất bại.", Toast.LENGTH_SHORT).show()
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressSaving.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.buttonSave.isEnabled = !isLoading
        binding.buttonRetake.isEnabled = !isLoading
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}