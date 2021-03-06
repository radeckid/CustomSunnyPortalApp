package pl.damrad.customsunnyportalapp.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_instalation.*
import pl.damrad.customsunnyportalapp.R
import pl.damrad.customsunnyportalapp.adapters.InstallationAdapter
import pl.damrad.customsunnyportalapp.adapters.items.*
import pl.damrad.customsunnyportalapp.statics.Keys
import pl.damrad.customsunnyportalapp.ui.MainActivity

/**
 * A simple [Fragment] subclass.
 */
class InstallationFragment : Fragment() {

    private lateinit var list: ArrayList<InstallationItem>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_instalation, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val activity = activity as MainActivity
        val data = activity.dataList

        list = createList(data)

        installationRecycler.apply {
            layoutManager = LinearLayoutManager(view.context)
            val myAdapter = InstallationAdapter(list, view.context)
            adapter = myAdapter
        }
    }

    private fun createList(data: HashMap<String, String>): ArrayList<InstallationItem> {
        val currentPower = data[Keys.CURRENT_POWER] ?: " "
        val subHeadPower = data[Keys.SUB_HEAD_POWER] ?: " "

        val currentState = data[Keys.CURRENT_STATE] ?: " "

        val allDayPower = data[Keys.ALL_DAY_POWER] ?: " "
        val allDayPowerUnit = data[Keys.ALL_DAY_POWER_UNIT] ?: " "
        val allTimePower = data[Keys.ALL_TIME_POWER] ?: " "
        val allDayUnderTextPower = data[Keys.ALL_DAY_TEXT] ?: " "

        val allDayCo2 = data[Keys.CO2_REDUCTION] ?: " "
        val allTimeCo2 = data[Keys.CO2_REDUCTION_TOGETHER] ?: " "
        val allDayUnderTextCo2 = data[Keys.CO2_REDUCTION_UNDER_TEXT] ?: " "

        val list = ArrayList<InstallationItem>()
        list.add(CurrentPowerItem(currentPower, subHeadPower))
        list.add(CurrentStateItem(currentState))
        list.add(AllDayItem(allDayPower, allDayPowerUnit, allTimePower, allDayUnderTextPower))
        list.add(Co2ReductionItem(allDayCo2, allTimeCo2, allDayUnderTextCo2))

        return list
    }

}
