package com.example.timecrafted.ui.main.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)

        binding.signOut.setOnClickListener {
            // Show confirmation dialog
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("Logout")
                .setMessage("Are you sure you want to log out?")
                .setCancelable(false)
                .setNegativeButton("No") { dialog, _ ->
                    dialog.dismiss()
                }
                .setPositiveButton("Yes") { _, _ ->
                    // Clear saved login data
                    val sharedPref = requireActivity().getSharedPreferences("LoginPref", android.content.Context.MODE_PRIVATE)
                    sharedPref.edit().clear().apply()

                    // Redirect to Login Screen
                    val intent = Intent(requireContext(), loginScreen::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                }

            val alert = builder.create()
            alert.show()
        }

        binding.changeProfilePictureBtn.setOnClickListener {
            val i = Intent(requireContext(), editProfilePhoto::class.java)
            startActivity(i)
        }

        binding.profileInfoBtn.setOnClickListener {
            val i = Intent(requireContext(), personalInformation::class.java)
            startActivity(i)
        }
        binding.addressBtn.setOnClickListener {
            val i = Intent(requireContext(), addressInformation::class.java)
            startActivity(i)
        }
        binding.orderHistoryBtn.setOnClickListener {
            val i = Intent(requireContext(), orderHistory::class.java)
            startActivity(i)
        }
//        binding.paymentMethodBtn.setOnClickListener {
//            val i = Intent(requireContext(), editProfilePhoto::class.java)
//            startActivity(i)
//        }
//        binding.appSettingsBtn.setOnClickListener {
//            val i = Intent(requireContext(), editProfilePhoto::class.java)
//            startActivity(i)
//        }
        binding.contactSupportBtn.setOnClickListener {
            val i = Intent(requireContext(), ContactSupportActivity::class.java)
            startActivity(i)
        }



        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
