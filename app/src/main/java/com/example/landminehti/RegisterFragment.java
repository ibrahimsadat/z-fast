package com.example.landminehti;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;

public class RegisterFragment extends Fragment {

    private String full_name, email, password, confirm_password, phone_number, user_id, user_pic;
    private ProgressDialog progressDialog;
    private TextView tv_login;
    private Button btn_register;
    private ImageView iv;
    private TextInputLayout itl_fullName, itl_email, itl_password, itl_confirm, itl_phone;
    private TextInputEditText et_fullName, et_email, et_password, et_confirm, et_phone;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private DocumentReference documentReference;
    private Uri imageUri = null;
    private Map<String, Object> map;

    public RegisterFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar()).hide();
        return inflater.inflate(R.layout.fragment_register, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tv_login = view.findViewById(R.id.tv_login);
        btn_register = view.findViewById(R.id.register_btn);
        iv = view.findViewById(R.id.register_iv);

        et_fullName = view.findViewById(R.id.register_et_fullName);
        et_email = view.findViewById(R.id.register_et_email);
        et_password = view.findViewById(R.id.register_et_password);
        et_confirm = view.findViewById(R.id.register_et_confPassword);
        et_phone = view.findViewById(R.id.register_et_phone);
        itl_fullName = view.findViewById(R.id.register_itl_fullName);
        itl_email = view.findViewById(R.id.register_itl_email);
        itl_password = view.findViewById(R.id.register_itl_password);
        itl_confirm = view.findViewById(R.id.register_itl_confPassword);
        itl_phone = view.findViewById(R.id.register_itl_phone);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();

        progressDialog = new ProgressDialog(getContext());
        map = new HashMap<>();

        iv.setOnClickListener(v -> {
            choosePicture();
        });

        tv_login.setOnClickListener(v -> {
            MainActivity.navController.navigate(R.id.action_registerFragment_to_loginFragment);
        });

        btn_register.setOnClickListener(v -> {
            full_name = et_fullName.getText().toString().trim();
            email = et_email.getText().toString().trim();
            password = et_password.getText().toString().trim();
            confirm_password = et_confirm.getText().toString().trim();
            phone_number = et_phone.getText().toString().trim();

            if (TextUtils.isEmpty(full_name)) {
                itl_fullName.setError("Full Name is required");
                return;
            } else
                itl_fullName.setError(null);

            if (TextUtils.isEmpty(email)) {
                itl_email.setError("Email is required");
                return;
            } else
                itl_email.setError(null);

            if (TextUtils.isEmpty(password)) {
                itl_password.setError("Password is required");
                return;
            } else
                itl_password.setError(null);

            if (TextUtils.isEmpty(confirm_password)) {
                itl_confirm.setError("Confirm Password is required");
                return;
            } else
                itl_confirm.setError(null);

            if (!confirm_password.equals(password)) {
                itl_confirm.setError("Password not match");
                return;
            } else
                itl_confirm.setError(null);

            if (TextUtils.isEmpty(phone_number)) {
                itl_phone.setError("Phone Number is required");
                return;
            } else if (phone_number.length() < 11) {
                itl_phone.setError("must be 11 characters");
                return;
            } else
                itl_phone.setError(null);

            if (imageUri == null) {
                Toast.makeText(getContext(), "Select image", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!Util.checkConnection(getContext())) {
                Toast.makeText(getContext(), "No Internet", Toast.LENGTH_SHORT).show();
                return;
            }

            progressDialog.setMessage("Please wait...");
            progressDialog.show();
            progressDialog.setCanceledOnTouchOutside(false);

            mAuth.fetchSignInMethodsForEmail(email).addOnSuccessListener(signInMethodQueryResult -> {
                mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task1 -> {
                    if (task1.isSuccessful()) {
                        user_id = mAuth.getCurrentUser().getUid();
                        documentReference = db.collection(Util.USERS).document(user_id);
                        map.put(Util.USER_ID, user_id);
                        map.put(Util.FULL_NAME, full_name);
                        map.put(Util.EMAIL, email);
                        map.put(Util.PHONE_NUMBER, phone_number);
                        map.put(Util.USER_PIC, user_pic);
                        documentReference.set(map).addOnCompleteListener(task -> {
                            MainActivity.navController.navigate(R.id.action_registerFragment_to_homeFragment);
                            progressDialog.dismiss();
                        });
                    } else {
                        Toast.makeText(getContext(), "Error! " + task1.getException(), Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
                    }
                });

            }).addOnFailureListener(e -> {
                Toast.makeText(getContext(), "Error! " + e.getMessage(), Toast.LENGTH_LONG).show();
                progressDialog.dismiss();
            });
        });

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(),
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        MainActivity.navController.navigate(R.id.action_registerFragment_to_loginFragment);
                    }
                });
    }

    private void choosePicture() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            iv.setImageURI(imageUri);

        }
    }


}