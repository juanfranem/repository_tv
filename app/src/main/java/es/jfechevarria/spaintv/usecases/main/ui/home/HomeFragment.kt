package es.jfechevarria.spaintv.usecases.main.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import es.jfechevarria.data.Repository
import es.jfechevarria.domain.Channel
import es.jfechevarria.spaintv.Actions
import es.jfechevarria.spaintv.R
import es.jfechevarria.spaintv.databinding.FragmentHomeBinding
import es.jfechevarria.spaintv.usecases.videoView.PlayerActivity

class HomeFragment : Fragment() {

    private lateinit var viewModel: HomeViewModel
    private lateinit var adapter: HomeAdapter
    private lateinit var binding: FragmentHomeBinding
    private lateinit var mInterstitialAd: InterstitialAd

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel =
            ViewModelProviders.of(this).get(HomeViewModel::class.java)
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        binding.recycler.layoutManager = LinearLayoutManager(requireContext())
        adapter = HomeAdapter(
            listOf(),
            Repository(requireContext()),
            object : Actions {
                override fun onClick(channel: Channel) {
                    if (mInterstitialAd.isLoaded) {
                        mInterstitialAd.show()
                    }
                    startActivity(Intent(requireContext(), PlayerActivity::class.java).apply {
                        putExtra("DATA_EXTRA", channel)
                    })
                }
            }) {
            if (it < 1) {
                binding.empty.visibility = View.VISIBLE
            } else {
                binding.empty.visibility = View.GONE
            }
        }
        binding.recycler.adapter = adapter
        viewModel.data.observe(viewLifecycleOwner, Observer {
            if (it.isEmpty()) {
                binding.empty.visibility = View.VISIBLE
            } else {
                binding.empty.visibility = View.GONE
                adapter.submitList(it)
            }
        })
        binding.button.setOnClickListener {
            Navigation.findNavController(it).navigate(R.id.action_nav_channel_to_nav_all_channels)
        }
        mInterstitialAd = InterstitialAd(requireContext())
        mInterstitialAd.adUnitId = getString(R.string.ad_inter)
        mInterstitialAd.loadAd(AdRequest.Builder().build())
        mInterstitialAd.adListener = object: AdListener() {
            override fun onAdClosed() {
                mInterstitialAd.loadAd(AdRequest.Builder().build())
            }
        }
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        viewModel.load()
    }
}
