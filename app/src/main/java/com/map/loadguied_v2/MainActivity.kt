package com.map.loadguied_v2

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.map.loadguied_v2.createGuide.createGuideMainActivity


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        var createGuideBtn = findViewById<Button>(R.id.createGuideBtn)
        createGuideBtn.setOnClickListener {
//            Toast.makeText(applicationContext, "클릭이벤트", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, createGuideMainActivity::class.java)

            Toast.makeText(applicationContext, "클릭이벤트", Toast.LENGTH_SHORT).show()

            startActivity(intent)
        }

    }
}