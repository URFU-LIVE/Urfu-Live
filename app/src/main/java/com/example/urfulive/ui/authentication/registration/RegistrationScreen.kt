import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.urfulive.ui.theme.UrfuLiveTheme
import com.example.urfulive.R
import com.example.urfulive.data.DTOs.AuthResponse

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun RegistrationScreen(
    onLogoClick: () -> Unit,
    onRegisterClick: () -> Unit,
    onRegisterSuccess: (AuthResponse) -> Unit,
    onRegisterError: (Exception) -> Unit,
    viewModel: RegistrationViewModel = viewModel()
) {
    var passwordVisible by remember { mutableStateOf(false) }

    val loginValue by viewModel.login.collectAsState()
    val mailValue by viewModel.mail.collectAsState()
    val nameValue by viewModel.name.collectAsState()
    val birthDateValue by viewModel.birthDate.collectAsState()
    val passwordValue by viewModel.password.collectAsState()

    val registerCallback = remember {
        object : RegistrationViewModel.RegisterCallback {
            override fun onSuccess(user: AuthResponse) {
                onRegisterSuccess(user)
                onRegisterClick()
            }

            override fun onError(error: Exception) {
                onRegisterError(error)
            }
        }
    }

    Box(
        modifier = Modifier
            .background(Color(0xFF0D0D0D)).fillMaxSize()
    )
            {
                Image(
                    painter = painterResource(id = R.drawable.heartlast),
                    contentDescription = "Logo",
                    modifier = Modifier
                        .padding(top = 42.dp, start = 35.dp)
                        .clickable { onLogoClick() }
                )

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(start = 41.dp, end = 42.dp)
                ) {
                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "Регистрация",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        lineHeight = 28.sp,
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(top = 127.dp)
                    )

                    Spacer(modifier = Modifier.height(80.dp))

                    // Логин
                    Text("Логин(Имя пользователя):", color = Color.White, modifier = Modifier.padding(start = 11.5.dp))
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = loginValue,
                        singleLine = true,
                        onValueChange = { viewModel.onLoginChange(it) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(15.dp),
                        colors = textFieldColors()
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Имя и Фамилия
                    Text("Имя и Фамилия:", color = Color.White, modifier = Modifier.padding(start = 11.5.dp))
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = nameValue,
                        singleLine = true,
                        onValueChange = { viewModel.onNameChange(it) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(15.dp),
                        colors = textFieldColors()
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Почта
                    Text("Почта:", color = Color.White, modifier = Modifier.padding(start = 11.5.dp))
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = mailValue,
                        singleLine = true,
                        onValueChange = { viewModel.onMailChange(it) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(15.dp),
                        colors = textFieldColors()
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Дата рождения с автоматическим форматированием
                    Text("Дата рождения:", color = Color.White, modifier = Modifier.padding(start = 11.5.dp))
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = birthDateValue,
                        onValueChange = { newValue ->
                            // Удаляем все нецифровые символы
                            val digits = newValue.filter { it.isDigit() }

                            // Ограничиваем длину 8 цифрами (ДДММГГГГ)
                            val limitedDigits = if (digits.length > 8) digits.substring(0, 8) else digits

                            viewModel.onBirthDateChange(limitedDigits)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(15.dp),
                        placeholder = { Text("ДД-MM-ГГГГ", color = Color.Gray) },
                        colors = textFieldColors(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        visualTransformation = DateTransformation()
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Пароль
                    Text("Пароль:", color = Color.White, modifier = Modifier.padding(start = 11.5.dp))
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
                        colors = textFieldColors(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            viewModel.onRegisterClick(
                                loginValue,
                                mailValue,
                                passwordValue,
                                nameValue,
                                birthDateValue,
                                registerCallback
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(15.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(238, 126, 86),
                            contentColor = Color.Black
                        )
                    ) {
                        Text("Зарегистрироваться", style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }
}

class DateTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val trimmed = text.text.take(8) // Берем только первые 8 символов
        val formatted = buildString {
            for (i in trimmed.indices) {
                append(trimmed[i])
                when (i) {
                    1 -> if (trimmed.length > 2) append('-')
                    3 -> if (trimmed.length > 4) append('-')
                }
            }
        }

        val dateMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                val transformedOffset = when {
                    offset <= 1 -> offset
                    offset <= 3 -> offset + 1
                    offset <= 7 -> offset + 2
                    else -> formatted.length
                }
                return transformedOffset.coerceAtMost(formatted.length)
            }

            override fun transformedToOriginal(offset: Int): Int {
                return when {
                    offset <= 2 -> offset
                    offset <= 5 -> offset - 1
                    else -> offset - 2
                }.coerceIn(0, trimmed.length)
            }
        }

        return TransformedText(AnnotatedString(formatted), dateMapping)
    }
}

@Composable
private fun textFieldColors() = OutlinedTextFieldDefaults.colors(
    unfocusedTextColor = Color.White,
    focusedTextColor = Color.White,
    unfocusedContainerColor = Color(0xFF1D1D1D),
    focusedContainerColor = Color(0xFF1D1D1D),
    unfocusedBorderColor = Color.Transparent,
    focusedBorderColor = Color.Transparent,
    cursorColor = Color.White
)