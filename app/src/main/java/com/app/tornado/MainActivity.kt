package com.app.tornado

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.common.util.UnstableApi
import com.app.tornado.databinding.ActivityMainBinding
import es.dmoral.toasty.Toasty


@UnstableApi
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btPlay.setOnClickListener {
            if(binding.etMagnet.text.isNotBlank()){
                val intent = Intent(this, PlayerActivity::class.java)

                intent.putExtra(PARAM_MAGNET_LINK, binding.etMagnet.text.toString())
                startActivity(intent)
            } else {
                Toasty.error(this, "Paste the magnet link in the above field").show()
            }
        }

    }

    companion object {
        const val PARAM_MAGNET_LINK = "PARAM_MAGNET_LINK"
    }

}