package com.Example.IRCTCWhereismyTrain.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.widget.ImageView;
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
import com.Example.IRCTCWhereismyTrain.adapter.SearchTrainListAdapter;
import com.Example.IRCTCWhereismyTrain.adapter.SearchTrainListAdapter.setTrianListClickListner;
import com.Example.IRCTCWhereismyTrain.dialog.DataLoaderdialog;
import com.Example.IRCTCWhereismyTrain.model.CityBeen;
import com.Example.IRCTCWhereismyTrain.util.Utils;
import com.Example.IRCTCWhereismyTrain.widget.MyEditText;
import com.Example.IRCTCWhereismyTrain.widget.MyTextView;
import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.applandeo.materialcalendarview.listeners.OnDayClickListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class TrainBookFragment extends Fragment implements setTrianListClickListner {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    Activity activity;
    @BindView(R.id.calendarView)
    CalendarView calendarView;
    ArrayList<CityBeen> cityBeenArrayList = new ArrayList();
    ArrayList<CityBeen> cityBeenArrayList1 = new ArrayList();
    Calendar clickedDayCalendar = Calendar.getInstance();
    DataLoaderdialog dataLoaderdialog;
    @BindView(R.id.et_city)
    MyEditText et_city;
    CityBeen fromCityBeen;
    boolean isDateSelect = false;
    boolean isFrom = true;
    @BindView(R.id.iv_search)
    ImageView iv_search;
    @BindView(R.id.ll_search)
    LinearLayout ll_search;
    Context mContext;
    private OnFragmentInteractionListener mListener;
    private String mParam1;
    private String mParam2;
    ProgressDialog progressDialog;
    @BindView(R.id.rl_datepicker)
    RelativeLayout rl_datepicker;
    @BindView(R.id.rv_popular_search)
    RecyclerView rv_popular_search;
    @BindView(R.id.rv_search)
    RecyclerView rv_search;
    CityBeen toCityBeen;
    SearchTrainListAdapter trainListAdapter;
    SearchTrainListAdapter trainpopularListAdapter;
    @BindView(R.id.tv_date)
    MyTextView tv_date;
    @BindView(R.id.tv_date_day)
    MyTextView tv_date_day;
    @BindView(R.id.tv_date_month)
    MyTextView tv_date_month;
    @BindView(R.id.tv_from)
    MyTextView tv_from;
    @BindView(R.id.tv_from_sation)
    MyTextView tv_from_sation;
    @BindView(R.id.tv_popular_lable)
    MyTextView tv_popular_lable;
    @BindView(R.id.tv_to)
    MyTextView tv_to;
    @BindView(R.id.tv_to_station)
    MyTextView tv_to_station;

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    public static TrainBookFragment newInstance(String str, String str2) {
        TrainBookFragment trainBookFragment = new TrainBookFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ARG_PARAM1, str);
        bundle.putString(ARG_PARAM2, str2);
        trainBookFragment.setArguments(bundle);
        return trainBookFragment;
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (getArguments() != null) {
            this.mParam1 = getArguments().getString(ARG_PARAM1);
            this.mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View inflate = layoutInflater.inflate(R.layout.frag_book_train, viewGroup, false);
        ButterKnife.bind((Object) this, inflate);
        this.mContext = getActivity();
        this.progressDialog = new ProgressDialog(this.mContext);
        this.dataLoaderdialog = new DataLoaderdialog(this.mContext);
        this.calendarView.setOnDayClickListener(new OnDayClickListener() {
            public void onDayClick(EventDay eventDay) {
                if (eventDay.getCalendar().getTime().compareTo(Calendar.getInstance().getTime()) >= 0 || DateUtils.isToday(eventDay.getCalendar().getTimeInMillis())) {
                    TrainBookFragment.this.clickedDayCalendar = eventDay.getCalendar();
                    TrainBookFragment.this.isDateSelect = true;
                    return;
                }
                TrainBookFragment.this.isDateSelect = false;
            }
        });
        Date time = this.clickedDayCalendar.getTime();
        String format = new SimpleDateFormat("dd").format(time);
        String format2 = new SimpleDateFormat("MMMM").format(time);
        String format3 = new SimpleDateFormat("EEEE").format(time);
        String format4 = new SimpleDateFormat("yyyy").format(time);
        this.tv_date.setText(format);
        MyTextView myTextView = this.tv_date_month;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(format2);
        stringBuilder.append(" ");
        stringBuilder.append(format4);
        myTextView.setText(stringBuilder.toString());
        this.tv_date_day.setText(format3);
        this.rv_search.setLayoutManager(new LinearLayoutManager(this.mContext, 1, false));
        this.rv_popular_search.setLayoutManager(new LinearLayoutManager(this.mContext, 1, false));
        inflate.setFocusableInTouchMode(true);
        inflate.requestFocus();
        inflate.setOnKeyListener(new OnKeyListener() {
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (i != 4 || !TrainBookFragment.this.ll_search.isShown()) {
                    return false;
                }
                TrainBookFragment.this.ll_search.setVisibility(View.GONE);
                Log.e("KEYCODE_BACK", "=========>>>>>>>");
                return true;
            }
        });
        this.et_city.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            public void afterTextChanged(Editable editable) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("=====>>>>>");
                String str = " ";
                stringBuilder.append(editable.toString().startsWith(str));
                Log.e("afterTextChanged", stringBuilder.toString());
                if (editable.toString().equalsIgnoreCase("") || editable.toString().startsWith(str)) {
                    TrainBookFragment.this.rv_search.setVisibility(View.GONE);
                    TrainBookFragment.this.tv_popular_lable.setVisibility(View.VISIBLE);
                    TrainBookFragment.this.rv_popular_search.setVisibility(View.VISIBLE);
                    return;
                }
                if (!TrainBookFragment.this.rv_search.isShown()) {
                    TrainBookFragment.this.rv_search.setVisibility(View.VISIBLE);
                    TrainBookFragment.this.tv_popular_lable.setVisibility(View.GONE);
                    TrainBookFragment.this.rv_popular_search.setVisibility(View.GONE);
                }
                if (Utils.isNetworkAvailable(TrainBookFragment.this.mContext)) {
                    TrainBookFragment.this.CallApi(editable.toString());
                } else {
                    Toast.makeText(TrainBookFragment.this.mContext, TrainBookFragment.this.mContext.getString(R.string.no_internet), 0).show();
                }
            }
        });
        this.cityBeenArrayList1 = (ArrayList) new Gson().fromJson(getString(R.string.popular_place), new TypeToken<ArrayList<CityBeen>>() {
        }.getType());
        return inflate;
    }

    @OnClick({R.id.ll_date})
    public void onClickDate() {
        this.rl_datepicker.setVisibility(View.VISIBLE);
        OpenCalender();
    }

    @OnClick({R.id.tv_done})
    public void onClickDone() {
        if (this.isDateSelect) {
            this.rl_datepicker.setVisibility(View.GONE);
            Date time = this.clickedDayCalendar.getTime();
            String format = new SimpleDateFormat("dd").format(time);
            String format2 = new SimpleDateFormat("MMMM").format(time);
            String format3 = new SimpleDateFormat("EEEE").format(time);
            String format4 = new SimpleDateFormat("yyyy").format(time);
            this.tv_date.setText(format);
            MyTextView myTextView = this.tv_date_month;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(format2);
            stringBuilder.append(" ");
            stringBuilder.append(format4);
            myTextView.setText(stringBuilder.toString());
            this.tv_date_day.setText(format3);
            return;
        }
        Toast.makeText(this.mContext, "Please Select Date", 0).show();
    }

    @OnClick({R.id.tv_search})
    public void onClickSearch() {
        CityBeen cityBeen = this.fromCityBeen;
        if (cityBeen == null || this.toCityBeen == null) {
            Toast.makeText(this.mContext, "Please select city.", 0).show();
        } else if (cityBeen.code == null || this.toCityBeen.code == null) {
            Toast.makeText(this.mContext, "Please select genuine city.", 0).show();
        } else {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(this.tv_from.getText().toString());
            stringBuilder.append(" To ");
            stringBuilder.append(this.tv_to.getText().toString());
            String stringBuilder2 = stringBuilder.toString();
            Date time = this.clickedDayCalendar.getTime();
            String str = "yyyyMMdd";
            String format = new SimpleDateFormat(str).format(time);
            String format2 = new SimpleDateFormat("dd MMM,EEEE").format(time);
            StringBuilder stringBuilder3 = new StringBuilder();
            stringBuilder3.append(this.fromCityBeen.code);
            String str2 = "/";
            stringBuilder3.append(str2);
            stringBuilder3.append(this.toCityBeen.code);
            stringBuilder3.append(str2);
            stringBuilder3.append(new SimpleDateFormat(str).format(time));
            String stringBuilder4 = stringBuilder3.toString();
            stringBuilder = new StringBuilder();
            stringBuilder.append("===>>>");
            stringBuilder.append(format2);
            Log.e("dates", stringBuilder.toString());
            TrainRouteListFragment trainRouteListFragment = new TrainRouteListFragment();
            openFragment(TrainRouteListFragment.newInstance(stringBuilder2, format2, stringBuilder4, format, this.fromCityBeen.code, this.toCityBeen.code));
            this.fromCityBeen = null;
            this.toCityBeen = null;
        }
    }

    @OnClick({R.id.ll_from})
    public void onClickFrom() {
        this.isFrom = true;
        this.trainpopularListAdapter = new SearchTrainListAdapter(this.mContext, this.cityBeenArrayList1, this, this.isFrom);
        this.rv_popular_search.setAdapter(this.trainpopularListAdapter);
        this.ll_search.setVisibility(View.VISIBLE);
    }

    @OnClick({R.id.ll_to})
    public void onClickTo() {
        this.isFrom = false;
        this.trainpopularListAdapter = new SearchTrainListAdapter(this.mContext, this.cityBeenArrayList1, this, this.isFrom);
        this.rv_popular_search.setAdapter(this.trainpopularListAdapter);
        this.ll_search.setVisibility(View.VISIBLE);
    }

    @OnClick({R.id.ll_to})
    public void onClickSearchCity() {
        if (!(this.et_city.getText().toString().toString().equalsIgnoreCase("") || this.et_city.getText().toString().toString().startsWith(" "))) {
            if (Utils.isNetworkAvailable(this.mContext)) {
                CallApi(this.et_city.getText().toString());
            } else {
                Context context = this.mContext;
                Toast.makeText(context, context.getString(R.string.no_internet), 0).show();
            }
        }
    }

    public void OpenCalender() {
        Date date = new Date();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("===>>>>");
        stringBuilder.append(date);
        Log.e("current date", stringBuilder.toString());
        Calendar instance = Calendar.getInstance();
        Calendar instance2 = Calendar.getInstance();
        instance.setTime(date);
        instance.add(5, -1);
        instance2.setTime(date);
        instance2.add(2, 4);
        this.calendarView.setMinimumDate(instance);
        this.calendarView.setMaximumDate(instance2);
    }

    public void onAttach(Context context) {
        super.onAttach(context);
        this.activity = (Activity) context;
    }

    public void CallApi(String str) {
        OkHttpClient okHttpClient = new OkHttpClient();
        HttpUrl.Builder newBuilder = HttpUrl.parse("https://www.makemytrip.com/pwa-hlp/getRailsCity").newBuilder();
        newBuilder.addQueryParameter("city", str);
        okHttpClient.newCall(new Request.Builder().url(newBuilder.build()).build()).enqueue(new Callback() {
            public void onFailure(Call call, IOException iOException) {
                Log.e("AsyncOkHttp", "Error in getting response");
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
                        TrainBookFragment.this.cityBeenArrayList = (ArrayList) gson.fromJson(string, type);
                        TrainBookFragment.this.trainListAdapter = new SearchTrainListAdapter(TrainBookFragment.this.mContext, TrainBookFragment.this.cityBeenArrayList, TrainBookFragment.this, TrainBookFragment.this.isFrom);
                        TrainBookFragment.this.activity.runOnUiThread(new Runnable() {
                            public void run() {
                                if (!(TrainBookFragment.this.activity == null || TrainBookFragment.this.activity.isFinishing() || TrainBookFragment.this.dataLoaderdialog == null)) {
                                    TrainBookFragment.this.dataLoaderdialog.dismiss();
                                }
                                try {
                                    TrainBookFragment.this.rv_search.setAdapter(TrainBookFragment.this.trainListAdapter);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        return;
                    } catch (Exception e) {
                        e.printStackTrace();
                        return;
                    }
                }
                Log.i("AsyncOkHttp_no_data", "Failed To load");
            }
        });
    }

    public void onClickTrianList(CityBeen cityBeen, boolean z) {
        String str = ",";
        String str2 = "";
        MyTextView myTextView;
        StringBuilder stringBuilder;
        if (z) {
            try {
                this.fromCityBeen = new CityBeen();
                this.fromCityBeen = cityBeen;
                if (this.fromCityBeen.cityName != null) {
                    myTextView = this.tv_from;
                    stringBuilder = new StringBuilder();
                    stringBuilder.append(this.fromCityBeen.cityName.split(str)[0]);
                    stringBuilder.append(str2);
                    myTextView.setText(stringBuilder.toString());
                }
                myTextView = this.tv_from_sation;
                stringBuilder = new StringBuilder();
                stringBuilder.append(this.fromCityBeen.code);
                stringBuilder.append(str2);
                myTextView.setText(stringBuilder.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            this.toCityBeen = new CityBeen();
            this.toCityBeen = cityBeen;
            if (this.toCityBeen.cityName != null) {
                myTextView = this.tv_to;
                stringBuilder = new StringBuilder();
                stringBuilder.append(this.toCityBeen.cityName.split(str)[0]);
                stringBuilder.append(str2);
                myTextView.setText(stringBuilder.toString());
            }
            myTextView = this.tv_to_station;
            stringBuilder = new StringBuilder();
            stringBuilder.append(this.toCityBeen.code);
            stringBuilder.append(str2);
            myTextView.setText(stringBuilder.toString());
        }
        this.et_city.setText(str2);
        this.cityBeenArrayList.clear();
        this.ll_search.setVisibility(View.GONE);
        Utils.hideKeyboardFrom(this.mContext, this.et_city);
    }

    public void openFragment(Fragment fragment) {
        FragmentTransaction beginTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        beginTransaction.replace(R.id.frame, fragment);
        beginTransaction.addToBackStack(fragment.toString());
        beginTransaction.commit();
    }

    @OnClick({R.id.iv_back})
    public void onClickBack() {
        ((ActivityMainTicketBook) this.activity).onBackPressed();
    }

    @OnClick({R.id.iv_back_search})
    public void onClickSearchBack() {
        this.ll_search.setVisibility(View.GONE);
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
