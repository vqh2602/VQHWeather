package com.example.vqhcovid;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.vqhcovid.News.News;
import com.example.vqhcovid.News.NewsAdapter;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NewsActivity extends AppCompatActivity {
    ListView lvNews;
    NewsAdapter newsAdapter;
    ArrayList<News> lsNews;
    ArrayList<String> lsTitle, lsLink, lsImgLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        lvNews = findViewById(R.id.lvNews);
        lsNews = new ArrayList<News>();


        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new ReadData().execute("https://vtc.vn/rss/suc-khoe.rss");
            }
        });

        newsAdapter = new NewsAdapter(NewsActivity.this, android.R.layout.simple_list_item_1, lsNews);
        lvNews.setAdapter(newsAdapter);

        lvNews.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(NewsActivity.this, WebviewActivity.class);
                intent.putExtra("link", lsNews.get(position).link);
                startActivity(intent);

            }
        });
    }

    class ReadData extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {
            return readDataFromUrl(params[0]);
        }

        @Override
        protected void onPostExecute(String s) {
            XMLDOMParser parser = new XMLDOMParser();
            Document document = parser.getDocument(s);
            NodeList nodeList = document.getElementsByTagName("item");
            NodeList nodeListDescription = document.getElementsByTagName("description");
            String imageUrl = "";
            String title = "";
            String link = "";
            for (int i = 0; i < nodeList.getLength(); i++) {
                String cdata = nodeListDescription.item(i + 1).getTextContent();
//                Pattern p = Pattern.compile("<img[^>]+src\\s*=\\s*['\"]([^'\"]+)['\"][^>]*>");

                Pattern p = Pattern.compile("<[iI][mM][gG][^>]+[sS][rR][cC]\\s*=\\s*['\"]([^'\"]+)['\"][^>]*>");
                Matcher matcher = p.matcher(cdata);

                if (matcher.find()) {
                    imageUrl = matcher.group();
                    int start = imageUrl.indexOf("src=\"") + 5;
                    int end = imageUrl.indexOf("\"", start);
                    imageUrl = imageUrl.substring(start, end);
                }
                Element element = (Element) nodeList.item(i);
                title = parser.getValue(element, "title");
                link = parser.getValue(element, "link");

                lsNews.add(new News(title, link, imageUrl));
            }

            newsAdapter.notifyDataSetChanged();
            super.onPostExecute(s);
        }
    }

    private String readDataFromUrl(String theUrl) {
        StringBuilder content = new StringBuilder();

        try {
            // create url object
            URL url = new URL(theUrl);

            // create a url connection object
            URLConnection urlConnection = url.openConnection();

            // wrap the url connection in a buffered reader
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

            String line;

            // read from the url connection via the buffered reader
            while((line = bufferedReader.readLine()) != null) {
                content.append(line + "\n");
            }
            bufferedReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return content.toString();
    }
}