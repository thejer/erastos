# ViewModels Guide

**Project:** Erastos Book Library  
**Last Updated:** April 17, 2026  
**Status:** Active

---

## Table of Contents

1. [Overview](#overview)
2. [ViewModel Fundamentals](#viewmodel-fundamentals)
3. [MVI ViewModel Pattern](#mvi-viewmodel-pattern)
4. [Creating a ViewModel](#creating-a-viewmodel)
5. [State Management](#state-management)
6. [Event Handling](#event-handling)
7. [Intent Handling](#intent-handling)
8. [Async Operations](#async-operations)
9. [Error Handling](#error-handling)
10. [Testing ViewModels](#testing-viewmodels)
11. [Best Practices](#best-practices)
12. [Troubleshooting](#troubleshooting)

---

## Overview

A **ViewModel** is the presentation layer component responsible for:
- Managing UI state
- Processing user events
- Coordinating with repositories
- Handling async operations
- Surviving configuration changes (screen rotations)

In Erastos, we use **MVI (Model-View-Intent)** pattern where ViewModels implement two key interfaces:
- `HasState<T>` - Manages observable state
- `HasEvents<T>` - Processes UI events

```
Composable Screen
    ↓ sends event via
ViewModel.onEvent(event)
    ↓ processes with
Repository/UseCase
    ↓ updates state via
updateState(reducer)
    ↓ emits back to
Composable via StateFlow
    ↓ UI Re-renders
```

---

## ViewModel Fundamentals

### Android ViewModel Lifecycle

```
onCreate() 
    ↓
onCleared() 
```

The ViewModel persists as long as its associated scope (usually Activity) is alive.

```kotlin
class MyViewModel : ViewModel() {
    init {
        // Called when ViewModel is created
        initializeData()
    }
    
    override fun onCleared() {
        super.onCleared()
        // Called when ViewModel is about to be destroyed
        // Clean up resources here
    }
}
```

### Key Properties

- **Survives configuration changes** - Retained during screen rotation
- **Scoped to Activity/Fragment** - Lives as long as the Activity/Fragment
- **Not garbage collected until scope ends** - Even if Composable recomposes
- **Thread-safe** - Safe to access from multiple coroutines

---

## MVI ViewModel Pattern

### Architecture in Erastos

Every feature ViewModel implements:

```kotlin
interface HasState<T : BaseViewState> {
    val viewState: T
    val viewStateFlow: StateFlow<T>
    fun updateState(reducer: (T) -> T)
}

interface HasEvents<T : BaseEvent> {
    fun onEvent(event: T)
}
```

### Complete MVI ViewModel Structure

```kotlin
@HiltViewModel
class MyFeatureViewModel @Inject constructor(
    private val repository: MyRepository
) : ViewModel(),
    HasState<MyFeatureState> by HasStateImpl(MyFeatureState()),
    HasEvents<MyFeatureEvent> by object : HasEvents<MyFeatureEvent> {
        override fun onEvent(event: MyFeatureEvent) {
            when(event) {
                // handle events
            }
        }
    }
{
    // Implementation details...
}
```

### Alternative Pattern (Inheritance)

```kotlin
@HiltViewModel
class MyFeatureViewModel @Inject constructor(
    private val repository: MyRepository
) : ViewModel(),
    HasState<MyFeatureState>,
    HasEvents<MyFeatureEvent> {
    
    private val _stateImpl = HasStateImpl(MyFeatureState())
    override val viewState by _stateImpl::viewState
    override val viewStateFlow by _stateImpl::viewStateFlow
    override fun updateState(reducer: (MyFeatureState) -> MyFeatureState) {
        _stateImpl.updateState(reducer)
    }
    
    override fun onEvent(event: MyFeatureEvent) {
        when(event) {
            // handle events
        }
    }
}
```

---

## Creating a ViewModel

### Step 1: Define Contract

```kotlin
// File: presentation/LibraryHomeContract.kt
sealed class LibraryHomeScreenEvent : BaseEvent {
    data object ScreenCreatedEvent : LibraryHomeScreenEvent()
    data class BookShelfSelectEvent(val bookshelf: Bookshelf) : LibraryHomeScreenEvent()
    data object AddBookClickEvent : LibraryHomeScreenEvent()
}

sealed class LibraryHomeScreenIntent : BaseIntent {
    data class NavigateToBookDetail(val bookId: Int) : LibraryHomeScreenIntent()
}

data class HomeScreenViewState(
    val bookshelves: List<Bookshelf> = listOf(Bookshelf(name = "All")),
    val books: List<Book> = emptyList(),
    val selectedBookshelf: Bookshelf = Bookshelf(name = "All"),
    val isLoading: Boolean = false,
    val errorMessage: String = ""
) : BaseViewState
```

### Step 2: Create ViewModel

```kotlin
// File: presentation/LibraryHomeViewModel.kt
@HiltViewModel
class LibraryHomeViewModel @Inject constructor(
    private val bookRepository: BookRepository,
    private val bookshelfRepository: BookshelfRepository
) : ViewModel(),
    HasState<HomeScreenViewState> by HasStateImpl(HomeScreenViewState()),
    HasEvents<LibraryHomeScreenEvent> {
    
    // Intent flow for navigation
    private val _intentsFlow = MutableSharedFlow<LibraryHomeScreenIntent>()
    val intentsFlow: SharedFlow<LibraryHomeScreenIntent> = _intentsFlow
    
    override fun onEvent(event: LibraryHomeScreenEvent) {
        when (event) {
            is LibraryHomeScreenEvent.ScreenCreatedEvent -> loadInitialData()
            is LibraryHomeScreenEvent.BookShelfSelectEvent -> selectBookshelf(event.bookshelf)
            is LibraryHomeScreenEvent.AddBookClickEvent -> openAddBookDialog()
        }
    }
    
    private fun loadInitialData() {
        viewModelScope.launch {
            updateState { it.copy(isLoading = true) }
            try {
                val bookshelves = bookshelfRepository.getAllBookshelves()
                updateState { 
                    it.copy(
                        bookshelves = bookshelves,
                        selectedBookshelf = bookshelves.firstOrNull() ?: Bookshelf(),
                        isLoading = false
                    )
                }
                // Load books for the first bookshelf
                loadBooksForBookshelf(bookshelves.firstOrNull() ?: Bookshelf())
            } catch (e: Exception) {
                updateState {
                    it.copy(
                        errorMessage = e.message ?: "Unknown error",
                        isLoading = false
                    )
                }
            }
        }
    }
    
    private fun selectBookshelf(bookshelf: Bookshelf) {
        updateState { it.copy(selectedBookshelf = bookshelf) }
        loadBooksForBookshelf(bookshelf)
    }
    
    private fun loadBooksForBookshelf(bookshelf: Bookshelf) {
        viewModelScope.launch {
            updateState { it.copy(isLoading = true) }
            try {
                val books = bookRepository.getBooksByBookshelf(bookshelf.id)
                updateState {
                    it.copy(
                        books = books,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                updateState {
                    it.copy(
                        errorMessage = e.message ?: "Failed to load books",
                        isLoading = false
                    )
                }
            }
        }
    }
    
    private fun openAddBookDialog() {
        updateState { it.copy(shouldOpenBottomSheet = true) }
    }
}
```

### Step 3: Use in Composable

```kotlin
@Composable
fun LibraryHomeScreen(
    viewModel: LibraryHomeViewModel = hiltViewModel()
) {
    val viewState by viewModel.observableViewState()
    
    // Initialize on screen create
    doOnScreenCreate {
        viewModel.onEvent(LibraryHomeScreenEvent.ScreenCreatedEvent)
    }
    
    // Handle user events
    HomeView(
        bookshelves = viewState.bookshelves,
        selectedBookshelf = viewState.selectedBookshelf,
        onBookshelfSelected = { bookshelf ->
            viewModel.onEvent(LibraryHomeScreenEvent.BookShelfSelectEvent(bookshelf))
        }
    )
}
```

---

## State Management

### State is the Single Source of Truth

```kotlin
// State should contain all necessary UI data
data class HomeScreenViewState(
    val bookshelves: List<Bookshelf> = emptyList(),
    val books: List<Book> = emptyList(),
    val selectedBookshelf: Bookshelf? = null,
    val isLoading: Boolean = false,
    val errorMessage: String = "",
    val shouldShowBottomSheet: Boolean = false,
    val bottomSheetType: BottomSheetType = BottomSheetType.NONE
) : BaseViewState
```

### Immutable Updates Using Copy

```kotlin
// Always use copy() for immutable updates
private fun updateBooks(newBooks: List<Book>) {
    updateState { currentState ->
        currentState.copy(
            books = newBooks,
            isLoading = false
        )
    }
}

// Multiple fields
private fun finishLoading(result: LoadResult) {
    updateState { 
        it.copy(
            isLoading = false,
            books = result.books,
            errorMessage = if (result.isSuccess) "" else result.error
        )
    }
}
```

### StateFlow Observation

```kotlin
// In Composable - automatic lifecycle management
val viewState by viewModel.observableViewState()

// Manual subscription (rarely needed)
val viewState = viewModel.viewStateFlow.collectAsStateWithLifecycle()
```

### Derived State

When you need computed/derived values:

```kotlin
@HiltViewModel
class BookViewModel @Inject constructor(
    private val repository: BookRepository
) : ViewModel(),
    HasState<BookState> by HasStateImpl(BookState()) {
    
    // Derived state - computed from viewState
    val filteredBooks = viewStateFlow.map { state ->
        state.books.filter { book ->
            book.title.contains(state.searchQuery, ignoreCase = true)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = emptyList()
    )
    
    // Usage in Composable
    val filtered by filteredBooks.collectAsStateWithLifecycle()
}
```

---

## Event Handling

### Event Processing Flow

```kotlin
override fun onEvent(event: BookSearchEvent) {
    when (event) {
        is BookSearchEvent.SearchQueryChanged -> handleQueryChange(event.query)
        is BookSearchEvent.SearchClicked -> performSearch()
        is BookSearchEvent.ClearSearch -> clearSearch()
    }
}
```

### Complex Event Handling with Async

```kotlin
override fun onEvent(event: LibraryEvent) {
    when (event) {
        is LibraryEvent.AddBook -> addBookWithLoading(event.book)
        is LibraryEvent.DeleteBook -> deleteBookWithConfirmation(event.bookId)
    }
}

private fun addBookWithLoading(book: Book) {
    viewModelScope.launch {
        // Show loading state
        updateState { it.copy(isLoading = true, errorMessage = "") }
        
        try {
            // Perform async operation
            bookRepository.addBook(book)
            
            // Update state on success
            updateState { state ->
                state.copy(
                    books = state.books + book,
                    isLoading = false
                )
            }
            
            // Emit success intent for navigation
            _intentsFlow.emit(LibraryIntent.ShowSuccessMessage("Book added"))
        } catch (e: Exception) {
            // Handle error
            updateState {
                it.copy(
                    errorMessage = e.message ?: "Failed to add book",
                    isLoading = false
                )
            }
        }
    }
}
```

### Event Deduplication

Prevent duplicate event processing:

```kotlin
private var lastSearchTime = 0L

override fun onEvent(event: BookEvent) {
    when (event) {
        is BookEvent.Search -> {
            val currentTime = System.currentTimeMillis()
            // Only process if > 500ms since last search
            if (currentTime - lastSearchTime > 500) {
                lastSearchTime = currentTime
                performSearch(event.query)
            }
        }
    }
}
```

---

## Intent Handling

### Intents for Navigation & Side Effects

Intents are optional and used when you need to trigger external actions from the ViewModel:

```kotlin
sealed class BookDetailIntent : BaseIntent {
    data class NavigateToAuthor(val authorId: Int) : BookDetailIntent()
    data object ShowDeleteConfirmation : BookDetailIntent()
}

@HiltViewModel
class BookDetailViewModel @Inject constructor(
    private val bookRepository: BookRepository
) : ViewModel(),
    HasState<BookDetailState> by HasStateImpl(BookDetailState()),
    HasEvents<BookDetailEvent> {
    
    // Intent flow for side effects
    private val _intentsFlow = MutableSharedFlow<BookDetailIntent>()
    val intentsFlow: SharedFlow<BookDetailIntent> = _intentsFlow
    
    private fun deleteBook(bookId: Int) {
        viewModelScope.launch {
            try {
                bookRepository.deleteBook(bookId)
                // Emit intent to navigate away
                _intentsFlow.emit(BookDetailIntent.NavigateBack)
            } catch (e: Exception) {
                updateState { it.copy(errorMessage = e.message ?: "Delete failed") }
            }
        }
    }
}
```

### Subscribing to Intents in Composable

```kotlin
@Composable
fun BookDetailScreen(
    viewModel: BookDetailViewModel = hiltViewModel(),
    navController: NavController
) {
    val viewState by viewModel.observableViewState()
    
    // Subscribe to intents
    viewModel.subscribeForIntents { intent ->
        when (intent) {
            is BookDetailIntent.NavigateToAuthor -> {
                navController.navigate("author/${intent.authorId}")
            }
            is BookDetailIntent.ShowDeleteConfirmation -> {
                // Show dialog
            }
        }
    }
    
    BookDetailView(viewState = viewState)
}
```

---

## Async Operations

### Using ViewModelScope

Always use `viewModelScope` for coroutines - automatically cancels when ViewModel is destroyed:

```kotlin
@HiltViewModel
class BookViewModel @Inject constructor(
    private val repository: BookRepository
) : ViewModel() {
    
    fun loadBooks() {
        viewModelScope.launch {  // ✅ Good - uses viewModelScope
            val books = repository.getAllBooks()
            updateState { it.copy(books = books) }
        }
    }
    
    // ❌ Bad - doesn't use viewModelScope
    fun loadBooksWrong() {
        GlobalScope.launch {
            val books = repository.getAllBooks()
        }
    }
}
```

### Handling Cancellation

```kotlin
private var searchJob: Job? = null

override fun onEvent(event: SearchEvent) {
    when (event) {
        is SearchEvent.Search -> {
            // Cancel previous search
            searchJob?.cancel()
            
            searchJob = viewModelScope.launch {
                updateState { it.copy(isLoading = true) }
                try {
                    val results = repository.search(event.query)
                    updateState { it.copy(searchResults = results, isLoading = false) }
                } catch (e: CancellationException) {
                    // Search was cancelled, ignore
                    throw e
                } catch (e: Exception) {
                    updateState { it.copy(errorMessage = e.message, isLoading = false) }
                }
            }
        }
    }
}
```

### Flow-Based Async

```kotlin
@HiltViewModel
class BookViewModel @Inject constructor(
    private val repository: BookRepository
) : ViewModel() {
    
    // Automatic flow collection
    val books = repository.observeBooks()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    
    override fun onCleared() {
        super.onCleared()
        // Flow collection automatically stopped here
    }
}
```

### Combining Multiple Async Operations

```kotlin
private fun loadAllData() {
    viewModelScope.launch {
        try {
            // Run in parallel
            val (books, bookshelves) = awaitAll(
                async { bookRepository.getBooks() },
                async { bookshelfRepository.getBookshelves() }
            )
            
            updateState {
                it.copy(
                    books = books,
                    bookshelves = bookshelves,
                    isLoading = false
                )
            }
        } catch (e: Exception) {
            updateState { it.copy(errorMessage = e.message) }
        }
    }
}
```

---

## Error Handling

### Structured Error Handling

```kotlin
private fun loadBooks() {
    viewModelScope.launch {
        updateState { it.copy(isLoading = true, errorMessage = "") }
        
        try {
            val books = bookRepository.getAllBooks()
            updateState { 
                it.copy(
                    books = books,
                    isLoading = false
                )
            }
        } catch (e: IOException) {
            updateState {
                it.copy(
                    errorMessage = "Network error: ${e.message}",
                    isLoading = false
                )
            }
        } catch (e: IllegalArgumentException) {
            updateState {
                it.copy(
                    errorMessage = "Invalid data: ${e.message}",
                    isLoading = false
                )
            }
        } catch (e: Exception) {
            updateState {
                it.copy(
                    errorMessage = "Unknown error: ${e.message}",
                    isLoading = false
                )
            }
        }
    }
}
```

### Custom Error Handling

```kotlin
sealed class ApiError(message: String) : Exception(message) {
    data class NetworkError(val exception: IOException) : ApiError("Network unavailable")
    data class ServerError(val code: Int) : ApiError("Server error: $code")
    data class DataError(val message: String) : ApiError(message)
}

private fun loadBooks() {
    viewModelScope.launch {
        try {
            val books = bookRepository.getAllBooks()
            updateState { it.copy(books = books, isLoading = false) }
        } catch (e: ApiError) {
            updateState {
                it.copy(
                    errorMessage = when(e) {
                        is ApiError.NetworkError -> "Check your internet connection"
                        is ApiError.ServerError -> "Server is temporarily unavailable"
                        is ApiError.DataError -> e.message
                    },
                    isLoading = false
                )
            }
        }
    }
}
```

### User-Friendly Error Messages

```kotlin
private fun getUserFriendlyErrorMessage(exception: Exception): String {
    return when (exception) {
        is IOException -> "Unable to connect. Please check your internet."
        is TimeoutException -> "Request timed out. Please try again."
        is HttpException -> "Server error (${exception.code}). Please try again."
        else -> "Something went wrong. Please try again."
    }
}
```

---

## Testing ViewModels

### Unit Test Example

```kotlin
class LibraryHomeViewModelTest {
    
    private lateinit var viewModel: LibraryHomeViewModel
    private val bookRepository: BookRepository = mockk()
    private val bookshelfRepository: BookshelfRepository = mockk()
    
    @Before
    fun setUp() {
        viewModel = LibraryHomeViewModel(bookRepository, bookshelfRepository)
    }
    
    @Test
    fun `onEvent ScreenCreatedEvent - loads bookshelves and books`() = runTest {
        // Arrange
        val mockBookshelves = listOf(Bookshelf(name = "Fiction"))
        val mockBooks = listOf(Book(title = "Test Book", bookshelfId = 1))
        coEvery { bookshelfRepository.getAllBookshelves() } returns mockBookshelves
        coEvery { bookRepository.getBooksByBookshelf(1) } returns mockBooks
        
        // Act
        viewModel.onEvent(LibraryHomeScreenEvent.ScreenCreatedEvent)
        
        // Assert
        val state = viewModel.viewState
        assertEquals(mockBookshelves, state.bookshelves)
        assertEquals(mockBooks, state.books)
        assertEquals(false, state.isLoading)
    }
    
    @Test
    fun `onEvent BookShelfSelectEvent - updates selected bookshelf`() = runTest {
        // Arrange
        val bookshelf = Bookshelf(id = 2, name = "Non-Fiction")
        coEvery { bookRepository.getBooksByBookshelf(2) } returns emptyList()
        
        // Act
        viewModel.onEvent(LibraryHomeScreenEvent.BookShelfSelectEvent(bookshelf))
        
        // Assert
        assertEquals(bookshelf, viewModel.viewState.selectedBookshelf)
    }
    
    @Test
    fun `onEvent loadBooks - handles error gracefully`() = runTest {
        // Arrange
        coEvery { bookshelfRepository.getAllBookshelves() } throws IOException("Network error")
        
        // Act
        viewModel.onEvent(LibraryHomeScreenEvent.ScreenCreatedEvent)
        
        // Assert
        val state = viewModel.viewState
        assertEquals(false, state.isLoading)
        assertEquals("Network error", state.errorMessage)
    }
}
```

---

## Best Practices

### 1. Single Responsibility

Each ViewModel should handle only one feature:

```kotlin
// ✅ Good - Single feature
@HiltViewModel
class BookDetailViewModel @Inject constructor(
    private val bookRepository: BookRepository
) : ViewModel()

// ❌ Bad - Multiple responsibilities
@HiltViewModel
class AppViewModel @Inject constructor(
    private val bookRepository: BookRepository,
    private val authRepository: AuthRepository,
    private val settingsRepository: SettingsRepository,
    private val notificationManager: NotificationManager
) : ViewModel()
```

### 2. Use Type-Safe State

```kotlin
// ✅ Good - Type-safe with sealed classes
sealed class LoadingState {
    object Idle : LoadingState()
    object Loading : LoadingState()
    data class Success(val data: List<Book>) : LoadingState()
    data class Error(val exception: Exception) : LoadingState()
}

data class ViewState(val loadingState: LoadingState = LoadingState.Idle)

// ❌ Bad - Boolean flags are error-prone
data class ViewState(
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val isSuccess: Boolean = false
)
```

### 3. Avoid Memory Leaks

```kotlin
// ✅ Good - Uses viewModelScope
viewModelScope.launch {
    repository.observe().collect { data ->
        updateState { it.copy(data = data) }
    }
}

// ❌ Bad - Can cause memory leak
launch {
    repository.observe().collect { data ->
        updateState { it.copy(data = data) }
    }
}
```

### 4. Initialize State Properly

```kotlin
// ✅ Good - Provides sensible defaults
data class ViewState(
    val books: List<Book> = emptyList(),
    val isLoading: Boolean = false,
    val selectedBook: Book? = null
) : BaseViewState

// Use defaults
private val _state = HasStateImpl(ViewState())
```

### 5. Consistent Event Naming

```kotlin
// ✅ Good - Clear, consistent naming
sealed class BookEvent : BaseEvent {
    data object ScreenCreatedEvent : BookEvent()
    data object RefreshClicked : BookEvent()
    data class BookSelected(val bookId: Int) : BookEvent()
    data object DeleteConfirmed : BookEvent()
}

// ❌ Bad - Inconsistent naming
sealed class BookEvent : BaseEvent {
    object Init : BookEvent()
    object RefreshClick : BookEvent()
    data class Select(val id: Int) : BookEvent()
    object ConfirmDelete : BookEvent()
}
```

### 6. Document Complex State Changes

```kotlin
@HiltViewModel
class BookViewModel @Inject constructor(
    private val repository: BookRepository
) : ViewModel() {
    
    /**
     * Searches for books by title.
     * 
     * Updates the state with:
     * - isLoading: true while fetching
     * - searchResults: populated on success
     * - errorMessage: set on failure
     */
    private fun searchBooks(query: String) {
        viewModelScope.launch {
            updateState { it.copy(isLoading = true, errorMessage = "") }
            try {
                val results = repository.searchByTitle(query)
                updateState { it.copy(searchResults = results, isLoading = false) }
            } catch (e: Exception) {
                updateState { it.copy(errorMessage = e.message, isLoading = false) }
            }
        }
    }
}
```

---

## Troubleshooting

### Problem 1: State Not Updating in UI

**Symptom:** UI doesn't update when state changes.

**Solution:** Ensure you're using `observableViewState()`:
```kotlin
// ✅ Correct
val viewState by viewModel.observableViewState()

// ❌ Wrong
val viewState = viewModel.viewState  // Won't recompose on changes
```

### Problem 2: Memory Leak in ViewModel

**Symptom:** ViewModel is never garbage collected.

**Solution:** Use `viewModelScope` and handle cancellation:
```kotlin
// ✅ Correct
viewModelScope.launch {
    repository.observe().collect { }  // Auto-cancelled on clear
}

// ❌ Wrong
GlobalScope.launch {  // Never cancelled!
    repository.observe().collect { }
}
```

### Problem 3: Race Conditions

**Symptom:** State becomes inconsistent with multiple rapid events.

**Solution:** Use proper synchronization and cancellation:
```kotlin
private var loadJob: Job? = null

fun loadData() {
    loadJob?.cancel()  // Cancel previous
    loadJob = viewModelScope.launch {
        // Load data
    }
}
```

### Problem 4: State Loss on Configuration Change

**Symptom:** State resets when screen rotates.

**Solution:** ViewModel automatically retained - ensure you're not resetting state:
```kotlin
// ✅ Correct - State persists
@HiltViewModel
class MyViewModel : ViewModel() {
    private val _state = HasStateImpl(MyState())  // Persists
}

// ❌ Wrong - State reset on recompose
@Composable
fun MyScreen() {
    val state = remember { mutableStateOf(MyState()) }  // Lost on rotation
}
```

### Problem 5: Stale Data After Long Pause

**Symptom:** Data becomes outdated when app is backgrounded.

**Solution:** Add refresh logic on screen resumed:
```kotlin
override fun onEvent(event: Event) {
    when (event) {
        is Event.ScreenResumed -> refreshData()
    }
}

private fun refreshData() {
    viewModelScope.launch {
        val freshData = repository.getFreshData()
        updateState { it.copy(data = freshData) }
    }
}
```

---

## AI Agent Rules for ViewModel Generation

### RULE 1: MVI Pattern Is Mandatory
```kotlin
// ✅ CORRECT
@HiltViewModel
class MyViewModel @Inject constructor(
    private val repository: MyRepository
) : ViewModel(),
    HasState<MyState> by HasStateImpl(MyState()),
    HasEvents<MyEvent> {
    
    override fun onEvent(event: MyEvent) {
        when (event) {
            is MyEvent.Action1 -> handleAction1()
        }
    }
}

// ❌ REJECT - Missing MVI pattern
class MyViewModel : ViewModel() {
    val data = mutableStateOf(MyState())
}
```

### RULE 2: State Immutability Is Mandatory
```kotlin
// ✅ CORRECT
updateState { currentState ->
    currentState.copy(
        items = newItems,
        isLoading = false
    )
}

// ❌ REJECT - Mutable state modification
viewState.items = newItems
viewState.isLoading = false
```

### RULE 3: Use viewModelScope Only
```kotlin
// ✅ CORRECT
viewModelScope.launch {
    val data = repository.fetchData()
    updateState { it.copy(data = data) }
}

// ❌ REJECT - GlobalScope
GlobalScope.launch {
    val data = repository.fetchData()
}

// ❌ REJECT - launch without scope
launch {
    val data = repository.fetchData()
}
```

### RULE 4: Error Handling Is Required
```kotlin
// ✅ CORRECT
private fun loadData() {
    viewModelScope.launch {
        updateState { it.copy(isLoading = true) }
        try {
            val data = repository.getData()
            updateState { it.copy(data = data, isLoading = false) }
        } catch (e: Exception) {
            updateState { 
                it.copy(
                    errorMessage = e.message ?: "Error",
                    isLoading = false
                )
            }
        }
    }
}

// ❌ REJECT - No error handling
private fun loadData() {
    viewModelScope.launch {
        val data = repository.getData()
        updateState { it.copy(data = data) }
    }
}
```

### RULE 5: No Exposed Mutable State
```kotlin
// ✅ CORRECT
private val _customFlow = MutableSharedFlow<MyIntent>()
val customFlow: SharedFlow<MyIntent> = _customFlow

// ❌ REJECT - Exposed mutable state
val customFlow = MutableSharedFlow<MyIntent>()
```

### RULE 6: Events Have Clear Names
```kotlin
// ✅ CORRECT
sealed class MyEvent : BaseEvent {
    data object ScreenCreatedEvent : MyEvent()
    data class ItemSelectedEvent(val id: Int) : MyEvent()
    data object RefreshRequestedEvent : MyEvent()
}

// ❌ REJECT - Vague event names
sealed class MyEvent : BaseEvent {
    object Init : MyEvent()
    object Click : MyEvent()
    object Reload : MyEvent()
}
```

### RULE 7: State Has Sensible Defaults
```kotlin
// ✅ CORRECT
data class MyState(
    val items: List<Item> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String = "",
    val selectedItem: Item? = null
) : BaseViewState

// ❌ REJECT - No defaults
data class MyState(
    val items: List<Item>,
    val isLoading: Boolean,
    val errorMessage: String,
    val selectedItem: Item?
) : BaseViewState
```

### RULE 8: @HiltViewModel + @Inject
Every ViewModel MUST have these annotations:
```kotlin
// ✅ CORRECT
@HiltViewModel
class MyViewModel @Inject constructor(
    private val repository: Repository
) : ViewModel()

// ❌ REJECT - Missing annotations
class MyViewModel(repository: Repository) : ViewModel()
```

## ViewModel Checklist for AI Agents

**USE THIS BEFORE EVERY VIEWMODEL CODE SUBMISSION**

### Annotations & Signature
- [ ] Class annotated with `@HiltViewModel`
- [ ] Constructor annotated with `@Inject`
- [ ] Extends `ViewModel`
- [ ] Implements `HasState<T>` interface
- [ ] Implements `HasEvents<T>` interface

### State Management
- [ ] State defined in separate Contract file
- [ ] State is data class inheriting `BaseViewState`
- [ ] All state properties have default values
- [ ] State is immutable (all vals)
- [ ] Only public property is viewStateFlow
- [ ] updateState() used with immutable copy()

### Event Handling
- [ ] Events defined in Contract file
- [ ] All Events sealed class inheriting `BaseEvent`
- [ ] Uses data object/data class for events
- [ ] onEvent() method maps all events to handlers
- [ ] Clear event naming convention

### Async Operations
- [ ] Uses `viewModelScope.launch` for coroutines
- [ ] All suspend functions properly awaited
- [ ] Error handling with try-catch blocks
- [ ] Loading states properly managed
- [ ] CancellationException properly handled
- [ ] No GlobalScope usage

### Side Effects & Navigation
- [ ] IntentsFlow defined for side effects (if needed)
- [ ] IntentsFlow marked with underscore backing property
- [ ] Public intentsFlow is immutable
- [ ] Intents emitted for navigation/side effects

### Documentation & Testing
- [ ] KDoc for all public methods
- [ ] Complex logic has inline comments
- [ ] Unit tests exist for ViewModel
- [ ] Tests cover happy path and error cases
- [ ] Mock repositories used in tests

### Memory & Performance
- [ ] No mutable properties exposed
- [ ] No memory leaks (viewModelScope handles cleanup)
- [ ] Flows properly cancelled on clear()
- [ ] No infinite loops or recursive calls
- [ ] Reasonable number of state properties (< 15)


