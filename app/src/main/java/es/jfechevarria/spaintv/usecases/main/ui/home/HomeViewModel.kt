package es.jfechevarria.spaintv.usecases.main.ui.home

import android.app.Application
import androidx.lifecycle.*
import es.jfechevarria.data.Repository
import es.jfechevarria.domain.Channel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Exception

class HomeViewModel(val app: Application) : AndroidViewModel(app) {

    private val repository = Repository(app)
    private var _data = MutableLiveData<List<Channel>>()
    val data: LiveData<List<Channel>>
        get() = _data

    fun load() {
        viewModelScope.launch {
            try {
                val list = withContext(Dispatchers.IO) {
                    repository.roomDB?.dataDao()?.all()
                }
                _data.postValue(list)
            } catch (e: Exception) {
            }

        }
    }
}