import android.os.Build
import androidx.activity.compose.BackHandler
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import live.urfu.frontend.R
import live.urfu.frontend.data.DTOs.AuthResponse

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun RegistrationScreen(
    onLogoClick: () -> Unit,
    onRegisterClick: () -> Unit,
    onRegisterSuccess: (AuthResponse) -> Unit,
    onRegisterError: (Exception) -> Unit,
    viewModel: RegistrationViewModel = viewModel()
) {
    var currentStep by remember { mutableStateOf(1) }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPassword by remember { mutableStateOf("") }

    val loginValue by viewModel.login.collectAsState()
    val mailValue by viewModel.mail.collectAsState()
    val nameValue by viewModel.name.collectAsState()
    val birthDateValue by viewModel.birthDate.collectAsState()
    val passwordValue by viewModel.password.collectAsState()

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp
    val isSmallScreen = screenWidth < 400

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

    BackHandler(enabled = currentStep > 1) {
        currentStep -= 1
    }

    Box(
        modifier = Modifier
            .background(Color(0xFF0D0D0D))
            .fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.heartlast),
            contentDescription = "Logo",
            modifier = Modifier
                .padding(top = 38.dp, end = 41.dp)
                .clickable { onLogoClick() }
                .align(Alignment.TopEnd)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 41.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(100.dp))

            when (currentStep) {
                1 -> UsernameStep(
                    value = loginValue,
                    onValueChange = { viewModel.onLoginChange(it) },
                    onNextClick = {
                        if (loginValue.isNotBlank()) currentStep = 2
                    },
                    onBackClick = null, // Первый шаг - нет кнопки назад
                    isSmallScreen = isSmallScreen
                )
                2 -> NameStep(
                    value = nameValue,
                    onValueChange = { viewModel.onNameChange(it) },
                    onNextClick = {
                        if (nameValue.isNotBlank()) currentStep = 3
                    },
                    onBackClick = { currentStep = 1 },
                    isSmallScreen = isSmallScreen

                )
                3 -> EmailStep(
                    value = mailValue,
                    onValueChange = { viewModel.onMailChange(it) },
                    onNextClick = {
                        if (mailValue.isNotBlank()) currentStep = 4
                    },
                    onBackClick = { currentStep = 2 },
                    isSmallScreen = isSmallScreen
                )
                4 -> BirthDateStep(
                    value = birthDateValue,
                    onValueChange = { viewModel.onBirthDateChange(it) },
                    onNextClick = {
                        if (birthDateValue.isNotBlank()) currentStep = 5
                    },
                    onBackClick = { currentStep = 3 },
                    isSmallScreen = isSmallScreen
                )
                5 -> PasswordStep(
                    value = passwordValue,
                    confirmValue = confirmPassword,
                    onValueChange = { viewModel.onPasswordChange(it) },
                    onConfirmValueChange = { confirmPassword = it },
                    passwordVisible = passwordVisible,
                    onPasswordVisibilityToggle = { passwordVisible = !passwordVisible },
                    onRegisterClick = {
                        if (passwordValue.isNotBlank() && passwordValue == confirmPassword) {
                            viewModel.onRegisterClick(
                                loginValue,
                                mailValue,
                                passwordValue,
                                nameValue,
                                birthDateValue,
                                registerCallback
                            )
                        }
                    },
                    onBackClick = { currentStep = 4 },
                    isSmallScreen = isSmallScreen
                )
            }
        }
        RegistrationButtons(
            currentStep = currentStep,
            onBackClick = if (currentStep > 1) { { currentStep -= 1 } } else null,
            onNextClick = {
                when (currentStep) {
                    1 -> if (loginValue.isNotBlank()) currentStep = 2
                    2 -> if (nameValue.isNotBlank()) currentStep = 3
                    3 -> if (mailValue.isNotBlank()) currentStep = 4
                    4 -> if (birthDateValue.isNotBlank()) currentStep = 5
                    5 -> if (passwordValue.isNotBlank() && passwordValue == confirmPassword) {
                        viewModel.onRegisterClick(
                            loginValue,
                            mailValue,
                            passwordValue,
                            nameValue,
                            birthDateValue,
                            registerCallback
                        )
                    }
                }
            },
            isEnabled = when (currentStep) {
                1 -> loginValue.isNotBlank()
                2 -> nameValue.isNotBlank()
                3 -> mailValue.isNotBlank()
                4 -> birthDateValue.length >= 8
                5 -> passwordValue.isNotBlank() && confirmPassword.isNotBlank() && passwordValue == confirmPassword
                else -> false
            },
            buttonText = if (currentStep == 5) "Зарегистрироваться" else "Далее",
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 15.dp),
            isSmallScreen = isSmallScreen
        )
    }
}

