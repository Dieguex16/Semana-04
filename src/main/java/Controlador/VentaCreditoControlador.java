/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controlador;

import Modelo.Credito;
import Vista.CVentaCredito;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author dgarc
 */
public class VentaCreditoControlador {

    private ArrayList<Credito> ventas; // Lista para almacenar las ventas
    private CVentaCredito vista; // Vista de la venta al crédito

    public VentaCreditoControlador(CVentaCredito vista) {
        this.vista = vista;
        this.ventas = new ArrayList<>(); // Inicializar la lista de ventas
        iniciarVista(); // Iniciar la vista
    }

    // Método para iniciar la vista
    private void iniciarVista() {
        configurarComboBoxProductos();
        configurarComboBoxLetras();
        configurarBotonAdquirir();
        configurarBotonResumen();
        actualizarFechaYHora();
    }

    // Configurar el combo box de productos
    private void configurarComboBoxProductos() {
        String[] productos = {"Lavadora", "Refrigeradora", "Licuadora", "Extractora", "Radiograbadora", "DVD", "Blue Ray"};
        vista.cmbProducto.setModel(new DefaultComboBoxModel<>(productos));
    }

    // Configurar el combo box de letras
    private void configurarComboBoxLetras() {
        String[] letras = {"3", "6", "9", "12"};
        vista.cmbLetras.setModel(new DefaultComboBoxModel<>(letras));
    }

