package com.healthx.healthx;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.app.Activity.RESULT_OK;
import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;

public class TrackProgressFragment extends Fragment {
    int cameraRequestCode = 001;
    FirebaseAuth mfirebaseAuth;
    FirebaseFirestore db;

    TextView activity_main_text_day_of_month;
    TextView activity_main_text_day_of_week;
    TextView totalCals, totalCarbs, totalFats, totalProts;

    TextView result;
    List<ListItem> listItems = new ArrayList<>();


    int itemnum = 0;
    ImageView camera_icon;
    Classifier classifier;

    RecyclerView foodItemRecyclerView;
    RecyclerView.Adapter foodItemRecyclerViewAdapter = new FoodItemsAdapter(listItems, getContext());


    Map<String, Object> update_food = new HashMap<>();


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.track_progress, container, false);
        camera_icon = view.findViewById(R.id.camera_icon);
        result = view.findViewById(R.id.result);
        foodItemRecyclerView = view.findViewById(R.id.calorie_recycler_view);
        foodItemRecyclerView.setHasFixedSize(true);
        foodItemRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));



        foodItemRecyclerView.setAdapter(foodItemRecyclerViewAdapter);


        classifier = new Classifier(Utils.assetFilePath(getContext() ,"mobilenet-v2.pt"));


        camera_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                startActivityForResult(cameraIntent,cameraRequestCode);            }
        });


        return view;


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == cameraRequestCode && resultCode == RESULT_OK){
            Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");
            String pred = classifier.predict(imageBitmap);
            result.setText(pred);
            firebaseUpdate(pred);


        }
    }

    public void update_recycler_view(String val){
        ListItem listItem = new ListItem(val);
        listItems.add(listItem);
    }

    public String getitemNum(int index){
        int newitemnum = itemnum+1;

        String toreturn , num;
        num = Integer.toString(newitemnum);
        toreturn = "fooditem" + num;
        System.out.println(toreturn);
        return toreturn;
    }

    public void updatefood(String val){
        int itemLabel = update_food.size();
        itemLabel = itemLabel + 1;
        String toAdd = "fooditem" + Integer.toString(itemLabel);
        update_food.put(toAdd, val);

        System.out.println("hashmap updated");

    }

    public void firebaseUpdate(String value){
        updatefood(value);
        update_recycler_view(value);
        mfirebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        String UserId = mfirebaseAuth.getCurrentUser().getUid();
        db.collection("foodTrack").document(UserId)
                .set(update_food, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast toast = Toast.makeText(getContext(), "Added to Firestore", Toast.LENGTH_LONG);
                toast.show();
            }
        });
    }
    

}
