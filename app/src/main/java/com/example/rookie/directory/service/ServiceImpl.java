package com.example.rookie.directory.service;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.net.Uri;
import android.os.RemoteException;
import android.provider.ContactsContract;

import com.example.rookie.directory.vo.Person;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rookie on 2015/12/13.
 */

public class ServiceImpl {

    /**
     * 读取通讯录的信息，进行展示
     * @return
     */
    public List<Person> getContacts(Context context){
        List<Person> contacts = new ArrayList<Person>();
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(ContactsContract.RawContacts.CONTENT_URI, new String[]{ContactsContract.RawContacts._ID}, null, null, null);
        Person p;
        while (cursor.moveToNext()){
            p = new Person();
            //获取id
            long rawContactId = cursor.getLong(cursor.getColumnIndex(ContactsContract.RawContacts._ID));
            p.setRawContactId(rawContactId);
            Cursor dataCursor = resolver.query(
                    ContactsContract.Data.CONTENT_URI,
                    null,
                    ContactsContract.Data.RAW_CONTACT_ID + "=?",
                    new String[]{String.valueOf(rawContactId)},
                    null);
            while (dataCursor.moveToNext()){
                String data1 = dataCursor.getString(dataCursor.getColumnIndex(ContactsContract.Data.DATA1));
                String mimetype = dataCursor.getString(dataCursor.getColumnIndex(ContactsContract.Data.MIMETYPE));
                if (mimetype.equals(ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)){
                    p.setDisPlayName(data1);
                } else if (mimetype.equals(ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)){
                    p.setNumber(data1);
                }
            }
            contacts.add(p);
            dataCursor.close();
        }
        cursor.close();
        return contacts;
    }
    /**
     * 插入新的记录
     * @param person
     * @return
     */
    public boolean  insert(Context context,Person person){
        ContentResolver resolver = context.getContentResolver();
        ContentValues values = new ContentValues();
        Uri rawContactUri = resolver.insert(ContactsContract.RawContacts.CONTENT_URI,values);
        long rawContactId = ContentUris.parseId(rawContactUri);

        ContentValues valuesData1 = new ContentValues();
        valuesData1.put(ContactsContract.Data.RAW_CONTACT_ID,rawContactId);
        valuesData1.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
        valuesData1.put(ContactsContract.CommonDataKinds.Phone.NUMBER,person.getNumber());
        resolver.insert(ContactsContract.Data.CONTENT_URI,valuesData1);

        ContentValues valuesData2 = new ContentValues();
        valuesData2.put(ContactsContract.Data.RAW_CONTACT_ID,rawContactId);
        valuesData2.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE);
        valuesData2.put(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME,person.getDisPlayName());
        resolver.insert(ContactsContract.Data.CONTENT_URI,valuesData2);

        return true;
    }

    /**
     * 删除联系人
     */
    public void delete(Context context,Person p){
        ContentResolver resolver = context.getContentResolver();
        resolver.delete(ContactsContract.RawContacts.CONTENT_URI, ContactsContract.RawContacts._ID +"=?",
                new String[]{String.valueOf(p.getRawContactId())});
    }
    /**
     * 更新联系人
     */
    public boolean update(Context context,Person p){
        ContentResolver resolver = context.getContentResolver();
        ArrayList<ContentProviderOperation> ops = new ArrayList<>();

        ops.add(ContentProviderOperation
                        .newUpdate(ContactsContract.Data.CONTENT_URI)
                        .withSelection(ContactsContract.Data.RAW_CONTACT_ID + "=? AND "+ ContactsContract.Data.MIMETYPE + "=?",
                                new String[]{String.valueOf(p.getRawContactId()),
                                        ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE})
                .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, p.getDisPlayName())
                .build()
        );

        ops.add(ContentProviderOperation
                        .newUpdate(ContactsContract.Data.CONTENT_URI)
                        .withSelection(ContactsContract.Data.RAW_CONTACT_ID + "=? AND "+ ContactsContract.Data.MIMETYPE + "=?",
                                new String[]{String.valueOf(p.getRawContactId()),
                                        ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE})
                        .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER,p.getNumber())
                        .build()
        );

        try {
            resolver.applyBatch(ContactsContract.AUTHORITY,ops);
        } catch (RemoteException e){
            e.printStackTrace();
        } catch (OperationApplicationException e){
            e.printStackTrace();

        }
        return true;
    }

}












