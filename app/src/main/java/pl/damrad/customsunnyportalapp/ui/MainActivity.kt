package pl.damrad.customsunnyportalapp.ui

import android.annotation.SuppressLint
import android.app.Dialog
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.*
import android.view.MenuItem
import android.view.View
import android.view.Window
import android.webkit.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.nav_header.*
import kotlinx.android.synthetic.main.progress_dialog.*
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import pl.damrad.customsunnyportalapp.R
import pl.damrad.customsunnyportalapp.statics.DataObjects
import pl.damrad.customsunnyportalapp.statics.Keys
import pl.damrad.customsunnyportalapp.ui.fragments.EnergyAndPowerFragment
import pl.damrad.customsunnyportalapp.ui.fragments.InstallationFragment
import java.io.File

class MainActivity : AppCompatActivity() {

    companion object {
        const val END_SCALE: Float = 0.7f
        const val INSTALLATION_FRAGMENT: String = "pl.damrad.customsunnyportalapp.ui.INSTALLATION_FRAGMENT"
        const val ENERGY_AND_POWER_FRAGMENT: String = "pl.damrad.customsunnyportalapp.ui.ENERGY_AND_POWER_FRAGMENT"
        const val VISIBLE_FRAGMENT: String = "pl.damrad.customsunnyportalapp.ui.VISIBLE_FRAGMENT"
    }

    var dataList: HashMap<String, String> = HashMap()

    private lateinit var toggle: ActionBarDrawerToggle
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

        mainWeb.settings.loadWithOverviewMode = true
        mainWeb.settings.useWideViewPort = true
        mainWeb.setPadding(0, 0, 0, 0)
        mainWeb.webViewClient = setWebClient()
        mainWeb.settings.javaScriptEnabled = true
        mainWeb.settings.loadsImagesAutomatically = true
        mainWeb.addJavascriptInterface(jInterface, "HtmlViewer")
        mainWeb.setDownloadListener(myDownloadListener())

        mainWeb.loadUrl(DataObjects.INSTALLATION_URL)

