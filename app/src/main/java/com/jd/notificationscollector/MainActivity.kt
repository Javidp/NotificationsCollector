package com.jd.notificationscollector

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import com.jd.notificationscollector.apps.AppsFragment
import com.jd.notificationscollector.delete.DeleteNotificationsFragment
import com.jd.notificationscollector.notifications.NotificationsFragment

class MainActivity : AppCompatActivity(R.layout.activity_main) {

    private lateinit var toolbar: Toolbar
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navController: NavController

    private var activeFragmentTag: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        drawerLayout = findViewById(R.id.drawer_layout)
        val toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_drawer_content_desc, R.string.close_drawer_content_desc)
        drawerLayout.addDrawerListener(toggle)
        toggle.isDrawerIndicatorEnabled = true
        toggle.syncState()

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_notifications, R.id.nav_apps, R.id.nav_delete_notifications
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)

        val navView: NavigationView = findViewById(R.id.nav_view)
        navView.setupWithNavController(navController)
        navView.setNavigationItemSelectedListener { menuItem -> onNavigationItemSelect(menuItem) }

        initFragments(navView)
    }

    private fun initFragments(navView: NavigationView) {
        loadFragment(NotificationsFragment::class.java)
        toolbar.title = getString(R.string.menu_notifications)
        navView.setCheckedItem(R.id.nav_notifications)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        for (fragment in supportFragmentManager.fragments) {
            fragment?.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun onNavigationItemSelect(menuItem: MenuItem): Boolean {
        supportFragmentManager.findFragmentByTag(activeFragmentTag)?.let {
            supportFragmentManager.beginTransaction().hide(it).commit()
        }

        val status = when (menuItem.itemId) {
            R.id.nav_notifications -> {
                loadFragment(NotificationsFragment::class.java)
                toolbar.title = getString(R.string.menu_notifications)
                true
            }
            R.id.nav_apps -> {
                loadFragment(AppsFragment::class.java)
                toolbar.title = getString(R.string.menu_apps)
                true
            }
            R.id.nav_delete_notifications -> {
                loadFragment(DeleteNotificationsFragment::class.java)
                toolbar.title = getString(R.string.menu_delete_notifications)
                true
            }
            else -> false
        }

        drawerLayout.closeDrawers()
        return status
    }

    private fun loadFragment(fragmentType: Class<out Fragment>) {
        val activeFragment = supportFragmentManager.findFragmentByTag(fragmentType.canonicalName)
        if (activeFragment != null) {
            supportFragmentManager.beginTransaction()
                .show(activeFragment)
                .commit()
        } else {
            val fragment = fragmentType.newInstance()
            supportFragmentManager.beginTransaction()
                .add(R.id.nav_host_fragment, fragment, fragment.javaClass.canonicalName)
                .commit()
        }
        activeFragmentTag = fragmentType.canonicalName
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

}
