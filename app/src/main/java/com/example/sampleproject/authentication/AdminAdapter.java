package com.example.sampleproject.authentication;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.sampleproject.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.List;
import java.util.Map;

public class AdminAdapter extends RecyclerView.Adapter<AdminAdapter.MyViewHolder>{

    private List<Map> userList;
    private FirebaseFirestore firestore;
    private CollectionReference cr;
    private DocumentReference dr;


    public class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView nameSurname, email, username, phoneNumber, role;

        public MyViewHolder(View view) {
            super(view);
            nameSurname = view.findViewById(R.id.admin_name_surname);
            email = view.findViewById(R.id.admin_email);
            username = view.findViewById(R.id.admin_username);
            phoneNumber = view.findViewById(R.id.admin_phone_number);
            role = view.findViewById(R.id.admin_role);
            view.findViewById(R.id.button6).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    remove(getAdapterPosition());
                }
            });
        }
    }

    //Constructor
    public AdminAdapter(List<Map> userMap) {
        this.userList = userMap;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                                      .inflate(R.layout.admin_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Map user = userList.get(position);
        holder.nameSurname.setText(user.get("name").toString().concat(" ").concat(user.get("surname").toString()));
        holder.email.setText(user.get("email").toString());
        holder.phoneNumber.setText(user.get("phone number").toString());
        holder.role.setText(user.get("role").toString());
        holder.username.setText(user.get("username").toString());
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public void remove(int position){
        final int pos = position;
        firestore = FirebaseFirestore.getInstance();
        cr = firestore.collection("admin");

        final String toDelete = userList.get(position).get("email").toString();

        dr = cr.document(toDelete);
        dr.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                cr = firestore.collection("emails");
                dr = cr.document(toDelete);
                dr.update("confirmed", "true");

                userList.remove(pos);
                notifyItemRemoved(pos);
                notifyItemChanged(pos, userList.size());

            }
        });


    }

}