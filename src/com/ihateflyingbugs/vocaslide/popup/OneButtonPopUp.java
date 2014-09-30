package com.ihateflyingbugs.vocaslide.popup;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ihateflyingbugs.vocaslide.R;

public class OneButtonPopUp extends Activity {

	Button bt_close_doc_popup;
	TextView tv_doc_title;
	TextView tv_doc_contents;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.popup_onebutton);

		tv_doc_title = (TextView) findViewById(R.id.tv_doc_title);
		tv_doc_contents = (TextView) findViewById(R.id.tv_doc_contents);
		bt_close_doc_popup = (Button) findViewById(R.id.bt_close_doc_popup);

		final int sort = getIntent().getIntExtra("title", 1);

		switch (sort) {
		case 0:
			tv_doc_contents.setText("�����");
			tv_doc_title.setText("�������� Ǫ������̶�?");
			break;
		case 1:

			tv_doc_contents.setText("�����");
			tv_doc_title.setText("ī��Ÿ�� ����̶�?");
			break;
		case 2:

			tv_doc_contents.setText("�����");
			tv_doc_title.setText("������ Ǫ������̶�?");
			break;
		case 3:

			tv_doc_contents.setText("�����");
			tv_doc_title.setText("���߰��� Ǫ������̶�?");
			break;
		case 4:

			tv_doc_contents.setText("1��� �ܾ����� �����");
			tv_doc_title.setText("");
			break;
		case 5:

			tv_doc_contents.setText("2-3��� �ܾ����� �����");
			tv_doc_title.setText("");
			break;
		case 6:

			tv_doc_contents.setText("4-5��� �ܾ����� �����");
			tv_doc_title.setText("");
			break;
		case 7:

			tv_doc_contents.setText("6-7��� �ܾ����� �����");
			tv_doc_title.setText("");
			break;

		case 8:

			tv_doc_contents.setText("8-9��� �ܾ����� �����");
			tv_doc_title.setText("");
			break;

		default:
			break;
		}

		bt_close_doc_popup.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				switch (sort) {
				case 0:
					finish();
					break;
				case 1:
					finish();
					break;
				case 2:
					finish();
					break;
				case 3:
					finish();
					break;
				case 4:
					finish();
					break;
				case 5:
					finish();
					break;
				case 6:
					finish();
					break;
				default:
					finish();
					break;
				}
			}
		});

	}

}
