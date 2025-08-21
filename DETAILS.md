# NexusCart Technical Details

A deep dive into NexusCart’s hybrid architecture, security model, networking, paging, error handling, and UI state management, with concrete excerpts from the codebase.

---

## 1. Clean Architecture & Hybrid Approach

- **Layers**
  - `data`: Networking (Retrofit), persistence (Room, DataStore), DTOs/entities, repositories, mappers, paging.
  - `domain`: Pure business logic: use cases, repository interfaces, domain models, error types, utilities.
  - `presentation`: MVVM for XML screens and MVI for Compose screens; navigation and shared UI utilities.

---

## 2. MVVM (XML Example: Profile)

```kotlin
@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val updateUserProfileUseCase: UpdateUserProfileUseCase,
) : ViewModel() {
    private val _user = MutableStateFlow<User?>(null)
    val user = _user.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    private val _isUpdating = MutableStateFlow(false)
    val isUpdating = _isUpdating.asStateFlow()

    private val _error = MutableStateFlow<AppException?>(null)
    val error = _error.asStateFlow()
}
```

Fragment observing ViewModel:

```kotlin
private fun observeViewModel() {
    viewLifecycleOwner.lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            launch {
                combine(viewModel.isLoading, viewModel.error) { isLoading, error ->
                    isLoading to error
                }.collectLatest { (isLoading, error) ->
                    when {
                        isLoading -> binding.stateLayout.showLoading()
                        error != null -> {
                            binding.stateLayout.showError(subtitle = error.message)
                            viewModel.errorHandled()
                        }
                        else -> binding.stateLayout.showContent()
                    }
                }
            }
        }
    }
}
```

---

## 3. MVI (Compose Example: Login)

```kotlin
data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val emailError: String? = null,
    val passwordError: String? = null,
    override val isLoading: Boolean = false,
    override val error: AppException? = null
) : BaseUiState
```

```kotlin
@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
) : BaseViewModel<LoginUiState, LoginEvent, LoginEffect>(
    initialState = LoginUiState()
) {
    override fun handleEvent(event: LoginEvent) {
        when (event) {
            is LoginEvent.EmailChanged -> setState { copy(email = event.email) }
            is LoginEvent.PasswordChanged -> setState { copy(password = event.password) }
            is LoginEvent.LoginClicked -> login()
        }
    }
}
```

```kotlin
@Composable
fun LoginRoute(
    navActions: LoginNavActions,
    viewModel: LoginViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(true) {
        viewModel.uiEffect.collectLatest { effect ->
            when (effect) {
                is LoginEffect.NavigateToHome -> navActions.navigateToHome()
                is LoginEffect.ShowSnackbar -> snackbarHostState.showSnackbar(effect.message)
            }
        }
    }
    LoginScreen(state = state, onEvent = viewModel::onEvent, snackbarHostState = snackbarHostState)
}
```

---

## 4. Navigation Composition

```kotlin
@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val navActions = remember(navController, context) {
        AppNavActions(navController, context as Activity)
    }
    NavHost(
        navController = navController,
        startDestination = SplashScreenRoute,
        modifier = modifier
    ) {
        splashNavGraph(navController = navController, navActions = navActions)
        loginNavGraph(navController = navController, navActions = navActions)
    }
}
```

---

## 5. Secure Token Management

- `CryptoManager`: Handles AES/GCM encryption in AndroidKeyStore.
- `SessionStorage`: Persists encrypted token in DataStore.
- `SessionManager`: Holds in-memory StateFlow for session tokens.
- `OkHttp Interceptor`: Injects `Authorization: Bearer <token>` only for `@Authenticated` endpoints.

---

## 6. Repository Bindings

```kotlin
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds @Singleton abstract fun bindProductRepository(impl: ProductRepositoryImpl): ProductRepository
    @Binds @Singleton abstract fun bindOrderRepository(impl: OrderRepositoryImpl): OrderRepository
    @Binds @Singleton abstract fun bindUserRepository(impl: UserRepositoryImpl): UserRepository
    @Binds @Singleton abstract fun bindCartRepository(impl: CartRepositoryImpl): CartRepository
    @Binds @Singleton abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository
}
```

---

## 7. Use Cases

- `LoginUseCase`: Validates non-empty fields, delegates to AuthRepository.
- `UpdateUserProfileUseCase`: Validates email and profile data.
- `SearchProductsUseCase`: Enforces min query length.

Promotes testability & domain-driven design.

---

## 8. Error Handling

- Centralized `AppException` sealed class.
- `ErrorMapper` maps HttpException → domain errors.
- `safeApiCall` standardizes resource emission.
- UI handles via `StateLayout` or MVI effects.

Example:
```kotlin
when (resource) {
  is Resource.Loading -> binding.stateLayout.showLoading()
  is Resource.Error -> binding.stateLayout.showError(resource.exception.message)
  is Resource.Success -> showContent()
}
```

---

## 9. Offline-First with Paging 3

- Products: `RemoteMediator` ensures DB is source of truth.
- Categories: `CategoryProductRemoteMediator` with assisted injection.
- Orders & search: network-only PagingSources.

Ensures scalability and offline browsing.

---

## 10. Reactive Search

```kotlin
init {
    productsFlow = _searchQuery
        .debounce(300L)
        .distinctUntilChanged()
        .flatMapLatest { query ->
            if (query.length >= SearchProductsUseCase.MIN_QUERY_LENGTH) {
                searchProductsUseCase(query)
            } else MutableStateFlow(PagingData.empty())
        }
        .cachedIn(viewModelScope)
}
```

---

## 11. UI Enhancements

- Auto-repeat increment/decrement buttons in cart.
- Double-tap back to exit in home.
- Expand/collapse animations for product descriptions.

---

## 12. Testing Strategy

- **Domain**: Unit test use cases.
- **Data**: Repository tests with fake API/DB.
- **Presentation**: ViewModel state/effect tests.
- **UI**: Snapshot/manual verification.

CI/CD planned with unit + instrumentation.

---

## 13. Performance

- DB-first strategy minimizes network.
- Debounced search reduces API churn.
- Logging only in debug builds.
- Cached data enables fast UX.

---

## 14. Coding Conventions

- Layered architecture enforced.
- Immutable state objects.
- Verb-based method names.
- No Android classes in domain.
- SOLID adherence.

---

## 15. Roadmap

- Extend Compose usage.
- Add navigation tests.
- Expand offline caching for orders.
- CI/CD integration.

---

## 16. Conclusion

NexusCart is a showcase of hybrid MVVM/MVI Clean Architecture:
- Secure token/session management.
- Offline-first with Paging 3.
- Consistent UI state handling.
- Testable, scalable, and modular.

---

**End of Technical Details Document**
