package com.example.taskmanager.AIFunction;

import android.content.Context;
import android.content.Intent;
import android.provider.ContactsContract;


import com.example.taskmanager.Model.TaskModels;
import com.example.taskmanager.Utility.DatabaseHandler;
import com.example.taskmanager.Adapter.TaskAdapter;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;


public class PrioritisationFunction {

    public ArrayList<Integer> taskPriorityRating(ArrayList<String> arrayList){

        //Keywords
        String [] schoolWorkArray = {"project", "plan", "assignment", "activity", "task", "program", "job", "design", "goal", "homework", "coursework", "classwork", "essay", "report",
                "presentation", "analyze", "evaluate", "edit", "build", "rework", "write", "implement", "create", "research", "develop", "move", "reallocate", "plan", "apply"};
        String [] tasksArray = {"email", "call", "text", "shop", "shopping", "sell", "check", "make", "review", "sort", "find", "read", "send", "schedule", "post", "organise", "print"};
        String [] choresTier1Array = {"clean", "hoover", "vacuum", "wash", "laundry", "laundrette", "sweep", "fold", "clear", "sort", "mop", "tidy"};
        String [] choresTier2Array = {"walk", "feed", "dust", "wipe", "trash"};

        ArrayList<Integer> ratingList = new ArrayList<>();

        for (String string : arrayList) {
            System.out.println(string + " at index " + arrayList.indexOf(string));
        }

        int priorityRating = 0;

        for(int i=0; i<arrayList.size(); i++) {
            //initialise variable
            String str = arrayList.get(i);
            String[] str2 = str.split(" ");
            ArrayList<String> taskWordsArray = new ArrayList<>();

            //adds words in tasks sentence to arraylist
            for (String s : str2) {
                taskWordsArray.add(s.toLowerCase(Locale.ROOT));
            }
            for (String string : taskWordsArray) {
                System.out.println(string);
            }

            for (String str3 : taskWordsArray) {
                if (str3.length() < 3) {
                    break;
                }
                for (String str4 : schoolWorkArray) {
                    if (str3.equals(str4)){
                        priorityRating += 4;
                    }
                }
                for (String str5 : tasksArray) {
                    if (str3.equals(str5)){
                        priorityRating += 3;
                    }
                }
                for (String str6 : choresTier1Array) {
                    if (str3.equals(str6)) {
                        priorityRating += 2;
                    }
                }
                for (String str7 : choresTier2Array) {
                    if (str3.equals(str7)) {
                        priorityRating += 1;
                    }
                }
            }
            System.out.println("Task at position " + i + " in arrayList has a priority rating of " + priorityRating);
            ratingList.add(priorityRating);
            priorityRating = 0;
        }

        return ratingList;

    }






}
