package se.umu.dv21jen.picdaily

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SplashScreenActivity : AppCompatActivity() {
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        firebaseAuth = Firebase.auth

        if(firebaseAuth.currentUser != null) {
            var intent = Intent(application, MainActivity::class.java)
            intent.putExtra("WELCOME_MESSAGE", true)
            startActivity(intent)
            finish()
        } else {
            var intent = Intent(application, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}