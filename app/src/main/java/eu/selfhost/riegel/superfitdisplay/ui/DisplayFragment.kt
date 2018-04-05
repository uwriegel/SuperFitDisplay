package eu.selfhost.riegel.superfitdisplay.ui

import android.annotation.SuppressLint
import android.location.Location
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.PagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebView
import com.google.gson.Gson
import eu.selfhost.riegel.superfitdisplay.LocationData
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

        displayWebView.addJavascriptInterface(object {
            @JavascriptInterface
            fun onLocation(longitude: Double, latitude: Double) {
                val location = Location("")
                location.longitude = longitude
                location.latitude = latitude
                (activity as DisplayActivity).getMapsFragment().onLocation(location)
            }

            @JavascriptInterface
            fun setTrack(trackString: String) {
                val g = Gson()
                val trackPoints = g.fromJson(trackString, Array<LocationData>::class.java)
                (activity as DisplayActivity).getMapsFragment().loadTrack(trackPoints)
            }
        }, "Native")

        val trackNumber = activity!!.intent.getLongExtra("TrackNumber", -1)
        if (trackNumber != -1L) {
            (activity as DisplayActivity).pager.currentItem = 1
            displayWebView.loadUrl("file:///android_asset/display.html#$trackNumber")
        }
        else
            displayWebView.loadUrl("file:///android_asset/display.html")

        return layout
    }

     private lateinit var displayWebView: WebView
}