package com.example.movieapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupWithNavController
import com.example.movieapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private lateinit var appBarConfig: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding =
            DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
        val navHost =
            supportFragmentManager.findFragmentById(R.id.fragmentContainer) as NavHostFragment
        navController = navHost.navController
        binding.bottomNav.setupWithNavController(navController)
        setSupportActionBar(findViewById(R.id.toolbarMovie))
        appBarConfig = AppBarConfiguration(navController.graph)
        findViewById<Toolbar>(R.id.toolbarMovie).setupWithNavController(navController, appBarConfig)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_favorite, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.favoriteMoviesFragment -> {
                navController.navigate(R.id.favoriteMoviesFragment)
            }
        }
        return false
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfig) || super.onSupportNavigateUp()
    }
}