package es.jfechevarria.spaintv.usecases.main.ui.allChannels

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.paging.PagedList
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import es.jfechevarria.data.Repository
import es.jfechevarria.domain.Channel
import es.jfechevarria.spaintv.Actions
import es.jfechevarria.spaintv.R
import es.jfechevarria.spaintv.databinding.FragmentAllChannelsBinding
import es.jfechevarria.spaintv.usecases.videoView.PlayerActivity

class AllChannelsFragment : Fragment(), SearchView.OnQueryTextListener {

    private lateinit var viewModel: AllChannelsViewModel
    private lateinit var binding: FragmentAllChannelsBinding
    private lateinit var adapter: ChannelsAdapter
    private lateinit var mInterstitialAd: InterstitialAd

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel =
            ViewModelProviders.of(this).get(AllChannelsViewModel::class.java)
        binding = FragmentAllChannelsBinding.inflate(inflater, container, false)
        binding.recycler.layoutManager = LinearLayoutManager(requireContext())
        adapter = ChannelsAdapter(
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

                })
        binding.recycler.adapter = adapter
        viewModel.data?.observe(viewLifecycleOwner, Observer {
            adapter.submitList(it)
        })
        viewModel.queryText.postValue("")
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        menu.findItem(R.id.action_search).isVisible = true
        val searchItem = menu.findItem(R.id.action_search)
        val searchView: SearchView? = searchItem?.actionView as SearchView
        searchView?.queryHint = getString(R.string.search_query)
        searchView?.setOnQueryTextListener(this)
        searchView?.isIconified = false;
    }

    override fun onResume() {
        super.onResume()
        adapter.notifyDataSetChanged()
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        if (newText.isNullOrEmpty())
            viewModel.queryText.postValue(newText)
        else if (newText.length > 3)
            viewModel.queryText.postValue(newText)
        return true
    }


}
