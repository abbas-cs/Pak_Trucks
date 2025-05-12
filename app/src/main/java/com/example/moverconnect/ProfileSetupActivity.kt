package com.example.moverconnect

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.*

class ProfileSetupActivity : AppCompatActivity() {

    private lateinit var profileImage: ShapeableImageView
    private lateinit var nameInput: TextInputEditText
    private lateinit var phoneInput: TextInputEditText
    private lateinit var emailInput: TextInputEditText
    private lateinit var addressInput: TextInputEditText
    private lateinit var bioInput: TextInputEditText
    private lateinit var saveButton: MaterialButton

    private val getContent = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                profileImage.setImageURI(uri)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_setup)

        initializeViews()
        setupClickListeners()
        loadUserData()
    }

    private fun initializeViews() {
        profileImage = findViewById(R.id.profileImage)
        nameInput = findViewById(R.id.nameInput)
        phoneInput = findViewById(R.id.phoneInput)
        emailInput = findViewById(R.id.emailInput)
        addressInput = findViewById(R.id.addressInput)
        bioInput = findViewById(R.id.bioInput)
        saveButton = findViewById(R.id.saveButton)

        // Set up toolbar
        findViewById<View>(R.id.toolbar).setOnClickListener {
            onBackPressed()
        }
    }

    private fun setupClickListeners() {
        // Profile image click
        profileImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            getContent.launch(intent)
        }

        // Save button click
        saveButton.setOnClickListener {
            if (validateInputs()) {
                saveProfile()
            }
        }
    }

    private fun loadUserData() {
        // TODO: Load user data from preferences or database
        // For now, using dummy data
        nameInput.setText("Alex")
        phoneInput.setText("+1 234 567 8900")
        emailInput.setText("alex@example.com")
        addressInput.setText("123 Main St, City, State")
        bioInput.setText("I'm a customer looking for reliable moving services.")
    }

    private fun validateInputs(): Boolean {
        var isValid = true

        // Name validation
        if (nameInput.text.toString().trim().isEmpty()) {
            findViewById<TextInputLayout>(R.id.nameLayout).error = "Name is required"
            isValid = false
        } else {
            findViewById<TextInputLayout>(R.id.nameLayout).error = null
        }

        // Phone validation
        if (phoneInput.text.toString().trim().isEmpty()) {
            findViewById<TextInputLayout>(R.id.phoneLayout).error = "Phone number is required"
            isValid = false
        } else {
            findViewById<TextInputLayout>(R.id.phoneLayout).error = null
        }

        // Address validation
        if (addressInput.text.toString().trim().isEmpty()) {
            findViewById<TextInputLayout>(R.id.addressLayout).error = "Address is required"
            isValid = false
        } else {
            findViewById<TextInputLayout>(R.id.addressLayout).error = null
        }

        return isValid
    }

    private fun saveProfile() {
        saveButton.isEnabled = false
        saveButton.text = "Saving..."

        // Simulate network delay
        CoroutineScope(Dispatchers.Main).launch {
            delay(1000) // 1 second delay

            // TODO: Save profile data to backend
            // For now, just show success message
            Toast.makeText(this@ProfileSetupActivity, "Profile saved successfully!", Toast.LENGTH_SHORT).show()
            
            saveButton.isEnabled = true
            saveButton.text = "Save Profile"
            
            // Return to previous screen
            finish()
        }
    }
} 