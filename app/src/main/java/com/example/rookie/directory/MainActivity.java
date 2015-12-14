package com.example.rookie.directory;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rookie.directory.adapter.MyAdapter;
import com.example.rookie.directory.factory.ServiceFactory;
import com.example.rookie.directory.vo.Person;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {
    private ListView contactsView;
    List<Person> contactsList = new ArrayList<Person>();
    private MyAdapter adapter;
    private Button btn_add;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        contactsView = (ListView) findViewById(R.id.contacts_view);
        btn_add = (Button) findViewById(R.id.btn_add);
        this.adapter = new MyAdapter(contactsList,this);
        contactsView.setAdapter(adapter);

        setContactsData();
        /**
         * 新建联系人
         */
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddDialog();
                setContactsData();
            }
        });
        /**
         * 点击某一项的时候
         */
        contactsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Person p = (Person) adapter.getItem(position);
                showLongClickDialog(p);
            }
        });
        /**
         * ߷长按某一项的时候,进行删除操作
         */
        contactsView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Person p = (Person) adapter.getItem(position);
                ServiceFactory.getService().delete(MainActivity.this,p);
                setContactsData();
                return true;
            }
        });

        setContactsData();
    }

    /**
     * 用户页面更新操作使用
     */
    private void setContactsData() {
        List<Person> contactData =ServiceFactory.getService().getContacts(this);
        contactsList.clear();
        contactsList.addAll(contactData);
        adapter.notifyDataSetChanged();
    }

    /**
     * 弹出增加联系人的dialog
     */
    private void showAddDialog(){
        View view = View.inflate(this, R.layout.dialog, null);
        final EditText et_name = (EditText) view.findViewById(R.id.et_name);
        final EditText et_phone = (EditText) view.findViewById(R.id.et_phone);

        new AlertDialog.Builder(this)
                .setTitle("添加联系人")
                .setView(view).setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Person person = new Person();
                person.setDisPlayName(et_name.getText()+"");
                person.setNumber(et_phone.getText() + "");
                boolean flag = ServiceFactory.getService().insert(MainActivity.this,person);
                setContactsData();  //刷新页面
                if (flag){
                    //弹出插入成功的提示
                    Toast.makeText(MainActivity.this,"Congratulations, 添加联系人成功！",Toast.LENGTH_SHORT).show();
                }
            }
        }).setNegativeButton("取消",null)
          .show();
    }

    /**
     * 弹出更新的dialog
     * @param oldPerson
     */
    private void showUpdateDialog(final Person oldPerson){
        View view = View.inflate(this,R.layout.dialog,null);
        final EditText et_name = (EditText) view.findViewById(R.id.et_name);
        final EditText et_phone = (EditText) view.findViewById(R.id.et_phone);

        et_name.setText(oldPerson.getDisPlayName());
        et_phone.setText(oldPerson.getNumber());

        new AlertDialog.Builder(this)
                .setTitle("修改联系人")
                .setView(view).setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Person p = new Person();
                p.setRawContactId(oldPerson.getRawContactId());
                p.setDisPlayName(et_name.getText()+"");
                p.setNumber(et_phone.getText() + "");
                boolean flag = ServiceFactory.getService().update(MainActivity.this, p);

                setContactsData();  //刷新页面
                if (flag){
                    //弹出更新成功的提示
                    Toast.makeText(MainActivity.this,"Congratulations, 修改联系人成功！",Toast.LENGTH_SHORT).show();
                }
            }
        }).setNegativeButton("取消",null)
                .show();
    }

    private void showLongClickDialog(final Person p){
        new AlertDialog.Builder(this)
                .setItems(new String[]{"Call", "Message", "修改联系人", "分享名片"},
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0:
                                        Intent intentCall = new Intent();
                                        intentCall.setAction(Intent.ACTION_CALL);
                                        intentCall.setData(Uri.parse("tel:" + p.getNumber()));
                                        startActivity(intentCall);
                                        break;
                                    case 1:
                                        Intent intentMessage = new Intent();
                                        intentMessage.setAction(Intent.ACTION_SENDTO);
                                        intentMessage.setData(Uri.parse("smsto://" + p.getNumber()));
                                        startActivity(intentMessage);
                                        break;
                                    case 2:
                                        showUpdateDialog(p);
                                        setContactsData();
                                        break;
                                    case 3:
                                        Intent intentShare = new Intent();
                                        intentShare.setAction(Intent.ACTION_SEND);
                                        intentShare.putExtra(Intent.EXTRA_TEXT, "欢迎使用iContact软件! " + "\n" + p.getDisPlayName() + " : " + p.getNumber());
                                        intentShare.setType("text/plain");

                                        //设置分享列表的标题，并且每次都显示分享列表
                                        startActivity(Intent.createChooser(intentShare, "分享名片到"));
                                        break;
                                    default:
                                        break;
                                }

                            }
                        }).show();
    }


}
