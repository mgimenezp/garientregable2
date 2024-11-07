package org.example;
public class ProcesamientoPago implements Runnable {
    private final Pedido pedido;

    public ProcesamientoPago(Pedido pedido) {
        this.pedido = pedido;
    }

    @Override
    public void run() {
        System.out.println("Procesando pago del " + pedido);
        try {
            Thread.sleep(200); 
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        System.out.println("Pago completado del " + pedido);
    }
}
