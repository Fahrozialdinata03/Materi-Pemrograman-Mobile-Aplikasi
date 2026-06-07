package com.example.pretes_Akmal.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pretes_Akmal.ListMahasiswaActivity;
import com.example.pretes_Akmal.R;
import com.example.pretes_Akmal.UpdateActivity;
import com.example.pretes_Akmal.db.DbHelper;
import com.example.pretes_Akmal.model.Mahasiswa;

import java.io.Serializable;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MahasiswaAdapter extends RecyclerView.Adapter<MahasiswaAdapter.StudentViewHolder> {

    private ArrayList<Mahasiswa> listStudents = new ArrayList<>();
    private Activity activity;
    private DbHelper dbHelper;

    public MahasiswaAdapter(Activity activity) {
        this.activity = activity;
        dbHelper = new DbHelper(activity);
    }

    public ArrayList<Mahasiswa> getListStudents() {
        return listStudents;
    }

    public void setListStudents(ArrayList<Mahasiswa> listNotes) {
        if (listNotes.size() > 0) {
            this.listStudents.clear();
            this.listStudents.addAll(listNotes);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public StudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mahasiswa, parent, false);
        return new StudentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.tvNim.setText(listStudents.get(position).getNim());
        holder.tvName.setText(listStudents.get(position).getName());

        holder.btnEdit.setOnClickListener((View v) -> {
            Intent intent = new Intent(activity, UpdateActivity.class);
            intent.putExtra("user", (Serializable) listStudents.get(position));
            activity.startActivity(intent);
        });

        holder.btnDelete.setOnClickListener((View v) -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            // Menggunakan string resource
            builder.setTitle(R.string.delete_confirm_title);
            builder.setMessage(R.string.delete_confirm_message);

            builder.setPositiveButton(R.string.delete_positive_button, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dbHelper.deleteUser(listStudents.get(position).getId());
                    // Menggunakan string resource
                    Toast.makeText(activity, R.string.delete_success, Toast.LENGTH_SHORT).show();

                    Intent myIntent = new Intent(activity, ListMahasiswaActivity.class);
                    activity.startActivity(myIntent);
                    activity.finish();
                }
            });
            builder.setNegativeButton(R.string.delete_negative_button, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
        });
    }

    @Override
    public int getItemCount() {
        return listStudents.size();
    }

    public class StudentViewHolder extends RecyclerView.ViewHolder {
        final TextView tvNim, tvName;
        final Button btnEdit, btnDelete;

        public StudentViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNim = itemView.findViewById(R.id.tv_item_nim);
            tvName = itemView.findViewById(R.id.tv_item_name);
            btnEdit = itemView.findViewById(R.id.btn_edit);
            btnDelete = itemView.findViewById(R.id.btn_delete);
        }
    }
}
