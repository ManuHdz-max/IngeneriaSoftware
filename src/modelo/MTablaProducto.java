/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

import java.util.ArrayList;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author ericg
 */
public class MTablaProducto extends AbstractTableModel{
    
    private ArrayList<DatosTablasProductos> datosTablasProductos;
    public MTablaProducto(ArrayList mtp){
        datosTablasProductos = mtp;
    }
}
