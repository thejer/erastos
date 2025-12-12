package com.karetlabs.erastos.library.ui.components

import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.VectorDrawable
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.core.content.res.ResourcesCompat
import com.karetlabs.erastos.R

@Composable
fun getPainter(id: Int?): Painter {
    val context = LocalContext.current
    val isValidResource = id != null && id != 0 && id != -1 && context.isDrawableAvailable(id)
    return if (isValidResource) {
        painterResource(id = id)
    } else {
        painterResource(id = R.drawable.ic_empty)
    }
}

fun Context.isDrawableAvailable(id: Int): Boolean {
    return try {
        val drawable = ResourcesCompat.getDrawable(resources, id, null)
        val result = drawable != null && (drawable is VectorDrawable || drawable is BitmapDrawable)
        result
    } catch (e: Exception) {
        false
    }
}