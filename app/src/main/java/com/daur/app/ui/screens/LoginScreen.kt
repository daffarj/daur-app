package com.daur.app.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.daur.app.data.SessionManager
import com.daur.app.ui.components.DaurLogo
import com.daur.app.ui.theme.*
import com.daur.app.viewmodel.AuthUiState
import com.daur.app.viewmodel.AuthViewModel

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    viewModel: AuthViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var isLoginTab by remember { mutableStateOf(true) }
    val context = LocalContext.current  // ← tambahkan ini

    // Navigate on success
    LaunchedEffect(uiState) {
        if (uiState is AuthUiState.Success) {
            SessionManager.save(context)  // ← tambahkan ini
            viewModel.resetState()
            onLoginSuccess()
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(Background)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // ── Header hijau ──────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Primary, Color(0xFF004D38))
                        )
                    )
                    .padding(top = 56.dp, bottom = 40.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    DaurLogo(
                        modifier = Modifier.size(72.dp),
                        tint = Color.White
                    )
                    Spacer(Modifier.height(12.dp))
                    Text(
                        text = "DAUR",
                        color = Color.White,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 4.sp
                    )
                    Text(
                        text = "Kelola Sampah Jadi Berkah",
                        color = Color.White.copy(alpha = 0.85f),
                        fontSize = 13.sp
                    )
                }
            }

            // ── Tab Login / Daftar ────────────────────────
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .offset(y = (-20).dp),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(8.dp),
                colors = CardDefaults.cardColors(containerColor = Surface)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {

                    // Tab row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(SurfaceContainer)
                            .padding(4.dp)
                    ) {
                        TabButton(
                            text = "Masuk",
                            selected = isLoginTab,
                            onClick = { isLoginTab = true },
                            modifier = Modifier.weight(1f)
                        )
                        TabButton(
                            text = "Daftar",
                            selected = !isLoginTab,
                            onClick = { isLoginTab = false },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(Modifier.height(24.dp))

                    // Form
                    AnimatedContent(
                        targetState = isLoginTab,
                        transitionSpec = {
                            if (targetState) {
                                slideInHorizontally { -it } + fadeIn() togetherWith
                                slideOutHorizontally { it } + fadeOut()
                            } else {
                                slideInHorizontally { it } + fadeIn() togetherWith
                                slideOutHorizontally { -it } + fadeOut()
                            }
                        }, label = "formAnim"
                    ) { isLogin ->
                        if (isLogin) {
                            LoginForm(
                                uiState = uiState,
                                onLogin = { email, pass -> viewModel.login(email, pass) },
                                onForgotPassword = { /* TODO: forgot password */ }
                            )
                        } else {
                            RegisterForm(
                                uiState = uiState,
                                onRegister = { email, pass, nama ->
                                    viewModel.register(email, pass, nama)
                                }
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            // ── Divider atau masuk dengan ─────────────────
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 40.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Divider(modifier = Modifier.weight(1f), color = OutlineVariant)
                Text(
                    text = "  atau masuk dengan  ",
                    color = OnSurfaceVariant,
                    fontSize = 12.sp
                )
                Divider(modifier = Modifier.weight(1f), color = OutlineVariant)
            }

            Spacer(Modifier.height(16.dp))

            // ── Google Sign In (placeholder) ──────────────
            OutlinedButton(
                onClick = { /* TODO: Google Sign In */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 40.dp)
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, OutlineVariant)
            ) {
                Icon(
                    imageVector = Icons.Filled.AccountCircle,
                    contentDescription = "Google",
                    tint = Color(0xFF4285F4),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text("Google", color = OnSurface, fontWeight = FontWeight.Medium)
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}

// ── Login Form ────────────────────────────────────────────
@Composable
private fun LoginForm(
    uiState: AuthUiState,
    onLogin: (String, String) -> Unit,
    onForgotPassword: () -> Unit
) {
    var email    by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showPass by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
    val isLoading = uiState is AuthUiState.Loading

    Column {
        // Email
        DaurTextField(
            value = email,
            onValueChange = { email = it },
            label = "Email",
            placeholder = "nama@email.com",
            leadingIcon = Icons.Outlined.Email,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction    = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Down) }
            ),
            enabled = !isLoading
        )

        Spacer(Modifier.height(12.dp))

        // Password
        DaurTextField(
            value = password,
            onValueChange = { password = it },
            label = "Kata Sandi",
            placeholder = "••••••••",
            leadingIcon = Icons.Outlined.Lock,
            trailingIcon = if (showPass) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
            onTrailingClick = { showPass = !showPass },
            visualTransformation = if (showPass) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction    = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    focusManager.clearFocus()
                    onLogin(email, password)
                }
            ),
            enabled = !isLoading
        )

        // Lupa kata sandi
        TextButton(
            onClick = onForgotPassword,
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Lupa Kata Sandi?", color = Primary, fontSize = 13.sp)
        }

        // Error message
        ErrorMessage(uiState)

        Spacer(Modifier.height(4.dp))

        // Tombol masuk
        Button(
            onClick = { onLogin(email, password) },
            enabled = !isLoading,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Primary)
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(22.dp),
                    color = Color.White,
                    strokeWidth = 2.5.dp
                )
            } else {
                Text("Masuk", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
            }
        }
    }
}

