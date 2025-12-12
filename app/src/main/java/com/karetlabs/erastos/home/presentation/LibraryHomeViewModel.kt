package com.karetlabs.erastos.home.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.karetlabs.erastos.home.presentation.LibraryHomeScreenEvent.AddBookClickEvent
import com.karetlabs.erastos.home.presentation.LibraryHomeScreenEvent.BookClickEvent
import com.karetlabs.erastos.home.presentation.LibraryHomeScreenEvent.BookShelfSelectEvent
import com.karetlabs.erastos.home.presentation.LibraryHomeScreenEvent.DismissBottomSheetEvent
import com.karetlabs.erastos.home.presentation.LibraryHomeScreenEvent.ScanIsbnClickEvent
import com.karetlabs.erastos.home.presentation.LibraryHomeScreenEvent.ScreenCreatedEvent
import com.karetlabs.erastos.home.presentation.LibraryHomeScreenEvent.SearchQueryChangeEvent
import com.karetlabs.erastos.library.presentation.mvi.HasEvents
import com.karetlabs.erastos.library.presentation.mvi.HasIntents
import com.karetlabs.erastos.library.presentation.mvi.HasIntentsImpl
import com.karetlabs.erastos.library.presentation.mvi.HasState
import com.karetlabs.erastos.library.presentation.mvi.HasStateImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LibraryHomeViewModel @Inject constructor(
    private val getBooksAndBookShelvesPairUseCase: GetBooksAndBookShelvesPairUseCase,
) : ViewModel(),
    HasEvents<LibraryHomeScreenEvent>,
    HasIntents<LibraryHomeScreenIntent> by HasIntentsImpl(),
    HasState<HomeScreenViewState> by HasStateImpl(HomeScreenViewState()) {

    override fun onEvent(event: LibraryHomeScreenEvent) {
        when (event) {
            ScreenCreatedEvent -> onCreate()
            AddBookClickEvent -> showHideBottomSheet(true, BottomSheetType.ADD_BOOK)
            is BookClickEvent -> {}
            is BookShelfSelectEvent -> {}
            is SearchQueryChangeEvent -> {}
            DismissBottomSheetEvent -> showHideBottomSheet(false, BottomSheetType.NONE)
            ScanIsbnClickEvent -> showHideBottomSheet(true, BottomSheetType.SCAN_ISBN)
        }
    }

    private fun showHideBottomSheet(shouldShow: Boolean, sheetType: BottomSheetType) {
        updateState { state ->
            state.copy(shouldOpenBottomSheet = shouldShow, bottomSheetType = sheetType)
        }
    }

    private fun onCreate() {
        viewModelScope.launch {
            getBooksAndBookShelvesPairUseCase().collect {
                updateState { state ->
                    state.copy(
                        books = it.first,
                        bookshelves = it.second
                    )
                }
            }
        }
    }
}