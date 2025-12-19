package com.example.realtimechat.data.socket

import android.R.id.message
import android.util.Log
import com.example.realtimechat.utils.Constant
import io.socket.client.IO
import io.socket.client.Socket
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString
import java.net.URISyntaxException
import java.util.concurrent.TimeUnit

private const val TAG = "SocketHandler"

/**
 * Manages the WebSocket connection for real-time chat communication.
 *
 * This handler wraps an [OkHttpClient] WebSocket implementation, providing a simplified interface
 * to connect, disconnect, send messages, and listen for incoming events. It handles the lifecycle
 * of the socket connection and dispatches events via callback functions.
 *
 * @property onMessageReceived A callback function invoked when a new text or byte message is received from the server.
 *                             The received data is passed as a UTF-8 string.
 * @property onConnectionChanged A callback function invoked when the connection state changes.
 *                               It provides a boolean indicating connected status (true = open, false = closed/failed)
 *                               and a String message describing the event (e.g., error reason or status code).
 */
class SocketHandler(
    private val onMessageReceived: (String) -> Unit,
    private val onConnectionChanged: (Boolean, String) -> Unit
) {

    /*============ This Code Use for Socket.IO =================*/
    //    Enter Socket.IO Server URL and click on connect
    /*private var socket: Socket? = null

    init {
        try {
            // Options to force WebSocket to avoid polling issues
            val options = IO.Options().apply {
                transports = arrayOf(io.socket.engineio.client.transports.WebSocket.NAME)
            }
            socket = IO.socket(Constant.SERVER_URL, options)
        } catch (e: URISyntaxException) {
            e.printStackTrace()
        }
    }

    fun connect() {
        socket?.connect()
        setupListeners()
    }

    private fun setupListeners() {
        socket?.on(Socket.EVENT_CONNECT) {
            onConnectionChanged(true, "Connected")
        }

        socket?.on(Socket.EVENT_DISCONNECT) {
            onConnectionChanged(false, "Disconnected")
        }

        // Listen for specific event name defined by server
        socket?.on("receive_message") { args ->
            if (args.isNotEmpty()) {
                val data = args[0].toString()
                onMessageReceived(data)
            }
        }
    }

    fun send(message: String): Boolean {
        socket?.emit("send_message", message)
        return true
    }

    fun disconnect() {
        socket?.disconnect()
        socket?.off()
    }*/
    /*============ END =================*/


    /*============ This Code Use for Web Socket with OkHttp =================*/
    private val client = OkHttpClient.Builder()
        .pingInterval(15, TimeUnit.SECONDS)
        .build()

    private var webSocket: WebSocket? = null

    private val request = Request.Builder()
        .url(Constant.WS_LIVE_URL)
        .build()

    fun connect() {
        webSocket = client.newWebSocket(request, socketListener)
    }

    fun send(message: String): Boolean {
        return webSocket?.send(message) ?: false
    }

    fun disconnect() {
        webSocket?.close(1000, "App closed")
    }

    private val socketListener = object : WebSocketListener() {

        override fun onOpen(webSocket: WebSocket, response: Response) {
            Log.d(TAG, "onOpen: $response")
            onConnectionChanged(true, response.message)
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            Log.d(TAG, "onMessage: $text")
            onMessageReceived(text)
        }

        override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
            Log.d(TAG, "onMessage: $bytes")
            onMessageReceived(bytes.utf8())
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            Log.d(TAG, "onFailure: ${t.message}, $response")
            onConnectionChanged(false, response?.message ?: t.message.toString())
        }

        override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
            Log.d(TAG, "onClosed: $code, $reason")
            onConnectionChanged(false, "$code, $reason")
        }
    }

    /*============ END =================*/
}