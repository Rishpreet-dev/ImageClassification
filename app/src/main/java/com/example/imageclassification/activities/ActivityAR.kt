package com.example.imageclassification.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.example.imageclassification.R

class ActivityAR: AppCompatActivity() {

    private lateinit var arGlassesBtn: AppCompatButton
    private lateinit var arJewelleryBtn: AppCompatButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ar)

        arGlassesBtn = findViewById(R.id.activity_glasses_btn)
        arJewelleryBtn = findViewById(R.id.activity_jewellery_btn)

        arGlassesBtn.setOnClickListener {
            startActivity(Intent(this, ActivityARGlasses::class.java))
        }

        arJewelleryBtn.setOnClickListener {
            startActivity(Intent(this, ActivityARJewellery::class.java))
        }
    }
}