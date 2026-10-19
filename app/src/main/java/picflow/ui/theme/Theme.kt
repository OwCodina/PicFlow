package picflow.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColors = lightColorScheme(
    primary = PicFlowPrimary,
    onPrimary = PicFlowOnPrimary,
    secondary = PicFlowSecondary
)

private val DarkColors = darkColorScheme(
    primary = PicFlowPrimaryDark,
    secondary = PicFlowSecondaryDark
)

@Composable
fun PicFlowTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        content = content
    )
}
