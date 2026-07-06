package com.techuntried.accountsbasics2.ui.commons

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.techuntried.accountsbasics2.R
import com.techuntried.accountsbasics2.ui.game.CoinTag
import com.techuntried.accountsbasics2.ui.theme.BackgroundColor
import com.techuntried.accountsbasics2.ui.theme.BorderColor
import com.techuntried.accountsbasics2.ui.theme.CircularProgressColor
import com.techuntried.accountsbasics2.ui.theme.InputBackgroundColor
import com.techuntried.accountsbasics2.ui.theme.InputErrorColor
import com.techuntried.accountsbasics2.ui.theme.InputHintColor
import com.techuntried.accountsbasics2.ui.theme.InputIconColor
import com.techuntried.accountsbasics2.ui.theme.InputTextColor
import com.techuntried.accountsbasics2.ui.theme.MainText
import com.techuntried.accountsbasics2.ui.theme.PrimaryColor
import com.techuntried.accountsbasics2.ui.theme.ShimmerColor
import com.techuntried.accountsbasics2.ui.theme.ToolbarBackgroundColor
import com.techuntried.accountsbasics2.ui.theme.ToolbarContentColor
import com.techuntried.accountsbasics2.utils.debouncedClickable
import com.techuntried.accountsbasics2.utils.rememberDebouncedClick

@Composable
fun CommonToolbar(
    modifier: Modifier = Modifier,
    title: String,
    isNavigationIcon: Boolean = false,
    isTitleClickEnabled: Boolean = false,
    navigationIcon: Int = R.drawable.back_arrow_icon,
    onNavigationClick: () -> Unit = {},
    onTitleClick: () -> Unit = {},
    actions: List<ToolbarAction> = emptyList(),
    showOverflowMenu: Boolean = false,
    isBalance: Boolean = false,
    balance: Int? = null,
    contentColor: Color = ToolbarContentColor,
    onBalanceClick: () -> Unit = {}
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .height(56.dp) // Standard toolbar height
                .background(ToolbarBackgroundColor)
                .padding(horizontal = 10.dp)
        ) {
            // Back icon (left)
            if (isNavigationIcon) {
                IconButton(
                    onClick = rememberDebouncedClick { onNavigationClick() }, modifier = Modifier
                        .padding(end = 4.dp)
                        .align(Alignment.CenterVertically)
                        .size(36.dp)
                ) {
                    Icon(
                        painter = painterResource(id = navigationIcon),
                        contentDescription = "Back",
                        tint = contentColor,
                        modifier = Modifier
                            .size(24.dp)

                    )
                }
            } else {
                Spacer(modifier = Modifier.width(6.dp))
            }

            // Title (center)
            Text(
                text = title,
                modifier = Modifier
                    .weight(1f)
                    .align(Alignment.CenterVertically),
                color = contentColor,
                style = MaterialTheme.typography.headlineSmall,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )

            // Right icon
            if (!showOverflowMenu) {
                actions.forEach { action ->
                    IconButton(onClick = action.onClick) {
                        Icon(
                            painter = painterResource(action.icon),
                            contentDescription = action.contentDescription,
                            tint = contentColor
                        )
                    }
                }
            } else {
                var expanded by remember { mutableStateOf(false) }

                Box(
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                ) {
                    IconButton(
                        onClick = { expanded = true }, modifier = Modifier
                            .size(36.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.menu_icon),
                            tint = Color.White,
                            contentDescription = "More",
                            modifier = Modifier
                                .size(24.dp)

                        )
                    }

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier.background(Color.White)
                    ) {
                        actions.forEach { action ->
                            DropdownMenuItem(
                                modifier = Modifier
                                    .height(36.dp)
                                    .background(Color.White),
                                text = {
                                    Text(
                                        action.contentDescription ?: "",
                                        style = MaterialTheme.typography.labelMedium,
                                    )
                                },
                                onClick = {
                                    expanded = false
                                    action.onClick()
                                }
                            )
                        }
                    }
                }

            }


            if (isBalance) {
                BalanceLayout(
                    onClick = onBalanceClick,
                    balance = balance,
                    modifier = Modifier
                        .padding(end = 6.dp)
                        .align(Alignment.CenterVertically)
                )
            }
        }
        HorizontalDivider(modifier = Modifier.align(Alignment.BottomCenter), color = BorderColor)
    }
}

