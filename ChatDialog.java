package com.twasel;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Dialog;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.adapters.MessagesListAdapter;
import com.cls.message;
import com.cls.user;

import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by MOHAMMAD ALSAMARAH on 10/29/2015.
 */
@SuppressWarnings("ALL")
public  class ChatDialog  extends Dialog {

    private static final int NO_OF_EMOTICONS = 54;

    private GridView emoticonsCover;
    private List<String> MsgList= new ArrayList<>();

    private List<Bitmap> emoticons= new ArrayList<>();
    private List<String> emoticonsPath= new ArrayList<>();
    private List<String> emoticonsName= new ArrayList<>();
    ListView messageHistoryList;
    EditText message;
    LinearLayout btnsChatAtachment;
    String to_user, login_user;
    Typeface FontFaceIcon, FontFaceText;
    ImageView imgChatProfile, emoticons_button;
    TextView txtChatProfile, txtPosterString;
    MessagesListAdapter adapter;
    Button sendMessageButton  , btn_add_photo_chat, btn_add_video_chat,btn_add_loction_chat,btn_add_caputer_chat,btnAttachment;
    String longitude = "",  latitude = "";

    List<com.cls.message> messagesItems = new ArrayList<>();


    boolean listbtns = false;
    Context context;

    public ChatDialog(Context context, String to_user) {
        super(context);
        this.context= context;
        this.to_user = to_user;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //before

        setContentView(R.layout.activity_chat);

        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        FontFaceIcon = Typeface.createFromAsset(context.getAssets(), "fonts/ionicons.ttf");
        FontFaceText = Typeface.createFromAsset(context.getAssets(), "fonts/exolight.ttf");

        sendMessageButton = (Button)findViewById(R.id.sendMessageButton);
        emoticons_button= (ImageView)findViewById(R.id.emoticons_button);

        btn_add_photo_chat= (Button)findViewById(R.id.btn_add_photo_chat);
        btn_add_video_chat = (Button)findViewById(R.id.btn_add_video_chat);
        btn_add_loction_chat = (Button)findViewById(R.id.btn_add_loction_chat);
        btn_add_caputer_chat = (Button)findViewById(R.id.btn_add_caputer_chat);
        btnAttachment= (Button)findViewById(R.id.btnAttachment);
        imgChatProfile = (ImageView) findViewById(R.id.imgChatProfile);
        txtChatProfile = (TextView) findViewById(R.id.txtChatProfile);
        txtPosterString= (TextView) findViewById(R.id.txtPosterString);
        message = (EditText)findViewById(R.id.chat_content);
        btnsChatAtachment = (LinearLayout) findViewById(R.id.btnsChatAtachment);

        messageHistoryList = (ListView)findViewById(R.id.messageHistoryList);
        login_user = "1";

        UserFunc.SetSeenMsg(to_user, "1");

        build_chat_list();
        // FaceMessageButton.setTypeface(FontFaceIcon);
        sendMessageButton.setTypeface(FontFaceIcon);

        btn_add_photo_chat.setTypeface(FontFaceIcon);
        btn_add_video_chat.setTypeface(FontFaceIcon);
        btn_add_loction_chat.setTypeface(FontFaceIcon);
        btn_add_caputer_chat.setTypeface(FontFaceIcon);
        btnAttachment.setTypeface(FontFaceIcon);


        btnAttachment.setText("\uf367");


        btnAttachment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listbtns == false) {
                    btnsChatAtachment.setVisibility(View.VISIBLE);
                    listbtns = true;
                }
                else {
                    listbtns = false;
                    btnsChatAtachment.setVisibility(View.GONE);
                }
            }
        });
        btn_add_photo_chat.setText("\uf148");
        btn_add_video_chat.setText("\uf4cd");
        btn_add_loction_chat.setText("\uf203");
        btn_add_caputer_chat.setText("\uf118");

        user to_userobj=null;
        try {
            to_userobj = UserFunc.getUserById(to_user);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        txtChatProfile.setText(to_userobj.getName_first() + " " + to_userobj.getName_last());

        try {
            Bitmap bitmap = BitmapFactory.decodeStream((InputStream) new URL(UserFunc.URL_Server + "media/" + to_userobj.getPicture()).getContent());
            imgChatProfile.setImageBitmap(bitmap);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        sendMessageButton.setText("\uf2f6");
        // FaceMessageButton.setText("\uf31c");

        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendMsg();
            }
        });


        emoticonsCover = (GridView) findViewById(R.id.emoticons_grid);

        emoticons_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (emoticonsCover.getVisibility() == View.VISIBLE)
                    emoticonsCover.setVisibility(View.GONE);
                else {
                    emoticonsCover.setVisibility(View.VISIBLE);
                }

            }
        });
        readEmoticons();
        EmoticonsGridAdapter emoticonsGridAdapter = new EmoticonsGridAdapter(context,emoticons);
        emoticonsCover.setAdapter(emoticonsGridAdapter);
    }

    void SendMsg()
    {
        if(message.getText().toString().length() > 0) {
            Spanned sp = message.getText();
            String txt = Html.toHtml(sp).toString();



            //Toast.makeText(context, sp, Toast.LENGTH_LONG).show();
            try {
                UserFunc.SendMsg(login_user, to_user,  txt, longitude,latitude);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            message.setText("");
            txtPosterString.setText("");

            try {
                messagesItems = UserFunc.GetMessagesByToUser(login_user,to_user);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            build_chat_list();

        }
    }
    void build_chat_list()
    {
        try {
            messagesItems = UserFunc.GetMessagesByToUser(login_user,to_user);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if(messagesItems != null) {

            adapter = new MessagesListAdapter(context,messagesItems , login_user );
            messageHistoryList.setAdapter(adapter);
        }

        scrollMyListViewToBottom();

    }

    /**
     * Reading all emoticons in local cache
     */
    private void readEmoticons () {

        //emoticons = new Bitmap[NO_OF_EMOTICONS];
        for (short i = 0; i < NO_OF_EMOTICONS; i++) {
            emoticons.add( getImage((i+1) + ".png"));
            emoticonsPath.add("emoticons/"+ (i+1) + ".png");
            emoticonsName.add( (i+1) + ".png");
        }

    }

    /**
     * For loading smileys from assets
     */
    private Bitmap getImage(String path) {
        AssetManager mngr = context. getAssets();
        InputStream in = null;
        try {
            in = mngr.open("emoticons/" + path);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Bitmap temp = BitmapFactory.decodeStream(in, null, null);
        return temp;
    }

    public void scrollMyListViewToBottom() {
        messageHistoryList.post(new Runnable() {
            @Override
            public void run() {
                // Select the last row so it will scroll into view...

                messageHistoryList.setSelection(adapter.getCount() - 1);
            }
        });
    }


    public class EmoticonsGridAdapter extends BaseAdapter {

        private List<Bitmap> paths;
        Context mContext;

        public EmoticonsGridAdapter(Context context, List<Bitmap> paths) {
            this.mContext = context;
            this.paths = paths;
        }
        Context context;
        public View getView(int position, View convertView, ViewGroup parent){

            final int npos = position;
            View v = convertView;
            if (v == null) {
                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = inflater.inflate(R.layout.emoticons_item, null);
            }
            context = v.getContext();
            final Bitmap path = paths.get(position);

            ImageView image = (ImageView) v.findViewById(R.id.item);
            image.setImageBitmap((path));

            //image.
            image.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {


                    Html.ImageGetter imageGetter = new Html.ImageGetter() {
                        public Drawable getDrawable(String source) {
                            // StringTokenizer st = new StringTokenizer(emoticonsPath.get(npos), ".");
                            //Drawable d = new BitmapDrawable(getResources(),emoticonsPath.get(npos));
                            Bitmap bm = getBitmapFromAsset(context, emoticonsPath.get(npos));
                            Drawable d = new BitmapDrawable(bm);
                            d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
                            return d;
                        }
                    };


                    Spanned cs = Html.fromHtml("<img width=\"15\" height=\"15\" src ='"+UserFunc.URL_Server+ emoticonsPath.get(npos) +"'/>", imageGetter, null);

                    int cursorPosition = message.getSelectionStart();
                    message.getText().insert(cursorPosition, cs);



                }
            });

            return v;
        }

        public  Bitmap getBitmapFromAsset(Context context, String filePath) {
            AssetManager assetManager = context.getAssets();

            InputStream istr;
            Bitmap bitmap = null;
            try {
                istr = assetManager.open(filePath);
                bitmap = BitmapFactory.decodeStream(istr);
            } catch (IOException e) {
                // handle exception
            }

            return bitmap;
        }
        @Override
        public int getCount() {
            return paths.size();
        }

        @Override
        public Bitmap getItem(int position) {
            return paths.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        private Bitmap getImage (String path) {
            AssetManager mngr = mContext.getAssets();
            InputStream in = null;

            try {
                in = mngr.open("emoticons/" + path);
            } catch (Exception e){
                e.printStackTrace();
            }

            //BitmapFactory.Options options = new BitmapFactory.Options();
            //options.inSampleSize = chunks;

            Bitmap temp = BitmapFactory.decodeStream(in ,null ,null);
            return temp;
        }

    }

}
