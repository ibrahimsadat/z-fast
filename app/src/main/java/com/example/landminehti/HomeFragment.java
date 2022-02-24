package com.example.landminehti;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class HomeFragment extends Fragment {

    private ImageView iv;
    private TextView tv, tv_long, tv_lat, tv_yn;
    private Button btn_location, btn_result;
    private FloatingActionButton fab_reset;
    private ProgressBar pb_location, pb_result;
    private boolean isLongFinished, isLatFinished;

    private FirebaseDatabase database;
    private DatabaseReference mRef;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private String user_id;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar()).show();
        requireActivity().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        requireActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @SuppressLint("CutPasteId")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        iv = view.findViewById(R.id.home_iv);
        tv = view.findViewById(R.id.home_tv);
        tv_long = view.findViewById(R.id.home_tv_long);
        tv_lat = view.findViewById(R.id.home_tv_lat);
        tv_yn = view.findViewById(R.id.home_tv_yn);
        btn_location = view.findViewById(R.id.home_btn_location);
        btn_result = view.findViewById(R.id.home_btn_result);
        fab_reset = view.findViewById(R.id.fab_reset);
        pb_location = view.findViewById(R.id.home_pb_location);
        pb_result = view.findViewById(R.id.home_pb_result);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        user_id = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        db.collection(Util.USERS).document(user_id).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot snapshot = task.getResult();
                String pic_url = (String) snapshot.get(Util.USER_PIC);

                Picasso.get()
                        .load(pic_url)
                        .placeholder(R.drawable.ic_load)
                        .error(R.drawable.ic_username)
                        .centerCrop()
                        .resize((int) getResources().getDimension(R.dimen._64sdp), (int) getResources().getDimension(R.dimen._64sdp))
                        .into(iv);

                String username = (String) snapshot.get(Util.FULL_NAME);
                tv.setText(username);
            }
        });

        database = FirebaseDatabase.getInstance();
        mRef = database.getReference();

        btn_location.setOnClickListener(v -> {

            btn_location.setVisibility(View.INVISIBLE);
            pb_location.setVisibility(View.VISIBLE);

            mRef.child(MainActivity.LONGITUDE).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    tv_long.setText(snapshot.getValue().toString());
                    isLongFinished = true;
                    if (isLatFinished) {
                        btn_location.setVisibility(View.VISIBLE);
                        pb_location.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getContext(), "Error! " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    isLongFinished = true;
                    if (isLatFinished) {
                        btn_location.setVisibility(View.VISIBLE);
                        pb_location.setVisibility(View.GONE);
                    }
                }
            });

            mRef.child(MainActivity.LATITUDE).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    tv_lat.setText(snapshot.getValue().toString());
                    isLatFinished = true;
                    if (isLongFinished) {
                        btn_location.setVisibility(View.VISIBLE);
                        pb_location.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getContext(), "Error! " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    isLatFinished = true;
                    if (isLongFinished) {
                        btn_location.setVisibility(View.VISIBLE);
                        pb_location.setVisibility(View.GONE);
                    }
                }
            });
        });

        btn_result.setOnClickListener(v -> {

            btn_result.setVisibility(View.INVISIBLE);
            pb_result.setVisibility(View.VISIBLE);

            mRef.child(MainActivity.RESULT).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    tv_yn.setText(snapshot.getValue().toString());
                    btn_result.setVisibility(View.VISIBLE);
                    pb_result.setVisibility(View.GONE);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getContext(), "Error! " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    btn_result.setVisibility(View.VISIBLE);
                    pb_result.setVisibility(View.GONE);
                }
            });
        });

        fab_reset.setOnClickListener(v -> {
            mRef.child(MainActivity.LONGITUDE).setValue("null");
            mRef.child(MainActivity.LATITUDE).setValue("null");
            mRef.child(MainActivity.RESULT).setValue("null");
            Toast.makeText(getContext(), "Values have been reset", Toast.LENGTH_SHORT).show();
        });

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(),
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        requireActivity().finishAffinity();
                    }
                });
    }
}