package com.tinysoft.pokemon.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.tinysoft.pokemon.R
import com.tinysoft.pokemon.databinding.ActivityMainBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {
    private val viewModel by viewModel<MainViewModel>()

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupNavigationController()

        viewModel.fetchPokemonContent()
        viewModel.forceReload()
    }

    private fun setupNavigationController() {
        val navHost = supportFragmentManager.findFragmentById(R.id.fragment_container) as NavHostFragment
        val navController = navHost.navController
        val navGraph = navController.navInflater.inflate(R.navigation.main_graph)
        navController.graph = navGraph
        getBottomNavigationView().setupWithNavController(navController)
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.action_home -> {
                    // Show Bottom Navigation Bar
                    setBottomBarVisibility(true)
                }
                else -> setBottomBarVisibility(false) // Hide Bottom Navigation Bar
            }

        }
    }

    private fun getBottomNavigationView(): BottomNavigationView {
        return binding.bottomNavigationView
    }

    fun setBottomBarVisibility(visible: Boolean) {
        binding.bottomNavigationView.visibility = if (visible) View.VISIBLE else View.GONE
    }

    companion object {
        const val TAG = "MainActivity"
    }
}