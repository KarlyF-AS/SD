package com.example.iniciosimondice

import android.os.Bundle
import android.util.Log
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.iniciosimondice.ui.theme.InicioSimonDiceTheme
import com.example.iniciosimondice.ui.theme.SimonViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
                botonesColores()
            /*InicioSimonDiceTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }*/
        }
    }
}
val colores = Colores.entries
@Composable
fun botonesColores() {
    val viewModel = remember { SimonViewModel() }
    val iluminado = viewModel.iluminado
    val estadoJuego = viewModel.estadoJuego
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center
    ) {
        Text(viewModel.texto)
        Row {
            repeat(2) {
                index ->
                Box(
                    modifier = Modifier
                        .padding(50.dp)
                        .size(100.dp)
                        .background(colores[index].color.copy(alpha = if (index == iluminado) 1f else 0.5f), CircleShape)
                        .clickable(enabled = estadoJuego == EstadoJuego.ESPERANDO_RESPUESTA) {
                         viewModel.validarSecuenciaVM(index)
                        }
                )
            }
        }
        Row {
            repeat(2) {
                    index ->
                    val pos = index + 2
            Box(
                    modifier = Modifier
                        .padding(50.dp)
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(colores[pos].color.copy(alpha = if (pos == iluminado) 1f else 0.5f))
                        .clickable(enabled = estadoJuego == EstadoJuego.ESPERANDO_RESPUESTA) {
                            viewModel.validarSecuenciaVM(index)                        }
                )
            }
        }
        Button(
            onClick = {viewModel.generarSecuencia()}
        ) {
            Text(text = "Iniciar")
        }
    }
    @Composable
    fun Greeting(name: String, modifier: Modifier = Modifier) {
        Text(
            text = "Hello $name!",
            modifier = modifier
        )
    }


    @Composable
    fun GreetingPreview() {
        InicioSimonDiceTheme {
            Greeting("Android")
        }
    }
}