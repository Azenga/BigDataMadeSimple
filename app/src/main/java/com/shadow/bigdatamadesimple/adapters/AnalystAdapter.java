package com.shadow.bigdatamadesimple.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.shadow.bigdatamadesimple.MessageActivity;
import com.shadow.bigdatamadesimple.R;
import com.shadow.bigdatamadesimple.models.User;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AnalystAdapter extends RecyclerView.Adapter<AnalystAdapter.AnalystHolder> {

    public static final String TAG = "AnalystAdapter";
    private List<User> mUserList;
    private Context mContext;

    private StorageReference mRef;

    public AnalystAdapter(Context context, List<User> userList) {
        mContext = context;
        mUserList = userList;
        mRef = FirebaseStorage.getInstance().getReference("avatars");
    }


    @NonNull
    @Override
    public AnalystHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.single_user_item, viewGroup, false);

        return new AnalystHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AnalystHolder analystHolder, int position) {
        final User user = mUserList.get(position);

        analystHolder.usernameTV.setText(user.getFullName());
        //Get and set Image

        String userImageName = user.getImageName();

        if (userImageName != null) {
            StorageReference userPicRef = mRef.child(userImageName);
            final long MB = 1024 * 1024;

            userPicRef.getBytes(MB)
                    .addOnSuccessListener(
                            bytes -> {
                                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                analystHolder.profileCIV.setImageBitmap(bitmap);
                            }
                    )
                    .addOnFailureListener(
                            e -> {
                                Toast.makeText(mContext, "Error getting Image", Toast.LENGTH_SHORT).show();
                                Log.e(TAG, "onBindViewHolder: Getting Image Error", e);
                            }
                    );
        }

        //Setting OnClickListener
        analystHolder.mView.setOnClickListener(
                view -> {

                    Intent messageIntent = new Intent(mContext, MessageActivity.class);
                    messageIntent.putExtra(MessageActivity.USER_UID_PARAM, user.getUid());
                    mContext.startActivity(messageIntent);
                }
        );
    }

    @Override
    public int getItemCount() {
        return mUserList.size();
    }

    public static class AnalystHolder extends RecyclerView.ViewHolder {
        View mView;
        CircleImageView profileCIV;
        TextView usernameTV;

        public AnalystHolder(@NonNull View itemView) {
            super(itemView);

            mView = itemView;
            profileCIV = itemView.findViewById(R.id.profile_civ);
            usernameTV = itemView.findViewById(R.id.username_tv);
        }
    }
}
