package es.jfechevarria.spaintv.extensions

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

inline fun <reified T : ViewModel> FragmentActivity.viewModel(body: T.() -> Unit): T {
    val vm = ViewModelProvider(this)[T::class.java]
    vm.body()
    return vm
}
