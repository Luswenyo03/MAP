package com.example.myhockey

import android.app.Dialog
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide
import com.google.firebase.database.FirebaseDatabase
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONObject
import java.io.IOException
import java.util.*

class AddNewsDialogFragment(
    private val newsItem: admin_news_report.NewsItem? = null,
    private val onNewsChanged: () -> Unit
) : DialogFragment() {

    private lateinit var imagePreview: ImageView
    private lateinit var pickImageButton: Button
    private lateinit var submitButton: Button
    private lateinit var deleteButton: Button
    private lateinit var titleEditText: EditText
    private lateinit var contentEditText: EditText
    private var imageUri: Uri? = null
    private var imageUrlFromNewsItem: String? = null  // store original image url

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            imageUri = uri
            imagePreview.setImageURI(uri)
            imagePreview.visibility = View.VISIBLE
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = LayoutInflater.from(requireContext())
        val view = inflater.inflate(R.layout.dialog_add_news, null)

        imagePreview = view.findViewById(R.id.imagePreview)
        pickImageButton = view.findViewById(R.id.btnPickImage)
        submitButton = view.findViewById(R.id.btnSubmitNews)
        deleteButton = view.findViewById(R.id.btnDeleteNews)
        titleEditText = view.findViewById(R.id.editTitle)
        contentEditText = view.findViewById(R.id.editContent)

        // If editing existing news, pre-fill fields & adjust UI
        newsItem?.let {
            titleEditText.setText(it.title)
            contentEditText.setText(it.content)
            imageUrlFromNewsItem = it.imageUrl
            if (!imageUrlFromNewsItem.isNullOrEmpty()) {
                Glide.with(this).load(imageUrlFromNewsItem).into(imagePreview)
                imagePreview.visibility = View.VISIBLE
            }
            submitButton.text = "Update News"
            deleteButton.visibility = View.VISIBLE
        } ?: run {
            deleteButton.visibility = View.GONE
            submitButton.text = "Add News"
        }

        pickImageButton.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        submitButton.setOnClickListener {
            val title = titleEditText.text.toString().trim()
            val content = contentEditText.text.toString().trim()

            if (title.isEmpty() || content.isEmpty()) {
                Toast.makeText(requireContext(), "Please enter title and content", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            submitButton.isEnabled = false
            submitButton.text = if (newsItem == null) "Uploading..." else "Updating..."

            if (imageUri != null) {
                uploadImageThenSaveNews(title, content, imageUri!!)
            } else {
                // No new image selected; use existing imageUrl if editing
                saveNewsToDatabase(title, content, imageUrlFromNewsItem)
            }
        }

        deleteButton.setOnClickListener {
            deleteNews()
        }

        return AlertDialog.Builder(requireContext())
            .setView(view)
            .create()
    }

    private fun uploadImageThenSaveNews(title: String, content: String, imageUri: Uri) {
        val inputStream = requireContext().contentResolver.openInputStream(imageUri)
        if (inputStream == null) {
            Toast.makeText(requireContext(), "Failed to read image", Toast.LENGTH_SHORT).show()
            resetSubmitButton()
            return
        }
        val requestBody = inputStream.readBytes().let {
            MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", "news.jpg",
                    RequestBody.create("image/*".toMediaTypeOrNull(), it))
                .addFormDataPart("upload_preset", "unsigned_news")
                .build()
        }

        val request = Request.Builder()
            .url("https://api.cloudinary.com/v1_1/ddncw6btd/image/upload")
            .post(requestBody)
            .build()

        val client = OkHttpClient()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                requireActivity().runOnUiThread {
                    Toast.makeText(requireContext(), "Cloudinary upload failed", Toast.LENGTH_SHORT).show()
                    resetSubmitButton()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) {
                    requireActivity().runOnUiThread {
                        Toast.makeText(requireContext(), "Cloudinary error: ${response.message}", Toast.LENGTH_SHORT).show()
                        resetSubmitButton()
                    }
                    return
                }

                val responseBody = response.body?.string()
                val json = JSONObject(responseBody ?: "")
                val imageUrl = json.getString("secure_url")

                requireActivity().runOnUiThread {
                    saveNewsToDatabase(title, content, imageUrl)
                }
            }
        })
    }

    private fun saveNewsToDatabase(title: String, content: String, imageUrl: String?) {
        val database = FirebaseDatabase.getInstance()
        val newsRef = database.getReference("news")

        val newsId = newsItem?.id ?: newsRef.push().key ?: UUID.randomUUID().toString()

        val newsData = hashMapOf<String, Any?>(
            "id" to newsId,
            "title" to title,
            "content" to content,
            "imageUrl" to imageUrl,
            "timestamp" to System.currentTimeMillis()
        )

        newsRef.child(newsId).setValue(newsData)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), if (newsItem == null) "News added successfully" else "News updated successfully", Toast.LENGTH_SHORT).show()
                onNewsChanged()
                dismiss()
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Failed to save news", Toast.LENGTH_SHORT).show()
                resetSubmitButton()
            }
    }

    private fun deleteNews() {
        val database = FirebaseDatabase.getInstance()
        val newsRef = database.getReference("news")
        val newsId = newsItem?.id ?: return

        AlertDialog.Builder(requireContext())
            .setTitle("Delete News")
            .setMessage("Are you sure you want to delete this news?")
            .setPositiveButton("Delete") { _, _ ->
                newsRef.child(newsId).removeValue()
                    .addOnSuccessListener {
                        Toast.makeText(requireContext(), "News deleted successfully", Toast.LENGTH_SHORT).show()
                        onNewsChanged()
                        dismiss()
                    }
                    .addOnFailureListener {
                        Toast.makeText(requireContext(), "Failed to delete news", Toast.LENGTH_SHORT).show()
                    }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun resetSubmitButton() {
        submitButton.isEnabled = true
        submitButton.text = if (newsItem == null) "Add News" else "Update News"
    }
}
