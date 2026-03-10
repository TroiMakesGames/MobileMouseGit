package com.troi.mobilemouse

import android.os.Bundle
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity
import java.io.OutputStream
import java.net.Socket
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {

    private val pcIp = "192.168.31.245"  // Replace with your PC IP
    private val pcPort = 5000          // Port your server will listen on

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Full screen tappable view
        val view = object : android.view.View(this) {
            override fun onTouchEvent(event: MotionEvent?): Boolean {
                if (event?.action == MotionEvent.ACTION_DOWN) {
                    sendTapToPC()
                }
                return true
            }
        }

        setContentView(view)
    }

    private fun sendTapToPC() {
        thread {
            try {
                Socket(pcIp, pcPort).use { socket ->
                    val out: OutputStream = socket.getOutputStream()
                    out.write("tap\n".toByteArray())
                    out.flush()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}