package se.umu.dv21jen.picdaily

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import se.umu.dv21jen.picdaily.models.UserCollection
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class AddCollectionActivity : AppCompatActivity() {
    private lateinit var nameEditText: TextInputEditText
    private lateinit var daysEditText: TextInputEditText
    private lateinit var createCollectionButton: Button
    private lateinit var uid: String
    private lateinit var topAppBar: MaterialToolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_collection)

        uid = Firebase.auth.uid.toString()

        nameEditText = findViewById(R.id.name_edit_text)
        daysEditText = findViewById(R.id.days_edit_text)
        createCollectionButton = findViewById(R.id.create_collection_button)

        createCollectionButton.setOnClickListener {
            val goal = Integer.parseInt(daysEditText.text.toString()).toLong()
            val name = nameEditText.text.toString()
            val currentDate = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                .format(LocalDateTime.now())



            val db = Firebase.firestore
            val userRef = db.collection("users").document(uid)

            db.runTransaction { transaction ->
                val snapshot = transaction.get(userRef)

                val numCollections = snapshot.getLong("numCollections")!!
                val newCollection = UserCollection(
                    id = numCollections,
                    goal = goal,
                    started = currentDate,
                    ended = "9999-12-31",
                    purpose = name,
                    lastPictureTaken = "2000-01-01",
                    numImages = 0L,
                )

                transaction.update(userRef, "numCollections", numCollections.plus(1))
                transaction.update(userRef, "collections", FieldValue.arrayUnion(newCollection))
            }.addOnCompleteListener { task ->
                if(task.isSuccessful){
                    Toast.makeText(this, "Successfully added a new collection.", Toast.LENGTH_SHORT).show()

                } else {
                    Toast.makeText(this, "Failed adding a new collection.", Toast.LENGTH_SHORT).show()

                }
                val intent = Intent(this, MainActivity::class.java)
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