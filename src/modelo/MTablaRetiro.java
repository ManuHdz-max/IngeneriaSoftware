/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author Hp EliteBook
 */
public class MTablaRetiro extends AbstractTableModel{
    private final String[] columnas = {"Cajero", "Monto", "Fecha"};
    private final List<DatosTablaRetiro> retiros;

    public MTablaRetiro(List<DatosTablaRetiro> productos) {
        this.retiros = productos;
    }

    @Override
    public int getRowCount() {
        return retiros.size();
    }

    @Override
    public int getColumnCount() {
        return columnas.length;
    }

    @Override
    public String getColumnName(int column) {
        return columnas[column];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        DatosTablaRetiro retiro = retiros.get(rowIndex);
        return switch (columnIndex) {
            case 0 -> retiro.getNombreCajero();
            case 1 -> retiro.getMonto();
            case 2 -> retiro.getFecha();
            default -> null;
        };
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        // Por ejemplo: solo permitir editar cantidad y descuento
        return columnIndex == 1 || columnIndex == 2;
    }
    
    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        DatosTablaRetiro retiro = retiros.get(rowIndex);
        switch (columnIndex) {
            case 1 -> retiro.setNombreCajero(aValue.toString());
            case 2 -> retiro.setMonto(Integer.parseInt(aValue.toString()));
            case 3 -> {
                LocalDate fechaActual = LocalDate.now();
                Date fechaConvertida = Date.from(fechaActual.atStartOfDay(ZoneId.systemDefault()).toInstant());
                retiro.setFecha(fechaConvertida);
            }
        }
        fireTableCellUpdated(rowIndex, 4); // Actualiza subtotal
    }
}