@Composable
private fun UsernameStep(
    value: String,
    onValueChange: (String) -> Unit,
    onNextClick: () -> Unit,
    onBackClick: (() -> Unit)?,
    isSmallScreen: Boolean
) {
    RegistrationStepTemplate(
        title = "Введите имя пользователя",
        subtitle = "Оно может быть изменено в настройках приложения",
        content = {
            Text(
                "Имя пользователя",
                color = Color.White,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 11.5.dp, start = 10.5.dp),
                textAlign = TextAlign.Start,
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextField(
                value = value,
                singleLine = true,
                onValueChange = onValueChange,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(15.dp),
                colors = textFieldColors()
            )
        },
        buttonText = "Далее",
        onButtonClick = onNextClick,
        onBackClick = onBackClick,
        isButtonEnabled = value.isNotBlank(),
        isSmallScreen = isSmallScreen
    )
}

@Composable
private fun NameStep(
    value: String,
    onValueChange: (String) -> Unit,
    onNextClick: () -> Unit,
    onBackClick: (() -> Unit)?,
    isSmallScreen: Boolean
) {
    RegistrationStepTemplate(
        title = "Введите ваше имя и фамилию",
        subtitle = "Они будут отображаться в профиле приложения",
        content = {
            Text(
                "Имя и Фамилия",
                color = Color.White,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 11.5.dp, start = 10.5.dp),
                textAlign = TextAlign.Start,
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextField(
                value = value,
                singleLine = true,
                onValueChange = onValueChange,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(15.dp),
                colors = textFieldColors()
            )
        },
        buttonText = "Далее",
        onButtonClick = onNextClick,
        onBackClick = onBackClick,
        isButtonEnabled = value.isNotBlank(),
        isSmallScreen = isSmallScreen
    )
}

@Composable
private fun EmailStep(
    value: String,
    onValueChange: (String) -> Unit,
    onNextClick: () -> Unit,
    onBackClick: (() -> Unit)?,
    isSmallScreen: Boolean
) {
    RegistrationStepTemplate(
        title = "Введите адрес вашей электронной почты",
        subtitle = "Мы будем использовать его для отправки уведомлений",
        content = {
            Text(
                "E-mail",
                color = Color.White,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 11.5.dp, start = 10.5.dp),
                textAlign = TextAlign.Start,
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextField(
                value = value,
                singleLine = true,
                onValueChange = onValueChange,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(15.dp),
                colors = textFieldColors()
            )
        },
        buttonText = "Далее",
        onButtonClick = onNextClick,
        onBackClick = onBackClick,
        isButtonEnabled = value.isNotBlank(),
        isSmallScreen = isSmallScreen
    )
}

