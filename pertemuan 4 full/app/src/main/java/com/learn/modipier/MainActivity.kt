package com.learn.modipier

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.learn.modipier.ui.theme.ModipierTheme
import androidx.compose.material.icons.outlined.ChatBubbleOutline

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ModipierTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    PostCard(
                        modifier = Modifier.padding(innerPadding),
                        onPostClick = { }
                    )
                }
            }
        }
    }
}

@Composable
fun PostCard(
    modifier: Modifier = Modifier,
    onPostClick: () -> Unit
) {
    var showCommentSection by remember { mutableStateOf(false) }

    val images = listOf(
        R.drawable.post3,
        R.drawable.post2,
        R.drawable.post1
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 6.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Black, // atau Color(0xFF121212) untuk warna gelap kustom
        )
    ) {
        Column(
            modifier = Modifier
                .wrapContentHeight()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onPostClick() },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.pfp),
                        contentDescription = "Avatar",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                    )
                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = "Jang Jenitsu",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "@jang_jenitsu",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }
                }
                var isFollowing by remember { mutableStateOf(false) }

                Button(
                    modifier = Modifier
                        .height(30.dp),
                    onClick = { isFollowing = !isFollowing },
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    border = BorderStroke(1.dp,
                        if (isFollowing) Color.White else Color.Black),
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isFollowing) Color.Black else Color.White
                    )
                ) {
                    Text(
                        text = if (isFollowing) "Mengikuti" else "Ikuti",
                        style = MaterialTheme.typography.labelMedium,
                        color = if (isFollowing) Color.White else Color.Black,
                        fontWeight = FontWeight.Bold,
                    )
                }
                Text(
                    modifier = Modifier
                        .padding(horizontal = 8.dp),
                    text = "⋮",
                    color = Color.White
                )
            }
            Text(
                text = "Sesakit apapun itu, diam dan tahanlah, " +
                        "Jika kau seorang lelaki dan jika kau memang terlahir sebagai laki-laki.",
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.padding(vertical = 1.dp, horizontal = 2.dp),
                color = Color.White
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Swipeable Image Gallery
            SwipeableImageGallery(images = images, onImageClick = onPostClick)

            // Action Buttons Row



            // Row action (Like, Comment, Share)
            ActionButtonsRow(
                isCommentActive = showCommentSection,
                onCommentClick = {
                    showCommentSection = !showCommentSection
                }
            )

            if (showCommentSection) {
                CommentSection()
            }
            CommentSection()
        }
    }
}

@Composable
fun SwipeableImageGallery(images: List<Int>, onImageClick: () -> Unit) {
    val pagerState = rememberPagerState(pageCount = { images.size })

    Box {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxWidth()
        ) { page ->
            Image(
                painter = painterResource(id = images[page]),
                contentDescription = "Post Image ${page + 1}",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .height(220.dp)
                    .clickable(onClick = onImageClick)
            )
        }

        // Indicator dots
        if (images.size > 1) {
            Row(
                Modifier
                    .align(Alignment.BottomCenter)
                    .padding(8.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                repeat(images.size) { iteration ->
                    val color = if (pagerState.currentPage == iteration)
                        Color.White else Color.White.copy(alpha = 0.5f)
                    Box(
                        modifier = Modifier
                            .padding(2.dp)
                            .clip(CircleShape)
                            .size(8.dp)
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            drawCircle(color = color)
                        }
                    }
                }
            }
        }
    }
    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp) // Jarak antar ikon
    ) {
        Text(
            text = "8:10 • 15 Nov 25 •",
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray,
            modifier = Modifier
                .padding(vertical = 6.dp)
        )
        Text(
            text = "200rb",
            style = MaterialTheme.typography.bodySmall,
            color = Color.White,
            modifier = Modifier
                .padding(vertical = 6.dp)
        )
        Text(
            text = "Tayangan",
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray,
            modifier = Modifier
                .padding(vertical = 6.dp)
        )
    }
}

@Composable
fun ActionButtonsRow(
    isCommentActive: Boolean,
    onCommentClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        IconLike()
        IconComment(isActive = isCommentActive,
            onClick = onCommentClick)
        IconShare()
    }
}

