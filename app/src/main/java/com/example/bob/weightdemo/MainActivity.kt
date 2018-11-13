package com.example.bob.weightdemo

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    lateinit var pair :TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        pair = findViewById(R.id.search)

        pair.setOnClickListener{
            pairClick()
        }
    }

    private fun pairClick(){
        startActivity(Intent(MainActivity@this, PairActivity::class.java))
    }
}
