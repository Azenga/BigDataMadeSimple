package com.shadow.bigdatamadesimple;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.shadow.bigdatamadesimple.adapters.MessageAdapter;
import com.shadow.bigdatamadesimple.models.Message;
import com.shadow.bigdatamadesimple.models.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageActivity extends AppCompatActivity {

    private static final String TAG = "MessageActivity";
    public static final String USER_UID_PARAM = "user-uid";

    //Widgets
    private Toolbar toolbar;
    private CircleImageView receiverCIV;
    private TextView receiverUsernameTV;
    private RecyclerView messagesContainer;
    private EditText messageET;
    private ImageButton sendMessageIB;

    private String mReceiverUid = null;

    private FirebaseAuth mAuth;
    private FirebaseFirestore mDb;
    private StorageReference mRef;
    private List<Message> messages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        initComponents();

        if (getSupportActionBar() == null) setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if (getIntent() != null) mReceiverUid = getIntent().getStringExtra(USER_UID_PARAM);

        mAuth = FirebaseAuth.getInstance();
        mDb = FirebaseFirestore.getInstance();
        mRef = FirebaseStorage.getInstance().getReference("avatars");

        messages = new ArrayList<>();

        sendMessageIB.setOnClickListener(view -> prepareSend());
    }

    private void readMessages(String myUid, String receiverId, String imageName) {
        mDb.collection("chats")
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .addSnapshotListener(
                        (queryDocumentSnapshots, e) -> {
                            if (e != null) {
                                Toast.makeText(this, "A fatal error occurred", Toast.LENGTH_SHORT).show();
                                Log.e(TAG, "readMessages: Failed", e);
                                return;
                            }

                            if (queryDocumentSnapshots.isEmpty()) {
                                Toast.makeText(this, "No chats yet", Toast.LENGTH_SHORT).show();
                            } else {
                                messages.clear();
                                for (DocumentSnapshot snapshot : queryDocumentSnapshots.getDocuments()) {
                                    Message msg = snapshot.toObject(Message.class);

                                    if (
                                            (msg.getSender().equals(myUid) && msg.getReceiver().equals(receiverId))
                                                    ||
                                                    (msg.getSender().equals(receiverId) && msg.getReceiver().equals(myUid))
                                    ) {
                                        messages.add(msg);
                                    }
                                }
                                Toast.makeText(this, String.valueOf(messages.size()), Toast.LENGTH_SHORT).show();
                                MessageAdapter adapter = new MessageAdapter(this, messages, imageName);
                                messagesContainer.setAdapter(adapter);
                            }
                        }
                );
    }


    private void prepareSend() {
        String content = messageET.getText().toString().trim();

        if (TextUtils.isEmpty(content)) {
            messageET.setError("Fill in something");
            messageET.requestFocus();
            return;
        }

        Map<String, Object> msg = new HashMap<>();
        String senderUid = mAuth.getCurrentUser().getUid();

        msg.put("sender", senderUid);
        msg.put("receiver", mReceiverUid);
        msg.put("content", content);
        msg.put("timestamp", FieldValue.serverTimestamp());

        sendMessage(msg);
    }

    private void initComponents() {
        toolbar = findViewById(R.id.toolbar);
        receiverCIV = findViewById(R.id.receiver_civ);
        receiverUsernameTV = findViewById(R.id.receiver_username_tv);

        messagesContainer = findViewById(R.id.messages_container);
        messagesContainer.setLayoutManager(new LinearLayoutManager(this));
        messagesContainer.setHasFixedSize(true);

        messageET = findViewById(R.id.message_et);
        sendMessageIB = findViewById(R.id.send_msg_ib);

    }

    private void sendMessage(Map<String, Object> msg) {
        mDb.collection("chats")
                .add(msg)
                .addOnCompleteListener(
                        task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(this, "Message sent", Toast.LENGTH_SHORT).show();
                                messageET.setText("");
                            } else {
                                Toast.makeText(this, "A fatal error occurred", Toast.LENGTH_SHORT).show();
                                Log.e(TAG, "sendMessage: ", task.getException());
                            }
                        }
                );
    }

    private void updateUI(User user) {
        receiverUsernameTV.setText(user.getFullName());

        //Get Image
        if (user.getImageName() != null) {
            StorageReference userPicRef = mRef.child(user.getImageName());
            final long MB = 1024 * 1024;

            userPicRef.getBytes(MB)
                    .addOnSuccessListener(
                            bytes -> {
                                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                receiverCIV.setImageBitmap(bitmap);
                            }
                    )
                    .addOnFailureListener(
                            e -> {
                                Toast.makeText(this, "Error getting Image", Toast.LENGTH_SHORT).show();
                                Log.e(TAG, "onBindViewHolder: Getting Image Error", e);
                            }
                    );
        }

        //Get Set Messages
        readMessages(mAuth.getCurrentUser().getUid(), user.getUid(), user.getImageName());
    }

    private void checkUserDetails(String uid) {
        mDb.document("users/" + uid)
                .addSnapshotListener(
                        (documentSnapshot, e) -> {
                            if (e != null) {
                                Toast.makeText(this, "A fatal error occurred", Toast.LENGTH_SHORT).show();
                                Log.e(TAG, "checkUserDetails: Failed", e);
                                return;
                            }

                            if (documentSnapshot.exists()) {
                                User user = documentSnapshot.toObject(User.class);
                                user.setUid(documentSnapshot.getId());
                                if (user != null) updateUI(user);
                            }

                        }
                );
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (mReceiverUid != null) {
            checkUserDetails(mReceiverUid);
        }
    }
}
