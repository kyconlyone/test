package com.ihateflyingbugs.vocaslide.tutorial;
 
import java.util.LinkedHashMap;
import java.util.Map;

import com.ihateflyingbugs.vocaslide.R;
 
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;

public class SectionedAdapter extends BaseAdapter {
     
    public final static int TYPE_SECTION_HEADER = 0;
 
    public ArrayAdapter<String> headers;
    public Map<String, Adapter> sections = new LinkedHashMap<String, Adapter>();
     
    // ��� ����
    public SectionedAdapter(Context context) {
        headers = new ArrayAdapter<String>(context, R.layout.item_header);
    }
     
    // ���, ���� �ʵ� �߰� �޼ҵ�
    public void addSection(String section, Adapter adapter) {
        this.headers.add(section);
        this.sections.put(section, adapter);
    }
     
    // ��� �������� �� ��ȯ(+ ��� �ʵ�)
    @Override
    public int getCount() {
        int total = 0;
        for(Adapter adapter : this.sections.values())
            total += adapter.getCount() + 1;
         
        return total;
    }
 
    @Override
    public Object getItem(int position) {
        for(Object section : this.sections.keySet()) {
            Adapter adapter = sections.get(section);
            int size = adapter.getCount() + 1;
             
            // position�� �ش� section �ȿ� �ִ��� üũ
            if(position == 0) return section;
            if(position < size) return adapter.getItem(position - 1);
             
            // position�� ������ ũ�⸦ �Ѿ�ٸ� ���� �������� ����
            position -= size;
        }
        return null;
    }
 
    @Override
    public long getItemId(int position) {
        return 0;
    }
 
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int sectionNum = 0;
        for(Object section : this.sections.keySet()) {
            Adapter adapter = sections.get(section);
            int size = adapter.getCount() + 1;
             
            // position�� �ش� section �ȿ� �ִ��� üũ
            if(position == 0) return headers.getView(sectionNum, convertView, parent);
            if(position < size) return adapter.getView(position - 1, convertView, parent);
             
            // position�� ������ ũ�⸦ �Ѿ�ٸ� ���� �������� ����
            position -= size;
            sectionNum++;
        }
        return null;
    }
 
    // �� Ÿ���� ũ��(section�� ����)�� ��ȯ
    public int getViewTypeCount() {
        int total = 1;
        for(Adapter adapter : this.sections.values())
            total += adapter.getViewTypeCount();
         
        return total;
    }
     
    // ������(�ʵ�)�� Ÿ���� ��ȯ
    public int getItemViewType(int position) {
        int type = 1;
        for(Object section : this.sections.keySet()) {
             
            // �ش� ������ ����Ϳ� ������� ������ ũ�⸦ ��ȯ
            Adapter adapter = sections.get(section);
            int size = adapter.getCount() + 1;  // +1 : header
             
            // position�� �ش� section �ȿ� �ִ��� üũ
            if(position == 0) return TYPE_SECTION_HEADER;
            if(position < size) return type + adapter.getItemViewType(position - 1);
             
            // position�� ������ ũ�⸦ �Ѿ�ٸ� ���� �������� ����
            position -= size;
            type += adapter.getViewTypeCount();
        }
        return -1;
    }
     
    public boolean areAllItemsSectable() {
        return false;
    }
     
    public boolean isEnabled(int position) {
        return (getItemViewType(position) != TYPE_SECTION_HEADER);
    }
}