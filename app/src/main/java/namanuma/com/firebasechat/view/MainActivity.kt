package namanuma.com.firebasechat.view

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import namanuma.com.firebasechat.R
import namanuma.com.firebasechat.utils.FragmentType
import namanuma.com.firebasechat.view.activity.LoginActivity
import namanuma.com.firebasechat.view.fragment.ChatFragment
import namanuma.com.firebasechat.view.fragment.UserFragment

class MainActivity : AppCompatActivity() {

    private var auth: FirebaseAuth? = null
    private var viewPager: ViewPager? = null
    private var tabLayout: TabLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewPager = findViewById(R.id.viewPager) as ViewPager
        tabLayout = findViewById(R.id.tabLayout) as TabLayout

        auth = FirebaseAuth.getInstance()

        viewPager?.adapter = ViewPageAdapter(supportFragmentManager)
        viewPager?.addOnPageChangeListener(onPageChangeListener())
        tabLayout?.setupWithViewPager(viewPager, true)
        tabLayout?.visibility = View.GONE
    }

    public override fun onStart() {
        super.onStart()

        checkLogin()
    }

    public override fun onStop() {
        super.onStop()
    }

    private fun checkLogin() {
        auth?.let {
            if (it.currentUser == null) {
                startActivity(Intent(this, LoginActivity::class.java))
            }
        }
    }

    private fun logout() {
        auth?.let {
            it.signOut()
        }
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

        override fun getItem(position: Int): Fragment = ChatFragment()

        override fun getPageTitle(position: Int): CharSequence = FragmentType.values()[position].tabName

        override fun getCount(): Int = FragmentType.values().size
    }
}
