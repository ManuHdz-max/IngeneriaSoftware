/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JDialog.java to edit this template
 */
package vista;

import control.AdnDatos;
import control.InventarioJpaController;
import control.PedidoJpaController;
import control.ProductoJpaController;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import modelo.DatosTablaInventario;
import modelo.DatosTablaInventarioBS;
import modelo.DatosTablaPedidoInv;
import modelo.Inventario;
import modelo.MTablaInventario;
import modelo.MTablaInventarioBS;
import modelo.MTablaProducto;
import modelo.MTablaPedidoInv;
import modelo.Pedido;
import modelo.Producto;

/**
 *
 * @author ericg
 */
public class InterfazInventario extends javax.swing.JDialog {
    private ProductoJpaController cProducto;
    private PedidoJpaController cPedido;
    private InventarioJpaController cInventario;
    private AdnDatos adn;
    private ArrayList<Producto> datosProductos;
    private MTablaProducto modTabProducto;
    private ArrayList<DatosTablaPedidoInv> datosPedidos;
    private MTablaPedidoInv modTabPedido;
    private ArrayList<DatosTablaInventario> datosInventarios;
    private MTablaInventario modTabInventario;
    private ArrayList<DatosTablaInventarioBS> datosInventariosBS;
    private MTablaInventarioBS modTabInventarioBS;
    private final String SELECTCBM = "Selecciona Filtro";
    private final String SELECTCBM2 = "Sin Filtro";
    private final String SELECTCBM3 = "Precio menor a";
    private final String SELECTCBM4 = "Precio mayor a";
    private final String SELECTCBM5 = "Id. Inventario";
    private final String SELECTCBM6 = "Nombre Producto";
    private final String SELECTCBM7 = "Ubicacion";
    private List<Producto> productos;
    private List<Producto> productos_s;
    private List<Pedido> pedidos;
    private List<Pedido> pedidos_s;
    private List<Inventario> inventarios;
    private List<Inventario> inventarios_s;
    
    /**
     * Creates new form InterfazInventario
     */
    public InterfazInventario(java.awt.Frame parent, boolean modal) {
        super(parent, modal);        
        initComponents();
        adn = new AdnDatos();
        cProducto = new ProductoJpaController(adn.getEnf());
        productos = cProducto.findProductoEntities();
        cPedido = new PedidoJpaController(adn.getEnf());
        pedidos = cPedido.findPedidoEntities();
        cInventario = new InventarioJpaController(adn.getEnf());
        inventarios = cInventario.findInventarioEntities();
        datosProductos = new ArrayList<>();
        modTabProducto = new MTablaProducto(datosProductos);
        lproductos.setModel(modTabProducto);
        btnBuscar.setEnabled(false);
        cargarProductos();
        
        /*datosPedidos  = new ArrayList<>();
        modTabPedido = new MTablaPedidoInv(datosPedidos);
        lpedidos.setModel(modTabPedido);*/
        
    }
    
    private void cargarProductos(){
        cbmColumn.removeAllItems();
        cbmColumn.addItem(SELECTCBM);
        cbmColumn.addItem(SELECTCBM2);
        for(int i = 0; i<modTabProducto.getColumnCount()-1; i++){
            cbmColumn.addItem(modTabProducto.getColumnName(i));
        }
        cbmColumn.addItem(SELECTCBM3);
        cbmColumn.addItem(SELECTCBM4);
        
        productos_s = new ArrayList<>();
        for(Producto pr : productos)
            productos_s.add(pr);
        datosProductos = new ArrayList<>();
        for(Producto pr : productos_s)
            datosProductos.add(pr);
        modTabProducto = new MTablaProducto(datosProductos);
        lproductos.setModel(modTabProducto);
    }
    