    // Configurar el botón "Adquirir"
    private void configurarBotonAdquirir() {
        vista.btnAdquirir.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                adquirirProducto();
            }
        });
    }

    // Configurar el botón "Resumen"
    private void configurarBotonResumen() {
        vista.btnResumen.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mostrarResumen();
            }
        });
    }

    // Método para adquirir un producto y mostrar el monto final a pagar
    public void adquirirProducto() {
        // Obtener datos de la vista
        String cliente = vista.txtCliente.getText();
        String ruc = vista.txtRuc.getText();
        String producto = (String) vista.cmbProducto.getSelectedItem();
        int cantidad;

        // Verifica si hay un producto seleccionado
        if (producto == null || producto.isEmpty()) {
            mostrarError("Por favor, seleccione un producto.");
            return; // Salir del método si no hay producto seleccionado
        }

        // Manejo de errores al parsear la cantidad
        cantidad = obtenerCantidad(); // Usar método para obtener la cantidad
        if (cantidad <= 0) {
            return; // Si hay error en la cantidad, ya se mostró el mensaje
        }
        // Comprobar si el producto ya fue agregado
        if (productoYaAgregado(producto)) {
            mostrarError("El producto ya ha sido agregado.");
            return; // Salir si el producto ya existe
        }

        // Obtener fecha y hora actuales
        String fecha = obtenerFechaActual();
        String hora = obtenerHoraActual();

        // Crear una nueva venta de tipo Credito
        int letras = Integer.parseInt((String) vista.cmbLetras.getSelectedItem()); // Obtener el número de letras (cuotas)
        Credito nuevaVenta = new Credito(cantidad, cliente, fecha, hora, producto, ruc, letras);

        // Agregar la venta a la lista
        ventas.add(nuevaVenta);

        // Mostrar la venta en la tabla
        mostrarVentaEnTabla(nuevaVenta);

        // Calcular el monto final a pagar (incluyendo el descuento)
        double montoFinal = nuevaVenta.calculaMontoTotal();

        // Mostrar el monto final en txtMontoPago con dos decimales
        vista.txtMontoPago.setText("$" + String.format("%.2f", montoFinal));
    }

    // Método para obtener la cantidad ingresada
    private int obtenerCantidad() {
        try {
            return Integer.parseInt(vista.txtCantidad.getText());
        } catch (NumberFormatException e) {
            mostrarError("Por favor, ingrese un número válido para la cantidad.");
            return -1; // Indicar un error en la cantidad
        }
    }

    // Método para mostrar un mensaje de error
    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(vista, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }

    // Comprobar si el producto ya fue agregado
    private boolean productoYaAgregado(String producto) {
        for (Credito venta : ventas) {
            if (venta.getProducto().trim().equalsIgnoreCase(producto.trim())) {
                return true; // El producto ya existe
            }
        }
        return false; // El producto no existe
    }

    // Método para mostrar la venta en la tabla
    private void mostrarVentaEnTabla(Credito venta) {
        // Crear un modelo de tabla
        DefaultTableModel modelo = (DefaultTableModel) vista.tblProducto.getModel();

        // Obtener el número actual de filas en la tabla para usarlo como contador
        int itemCount = modelo.getRowCount() + 1; // Sumar 1 para que el conteo empiece desde 1

        // Agregar la venta a la tabla
        Object[] fila = {
            itemCount, // Este será el contador que se incrementa
            venta.getProducto(),
            venta.getCantidad(),
            venta.asignaPrecio(), // Asegúrate de que este método retorne el precio correcto
            venta.calculaSubtotal() // Asegúrate de que este método retorne el subtotal correcto
        };
        modelo.addRow(fila);
    }

    // Método para obtener la fecha actual
    private String obtenerFechaActual() {
        SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy");
        Date fechaActual = new Date();
        return formatoFecha.format(fechaActual);
    }

    // Método para obtener la hora actual
    private String obtenerHoraActual() {
        SimpleDateFormat formatoHora = new SimpleDateFormat("HH:mm:ss");
        Date fechaActual = new Date();
        return formatoHora.format(fechaActual);
    }

    // Método para obtener la cantidad de letras seleccionada
    private int obtenerLetras() {
        return Integer.parseInt((String) vista.cmbLetras.getSelectedItem());
    }

    // Método para calcular el monto total a pagar
    private double calcularMontoTotal() {
        double subtotalTotal = 0;

        for (Credito venta : ventas) {
            subtotalTotal += venta.calculaSubtotal();
        }

        return subtotalTotal; // Retorna el subtotal total
    }

    // Método para mostrar el resumen de la venta en la tabla tblLetras
    private void mostrarResumen() {
        // Obtener el número de letras (cuotas)
        int letras = obtenerLetras();
        double montoTotal = calcularMontoTotal(); // Calcular el monto total a pagar antes de descuento
        double descuentoTotal = calcularDescuentoTotal(); // Obtener el descuento total

        // Calcular el monto total después del descuento
        double montoFinal = montoTotal - descuentoTotal;
        double montoMensual = montoFinal / letras; // Monto mensual después del descuento

        // Limpiar la tabla antes de mostrar el resumen
        DefaultTableModel modelo = (DefaultTableModel) vista.tblLetras.getModel();
        modelo.setRowCount(0); // Limpiar la tabla

        // Agregar las cuotas a la tabla
        for (int i = 1; i <= letras; i++) {
            Object[] fila = {
                i, // Número de cuota
                String.format("%.2f", montoMensual) // Monto de la cuota formateado
            };
            modelo.addRow(fila);
        }

        // Mostrar el monto total a pagar en txtMontoPago
        vista.txtMontoPago.setText("$" + String.format("%.2f", montoFinal)); // Mostrar el total después del descuento
    }

    // Método para calcular el descuento total
    private double calcularDescuentoTotal() {
        double descuentoTotal = 0;

        // Sumar los descuentos de todas las ventas
        for (Credito venta : ventas) {
            descuentoTotal += venta.calculaDescuento(); // Obtener el descuento total
        }

        return descuentoTotal; // Retorna el descuento total
    }

    // Método para actualizar la fecha y hora en tiempo real
    public void actualizarFechaYHora() {
        java.util.Timer timer = new java.util.Timer();
        timer.scheduleAtFixedRate(new java.util.TimerTask() {
            @Override
            public void run() {
                SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy");
                SimpleDateFormat formatoHora = new SimpleDateFormat("HH:mm:ss");
                Date fechaActual = new Date();
                String fecha = formatoFecha.format(fechaActual);
                String hora = formatoHora.format(fechaActual);
                vista.lblFecha.setText(fecha);
                vista.lblHora.setText(hora);
            }
        }, 0, 1000); // Actualizar cada segundo
    }
}
