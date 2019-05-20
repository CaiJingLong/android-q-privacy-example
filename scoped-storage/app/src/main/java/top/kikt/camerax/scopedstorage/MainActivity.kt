package top.kikt.camerax.scopedstorage

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File

class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val path = externalCacheDir?.absoluteFile?.path

        Log.d(TAG, "cache dir = $path")

        val file = File(path, "abc.txt")

        file.writeText("我要往里写数据")

        val text = file.readText()

        Log.d(TAG, "数据是: $text")

        bt_media_page.setOnClickListener {
            startActivity(Intent(this, MediaScanActivity::class.java))
        }
    }
}
