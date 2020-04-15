package pl.damrad.customsunnyportalapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.current_instalation_state.view.*
import kotlinx.android.synthetic.main.empty_item.view.*
import kotlinx.android.synthetic.main.energy_fotovoltanic.view.*
import kotlinx.android.synthetic.main.item_current_power.view.*
import pl.damrad.customsunnyportalapp.R
import pl.damrad.customsunnyportalapp.statics.CardIds

class InstalationAdapter(
    private val list: ArrayList<InstalationItem>,
    private val context: Context
) : RecyclerView.Adapter {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            when (viewType) {
                CardIds.CURRENT_POWER ->
                    LayoutInflater.from(context).inflate(R.layout.item_current_power, parent, false)
                CardIds.CURRENT_STATE ->
                    LayoutInflater.from(context).inflate(R.layout.current_instalation_state, parent, false)
                CardIds.ALL_DAY_STATE ->
                    LayoutInflater.from(context).inflate(R.layout.energy_fotovoltanic, parent, false)
                else ->
                    LayoutInflater.from(context).inflate(R.layout.empty_item, parent, false)
            }
        )
    }

    override fun onBindViewHolder(holder: InstalationAdapter.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            CardIds.CURRENT_POWER -> {
                holder.currentPowerTV.text = list[position].currentPower.toString()
            }
            CardIds.CURRENT_STATE -> {
                Picasso.with(context).load("http://i.imgur.com/DvpvklR.png").into(holder.stateIV)
            }
            CardIds.ALL_DAY_STATE -> {
                holder.allDayTV.text = list[position].allDayVolt.toString()
            }
            else -> {
                holder.errorText.text = "Błąd wczytywania"
            }
        }
    }


    override fun getItemCount(): Int = list.size

    inner class CurrentPowerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val currentPowerTV: TextView = itemView.valueCurrentTV
    }

    inner class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val stateIV: ImageView = itemView.valueStateIV
    }

    inner class ErrorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val errorText: TextView = itemView.errorTV
    }

    inner class AllDayViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val allDayTV: TextView = itemView.valueAllDayTV
    }
}