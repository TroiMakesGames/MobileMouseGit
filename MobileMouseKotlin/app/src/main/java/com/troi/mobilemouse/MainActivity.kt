package com.troi.mobilemouse

import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import java.net.Socket
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {

    // PC connection
    private val pcIp = "192.168.31.245"   // CHANGE TO YOUR PC IP
    private val pcPort = 5000

    // event queue
    private val eventQueue = ConcurrentLinkedQueue<String>()

    // move throttling
    private var moveCounter = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val view = View(this)
        setContentView(view)

        thread {
            try {
                val socket = Socket(pcIp, pcPort)
                val writer = socket.getOutputStream().bufferedWriter()

                while (true) {
                    val msg = eventQueue.poll()
                    if (msg != null) {
                        writer.write(msg)
                        writer.newLine()
                        writer.flush()
                    }
                    Thread.sleep(2) // small delay to avoid CPU spin
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y

        when (event.action) {

            MotionEvent.ACTION_DOWN -> {
                eventQueue.add("DOWN,$x,$y")
            }

            MotionEvent.ACTION_MOVE -> {
                moveCounter++

                // Send only every 3rd move
                if (moveCounter % 5 == 0) {
                    eventQueue.add("MOVE,$x,$y")
                }
            }

            MotionEvent.ACTION_UP -> {
                eventQueue.add("UP,$x,$y")
                moveCounter = 0
            }
        }

        return true
    }

    private fun startSenderThread() {

        Thread {

            try {

                val socket = Socket(pcIp, pcPort)
                val writer = socket.getOutputStream().bufferedWriter()

                while (true) {

                    val msg = eventQueue.poll()

                    if (msg != null) {
                        writer.write(msg)
                        writer.newLine()
                        writer.flush()
                    }

                    Thread.sleep(2)
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }

        }.start()
    }
}