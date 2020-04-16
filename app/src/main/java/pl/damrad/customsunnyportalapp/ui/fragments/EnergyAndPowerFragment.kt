package pl.damrad.customsunnyportalapp.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import pl.damrad.customsunnyportalapp.R

/**
 * A simple [Fragment] subclass.
 */
class EnergyAndPowerFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_energy_and_power, container, false)
    }

}
