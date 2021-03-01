package com.lediya.homelauncher

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DividerItemDecoration
import com.lediya.appmanager.PackageManager
import com.lediya.appmanager.model.AppDetail
import com.lediya.homelauncher.adapter.AppAdapter
import com.lediya.homelauncher.databinding.ActivityHomeBinding


@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
class HomeActivity : AppCompatActivity() {
    private lateinit var apps: MutableList<AppDetail>
    private lateinit var binding: ActivityHomeBinding
    private var  appAdapter = AppAdapter()
    private lateinit var manager: PackageManager
    private var applicationReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        @RequiresApi(Build.VERSION_CODES.P)
        override  fun onReceive(context: Context, intent: Intent) {
            if(!intent.action.isNullOrBlank()&&! intent.action.isNullOrEmpty()){
                if(intent.action.equals(Intent.ACTION_PACKAGE_ADDED)){
                    Toast.makeText(context, "${manager.getApplicationName(context, intent)} Installed", Toast.LENGTH_LONG).show()
                    loadData()
                } else if(intent.action.equals(Intent.ACTION_PACKAGE_REMOVED)){
                    loadData()
                }
            }
        }
    }
    private fun registerBroadCastReceiver(){
        val filter = IntentFilter(Intent.ACTION_PACKAGE_ADDED)
        filter.addAction(Intent.ACTION_PACKAGE_REMOVED)
        filter.addAction(Intent.ACTION_PACKAGE_CHANGED)
        filter.addDataScheme("package")
        registerReceiver(applicationReceiver, filter)
    }

    @RequiresApi(Build.VERSION_CODES.P)
     private fun loadData() {
        apps= manager.loadApps()
        apps.sortBy { it.label.toString() }
        appAdapter.setItems(apps)
    }
    private fun setAdapter(){
        binding.recyclerView.adapter = appAdapter
        binding.recyclerView.addItemDecoration(DividerItemDecoration( binding.recyclerView.getContext(), DividerItemDecoration.VERTICAL))
    }
    private fun listener(){
        appAdapter.onItemClick = { position ->
            val i = manager.getPackageManager().getLaunchIntentForPackage(apps[position].packageName.toString())
            this@HomeActivity.startActivity(i)

        }
        appAdapter.onItemLongClick = { position ->
            val packageURI = Uri.parse("package:"+apps[position].packageName.toString())
            val uninstallIntent = Intent(Intent.ACTION_DELETE, packageURI)
            startActivity(uninstallIntent)
        }
        binding.searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                // filter recycler view when query submitted
                appAdapter.getFilter().filter(query)
                return false
            }

            override fun onQueryTextChange(query: String): Boolean {
                // filter recycler view when text is changed
                appAdapter.getFilter().filter(query)
                return false
            }
        })
    }
    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home)
        manager= PackageManager(packageManager)
        setAdapter()
        listener()
        loadData()
        registerBroadCastReceiver()
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(applicationReceiver)
    }

}
