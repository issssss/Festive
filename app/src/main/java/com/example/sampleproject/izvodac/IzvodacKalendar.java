package com.example.sampleproject.izvodac;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import com.example.sampleproject.R;

import javax.annotation.Nullable;

public class IzvodacKalendar extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        return inflater.inflate(R.layout.izvodac_kalendar, container, false);

    }
}
