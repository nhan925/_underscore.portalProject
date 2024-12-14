package com.example.login_portal.ui.information

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.example.login_portal.R
import com.example.login_portal.databinding.FragmentInformationBinding
import com.google.android.material.navigation.NavigationView
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.launch
import java.io.File


class InformationFragment : Fragment() {
    private lateinit var informationViewModel: InformationViewModel
    private var _binding: FragmentInformationBinding? = null
    private lateinit var adapter: FragmentPageAdapter

    private val selectImageRequestCode = 1001
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onResume() {
        super.onResume()
        viewLifecycleOwner.lifecycleScope.launch {
            informationViewModel.reset()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val factory = InformationViewModelFactory(requireContext())
        informationViewModel = ViewModelProvider(this,factory).get(InformationViewModel::class.java)

        _binding = FragmentInformationBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = informationViewModel
        val root: View = binding.root

        // Create tabLayout and viewPager2
        adapter = FragmentPageAdapter(childFragmentManager, lifecycle)

        binding.informationFragmentTabLayout.addTab(binding.informationFragmentTabLayout.newTab().setText(R.string.infor_student_frag_information_general))
        binding.informationFragmentTabLayout.addTab(binding.informationFragmentTabLayout.newTab().setText(R.string.infor_student_frag_information_detail))
        binding.informationFragmentTabLayout.addTab(binding.informationFragmentTabLayout.newTab().setText(R.string.infor_student_frag_information_contact))

        binding.informationFragmentViewpager2.adapter = adapter
        binding.informationFragmentTabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tab != null) {
                    binding.informationFragmentViewpager2.currentItem = tab.position
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        binding.informationFragmentViewpager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                binding.informationFragmentTabLayout.selectTab(binding.informationFragmentTabLayout.getTabAt(position))
            }
        })

        binding.informationFragmentStudentAvatar.setOnClickListener{
            showAvatarOptions()
        }

        val navigationView = requireActivity().findViewById<NavigationView>(R.id.nav_view)
        val headerView = navigationView.getHeaderView(0) // Header đầu tiên
        val navAvatarImageView = headerView.findViewById<ImageView>(R.id.nav_header_main_student_avatar)

        informationViewModel.informations.observe(viewLifecycleOwner) { info ->
            val avatarUrl = info?.AvatarUrl?.takeIf { it.isNotEmpty() } ?: ""
            loadAvatarIntoImageView(avatarUrl, binding.informationFragmentStudentAvatar)
            loadAvatarIntoImageView(avatarUrl, navAvatarImageView)
        }
        return root
    }

    private fun loadAvatarIntoImageView(imageUrl: String?, imageView: ImageView) {
        Glide.with(requireContext())
            .load(imageUrl)
            .placeholder(R.drawable.baseline_person_24) // Ảnh mặc định
            .error(R.drawable.baseline_person_24)
            .transform(CenterCrop())
            .into(imageView)
    }

    private fun showAvatarOptions() {
        var resource = requireContext()
        val options = arrayOf(resource.getString(R.string.infor_student_frag_upload_avatar),
            resource.getString(R.string.infor_student_frag_remove_avatar))
        val builder = android.app.AlertDialog.Builder(requireContext())
        builder.setTitle(R.string.infor_student_frag_change_avatar)
        builder.setItems(options) { _, which ->
            when (which) {
                0 -> selectAvatarImage()
                1 -> removeAvatar()
            }
        }
        builder.show()
    }

    private fun selectAvatarImage() {
        val intent = Intent(Intent.ACTION_PICK).apply {
            type = "image/*"
            putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/jpeg", "image/png", "image/gif","image/bmp","image/tiff"))
        }
        startActivityForResult(Intent.createChooser(intent, "Select Avatar"), selectImageRequestCode)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == selectImageRequestCode && resultCode == RESULT_OK) {
            val imageUri: Uri? = data?.data
            Log.e("URI", "Failed to decode bitmap from URI: ${imageUri!!.isAbsolute}")
            if (imageUri != null) {
                informationViewModel.UploadAvatar(imageUri)
            }
        }
    }

    private fun removeAvatar(){
        informationViewModel.removeAvatar()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

