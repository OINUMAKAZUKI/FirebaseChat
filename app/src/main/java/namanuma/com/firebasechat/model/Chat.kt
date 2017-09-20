package namanuma.com.firebasechat.model
import java.util.*


/**
 * Created by kazuki on 2017/09/18.
 */
class Chat() {

    constructor(id: String, name: String, userId: String, ctime: Long, message: String, imagePath: String) : this() {
        this.id = id
        this.name = name
        this.userId = userId
        this.ctime = ctime
        this.message = message
        this.imagePath = imagePath
    }

    constructor(id: String, name: String, userId: String, ctime: Long, message: String) : this(id = id, name = name, userId = userId, ctime = ctime, message = message, imagePath = "")

    var id: String = ""
    var name: String = ""
    var userId: String = ""
    var ctime: Long = 0L
    var message: String = ""
    var imagePath: String = ""

    fun change(char: Chat?) {
        char?.let {
            this.name = char.name
            this.message = char.message
            this.imagePath = char.imagePath
        }
    }

    fun toMap(): Map<String, Any> {
        val result = HashMap<String, Any>()
        result.put("id", id)
        result.put("userId", userId)
        result.put("name", name)
        result.put("ctime", ctime)
        result.put("message", message)
        result.put("imagePath", imagePath)
        return result
    }
}