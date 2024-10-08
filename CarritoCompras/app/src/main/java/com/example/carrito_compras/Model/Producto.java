package com.example.carrito_compras.Model;

public class Producto {
    private String Uid, Producto, Precio, Categoria, Descripcion, ImagenUrl;

    public Producto(){
    }

    public String getUid() {
        return Uid;
    }
    public void setUid(String uid) {
        Uid = uid;
    }

    public String getProducto() {
        return Producto;
    }
    public void setProducto(String producto) {
        Producto = producto;
    }

    public String getPrecio() {
        return Precio;
    }
    public void setPrecio(String precio) {
        Precio = precio;
    }

    public String getCategoria() {
        return Categoria;
    }
    public void setCategoria(String categoria) {
        Categoria = categoria;
    }

    public String getDescripcion() {
        return Descripcion;
    }
    public void setDescripcion(String descripcion) {
        Descripcion = descripcion;
    }

    public String getImagenUrl() {
        return ImagenUrl;
    }
    public void setImagenUrl(String imagenUrl) {
        this.ImagenUrl = imagenUrl;
    }

    @Override
    public String toString() {
        return Producto+"\nCategoria: "+Categoria+"\nPrecio: "+Precio+"\nDescripcion: "+Descripcion;
    }
}
