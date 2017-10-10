package namanuma.com.firebasechat.service

import android.app.Notification
import android.app.PendingIntent
import android.content.Intent
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import namanuma.com.firebasechat.R
import namanuma.com.firebasechat.view.activity.WebViewActivity

/**
 * Created by k.oinuma on 2017/09/29.
 */
class MyFirebaseMessagingService : FirebaseMessagingService() {
    override fun onMessageReceived(rm: RemoteMessage?) {
        super.onMessageReceived(rm)

        val builder: NotificationCompat.Builder = NotificationCompat.Builder(applicationContext).apply {
            setSmallIcon(R.mipmap.ic_launcher)
            setContentTitle(getString(R.string.app_name))
            setContentText(rm?.notification?.body)
            setDefaults(
                    Notification.DEFAULT_SOUND or Notification.DEFAULT_VIBRATE or Notification.DEFAULT_LIGHTS
            )
            setAutoCancel(true)
        }

        val url = rm?.data?.get("URL") ?: ""

        // Tap to generate intent
        val intent = Intent(this, WebViewActivity::class.java)
        intent.putExtra("URL", url)

        val pIntent: PendingIntent = PendingIntent.getActivity(applicationContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        builder.setContentIntent(pIntent)

        // Show notification
        val manager: NotificationManagerCompat = NotificationManagerCompat.from(applicationContext)
        manager.notify(0, builder.build())
    }
}