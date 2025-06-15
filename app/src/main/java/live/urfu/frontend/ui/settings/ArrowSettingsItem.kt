package live.urfu.frontend.ui.settings

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import live.urfu.frontend.R

@Composable
fun ArrowSettingsItem(
    title: String,
    currentValue: String? = null,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .padding(vertical = 5.dp)
            .clickable(onClick = onClick)
            .padding(vertical = 19.dp, horizontal = 20.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White,
            )

            Spacer(modifier = Modifier.width(10.dp))

            Row(verticalAlignment = Alignment.CenterVertically,) {
                if (!currentValue.isNullOrEmpty()) {
                    Text(
                        text = currentValue,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF8C8C8C),
                        modifier = Modifier.padding(end = 0.dp).weight(1f, fill = false),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }

                Image(
                    painter = painterResource(id = R.drawable.chevron_left),
                    contentDescription = "Arrow",
                    colorFilter = ColorFilter.tint(Color(0xFF8C8C8C)),
                    modifier = Modifier
                        .clickable { }
                        .graphicsLayer(scaleX = -1f)
                        .size(24.dp),
                )
            }
        }
    }
}
