package tahadeta.example.savedplaces

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import tahadeta.example.savedplaces.model.FavouritePlace

class FavouriteAdapter(
    private val context: Context?,
    private val list: List<FavouritePlace>
) : RecyclerView.Adapter<FavouriteAdapter.ViewHolder>() {

    private var listData: MutableList<FavouritePlace> = list as MutableList<FavouritePlace>

    /**
     * Find all the views of the list item
     */
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindView(item: FavouritePlace, position: Int, context: Context?) {
            var cordToShow: String = ""

            val deleteImage: TextView = itemView.findViewById(R.id.delete_tv)
            val labelPlace: TextView = itemView.findViewById(R.id.place_name)
            val coordonateItem: TextView = itemView.findViewById(R.id.coordonate_item)

            if (item.lat.toString().length > 7) cordToShow += item.lat.toString().take(6) else
                cordToShow += item.lat.toString()

            if (item.lng.toString().length > 7) cordToShow += item.lng.toString().take(6) else
                cordToShow += item.lng.toString()

            coordonateItem.text = cordToShow
            labelPlace.text = item.title

            /*
            deleteImage.setOnClickListener {
                deleteItem(position)
            }
             */
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.bindView(item, position, context)

        val delete = holder.itemView.findViewById<TextView>(R.id.delete_tv)
        delete.setOnClickListener {
            deleteItem(position)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView: View = LayoutInflater.from(context).inflate(R.layout.favourite_item, parent, false)
        return ViewHolder(itemView)
    }

    override fun getItemCount(): Int = list.size

    fun deleteItem(index: Int) {
        listData.removeAt(index)
        notifyDataSetChanged()
    }
}
