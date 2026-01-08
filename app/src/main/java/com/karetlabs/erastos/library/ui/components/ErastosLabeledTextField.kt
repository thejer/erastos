package com.karetlabs.erastos.library.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.KeyboardActionHandler
import androidx.compose.foundation.text.input.OutputTransformation
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.karetlabs.erastos.library.ui.theme.CustomTypography
import com.karetlabs.erastos.library.ui.theme.dimension
import com.karetlabs.erastos.library.ui.theme.typography

/**
 * Shared DS component: label + ErastosTextField
 * - Internally uses TextFieldState to comply with ErastosTextField API
 * - Accepts external String value and syncs it into TextFieldState safely
 */
@Composable
fun ErastosLabeledTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    fieldLength: ITextFieldLength,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    colors: TextFieldColors,
    keyboardType: KeyboardType = KeyboardType.Text,
    keyboardCapitalization: KeyboardCapitalization = KeyboardCapitalization.None,
    enabled: Boolean = true,
    singleLine: Boolean = true,
    height: Dp = 56.dp,
    shape: Shape = RoundedCornerShape(dimension.radiusBase),
    contentPadding: PaddingValues = PaddingValues(horizontal = dimension.paddingMedium),
    labelStyle: TextStyle = CustomTypography.fieldLabel,
    textStyle: TextStyle = typography.bodyMedium,
    trailingIcon: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    onKeyboardAction: KeyboardActionHandler? = null,
    outputTransformation: OutputTransformation? = null,
    shouldRequestFocus: Boolean = false,
) {
    val dimens = dimension

    // Canonical state object for your ErastosTextField
    val state = rememberTextFieldState(initialText = value)

    // Sync external value -> state (avoids cursor jump loops)
    LaunchedEffect(value) {
        val current = state.text.toString()
        if (current != value) {
            state.setTextAndPlaceCursorAtEnd(value)
        }
    }

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = labelStyle,
            color = colorScheme.onSurfaceVariant
        )

        Spacer(Modifier.height(dimens.componentSpacingSmall))

        ErastosTextField(
            state = state,
            enabled = enabled,
            singleLine = singleLine,
            fieldLength = fieldLength,
            colors = colors,
            shape = shape,
            style = textStyle,
            placeholder = {
                placeholder?.let {
                    Text(
                        text = it,
                        style = textStyle,
                        color = colorScheme.onSurfaceVariant.copy(alpha = 0.32f)
                    )
                }
            },
            leadingIcon = leadingIcon,
            trailingIcon = trailingIcon,
            keyboardOptions = KeyboardOptions(
                capitalization = keyboardCapitalization,
                keyboardType = keyboardType
            ),
            onKeyboardAction = onKeyboardAction,
            outputTransformation = outputTransformation,
            shouldRequestFocus = shouldRequestFocus,
            height = height,
            contentPadding = contentPadding,
            // Use your built-in bridge to emit String updates
            onTextChanged = onValueChange
        )
    }
}