package top.kikit.uniqueidprivacy

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.net.wifi.WifiManager
import android.os.Bundle
import android.telephony.TelephonyManager
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.tbruyelle.rxpermissions2.RxPermissions
import java.util.*

class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity";

    private val rxPermissions = RxPermissions(this)

    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        willErrorInAndroidQ()

        createRandomUUID()
    }

    private fun createRandomUUID() {
        UUID.randomUUID().toString()
    }

    private  fun willErrorInAndroidQ() {
        val mTelephonyMgr = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

        rxPermissions.request(Manifest.permission.READ_PHONE_STATE)
            .subscribe {
                if (it) {
                    val subscriberId = mTelephonyMgr.subscriberId // imsi
                    val imei = mTelephonyMgr.imei // imei

                    Log.i(TAG, "subscriberId : $subscriberId")
                    Log.i(TAG, "imei : $imei")
                }
            }

        val wifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        rxPermissions.request(Manifest.permission.ACCESS_WIFI_STATE)
            .subscribe {
                wifiManager.configuredNetworks
                Log.i(TAG, "mac = ${wifiManager.connectionInfo.macAddress}")
                Log.i(TAG, "mac = ${wifiManager.connectionInfo.macAddress}")
                Log.i(TAG, "mac = ${wifiManager.connectionInfo.macAddress}")
            }


    }
}
