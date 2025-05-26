/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JDialog.java to edit this template
 */
package vista;

import control.ClienteJpaController;
import control.DetallePedidoJpaController;
import control.InventarioJpaController;
import control.PedidoJpaController;
import control.ProductoJpaController;
import control.exceptions.NonexistentEntityException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import modelo.Pedido;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.management.loading.MLet;
import javax.swing.SpinnerNumberModel;
import modelo.Cliente;
import modelo.DetallePedido;
import modelo.Inventario;
import modelo.Producto;

/**
 *
 * @author magal
 */
public class InterfazPed extends javax.swing.JDialog {

    /**
     * Creates new form InterfazPed
     */
    private EntityManagerFactory emf;
    private PedidoJpaController cPedido;
    private DetallePedidoJpaController cDetalleP;
    private ClienteJpaController cCliente;
    private ProductoJpaController cProducto;
    private List<DetallePedido> detalles;
    private List<Pedido> pedidos;
    private Pedido pbusqueda;
    private boolean banderaEncontrado = false;
    private String tiposProducto[] = {"Selecicona tipo del produto", "Calzado", "Prenda", "Accesorio"};
    private int tipSelec = 0;
    private DefaultTableModel mt;
    private String nl = System.lineSeparator();
    private Date fechaEla = new Date();
    private Date fechaEntre = new Date();
    private SimpleDateFormat formato = new SimpleDateFormat("dd 'de' MMM 'de' yyy ");
    private boolean confirmaFecha=false;
    //combos cargar registrar
    private List<Cliente> clientes;
    private List<Producto> productos;
    private int selecPro = 0;
    private SpinnerNumberModel Msp = new SpinnerNumberModel(1, 1, 1, 1);
    //generar total
    private float acumulaTotal = 0;
    private Pedido NuevoP = null;
    //cantidad de inventario
    private InventarioJpaController cInventario;
    private List<Inventario> inventario;
    //guardar detalles externos
    // private Map<Pedido, String> descripciones = new HashMap<>();
    //valores para calcular precios
    private BigDecimal costoProductos = BigDecimal.ZERO;
    private BigDecimal totalMonto = BigDecimal.ZERO;

    public InterfazPed(java.awt.Frame parent, boolean modal) throws Exception {
        super(parent, modal);
        initComponents();
        emf = Persistence.createEntityManagerFactory("MiChingonPU");
        EntityManagerFactory emf2 = Persistence.createEntityManagerFactory("MiChingonPU");
        cPedido = new PedidoJpaController(emf);
        cDetalleP = new DetallePedidoJpaController(emf);
        cCliente = new ClienteJpaController(emf);
        cProducto = new ProductoJpaController(emf);
        cInventario = new InventarioJpaController(emf);

        clientes = cCliente.findClienteEntities();
        productos = cProducto.findProductoEntities();
        detalles = cDetalleP.findDetallePedidoEntities();
        inventario = cInventario.findInventarioEntities();
        mt = new DefaultTableModel(new Object[]{"Id Pedido", "Cliente", "Fecha de entrega", "Productos", "Estado", "Total"}, 0);

        pedidos = cPedido.findPedidoEntities();
        cantidadSP.setModel(Msp);
        cargarPedidos();
        calendarioSelec.setEnabled(false);
        Bconfi.setEnabled(false);
        impL.setText("");
        Bcancelar.setEnabled(false);
        cargarCB();

    }

