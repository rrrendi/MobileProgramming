package com.app.dreamboard.presentation.sign_in

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.dreamboard.R // Pastikan import R sesuai package kamu

@Composable
fun SignInScreen(
    state: SignInState,
    onSignInClick: () -> Unit
) {
    val context = LocalContext.current

    // State untuk Animasi Masuk
    var isVisible by remember { mutableStateOf(false) }

    // Trigger animasi saat layar dibuka
    LaunchedEffect(Unit) {
        isVisible = true
    }

    // Tampilkan Error jika ada
    LaunchedEffect(key1 = state.signInError) {
        state.signInError?.let { error ->
            Toast.makeText(context, error, Toast.LENGTH_LONG).show()
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // 1. BACKGROUND IMAGE (bg3)
        Image(
            painter = painterResource(id = R.drawable.bg3), // Pastikan file bg3 ada di drawable
            contentDescription = "Background",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // 2. OVERLAY GRADIENT (Agar teks terbaca jelas)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.3f), // Sedikit gelap di tengah
                            Color.Black.copy(alpha = 0.7f)  // Gelap di bawah
                        )
                    )
                )
        )

        // 3. KONTEN UTAMA (Tengah Layar)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Bottom, // Konten di bawah biar estetik
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Animasi Judul & Slogan
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(animationSpec = tween(1000)) + slideInVertically(initialOffsetY = { 50 })
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    // JUDUL APLIKASI
                    Text(
                        text = "Dream Board",
                        style = MaterialTheme.typography.displayMedium.copy(
                            fontFamily = FontFamily.Serif, // Font Serif = Kesan Dreamy/Mewah
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // SLOGAN
                    Text(
                        text = "Visualize. Believe. Achieve.\nYour future starts here.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White.copy(alpha = 0.8f),
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Animasi Kartu Login
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(animationSpec = tween(1500)) + slideInVertically(initialOffsetY = { 100 })
            ) {
                // KARTU GLASSMORPHISM
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White.copy(alpha = 0.15f) // Putih Transparan (Glass)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Welcome Back",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // TOMBOL LOGIN CUSTOM
                        Button(
                            onClick = onSignInClick,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.White, // Tombol Putih Bersih
                                contentColor = Color.Black    // Teks Hitam
                            ),
                            elevation = ButtonDefaults.buttonElevation(8.dp)
                        ) {
                            // Icon Google (Pakai resource default atau text saja jika belum ada icon)
                            // Jika punya icon google: Icon(painter = painterResource(id = R.drawable.ic_google), ...)
                            Text(
                                text = "Continue with Google",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(50.dp)) // Jarak ke bawah layar
        }

        // LOADING INDICATOR (Jika sedang proses login)
        if (state.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color.White)
            }
        }
    }
}