package com.tinysoft.pokemon.fragments

import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.navigation.navOptions
import com.tinysoft.pokemon.R
import com.tinysoft.pokemon.activities.MainActivity
import com.tinysoft.pokemon.activities.MainViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

abstract class AbsMainFragment(@LayoutRes layout: Int) : Fragment(layout) {
    val mainViewModel: MainViewModel by sharedViewModel()

    val mainActivity: MainActivity
        get() = activity as MainActivity

    val navOptions by lazy {
        navOptions {
            launchSingleTop = false
            anim {
                enter = R.anim.fragment_open_enter
                exit = R.anim.fragment_open_exit
                popEnter = R.anim.fragment_close_enter
                popExit = R.anim.fragment_close_exit
            }
        }
    }
}