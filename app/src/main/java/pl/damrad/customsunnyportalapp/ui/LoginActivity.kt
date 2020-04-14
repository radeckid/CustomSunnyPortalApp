package pl.damrad.customsunnyportalapp.ui

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.Window
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.progress_dialog.*
import pl.damrad.customsunnyportalapp.R
import pl.damrad.customsunnyportalapp.statics.DataObjects

class LoginActivity : AppCompatActivity() {

    companion object {
        const val SHARED_LOGIN_DATA = "pl.damrad.customsunnyportalapp.SHARED_LOGIN_DATA"
        const val SHARED_EMAIL = "pl.damrad.customsunnyportalapp.SHARED_EMAIL"
        const val SHARED_PASSWORD = "pl.damrad.customsunnyportalapp.SHARED_PASSWORD"
        const val SHARED_REMEMBER = "pl.damrad.customsunnyportalapp.SHARED_REMEMBER"
    }

    private var email: String? = null
    private var pass: String? = null
    private var loginFlag = false
    private var dialogFlag = true
    private lateinit var dialog: Dialog


    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        showDialog()

        val sharedPref = applicationContext.getSharedPreferences(SHARED_LOGIN_DATA, Context.MODE_PRIVATE)

        loginWeb.webViewClient = setWebClient()
        loginWeb.settings.javaScriptEnabled = true
        loginWeb.settings.loadsImagesAutomatically = false
        loginWeb.loadUrl(DataObjects.LOGIN_URL)

        email = sharedPref.getString(SHARED_EMAIL, "")
        pass = sharedPref.getString(SHARED_PASSWORD, "")
        val remember = sharedPref.getBoolean(SHARED_REMEMBER, false)

        rememberMeCheckBox.isChecked = remember

        if (!email.isNullOrEmpty()) {
            emailTV.editText?.setText(email)
            if (!pass.isNullOrEmpty()) {
                passwordTV.editText?.setText(pass)
                passwordTV.editText?.setSelection(pass!!.length)
            }
        }

        onClickListeners(sharedPref)
        openWebBrowser()
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


                if (loginWeb.url == DataObjects.LOGIN_URL) {
                    if (!email.isNullOrEmpty() && !pass.isNullOrEmpty()) {
                        setEmailAndPasswordInput(email!!, pass!!)
                    }
                }

                if (loginFlag && loginWeb.url == DataObjects.LOGGED_URL) {
                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                    intent.putExtra(DataObjects.URL_INTENT, loginWeb.url)
                    startActivity(intent)
                    finish()
                    loginFlag = false;
                }

                if (dialog.isShowing) {
                    try {
                        dialog.dismiss()
                        loginBtn.isEnabled = true
                    } catch (ignored: Exception) {
                    }
                }
            }
        }
    }

    private fun showDialog(title: String = "") {
        if (dialogFlag) {
            dialog = Dialog(this@LoginActivity)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setCancelable(false)
            dialog.setContentView(R.layout.progress_dialog)
            dialogFlag = false
        }

        loginBtn.isEnabled = false

        if (title.isNotEmpty()) {
            dialog.progressDialogText.text = title
        }

        if (!this@LoginActivity.isFinishing) {
            dialog.show()
        }
    }

    private fun openWebBrowser() {
        siteLinkTV.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://${DataObjects.BASE_URL}"))
            startActivity(intent)
            finish()
        }
    }

    private fun onClickListeners(sharedPref: SharedPreferences) {
        loginBtn.setOnClickListener {

            loginWeb.loadUrl(
                "javascript:(function() { " +
                        "document.getElementById('ctl00_ContentPlaceHolder1_LoginControl1_LoginBtn').click();" +
                        "})()"
            )

            loginFlag = true

            if (rememberMeCheckBox.isChecked) {
                sharedPref.edit()
                    .putString(SHARED_EMAIL, emailTV.editText?.text.toString().trim())
                    .putString(SHARED_PASSWORD, passwordTV.editText?.text.toString())
                    .putBoolean(SHARED_REMEMBER, true)
                    .apply()
            } else if (!rememberMeCheckBox.isChecked) {
                sharedPref.edit()
                    .putString(SHARED_EMAIL, "")
                    .putString(SHARED_PASSWORD, "")
                    .putBoolean(SHARED_REMEMBER, false)
                    .apply()
            }
        }
    }

    private fun setEmailAndPasswordInput(email: String, password: String) {
        loginWeb.loadUrl(
            "javascript:(function() {" +
                    "document.getElementById('txtUserName').setAttribute('value','${email.trim()}');" +
                    "document.getElementById('txtPassword').setAttribute('value','$password');" +
                    "})()"
        )
    }
}
