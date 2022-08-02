package tahadeta.example.savedplaces.ui.demo

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.spicyanimation.SpicyAnimation
import me.relex.circleindicator.CircleIndicator3
import tahadeta.example.savedplaces.R

class DemoActivity : AppCompatActivity(), ViewPagerAdapter.ConditionViewPager {

    private val data = mutableListOf<String>()
    private lateinit var viewPager: ViewPager2
    private lateinit var indicator: CircleIndicator3
    private lateinit var nextImage: ImageView
    private lateinit var backImage: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_demo)
        castView()
        addToList()

        viewPager.adapter = ViewPagerAdapter(this.baseContext, data, this)
        viewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL

        indicator.setViewPager(viewPager)

        nextImage.setOnClickListener {
            viewPager.apply {
                beginFakeDrag()
                fakeDragBy(-10f)
                endFakeDrag()
            }
        }

        backImage.setOnClickListener {
            viewPager.apply {
                beginFakeDrag()
                fakeDragBy(10f)
                endFakeDrag()
            }
        }

        SpicyAnimation().fadeToRight(nextImage, 20F, 1400)
        SpicyAnimation().fadeToLeft(backImage, 20F, 1400)

        Handler().postDelayed({
            nextImage.visibility = View.GONE
            backImage.visibility = View.GONE
        }, 1500) // 1500 is the delayed time in milliseconds.
    }

    private fun castView() {

        viewPager = findViewById(R.id.view_pager2)
        indicator = findViewById(R.id.indicator)
        nextImage = findViewById(R.id.next_iv)
        backImage = findViewById(R.id.back_iv)
    }

    private fun addToList() {
        for (item in 1..3) {
            data.add("item $item")
        }
    }

    @SuppressLint("ResourceType")
    override fun condition(position: Int, fullSize: Int) {
        // Toast.makeText(this, "$position / $fullSize", Toast.LENGTH_SHORT).show()
    }
}
