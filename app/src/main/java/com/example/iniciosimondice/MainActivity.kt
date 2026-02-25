package com.example.iniciosimondice

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.iniciosimondice.data.RoomDB
import com.example.iniciosimondice.ui.theme.SimonViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val dataBase = RoomDB.getDatabase(this)
        enableEdgeToEdge()
        setContent {
            val viewModel: SimonViewModel = viewModel(
                factory = object : ViewModelProvider.Factory {
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        return SimonViewModel(dataBase.recordDAO()) as T
                    }
                }
            )
            BotonesColores(viewModel)
        }
    }
}

val colores = Colores.entries

@Composable
fun BotonesColores(viewModel: SimonViewModel) {
    val iluminado = viewModel.iluminado
    val estadoJuego = viewModel.estadoJuego
    val ronda = viewModel.ronda
    val punto = viewModel.puntos
    val record = viewModel.record

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Record: $record")
        Text(viewModel.texto)
        Row {
            Text(text = "Puntos: $punto")
            Text(text = "Ronda: $ronda", modifier = Modifier.padding(16.dp), fontSize = 35.sp)
        }
        Row {
            repeat(2) { index ->
                Box(
                    modifier = Modifier
                        .padding(50.dp)
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(colores[index].color.copy(alpha = if (index == iluminado) 1f else 0.5f))
                        .clickable(enabled = estadoJuego == EstadoJuego.ESPERANDO_RESPUESTA) {
                            viewModel.validarSecuenciaVM(index)
                        }
                )
            }
        }
        Row {
            repeat(2) { index ->
                val pos = index + 2
                Box(
                    modifier = Modifier
                        .padding(50.dp)
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(colores[pos].color.copy(alpha = if (pos == iluminado) 1f else 0.5f))
                        .clickable(enabled = estadoJuego == EstadoJuego.ESPERANDO_RESPUESTA) {
                            viewModel.validarSecuenciaVM(pos)
                        }
                )
            }
        }
        Button(
            onClick = { viewModel.generarSecuencia() },
            enabled = estadoJuego == EstadoJuego.INICIO || estadoJuego == EstadoJuego.JUEGO_TERMINADO
        ) {
            Text(text = "Iniciar")
        }
    }
}
