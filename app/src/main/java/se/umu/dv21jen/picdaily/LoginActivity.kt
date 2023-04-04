package se.umu.dv21jen.picdaily

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.Toast
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var loginButton: Button
    private lateinit var emailInput: TextInputLayout
    private lateinit var passwordInput: TextInputLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        firebaseAuth = Firebase.auth
        loginButton = findViewById(R.id.login_button)
        emailInput = findViewById(R.id.login_email_input)
        passwordInput = findViewById(R.id.login_password_input)

        loginButton.setOnClickListener {
            val email = (emailInput.editText?.text).toString()
            val password = (passwordInput.editText?.text).toString()

            Log.i(TAG, "Email = $email -- Password = $password")
            firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "signInWithEmail:success")
                        val intent = Intent(application, MainActivity::class.java)
                        intent.putExtra("WELCOME_MESSAGE", true)
                        startActivity(intent)
                    } else {
                        val view = this.currentFocus
                        if (view != null) {
                            val inm: InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                            inm.hideSoftInputFromWindow(view.windowToken, 0)
                        }

                        Toast.makeText(this, "Login failed.", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    companion object {
        private const val TAG = "LOGINACTIVITY";
    }
}