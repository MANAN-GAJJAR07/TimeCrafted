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

    // ActivityResultLauncher for picking/editing profile photo
    private val editPhotoLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == androidx.appcompat.app.AppCompatActivity.RESULT_OK) {
            val imageUriString = result.data?.getStringExtra("selectedImageUri")
            imageUriString?.let {
                val imageUri = Uri.parse(it)
                binding.profileImage.setImageURI(imageUri)
            }
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
                    val sharedPref = requireActivity().getSharedPreferences(
                        "LoginPref",
                        android.content.Context.MODE_PRIVATE
                    )
                    sharedPref.edit().clear().apply()

                    val intent = Intent(requireContext(), loginScreen::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
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

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
