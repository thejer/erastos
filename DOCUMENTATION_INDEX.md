# 📚 Erastos Documentation Index

**For AI Agents & Developers**

---

## 🚀 Start Here

### If you're an AI agent adding code to this project:
1. **Start:** [`AI_AGENT_MASTER_REFERENCE.md`](./AI_AGENT_MASTER_REFERENCE.md) - Overview and quick reference
2. **Plan:** [`AI_AGENT_INSTRUCTIONS.md`](./AI_AGENT_INSTRUCTIONS.md) - Feature addition workflows
3. **Implement:** Refer to specialized guides below based on task type

### If you're a developer:
1. **Architecture:** [`GUIDELINES.md`](./GUIDELINES.md) - Full project guidelines
2. **Specific Areas:** Choose a guide below based on what you're working on

---

## 📖 Complete Documentation

### Core Documentation
| Document | Purpose | Audience |
|----------|---------|----------|
| [`GUIDELINES.md`](./GUIDELINES.md) | Project rules, conventions, and architecture | Everyone |
| [`AI_AGENT_MASTER_REFERENCE.md`](./AI_AGENT_MASTER_REFERENCE.md) | Quick reference and common scenarios | AI Agents |
| [`AI_AGENT_INSTRUCTIONS.md`](./AI_AGENT_INSTRUCTIONS.md) | Step-by-step workflows for feature addition | AI Agents |

### Technical Guides
| Document | Purpose | When to Use |
|----------|---------|------------|
| [`COLORS_AND_THEMING.md`](./COLORS_AND_THEMING.md) | Theme system and color specifications | UI development |
| [`VIEWMODELS.md`](./VIEWMODELS.md) | State management and MVI pattern | ViewModel/Business logic |
| [`COMPOSABLE_UI.md`](./COMPOSABLE_UI.md) | UI component creation guidelines | Composable/Screen development |

---

## 🎯 Quick Links by Task

### I want to...