data class ToolbarAction(
    val icon: Int,
    val contentDescription: String? = null,
    val onClick: () -> Unit
)

@Composable
fun CommonButton(
    modifier: Modifier = Modifier,
    text: String,
    backgroundColor: Color = PrimaryColor,
    contentColor: Color = Color.White,
    shape: Shape = RoundedCornerShape(30.dp),
    height: Dp = 50.dp,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    onClick: () -> Unit = {}
) {
    Button(
        onClick = rememberDebouncedClick { onClick() },
        modifier = modifier
            .fillMaxWidth()
            .height(height),
        enabled = enabled,
        shape = shape,
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor,
            contentColor = Color.White,
            disabledContainerColor = Color.LightGray,
            disabledContentColor = Color.DarkGray
        )
    ) {
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.size(36.dp), color = contentColor)
        } else {
            Text(text = text, color = contentColor, style = MaterialTheme.typography.titleMedium)
        }
    }
}

@Composable
fun CommonButton(
    modifier: Modifier = Modifier,
    text: String = "Okay",
    enabled: Boolean = true,
    backgroundColorGradients: List<Color>,
    textColor: Color = BackgroundColor,
    shape: RoundedCornerShape = RoundedCornerShape(10.dp),
    onClick: () -> Unit = {},
) {
    Button(
        onClick = rememberDebouncedClick { onClick() },
        enabled = enabled,
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
            .clip(shape)
            .background(
                brush = Brush.linearGradient(
                    colors = backgroundColorGradients, // Purple → Orange
                    start = Offset(0f, 0f),
                    end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
                )
            ),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent, // 🔥 important
            disabledContainerColor = Color.Transparent
        ),
        shape = shape
    ) {
        Text(
            text = text,
            color = textColor,
            style = MaterialTheme.typography.titleMedium
        )
    }
}

@Composable
fun CommonTextButton(
    modifier: Modifier = Modifier,
    text: String,
    contentColor: Color = Color.White,
    shape: Shape = RoundedCornerShape(30.dp),
    height: Dp = 50.dp,
    enabled: Boolean = true,
    onClick: () -> Unit = {}
) {
    TextButton(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(height),
        enabled = enabled,
        shape = shape,
    ) {
        Text(text = text, color = contentColor, style = MaterialTheme.typography.titleMedium)
    }
}

@Composable
fun TryAgainButton(
    modifier: Modifier = Modifier,
    text: String,
    coinCost: Int = 25,
    enabled: Boolean = true,
    backgroundColor: Color = Color.White,
    textColor: Color = BackgroundColor,
    disabledContainerColor: Color = Color.White,
    shape: RoundedCornerShape = RoundedCornerShape(10.dp),
    onClick: () -> Unit,
) {
    Box(modifier = modifier) {

        Button(
            onClick = rememberDebouncedClick { onClick() },
            enabled = enabled,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = shape,
            colors = ButtonDefaults.buttonColors(
                containerColor = backgroundColor,
                disabledContainerColor = disabledContainerColor
            )
        ) {
            Text(
                text = text,
                color = textColor,
                style = MaterialTheme.typography.titleMedium,
            )
        }

        // 🪙 Coin tag overlay
        CoinTag(
            coins = coinCost,
            enabled = enabled,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = (-6).dp, y = (-6).dp)
        )
    }
}

@Composable
fun TryAgainAdButton(
    modifier: Modifier = Modifier,
    text: String,
    enabled: Boolean = true,
    backgroundColor: Color = Color.White,
    textColor: Color = BackgroundColor,
    disabledContainerColor: Color = Color.White,
    shape: RoundedCornerShape = RoundedCornerShape(10.dp),
    onClick: () -> Unit,
) {
    Box(modifier = modifier) {

        Button(
            onClick = rememberDebouncedClick { onClick() },
            enabled = enabled,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = shape,
            colors = ButtonDefaults.buttonColors(
                containerColor = backgroundColor,
                disabledContainerColor = disabledContainerColor
            )
        ) {
            Text(
                text = text,
                color = textColor,
                style = MaterialTheme.typography.titleMedium
            )
        }

        // 🪙 Coin tag overlay
        WatchAdTag(
            enabled = enabled,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = (-6).dp, y = (-6).dp)
        )
    }
}