    private void cargarPedidos(){
        cbmFiltroP.removeAllItems();
        cbmFiltroP.addItem(SELECTCBM);
        cbmFiltroP.addItem(SELECTCBM2);
        cbmFiltroP.addItem("Identificador Pedido");
        cbmFiltroP.addItem("Cancelado");
        cbmFiltroP.addItem("No recogido");
        cargarDeNuevoPedidos();
    }
    private void cargarDeNuevoPedidos(){
        try {
            SimpleDateFormat formato = new SimpleDateFormat("yyyy-dd-MM");
            Date fecha_actual = new Date();
            String f1 = formato.format(fecha_actual);
            Date fecha_ac = formato.parse(f1);
            pedidos_s = new ArrayList<>();
            for(Pedido pd : pedidos)
                if((pd.getEstado().toLowerCase().contains("pendiente") && fecha_ac.after(pd.getFechaEntregaEstimada())) || pd.getEstado().toLowerCase().contains("cancelado"))
                    pedidos_s.add(pd);
            datosPedidos = new ArrayList<>();
            for(Pedido pd : pedidos_s){
                DatosTablaPedidoInv pdd = new DatosTablaPedidoInv(pd);
                datosPedidos.add(pdd);
            }
            modTabPedido = new MTablaPedidoInv(datosPedidos);
            lpedidos.setModel(modTabPedido);
        } catch (ParseException ex) {
                //Error en crear Fecha Actual
        }
    }
    
    private void cargarInventario(){
        cbmFiltroA.removeAllItems();
        cbmFiltroA.addItem(SELECTCBM);
        cbmFiltroA.addItem(SELECTCBM2);
        cbmFiltroA.addItem(SELECTCBM5);
        cbmFiltroA.addItem(SELECTCBM6);
        cbmFiltroA.addItem(SELECTCBM7);
        
        inventarios_s = new ArrayList<>();
        for(Inventario inv: inventarios)
            inventarios_s.add(inv);
        datosInventarios = new ArrayList<>();
        for(Inventario inv: inventarios_s){
            DatosTablaInventario di = new DatosTablaInventario(inv);
            datosInventarios.add(di);
        }
        modTabInventario = new MTablaInventario(datosInventarios);
        linventarios.setModel(modTabInventario);
    }
    
    private void cargarInventarioBS(){
        inventarios_s = new ArrayList<>();
        for(Inventario inv: inventarios)
            inventarios_s.add(inv);
        datosInventariosBS = new ArrayList<>();
        for(Inventario inv: inventarios_s){
            if(inv.getCantidadActual()<=inv.getCantidadMinima()){
                DatosTablaInventarioBS di = new DatosTablaInventarioBS(inv);
                datosInventariosBS.add(di);
            }
        }
        modTabInventarioBS = new MTablaInventarioBS(datosInventariosBS);
        linventariosBS.setModel(modTabInventarioBS);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        EditarPedido = new javax.swing.JDialog();
        AgregarStock = new javax.swing.JDialog();
        AgregarProducto = new javax.swing.JDialog();
        tabPanel = new javax.swing.JTabbedPane();
        menuInventario = new javax.swing.JPanel();
        scrollProductos = new javax.swing.JScrollPane();
        lproductos = new javax.swing.JTable();
        jLabel8 = new javax.swing.JLabel();
        txtBuscar = new javax.swing.JTextField();
        cbmColumn = new javax.swing.JComboBox<>();
        btnBuscar = new javax.swing.JButton();
        menuProducto = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        txtNombre = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtDescripcion = new javax.swing.JTextArea();
        txtTalla = new javax.swing.JTextField();
        txtColor = new javax.swing.JTextField();
        txtPrecio = new javax.swing.JTextField();
        btnR = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        menuPedido = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        lpedidos = new javax.swing.JTable();
        jLabel10 = new javax.swing.JLabel();
        txtBuscarP = new javax.swing.JTextField();
        cbmFiltroP = new javax.swing.JComboBox<>();
        btnBuscarP = new javax.swing.JButton();
        btnPosponer = new javax.swing.JButton();
        btnEliminar = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        linventarios = new javax.swing.JTable();
        jLabel11 = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        linventariosBS = new javax.swing.JTable();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        txtBuscarA = new javax.swing.JTextField();
        btnBuscarA = new javax.swing.JButton();
        cbmFiltroA = new javax.swing.JComboBox<>();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();

        javax.swing.GroupLayout EditarPedidoLayout = new javax.swing.GroupLayout(EditarPedido.getContentPane());
        EditarPedido.getContentPane().setLayout(EditarPedidoLayout);
        EditarPedidoLayout.setHorizontalGroup(
            EditarPedidoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        EditarPedidoLayout.setVerticalGroup(
            EditarPedidoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout AgregarStockLayout = new javax.swing.GroupLayout(AgregarStock.getContentPane());
        AgregarStock.getContentPane().setLayout(AgregarStockLayout);
        AgregarStockLayout.setHorizontalGroup(
            AgregarStockLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        AgregarStockLayout.setVerticalGroup(
            AgregarStockLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout AgregarProductoLayout = new javax.swing.GroupLayout(AgregarProducto.getContentPane());
        AgregarProducto.getContentPane().setLayout(AgregarProductoLayout);
        AgregarProductoLayout.setHorizontalGroup(
            AgregarProductoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        AgregarProductoLayout.setVerticalGroup(
            AgregarProductoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        tabPanel.setTabPlacement(javax.swing.JTabbedPane.LEFT);
        tabPanel.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                tabPanelStateChanged(evt);
            }
        });

        lproductos.setModel(new javax.swing.table.DefaultTableModel(
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
        scrollProductos.setViewportView(lproductos);

        jLabel8.setFont(new java.awt.Font("Segoe UI", 2, 14)); // NOI18N
        jLabel8.setText("Dato a buscar:");

        txtBuscar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtBuscarActionPerformed(evt);
            }
        });

        cbmColumn.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cbmColumn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbmColumnActionPerformed(evt);
            }
        });

