import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
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
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.tooling.preview.Preview
import com.example.urfulive.R
import com.example.urfulive.ui.theme.UrfuLiveTheme
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Arrangement
import com.example.urfulive.data.model.User


@Composable
fun LoginScreen(
    onRegisterClick: () -> Unit,
    onLoginClick: () -> Unit,
    onLoginSuccess: (User) -> Unit,
    onLoginError: (Exception) -> Unit,
    viewModel: LoginViewModel = viewModel()
) {
    // Считываем текущие значения полей из ViewModel
    val loginValue by viewModel.login.collectAsState()
    val passwordValue by viewModel.password.collectAsState()
    val loginCallback = remember {
        object : LoginViewModel.LoginCallback {
            override fun onSuccess(user: User) {
                onLoginSuccess(user)
                onLoginClick() // Навигация после успешной регистрации
            }

            override fun onError(error: Exception) {
                onLoginError(error)
            }
        }
    }
    // Состояние для показа/скрытия пароля
    var passwordVisible by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0D0D0D)) // Фон экрана

    ) {

        Box( modifier = Modifier
            .fillMaxSize()
            .padding(96.dp)) {
            Image(
                painter = painterResource(id = R.drawable.loginlogo), // или heart иконка
                contentDescription = "Heart Logo",
                modifier = Modifier
                    .size(150.dp)
                    .align(Alignment.TopCenter)
                    .padding(top = 0.dp)
            )
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 42.dp, end = 41.dp)
        ) {
            Spacer(modifier = Modifier.height(246.dp))

            // Заголовок "Добро пожаловать"
            Text(
                text = "Добро пожаловать в\nUrfu Live",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(44.dp))

            // Подзаголовок: "Войдите или Зарегистрируйтесь"
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Войдите или ",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White,
                    modifier = Modifier.align(Alignment.CenterVertically).padding(start = 15.5.dp),
                )
                Text(
                    text = "Зарегистрируйтесь",
                    color = Color(0xFF76B6FE),
                    modifier = Modifier.align(Alignment.CenterVertically).clickable {
                        onRegisterClick()
                    },
                    style = MaterialTheme.typography.titleSmall
                )

            }

            Spacer(modifier = Modifier.height(47.dp))

            // Поле "Логин"
            Text(
                text = "Логин:",
                style = MaterialTheme.typography.bodySmall,
                color = Color.White,
                modifier = Modifier.padding(start = 10.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextField(
                value = loginValue,
                onValueChange = { viewModel.onLoginChange(it) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(15.dp),
                singleLine = true,
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    textColor = Color.White,
                    backgroundColor = Color(0xFF1D1D1D),
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    cursorColor = Color.White
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Поле "Пароль"
            Text(
                text = "Пароль:",
                style = MaterialTheme.typography.bodySmall,
                color = Color.White,
                modifier = Modifier.padding(start = 10.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextField(
                value = passwordValue,
                onValueChange = { viewModel.onPasswordChange(it) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(15.dp),
                singleLine = true,
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    Icon(
                        painter = if (passwordVisible) {
                            painterResource(id = R.drawable.eye) //////////////////////////////////////////////////////////////ЗЗЗЗЗЗЗЗЗААААААААААГЛУШКА open - close
                        } else {
                            painterResource(id = R.drawable.eye)
                        },
                        contentDescription = "Toggle password visibility",
                        tint = Color.White,
                        modifier = Modifier
                            .size(30.dp)
                            .clickable {
                                passwordVisible = !passwordVisible
                            }
                    )
                },
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    textColor = Color.White,
                    backgroundColor = Color(0xFF1D1D1D),
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    cursorColor = Color.White
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )

            Spacer(modifier = Modifier.height(14.dp))

            // "Восстановить пароль" (кликабельный текст)
            Text(
                text = "Восстановить пароль",
                style = MaterialTheme.typography.titleSmall,
                color = Color(0xFF76B6FE),
                modifier = Modifier
                    .padding(start = 9.dp)
                    .clickable {
                        viewModel.onRestorePasswordClick()
                    }

            )

            Spacer(modifier = Modifier.height(164.dp))

            // Кнопка "Войти"
            Button(
                onClick = { viewModel.onLoginClick(loginValue, passwordValue, loginCallback) },
                modifier = Modifier.fillMaxWidth().padding(WindowInsets.navigationBars.asPaddingValues()),
                shape = RoundedCornerShape(15.dp),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color(red = 238, green = 126, blue = 86),
                    contentColor = Color.Black
                )
            ) {
                Text(
                    text = "Войти",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    UrfuLiveTheme {
        LoginScreen(
            onRegisterClick = { /* пустой callback для превью */ },
            onLoginClick = { /* пустой callback для превью */ },
            onLoginSuccess = { /* пустой callback для превью */ },
            onLoginError = { /* пустой callback для превью */ },
        )
    }
}