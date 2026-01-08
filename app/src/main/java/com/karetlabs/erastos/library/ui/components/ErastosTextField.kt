package com.karetlabs.erastos.library.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.KeyboardActionHandler
import androidx.compose.foundation.text.input.OutputTransformation
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.maxLength
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import com.karetlabs.erastos.library.ui.DevicePreviews
import com.karetlabs.erastos.library.ui.theme.ComponentDimensions
import com.karetlabs.erastos.library.ui.theme.ErastosTheme
import com.karetlabs.erastos.library.ui.theme.dimension
import com.karetlabs.erastos.library.ui.theme.typography


sealed interface ITextFieldLength {
    val max: Int
}

enum class TextFieldLength(override val max: Int) : ITextFieldLength {
    DEFAULT(500),
    NAME(50),
    PASSWORD(50),
    EMAIL(50),
    PHONE(20),
    SEARCH(20),
    ISBN(12);

    companion object {
        fun custom(length: Int): ITextFieldLength = CustomTextFieldLength(length)
    }
}

data class CustomTextFieldLength(override val max: Int) : ITextFieldLength

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ErastosTextField(
    modifier: Modifier = Modifier,
    state: TextFieldState,
    showError: Boolean = false,
    onTextChanged: ((String) -> Unit)? = null,
    colors: TextFieldColors,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    trailingIcon: @Composable (() -> Unit)? = null,
    singleLine: Boolean = true,
    enabled: Boolean = true,
    fieldLength: ITextFieldLength,
    shape: Shape = RoundedCornerShape(dimension.radiusMedium),
    placeholder: @Composable (() -> Unit)? = null,
    style: TextStyle = typography.bodyLarge,
    leadingIcon: (@Composable () -> Unit)? = null,
    onKeyboardAction: KeyboardActionHandler? = null,
    outputTransformation: OutputTransformation? = null,
    shouldRequestFocus: Boolean = false,
    height: Dp = ComponentDimensions.textFieldHeight,
    contentPadding: PaddingValues = PaddingValues(start = dimension.paddingMedium),
) {
    val fontSize = with(LocalDensity.current) {
        val fontScale = this.fontScale
        val textSize =
            if (fontScale > 1.0) style.fontSize.value + 2f else style.fontSize.value
        (textSize / fontScale).sp
    }

    val focusRequester = remember { FocusRequester() }

    if (shouldRequestFocus) {
        LaunchedEffect(Unit) { focusRequester.requestFocus() }
    }

    // Keep a simple bridge if callers still care about plain String changes
    LaunchedEffect(state, onTextChanged) {
        if (onTextChanged == null) return@LaunchedEffect
        snapshotFlow { state.text.toString() }.collect { onTextChanged(it) }
    }

    OutlinedTextField(
        state = state,
        enabled = enabled,
        readOnly = !enabled,
        isError = showError,
        textStyle = style.copy(fontSize = fontSize),
        label = null,
        placeholder = placeholder,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        keyboardOptions = keyboardOptions,
        onKeyboardAction = onKeyboardAction,
        lineLimits = if (singleLine) {
            TextFieldLineLimits.SingleLine
        } else {
            TextFieldLineLimits.MultiLine()
        },
        inputTransformation = InputTransformation.maxLength(fieldLength.max),
        outputTransformation = outputTransformation,
        shape = shape,
        colors = colors,
        contentPadding = contentPadding,
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = height)
            .onFocusChanged { }
            .focusRequester(focusRequester)
    )
}

@DevicePreviews
@Composable
private fun ErastosTextFieldPreview() {
    val state = rememberTextFieldState("")
    ErastosTheme {
        ErastosTextField(
            state = state,
            fieldLength = TextFieldLength.DEFAULT,
            colors = TextFieldDefaults.colors(),
            placeholder = { Text(text = "Search…", style = typography.bodyMedium) },
            height = ComponentDimensions.searchBarHeightCompact
        )
    }
}
