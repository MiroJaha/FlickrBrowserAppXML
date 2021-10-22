package com.example.flickrbrowserappxml

import org.simpleframework.xml.Attribute
import org.simpleframework.xml.Element
import org.simpleframework.xml.Root
import java.io.Serializable

@Root(name = "photo", strict = false)
class Photo @JvmOverloads constructor(
    @field:Attribute(name = "title")
    @param:Attribute(name = "title")
    var title: String? = null,

    @field:Attribute(name = "server")
    @param:Attribute(name = "server")
    var server: String? = null,

    @field:Attribute(name = "id")
    @param:Attribute(name = "id")
    var id : String? = null,

    @field:Attribute(name = "secret")
    @param:Attribute(name = "secret")
    var secret: String? = "null"

) : Serializable