package com.github.composemusic.route.searchresult

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.composemusic.bean.search.SearchRecordBean
import com.github.composemusic.bean.song.SongMediaBean
import com.github.composemusic.route.musicplayer.service.MusicServiceHandler
import com.github.composemusic.route.search.SearchEvent
import com.github.composemusic.route.search.SearchStatus
import com.github.composemusic.usecase.search.SearchUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchResultViewModel @Inject constructor(
    private val useCase: SearchUseCase,
    private val savedStateHandle: SavedStateHandle,
    private val musicServiceHandler: MusicServiceHandler
) : ViewModel() {
    private val _uiStatus = mutableStateOf(SearchResultUIStatus())
    val uiStatus: State<SearchResultUIStatus> = _uiStatus

    private val _eventFlow = MutableSharedFlow<SearchStatus>()
    val eventFlow = _eventFlow.asSharedFlow()

    init {
        viewModelScope.launch {
            savedStateHandle.get<String>("key")?.let {
                if (it.isNotEmpty()) {
                    _uiStatus.value = uiStatus.value.copy(
                        keyword = it,
                        buffer = it
                    )
                }
            }
        }
    }
    fun onEvent(event: SearchEvent) {
        viewModelScope.launch(Dispatchers.Main) {
            when (event) {
                is SearchEvent.Search -> {
                    if (_uiStatus.value.buffer.isEmpty()) {
                        _eventFlow.emit(SearchStatus.SearchEmpty)
                    } else {
                        _uiStatus.value = uiStatus.value.copy(
                            keyword = _uiStatus.value.buffer,
                        )
                        useCase.insert(
                            SearchRecordBean(
                                createTime = System.currentTimeMillis(),
                                keyword = _uiStatus.value.keyword
                            )
                        )
                    }
                }
                is SearchEvent.ChangeKey -> {
                    _uiStatus.value = uiStatus.value.copy(
                        buffer = event.key
                    )
                }
                is SearchEvent.InsertMusicItem -> {
                    musicServiceHandler.isExistPlaylist(
                        SongMediaBean(
                            createTime = System.currentTimeMillis(),
                            songID = event.bean.id,
                            songName = event.bean.name,
                            cover = event.bean.al.picUrl,
                            artist = event.bean.ar[0].name,
                            url = "",
                            isLoading = false,
                            duration = 0L,
                            size = ""
                        )
                    )
                }

                else -> {}
            }
        }
    }
}