package com.Example.IRCTCWhereismyTrain.fragment;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.Example.IRCTCWhereismyTrain.R;
import com.Example.IRCTCWhereismyTrain.activity.ActivityMainPNRStatus;
import com.Example.IRCTCWhereismyTrain.widget.MyEditText;

public class PNRStatusListFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    Activity activity;
    Context mContext;
    private OnFragmentInteractionListener mListener;
    private String mParam1;
    private String mParam2;
    @BindView(R.id.tv_pnr_num)
    MyEditText tv_pnr_num;

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    public static PNRStatusListFragment newInstance(String str, String str2) {
        PNRStatusListFragment pNRStatusListFragment = new PNRStatusListFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ARG_PARAM1, str);
        bundle.putString(ARG_PARAM2, str2);
        pNRStatusListFragment.setArguments(bundle);
        return pNRStatusListFragment;
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (getArguments() != null) {
            this.mParam1 = getArguments().getString(ARG_PARAM1);
            this.mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View inflate = layoutInflater.inflate(R.layout.frag_pnr_status_list, viewGroup, false);
        ButterKnife.bind(this, inflate);
        this.mContext = getActivity();
        return inflate;
    }

    @OnClick({R.id.tv_search})
    public void onClickSearch() {
        if (this.tv_pnr_num.getText().toString().length() == 10) {
            ActivityMainPNRStatus pNRStatusMainActivityMainPNRStatus = (ActivityMainPNRStatus) getActivity();
            PNRStatusFragment pNRStatusFragment = new PNRStatusFragment();
            pNRStatusMainActivityMainPNRStatus.openFragment(PNRStatusFragment.newInstance(this.tv_pnr_num.getText().toString()));
            return;
        }
        Toast.makeText(this.mContext, "Please Enter Vaild PNR Number", 0).show();
    }

    public void onAttach(Context context) {
        super.onAttach(context);
        this.activity = (Activity) context;
    }

    @OnClick({R.id.iv_back})
    public void onClickBack() {
        getActivity().onBackPressed();
    }

    public void onButtonPressed(Uri uri) {
        OnFragmentInteractionListener onFragmentInteractionListener = this.mListener;
        if (onFragmentInteractionListener != null) {
            onFragmentInteractionListener.onFragmentInteraction(uri);
        }
    }

    public void onDetach() {
        super.onDetach();
        this.mListener = null;
    }
}
