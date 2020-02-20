package com.Example.IRCTCWhereismyTrain.fragment;

import android.app.Activity;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.Example.IRCTCWhereismyTrain.R;
import com.Example.IRCTCWhereismyTrain.adapter.SearchTrainListAdapter;
import com.Example.IRCTCWhereismyTrain.adapter.SearchTrainListAdapter.setTrianListClickListner;
import com.Example.IRCTCWhereismyTrain.dialog.DataLoaderdialog;
import com.Example.IRCTCWhereismyTrain.model.CityBeen;
import com.Example.IRCTCWhereismyTrain.util.Utils;
import com.Example.IRCTCWhereismyTrain.widget.GpsTracker;
import com.Example.IRCTCWhereismyTrain.widget.MyEditText;
import com.Example.IRCTCWhereismyTrain.widget.MyTextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LiveStationListFragment extends Fragment implements setTrianListClickListner {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    Activity activity;

    ArrayList<CityBeen> cityBeenArrayList = new ArrayList();
    ArrayList<CityBeen> cityBeenArrayList1 = new ArrayList();
    DataLoaderdialog dataLoaderdialog;
    @BindView(R.id.et_station)
    MyEditText et_station;
    @BindView(R.id.fl_adplaceholder)
    FrameLayout fl_adplaceholder;
    GpsTracker gpsTracker;
    @BindView(R.id.ll_search)
    LinearLayout ll_search;
    @BindView(R.id.ll_station)
    LinearLayout ll_station;
    Context mContext;
    private OnFragmentInteractionListener mListener;
    private String mParam1;
    private String mParam2;
    @BindView(R.id.rv_popular_search)
    RecyclerView rv_popular_search;
    @BindView(R.id.rv_search)
    RecyclerView rv_search;
    SearchTrainListAdapter trainListAdapter;
    @BindView(R.id.tv_popular_lable)
    MyTextView tv_popular_lable;
    @BindView(R.id.tv_station_code)
    MyTextView tv_station_code;
    @BindView(R.id.tv_station_name)
    MyTextView tv_station_name;

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    public static LiveStationListFragment newInstance(String str, String str2) {
        LiveStationListFragment liveStationListFragment = new LiveStationListFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ARG_PARAM1, str);
        bundle.putString(ARG_PARAM2, str2);
        liveStationListFragment.setArguments(bundle);
        return liveStationListFragment;
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (getArguments() != null) {
            this.mParam1 = getArguments().getString(ARG_PARAM1);
            this.mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View inflate = layoutInflater.inflate(R.layout.frag_station_list_live, viewGroup, false);
        ButterKnife.bind(this, inflate);
        this.mContext = getActivity();
        this.dataLoaderdialog = new DataLoaderdialog(this.mContext);
        this.rv_search.setLayoutManager(new LinearLayoutManager(this.mContext, 1, false));
        this.rv_popular_search.setLayoutManager(new LinearLayoutManager(this.mContext, 1, false));
        this.et_station.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            public void afterTextChanged(Editable editable) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("=====>>>>>");
                stringBuilder.append(editable.toString());
                Log.e("afterTextChanged", stringBuilder.toString());
                if (editable.toString().equalsIgnoreCase("") || editable.toString().startsWith(" ")) {
                    LiveStationListFragment.this.rv_search.setVisibility(View.GONE);
                    LiveStationListFragment.this.tv_popular_lable.setVisibility(View.VISIBLE);
                    LiveStationListFragment.this.rv_popular_search.setVisibility(View.VISIBLE);
                    return;
                }
                if (!LiveStationListFragment.this.rv_search.isShown()) {
                    LiveStationListFragment.this.rv_search.setVisibility(View.VISIBLE);
                    LiveStationListFragment.this.tv_popular_lable.setVisibility(View.GONE);
                    LiveStationListFragment.this.rv_popular_search.setVisibility(View.GONE);
                }
                if (Utils.isNetworkAvailable(LiveStationListFragment.this.mContext)) {
                    LiveStationListFragment.this.CallApi(editable.toString());
                } else {
                    Toast.makeText(LiveStationListFragment.this.mContext, LiveStationListFragment.this.mContext.getString(R.string.no_internet), 0).show();
                }
            }
        });
        this.cityBeenArrayList1 = new Gson().fromJson(getString(R.string.popular_station_place), new TypeToken<ArrayList<CityBeen>>() {
        }.getType());
        LoadLocationCity();
        return inflate;
    }

    private void LoadLocationCity() {
        this.gpsTracker = new GpsTracker(getActivity());
        if (this.gpsTracker.canGetLocation()) {
            try {
                List fromLocation = new Geocoder(getActivity(), Locale.getDefault()).getFromLocation(this.gpsTracker.getLatitude(), this.gpsTracker.getLongitude(), 1);
                if (fromLocation.size() > 0) {
                    this.et_station.setText(((Address) fromLocation.get(0)).getLocality());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @OnClick({R.id.ll_station})
    public void onClickStation() {
        this.trainListAdapter = new SearchTrainListAdapter(this.mContext, this.cityBeenArrayList1, this);
        this.rv_popular_search.setAdapter(this.trainListAdapter);
        this.ll_search.setVisibility(View.VISIBLE);
    }

    @OnClick({R.id.tv_search})
    public void onClickSearch() {
        if (this.tv_station_code.getText().toString().length() > 0) {
            LiveStationFragment liveStationFragment = new LiveStationFragment();
            openFragment(LiveStationFragment.newInstance(this.tv_station_code.getText().toString()));
            return;
        }
        Toast.makeText(this.mContext, "Please Select Station.", 0).show();
    }

    @OnClick({R.id.iv_search})
    public void onClickSearch1() {
        if (!(this.et_station.getText().toString().equalsIgnoreCase("") || this.et_station.getText().toString().startsWith(" "))) {
            if (Utils.isNetworkAvailable(this.mContext)) {
                CallApi(this.et_station.getText().toString());
            } else {
                Context context = this.mContext;
                Toast.makeText(context, context.getString(R.string.no_internet), 0).show();
            }
        }
    }

    @OnClick({R.id.iv_back})
    public void onClickBack() {
        getActivity().onBackPressed();
    }

    @OnClick({R.id.iv_back_search})
    public void onClickSearchBack() {
        this.et_station.setText("");
        this.cityBeenArrayList.clear();
        this.ll_search.setVisibility(View.GONE);
        Utils.hideKeyboardFrom(this.mContext, this.et_station);
    }

    public void CallApi(String str) {
        OkHttpClient okHttpClient = new OkHttpClient();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("https://www.makemytrip.com/pwa-hlp/getRailsCity?city=");
        stringBuilder.append(str);
        okHttpClient.newCall(new Request.Builder().url(HttpUrl.parse(stringBuilder.toString()).newBuilder().build()).build()).enqueue(new Callback() {
            public void onFailure(Call call, IOException iOException) {
                Log.e("AsyncOkHttp", "Error in getting response");
                LiveStationListFragment.this.activity.runOnUiThread(new Runnable() {
                    public void run() {
                        if (LiveStationListFragment.this.activity != null && !LiveStationListFragment.this.activity.isFinishing() && LiveStationListFragment.this.dataLoaderdialog != null) {
                            Toast.makeText(LiveStationListFragment.this.mContext, "Error in getting response", 0).show();
                        }
                    }
                });
            }

            public void onResponse(Call call, Response response) throws IOException {
                String string = response.body().string();
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("===>>>>");
                stringBuilder.append(string);
                Log.e("jsonOutput", stringBuilder.toString());
                if (string.length() > 0) {
                    Gson gson = new Gson();
                    Type type = new TypeToken<ArrayList<CityBeen>>() {
                    }.getType();
                    try {
                        LiveStationListFragment.this.cityBeenArrayList = gson.fromJson(string, type);
                        LiveStationListFragment.this.trainListAdapter = new SearchTrainListAdapter(LiveStationListFragment.this.mContext, LiveStationListFragment.this.cityBeenArrayList, LiveStationListFragment.this);
                        LiveStationListFragment.this.activity.runOnUiThread(new Runnable() {
                            public void run() {
                                if (LiveStationListFragment.this.activity != null && !LiveStationListFragment.this.activity.isFinishing() && LiveStationListFragment.this.dataLoaderdialog != null) {
                                    LiveStationListFragment.this.rv_search.setAdapter(LiveStationListFragment.this.trainListAdapter);
                                }
                            }
                        });
                    } catch (Exception unused) {
                    }
                }
            }
        });
    }

    public void onClickTrianList(CityBeen cityBeen, boolean z) {
        CityBeen cityBeen2 = new CityBeen();
        MyTextView myTextView = this.tv_station_name;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(cityBeen.name);
        String str = "";
        stringBuilder.append(str);
        myTextView.setText(stringBuilder.toString());
        myTextView = this.tv_station_code;
        stringBuilder = new StringBuilder();
        stringBuilder.append(cityBeen.code);
        stringBuilder.append(str);
        myTextView.setText(stringBuilder.toString());
        this.et_station.setText(str);
        this.cityBeenArrayList.clear();
        this.ll_search.setVisibility(View.GONE);
        Utils.hideKeyboardFrom(this.mContext, this.et_station);
    }

    public void onAttach(Context context) {
        super.onAttach(context);
        this.activity = (Activity) context;
    }

    public void openFragment(Fragment fragment) {
        FragmentTransaction beginTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        beginTransaction.replace(R.id.frame, fragment);
        beginTransaction.addToBackStack(fragment.toString());
        beginTransaction.commit();
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
