package com.curseclient.client.module.modules.visual

import com.curseclient.client.event.events.render.Render2DEvent
import com.curseclient.client.event.listener.safeListener
import com.curseclient.client.module.Category
import com.curseclient.client.module.Module
import com.curseclient.client.setting.setting
import com.curseclient.client.utility.misc.SoundUtils
import com.curseclient.client.utility.render.RenderUtils2D
import com.curseclient.client.utility.render.vector.Vec2d
import me.surge.animation.Animation
import me.surge.animation.Easing
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.potion.Potion
import net.minecraft.potion.PotionEffect
import java.awt.Color

object FullBright : Module(
    "FullBright",
    "Makes everything brighter",
    Category.VISUAL
) {

    val mode by setting("Mode", BrightnessMode.Gamma)
    private val fadeInOutDuration by setting("FadeInOutDuration", 1000.0, 0.0, 5000.0, 50.0)
    private val fade = Animation({ fadeInOutDuration.toFloat() }, false, Easing.LINEAR)

    init {
        safeListener<Render2DEvent> {
            val scaledResolution = ScaledResolution(mc)
            val alpha = (fade.getAnimationFactor() * 255).toInt().coerceIn(0, 255)
            RenderUtils2D.drawGradientRect(
                Vec2d(0, 0),
                Vec2d(scaledResolution.scaledWidth, scaledResolution.scaledHeight),
                Color(0, 255, 0, alpha),
                Color(65, 255, 0, alpha),
                Color(65, 255, 0, alpha),
                Color(0, 255, 0, alpha)
            )
        }
    }

    override fun onEnable() {
        val gamma = (fade.getAnimationFactor() * 1000).toFloat().coerceIn(0f, 1000f)
        when (mode.name) {
            "Gamma" -> {
                mc.gameSettings.gammaSetting = gamma
                applyPotionEffect(false)
            }
            "Potion" -> applyPotionEffect(true)
        }
        fade.resetToDefault()
        SoundUtils.playSound { "vision.wav" }
    }

    override fun onDisable() {
        fade.resetToDefault()
        mc.gameSettings.gammaSetting = 0f // This is why u need fullbright 😏
        applyPotionEffect(false)
    }

    private fun applyPotionEffect(apply: Boolean) {
        val potionEffect = Potion.getPotionById(16)

        potionEffect?.let {
            val effect = PotionEffect(it, Int.MAX_VALUE, 0, false, false)
            if (apply) {
                mc.player?.addPotionEffect(effect)
            } else {
                mc.player?.removePotionEffect(it)
            }
        }
    }

    enum class BrightnessMode {
        Gamma,
        Potion
    }
}