    public void cargarPedidos() throws Exception {

        int numfilas = mt.getRowCount();
        for (int i = numfilas - 1; i >= 0; i--) {
            mt.removeRow(i);
        }
        if (banderaEncontrado) {
            Object[] fila = {
                pbusqueda.getIdPedido(),
                pbusqueda.getIdCliente().getNombre() + " " + pbusqueda.getIdCliente().getApellido(),
                formato.format(pbusqueda.getFechaEntregaEstimada()), pbusqueda.getEstado(),
                buscaProductos(pbusqueda),//productos
                pbusqueda.getTotal()
            };
            mt.addRow(fila);
            tPedidos.setModel(mt);
            String text = "Id.Pedido:" + pbusqueda.getIdPedido() + nl + " Cliente:" + pbusqueda.getIdCliente().getNombre() + " " + pbusqueda.getIdCliente().getApellido() + nl
                    + " Fecha elaboracion: " + pbusqueda.getFechaPedido()
                    + " Fecha estimada de entrega: " + formato.format(pbusqueda.getFechaEntregaEstimada()) + nl + "Estado: " + pbusqueda.getEstado()
                    + nl + buscaProductos(pbusqueda) + nl + " TOTAL: " + pbusqueda.getTotal();
            detallesText.setText(text);
            banderaEncontrado = false;
        } else {
            for (Pedido p : pedidos) {
                Object[] fila = {
                    p.getIdPedido(),
                    p.getIdCliente().getNombre() + " " + p.getIdCliente().getApellido(),
                    convertirFecha(p.getFechaEntregaEstimada()), buscaProductosMuestra(p), p.getEstado(),
                    p.getTotal()
                };
                mt.addRow(fila);

            }
        }
        tPedidos.setModel(mt);

    }

    private void cargarCB() {
        clientes = cCliente.findClienteEntities();
        productos = cProducto.findProductoEntities();

        colorCB.removeAllItems();
        clienteCB.removeAllItems();
        productoCB.removeAllItems();
        tallaCB.removeAllItems();

        clienteCB.addItem("Selecciona cliente");
        productoCB.addItem("Selecciona producto");
        colorCB.addItem("Selecciona color");
        tallaCB.addItem("Selecciona talla");

        for (Cliente c : clientes) {
            clienteCB.addItem(c.getNombre() + " " + c.getApellido());
        }
        for (Producto p : productos) {
            productoCB.addItem("id: " + p.getIdProducto() + " -- " + p.getNombre());

        }
        cantidadSP.setValue(0);

    }

    private String convertirFecha(Date fecha) {
        if (fecha == null) {
            return "No se registró fecha";
        }

        // Convertir Date a LocalDateTime
        LocalDateTime fechaHora = fecha.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, dd 'de' MMMM yyyy - HH:mm:ss");
        return fechaHora.format(formatter);
    }

    private String buscaProductos(Pedido p) throws Exception {
        String listaP = "";
        float precioP = 0;
        float totalPedido = 0;
        BigDecimal totalTem;
        BigDecimal precioUnitario;
        detalles = cDetalleP.findDetallePedidoEntities();
        for (DetallePedido de : detalles) {
            if (de.getIdPedido().getIdPedido() == p.getIdPedido()) {
                //calcular precio por cantidad de productos
                precioUnitario = new BigDecimal(de.getCantidad());
                totalTem = precioUnitario.multiply(de.getIdProducto().getPrecio());
                precioP = totalTem.floatValue();
                totalPedido += precioP;
                listaP += "Producto: " + de.getIdProducto().getNombre() + " Cantidad: " + de.getCantidad() + " Precio Individual: " + de.getIdProducto().getPrecio() + nl
                        + " Subtotal:" + precioP + nl;
            }
        }
        //guardar total en pedido

        return listaP;

    }

    private Inventario buscaInventario(Producto p) {
        for (Inventario in : inventario) {
            System.out.println("ID INVENTARIO" + in.getIdInventario());
            if (in.getIdProducto().getIdProducto() == p.getIdProducto()) {
                return in;
            }
        }
        return null;
    }

