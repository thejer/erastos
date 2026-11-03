# AI Agent Instructions - Erastos Project

**Purpose:** Complete reference guide for AI agents adding code to the Erastos Book Library project  
**Last Updated:** April 17, 2026  
**Status:** Active

---

## Table of Contents

1. [Quick Start for AI Agents](#quick-start-for-ai-agents)
2. [Architecture Overview](#architecture-overview)
3. [Adding a New Feature](#adding-a-new-feature)
4. [File Structure Requirements](#file-structure-requirements)
5. [Code Generation Checklists](#code-generation-checklists)
6. [Required Documentation](#required-documentation)
7. [Testing Requirements](#testing-requirements)
8. [Common Scenarios](#common-scenarios)

---

## Quick Start for AI Agents

### Before Making Any Code Changes

1. **Read the Context**
   - Review `GUIDELINES.md` - Overall project rules and conventions
   - Review `COLORS_AND_THEMING.md` - Theme and color system
   - Review `VIEWMODELS.md` - State management patterns
   - Review `COMPOSABLE_UI.md` - UI component guidelines
   - Review `AI_AGENT_INSTRUCTIONS.md` - This file

2. **Understand the Request**
   - Is this a new feature, bug fix, or refactor?
   - What files need to be created or modified?
   - Does this involve UI changes, business logic, or both?

3. **Plan the Implementation**
   - Identify all files that need to be created/modified
   - Map out the dependency flow
   - Check for conflicts with existing code

4. **Execute the Changes**
   - Follow the appropriate checklist for the type of change
   - Use exact conventions from the guidelines
   - Add all required tests and documentation

5. **Validate the Work**
   - Run through the final validation checklist
   - Ensure no breaking changes
   - Verify all imports and dependencies

---

## Architecture Overview

### Erastos MVI Architecture

```
USER INTERACTION (Composable Screen)
           ↓
    EVENT (User Action)
           ↓
  ViewModel.onEvent(event)
           ↓
   Process with Repository
           ↓
   updateState(reducer)
           ↓
  STATE UPDATED (StateFlow)
           ↓
   UI RECOMPOSED (Observes new state)
```

### Layered Architecture

```
PRESENTATION LAYER
├── Composables (UI screens & components)
├── ViewModels (State & event management)
└── Contracts (Events, Intents, State definitions)

DATA LAYER
├── Repositories (Business logic)
├── Local Data Source (Room Database)
└── Remote Data Source (Retrofit API)

NETWORK LAYER
├── API Service (Retrofit interface)
└── Network Models (DTOs)

DEPENDENCY INJECTION
└── Hilt Modules (Configuration & providers)
```

### Key Principles

✅ **MVI Pattern** - All UI state managed via MVI  
✅ **Immutability** - No mutable state in data classes  
✅ **Type Safety** - Sealed classes for Events/Intents  
✅ **Separation of Concerns** - Each layer has single responsibility  
✅ **Testability** - All business logic is testable  
✅ **Theme Integration** - All colors from Material theme  

---

## Adding a New Feature

### Step-by-Step Workflow

#### Phase 1: Planning
```
1. Create Contract file with Events, Intents, and State
2. Define data model if needed
3. Plan repository interface
4. Identify API endpoints (if remote)
```

#### Phase 2: Data Layer
```
1. Create/update Room Entity if needed
2. Create/update DAO if needed
3. Create Repository interface and implementation
4. Create network models (DTOs) if needed
5. Create API service endpoint if needed
```

#### Phase 3: Presentation Layer
```
1. Create ViewModel with MVI pattern
2. Implement event handling
3. Implement state management
4. Create main Composable Screen
5. Create UI components as needed
```

#### Phase 4: Integration
```
1. Add to navigation graph
2. Wire up Hilt dependencies
3. Add to parent screen navigation
```

#### Phase 5: Validation
```
1. Add unit tests
2. Add preview tests
3. Verify all code follows guidelines
4. Check for memory leaks
5. Test dark/light mode
```

### Complete Example: Adding "Book Ratings" Feature

#### 1. Create Contract (`presentation/BookRatingContract.kt`)
```kotlin
package com.karetlabs.erastos.home.presentation

sealed class BookRatingEvent : BaseEvent {
    data object ScreenCreatedEvent : BookRatingEvent()
    data class RatingChangedEvent(val bookId: Int, val rating: Float) : BookRatingEvent()
    data class SubmitRatingEvent(val bookId: Int, val rating: Float) : BookRatingEvent()
}

sealed class BookRatingIntent : BaseIntent {
    data object NavigateBack : BookRatingIntent()
    data class ShowSuccessMessage(val message: String) : BookRatingIntent()
}

data class BookRatingState(
    val book: Book? = null,
    val currentRating: Float = 0f,
    val isLoading: Boolean = false,
    val errorMessage: String = "",
    val isRatingSubmitted: Boolean = false
) : BaseViewState
```

#### 2. Update Data Model (`data/model/Book.kt`)
```kotlin
// Add rating field to Book entity
@Entity(tableName = "books")
data class Book(
    // ...existing fields...
    val rating: Float = 0f,
    val userRating: Float = 0f
)
```

#### 3. Create Repository (`repository/BookRepository.kt`)
```kotlin
interface BookRepository {
    suspend fun getBook(bookId: Int): Book
    suspend fun rateBook(bookId: Int, rating: Float): Boolean
}

class BookRepositoryImpl @Inject constructor(
    private val bookDao: BookDao,
    private val apiService: ApiService
) : BookRepository {
    override suspend fun getBook(bookId: Int): Book = bookDao.getBookById(bookId)
    
    override suspend fun rateBook(bookId: Int, rating: Float): Boolean {
        return try {
            apiService.submitRating(bookId, rating)
            bookDao.updateBookRating(bookId, rating)
            true
        } catch (e: Exception) {
            false
        }
    }
}
```

#### 4. Create ViewModel (`presentation/BookRatingViewModel.kt`)
```kotlin
@HiltViewModel
class BookRatingViewModel @Inject constructor(
    private val bookRepository: BookRepository,
    @Assisted private val bookId: Int
) : ViewModel(),
    HasState<BookRatingState> by HasStateImpl(BookRatingState()),
    HasEvents<BookRatingEvent> {
    
    private val _intentsFlow = MutableSharedFlow<BookRatingIntent>()
    val intentsFlow: SharedFlow<BookRatingIntent> = _intentsFlow
    
    override fun onEvent(event: BookRatingEvent) {
        when (event) {
            is BookRatingEvent.ScreenCreatedEvent -> loadBook()
            is BookRatingEvent.RatingChangedEvent -> updateRating(event.rating)
            is BookRatingEvent.SubmitRatingEvent -> submitRating(event.rating)
        }
    }
    
    private fun loadBook() {
        viewModelScope.launch {
            updateState { it.copy(isLoading = true) }
            try {
                val book = bookRepository.getBook(bookId)
                updateState { it.copy(book = book, currentRating = book.userRating, isLoading = false) }
            } catch (e: Exception) {
                updateState { it.copy(errorMessage = e.message ?: "Failed to load", isLoading = false) }
            }
        }
    }
    
    private fun updateRating(rating: Float) {
        updateState { it.copy(currentRating = rating) }
    }
    
    private fun submitRating(rating: Float) {
        viewModelScope.launch {
            updateState { it.copy(isLoading = true) }
            try {
                val success = bookRepository.rateBook(bookId, rating)
                if (success) {
                    updateState { it.copy(isRatingSubmitted = true, isLoading = false) }
                    _intentsFlow.emit(BookRatingIntent.ShowSuccessMessage("Rating submitted!"))
                    _intentsFlow.emit(BookRatingIntent.NavigateBack)
                } else {
                    updateState { it.copy(errorMessage = "Failed to submit", isLoading = false) }
                }
            } catch (e: Exception) {
                updateState { it.copy(errorMessage = e.message, isLoading = false) }
            }
        }
    }
}
```

#### 5. Create Screen (`BookRatingScreen.kt`)
```kotlin
@Composable
fun BookRatingScreen(
    bookId: Int,
    viewModel: BookRatingViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val viewState by viewModel.observableViewState()
    
    doOnScreenCreate {
        viewModel.onEvent(BookRatingEvent.ScreenCreatedEvent)
    }
    
    viewModel.subscribeForIntents { intent ->
        when (intent) {
            is BookRatingIntent.NavigateBack -> onNavigateBack()
            is BookRatingIntent.ShowSuccessMessage -> { /* Show toast */ }
        }
    }
    
    BookRatingView(viewState, viewModel)
}

@Composable
private fun BookRatingView(
    viewState: BookRatingState,
    viewModel: BookRatingViewModel
) {
    val colorScheme = MaterialTheme.colorScheme
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(dimension.screenPaddingHorizontal)
    ) {
        viewState.book?.let { book ->
            Text(book.title, style = MaterialTheme.typography.headlineMedium)
            
            RatingBar(
                rating = viewState.currentRating,
                onRatingChanged = { newRating ->
                    viewModel.onEvent(BookRatingEvent.RatingChangedEvent(book.id, newRating))
                }
            )
            
            Button(
                onClick = {
                    viewModel.onEvent(BookRatingEvent.SubmitRatingEvent(book.id, viewState.currentRating))
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Submit Rating")
            }
        } ?: run {
            CircularProgressIndicator()
        }
    }
}
```

#### 6. Create Component (`components/RatingBar.kt`)
```kotlin
@Composable
fun RatingBar(
    rating: Float,
    onRatingChanged: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        repeat(5) { index ->
            Icon(
                painter = painterResource(R.drawable.ic_star),
                contentDescription = null,
                tint = if (index < rating) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                modifier = Modifier
                    .size(32.dp)
                    .clickable { onRatingChanged((index + 1).toFloat()) }
            )
        }
    }
}
```

#### 7. Add Preview
```kotlin
@Preview
@Composable
fun BookRatingScreenPreview() {
    ErastosTheme {
        BookRatingView(
            viewState = BookRatingState(
                book = Book(id = 1, title = "Sample Book", bookshelfId = 1, addMode = "MANUAL")
            ),
            viewModel = mockViewModel()
        )
    }
}
```

---

## File Structure Requirements

### When Creating New Feature Files

Follow this exact structure:

```
app/src/main/java/com/karetlabs/erastos/[feature]/
├── [Feature]Screen.kt                    # Main Composable
├── components/                           # Reusable UI components
│   ├── [Component]View.kt
│   └── [Component].kt
└── presentation/                         # MVI Pattern
    ├── [Feature]Contract.kt              # Events, Intents, State
    ├── [Feature]ViewModel.kt             # State management
    └── ...other presentation files
```

### File Naming Rules

| Purpose | Pattern | Example |
|---------|---------|---------|
| Main Screen | `[Feature]Screen.kt` | `BookRatingScreen.kt` |
| Components | `[Component]View.kt` or `[Component].kt` | `RatingBar.kt`, `BookCardView.kt` |
| ViewModel | `[Feature]ViewModel.kt` | `BookRatingViewModel.kt` |
| Contract | `[Feature]Contract.kt` | `BookRatingContract.kt` |
| Repository | `[Entity]Repository.kt` | `BookRepository.kt` |
| DAO | `[Entity]Dao.kt` | `BookDao.kt` |
| Entity | `[Entity].kt` | `Book.kt` |

### Package Naming

```
com.karetlabs.erastos.[feature].[layer]

Examples:
com.karetlabs.erastos.home.presentation
com.karetlabs.erastos.library.ui.theme
com.karetlabs.erastos.data.db.daos
com.karetlabs.erastos.network
```

---

## Code Generation Checklists

### ✅ Creating a New ViewModel

When generating a ViewModel, ensure:

- [ ] File named `[Feature]ViewModel.kt` in `presentation/` folder
- [ ] Annotated with `@HiltViewModel`
- [ ] Constructor has `@Inject` annotation
- [ ] Implements `HasState<T>` interface
- [ ] Implements `HasEvents<T>` interface
- [ ] Contains `viewModelScope.launch` for async operations
- [ ] All state updates use immutable `copy()`
- [ ] Error handling with try-catch blocks
- [ ] Events mapped to handler functions
- [ ] Loading states properly managed
- [ ] No mutable public properties (only val)
- [ ] Documented with KDoc comments

**Template:**
```kotlin
@HiltViewModel
class [Feature]ViewModel @Inject constructor(
    private val repository: [Feature]Repository
) : ViewModel(),
    HasState<[Feature]State> by HasStateImpl([Feature]State()),
    HasEvents<[Feature]Event> {
    
    private val _intentsFlow = MutableSharedFlow<[Feature]Intent>()
    val intentsFlow: SharedFlow<[Feature]Intent> = _intentsFlow
    
    override fun onEvent(event: [Feature]Event) {
        when (event) {
            // Handle each event
        }
    }
}
```

### ✅ Creating a New Composable Screen

When generating a Composable Screen, ensure:

- [ ] File named `[Feature]Screen.kt` at package root
- [ ] Annotated with `@Composable`
- [ ] Uses `hiltViewModel()` for ViewModel injection
- [ ] Gets state with `observableViewState()`
- [ ] Calls `doOnScreenCreate` for initialization
- [ ] Subscribes to intents if needed
- [ ] Extracts theme values at top
- [ ] All callbacks properly wired to events
- [ ] Modifier parameters last with default
- [ ] Preview function included
- [ ] Loading, error, and empty states handled
- [ ] All colors from theme, not hardcoded

**Template:**
```kotlin
@Composable
fun [Feature]Screen(
    viewModel: [Feature]ViewModel = hiltViewModel()
) {
    val viewState by viewModel.observableViewState()
    
    doOnScreenCreate {
        viewModel.onEvent([Feature]Event.ScreenCreatedEvent)
    }
    
    viewModel.subscribeForIntents { intent ->
        when (intent) {
            // Handle intents
        }
    }
    
    [Feature]View(viewState = viewState, viewModel = viewModel)
}

@Composable
private fun [Feature]View(
    viewState: [Feature]State,
    viewModel: [Feature]ViewModel,
    modifier: Modifier = Modifier
) {
    val colorScheme = MaterialTheme.colorScheme
    
    // Implementation
}

@Preview
@Composable
fun [Feature]ScreenPreview() {
    ErastosTheme {
        [Feature]View(viewState = [Feature]State())
    }
}
```

### ✅ Creating Contract File

When generating Events, Intents, and State, ensure:

- [ ] File named `[Feature]Contract.kt` in `presentation/` folder
- [ ] All Events sealed class inheriting `BaseEvent`
- [ ] All Intents sealed class inheriting `BaseIntent`
- [ ] State data class inheriting `BaseViewState`
- [ ] All state properties have sensible defaults
- [ ] State immutable (all vals)
- [ ] Events use `data object` or `data class`
- [ ] Intents use `data object` or `data class`
- [ ] Enums for multi-state values
- [ ] KDoc for complex types

**Template:**
```kotlin
sealed class [Feature]Event : BaseEvent {
    data object ScreenCreatedEvent : [Feature]Event()
    data class ItemSelectedEvent(val item: T) : [Feature]Event()
}

sealed class [Feature]Intent : BaseIntent {
    data object NavigateBack : [Feature]Intent()
    data class NavigateTo(val id: Int) : [Feature]Intent()
}

data class [Feature]State(
    val items: List<T> = emptyList(),
    val selectedItem: T? = null,
    val isLoading: Boolean = false,
    val errorMessage: String = ""
) : BaseViewState
```

### ✅ Creating a UI Component

When generating a reusable Component, ensure:

- [ ] File in `components/` subfolder
- [ ] Named semantically: `[Component]View.kt` or `[Component].kt`
- [ ] Annotated with `@Composable`
- [ ] No internal state (stateless component)
- [ ] All callbacks provided as parameters
- [ ] Modifier parameter last with default
- [ ] Theme colors used, not hardcoded
- [ ] Content descriptions for accessibility
- [ ] KDoc with @param annotations
- [ ] Preview function included

**Template:**
```kotlin
/**
 * [Description]
 *
 * @param data The data to display
 * @param onClick Callback when clicked
 * @param modifier Optional modifier for layout
 */
@Composable
fun [Component](
    data: T,
    onClick: (T) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val colorScheme = MaterialTheme.colorScheme
    
    // Implementation
}

@Preview
@Composable
fun [Component]Preview() {
    ErastosTheme {
        [Component](data = sampleData)
    }
}
```

### ✅ Creating a Repository

When generating a Repository, ensure:

- [ ] Interface defined with clear contract
- [ ] Implementation annotated with `@Inject`
- [ ] All async operations use suspend
- [ ] Error handling with try-catch
- [ ] Proper separation: local vs remote data
- [ ] Dependencies injected in constructor
- [ ] No direct DAO access from outside repository
- [ ] Data mapping (DTO → Domain model)
- [ ] Clear method names

**Template:**
```kotlin
interface [Entity]Repository {
    suspend fun getAll(): List<[Entity]>
    suspend fun getById(id: Int): [Entity]
    suspend fun save(entity: [Entity]): Boolean
}

class [Entity]RepositoryImpl @Inject constructor(
    private val dao: [Entity]Dao,
    private val apiService: ApiService
) : [Entity]Repository {
    override suspend fun getAll(): List<[Entity]> = dao.getAll()
    
    override suspend fun getById(id: Int): [Entity] = dao.getById(id)
    
    override suspend fun save(entity: [Entity]): Boolean = try {
        dao.insert(entity)
        true
    } catch (e: Exception) {
        false
    }
}
```

---

## Required Documentation

### Every Feature Must Include

1. **KDoc Comments** - All public functions documented
```kotlin
/**
 * Searches for books by title or ISBN.
 *
 * @param query The search query string
 * @param searchType Type of search (TITLE or ISBN)
 * @return List of matching books
 * @throws IOException if network request fails
 */
suspend fun searchBooks(query: String, searchType: SearchType): List<Book>
```

2. **Inline Comments** - Complex logic explained
```kotlin
// Filter books to only show those matching the selected bookshelf
val filteredBooks = books.filter { book ->
    book.bookshelfId == selectedBookshelf.id
}
```

3. **File Header** - Optional but encouraged
```kotlin
/**
 * Book rating feature for allowing users to rate books.
 *
 * Uses MVI pattern with:
 * - BookRatingState for state management
 * - BookRatingEvent for user interactions
 * - BookRatingViewModel for business logic
 */
package com.karetlabs.erastos.home.presentation
```

### State Documentation Example
```kotlin
data class BookRatingState(
    /** List of all books available for rating */
    val books: List<Book> = emptyList(),
    
    /** Currently selected book being rated */
    val selectedBook: Book? = null,
    
    /** User's rating for the selected book (0-5) */
    val userRating: Float = 0f,
    
    /** Whether data is currently loading */
    val isLoading: Boolean = false,
    
    /** Error message if operation failed */
    val errorMessage: String = "",
    
    /** Whether the rating was successfully submitted */
    val isRatingSubmitted: Boolean = false
) : BaseViewState
```

---

## Testing Requirements

### Every Feature Must Have Tests

#### 1. ViewModel Unit Tests
```kotlin
class [Feature]ViewModelTest {
    
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()
    
    private lateinit var viewModel: [Feature]ViewModel
    private val repository: [Feature]Repository = mockk()
    
    @Before
    fun setUp() {
        viewModel = [Feature]ViewModel(repository)
    }
    
    @Test
    fun `onEvent ScreenCreatedEvent - loads data`() = runTest {
        // Arrange
        coEvery { repository.getData() } returns mockData
        
        // Act
        viewModel.onEvent([Feature]Event.ScreenCreatedEvent)
        
        // Assert
        assertEquals(mockData, viewModel.viewState.data)
    }
    
    @Test
    fun `onEvent - handles errors gracefully`() = runTest {
        // Arrange
        coEvery { repository.getData() } throws IOException("Network error")
        
        // Act
        viewModel.onEvent([Feature]Event.ScreenCreatedEvent)
        
        // Assert
        assertEquals("Network error", viewModel.viewState.errorMessage)
    }
}
```

#### 2. Composable Preview Tests
```kotlin
@Preview(name = "Light Mode", showBackground = true)
@Preview(name = "Dark Mode", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun [Feature]ScreenPreviews() {
    ErastosTheme {
        [Feature]View(viewState = [Feature]State())
    }
}
```

#### 3. Component Previews
```kotlin
@DevicePreviews
@Composable
fun [Component]Previews() {
    ErastosTheme {
        [Component](data = sampleData)
    }
}
```

---

## Common Scenarios

### Scenario 1: Adding a New Screen Tab

**Files to Create:**
1. `[Feature]Contract.kt` - Events, Intents, State
2. `[Feature]ViewModel.kt` - Business logic
3. `[Feature]Screen.kt` - UI implementation
4. `components/` - UI components

**Files to Modify:**
1. Navigation graph - Add route
2. Parent screen - Add tab

**Checklist:**
- [ ] Contract properly typed
- [ ] ViewModel follows MVI pattern
- [ ] Screen uses observableViewState()
- [ ] Theme colors used throughout
- [ ] Both light/dark mode previews
- [ ] Loading/error states handled
- [ ] Navigation wired correctly

### Scenario 2: Adding a Network API Endpoint

**Files to Create:**
1. Network DTOs in `network/data/`
2. API endpoint in `ApiService.kt`

**Files to Modify:**
1. Repository - Add method calling API
2. ViewModel - Handle API calls with proper error handling

**Checklist:**
- [ ] DTO classes properly annotated
- [ ] API method returns suspend function
- [ ] Repository maps DTO → Domain model
- [ ] Error handling in ViewModel
- [ ] Loading state managed
- [ ] Network timeout handling

### Scenario 3: Adding a Database Entity

**Files to Create:**
1. Entity in `data/model/`
2. DAO in `data/db/daos/`

**Files to Modify:**
1. `AppDatabase.kt` - Add entity
2. Existing repositories - Update if needed

**Checklist:**
- [ ] Entity annotated with `@Entity`
- [ ] PrimaryKey properly defined
- [ ] Foreign keys defined if needed
- [ ] DAO has suspend functions
- [ ] Database migration handled (version bump)
- [ ] DAO tested

### Scenario 4: Modifying Existing UI Component

**Process:**
1. Identify the component file
2. Review current implementation
3. Make changes following guidelines
4. Update/add previews
5. Ensure backward compatibility

**Checklist:**
- [ ] Modifier parameter not changed
- [ ] Callback signatures unchanged
- [ ] Default parameters preserved
- [ ] Theme colors updated if needed
- [ ] Previews reflect changes
- [ ] No breaking changes to API

---

## Validation Checklist Before Submission

### Code Quality
- [ ] All Kotlin style guidelines followed
- [ ] Naming conventions consistent
- [ ] No unused imports or variables
- [ ] No hardcoded strings (use resources)
- [ ] No hardcoded colors (use theme)
- [ ] No lateinit in data classes
- [ ] Proper null safety

### Architecture Compliance
- [ ] Correct file structure and locations
- [ ] MVI pattern properly implemented
- [ ] State is immutable
- [ ] Events properly sealed
- [ ] Repository handles all data access
- [ ] ViewModel uses viewModelScope
- [ ] Dependencies injected with Hilt

### UI & Theming
- [ ] All colors from MaterialTheme.colorScheme
- [ ] Typography from MaterialTheme.typography
- [ ] Dimensions from dimension object
- [ ] Modifier parameters follow order (data, callbacks, optional, modifier)
- [ ] All Composables have default Modifier parameter
- [ ] Content descriptions for accessibility

### Testing & Documentation
- [ ] Unit tests written for ViewModel
- [ ] Preview functions included
- [ ] Both light and dark mode previews
- [ ] KDoc comments added
- [ ] Complex logic explained with comments
- [ ] No TODO/FIXME without context

### Performance
- [ ] No unnecessary recompositions
- [ ] Keys used for list items
- [ ] Large Composables broken into pieces
- [ ] Remember used for expensive computations
- [ ] No memory leaks (viewModelScope used)

### Final Review
- [ ] Code builds without errors
- [ ] No lint warnings (or documented)
- [ ] All imports are correct
- [ ] Package structure correct
- [ ] File names follow conventions
- [ ] Ready for code review

---

## Quick Reference: Common Commands

```bash
# Build the project
./gradlew build

# Run tests
./gradlew test

# Format code
./gradlew spotlessApply

# Check for issues
./gradlew lint

# Clean build
./gradlew clean build
```

---

## When You're Unsure

1. **Check existing implementations** - Look at similar features already in the codebase
2. **Follow the patterns** - Use exact same structure as existing features
3. **Review the guidelines** - Refer back to GUIDELINES.md, VIEWMODELS.md, COMPOSABLE_UI.md
4. **Think MVI** - If in doubt about architecture, think "MVI pattern"
5. **Theme first** - Always use theme colors, never hardcode
6. **Test as you build** - Add tests alongside code

---

**These instructions are definitive. Follow them exactly when generating code for the Erastos project.**


