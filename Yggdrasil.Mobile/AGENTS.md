## Project overview

- Tech stack: Kotlin, Compose Multiplatform, Material3 UI
- Target: Android & iOS, single‑code‑base app.

---
## Coding standards

### Kotlin style
- Prefer `val` over `var` except where mutation is clearly intentional.
- Use explicit types for all variables
- Use `suspend` for async functions; avoid blocking calls in `viewModelScope`/`coroutineScope`.
- Prefer `State`/`MutableState` and `remember` for UI state; avoid global mutable state.

### Compose / UI
- Use `@Composable` functions with stable parameters where possible.
- Prefer small, reusable composables rather than huge `screen` lambdas.
- Use `Modifier` chains; avoid deeply nested `Box`/`Column` trees when a simpler layout exists.
- Use semantic `contentDescription` for interactive icons.

### Testing
- Prefer unit tests for business logic and data layers.
- Prefer instrumented / integration tests for UI‑logic helpers, not every screen.
- New features should be covered by at least one unit test.

---
## Project structure
- `core.yggdrasil.*` → shared business logic, data models, and domain code.
- `core.yggdrasil.content` → shared UI composables, theming, navigation helpers.
- `core.yggdrasil.viewmodels` → ViewModel classes that encapsulate UI state, handle business logic coordination, and expose state and actions to composables.
- `core.yggdrasil.views` → Composable UI entry points (e.g., screens, fragments‑like views) that wire together `content` and `viewmodels` without duplicating behavior.

Place new modules under the appropriate package.

---

## How to handle changes

1. **Read the existing implementation** of the file and related screens before editing.
2. **Prefer small, focused diffs** over rewriting entire files.
3. **Update tests** alongside business‑logic changes.
4. **Never remove unused code without confirming it isn’t imported elsewhere.**

---

## Do / Don’t

### Do

- Use `viewModelScope.launch { ... }` or `coroutineScope.launch { ... }` for background work.
- Follow the MVVM architecture. Create ViewModels per screen, navigation destination, or major UI component—scoped to their lifecycle for state survival during config changes/recompositions. Use viewModel() composable to auto-instantiate and manage them.
- Leverage `Flow`/`StateFlow` for streaming data and reactive UI.
- Prefer sealed hierarchies for domain state (e.g., `State.Loading`, `Success`, `Error`).
- Use descriptive function and parameter names; avoid abbreviations.
- Use the AppColors and Dimensions objects in core.yggdrasil.ui if you need colors and dimensions for components.

### Don’t

- Don’t hard‑code colors, strings, or dimensions in composable functions.
- Don’t import platform‑specific code into `core.yggdrasil.*` unless behind an interface.
- Don’t introduce third‑party dependencies.
- Don’t leave `TODO` without a brief description or issue reference.

### When to create ViewModels
- One per screen/route: LoginScreen, VideoPlayerScreen, DownloadsListScreen
- Navigation-scoped: When using Compose Navigation (NavHostController)
- Reusable components: Complex dialogs/modals with independent state (rare)
- Shared state: App-wide (e.g., AuthViewModel) via rememberViewModelStoreOwner

---

## Safety and permissions

- Allowed: reading files, editing source code, updating tests and docs.
- Ask first before:
    - deleting files or entire directories.
    - changing dependencies in `build.gradle.kts` or `dependencies` blocks.

---