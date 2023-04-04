package se.umu.dv21jen.picdaily.models

data class User(
    var name: String? = null,
    var numCollections: Long? = null,
    var collections: MutableList<UserCollection> = mutableListOf(),
) : java.io.Serializable
