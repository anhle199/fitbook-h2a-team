package com.h2a.fitbook.views.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.h2a.fitbook.R

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.goMainBtn).setOnClickListener {
            val intent = Intent(this, MainFeatureActivity::class.java)

            startActivity(intent)
            finish()
        }
    }
}