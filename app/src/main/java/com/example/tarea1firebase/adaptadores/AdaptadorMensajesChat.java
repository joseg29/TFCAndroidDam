/**
 * Adaptador encargado de manejar la lista de mensajes en el chat y mostrarlos en la interfaz de usuario.
 * @author
 */
package com.example.tarea1firebase.adaptadores;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.tarea1firebase.entidades.Mensaje;
import com.example.tarea1firebase.R;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class AdaptadorMensajesChat extends RecyclerView.Adapter<AdaptadorMensajesChat.ViewHolder> {
    private List<Mensaje> listaMensajes;
    private FirebaseAuth mAuth;

    /**
     * Constructor de la clase AdaptadorMensajesChat.
     * @param listaUsuarios Lista de mensajes a mostrar en el chat.
     */
    public AdaptadorMensajesChat(ArrayList<Mensaje> listaUsuarios) {
        this.listaMensajes = listaUsuarios;
    }

    /**
     * Clase interna encargada de manejar los elementos de la vista de cada mensaje en el chat.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView fechaMensaje, textoMensaje;
        private LinearLayout layoutMensaje, layoutFecha;

        /**
         * Constructor de la clase ViewHolder.
         * @param v Vista que contiene los elementos de la vista de cada mensaje en el chat.
         */
        public ViewHolder(View v) {
            super(v);
            fechaMensaje = v.findViewById(R.id.lblMensajeHora);
            textoMensaje = v.findViewById(R.id.lblMensajeTexto);
            layoutMensaje = v.findViewById(R.id.ubicacionMensajeLayout);
            layoutFecha = v.findViewById(R.id.ubicacionFechaLayout);

        }
    }

    /**
     * Método encargado de crear la vista de cada mensaje en el chat.
     * @param parent   Grupo al que se va a agregar la vista del mensaje.
     * @param viewType Tipo de vista que se está creando.
     * @return Retorna el ViewHolder con el layout seteado que previamente definimos.
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_mensaje, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }


    /**
     * Configura la apariencia de los mensajes dentro del adaptador de RecyclerView según si el
     * remitente es el usuario actual o no.
     * @param holder   ViewHolder que contiene los elementos de la vista de cada mensaje en el chat.
     * @param position La posición del mensaje dentro del adaptador.
     */
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        /**
         * Establece el texto del mensaje y la fecha y hora del mismo utilizando los valores
         * almacenados en el objeto de lista de mensajes para la posición dada.
         *
         * A continuación, obtiene la UID del usuario actual utilizando Firebase Authentication.
         *
         * A continuación, se configura la apariencia del mensaje y su disposición dentro del
         * ViewHolder según si el remitente es el usuario actual o no.
         *
         * Si el remitente es el usuario actual, el mensaje se mostrará alineado a la derecha
         * con un fondo verde claro. Si el remitente no es el usuario actual,
         *
         * el mensaje se mostrará alineado a la izquierda con un fondo gris claro.
         *
         * El padding y la gravedad del mensaje y su fecha también se ajustan de acuerdo con la
         * alineación del mensaje.
         */
        holder.textoMensaje.setText(listaMensajes.get(position).getTexto());
        holder.fechaMensaje.setText(listaMensajes.get(position).getFechaYHora());
        mAuth = FirebaseAuth.getInstance();
        String uidActual = mAuth.getCurrentUser().getUid();

        if (listaMensajes.get(position).getRemitente().equals(uidActual)) {
            holder.textoMensaje.setBackgroundResource(R.drawable.corner_mensaje_enviado);
            holder.textoMensaje.setGravity(Gravity.RIGHT);
            holder.textoMensaje.setPadding(0, 12, 25, 0);
            holder.layoutMensaje.setGravity(Gravity.RIGHT);
            holder.layoutFecha.setGravity(Gravity.RIGHT);
            holder.fechaMensaje.setGravity(Gravity.RIGHT);
        } else {
            holder.textoMensaje.setBackgroundResource(R.drawable.corner_mensaje_recibido);
            holder.textoMensaje.setPadding(25, 12, 0, 0);
            holder.layoutMensaje.setGravity(Gravity.LEFT);
            holder.layoutFecha.setGravity(Gravity.LEFT);
            holder.fechaMensaje.setGravity(Gravity.LEFT);
        }
    }


    /**
     * Método que devuelve la cantidad de elementos en la lista de mensajes.
     * @return La cantidad de mensajes en la lista.
     */
    @Override
    public int getItemCount() {
        return listaMensajes.size();
    }
}
