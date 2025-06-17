import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.material3.MaterialTheme
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import live.urfu.frontend.data.DTOs.AuthResponse
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.ui.platform.LocalConfiguration
import live.urfu.frontend.R
import live.urfu.frontend.ui.main.PostViewModel


@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
fun LoginScreen(
    onRegisterClick: () -> Unit,
    onLoginClick: () -> Unit,
    onRestorePasswordClick: () -> Unit,
    onLoginSuccess: (AuthResponse) -> Unit,
    onLoginError: (Exception) -> Unit,
    postViewModel: PostViewModel,
    viewModel: LoginViewModel = viewModel()
) {
    val loginValue by viewModel.login.collectAsState()
    val passwordValue by viewModel.password.collectAsState()

    var passwordVisible by remember { mutableStateOf(false) }

    val loginCallback = remember {
        object : LoginViewModel.LoginCallback {
            override fun onSuccess(user: AuthResponse) {
                onLoginSuccess(user)
                onLoginClick()
            }

            override fun onError(error: Exception) {
                onLoginError(error)
            }
        }
    }

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp
    val isSmallScreen = screenWidth < 400
    val paddingBeforeGreeting = when {
        isSmallScreen -> 1.dp
        else -> 30.dp
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0D0D0D))
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(30.dp)) //todo Тут надо поменять - задачка для Артема

        Image(
            painter = painterResource(id = R.drawable.loginlogo),
            contentDescription = "Heart Logo",
            modifier = Modifier
                .size(120.dp)
        )

        Spacer(modifier = Modifier.height(paddingBeforeGreeting))

        Text(
            text = "Добро пожаловать в\nUrfu Live",
            style = MaterialTheme.typography.titleMedium,
            color = Color.White,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Войдите или",
                style = MaterialTheme.typography.bodySmall,
                color = Color.White
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                text = "Зарегистрируйтесь",
                color = Color(0xFF76B6FE),
                modifier = Modifier.clickable { onRegisterClick() },
                style = MaterialTheme.typography.titleSmall
            )
        }

        Spacer(modifier = Modifier.height(36.dp))

        Text("Логин:", color = Color.White, style = MaterialTheme.typography.bodySmall, modifier = Modifier.align(Alignment.Start))
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = loginValue,
            onValueChange = { viewModel.onLoginChange(it) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedTextColor = Color.White,
                focusedTextColor = Color.White,
                unfocusedContainerColor = Color(0xFF1D1D1D),
                focusedContainerColor = Color(0xFF1D1D1D),
                unfocusedBorderColor = Color.Transparent,
                focusedBorderColor = Color.Transparent,
                cursorColor = Color.White
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text("Пароль:", color = Color.White, style = MaterialTheme.typography.bodySmall, modifier = Modifier.align(Alignment.Start))
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = passwordValue,
            onValueChange = { viewModel.onPasswordChange(it) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            singleLine = true,
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.eye),
                    contentDescription = "Включить отобржаение пароля",
                    tint = Color.White,
                    modifier = Modifier
                        .size(24.dp)
                        .clickable { passwordVisible = !passwordVisible }
                )
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedTextColor = Color.White,
                focusedTextColor = Color.White,
                unfocusedContainerColor = Color(0xFF1D1D1D),
                focusedContainerColor = Color(0xFF1D1D1D),
                unfocusedBorderColor = Color.Transparent,
                focusedBorderColor = Color.Transparent,
                cursorColor = Color.White
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Восстановить пароль",
            color = Color(0xFF76B6FE),
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier
                .align(Alignment.Start)
                .clickable { onRestorePasswordClick() }
        )

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = { viewModel.onLoginClick(loginValue, passwordValue, loginCallback) },
            modifier = Modifier
                .fillMaxWidth()
                .systemBarsPadding()
                .padding(bottom = 15.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFEE7E56),
                contentColor = Color.Black
            )
        ) {
            Text(text = "Войти", style = MaterialTheme.typography.bodyLarge)
        }
    }
}