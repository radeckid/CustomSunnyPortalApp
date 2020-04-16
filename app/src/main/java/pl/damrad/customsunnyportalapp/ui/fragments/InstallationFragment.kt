package pl.damrad.customsunnyportalapp.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_instalation.*

import pl.damrad.customsunnyportalapp.R
import pl.damrad.customsunnyportalapp.adapters.InstallationAdapter
import pl.damrad.customsunnyportalapp.adapters.InstallationItem
import pl.damrad.customsunnyportalapp.ui.MainActivity

/**
 * A simple [Fragment] subclass.
 */
class InstallationFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_instalation, container, false)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val list = ArrayList<InstallationItem>()
        list.add(InstallationItem("1500", null, null, null))
        list.add(InstallationItem(null, "OK", null, null))
        list.add(InstallationItem(null, null, "51,21", "811"))

        installationRecycler.apply {
            layoutManager = LinearLayoutManager(view.context)
            val myAdapter = InstallationAdapter(list, view.context)
            adapter = myAdapter
        }
    }

}
