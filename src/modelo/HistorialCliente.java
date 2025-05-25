/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author magal
 */
@Entity
@Table(name = "historial_cliente")
@NamedQueries({
    @NamedQuery(name = "HistorialCliente.findAll", query = "SELECT h FROM HistorialCliente h"),
    @NamedQuery(name = "HistorialCliente.findByIdCliente", query = "SELECT h FROM HistorialCliente h WHERE h.idCliente = :idCliente"),
    @NamedQuery(name = "HistorialCliente.findByNombre", query = "SELECT h FROM HistorialCliente h WHERE h.nombre = :nombre"),
    @NamedQuery(name = "HistorialCliente.findByIdPedido", query = "SELECT h FROM HistorialCliente h WHERE h.idPedido = :idPedido"),
    @NamedQuery(name = "HistorialCliente.findByFechaPedido", query = "SELECT h FROM HistorialCliente h WHERE h.fechaPedido = :fechaPedido"),
    @NamedQuery(name = "HistorialCliente.findByIdDevolucion", query = "SELECT h FROM HistorialCliente h WHERE h.idDevolucion = :idDevolucion"),
    @NamedQuery(name = "HistorialCliente.findByFechaDevolucion", query = "SELECT h FROM HistorialCliente h WHERE h.fechaDevolucion = :fechaDevolucion"),
    @NamedQuery(name = "HistorialCliente.findByIdVale", query = "SELECT h FROM HistorialCliente h WHERE h.idVale = :idVale"),
    @NamedQuery(name = "HistorialCliente.findByIdTransaccion", query = "SELECT h FROM HistorialCliente h WHERE h.idTransaccion = :idTransaccion"),
    @NamedQuery(name = "HistorialCliente.findByTipoPago", query = "SELECT h FROM HistorialCliente h WHERE h.tipoPago = :tipoPago"),
    @NamedQuery(name = "HistorialCliente.findByMonto", query = "SELECT h FROM HistorialCliente h WHERE h.monto = :monto")})
public class HistorialCliente implements Serializable {

    private static final long serialVersionUID = 1L;
    @Column(name = "id_cliente")
    private Integer idCliente;
    @Column(name = "nombre")
    private String nombre;
    @Column(name = "id_pedido")
    private Integer idPedido;
    @Column(name = "fecha_pedido")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaPedido;
    @Column(name = "id_devolucion")
    private Integer idDevolucion;
    @Column(name = "fecha_devolucion")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaDevolucion;
    @Column(name = "id_vale")
    private Integer idVale;
    @Column(name = "id_transaccion")
    private Integer idTransaccion;
    @Column(name = "tipo_pago")
    private String tipoPago;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "monto")
    private BigDecimal monto;

    public HistorialCliente() {
    }

    public Integer getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(Integer idCliente) {
        this.idCliente = idCliente;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Integer getIdPedido() {
        return idPedido;
    }

    public void setIdPedido(Integer idPedido) {
        this.idPedido = idPedido;
    }

    public Date getFechaPedido() {
        return fechaPedido;
    }

    public void setFechaPedido(Date fechaPedido) {
        this.fechaPedido = fechaPedido;
    }

    public Integer getIdDevolucion() {
        return idDevolucion;
    }

    public void setIdDevolucion(Integer idDevolucion) {
        this.idDevolucion = idDevolucion;
    }

    public Date getFechaDevolucion() {
        return fechaDevolucion;
    }

    public void setFechaDevolucion(Date fechaDevolucion) {
        this.fechaDevolucion = fechaDevolucion;
    }

    public Integer getIdVale() {
        return idVale;
    }

    public void setIdVale(Integer idVale) {
        this.idVale = idVale;
    }

    public Integer getIdTransaccion() {
        return idTransaccion;
    }

    public void setIdTransaccion(Integer idTransaccion) {
        this.idTransaccion = idTransaccion;
    }

    public String getTipoPago() {
        return tipoPago;
    }

    public void setTipoPago(String tipoPago) {
        this.tipoPago = tipoPago;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }
    
}
