package org.example;
//
import java.util.concurrent.*;
import java.util.*;

public class SistemaPedidos {
    private final ExecutorService poolProcesamiento;
    private final ExecutorService poolEmpaquetado;
    private final ExecutorService poolEnvio;
    private final PriorityBlockingQueue<Pedido> colaPedidos = new PriorityBlockingQueue<>();
    private final List<Long> tiemposProcesamiento = new ArrayList<>();

    public SistemaPedidos(int hilosProcesamiento, int hilosEmpaquetado, int hilosEnvio) {
        this.poolProcesamiento = Executors.newFixedThreadPool(hilosProcesamiento);
        this.poolEmpaquetado = Executors.newFixedThreadPool(hilosEmpaquetado);
        this.poolEnvio = Executors.newFixedThreadPool(hilosEnvio);
    }

    public void agregarPedido(Pedido pedido) {
        colaPedidos.add(pedido);
    }

    public void procesarColaPedidos() {
        List<CompletableFuture<Void>> futures = new ArrayList<>();

        while (!colaPedidos.isEmpty()) {
            Pedido pedido = colaPedidos.poll();
            long inicio = System.nanoTime();

            // Procesar el pedido en distintas etapas con sus respectivos pools
            CompletableFuture<Void> futuro = CompletableFuture
                .runAsync(new ProcesamientoPago(pedido), poolProcesamiento)
                .thenRunAsync(() -> {
                    System.out.println("Procesando a empaquetado " + pedido);
                    poolEmpaquetado.submit(new EmpaquetadoPedido(pedido));
                })
                .thenRunAsync(() -> {
                    System.out.println("Procesando a envío " + pedido);
                    poolEnvio.submit(new EnvioPedido(pedido));
                })
                .thenRun(() -> {
                    long fin = System.nanoTime();
                    long duracion = fin - inicio;
                    synchronized (tiemposProcesamiento) {
                        tiemposProcesamiento.add(duracion);
                    }
                    System.out.println("Tiempo total de procesamiento del " + pedido + ": " + duracion / 1_000_000 + " ms");
                })
                .exceptionally(ex -> {
                    System.err.println("Error procesando el pedido " + pedido.getId() + ": " + ex.getMessage());
                    return null;
                });

            futures.add(futuro);
        }

        // Esperar a que todos los pedidos se completen
        cerrarSistema(futures);
    }

    public void cerrarSistema(List<CompletableFuture<Void>> futures) {
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        shutdownPool(poolProcesamiento, "procesamiento");
        shutdownPool(poolEmpaquetado, "empaquetado");
        shutdownPool(poolEnvio, "envío");

        calcularMediaTiempoProcesamiento();
    }

    private void shutdownPool(ExecutorService pool, String nombre) {
        pool.shutdown();
        try {
            if (!pool.awaitTermination(60, TimeUnit.SECONDS)) {
                pool.shutdownNow();
            }
        } catch (InterruptedException e) {
            pool.shutdownNow();
        }
        System.out.println("Pool de " + nombre + " cerrado.");
    }

    public void calcularMediaTiempoProcesamiento() {
        long sumaTotal = tiemposProcesamiento.stream().mapToLong(Long::longValue).sum();
        double media = (double) sumaTotal / tiemposProcesamiento.size();
        System.out.println("Tiempo medio de procesamiento: " + media / 1_000_000 + " ms");
    }

    public static void main(String[] args) {
        SistemaPedidos sistema = new SistemaPedidos(5, 3, 2);

        for (int i = 1; i <= 100; i++) {
            sistema.agregarPedido(new Pedido(i, 10));
        }
        for (int i = 101; i <= 200; i++) {
            sistema.agregarPedido(new Pedido(i, 5));
        }
        for (int i = 201; i <= 300; i++) {
            sistema.agregarPedido(new Pedido(i, 1));
        }

        sistema.procesarColaPedidos();
    }
}