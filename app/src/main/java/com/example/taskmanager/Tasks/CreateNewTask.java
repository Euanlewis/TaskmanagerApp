package com.example.taskmanager.Tasks;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.taskmanager.Model.TaskModels;
import com.example.taskmanager.R;
import com.example.taskmanager.Utility.DatabaseHandler;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

// Function for creating a new task.

public class CreateNewTask extends BottomSheetDialogFragment {

    public static final String TAG = "ActionBottomDialog";

    private EditText newTaskText;
    private Button newTaskSaveButton;
    private DatabaseHandler db;

    public static CreateNewTask newInstance(){
        return new CreateNewTask();
    }

// Responsible for style/font of the task
    @Override
    public void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.DialogStyle);

    }

// Responsible for extending and resizing the input box when typing new task.
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,@Nullable ViewGroup container,@Nullable Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.new_task, container, false);
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        return view;
    }

// Responsible for the function to be executed on the dialog fragment.
    @Override
    public void onViewCreated(@NonNull View view,@Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        newTaskText = requireView().findViewById(R.id.addTaskText);
        newTaskSaveButton = getView().findViewById(R.id.addTaskButton);

        db = new DatabaseHandler(getActivity());
        db.openDatabase();

        boolean isUpdate = false;
        final Bundle bundle = getArguments();
        if(bundle != null){
            isUpdate = true;
            String task = bundle.getString("task");
            newTaskText.setText(task);
            if(task.length() > 0){
                newTaskSaveButton.setTextColor(Color.GREEN);
            }
        }
        newTaskText.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.toString().equals("")){
                    newTaskSaveButton.setEnabled(false);
                    newTaskSaveButton.setTextColor(Color.GRAY);
                }else {
                    newTaskSaveButton.setEnabled(true);
                    newTaskSaveButton.setTextColor(Color.GREEN);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }

        });

        final boolean finalIsUpdate = isUpdate;
        newTaskSaveButton.setOnClickListener(view1 -> {
            String text = newTaskText.getText().toString();
            if(finalIsUpdate){
                db.updateTask(bundle.getInt("id"), text);
            }else{
                TaskModels task = new TaskModels();
                task.setTask(text);
                task.setStatus(0);
                db.insertTask(task);
            }
            dismiss();
        });

    }

// Method to ensure recyclerview is updated with old or updated task
    @Override
    public void onDismiss(@NonNull DialogInterface dialog){
        Activity activity = getActivity();
        if(activity instanceof DialogCloseListener){
            ((DialogCloseListener)activity).handleDialogClose(dialog);
        }
    }

}