    private String buscaProductosMuestra(Pedido p) throws Exception {
        String listaP = "";
        float precioP = 0;
        float totalPedido = 0;
        BigDecimal totalTem;
        BigDecimal precioUnitario;
        detalles = cDetalleP.findDetallePedidoEntities();
        for (DetallePedido de : detalles) {
            if (de.getIdPedido().getIdPedido() == p.getIdPedido()) {
                //calcular precio por cantidad de productos
                precioUnitario = new BigDecimal(de.getCantidad());
                totalTem = precioUnitario.multiply(de.getIdProducto().getPrecio());
                precioP = totalTem.floatValue();
                totalPedido += precioP;
                listaP += "Producto: " + de.getIdProducto().getNombre();
            }
        }
        //guardar total en pedido
       

        return listaP;
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jCalendar1 = new com.toedter.calendar.JCalendar();
        jLabel1 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel3 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        txtResult = new javax.swing.JLabel();
        TextIdP = new javax.swing.JTextField();
        bBuscar = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tPedidos = new javax.swing.JTable();
        jLabel9 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        detallesText = new javax.swing.JTextArea();
        jLabel11 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        impL = new javax.swing.JLabel();
        Bcancelar = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jLabel10 = new javax.swing.JLabel();
        jButton5 = new javax.swing.JButton();
        calendarioSelec = new com.toedter.calendar.JCalendar();
        Bconfi = new javax.swing.JButton();
        textFecha = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        colorCB = new javax.swing.JComboBox<>();
        clienteCB = new javax.swing.JComboBox<>();
        productoCB = new javax.swing.JComboBox<>();
        tallaCB = new javax.swing.JComboBox<>();
        jLabel7 = new javax.swing.JLabel();
        cantidadSP = new javax.swing.JSpinner();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jLabel1.setFont(new java.awt.Font("Segoe UI", 2, 18)); // NOI18N
        jLabel1.setText("Pedido");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        jLabel2.setFont(new java.awt.Font("Segoe UI", 2, 14)); // NOI18N
        jLabel2.setText("Codigo del pedido:");

        txtResult.setFont(new java.awt.Font("Segoe UI", 2, 14)); // NOI18N
        txtResult.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        txtResult.setText("Resultados");

        bBuscar.setFont(new java.awt.Font("Segoe UI", 2, 14)); // NOI18N
        bBuscar.setText("Buscar");
        bBuscar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                bBuscarMouseClicked(evt);
            }
        });
        bBuscar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bBuscarActionPerformed(evt);
                bBuscarActionPerformed1(evt);
            }
        });

        tPedidos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tPedidos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tPedidosMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tPedidos);

        jLabel9.setFont(new java.awt.Font("Segoe UI", 2, 14)); // NOI18N
        jLabel9.setText("Busca el codigo del producto para ver detalles");

        detallesText.setColumns(20);
        detallesText.setRows(5);
        jScrollPane2.setViewportView(detallesText);

        jLabel11.setFont(new java.awt.Font("Segoe UI", 2, 14)); // NOI18N
        jLabel11.setText("Detalles del pedido");

        jButton1.setFont(new java.awt.Font("Segoe UI", 3, 14)); // NOI18N
        jButton1.setText("Imprimir");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        impL.setText("Imprimiendo....");

        Bcancelar.setFont(new java.awt.Font("Segoe UI", 2, 14)); // NOI18N
        Bcancelar.setText("Cancelar pedido ");
        Bcancelar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BcancelarActionPerformed(evt);
            }
        });

        jButton4.setFont(new java.awt.Font("Segoe UI", 2, 14)); // NOI18N
        jButton4.setText("Actualizar tabla");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 302, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(TextIdP, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(81, 81, 81)
                        .addComponent(bBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(Bcancelar)
                        .addGap(24, 24, 24))))
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(93, 93, 93)
                        .addComponent(txtResult, javax.swing.GroupLayout.PREFERRED_SIZE, 527, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 798, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 164, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 555, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(43, 43, 43)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(impL, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 166, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(jPanel3Layout.createSequentialGroup()
                                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(21, 21, 21)))))))
                .addContainerGap(14, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(TextIdP, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(bBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Bcancelar, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(txtResult)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 246, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 14, Short.MAX_VALUE)
                .addComponent(jLabel11)
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(impL))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jTabbedPane1.addTab("Busqueda", jPanel3);

        jLabel4.setFont(new java.awt.Font("Segoe UI", 3, 14)); // NOI18N
        jLabel4.setText("Registrar pedido");

        jLabel5.setFont(new java.awt.Font("Segoe UI", 2, 14)); // NOI18N
        jLabel5.setText("Cliente:");

        jLabel6.setFont(new java.awt.Font("Segoe UI", 2, 14)); // NOI18N
        jLabel6.setText("Producto:");

        jLabel8.setFont(new java.awt.Font("Segoe UI", 2, 14)); // NOI18N
        jLabel8.setText("Talla:");

        jButton2.setFont(new java.awt.Font("Segoe UI", 2, 14)); // NOI18N
        jButton2.setText("Agregar a pedido");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton3.setFont(new java.awt.Font("Segoe UI", 2, 14)); // NOI18N
        jButton3.setText("Finalizar pedido");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jLabel10.setFont(new java.awt.Font("Segoe UI", 2, 14)); // NOI18N
        jLabel10.setText("Fecha de entrega:");

        jButton5.setFont(new java.awt.Font("Segoe UI", 2, 14)); // NOI18N
        jButton5.setText("Seleccionar");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        calendarioSelec.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                calendarioSelecMouseClicked(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                calendarioSelecMousePressed(evt);
            }
        });

        Bconfi.setFont(new java.awt.Font("Segoe UI", 2, 14)); // NOI18N
        Bconfi.setText("Confirmar");
        Bconfi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BconfiActionPerformed(evt);
            }
        });

        textFecha.setText("Fecha:");

        jLabel3.setFont(new java.awt.Font("Segoe UI", 2, 14)); // NOI18N
        jLabel3.setText("Color:");

        colorCB.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        clienteCB.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        clienteCB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clienteCBActionPerformed(evt);
            }
        });

        productoCB.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        productoCB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                productoCBActionPerformed(evt);
            }
        });

        tallaCB.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel7.setFont(new java.awt.Font("Segoe UI", 2, 14)); // NOI18N
        jLabel7.setText("Cantidad: ");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 32, Short.MAX_VALUE)
                        .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(43, 43, 43)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(textFecha, javax.swing.GroupLayout.PREFERRED_SIZE, 276, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(49, 49, 49)
                                .addComponent(Bconfi, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(calendarioSelec, javax.swing.GroupLayout.PREFERRED_SIZE, 418, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addGap(0, 64, Short.MAX_VALUE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 157, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(62, 62, 62)
                                .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 171, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(131, 131, 131)
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(tallaCB, javax.swing.GroupLayout.PREFERRED_SIZE, 156, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(clienteCB, javax.swing.GroupLayout.PREFERRED_SIZE, 301, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(productoCB, javax.swing.GroupLayout.PREFERRED_SIZE, 202, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(248, 248, 248)
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel4Layout.createSequentialGroup()
                                        .addComponent(jLabel7)
                                        .addGap(18, 18, 18)
                                        .addComponent(cantidadSP, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel4Layout.createSequentialGroup()
                                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(29, 29, 29)
                                        .addComponent(colorCB, javax.swing.GroupLayout.PREFERRED_SIZE, 156, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                        .addGap(0, 0, Short.MAX_VALUE))))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 15, Short.MAX_VALUE)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(clienteCB, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(32, 32, 32)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(jLabel6)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(colorCB, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(5, 5, 5)
                        .addComponent(productoCB, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tallaCB, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel8)
                            .addComponent(jLabel7)
                            .addComponent(cantidadSP, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 30, Short.MAX_VALUE)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(textFecha)
                            .addComponent(Bconfi, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(calendarioSelec, javax.swing.GroupLayout.PREFERRED_SIZE, 188, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(151, 151, 151)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel10)
                            .addComponent(jButton5))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 13, Short.MAX_VALUE)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(16, 16, 16))
        );

        jTabbedPane1.addTab("Registro", jPanel4);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 818, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel1)
                .addGap(381, 381, 381))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(501, 501, 501)
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(22, 22, 22)
                        .addComponent(jLabel1)
                        .addGap(18, 18, 18)
                        .addComponent(jTabbedPane1)))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void bBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bBuscarActionPerformed
        // TODO add your handling code here:
        detallesText.setText("");
        String idbusqueda = TextIdP.getText();
        if (!idbusqueda.isEmpty()) {
            pedidos = cPedido.findPedidoEntities();
            for (Pedido p : pedidos) {
                if (p.getIdPedido().toString().equals(idbusqueda)) {
                    pbusqueda = p;
                    banderaEncontrado = true;
                    txtResult.setText("Resultados: Pedido encontrado");
                    break;
                } else {
                    banderaEncontrado = false;
                }

            }
            if (!banderaEncontrado) {
                txtResult.setText(" Resultados: No se encontraron resultados");
            }
            try {
                cargarPedidos();
            } catch (Exception ex) {
                Logger.getLogger(InterfazPed.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            txtResult.setText(" Resultados: No se encontraron resultados");
            JOptionPane.showMessageDialog(
                    null,
                    "Ingresa un identificador",
                    "Advertencia",
                    JOptionPane.WARNING_MESSAGE
            );
            try {
                cargarPedidos();
            } catch (Exception ex) {
                Logger.getLogger(InterfazPed.class.getName()).log(Level.SEVERE, null, ex);
            }
        }


    }//GEN-LAST:event_bBuscarActionPerformed

    private void bBuscarMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_bBuscarMouseClicked
        // TODO add your handling code here:

    }//GEN-LAST:event_bBuscarMouseClicked

    private void bBuscarActionPerformed1(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bBuscarActionPerformed1
        // TODO add your handling code here:

    }//GEN-LAST:event_bBuscarActionPerformed1

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        try {
            // TODO add your handling code here:
            registrarPedido();
        } catch (Exception ex) {
            Logger.getLogger(InterfazPed.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:

        if (!detallesText.getText().isEmpty())
            impL.setText("Imprimiendo...");
        else
            impL.setText("");
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        // TODO add your handling code here:
        calendarioSelec.setEnabled(true);
        calendarioSelec.setMinSelectableDate(new Date());
        Bconfi.setEnabled(true);
    }//GEN-LAST:event_jButton5ActionPerformed

    private void BconfiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BconfiActionPerformed
        // TODO add your handling code here:
        guardarFecha();
        confirmaFecha=true;
    }//GEN-LAST:event_BconfiActionPerformed

    private void calendarioSelecMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_calendarioSelecMouseClicked
        // TODO add your handling code here:
        textFecha.setText("Fecha: " + calendarioSelec.getDate());
    }//GEN-LAST:event_calendarioSelecMouseClicked

    private void calendarioSelecMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_calendarioSelecMousePressed
        // TODO add your handling code here:
        textFecha.setText("Fecha: " + calendarioSelec.getDate());
    }//GEN-LAST:event_calendarioSelecMousePressed

    private void clienteCBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clienteCBActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_clienteCBActionPerformed

    private void productoCBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_productoCBActionPerformed
        // TODO add your handling code here:
        //cambiar talla, tipo y color segun producto
        selecPro = productoCB.getSelectedIndex();
        if (selecPro > 0) {
            colorCB.removeAllItems();
            tallaCB.removeAllItems();
            System.out.println("seleccion de producto" + selecPro);
            Producto seP = productos.get(selecPro - 1);
            String[] colores = seP.getColor().split(",");

            for (int i = 0; i < colores.length; i++) {
                colorCB.addItem(colores[i]);
            }
            String tallas[] = seP.getTalla().split(",");

            for (int i = 0; i < tallas.length; i++) {
                tallaCB.addItem(tallas[i]);
            }
            Inventario inven = buscaInventario(seP);
            if (inven != null && inven.getCantidadActual() > 0) {
                Msp.setMinimum(1);
                Msp.setValue(1);
                Msp.setMaximum(inven.getCantidadActual());
                cantidadSP.setModel(Msp);
            } else {//no esta registrado en inventario
                Msp.setValue(0);
                cantidadSP.setModel(Msp);
            }
        } else {
            colorCB.removeAllItems();
            colorCB.addItem("Selecciona color");
        }
    }//GEN-LAST:event_productoCBActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // TODO add your handling code here:

        if (NuevoP != null) {
            JOptionPane.showMessageDialog(
                    null,
                    "Pedido" + NuevoP.getIdPedido() + " finalizado Monto Total:$" + totalMonto.toString(),
                    "Informacion",
                    JOptionPane.WARNING_MESSAGE
            );
            //guardarTotal

            try {
                NuevoP.setTotal(totalMonto);
                cPedido.edit(NuevoP);
                System.out.println(totalMonto + "Total registrado de pedido" + NuevoP.getIdPedido());
            } catch (Exception ex) {
                Logger.getLogger(InterfazPed.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        //preparar para nuevo pedido
        NuevoP = null;
        totalMonto = BigDecimal.ZERO;
        costoProductos = BigDecimal.ZERO;
        cargarCB();
    }//GEN-LAST:event_jButton3ActionPerformed

    private void BcancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BcancelarActionPerformed
        // TODO add your handling code here:
        //cancelacion
        int filaS = tPedidos.getSelectedRow();

        if (filaS >= 0) {
            int idPed = Integer.parseInt(mt.getValueAt(filaS, 0).toString());
            Pedido ped = buscaPedidoTabla(idPed);
            int resp = JOptionPane.showConfirmDialog(
                    null,
                    "¿Cancelar el pedido?"+ped.getIdPedido(),
                    "Aviso",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE
            );
            if (resp == JOptionPane.YES_OPTION) {

                try {
                    ped.setEstado("cancelado");
                    cPedido.edit(ped);
                    System.out.println("cancelado pedido " + ped.getIdCliente());
                } catch (NonexistentEntityException ex) {
                    Logger.getLogger(InterfazPed.class.getName()).log(Level.SEVERE, null, ex);
                } catch (Exception ex) {
                    Logger.getLogger(InterfazPed.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                JOptionPane.showMessageDialog(
                        null,
                        "Se evito la cancelacion",
                        "Advertencia",
                        JOptionPane.WARNING_MESSAGE
                );

            }
        } else {
            JOptionPane.showMessageDialog(
                    null,
                    "Selecciona una fila",
                    "Advertencia",
                    JOptionPane.WARNING_MESSAGE
            );
        }
    }//GEN-LAST:event_BcancelarActionPerformed

    private void tPedidosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tPedidosMouseClicked
        // TODO add your handling code here:
        Bcancelar.setEnabled(true);


    }//GEN-LAST:event_tPedidosMouseClicked

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        try {
            // TODO add your handling code here:
            cargarPedidos();
        } catch (Exception ex) {
            Logger.getLogger(InterfazPed.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jButton4ActionPerformed
    public void guardarFecha() {

        fechaEntre = calendarioSelec.getDate();
        textFecha.setText("Fecha: " + formato.format(fechaEntre));
        calendarioSelec.setEnabled(false);

    }

    public void registrarPedido() throws NonexistentEntityException, Exception {//agrega a detalle
        if (clienteCB.getSelectedIndex() != 0 && productoCB.getSelectedIndex() != 0 && fechaEla != null && fechaEntre != null && (Integer) cantidadSP.getValue() !=0 && confirmaFecha  )  {

            if (tallaCB.getSelectedIndex() >= 0) {

                Cliente client = clientes.get(clienteCB.getSelectedIndex() - 1);
                Date fechaActual = new Date();//fecha de generacion de pedido actual

                Producto prod = productos.get(productoCB.getSelectedIndex() - 1);
                BigInteger cant = new BigInteger(cantidadSP.getValue().toString());
                costoProductos = (prod.getPrecio()).multiply(new BigDecimal(cant));

                totalMonto = totalMonto.add(costoProductos);
                System.out.println(costoProductos + " cantidad" + cant + "total:" + totalMonto);

                String talla = tallaCB.getSelectedItem().toString();
                String color = colorCB.getSelectedItem().toString();
                String msj = "Cliente: " + client.getNombre() + " " + client.getApellido() + " Poducto: " + prod.getNombre() + "\n"
                        + " Fecha entrega:" + formato.format(fechaEntre) + " Costo:" + costoProductos;

                int resp = JOptionPane.showConfirmDialog(
                        null,
                        "¿Agregar al pedido: " + msj + "?",
                        "Aviso",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE
                );
                if (resp == JOptionPane.YES_OPTION) {

                    //agregar nuevo
                    if (NuevoP == null) {

                        Pedido nuevoP = new Pedido();
                        nuevoP.setIdCliente(client);
                        nuevoP.setFechaPedido(fechaActual);
                        nuevoP.setFechaEntregaEstimada(fechaEntre);//FECHA SELECCIONADA EN CALENDARIO
                        nuevoP.setEstado("Pendiente");//para nuevo pedido

                        cPedido.create(nuevoP);
                        NuevoP = nuevoP;
                        System.out.println("Pedido nuevo registrado" + NuevoP.getIdPedido());
                    }
                    DetallePedido producto = new DetallePedido();
                    producto.setIdPedido(NuevoP);
                    producto.setIdProducto(prod);
                    producto.setCantidad(cant);
                    System.out.println("detalle de Producto agregado ");

                } else {
                    cargarCB();
                    System.out.println("Pedido cancelado...");
                }

            } else {
                JOptionPane.showMessageDialog(
                        null,
                        "Ingresa una talla para este ipo de producto",
                        "Advertencia",
                        JOptionPane.WARNING_MESSAGE
                );
            }
            confirmaFecha=false;

        } else {
            JOptionPane.showMessageDialog(
                    null,
                    "Ingresa cadenas validas para los campos",
                    "Advertencia",
                    JOptionPane.WARNING_MESSAGE
            );
        }
    }

    private Pedido buscaPedidoTabla(int id) {
        pedidos=cPedido.findPedidoEntities();
        for (Pedido p : pedidos) {
            if (id == p.getIdPedido()) {
                return p;
            }
        }
        return null;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;

                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(InterfazPed.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(InterfazPed.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(InterfazPed.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(InterfazPed.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                InterfazPed dialog = null;
                try {
                    dialog = new InterfazPed(new javax.swing.JFrame(), true);
                } catch (Exception ex) {
                    Logger.getLogger(InterfazPed.class.getName()).log(Level.SEVERE, null, ex);
                }
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });

    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton Bcancelar;
    private javax.swing.JButton Bconfi;
    private javax.swing.JTextField TextIdP;
    private javax.swing.JButton bBuscar;
    private com.toedter.calendar.JCalendar calendarioSelec;
    private javax.swing.JSpinner cantidadSP;
    private javax.swing.JComboBox<String> clienteCB;
    private javax.swing.JComboBox<String> colorCB;
    private javax.swing.JTextArea detallesText;
    private javax.swing.JLabel impL;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private com.toedter.calendar.JCalendar jCalendar1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JComboBox<String> productoCB;
    private javax.swing.JTable tPedidos;
    private javax.swing.JComboBox<String> tallaCB;
    private javax.swing.JLabel textFecha;
    private javax.swing.JLabel txtResult;
    // End of variables declaration//GEN-END:variables
}
