package com.example.myhockey

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.OpenableColumns
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONObject
import java.io.IOException

class user_signup : Fragment() {

    private var selectedFileUri: Uri? = null
    private lateinit var dbRef: DatabaseReference
    private lateinit var auth: FirebaseAuth

    companion object {
        const val FILE_PICKER_REQUEST_CODE = 1001
        const val ADMIN_EMAIL = "admin@example.com"
        const val ADMIN_PASSWORD = "adminpass"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_user_auth, container, false)

        // Initialize Firebase
        dbRef = FirebaseDatabase.getInstance().getReference("users")
        auth = FirebaseAuth.getInstance()

        // Create default admin in Firebase Authentication and Database
        createDefaultAdmin()

        // Forms and toggle
        val signUpForm = view.findViewById<LinearLayout>(R.id.sign_up)
        val signInForm = view.findViewById<LinearLayout>(R.id.sign_in_form)
        val toggleToSignin = view.findViewById<TextView>(R.id.textView7)
        val toggleToSignup = view.findViewById<TextView>(R.id.toggleToSignup)
        val signupSkipButton = view.findViewById<TextView>(R.id.skipTextView1)
        val loginSkipButton = view.findViewById<TextView>(R.id.skipTextView2)

        // Sign-in fields
        val loginBtn = view.findViewById<TextView>(R.id.btnLogin)
        val emailSignIn = view.findViewById<TextInputEditText>(R.id.TextEmail)
        val passwordSignIn = view.findViewById<TextInputEditText>(R.id.login_password)
        val emailInputLayout = view.findViewById<TextInputLayout>(R.id.emailLayout)
        val passwordInputLayout = view.findViewById<TextInputLayout>(R.id.passwordLayout)
        val forgotPassword = view.findViewById<TextView>(R.id.forgotPasswordText)

        // Sign-up fields
        val firstNameInput = view.findViewById<TextInputEditText>(R.id.editTextFirstName)
        val lastNameInput = view.findViewById<TextInputEditText>(R.id.editTextLastName)
        val emailSignUpInput = view.findViewById<TextInputEditText>(R.id.editTextEmail)
        val idNumberInput = view.findViewById<TextInputEditText>(R.id.editTextNumber)
        val phoneInput = view.findViewById<TextInputEditText>(R.id.editTextPhone)
        val passwordSignUpInput = view.findViewById<TextInputEditText>(R.id.editTextPassword)
        val rePasswordInput = view.findViewById<TextInputEditText>(R.id.editTextRePassword)
        val radioGroupGender = view.findViewById<RadioGroup>(R.id.radioGroupGender)
        val signUpButton = view.findViewById<Button>(R.id.button2)
        val buttonSelectFile = view.findViewById<Button>(R.id.buttonSelectFile)
        val textViewFileName = view.findViewById<TextView>(R.id.textViewFileName)

        val slideUp = AnimationUtils.loadAnimation(context, R.anim.slide_up)
        val slideDown = AnimationUtils.loadAnimation(context, R.anim.slide_down)

        // Toggle sign-up/sign-in forms
        toggleToSignin.setOnClickListener {
            signUpForm.startAnimation(slideDown)
            signUpForm.visibility = View.GONE
            signInForm.startAnimation(slideUp)
            signInForm.visibility = View.VISIBLE
        }
        toggleToSignup.setOnClickListener {
            signInForm.startAnimation(slideDown)
            signInForm.visibility = View.GONE
            signUpForm.startAnimation(slideUp)
            signUpForm.visibility = View.VISIBLE
        }

        // Skip buttons
        signupSkipButton.setOnClickListener { goToMainActivity() }
        loginSkipButton.setOnClickListener { goToMainActivity() }