**Add a new feature**
- Read: [`AI_AGENT_INSTRUCTIONS.md`](./AI_AGENT_INSTRUCTIONS.md#adding-a-new-feature)
- Template: Complete example provided in document

**Create a new Composable**
- Read: [`COMPOSABLE_UI.md`](./COMPOSABLE_UI.md)
- Checklist: Use the "AI Agent Rules" section

**Create a new ViewModel**
- Read: [`VIEWMODELS.md`](./VIEWMODELS.md)
- Template: Code generation checklist provided

**Update colors/theming**
- Read: [`COLORS_AND_THEMING.md`](./COLORS_AND_THEMING.md)
- Rules: Check "AI Agent Rules" section

**Fix a bug**
- Read: [`GUIDELINES.md`](./GUIDELINES.md#code-conventions)
- Then: Specific technical guide for the layer

**Understand the architecture**
- Read: [`GUIDELINES.md#architecture--patterns`](./GUIDELINES.md#architecture--patterns)
- Then: [`AI_AGENT_MASTER_REFERENCE.md#-learning-path-for-ai-agents`](./AI_AGENT_MASTER_REFERENCE.md#-learning-path-for-ai-agents)

**See examples**
- Read: [`AI_AGENT_INSTRUCTIONS.md#complete-example`](./AI_AGENT_INSTRUCTIONS.md#complete-example-adding-book-ratings-feature)
- Or: [`AI_AGENT_MASTER_REFERENCE.md#-fast-track-common-scenarios`](./AI_AGENT_MASTER_REFERENCE.md#-fast-track-common-scenarios)

---

## ✅ Pre-Submission Checklists

All checklists are embedded in the relevant guides:
- **Composable UI Checklist** → [`COMPOSABLE_UI.md`](./COMPOSABLE_UI.md#composable-ui-checklist)
- **ViewModel Checklist** → [`VIEWMODELS.md`](./VIEWMODELS.md#viewmodel-checklist-for-ai-agents)
- **Color & Theming Checklist** → [`COLORS_AND_THEMING.md`](./COLORS_AND_THEMING.md#color--theming-checklist-for-ai-agents)
- **Validation Checklist** → [`AI_AGENT_INSTRUCTIONS.md`](./AI_AGENT_INSTRUCTIONS.md#validation-checklist-before-submission)

---

## 🔴 Red Flags (Will Be Rejected)

See detailed list: [`AI_AGENT_MASTER_REFERENCE.md#-hard-rules-will-be-rejected`](./AI_AGENT_MASTER_REFERENCE.md#-hard-rules-will-be-rejected)

**Quick summary:**
- ❌ Hardcoded colors
- ❌ Hardcoded padding/spacing
- ❌ GlobalScope launch
- ❌ Mutable state in components
- ❌ No error handling
- ❌ Wrong file location
- ❌ Missing previews
- ❌ No theme wrapping

---

## 📋 Directory Structure

```
/erastos
├── README.md (this file)
├── GUIDELINES.md (main rules)
├── AI_AGENT_MASTER_REFERENCE.md (AI quick ref)
├── AI_AGENT_INSTRUCTIONS.md (workflows)
├── COLORS_AND_THEMING.md (theme guide)
├── VIEWMODELS.md (state management)
├── COMPOSABLE_UI.md (UI components)
│
└── app/src/main/java/com/karetlabs/erastos/
    ├── [feature]/ (feature modules)
    ├── data/ (data layer)
    ├── network/ (api layer)
    ├── di/ (dependency injection)
    └── library/ (shared utilities)
```

---

## 📞 Common Questions

**Q: Where do I put new files?**
A: See [`GUIDELINES.md#directory-structure`](./GUIDELINES.md#directory-structure)

**Q: How do I name files?**
A: See [`GUIDELINES.md#naming-conventions`](./GUIDELINES.md#naming-conventions)

**Q: What's the MVI pattern?**
A: See [`GUIDELINES.md#mvi-architecture-rules`](./GUIDELINES.md#mvi-architecture-rules) or [`VIEWMODELS.md`](./VIEWMODELS.md)

**Q: How do I use theme colors?**
A: See [`COLORS_AND_THEMING.md#using-colors-in-compose`](./COLORS_AND_THEMING.md#using-colors-in-compose)

**Q: What's state hoisting?**
A: See [`COMPOSABLE_UI.md#state-hoisting`](./COMPOSABLE_UI.md#state-hoisting)

**Q: How do I handle errors?**
A: See [`VIEWMODELS.md#error-handling`](./VIEWMODELS.md#error-handling)

---

## 🎓 Learning Resources

### For Understanding Compose
- [Jetpack Compose Documentation](https://developer.android.com/jetpack/compose)
- [Material 3 Design System](https://m3.material.io/)
- Examples in project: `app/src/main/java/com/karetlabs/erastos/`

### For Understanding MVI Pattern
- Examples in project: Look for `*Contract.kt` and `*ViewModel.kt` files
- Reference: [`VIEWMODELS.md`](./VIEWMODELS.md)
- Pattern diagram: [`AI_AGENT_MASTER_REFERENCE.md`](./AI_AGENT_MASTER_REFERENCE.md#step-2-understand-the-flow)

### For Understanding Hilt
- [Hilt Documentation](https://developer.android.com/training/dependency-injection/hilt-android)
- Project setup: `app/src/main/java/com/karetlabs/erastos/di/`

---

## 📈 Code Generation Workflow

```
┌─────────────────────────┐
│  Read Master Reference  │
└────────┬────────────────┘
         │
         ↓
┌─────────────────────────┐
│  Understand the Task    │
└────────┬────────────────┘
         │
         ↓
┌─────────────────────────┐
│  Plan Implementation    │
│ (Read AI_AGENT_INSTR...)│
└────────┬────────────────┘
         │
         ↓
┌─────────────────────────┐
│  Generate Code          │
│ (Use specific guides)   │
└────────┬────────────────┘
         │
         ↓
┌─────────────────────────┐
│  Run Checklists         │
└────────┬────────────────┘
         │
         ↓
┌─────────────────────────┐
│  Submit for Review      │
└─────────────────────────┘
```

---

## ⚡ Pro Tips

1. **Copy existing patterns** - Find similar code in the project and follow its structure
2. **Use templates** - All document have code templates, use them as starting points
3. **Test as you code** - Use previews to verify UI code immediately
4. **Read error messages** - Kotlin compiler is helpful, read what it tells you
5. **Follow the theme** - If you're confused about colors/sizes, use the theme system
6. **Keep it simple** - Smaller files and functions are easier to maintain
7. **Document your code** - Add KDoc comments, especially for complex logic
8. **Run checklists** - Use the provided checklists before submitting

---

## 📊 Documentation Stats

| Document | Lines | Focus |
|----------|-------|-------|
| GUIDELINES.md | 1000+ | Complete project guidelines |
| AI_AGENT_MASTER_REFERENCE.md | 400+ | AI quick reference |
| AI_AGENT_INSTRUCTIONS.md | 600+ | Detailed workflows |
| COLORS_AND_THEMING.md | 500+ | Theme system |
| VIEWMODELS.md | 600+ | State management |
| COMPOSABLE_UI.md | 1300+ | UI components |

**Total:** 4,400+ lines of comprehensive documentation

---

## 🚀 Getting Started Checklist

- [ ] Read [`AI_AGENT_MASTER_REFERENCE.md`](./AI_AGENT_MASTER_REFERENCE.md)
- [ ] Understand the project structure (see README above)
- [ ] Know the MVI pattern (see quick explanation in Master Reference)
- [ ] Bookmark this index for quick access
- [ ] Read the specific guide for your task
- [ ] Use the provided code templates
- [ ] Run the checklist before submitting

---

## 📞 Need Help?

**Architecture Question** → [`GUIDELINES.md#architecture--patterns`](./GUIDELINES.md#architecture--patterns)

**Code Generation Question** → [`AI_AGENT_INSTRUCTIONS.md`](./AI_AGENT_INSTRUCTIONS.md)

**UI Question** → [`COMPOSABLE_UI.md`](./COMPOSABLE_UI.md)

**State Management Question** → [`VIEWMODELS.md`](./VIEWMODELS.md)

**Theme Question** → [`COLORS_AND_THEMING.md`](./COLORS_AND_THEMING.md)

**Stuck?** → [`AI_AGENT_MASTER_REFERENCE.md#-when-youre-stuck`](./AI_AGENT_MASTER_REFERENCE.md#-when-youre-stuck)

---

**Version:** 1.0  
**Last Updated:** April 17, 2026  
**Status:** Active & Maintained


