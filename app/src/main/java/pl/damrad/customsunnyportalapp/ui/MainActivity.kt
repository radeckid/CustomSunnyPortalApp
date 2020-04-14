package pl.damrad.customsunnyportalapp.ui

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.os.AsyncTask
import android.os.Bundle
import android.view.MenuItem
import android.view.Window
import android.webkit.JavascriptInterface
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.nav_header.*
import kotlinx.android.synthetic.main.progress_dialog.*
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import pl.damrad.customsunnyportalapp.R
import pl.damrad.customsunnyportalapp.statics.DataObjects

class MainActivity : AppCompatActivity() {

    lateinit var toggle: ActionBarDrawerToggle
    private var dialogFlag = true
    private lateinit var dialog: Dialog
    private var jInterface: MyJavaScriptInterface = MyJavaScriptInterface()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        toggle = ActionBarDrawerToggle(this@MainActivity, drawerLayout, R.string.open, R.string.close)

        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setNavigationItemListener()

        val url = intent.extras?.get(DataObjects.URL_INTENT)

        mainWeb.webViewClient = setWebClient()
        mainWeb.settings.javaScriptEnabled = true
        mainWeb.settings.loadsImagesAutomatically = false
        mainWeb.addJavascriptInterface(jInterface, "HtmlViewer");

        mainWeb.loadUrl(url.toString())
    }

    private inner class MyJavaScriptInterface() {
        @JavascriptInterface
        fun showHTML(html: String?) {
            if (html != null) {
                AsyncGet(html).execute()
            }
        }
    }

    private fun setWebClient(): WebViewClient {
        return object : WebViewClient() {

            @SuppressLint("SetJavaScriptEnabled")
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {

                //Handle domain links and open it in this app
                if (DataObjects.BASE_URL == request!!.url.host) {
                    view!!.settings.loadsImagesAutomatically = false
                    view.settings.javaScriptEnabled = true
                    view.settings.javaScriptCanOpenWindowsAutomatically = true
                    view.clearCache(true)
                    view.loadUrl(request.url.toString())

                    return true
                }

                //If link is from another domain it just start a normal browser
                val intent = Intent(Intent.ACTION_VIEW, request.url)
                startActivity(intent)
                return false
            }


            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                showDialog()
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)

                mainWeb.loadUrl("javascript:window.HtmlViewer.showHTML('<head>'+document.getElementsByTagName('html')[0].innerHTML+'</head>');")

                if (dialog.isShowing) {
                    try {
                        dialog.dismiss()
                    } catch (ignored: Exception) {
                    }
                }

            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    private inner class AsyncGet(
        val data: String
    ) : AsyncTask<Void, Void, String>() {


        override fun doInBackground(vararg params: Void?): String {
            val document: Document = Jsoup.parse(data)
            return document.getElementById("ctl00_Header_lblUserName").text()

        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)

            if (result!!.isNotEmpty()) {
                logedUserTV.text = result
            }
        }

    }

    private fun setNavigationItemListener() {
        navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.instlationViewItem -> {

                }
                R.id.energyAndPowerItem -> {

                }
                R.id.logOutItem -> {
                    val intent = Intent(this@MainActivity, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
            true
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showDialog(title: String = "") {
        if (dialogFlag) {
            dialog = Dialog(this@MainActivity)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setCancelable(false)
            dialog.setContentView(R.layout.progress_dialog)
            dialogFlag = false
        }

        if (title.isNotEmpty()) {
            dialog.progressDialogText.text = title
        }

        if (!this@MainActivity.isFinishing) {
            dialog.show()
        }
    }
}

