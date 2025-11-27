package com.example.timecrafted.ui.main.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.timecrafted.R
import com.example.timecrafted.data.auth.AuthRepository
import com.example.timecrafted.data.cloudinary.CloudinaryService
import com.example.timecrafted.data.repository.UserRepository
import com.example.timecrafted.databinding.FragmentProfileBinding
import com.example.timecrafted.ui.auth.loginScreen
import com.example.timecrafted.ui.main.ContactSupportActivity
import com.example.timecrafted.ui.profile.addressInformation
import com.example.timecrafted.ui.profile.editProfilePhoto
import com.example.timecrafted.ui.profile.orderHistory
import com.example.timecrafted.ui.profile.personalInformation

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    
    private val authRepository by lazy { AuthRepository() }
    private val userRepository by lazy { UserRepository() }

    // ActivityResultLauncher for picking/editing profile photo
    private val editPhotoLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == androidx.appcompat.app.AppCompatActivity.RESULT_OK) {
            // Profile image updated, reload user data
            loadUserProfile()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)

        // Sign out button
        binding.signOut.setOnClickListener {
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("Logout")
                .setMessage("Are you sure you want to log out?")
                .setCancelable(false)
                .setNegativeButton("No") { dialog, _ -> dialog.dismiss() }
                .setPositiveButton("Yes") { _, _ ->
                    com.example.timecrafted.data.auth.AuthRepository().signOut {
                        val intent = Intent(requireContext(), loginScreen::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        requireActivity().finish()
                    }
                }
            builder.create().show()
        }

        // Open Edit Profile Photo Activity
        binding.changeProfilePictureBtn.setOnClickListener {
            val intent = Intent(requireContext(), editProfilePhoto::class.java)
            editPhotoLauncher.launch(intent)
        }

        // Open other profile screens
        binding.profileInfoBtn.setOnClickListener {
            startActivity(Intent(requireContext(), personalInformation::class.java))
        }

        binding.addressBtn.setOnClickListener {
            startActivity(Intent(requireContext(), addressInformation::class.java))
        }

        binding.orderHistoryBtn.setOnClickListener {
            startActivity(Intent(requireContext(), orderHistory::class.java))
        }

        binding.contactSupportBtn.setOnClickListener {
            startActivity(Intent(requireContext(), ContactSupportActivity::class.java))
        }

        // App Settings button
        binding.appSettingsBtn.setOnClickListener {
            startActivity(Intent(requireContext(), com.example.timecrafted.ui.settings.SettingsActivity::class.java))
        }

        // Load user profile data
        loadUserProfile()

        return binding.root
    }

    private fun loadUserProfile() {
        val userId = authRepository.getCurrentUserId()
        if (userId == null) {
            return
        }

        userRepository.getUserProfile(
            userId = userId,
            onSuccess = { profile ->
                profile?.let {
                    // Update name and email
                    binding.rpyzi7sil7g.text = it.name.ifEmpty { "User" }
                    binding.rkwt9mh0u0k.text = it.email.ifEmpty { authRepository.getCurrentUserEmail() ?: "" }

                    // Load profile image from Cloudinary
                    if (it.profileImagePath.isNotEmpty()) {
                        val imageUrl = CloudinaryService.getImageUrl(it.profileImagePath)
                        Glide.with(requireContext())
                            .load(imageUrl)
                            .placeholder(R.drawable.img_profile)
                            .error(R.drawable.img_profile)
                            .into(binding.profileImage)
                    } else {
                        // Use default image
                        binding.profileImage.setImageResource(R.drawable.img_profile)
                    }
                } ?: run {
                    // No profile found, use auth data
                    val user = authRepository.getCurrentUser()
                    binding.rpyzi7sil7g.text = user?.displayName ?: "User"
                    binding.rkwt9mh0u0k.text = user?.email ?: ""
                }
            },
            onError = {
                // On error, use auth data
                val user = authRepository.getCurrentUser()
                binding.rpyzi7sil7g.text = user?.displayName ?: "User"
                binding.rkwt9mh0u0k.text = user?.email ?: ""
            }
        )
    }

    override fun onResume() {
        super.onResume()
        loadUserProfile()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
