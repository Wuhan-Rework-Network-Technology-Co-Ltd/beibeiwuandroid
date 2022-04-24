package xin.banghua.beiyuan;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.fragment.app.FragmentActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ConversationListActivity extends FragmentActivity {
    @BindView(R.id.address_book)
    Button address_book;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation_list);


        ButterKnife.bind(this);

        address_book.setOnClickListener(v -> {
            Intent intent = new Intent(ConversationListActivity.this,Main2Activity.class);
            startActivity(intent);
        });
    }
}
