package com.rubensousa.android8composecrash

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.ViewStructure
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.AbstractComposeView

class Android8FixComposeView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : AbstractComposeView(context, attrs, defStyleAttr) {

    private val content = mutableStateOf<(@Composable () -> Unit)?>(null)

    @Suppress("RedundantVisibilityModifier")
    protected override var shouldCreateCompositionOnAttachedToWindow: Boolean = false
        private set

    init {
        if (isOreo()) {
            disableAssist()
        }
    }

    @Composable
    override fun Content() {
        content.value?.invoke()
    }

    override fun getAccessibilityClassName(): CharSequence {
        return javaClass.name
    }

    override fun dispatchProvideStructure(structure: ViewStructure?) {
        if (shouldDisableVirtualStructure()) {
            return
        }
        super.dispatchProvideStructure(structure)
    }

    override fun dispatchProvideAutofillStructure(structure: ViewStructure, flags: Int) {
        if (shouldDisableVirtualStructure()) {
            return
        }
        super.dispatchProvideAutofillStructure(structure, flags)
    }

    override fun onProvideVirtualStructure(structure: ViewStructure?) {
        if (shouldDisableVirtualStructure()) {
            return
        }
        super.onProvideVirtualStructure(structure)
    }

    override fun onProvideAutofillStructure(structure: ViewStructure?, flags: Int) {
        if (shouldDisableVirtualStructure()) {
            return
        }
        super.onProvideAutofillStructure(structure, flags)
    }

    /**
     * Set the Jetpack Compose UI content for this view.
     * Initial composition will occur when the view becomes attached to a window or when
     * [createComposition] is called, whichever comes first.
     */
    fun setContent(content: @Composable () -> Unit) {
        shouldCreateCompositionOnAttachedToWindow = true
        this.content.value = content
        if (isAttachedToWindow) {
            createComposition()
        }
    }

    private fun disableAssist() {
        try {
            val method = this::class.java.getMethod("setAssistBlocked", Boolean::class.java)
            method.invoke(this, true)
        } catch (error: Throwable) {
            // Nothing we can do
        }
    }

    /**
     * Android 8 doesn't seem to support this correctly for Compose accessibility notes, so turn it off
     */
    private fun shouldDisableVirtualStructure(): Boolean {
        return isOreo()
    }

    private fun isOreo(): Boolean = Build.VERSION.SDK_INT == 26

}
