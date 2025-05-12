package com.example.moverconnect

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.card.MaterialCardView
import com.google.android.material.imageview.ShapeableImageView

class CustomerDashboardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_customer_dashboard)

        setupBottomNavigation()
        setupDashboardButtons()
        setupProfileCard()
    }

    private fun setupBottomNavigation() {
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> true
                R.id.navigation_requests -> {
                    // TODO: Navigate to requests
                    true
                }
                R.id.navigation_profile -> {
                    // TODO: Navigate to profile
                    true
                }
                R.id.navigation_settings -> {
                    // TODO: Navigate to settings
                    true
                }
                else -> false
            }
        }
    }

    private fun setupDashboardButtons() {
        val createMoveButton = findViewById<MaterialCardView>(R.id.createMoveRequestButton)
        val browseDriversButton = findViewById<MaterialCardView>(R.id.browseDriversButton)
        val myRequestsButton = findViewById<MaterialCardView>(R.id.myRequestsButton)

        // Set button texts and icons
        createMoveButton.findViewById<TextView>(R.id.buttonText).text = "Create Move Request"
        createMoveButton.findViewById<ImageView>(R.id.buttonIcon).setImageResource(R.drawable.ic_truck)

        browseDriversButton.findViewById<TextView>(R.id.buttonText).text = "Browse Drivers"
        browseDriversButton.findViewById<ImageView>(R.id.buttonIcon).setImageResource(R.drawable.ic_search)

        myRequestsButton.findViewById<TextView>(R.id.buttonText).text = "My Requests"
        myRequestsButton.findViewById<ImageView>(R.id.buttonIcon).setImageResource(R.drawable.ic_list)

        // Add click listeners with animations
        val buttonAnimation = AnimationUtils.loadAnimation(this, R.anim.button_press)

        createMoveButton.setOnClickListener { view ->
            view.startAnimation(buttonAnimation)
            val intent = Intent(this, CreateMoveRequestActivity::class.java)
            startActivity(intent)
        }

        browseDriversButton.setOnClickListener { view ->
            view.startAnimation(buttonAnimation)
            Toast.makeText(this, "Browse Drivers feature coming soon!", Toast.LENGTH_SHORT).show()
        }

        myRequestsButton.setOnClickListener { view ->
            view.startAnimation(buttonAnimation)
            val intent = Intent(this, MyRequestsActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupProfileCard() {
        val profileCard = findViewById<MaterialCardView>(R.id.profileCard)
        val profileImage = findViewById<ShapeableImageView>(R.id.profileImage)

        profileCard.setOnClickListener {
            val intent = Intent(this, ProfileSetupActivity::class.java)
            startActivity(intent)
        }

        // Set default profile image
        profileImage.setImageResource(R.drawable.ic_profile_placeholder)
    }
} 