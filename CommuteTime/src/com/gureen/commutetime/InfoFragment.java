package com.gureen.commutetime;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class InfoFragment extends Fragment {
    
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_info, container, false);
	}
	
	@Override
	public void onAttach(Activity activity) {
	    super.onAttach(activity);
	}

	@Override
	public void onDetach() {
	    super.onDetach();
	}

}
