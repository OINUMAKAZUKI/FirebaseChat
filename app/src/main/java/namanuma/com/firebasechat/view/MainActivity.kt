package namanuma.com.firebasechat.view

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import namanuma.com.firebasechat.R
import namanuma.com.firebasechat.utils.FragmentType
import namanuma.com.firebasechat.view.fragment.ChatFragment
import namanuma.com.firebasechat.view.fragment.UserFragment

class MainActivity : AppCompatActivity() {

    private var viewPager: ViewPager? = null
    private var tabLayout: TabLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewPager = findViewById(R.id.viewPager) as ViewPager
        tabLayout = findViewById(R.id.tabLayout) as TabLayout

        viewPager?.adapter = ViewPageAdapter(supportFragmentManager)
        viewPager?.addOnPageChangeListener(onPageChangeListener())
        tabLayout?.setupWithViewPager(viewPager, true)
    }

    private fun onPageChangeListener(): ViewPager.OnPageChangeListener {
        return object: ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
            }
        }
    }

    private inner class ViewPageAdapter(fm: FragmentManager?) : FragmentPagerAdapter(fm) {

        override fun getItem(position: Int): Fragment = when(FragmentType.values()[position]) {
            FragmentType.HOME -> UserFragment()
            else -> ChatFragment()
        }

        override fun getPageTitle(position: Int): CharSequence = FragmentType.values()[position].tabName

        override fun getCount(): Int = FragmentType.values().size
    }
}
