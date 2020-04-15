package pl.damrad.customsunnyportalapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager.VERTICAL
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_instalation.*

import pl.damrad.customsunnyportalapp.R
import pl.damrad.customsunnyportalapp.adapters.InstalationAdapter
import pl.damrad.customsunnyportalapp.adapters.InstalationItem

/**
 * A simple [Fragment] subclass.
 */
class InstalationFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_instalation, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val list = ArrayList<InstalationItem>()
        list.add()

        instalationRecycler.layoutManager = LinearLayoutManager(context)
        instalationRecycler.adapter = InstalationAdapter( ,context)
    }

}
