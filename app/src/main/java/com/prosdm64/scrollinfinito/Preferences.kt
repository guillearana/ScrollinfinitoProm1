package com.prosdm64.scrollinfinito

import android.content.Context
import android.content.SharedPreferences

/**
 * Preferences administra las tareas almacenadas de manera persistente usando SharedPreferences.
 * @param context Contexto de la aplicación.
 */
class Preferences(context: Context) {

    companion object {
        const val PREFS_NAME = "myDatabase"  // Nombre de las preferencias
        const val TASKS = "tasks_value"  // Clave para almacenar las tareas
    }

    // Preferencias de la aplicación, donde se almacenan las tareas
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, 0)

    /**
     * Guarda la lista de tareas en las preferencias.
     * @param tasks Lista de tareas a guardar.
     */
    fun saveTasks(tasks: List<String>) {
        prefs.edit().putStringSet(TASKS, tasks.toSet()).apply()  // Convierte la lista a Set y guarda
    }

    /**
     * Recupera las tareas guardadas en las preferencias y las devuelve como lista mutable.
     * @return Lista mutable de tareas almacenadas.
     */
    fun getTasks(): MutableList<String> {
        return prefs.getStringSet(TASKS, emptySet())?.toMutableList() ?: mutableListOf()
    }
}