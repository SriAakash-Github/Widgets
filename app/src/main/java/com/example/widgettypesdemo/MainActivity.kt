package com.example.widgettypesdemo

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.GridView
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    var items: Array<String> = arrayOf("One", "Two", "Three", "Four", "Five")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Handle widget-specific intents
        handleWidgetIntents()

        // GridView (Collection Widget)
        val gridView = findViewById<GridView>(R.id.gridView)
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1, items
        )
        gridView.adapter = adapter

        // Button (Control Widget)
        val button = findViewById<Button>(R.id.buttonClick)
        button.setOnClickListener { v: View? ->
            Toast.makeText(
                this,
                "Button Clicked!",
                Toast.LENGTH_SHORT
            ).show()
        }

        // Switch
        val switchToggle = findViewById<Switch>(R.id.switchToggle)
        switchToggle.setOnCheckedChangeListener { compoundButton: CompoundButton?, isChecked: Boolean ->
            Toast.makeText(
                this,
                "Switch: $isChecked", Toast.LENGTH_SHORT
            ).show()
        }

        // CheckBox
        val checkBox = findViewById<CheckBox>(R.id.checkBox)
        checkBox.setOnCheckedChangeListener { compoundButton: CompoundButton?, isChecked: Boolean ->
            Toast.makeText(
                this,
                "Checkbox: $isChecked", Toast.LENGTH_SHORT
            ).show()
        }

        // Hybrid Widget (Song Controller Simulation)
        val songTitle = findViewById<TextView>(R.id.songTitle)
        val btnPrev = findViewById<Button>(R.id.btnPrev)
        val btnNext = findViewById<Button>(R.id.btnNext)

        btnPrev.setOnClickListener { v: View? ->
            songTitle.text = "Previous Song"
        }
        btnNext.setOnClickListener { v: View? ->
            songTitle.text = "Next Song"
        }
    }
    
    private fun handleWidgetIntents() {
        val intent = intent
        
        // Handle collection widget item selection
        val selectedItem = intent.getStringExtra("selected_item")
        if (selectedItem != null) {
            Toast.makeText(this, "Selected item: $selectedItem", Toast.LENGTH_LONG).show()
        }
        
        // Handle hybrid widget player opening
        val openPlayer = intent.getBooleanExtra("open_player", false)
        if (openPlayer) {
            Toast.makeText(this, "Opening Media Player", Toast.LENGTH_SHORT).show()
            // Could navigate to a specific fragment or activity here
        }
    }
}