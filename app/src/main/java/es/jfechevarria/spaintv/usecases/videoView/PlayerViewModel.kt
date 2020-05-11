package es.jfechevarria.spaintv.usecases.videoView

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import es.jfechevarria.data.Repository
import es.jfechevarria.domain.Channel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PlayerViewModel(val app: Application): AndroidViewModel(app) {

    var current: Channel? = null

    lateinit var changeChannel: (any: Channel) -> Unit

}