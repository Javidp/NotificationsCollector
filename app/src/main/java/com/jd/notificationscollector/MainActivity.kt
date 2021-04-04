package com.jd.notificationscollector

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
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

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navController: NavController

    private var activeFragmentTag: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
//        val toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_drawer_content_desc, R.string.close_drawer_content_desc)
//        drawerLayout.addDrawerListener(toggle)
//        toggle.isDrawerIndicatorEnabled = true
//        toggle.syncState()

        val navView: NavigationView = findViewById(R.id.nav_view)
//        val navController = findNavController(R.id.nav_host_fragment)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_notifications, R.id.nav_apps, R.id.nav_delete_notifications
            ), drawerLayout
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)




//        navView.setNavigationItemSelectedListener { menuItem ->
//
//            val status = when(menuItem.itemId) {
//                R.id.nav_notifications -> {
//                    navController.navigate(R.id.nav_notifications)
//                    true
//                }
//                R.id.nav_apps -> {
//                    navController.navigate(R.id.nav_apps)
//                    true
//                }
//                R.id.nav_delete_notifications -> {
//                    navController.navigate(R.id.nav_delete_notifications)
//                    true
//                }
//                else -> false
//            }
//
//            drawerLayout.closeDrawers()
//            status
//        }


//        navHostFragment.id

//        Log.i("TEST", "nav host id: ${R.id.nav_host_fragment}")
//        val initFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)?.childFragmentManager?.fragments?.get(0)
////        val initFragment = supportFragmentManager.findFragmentByTag("DEFAULT_TAG")
////        val initFragment = supportFragmentManager.findFragmentById(navController.currentDestination?.id ?: 0)
////        val initFragment = supportFragmentManager.primaryNavigationFragment
//        Log.i("TEST", "init fragment: $initFragment")
//        Log.i("TEST", "init fragment tag: ${initFragment?.tag}")
//        Log.i("TEST", "init fragment id: ${initFragment?.id}, notifications fragment id: ${R.id.nav_notifications}")
//        Log.i("TEST", "current destination id: ${navController.currentDestination?.id}")
//        Log.i("TEST", "nav host fragment id: ${navHostFragment.id}")
//
//
//        if (initFragment != null) {
//            supportFragmentManager.beginTransaction().replace(R.id.nav_host_fragment, initFragment, initFragment.javaClass.canonicalName).commit()
//            activeFragmentTag = initFragment.javaClass.canonicalName
//        } else {
//            val fragment = NotificationsFragment()
//            supportFragmentManager.beginTransaction().add(
//                R.id.nav_host_fragment,
//                fragment,
//                fragment.javaClass.canonicalName
//            ).commit()
//            activeFragmentTag = fragment.javaClass.canonicalName
//        }
//        toolbar.title = getString(R.string.menu_notifications)
//
//
//
//        navView.setNavigationItemSelectedListener { menuItem ->
//            supportFragmentManager.findFragmentByTag(activeFragmentTag)?.let {
//                supportFragmentManager.beginTransaction().hide(it).commit()
//            }
//
//            val status = when (menuItem.itemId) {
//                R.id.nav_notifications -> {
//                    val activeNotificationsFragment = supportFragmentManager.findFragmentByTag(
//                        NotificationsFragment::class.java.canonicalName
//                    )
//                    if (activeNotificationsFragment != null) {
//                        supportFragmentManager.beginTransaction().show(activeNotificationsFragment)
//                            .commit()
//                    } else {
//                        val notificationsFragment = NotificationsFragment()
//                        supportFragmentManager.beginTransaction().add(
//                            R.id.nav_host_fragment,
//                            notificationsFragment,
//                            notificationsFragment.javaClass.canonicalName
//                        ).commit()
//                    }
//                    activeFragmentTag = NotificationsFragment::class.java.canonicalName
//                    toolbar.title = getString(R.string.menu_notifications)
//                    true
//                }
//                R.id.nav_apps -> {
//                    val activeAppsFragment =
//                        supportFragmentManager.findFragmentByTag(AppsFragment::class.java.canonicalName)
//                    if (activeAppsFragment != null) {
//                        supportFragmentManager.beginTransaction().show(activeAppsFragment).commit()
//                    } else {
//                        val appsFragment = AppsFragment()
//                        supportFragmentManager.beginTransaction().add(
//                            R.id.nav_host_fragment,
//                            appsFragment,
//                            appsFragment.javaClass.canonicalName
//                        ).commit()
//                    }
//                    activeFragmentTag = AppsFragment::class.java.canonicalName
//                    toolbar.title = getString(R.string.menu_apps)
//                    true
//                }
//                R.id.nav_delete_notifications -> {
//                    val activeDeleteNotificationsFragment =
//                        supportFragmentManager.findFragmentByTag(
//                            DeleteNotificationsFragment::class.java.canonicalName
//                        )
//                    if (activeDeleteNotificationsFragment != null) {
//                        supportFragmentManager.beginTransaction().show(
//                            activeDeleteNotificationsFragment
//                        ).commit()
//                    } else {
//                        val deleteNotificationsFragment = DeleteNotificationsFragment()
//                        supportFragmentManager.beginTransaction().add(
//                            R.id.nav_host_fragment,
//                            deleteNotificationsFragment,
//                            deleteNotificationsFragment.javaClass.canonicalName
//                        ).commit()
//                    }
//                    activeFragmentTag = DeleteNotificationsFragment::class.java.canonicalName
//                    toolbar.title = getString(R.string.menu_delete_notifications)
//                    true
//                }
//                else -> false
//            }
//
//            drawerLayout.closeDrawers()
//            status
//        }
    }

    override fun onSupportNavigateUp(): Boolean {
//        val navController = findNavController(R.id.nav_host_fragment)
//        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
//        val navController = navHostFragment.navController
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

}
