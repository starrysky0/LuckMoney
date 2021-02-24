package com.yorhp.luckmoney;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.yorhp.luckmoney.service.LuckMoneyService;
import com.yorhp.luckmoney.util.SharedPreferencesUtil;

public class SettingActivity extends AppCompatActivity {

    private EditText et_setting;
    private Button bt_confirm;
    private Button bt_cancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initView();
    }

    private void initView() {
        et_setting = findViewById(R.id.et_setting);
        bt_confirm = findViewById(R.id.bt_confirm);
        bt_cancel = findViewById(R.id.bt_cancel);

        bt_confirm.setOnClickListener(v -> {
            String trim = et_setting.getText().toString().trim();
            if (TextUtils.isEmpty(trim)) {
                Toast.makeText(this, "配置参数不能为空", Toast.LENGTH_SHORT).show();
            } else {
                String[] split = trim.split(",");
                if (split.length == 7) {
                    SharedPreferencesUtil.save("HUMAN_LIST_TXT_ID", split[0]);
                    SharedPreferencesUtil.save("AVATAR_ID", split[0]);
                    SharedPreferencesUtil.save("AUL_ID", split[0]);
                    SharedPreferencesUtil.save("OPEN_ID", split[0]);
                    SharedPreferencesUtil.save("HUMAN_LIST", split[0]);
                    SharedPreferencesUtil.save("AUM_ID", split[0]);
                    SharedPreferencesUtil.save("DETAIL_CHAT_LIST_ID", split[0]);
                    LuckMoneyService.HUMAN_LIST_TXT_ID = split[0];
                    LuckMoneyService.AVATAR_ID = split[1];
                    LuckMoneyService.AUL_ID = split[2];
                    LuckMoneyService.OPEN_ID = split[3];
                    LuckMoneyService.HUMAN_LIST = split[4];
                    LuckMoneyService.AUM_ID = split[5];
                    LuckMoneyService.DETAIL_CHAT_LIST_ID = split[6];
                } else {
                    Toast.makeText(this, "配置参数有误", Toast.LENGTH_SHORT).show();
                }
            }

        });
        bt_cancel.setOnClickListener(v -> {
            LuckMoneyService.HUMAN_LIST_TXT_ID = "com.tencent.mm:id/cyv";
            LuckMoneyService.AVATAR_ID = "com.tencent.mm:id/au2";
            LuckMoneyService.AUL_ID = "com.tencent.mm:id/tt";
            LuckMoneyService.OPEN_ID = "com.tencent.mm:id/f4f";
            LuckMoneyService.HUMAN_LIST = "com.tencent.mm:id/f67";
            LuckMoneyService.AUM_ID = "com.tencent.mm:id/ahs";
            LuckMoneyService.DETAIL_CHAT_LIST_ID = "com.tencent.mm:id/awv";
            SharedPreferencesUtil.save("HUMAN_LIST_TXT_ID", LuckMoneyService.HUMAN_LIST_TXT_ID);
            SharedPreferencesUtil.save("AVATAR_ID",  LuckMoneyService.AVATAR_ID);
            SharedPreferencesUtil.save("AUL_ID", LuckMoneyService.AUL_ID );
            SharedPreferencesUtil.save("OPEN_ID", LuckMoneyService.OPEN_ID);
            SharedPreferencesUtil.save("HUMAN_LIST", LuckMoneyService.HUMAN_LIST);
            SharedPreferencesUtil.save("AUM_ID", LuckMoneyService.AUM_ID);
            SharedPreferencesUtil.save("DETAIL_CHAT_LIST_ID", LuckMoneyService.DETAIL_CHAT_LIST_ID);
            StringBuffer buffer = new StringBuffer();
            buffer.append(LuckMoneyService.HUMAN_LIST_TXT_ID);
            buffer.append(",");
            buffer.append(LuckMoneyService.AVATAR_ID);
            buffer.append(",");
            buffer.append(LuckMoneyService.AUL_ID);
            buffer.append(",");
            buffer.append(LuckMoneyService.OPEN_ID);
            buffer.append(",");
            buffer.append(LuckMoneyService.HUMAN_LIST);
            buffer.append(",");
            buffer.append(LuckMoneyService.AUM_ID);
            buffer.append(",");
            buffer.append(LuckMoneyService.DETAIL_CHAT_LIST_ID);
            et_setting.setText(buffer.toString());

        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
