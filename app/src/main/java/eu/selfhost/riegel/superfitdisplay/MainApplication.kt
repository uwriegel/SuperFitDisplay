package eu.selfhost.riegel.superfitdisplay

import android.app.Application
import org.mapsforge.map.android.graphics.AndroidGraphicFactory

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        AndroidGraphicFactory.createInstance(this)
    }
}