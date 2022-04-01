package com.h2a.fitbook

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.goMainBtn).setOnClickListener {
            val intent = Intent(this, DrawerActivity::class.java)

            startActivity(intent)
            finish()
        }
    }
}