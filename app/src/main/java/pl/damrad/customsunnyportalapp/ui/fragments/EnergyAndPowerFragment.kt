package pl.damrad.customsunnyportalapp.ui.fragments

import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_energy_and_power.*
import ozaydin.serkan.com.image_zoom_view.ImageViewZoomConfig
import pl.damrad.customsunnyportalapp.R
import java.io.File
import java.io.FileNotFoundException


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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        reloadDiagram()

        reloadDiagramBtn.setOnClickListener {
            reloadDiagram()
        }
    }

    private fun reloadDiagram() {
            val filePath = context?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            val file = File(filePath, "diagram.png")

            val dataBitmap = try {
                BitmapFactory.decodeFile(file.path)
            } catch (e: FileNotFoundException) {
                null
            }

            if (dataBitmap != null) {
                diagramIV.setImageBitmap(dataBitmap)
                reloadDiagramBtn.visibility = View.GONE
            } else {
                reloadDiagramBtn.visibility = View.VISIBLE
            }
    }

}
