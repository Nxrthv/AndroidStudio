package com.example.carrito_compras;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ArrayAdapter;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;

public class MainActivity extends AppCompatActivity {

    private ListView list, listCategory, listPrice;
    private final ArrayList<String> products = new ArrayList<>();
    private final ArrayList<String> category = new ArrayList<>();
    private final ArrayList<String> price = new ArrayList<>();

    private ArrayAdapter<String> arrayAdapter, categoryAdapter, priceAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, products);
        list = findViewById(R.id.list);
        list.setAdapter(arrayAdapter);

        categoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, category);
        listCategory = findViewById(R.id.listCategoria);
        listCategory.setAdapter(categoryAdapter);

        priceAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, price);
        listPrice = findViewById(R.id.listPrecio);
        listPrice.setAdapter(priceAdapter);

        EditText producto = findViewById(R.id.iptProducto);
        EditText precio = findViewById(R.id.iptPrecio);
        EditText categoria = findViewById(R.id.iptCategoria);
        EditText description = findViewById(R.id.iptDescripcion);

        Button guardar = findViewById(R.id.guardar);
        Button buscar = findViewById(R.id.buscar);

        buscar.setOnClickListener(v -> {
            Intent i = new Intent(MainActivity.this, GetMain.class);
            startActivity(i);
        });

        guardar.setOnClickListener(v ->{
            String txtProducto = producto.getText().toString();
            String txtPrecio = precio.getText().toString();
            String txtCategoria = categoria.getText().toString();
            String txtDescripcion = description.getText().toString();

            Post nuevoPost = new Post();
            nuevoPost.setProducto(txtProducto);
            nuevoPost.setPrecio(txtPrecio);
            nuevoPost.setCategoria(txtCategoria);
            nuevoPost.setDescripcion(txtDescripcion);

            products.add(nuevoPost.getProducto());

            arrayAdapter.notifyDataSetChanged();
        });

        getPosts();
    }

    public static class Post {
        private String Product, Price, Category, Description;

        public String getProducto() {
            return Product;
        }

        public void setProducto(String product) {
            this.Product = Product;
        }

        public String getPrecio() {
            return Price;
        }

        public void setPrecio(String precio) {
            this.Price = Price;
        }

        public String getCategoria() {
            return Category;
        }

        public void setCategoria(String categoria) {
            this.Category = Category;
        }

        public String getDescripcion() {
            return Description;
        }

        public void setDescripcion(String descripcion) {
            this.Description = Description;
        }
    }

    public interface PostService {
        String API_ROUTE = "/productos";

        @GET(API_ROUTE)
        Call<List<Post>> getPost();
    }

    private void getPosts() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:8000/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        PostService postService = retrofit.create(PostService.class);
        Call<List<Post>> call = postService.getPost();

        call.enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    for (Post post : response.body()) {
                        products.add(post.getProducto());
                        category.add(post.getCategoria());
                        price.add(post.getPrecio());
                    }
                    arrayAdapter.notifyDataSetChanged();
                    categoryAdapter.notifyDataSetChanged();
                    priceAdapter.notifyDataSetChanged();
                } else {
                    Log.e("API_ERROR", "Error en la respuesta: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Post>> call, Throwable t) {
                Log.e("API_FAILURE", "Error: " + t.getMessage());
            }
        });
    }
}