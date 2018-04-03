package eu.selfhost.riegel.superfitdisplay.ui

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import eu.selfhost.riegel.superfitdisplay.R
import eu.selfhost.riegel.superfitdisplay.maps.LocationSetter
import eu.selfhost.riegel.superfitdisplay.maps.MapView
import eu.selfhost.riegel.superfitdisplay.maps.TrackingLine
import org.mapsforge.core.model.LatLong
import org.mapsforge.map.android.graphics.AndroidGraphicFactory
import org.mapsforge.map.android.rotation.RotateView
import org.mapsforge.map.android.util.AndroidPreferences
import org.mapsforge.map.android.util.AndroidUtil
import org.mapsforge.map.datastore.MapDataStore
import org.mapsforge.map.layer.cache.TileCache
import org.mapsforge.map.model.common.PreferencesFacade
import org.mapsforge.map.reader.MapFile
import org.mapsforge.map.rendertheme.InternalRenderTheme
import java.io.File
import java.util.ArrayList

class MapsFragment : Fragment(), LocationSetter {
    @SuppressLint("MissingPermission")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val layout = inflater.inflate(R.layout.map_fragment, container, false)
        this.preferencesFacade = AndroidPreferences(this.activity!!.getSharedPreferences(this.javaClass.simpleName, Context.MODE_PRIVATE))
        mapView = layout.findViewById(R.id.mapView)
        layout.findViewById<RotateView>(R.id.rotateView).setLayerType(View.LAYER_TYPE_SOFTWARE, null)
        mapView!!.model.frameBufferModel.overdrawFactor = 1.0
        mapView!!.model.init(this.preferencesFacade)
        mapView!!.isClickable = true
        mapView!!.mapScaleBar!!.isVisible = true // false

        createTileCaches()
        createLayers()

        mapView!!.setLocationSetter(this)
        mapView!!.layerManager!!.layers.add(currentTrack)
        return layout
    }

    override fun changeValue(followLocation: Boolean) {
        this.followLocation = followLocation
        if (this.followLocation && recentLocation != null)
            mapView!!.setCenter(LatLong(recentLocation!!.latitude, recentLocation!!.longitude))
    }

    fun setLocationCenter() {
        mapView!!.onCenter()
    }

    override fun onPause() {
        mapView!!.model.save(this.preferencesFacade)
        this.preferencesFacade.save()
        super.onPause()
    }

    override fun onDestroyView() {
        mapView = null
        super.onDestroyView()
    }

    fun onLocation(location: Location) {
        recentLocation = location
        if (mapView != null) {

            val latLong = LatLong(location.latitude, location.longitude)

            currentTrack.latLongs.add(latLong)
            currentTrack.requestRedraw()

            if (followLocation)
                mapView!!.setCenter(latLong)
            if (::center.isInitialized)
                mapView!!.layerManager!!.layers.remove(center)
            center = LocationMarker(latLong)
            mapView!!.layerManager!!.layers.add(center)
        }
    }

    private fun createTileCaches() {
        this.tileCaches.add(AndroidUtil.createTileCache(activity, this.javaClass.simpleName,
                mapView!!.model.displayModel.tileSize, 1.0f,
                mapView!!.model.frameBufferModel.overdrawFactor))
    }

    private fun createLayers() {
        val tileRendererLayer = AndroidUtil.createTileRendererLayer(this.tileCaches[0],
                this.mapView!!.model.mapViewPosition, getMapFile(), InternalRenderTheme.OSMARENDER, false, true, false)
        this.mapView!!.layerManager!!.layers.add(tileRendererLayer)
    }

    private fun getMapFile(): MapDataStore {
        return MapFile(File(getMapFileDirectory(), "germany.map"))
    }

    private fun getMapFileDirectory(): File {
        val dir = getExternalStorageDirectory(activity!!)
        return File(dir + "/Maps")
    }

    private fun getRootOfExternalStorage(file: File, context: Context): String =
            file.absolutePath.replace("/Android/data/${context.packageName}/files".toRegex(), "")

    private  fun getExternalStorageDirectory(context: Context): String {
        val externalStorageFiles = ContextCompat.getExternalFilesDirs(context, null)
        return externalStorageFiles.map { getRootOfExternalStorage(it, context) }.filter { !it.contains("emulated") }.first()
    }

    private lateinit var preferencesFacade: PreferencesFacade
    private var mapView: MapView? = null
    private var tileCaches: MutableList<TileCache> = ArrayList()
    private var followLocation = true
    private var recentLocation: Location? = null
    private val currentTrack = TrackingLine(AndroidGraphicFactory.INSTANCE)

    private lateinit var center: LocationMarker
}