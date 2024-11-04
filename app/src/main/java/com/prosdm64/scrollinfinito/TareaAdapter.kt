package com.prosdm64.scrollinfinito

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import android.app.AlertDialog
import android.widget.EditText

// Adaptador para gestionar la lista de tareas en el RecyclerView
class TareaAdapter(
    private val listaTareas: MutableList<Tarea>, // Lista de tareas a mostrar
    private val onStarClick: (Int) -> Unit  // Función que se ejecuta al hacer clic en el icono de "estrella" (para marcar como favorita)
) : RecyclerView.Adapter<TareaAdapter.TareaViewHolder>() {

    // Crea y devuelve un ViewHolder para un ítem de tarea, inflando su layout desde XML
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TareaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_tarea_layout, parent, false) // Inflamos el layout de cada tarea
        return TareaViewHolder(view)
    }

    // Vincula los datos de una tarea específica a las vistas del ViewHolder
    override fun onBindViewHolder(holder: TareaViewHolder, position: Int) {
        val tarea = listaTareas[position]
        holder.tareaTextView.text = tarea.texto // Muestra el texto de la tarea

        // Configura la imagen de la estrella según si la tarea es favorita o no
        holder.starImageView.setImageResource(
            if (tarea.esFavorita) R.drawable.bahai_solid_24 else R.drawable.bahai_24
        )


        // Configura el evento de clic en la estrella para marcar/desmarcar como favorita
        holder.starImageView.setOnClickListener {
            onStarClick(position)
        }
        // Funcionalidad para editar la tarea
        holder.tareaTextView.setOnClickListener {
            val builder = AlertDialog.Builder(holder.itemView.context)
            builder.setTitle("Modificar Tarea")

            // Inflar el diseño del diálogo
            val inflater = LayoutInflater.from(holder.itemView.context)
            val dialogView = inflater.inflate(R.layout.dialog_edit_tarea, null)
            builder.setView(dialogView)

            // Enlazar el EditText
            val editTextTarea = dialogView.findViewById<EditText>(R.id.editTextTarea)
            editTextTarea.setText(tarea.texto) // Prellenar con el texto actual

            // Configurar los botones del diálogo
            builder.setPositiveButton("Actualizar") { dialog, which ->
                tarea.texto = editTextTarea.text.toString() // Actualizar el texto de la tarea
                notifyItemChanged(position) // Notificar que el item ha cambiado
            }
            builder.setNegativeButton("Cancelar", null)

            builder.show() // Mostrar el diálogo
        }
    }


    // Devuelve el número de tareas en la lista
    override fun getItemCount(): Int = listaTareas.size

    // ViewHolder para la vista de cada tarea
    inner class TareaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // TextView para mostrar el texto de la tarea
        val tareaTextView: TextView = itemView.findViewById(R.id.tareaTexto)

        // ImageView para el icono de estrella (acción de marcar como favorita)
        val starImageView: ImageView = itemView.findViewById(R.id.bahai)
    }
}
