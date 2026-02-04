package com.fly.motion.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.fly.motion.ui.MainActivity

class FlymoActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Redirect to UI Main Activity
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
