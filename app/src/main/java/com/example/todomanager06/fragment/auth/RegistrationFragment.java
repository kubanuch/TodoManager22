package com.example.todomanager06.fragment.auth;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.todomanager06.R;
import com.example.todomanager06.databinding.FragmentRegistrationBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.TimeUnit;

public class RegistrationFragment extends Fragment {

    private static final String TAG = "PhoneAuthActivity";
    private FragmentRegistrationBinding binding;

    // [НАЧАТЬ декларирование]
    private FirebaseAuth mAuth;
    //      [КОНЕЦ объявления_auth]
    private String mVerificationId;

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentRegistrationBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initClickers();
        // [НАЧАТЬ инициализацию_авторитета]
        // Инициализировать Авторизацию Базы Данных
        mAuth = FirebaseAuth.getInstance();
        // [ЗАВЕРШИТЬ инициализацию_авторитета]

        // Инициализация обратных вызовов с авторизацией телефона
        // [ЗАПУСТИТЬ обратные вызовы phone_auth_callbacks]
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {


            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                // Этот обратный вызов будет вызван в двух ситуациях:
                // 1 - Мгновенная проверка. В некоторых случаях номер телефона может быть мгновенно
                // проверено без необходимости отправлять или вводить проверочный код.
                // 2 - Автоматическое извлечение. На некоторых устройствах сервисы Google Play могут автоматически
                // обнаружение входящего проверочного SMS-сообщения и выполнение проверки без
                // действие пользователя.
                Log.d(TAG, "onVerificationCompleted:" + credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                // Этот обратный вызов вызывается при выполнении недопустимого запроса на проверку,
                // например, если формат номера телефона недопустим.
                Log.w(TAG, "onVerificationFailed", e);

                // Неверный запрос
                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Квота SMS для проекта была превышена
                } else if (e instanceof FirebaseTooManyRequestsException) {

                }
                // Показать сообщение и обновить пользовательский интерфейс
            }

            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                // Код подтверждения SMS был отправлен на указанный номер телефона, мы
                // теперь нужно попросить пользователя ввести код, а затем создать учетные данные
                // путем объединения кода с идентификатором подтверждения.
                Log.d(TAG, "onCodeSent:" + verificationId);
                // Сохраните идентификатор подтверждения и токен повторной отправки, чтобы мы могли использовать их позже
                mVerificationId = verificationId;
            }
        };
        // [ЗАВЕРШИТЬ обратный вызов_авторитета]
    }

    private void initClickers() {
        binding.shedNumberBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String number = binding.numberEt.getText().toString();
                startPhoneNumberVerification(number);
            }
        });
        binding.shedCodeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String code = binding.codeEt.getText().toString();
                verifyPhoneNumberWithCode(mVerificationId,code);
            }
        });
    }

    @Override
    public void onStart() {
            super.onStart();


        }
    // [КОНЕЦ on_start_check_user]


    private void startPhoneNumberVerification(String phoneNumber) {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phoneNumber)  // Номер телефона для подтверждения
                        .setTimeout(60L, TimeUnit.SECONDS)// Время ожидания и единица измерения
                        .setActivity(requireActivity())// Действие (для привязки обратного вызова)
                        .setCallbacks(mCallbacks)// Проверка состояния измененных обратных вызовов
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
        // [КОНЕЦ start_phone_auth]
    }

    private void verifyPhoneNumberWithCode(String verificationId, String code) {
        // [НАЧАТЬ проверку с помощью кода]
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        // [ЗАВЕРШИТЬ проверку с помощью кода]
        signInWithPhoneAuthCredential(credential);

    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(requireActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(Task<AuthResult> task) {
//                        FirebaseAuth mAuth;
//                        mAuth = FirebaseAuth.getInstance();
                        FirebaseUser currentUser = mAuth.getCurrentUser();
                        if ( currentUser != null ) {
                            Navigation.findNavController(requireView()).navigate(R.id.finishRegistrationFragment);
                        }else if (!task.isSuccessful()) {
                            Navigation.findNavController(requireView()).navigate(R.id.homeFragment);
                            // Успешный вход, обновите пользовательский интерфейс с помощью информации о вошедшем пользователе
                            Log.d(TAG, "signInWithCredential:success");
                            // Обновление пользовательского интерфейса
                        }

                    }
                });
    }

}

