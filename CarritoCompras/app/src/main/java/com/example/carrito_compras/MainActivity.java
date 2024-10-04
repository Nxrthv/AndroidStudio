package com.example.carrito_compras;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.carrito_compras.Model.Producto;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    EditText producto, precio, categoria, descripcion;

    ImageButton guardar, buscar, actualizar, eliminar, menunav;

    ImageView imagen;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    private FirebaseAnalytics mFirebaseAnalytics;

    private static final int IMAGE_REQUEST_CODE = 100;
    private Uri imageUri;
    private String downloadUrl;

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

        imagen = findViewById(R.id.image);

        producto = findViewById(R.id.iptProducto);
        precio = findViewById(R.id.iptPrecio);
        categoria = findViewById(R.id.iptCategoria);
        descripcion = findViewById(R.id.iptDescripcion);

        menunav = findViewById(R.id.menu);
        guardar = findViewById(R.id.save);
        buscar = findViewById(R.id.search);
        actualizar = findViewById(R.id.update);
        eliminar = findViewById(R.id.delete);

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

        imagen.setOnClickListener(v ->{
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, IMAGE_REQUEST_CODE);
        });

        guardar.setOnClickListener(v ->{

            String product = producto.getText().toString();
            String price = precio.getText().toString();
            String category = categoria.getText().toString();
            String description = descripcion.getText().toString();

            if(product.isEmpty() || price.isEmpty() ||  category.isEmpty()){
                validation();
            }else {
                Producto p = new Producto();
                p.setUid(UUID.randomUUID().toString());
                p.setProducto(product);
                p.setPrecio("S/. "+price);
                p.setCategoria(category);
                p.setDescripcion(description);

                if (imageUri != null) {
                    imgToFireBase(imageUri, p.getUid());
                } else{
                    saveProduct(p);
                }
            }
        });

        eliminar.setOnClickListener(v ->{
            String product = producto.getText().toString();

            if(product.isEmpty()){
                Toast.makeText(this, "Es necesario el Nombre del Producto", Toast.LENGTH_SHORT).show();
            } else {
                Query query = databaseReference.child("Producto").orderByChild("producto").equalTo(product);
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot productoSnapshot : dataSnapshot.getChildren()) {
                                productoSnapshot.getRef().removeValue()
                                        .addOnSuccessListener(aVoid -> {
                                            Toast.makeText(MainActivity.this, "Producto eliminado", Toast.LENGTH_SHORT).show();
                                            limpiarCampos();
                                        })
                                        .addOnFailureListener(e -> {
                                            Toast.makeText(MainActivity.this, "Error al eliminar el producto", Toast.LENGTH_SHORT).show();
                                        });
                            }
                        } else {
                            Toast.makeText(MainActivity.this, "Producto no encontrado", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(MainActivity.this, "Error en la consulta", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        actualizar.setOnClickListener(v ->{
            String product = producto.getText().toString();
            String act_price = precio.getText().toString();
            String act_category = categoria.getText().toString();
            String act_description = descripcion.getText().toString();

            if(product.isEmpty()){
                Toast.makeText(this, "El nombre del producto es obligatorio", Toast.LENGTH_SHORT).show();
                producto.setError("Required");
            } else {
                Query query = databaseReference.child("Producto").orderByChild("producto").equalTo(product);
                query.addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot productoSnapshot : dataSnapshot.getChildren()) {
                                Map<String, Object> updates = new HashMap<>();

                                if (!act_price.isEmpty()) {
                                    updates.put("precio", "S/. " + act_price);
                                }
                                if (!act_category.isEmpty()) {
                                    updates.put("categoria", act_category);
                                }
                                if (!act_description.isEmpty()) {
                                    updates.put("descripcion", act_description);
                                }
                                if (!updates.isEmpty()) {
                                    productoSnapshot.getRef().updateChildren(updates);
                                    Toast.makeText(MainActivity.this, "Producto actualizado", Toast.LENGTH_SHORT).show();
                                    limpiarCampos();
                                } else {
                                    Toast.makeText(MainActivity.this, "No hay campos nuevos para actualizar", Toast.LENGTH_SHORT).show();
                                }
                            }
                        } else {
                            Toast.makeText(MainActivity.this, "Producto no encontrado", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(MainActivity.this, "Error en la consulta", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        buscar.setOnClickListener(v ->{
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

    private void imgToFireBase(Uri imageUri, String productId) {
        if (imageUri != null) {
            String fileName = UUID.randomUUID().toString();
            StorageReference storageReference = FirebaseStorage.getInstance().getReference("images/" + fileName);

            storageReference.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                            downloadUrl = uri.toString();
                        });
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Error al cargar la imagen", Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private void saveProduct(Producto p) {
        databaseReference.child("Producto").child(p.getUid()).setValue(p)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Agregado", Toast.LENGTH_SHORT).show();
                        limpiarCampos();
                    } else {
                        Toast.makeText(this, "Error al agregar", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_REQUEST_CODE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            imagen.setImageURI(imageUri);
            String productId = UUID.randomUUID().toString();
            imgToFireBase(imageUri, productId);
        }
    }

    private void validation(){
        EditText producto, precio, categoria, descripcion;
        producto = findViewById(R.id.iptProducto);
        precio = findViewById(R.id.iptPrecio);
        categoria = findViewById(R.id.iptCategoria);
        descripcion = findViewById(R.id.iptDescripcion);

        String txtProducto, txtPrecio, txtCategoria, txtDescripcion;
        txtProducto = producto.getText().toString();
        txtPrecio = precio.getText().toString();
        txtCategoria = categoria.getText().toString();
        txtDescripcion = descripcion.getText().toString();

        if(txtProducto.isEmpty()){
            producto.setError("Required");
        } else if(txtPrecio.isEmpty()) {
            precio.setError("Required");
        }else if(txtCategoria.isEmpty()){
            categoria.setError("Required");
        }else if(txtDescripcion.isEmpty()){
            descripcion.setError("Required");
        }else {
            try {
                double precioDouble = Double.parseDouble(txtPrecio);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Por favor, ingresa un número válido en el campo de precio", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void limpiarCampos() {
        EditText producto = findViewById(R.id.iptProducto);
        EditText precio = findViewById(R.id.iptPrecio);
        EditText categoria = findViewById(R.id.iptCategoria);
        EditText descripcion= findViewById(R.id.iptDescripcion);
        ImageView imagen = findViewById(R.id.image);

        producto.setText("");
        precio.setText("");
        categoria.setText("");
        descripcion.setText("");
        //imagen.clear();
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