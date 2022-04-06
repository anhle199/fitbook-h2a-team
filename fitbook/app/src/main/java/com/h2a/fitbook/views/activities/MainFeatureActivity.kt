package com.h2a.fitbook.views.activities

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import com.h2a.fitbook.R
import com.h2a.fitbook.databinding.ActivityDrawerBinding

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
        val navController = findNavController(R.id.nav_host_fragment_content_drawer)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow, R.id.nav_health_management, R.id.nav_statistics, R.id.nav_about, R.id.nav_settings
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        // Implement logout action
        // and override navigate action to remaining fragments.
        navView.setNavigationItemSelectedListener(this)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.drawer, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_drawer)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    // Implement logout action and override navigate action to remaining fragments.
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        item.isChecked = true

        when (item.itemId) {
            // If you add a new fragment, you must be add its MenuItem ID to below.
            R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow, R.id.nav_health_management, R.id.nav_statistics, R.id.nav_about, R.id.nav_settings -> {
                val navController = findNavController(R.id.nav_host_fragment_content_drawer)
                navController.navigate(item.itemId)
            }
            R.id.nav_logout -> {
                binding.drawerLayout.closeDrawer(GravityCompat.START)
                finish()
                return true
            }
        }

        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }
}
