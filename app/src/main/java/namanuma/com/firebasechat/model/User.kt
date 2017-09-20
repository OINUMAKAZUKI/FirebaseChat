package namanuma.com.firebasechat.model

/**
 * Created by kazuki on 2017/09/18.
 */
class User() {

    constructor(id: Int, name: String) : this() {
        this.id = id
        this.name = name
    }

    var id: Int = 0
    var name: String = ""
}
