package com.example.sports_match_day.ui.base

import androidx.lifecycle.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * Created by Kristo on 09-Mar-21
 */
abstract class ScopedViewModel : ViewModel() {
    private val coroutineJob = Job()
    val uiScope = CoroutineScope(Dispatchers.Main + coroutineJob)

    override fun onCleared() {
        super.onCleared()
        coroutineJob.cancel()
    }

    protected val _isDataLoading = MutableLiveData<Boolean>()
    val isDataLoading: LiveData<Boolean> = _isDataLoading

    protected val _apiErrorMessage = MutableLiveData<Throwable>()
    val apiErrorMessage: LiveData<Throwable> = _apiErrorMessage.toSingleEvent()

    fun clearError(){
        _apiErrorMessage.value = Throwable()
    }

    protected fun launchWithLoad(
        onSuccess: suspend () -> Unit,
        onError: suspend (Throwable) -> Unit
    ): Job {
        return uiScope.launch {
            try {
                _isDataLoading.value = true
                onSuccess()
            } catch (t: Throwable) {
                Timber.e(t)
                _apiErrorMessage.value = t
                onError(t)
            } finally {
                _isDataLoading.value = false
            }
        }
    }
}
fun <T> LiveData<T>.observeOnce(lifecycleOwner: LifecycleOwner, observer: Observer<T>) {
    observe(lifecycleOwner, object : Observer<T> {
        override fun onChanged(t: T?) {
            if(t == null) return
            observer.onChanged(t)
            removeObserver(this)
        }
    })
}