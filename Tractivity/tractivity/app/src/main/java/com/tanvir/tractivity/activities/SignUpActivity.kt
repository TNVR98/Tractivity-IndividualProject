package com.tanvir.tractivity.activities

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.tanvir.tractivity.model.FireStoreClass
import com.tanvir.tractivity.R
import com.tanvir.tractivity.model.UserClass
import kotlinx.android.synthetic.main.activity_sign_up.*

@Suppress("DEPRECATION")
/**
 * The sign Screen of the application was implemented here
 * with Gui designed in activity_sign_up.xml file
 */
class SignUpActivity : BaseActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        auth = Firebase.auth

        bt_SignUp.setOnClickListener {
            createUser()
        }

        //Intend to login page
        tv_login.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
            }
    }

    // user is created and stored on authentication server and firestore
    private fun createUser(){
        val name: String = et_name.text.toString().trim{ it <= ' '}
        val email: String = et_email.text.toString().trim{ it <= ' '}
        val password : String = et_password.text.toString()
        val reTypedPassword : String = et_reTypePassword.text.toString()

        if(validateSignUp(name,email,password,reTypedPassword)){
            auth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(this){
                task ->
                if (task.isSuccessful) {
                    val user = UserClass(Firebase.auth.currentUser!!.uid,name,email)

                    FireStoreClass().registerUserOnDB(user)
                    Toast.makeText(this,
                        "Welcome $name to Tractivity",Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, TractivityMain::class.java)
                    startActivity(intent)

                    finish()
                }else {
                    Toast.makeText(this,task.exception!!.message.toString(),Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    //function to validate the sighUp form
    private fun validateSignUp (name:String , email: String ,
                                password: String, reTypedPassword : String) :Boolean{
        when {
            TextUtils.isEmpty(name) ->{
                showError("Please enter your name")
                return false
            }
            TextUtils.isEmpty(email) ->{
                showError("Please enter your email address")
                return false
            }
            TextUtils.isEmpty(password) ->{
                showError("Please enter a password")
                return false
            }
            TextUtils.isEmpty(reTypedPassword) ->{
                showError("Please re-Type your password")
                return false
            }
            !isPasswordMatched(password,reTypedPassword) ->{
                return false
            }
            else ->{
                return true

            }
        }
    }

    //checks if both the passwords matches
    private fun isPasswordMatched (password:String, reTypedPassword:String) :Boolean{
        return if(password == reTypedPassword){
            true
        }else{
            showError("Passwords do not match, please retype")
            false
        }
    }
}