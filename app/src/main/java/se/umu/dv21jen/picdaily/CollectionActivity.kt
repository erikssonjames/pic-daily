package se.umu.dv21jen.picdaily

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import se.umu.dv21jen.picdaily.models.Image
import se.umu.dv21jen.picdaily.models.NewCollection
import se.umu.dv21jen.picdaily.models.UserCollection
import se.umu.dv21jen.picdaily.utils.TimeObj
import java.io.Serializable
import java.net.URL
import java.util.concurrent.Executors

class CollectionActivity : AppCompatActivity() {
    private lateinit var collection: UserCollection
    private var currentImageIndex: Int = 0
    private lateinit var imageView: ImageView
    private var currentImageState: ImageStates = ImageStates.NO_IMAGES
    private var firebaseAuth: FirebaseAuth = Firebase.auth
    private lateinit var collectionTimeObj: TimeObj
    private lateinit var topAppBar: MaterialToolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_collection)

        imageView = findViewById(R.id.collection_current_image_shown)
        topAppBar = findViewById(R.id.app_bar)

        collection = getSerializable(this, "COLLECTION", UserCollection::class.java)
        collectionTimeObj = TimeObj(collection.lastPictureTaken, "GMT")

        setUpInformation()

        if(collection.numImages != null && collection.numImages!!.toInt() > 0){
            currentImageIndex = collection.numImages!!.toInt() - 1
            setCurrentImage(collection.images[currentImageIndex])
        }

        setUpButtons()
    }

    @SuppressLint("SetTextI18n")
    private fun setUpInformation() {
        val purposeTest = findViewById<TextView>(R.id.collection_purpose_title)
        val goalText = findViewById<TextView>(R.id.collection_goal_text)
        val startedText = findViewById<TextView>(R.id.collection_started_text)
        val pictureTakenTodayText = findViewById<TextView>(R.id.picture_taken_today_text)
        val pictureTakenTodaySubText = findViewById<TextView>(R.id.picture_taken_today_subtext)

        purposeTest.text = collection.purpose
        goalText.text = "${collection.numImages}/${collection.goal}"
        startedText.text = "Started: ${collection.started}"

        setCurrentImageShownText()

        if(collectionTimeObj.isWithinToday()){
            pictureTakenTodayText.text = resources.getText(R.string.picture_taken_today)
            pictureTakenTodaySubText.text = resources.getText(R.string.retake_picture)
        } else {
            pictureTakenTodayText.text = resources.getText(R.string.no_picture_today)
            pictureTakenTodaySubText.text = resources.getText(R.string.take_picture)
        }
    }

    private fun setCurrentImageShownText(){
        val text = when(currentImageState){
            ImageStates.LOADING -> {
                "Loading image"
            }
            ImageStates.HAS_IMAGES -> {
                "Images (${currentImageIndex + 1}/${collection.numImages})"
            }
            ImageStates.NO_IMAGES -> {
                "No images"
            }
        }

        findViewById<TextView>(R.id.current_num_image_shown).text = text
    }

    private fun setUpButtons() {
        val leftArrowButton = findViewById<ImageView>(R.id.collection_left_image_button)
        val rightArrowButton = findViewById<ImageView>(R.id.collection_right_image_button)
        val viewImageGalleryButton = findViewById<Button>(R.id.collection_view_image_gallery)
        val takePictureButton = findViewById<FloatingActionButton>(R.id.take_picture_button)

        leftArrowButton.setOnClickListener {
            val image = getLeftImage()
            setCurrentImage(image)
        }

        rightArrowButton.setOnClickListener {
            val image = getRightImage()
            setCurrentImage(image)
        }

        viewImageGalleryButton.setOnClickListener {
            Log.d(TAG, "View gallery")
        }

        takePictureButton.setOnClickListener {
            val intent = Intent(this, CameraActivity::class.java)

            val newCollection = NewCollection(
                retakePicture = collectionTimeObj.isWithinToday(),
                collectionId = collection.id,
                userId = firebaseAuth.uid,
                null,
                numPictures = collection.numImages,
                null
            )

            intent.putExtra("NEW_COLLECTION", newCollection)

            startActivity(intent)
        }

        topAppBar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun getLeftImage(): Image {
        if(currentImageIndex == 0) {
            currentImageIndex = (collection.numImages?.toInt() ?: 0) - 1
        } else {
            currentImageIndex -= 1
        }

        return collection.images[currentImageIndex]
    }

    private fun getRightImage(): Image {
        if(currentImageIndex + 1 < (collection.numImages?.toInt() ?: 0)) {
            currentImageIndex += 1
        } else {
            currentImageIndex = 0
        }

        return collection.images[currentImageIndex]
    }

    private fun setCurrentImage(image: Image?) {
        if(image == null){
            Log.d(TAG, "No image available")
        } else {
            val executor = Executors.newSingleThreadExecutor()
            val handler = Handler(Looper.getMainLooper())
            toggleLoading()
            executor.execute {
                val url = URL(image.url)
                val bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream())
                handler.post {
                    imageView.setImageBitmap(bmp)
                    currentImageState = ImageStates.HAS_IMAGES
                    setCurrentImageShownText()
                    toggleLoading()
                }
            }
        }
    }

    private fun toggleLoading(){
        currentImageState = when(currentImageState){
            ImageStates.LOADING -> ImageStates.HAS_IMAGES
            ImageStates.HAS_IMAGES -> ImageStates.LOADING
            else -> ImageStates.NO_IMAGES
        }

        val progressBar = findViewById<ProgressBar>(R.id.image_loading_progress_bar)
        val isCurrentlyLoading = progressBar.isIndeterminate
        progressBar.isIndeterminate = !isCurrentlyLoading
    }

    private fun <T : Serializable?> getSerializable(activity: Activity, name: String, clazz: Class<T>): T
    {
        return if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            activity.intent.getSerializableExtra(name, clazz)!!
        else
            activity.intent.getSerializableExtra(name) as T
    }

    companion object {
        private const val TAG = "CollectionActivity"
    }

    enum class ImageStates {
        LOADING,
        NO_IMAGES,
        HAS_IMAGES
    }
}