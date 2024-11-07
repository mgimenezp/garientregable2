package org.example;
public class Pedido implements Comparable<Pedido> {
    private final int id;
    private final int prioridad;  // 1 = mayor prioridad, 10 = menor prioridad

    public Pedido(int id, int prioridad) {
        this.id = id;
        this.prioridad = prioridad;
    }

    public int getId() {
        return id;
    }

    public int getPrioridad() {
        return prioridad;
    }

    @Override
    public String toString() {
        return "Pedido, id=" + id + ", prioridad=" + prioridad;
    }

    // Método para comparar por prioridad
    @Override
    public int compareTo(Pedido otroPedido) {
        return Integer.compare(this.prioridad, otroPedido.getPrioridad());
    }
}