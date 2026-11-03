# Composable UI Guide

**Project:** Erastos Book Library  
**Last Updated:** April 17, 2026  
**Status:** Active

---

## Table of Contents

1. [Overview](#overview)
2. [Composable Fundamentals](#composable-fundamentals)
3. [Anatomy of a Composable](#anatomy-of-a-composable)
4. [State Hoisting](#state-hoisting)
5. [Modifiers](#modifiers)
6. [Layouts](#layouts)
7. [Components](#components)
8. [Theme Integration](#theme-integration)
9. [Preview & Testing](#preview--testing)
10. [Performance Optimization](#performance-optimization)
11. [Best Practices](#best-practices)
12. [Common Patterns](#common-patterns)

---

## Overview

**Jetpack Compose** is a modern declarative UI framework for Android. Instead of imperative XML layouts, Compose uses Kotlin functions to describe UI.

### Key Principles

1. **Declarative** - Describe what UI should look like
2. **Composable** - Build UIs from small, reusable functions
3. **Reactive** - UI automatically updates when data changes
4. **Type-Safe** - Full Kotlin type safety at compile time

### Composable Function

```kotlin
@Composable
fun BookCard(book: Book) {
    Card {
        Column {
            Text(book.title)
            Text(book.author)
        }
    }
}
```

---

## Composable Fundamentals

### What is a Composable?

A **Composable** is a `@Composable` annotated function that:
- Returns `Unit` (displays UI, doesn't return data)
- Can be called multiple times and in any order
- Is recomposed when state changes
- Must follow specific rules (Compose Compiler enforces them)

```kotlin
@Composable
fun MyComposable() {
    // Describe UI here
}
```

### The Composable Lifecycle

```
Initial Composition
    ↓
Recomposition (when state changes)
    ↓
Disposal (when removed from composition)
```

### Remember and State

```kotlin
@Composable
fun Counter() {
    // State survives recomposition
    var count by remember { mutableStateOf(0) }
    
    Column {
        Text("Count: $count")
        Button(onClick = { count++ }) {
            Text("Increment")
        }
    }
}
```

### Side Effects

For operations that should run outside the normal composition:

```kotlin
@Composable
fun MyScreen() {
    // Runs once per composition
    LaunchedEffect(Unit) {
        // Initialization code
        loadData()
    }
    
    // Runs every time 'key' changes
    LaunchedEffect(itemId) {
        // Reload when itemId changes
        loadItem(itemId)
    }
}
```

---

## Anatomy of a Composable

### Typical Composable Structure

```kotlin
/**
 * Displays a book card with cover image, title, and author.
 *
 * @param book The book to display
 * @param onBookClick Callback when card is clicked
 * @param modifier Optional modifier for layout customization
 */
@Composable
fun BookCard(
    book: Book,
    onBookClick: (Book) -> Unit = {},
    modifier: Modifier = Modifier
) {
    // Extract theme values
    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography
    
    // Card container
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onBookClick(book) },
        colors = CardDefaults.cardColors(
            containerColor = colorScheme.surface
        )
    ) {
        // Card content
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Cover image
            AsyncImage(
                model = book.coverUrl,
                contentDescription = "Book cover",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentScale = ContentScale.Crop
            )
            
            // Title
            Text(
                text = book.title,
                style = typography.titleLarge,
                color = colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            
            // Author
            Text(
                text = book.author,
                style = typography.bodyMedium,
                color = colorScheme.onSurfaceVariant
            )
        }
    }
}
```

### Function Parameter Order

Follow this order for maximum clarity:

```kotlin
@Composable
fun Component(
    // Required data parameters
    data: String,
    number: Int,
    
    // Callback parameters
    onClick: () -> Unit = {},
    onItemClick: (Item) -> Unit = {},
    
    // Optional parameters with defaults
    isEnabled: Boolean = true,
    isSelected: Boolean = false,
    
    // Modifier should be last
    modifier: Modifier = Modifier
) {
    // Implementation
}
```

---

## State Hoisting

### What is State Hoisting?

State should live at the **lowest common ancestor** of components that need it.

```
Parent (owns state)
├── Child1 (reads state)
└── Child2 (reads state)
```

### Example: Correct State Hoisting

```kotlin
// ✅ Good - State at parent level
@Composable
fun LibraryScreen() {
    var selectedBookshelf by remember { mutableStateOf<Bookshelf?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    
    Column {
        SearchInput(
            query = searchQuery,
            onQueryChanged = { searchQuery = it }
        )
        
        BookshelfSelector(
            selectedBookshelf = selectedBookshelf,
            onBookshelfSelected = { selectedBookshelf = it }
        )
        
        BooksList(
            bookshelf = selectedBookshelf,
            searchQuery = searchQuery
        )
    }
}

@Composable
private fun SearchInput(
    query: String,
    onQueryChanged: (String) -> Unit
) {
    TextField(
        value = query,
        onValueChange = onQueryChanged
    )
}

@Composable
private fun BookshelfSelector(
    selectedBookshelf: Bookshelf?,
    onBookshelfSelected: (Bookshelf) -> Unit
) {
    // Display options, call callback
}

@Composable
private fun BooksList(
    bookshelf: Bookshelf?,
    searchQuery: String
) {
    // Filtered and displayed books
}
```

### Anti-Pattern: State in Child Composable

```kotlin
// ❌ Bad - State in child makes sharing difficult
@Composable
fun LibraryScreen() {
    BookshelfSelector()  // State is hidden inside
    BooksList()          // Can't share state
}

@Composable
private fun BookshelfSelector() {
    var selectedBookshelf by remember { mutableStateOf<Bookshelf?>(null) }
    // State trapped here, can't access from BooksList
}
```

---

## Modifiers

### What are Modifiers?

Modifiers describe how a Composable should appear and behave:

```kotlin
Text(
    text = "Hello",
    modifier = Modifier
        .padding(16.dp)
        .fillMaxWidth()
        .background(Color.Blue)
        .clickable { /* Handle click */ }
)
```

### Modifier Order Matters

```kotlin
// Order affects layout
Modifier
    .fillMaxWidth()           // Take full width
    .padding(16.dp)           // Add padding after width is set
    .background(Color.Gray)   // Background fills available space

// vs.

Modifier
    .padding(16.dp)           // Padding reduces available space
    .fillMaxWidth()           // Fills remaining width
    .background(Color.Gray)   // Background fills remaining space
```

### Common Modifiers

```kotlin
Text(
    text = "Hello",
    modifier = Modifier
        // Size
        .width(100.dp)
        .height(50.dp)
        .size(100.dp)
        .fillMaxWidth()
        .fillMaxHeight()
        .fillMaxSize()
        
        // Padding & Spacing
        .padding(16.dp)
        .padding(horizontal = 16.dp, vertical = 8.dp)
        
        // Alignment
        .align(Alignment.Center)
        .alignBy { it.measuredHeight / 2 }
        
        // Styling
        .background(Color.Gray)
        .border(1.dp, Color.Black)
        .clip(RoundedCornerShape(8.dp))
        .shadow(elevation = 4.dp)
        
        // Interaction
        .clickable { }
        .enabled(true)
        .alpha(0.5f)
        
        // Layout
        .weight(1f)
        .offset(x = 10.dp, y = 20.dp)
)
```

### Composing Modifiers

```kotlin
// Create reusable modifier combinations
fun Modifier.cardStyle() = this
    .fillMaxWidth()
    .padding(16.dp)
    .background(
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(8.dp)
    )
    .shadow(elevation = 4.dp)

// Usage
Card(modifier = Modifier.cardStyle()) {
    // Content
}
```

### Modifier Extension Pattern

```kotlin
@Composable
fun MyComponent(modifier: Modifier = Modifier) {
    // Add your own modifiers
    val componentModifier = modifier
        .fillMaxWidth()
        .padding(16.dp)
    
    Box(modifier = componentModifier) {
        // Content
    }
}

// Usage - can override with custom modifiers
MyComponent(modifier = Modifier.height(100.dp))
```

---

## Layouts

### Column (Vertical Layout)

```kotlin
@Composable
fun VerticalLayout() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text("First")
        Text("Second")
        Text("Third")
    }
}
```

### Row (Horizontal Layout)

```kotlin
@Composable
fun HorizontalLayout() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Filled.Book, contentDescription = null)
        Text("Book Title")
    }
}
```

### Box (Layering)

```kotlin
@Composable
fun LayeredLayout() {
    Box(
        modifier = Modifier
            .size(200.dp)
            .background(Color.Blue)
    ) {
        // Content stacks on top
        Icon(
            Icons.Filled.Star,
            contentDescription = null,
            modifier = Modifier.align(Alignment.TopEnd)
        )
        
        Text(
            "Center",
            modifier = Modifier.align(Alignment.Center)
        )
    }
}
```

### LazyColumn (Scrollable List)

```kotlin
@Composable
fun BookList(books: List<Book>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(
            items = books,
            key = { it.id }
        ) { book ->
            BookCard(book)
        }
    }
}
```

### LazyVerticalGrid (Grid Layout)

```kotlin
@Composable
fun BookGrid(books: List<Book>) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(books) { book ->
            BookCard(book)
        }
    }
}
```

### Arrangement

```kotlin
Row(
    horizontalArrangement = Arrangement.spacedBy(8.dp)  // Space between items
)

Row(
    horizontalArrangement = Arrangement.SpaceBetween   // Max space between items
)

Row(
    horizontalArrangement = Arrangement.Center         // Center items
)

Row(
    horizontalArrangement = Arrangement.Start          // Left-align items
)
```

---

## Components

### Text

```kotlin
Text(
    text = "Hello World",
    style = MaterialTheme.typography.headlineLarge,
    color = MaterialTheme.colorScheme.onBackground,
    fontSize = 24.sp,
    fontWeight = FontWeight.Bold,
    textAlign = TextAlign.Center,
    maxLines = 1,
    overflow = TextOverflow.Ellipsis,
    modifier = Modifier.padding(16.dp)
)
```

### Button

```kotlin
// Primary Button
Button(
    onClick = { /* Handle click */ },
    modifier = Modifier.fillMaxWidth(),
    colors = ButtonDefaults.buttonColors(
        containerColor = MaterialTheme.colorScheme.primary
    )
) {
    Text("Click Me")
}

// Outlined Button
OutlinedButton(
    onClick = { }
) {
    Text("Secondary")
}

// Text Button
TextButton(
    onClick = { }
) {
    Text("Tertiary")
}
```

### TextField

```kotlin
var text by remember { mutableStateOf("") }

TextField(
    value = text,
    onValueChange = { text = it },
    label = { Text("Enter text") },
    placeholder = { Text("Type here...") },
    modifier = Modifier.fillMaxWidth(),
    colors = TextFieldDefaults.colors(
        focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
        unfocusedContainerColor = MaterialTheme.colorScheme.surface
    ),
    singleLine = true,
    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
)
```

### Card

```kotlin
Card(
    modifier = Modifier
        .fillMaxWidth()
        .clickable { },
    colors = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.surface
    ),
    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Card Title")
        Text("Card content")
    }
}
```

### Surface

```kotlin
Surface(
    modifier = Modifier.fillMaxWidth(),
    color = MaterialTheme.colorScheme.surface,
    shadowElevation = 8.dp
) {
    Text("Content", modifier = Modifier.padding(16.dp))
}
```

### Icon

```kotlin
Icon(
    painter = painterResource(R.drawable.ic_book),
    contentDescription = "Book icon",
    tint = MaterialTheme.colorScheme.primary,
    modifier = Modifier.size(24.dp)
)
```

### Image/AsyncImage

```kotlin
// Local image
Image(
    painter = painterResource(R.drawable.book_cover),
    contentDescription = "Book cover",
    modifier = Modifier
        .size(200.dp)
        .clip(RoundedCornerShape(8.dp)),
    contentScale = ContentScale.Crop
)

// Remote image (using Coil)
AsyncImage(
    model = "https://example.com/book.jpg",
    contentDescription = "Book cover",
    modifier = Modifier.size(200.dp),
    contentScale = ContentScale.Crop,
    contentAlignment = Alignment.Center
)
```

---

## Theme Integration

### Using Theme Colors

```kotlin
@Composable
fun ThemedCard() {
    val colorScheme = MaterialTheme.colorScheme
    
    Card(
        colors = CardDefaults.cardColors(
            containerColor = colorScheme.surface,
            contentColor = colorScheme.onSurface
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Title",
                color = colorScheme.onSurface,
                style = MaterialTheme.typography.headlineSmall
            )
            
            Text(
                text = "Description",
                color = colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
```

### Custom Theme Composables

```kotlin
@Composable
fun ErrorMessage(
    text: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                MaterialTheme.colorScheme.errorContainer,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(16.dp)
    ) {
        Text(
            text = text,
            color = MaterialTheme.colorScheme.onErrorContainer,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

// Usage
ErrorMessage("Something went wrong!")
```

### Dimension Spacing

```kotlin
@Composable
fun SpacedLayout() {
    Column(
        modifier = Modifier.padding(dimension.screenPaddingHorizontal),
        verticalArrangement = Arrangement.spacedBy(dimension.componentSpacingMedium)
    ) {
        Text("Title")
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimension.paddingSmall)
        ) {
            Text("Content")
        }
    }
}
```

---

## Preview & Testing

### Basic Previews

```kotlin
@Preview(
    name = "Light Mode",
    showBackground = true
)
@Composable
fun BookCardPreview() {
    ErastosTheme {
        BookCard(
            book = Book(
                title = "The Great Gatsby",
                author = "F. Scott Fitzgerald",
                coverUrl = null
            ),
            onBookClick = {}
        )
    }
}
```

### Multiple Previews

```kotlin
@Preview(
    name = "Light Mode",
    showBackground = true,
    backgroundColor = 0xFFFFFFFF
)
@Preview(
    name = "Dark Mode",
    showBackground = true,
    backgroundColor = 0xFF000000,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun BookCardPreviews() {
    ErastosTheme {
        BookCard(
            book = Book(title = "Sample"),
            onBookClick = {}
        )
    }
}
```

### Device Previews

```kotlin
@Preview(device = Devices.PHONE)
@Preview(device = Devices.TABLET)
@Composable
fun ResponsivePreview() {
    ErastosTheme {
        MyScreen()
    }
}
```

### Custom Preview Annotation

```kotlin
@Preview(
    name = "Phone",
    device = Devices.PHONE,
    showSystemUi = true
)
@Preview(
    name = "Tablet",
    device = Devices.TABLET,
    showSystemUi = true
)
annotation class DevicePreviews

// Usage
@DevicePreviews
@Composable
fun MyScreenPreview() {
    ErastosTheme {
        MyScreen()
    }
}
```

---

## Performance Optimization

### Avoid Recomposition

```kotlin
// ❌ Bad - Function created on each recomposition
@Composable
fun MyList(items: List<String>) {
    LazyColumn {
        items(items) { item ->
            // This lambda is recreated every recomposition
            Text(item, modifier = Modifier.clickable { doSomething(item) })
        }
    }
}

// ✅ Good - Remember the lambda
@Composable
fun MyList(
    items: List<String>,
    onItemClick: (String) -> Unit
) {
    LazyColumn {
        items(items) { item ->
            Text(item, modifier = Modifier.clickable { onItemClick(item) })
        }
    }
}
```

### Use Keys for Lists

```kotlin
// ✅ Good - Provides stable identity
LazyColumn {
    items(
        items = books,
        key = { it.id }  // Stable key
    ) { book ->
        BookCard(book)
    }
}

// ❌ Bad - Index as key
LazyColumn {
    items(books.size) { index ->
        BookCard(books[index])  // Unstable
    }
}
```

### Lazy Computations

```kotlin
// ✅ Good - Computed lazily
@Composable
fun FilteredList(items: List<Book>, query: String) {
    val filtered = remember(items, query) {
        items.filter { it.title.contains(query, ignoreCase = true) }
    }
    
    LazyColumn {
        items(filtered) { book ->
            BookCard(book)
        }
    }
}
```

### Skipping Recomposition

```kotlin
// ✅ Good - Prevents unnecessary recompositions
@Composable
fun ParentScreen(viewModel: MyViewModel) {
    val books by viewModel.books.collectAsStateWithLifecycle()
    
    // This doesn't recompose when books change
    Row {
        UnrelatedContent()  // Not recomposed
        
        // Only this recomposes
        BookList(books = books)
    }
}
```

---

## Best Practices

### 1. Always Provide Modifier Default Parameter

```kotlin
// ✅ Good - Allows customization
@Composable
fun MyCard(title: String, modifier: Modifier = Modifier) {
    Card(modifier = modifier) {
        Text(title)
    }
}

// ❌ Bad - No customization possible
@Composable
fun MyCard(title: String) {
    Card {
        Text(title)
    }
}
```

### 2. Extract Large Composables

```kotlin
// ✅ Good - Smaller, reusable components
@Composable
fun BookScreen() {
    Column {
        BookHeader()
        BookContent()
        BookFooter()
    }
}

@Composable
private fun BookHeader() { }

@Composable
private fun BookContent() { }

@Composable
private fun BookFooter() { }
```

### 3. Use Semantic Names

```kotlin
// ✅ Good - Clear intent
@Composable
fun BookCard(book: Book) { }

@Composable
fun EmptyBooksPlaceholder() { }

@Composable
fun LoadingSpinner() { }

// ❌ Bad - Unclear purpose
@Composable
fun ItemWidget(item: Any) { }

@Composable
fun Box1() { }

@Composable
fun Circle() { }
```

### 4. Null-Safe Content

```kotlin
// ✅ Good - Handles null gracefully
@Composable
fun AuthorSection(author: Author?) {
    author?.let {
        Column {
            Text(it.name)
            Text(it.bio)
        }
    } ?: run {
        Text("No author information available")
    }
}
```

### 5. Document Complex Composables

```kotlin
/**
 * Displays a grid of books with pagination support.
 *
 * @param books List of books to display
 * @param onBookClick Callback when a book is selected
 * @param isLoading Whether books are being loaded
 * @param modifier Modifier for customization
 */
@Composable
fun BookGrid(
    books: List<Book>,
    onBookClick: (Book) -> Unit,
    isLoading: Boolean = false,
    modifier: Modifier = Modifier
) {
    // Implementation
}
```

---

## Common Patterns

### Loading State

```kotlin
@Composable
fun ContentWithLoading(
    isLoading: Boolean,
    content: @Composable () -> Unit
) {
    if (isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
        content()
    }
}

// Usage
ContentWithLoading(isLoading = viewState.isLoading) {
    BookList(books = viewState.books)
}
```

### Error Handling

```kotlin
@Composable
fun ContentWithError(
    errorMessage: String?,
    onRetry: () -> Unit,
    content: @Composable () -> Unit
) {
    if (errorMessage != null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(errorMessage)
            Button(onClick = onRetry) {
                Text("Retry")
            }
        }
    } else {
        content()
    }
}

// Usage
ContentWithError(
    errorMessage = viewState.errorMessage,
    onRetry = { viewModel.onEvent(RetryEvent) }
) {
    BookList(books = viewState.books)
}
```

### Empty State

```kotlin
@Composable
fun ContentWithEmptyState(
    items: List<Any>,
    emptyContent: @Composable () -> Unit,
    content: @Composable () -> Unit
) {
    if (items.isEmpty()) {
        emptyContent()
    } else {
        content()
    }
}

// Usage
ContentWithEmptyState(
    items = viewState.books,
    emptyContent = {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(Icons.Default.Book, contentDescription = null)
            Text("No books found")
        }
    }
) {
    BookList(books = viewState.books)
}
```

### Conditional Navigation

```kotlin
@Composable
fun MyScreen(navController: NavController) {
    val viewState by viewModel.observableViewState()
    
    // Handle navigation intents
    viewModel.subscribeForIntents { intent ->
        when (intent) {
            is MyIntent.NavigateToDetail -> {
                navController.navigate("detail/${intent.id}")
            }
        }
    }
    
    MyScreenContent(viewState = viewState)
}
```

---

## Composable UI Checklist

**USE THIS BEFORE EVERY CODE SUBMISSION**

### Required for Every Composable
- [ ] Has `@Composable` annotation
- [ ] Modifier parameter is LAST with default `Modifier`
- [ ] Uses theme colors ONLY (no hardcoded colors)
- [ ] All callbacks provided as parameters
- [ ] Proper KDoc documentation

### Screen Composables (`[Feature]Screen.kt`)
- [ ] Gets ViewModel with `hiltViewModel()`
- [ ] Observes state with `observableViewState()`
- [ ] Calls `doOnScreenCreate` for initialization events
- [ ] Subscribes to intents if navigation needed
- [ ] Extracts theme values at function start
- [ ] Has preview with `ErastosTheme` wrapper

### Component Composables (`components/[Component].kt`)
- [ ] Stateless (no internal state)
- [ ] All data comes from parameters
- [ ] All callbacks are parameters
- [ ] Small and reusable
- [ ] Has preview function

### Layout & Performance
- [ ] Large Composables split into smaller pieces
- [ ] State hoisted to correct parent level
- [ ] Keys used for list items (stable keys, not indices)
- [ ] `remember` used for expensive computations
- [ ] No anonymous functions in callbacks

### Testing & Documentation
- [ ] Preview function included
- [ ] Both light AND dark mode previews
- [ ] KDoc for all parameters
- [ ] Comments for complex logic

### Accessibility
- [ ] Content descriptions provided for icons/images
- [ ] Color contrast adequate (4.5:1 minimum)
- [ ] Interactive elements are large enough (48dp minimum)
- [ ] No color-only indicators (text labels too)

---

## AI Agent Rules for Composable Generation

### RULE 1: Theme Colors Are Mandatory
```kotlin
// ✅ CORRECT
Text(
    text = "Hello",
    color = MaterialTheme.colorScheme.onBackground,
    style = MaterialTheme.typography.bodyMedium
)

// ❌ REJECT - Hardcoded color
Text(
    text = "Hello",
    color = Color(0xFF1C1B1F),
    style = MaterialTheme.typography.bodyMedium
)
```

### RULE 2: Modifier Parameter Must Be Last
```kotlin
// ✅ CORRECT
@Composable
fun MyButton(
    text: String,
    onClick: () -> Unit = {},
    isEnabled: Boolean = true,
    modifier: Modifier = Modifier
)

// ❌ REJECT - Modifier not last
@Composable
fun MyButton(
    modifier: Modifier = Modifier,
    text: String,
    onClick: () -> Unit = {}
)
```

### RULE 3: No Internal State in Components
```kotlin
// ✅ CORRECT
@Composable
fun BookCard(
    book: Book,
    onBookClick: (Book) -> Unit,
    modifier: Modifier = Modifier
)

// ❌ REJECT - Has internal state
@Composable
fun BookCard(
    book: Book,
    modifier: Modifier = Modifier
) {
    var isSelected by remember { mutableStateOf(false) }
    // ...
}
```

### RULE 4: Previews Are Mandatory
Every public Composable MUST have at least one preview, screen Composables MUST have light AND dark mode previews.

```kotlin
// ✅ CORRECT
@Preview(name = "Light", showBackground = true)
@Preview(name = "Dark", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun MyScreenPreviews() {
    ErastosTheme {
        MyScreen()
    }
}
```

### RULE 5: List Items Must Have Stable Keys
```kotlin
// ✅ CORRECT
LazyColumn {
    items(books, key = { it.id }) { book ->
        BookCard(book)
    }
}

// ❌ REJECT - Index as key
LazyColumn {
    items(books.size) { index ->
        BookCard(books[index])
    }
}
```

### RULE 6: Documentation Required
Every Composable with parameters needs KDoc:
```kotlin
/**
 * Displays a book card with cover image, title, and author.
 *
 * @param book The book to display
 * @param onBookClick Callback when the card is clicked
 * @param modifier Modifier for layout customization
 */
@Composable
fun BookCard(
    book: Book,
    onBookClick: (Book) -> Unit = {},
    modifier: Modifier = Modifier
)
```


