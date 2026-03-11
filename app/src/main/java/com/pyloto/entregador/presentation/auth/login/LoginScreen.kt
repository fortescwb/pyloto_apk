package com.pyloto.entregador.presentation.auth.login

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pyloto.entregador.R
import com.pyloto.entregador.presentation.theme.PylotoColors
import com.pyloto.entregador.presentation.theme.PylotoTheme

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val focusManager = LocalFocusManager.current
    var senhaVisivel by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is LoginEvent.LoginSuccess -> onLoginSuccess()
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
                .imePadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(80.dp))

            // ─── Branding Area ────────────────────────────────────

            // Logo placeholder — badge dourado com inicial
            Surface(
                modifier = Modifier.size(88.dp),
                shape = MaterialTheme.shapes.large,
                color = PylotoColors.Gold,
                shadowElevation = 8.dp
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = "P",
                        style = MaterialTheme.typography.displaySmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = PylotoColors.White
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Pyloto",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = PylotoColors.Black
                )
            )

            Text(
                text = "Entregador",
                style = MaterialTheme.typography.titleMedium.copy(
                    color = PylotoTheme.extendedColors.techBlue,
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 2.sp
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Faça login para começar suas entregas",
                style = MaterialTheme.typography.bodyMedium,
                color = PylotoColors.TextSecondary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(48.dp))

            // ─── Form Card ────────────────────────────────────────

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large,
                color = PylotoColors.Parchment,
                tonalElevation = 1.dp
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Email field
                    OutlinedTextField(
                        value = uiState.email,
                        onValueChange = viewModel::onEmailChange,
                        label = { Text(stringResource(R.string.login_email)) },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Email,
                                contentDescription = null,
                                tint = PylotoTheme.extendedColors.techBlue
                            )
                        },
                        isError = uiState.emailError != null,
                        supportingText = uiState.emailError?.let { error ->
                            { Text(error, color = MaterialTheme.colorScheme.error) }
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { focusManager.moveFocus(FocusDirection.Down) }
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = MaterialTheme.shapes.medium,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PylotoColors.TechBlue,
                            unfocusedBorderColor = PylotoColors.Outline,
                            cursorColor = PylotoColors.TechBlue,
                            focusedLabelColor = PylotoColors.TechBlue,
                            focusedContainerColor = PylotoColors.White,
                            unfocusedContainerColor = PylotoColors.White
                        )
                    )

                    // Senha field
                    OutlinedTextField(
                        value = uiState.senha,
                        onValueChange = viewModel::onSenhaChange,
                        label = { Text(stringResource(R.string.login_senha)) },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Lock,
                                contentDescription = null,
                                tint = PylotoTheme.extendedColors.techBlue
                            )
                        },
                        trailingIcon = {
                            IconButton(onClick = { senhaVisivel = !senhaVisivel }) {
                                Icon(
                                    imageVector = if (senhaVisivel) Icons.Default.VisibilityOff
                                    else Icons.Default.Visibility,
                                    contentDescription = if (senhaVisivel) "Ocultar senha"
                                    else "Mostrar senha",
                                    tint = PylotoColors.TextSecondary
                                )
                            }
                        },
                        isError = uiState.senhaError != null,
                        supportingText = uiState.senhaError?.let { error ->
                            { Text(error, color = MaterialTheme.colorScheme.error) }
                        },
                        visualTransformation = if (senhaVisivel) VisualTransformation.None
                        else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                focusManager.clearFocus()
                                viewModel.login()
                            }
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = MaterialTheme.shapes.medium,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PylotoColors.TechBlue,
                            unfocusedBorderColor = PylotoColors.Outline,
                            cursorColor = PylotoColors.TechBlue,
                            focusedLabelColor = PylotoColors.TechBlue,
                            focusedContainerColor = PylotoColors.White,
                            unfocusedContainerColor = PylotoColors.White
                        )
                    )

                    // Esqueci senha link
                    TextButton(
                        onClick = { /* TODO: Fluxo de recuperação de senha */ },
                        modifier = Modifier.align(Alignment.End),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.login_esqueci_senha),
                            style = MaterialTheme.typography.labelMedium,
                            color = PylotoTheme.extendedColors.techBlue
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // ─── Error message ────────────────────────────────────

            AnimatedVisibility(
                visible = uiState.error != null,
                enter = fadeIn() + slideInVertically()
            ) {
                uiState.error?.let { error ->
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.small,
                        color = MaterialTheme.colorScheme.errorContainer
                    ) {
                        Text(
                            text = error,
                            modifier = Modifier.padding(12.dp),
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            style = MaterialTheme.typography.bodySmall,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ─── CTA Principal — Dourado (ação primária no app) ──

            Button(
                onClick = viewModel::login,
                enabled = !uiState.isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = MaterialTheme.shapes.medium,
                colors = ButtonDefaults.buttonColors(
                    containerColor = PylotoTheme.extendedColors.gold,
                    contentColor = PylotoColors.White,
                    disabledContainerColor = PylotoColors.GoldLight,
                    disabledContentColor = PylotoColors.White.copy(alpha = 0.7f)
                ),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 4.dp,
                    pressedElevation = 8.dp
                )
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(22.dp),
                        strokeWidth = 2.5.dp,
                        color = PylotoColors.White
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Entrando...",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                } else {
                    Text(
                        text = stringResource(R.string.login_entrar),
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ─── Link para cadastro ───────────────────────────────

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Ainda não tem conta?",
                    style = MaterialTheme.typography.bodyMedium,
                    color = PylotoColors.TextSecondary
                )
                TextButton(onClick = onNavigateToRegister) {
                    Text(
                        text = "Cadastre-se",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = PylotoTheme.extendedColors.techBlue
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // ─── Selo de segurança ────────────────────────────────

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.padding(bottom = 24.dp)
            ) {
                Surface(
                    shape = MaterialTheme.shapes.extraSmall,
                    color = PylotoTheme.extendedColors.militaryGreen.copy(alpha = 0.1f)
                ) {
                    Text(
                        text = "🔒 Conexão segura",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = PylotoTheme.extendedColors.militaryGreen
                    )
                }
            }
        }
    }
}
