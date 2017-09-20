package namanuma.com.firebasechat.view.adapter

import android.content.Context
import android.net.Uri
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.firebase.ui.storage.images.FirebaseImageLoader
import com.google.firebase.storage.FirebaseStorage
import namanuma.com.firebasechat.R
import namanuma.com.firebasechat.extension.time
import namanuma.com.firebasechat.model.Chat

/**
 * Created by kazuki on 2017/09/18.
 */
class ChatAdapter constructor(private var context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var userId: String = ""
    var chats: List<Chat>? = null
    var storage: FirebaseStorage? = null

    init {
        storage = FirebaseStorage.getInstance()
    }

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

        var chatText: TextView = itemView?.findViewById(R.id.chatText) as TextView
        var nameText: TextView = itemView?.findViewById(R.id.nameText) as TextView
        var timeText: TextView = itemView?.findViewById(R.id.timeText) as TextView
        var imageView: ImageView = itemView?.findViewById(R.id.imageView) as ImageView
        fun setup(chat: Chat?) {
            chat?.let {
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
}
