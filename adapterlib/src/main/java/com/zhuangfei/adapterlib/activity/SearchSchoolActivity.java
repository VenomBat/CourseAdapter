package com.zhuangfei.adapterlib.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.zhuangfei.adapterlib.ParseManager;
import com.zhuangfei.adapterlib.R;
import com.zhuangfei.adapterlib.apis.model.SearchResultModel;
import com.zhuangfei.adapterlib.activity.adapter.SearchSchoolAdapter;
import com.zhuangfei.adapterlib.station.StationManager;
import com.zhuangfei.adapterlib.utils.ViewUtils;
import com.zhuangfei.adapterlib.apis.TimetableRequest;
import com.zhuangfei.adapterlib.apis.model.AdapterResultV2;
import com.zhuangfei.adapterlib.apis.model.ListResult;
import com.zhuangfei.adapterlib.apis.model.ObjResult;
import com.zhuangfei.adapterlib.apis.model.School;
import com.zhuangfei.adapterlib.apis.model.StationModel;
import com.zhuangfei.adapterlib.apis.model.TemplateModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchSchoolActivity extends AppCompatActivity {

    Activity context;

    ListView searchListView;
    List<SearchResultModel> models;
    List<SearchResultModel> allDatas;
    SearchSchoolAdapter searchAdapter;
    List<TemplateModel> templateModels;
    String baseJs;

    EditText searchEditText;
    LinearLayout loadLayout;

    boolean firstStatus=true;
    public static final int RESULT_CODE=10;
    public static final String EXTRA_SEARCH_KEY="key";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_school);
        ViewUtils.setStatusTextGrayColor(this);
        initView();
        inits();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(ParseManager.isSuccess()){
            setResult(RESULT_CODE);
            finish();
        }
    }

    private void initView() {
        searchListView=findViewById(R.id.id_search_listview);
        searchEditText=findViewById(R.id.id_search_edittext);
        loadLayout=findViewById(R.id.id_loadlayout);
    }

    public void setLoadLayout(boolean isShow) {
        if (isShow) {
            loadLayout.setVisibility(View.VISIBLE);
        } else {
            loadLayout.setVisibility(View.GONE);
        }
    }

    private void inits() {
        context = this;
//        backLayout = findViewById(R.id.id_back);
//        backLayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                goBack();
//            }
//        });
        ParseManager.clearCache();

        models = new ArrayList<>();
        allDatas=new ArrayList<>();
        searchAdapter = new SearchSchoolAdapter(this, allDatas,models);
        searchListView.setAdapter(searchAdapter);
        searchEditText.addTextChangedListener(textWatcher);

        searchListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                onItemClicked(i);
            }
        });
        findViewById(R.id.id_search_search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                search();
            }
        });

        String searchKey=getIntent().getStringExtra(EXTRA_SEARCH_KEY);
        if(!TextUtils.isEmpty(searchKey)){
            search(searchKey);
        }
    }

    public void onItemClicked(int i) {
        SearchResultModel model=models.get(i);
        if(model==null) return;
        //通用算法解析
        if(model.getType()==SearchResultModel.TYPE_COMMON){
            TemplateModel templateModel = (TemplateModel) model.getObject();
            if (templateModel!=null){
                if(baseJs==null){
                    Toast.makeText(this,"基础函数库发生异常，请联系qq:1193600556",Toast.LENGTH_SHORT).show();
                }else if(templateModel.getTemplateTag().startsWith("custom/")){
                    Intent intent=new Intent(this,AdapterTipActivity.class);
                    startActivity(intent);
                }
                else {
                    toAdapterSameTypeActivity(templateModel.getTemplateName(),templateModel.getTemplateJs());
                }
            }
        }
        //学校教务导入
        else if(model.getType()==SearchResultModel.TYPE_SCHOOL){
            School school = (School) model.getObject();
            if(school!=null){
                if(school.getParsejs()!=null&&school.getParsejs().startsWith("template/")){
                    TemplateModel searchModel=searchInTemplate(templateModels,school.getParsejs());
                    if(baseJs==null){
                        Toast.makeText(this,"基础函数库发生异常，请联系qq:1193600556",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if(searchModel!=null){
                        toAdapterSchoolActivity(school.getSchoolName(),
                                school.getUrl(),
                                school.getType(),
                                searchModel.getTemplateJs()+baseJs);
                    }else {
                        Toast.makeText(this,"通用解析模板发生异常，请联系qq:1193600556",Toast.LENGTH_SHORT).show();
                    }
                }else{
                    toAdapterSchoolActivity(school.getSchoolName(),school.getUrl(),school.getType(),school.getParsejs());
                }

            }
        }
        //服务站
        else{
            StationModel stationModel= (StationModel) model.getObject();
            StationManager.openStationWithout(this,stationModel);
        }
    }

    public void toAdapterSameTypeActivity(String type,String js){
        Intent intent=new Intent(this,AdapterSameTypeActivity.class);
        intent.putExtra(AdapterSameTypeActivity.EXTRA_TYPE,type);
        intent.putExtra(AdapterSameTypeActivity.EXTRA_JS,js);
        startActivity(intent);
    }

    public void toAdapterSchoolActivity(String school,String url,String type,String js){
        Intent intent=new Intent(this,AdapterSchoolActivity.class);
        intent.putExtra(AdapterSchoolActivity.EXTRA_URL,url);
        intent.putExtra(AdapterSchoolActivity.EXTRA_SCHOOL,school);
        intent.putExtra(AdapterSchoolActivity.EXTRA_TYPE,type);
        intent.putExtra(AdapterSchoolActivity.EXTRA_PARSEJS,js);
        startActivity(intent);
    }

    public TemplateModel searchInTemplate(List<TemplateModel> models,String tag){
        if(models==null||tag==null) return null;
        for(TemplateModel model:models){
            if(model!=null){
                if(tag.equals(model.getTemplateTag())){
                    return model;
                }
            }
        }
        return null;
    }

    public Activity getContext() {
        return context;
    }

    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            String key = charSequence.toString();
            firstStatus=false;
            if (TextUtils.isEmpty(key)) {
                models.clear();
                allDatas.clear();
                searchAdapter.notifyDataSetChanged();
            } else {
                search(charSequence.toString());
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    public void search() {
        String key = searchEditText.getText().toString();
        search(key);
    }

    public void search(final String key) {
        if(TextUtils.isEmpty(key)) {
            return;
        }

        models.clear();
        allDatas.clear();

        if (!TextUtils.isEmpty(key)) {
            setLoadLayout(true);
            TimetableRequest.getAdapterSchoolsV2(this, key, new Callback<ObjResult<AdapterResultV2>>() {
                @Override
                public void onResponse(Call<ObjResult<AdapterResultV2>> call, Response<ObjResult<AdapterResultV2>> response) {
                    ObjResult<AdapterResultV2> result = response.body();
                    if (result != null) {
                        if (result.getCode() == 200) {
                            showResult(result.getData(),key);
                        } else {
                            Toast.makeText(SearchSchoolActivity.this, result.getMsg(), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(SearchSchoolActivity.this, "school response is null!", Toast.LENGTH_SHORT).show();
                    }
                    setLoadLayout(false);
                }

                @Override
                public void onFailure(Call<ObjResult<AdapterResultV2>> call, Throwable t) {
                    setLoadLayout(false);
                }
            });
        }
    }

    public void searchStation(final String key) {
        if (!TextUtils.isEmpty(key)) {
            setLoadLayout(true);
            TimetableRequest.getStations(this, key, new Callback<ListResult<StationModel>>() {
                @Override
                public void onResponse(Call<ListResult<StationModel>> call, Response<ListResult<StationModel>> response) {
                    setLoadLayout(false);
                    ListResult<StationModel> result = response.body();
                    if (result != null) {
                        if (result.getCode() == 200) {
                            showStationResult(result.getData(),key);
                        } else {
                            Toast.makeText(SearchSchoolActivity.this, result.getMsg(), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(SearchSchoolActivity.this, "school response is null!", Toast.LENGTH_SHORT).show();
                    }
                    setLoadLayout(false);
                }

                @Override
                public void onFailure(Call<ListResult<StationModel>> call, Throwable t) {
                    setLoadLayout(false);
                }
            });
        }
    }

    private void showStationResult(List<StationModel> result,String key) {
        if(!firstStatus&&searchEditText.getText()!=null&&key!=null&&!searchEditText.getText().toString().equals(key)){
            return;
        }
        if (result == null) return;
        List<SearchResultModel> addList=new ArrayList<>();
        for (int i=0;i<Math.min(result.size(),SearchSchoolAdapter.TYPE_STATION_MAX_SIZE);i++) {
            StationModel model=result.get(i);
            SearchResultModel searchResultModel = new SearchResultModel();
            searchResultModel.setType(SearchResultModel.TYPE_STATION);
            if(result.size()>3){
                searchResultModel.setType(SearchResultModel.TYPE_STATION_MORE);
            }
            searchResultModel.setObject(model);
            addModelToList(searchResultModel);
        }

        for (int i=0;i<result.size();i++) {
            StationModel model=result.get(i);
            SearchResultModel searchResultModel = new SearchResultModel();
            searchResultModel.setType(SearchResultModel.TYPE_STATION);
            searchResultModel.setObject(model);
            addList.add(searchResultModel);
        }

        sortResult();
        addAllDataToList(addList);
        searchAdapter.notifyDataSetChanged();
    }

    /**
     *
     * @param result
     * @param key 用于校验输入框是否发生了变化，如果变化，则忽略
     */
    private void showResult(AdapterResultV2 result,String key) {
        if(!firstStatus&&searchEditText.getText()!=null&&key!=null&&!searchEditText.getText().toString().equals(key)){
            return;
        }
        if(result==null) return;
        baseJs=result.getBase();
        templateModels=result.getTemplate();
        List<School> list=result.getSchoolList();
        if (list == null) {
            return;
        }

        if(templateModels!=null){
            for (TemplateModel model : templateModels) {
                if(firstStatus||(model.getTemplateName()!=null&&model.getTemplateName().indexOf(key)!=-1)){
                    SearchResultModel searchResultModel = new SearchResultModel();
                    searchResultModel.setType(SearchResultModel.TYPE_COMMON);
                    searchResultModel.setObject(model);
                    addModelToList(searchResultModel);
                }
            }
        }

        for (School schoolBean : list) {
            SearchResultModel searchResultModel = new SearchResultModel();
            searchResultModel.setType(SearchResultModel.TYPE_SCHOOL);
            searchResultModel.setObject(schoolBean);
            addModelToList(searchResultModel);
        }

        SearchResultModel searchResultModel = new SearchResultModel();
        searchResultModel.setType(SearchResultModel.TYPE_COMMON);
        TemplateModel addAdapterModel=new TemplateModel();
        addAdapterModel.setTemplateName("申请学校适配");
        addAdapterModel.setTemplateTag("custom/upload");
        searchResultModel.setObject(addAdapterModel);

        if(firstStatus||addAdapterModel.getTemplateName().indexOf(key)!=-1||result.getSchoolList().size()==0){
            addModelToList(searchResultModel);
        }
        sortResult();
        searchAdapter.notifyDataSetChanged();
    }

    public void sortResult() {
        if (models != null) {
            Collections.sort(models);
        }
    }

    public synchronized void addModelToList(SearchResultModel searchResultModel) {
        if (models != null) {
            models.add(searchResultModel);
        }
    }

    public synchronized void addAllDataToList(List<SearchResultModel> searchResultModels) {
        if (allDatas != null) {
            for(SearchResultModel model:searchResultModels){
                allDatas.add(model);
            }
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}