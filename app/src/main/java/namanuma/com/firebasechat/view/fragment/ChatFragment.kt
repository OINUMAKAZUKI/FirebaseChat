package namanuma.com.firebasechat.view.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Process
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import com.google.firebase.database.*
import namanuma.com.firebasechat.R
import namanuma.com.firebasechat.model.Chat
import namanuma.com.firebasechat.model.User
import namanuma.com.firebasechat.view.adapter.ChatAdapter
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.FirebaseStorage
import java.io.File
import java.net.Authenticator
import java.net.URI


/**
 * Created by kazuki on 2017/09/18.
 */
class ChatFragment : Fragment() {

    private val RESULT_PICK_IMAGEFILE = 1000
    private var recyclerView: RecyclerView? = null
    private var editText: EditText? = null
    private var sendText: ImageView? = null
    private var sendImage: ImageView? = null
    private var adapter: ChatAdapter? = null
    private var database: DatabaseReference? = null
    private var storage: FirebaseStorage? = null
    private var auth: FirebaseAuth? = null
    private var chats = mutableListOf<Chat>()
    private var userId = ""

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater?.inflate(R.layout.fragment_chat, container, false)

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // view の初期化
        view?.let {
            recyclerView = it.findViewById(R.id.recyclerView) as RecyclerView
            editText = it.findViewById(R.id.sendEditText) as EditText
            sendText = it.findViewById(R.id.sendTextImage) as ImageView
            sendImage = it.findViewById(R.id.sendImageImage) as ImageView
        }

        // RealTimeDatabase 初期化
        database = FirebaseDatabase.getInstance().reference
        database?.child("chats")?.addChildEventListener(childEventListener())

        // Storage 初期化
        storage = FirebaseStorage.getInstance()

        // Auth 初期化
        auth = FirebaseAuth.getInstance()
        userId = auth?.currentUser?.uid ?: "none"

        adapter = ChatAdapter(activity)
        adapter?.userId = userId
        recyclerView?.let {
            it.adapter = adapter
            it.layoutManager = LinearLayoutManager(activity)
            it.setOnTouchListener { _, _ ->
                if (it.hasFocus()) {
                    false
                } else {
                    (activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
                                .hideSoftInputFromWindow(it.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
                    it.requestFocus()
                    true
                }
            }
        }

        // keyboard 位置調整
        recyclerView?.addOnLayoutChangeListener { _, _, _, _, bottom, _, _, _, oldBottom ->
            if (bottom < oldBottom) {
                recyclerView?.postDelayed({
                    val position = adapter?.chats?.size ?: 0
                    (recyclerView?.layoutManager as LinearLayoutManager).scrollToPositionWithOffset(position - 1, 0)
                }, 100)
            }
        }

        // 送信
        sendText?.setOnClickListener {
            editText?.let {
                if (it.text.isNotEmpty()) {
                    database?.child("users")?.child(userId)?.addListenerForSingleValueEvent(postListener(it.text.toString()))
                }
            }
        }

        // 画像送信
        sendImage?.setOnClickListener {
            openImageFile()
        }
    }

    override fun onResume() {
        super.onResume()
        // ログインした後に、UserId を再取得
        auth?.let {
            it.currentUser?.let {
                userId = it.uid
                adapter?.let {
                    it.userId = userId
                }
                adapter?.notifyDataSetChanged()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RESULT_PICK_IMAGEFILE && resultCode == Activity.RESULT_OK) {
            val uri = data?.data
            database?.child("users")?.child(userId)?.addListenerForSingleValueEvent(postListener(uri))
        }
    }

    private fun writeNewPost(user: User, message: String) {
        val key = database?.child("chats")?.push()?.key ?: ""
        val chat = Chat(key, user.name, user.id, System.currentTimeMillis(), message)
        val postValues = chat.toMap()
        val childUpdates = HashMap<String, Any>()
        val userId = chat.userId
        childUpdates.put("/chats/$key", postValues)
        childUpdates.put("/user-chats/$userId/$key", postValues)

        database?.updateChildren(childUpdates)
        editText?.text?.clear()
    }

    private fun uploadImage(uri: Uri?, user: User?) {
        val key = database?.child("chats")?.push()?.key ?: ""

        uri?.let {
            val pathName = it.lastPathSegment
            val storageRef = storage?.reference
            val mountainsRef = storageRef?.child("chats/$key/$pathName")
            val uploadTask = mountainsRef?.putFile(it)
            uploadTask?.addOnFailureListener {
                it.printStackTrace()
            }?.addOnSuccessListener {
                val imagePath: String = it.downloadUrl?.toString() ?: ""
                user?.let {
                    val chat = Chat(key, user.name, user.id, System.currentTimeMillis(), "", imagePath)
                    val childUpdates = HashMap<String, Any>()
                    val postValues = chat.toMap()
                    childUpdates.put("/chats/$key", postValues)
                    childUpdates.put("/user-chats/$userId/$key", postValues)

                    database?.updateChildren(childUpdates)
                }
            }
        }
    }

    private fun postListener(message: String): ValueEventListener {
        return object: ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {
            }

            override fun onDataChange(p0: DataSnapshot?) {
                val user: User? = p0?.getValue(User::class.java)
                user?.let {
                    writeNewPost(it, message)
                }
            }
        }
    }

    private fun postListener(uri: Uri?): ValueEventListener {
        return object: ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {
            }

            override fun onDataChange(p0: DataSnapshot?) {
                val user: User? = p0?.getValue(User::class.java)
                user?.let {
                    uploadImage(uri, it)
                }
            }
        }
    }

    private fun openImageFile() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "image/*"
        startActivityForResult(intent, RESULT_PICK_IMAGEFILE)
    }

    private fun singleValueEventListener(): ValueEventListener {
        return object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {
            }

            override fun onDataChange(p0: DataSnapshot?) {
                val chat = p0?.getValue(Chat::class.java)
                chat?.let {
                    chats.add(it)
                }
                adapter?.chats = chats
                adapter?.notifyDataSetChanged()
            }

        }
    }

    private fun childEventListener(): ChildEventListener {
        return object: ChildEventListener {
            override fun onCancelled(p0: DatabaseError?) {
            }

            override fun onChildMoved(p0: DataSnapshot?, p1: String?) {
            }

            override fun onChildChanged(p0: DataSnapshot?, p1: String?) {
                val chat = p0?.getValue(Chat::class.java)
                val id = chat?.id ?: 0

                chats.findLast { it.id == id }?.change(chat)
                adapter?.chats = chats
                adapter?.notifyDataSetChanged()
            }

            override fun onChildAdded(p0: DataSnapshot?, p1: String?) {
                val chat = p0?.getValue(Chat::class.java)
                chat?.let {
                    chats.add(it)
                }
                adapter?.chats = chats
                adapter?.notifyDataSetChanged()

                (recyclerView?.layoutManager as LinearLayoutManager).scrollToPosition(chats.size - 1)
            }

            override fun onChildRemoved(p0: DataSnapshot?) {
                val chat = p0?.getValue(Chat::class.java)
                val id = chat?.id ?: 0
                val index = chats.count { it.id == id }
                index.let {
                    chats.removeAt(it)
                }
                adapter?.chats = chats
                adapter?.notifyDataSetChanged()
            }

        }
    }
}