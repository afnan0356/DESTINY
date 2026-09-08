package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.data.CharacterData

@Composable
fun CharacterAvatar(
    skinTone: String,
    eyeStyle: String,
    eyeColor: String,
    browStyle: String,
    hairStyle: String,
    hairColor: String,
    facialHairStyle: String,
    facialHairColor: String,
    modifier: Modifier = Modifier,
    size: Dp = 140.dp
) {
    val skinHex = CharacterData.getColorHex(skinTone, CharacterData.SKIN_TONES, 0xFFE0AC84)
    val eyeHex = CharacterData.getColorHex(eyeColor, CharacterData.EYE_COLORS, 0xFF5A3825)
    val hairHex = CharacterData.getColorHex(hairColor, CharacterData.HAIR_COLORS, 0xFF1E1E1E)
    val facialHairHex = CharacterData.getColorHex(facialHairColor, CharacterData.HAIR_COLORS, hairHex)

    val skinColor = Color(skinHex)
    val eyeColorVal = Color(eyeHex)
    val hairColorVal = Color(hairHex)
    val facialHairColorVal = Color(facialHairHex)

    Box(
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF0F172A), Color(0xFF1E293B), Color(0xFF0F172A))
                )
            )
            .border(1.dp, Color(0xFF334155), RoundedCornerShape(20.dp))
            .testTag("character_avatar"),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size * 0.9f)) {
            val w = this.size.width
            val h = this.size.height
            val scaleX = w / 120f
            val scaleY = h / 120f

            fun sx(x: Float) = x * scaleX
            fun sy(y: Float) = y * scaleY

            // 1. Aura
            drawCircle(
                color = Color(0x3338BDF8),
                radius = sx(50f),
                center = Offset(sx(60f), sy(60f))
            )

            // 2. Neck
            val neckPath = Path().apply {
                moveTo(sx(48f), sy(76f))
                lineTo(sx(48f), sy(104f))
                lineTo(sx(72f), sy(104f))
                lineTo(sx(72f), sy(76f))
                close()
            }
            drawPath(neckPath, skinColor)

            // 3. Hair background (if Afro or Long Flow)
            if (hairStyle == "Afro") {
                drawCircle(
                    color = hairColorVal,
                    radius = sx(38f),
                    center = Offset(sx(60f), sy(46f))
                )
            } else if (hairStyle == "Long Flow") {
                val longHairBack = Path().apply {
                    moveTo(sx(24f), sy(40f))
                    quadraticBezierTo(sx(16f), sy(70f), sx(26f), sy(96f))
                    lineTo(sx(94f), sy(96f))
                    quadraticBezierTo(sx(104f), sy(70f), sx(96f), sy(40f))
                    close()
                }
                drawPath(longHairBack, hairColorVal)
            }

            // 4. Head base
            drawRoundRect(
                color = skinColor,
                topLeft = Offset(sx(32f), sy(30f)),
                size = Size(sx(56f), sy(60f)),
                cornerRadius = CornerRadius(sx(28f), sy(30f))
            )

            // 5. Ears
            drawOval(
                color = skinColor,
                topLeft = Offset(sx(27f), sy(52f)),
                size = Size(sx(8f), sy(15f))
            )
            drawOval(
                color = skinColor,
                topLeft = Offset(sx(85f), sy(52f)),
                size = Size(sx(8f), sy(15f))
            )

            // 6. Eyebrows
            val browStrokeWidth = when (browStyle) {
                "Thick Bushy" -> sx(4.8f)
                "Thin Curved" -> sx(1.8f)
                else -> sx(3.0f)
            }
            when (browStyle) {
                "Arched" -> {
                    val leftBrow = Path().apply {
                        moveTo(sx(34f), sy(51f))
                        quadraticBezierTo(sx(42f), sy(45f), sx(50f), sy(50f))
                    }
                    val rightBrow = Path().apply {
                        moveTo(sx(70f), sy(50f))
                        quadraticBezierTo(sx(78f), sy(45f), sx(86f), sy(51f))
                    }
                    drawPath(leftBrow, hairColorVal, style = Stroke(browStrokeWidth, cap = StrokeCap.Round))
                    drawPath(rightBrow, hairColorVal, style = Stroke(browStrokeWidth, cap = StrokeCap.Round))
                }
                else -> {
                    drawLine(
                        color = hairColorVal,
                        start = Offset(sx(34f), sy(50f)),
                        end = Offset(sx(50f), sy(50f)),
                        strokeWidth = browStrokeWidth,
                        cap = StrokeCap.Round
                    )
                    drawLine(
                        color = hairColorVal,
                        start = Offset(sx(70f), sy(50f)),
                        end = Offset(sx(86f), sy(50f)),
                        strokeWidth = browStrokeWidth,
                        cap = StrokeCap.Round
                    )
                }
            }

            // 7. Eyes
            val eyeRx = when (eyeStyle) {
                "Round" -> sx(6.5f)
                "Monolid" -> sx(7.5f)
                else -> sx(7f)
            }
            val eyeRy = when (eyeStyle) {
                "Round" -> sy(6f)
                "Hooded" -> sy(3.5f)
                "Monolid" -> sy(3.2f)
                else -> sy(4.5f)
            }

            // Left Eye
            drawOval(
                color = Color.White,
                topLeft = Offset(sx(42f) - eyeRx, sy(58f) - eyeRy),
                size = Size(eyeRx * 2, eyeRy * 2)
            )
            drawCircle(color = eyeColorVal, radius = sx(3.2f), center = Offset(sx(42f), sy(58f)))
            drawCircle(color = Color.Black, radius = sx(1.6f), center = Offset(sx(42f), sy(58f)))
            drawCircle(color = Color.White, radius = sx(0.8f), center = Offset(sx(43f), sy(57f)))

            // Right Eye
            drawOval(
                color = Color.White,
                topLeft = Offset(sx(78f) - eyeRx, sy(58f) - eyeRy),
                size = Size(eyeRx * 2, eyeRy * 2)
            )
            drawCircle(color = eyeColorVal, radius = sx(3.2f), center = Offset(sx(78f), sy(58f)))
            drawCircle(color = Color.Black, radius = sx(1.6f), center = Offset(sx(78f), sy(58f)))
            drawCircle(color = Color.White, radius = sx(0.8f), center = Offset(sx(79f), sy(57f)))

            // 8. Nose
            drawLine(
                color = Color(0x99A87046),
                start = Offset(sx(60f), sy(58f)),
                end = Offset(sx(58f), sy(68f)),
                strokeWidth = sx(1.4f),
                cap = StrokeCap.Round
            )
            drawLine(
                color = Color(0x99A87046),
                start = Offset(sx(58f), sy(68f)),
                end = Offset(sx(63f), sy(68f)),
                strokeWidth = sx(1.4f),
                cap = StrokeCap.Round
            )

            // 9. Mouth
            val mouthPath = Path().apply {
                moveTo(sx(52f), sy(76f))
                quadraticBezierTo(sx(60f), sy(80f), sx(68f), sy(76f))
            }
            drawPath(mouthPath, Color(0xFF8C4E3D), style = Stroke(sx(2f), cap = StrokeCap.Round))

            // 10. Facial Hair
            when (facialHairStyle) {
                "Full Beard" -> {
                    val beardPath = Path().apply {
                        moveTo(sx(34f), sy(66f))
                        quadraticBezierTo(sx(34f), sy(92f), sx(60f), sy(94f))
                        quadraticBezierTo(sx(86f), sy(92f), sx(86f), sy(66f))
                        quadraticBezierTo(sx(70f), sy(76f), sx(60f), sy(76f))
                        quadraticBezierTo(sx(50f), sy(76f), sx(34f), sy(66f))
                        close()
                    }
                    drawPath(beardPath, facialHairColorVal)
                }
                "Goatee" -> {
                    val goateePath = Path().apply {
                        moveTo(sx(50f), sy(74f))
                        quadraticBezierTo(sx(60f), sy(72f), sx(70f), sy(74f))
                        lineTo(sx(60f), sy(92f))
                        close()
                    }
                    drawPath(goateePath, facialHairColorVal)
                }
                "Classic Mustache" -> {
                    val stachePath = Path().apply {
                        moveTo(sx(46f), sy(72f))
                        quadraticBezierTo(sx(60f), sy(74f), sx(74f), sy(72f))
                        quadraticBezierTo(sx(60f), sy(77f), sx(46f), sy(72f))
                        close()
                    }
                    drawPath(stachePath, facialHairColorVal)
                }
                "Light Stubble" -> {
                    drawOval(
                        color = facialHairColorVal.copy(alpha = 0.35f),
                        topLeft = Offset(sx(44f), sy(72f)),
                        size = Size(sx(32f), sy(18f))
                    )
                }
            }

            // 11. Top Hair
            if (hairStyle != "Bald" && hairStyle != "Afro" && hairStyle != "Long Flow") {
                val hairPath = Path().apply {
                    moveTo(sx(28f), sy(48f))
                    quadraticBezierTo(sx(30f), sy(22f), sx(60f), sy(22f))
                    quadraticBezierTo(sx(90f), sy(22f), sx(92f), sy(48f))
                    quadraticBezierTo(sx(60f), sy(30f), sx(28f), sy(48f))
                    close()
                }
                drawPath(hairPath, hairColorVal)
            }
        }
    }
}
