package com.example.flickrbrowserappxml

import org.simpleframework.xml.ElementList
import org.simpleframework.xml.Path
import org.simpleframework.xml.Root
import java.io.Serializable

@Root(name = "rsp", strict = false)
class Rsp: Serializable {

    @field:ElementList(inline = true,name = "photo")
    @field:Path("photos")
    var photo: List<Photo>? = arrayListOf()

}