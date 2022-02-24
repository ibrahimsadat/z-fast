package com.example.landminehti;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

import java.util.Objects;

public class LoginFragment extends Fragment implements View.OnClickListener {

    private TextView tv_signUp, tv_forgot;
    private Button btn_login;
    private String email, password;
    private ProgressDialog progressDialog;
    private TextInputEditText et_email, et_password;
    private TextInputLayout itl_email, itl_password;

    private FirebaseAuth mAuth;

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar()).hide();
        requireActivity().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        requireActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {
            MainActivity.navController.navigate(R.id.action_loginFragment_to_homeFragment);
        }

        tv_signUp = view.findViewById(R.id.login_tv_signUp);
        tv_forgot = view.findViewById(R.id.login_tv_forgot);
        btn_login = view.findViewById(R.id.login_btn_login);

        et_email = view.findViewById(R.id.login_et_email);
        et_password = view.findViewById(R.id.login_et_password);
        itl_email = view.findViewById(R.id.itl_email);
        itl_password = view.findViewById(R.id.itl_password);

        progressDialog = new ProgressDialog(getContext());

        tv_signUp.setOnClickListener(this);
        tv_forgot.setOnClickListener(this);
        btn_login.setOnClickListener(this);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.login_tv_signUp:
                MainActivity.navController.navigate(R.id.action_loginFragment_to_registerFragment);
                break;

            case R.id.login_tv_forgot:
                final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                final View view = LayoutInflater.from(getContext()).inflate(R.layout.custom_dialog, null, false);
                builder.setView(view);
                final AlertDialog alertDialog = builder.create();
                alertDialog.show();
                alertDialog.setCanceledOnTouchOutside(false);

                final EditText et_resetEmail = view.findViewById(R.id.dialog_et_email);
                final TextView tv_submit = view.findViewById(R.id.dialog_tv_submit);
                final TextView tv_cancel = view.findViewById(R.id.dialog_tv_cancel);
                final ProgressBar pb = view.findViewById(R.id.dialog_pb);

                tv_submit.setOnClickListener(v1 -> {

                    String reset_email = et_resetEmail.getText().toString();
                    if (TextUtils.isEmpty(reset_email)) {
                        Toast.makeText(getContext(), "Email is required", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (!Util.checkConnection(getContext())) {
                        Toast.makeText(getContext(), "No Internet", Toast.LENGTH_SHORT).show();
                    } else {

                        pb.setVisibility(View.VISIBLE);
                        tv_submit.setVisibility(View.INVISIBLE);
                        tv_cancel.setVisibility(View.INVISIBLE);

                        mAuth.fetchSignInMethodsForEmail(reset_email).addOnSuccessListener(task -> {

                            mAuth.sendPasswordResetEmail(reset_email)
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(getContext(), "Reset link sent to your email", Toast.LENGTH_LONG).show();
                                        alertDialog.dismiss();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(getContext(), "Error! " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        pb.setVisibility(View.GONE);
                                        tv_submit.setVisibility(View.VISIBLE);
                                        tv_cancel.setVisibility(View.VISIBLE);
                                    });

                        }).addOnFailureListener(e -> {
                            Toast.makeText(getContext(), "Error! " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            pb.setVisibility(View.GONE);
                            tv_submit.setVisibility(View.VISIBLE);
                            tv_cancel.setVisibility(View.VISIBLE);
                        });
                    }
                });
                tv_cancel.setOnClickListener(v12 -> alertDialog.dismiss());
                return;

            case R.id.login_btn_login:
                email = et_email.getText().toString().trim();
                password = et_password.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    itl_email.setError("Email is required");
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    itl_password.setError("Password is required");
                    itl_email.setError(null);
                    return;
                }

                if (!Util.checkConnection(getContext())) {
                    itl_email.setError(null);
                    Toast.makeText(getContext(), "No Internet", Toast.LENGTH_SHORT).show();
                    return;
                }

                progressDialog.setMessage("Please wait...");
                progressDialog.show();
                progressDialog.setCanceledOnTouchOutside(false);

                mAuth.fetchSignInMethodsForEmail(email).addOnSuccessListener(task -> {

                    mAuth.signInWithEmailAndPassword(email, password).addOnSuccessListener(task1 -> {
                        MainActivity.navController.navigate(R.id.action_loginFragment_to_homeFragment);
                        progressDialog.dismiss();
                    })
                            .addOnFailureListener(e -> {
                                //email does not exist
                                if (e instanceof FirebaseAuthInvalidUserException) {
                                    itl_email.setError("Invalid email");
                                    itl_password.setError(null);
                                }

                                //wrong password
                                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                                    itl_password.setError("Password is wrong");
                                    itl_email.setError(null);
                                }
                                progressDialog.dismiss();
                            });
                }).addOnFailureListener(e -> {
                    itl_email.setError("Enter correct account");
                    itl_password.setError(null);
                    progressDialog.dismiss();
                });
        }
    }
}