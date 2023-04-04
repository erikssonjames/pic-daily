package se.umu.dv21jen.picdaily.models

data class NewCollection (
    val retakePicture: Boolean?,
    val collectionId: Long?,
    val userId: String?,
    var imageRef: String?,
    val numPictures: Number?,
    var downloadUrl: String?,
) : java.io.Serializable
