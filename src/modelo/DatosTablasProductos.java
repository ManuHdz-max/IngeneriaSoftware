/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

import java.math.BigDecimal;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Text;

/**
 *
 * @author ericg
 */
public class DatosTablasProductos {
    private int idProducto;
    private String nombre;
    private Text descripcion;
    private String talla;
    private String color;
    private BigDecimal precio;
    
    public DatosTablasProductos() throws ParserConfigurationException{
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.newDocument();
        descripcion = doc.createTextNode("");
        idProducto = -1;
        nombre = new String();
        talla = new String();
        color = new String();
        precio = new BigDecimal(-1);
    }

    public int getIdProducto() {
        return idProducto;
    }
    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }
    
    public String getNombre(){
        return nombre;
    }
    public void setNombre(String nm){
        this.nombre = nm;
    }
    
    public Text getDescripcion(){
        return descripcion;
    }
    public void setDescripcion(Text dp){
        this.descripcion = dp;
    }
    
    public String getTalla(){
        return talla;
    }
    public void setTalla(String tl){
        this.talla = tl;
    }

    public String getColor() {
        return color;
    }
    public void setColor(String color) {
        this.color = color;
    }

    public BigDecimal getPrecio() {
        return precio;
    }
    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }
}
