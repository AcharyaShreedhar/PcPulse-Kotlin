import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.shree.pcpulse.R
import com.shree.pcpulse.Store

class StoreAdapter : RecyclerView.Adapter<StoreAdapter.StoreViewHolder>() {

    private var storeList: List<Store> = ArrayList() // Initialize an empty list

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoreViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.store_item, parent, false)
        return StoreViewHolder(view)
    }

    override fun onBindViewHolder(holder: StoreViewHolder, position: Int) {
        val store = storeList[position]
        holder.storeNameTextView.text = store.name
        holder.storeAddressTextView.text = store.address
        holder.storeRatingBar.rating = store.rating.toFloat()
    }

    override fun getItemCount(): Int {
        return storeList.size
    }

    fun setStores(stores: List<Store>) {
        storeList = stores
        notifyDataSetChanged()
    }

    inner class StoreViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val storeNameTextView: TextView = itemView.findViewById(R.id.storeNameTextView)
        val storeAddressTextView: TextView = itemView.findViewById(R.id.storeAddressTextView)
        val storeRatingBar: RatingBar = itemView.findViewById(R.id.storeRatingBar)
    }
}