        btnBuscar.setFont(new java.awt.Font("Segoe UI", 2, 12)); // NOI18N
        btnBuscar.setText("Buscar");
        btnBuscar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBuscarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout menuInventarioLayout = new javax.swing.GroupLayout(menuInventario);
        menuInventario.setLayout(menuInventarioLayout);
        menuInventarioLayout.setHorizontalGroup(
            menuInventarioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(menuInventarioLayout.createSequentialGroup()
                .addGroup(menuInventarioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(menuInventarioLayout.createSequentialGroup()
                        .addGap(15, 15, 15)
                        .addComponent(scrollProductos, javax.swing.GroupLayout.PREFERRED_SIZE, 555, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(menuInventarioLayout.createSequentialGroup()
                        .addGap(47, 47, 47)
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 210, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(cbmColumn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnBuscar)))
                .addContainerGap(12, Short.MAX_VALUE))
        );
        menuInventarioLayout.setVerticalGroup(
            menuInventarioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, menuInventarioLayout.createSequentialGroup()
                .addContainerGap(47, Short.MAX_VALUE)
                .addGroup(menuInventarioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8)
                    .addComponent(cbmColumn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnBuscar))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scrollProductos, javax.swing.GroupLayout.PREFERRED_SIZE, 225, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10))
        );

        tabPanel.addTab("Buscar", menuInventario);

        jLabel1.setFont(new java.awt.Font("Segoe UI", 2, 14)); // NOI18N
        jLabel1.setText("Nombre:");

        jLabel2.setFont(new java.awt.Font("Segoe UI", 2, 14)); // NOI18N
        jLabel2.setText("Descripcion:");

        jLabel3.setFont(new java.awt.Font("Segoe UI", 2, 14)); // NOI18N
        jLabel3.setText("Talla:");

        jLabel4.setFont(new java.awt.Font("Segoe UI", 2, 14)); // NOI18N
        jLabel4.setText("Color:");

        jLabel5.setFont(new java.awt.Font("Segoe UI", 2, 14)); // NOI18N
        jLabel5.setText("Precio:");

        txtDescripcion.setColumns(20);
        txtDescripcion.setRows(5);
        jScrollPane1.setViewportView(txtDescripcion);

        txtTalla.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtTallaActionPerformed(evt);
            }
        });

        btnR.setFont(new java.awt.Font("Segoe UI", 2, 14)); // NOI18N
        btnR.setText("Registrar");
        btnR.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRActionPerformed(evt);
            }
        });

        jLabel6.setFont(new java.awt.Font("Segoe UI", 3, 18)); // NOI18N
        jLabel6.setText("Registrar Producto");

        javax.swing.GroupLayout menuProductoLayout = new javax.swing.GroupLayout(menuProducto);
        menuProducto.setLayout(menuProductoLayout);
        menuProductoLayout.setHorizontalGroup(
            menuProductoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(menuProductoLayout.createSequentialGroup()
                .addGap(72, 72, 72)
                .addGroup(menuProductoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(menuProductoLayout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addGap(21, 21, 21)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 237, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(menuProductoLayout.createSequentialGroup()
                        .addGroup(menuProductoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3)
                            .addComponent(jLabel4)
                            .addComponent(jLabel5))
                        .addGap(40, 40, 40)
                        .addGroup(menuProductoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtTalla, javax.swing.GroupLayout.PREFERRED_SIZE, 210, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtPrecio, javax.swing.GroupLayout.PREFERRED_SIZE, 210, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(menuProductoLayout.createSequentialGroup()
                                .addComponent(txtColor, javax.swing.GroupLayout.PREFERRED_SIZE, 210, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(53, 53, 53)
                                .addComponent(btnR))))
                    .addGroup(menuProductoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(jLabel6)
                        .addGroup(menuProductoLayout.createSequentialGroup()
                            .addComponent(jLabel1)
                            .addGap(27, 27, 27)
                            .addComponent(txtNombre, javax.swing.GroupLayout.PREFERRED_SIZE, 210, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(80, Short.MAX_VALUE))
        );
        menuProductoLayout.setVerticalGroup(
            menuProductoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(menuProductoLayout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addComponent(jLabel6)
                .addGap(18, 18, 18)
                .addGroup(menuProductoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtNombre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(menuProductoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(menuProductoLayout.createSequentialGroup()
                        .addGap(15, 15, 15)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(menuProductoLayout.createSequentialGroup()
                        .addGap(39, 39, 39)
                        .addComponent(jLabel2)))
                .addGap(14, 14, 14)
                .addGroup(menuProductoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(txtTalla, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(menuProductoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(txtColor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnR))
                .addGap(6, 6, 6)
                .addGroup(menuProductoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(txtPrecio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(49, Short.MAX_VALUE))
        );

        tabPanel.addTab("Registrar", menuProducto);

        jLabel9.setFont(new java.awt.Font("Segoe UI", 3, 18)); // NOI18N
        jLabel9.setText("Pedidos Pendientes");

        lpedidos.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane2.setViewportView(lpedidos);

        jLabel10.setFont(new java.awt.Font("Segoe UI", 2, 14)); // NOI18N
        jLabel10.setText("Dato a buscar:");

        cbmFiltroP.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cbmFiltroP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbmFiltroPActionPerformed(evt);
            }
        });

        btnBuscarP.setText("Buscar");
        btnBuscarP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBuscarPActionPerformed(evt);
            }
        });

        btnPosponer.setText("Posponer");

        btnEliminar.setText("Eliminar");
        btnEliminar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEliminarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout menuPedidoLayout = new javax.swing.GroupLayout(menuPedido);
        menuPedido.setLayout(menuPedidoLayout);
        menuPedidoLayout.setHorizontalGroup(
            menuPedidoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(menuPedidoLayout.createSequentialGroup()
                .addGroup(menuPedidoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(menuPedidoLayout.createSequentialGroup()
                        .addGap(35, 35, 35)
                        .addGroup(menuPedidoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addGroup(menuPedidoLayout.createSequentialGroup()
                                .addComponent(jLabel9)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btnPosponer)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnEliminar))
                            .addGroup(menuPedidoLayout.createSequentialGroup()
                                .addComponent(jLabel10)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtBuscarP, javax.swing.GroupLayout.PREFERRED_SIZE, 226, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cbmFiltroP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnBuscarP))))
                    .addGroup(menuPedidoLayout.createSequentialGroup()
                        .addGap(15, 15, 15)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 553, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(14, Short.MAX_VALUE))
        );
        menuPedidoLayout.setVerticalGroup(
            menuPedidoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(menuPedidoLayout.createSequentialGroup()
                .addContainerGap(30, Short.MAX_VALUE)
                .addGroup(menuPedidoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, menuPedidoLayout.createSequentialGroup()
                        .addGroup(menuPedidoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnPosponer)
                            .addComponent(btnEliminar))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, menuPedidoLayout.createSequentialGroup()
                        .addComponent(jLabel9)
                        .addGap(27, 27, 27)))
                .addGroup(menuPedidoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(txtBuscarP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbmFiltroP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnBuscarP))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10))
        );

        tabPanel.addTab("Pedidos", menuPedido);

        linventarios.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane3.setViewportView(linventarios);

        jLabel11.setFont(new java.awt.Font("Segoe UI", 3, 18)); // NOI18N
        jLabel11.setText("Stock Producto");

        linventariosBS.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane4.setViewportView(linventariosBS);

        jLabel12.setFont(new java.awt.Font("Segoe UI", 2, 12)); // NOI18N
        jLabel12.setText("Productos Bajo Stock:");

        jLabel13.setFont(new java.awt.Font("Segoe UI", 2, 14)); // NOI18N
        jLabel13.setText("Dato a buscar:");

        btnBuscarA.setText("Buscar");
        btnBuscarA.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBuscarAActionPerformed(evt);
            }
        });

        cbmFiltroA.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cbmFiltroA.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbmFiltroAActionPerformed(evt);
            }
        });

        jButton1.setText("Aumentar Stock");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setText("Agregar Producto");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 553, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(14, 14, 14))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(25, 25, 25)
                                .addComponent(jLabel13)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtBuscarA, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cbmFiltroA, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 8, Short.MAX_VALUE)
                                .addComponent(btnBuscarA))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(jButton2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jButton1)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(40, 40, 40)
                        .addComponent(jLabel11)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel12)
                        .addGap(12, 12, 12)))
                .addGap(22, 22, 22))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap(14, Short.MAX_VALUE)
                        .addComponent(jLabel12)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(30, 30, 30)
                        .addComponent(jLabel11)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton1)
                            .addComponent(jButton2))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel13)
                            .addComponent(txtBuscarA, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnBuscarA)
                            .addComponent(cbmFiltroA, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 159, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10))
        );

        tabPanel.addTab("Almacen", jPanel1);

        jLabel7.setFont(new java.awt.Font("Segoe UI", 3, 24)); // NOI18N
        jLabel7.setText("Inventario");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tabPanel)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addGap(273, 273, 273)
                .addComponent(jLabel7)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(tabPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 311, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtTallaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtTallaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtTallaActionPerformed

    private void btnRActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRActionPerformed
        Producto p = new Producto();
        if(!txtNombre.getText().trim().isEmpty() && !txtDescripcion.getText().trim().isEmpty() && !txtTalla.getText().trim().isEmpty()
                && !txtColor.getText().trim().isEmpty()){
            p.setNombre((String)txtNombre.getText());
            p.setDescripcion(txtDescripcion.getText());
            p.setTalla((String)txtTalla.getText());
            p.setColor((String)txtColor.getText());
            try{
                p.setPrecio(new BigDecimal(txtPrecio.getText()));
                cProducto.create(p);
            }catch(Exception e){
                JOptionPane.showMessageDialog(this,"Formato de Precio invalido");
            }
        }
        else
            JOptionPane.showMessageDialog(this,"Exiten campos vacios");
    }//GEN-LAST:event_btnRActionPerformed

    private void txtBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtBuscarActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtBuscarActionPerformed

    private void cbmColumnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbmColumnActionPerformed
        if(cbmColumn.getSelectedItem()!= SELECTCBM)
            btnBuscar.setEnabled(true);
        else
            btnBuscar.setEnabled(false);
    }//GEN-LAST:event_cbmColumnActionPerformed

    private void btnBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuscarActionPerformed
        int opc = cbmColumn.getSelectedIndex();
        lproductos.setModel(new MTablaProducto(new ArrayList<>()));
        try{
            productos_s = new ArrayList<>();
            switch(opc){
                case 2:
                    for(Producto pr : productos){
                        int idp = Integer.parseInt(txtBuscar.getText());
                        if(pr.getIdProducto()== idp)
                            productos_s.add(pr);
                    }
                    break;
                case 3:
                    for(Producto pr : productos)
                        if(pr.getNombre().toLowerCase().contains(txtBuscar.getText().toLowerCase()))
                            productos_s.add(pr);
                    break;
                case 4:
                    for(Producto pr : productos)
                        if(pr.getDescripcion().toLowerCase().contains(txtBuscar.getText().toLowerCase()))
                            productos_s.add(pr);
                    break;
                case 5:
                    for(Producto pr : productos)
                        if(pr.getTalla().toLowerCase().contains(txtBuscar.getText().toLowerCase()))
                            productos_s.add(pr);
                    break;
                case 6:
                    for(Producto pr : productos)
                        if(pr.getColor().toLowerCase().contains(txtBuscar.getText().toLowerCase()))
                            productos_s.add(pr);
                    break;
                case 7:
                    BigDecimal bd = new BigDecimal(txtBuscar.getText());
                    for(Producto pr : productos)
                        if(bd.compareTo(pr.getPrecio())>=0)
                            productos_s.add(pr);
                    break;
                case 8:
                    BigDecimal bdM = new BigDecimal(txtBuscar.getText());
                    for(Producto pr : productos)
                        if(bdM.compareTo(pr.getPrecio())<=0)
                            productos_s.add(pr);
                    break;
                default:
                    for(Producto pr : productos)
                        productos_s.add(pr);
                    break;
            }
            datosProductos = new ArrayList<>();
            for(Producto ps : productos_s)
                datosProductos.add(ps);
            modTabProducto  = new MTablaProducto(datosProductos);
            lproductos.setModel(modTabProducto);
        }
        catch(Exception e){
            JOptionPane.showMessageDialog(this,"Formato de Busqueda invalido");
        }
    }//GEN-LAST:event_btnBuscarActionPerformed

    private void cbmFiltroPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbmFiltroPActionPerformed
        if(cbmFiltroP.getSelectedItem()!= SELECTCBM)
            btnBuscarP.setEnabled(true);
        else
            btnBuscarP.setEnabled(false);
        if(cbmFiltroP.getSelectedItem()== "Identificador Pedido")
            txtBuscarP.setEnabled(true);
        else
            txtBuscarP.setEnabled(false);
    }//GEN-LAST:event_cbmFiltroPActionPerformed

    private void btnBuscarPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuscarPActionPerformed
        int opc2 = cbmFiltroP.getSelectedIndex();
        lpedidos.setModel(new MTablaPedidoInv(new ArrayList<>()));
        try{
            pedidos_s = new ArrayList<>();
            switch(opc2){
                case 2:
                    for(Pedido pd : pedidos){
                        int idp = Integer.parseInt(txtBuscarP.getText());
                        if(pd.getIdPedido()== idp)
                            pedidos_s.add(pd);
                    }
                    break;
                case 3:
                    for(Pedido pd : pedidos)
                        if(pd.getEstado().toLowerCase().contains("cancelado"))
                            pedidos_s.add(pd);
                    break;
                case 4:
                    SimpleDateFormat formato = new SimpleDateFormat("yyyy-dd-MM");
                    Date fecha_actual = new Date();
                    String f1 = formato.format(fecha_actual);
                    Date fecha_ac = formato.parse(f1);
                    for(Pedido pd : pedidos)
                        if(fecha_ac.after(pd.getFechaEntregaEstimada()))
                            pedidos_s.add(pd);
                    break;
                default:
                    cargarDeNuevoPedidos();
                    break;
            }
            datosPedidos = new ArrayList<>();
            for(Pedido ps : pedidos_s){
                DatosTablaPedidoInv psd = new DatosTablaPedidoInv(ps);
                datosPedidos.add(psd);
            }
            modTabPedido  = new MTablaPedidoInv(datosPedidos);
            lpedidos.setModel(modTabPedido);
        }
        catch(Exception e){
            JOptionPane.showMessageDialog(this,"Formato de Busqueda invalido");
        }
    }//GEN-LAST:event_btnBuscarPActionPerformed

    private void tabPanelStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_tabPanelStateChanged
        int ind = tabPanel.getSelectedIndex();
        if(ind == 2)
            cargarPedidos();
        if(ind == 3){
            cargarInventario();
            cargarInventarioBS();
        }
    }//GEN-LAST:event_tabPanelStateChanged

    private void cbmFiltroAActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbmFiltroAActionPerformed
        if(cbmFiltroA.getSelectedItem()!= SELECTCBM)
            btnBuscarA.setEnabled(true);
        else
            btnBuscarA.setEnabled(false);
    }//GEN-LAST:event_cbmFiltroAActionPerformed

    private void btnBuscarAActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuscarAActionPerformed
        int opc3= cbmFiltroA.getSelectedIndex();
        linventarios.setModel(new MTablaInventario(new ArrayList<>()));
        try{
            inventarios_s = new ArrayList<>();
            switch(opc3){
                case 2:
                    for(Inventario iv : inventarios){
                        int idi = Integer.parseInt(txtBuscarA.getText());
                        if(iv.getIdInventario()== idi)
                            inventarios_s.add(iv);
                    }
                    break;
                case 3:
                    for(Inventario iv : inventarios)
                        if(iv.getIdProducto().getNombre().toLowerCase().contains(txtBuscarA.getText().toLowerCase()))
                            inventarios_s.add(iv);
                    break;
                case 4:
                    for(Inventario iv : inventarios)
                        if(iv.getIdProducto().getDescripcion().toLowerCase().contains(txtBuscarA.getText().toLowerCase()))
                            inventarios_s.add(iv);
                    break;
                default:
                    for(Inventario iv : inventarios)
                        inventarios_s.add(iv);
                    break;
            }
            
            datosInventarios = new ArrayList<>();
            for(Inventario ps : inventarios_s){
                DatosTablaInventario psi = new DatosTablaInventario(ps);
                datosInventarios.add(psi);
            }
            modTabInventario  = new MTablaInventario(datosInventarios);
            linventarios.setModel(modTabInventario);
            cargarInventarioBS();
        }
        catch(Exception e){
            JOptionPane.showMessageDialog(this,"Formato de Busqueda invalido");
        }
    }//GEN-LAST:event_btnBuscarAActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton2ActionPerformed

    private void btnEliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminarActionPerformed
        Inventario invs = modTabInventario.getInventario(linventarios.getSelectedRow());
        
    }//GEN-LAST:event_btnEliminarActionPerformed

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
            java.util.logging.Logger.getLogger(InterfazInventario.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(InterfazInventario.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(InterfazInventario.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(InterfazInventario.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                InterfazInventario dialog = new InterfazInventario(new javax.swing.JFrame(), true);
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
    private javax.swing.JDialog AgregarProducto;
    private javax.swing.JDialog AgregarStock;
    private javax.swing.JDialog EditarPedido;
    private javax.swing.JButton btnBuscar;
    private javax.swing.JButton btnBuscarA;
    private javax.swing.JButton btnBuscarP;
    private javax.swing.JButton btnEliminar;
    private javax.swing.JButton btnPosponer;
    private javax.swing.JButton btnR;
    private javax.swing.JComboBox<String> cbmColumn;
    private javax.swing.JComboBox<String> cbmFiltroA;
    private javax.swing.JComboBox<String> cbmFiltroP;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JTable linventarios;
    private javax.swing.JTable linventariosBS;
    private javax.swing.JTable lpedidos;
    private javax.swing.JTable lproductos;
    private javax.swing.JPanel menuInventario;
    private javax.swing.JPanel menuPedido;
    private javax.swing.JPanel menuProducto;
    private javax.swing.JScrollPane scrollProductos;
    private javax.swing.JTabbedPane tabPanel;
    private javax.swing.JTextField txtBuscar;
    private javax.swing.JTextField txtBuscarA;
    private javax.swing.JTextField txtBuscarP;
    private javax.swing.JTextField txtColor;
    private javax.swing.JTextArea txtDescripcion;
    private javax.swing.JTextField txtNombre;
    private javax.swing.JTextField txtPrecio;
    private javax.swing.JTextField txtTalla;
    // End of variables declaration//GEN-END:variables
}
