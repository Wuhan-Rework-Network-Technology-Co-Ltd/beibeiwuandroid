package xin.banghua.beiyuan.Personage;


import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import xin.banghua.beiyuan.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class PersonalGuanzhuFragment extends Fragment {


    public PersonalGuanzhuFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_personal_guanzhu, container, false);
    }

}
