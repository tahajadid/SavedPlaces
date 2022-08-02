package tahadeta.example.savedplaces.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.spicyanimation.SpicyAnimation
import tahadeta.example.savedplaces.R
import tahadeta.example.savedplaces.helper.Constants.FIRST_CON
import tahadeta.example.savedplaces.helper.ModelPreferencesManager
import tahadeta.example.savedplaces.ui.demo.DemoActivity
import tahadeta.example.savedplaces.ui.map.MapsActivity

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        ModelPreferencesManager.with(this.application)

        val view3 = findViewById<View>(R.id.view3)
        val onlySaved = findViewById<ImageView>(R.id.only_saved)
        val logo = findViewById<ImageView>(R.id.logo_iv)

        SpicyAnimation().rectangularRoad(
            view3,
            50F,
            250,
            false
        ) // true if you want a fade in animation start/end

        Handler().postDelayed({
            logo.visibility = View.VISIBLE
            onlySaved.visibility = View.VISIBLE
            SpicyAnimation().fadeToDown(onlySaved, 40F, 600)
            SpicyAnimation().fadeToUp(logo, 20F, 500)
        }, 2000) // 600 is the delayed time in milliseconds.

        Handler().postDelayed({

            if (ModelPreferencesManager.get<Boolean>(FIRST_CON) == null) {
                ModelPreferencesManager.put(true, FIRST_CON)
                val intent = Intent(this, DemoActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                val intent = Intent(this, MapsActivity::class.java)
                startActivity(intent)
                finish()
            }
        }, 3000) // 600 is the delayed time in milliseconds.
    }
}
