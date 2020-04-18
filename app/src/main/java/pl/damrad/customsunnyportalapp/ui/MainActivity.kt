package pl.damrad.customsunnyportalapp.ui

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.os.AsyncTask
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.Window
import android.webkit.JavascriptInterface
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.nav_header.*
import kotlinx.android.synthetic.main.progress_dialog.*
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import pl.damrad.customsunnyportalapp.R
import pl.damrad.customsunnyportalapp.ui.fragments.EnergyAndPowerFragment
import pl.damrad.customsunnyportalapp.ui.fragments.InstallationFragment
import pl.damrad.customsunnyportalapp.statics.DataObjects
import pl.damrad.customsunnyportalapp.statics.Keys

class MainActivity : AppCompatActivity() {

    companion object {
        const val END_SCALE: Float = 0.7f
    }

    var dataList: HashMap<String, String> = HashMap()

    lateinit var toggle: ActionBarDrawerToggle
    private var dialogFlag = true
    private lateinit var dialog: Dialog
    private var jInterface: MyJavaScriptInterface = MyJavaScriptInterface()

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setDrawerToggle()
        setToolbar()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        navView.setCheckedItem(R.id.instlationViewItem)
        setNavigationItemListener()

        val url = intent.extras?.get(DataObjects.URL_INTENT)

        mainWeb.webViewClient = setWebClient()
        mainWeb.settings.javaScriptEnabled = true
        mainWeb.settings.loadsImagesAutomatically = true
        mainWeb.addJavascriptInterface(jInterface, "HtmlViewer");

        mainWeb.loadUrl(DataObjects.INSTALLATION_URL)
    }

    private fun setToolbar() {
        toolbarMain.setNavigationOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        toolbarMain.setOnMenuItemClickListener {
            if (it.itemId == R.id.refreshDataItem) {
                mainWeb.reload()
                return@setOnMenuItemClickListener true
            }
            return@setOnMenuItemClickListener false
        }
    }

    private fun setDrawerToggle() {
        toggle = ActionBarDrawerToggle(this@MainActivity, drawerLayout, R.string.open, R.string.close)

        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        drawerLayout.setScrimColor(Color.TRANSPARENT)
        drawerLayout.elevation = 0.0f
        drawerLayout.addDrawerListener(object : DrawerLayout.SimpleDrawerListener() {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {

                // Scale the View based on current slide offset
                val diffScaledOffset = slideOffset * (1 - END_SCALE)
                val offsetScale: Float = 1.0f - diffScaledOffset
                contentView.scaleX = offsetScale
                contentView.scaleY = offsetScale

                // Translate the View, accounting for the scaled width
                val xOffset = drawerView.width * slideOffset
                val xOffsetDiff = contentView.width * diffScaledOffset / 2
                val xTranslation = xOffset - xOffsetDiff
                contentView.translationX = xTranslation
            }
        })
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
                    view!!.settings.loadsImagesAutomatically = true
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

                Thread.sleep(500)

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
    ) : AsyncTask<Void, Void, HashMap<String, String>>() {


        override fun doInBackground(vararg params: Void?): HashMap<String, String> {
            val hashMap = HashMap<String, String>()

            val document: Document = Jsoup.parse(data)

            val installationName = document.getElementsByClass("analysis").text()

            val currentPower = document.selectFirst("div[data-name='pvPower']").select(".mainValueAmount").text()
            val currentState = document.getElementsByClass("analysis").text() //TODO

            val allDayPower = document.getElementById("ctl00_ContentPlaceHolder1_UserControlShowDashboard1_energyYieldWidget_energyYieldValue").text()
            val allDayUnderText =
                document.getElementById("ctl00_ContentPlaceHolder1_UserControlShowDashboard1_energyYieldWidget_energyYieldPeriodTitle").text()
            val allTimePower =
                document.getElementById("ctl00_ContentPlaceHolder1_UserControlShowDashboard1_energyYieldWidget_energyYieldTotalValue").text()

            val co2ReductionValue = document.getElementById("ctl00_ContentPlaceHolder1_UserControlShowDashboard1_carbonWidget_carbonReductionValue").text()
            val co2ReductionTogether = document.getElementById("ctl00_ContentPlaceHolder1_UserControlShowDashboard1_carbonWidget_carbonReductionTotalValue").text()
            val co2ReductionUnderText = document.getElementById("ctl00_ContentPlaceHolder1_UserControlShowDashboard1_carbonWidget_carbonReductionPeriodTitle").text()


            hashMap[Keys.INSTALLATION_NAME] = installationName
            hashMap[Keys.CURRENT_POWER] = currentPower
            hashMap[Keys.CURRENT_STATE] = currentState

            hashMap[Keys.ALL_DAY_POWER] = allDayPower
            hashMap[Keys.ALL_DAY_TEXT] = allDayUnderText
            hashMap[Keys.ALL_TIME_POWER] = allTimePower

            hashMap[Keys.CO2_REDUCTION] = co2ReductionValue
            hashMap[Keys.CO2_REDUCTION_TOGETHER] = co2ReductionTogether
            hashMap[Keys.CO2_REDUCTION_UNDER_TEXT] = co2ReductionUnderText

            return hashMap
        }

        override fun onPostExecute(result: HashMap<String, String>?) {
            super.onPostExecute(result)

            if (result!!.isNotEmpty()) {
                logedUserTV.text = result[Keys.INSTALLATION_NAME]
                dataList = result
                supportFragmentManager.beginTransaction().replace(R.id.fragmentContainer, InstallationFragment()).commit()
            }
        }

    }

    private fun setNavigationItemListener() {
        navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.instlationViewItem -> {
                    supportFragmentManager.beginTransaction().replace(R.id.fragmentContainer, InstallationFragment()).commit()
                }
                R.id.energyAndPowerItem -> {
                    supportFragmentManager.beginTransaction().replace(R.id.fragmentContainer, EnergyAndPowerFragment()).commit()
                }
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }

        logOutItem.setOnClickListener {
            val intent = Intent(this@MainActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
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

