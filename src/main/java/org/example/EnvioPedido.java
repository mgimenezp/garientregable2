package org.example;
public class EnvioPedido implements Runnable {
    private final Pedido pedido;

    public EnvioPedido(Pedido pedido) {
        this.pedido = pedido;
    }

    @Override
    public void run() {
        System.out.println("Enviando " + pedido);
        try {
            Thread.sleep(400);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        System.out.println("Pedido enviado: " + pedido);
    }
}
