package adapters;

import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.ultimatesoftil.trackeru.R;

/**
 * Created by mike on 17 Dec 2017.
 */

public class MyHolder2 {


    TextView status,username;
    ImageView img2;
    ImageView status_img;
    ImageButton menu;


    public MyHolder2(View itemView) {

        menu=(ImageButton)itemView.findViewById(R.id.imageButton);
        username= (TextView) itemView.findViewById(R.id.usename);
        img2=(ImageView) itemView.findViewById(R.id.groupprofile);
        status_img=(ImageView)itemView.findViewById(R.id.status_img);
        status=(TextView)itemView.findViewById(R.id.status_text);

    }
}