@Composable
fun CommonCircularProgress(modifier: Modifier = Modifier, color: Color = CircularProgressColor) {
    CircularProgressIndicator(
        modifier = modifier,
        color = color
    )
}

@Preview
@Composable
fun ErrorMessageView(
    modifier: Modifier = Modifier,
    icon: Int = R.drawable.empty_progress_icon,
    errorTitle: String = "Error",
    description: String? = null,
    actionButton: String? = "Try Again",
    action: () -> Unit = {}
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = icon),
            contentDescription = null, // since accessibility="no"
            modifier = Modifier
                .size(150.dp)
        )

        Text(
            text = errorTitle,
            color = Color.Black,
            style = MaterialTheme.typography.titleMedium,
            maxLines = 1,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp)
        )

        description?.let {
            Text(
                text = it,
                color = Color.Black,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 4.dp, start = 12.dp, end = 12.dp)
            )
        }

        actionButton?.let {
            Text(
                text = it,
                color = Color.White,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(top = 8.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(PrimaryColor)
                    .debouncedClickable { action() }
                    .padding(vertical = 6.dp, horizontal = 12.dp)
            )
        }
    }
}


@Preview
@Composable
fun BalanceLayout(
    modifier: Modifier = Modifier,
    balance: Int? = 500,
    onClick: () -> Unit = {},
) {
    if (balance == null) {
        Box(
            modifier
                .width(64.dp)
                .height(32.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(
                    color = ShimmerColor,
                )
        ) {}
    } else {
        Row(
            modifier = modifier
                .clip(RoundedCornerShape(20.dp))
                .background(
                    color = Color.White,
                )
                .border(1.dp, BorderColor, RoundedCornerShape(20.dp))
                .clickable { onClick() }
                .padding(horizontal = 8.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Coin Icon
            Icon(
                painter = painterResource(id = R.drawable.coin),
                contentDescription = null,
                tint = Color.Unspecified,
                modifier = Modifier
                    .size(24.dp)
                    .zIndex(1f)
            )

            Text(
                text = balance.toString(),
                color = MainText,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .padding(start = 6.dp)
                    .defaultMinSize(minWidth = 20.dp),
                textAlign = TextAlign.Center
            )

        }
    }
}


@Preview
@Composable
fun CommonTextInput(
    modifier: Modifier = Modifier,
    value: String = "",
    onValueChange: (String) -> Unit = {},
    label: String? = null,
    placeHolder: String? = "",
    textColor: Color = InputTextColor,
    backgroundColor: Color = InputBackgroundColor,
    labelColor: Color = Color.White,
    isError: Boolean = false,
    errorMsg: String? = null,
    trailingIcon: ImageVector? = null,
    leadingIcon: ImageVector? = null,
    inputType: String = "name",
    maxLength: Int = 64,
    enabled: Boolean = true,
    imeAction: ImeAction = ImeAction.Next
) {
    var passwordVisible by remember { mutableStateOf(false) }

    val keyboardOptions = when (inputType.lowercase()) {
        "name" -> KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = imeAction
        )

        "email" -> KeyboardOptions(
            keyboardType = KeyboardType.Email,
            imeAction = imeAction
        )

        "number" -> KeyboardOptions(
            keyboardType = KeyboardType.Number,
            imeAction = imeAction
        )

        "password" -> KeyboardOptions(
            keyboardType = KeyboardType.Password,
            imeAction = imeAction
        )

        else -> KeyboardOptions.Default
    }

    val visualTransformation = if (inputType.lowercase() == "password" && !passwordVisible)
        PasswordVisualTransformation()
    else
        VisualTransformation.None

    Column(modifier = modifier) {
        label?.let {
            Text(
                text = label,
                color = labelColor,
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.padding(bottom = 4.dp)
            )
        }

        TextField(
            value = value,
            onValueChange = {
                if (it.length <= maxLength) {
                    onValueChange(it)
                }
            },
            placeholder = {
                Text(
                    placeHolder ?: "",
                    color = InputHintColor,
                    style = MaterialTheme.typography.bodyLarge
                )
            },
            singleLine = true,
            textStyle = MaterialTheme.typography.bodyLarge,
            enabled = enabled,
            leadingIcon = leadingIcon?.let {
                { Icon(imageVector = it, contentDescription = null, tint = InputIconColor) }
            },
            trailingIcon = {
                val icon =
                    if (passwordVisible) R.drawable.visibility_icon else R.drawable.visibility_off_icon
                when {
                    inputType.lowercase() == "password" -> {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                painter = painterResource(icon),
                                tint = InputIconColor,
                                contentDescription = if (passwordVisible) "Hide password" else "Show password"
                            )
                        }
                    }

                    trailingIcon != null -> {
                        Icon(
                            imageVector = trailingIcon,
                            contentDescription = null,
                            tint = InputIconColor
                        )
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, BorderColor, RoundedCornerShape(10.dp))
                .height(56.dp),
            shape = RoundedCornerShape(10.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = backgroundColor,
                unfocusedContainerColor = backgroundColor,
                unfocusedIndicatorColor = Color.Transparent,
                errorIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                disabledContainerColor = backgroundColor,
                disabledIndicatorColor = Color.Transparent,
                disabledLeadingIconColor = InputIconColor,
                disabledTrailingIconColor = InputIconColor,
                disabledTextColor = Color.Black,
                focusedTextColor = textColor,
                unfocusedTextColor = textColor,
            ),
            keyboardOptions = keyboardOptions,
            visualTransformation = visualTransformation,
        )

        if (isError && errorMsg != null) {
            Text(
                text = errorMsg,
                color = InputErrorColor,
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(start = 16.dp, top = 4.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommonDropdownInput(
    modifier: Modifier = Modifier,
    value: String = "",
    onValueChange: (String) -> Unit = {},
    label: String? = null,
    placeHolder: String? = null,
    placeholderStyle: TextStyle = TextStyle(color = Color.Gray),
    labelStyle: TextStyle = TextStyle.Default,
    textColor: Color = Color.Black,
    backgroundColor: Color = Color.White,
    labelColor: Color = Color.White,
    isError: Boolean = false,
    errorMsg: String? = null,
    options: List<String> = emptyList() // 👈 dropdown values
) {
    var expanded by remember { mutableStateOf(false) }


    Column(modifier = modifier) {
        label?.let {
            Text(
                text = it,
                color = labelColor,
                fontSize = 14.sp,
                style = labelStyle,
                modifier = Modifier.padding(bottom = 6.dp)
            )
        }

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {

            TextField(
                value = value,
                onValueChange = { }, // disabled, only selection allowed
                readOnly = true,
                singleLine = true,
                textStyle = TextStyle(
                    color = textColor,
                    fontSize = 16.sp
                ),
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                },
                placeholder = {
                    if (placeHolder != null) {
                        Text(text = placeHolder, style = placeholderStyle)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, BorderColor, RoundedCornerShape(10.dp))
                    .height(50.dp)
                    .menuAnchor(),
                shape = RoundedCornerShape(10.dp),
                isError = isError,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = backgroundColor,
                    unfocusedContainerColor = backgroundColor,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    errorIndicatorColor = Color.Transparent
                )
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                containerColor = Color.White
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option, color = Color.Black) },
                        onClick = {
                            onValueChange(option)
                            expanded = false
                        }
                    )
                }
            }
        }

        if (isError && errorMsg != null) {
            Text(
                text = errorMsg,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(start = 16.dp, top = 4.dp)
            )
        }
    }
}

@Composable
fun CommonButtonWrap(
    modifier: Modifier = Modifier,
    text: String,
    enabled: Boolean = true,
    backgroundColor: Color = Color.White,
    textColor: Color = BackgroundColor,
    height: Dp = 48.dp,
    textSize: TextUnit = 16.sp,
    shape: RoundedCornerShape = RoundedCornerShape(10.dp),
    onClick: () -> Unit,
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .height(height),
        shape = shape,
        colors = ButtonDefaults.buttonColors(containerColor = backgroundColor)
    ) {
        Text(
            text = text,
            color = textColor,
            style = MaterialTheme.typography.titleMedium.copy(fontSize = textSize)
        )
    }
}


@Composable
fun AppText(
    modifier: Modifier = Modifier,
    text: String
) {
    Text(text = text, modifier = modifier)
}

@Composable
fun AppLinearProgress(modifier: Modifier = Modifier, progress: Float) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 500) // adjust speed here
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(12.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(Color.LightGray.copy(alpha = 0.5f))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(animatedProgress)
                .fillMaxHeight()
                .clip(RoundedCornerShape(10.dp))
                .background(MainText)
        )
    }
}