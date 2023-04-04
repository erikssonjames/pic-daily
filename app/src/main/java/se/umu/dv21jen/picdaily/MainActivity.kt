package se.umu.dv21jen.picdaily

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup.LayoutParams
import android.view.ViewGroup.MarginLayoutParams
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import android.widget.Toast
import androidx.core.view.marginRight
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import se.umu.dv21jen.picdaily.components.CollectionListAdapter
import se.umu.dv21jen.picdaily.models.User

class MainActivity : AppCompatActivity() {
    private var firebaseAuth: FirebaseAuth = Firebase.auth
    private var firebaseFirestore: FirebaseFirestore = Firebase.firestore
    private lateinit var collectionListAdapter: CollectionListAdapter
    private lateinit var addCollectionButton: Button
    private var user: User?

    init {
        val userId = firebaseAuth.currentUser?.uid
        user = User(userId)
        initUser()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        addCollectionButton = findViewById(R.id.add_collection_button)

        val displayWelcomeMessage = intent.getBooleanExtra("WELCOME_MESSAGE", false)
        if(displayWelcomeMessage) welcomeToast()

        addCollectionButton.setOnClickListener {
            val intent = Intent(this, AddCollectionActivity::class.java)
            startActivity(intent)
        }

//        populateFirebaseStore()
    }

    private fun initUser() {
        val userID = firebaseAuth.currentUser?.uid
        val userRef = firebaseFirestore.collection("users").document(userID.toString())

        userRef.get()
            .addOnCompleteListener {
                if(it.isSuccessful){
                    val document = it.result
                    if(document != null){
                        user = document.toObject(User::class.java)
                        populateCollectionListView()
                    }
                }
            }.addOnFailureListener {
                Log.d(TAG, "FAiled!!!")
            }

    }

    private fun welcomeToast() {
        val username = firebaseAuth.currentUser?.displayName

        var message = "Welcome back"
        if(username != null && username.isNotEmpty()) message += " $username"
        message += "!"

        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun populateCollectionListView(){
        collectionListAdapter = CollectionListAdapter(user!!.collections, this)

        val listView = findViewById<ListView>(R.id.collections_list_view)
        listView.adapter = collectionListAdapter
    }

    companion object {
        private const val TAG = "MAIN_ACTIVITY"
    }

    private fun populateFirebaseStore() {
        val store = Firebase.firestore
        val userId = firebaseAuth.currentUser?.uid

        val image1 = hashMapOf(
            "id" to 0,
            "added" to "2023-03-25",
            "url" to "https://firebasestorage.googleapis.com/v0/b/picdaily.appspot.com/o/bonk_police.jpg?alt=media&token=f1e2505a-3715-448d-885a-71de6cabebbb"
        )

        val image2 = hashMapOf(
            "id" to 1,
            "added" to "2023-03-26",
            "url" to "https://firebasestorage.googleapis.com/v0/b/picdaily.appspot.com/o/bonk_police.jpg?alt=media&token=f1e2505a-3715-448d-885a-71de6cabebbb"
        )

        val image3 = hashMapOf(
            "id" to 2,
            "added" to "2023-03-27",
            "url" to "https://firebasestorage.googleapis.com/v0/b/picdaily.appspot.com/o/bonk_police.jpg?alt=media&token=f1e2505a-3715-448d-885a-71de6cabebbb"
        )

        val image4 = hashMapOf(
            "id" to 3,
            "added" to "2023-03-28",
            "url" to "https://firebasestorage.googleapis.com/v0/b/picdaily.appspot.com/o/bonk_police.jpg?alt=media&token=f1e2505a-3715-448d-885a-71de6cabebbb"
        )

        val collection1 = hashMapOf(
            "id" to 0,
            "ended" to "9999-12-31",
            "goal" to 365,
            "lastPictureTaken" to "2023-04-01",
            "purpose" to "Measure gym progress",
            "started" to "2023-03-01",
            "numImages" to 4,
            "images" to listOf(image1, image2, image3, image4)
        )

        val collection2 = hashMapOf(
            "id" to 1,
            "ended" to "9999-12-31",
            "goal" to 520,
            "lastPictureTaken" to "2023-03-31",
            "purpose" to "Log haircut progress",
            "started" to "2022-02-12",
            "numImages" to 0,
            "images" to listOf<String>()
        )

        val createdUser = hashMapOf(
            "name" to "James",
            "numberCollections" to 2,
            "collections" to listOf(collection1, collection2),
        )

        store.collection("users").document(userId.toString())
            .set(createdUser)
            .addOnSuccessListener {
                Log.d(TAG, "Successfully added the createdUser")
            }.addOnFailureListener {
                Log.d(TAG, "Something went wrong adding the user! Exception = ${it.message}")
            }
    }
}