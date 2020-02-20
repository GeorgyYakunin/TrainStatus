package com.Example.IRCTCWhereismyTrain.fragment;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.Example.IRCTCWhereismyTrain.R;
import com.Example.IRCTCWhereismyTrain.activity.ActivityMainTrainSchedule;
import com.Example.IRCTCWhereismyTrain.adapter.SearchTrainListByCdNAdapter;
import com.Example.IRCTCWhereismyTrain.adapter.SearchTrainListByCdNAdapter.setTrianListClickListner;
import com.Example.IRCTCWhereismyTrain.adapter.SerachTrainListByPtm;
import com.Example.IRCTCWhereismyTrain.model.Train;
import com.Example.IRCTCWhereismyTrain.model.TrainListByCdNBeen;
import com.Example.IRCTCWhereismyTrain.model.TrainListByCdNBeen.C1089R;
import com.Example.IRCTCWhereismyTrain.util.Utils;
import com.Example.IRCTCWhereismyTrain.widget.MyEditText;
import com.Example.IRCTCWhereismyTrain.widget.MyTextView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.util.ArrayList;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;

public class TrainScheduleListFragment extends Fragment implements setTrianListClickListner, SerachTrainListByPtm.setTrianListClickListner {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    Activity activity;
    TrainListByCdNBeen cityBeenArrayList1 = new TrainListByCdNBeen();
    @BindView(R.id.et_train)
    MyEditText et_train;
    @BindView(R.id.fl_adplaceholder)
    FrameLayout fl_adplaceholder;
    TrainListByCdNBeen listByCdNBeen;
    @BindView(R.id.ll_search)
    LinearLayout ll_search;
    Context mContext;
    private OnFragmentInteractionListener mListener;
    private String mParam1;
    private String mParam2;
    @BindView(R.id.rv_popular_search)
    RecyclerView rv_popular_search;
    @BindView(R.id.rv_search)
    RecyclerView rv_search;
    SerachTrainListByPtm serachTrainListByPtmAdapter;
    SearchTrainListByCdNAdapter trainListAdapter;
    ArrayList<Train> train_list;
    @BindView(R.id.tv_popular_lable)
    MyTextView tv_popular_lable;
    @BindView(R.id.tv_train_code)
    MyTextView tv_train_code;
    @BindView(R.id.tv_train_name)
    MyTextView tv_train_name;

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    public static TrainScheduleListFragment newInstance(String str, String str2) {
        TrainScheduleListFragment trainScheduleListFragment = new TrainScheduleListFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ARG_PARAM1, str);
        bundle.putString(ARG_PARAM2, str2);
        trainScheduleListFragment.setArguments(bundle);
        return trainScheduleListFragment;
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (getArguments() != null) {
            this.mParam1 = getArguments().getString(ARG_PARAM1);
            this.mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View inflate = layoutInflater.inflate(R.layout.frag_train_schedule_list, viewGroup, false);
        ButterKnife.bind((Object) this, inflate);
        this.rv_search.setLayoutManager(new LinearLayoutManager(this.mContext, 1, false));
        this.rv_popular_search.setLayoutManager(new LinearLayoutManager(this.mContext, 1, false));
        this.mContext = getActivity();
        inflate.setOnKeyListener(new OnKeyListener() {
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (i != 4 || !TrainScheduleListFragment.this.ll_search.isShown()) {
                    return false;
                }
                TrainScheduleListFragment.this.ll_search.setVisibility(View.GONE);
                Log.e("KEYCODE_BACK", "=========>>>>>>>");
                return true;
            }
        });
        this.et_train.addTextChangedListener(new TextWatcher() {
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
                    TrainScheduleListFragment.this.rv_search.setVisibility(View.GONE);
                    TrainScheduleListFragment.this.tv_popular_lable.setVisibility(View.VISIBLE);
                    TrainScheduleListFragment.this.rv_popular_search.setVisibility(View.VISIBLE);
                    return;
                }
                if (!TrainScheduleListFragment.this.rv_search.isShown()) {
                    TrainScheduleListFragment.this.rv_search.setVisibility(View.VISIBLE);
                    TrainScheduleListFragment.this.tv_popular_lable.setVisibility(View.GONE);
                    TrainScheduleListFragment.this.rv_popular_search.setVisibility(View.GONE);
                }
                if (Utils.isNetworkAvailable(TrainScheduleListFragment.this.mContext)) {
                    TrainScheduleListFragment.this.CallApi(editable.toString());
                } else {
                    Toast.makeText(TrainScheduleListFragment.this.mContext, TrainScheduleListFragment.this.mContext.getString(R.string.no_internet), 0).show();
                }
            }
        });
        this.cityBeenArrayList1 = (TrainListByCdNBeen) new Gson().fromJson(getString(R.string.popular_train_schedule), new TypeToken<TrainListByCdNBeen>() {
        }.getType());
        return inflate;
    }

    @OnClick({R.id.ll_station})
    public void onClickStation() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("===>>>");
        stringBuilder.append(this.cityBeenArrayList1.data.f101r.size());
        Log.e("onClickStation", stringBuilder.toString());
        this.trainListAdapter = new SearchTrainListByCdNAdapter(this.mContext, this.cityBeenArrayList1.data.f101r, this);
        this.rv_popular_search.setAdapter(this.trainListAdapter);
        this.ll_search.setVisibility(View.VISIBLE);
    }

    @OnClick({R.id.tv_search})
    public void onClickSearch() {
        if (this.tv_train_code.getText().toString().length() > 0) {
            ActivityMainTrainSchedule activityMainTrainSchedule = (ActivityMainTrainSchedule) getActivity();
            TrainScheduleFragment trainScheduleFragment = new TrainScheduleFragment();
            activityMainTrainSchedule.openFragment(TrainScheduleFragment.newInstance(this.tv_train_code.getText().toString()));
            return;
        }
        Toast.makeText(this.mContext, "Please Select Train.", 0).show();
    }

    public void CallApi(String str) {
        OkHttpClient okHttpClient = new OkHttpClient();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("https://travel.paytm.com/api/trains-search/v1/train/");
        stringBuilder.append(str);
        okHttpClient.newCall(new Request.Builder().url(HttpUrl.parse(stringBuilder.toString()).newBuilder().build()).build()).enqueue(new Callback() {
            public void onFailure(Call call, IOException iOException) {
                Log.e("AsyncOkHttp", "Error in getting response");
                TrainScheduleListFragment.this.activity.runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(TrainScheduleListFragment.this.mContext, "Error in getting response", 0).show();
                    }
                });
            }

            public void onResponse(Call call, Response response) throws IOException {
                try {
                    String string = response.body().string();
                    try {
                        TrainScheduleListFragment.this.train_list = new ArrayList();
                        if (string.length() > 0) {
                            JSONArray jSONArray = new JSONObject(string).getJSONArray("body");
                            int i = 0;
                            jSONArray = jSONArray.getJSONObject(0).getJSONArray("trains");
                            if (jSONArray.length() > 0) {
                                while (i < jSONArray.length()) {
                                    JSONObject jSONObject = jSONArray.getJSONObject(i);
                                    Train train = new Train();
                                    train.train_name = jSONObject.get("trainName").toString();
                                    train.train_number = jSONObject.get("trainNumber").toString();
                                    TrainScheduleListFragment.this.train_list.add(train);
                                    i++;
                                }
                            }
                            TrainScheduleListFragment.this.serachTrainListByPtmAdapter = new SerachTrainListByPtm(TrainScheduleListFragment.this.train_list, TrainScheduleListFragment.this.mContext, TrainScheduleListFragment.this);
                            TrainScheduleListFragment.this.activity.runOnUiThread(new Runnable() {
                                public void run() {
                                    TrainScheduleListFragment.this.rv_search.setAdapter(TrainScheduleListFragment.this.serachTrainListByPtmAdapter);
                                }
                            });
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
            }
        });
    }

    @OnClick({R.id.iv_search})
    public void onClickSearch1() {
        if (!(this.et_train.getText().toString().equalsIgnoreCase("") || this.et_train.getText().toString().startsWith(" "))) {
            if (Utils.isNetworkAvailable(this.mContext)) {
                CallApi(this.et_train.getText().toString());
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
        this.et_train.setText("");
        this.listByCdNBeen = new TrainListByCdNBeen();
        this.ll_search.setVisibility(View.GONE);
        Utils.hideKeyboardFrom(this.mContext, this.et_train);
    }

    public void onAttach(Context context) {
        super.onAttach(context);
        this.activity = (Activity) context;
    }

    public void onDestroy() {
        super.onDestroy();
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

    public void onClickTrianList(Train train) {
        this.tv_train_code.setText(train.train_number);
        this.tv_train_name.setText(train.train_name);
        this.et_train.setText("");
        this.listByCdNBeen = new TrainListByCdNBeen();
        this.ll_search.setVisibility(View.GONE);
        Utils.hideKeyboardFrom(this.mContext, this.et_train);
    }

    public void onClickTrianList(C1089R c1089r) {
        this.tv_train_code.setText(c1089r.xtr.tid);
        this.tv_train_name.setText(c1089r.f104n);
        this.et_train.setText("");
        this.listByCdNBeen = new TrainListByCdNBeen();
        this.ll_search.setVisibility(View.GONE);
        Utils.hideKeyboardFrom(this.mContext, this.et_train);
    }
}
