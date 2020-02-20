package com.Example.IRCTCWhereismyTrain.fragment;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.Example.IRCTCWhereismyTrain.R;
import com.Example.IRCTCWhereismyTrain.activity.ActivityMainTicketBook;
import com.Example.IRCTCWhereismyTrain.adapter.TrainListAdapter;
import com.Example.IRCTCWhereismyTrain.adapter.TrainListAdapter.setOnTrainDetailListner;
import com.Example.IRCTCWhereismyTrain.dialog.DataLoaderdialog;
import com.Example.IRCTCWhereismyTrain.model.TrainListBeen;
import com.Example.IRCTCWhereismyTrain.model.TrainListBeen.TrainBtwnStnsList;
import com.Example.IRCTCWhereismyTrain.util.Utils;
import com.Example.IRCTCWhereismyTrain.widget.MyTextView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Comparator;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request.Builder;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class TrainRouteListFragment extends Fragment implements setOnTrainDetailListner {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_PARAM3 = "param3";
    private static final String ARG_PARAM4 = "param4";
    private static final String ARG_PARAM5 = "param5";
    private static final String ARG_PARAM6 = "param6";
    Activity activity;
    DataLoaderdialog dataLoaderdialog;
    private String date;
    public String doj;
    private String fromCode;
    @BindView(R.id.layout_no_data)
    View layout_no_data;
    @BindView(R.id.ll_arrival)
    LinearLayout ll_arrival;
    @BindView(R.id.ll_data)
    RelativeLayout ll_data;
    @BindView(R.id.ll_departure)
    LinearLayout ll_departure;
    @BindView(R.id.ll_travel_time)
    LinearLayout ll_travel_time;
    Context mContext;
    private OnFragmentInteractionListener mListener;
    private String qury;
    private String route;
    @BindView(R.id.rv_rail)
    RecyclerView rv_rail;
    private String toCode;
    TrainListAdapter trainListAdapter;
    TrainListBeen trainListBeen = new TrainListBeen();
    @BindView(R.id.tv_route_date)
    MyTextView tv_route_date;
    @BindView(R.id.tv_route_name)
    MyTextView tv_route_name;

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    public static TrainRouteListFragment newInstance(String str, String str2, String str3, String str4, String str5, String str6) {
        TrainRouteListFragment trainRouteListFragment = new TrainRouteListFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ARG_PARAM1, str);
        bundle.putString(ARG_PARAM2, str2);
        bundle.putString(ARG_PARAM3, str3);
        bundle.putString(ARG_PARAM4, str4);
        bundle.putString(ARG_PARAM5, str5);
        bundle.putString(ARG_PARAM6, str6);
        trainRouteListFragment.setArguments(bundle);
        return trainRouteListFragment;
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (getArguments() != null) {
            this.route = getArguments().getString(ARG_PARAM1);
            this.date = getArguments().getString(ARG_PARAM2);
            this.qury = getArguments().getString(ARG_PARAM3);
            this.doj = getArguments().getString(ARG_PARAM4);
            this.fromCode = getArguments().getString(ARG_PARAM5);
            this.toCode = getArguments().getString(ARG_PARAM6);
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("====>>>>>");
            stringBuilder.append(this.date);
            Log.e("222222dates", stringBuilder.toString());
        }
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View inflate = layoutInflater.inflate(R.layout.frag_train_route_list, viewGroup, false);
        ButterKnife.bind((Object) this, inflate);
        this.mContext = getActivity();
        this.dataLoaderdialog = new DataLoaderdialog(this.mContext);
        this.tv_route_name.setText(this.route);
        this.tv_route_date.setText(this.date);
        this.rv_rail.setLayoutManager(new LinearLayoutManager(this.mContext, 1, false));
        if (Utils.isNetworkAvailable(this.mContext)) {
            CallApi();
        } else {
            Context context = this.mContext;
            Toast.makeText(context, context.getString(R.string.no_internet), 0).show();
        }
        this.tv_route_name.setSelected(true);
        return inflate;
    }

    @OnClick({R.id.ll_travel_time})
    public void onClickTravelTime() {
        changeTopTab(1);
    }

    @OnClick({R.id.ll_departure})
    public void onClickDepature() {
        changeTopTab(2);
    }

    @OnClick({R.id.ll_arrival})
    public void onClickArrival() {
        changeTopTab(3);
    }

    public void changeTopTab(int i) {
        String str = "1";
        String str2 = "0";
        if (i == 1) {
            this.ll_travel_time.getChildAt(1).setVisibility(View.VISIBLE);
            this.ll_departure.getChildAt(1).setVisibility(View.GONE);
            this.ll_arrival.getChildAt(1).setVisibility(View.GONE);
            ((MyTextView) this.ll_travel_time.getChildAt(0)).setMyTypeface(this.mContext, getString(R.string.medium));
            ((MyTextView) this.ll_departure.getChildAt(0)).setMyTypeface(this.mContext, getString(R.string.regular));
            ((MyTextView) this.ll_arrival.getChildAt(0)).setMyTypeface(this.mContext, getString(R.string.regular));
            if (this.ll_travel_time.getTag().toString().equalsIgnoreCase(str2)) {
                Collections.sort(this.trainListBeen.trainBtwnStnsList, new Comparator<TrainBtwnStnsList>() {
                    public int compare(TrainBtwnStnsList trainBtwnStnsList, TrainBtwnStnsList trainBtwnStnsList2) {
                        return Utils.getTimeDiff(trainBtwnStnsList.departureTime, trainBtwnStnsList.arrivalTime).compareTo(Utils.getTimeDiff(trainBtwnStnsList2.departureTime, trainBtwnStnsList2.arrivalTime));
                    }
                });
                this.ll_travel_time.setTag(str);
                this.ll_travel_time.getChildAt(1).setRotation(0.0f);
            } else {
                Collections.sort(this.trainListBeen.trainBtwnStnsList, new Comparator<TrainBtwnStnsList>() {
                    public int compare(TrainBtwnStnsList trainBtwnStnsList, TrainBtwnStnsList trainBtwnStnsList2) {
                        return Utils.getTimeDiff(trainBtwnStnsList2.departureTime, trainBtwnStnsList2.arrivalTime).compareTo(Utils.getTimeDiff(trainBtwnStnsList.departureTime, trainBtwnStnsList.arrivalTime));
                    }
                });
                this.ll_travel_time.setTag(str2);
                this.ll_travel_time.getChildAt(1).setRotation(180.0f);
            }
            this.trainListAdapter.notifyDataSetChanged();
        } else if (i == 2) {
            this.ll_travel_time.getChildAt(1).setVisibility(View.GONE);
            this.ll_departure.getChildAt(1).setVisibility(View.VISIBLE);
            this.ll_arrival.getChildAt(1).setVisibility(View.GONE);
            ((MyTextView) this.ll_travel_time.getChildAt(0)).setMyTypeface(this.mContext, getString(R.string.regular));
            ((MyTextView) this.ll_departure.getChildAt(0)).setMyTypeface(this.mContext, getString(R.string.medium));
            ((MyTextView) this.ll_arrival.getChildAt(0)).setMyTypeface(this.mContext, getString(R.string.regular));
            if (this.ll_departure.getTag().toString().equalsIgnoreCase(str2)) {
                Collections.sort(this.trainListBeen.trainBtwnStnsList, new Comparator<TrainBtwnStnsList>() {
                    public int compare(TrainBtwnStnsList trainBtwnStnsList, TrainBtwnStnsList trainBtwnStnsList2) {
                        return trainBtwnStnsList.departureTime.compareTo(trainBtwnStnsList2.departureTime);
                    }
                });
                this.ll_departure.setTag(str);
                this.ll_departure.getChildAt(1).setRotation(0.0f);
            } else {
                Collections.sort(this.trainListBeen.trainBtwnStnsList, new Comparator<TrainBtwnStnsList>() {
                    public int compare(TrainBtwnStnsList trainBtwnStnsList, TrainBtwnStnsList trainBtwnStnsList2) {
                        return trainBtwnStnsList2.departureTime.compareTo(trainBtwnStnsList.departureTime);
                    }
                });
                this.ll_departure.setTag(str2);
                this.ll_departure.getChildAt(1).setRotation(180.0f);
            }
            this.trainListAdapter.notifyDataSetChanged();
        } else if (i == 3) {
            this.ll_travel_time.getChildAt(1).setVisibility(View.GONE);
            this.ll_departure.getChildAt(1).setVisibility(View.GONE);
            this.ll_arrival.getChildAt(1).setVisibility(View.VISIBLE);
            ((MyTextView) this.ll_travel_time.getChildAt(0)).setMyTypeface(this.mContext, getString(R.string.regular));
            ((MyTextView) this.ll_departure.getChildAt(0)).setMyTypeface(this.mContext, getString(R.string.regular));
            ((MyTextView) this.ll_arrival.getChildAt(0)).setMyTypeface(this.mContext, getString(R.string.medium));
            if (this.ll_arrival.getTag().toString().equalsIgnoreCase(str2)) {
                Collections.sort(this.trainListBeen.trainBtwnStnsList, new Comparator<TrainBtwnStnsList>() {
                    public int compare(TrainBtwnStnsList trainBtwnStnsList, TrainBtwnStnsList trainBtwnStnsList2) {
                        return trainBtwnStnsList.arrivalTime.compareTo(trainBtwnStnsList2.arrivalTime);
                    }
                });
                this.ll_arrival.setTag(str);
                this.ll_arrival.getChildAt(1).setRotation(0.0f);
            } else {
                Collections.sort(this.trainListBeen.trainBtwnStnsList, new Comparator<TrainBtwnStnsList>() {
                    public int compare(TrainBtwnStnsList trainBtwnStnsList, TrainBtwnStnsList trainBtwnStnsList2) {
                        return trainBtwnStnsList2.arrivalTime.compareTo(trainBtwnStnsList.arrivalTime);
                    }
                });
                this.ll_arrival.setTag(str2);
                this.ll_arrival.getChildAt(1).setRotation(180.0f);
            }
            this.trainListAdapter.notifyDataSetChanged();
        }
    }

    public void onClickTicketClassListner(int i) {
        final String toJson = new Gson().toJson(this.trainListBeen.trainBtwnStnsList.get(i));
        if (this.fromCode.equalsIgnoreCase(((TrainBtwnStnsList) this.trainListBeen.trainBtwnStnsList.get(i)).frmStnCode)) {
            LiveStatusFragment liveStatusFragment = new LiveStatusFragment();
            openFragment(LiveStatusFragment.newInstance(toJson, this.doj));
            return;
        }
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this.mContext);
        bottomSheetDialog.setContentView((int) R.layout.dialog_train_didnt_founded);
        MyTextView myTextView = (MyTextView) bottomSheetDialog.findViewById(R.id.tv_search_label);
        MyTextView myTextView2 = (MyTextView) bottomSheetDialog.findViewById(R.id.tv_error_label);
        MyTextView myTextView3 = (MyTextView) bottomSheetDialog.findViewById(R.id.tv_point1);
        MyTextView myTextView4 = (MyTextView) bottomSheetDialog.findViewById(R.id.tv_point2);
        MyTextView myTextView5 = (MyTextView) bottomSheetDialog.findViewById(R.id.tv_point3);
        MyTextView myTextView6 = (MyTextView) bottomSheetDialog.findViewById(R.id.tv_back);
        MyTextView myTextView7 = (MyTextView) bottomSheetDialog.findViewById(R.id.tv_ok);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("You Searched for trains between ");
        stringBuilder.append(((TrainBtwnStnsList) this.trainListBeen.trainBtwnStnsList.get(i)).frmStnCode);
        stringBuilder.append(" and ");
        stringBuilder.append(((TrainBtwnStnsList) this.trainListBeen.trainBtwnStnsList.get(i)).toStnCode);
        myTextView.setText(stringBuilder.toString());
        StringBuilder stringBuilder2 = new StringBuilder();
        stringBuilder2.append("But this train travel between ");
        stringBuilder2.append(((TrainBtwnStnsList) this.trainListBeen.trainBtwnStnsList.get(i)).frmStnCode);
        stringBuilder2.append("( ");
        stringBuilder2.append(((TrainBtwnStnsList) this.trainListBeen.trainBtwnStnsList.get(i)).frmStnName);
        stringBuilder2.append(") and ");
        stringBuilder2.append(((TrainBtwnStnsList) this.trainListBeen.trainBtwnStnsList.get(i)).toStnCode);
        stringBuilder2.append("(");
        stringBuilder2.append(((TrainBtwnStnsList) this.trainListBeen.trainBtwnStnsList.get(i)).toStnName);
        stringBuilder2.append(")");
        myTextView2.setText(stringBuilder2.toString());
        myTextView3.setText(this.fromCode);
        myTextView4.setText(((TrainBtwnStnsList) this.trainListBeen.trainBtwnStnsList.get(i)).frmStnCode);
        myTextView5.setText(((TrainBtwnStnsList) this.trainListBeen.trainBtwnStnsList.get(i)).toStnCode);
        myTextView6.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                bottomSheetDialog.dismiss();
            }
        });
        myTextView7.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                bottomSheetDialog.dismiss();
                TrainRouteListFragment trainRouteListFragment = TrainRouteListFragment.this;
                LiveStatusFragment liveStatusFragment = new LiveStatusFragment();
                trainRouteListFragment.openFragment(LiveStatusFragment.newInstance(toJson, TrainRouteListFragment.this.doj));
            }
        });
        bottomSheetDialog.getWindow().setGravity(80);
        bottomSheetDialog.getWindow().setBackgroundDrawableResource(17170445);
        bottomSheetDialog.getWindow().setLayout(-1, -2);
        bottomSheetDialog.setCancelable(false);
        bottomSheetDialog.show();
    }

    public void onAttach(Context context) {
        super.onAttach(context);
        this.activity = (Activity) context;
    }

    public void CallApi() {
        Activity activity = this.activity;
        if (!(activity == null || activity.isFinishing())) {
            DataLoaderdialog dataLoaderdialog = this.dataLoaderdialog;
            if (dataLoaderdialog != null) {
                dataLoaderdialog.show();
            }
        }
        OkHttpClient okHttpClient = new OkHttpClient();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("https://rails.makemytrip.com/pwa/mobile/searchWithAvail/");
        stringBuilder.append(this.qury);
        okHttpClient.newCall(new Builder().url(HttpUrl.parse(stringBuilder.toString()).newBuilder().build()).build()).enqueue(new Callback() {
            public void onFailure(Call call, IOException iOException) {
                Log.e("AsyncOkHttp", "Error in getting response");
                TrainRouteListFragment.this.activity.runOnUiThread(new Runnable() {
                    public void run() {
                        if (!(TrainRouteListFragment.this.activity == null || TrainRouteListFragment.this.activity.isFinishing() || TrainRouteListFragment.this.dataLoaderdialog == null)) {
                            TrainRouteListFragment.this.dataLoaderdialog.dismiss();
                        }
                        Toast.makeText(TrainRouteListFragment.this.mContext, "Error in getting response", 0).show();
                    }
                });
            }

            public void onResponse(Call call, Response response) throws IOException {
                final ResponseBody body = response.body();
                String string = body.string();
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("===>>>>");
                stringBuilder.append(string);
                Log.e("jsonOutput", stringBuilder.toString());
                if (string.equalsIgnoreCase("") || string.length() <= 0) {
                    TrainRouteListFragment.this.activity.runOnUiThread(new Runnable() {
                        public void run() {
                            if (!(TrainRouteListFragment.this.activity == null || TrainRouteListFragment.this.activity.isFinishing() || TrainRouteListFragment.this.dataLoaderdialog == null)) {
                                TrainRouteListFragment.this.dataLoaderdialog.dismiss();
                            }
                            try {
                                TrainRouteListFragment.this.ll_data.setVisibility(View.GONE);
                                TrainRouteListFragment.this.layout_no_data.setVisibility(View.VISIBLE);
                                Log.i("AsyncOkHttp_no_data", body.string());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    return;
                }
                Gson gson = new Gson();
                Type type = new TypeToken<TrainListBeen>() {
                }.getType();
                TrainRouteListFragment.this.trainListBeen = (TrainListBeen) gson.fromJson(string, type);
                TrainRouteListFragment.this.activity.runOnUiThread(new Runnable() {
                    public void run() {
                        if (!(TrainRouteListFragment.this.activity == null || TrainRouteListFragment.this.activity.isFinishing() || TrainRouteListFragment.this.dataLoaderdialog == null)) {
                            TrainRouteListFragment.this.dataLoaderdialog.dismiss();
                        }
                        if (TrainRouteListFragment.this.trainListBeen.trainBtwnStnsList == null) {
                            TrainRouteListFragment.this.ll_data.setVisibility(View.GONE);
                            TrainRouteListFragment.this.layout_no_data.setVisibility(View.VISIBLE);
                        } else if (TrainRouteListFragment.this.trainListBeen.trainBtwnStnsList.size() > 0) {
                            Collections.sort(TrainRouteListFragment.this.trainListBeen.trainBtwnStnsList, new Comparator<TrainBtwnStnsList>() {
                                public int compare(TrainBtwnStnsList trainBtwnStnsList, TrainBtwnStnsList trainBtwnStnsList2) {
                                    try {
                                        String timeDiff = Utils.getTimeDiff(trainBtwnStnsList.departureTime, trainBtwnStnsList.arrivalTime);
                                        timeDiff.getClass();
                                        timeDiff = timeDiff;
                                        String timeDiff2 = Utils.getTimeDiff(trainBtwnStnsList2.departureTime, trainBtwnStnsList2.arrivalTime);
                                        timeDiff2.getClass();
                                        return timeDiff.compareTo(timeDiff2);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        return 0;
                                    }
                                }
                            });
                            TrainRouteListFragment.this.trainListAdapter = new TrainListAdapter(TrainRouteListFragment.this.mContext, TrainRouteListFragment.this.trainListBeen.trainBtwnStnsList, TrainRouteListFragment.this, TrainRouteListFragment.this.doj);
                            TrainRouteListFragment.this.rv_rail.setAdapter(TrainRouteListFragment.this.trainListAdapter);
                        } else {
                            TrainRouteListFragment.this.ll_data.setVisibility(View.GONE);
                            TrainRouteListFragment.this.layout_no_data.setVisibility(View.VISIBLE);
                        }
                    }
                });
            }
        });
    }

    @OnClick({R.id.iv_back})
    public void onClickBack() {
        ((ActivityMainTicketBook) getActivity()).onBackPressed();
    }

    public void openFragment(Fragment fragment) {
        FragmentTransaction beginTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        beginTransaction.replace(R.id.frame, fragment);
        beginTransaction.addToBackStack(fragment.toString());
        beginTransaction.commit();
    }

    public void onDestroy() {
        super.onDestroy();
        DataLoaderdialog dataLoaderdialog = this.dataLoaderdialog;
        if (dataLoaderdialog != null && dataLoaderdialog.isShowing()) {
            this.dataLoaderdialog.cancel();
        }
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
