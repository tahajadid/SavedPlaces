package tahadeta.example.savedplaces.ui.demo

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.spicyanimation.SpicyAnimation
import tahadeta.example.savedplaces.R
import tahadeta.example.savedplaces.ui.MapsActivity

class ViewPagerAdapter(
    private val context: Context?,
    private val dataValue: List<String>,
    private val condition: ConditionViewPager
) :
    RecyclerView.Adapter<ViewPagerAdapter.ViewPagerViewHolder>() {

    inner class ViewPagerViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val title: TextView = view.findViewById(R.id.text_in_slide)
        val go: ImageView = view.findViewById(R.id.go_iv)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewPagerViewHolder =
        ViewPagerViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_page, parent, false)
        )

    override fun onBindViewHolder(holder: ViewPagerViewHolder, position: Int) {
        condition.condition(position, dataValue.size)

        holder.title.text = dataValue[position]
        if (position + 1 == dataValue.size) {
            holder.go.visibility = View.VISIBLE
            SpicyAnimation().fadeToUp(holder.go, 40F, 600)

        }

        holder.go.setOnClickListener {
            val intentMaps = Intent(this.context, MapsActivity::class.java)
            intentMaps.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            this.context?.startActivity(intentMaps)
        }
    }

    override fun getItemCount(): Int = dataValue.size

    interface ConditionViewPager {

        fun condition(position: Int, fullSize: Int)
    }
}
