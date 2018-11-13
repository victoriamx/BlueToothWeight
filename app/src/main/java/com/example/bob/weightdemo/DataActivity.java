package com.example.bob.weightdemo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.lifesense.ble.bean.WeightData_A3;

/**
 * Created by Bob on 2018/11/13.
 */

public class DataActivity extends AppCompatActivity {
	private WeightData_A3 mWeightData_a3;
	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_data);
		mWeightData_a3 = getIntent().getParcelableExtra("data");
	}
}
