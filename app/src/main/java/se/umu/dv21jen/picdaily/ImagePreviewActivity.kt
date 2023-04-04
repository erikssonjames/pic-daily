package se.umu.dv21jen.picdaily

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.ViewTreeObserver
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.internal.ViewUtils
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import se.umu.dv21jen.picdaily.models.Image
import se.umu.dv21jen.picdaily.models.NewCollection
import se.umu.dv21jen.picdaily.models.User
import se.umu.dv21jen.picdaily.models.UserCollection
import java.io.ByteArrayInputStream
import java.io.IOException
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.reflect.typeOf
import kotlin.streams.toList

class ImagePreviewActivity : AppCompatActivity() {
    private val firebaseStorage: FirebaseStorage = Firebase.storage
    private val firebaseFirestore: FirebaseFirestore = Firebase.firestore
    private lateinit var imageUrl: String
    private lateinit var newCollection: NewCollection
    private lateinit var saveButton: Button
    private lateinit var retakeButton: Button
    private lateinit var imagePreview: ImageView
    private lateinit var topAppBar: MaterialToolbar

    companion object {
        private const val TAG = "ImagePreview"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_preview)

        newCollection = intent.extras?.getSerializable("NEW_COLLECTION") as NewCollection

        saveButton = findViewById(R.id.save_button)
        retakeButton = findViewById(R.id.retake_button)
        imagePreview = findViewById(R.id.image_preview)

        val imageRef = firebaseStorage.reference.child(newCollection.imageRef.toString())
        val ONE_MEGABYE: Long = 1024 * 1024
        imageRef.getBytes(ONE_MEGABYE).addOnSuccessListener {
            val bmp = BitmapFactory.decodeByteArray(it, 0, it.size)
            val scaled = Bitmap.createScaledBitmap(bmp, bmp.width, bmp.height, true)
            imagePreview.setImageBitmap(scaled)
        }.addOnFailureListener {
            Log.d(TAG, "Failed downloading")
        }

        imageRef.downloadUrl.addOnSuccessListener {
            imageUrl = it.path!!
        }

        saveButton.setOnClickListener {
            val userId = Firebase.auth.currentUser?.uid
            val userRef = firebaseFirestore.collection("users").document(userId.toString())

            firebaseFirestore.runTransaction { transaction ->
                val snapshot = transaction.get(userRef)

                val currentDate = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                    .format(LocalDateTime.now(ZoneId.of("GMT")))

                val user = snapshot.toObject(User::class.java)!!

                var collection: UserCollection? = null
                for (userCollection in user.collections){
                    if(userCollection.id == newCollection.collectionId) collection = userCollection
                }

                if(newCollection.retakePicture == true){
                    for(image in collection!!.images){
                        image.url = newCollection.downloadUrl
                        image.added = currentDate
                    }
                } else {
                    val image = Image(
                        id = newCollection.numPictures!!.toLong(),
                        url = newCollection.downloadUrl,
                        added = currentDate
                    )
                    collection!!.lastPictureTaken = currentDate
                    collection.numImages = collection.numImages!!.plus(1)
                    collection.images.add(image)
                }
                transaction.update(userRef, "collections", user.collections)

                collection
            }.addOnCompleteListener { task ->
                if(task.isSuccessful){
                    val text = when(newCollection.retakePicture){
                        true -> "Image successfully updated."
                        false -> "Image successfully added."
                        else -> ""
                    }

                    Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
                } else {
                    val text = when(newCollection.retakePicture){
                        true -> "Failed updating the image."
                        false -> "Failed adding the image."
                        else -> ""
                    }

                    Log.d(TAG, task.exception.toString())

                    Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
                }

                val intent = Intent(this, CollectionActivity::class.java)
                intent.putExtra("COLLECTION", task.result as UserCollection)
                startActivity(intent)
                this.finish()
            }
        }

        retakeButton.setOnClickListener {
            imageRef.delete()
                .addOnSuccessListener {
                    Log.d(TAG, "Successfully removed the image")
                }.addOnFailureListener {
                    Log.d(TAG, "Failed removing the image")
                }
                .addOnCompleteListener {
                    val intent = Intent(this, CameraActivity::class.java)
                    intent.putExtra("NEW_COLLECTION", newCollection)
                    startActivity(intent)
                    this.finish()
                }
        }

        topAppBar = findViewById(R.id.app_bar)

        topAppBar.setNavigationOnClickListener {
            finish()
        }
    }
}