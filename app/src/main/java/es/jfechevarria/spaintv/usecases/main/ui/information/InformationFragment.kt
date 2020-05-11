package es.jfechevarria.spaintv.usecases.main.ui.information

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import es.jfechevarria.spaintv.databinding.FragmentSlideshowBinding

class InformationFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentSlideshowBinding.inflate(inflater, container, false)
        binding.webview.loadUrl("file:///android_asset/index.html")
        return binding.root
    }
}
