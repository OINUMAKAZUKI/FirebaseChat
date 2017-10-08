package namanuma.com.firebasechat.view.adapter

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.net.Uri
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import namanuma.com.firebasechat.R
import namanuma.com.firebasechat.extension.time
import namanuma.com.firebasechat.model.Chat

/**
 * Created by kazuki on 2017/09/18.
 */
class ChatAdapter constructor(private var context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var userId: String = ""
    var chats: List<Chat>? = null
    var adminUser: String = ""
    var colorString: String = ""
    var adminColorString: String = ""
    var config: FirebaseRemoteConfig = FirebaseRemoteConfig.getInstance()

    enum class ViewType {
        MY, YOU
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder =
            when(viewType) {
                ViewType.MY.ordinal -> ViewHolder(LayoutInflater.from(context).inflate(R.layout.list_item_chat_myself, parent, false))
                else -> ViewHolder(LayoutInflater.from(context).inflate(R.layout.list_item_chat_yourself, parent, false))
            }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        if (holder is ViewHolder) {
            holder.setup(chats?.get(position))
        }
    }

    override fun getItemViewType(position: Int): Int {
        chats?.let {
            return when(it[position].userId) {
                userId -> ViewType.MY.ordinal
                else -> ViewType.YOU.ordinal
            }
        }

        return super.getItemViewType(position)
    }

    override fun getItemCount(): Int = chats?.size ?: 0

    inner class ViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {

        var cardView: CardView = itemView?.findViewById(R.id.cardView) as CardView
        var chatText: TextView = itemView?.findViewById(R.id.chatText) as TextView
        var nameText: TextView = itemView?.findViewById(R.id.nameText) as TextView
        var timeText: TextView = itemView?.findViewById(R.id.timeText) as TextView
        var imageView: ImageView = itemView?.findViewById(R.id.imageView) as ImageView

        fun setup(chat: Chat?) {
            remoteConfig()

            chat?.let {
                if (it.userId == adminUser) {
                    val color = Color.parseColor(adminColorString)
                    cardView.cardBackgroundColor = ColorStateList.valueOf(color)
                }

                nameText.text = it.name
                timeText.text = it.ctime.time
                chatText.text = it.message

                if (it.imagePath.isNotEmpty()) {
                    imageView.visibility = View.VISIBLE
                    val uri = Uri.parse(it.imagePath)
                    Glide.with(context).load(uri).into(imageView)
                } else {
                    imageView.visibility = View.GONE
                }
            }
        }
    }

    private fun remoteConfig() {
        adminUser = config.getString("admin_user")
        adminColorString = config.getString("admin_message_color")
        colorString = config.getString("message_color")

        adminUser = if (adminUser.isEmpty()) "xmuLhfqtq6Sfn6bcJWZiGTkT4oI3" else adminUser
        adminColorString = if (adminColorString.isEmpty()) "#00ff00" else adminColorString
    }
}
