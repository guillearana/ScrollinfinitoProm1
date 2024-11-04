package com.prosdm64.scrollinfinito

import android.graphics.Canvas
import android.graphics.Color
import android.media.MediaPlayer
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {
    // Declaración de variables que se inicializan más adelante
    private lateinit var listaTareas: MutableList<Tarea> // Lista de tareas
    private lateinit var tareaAdapter: TareaAdapter // Adaptador para el RecyclerView
    private lateinit var tareaInput: EditText // Campo de entrada de texto
    private lateinit var btnAnadirTarea: Button // Botón para añadir tareas
    private lateinit var recyclerViewTareas: RecyclerView // RecyclerView para mostrar la lista de tareas
    private lateinit var mp: MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main) // Asocia el layout principal con esta actividad

        // Inicializamos la lista de tareas
        listaTareas = mutableListOf()

        // Enlazamos las vistas con los IDs del layout XML
        mp = MediaPlayer.create(this, R.raw.ding) // Carga el sonido
        tareaInput = findViewById(R.id.tareaInput)
        btnAnadirTarea = findViewById(R.id.btnAnadirTarea)
        recyclerViewTareas = findViewById(R.id.recyclerViewTareas)
        attachSwipeToDelete() // Configura la eliminación de tareas al deslizar

        // Inicialización del adaptador con las acciones de borrado y marcación de favoritas
        tareaAdapter = TareaAdapter(
            listaTareas,
            { position -> // Función para marcar o desmarcar una tarea como favorita
                val tarea = listaTareas[position]
                if (tarea.esFavorita) {
                    // Si la tarea ya es favorita, la desmarcamos y la devolvemos a su posición original
                    tarea.esFavorita = false
                    val nuevaPosicion = tarea.posicionOriginal
                    listaTareas.removeAt(position)
                    listaTareas.add(nuevaPosicion, tarea) // La movemos a su posición original
                    tareaAdapter.notifyItemMoved(position, nuevaPosicion) // Notificamos el movimiento
                } else {
                    // Si la tarea no es favorita, la marcamos como favorita y la movemos al principio de la lista
                    tarea.esFavorita = true
                    tarea.posicionOriginal = position // Guardamos la posición actual como original
                    listaTareas.removeAt(position)
                    listaTareas.add(0, tarea) // Movemos la tarea al principio de la lista
                    tareaAdapter.notifyItemMoved(position, 0) // Notificamos el movimiento
                }
                tareaAdapter.notifyDataSetChanged() // Notificamos que el conjunto de datos ha cambiado
            }
        )

        // Configuración del RecyclerView
        recyclerViewTareas.layoutManager = LinearLayoutManager(this) // Configuramos el layout lineal
        recyclerViewTareas.adapter = tareaAdapter // Asociamos el adaptador al RecyclerView

        // Listener para el botón de añadir tarea
        btnAnadirTarea.setOnClickListener {
            agregarTarea() // Llamada a la función para añadir una tarea
        }
    }

    // Función para agregar una nueva tarea a la lista
    private fun agregarTarea() {
        val nuevaTareaTexto = tareaInput.text.toString() // Obtenemos el texto del input
        if (!TextUtils.isEmpty(nuevaTareaTexto)) { // Verificamos que no esté vacío
            val nuevaTarea = Tarea(nuevaTareaTexto, false, listaTareas.size) // Creamos una nueva tarea con la posición inicial
            listaTareas.add(nuevaTarea) // Añadimos la nueva tarea a la lista
            mp.start() // Reproduce el sonido
            tareaAdapter.notifyItemInserted(listaTareas.size - 1) // Notificamos la inserción al adaptador
            tareaInput.setText("") // Limpiamos el campo de texto

        }
    }

    /**
     * Método que permite eliminar una tarea de la lista.
     * Notifica al adaptador que se ha eliminado un elemento y actualiza las preferencias.
     *
     * @param position La posición de la tarea a eliminar en la lista.
     */
    private fun deleteTask(position: Int) {
        listaTareas.removeAt(position) // Elimina la tarea de la lista
        tareaAdapter.notifyItemRemoved(position) // Notifica al adaptador que se ha eliminado el elemento
        tareaAdapter.notifyItemRangeChanged(position, listaTareas.size) // Actualizamos el rango
    }

    /**
     * Configura el gesto de deslizamiento para eliminar una tarea.
     */
    private fun attachSwipeToDelete() {
        val itemTouchHelper = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {

            /**
             * Método que maneja el movimiento de un elemento dentro del RecyclerView.
             * En este caso, no se utiliza, así que devuelve false.
             *
             * @param recyclerView El RecyclerView donde ocurre el movimiento.
             * @param viewHolder El ViewHolder del elemento que se está moviendo.
             * @param target El ViewHolder del elemento objetivo donde se está moviendo.
             * @return false porque no se usa el movimiento.
             */
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false // No se utiliza el movimiento vertical
            }

            /**
             * Método que se llama para dibujar el ítem mientras se desliza.
             * Cambia el color del fondo del ítem a rojo mientras se desliza.
             *
             * @param c Canvas donde se dibuja el RecyclerView.
             * @param recyclerView El RecyclerView.
             * @param viewHolder El ViewHolder del ítem que se desliza.
             * @param dX Desplazamiento en X durante el deslizamiento.
             * @param dY Desplazamiento en Y durante el deslizamiento.
             * @param actionState Estado actual del gesto (deslizar, arrastrar, etc.).
             * @param isCurrentlyActive Indica si el gesto está activo.
             */
            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                    val itemView = viewHolder.itemView // Obtiene la vista del ítem

                    // Cambia el color del fondo del ítem a rojo mientras se desliza
                    itemView.setBackgroundColor(Color.RED)

                    // Aplica la traducción en X para el desplazamiento
                    itemView.translationX = dX
                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            }

            /**
             * Método que se llama cuando un ítem ha sido deslizado.
             * Llama a la función de eliminación después del deslizamiento.
             *
             * @param viewHolder El ViewHolder que fue deslizado.
             * @param direction La dirección en la que se deslizó (izquierda o derecha).
             */
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition // Obtiene la posición del ítem deslizado
                deleteTask(position) // Llama a la función de eliminación
            }

            /**
             * Método que se llama para limpiar la vista del ítem después de que se ha deslizado.
             * Restablece el color de fondo al original.
             *
             * @param recyclerView El RecyclerView donde se encuentra el ítem.
             * @param viewHolder El ViewHolder del ítem que fue deslizado.
             */
            override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
                super.clearView(recyclerView, viewHolder)
                // Restablece el color de fondo al original después de que el usuario deja de deslizar
                viewHolder.itemView.setBackgroundColor(Color.TRANSPARENT)
            }
        }

        // Asocia el ItemTouchHelper al RecyclerView
        ItemTouchHelper(itemTouchHelper).attachToRecyclerView(recyclerViewTareas)
    }
}
