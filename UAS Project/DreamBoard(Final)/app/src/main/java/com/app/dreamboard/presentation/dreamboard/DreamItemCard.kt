package com.app.dreamboard.presentation.dreamboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.app.dreamboard.data.model.DreamItem

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DreamItemCard(
    dream: DreamItem,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    // Logika Hitam Putih jika tercapai
    val colorMatrix = if (dream.isAchieved) {
        ColorMatrix().apply { setToSaturation(0f) }
    } else {
        null
    }

    // Warna Hijau Cerah
    val greenColor = Color(0xFF00E676)

    Card(
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier
            .padding(6.dp)
            .fillMaxWidth()
            .wrapContentHeight()
            .clip(RoundedCornerShape(20.dp))
            .combinedClickable(
                onClick = { onClick() },
                onLongClick = { onLongClick() }
            ),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        // Border Hijau jika tercapai
        border = if (dream.isAchieved) BorderStroke(1.dp, greenColor) else null
    ) {
        Box(modifier = Modifier.wrapContentSize()) {

            // 1. GAMBAR
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(dream.imageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = null,
                contentScale = ContentScale.FillWidth,
                colorFilter = if (dream.isAchieved) ColorFilter.colorMatrix(colorMatrix!!) else null,
                modifier = Modifier.fillMaxWidth()
            )

            // 2. GRADIENT
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f)),
                            startY = 150f
                        )
                    )
            )

            // 3. ICON CEKLIS MINIMALIS (Kembali ke gaya badge kecil)
            if (dream.isAchieved) {
                Surface(
                    color = greenColor, // Background Hijau
                    shape = CircleShape,
                    modifier = Modifier
                        .padding(12.dp)
                        .size(24.dp) // Ukuran kecil minimalis
                        .align(Alignment.TopEnd) // Pojok Kanan Atas
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Check,
                        contentDescription = "Tercapai",
                        tint = Color.White, // Icon Putih biar kontras dengan hijau
                        modifier = Modifier.padding(4.dp)
                    )
                }
            }

            // 4. JUDUL & LABEL
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp)
            ) {
                Text(
                    text = dream.title,
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    ),
                    maxLines = 3
                )

                if (dream.isAchieved) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "TERCAPAI ✨", // Tetap TERCAPAI
                        color = greenColor,   // Tetap Hijau
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }
            }
        }
    }
}