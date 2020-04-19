package pl.damrad.customsunnyportalapp.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_error.*
import pl.damrad.customsunnyportalapp.R
import pl.damrad.customsunnyportalapp.statics.Keys

class ErrorActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_error)

        errorMessage()

        refreshErrorButton.setOnClickListener {
            val intent = Intent(this@ErrorActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun errorMessage() {
        val intent = intent;
        val message = intent.getIntExtra(Keys.ERROR_ACTION, 22)

        val text: String

        when (message) {
            -16 -> {
                text = getString(R.string.ERROR_UNSAFE_RESOURCE)
            }
            -15 -> {
                text = getString(R.string.ERROR_TOO_MANY_REQUESTS)
            }
            -14 -> {
                text = getString(R.string.ERROR_FILE_NOT_FOUND)
            }
            -13 -> {
                text = getString(R.string.ERROR_FILE)
            }
            -12 -> {
                text = getString(R.string.ERROR_BAD_URL)
            }
            -11 -> {
                text = getString(R.string.ERROR_FAILED_SSL_HANDSHAKE)
            }
            -10 -> {
                text = getString(R.string.ERROR_UNSUPPORTED_SCHEME)
            }
            -9 -> {
                text = getString(R.string.ERROR_REDIRECT_LOOP)
            }
            -8 -> {
                text = getString(R.string.ERROR_TIMEOUT)
            }
            -7 -> {
                text = getString(R.string.ERROR_IO)
            }
            -6 -> {
                text = getString(R.string.ERROR_CONNECT)
            }
            -5 -> {
                text = getString(R.string.ERROR_PROXY_AUTHENTICATION)
            }
            -4 -> {
                text = getString(R.string.ERROR_AUTHENTICATION)
            }
            -3 -> {
                text = getString(R.string.ERROR_UNSUPPORTED_AUTH_SCHEME)
            }
            -2 -> {
                text = getString(R.string.ERROR_HOST_LOOKUP)
            }
            -1 -> {
                text = getString(R.string.ERROR_UNKNOWN)
            }
            0 -> {
                text = getString(R.string.SAFE_BROWSING_THREAT_UNKNOWN)
            }
            1 -> {
                text = getString(R.string.SAFE_BROWSING_THREAT_MALWARE)
            }
            2 -> {
                text = getString(R.string.SAFE_BROWSING_THREAT_PHISHING)
            }
            3 -> {
                text = getString(R.string.SAFE_BROWSING_THREAT_UNWANTED_SOFTWARE)
            }
            4 -> {
                text = getString(R.string.SAFE_BROWSING_THREAT_BILLING)
            }
            else -> {
                text = "NULL"
            }
        }
        errorTV.text = text
    }
}
