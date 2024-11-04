package com.prosdm64.scrollinfinito

// Clase de datos que representa una tarea
data class Tarea(
    var texto: String,               // Texto descriptivo de la tarea
    var esFavorita: Boolean = false, // Indica si la tarea está marcada como favorita
    var posicionOriginal: Int        // Almacena la posición original de la tarea en la lista
)
