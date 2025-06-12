package com.example.myhockey

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import java.net.URLEncoder

class PdfViewerFragment : Fragment() {

    companion object {
        private const val ARG_PDF_URL = "pdf_url"

        fun newInstance(pdfUrl: String): PdfViewerFragment {
            val fragment = PdfViewerFragment()
            val bundle = Bundle()
            bundle.putString(ARG_PDF_URL, pdfUrl)
            fragment.arguments = bundle
            return fragment
        }
    }

    private var pdfUrl: String? = null
    private lateinit var webView: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pdfUrl = arguments?.getString(ARG_PDF_URL)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        webView = WebView(requireContext())
        return webView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        webView.webViewClient = WebViewClient()
        webView.settings.javaScriptEnabled = true
        webView.settings.cacheMode = WebSettings.LOAD_NO_CACHE

        pdfUrl?.let {
            try {
                val encodedUrl = URLEncoder.encode(it, "UTF-8")
                val googleDocsUrl = "https://docs.google.com/gview?embedded=true&url=$encodedUrl"
                webView.loadUrl(googleDocsUrl)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
