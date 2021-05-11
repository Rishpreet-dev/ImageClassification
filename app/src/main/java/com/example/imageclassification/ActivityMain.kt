package com.example.imageclassification

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.AppCompatButton

class ActivityMain : AppCompatActivity() {

    private lateinit var imgClassificationBtn: AppCompatButton
    private lateinit var arGlassesBtn: AppCompatButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        imgClassificationBtn = findViewById(R.id.img_classification_btn)
        arGlassesBtn = findViewById(R.id.ar_glasses_btn)

        imgClassificationBtn.setOnClickListener {
            startActivity(Intent(applicationContext, ActivityImageClassification::class.java))
        }

        arGlassesBtn.setOnClickListener {
            startActivity(Intent(applicationContext, ActivityARGlasses::class.java))
        }

    }
}