// ── Register Form ─────────────────────────────────────────
@Composable
private fun RegisterForm(
    uiState: AuthUiState,
    onRegister: (String, String, String) -> Unit
) {
    var nama      by remember { mutableStateOf("") }
    var email     by remember { mutableStateOf("") }
    var password  by remember { mutableStateOf("") }
    var confirmPw by remember { mutableStateOf("") }
    var showPass  by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
    val isLoading = uiState is AuthUiState.Loading

    val passwordMatch = confirmPw.isEmpty() || password == confirmPw

    Column {
        // Nama Lengkap
        DaurTextField(
            value = nama,
            onValueChange = { nama = it },
            label = "Nama Lengkap",
            placeholder = "Nama kamu",
            leadingIcon = Icons.Outlined.Person,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
            enabled = !isLoading
        )

        Spacer(Modifier.height(12.dp))

        // Email
        DaurTextField(
            value = email,
            onValueChange = { email = it },
            label = "Email",
            placeholder = "nama@email.com",
            leadingIcon = Icons.Outlined.Email,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction    = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
            enabled = !isLoading
        )

        Spacer(Modifier.height(12.dp))

        // Password
        DaurTextField(
            value = password,
            onValueChange = { password = it },
            label = "Kata Sandi",
            placeholder = "Min. 6 karakter",
            leadingIcon = Icons.Outlined.Lock,
            trailingIcon = if (showPass) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
            onTrailingClick = { showPass = !showPass },
            visualTransformation = if (showPass) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction    = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
            enabled = !isLoading
        )

        Spacer(Modifier.height(12.dp))

        // Konfirmasi Password
        DaurTextField(
            value = confirmPw,
            onValueChange = { confirmPw = it },
            label = "Konfirmasi Kata Sandi",
            placeholder = "Ulangi kata sandi",
            leadingIcon = Icons.Outlined.Lock,
            visualTransformation = PasswordVisualTransformation(),
            isError = !passwordMatch,
            supportingText = if (!passwordMatch) "Kata sandi tidak cocok" else null,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction    = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    focusManager.clearFocus()
                    if (passwordMatch) onRegister(email, password, nama)
                }
            ),
            enabled = !isLoading
        )

        // Error message
        ErrorMessage(uiState)

        Spacer(Modifier.height(16.dp))

        // Tombol daftar
        Button(
            onClick = { if (passwordMatch) onRegister(email, password, nama) },
            enabled = !isLoading && passwordMatch,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Primary)
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(22.dp),
                    color = Color.White,
                    strokeWidth = 2.5.dp
                )
            } else {
                Text("Daftar Sekarang", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
            }
        }
    }
}

// ── Reusable TextField ────────────────────────────────────
@Composable
private fun DaurTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    leadingIcon: androidx.compose.ui.graphics.vector.ImageVector,
    trailingIcon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    onTrailingClick: (() -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    isError: Boolean = false,
    supportingText: String? = null,
    enabled: Boolean = true
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        placeholder = { Text(placeholder, color = OnSurfaceVariant.copy(alpha = 0.6f)) },
        leadingIcon = {
            Icon(leadingIcon, contentDescription = null, tint = if (isError) MaterialTheme.colorScheme.error else Primary)
        },
        trailingIcon = if (trailingIcon != null) ({
            IconButton(onClick = { onTrailingClick?.invoke() }) {
                Icon(trailingIcon, contentDescription = null, tint = OnSurfaceVariant)
            }
        }) else null,
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        isError = isError,
        supportingText = if (supportingText != null) ({ Text(supportingText) }) else null,
        enabled = enabled,
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor   = Primary,
            focusedLabelColor    = Primary,
            cursorColor          = Primary,
            unfocusedBorderColor = OutlineVariant
        )
    )
}

// ── Tab Button ────────────────────────────────────────────
@Composable
private fun TabButton(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(40.dp),
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (selected) Primary else Color.Transparent,
            contentColor   = if (selected) Color.White else OnSurfaceVariant
        ),
        elevation = ButtonDefaults.buttonElevation(0.dp)
    ) {
        Text(text, fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal, fontSize = 14.sp)
    }
}

// ── Error Message ─────────────────────────────────────────
@Composable
private fun ErrorMessage(uiState: AuthUiState) {
    AnimatedVisibility(visible = uiState is AuthUiState.Error) {
        if (uiState is AuthUiState.Error) {
            Spacer(Modifier.height(8.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                ),
                shape = RoundedCornerShape(10.dp)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Filled.Warning,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        uiState.message,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 13.sp
                    )
                }
            }
            Spacer(Modifier.height(8.dp))
        }
    }
}
