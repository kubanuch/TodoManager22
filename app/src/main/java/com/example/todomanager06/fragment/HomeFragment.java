package com.example.todomanager06.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.todomanager06.App;
import com.example.todomanager06.adapter.HomeAdapter;
import com.example.todomanager06.databinding.FragmentHomeBinding;
import com.example.todomanager06.model.TaskModel;

import java.util.List;

public class HomeFragment extends Fragment implements HomeAdapter.Listener {

    private FragmentHomeBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(getLayoutInflater(), container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initClickers();
        initAdapter();
    }

    private void initAdapter() {
        App.getApp().getDb().taskDao().getData().observe(getViewLifecycleOwner(), task -> {
            HomeAdapter homeAdapter = new HomeAdapter((List<TaskModel>) task, this::OnLongClick);
            binding.homeRecycler.setAdapter(homeAdapter);
        });
    }

    private void initClickers() {
        binding.openCreateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CreateTaskFragment createTaskFragment = new CreateTaskFragment();
                createTaskFragment.show(requireActivity().getSupportFragmentManager(), "");
            }
        });
    }

    @Override
    public void OnLongClick(TaskModel model) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Удалить запись")
                .setMessage("Вы уверены, что хотите удалить эту запись?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        App.getApp().getDb().taskDao().delete(model);
                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
}