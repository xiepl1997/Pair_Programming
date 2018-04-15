/*
project:"生成你的四则运算"
authors:xiepeiliang jiangenyuan
create date:2018.4.12
 */

package com.example.xpl.pair_programming;

import android.os.Bundle;
import android.renderscript.Script;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements CheckBox.OnCheckedChangeListener,FloatingActionButton.OnClickListener,
        AdapterView.OnItemClickListener{
    //控件
    private EditText editText1 = null;
    private EditText editText2 = null;
    private CheckBox checkBox_add = null;
    private CheckBox checkBox_sub = null;
    private CheckBox checkBox_mul = null;
    private CheckBox checkBox_div = null;
    private CheckBox checkBox_bracker = null;
    private CheckBox checkBox_decimal = null;
    private ListView listView = null;
    int sum = 0;//题数
    float max = 0;//最大不超过数
    Random random;//随机数生成
    //标记
    boolean add = false;
    boolean sub = false;
    boolean mul = false;
    boolean div = false;
    boolean bracker = false;
    boolean deciml = false;
    //listview列表每列字符串，也就是运算式
    String[] items;
    //运算符数组
    String[] operator;
    //每个式子的长度，也就是式子中数的个数
    int itemLen[];
    //是否选中符号标记
    boolean checked = false;
    //运算符优先表
    HashMap<Character, Integer> prior;
    //左括号、右括号存在标记
    boolean leftbra[];
    boolean rightbra[];
    //值数组
    double[] result;
    //listview适配
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        random = new Random();
        ///初始化控件
        listView = (ListView) findViewById(R.id.listview);
        editText1 = (EditText) findViewById(R.id.quesSum_edit);
        editText2 = (EditText) findViewById(R.id.maxdigit_edit);
        checkBox_add = (CheckBox) findViewById(R.id.add_checkbox);
        checkBox_sub = (CheckBox) findViewById(R.id.sub_checkbox);
        checkBox_mul = (CheckBox) findViewById(R.id.mul_checkbox);
        checkBox_div = (CheckBox) findViewById(R.id.div_checkbox);
        checkBox_bracker = (CheckBox) findViewById(R.id.bracker_checkbox);
        checkBox_decimal = (CheckBox) findViewById(R.id.decimal_checkbox);

        // 使用外部类对多checkbox进行监听
        checkBox_add.setOnCheckedChangeListener(this);
        checkBox_sub.setOnCheckedChangeListener(this);
        checkBox_mul.setOnCheckedChangeListener(this);
        checkBox_div.setOnCheckedChangeListener(this);
        checkBox_bracker.setOnCheckedChangeListener(this);
        checkBox_decimal.setOnCheckedChangeListener(this);

        ///按下悬浮按钮生成listview数据
        ///核心代码
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(this);
    }

    //动态处理运算符数组并返回checked值
    public boolean getOperatorArray(boolean add1, boolean sub1,boolean mul1, boolean div1) {
        checked = false;
        int len = 0;
        if (add1) len++;
        if (sub1) len++;
        if (mul1) len++;
        if (div1) len++;
        if (len != 0) checked = true;
        operator = new String[len];
        int i = 0;
        if (add1)
            operator[i++] = "+";
        if (sub1)
            operator[i++] = "-";
        if (mul1)
            operator[i++] = "*";
        if (div1)
            operator[i++] = "/";
        return checked;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //多checkbox监听
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.add_checkbox:
                if (isChecked)
                    add = true;
                else
                    add = false;
                break;
            case R.id.sub_checkbox:
                if (isChecked)
                    sub = true;
                else
                    sub = false;
                break;
            case R.id.mul_checkbox:
                if (isChecked)
                    mul = true;
                else
                    mul = false;
                break;
            case R.id.div_checkbox:
                if (isChecked)
                    div = true;
                else
                    div = false;
                break;
            case R.id.bracker_checkbox:
                if (isChecked)
                    bracker = true;
                else
                    bracker = false;
                break;
            case R.id.decimal_checkbox:
                if (isChecked)
                    deciml = true;
                else
                    deciml = false;
                break;
        }
    }

    //前两数
    public void step1() {
        //前两数
        for (int i = 0; i < sum; i++) {
            int flag = 0;
            //若括号
            if (bracker && itemLen[i] > 2 && random.nextInt(2) == 1 && leftbra[i] == false) {
                items[i] += "(";
                leftbra[i] = true;
            }
            //若小数
            if (!deciml)
                items[i] += String.valueOf(random.nextInt((int) max) + 1);
            else
                items[i] += String.valueOf(random.nextInt((int) max) + random.nextInt(10) / 10.0);
            items[i] += operator[random.nextInt(operator.length)];
            if (bracker) {
                if (itemLen[i] == 3 && leftbra[i] == false) {
                    flag = 1;
                    items[i] += "(";
                    leftbra[i] = true;
                }
                if (itemLen[i] == 4 && leftbra[i] == false && random.nextInt(2) == 1) {
                    flag = 1;
                    items[i] += "(";
                    leftbra[i] = true;
                }
            }
            if (!deciml)
                items[i] += String.valueOf(random.nextInt((int) max) + 1);
            else
                items[i] += String.valueOf(random.nextInt((int) max) + random.nextInt(10) / 10.0);
            if (bracker && leftbra[i] == true && flag == 0 && itemLen[i] == 3) {
                items[i] += ")";
                rightbra[i] = true;
            }
        }
    }

    //第三数
    public void step2() {
        //第三数
        for (int i = 0; i < sum; i++) {
            int flag = 0;
            if (itemLen[i] > 2) {
                items[i] += operator[random.nextInt(operator.length)];
                if (bracker && itemLen[i] == 4 && leftbra[i] == false) {
                    flag = 1;
                    items[i] += "(";
                    leftbra[i] = true;
                }
                if (!deciml)
                    items[i] += String.valueOf(random.nextInt((int) max) + 1);
                else
                    items[i] += String.valueOf(random.nextInt((int) max) + random.nextInt(10) / 10.0);
                if (bracker && itemLen[i] == 3 && rightbra[i] == false) {
                    items[i] += ")";
                    rightbra[i] = true;
                }
                if (bracker && itemLen[i] == 4 && flag == 0 && leftbra[i] == true && items[i].charAt(0) == '(' && rightbra[i] == false) {
                    items[i] += ")";
                    rightbra[i] = true;
                }
                if (bracker && itemLen[i] == 4 && flag == 0 && leftbra[i] == true && random.nextInt(2) == 1 && rightbra[i] == false) {
                    items[i] += ")";
                    rightbra[i] = true;
                }
            } else if (itemLen[i] == 2) {
                items[i] += "=";
            }
        }
    }

    //第四数
    public void step3() {
        for (int i = 0; i < sum; i++) {
            if (itemLen[i] > 3) {
                items[i] += operator[random.nextInt(operator.length)];
                if (!deciml)
                    items[i] += String.valueOf(random.nextInt((int) max) + 1);
                else
                    items[i] += String.valueOf(random.nextInt((int) max) + random.nextInt(10) / 10.0);
                if (bracker && rightbra[i] == false) {
                    items[i] += ")";
                    rightbra[i] = true;
                }
                items[i] += "=";
            } else if (itemLen[i] == 3) {
                items[i] += "=";
            }
        }
    }

    //构建随机式，成功则返回1,失败则返回0
    public int buildItems(int sum) {
        if(sum<=0)return 0;
        items = new String[sum];
        itemLen = new int[sum];
        leftbra = new boolean[sum];
        rightbra = new boolean[sum];
        result = new double[sum];
        Random random1 = new Random();
        ///items初始化，与每个items长度，以及括号状态
        for (int i = 0; i < sum; i++) {
            items[i] = "";
            ///定义式子最多有4个数，最少2个，括号情况下3个或4个
            if (bracker==false)
                itemLen[i] = random1.nextInt(3) + 2;
            else
                itemLen[i] = random1.nextInt(2) + 3;
            ///括号状态
            leftbra[i] = false;
            rightbra[i] = false;
            result[i] = -1;
        }
        try {
            step1();
            step2();
            step3();
        } catch (Exception e) {
            return 0;
        }
        return 1;
    }

    //出错逻辑处理,返回出错类型并返回出错类型,返回0则无错,1为max、sum输入不合法，2为sum
    // 太大(100000以上)，3为没选操作符
    public int dealError(String sum_str, String max_str, boolean checked) {
        //editview非法输入捕捉
        try {
            sum = Integer.parseInt(sum_str);
            max = Float.parseFloat(max_str);
        } catch (Exception e) {
            return 1;
        } finally {
            if(max<=0||sum<=0)
                return 1;
        }
        if (sum >= 100000) {
            return 2;
        }
        if (checked == false) {
            return 3;
        }
        return 0;
    }

    //悬浮按钮点击监听
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.fab) {
            int mark = dealError(editText1.getText().toString(), editText2.getText().toString(), getOperatorArray(add,sub,mul,div));
            //无错
            if (mark == 0) {
                int temp = buildItems(sum);
                if (temp == 1) {
                    //listview装入数据
                    adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, items);
                    listView.setAdapter(adapter);
                    listView.setOnItemClickListener(this);
                    Snackbar.make(v, "已生成你的四则运算啦^_^", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                } else {
                    Snackbar.make(v, "生成失败^_^", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }
            //sum，max不合法
            else if (mark == 1) {
                Snackbar.make(v, "请检查下条件是否合理哟^_^", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
            //sum和法，但太大
            else if (mark == 2) {
                Snackbar.make(v, "生成题目数量太多小朋友消化不了哟^_^", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
            //没有选操作符
            else if (mark == 3) {
                Snackbar.make(v, "您忘了选操作符哟^_^", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        }
    }

    //listview点击监听，用于更新答案
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String str;
        if(result[position]==-1){
            str = items[position].substring(0, items[position].length()-1);
            result[position] = Calculator.conversion(str);
            BigDecimal bigDecimal = new BigDecimal(result[position]);
            result[position] = bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
            items[position] += "  "+String.valueOf(result[position]);
            result[position] = -2;
            adapter.notifyDataSetChanged();
        }
    }
}