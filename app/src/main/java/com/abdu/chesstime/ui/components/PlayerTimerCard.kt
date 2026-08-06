package com.abdu.chesstime.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.abdu.chesstime.timer.ChessPlayer
import com.abdu.chesstime.timer.TimeFormatter

@Composable
fun PlayerTimerCard(
    player: ChessPlayer,
    timeMillis: Long,
    moves: Int,
    isActive: Boolean,
    isGameOver: Boolean,
    isWinner: Boolean,
    onTap: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isBlack = player == ChessPlayer.BLACK

    val baseBackgroundColor = if (isBlack) {
        if (isActive) Color(0xFF1E261E) else Color(0xFF141416)
    } else {
        if (isActive) Color(0xFFE8F5E9) else Color(0xFFF4F4F6)
    }

    val animatedBgColor by animateColorAsState(
        targetValue = when {
            isGameOver && isWinner -> Color(0xFF2E7D32)
            isGameOver && !isWinner -> if (isBlack) Color(0xFF2A1515) else Color(0xFFFFEBEE)
            else -> baseBackgroundColor
        },
        animationSpec = tween(durationMillis = 250),
        label = "bgColorAnimation"
    )

    val textColor = if (isBlack) {
        if (isGameOver && !isWinner) Color(0xFFFF8A80) else Color(0xFFFFFFFF)
    } else {
        if (isGameOver && !isWinner) Color(0xFFC62828) else Color(0xFF111111)
    }

    val activeBorderColor = if (isActive && !isGameOver) {
        Color(0xFF4CAF50)
    } else if (isGameOver && isWinner) {
        Color(0xFFFFD700)
    } else {
        Color.Transparent
    }

    val infiniteTransition = rememberInfiniteTransition(label = "pulseTransition")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = if (isActive && !isGameOver) 1.02f else 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    Surface(
        modifier = modifier
            .fillMaxSize()
            .testTag(if (isBlack) "player_card_black" else "player_card_white")
            .clip(RoundedCornerShape(0.dp))
            .border(
                width = if (isActive || (isGameOver && isWinner)) 4.dp else 0.dp,
                color = activeBorderColor,
                shape = RoundedCornerShape(0.dp)
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onTap
            ),
        color = animatedBgColor
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .scale(pulseScale)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = if (isBlack) "BLACK" else "WHITE",
                        color = textColor.copy(alpha = 0.7f),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp
                    )

                    if (isActive && !isGameOver) {
                        Spacer(modifier = Modifier.size(8.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFF4CAF50))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = "Active Turn",
                                    tint = Color.White,
                                    modifier = Modifier.size(12.dp)
                                )
                                Text(
                                    text = "TURN",
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }
                        }
                    }

                    if (isWinner) {
                        Spacer(modifier = Modifier.size(8.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFFFFD700))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.MilitaryTech,
                                    contentDescription = "Winner",
                                    tint = Color.Black,
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = "WINNER",
                                    color = Color.Black,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = TimeFormatter.formatTime(timeMillis),
                    color = textColor,
                    fontSize = 68.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = (-1).sp,
                    modifier = Modifier.testTag(if (isBlack) "timer_text_black" else "timer_text_white")
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Moves: $moves",
                    color = textColor.copy(alpha = 0.6f),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

// Created by Abdughafur
// Source: https://github.com/Abdughafur/ChessTime
