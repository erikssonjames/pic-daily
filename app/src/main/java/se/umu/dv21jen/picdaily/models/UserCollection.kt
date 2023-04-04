package se.umu.dv21jen.picdaily.models

data class UserCollection(
    var id: Long? = null,
    var goal: Long? = null,
    var started: String? = null,
    var ended: String? = null,
    var purpose: String? = null,
    var lastPictureTaken: String? = null,
    var numImages: Long? = null,
    var images: MutableList<Image> = mutableListOf()
) : java.io.Serializable
