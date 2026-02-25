package com.example.iniciosimondice.ui.theme

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.iniciosimondice.EstadoJuego
import com.example.iniciosimondice.SimonDice
import com.example.iniciosimondice.data.DAO.RecordDAO
import com.example.iniciosimondice.data.Entity.RecordEntity
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SimonViewModel(private val recordDAO: RecordDAO) : ViewModel() {
    val juego = SimonDice()
    var ronda by mutableStateOf(0)
    var puntos by mutableStateOf(0)
    var record by mutableStateOf(0)

    init {
        viewModelScope.launch {
            record = recordDAO.getMaxRecord() ?: 0
        }
    }

    val texto by derivedStateOf {
        when (estadoJuego) {
            EstadoJuego.INICIO -> "Presiona el botón para iniciar"
            EstadoJuego.MOSTRANDO_SECUENCIA -> "Observa la secuencia"
            EstadoJuego.ESPERANDO_RESPUESTA -> "Repite la secuencia"
            EstadoJuego.JUEGO_TERMINADO -> "¡Juego terminado! Presiona para reiniciar"
        }
    }
    var estadoJuego by mutableStateOf(EstadoJuego.INICIO)
    var iluminado by mutableStateOf(-1)

    fun generarSecuencia() {
        viewModelScope.launch {
            if (estadoJuego == EstadoJuego.JUEGO_TERMINADO) {
                juego.reiniciarJuego()
                ronda = 0
                puntos = 0
            }
            estadoJuego = EstadoJuego.MOSTRANDO_SECUENCIA
            juego.agregarNuevoColor()
            ronda++
            juego.secuenciaJugador.clear()
            delay(500)
            for (colorIndex in juego.secuenciaComputador) {
                iluminado = colorIndex
                delay(300)
                iluminado = -1
                delay(300)
            }
            estadoJuego = EstadoJuego.ESPERANDO_RESPUESTA
        }
    }

    fun validarSecuenciaVM(indexColor: Int) {
        if (estadoJuego != EstadoJuego.ESPERANDO_RESPUESTA) return
        val resultado = juego.validarSecuencia(indexColor)
        if (!resultado) {
            estadoJuego = EstadoJuego.JUEGO_TERMINADO
            actualizarRecord()
        } else if (juego.secuenciaJugador.size == juego.secuenciaComputador.size) {
            viewModelScope.launch {
                delay(500)
                puntos++
                generarSecuencia()
            }
        }
    }

    private fun actualizarRecord() {
        if (puntos > record) {
            record = puntos
            viewModelScope.launch {
                recordDAO.insertRecord(RecordEntity(id = 1, maxRecord = record))
            }
        }
    }
}
