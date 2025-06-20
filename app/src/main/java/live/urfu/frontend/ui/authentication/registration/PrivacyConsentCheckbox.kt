package live.urfu.frontend.ui.authentication.registration

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp

@Composable
fun PrivacyConsentCheckbox(
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    onPrivacyPolicyClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Checkbox(
            checked = isChecked,
            onCheckedChange = onCheckedChange,
            colors = CheckboxDefaults.colors(
                checkedColor = Color(0xFFEE7E56),
                uncheckedColor = Color.Gray,
                checkmarkColor = Color.Black
            ),
            modifier = Modifier.padding(top = 2.dp)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = buildAnnotatedString {
                append("Я согласен на ")
                withStyle(
                    style = SpanStyle(
                        color = Color(0xFF76B6FE),
                        textDecoration = TextDecoration.Underline
                    )
                ) {
                    append("обработку персональных данных")
                }
            },
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White,
            modifier = Modifier
                .clickable { onPrivacyPolicyClick() }
                .padding(top = 2.dp)
        )
    }
}
