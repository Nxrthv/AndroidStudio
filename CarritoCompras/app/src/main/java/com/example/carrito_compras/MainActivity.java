package com.example.carrito_compras;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.carrito_compras.Model.Producto;
import com.google.android.material.animation.Positioning;
import com.google.firebase.FirebaseApp;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public class MainActivity extends AppCompatActivity {

    EditText producto, precio, categoria;

    ImageButton guardar, buscar, actualizar, eliminar, menunav;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    private FirebaseAnalytics mFirebaseAnalytics;

    private ListView listProduct, listCategory, listPrice;

    private final ArrayList<?> product = new ArrayList<>();
    private final ArrayList<String> category = new ArrayList<>();
    private final ArrayList<String> price = new ArrayList<>();

    private ArrayAdapter<?> productAdapter, categoryAdapter, priceAdapter;

    private PostService postService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.224.36:8000/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        //categoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, category);
        //listCategory = findViewById(R.id.listCategoria);
        //listCategory.setAdapter(categoryAdapter);

        //priceAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, price);
        //listPrice = findViewById(R.id.listPrecio);
        //listPrice.setAdapter(priceAdapter);

        producto = findViewById(R.id.iptProducto);
        precio = findViewById(R.id.iptPrecio);
        categoria = findViewById(R.id.iptCategoria);

        menunav = findViewById(R.id.menu);
        guardar = findViewById(R.id.save);
        buscar = findViewById(R.id.search);
        actualizar = findViewById(R.id.update);

        listProduct = findViewById(R.id.list);
        productAdapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, product);
        listProduct.setAdapter(productAdapter);

        /**listProduct.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
                Producto selectedProduct;
                selectedProduct = productAdapter.getItem(position);

                Intent intent = new Intent(MainActivity.this, GetMain.class);

                intent.putExtra("producto", selectedProduct.getProducto());
                intent.putExtra("precio", selectedProduct.getPrecio());
                intent.putExtra("categoria", selectedProduct.getCategoria());
                intent.putExtra("descripcion", selectedProduct.getDescripcion());

                startActivity(intent);
            }
        });**/

        initDataBase();
        getData();

        menunav.setOnClickListener(v ->{
            Intent intent = new Intent(MainActivity.this, ActivityMenu.class);
            startActivity(intent);
        });

        guardar.setOnClickListener(v ->{

            String product = producto.getText().toString();
            String price = precio.getText().toString();
            String category = categoria.getText().toString();

            if(product.isEmpty()||price.isEmpty()||category.isEmpty()){
                validation();
            }else {
                Producto p = new Producto();
                p.setUid(UUID.randomUUID().toString());
                p.setProducto(product);
                p.setPrecio("S/. "+price);
                p.setCategoria(category);
                p.setDescripcion("Ingresar en la Web");
                databaseReference.child("Producto").child(p.getUid()).setValue(p);
                Toast.makeText(this, "Agregado", Toast.LENGTH_SHORT).show();
                limpiarCampos();
            }
            /*Post nuevoPost = new Post();
            nuevoPost.setProducto(txtProducto);
            nuevoPost.setPrecio(txtPrecio);
            nuevoPost.setCategoria(txtCategoria);
            nuevoPost.setDescripcion("Ingresar desde Web");

            postService = retrofit.create(PostService.class);
            Call<Post> call = postService.insertPost(nuevoPost);

            call.enqueue(new Callback<Post>() {
                @Override
                public void onResponse(Call<Post> call, Response<Post> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Log.d("DEBUG", "Datos recibidos: " + response.body());

                        product.add(nuevoPost.getProducto());
                        category.add(nuevoPost.getCategoria());
                        price.add("S/ "+nuevoPost.getPrecio());

                        arrayAdapter.notifyDataSetChanged();
                        categoryAdapter.notifyDataSetChanged();
                        priceAdapter.notifyDataSetChanged();

                        limpiarCampos();

                        Toast.makeText(MainActivity.this, "Producto guardado con éxito", Toast.LENGTH_SHORT).show();
                    } else if (response.code() == 400) {
                        Toast.makeText(MainActivity.this, "Error en la validación: " + response.message(), Toast.LENGTH_SHORT).show();
                    } else {
                        try {
                            Log.e("API_ERROR", response.errorBody().string());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(Call<Post> call, Throwable t) {
                    Toast.makeText(MainActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });**/
        });

        actualizar.setOnClickListener(v ->{
            obtenerDatos();
        });

        buscar.setOnClickListener(v -> {
            PostService postService = retrofit.create(PostService.class);
            Call<ApiResponse> call = postService.getProduct(producto.getText().toString());

            call.enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {

                        Post producto = response.body().getMessage();
                        EditText editTextPrecio= findViewById(R.id.iptPrecio);
                        EditText editTextCategoria = findViewById(R.id.iptCategoria);

                        editTextPrecio.setText(producto.getPrecio());
                        editTextCategoria.setText(producto.getCategoria());

                    } else {
                        Log.e("API_ERROR", "Producto no encontrado o error en la respuesta: " + response.code());
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse> call, Throwable t) {
                    Log.e("API_FAILURE", "Error: " + t.getMessage());
                }
            });
        });
    }

    private void validation(){
        EditText producto, precio, categoria;
        producto = findViewById(R.id.iptProducto);
        precio = findViewById(R.id.iptPrecio);
        categoria = findViewById(R.id.iptCategoria);

        String txtProducto, txtPrecio, txtCategoria;
        txtProducto = producto.getText().toString();
        txtPrecio = precio.getText().toString();
        txtCategoria = categoria.getText().toString();

        if(txtProducto.isEmpty()){
            producto.setError("Required");
        } else if(txtPrecio.isEmpty()) {
            precio.setError("Required");
        }else if(txtCategoria.isEmpty()){
            categoria.setError("Required");
        }
    }

    private void limpiarCampos() {
        EditText producto = findViewById(R.id.iptProducto);
        EditText precio = findViewById(R.id.iptPrecio);
        EditText categoria = findViewById(R.id.iptCategoria);

        producto.setText("");
        precio.setText("");
        categoria.setText("");
    }

    private void getData(){
        databaseReference.child("Producto").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listProduct = findViewById(R.id.list);
                ArrayList<Producto> productList = new ArrayList<>();

                for (DataSnapshot objSnaptshot : dataSnapshot.getChildren()){
                    Producto p = objSnaptshot.getValue(Producto.class);
                    if (p != null){
                        productList.add(p);
                    }
                }
                ArrayAdapter<Producto> productAdapter = new ArrayAdapter<> (MainActivity.this, android.R.layout.simple_list_item_1, productList);
                listProduct.setAdapter(productAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("getData", "Error al leer los datos", error.toException());
            }
        });
    }

    private void initDataBase(){
        FirebaseApp.initializeApp(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }

    public class ApiResponse {
        private Post message;
        private int status;

        public Post getMessage() {
            return message;
        }

        public void setMessage(Post message) {
            this.message = message;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }
    }

    public static class Post {

        private String Product, Price, Category, Description;

        public String getProducto() {
            return Product;
        }

        public void setProducto(String product) {
            this.Product = product;
        }

        public String getPrecio() {
            return Price;
        }

        public void setPrecio(String precio) {
            this.Price = precio;
        }

        public String getCategoria() {
            return Category;
        }

        public void setCategoria(String categoria) {
            this.Category = categoria;
        }

        public String getDescripcion() {
            return Description;
        }

        public void setDescripcion(String descripcion) {
            this.Description = descripcion;
        }

        @Override
        public String toString() {
            return "Producto: " + Product + ", Categoria: " + Category + ", Precio: " + Price;
        }
    }

    private void obtenerDatos() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.224.36:8000/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        PostService postService = retrofit.create(PostService.class);
        Call<List<Post>> call = postService.getPost();

        call.enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    product.clear();
                    category.clear();
                    price.clear();
                    for (Post post : response.body()) {
                        //product.add(post.getProducto());
                        category.add(post.getCategoria());
                        price.add(post.getPrecio());
                    }
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

    public interface PostService {
        String API_ROUTE = "/api/productos";

        @GET(API_ROUTE)
        Call<List<Post>> getPost();

        @GET("productos/{name}")
        Call<ApiResponse> getProduct(@Path("name") String name);

        @POST(API_ROUTE)
        Call<Post> insertPost(@Body Post nuevoPost);
    }
}