@Composable
fun IconLike() {
    var isLiked by remember { mutableStateOf(false) }
    Button(
        modifier = Modifier
            .height(30.dp),
        onClick = { isLiked = !isLiked },
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
        border = BorderStroke(1.dp, if (isLiked) Color.Red else Color.LightGray),
        shape = RoundedCornerShape(20.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Black
        )
    ) {
        Icon(
            imageVector = if (isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
            contentDescription = "Like",
            modifier = Modifier.size(14.dp),
            tint = if (isLiked) Color.Red else Color.LightGray
        )
        Text(
            text = if (isLiked) "Disukai" else "Suka",
            modifier = Modifier.padding(start = 4.dp),
            style = MaterialTheme.typography.labelMedium,
            color = if (isLiked) Color.Red else Color.LightGray
        )
    }
}

@Composable
fun IconComment(
    isActive: Boolean,
    onClick: () -> Unit
) {
    Button(
        modifier = Modifier
            .height(30.dp),
        onClick = onClick,
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
        border = BorderStroke(1.dp,
            if (isActive) Color(0xFF1D9BF0) else Color.LightGray),
        shape = RoundedCornerShape(20.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Black
        )
    ) {
        Icon(
            imageVector = Icons.Outlined.ChatBubbleOutline,
            contentDescription = "Comment",
            modifier = Modifier.size(14.dp),
            tint = if (isActive) Color(0xFF1D9BF0) else Color.LightGray
        )
        Text(
            text = "Komentar",
            modifier = Modifier.padding(start = 4.dp),
            style = MaterialTheme.typography.labelMedium,
            color = if (isActive) Color(0xFF1D9BF0) else Color.LightGray
        )
    }
}

@Composable
fun IconShare() {
    var isShared by remember { mutableStateOf(false) }
    Button(
        modifier = Modifier
            .height(30.dp),
        onClick = { isShared = !isShared },
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
        border = BorderStroke(1.dp, if (isShared) Color.Green else Color.LightGray),
        shape = RoundedCornerShape(20.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Black
        )
    ) {
        Icon(
            imageVector = Icons.Default.Share,
            contentDescription = "Share",
            modifier = Modifier.size(14.dp),
            tint = if (isShared) Color.Green else Color.LightGray
        )
        Text(
            text = if (isShared) "Dibagikan" else "Bagikan",
            modifier = Modifier.padding(start = 4.dp),
            style = MaterialTheme.typography.labelMedium,
            color = if (isShared) Color.Green else Color.LightGray
        )
    }
}

@Composable
fun CommentSection() {
    Text(
        text = "Balasan yang paling relevan ▼",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(vertical = 12.dp, horizontal = 2.dp),
        color = Color.White
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier
                .weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.tanjiro),
                contentDescription = "Avatar",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Tanjori ",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "@tanjori • 5 menit",
                        style = MaterialTheme.typography.bodySmall, // Ukuran font lebih kecil
                        color = Color.Gray // Opsional: warna sedikit transparan
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp) // Jarak antar ikon
                ) {
                    Text(
                        text = "Membalas",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                    Text(
                        text = "@Jang_Jenitsu",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF1D9BF0)
                    )
                }
            }
        }
        Text(
            text = "⋮",
            color = Color.White
        )
    }
    Text(
        text = "Mantap bro",
        style = MaterialTheme.typography.bodySmall,
        modifier = Modifier
            .padding(vertical = 6.dp, horizontal = 2.dp)
            .offset(x = 50.dp), // Geser ke kanan 6.dp
        color = Color.White
    )
    Row(
        horizontalArrangement = Arrangement.spacedBy(45.dp) // Jarak antar ikon
    ) {
        Icon(
            imageVector = Icons.Default.FavoriteBorder,
            contentDescription = "Comment",
            modifier = Modifier
                .size(13.dp)
                .padding(horizontal = 1.dp)
                .offset(x = 53.dp),
            tint = Color.White
        )
        Icon(
            imageVector = Icons.Outlined.ChatBubbleOutline,
            contentDescription = "Comment",
            modifier = Modifier
                .size(13.dp)
                .padding(horizontal = 1.dp)
                .offset(x = 53.dp),
            tint = Color.White
        )
        Icon(
            imageVector = Icons.Outlined.BookmarkBorder,
            contentDescription = "Bookmark",
            modifier = Modifier
                .size(13.dp)
                .padding(horizontal = 1.dp)
                .offset(x = 53.dp),
            tint = Color.White
        )
        Icon(
            imageVector = Icons.Default.Share,
            contentDescription = "Comment",
            modifier = Modifier
                .size(13.dp)
                .padding(horizontal = 1.dp)
                .offset(x = 53.dp),
            tint = Color.White
        )
    }


    Divider(
        color = Color.Gray.copy(alpha = 0.2f),
        thickness = 1.dp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp)
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier
                .weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.nezuko),
                contentDescription = "Avatar",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Nezuki ",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "@nezuki • 30 menit",
                        style = MaterialTheme.typography.bodySmall, // Ukuran font lebih kecil
                        color = Color.Gray // Opsional: warna sedikit transparan
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp) // Jarak antar ikon
                ) {
                    Text(
                        text = "Membalas",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                    Text(
                        text = "@Jang_Jenitsu",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF1D9BF0)
                    )
                }
            }
        }
        Text(
            text = "⋮",
            color = Color.White
        )
    }
    Text(
        text = "Ah yang bener",
        style = MaterialTheme.typography.bodySmall,
        modifier = Modifier
            .padding(vertical = 6.dp, horizontal = 2.dp)
            .offset(x = 50.dp), // Geser ke kanan 6.dp
        color = Color.White
    )
    Row(
        horizontalArrangement = Arrangement.spacedBy(45.dp) // Jarak antar ikon
    ) {
        Icon(
            imageVector = Icons.Default.FavoriteBorder,
            contentDescription = "Comment",
            modifier = Modifier
                .size(13.dp)
                .padding(horizontal = 1.dp)
                .offset(x = 53.dp),
            tint = Color.White
        )
        Icon(
            imageVector = Icons.Outlined.ChatBubbleOutline,
            contentDescription = "Comment",
            modifier = Modifier
                .size(13.dp)
                .padding(horizontal = 1.dp)
                .offset(x = 53.dp),
            tint = Color.White
        )
        Icon(
            imageVector = Icons.Outlined.BookmarkBorder,
            contentDescription = "Bookmark",
            modifier = Modifier
                .size(13.dp)
                .padding(horizontal = 1.dp)
                .offset(x = 53.dp),
            tint = Color.White
        )
        Icon(
            imageVector = Icons.Default.Share,
            contentDescription = "Comment",
            modifier = Modifier
                .size(13.dp)
                .padding(horizontal = 1.dp)
                .offset(x = 53.dp),
            tint = Color.White
        )
    }

    Divider(
        color = Color.Gray.copy(alpha = 0.2f),
        thickness = 1.dp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp)
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier
                .weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.inosuke3),
                contentDescription = "Avatar",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Inosuki ",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "@inosuki • 1 jam",
                        style = MaterialTheme.typography.bodySmall, // Ukuran font lebih kecil
                        color = Color.Gray // Opsional: warna sedikit transparan
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp) // Jarak antar ikon
                ) {
                    Text(
                        text = "Membalas",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                    Text(
                        text = "@Jang_Jenitsu",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF1D9BF0)
                    )
                }
            }
        }
        Text(
            text = "⋮",
            color = Color.White
        )
    }
    Text(
        text = "Aku paling kuat",
        style = MaterialTheme.typography.bodySmall,
        modifier = Modifier
            .padding(vertical = 6.dp, horizontal = 2.dp)
            .offset(x = 50.dp), // Geser ke kanan 6.dp
        color = Color.White
    )
    Row(
        horizontalArrangement = Arrangement.spacedBy(45.dp) // Jarak antar ikon
    ) {
        Icon(
            imageVector = Icons.Default.FavoriteBorder,
            contentDescription = "Comment",
            modifier = Modifier
                .size(13.dp)
                .padding(horizontal = 1.dp)
                .offset(x = 53.dp),
            tint = Color.White
        )
        Icon(
            imageVector = Icons.Outlined.ChatBubbleOutline,
            contentDescription = "Comment",
            modifier = Modifier
                .size(13.dp)
                .padding(horizontal = 1.dp)
                .offset(x = 53.dp),
            tint = Color.White
        )
        Icon(
            imageVector = Icons.Outlined.BookmarkBorder,
            contentDescription = "Bookmark",
            modifier = Modifier
                .size(13.dp)
                .padding(horizontal = 1.dp)
                .offset(x = 53.dp),
            tint = Color.White
        )
        Icon(
            imageVector = Icons.Default.Share,
            contentDescription = "Comment",
            modifier = Modifier
                .size(13.dp)
                .padding(horizontal = 1.dp)
                .offset(x = 53.dp),
            tint = Color.White
        )
    }

    Divider(
        color = Color.Gray.copy(alpha = 0.2f),
        thickness = 1.dp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp)
    )
    Row(
        horizontalArrangement = Arrangement.spacedBy(40.dp) // Jarak antar ikon
    ) {
        Text(
            text = "Posting balasan Anda",
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier
                .weight(1f)
                .padding(vertical = 5.dp, horizontal = 2.dp),
            color = Color.Gray
        )
        Text(
            text = "\uD83D\uDCF8",
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier
                .padding(vertical = 5.dp, horizontal = 2.dp),
            color = Color.Gray
        )
    }
    Divider(
        color = Color.Gray.copy(alpha = 0.6f),
        thickness = 1.dp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp)
    )
}

@Preview(showBackground = true)
@Composable
fun PostCardPreview() {
    ModipierTheme {
        PostCard(onPostClick = { })
    }
}