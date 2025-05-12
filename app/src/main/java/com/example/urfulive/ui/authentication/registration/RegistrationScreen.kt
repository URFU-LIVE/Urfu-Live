import android.annotation.SuppressLint
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.urfulive.ui.theme.UrfuLiveTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import com.example.urfulive.R
import com.example.urfulive.data.DTOs.AuthResponse
import java.time.LocalDate
import java.time.format.DateTimeFormatter

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
    var showDatePicker by remember { mutableStateOf(false) }

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
            .fillMaxSize()
            .background(Color(0xFF0D0D0D))
    ) {
        Image(
            painter = painterResource(id = R.drawable.heartlast),
            contentDescription = "Logo",
            modifier = Modifier
                .padding(top = 42.dp, end = 0.dp, start = 35.dp)
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
                letterSpacing = 0.sp,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 127.dp)
            )

            Spacer(modifier = Modifier.height(80.dp))

            // Логин
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

            // Имя и Фамилия
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

            // Почта
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

            // Дата рождения с DatePicker
            Text(
                text = "Дата рождения:",
                style = MaterialTheme.typography.bodySmall,
                color = Color.White,
                modifier = Modifier.padding(start = 1.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextField(
                value = birthDateValue,
                onValueChange = { },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(15.dp),
                readOnly = true,
                trailingIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.calendar),
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(30.dp).clickable {
                            showDatePicker = true
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
                )
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Пароль
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

            // Кнопка регистрации
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
                Text(
                    text = "Зарегистрироваться",
                    style = MaterialTheme.typography.bodyLarge,
                )
            }
        }
    }

    if (showDatePicker) {
        CompositionLocalProvider(
            LocalMinimumInteractiveComponentEnforcement provides false
        ) {
            DatePickerDialog(
                onDateSelected = { selectedDate ->
                    viewModel.onBirthDateChange(selectedDate)
                },
                onDismiss = { showDatePicker = false }
            )
        }
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerDialog(
    onDateSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState()

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    datePickerState.selectedDateMillis?.let { dateMillis ->
                        val selectedDate = LocalDate.ofEpochDay(dateMillis / (24 * 60 * 60 * 1000))
                        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'00:00:00")
                        onDateSelected(selectedDate.format(formatter))
                        onDismiss()
                    }
                }
            ) {
                Text("OK", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Отмена", color = Color.White)
            }
        },
        title = {
            Text(
                "Выберите дату",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White
            )
        },
        text = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 1.dp) // Добавляем отступы по краям
            ) {
                DatePicker(
                    state = datePickerState,
                    modifier = Modifier
                        .scale(0.85f) // уменьшает всё внутри
                        .fillMaxWidth(), // можно добавить при необходимости
                    colors = DatePickerDefaults.colors(
                        containerColor = Color(0xFF1D1D1D),
                        titleContentColor = Color.White,
                        headlineContentColor = Color.White,
                        weekdayContentColor = Color.White,
                        subheadContentColor = Color.White,
                        navigationContentColor = Color.White,
                        dayContentColor = Color.White,
                        yearContentColor = Color.White,
                        currentYearContentColor = Color(0xFFFF6D00),
                        selectedYearContentColor = Color(0xFFFF6D00),
                        selectedYearContainerColor = Color(0x33FF6D00)
                    )
                )

            }
        },
        containerColor = Color(0xFF1D1D1D),
        titleContentColor = Color.White,
        textContentColor = Color.White,
    )
}

@SuppressLint("NewApi")
@RequiresApi(Build.VERSION_CODES.O)
@Preview(
    name = "Small screen (360x640)",
    device = "spec:width=360dp,height=640dp",
    showSystemUi = true,
)
@Composable
fun RegistrationPreviewSmall() {
    UrfuLiveTheme {
        RegistrationScreen(
            onRegisterClick = {},
            onRegisterSuccess = {},
            onLogoClick = {},
            onRegisterError = {},
        )
    }
}

@Preview(
    name = "Default screen",
    showBackground = true,
    showSystemUi = true
)
@Composable
fun RegistrationPreviewDefault() {
    UrfuLiveTheme {
        RegistrationScreen(
            onRegisterClick = {},
            onRegisterSuccess = {},
            onLogoClick = {},
            onRegisterError = {},
        )
    }
}

@Preview(
    name = "Large screen (500x1000)",
    device = "spec:width=500dp,height=1000dp",
    showSystemUi = true,
)
@Composable
fun RegistrationPreviewLarge() {
    UrfuLiveTheme {
        RegistrationScreen(
            onRegisterClick = {},
            onRegisterSuccess = {},
            onLogoClick = {},
            onRegisterError = {},
        )
    }
}