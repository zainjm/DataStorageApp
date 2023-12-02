package com.example.datastorageapp
import android.app.Application
import com.example.datastorageapp.data.AppDatabase

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // Initialize the database
        AppDatabase.getDatabase(this)
    }
}
