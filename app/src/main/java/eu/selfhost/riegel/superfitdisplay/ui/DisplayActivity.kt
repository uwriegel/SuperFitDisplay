package eu.selfhost.riegel.superfitdisplay.ui

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import eu.selfhost.riegel.superfitdisplay.R
import android.view.View
import android.view.WindowManager

class DisplayActivity() : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display)

        pager = findViewById<ViewPager>(R.id.viewPager)
        pager.adapter = PagerAdapter(supportFragmentManager)
        pager.addOnPageChangeListener(object: ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {}

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

            override fun onPageSelected(position: Int) {
                if (position == 1) {
                    val maps = getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.viewPager + ":1") as MapsFragment
                    maps.setLocationCenter()
                }
            }
        })
    }

    fun getMapsFragment(): MapsFragment {
        return (pager.adapter as PagerAdapter).getFragmentForPosition(pager, 1) as MapsFragment
    }

    private inner class PagerAdapter(fm: FragmentManager?)
        : FragmentPagerAdapter(fm) {

        override fun getCount(): Int {
            return 2
        }

        override fun getItem(position: Int): Fragment {
            when (position) {
                0 -> return DisplayFragment()
                else -> return MapsFragment()
            }
        }

        fun getFragmentForPosition(viewPager: ViewPager, position: Int): Fragment {
            val tag = makeFragmentName(viewPager.getId(), getItemId(position))
            val fragment = supportFragmentManager.findFragmentByTag(tag)
            return fragment
        }

        private fun makeFragmentName(containerViewId: Int, id: Long): String {
            return "android:switcher:$containerViewId:$id"
        }
    }

    override fun onResume() {
        super.onResume()
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN or WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        // TODO: dimmable sreen on
        //val pm = getSystemService(Context.POWER_SERVICE) as PowerManager
        //this.wakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "My Tag")
        //this.wakeLock?.acquire()
    }

    override fun onPause() {
        super.onPause()
//        mainActivity = null
        // this.wakeLock?.release()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            val decorView = window.decorView
            decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        }
    }

    //private var wakeLock: PowerManager.WakeLock? = null
    lateinit var pager: ViewPager
}


