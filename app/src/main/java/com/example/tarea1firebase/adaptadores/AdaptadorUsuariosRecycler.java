package com.example.tarea1firebase.adaptadores;

import static com.example.tarea1firebase.Registro.COLECCION;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Build;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tarea1firebase.PerfilUsuario;
import com.example.tarea1firebase.R;
import com.example.tarea1firebase.entidades.Usuario;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.FadingCircle;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AdaptadorUsuariosRecycler extends RecyclerView.Adapter<AdaptadorUsuariosRecycler.ViewHolder> {
    private List<Usuario> listaUsuarios;
    private List<Usuario> listaUsuariosFiltrados;
    private FirebaseAuth mAuth;
    private String usuarioActualUid;
    private FirebaseFirestore db;
    private List<String> favoritos;
    private ProgressBar progressBar;

    public AdaptadorUsuariosRecycler(ArrayList<Usuario> listaUsuarios) {
        this.listaUsuarios = listaUsuarios;
        this.listaUsuariosFiltrados = new ArrayList<>();
        listaUsuariosFiltrados.addAll(listaUsuarios);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView nombreUsu;
        private Button btnFav, btnVerPerf;
        private boolean isFavorite;
        private ProgressBar progressBar;


        public ViewHolder(View v) {
            super(v);
            nombreUsu = v.findViewById(R.id.txtNombreUsu);
            btnFav = v.findViewById(R.id.btnCoraVacio);
            btnVerPerf = v.findViewById(R.id.btnVerPerfil);
            progressBar = v.findViewById(R.id.spin_kit);
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    //será quien devuelva el ViewHolder con el layout seteado que previamente definimos
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_usuarios_explora, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        Sprite doubleBounce = new FadingCircle();
        viewHolder.progressBar.setIndeterminateDrawable(doubleBounce);
        return viewHolder;
    }


    //será quien se encargue de establecer los objetos en el ViewHolder
    @Override
    public void onBindViewHolder(ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        usuarioActualUid = mAuth.getCurrentUser().getUid();
        holder.isFavorite = false;
        holder.progressBar.setVisibility(View.VISIBLE);

        db.collection(COLECCION).document(usuarioActualUid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                new Handler().postDelayed(() -> holder.progressBar.setVisibility(View.GONE), 900);

                DocumentSnapshot document = task.getResult();

                //Obtenemos el usuario de la base de datos con todos sus campos
                Usuario usuario = document.toObject(Usuario.class);

                favoritos = usuario.getListaFavoritos();
                if (listaUsuariosFiltrados.size() > position) {
                    if (favoritos.contains(listaUsuariosFiltrados.get(position).getId())) {
                        holder.isFavorite = true;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            holder.btnFav.setForeground(ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.corazon_favoritos_relleno));
                        }
                    } else {
                        holder.isFavorite = false;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            holder.btnFav.setForeground(ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.corazon_favoritos_vacio));
                        }
                    }
                }
            }
        });

        holder.nombreUsu.setText(listaUsuariosFiltrados.get(position).getNombre().toUpperCase(Locale.ROOT));

        holder.btnVerPerf.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), PerfilUsuario.class);
            intent.putExtra("UidUsuario", listaUsuariosFiltrados.get(position).getId());
            v.getContext().startActivity(intent);
        });

        holder.btnFav.setOnClickListener(v -> {
            TransitionDrawable transitionDrawableIda = new TransitionDrawable(new Drawable[]{ContextCompat.getDrawable(v.getContext(), R.drawable.corazon_favoritos_vacio), ContextCompat.getDrawable(v.getContext(), R.drawable.corazon_favoritos_relleno)});
            TransitionDrawable transitionDrawableVuelta = new TransitionDrawable(new Drawable[]{ContextCompat.getDrawable(v.getContext(), R.drawable.corazon_favoritos_relleno), ContextCompat.getDrawable(v.getContext(), R.drawable.corazon_favoritos_vacio)

            });

            if (holder.isFavorite) {
                transitionDrawableVuelta.setCrossFadeEnabled(true);
                transitionDrawableVuelta.startTransition(300);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    holder.btnFav.setForeground(transitionDrawableVuelta);
                }
                db.collection(COLECCION).document(usuarioActualUid).update("listaFavoritos", FieldValue.arrayRemove(listaUsuariosFiltrados.get(position).getId())).addOnSuccessListener(documentReference -> {
                    holder.isFavorite = false;
                });
            } else {
                transitionDrawableIda.setCrossFadeEnabled(true);
                transitionDrawableIda.startTransition(300);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    holder.btnFav.setForeground(transitionDrawableIda);
                }
                db.collection(COLECCION).document(usuarioActualUid).update("listaFavoritos", FieldValue.arrayUnion(listaUsuariosFiltrados.get(position).getId())).addOnSuccessListener(documentReference -> {
                    holder.isFavorite = true;
                });
            }
        });
    }


    //será quien devuelva la cantidad de items que se encuentra en la lista
    @Override
    public int getItemCount() {
        return listaUsuariosFiltrados.size();
    }

    public void filter(String query, ProgressBar progressBar) {
        listaUsuariosFiltrados.clear();
        progressBar.setVisibility(View.VISIBLE);
        if (query.isEmpty()) {
            listaUsuariosFiltrados.addAll(listaUsuarios);
        } else {
            for (Usuario user : listaUsuarios) {
                if (user.getNombre().toLowerCase().contains(query.toLowerCase())) {
                    listaUsuariosFiltrados.add(user);
                }
            }
        }
        notifyDataSetChanged();
        new Handler().postDelayed(() -> progressBar.setVisibility(View.GONE), 900);
    }

}
