package com.example.game.systems

import com.example.game.model.ImpactEffect
import com.example.game.model.MathParticle

class ParticleSystem {

    fun update(
        dt: Float,
        particles: MutableList<MathParticle>,
        impacts: MutableList<ImpactEffect>
    ) {
        // Update particles
        val pIt = particles.iterator()
        while (pIt.hasNext()) {
            val p = pIt.next()
            p.lifetime -= dt
            if (p.lifetime <= 0f) {
                pIt.remove()
                continue
            }
            p.pos.x += p.vel.x * dt
            p.pos.y += p.vel.y * dt
            p.rotation += p.vRot * dt
            p.alpha = (p.lifetime / p.maxLife).coerceIn(0f, 1f)
        }

        // Update impacts
        val impIt = impacts.iterator()
        while (impIt.hasNext()) {
            val imp = impIt.next()
            imp.progress += dt / imp.duration
            if (imp.progress >= 1.0f) {
                impIt.remove()
                continue
            }
            imp.radius = imp.maxRadius * imp.progress
            imp.alpha = (1.0f - imp.progress).coerceIn(0f, 1f)
        }
    }
}
