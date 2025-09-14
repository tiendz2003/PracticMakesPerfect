package com.manutd.ronaldo.practicemakesperfect.presentation.ui.account

import android.content.ContentValues
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.navArgs
import coil.load
import com.manutd.ronaldo.practicemakesperfect.R
import com.manutd.ronaldo.practicemakesperfect.databinding.FragmentPreviewBinding
import androidx.core.net.toUri
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.InputStream


class PreviewFragment : Fragment() {
    private var _binding: FragmentPreviewBinding? = null
    private val binding get() = _binding!!
    private val args: PreviewFragmentArgs by navArgs()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPreviewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val tempImageUri = args.imageUri.toUri()
        setupListener(tempImageUri)
    }

    private fun setupListener(uri: Uri) {
        with(binding) {
            binding.ivPreview.load(uri)
            buttonRetake.setOnClickListener {
                findNavController().popBackStack()
            }
            buttonSave.setOnClickListener {
                saveImageToGallery(uri)
            }
        }
    }

    private fun saveImageToGallery(tempUri: Uri) {
        showLoading(true)

        //todo:Nên cho vào viewmodel
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val inputStream: InputStream? =
                    requireContext().contentResolver.openInputStream(tempUri)

                val contentValues = ContentValues().apply {
                    val name = "YourApp-Image-${System.currentTimeMillis()}.jpg"
                    put(MediaStore.MediaColumns.DISPLAY_NAME, name)
                    put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/YourAppName")
                        put(MediaStore.Images.Media.IS_PENDING, 1)
                    }
                }

                val contentResolver = requireContext().contentResolver
                val newImageUri = contentResolver.insert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    contentValues
                )

                if (inputStream != null && newImageUri != null) {
                    contentResolver.openOutputStream(newImageUri).use { outputStream ->
                        inputStream.copyTo(outputStream!!)
                    }

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        contentValues.clear()
                        contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
                        contentResolver.update(newImageUri, contentValues, null, null)
                    }
                    withContext(Dispatchers.Main) {
                        handleSaveSuccess(newImageUri)
                    }
                } else {
                    throw Exception("Không thể đọc file tạm hoặc tạo file mới.")
                }

            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    handleSaveError()
                }
            }
        }
    }

    private fun handleSaveSuccess(savedUri: Uri) {
        showLoading(false)
        Toast.makeText(requireContext(), "Đã lưu ảnh!", Toast.LENGTH_SHORT).show()

        // 5. Gửi kết quả (URI của ảnh đã lưu) về cho AccountFragment
        val accountBackStackEntry = findNavController().getBackStackEntry(R.id.accountFragment)
        accountBackStackEntry.savedStateHandle["captured_image_uri"] = savedUri.toString()


        // 6. Quay về AccountFragment, đồng thời xóa CameraFragment và PreviewFragment khỏi back stack
        findNavController().popBackStack(R.id.accountFragment, false)
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