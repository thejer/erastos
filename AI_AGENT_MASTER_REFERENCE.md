# AI Agent Master Reference - Erastos Project

**Version:** 1.0  
**Created:** April 17, 2026  
**Purpose:** Single source of truth for AI agents generating code for Erastos

---

## 📚 Documentation Structure

### Foundational Documents
1. **GUIDELINES.md** - Overall project architecture, rules, and conventions
2. **AI_AGENT_INSTRUCTIONS.md** - Step-by-step workflows for adding features
3. **This document** - Quick reference and coordination guide

### Specialized Guides  
4. **COLORS_AND_THEMING.md** - Theme system and color specifications
5. **VIEWMODELS.md** - State management and MVI pattern
6. **COMPOSABLE_UI.md** - UI component creation guidelines

---

## 🎯 Before You Start Any Task

### Step 1: Read the Right Documents
```
Is this a NEW FEATURE?
  → Read: AI_AGENT_INSTRUCTIONS.md (Section: Adding a New Feature)
  → Then: GUIDELINES.md (Architecture & Patterns)

Is this UI CODE?
  → Read: COMPOSABLE_UI.md (All sections)
  → Then: COLORS_AND_THEMING.md

Is this STATE MANAGEMENT?
  → Read: VIEWMODELS.md (All sections)
  → Then: GUIDELINES.md (MVI Architecture)

Is this a BUG FIX?
  → Read: GUIDELINES.md (Code Conventions)
  → Then: Check specific guide for the layer (UI/VM/Data)

Is this a REFACTOR?
  → Read: GUIDELINES.md (Best Practices)
  → Then: All specialized guides relevant to the code
```

### Step 2: Understand the Flow
```
USER INTERACTION
    ↓
Composable calls onEvent()
    ↓
ViewModel.onEvent() dispatches to handler
    ↓
Handler calls repository method
    ↓
Repository returns data
    ↓
ViewModel updates state with updateState()
    ↓
StateFlow emits new state
    ↓
Composable receives new state
    ↓
UI recomposes
```

### Step 3: Know Your Files

**For ANY new feature, you need:**
```
Feature Name: [Feature]

Files to CREATE:
├── presentation/
│   ├── [Feature]Contract.kt (Events, Intents, State)
│   └── [Feature]ViewModel.kt (Business logic)
├── [Feature]Screen.kt (Main Composable)
├── components/ (optional)
│   └── [Component].kt (Reusable UI pieces)
└── (optional based on needs)
    ├── data/model/[Entity].kt (Database entity)
    ├── data/db/daos/[Entity]Dao.kt (Database access)
    └── repository/[Entity]Repository.kt (Business layer)

Files to MODIFY:
├── navigation/ (Add routes)
├── di/AppModule.kt (Add dependencies)
└── Parent screens (Add navigation)
```

---

## ✅ Universal Checklist

### Before Writing Any Code
- [ ] I have read all relevant documentation
- [ ] I understand the MVI pattern
- [ ] I know what files need to be created
- [ ] I know what files need to be modified
- [ ] I have a plan for the implementation

### During Code Generation
- [ ] Following exact file structure
- [ ] Using correct naming conventions
- [ ] Implementing MVI pattern correctly
- [ ] Using theme colors (NO hardcoded colors)
- [ ] All state immutable (data classes)
- [ ] All async in viewModelScope
- [ ] Error handling present
- [ ] Documentation/comments added

### After Code Generation
- [ ] All files created in correct location
- [ ] All files follow naming conventions
- [ ] All imports correct and minimal
- [ ] No TODO/FIXME without context
- [ ] Tests/previews included
- [ ] Dark mode preview included
- [ ] Code compiles
- [ ] Ready for review

---

## 🔴 Hard Rules (Will Be Rejected)

