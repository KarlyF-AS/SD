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