        // Forgot Password
        forgotPassword.setOnClickListener {
            val email = emailSignIn.text.toString().trim()
            if (email.isEmpty()) {
                emailInputLayout.error = "Please enter your email"
                return@setOnClickListener
            } else {
                emailInputLayout.error = null
            }
            auth.sendPasswordResetEmail(email)
                .addOnSuccessListener {
                    Toast.makeText(context, "Password reset email sent!", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(context, "Failed to send reset email: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }

        // Sign-in login button
        loginBtn.setOnClickListener {
            val emailInput = emailSignIn.text.toString().trim()
            val passwordInput = passwordSignIn.text.toString().trim()

            var isValid = true
            if (emailInput.isEmpty()) {
                emailInputLayout.error = "Email is required"
                isValid = false
            } else {
                emailInputLayout.error = null
            }
            if (passwordInput.isEmpty()) {
                passwordInputLayout.error = "Password is required"
                isValid = false
            } else {
                passwordInputLayout.error = null
            }
            if (!isValid) return@setOnClickListener

            auth.signInWithEmailAndPassword(emailInput, passwordInput)
                .addOnSuccessListener {
                    dbRef.child(auth.currentUser!!.uid).get().addOnSuccessListener { snapshot ->
                        val user = snapshot.getValue(User::class.java)
                        if (user != null) {
                            when (user.role) {
                                "admin" -> startActivity(Intent(requireActivity(), admin::class.java))
                                "coach" -> {
                                    if (user.approved == true) {
                                        startActivity(Intent(requireActivity(), coach::class.java))
                                    } else {
                                        AlertDialog.Builder(requireContext())
                                            .setTitle("Account Pending")
                                            .setMessage("Your account is pending admin approval.")
                                            .setPositiveButton("OK", null)
                                            .show()
                                    }
                                }
                                else -> Toast.makeText(context, "Unknown role", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Toast.makeText(context, "User data not found", Toast.LENGTH_SHORT).show()
                        }
                    }.addOnFailureListener { e ->
                        Toast.makeText(context, "Failed to fetch user: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(context, "Login failed: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }

        // File selection
        buttonSelectFile.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "application/pdf"
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            startActivityForResult(intent, FILE_PICKER_REQUEST_CODE)
        }

        // Sign-up button
        signUpButton.setOnClickListener {
            val firstName = firstNameInput.text.toString().trim()
            val lastName = lastNameInput.text.toString().trim()
            val email = emailSignUpInput.text.toString().trim()
            val idNumber = idNumberInput.text.toString().trim()
            val phone = phoneInput.text.toString().trim()
            val password = passwordSignUpInput.text.toString().trim()
            val rePassword = rePasswordInput.text.toString().trim()
            val gender = view.findViewById<RadioButton>(radioGroupGender.checkedRadioButtonId)?.text?.toString()

            var valid = true
            if (firstName.isEmpty()) { firstNameInput.error = "First name is required"; valid = false } else { firstNameInput.error = null }
            if (lastName.isEmpty()) { lastNameInput.error = "Last name is required"; valid = false } else { lastNameInput.error = null }
            if (email.isEmpty()) { emailSignUpInput.error = "Email is required"; valid = false } else { emailSignUpInput.error = null }
            if (idNumber.isEmpty()) { idNumberInput.error = "ID number is required"; valid = false } else { idNumberInput.error = null }
            if (phone.isEmpty()) { phoneInput.error = "Phone is required"; valid = false } else { phoneInput.error = null }
            if (password.isEmpty()) { passwordSignUpInput.error = "Password is required"; valid = false } else { passwordSignUpInput.error = null }
            if (rePassword.isEmpty()) { rePasswordInput.error = "Please re-type password"; valid = false } else { rePasswordInput.error = null }
            if (password != rePassword) { rePasswordInput.error = "Passwords do not match"; valid = false }
            if (radioGroupGender.checkedRadioButtonId == -1) { Toast.makeText(context, "Please select gender", Toast.LENGTH_SHORT).show(); valid = false }
            if (selectedFileUri == null) { Toast.makeText(context, "Please select a file", Toast.LENGTH_SHORT).show(); valid = false }
            if (!valid) return@setOnClickListener

            // Create user with Firebase Authentication
            auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener { authResult ->
                    // Upload file to Cloudinary
                    uploadToCloudinary(
                        requireContext(),
                        selectedFileUri!!,
                        onSuccess = { fileUrl ->
                            // Save user data to Realtime Database
                            val user = User(
                                id = authResult.user!!.uid,
                                firstName = firstName,
                                lastName = lastName,
                                email = email,
                                idNumber = idNumber,
                                phone = phone,
                                gender = gender ?: "",
                                role = "coach",
                                imageUrl = fileUrl,
                                approved = false
                            )
                            dbRef.child(authResult.user!!.uid).setValue(user)
                                .addOnSuccessListener {
                                    Toast.makeText(context, "Sign-up successful! Awaiting admin approval.", Toast.LENGTH_SHORT).show()
                                    clearFields(
                                        firstNameInput,
                                        lastNameInput,
                                        emailSignUpInput,
                                        idNumberInput,
                                        phoneInput,
                                        passwordSignUpInput,
                                        rePasswordInput,
                                        radioGroupGender,
                                        textViewFileName
                                    )
                                    toggleToSignin.performClick()
                                }
                                .addOnFailureListener { e ->
                                    Toast.makeText(context, "Failed to save user: ${e.message}", Toast.LENGTH_SHORT).show()
                                    authResult.user?.delete()
                                }
                        },
                        onError = { error ->
                            Toast.makeText(context, "File upload failed: $error", Toast.LENGTH_SHORT).show()
                            authResult.user?.delete()
                        }
                    )
                }
                .addOnFailureListener { e ->
                    Toast.makeText(context, "Sign-up failed: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }

        return view
    }

    private fun createDefaultAdmin() {
        auth.signInWithEmailAndPassword(ADMIN_EMAIL, ADMIN_PASSWORD)
            .addOnSuccessListener {
                // Admin exists, ensure database entry
                val uid = auth.currentUser!!.uid
                dbRef.child(uid).get().addOnSuccessListener { snapshot ->
                    if (!snapshot.exists()) {
                        val defaultAdmin = User(
                            id = uid,
                            firstName = "Admin",
                            lastName = "Default",
                            email = ADMIN_EMAIL,
                            idNumber = "",
                            phone = "",
                            gender = "",
                            role = "admin",
                            imageUrl = "",
                            approved = true
                        )
                        dbRef.child(uid).setValue(defaultAdmin)
                    }
                }
                auth.signOut() // Sign out to allow user login
            }
            .addOnFailureListener {
                // Admin doesn't exist, create it
                auth.createUserWithEmailAndPassword(ADMIN_EMAIL, ADMIN_PASSWORD)
                    .addOnSuccessListener { authResult ->
                        val uid = authResult.user!!.uid
                        val defaultAdmin = User(
                            id = uid,
                            firstName = "Admin",
                            lastName = "Default",
                            email = ADMIN_EMAIL,
                            idNumber = "",
                            phone = "",
                            gender = "",
                            role = "admin",
                            imageUrl = "",
                            approved = true
                        )
                        dbRef.child(uid).setValue(defaultAdmin)
                            .addOnSuccessListener {
                                Log.d("FirebaseAuth", "Admin created successfully")
                                authResult.user?.updateProfile(
                                    com.google.firebase.auth.UserProfileChangeRequest.Builder()
                                        .setDisplayName("Admin Default")
                                        .build()
                                )
                                auth.signOut()
                            }
                            .addOnFailureListener { e ->
                                Log.e("FirebaseAuth", "Failed to save admin data: ${e.message}")
                                authResult.user?.delete()
                            }
                    }
                    .addOnFailureListener { e ->
                        Log.e("FirebaseAuth", "Failed to create admin: ${e.message}")
                    }
            }
    }

    private fun goToMainActivity() {
        val intent = Intent(requireContext(), MainActivity::class.java).apply {
            putExtra("showLandingPage", true)
            putExtra("showBottomNav", true)
        }
        startActivity(intent)
        activity?.finish()
        activity?.overridePendingTransition(R.anim.fade_in_slide_up, R.anim.fade_out)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == FILE_PICKER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            selectedFileUri = data?.data
            val fileName = selectedFileUri?.let { uri ->
                context?.contentResolver?.query(uri, null, null, null, null)?.use { cursor ->
                    val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    cursor.moveToFirst()
                    cursor.getString(nameIndex)
                }
            } ?: "Unknown file"
            view?.findViewById<TextView>(R.id.textViewFileName)?.text = fileName
        }
    }

    private fun clearFields(
        firstNameInput: TextInputEditText,
        lastNameInput: TextInputEditText,
        emailInput: TextInputEditText,
        idNumberInput: TextInputEditText,
        phoneInput: TextInputEditText,
        passwordInput: TextInputEditText,
        rePasswordInput: TextInputEditText,
        genderGroup: RadioGroup,
        fileNameText: TextView
    ) {
        firstNameInput.text?.clear()
        lastNameInput.text?.clear()
        emailInput.text?.clear()
        idNumberInput.text?.clear()
        phoneInput.text?.clear()
        passwordInput.text?.clear()
        rePasswordInput.text?.clear()
        genderGroup.clearCheck()
        selectedFileUri = null
        fileNameText.text = "No file selected"
    }

    private fun uploadToCloudinary(
        context: android.content.Context,
        fileUri: Uri,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        val inputStream = context.contentResolver.openInputStream(fileUri)
        if (inputStream == null) {
            onError("Could not open file stream")
            return
        }

        val requestBody = inputStream.readBytes().let {
            MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", "upload.pdf",
                    RequestBody.create("application/pdf".toMediaTypeOrNull(), it))
                .addFormDataPart("upload_preset", "unsigned_news")
                .build()
        }

        val request = Request.Builder()
            .url("https://api.cloudinary.com/v1_1/ddncw6btd/raw/upload")
            .post(requestBody)
            .build()

        OkHttpClient().newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                onError(e.message ?: "Upload failed")
            }

            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) {
                    onError("Upload failed: ${response.message}")
                    return
                }
                val json = JSONObject(response.body?.string() ?: "")
                val fileUrl = json.getString("secure_url")
                onSuccess(fileUrl)
            }
        })
    }
}