### UI Code
❌ **Hardcoded Color** - `Color(0xFF1C1B1F)` instead of `MaterialTheme.colorScheme.onBackground`  
❌ **Manual Padding** - `16.dp` instead of `dimension.screenPaddingHorizontal`  
❌ **No Theme Wrap** - Preview without `ErastosTheme { }`  
❌ **Modifier Not Last** - `fun MyComponent(modifier: Modifier = Modifier, data: String)`  
❌ **No Modifier Default** - `fun MyComponent(modifier: Modifier)` (no default)  
❌ **State in Component** - `remember { mutableStateOf() }` in reusable components  
❌ **No Preview** - Composable without `@Preview`  
❌ **Inconsistent Theme** - Different light/dark previews or missing one  

### ViewModel Code
❌ **No MVI Pattern** - ViewModel without `HasState` and `HasEvents`  
❌ **Mutable State** - `var state = mutableStateOf()` instead of `updateState()`  
❌ **GlobalScope** - `GlobalScope.launch { }` instead of `viewModelScope.launch { }`  
❌ **No Error Handling** - No try-catch in async operations  
❌ **Exposed Mutable** - `val _state = MutableStateFlow()` public instead of private  
❌ **No Defaults** - State properties without default values  
❌ **Wrong Scope** - Using `launch { }` without scope  

### General Code
❌ **Hardcoded Strings** - `"Hello"` instead of `stringResource(R.string.hello)`  
❌ **Wrong Location** - File in wrong package/folder  
❌ **Wrong Naming** - Not following conventions  
❌ **No Documentation** - No KDoc for public functions  
❌ **Unused Imports** - Leftover imports from code removal  
❌ **Bad Package Names** - Package names that don't match structure  

---

## 🟢 Fast Track: Common Scenarios

### Scenario A: Add New Screen Tab
**Time: 15 minutes | Complexity: Medium**

Files needed:
1. `[Feature]Contract.kt` - Copy from GUIDELINES.md Contract section
2. `[Feature]ViewModel.kt` - Copy from VIEWMODELS.md template
3. `[Feature]Screen.kt` - Copy from COMPOSABLE_UI.md template
4. Update navigation graph

Checklist:
- [ ] Contract has Events, Intents, State
- [ ] ViewModel implements MVI pattern
- [ ] Screen has observable state and preview
- [ ] Navigation wired correctly
- [ ] Theme colors used throughout

### Scenario B: Add UI Component to Existing Screen
**Time: 10 minutes | Complexity: Low**

Files needed:
1. `components/[Component].kt` - New reusable component

Checklist:
- [ ] Component is stateless
- [ ] All data comes from parameters
- [ ] Modifier parameter last with default
- [ ] Theme colors used
- [ ] Preview included (light + dark)
- [ ] KDoc added

### Scenario C: Add New API Endpoint
**Time: 20 minutes | Complexity: Medium**

Files needed:
1. Network DTO models in `network/data/`
2. API method in `network/ApiService.kt`
3. Repository method update

Checklist:
- [ ] DTOs properly annotated
- [ ] API method is suspend
- [ ] Error handling in calling code
- [ ] Loading state managed
- [ ] Data properly mapped to domain models

### Scenario D: Add Database Entity
**Time: 15 minutes | Complexity: Medium**

Files needed:
1. Entity in `data/model/`
2. DAO in `data/db/daos/`
3. Database version bump in `AppDatabase.kt`

Checklist:
- [ ] Entity annotated with `@Entity`
- [ ] Primary key defined
- [ ] Foreign keys if needed
- [ ] DAO has suspend functions
- [ ] Database version incremented

### Scenario E: Fix UI Bug
**Time: 5-10 minutes | Complexity: Low**

Process:
1. Find the Composable file
2. Identify the issue
3. Apply fix following COMPOSABLE_UI.md rules
4. Test in preview (light + dark)
5. Verify theme colors still correct

---

## 🎓 Learning Path for AI Agents

### New to Erastos?
1. Read `GUIDELINES.md` completely
2. Read `AI_AGENT_INSTRUCTIONS.md` (Architecture Overview section)
3. Try a simple task (Scenario D or E above)
4. Review the code you generated against checklists

### Building a Feature?
1. Read `AI_AGENT_INSTRUCTIONS.md` (Adding a New Feature)
2. Follow the step-by-step workflow
3. Use the provided code templates
4. Run through validation checklist before submission

