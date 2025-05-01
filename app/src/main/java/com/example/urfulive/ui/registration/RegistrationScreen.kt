import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.urfulive.ui.theme.UrfuLiveTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.ui.tooling.preview.Preview
import com.example.urfulive.R
import com.example.urfulive.data.DTOs.AuthResponse
import com.example.urfulive.data.model.User

//РАССТОЯНИЕ + 12
@Composable
fun RegistrationScreen(
    onLogoClick: () -> Unit,
    onRegisterClick: () -> Unit,
    onRegisterSuccess: (AuthResponse) -> Unit,
    onRegisterError: (Exception) -> Unit,
    viewModel: RegistrationViewModel = viewModel()
) {
    // Локальное состояние для показа/скрытия пароля
    var passwordVisible by remember { mutableStateOf(false) }

    // Подписываемся на стейт из ViewModel
    val loginValue by viewModel.login.collectAsState()
    val mailValue by viewModel.mail.collectAsState()
    val nameValue by viewModel.name.collectAsState()
    val birthDateValue by viewModel.birthDate.collectAsState()
    val passwordValue by viewModel.password.collectAsState()
    val registerCallback = remember {
        object : RegistrationViewModel.RegisterCallback {
            override fun onSuccess(user: AuthResponse) {
                onRegisterSuccess(user)
                onRegisterClick() // Навигация после успешной регистрации
            }

            override fun onError(error: Exception) {
                onRegisterError(error)
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0D0D0D))  // Фон экрана
    ) {
        // Логотип в правом верхнем углу
        Image(
            painter = painterResource(id = R.drawable.heartlast),
            contentDescription = "Logo",
            modifier = Modifier
                //.align(Alignment.TopStart)    // Прижимаем к верхнему правому краю
                .padding(top = 42.dp, end = 0.dp, start = 35.dp)

                .clickable { onLogoClick() }
        )

        // Вся остальная верстка (поля ввода, кнопка)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 41.dp, end = 42.dp)
        ) {

            Spacer(modifier = Modifier.height(24.dp))

            // Заголовок
            Text(
                text = "Регистрация",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                lineHeight = 28.sp,
                letterSpacing = 0.sp,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 127.dp)
            )

            Spacer(modifier = Modifier.height(80.dp))

            // Поле "Логин (Имя пользователя)"
            Text(
                text = "Логин(Имя пользователя):",
                color = Color.White,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 11.5.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextField(
                value = loginValue,
                singleLine = true,
                onValueChange = { viewModel.onLoginChange(it) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(15.dp),
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

            Spacer(modifier = Modifier.height(14.dp))

            // Поле "Имя и Фамилия:"
            Text(
                text = "Имя и Фамилия:",
                style = MaterialTheme.typography.bodySmall,
                color = Color.White,
                modifier = Modifier.padding(start = 11.5.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextField(
                value = nameValue,
                singleLine = true,
                onValueChange = { viewModel.onNameChange(it) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(15.dp),
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
            Spacer(modifier = Modifier.height(14.dp))

            // Поле "Почта:"
            Text(
                text = "Почта:",
                style = MaterialTheme.typography.bodySmall,
                color = Color.White,
                modifier = Modifier.padding(start = 11.5.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextField(
                value = mailValue,
                singleLine = true,
                onValueChange = { viewModel.onMailChange(it) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(15.dp),
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

            Spacer(modifier = Modifier.height(14.dp))

            // Поле "Дата рождения:"
            Text(
                text = "Дата рождения:",
                style = MaterialTheme.typography.bodySmall,
                color = Color.White,
                modifier = Modifier.padding(start = 11.5.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextField(
                value = birthDateValue,
                singleLine = true,
                onValueChange = { viewModel.onBirthDateChange(it) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(15.dp),
                trailingIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.calendar),
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(30.dp)
                    )
                },
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

            Spacer(modifier = Modifier.height(14.dp))

            // Поле "Пароль:"
            Text(
                text = "Пароль:",
                style = MaterialTheme.typography.bodySmall,
                color = Color.White,
                modifier = Modifier.padding(start = 11.5.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextField(
                value = passwordValue,
                singleLine = true,
                onValueChange = { viewModel.onPasswordChange(it) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(15.dp),
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.eye),
                        contentDescription = "Toggle password visibility",
                        tint = Color.White,
                        modifier = Modifier.size(30.dp).clickable {
                            passwordVisible = !passwordVisible
                        }
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedTextColor = Color.White,
                    focusedTextColor = Color.White,
                    unfocusedContainerColor = Color(0xFF1D1D1D),
                    focusedContainerColor = Color(0xFF1D1D1D),
                    unfocusedBorderColor = Color.Transparent,
                    focusedBorderColor = Color.Transparent,
                    cursorColor = Color.White
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )


            Spacer(modifier = Modifier.height(16.dp))

            // Кнопка "Зарегистрироваться"
            Button(
                onClick = { viewModel.onRegisterClick(loginValue, mailValue, passwordValue, nameValue, birthDateValue, registerCallback)  },
                modifier = Modifier.fillMaxWidth().padding(WindowInsets.navigationBars.asPaddingValues()),
                shape = RoundedCornerShape(15.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(red = 238, green = 126, blue = 86),
                    contentColor = Color.Black
                )
            ) {
                Text(
                    text="Зарегистрироваться",
                    style = MaterialTheme.typography.bodyLarge,
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RegistrationScreenPreview() {
    UrfuLiveTheme {
        RegistrationScreen(
            onLogoClick = { /* Навигация на экран входа, например, navController.navigate("login") */ },
            onRegisterClick = { /* Навигация на экран входа, например, navController.navigate("login") */ },
            onRegisterSuccess = {},
            onRegisterError = {}
        )
    }
}