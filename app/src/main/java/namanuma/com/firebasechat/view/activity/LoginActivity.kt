package namanuma.com.firebasechat.view.activity

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.TargetApi
import android.content.pm.PackageManager
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.app.LoaderManager.LoaderCallbacks

import android.content.CursorLoader
import android.content.Loader
import android.database.Cursor
import android.net.Uri
import android.os.AsyncTask

import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import android.text.TextUtils
import android.view.KeyEvent
import android.view.View
import android.view.View.OnClickListener
import android.view.inputmethod.EditorInfo

import java.util.ArrayList

import namanuma.com.firebasechat.R

import android.Manifest.permission.READ_CONTACTS
import android.util.Log
import android.widget.*
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import namanuma.com.firebasechat.model.User

class LoginActivity : AppCompatActivity() {

    // UI references.
    private var userNameView: EditText? = null
    private var progressView: View? = null
    private var auth: FirebaseAuth? = null
    private var database: DatabaseReference? = null
    private var analytics: FirebaseAnalytics? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        userNameView = findViewById(R.id.userName) as EditText
        progressView = findViewById(R.id.loginProgress)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference
        analytics = FirebaseAnalytics.getInstance(this)

        val signInButton = findViewById(R.id.signInButton) as Button
        signInButton.setOnClickListener {
            attemptLogin()
        }

    }

    public override fun onResume() {
        super.onResume()
        analytics?.setCurrentScreen(this, "ログイン画面", null)
    }

    private fun attemptLogin() {
        // Reset errors.
        userNameView!!.error = null

        // Store values at the time of the login attempt.
        val name = userNameView!!.text.toString()

        var cancel = false
        var focusView: View? = null

        // Check for a valid email address.
        if (TextUtils.isEmpty(name)) {
            userNameView!!.error = getString(R.string.error_field_required)
            focusView = userNameView
            cancel = true
        }

        if (cancel) {
            focusView!!.requestFocus()
        } else {
            login(name)
        }
    }

    private fun login(email: String, password: String) {
        auth?.let {
            it.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {

                        }
                    }
        }
    }

    private fun login(name: String) {
        val context = this
        showProgress(true)
        auth?.let {
            it.signInAnonymously().addOnCompleteListener {
                showProgress(false)
                if (it.isSuccessful) {
                    Toast.makeText(context, "successful", Toast.LENGTH_SHORT).show()
                    createUser(it.result.user.uid, name)
                    // Analytics
                    analytics?.logEvent(FirebaseAnalytics.Event.LOGIN, null)
                    finish()
                } else {
                    Toast.makeText(context, "error:" + it.exception!!.localizedMessage, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun createUser(id: String, name: String) {
        val user = User(id, name)
        database?.child("users")?.child(id)?.setValue(user)
    }

    private fun showProgress(show: Boolean) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            val shortAnimTime = resources.getInteger(android.R.integer.config_shortAnimTime)

            progressView!!.visibility = if (show) View.VISIBLE else View.GONE
            progressView!!.animate().setDuration(shortAnimTime.toLong()).alpha(
                    (if (show) 1 else 0).toFloat()).setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    progressView!!.visibility = if (show) View.VISIBLE else View.GONE
                }
            })
        } else {
            progressView!!.visibility = if (show) View.VISIBLE else View.GONE
        }
    }
}

