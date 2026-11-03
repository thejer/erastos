# Erastos - Project Guidelines & Rules

**Project:** Book Library Management Application  
**Language:** Kotlin  
**Platform:** Android (API 26+)  
**Compiled Target SDK:** 36  
**Architecture:** MVI (Model-View-Intent) with Jetpack Compose  

---

## Table of Contents

1. [Project Overview](#project-overview)
2. [Architecture & Patterns](#architecture--patterns)
3. [Directory Structure](#directory-structure)
4. [Code Conventions](#code-conventions)
5. [Naming Conventions](#naming-conventions)
6. [Kotlin Style Guidelines](#kotlin-style-guidelines)
7. [MVI Architecture Rules](#mvi-architecture-rules)
8. [Compose UI Guidelines](#compose-ui-guidelines)
9. [Dependency Injection](#dependency-injection)
10. [Database & Data Persistence](#database--data-persistence)
11. [Network & API](#network--api)
12. [Testing Guidelines](#testing-guidelines)
13. [Git & Commit Practices](#git--commit-practices)

---

## Project Overview

**Erastos** is a comprehensive book library management application for Android that enables users to:
- Manage personal book collections across multiple bookshelves
- Search for books by title or ISBN
- Scan ISBN codes using device camera
- Add books manually to the library
- Organize books into custom bookshelves

### Tech Stack

| Component | Technology | Version |
|-----------|-----------|---------|
| Build System | Gradle | 8.13.2 |
| Language | Kotlin | 2.1.0 |
| UI Framework | Jetpack Compose | 2025.12.00 |
| Navigation | Compose Navigation | 2.9.6 |
| Dependency Injection | Hilt | 2.57.2 |
| Code Generation | KSP | 2.0.21-1.0.27 |
| Database | Room | 2.8.4 |
| Networking | Retrofit | 3.0.0 |
| Image Loading | Coil | 3.3.0 |
| Camera | CameraX | 1.5.2 |
| Barcode Scanning | ML Kit | 17.3.0 |
| Serialization | Kotlinx Serialization | 1.9.0 |
| JVM Target | Java 17 | 17 |

---

## Architecture & Patterns

### MVI (Model-View-Intent) Architecture

Erastos follows the **MVI (Model-View-Intent)** unidirectional data flow pattern:

```
User Interaction
    ↓
Event (UI action captured)
    ↓
ViewModel (process event)
    ↓
Intent (optional navigation intent)
    ↓
State Update (new view state)
    ↓
View Rendered (UI reflects state)
```

### Component Layers

1. **Presentation Layer** (UI & State Management)
   - Composables (screens and components)
   - ViewModels
   - State, Events, and Intents
   - Contract files

2. **Data Layer** (Business Logic & Persistence)
   - Repositories
   - DAOs (Data Access Objects)
   - Models and Entities
   - Data sources

3. **Network Layer**
   - API service interfaces
   - Network models
   - Retrofit configuration

4. **Dependency Injection Layer**
   - Hilt modules
   - Singleton providers
   - Dependency configurations

---

## Directory Structure

```
app/
├── src/main/java/com/karetlabs/erastos/
│   ├── ErastosApplication.kt                 # Application entry point with Hilt setup
│   ├── MainActivity.kt                       # Main activity hosting Compose navigation
│   ├── camera/                               # Camera and barcode scanning features
│   │   ├── presentation/
│   │   └── ...camera-related files
│   ├── data/                                 # Data layer (models, database, repositories)
│   │   ├── db/                              # Room database setup and DAOs
│   │   │   ├── AppDatabase.kt
│   │   │   └── daos/                        # Data Access Objects
│   │   ├── model/                           # Entity models
│   │   │   ├── Book.kt
│   │   │   └── Bookshelf.kt
│   │   ├── source/                          # Data sources (local/remote)
│   │   └── repository/                      # Repository implementations
│   ├── di/                                  # Dependency Injection
│   │   └── AppModule.kt                    # Hilt modules & providers
│   ├── home/                                # Home/Library screen feature
│   │   ├── LibraryHomeScreen.kt            # Main Composable
│   │   ├── components/                      # Reusable UI components
│   │   │   ├── BookCardItemView.kt
│   │   │   ├── AddBookBottomSheetContent.kt
│   │   │   └── ...
│   │   └── presentation/                    # MVI pattern
│   │       ├── LibraryHomeViewModel.kt     # State & event management
│   │       ├── LibraryHomeContract.kt      # Events, Intents, State definitions
│   │       └── ...
│   ├── library/                             # Shared utilities & base classes
│   │   ├── presentation/                    # Shared presentation logic
│   │   │   ├── mvi/                        # MVI base classes
│   │   │   │   ├── Events.kt               # BaseEvent interface
│   │   │   │   ├── Intents.kt              # BaseIntent & HasIntents
│   │   │   │   └── State.kt                # BaseViewState & HasState
│   │   │   └── ...composable helpers
│   │   ├── ui/                             # Shared UI components & theme
│   │   │   ├── components/                 # Reusable UI components
│   │   │   ├── theme/                      # Material theme, typography, dimensions
│   │   │   └── ...
│   │   └── ...utilities
│   ├── nav/                                # Navigation setup
│   │   └── ...navigation graphs
│   └── network/                            # Network layer
│       ├── ApiService.kt                  # Retrofit service interface
│       ├── data/                          # Network DTOs/models
│       └── ...interceptors/converters
├── build.gradle.kts                        # App-level Gradle configuration
└── ...other gradle files
```

---

## Code Conventions

### General Principles

1. **Single Responsibility Principle (SRP)**
   - Each class/function should have one reason to change
   - Files should focus on one feature or concept

2. **DRY (Don't Repeat Yourself)**
   - Extract common logic into utility functions
   - Create reusable components for repeated UI patterns

3. **SOLID Principles**
   - Dependency inversion through Hilt
   - Interface-based abstractions for data sources
   - Immutable data models

4. **Immutability**
   - Use data classes with val properties
   - Leverage Kotlin's copy() function for state updates
   - Use sealed classes for type-safe unions

---

## Naming Conventions

### Files & Classes

| Type | Convention | Example |
|------|-----------|---------|
| **Screens** | `[Feature]Screen.kt` | `LibraryHomeScreen.kt` |
| **ViewModels** | `[Feature]ViewModel.kt` | `LibraryHomeViewModel.kt` |
| **Contracts** | `[Feature]Contract.kt` | `LibraryHomeContract.kt` |
| **Components** | `[Component]View.kt` or `[Component].kt` | `BookCardItemView.kt` |
| **Database** | `[Entity]Entity.kt` or just entity as is | `Book.kt`, `Bookshelf.kt` |
| **DAO** | `[Entity]Dao.kt` | `BookDao.kt` |
| **Repository** | `[Feature]Repository.kt` or `[Entity]Repository.kt` | `BookRepository.kt` |
| **Events** | `[Feature]ScreenEvent.kt` or sealed in Contract | `LibraryHomeScreenEvent` |
| **Intents** | `[Feature]ScreenIntent.kt` or sealed in Contract | `LibraryHomeScreenIntent` |
| **State** | `[Feature]ViewState.kt` or in Contract | `HomeScreenViewState` |
| **API Service** | `[Domain]Service.kt` or `ApiService.kt` | `ApiService.kt` |
| **Modules** | `[Domain]Module.kt` | `AppModule.kt` |

### Variables & Properties

- **CamelCase** for all variables and properties
- **Private properties**: prefix with underscore `_` for backing properties
- **Constants**: `UPPER_SNAKE_CASE` in companion objects
- **Flow/StateFlow**: use descriptive names with `Flow` suffix

```kotlin
// Good examples
val bookList: List<Book> = emptyList()
var selectedBookshelf: Bookshelf = Bookshelf()
private val _viewState = MutableStateFlow(initialState)
val viewState: StateFlow<State> = _viewState.asStateFlow()
```

### Functions & Methods

- **CamelCase** starting with lowercase
- **Event handlers**: `on[Event]` pattern
- **Boolean getters**: prefix with `is`, `has`, `should`, `can`
- **Composables**: lowercase for internal/private, CamelCase for public

```kotlin
// Good examples
fun onAddBookClick() { }
fun onSearchQueryChanged(query: String) { }
fun shouldShowBottomSheet(): Boolean { }
fun isBookAvailable(): Boolean { }

@Composable
fun LibraryHomeScreen() { }  // Public

@Composable
private fun BooksGrid() { }  // Private
```

### Enums & Sealed Classes

- **PascalCase** for enum values
- **PascalCase** for sealed class subclasses
- Use descriptive names

```kotlin
enum class BottomSheetType {
    ERROR, ADD_BOOK, SCAN_ISBN, NEW_BOOK_SEARCH, NONE
}

sealed class LibraryHomeScreenEvent : BaseEvent {
    data object ScreenCreatedEvent : LibraryHomeScreenEvent()
    data class BookShelfSelectEvent(val bookshelf: Bookshelf) : LibraryHomeScreenEvent()
}
```

---

## Kotlin Style Guidelines

### Class & Object Declarations

```kotlin
// Use data classes for models with automatic equals(), hashCode(), toString()
@Entity(tableName = "books")
data class Book(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val authors: List<String>,
    // properties ordered: required first, then optional
)

// Use sealed classes for type-safe enums
sealed class LibraryHomeScreenEvent : BaseEvent {
    data object ScreenCreatedEvent : LibraryHomeScreenEvent()
    data class BookShelfSelectEvent(val bookshelf: Bookshelf) : LibraryHomeScreenEvent()
}

// Use object for singletons and modules
@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    // provides...
}
```

### Properties & Variables

```kotlin
// Prefer val over var - immutability is safer
val immutableList: List<Book> = emptyList()

// Use explicit types for public APIs
fun getBooks(): List<Book> = repository.getAllBooks()

// Leverage Kotlin's type inference for local variables
val books = repository.getAllBooks()

// Use backing properties for properties with logic
private val _selectedBook = MutableStateFlow<Book?>(null)
val selectedBook: StateFlow<Book?> = _selectedBook.asStateFlow()
```

### Functions & Methods

```kotlin
// Prefer expression bodies for simple functions
fun isValidIsbn(isbn: String): Boolean = isbn.length == 13

// Use named parameters for clarity
repository.addBook(
    book = newBook,
    bookshelfId = selectedBookshelfId
)

// Extension functions for common operations
fun <T> List<T>.isNotEmpty(): Boolean = this.isNotEmpty()
```

### Collections

```kotlin
// Use immutable types in public APIs
fun getBooks(): List<Book> = _books.value  // not MutableList

// Use appropriate collection operations
val titles = books.map { it.title }
val availableBooks = books.filter { it.isAvailable }
val groupedByAuthor = books.groupBy { it.authors.firstOrNull() }

// Avoid mutable collections in data classes
data class Book(
    val id: Int,
    val authors: List<String>,  // immutable
)
```

### Null Safety

```kotlin
// Use safe call operators
val firstAuthor = book.authors.firstOrNull()

// Use elvis operator for defaults
val publisher = book.publisher ?: "Unknown"

// Use let for non-null checks
book?.let { 
    updateBook(it)
}

// Avoid !! operator - use requireNotNull for explicit failures
val requiredValue = book.coverUrl ?: error("Cover URL is required")
```

---

## MVI Architecture Rules

### 1. Sealed Class Hierarchy for Type Safety

All Events, Intents, and States must inherit from their base types:

```kotlin
sealed class LibraryHomeScreenEvent : BaseEvent {
    data object ScreenCreatedEvent : LibraryHomeScreenEvent()
    data class BookShelfSelectEvent(val bookshelf: Bookshelf) : LibraryHomeScreenEvent()
    data object AddBookClickEvent : LibraryHomeScreenEvent()
}

sealed class LibraryHomeScreenIntent : BaseIntent {
    data class BookShelfSelectIntent(val bookshelf: Bookshelf) : LibraryHomeScreenIntent()
}

data class HomeScreenViewState(
    val bookshelves: List<Bookshelf> = listOf(Bookshelf(name = "All")),
    val books: List<Book> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String = ""
) : BaseViewState
```

### 2. Event Handling in Composables

Events originate from user interactions and are captured in the Composable:

```kotlin
@Composable
fun LibraryHomeScreen(
    viewModel: LibraryHomeViewModel = hiltViewModel()
) {
    val viewState by viewModel.observableViewState()
    
    // Initialize screen
    doOnScreenCreate {
        viewModel.onEvent(ScreenCreatedEvent)
    }
    
    // Handle user interactions
    onBookshelfSelected = {
        viewModel.onEvent(BookShelfSelectEvent(it))
    }
}
```

### 3. ViewModel Processes Events and Updates State

The ViewModel receives events and updates the ViewState using a reducer pattern:

```kotlin
class LibraryHomeViewModel : ViewModel(), HasState<HomeScreenViewState>, HasEvents<LibraryHomeScreenEvent> {
    
    override fun onEvent(event: LibraryHomeScreenEvent) {
        when (event) {
            is ScreenCreatedEvent -> loadBookshelves()
            is BookShelfSelectEvent -> selectBookshelf(event.bookshelf)
            is AddBookClickEvent -> openAddBookBottomSheet()
            // ...
        }
    }
    
    private fun selectBookshelf(bookshelf: Bookshelf) {
        updateState { 
            it.copy(
                selectedBookshelf = bookshelf,
                books = filterBooksByBookshelf(bookshelf)
            )
        }
    }
}
```

### 4. State is Observable and Immutable

Use StateFlow for reactive state updates:

```kotlin
// In ViewModel
private val _state = MutableStateFlow(initialState)
override val viewStateFlow: StateFlow<HomeScreenViewState> = _state.asStateFlow()

override fun updateState(reducer: (HomeScreenViewState) -> HomeScreenViewState) {
    viewState = reducer(viewState)
}

// In Composable - always immutable
val viewState by viewModel.observableViewState()  // Read-only
```

### 5. Intents for Navigation & Side Effects

Intents are sent from ViewModel to coordinate navigation or side effects:

```kotlin
// Define in Contract
sealed class LibraryHomeScreenIntent : BaseIntent {
    data class NavigateToBookDetail(val bookId: Int) : LibraryHomeScreenIntent()
}

// Subscribe in Composable
viewModel.subscribeForIntents { intent ->
    when (intent) {
        is NavigateToBookDetail -> navController.navigate("detail/${intent.bookId}")
    }
}
```

### 6. Contract File Organization

Always define Events, Intents, and State in a single Contract file:

```kotlin
// File: LibraryHomeContract.kt
sealed class LibraryHomeScreenEvent : BaseEvent { /* ... */ }
sealed class LibraryHomeScreenIntent : BaseIntent { /* ... */ }
data class HomeScreenViewState(...) : BaseViewState
enum class BottomSheetType { /* ... */ }
enum class SearchType { /* ... */ }
```

---

## Compose UI Guidelines

### 1. Composable Function Naming & Organization

```kotlin
// Public screens use PascalCase
@Composable
fun LibraryHomeScreen(viewModel: LibraryHomeViewModel = hiltViewModel()) {
    // Screen-level logic
}

// Private components use camelCase or PascalCase consistently
@Composable
private fun HomeView(/* params */) {
    // View implementation
}

// Reusable components go in components/ folder
@Composable
fun BookCardItemView(book: Book, modifier: Modifier = Modifier) {
    // Reusable component
}
```

### 2. State Hoisting Pattern

```kotlin
// Good: State lifted to parent
@Composable
fun LibraryHomeScreen() {
    val viewState by viewModel.observableViewState()
    HomeView(
        books = viewState.books,
        onBookClick = { viewModel.onEvent(BookClickEvent(it)) }
    )
}

// Avoid: State in composed function
@Composable
fun HomeView(books: List<Book>) {
    var selectedBook by remember { mutableStateOf<Book?>(null) }  // Avoid
}
```

### 3. Modifier Parameter Placement

Always place Modifier as the last parameter with default:

```kotlin
@Composable
fun BookCardItemView(
    book: Book,
    onBookClick: (Book) -> Unit = {},
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier) {
        // content
    }
}
```

### 4. Theme & Dimensions

Use theme values from the centralized theme module:

```kotlin
@Composable
fun HomeTopBar() {
    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography
    
    Text(
        text = "Title",
        style = typography.headlineLarge,
        color = colorScheme.onBackground,
        modifier = Modifier.padding(dimension.screenPaddingHorizontal)
    )
}

// Dimensions available via: com.karetlabs.erastos.library.ui.theme.dimension
```

### 5. Preview Patterns

Always provide previews for Composables:

```kotlin
@DevicePreviews
@Composable
fun BookCardPreview() {
    ErastosTheme {
        BookCardItemView(
            book = previewBooks().first(),
            onBookClick = {}
        )
    }
}

@Composable
private fun previewBooks(): List<Book> = listOf(
    Book(title = "The Great Gatsby", /* ... */),
    // ...
)
```

### 6. Bottom Sheets & Dialogs

Centralized in `ErastosBottomSheet` component:

```kotlin
ErastosBottomSheet(
    visible = viewState.shouldOpenBottomSheet,
    onDismissRequest = { viewModel.onEvent(DismissBottomSheetEvent) }
) {
    when (viewState.bottomSheetType) {
        ADD_BOOK -> AddBookBottomSheetContent(/* ... */)
        SCAN_ISBN -> IsbnScanCameraScreen(/* ... */)
        NEW_BOOK_SEARCH -> BooksSearchResultSheetContent(/* ... */)
        ERROR -> ErrorBottomSheetContent(/* ... */)
        NONE -> Unit
    }
}
```

---

## Dependency Injection

### Hilt Configuration

1. **Application Setup**
   ```kotlin
   @HiltAndroidApp
   class ErastosApplication : Application()
   ```

2. **Module Providers** - All dependencies defined in `AppModule.kt`:
   ```kotlin
   @Module
   @InstallIn(SingletonComponent::class)
   object AppModule {
       
       @Provides
       @Singleton
       fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
           return AppDatabase.getDatabase(context)
       }
       
       @Provides
       @Singleton
       fun provideApiService(retrofit: Retrofit): ApiService {
           return retrofit.create(ApiService::class.java)
       }
   }
   ```

3. **ViewModels** - Annotated with `@HiltViewModel`:
   ```kotlin
   @HiltViewModel
   class LibraryHomeViewModel @Inject constructor(
       private val bookRepository: BookRepository,
       private val bookshelfRepository: BookshelfRepository
   ) : ViewModel(), HasState<HomeScreenViewState>, HasEvents<LibraryHomeScreenEvent> {
       // implementation
   }
   ```

4. **Injection in Composables**:
   ```kotlin
   val viewModel: LibraryHomeViewModel = hiltViewModel()
   ```

### Singleton Scope Rules

- Database instances should be Singleton
- Network clients (Retrofit) should be Singleton
- Repository instances should be Singleton
- ViewModels are scoped to Activity lifecycle
- Use appropriate scopes: `SingletonComponent`, `ActivityComponent`, `ViewModelComponent`

---

## Database & Data Persistence

### Room Configuration

1. **Entity Models** - Located in `data/model/`:
   ```kotlin
   @Entity(tableName = "books")
   data class Book(
       @PrimaryKey(autoGenerate = true)
       val id: Int = 0,
       val title: String,
       val authors: List<String>,
       val bookshelfId: Int  // Foreign key
   )
   ```

2. **Data Access Objects (DAOs)** - Located in `data/db/daos/`:
   ```kotlin
   @Dao
   interface BookDao {
       @Insert
       suspend fun insertBook(book: Book)
       
       @Query("SELECT * FROM books WHERE bookshelfId = :bookshelfId")
       suspend fun getBooksByBookshelf(bookshelfId: Int): List<Book>
       
       @Delete
       suspend fun deleteBook(book: Book)
   }
   ```

3. **Database Class** - Located in `data/db/`:
   ```kotlin
   @Database(entities = [Book::class, Bookshelf::class], version = 1)
   abstract class AppDatabase : RoomDatabase() {
       abstract fun bookDao(): BookDao
       abstract fun bookshelfDao(): BookshelfDao
       
       companion object {
           fun getDatabase(context: Context): AppDatabase =
               Room.databaseBuilder(context, AppDatabase::class.java, "erastos_db")
                   .fallbackToDestructiveMigration()
                   .build()
       }
   }
   ```

### Repository Pattern

Repositories abstract data sources and provide a clean API:

```kotlin
class BookRepository @Inject constructor(
    private val bookDao: BookDao,
    private val apiService: ApiService
) {
    suspend fun getAllBooks(): List<Book> = bookDao.getAllBooks()
    
    suspend fun searchBooks(query: String): List<Book> {
        val apiResults = apiService.searchBooks(query)
        return apiResults.map { it.toBook() }
    }
}
```

---

## Network & API

### Retrofit Service

Located in `network/ApiService.kt`:

```kotlin
interface ApiService {
    @GET("volumes")
    suspend fun searchBooks(
        @Query("q") query: String
    ): BooksSearchResponse
    
    @GET("volumes/{volumeId}")
    suspend fun getBookDetail(
        @Path("volumeId") volumeId: String
    ): Volume
}
```

### Network Models

Use separate models for network responses, then map to domain models:

```kotlin
// API response model
data class Volume(
    val id: String,
    val volumeInfo: VolumeInfo
)

// Mapper function
fun Volume.toBook(): Book = Book(
    title = volumeInfo.title,
    authors = volumeInfo.authors ?: emptyList(),
    // ... other mappings
)
```

### Serialization

Project uses Kotlinx Serialization with Gson converter:

```gradle
implementation(libs.kotlinx.serialization)
implementation(libs.converter.gson)
```

---

## Testing Guidelines

### Unit Testing

- Location: `app/src/test/java/`
- Framework: JUnit 4
- Use descriptive test names: `testFunctionName_WhenCondition_ThenExpectation()`

```kotlin
class LibraryHomeViewModelTest {
    
    @Test
    fun onScreenCreated_LoadsBookshelves_AndUpdatesState() {
        // Arrange
        val viewModel = LibraryHomeViewModel(mockRepository)
        
        // Act
        viewModel.onEvent(ScreenCreatedEvent)
        
        // Assert
        assertEquals(expectedBookshelves, viewModel.viewState.bookshelves)
    }
}
```

### Instrumentation Testing

- Location: `app/src/androidTest/java/`
- Framework: Espresso + Compose UI Testing

```kotlin
@RunWith(AndroidJUnit4::class)
class LibraryHomeScreenTest {
    
    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()
    
    @Test
    fun displaysBooksWhenLoaded() {
        composeTestRule.setContent {
            ErastosTheme {
                LibraryHomeScreen()
            }
        }
        composeTestRule.onNodeWithText("Book Title").assertIsDisplayed()
    }
}
```

---

## Git & Commit Practices

### Commit Message Format

Follow conventional commits:

```
<type>(<scope>): <subject>

<body>

<footer>
```

**Types:**
- `feat`: New feature
- `fix`: Bug fix
- `refactor`: Code refactoring
- `docs`: Documentation
- `style`: Formatting/style changes
- `test`: Adding/updating tests
- `chore`: Build process/dependencies
- `ci`: CI/CD changes

**Examples:**
```
feat(home): add book filtering by bookshelf
fix(camera): resolve barcode scanning crash on older devices
refactor(mvi): extract state update logic into helper function
docs(guidelines): add Compose UI guidelines
```

### Branch Naming

```
feature/add-book-search
bugfix/barcode-scanning-crash
refactor/database-architecture
docs/update-guidelines
```

### Pull Request Guidelines

1. Descriptive title and description
2. Link related issues
3. Include screenshots for UI changes
4. Ensure all tests pass
5. Request appropriate reviewers
6. Keep PRs focused and reasonably sized

---

## Best Practices Checklist

### Before Committing Code

- [ ] Code follows all naming conventions
- [ ] Sealed classes used for type safety
- [ ] MVI pattern correctly implemented
- [ ] State is immutable (use data classes)
- [ ] No lateinit properties in data classes
- [ ] Null safety properly handled
- [ ] Resource strings used instead of hardcoded text
- [ ] Proper Modifier parameter placement in Composables
- [ ] Theme colors/dimensions used consistently
- [ ] No TODO or FIXME comments without context
- [ ] Appropriate error handling
- [ ] Logging added where necessary

### Before Opening PR

- [ ] All tests pass locally
- [ ] No unused imports or variables
- [ ] Code formatted consistently
- [ ] Lint warnings resolved
- [ ] Documentation updated if needed
- [ ] Commit messages follow conventions
- [ ] Branch is up to date with main

---

## Common Patterns & Examples

### Adding a New Feature (Step by Step)

1. **Create Contract** (`presentation/[Feature]Contract.kt`):
   ```kotlin
   sealed class NewFeatureEvent : BaseEvent { /* ... */ }
   sealed class NewFeatureIntent : BaseIntent { /* ... */ }
   data class NewFeatureState(...) : BaseViewState
   ```

2. **Create ViewModel** (`presentation/[Feature]ViewModel.kt`):
   ```kotlin
   @HiltViewModel
   class NewFeatureViewModel @Inject constructor(
       private val repository: Repository
   ) : ViewModel(), HasState<NewFeatureState>, HasEvents<NewFeatureEvent> { /* ... */ }
   ```

3. **Create Screen** (`[Feature]Screen.kt`):
   ```kotlin
   @Composable
   fun NewFeatureScreen(viewModel: NewFeatureViewModel = hiltViewModel()) { /* ... */ }
   ```

4. **Create Components** (if needed) (`components/[Component].kt`):
   ```kotlin
   @Composable
   fun ComponentView(modifier: Modifier = Modifier) { /* ... */ }
   ```

### Handling Async Operations

```kotlin
class BookViewModel @Inject constructor(
    private val repository: BookRepository
) : ViewModel() {
    
    private fun loadBooks() {
        viewModelScope.launch {
            updateState { it.copy(isLoading = true) }
            try {
                val books = repository.getAllBooks()
                updateState { 
                    it.copy(
                        books = books,
                        isLoading = false
                    )
                }
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
}
```

---

## Version History

| Version | Date | Changes |
|---------|------|---------|
| 1.0 | 2026-04-17 | Initial guidelines document creation |

---

## Questions & Support

For questions about these guidelines or project architecture:
1. Consult the inline documentation in source files
2. Review existing implementations as examples
3. Reach out to the team lead or architecture team

---

**Last Updated:** April 17, 2026  
**Maintained By:** Development Team  
**Status:** Active

