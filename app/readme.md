# Guía de Supervivencia

## 1. Patrón Singleton (Base de Datos)

**Qué es:** Una forma de asegurar que solo existe una copia de la base de datos en toda la app para no gastar memoria.

- **Dónde está:** En la clase `RoomDB.kt`.
- **Bloque clave:** El `companion object`.
- **Cómo explicarlo:** "He usado un `companion object` con una variable `@Volatile` llamada `INSTANCE`. Esto garantiza que si la base de datos ya está creada, se reutilice la misma".

```kotlin
// Ejemplo en RoomDB.kt
companion object {
    @Volatile
    private var INSTANCE: RoomDB? = null
    fun getDatabase(context: Context): RoomDB {
        return INSTANCE ?: synchronized(this) { ... }
    }
}
```
---
## 2. Máquina de Estados (Enum)

**Qué es:** El "cerebro" que decide qué puede hacer el usuario en cada momento.

- **Dónde está:** En `EstadoJuego.kt` y se usa en `SimonViewModel.kt`.
- **Cómo explicarlo:** "Uso un `enum class` para definir los momentos del juego: `INICIO`, `MOSTRANDO_SECUENCIA`, etc. En el `ViewModel`, la variable `estadoJuego` bloquea o permite clics según el estado actual".

```kotlin
// Bloqueo de clics según estado en MainActivity.kt
.clickable(enabled = estadoJuego == EstadoJuego.ESPERANDO_RESPUESTA) {
    viewModel.validarSecuenciaVM(index)
}
```
___
## 3. Persistencia de Datos (Room / SQLite)

**Qué es:** Guardar los puntos para que no se borren al cerrar la app.

- **Dónde está:** `RecordEntity.kt` (la tabla), `RecordDAO.kt` (las órdenes) y `RoomDB.kt` (la conexión).
- **Cómo explicarlo:** "Uso Room para gestionar SQLite. La entidad `RecordEntity` define los campos (`id`, `record`) y el DAO tiene las funciones `suspend` para insertar y leer datos sin bloquear la pantalla".

```kotlin
// RecordDAO.kt
@Insert(onConflict = OnConflictStrategy.REPLACE)
suspend fun insertRecord(record: RecordEntity)
```
---
## 4. Arquitectura MVVM

**Qué es:** Separar el dibujo (Vista) de la lógica (ViewModel).

- **Clases involucradas:**
    - **Model:** `SimonDice.kt` (Lógica pura de listas).
    - **ViewModel:** `SimonViewModel.kt` (Controla los tiempos y estados).
    - **View:** `MainActivity.kt` (Dibuja los botones con Jetpack Compose).
- **Cómo explicarlo:** "La Vista no sabe nada de lógica; solo observa las variables del `ViewModel`. El `ViewModel` se encarga de llamar a las Corrutinas y a la Base de Datos".

## 5. Estados de UI (State Management)

**Qué es:** Que la pantalla se refresque sola cuando cambian los puntos o el texto.

- **Dónde está:** En `SimonViewModel.kt`.
- **Cómo explicarlo:** "Utilizo `mutableStateOf`. Cuando cambio el valor de `puntos` o `estadoJuego` en el `ViewModel`, Compose detecta el cambio y redibuja la pantalla automáticamente".

```kotlin
// SimonViewModel.kt
var puntos by mutableStateOf(0)
var estadoJuego by mutableStateOf(EstadoJuego.INICIO)
```
---
## 6. Corrutinas (Cuenta atrás y Tiempos)

**Qué es:** Hacer cosas "en paralelo" para que la app no se congele.

- **Dónde está:** En las funciones `generarSecuencia` y `validarSecuenciaVM` del `ViewModel`.
- **Cómo explicarlo:** "Uso `viewModelScope.launch` para poder usar `delay()`. Esto permite que los colores se iluminen uno a uno con una pausa sin que la aplicación se detenga".

```kotlin
// SimonViewModel.kt
viewModelScope.launch {
    delay(500) // Pausa de medio segundo
    // ... lógica
}
// Ejemplo de EstadoJuego.kt con funciones
enum class EstadoJuego(
    val permiteClics: Boolean,  // Propiedad
    val mensaje: String         // Propiedad
) {
    INICIO(false, "Toca cualquier botón para empezar"),
    MOSTRANDO_SECUENCIA(false, "Mira la secuencia..."),
    ESPERANDO_RESPUESTA(true, "Tu turno!"),
    PERDISTE(false, "Game Over! Toca para reiniciar");
    
    // Función dentro del enum
    fun puedeInteractuar(): Boolean {
        return this == ESPERANDO_RESPUESTA
    }
    
    // Otra función útil
    fun esEstadoActivo(): Boolean {
        return this != PERDISTE && this != INICIO
    }
}

// Uso en MainActivity:
.clickable(enabled = estadoJuego.permiteClics) {
    viewModel.validarSecuenciaVM(index)
}
// o también:
.clickable(enabled = estadoJuego.puedeInteractuar()) {
    viewModel.validarSecuenciaVM(index)
}
```
---
### Ejercicios:
- cuenta atrás:

```kotlin 
  // 1. Añade esta variable arriba con las demás
var cuentaAtras by mutableStateOf(0)

// 2. Modifica la función generarSecuencia
fun generarSecuencia() {
    viewModelScope.launch {
        // ... (tu código de reiniciar juego si es necesario)
        
        // --- AQUÍ LA CUENTA ATRÁS ---
        for (i in 5 downTo 1) {
            cuentaAtras = i
            delay(1000) // Espera un segundo por número
        }
        cuentaAtras = 0 // Al terminar, la ocultamos o ponemos a 0
        // ----------------------------

        estadoJuego = EstadoJuego.MOSTRANDO_SECUENCIA
        // ... (el resto de tu lógica para mostrar colores)
    }
}
```
---
- guarda los 10 mejores:

```kotlin 
  // 1. En RecordEntity.kt:
@PrimaryKey(autoGenerate = true) val id: Int = 0

//2. En el ViewModel (actualizarRecord): Quita el id = 1.
// CAMBIO: Quita el id fijo para que cree una fila nueva cada vez
recordDAO.insertRecord(RecordEntity(maxRecord = puntos))

// 3.En el DAO (RecordDAO.kt): Cambia la consulta para que te devuelva la lista de los mejores.
@Query("SELECT * FROM record ORDER BY maxRecord DESC LIMIT 10")
suspend fun getTop10Records(): List<RecordEntity>
```
---
---
- Funciones en Enums
```kotlin 
  enum class EstadoJuego {
    // Cada estado debe implementar la función "mensaje" obligatoriamente
    INICIO {
        override fun mensaje(): String = "¡Bienvenido! Pulsa el botón para empezar."
    },
    MOSTRANDO_SECUENCIA {
        override fun mensaje(): String = "Mira con atención..."
    },
    ESPERANDO_RESPUESTA {
        override fun mensaje(): String = "¡Tu turno! Repite los colores."
    },
    JUEGO_TERMINADO {
        override fun mensaje(): String = "¡Oh no! Has fallado. Pulsa para reintentar."
    };

    // La función se declara como 'abstract' para que cada estado la rellene
    abstract fun mensaje(): String
// / Cambia tu variable 'texto' por esto:
val textoActualizado by derivedStateOf {
    estadoJuego.mensaje() // Llama directamente a la función del Enum
}
}
```
