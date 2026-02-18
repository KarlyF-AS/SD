package com.example.iniciosimondice

class SimonDice {
    val secuenciaComputador = mutableListOf<Int>()
    var secuenciaJugador = mutableListOf<Int>()

    fun agregarNuevoColor() : Int {
        val nuevoColor = (0..3).random()
        secuenciaComputador.add(nuevoColor)
        return nuevoColor
    }
    fun validarSecuencia(indexColor: Int) : Boolean {
        secuenciaJugador.add(indexColor)
        println(secuenciaJugador==secuenciaComputador.subList(0, secuenciaJugador.size))
        return secuenciaJugador==secuenciaComputador.subList(0, secuenciaJugador.size)
    }
}