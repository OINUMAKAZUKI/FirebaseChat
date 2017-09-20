package namanuma.com.firebasechat.extension

import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by kazuki on 2017/09/20.
 */
val Long.time: String
    get() {
        val time = this
        if (time == 0L) {
            return ""
        }
        val df = SimpleDateFormat("HH:mm", Locale.US)
        df.timeZone = TimeZone.getTimeZone("Asia/Tokyo")
        return df.format(Date(time))
    }