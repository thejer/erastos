package com.karetlabs.erastos.library.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import com.karetlabs.erastos.library.ui.DevicePreviews
import com.karetlabs.erastos.library.ui.theme.ComponentDimensions
import com.karetlabs.erastos.library.ui.theme.CustomTypography
import com.karetlabs.erastos.library.ui.theme.ErastosTheme
import com.karetlabs.erastos.library.ui.theme.dimension
import com.karetlabs.erastos.library.ui.theme.typography

@Composable
fun ErastosLabeledPasswordField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    colors: TextFieldColors,
    enabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    var isPasswordVisible by remember { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = CustomTypography.fieldLabel,
            color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(dimension.componentSpacingSmall))
        OutlinedTextField(
            value = value,
            onValueChange = { onValueChange(it.take(TextFieldLength.PASSWORD.max)) },
            enabled = enabled,
            singleLine = true,
            textStyle = typography.bodyMedium,
            visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            trailingIcon = {
                TextButton(
                    onClick = { isPasswordVisible = !isPasswordVisible },
                    enabled = enabled,
                    contentPadding = PaddingValues(horizontal = dimension.paddingSmall)
                ) {
                    Text(
                        text = if (isPasswordVisible) "Hide" else "Show",
                        style = CustomTypography.metadata
                    )
                }
            },
            colors = colors,
            shape = RoundedCornerShape(dimension.radiusBase),
            modifier = Modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = ComponentDimensions.textFieldHeight)
        )
    }
}

@DevicePreviews
@Composable
private fun ErastosLabeledPasswordFieldPreview() {
    ErastosTheme {
        ErastosLabeledPasswordField(
            label = "Password",
            value = "sample-password",
            onValueChange = {},
            colors = androidx.compose.material3.TextFieldDefaults.colors()
        )
    }
}
