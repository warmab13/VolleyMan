package warmab.com.vollerymanager;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Warmab on 5/22/16.
 */
public class PostAdapter extends ArrayAdapter {

    private String URL_BASE = "http://servidorexterno.site90.com/datos";
    private static final String TAG = "PostAdapter";
    private static final String URL_JSON = "/social_media.json";
    List<Post> items;
    private RequestQueue requestQueue;
    JsonObjectRequest jsArrayRequest;

    public PostAdapter(Context context){
        super(context, 0);

        requestQueue = Volley.newRequestQueue(context);

        //Gestionar petición del archivo JSON

        // Nueva petición JSONObject
        jsArrayRequest = new JsonObjectRequest(Request.Method.GET, URL_BASE + URL_JSON,
                (String) null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        items = parseJson(response);
                        notifyDataSetChanged();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "Error Respuesta en JSON: " + error.getMessage());

                    }
                }
        );

        requestQueue.add(jsArrayRequest);


    }

    @Override
    public int getCount() {
        return items != null ? items.size() : 0;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());

        View listItemView;

        listItemView = null == convertView ? layoutInflater.inflate(R.layout.post, parent, false) : convertView;

        Post item = items.get(position);

        TextView textoTitulo = (TextView) listItemView.findViewById(R.id.textoTitulo);
        TextView textoDescripcion = (TextView) listItemView.findViewById(R.id.textoDescripcion);
        final ImageView imagenPost = (ImageView) listItemView.findViewById(R.id.imagenPost);

        textoTitulo.setText(item.getTitulo());
        textoDescripcion.setText(item.getDescripcion());

        ImageRequest request = new ImageRequest(
                URL_BASE + item.getImagen(),
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap response) {
                        imagenPost.setImageBitmap(response);
                    }
                }, 0, 0, null, null,
                new Response.ErrorListener() {
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "Error en respuesta Bitmap: " + error.getMessage());
                    }
                });
        requestQueue.add(request);
        return listItemView;

    }

    public List<Post> parseJson(JSONObject jsonObject){
        List<Post> posts = new ArrayList<>();
        JSONArray jsonArray = null;

        try {
            jsonArray = jsonObject.getJSONArray("items");

            for (int i = 0; i<jsonArray.length(); i++){
                try{
                    JSONObject object = jsonArray.getJSONObject(i);

                    Post post = new Post(
                            object.getString("titulo"),
                            object.getString("descripcion"),
                            object.getString("imagen")
                    );

                    posts.add(post);
                }catch (JSONException e){
                    Log.e(TAG, "Error de parsing: "+ e.getMessage());
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return posts;
    }

}

