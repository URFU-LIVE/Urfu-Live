package com.example.urfulive.ui.profile
import android.media.Image
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.urfulive.R

@Composable
fun Profile(viewModel: ProfileViewModel = viewModel()) {
    Image(
        painter = painterResource(id = R.drawable.ava),
        contentDescription = "Author Icon",
    )
}