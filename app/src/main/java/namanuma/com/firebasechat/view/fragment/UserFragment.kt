package namanuma.com.firebasechat.view.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import com.google.firebase.database.*
import namanuma.com.firebasechat.R
import namanuma.com.firebasechat.model.Chat
import namanuma.com.firebasechat.model.User
import namanuma.com.firebasechat.view.adapter.ChatAdapter
import java.sql.BatchUpdateException
import android.os.Process.myUserHandle
import android.os.UserHandle
import android.content.Context.USER_SERVICE
import android.os.Process
import android.os.UserManager
import android.text.Editable


/**
 * Created by kazuki on 2017/09/18.
 */
class UserFragment : Fragment() {

    private var editText: EditText? = null
    private var button: Button? = null
    private var database: DatabaseReference? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater?.inflate(R.layout.fragment_user, container, false)

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view?.let {
            editText = it.findViewById(R.id.nameEditText) as EditText
            button = it.findViewById(R.id.button) as Button
        }

        database = FirebaseDatabase.getInstance().reference

        val userId = Process.myUid()
        database?.child("users")?.child(userId.toString())?.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {
            }

            override fun onDataChange(p0: DataSnapshot?) {
                val user = p0?.getValue(User::class.java)
                user?.let {
                    editText?.setText(it.name)
                }
            }

        })
        button?.setOnClickListener {
            editText?.let {
                writeNewUser(userId.toString(), it.text.toString())
            }
        }

    }

    private fun writeNewUser(id: String, name: String) {
        val user = User(id, name)
        database?.child("users")?.child(id)?.setValue(user)
    }
}