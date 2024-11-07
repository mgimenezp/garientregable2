package org.example;
public class EmpaquetadoPedido implements Runnable {
    private final Pedido pedido;

    public EmpaquetadoPedido(Pedido pedido) {
        this.pedido = pedido;
    }

    @Override
    public void run() {
        System.out.println("Empaquetando " + pedido);
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        System.out.println("Empaquetado completado del " + pedido);
    }
}
