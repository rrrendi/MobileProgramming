package com.app.pertemuan3

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.app.pertemuan3.ui.theme.Pertemuan3Theme

@Composable
fun Kolom() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Text("Nama: LM Rendi Gumilar Saputra")
        Text("Jurusan: Teknik Informatika")
        Text("Universitas: STMIK MARDIRA INDONESIA")
    }
}

@Preview(showBackground = true)
@Composable
fun KolomPreview(){
    Pertemuan3Theme{
        Kolom()
    }
}

@Composable
fun Profile(){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ){
        Box(
            modifier = Modifier
                .size(120.dp)
                .background(Color.LightGray),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Foto",
                color = Color.Black,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(
                text = "Nama:",
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "LM Rendi Gumilar Saputra"
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProfilePrevie() {
    Pertemuan3Theme {
        Profile()
    }
}