### Encountering Errors?
1. Check if it's on the "Hard Rules" list
2. Read the specific guide for that code type
3. Look at existing similar code in project
4. Apply the fix following the patterns

---

## 📋 Quick Command Reference

```bash
# Build project
./gradlew build

# Run all tests
./gradlew test

# Run specific test
./gradlew test --tests MyViewModelTest

# Lint checks
./gradlew lint

# Format code (if available)
./gradlew spotlessApply

# Clean and rebuild
./gradlew clean build

# Check dependencies
./gradlew dependencies
```

---

## 🤝 Integration Points

### Where to Add Code
**UI Layer** → `app/src/main/java/com/karetlabs/erastos/[feature]/`  
**Data Layer** → `app/src/main/java/com/karetlabs/erastos/data/`  
**Network Layer** → `app/src/main/java/com/karetlabs/erastos/network/`  
**DI Setup** → `app/src/main/java/com/karetlabs/erastos/di/`  
**Tests** → `app/src/test/java/com/karetlabs/erastos/[feature]/`  

### Dependency Versions
Check `gradle/libs.versions.toml` for all versions (Compose, Material, Kotlin, etc.)

### Navigation
Update `app/src/main/java/com/karetlabs/erastos/nav/` for navigation graphs

---

## ⚠️ Common Mistakes & Fixes

### Mistake: Hardcoded color
```kotlin
// ❌ Wrong
Text("Hello", color = Color(0xFF1C1B1F))

// ✅ Fix
Text("Hello", color = MaterialTheme.colorScheme.onBackground)
```

### Mistake: Padding value
```kotlin
// ❌ Wrong
Box(modifier = Modifier.padding(16.dp))

// ✅ Fix
Box(modifier = Modifier.padding(dimension.paddingMedium))
```

### Mistake: State in component
```kotlin
// ❌ Wrong
@Composable
fun MyCard(data: String) {
    var clicked by remember { mutableStateOf(false) }
}

// ✅ Fix
@Composable
fun MyCard(
    data: String,
    onClicked: () -> Unit,
    modifier: Modifier = Modifier
)
```

### Mistake: GlobalScope launch
```kotlin
// ❌ Wrong
GlobalScope.launch {
    val data = repository.getData()
}

// ✅ Fix
viewModelScope.launch {
    val data = repository.getData()
    updateState { it.copy(data = data) }
}
```

### Mistake: No error handling
```kotlin
// ❌ Wrong
val data = repository.getData()
updateState { it.copy(data = data) }

// ✅ Fix
try {
    val data = repository.getData()
    updateState { it.copy(data = data) }
} catch (e: Exception) {
    updateState { it.copy(error = e.message) }
}
```

### Mistake: Modifier position
```kotlin
// ❌ Wrong
@Composable
fun MyComponent(modifier: Modifier = Modifier, title: String)

// ✅ Fix
@Composable
fun MyComponent(
    title: String,
    modifier: Modifier = Modifier
)
```

---

## 🔍 Code Review Criteria

Your generated code will be evaluated on:

1. **Correctness** - Does it work as intended?
2. **Architecture** - Does it follow MVI pattern?
3. **Consistency** - Does it match project conventions?
4. **Readability** - Is it easy to understand?
5. **Maintainability** - Is it easy to modify?
6. **Testability** - Is it properly tested?
7. **Performance** - Is it efficient?
8. **Accessibility** - Is it accessible to all users?

---

## 📞 When You're Stuck

1. **Check existing code** - Find similar implementation in project
2. **Review the guides** - Re-read the relevant markdown file
3. **Follow the pattern** - Copy structure from existing feature
4. **Read the rules** - Make sure you're not violating hard rules
5. **Test & validate** - Use the checklists to verify

---

## 🎯 Golden Rule

**"When in doubt, find similar code in the project and follow its pattern."**

The Erastos codebase is internally consistent. If you find code doing something differently than the guidelines suggest, the guidelines are correct - follow them anyway. Consistency is more important than any individual implementation detail.

---

**Last Updated:** April 17, 2026  
**Maintained By:** Development Team  
**For Questions:** Refer to specific guide documents above


