package se.umu.dv21jen.picdaily.models

data class Image(
    var id: Long? = null,
    var added: String? = null,
    var url: String? = null,
) : java.io.Serializable