@Composable
private fun BirthDateStep(
    value: String,
    onValueChange: (String) -> Unit,
    onNextClick: () -> Unit,
    onBackClick: (() -> Unit)?,
    isSmallScreen: Boolean
) {
    RegistrationStepTemplate(
        title = "Введите вашу дату рождения",
        subtitle = "Эта информация не будет показана другим пользователям",
        content = {
            Text(
                "Дата рождения",
                color = Color.White,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 11.5.dp, start = 10.5.dp),
                textAlign = TextAlign.Start,
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextField(
                value = value,
                onValueChange = { newValue ->
                    val digits = newValue.filter { it.isDigit() }
                    val limitedDigits = if (digits.length > 8) digits.substring(0, 8) else digits
                    onValueChange(limitedDigits)
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(15.dp),
                placeholder = { Text("ДД-ММ-ГГГГ", color = Color.Gray) },
                colors = textFieldColors(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                visualTransformation = DateTransformation()
            )
        },
        buttonText = "Далее",
        onButtonClick = onNextClick,
        onBackClick = onBackClick,
        isButtonEnabled = value.length >= 8,
        isSmallScreen = isSmallScreen
    )
}

@Composable
private fun PasswordStep(
    value: String,
    confirmValue: String,
    onValueChange: (String) -> Unit,
    onConfirmValueChange: (String) -> Unit,
    passwordVisible: Boolean,
    onPasswordVisibilityToggle: () -> Unit,
    onRegisterClick: () -> Unit,
    onBackClick: (() -> Unit)?,
    isSmallScreen: Boolean
) {
    RegistrationStepTemplate(
        title = "Введите и подтвердите ваш пароль",
        subtitle = "Придумайте надежный пароль для защиты аккаунта",
        content = {
            Text(
                "Пароль",
                color = Color.White,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 11.5.dp, start = 10.5.dp),
                textAlign = TextAlign.Start,
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextField(
                value = value,
                singleLine = true,
                onValueChange = onValueChange,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(15.dp),
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.eye),
                        contentDescription = "Toggle password visibility",
                        tint = Color.White,
                        modifier = Modifier
                            .size(30.dp)
                            .clickable { onPasswordVisibilityToggle() }
                    )
                },
                colors = textFieldColors(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                "Подтвердите пароль",
                color = Color.White,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 11.5.dp, start = 10.5.dp),
                textAlign = TextAlign.Start,
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextField(
                value = confirmValue,
                singleLine = true,
                onValueChange = onConfirmValueChange,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(15.dp),
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                colors = textFieldColors(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                isError = confirmValue.isNotEmpty() && value != confirmValue
            )

            // Показываем ошибку если пароли не совпадают
            if (confirmValue.isNotEmpty() && value != confirmValue) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "Пароли не совпадают",
                    color = Color.Red,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 11.5.dp)
                )
            }
        },
        buttonText = "Зарегистрироваться",
        onButtonClick = onRegisterClick,
        onBackClick = onBackClick,
        isButtonEnabled = value.isNotBlank() && confirmValue.isNotBlank() && value == confirmValue,
        isSmallScreen = isSmallScreen
    )
}

@Composable
private fun RegistrationStepTemplate(
    title: String,
    subtitle: String,
    content: @Composable ColumnScope.() -> Unit,
    buttonText: String,
    onButtonClick: () -> Unit,
    onBackClick: (() -> Unit)? = null,
    isButtonEnabled: Boolean = true,
    isSmallScreen: Boolean
) {
    val paddingBeforeContent = when {
        isSmallScreen -> 48.dp
        else -> 60.dp
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = Color.White,
            textAlign = TextAlign.Start,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyLarge,
            color = Color.Gray,
            textAlign = TextAlign.Start,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(paddingBeforeContent))

        content()
    }
}

@Composable
private fun RegistrationButtons(
    currentStep: Int,
    onBackClick: (() -> Unit)?,
    onNextClick: () -> Unit,
    isEnabled: Boolean,
    buttonText: String,
    modifier: Modifier = Modifier,
    isSmallScreen: Boolean
) {
    val fontSize = when {
        isSmallScreen -> 14.sp
        else -> 16.sp
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .systemBarsPadding(),
        horizontalArrangement = if (onBackClick != null) Arrangement.spacedBy(12.dp) else Arrangement.End
    ) {
        if (onBackClick != null) {
            // Кнопка "Назад"
            OutlinedButton(
                onClick = onBackClick,
                modifier = Modifier.weight(0.45f),
                shape = RoundedCornerShape(15.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color.White
                )
            ) {
                Text("Назад", style = MaterialTheme.typography.headlineMedium.copy(fontSize = fontSize, lineHeight = fontSize))
            }
        }

        // Основная кнопка
        Button(
            onClick = onNextClick,
            modifier = if (onBackClick != null) Modifier.weight(1f) else Modifier.width(150.dp),
            shape = RoundedCornerShape(15.dp),
            enabled = isEnabled,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(238, 126, 86),
                contentColor = Color.Black,
                disabledContainerColor = Color.Gray,
                disabledContentColor = Color.DarkGray
            )
        ) {
            Text(
                buttonText,
                style = MaterialTheme.typography.headlineMedium.copy(fontSize = fontSize, lineHeight = fontSize)
            )
        }
    }

}


// Класс DateTransformation остается тем же
class DateTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val trimmed = text.text.take(8)
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