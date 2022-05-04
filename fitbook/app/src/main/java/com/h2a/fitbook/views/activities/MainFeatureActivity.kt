package com.h2a.fitbook.views.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import coil.load
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.h2a.fitbook.R
import com.h2a.fitbook.databinding.ActivityDrawerBinding
import com.h2a.fitbook.utils.AuthenticationManager
import com.h2a.fitbook.utils.UtilFunctions

class MainFeatureActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityDrawerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDrawerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarDrawer.toolbar)

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController =
            findNavController(R.id.nav_host_fragment_content_drawer) // Passing each menu ID as a set of Ids because each // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home,
                R.id.nav_health_management,
                R.id.nav_statistics,
                R.id.nav_sharing,
                R.id.nav_about,
                R.id.nav_settings,
                R.id.nav_schedule,
                R.id.nav_profile
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        // Implement logout action
        // and override navigate action to remaining fragments.
        navView.setNavigationItemSelectedListener(this)

        // Set visible for drawer items to match with login state
        setVisibleForDrawerItems(navView)

        setUpNavigationHeader()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean { // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.drawer, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_drawer)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    // Implement logout action and override navigate action to other fragments.
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        item.isChecked = true

        // If you add a new fragment, you must add its MenuItem ID to the next line here.
        when (item.itemId) {
            R.id.nav_home, R.id.nav_health_management, R.id.nav_statistics, R.id.nav_sharing, R.id.nav_about, R.id.nav_settings, R.id.nav_schedule -> {
                val navController = findNavController(R.id.nav_host_fragment_content_drawer)
                navController.navigate(item.itemId)
            }
            R.id.nav_logout -> {
                binding.drawerLayout.closeDrawer(GravityCompat.START)
                Log.i("SignIn", "signed out")
                AuthenticationManager.instance.signOut(this)
                finish()
                return true
            }
            R.id.nav_login -> {
                binding.drawerLayout.closeDrawer(GravityCompat.START)
                Log.i("SignIn", "Starts LoginActivity intent from MainFeatureActivity")
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
                return true
            }
        }

        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun setVisibleForDrawerItems(navView: NavigationView) {
        val navMenu = navView.menu

        if (AuthenticationManager.instance.isSignedIn) {
            navMenu.findItem(R.id.nav_login).isVisible = false
        } else {
            navMenu.findItem(R.id.nav_health_management).isVisible = false
            navMenu.findItem(R.id.nav_statistics).isVisible = false
            navMenu.findItem(R.id.nav_sharing).isVisible = false
            navMenu.findItem(R.id.nav_about).isVisible = false
            navMenu.findItem(R.id.nav_settings).isVisible = false
            navMenu.findItem(R.id.nav_schedule).isVisible = false
            navMenu.findItem(R.id.nav_logout).isVisible = false
            navMenu.findItem(R.id.nav_login).isVisible = true
        }
    }

    private fun setUpNavigationHeader() {
        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val header = navView.getHeaderView(0)

        if (AuthenticationManager.instance.isSignedIn) {
            val navController = findNavController(R.id.nav_host_fragment_content_drawer)

            val avatarImageView = header.findViewById<ImageView>(R.id.nav_header_avatar)
            avatarImageView.setOnClickListener {
                navController.navigate(R.id.nav_profile)
                drawerLayout.closeDrawer(GravityCompat.START)
            }

            val userId = FirebaseAuth.getInstance().currentUser!!.uid
            UtilFunctions.fetchUserData(userId) { isSuccess, userData ->
                if (isSuccess && userData != null) {
                    val fullNameTextView = header.findViewById<TextView>(R.id.nav_header_full_name)
                    if (userData.profileImage.isEmpty()) {
                        avatarImageView.setImageResource(R.drawable.default_avatar)
                    } else {
                        avatarImageView.load(userData.profileImage)
                    }

                    fullNameTextView.text = userData.fullName
                }
            }
        } else {
            val fullNameTextView = header.findViewById<TextView>(R.id.nav_header_full_name)
            fullNameTextView.text = "Tài khoản khách"
        }
    }

}
