package eu.selfhost.riegel.superfitdisplay.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebView
import eu.selfhost.riegel.superfitdisplay.R

class DisplayFragment : Fragment() {
    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val layout =  inflater.inflate(R.layout.display_fragment, container, false)

        displayWebView = layout.findViewById(R.id.displayWebView)
        displayWebView.setBackgroundColor(0)
        val webSettings = displayWebView.settings
        webSettings.javaScriptEnabled = true
        // webSettings.domStorageEnabled = true
        WebView.setWebContentsDebuggingEnabled(true)
        // CORS allowed
        webSettings.allowUniversalAccessFromFileURLs = true
        displayWebView.webChromeClient = WebChromeClient()

        displayWebView.isHapticFeedbackEnabled = true
        displayWebView.loadUrl("file:///android_asset/display.html")

        return layout
    }

     private lateinit var displayWebView: WebView
}