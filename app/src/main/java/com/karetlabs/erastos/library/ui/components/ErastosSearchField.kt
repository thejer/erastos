package com.karetlabs.erastos.library.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.karetlabs.erastos.R
import com.karetlabs.erastos.library.ui.DevicePreviews
import com.karetlabs.erastos.library.ui.theme.ErastosTheme
import com.karetlabs.erastos.library.ui.theme.dimension
import com.karetlabs.erastos.library.ui.theme.typography

@Composable
fun ErastosSearchField(
    modifier: Modifier = Modifier,
    state: TextFieldState,
    placeholderText: String = stringResource(R.string.search),
    placeHolderStyle: TextStyle = typography.bodyMedium,
    isEnabled: Boolean = true,
    onClick: () -> Unit = {},
    onFocusEvent: (hasFocus: Boolean) -> Unit,
    onTextChanged: (String) -> Unit
) {
    val focusRequester = remember { FocusRequester() }

    ErastosTextField(
        modifier = modifier
            .heightIn(min = 44.dp)
            .clip(RoundedCornerShape(dimension.radiusXl))
            .clickable(onClick = onClick)
            .focusRequester(focusRequester)
            .onFocusEvent {
                onFocusEvent(it.hasFocus)
            },
        state = state,
        enabled = isEnabled,
        onTextChanged = onTextChanged,
        shape = RoundedCornerShape(dimension.radiusXl),
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            focusedContainerColor = colorScheme.surfaceVariant,
            unfocusedContainerColor = colorScheme.surfaceVariant,
            disabledContainerColor = colorScheme.surfaceVariant,
        ),
        fieldLength = TextFieldLength.SEARCH,
        leadingIcon = {
            Icon(
                painter = getPainter(R.drawable.ic_search),
                contentDescription = stringResource(R.string.search),
                modifier = Modifier.size(20.dp),
                tint = colorScheme.onSurfaceVariant
            )
        },
        placeholder = {
            Text(
                text = placeholderText,
                style = placeHolderStyle,
                color = colorScheme.onSurfaceVariant
            )
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Done
        ),
        style = typography.bodyMedium,
    )
}

@DevicePreviews
@Composable
private fun ErastosSearchFieldPreview() {
    ErastosTheme {
        val state = rememberTextFieldState()

        ErastosSearchField(
            state = state,
            onFocusEvent = {},
            onTextChanged = {}
        )
    }
}