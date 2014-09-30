package com.ihateflyingbugs.vocaslide.popup;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ihateflyingbugs.vocaslide.R;

public class TwoButtonPopUp extends Activity {

	Button bt_yes_popup;
	Button bt_no_popup;
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
		setContentView(R.layout.popup_twobutton);

		tv_doc_title = (TextView) findViewById(R.id.tv_two_title);
		tv_doc_contents = (TextView) findViewById(R.id.tv_two_contents);
		bt_yes_popup = (Button) findViewById(R.id.bt_yes_popup);
		bt_no_popup = (Button) findViewById(R.id.bt_no_popup);

		final int sort = getIntent().getIntExtra("title", 1);

		switch (sort) {
		case 0:
			tv_doc_contents.setText("���� �ܾ�����\n" + "��� �ܾ�������\n�����Ͻðڽ��ϱ�?");
			tv_doc_title.setText("");
			bt_yes_popup.setText("����");
			bt_no_popup.setText("���");
			break;
		case 1:

			tv_doc_contents.setText("�α׾ƿ��Ͻðڽ��ϱ�?");
			tv_doc_title.setText("");
			bt_yes_popup.setText("Ȯ��");
			bt_no_popup.setText("���");
			break;
		case 2:

			tv_doc_contents.setText("���� �� �ϼ���!");
			tv_doc_title.setText("");
			bt_yes_popup.setText("�����Ϸ� ����");
			bt_no_popup.setText("�������� �̷��");
			break;
		case 3:

			tv_doc_contents.setText("���� �����غ�~");
			tv_doc_title.setText("");
			bt_yes_popup.setText("�����Ϸ� ����");
			bt_no_popup.setText("�������� �̷��");
			break;
		case 4:

			tv_doc_contents.setText("������ �н�����\nä���� �ʾҽ��ϴ�.\n��ǥ �н����� ä���ּ���.");
			tv_doc_title.setText("");
			bt_yes_popup.setText("�����Ϸ� ����");
			bt_no_popup.setText("�������� �̷��");
			break;
		case 5:

			tv_doc_contents.setText("�̹� " + "��\n�Ⱓ�� " + "\n���ܾ �߰��Ǿ����ϴ�.");
			tv_doc_title.setText("");
			bt_yes_popup.setText("�����Ϸ� ����");
			bt_no_popup.setText("�������� �̷��");
			break;
		case 6:

			tv_doc_contents.setText("����\n�д翵�ܾ ���� �ܾ\n" + "%�̻� �����Ǿ����ϴ�.");
			tv_doc_title.setText("");
			bt_yes_popup.setText("�����Ϸ� ����");
			bt_no_popup.setText("�������� �̷��");
			break;

		case 7:

			tv_doc_contents.setText("���� �ܿ�� ����� ���� �ܾ�\n" + "����\n"
					+ "���� ��ٸ��� �ֽ��ϴ�.");
			tv_doc_title.setText("");
			bt_yes_popup.setText("�����Ϸ� ����");
			bt_no_popup.setText("�������� �̷��");
			break;

		default:
			break;
		}

		bt_yes_popup.setOnClickListener(new View.OnClickListener() {

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

		bt_no_popup.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
	}

}
