package com.shadow.bigdatamadesimple.adapters;

import android.content.Context;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.shadow.bigdatamadesimple.R;
import com.shadow.bigdatamadesimple.models.Message;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ChatHolder> {
    private static final String TAG = "MessageAdapter";

    private static final int MSG_TYPE_LEFT = 0, MSG_TYPE_RIGHT = 1;

    private List<Message> mMessageList;
    private Context mContext;
    private String mImageName;

    private FirebaseAuth mAuth;
    private StorageReference mRef;
    private String mCurrentUserUid = null;

    public MessageAdapter(Context mContext, List<Message> mMessageList, String imageName) {
        this.mMessageList = mMessageList;
        this.mContext = mContext;
        this.mImageName = imageName;

        mAuth = FirebaseAuth.getInstance();
        mRef = FirebaseStorage.getInstance().getReference("avatars");

    }

    @NonNull
    @Override
    public ChatHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

        if (viewType == MSG_TYPE_LEFT) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.single_chat_left, viewGroup, false);
            return new ChatHolder(view);

        } else {
            View view = LayoutInflater.from(mContext).inflate(R.layout.single_chat_right, viewGroup, false);
            return new ChatHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull ChatHolder chatHolder, int i) {
        Message message = mMessageList.get(i);

        chatHolder.msgTV.setText(message.getContent());

        if (mImageName != null) {
            StorageReference userPicRef = mRef.child(mImageName);
            final long MB = 1024 * 1024;

            userPicRef.getBytes(MB)
                    .addOnSuccessListener(
                            bytes -> {
                                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                chatHolder.profileCIV.setImageBitmap(bitmap);
                            }
                    )
                    .addOnFailureListener(
                            e -> {
                                Toast.makeText(mContext, "Error getting Image", Toast.LENGTH_SHORT).show();
                                Log.e(TAG, "onBindViewHolder: Getting Image Error", e);
                            }
                    );
        }
    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }

    public static class ChatHolder extends RecyclerView.ViewHolder {
        View mView;
        CircleImageView profileCIV;
        TextView msgTV;

        public ChatHolder(@NonNull View itemView) {
            super(itemView);

            mView = itemView;
            profileCIV = itemView.findViewById(R.id.profile_civ);
            msgTV = itemView.findViewById(R.id.msg_tv);
        }
    }

    @Override
    public int getItemViewType(int position) {
        mCurrentUserUid = mAuth.getCurrentUser().getUid();

        if (mMessageList.get(position).getSender().equals(mCurrentUserUid)) return MSG_TYPE_RIGHT;
        else return MSG_TYPE_LEFT;

    }
}
