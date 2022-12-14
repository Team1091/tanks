package com.team1091.tanks

import com.team1091.tanks.ai.AI
import com.team1091.tanks.ai.AdrianTankAi
import com.team1091.tanks.ai.BraedenTankAi
import com.team1091.tanks.ai.EthanTankAi
import com.team1091.tanks.ai.Mary
import com.team1091.tanks.entity.Faction
import com.team1091.tanks.entity.Pickup
import com.team1091.tanks.entity.Tank
import processing.core.PApplet
import processing.core.PConstants
import processing.core.PGraphics
import processing.core.PImage
import java.awt.Color
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

class TankSim : PApplet() {

    lateinit var tankImage: PImage
    lateinit var turretImage: PImage
    lateinit var shellImage: PImage
    lateinit var pickupImage: PImage
    lateinit var crossHairsImage: PImage

    lateinit var background: PGraphics

    lateinit var game: Game

    private val size = Vec2(800.0, 800.0)

    override fun settings() {
        size(size.x.toInt(), size.y.toInt())
    }

    override fun setup() {
        tankImage = loadImage("assets/tank.png")
        turretImage = loadImage("assets/turret.png")
        shellImage = loadImage("assets/shell.png")
        pickupImage = loadImage("assets/pickup.png")
        crossHairsImage = loadImage("assets/crosshairs.png")

        background = createGraphics(width, height)
        background.beginDraw()
        background.background(100)
        background.endDraw()


        // Add your tank here
        val ais = listOf(
//            DoNothingAi(),
            AdrianTankAi(),
            EthanTankAi(),
            BestTankEver(),
            BraedenTankAi(),
            Mary()
        )
        game = makeGame(ais)
    }

    override fun draw() {
        game.takeTurn(SECONDS_PER_FRAME)

        clear()
        imageMode(PConstants.CORNER)
        image(background, 0f, 0f)

        background.beginDraw()
        background.stroke(Color.DARK_GRAY.rgb)

        // draw tracks
        game.tanks.forEach { tank ->
            val leftF = Vec2(4.0, -6.0).rotate(tank.facing)
            val leftB = Vec2(-4.0, -6.0).rotate(tank.facing)
            val rightF = Vec2(4.0, 6.0).rotate(tank.facing)
            val rightB = Vec2(-4.0, 6.0).rotate(tank.facing)

            background.stroke(Color.DARK_GRAY.rgb)

            background.line(
                (tank.pos.x + leftF.x).toFloat(), (tank.pos.y + leftF.y).toFloat(),
                (tank.pos.x + leftB.x).toFloat(), (tank.pos.y + leftB.y).toFloat()
            )

            background.line(
                (tank.pos.x + rightF.x).toFloat(), (tank.pos.y + rightF.y).toFloat(),
                (tank.pos.x + rightB.x).toFloat(), (tank.pos.y + rightB.y).toFloat()
            )

        }
        background.endDraw()


        imageMode(CENTER)
        rectMode(CENTER)

        // draw target
        game.tanks.forEach { tank ->
            tank.targetPos?.let { pos ->
                val x = pos.x.toFloat()
                val y = pos.y.toFloat()
                tint(tank.faction.color.rgb)
                image(crossHairsImage, x, y)
            }
        }
        // render tanks
        game.tanks.forEach { tank ->
            tint(tank.faction.color.rgb)
            pushMatrix()
            translate(tank.pos.x.toFloat(), tank.pos.y.toFloat())

            text(tank.displayName, -30f, -10f)
            text("${tank.life} / ${tank.ammoCount}", -15f, 20f)
            rotate((tank.facing + Math.PI.toFloat() / 2.0).toFloat())
            image(tankImage, 0f, 0f)
            tint(Color.WHITE.rgb)
            rotate(tank.turretFacing.toFloat())
            image(turretImage, 0f, 0f)

            //Harsh's mods
            stroke(Color.green.rgb)
            line(0.0f, 0.0f, 0f, -70f) //line to see what direction tank is facing

//            stroke(Color.red.rgb)
//            line(
//                0.0f,
//                0.0f,
//                50 * sin(tank.targetDirection.toFloat()),
//                -50 * cos(tank.targetDirection.toFloat())
//            ) //line to see what direction it is trying to go
            popMatrix()


        }

        // draw projectile
        game.projectiles.forEach { projectile ->
            pushMatrix()
            translate(projectile.pos.x.toFloat(), projectile.pos.y.toFloat())
            rotate((projectile.facing + Math.PI.toFloat() / 2.0).toFloat())
            image(shellImage, 0f, 0f)
            popMatrix()
        }

        // draw pickups
        game.pickups.forEach { pickup ->
            pushMatrix()
            translate(pickup.pos.x.toFloat(), pickup.pos.y.toFloat())
            image(pickupImage, 0f, 0f)
            popMatrix()
        }

    }

}


fun makeGame(ais: List<AI>): Game {
    val size = Vec2(800.0, 800.0)

    val rotation = Random.nextDouble(Math.PI * 2)
    val game = Game(
        bounds = size,
        tanks = ais.shuffled().mapIndexed { i, ai ->
            val angle = rotation + (i * (Math.PI * 2) / (ais.size))
            Tank(
                ai = ai,
                life = TANK_MAX_LIFE,
                pos = Vec2(
                    START_RADIUS * cos(angle) + size.x / 2,
                    START_RADIUS * sin(angle) + size.y / 2
                ),
                facing = angle + Math.PI / 2,
                ammoCount = 5,
                faction = Faction.values()[i],
                targetPos = null
            )
        }.toMutableList(),
        pickups = (0 until MAX_PICKUPS).map {
            Pickup(
                Vec2(
                    x = Random.nextDouble(size.x),
                    y = Random.nextDouble(size.y)
                )
            )
        }.toMutableList()
    )

    return game
}