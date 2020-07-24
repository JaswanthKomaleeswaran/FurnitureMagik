package com.ui.furnituremagik;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class FirstFragment extends Fragment {
    private ArrayList<String> offerIdList, productNameList,productItemList,priceList,discountPriceList,pathList;
    private ArrayList<OfferDetails> offersList;
    private RecyclerView offerRecyclerView;
    private android.widget.ProgressBar progressBar;
    private SQLiteDatabase sqLiteDatabase;
    String offerId,productName,productItem,price,discountPrice,path;
    OfferAdapter offerAdapter;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_first,container, false);
        offerIdList=new ArrayList<>();
        productNameList=new ArrayList<>();
        productItemList=new ArrayList<>();
        priceList=new ArrayList<>();
        discountPriceList=new ArrayList<>();
        pathList=new ArrayList<>();
        offersList=new ArrayList<>();

        sqLiteDatabase=getActivity().openOrCreateDatabase(getResources().getString(R.string.db_name), Context.MODE_PRIVATE,null);
        offerRecyclerView=rootView.findViewById(R.id.offer_recycler);
        offerRecyclerView.setHasFixedSize(true);
        offerRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        progressBar=(android.widget.ProgressBar) rootView.findViewById(R.id.prgressbar);
        progressBar.getIndeterminateDrawable().setColorFilter(Color.BLACK, android.graphics.PorterDuff.Mode.MULTIPLY);
        progressBar.setVisibility(View.VISIBLE);
        offerAdapter=new OfferAdapter(getActivity(),offersList,sqLiteDatabase);
        offerRecyclerView.setAdapter(offerAdapter);


        try {
            Cursor resultSet = sqLiteDatabase.rawQuery("Select * from offers_table",null);
            resultSet.moveToFirst();
            while (!resultSet.isAfterLast()) {
                offerId = resultSet.getString(0);
                productName = resultSet.getString(1);
                productItem = resultSet.getString(2);
                price = String.valueOf(resultSet.getFloat(3));
                discountPrice = String.valueOf(resultSet.getFloat(4));
                path=resultSet.getString(5);
                offersList.add(new OfferDetails(offerId,productName,productItem,price,discountPrice,path));

                resultSet.moveToNext();
            }
            offerAdapter.notifyDataSetChanged();
            progressBar.setVisibility(View.INVISIBLE);

        }
        catch (Exception e) {
            progressBar.setVisibility(View.INVISIBLE);
        }

        return rootView;
    }

//    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//
//        view.findViewById(R.id.button_first).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                NavHostFragment.findNavController(FirstFragment.this)
//                        .navigate(R.id.action_FirstFragment_to_SecondFragment);
//            }
//        });
//    }
}