package pl.damrad.customsunnyportalapp.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.squareup.picasso.Picasso
import pl.damrad.customsunnyportalapp.R
import pl.damrad.customsunnyportalapp.adapters.items.*
import pl.damrad.customsunnyportalapp.statics.CardIds

class InstallationAdapter(
    private val list: ArrayList<InstallationItem>,
    private val context: Context
) : RecyclerView.Adapter<ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return when (viewType) {
            CardIds.CURRENT_POWER -> {
                val view = LayoutInflater.from(context).inflate(R.layout.item_current_power, parent, false)
                CurrentPowerViewHolder(view)
            }
            CardIds.CURRENT_STATE -> {
                val view = LayoutInflater.from(context).inflate(R.layout.current_installation_state, parent, false)
                InstallationStateHolder(view)
            }
            CardIds.ALL_DAY_STATE -> {
                val view = LayoutInflater.from(context).inflate(R.layout.energy_fotovoltanic, parent, false)
                AllDayViewHolder(view)
            }
            CardIds.CO_2_STATE -> {
                val view = LayoutInflater.from(context).inflate(R.layout.co2_reduction, parent, false)
                Co2ReductionViewHolder(view)
            }
            else -> {
                val view = LayoutInflater.from(context).inflate(R.layout.empty_item, parent, false)
                ErrorViewHolder(view)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when (holder) {
            is CurrentPowerViewHolder -> {
                holder.currentPowerTV.text = "${(list[position] as CurrentPowerItem).currentPower} W"
            }
            is InstallationStateHolder -> {
                //TODO placeholder image for a while
                Picasso.with(context).load(R.mipmap.ic_ok_foreground).into(holder.stateIV)
            }
            is AllDayViewHolder -> {
                holder.allDayTV.text = "${(list[position] as AllDayItem).allDayWh} kWh"
                holder.valueAllTogetherTV.text = "${context.getString(R.string.all_together)} ${(list[position] as AllDayItem).allTogetherWh} kWh"
                holder.textUnderToday.text = (list[position] as AllDayItem).allDayText
            }
            is Co2ReductionViewHolder -> {
                holder.valueCo2TV.text = "${(list[position] as Co2ReductionItem).allDayReduction} kg"
                holder.co2TogetherTV.text =
                    "${context.getString(R.string.all_together)} ${(list[position] as Co2ReductionItem).allTogetherReduction} kg"
                holder.co2TextToday.text = (list[position] as Co2ReductionItem).allDayReductionText
            }
            else -> {
                (holder as ErrorViewHolder).errorText.text = context.getString(R.string.error_loaded)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        val item = list[position]

        return when {
            (item is CurrentStateItem) -> {
                CardIds.CURRENT_STATE
            }
            (item is CurrentPowerItem) -> {
                CardIds.CURRENT_POWER
            }
            (item is AllDayItem) -> {
                CardIds.ALL_DAY_STATE
            }
            (item is Co2ReductionItem) -> {
                CardIds.CO_2_STATE
            }
            else -> {
                CardIds.ERROR_STATE
            }
        }
    }

    override fun getItemCount(): Int = list.size

    inner class CurrentPowerViewHolder(itemView: View) : ViewHolder(itemView) {
        val currentPowerTV: TextView = itemView.findViewById(R.id.valueCurrentTV)
    }

    inner class InstallationStateHolder(itemView: View) : ViewHolder(itemView) {
        val stateIV: ImageView = itemView.findViewById(R.id.valueStateIV)
    }

    inner class ErrorViewHolder(itemView: View) : ViewHolder(itemView) {
        val errorText: TextView = itemView.findViewById(R.id.errorTV)
    }

    inner class AllDayViewHolder(itemView: View) : ViewHolder(itemView) {
        val allDayTV: TextView = itemView.findViewById(R.id.valueAllDayTV)
        val valueAllTogetherTV: TextView = itemView.findViewById(R.id.valueAllTogetherTV)
        val textUnderToday: TextView = itemView.findViewById(R.id.textViewToday)
    }

    inner class Co2ReductionViewHolder(itemView: View) : ViewHolder(itemView) {
        val valueCo2TV: TextView = itemView.findViewById(R.id.valueCo2TV)
        val co2TogetherTV: TextView = itemView.findViewById(R.id.co2TogetherTV)
        val co2TextToday: TextView = itemView.findViewById(R.id.co2TextToday)
    }
}