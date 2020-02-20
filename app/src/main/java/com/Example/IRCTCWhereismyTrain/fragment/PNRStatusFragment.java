package com.Example.IRCTCWhereismyTrain.fragment;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.Example.IRCTCWhereismyTrain.R;
import com.Example.IRCTCWhereismyTrain.activity.ActivityMainPNRStatus;
import com.Example.IRCTCWhereismyTrain.dialog.DataLoaderdialog;
import com.Example.IRCTCWhereismyTrain.model.PnrStatusBeen;
import com.Example.IRCTCWhereismyTrain.model.PnrStatusBeen.Passengerstatus;
import com.Example.IRCTCWhereismyTrain.util.SharedPreference;
import com.Example.IRCTCWhereismyTrain.util.Utils;
import com.Example.IRCTCWhereismyTrain.widget.MyTextView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.lang.reflect.Type;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.HttpUrl.Builder;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.json.JSONException;
import org.json.JSONObject;

public class PNRStatusFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    Activity activity;
    DataLoaderdialog dataLoaderdialog;
    boolean is24formt = true;
    @BindView(R.id.layout_no_data)
    View layout_no_data;
    @BindView(R.id.ll_main)
    LinearLayout ll_main;
    Context mContext;
    private OnFragmentInteractionListener mListener;
    private String mParam1;
    PnrStatusBeen pnrStatusBeen;
    public String pnr_id;
    SharedPreference sharedPreference;
    @BindView(R.id.sv_data)
    ScrollView sv_data;
    @BindView(R.id.tv_book_stus)
    MyTextView tv_book_stus;
    @BindView(R.id.tv_bord_date)
    MyTextView tv_bord_date;
    @BindView(R.id.tv_bord_st)
    MyTextView tv_bord_st;
    @BindView(R.id.tv_bord_st_name)
    MyTextView tv_bord_st_name;
    @BindView(R.id.tv_chat_prepd)
    MyTextView tv_chat_prepd;
    @BindView(R.id.tv_confirm)
    MyTextView tv_confirm;
    @BindView(R.id.tv_curr_stus)
    MyTextView tv_curr_stus;
    @BindView(R.id.tv_declamer)
    MyTextView tv_declamer;
    @BindView(R.id.tv_dep_time)
    MyTextView tv_dep_time;
    @BindView(R.id.tv_distnce)
    MyTextView tv_distnce;
    @BindView(R.id.tv_jd)
    MyTextView tv_jd;
    @BindView(R.id.tv_passger)
    MyTextView tv_passger;
    @BindView(R.id.tv_pnr_num)
    MyTextView tv_pnr_num;
    @BindView(R.id.tv_resv_date)
    MyTextView tv_resv_date;
    @BindView(R.id.tv_resv_st)
    MyTextView tv_resv_st;
    @BindView(R.id.tv_resv_st_name)
    MyTextView tv_resv_st_name;
    @BindView(R.id.tv_route)
    MyTextView tv_route;
    @BindView(R.id.tv_seat_stus)
    MyTextView tv_seat_stus;
    @BindView(R.id.tv_train_id)
    MyTextView tv_train_id;
    @BindView(R.id.tv_train_name)
    MyTextView tv_train_name;
    @BindView(R.id.tv_train_time)
    MyTextView tv_train_time;

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    public void click_Share() {
    }

    public static PNRStatusFragment newInstance(String str) {
        PNRStatusFragment pNRStatusFragment = new PNRStatusFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ARG_PARAM1, str);
        pNRStatusFragment.setArguments(bundle);
        return pNRStatusFragment;
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (getArguments() != null) {
            this.pnr_id = getArguments().getString(ARG_PARAM1);
        }
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View inflate = layoutInflater.inflate(R.layout.frag_pnr_status, viewGroup, false);
        ButterKnife.bind((Object) this, inflate);
        this.mContext = getActivity();
        this.dataLoaderdialog = new DataLoaderdialog(this.mContext);
        this.sharedPreference = new SharedPreference(this.mContext);
        this.is24formt = this.sharedPreference.getTimeFormate();
        if (Utils.isNetworkAvailable(this.mContext)) {
            callApi();
        } else {
            Context context = this.mContext;
            Toast.makeText(context, context.getString(R.string.no_internet), 0).show();
        }
        return inflate;
    }

    @OnClick({R.id.rl_train_route})
    public void onClickTrainRoute() {
        ActivityMainPNRStatus pNRStatusMainActivityMainPNRStatus = (ActivityMainPNRStatus) getActivity();
        TrainScheduleFragment trainScheduleFragment = new TrainScheduleFragment();
        pNRStatusMainActivityMainPNRStatus.openFragment(TrainScheduleFragment.newInstance(this.pnrStatusBeen.trainDetails.train.number));
    }

    @OnClick({R.id.iv_back})
    public void onClickBack() {
        getActivity().onBackPressed();
    }

    @OnClick({R.id.iv_share})
    public void onClickShare() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("PNR Number - ");
        stringBuilder.append(this.pnrStatusBeen.pnrDetails.pnr);
        stringBuilder.append("\n Train Number -");
        stringBuilder.append(this.pnrStatusBeen.trainDetails.train.number);
        stringBuilder.append("");
        stringBuilder.append(this.pnrStatusBeen.trainDetails.train.name);
        stringBuilder.append("\n\n\n From:");
        stringBuilder.append(this.pnrStatusBeen.stationDetails.boardingPoint.name);
        String str = "(";
        stringBuilder.append(str);
        stringBuilder.append(this.pnrStatusBeen.stationDetails.boardingPoint.code);
        stringBuilder.append(")\n To:");
        stringBuilder.append(this.pnrStatusBeen.stationDetails.reservationUpto.name);
        stringBuilder.append(str);
        stringBuilder.append(this.pnrStatusBeen.stationDetails.reservationUpto.code);
        stringBuilder.append(")\n\n\nCurrent Status:");
        stringBuilder.append(((Passengerstatus) this.pnrStatusBeen.passengerDetails.passengerStatus.get(0)).currentStatus);
        Utils.ShareDetails(this.mContext, stringBuilder.toString());
    }

    public void onAttach(Context context) {
        super.onAttach(context);
        this.activity = (Activity) context;
    }

    private void callApi() {
        this.dataLoaderdialog.show();
        OkHttpClient okHttpClient = new OkHttpClient();
        Builder newBuilder = HttpUrl.parse("https://railsinfo-services.makemytrip.com/api/rails/pnr/currentstatus/v1").newBuilder();
        JSONObject jSONObject = new JSONObject();
        MediaType parse = MediaType.parse("application/json");
        try {
            jSONObject.put("pnrID", this.pnr_id);
            JSONObject jSONObject2 = new JSONObject();
            jSONObject2.put("channelCode", "ANDROID");
            jSONObject2.put("affiliateCode", "MMT001");
            jSONObject.put("trackingParams", jSONObject2);
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("=====>>>>");
            stringBuilder.append(jSONObject);
            Log.e("send data jsonObject", stringBuilder.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        okHttpClient.newCall(new Request.Builder().url(newBuilder.build()).post(RequestBody.create(parse, jSONObject.toString())).build()).enqueue(new Callback() {
            public void onFailure(Call call, IOException iOException) {
                Log.e("AsyncOkHttp", "Error in getting response");
                PNRStatusFragment.this.activity.runOnUiThread(new Runnable() {
                    public void run() {
                        if (PNRStatusFragment.this.activity != null && !PNRStatusFragment.this.activity.isFinishing() && PNRStatusFragment.this.dataLoaderdialog != null) {
                            PNRStatusFragment.this.dataLoaderdialog.dismiss();
                            Toast.makeText(PNRStatusFragment.this.mContext, "Error in getting response", 0).show();
                        }
                    }
                });
            }

            public void onResponse(Call call, Response response) throws IOException {
                ResponseBody body = response.body();
                String string = body.string();
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("===>>>>");
                stringBuilder.append(string);
                Log.e("jsonOutput", stringBuilder.toString());
                try {
                    if (new JSONObject(string).has("Error")) {
                        PNRStatusFragment.this.activity.runOnUiThread(new Runnable() {
                            public void run() {
                                if (PNRStatusFragment.this.activity != null && !PNRStatusFragment.this.activity.isFinishing() && PNRStatusFragment.this.dataLoaderdialog != null) {
                                    PNRStatusFragment.this.dataLoaderdialog.dismiss();
                                    PNRStatusFragment.this.sv_data.setVisibility(View.GONE);
                                    PNRStatusFragment.this.layout_no_data.setVisibility(View.VISIBLE);
                                }
                            }
                        });
                    } else if (string.length() > 0) {
                        PNRStatusFragment.this.layout_no_data.setVisibility(View.GONE);
                        PNRStatusFragment.this.sv_data.setVisibility(View.VISIBLE);
                        Gson gson = new Gson();
                        Type type = new TypeToken<PnrStatusBeen>() {
                        }.getType();
                        PNRStatusFragment.this.pnrStatusBeen = (PnrStatusBeen) gson.fromJson(string, type);
                        PNRStatusFragment.this.activity.runOnUiThread(new Runnable() {
                            public void run() {
                                if (!(PNRStatusFragment.this.activity == null || PNRStatusFragment.this.activity.isFinishing() || PNRStatusFragment.this.dataLoaderdialog == null || !PNRStatusFragment.this.dataLoaderdialog.isShowing())) {
                                    PNRStatusFragment.this.dataLoaderdialog.dismiss();
                                }
                                PNRStatusFragment.this.ll_main.setVisibility(View.VISIBLE);
                                MyTextView myTextView = PNRStatusFragment.this.tv_pnr_num;
                                StringBuilder stringBuilder = new StringBuilder();
                                stringBuilder.append("PNR ");
                                stringBuilder.append(PNRStatusFragment.this.pnr_id);
                                myTextView.setText(stringBuilder.toString());
                                myTextView = PNRStatusFragment.this.tv_train_name;
                                stringBuilder = new StringBuilder();
                                String str = "";
                                stringBuilder.append(str);
                                stringBuilder.append(PNRStatusFragment.this.pnrStatusBeen.trainDetails.train.number);
                                String str2 = " ";
                                stringBuilder.append(str2);
                                stringBuilder.append(PNRStatusFragment.this.pnrStatusBeen.trainDetails.train.name);
                                myTextView.setText(stringBuilder.toString());
                                myTextView = PNRStatusFragment.this.tv_jd;
                                stringBuilder = new StringBuilder();
                                stringBuilder.append(str);
                                String str3 = "dd MMM yy";
                                String str4 = "dd-MM-yyyy";
                                stringBuilder.append(Utils.changeDateFormate(PNRStatusFragment.this.pnrStatusBeen.pnrDetails.sourceDoj.formattedDate, str4, str3));
                                stringBuilder.append(" | ");
                                stringBuilder.append(Utils.convertMillsecTime(PNRStatusFragment.this.pnrStatusBeen.pnrDetails.sourceDoj.epoch.longValue(), PNRStatusFragment.this.is24formt));
                                myTextView.setText(stringBuilder.toString());
                                PNRStatusFragment.this.tv_seat_stus.setText(PNRStatusFragment.this.pnrStatusBeen.pnrDetails.pnrCurrentStatus);
                                if (PNRStatusFragment.this.pnrStatusBeen.trainDetails.chartPrepared.booleanValue()) {
                                    PNRStatusFragment.this.tv_chat_prepd.setText("Yes");
                                } else {
                                    PNRStatusFragment.this.tv_chat_prepd.setText("No");
                                }
                                myTextView = PNRStatusFragment.this.tv_passger;
                                stringBuilder = new StringBuilder();
                                stringBuilder.append(PNRStatusFragment.this.pnrStatusBeen.passengerDetails.count);
                                stringBuilder.append(" Passenger");
                                myTextView.setText(stringBuilder.toString());
                                PNRStatusFragment.this.tv_book_stus.setText(((Passengerstatus) PNRStatusFragment.this.pnrStatusBeen.passengerDetails.passengerStatus.get(0)).bookingStatus);
                                PNRStatusFragment.this.tv_curr_stus.setText(((Passengerstatus) PNRStatusFragment.this.pnrStatusBeen.passengerDetails.passengerStatus.get(0)).currentStatus);
                                PNRStatusFragment.this.tv_confirm.setText(((Passengerstatus) PNRStatusFragment.this.pnrStatusBeen.passengerDetails.passengerStatus.get(0)).confirmTktStatus);
                                myTextView = PNRStatusFragment.this.tv_train_id;
                                StringBuilder stringBuilder2 = new StringBuilder();
                                stringBuilder2.append(PNRStatusFragment.this.pnrStatusBeen.trainDetails.train.number);
                                stringBuilder2.append(str2);
                                stringBuilder2.append(PNRStatusFragment.this.pnrStatusBeen.trainDetails.train.name);
                                myTextView.setText(stringBuilder2.toString());
                                PNRStatusFragment.this.tv_train_time.setText(Utils.convertMillsecTime(PNRStatusFragment.this.pnrStatusBeen.pnrDetails.sourceDoj.epoch.longValue(), PNRStatusFragment.this.is24formt));
                                PNRStatusFragment.this.tv_bord_st_name.setText(PNRStatusFragment.this.pnrStatusBeen.cityDetails.sourceCity.name);
                                myTextView = PNRStatusFragment.this.tv_bord_st;
                                stringBuilder2 = new StringBuilder();
                                stringBuilder2.append(PNRStatusFragment.this.pnrStatusBeen.stationDetails.boardingPoint.code);
                                stringBuilder2.append(str2);
                                stringBuilder2.append(PNRStatusFragment.this.pnrStatusBeen.stationDetails.boardingPoint.name);
                                myTextView.setText(stringBuilder2.toString());
                                PNRStatusFragment.this.tv_bord_date.setText(Utils.changeDateFormate(PNRStatusFragment.this.pnrStatusBeen.pnrDetails.sourceDoj.formattedDate, str4, str3));
                                PNRStatusFragment.this.tv_distnce.setText(PNRStatusFragment.this.pnrStatusBeen.trainDetails.departureDetails.duration);
                                if (PNRStatusFragment.this.pnrStatusBeen.pnrDetails.destinationDoj.epoch != null) {
                                    PNRStatusFragment.this.tv_dep_time.setText(Utils.convertMillsecTime(PNRStatusFragment.this.pnrStatusBeen.pnrDetails.destinationDoj.epoch.longValue(), PNRStatusFragment.this.is24formt));
                                }
                                PNRStatusFragment.this.tv_resv_st_name.setText(PNRStatusFragment.this.pnrStatusBeen.cityDetails.destinationCity.name);
                                myTextView = PNRStatusFragment.this.tv_resv_st;
                                stringBuilder2 = new StringBuilder();
                                stringBuilder2.append(PNRStatusFragment.this.pnrStatusBeen.stationDetails.reservationUpto.code);
                                stringBuilder2.append(str2);
                                stringBuilder2.append(PNRStatusFragment.this.pnrStatusBeen.stationDetails.reservationUpto.name);
                                myTextView.setText(stringBuilder2.toString());
                                PNRStatusFragment.this.tv_resv_date.setText(Utils.changeDateFormate(PNRStatusFragment.this.pnrStatusBeen.pnrDetails.destinationDoj.formattedDate, str4, str3));
                                PNRStatusFragment.this.tv_declamer.setText(PNRStatusFragment.this.pnrStatusBeen.disclaimer);
                            }
                        });
                    } else {
                        StringBuilder stringBuilder2 = new StringBuilder();
                        stringBuilder2.append("Null  ");
                        stringBuilder2.append(body.string());
                        Log.i("AsyncOkHttp_no_data", stringBuilder2.toString());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @OnClick({R.id.iv_ref})
    public void onClickRef() {
        if (Utils.isNetworkAvailable(this.mContext)) {
            callApi();
            return;
        }
        Context context = this.mContext;
        Toast.makeText(context, context.getString(R.string.no_internet), 0).show();
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
