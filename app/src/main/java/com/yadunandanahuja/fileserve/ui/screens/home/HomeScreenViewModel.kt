package com.yadunandanahuja.fileserve.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.yadunandanahuja.fileserve.FileServeApplication
import com.yadunandanahuja.fileserve.data.models.FileInfoModel
import com.yadunandanahuja.fileserve.data.repositories.FileInfoRepository
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


data class HomeScreenState(
    val fileInfos: ImmutableList<FileInfoModel> = persistentListOf()
)


class HomeScreenViewModel(
    private val fileInfoRepository: FileInfoRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(HomeScreenState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            fileInfoRepository.truncate()
            val fileInfoFlow = fileInfoRepository.findAll()
            fileInfoFlow.collect {
                _uiState.value = HomeScreenState(it.toImmutableList())
            }
        }
    }

    fun saveFileInfo(fileInfo: FileInfoModel) {
        viewModelScope.launch(Dispatchers.IO) {
            fileInfoRepository.save(fileInfo)
        }
    }

    companion object {
        fun provideFactory() = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val application = extras[APPLICATION_KEY] as FileServeApplication
                return HomeScreenViewModel(application.appContainer.fileInfoRepository) as T
            }
        }
    }
}