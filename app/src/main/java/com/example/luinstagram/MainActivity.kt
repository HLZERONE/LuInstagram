package com.example.luinstagram

import android.app.Notification
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.media.Image
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.luinstagram.fragments.ComposeFragment
import com.example.luinstagram.fragments.FeedFragment
import com.example.luinstagram.fragments.ProfileFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.parse.*
import java.io.File
import android.text.style.AlignmentSpan

import android.text.SpannableString
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.ActionBar.DISPLAY_SHOW_CUSTOM
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager


class MainActivity : AppCompatActivity() {

    lateinit var bottomNavigation: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottomNavigation = findViewById(R.id.bottom_navigation)

        val fragmentManager: FragmentManager = supportFragmentManager

        //ACTION BAR
        //set color for actionbar
        val actionBar = supportActionBar
        val colorDrawable = ColorDrawable(Color.parseColor("#FFFFFF"))

        actionBar?.setBackgroundDrawable(colorDrawable)

        //Center logo in action bar
        actionBar?.setDisplayOptions(DISPLAY_SHOW_CUSTOM)
        actionBar?.setCustomView(R.layout.action_bar_title)

        //Set color to null
        bottomNavigation.itemIconTintList = null

        //Set On clicker for Bottom Navigation
        bottomNavigation.setOnItemSelectedListener {
                item->
            var fragmentToShow : Fragment? = null
            when(item.itemId){
                R.id.action_home -> {
                    fragmentToShow = FeedFragment()
                    //item.icon = ContextCompat.getDrawable(this, R.drawable.instagram_home_filled_24)
                }
                R.id.action_compose -> {
                    fragmentToShow = ComposeFragment()
                }
                R.id.action_profile -> {
                    fragmentToShow = ProfileFragment()
                }
            }

            if(fragmentToShow != null){
                fragmentManager.beginTransaction().replace(R.id.flContainer, fragmentToShow).commit()
            }

            true
        }


        // Set default selection
        bottomNavigation.selectedItemId = R.id.action_home
        //queryPosts()
    }


    companion object{
        const val TAG = "MainActivity"
    }
}