        val sharedPrefVisible = getSharedPreferences(VISIBLE_FRAGMENT, Context.MODE_PRIVATE)
        if (sharedPrefVisible != null) {
            val fragment = sharedPrefVisible.getString(VISIBLE_FRAGMENT, INSTALLATION_FRAGMENT)
            when (fragment) {
                INSTALLATION_FRAGMENT -> {
                    supportFragmentManager.beginTransaction().replace(R.id.fragmentContainer, InstallationFragment(), INSTALLATION_FRAGMENT).commit()
                }
                ENERGY_AND_POWER_FRAGMENT -> {
                    supportFragmentManager.beginTransaction().replace(R.id.fragmentContainer, EnergyAndPowerFragment(), ENERGY_AND_POWER_FRAGMENT)
                        .commit()
                }
            }
        } else {
            supportFragmentManager.beginTransaction().replace(R.id.fragmentContainer, InstallationFragment(), INSTALLATION_FRAGMENT).commit()
        }
    }

    override fun onStop() {
        super.onStop()

        val sharedPref = this@MainActivity.getPreferences(Context.MODE_PRIVATE) ?: return
        with(sharedPref.edit()) {
            putString(VISIBLE_FRAGMENT, supportFragmentManager.fragments[0].tag)
            commit()
        }
    }

    private fun setToolbar() {
        toolbarMain.setNavigationOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        toolbarMain.setOnMenuItemClickListener {
            if (it.itemId == R.id.refreshDataItem) {
                mainWeb.loadUrl(DataObjects.INSTALLATION_URL)
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
        drawerLayout.drawerElevation = 0.0f;
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

    private fun setNavigationItemListener() {
        navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.instlationViewItem -> {
                    supportFragmentManager.beginTransaction().replace(R.id.fragmentContainer, InstallationFragment(), INSTALLATION_FRAGMENT).commit()
                }
                R.id.energyAndPowerItem -> {
                    supportFragmentManager.beginTransaction().replace(R.id.fragmentContainer, EnergyAndPowerFragment(), ENERGY_AND_POWER_FRAGMENT)
                        .commit()
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

    //WebView client
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
                } else if (request.url.host?.endsWith(".png")!!) {
                    val source: Uri = Uri.parse(request.url.host)

                    // Make a new request pointing to the .apk url
                    val newRequest: DownloadManager.Request = DownloadManager.Request(source)

                    // appears the same in Notification bar while downloading
                    newRequest.setDescription("Description for the DownloadManager Bar")
                    newRequest.setTitle("diagram.png");
                    newRequest.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                    // save the file in the "Downloads" folder of SDCARD
                    newRequest.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "diagram.png")
                    // get download service and enqueue file
                    val manager: DownloadManager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                    manager.enqueue(newRequest)
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

            override fun onReceivedError(view: WebView?, errorCode: Int, description: String?, failingUrl: String?) {
                val intent = Intent(this@MainActivity, ErrorActivity::class.java)
                intent.putExtra(Keys.ERROR_ACTION, errorCode)
                startActivity(intent)
                finish()
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)

                if (url?.trim()?.endsWith(".png")!!) {
                    val downloadUrl = "javascript:(function() { " +
                            "    var element = document.createElement('a');\n" +
                            "    element.setAttribute('href', '');\n" +
                            "    element.setAttribute('download', '" + dataList[Keys.INSTALLATION_DIAGRAM_IMAGE] + "');\n" +
                            "    element.style.display = 'none';\n" +
                            "    document.body.appendChild(element);\n" +
                            "    element.click();\n" +
                            "})()"

                    mainWeb.loadUrl(downloadUrl)

                } else {
                    Handler().postDelayed({
                        mainWeb.loadUrl("javascript:window.HtmlViewer.showHTML('<head>'+document.getElementsByTagName('html')[0].innerHTML+'</head>');")
                    }, 1000)
                }

                if (dialog.isShowing) {
                    try {
                        dialog.dismiss()
                    } catch (ignored: Exception) {
                    }
                }

            }
        }
    }

    private fun myDownloadListener(): DownloadListener {
        return DownloadListener { url, _, _, mimetype, _ ->
            val cookie = CookieManager.getInstance().getCookie(url)

            val request = DownloadManager.Request(Uri.parse(url))

            val file = File(applicationContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "diagram.png")

            if (file.exists()) {
                file.delete()
            }

            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN)
                .setDestinationInExternalFilesDir(applicationContext, Environment.DIRECTORY_PICTURES, "diagram.png")
                .addRequestHeader("Cookie", cookie)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)
                .setMimeType(mimetype)

            val dm = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            dm.enqueue(request)
        }
    }

    //Interface needed to take all html file in string from javascript
    private inner class MyJavaScriptInterface() {
        @JavascriptInterface
        fun showHTML(html: String?) {
            if (html != null) {
                AsyncGet(html).execute()
            }
        }
    }

    //AsyncTask for Scraping data from web
    @SuppressLint("StaticFieldLeak")
    private inner class AsyncGet(
        val data: String
    ) : AsyncTask<Void, Void, HashMap<String, String>>() {


        override fun doInBackground(vararg params: Void?): HashMap<String, String> {
            val hashMap = HashMap<String, String>()

            val document: Document = Jsoup.parse(data)

            val installationName = document.getElementsByClass("analysis").text()
            var installationImageUrl = document.select("div.analysis").attr("style")

            installationImageUrl = try {
                installationImageUrl.substring(installationImageUrl.indexOf("url(") + 4, installationImageUrl.indexOf(");"))
            } catch (ignored: Exception) {
                ""
            }

            val installationDiagramUrl =
                document.getElementById("ctl00\$ContentPlaceHolder1\$UserControlShowDashboard1\$UserControlShowEnergyAndPower1\$_diagram")
                    .attr("src")

            val currentPower = document.selectFirst("div[data-name='pvPower']").select(".mainValueAmount").text()
            val subHeadPower = document.selectFirst("div[data-name='pvPower']").select(".widgetSubHead").text()

            var currentStateUrl = document.select("div[data-name='plantStatus']").select(".widgetBody").attr("style")
            currentStateUrl = try {
                currentStateUrl.substring(currentStateUrl.indexOf("url(\"") + 5, currentStateUrl.indexOf("\")"))
            } catch (ignored: Exception) {
                ""
            }

            val allDayPower =
                document.getElementById("ctl00_ContentPlaceHolder1_UserControlShowDashboard1_energyYieldWidget_energyYieldValue").text()
            val allDayPowerUnit =
                document.getElementById("ctl00_ContentPlaceHolder1_UserControlShowDashboard1_energyYieldWidget_energyYieldUnit").text()
            val allDayUnderText =
                document.getElementById("ctl00_ContentPlaceHolder1_UserControlShowDashboard1_energyYieldWidget_energyYieldPeriodTitle").text()
            val allTimePower =
                document.getElementById("ctl00_ContentPlaceHolder1_UserControlShowDashboard1_energyYieldWidget_energyYieldTotalValue").text()

            val co2ReductionValue =
                document.getElementById("ctl00_ContentPlaceHolder1_UserControlShowDashboard1_carbonWidget_carbonReductionValue").text()
            val co2ReductionTogether =
                document.getElementById("ctl00_ContentPlaceHolder1_UserControlShowDashboard1_carbonWidget_carbonReductionTotalValue").text()
            val co2ReductionUnderText =
                document.getElementById("ctl00_ContentPlaceHolder1_UserControlShowDashboard1_carbonWidget_carbonReductionPeriodTitle").text()


            hashMap[Keys.INSTALLATION_NAME] = installationName
            hashMap[Keys.INSTALLATION_IMAGE] = installationImageUrl

            hashMap[Keys.INSTALLATION_DIAGRAM_IMAGE] = installationDiagramUrl

            hashMap[Keys.CURRENT_POWER] = currentPower
            hashMap[Keys.SUB_HEAD_POWER] = subHeadPower

            hashMap[Keys.CURRENT_STATE] = currentStateUrl

            hashMap[Keys.ALL_DAY_POWER] = allDayPower
            hashMap[Keys.ALL_DAY_POWER_UNIT] = allDayPowerUnit
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

                var imageInstallation = result[Keys.INSTALLATION_IMAGE]
                if (!imageInstallation.isNullOrEmpty()) {
                    imageInstallation = "https://${DataObjects.BASE_URL}$imageInstallation"
                    imageInstallation = imageInstallation.replace("32x32", "250x250")

                    Picasso.get()
                        .load(imageInstallation)
                        .placeholder(R.drawable.ic_logo)
                        .into(fotovoltanicImage)

                }

                var imageDiagramUrl = result[Keys.INSTALLATION_DIAGRAM_IMAGE]
                if (!imageDiagramUrl.isNullOrEmpty()) {
                    imageDiagramUrl = "https://${DataObjects.BASE_URL}$imageDiagramUrl"

                    mainWeb.loadUrl(imageDiagramUrl)
                }

                dataList = result

                val actualFragment = supportFragmentManager.fragments[0]

                if (actualFragment.isVisible) {
                    supportFragmentManager.beginTransaction().replace(R.id.fragmentContainer, actualFragment.javaClass.newInstance()).commit()
                }

            }
        }

    }

    //Shows loading dialog
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

