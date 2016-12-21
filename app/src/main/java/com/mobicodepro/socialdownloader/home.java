package com.mobicodepro.socialdownloader;

/**
 * Created by mac on 25/01/16.
 */

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import dialog.dialoginfo;
import func.reg;

import com.loopj.android.http.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;



public class home extends Fragment {

    private String html = "", desc = "", imagina = "", url = "" , video = "" , videoArray="";
    EditText textField;
    Button past , btnshow , btnsharethisapp, btnrateus, btnmoreapp;
    TextView textView;
    ProgressDialog prgDialog;
    ArrayList<String> jVideo , jQuality;
    // variable to track event time
    private long mLastClickTime = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle saveInstanceState){

        View rootView = inflater.inflate(R.layout.home, container, false);
        LinearLayout linearlayout = (LinearLayout)rootView.findViewById(R.id.unitads);
        config.admob.admobBannerCall(getActivity(), linearlayout);

        textField = (EditText)rootView.findViewById(R.id.webobo);
        past = (Button)rootView.findViewById(R.id.btndl);
        btnshow = (Button)rootView.findViewById(R.id.btnshow);
        textView = (TextView)rootView.findViewById(R.id.textView);
        btnsharethisapp =(Button)rootView.findViewById(R.id.sharethisapp);
        btnrateus =(Button)rootView.findViewById(R.id.rateus);
        btnmoreapp =(Button)rootView.findViewById(R.id.moreapp);


        prgDialog = new ProgressDialog(getActivity());
        prgDialog.setCancelable(false);


        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        final String packageName = getActivity().getPackageName();

        Intent intent = getActivity().getIntent();

        if(intent.hasExtra("url")){

            textField.setText(func.reg.getBack(intent.getStringExtra("url").toString(), "(http(s)?:\\/\\/(.+?\\.)?[^\\s\\.]+\\.[^\\s\\/]{1,9}(\\/[^\\s]+)?)"));

            showContent( textField);

            getActivity().getIntent().removeExtra("url");
        }

        textField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {

                if(!textView.getText().toString().isEmpty()){
                    showContent(textField);
                }
                return false;
            }
        });

        past.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub

                // Gets the ID of the "paste" menu item

                    final android.content.ClipboardManager clipboard = (android.content.ClipboardManager)
                            getActivity().getSystemService(Context.CLIPBOARD_SERVICE);

                    ClipData clip = clipboard.getPrimaryClip();

                    if(clip != null){

                        ClipData.Item item = clip.getItemAt(0);
                        textField.setText(func.reg.getBack(item.getText().toString(), "(http(s)?:\\/\\/(.+?\\.)?[^\\s\\.]+\\.[^\\s\\/]{1,9}(\\/[^\\s]+)?)"));

                    }else{

                        Toast.makeText(getActivity(), "Empty clipboard!", Toast.LENGTH_LONG).show();
                    }

                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {

                }else{

                    showContent( textField);

                }
                mLastClickTime = SystemClock.elapsedRealtime();

            }
        });

        btnshow.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub

                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {

                }else{

                    showContent( textField);

                }
                mLastClickTime = SystemClock.elapsedRealtime();
            }
        });

        btnsharethisapp.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                func.share.mShareText("Hey my friend check out this app\n https://play.google.com/store/apps/details?id="+ packageName +" \n", getActivity());

            }
        });

        btnrateus.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + packageName)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + packageName)));
                }

            }
        });

        btnmoreapp.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://search?q=pub:Stormania")));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/search?q=pub:Stormania")));
                }

            }
        });


        return rootView;

    }

    private void showContent(EditText textField){

        url = textField.getText().toString().replaceAll(" ","");

        if(reg.getBack(url, "(http(s)?:\\/\\/(.+?\\.)?[^\\s\\.]+\\.[^\\s\\/]{1,9}(\\/[^\\s]+)?)").isEmpty()){

            Toast.makeText(getActivity() , "Please Check Url. if correct!" , Toast.LENGTH_LONG).show();

        }else{

                video = "";
                imagina = "";
                desc = "";
                videoArray = "";

                if(url.contains("keek")){
                    keek();

                }else {

                    // correct url structure
                        if(url.contains("facebook")){

                            url = "https://m.facebook." + reg.getBack(url,"(((?!.com).)+$)");

                        }else if(url.contains("vimeo.com")){

                            url = reg.getBack(url, "(((?!/).)+)$");

                            if(url.contains("?")){

                                url = url.split("\\?")[0];
                            }

                            url = "https://player.vimeo.com/video/" + url + "?title=0&byline=0&portrait=0";

                        }else if(url.contains("mobile.twitter.com") || url.contains("m.twitter.com")){

                             url.replace("mobile.twitter.com" , "twitter.com").replace("m.twitter.com" , "twitter.com");
                        }


                    url = func.reg.getBack(url, "(http(s)?:\\/\\/(.+?\\.)?[^\\s\\.]+\\.[^\\s\\/]{1,9}(\\/[^\\s]+)?)");

                    AsyncHttpClient client = new AsyncHttpClient();
                        client.setUserAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/48.0.2564.109 Safari/537.36");
                        client.get(url, new TextHttpResponseHandler() {
                            @Override
                            public void onStart() {
                                // Initiated the request
                                prgDialog.setMessage("Loading...");
                                prgDialog.show();

                            }

                            @Override
                            public void onFinish() {
                                // Completed the request (either success or failure)
                                prgDialog.hide();
                            }

                            @Override
                            public void onFailure(int arg0, Header[] arg1, String arg2, Throwable arg3) {
                                // TODO Auto-generated method stub
                                prgDialog.hide();
                                Toast.makeText(getActivity(), "Conexion Faild!", Toast.LENGTH_LONG).show();

                            }

                            @Override
                            public void onSuccess(int statusCode, Header[] headers, String responseBody) {
                                // TODO Auto-generated method stub

                                html = responseBody;

                                int i = 0;
                                for(Header header : headers){

                                    Log.e("header : ", header.getValue());
                                }


                                if (url.contains("twitter.com")){

                                    twitter();

                                }else if(url.contains("vine.co")){

                                    vine();

                                }else if(url.contains("instagram.com")){

                                    instagram();

                                }else if(url.contains("facebook.com") || url.contains("fb.com")){

                                    facebook();

                                }else if(url.contains("vimeo.com")){

                                    vimeo();

                                }else if(url.contains("flickr.com")){

                                    flickr();

                                }else if(url.contains("pinterest.com") || url.contains("pin.it")){

                                    pinterest();

                                }else if(url.contains("dailymotion.com") || url.contains("dai.ly")){

                                    dailymotion();

                                }else{

                                    tumblr();

                                }

                            }
                        });

                }

            }
    }


    private void asynHttp(final String web){

        AsyncHttpClient client = new AsyncHttpClient();
        client.setUserAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/48.0.2564.109 Safari/537.36");
        client.get(web, new TextHttpResponseHandler() {
            @Override
            public void onStart() {
                // Initiated the request

                prgDialog.setMessage("getting video...");
                prgDialog.show();

            }

            @Override
            public void onFinish() {
                // Completed the request (either success or failure)
                prgDialog.hide();
            }

            @Override
            public void onFailure(int arg0, Header[] arg1, String arg2, Throwable arg3) {
                // TODO Auto-generated method stub
                prgDialog.hide();
                Toast.makeText(getActivity(), "Conexion Faild!", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onSuccess(int arg0, Header[] arg1, String responseBody) {
                // TODO Auto-generated method stub

                html = responseBody;


                if (web.contains("instagram.com")) {

                    instagram();

                } else if (web.contains("vine.co")) {

                    vine();

                }else if(web.contains("tumblr.com")){

                    String vPrepare = reg.getBack(html , "tumblr.com/video_file/.+?/tumblr_([^'\"/]+)");

                    video = "https://vt.tumblr.com/tumblr_" + vPrepare + ".mp4";

                    mDialog();

                }else if (web.contains("vimeo.com")){

                    vimeo();

                }else if(web.contains("dailymotion")){

                    dailymotion();

                }else if(web.contains("savedeo.com")){

                    video = reg.getBack(html , "href=\"(.+?\\.mp4)\"");

                    mDialog();

                } else {

                    String powerJS = reg.getBack(html , "data-config=\"(.+?)\"");

                    if (!func.json.jsonObject(powerJS , "video_url").isEmpty()){

                        asynHttp("https://savedeo.com/download?url="+url);

                    }else if(!func.json.jsonObject(powerJS , "vmap_url").isEmpty()){

                        try{

                            video = reg.getBack(func.httpRequest.get(func.json.jsonObject(powerJS , "vmap_url")), "<MediaFile>[^<]+<\\!\\[CDATA\\[([^\\]]+)");
                            mDialog();

                        }catch(Exception e){

                        }

                    }

                }

            }
        });

    }

    @Override
    public void onResume() {

        super.onResume();

        Intent a = getActivity().getIntent();

        if (a.hasExtra("url")) {

            textField.setText(func.reg.getBack(a.getStringExtra("url").toString(), "(http(s)?:\\/\\/(.+?\\.)?[^\\s\\.]+\\.[^\\s\\/]{1,9}(\\/[^\\s]+)?)"));
            a.removeExtra("url");
            showContent(textField);

        }

    }

    @Override
    public void onPause() {
        super.onPause();
    }

    public void keek(){

        String keek = reg.getBack(url, "keek.com/keek/([\\w\\W]+)");

        video = "https://keekugc.cachefly.net/keek/video/" + keek + ".mp4";
        imagina = "https://keekugc.cachefly.net/keek/thumbnail/"+ keek;

        desc = "Enjoy!";

        mDialog();

    }

    public void vine(){

        video = reg.getBack(html, "property=\"twitter:player:stream\" content=\"([^\"]+)\"");
        imagina = reg.getBack(html, "property=\"og:image\" content=\"([^\"]+)\"");
        desc = reg.getBack(html, "property=\"og:title\" content=\"([^\"]+)\"");

        mDialog();

    }
    public void twitter(){

        String check = reg.getBack(html, "property=\"og:type\" content=\"([^\"]+)\"");
        String vine = reg.getBack(html, "data-expanded-url=\".+?://vine.co/v/([^\"]+)\"");
        desc = reg.getBack(html, "property=\"og:description\" content=\"([^\"]+)\"");
        imagina = reg.getBack(html, "property=\"og:image\" content=\"([^\"]+)\"");

        if (check.equals("video")){

            asynHttp(reg.getBack(html, "property=\"og:video:url\" content=\"([^\"]+)\""));
            imagina = func.convertCharacters.xmlchars(reg.getBack(html, "property=\"og:image\" content=\"([^\"]+)\""));

        }else if(!vine.isEmpty()  && imagina.contains("profile_images")){

            asynHttp("https://vine.co/v/"+vine);

        }else{

            String imageId = reg.getBack(html, "(https://pbs.twimg.com/media/[^\"]+)");
            String gifId = reg.getBack(html, "(https://pbs.twimg.com/tweet_video_thumb/[^\"']+)");

            if(!gifId.isEmpty()){

                video = "https://pbs.twimg.com/tweet_video/" + reg.getBack(gifId , "tweet_video_thumb/(.+?)\\.") + ".mp4";
                imagina = gifId;

            }else if (!imageId.isEmpty()){

                imagina = imageId;

            }

            mDialog();
        }

    }

    public void tumblr(){

        imagina = reg.getBack(html, "property=\"og:image\" content=\"([^\"]+)\"");

        desc = reg.getBack(html, "property=\"og:description\" content=\"([^\"]+)\"");

        String check = reg.getBack(html, "property=\"og:type\" content=\"([^\"]+)\"");

        String vine = reg.getBack(html,"vine.co/v/([^\\s\\/]+)");

        String instagram = reg.getBack(html,"instagram.com/p/((.+?))/");

        String vimeo = reg.getBack(html , "player.vimeo.com/([^\"]+)");

        String zvideo = reg.getBack(html,"(https://www.tumblr.com/video/[^\"']+)");

        String dailymotion = reg.getBack(html , "(https://www.dailymotion.com/embed/video/[^\"']+)");


        if (check.contains("video")){

            if(imagina.contains("media.tumblr.com")){

                String mediaT = reg.getBack(html , "(https://www.tumblr.com/video/[^\"']+)");

                Log.e("mediaT",mediaT);

                asynHttp(mediaT);

            }else{

                if(!vine.isEmpty()){

                    asynHttp("https://vine.co/v/"+vine);

                }else if(!instagram.isEmpty()){

                    asynHttp("https://www.instagram.com/p/" + instagram + "/");

                }else if(!vimeo.isEmpty()){

                    asynHttp("https://player.vimeo.com/" + vimeo);

                }else if(!dailymotion.isEmpty()){

                    asynHttp(dailymotion);

                }else{

                    if(!imagina.contains("ytimg")){

                        mDialog();

                    }else {
                        desc = "Sorry. you can't download video from youtube due to private policy of google play development agrements";
                        mDialog();
                    }
                }

            }

        }else{

            if(!vine.isEmpty()){

                asynHttp("https://vine.co/v/"+vine);

            }else if(!instagram.isEmpty()){

                asynHttp("https://www.instagram.com/p/" + instagram + "/");

            }else if(!vimeo.isEmpty()){

                asynHttp("https://player.vimeo.com/" + vimeo);

            }else if(!dailymotion.isEmpty()){

                asynHttp(dailymotion);

            }else if(!zvideo.isEmpty()) {

                video = zvideo;

                asynHttp(video);

            }else{

                video = reg.getBack(html, "property=\"og:video\" content=\"([^\"]+)\"");

                if(video.isEmpty()){

                    video = reg.getBack(html, "property=\"twitter:player:stream\" content=\"([^\"]+)\"");
                }

                imagina = reg.getBack(html,"(http:\\/\\/.+?\\.media\\.tumblr\\.com\\/.+?\\/tumblr[^\"]+)");

                if(imagina.isEmpty()){

                    imagina = reg.getBack(html, "name=\"twitter:image:src\" content=\"([^\"]+)\"");
                }

                desc = reg.getBack(html, "property=\"\"og:description\" content=\"([^\"]+)\"");

                if(!imagina.isEmpty() || !video.isEmpty()){

                    mDialog();
                }

            }
        }

    }

    public void instagram(){

        video = reg.getBack(html, "property=\"og:video\" content=\"([^\"]+)\"");
        imagina = reg.getBack(html, "property=\"og:image\" content=\"([^\"]+)\"");
        desc = reg.getBack(html, "property=\"og:description\" content=\"([^\"]+)\"");

        mDialog();

    }
    public void pinterest(){

        imagina = reg.getBack(html, "name=\"twitter:image:src\" content=\"([^\"]+)\"");

        desc = reg.getBack(html, "name=\"og:title\" content=\"([^\"]+)\"");

        mDialog();

    }

    public void facebook(){

        String jsonVideo = escapeXml(reg.getBack(html, "\"([^\"]+)\" data-sigil=\"inlineVideo\""));

        String jsonImage = escapeXml(reg.getBack(html, "data-store=\"([^\"]+imgsrc[^\"]+)\""));

        String jGif = reg.getBack(html, "class=\"_4o54\".+?&amp;url=(.+?)&");

        try {

            if(!jsonVideo.isEmpty()){
                JSONObject obj = new JSONObject(jsonVideo);
                video = obj.getString("src");

            }else if(!jGif.isEmpty()){

                imagina = URLDecoder.decode(jGif, "UTF-8");

            }else if(!jsonImage.isEmpty()){

                JSONObject obj = new JSONObject(jsonImage);
                imagina = obj.getString("imgsrc");
            }

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        mDialog();

    }

    public void vimeo(){

        String sJs = reg.getBack(html, "function\\(e,a\\)\\{var t=(((?!;if).)+)");

        try{

            JSONObject j = new JSONObject(func.convertCharacters.xmlchars(sJs));

            int loop = j.getJSONObject("request").getJSONObject("files").getJSONArray("progressive").length();
            JSONArray objArr = j.getJSONObject("request").getJSONObject("files").getJSONArray("progressive");

            if(loop > 0){

                videoArray = objArr.toString();
            }

        }catch(Exception e){

        }

        mDialog();

    }

    public void flickr(){

        imagina = reg.getBack(html, "property=\"og:image\" content=\"([^\"]+)\"");

        desc = reg.getBack(html, "property=\"og:description\" content=\"([^\"]+)\"");

        mDialog();

    }

    void dailymotion(){

        imagina = reg.getBack(html, "property=\"og:image\" content=\"([^\"]+)\"");
        desc = reg.getBack(html, "property=\"og:description\" content=\"([^\"]+)\"");

        String sJs = reg.getBack(html , "\"qualities\":((.+?))\\}\\]\\}");

        if(!sJs.isEmpty()){sJs = sJs+"}]}";}

        try {

            JSONObject jobj = new JSONObject(sJs);
            videoArray = jobj.toString();

        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), "Nothing Found ...", Toast.LENGTH_SHORT).show();
        }

    }

    public String escapeXml(String s) {
        return s.replaceAll("&#123;","{").replaceAll("&#125;","}").replaceAll("&amp;" ,"&").replaceAll("&gt;",">").replaceAll("&lt;","<").replaceAll("&quot;","\"").replaceAll("&apos;","'");
    }

    public void mDialog(){

        if(!video.isEmpty() || !imagina.isEmpty() || !videoArray.isEmpty()){

            FragmentManager fm = getActivity().getSupportFragmentManager();
            dialoginfo info = new dialoginfo();
            Bundle args = new Bundle();
            args.putString("videoArray", videoArray);
            args.putString("video", video);
            args.putString("image", imagina);
            if(desc.length() > 300){desc=desc.substring(0,300);}
            args.putString("desc", desc);
            info.setArguments(args);
            info.show(fm, "fragment_info");

        }else{

            Toast.makeText(getContext(), "There is No results try again with new Link!", Toast.LENGTH_LONG).show();
        }
    }

}