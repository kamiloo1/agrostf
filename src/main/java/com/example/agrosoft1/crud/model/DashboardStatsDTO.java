package com.example.agrosoft1.crud.model;

/**
 * DTO para estadísticas del dashboard
 */
public class DashboardStatsDTO {
    private long totalUsuarios;
    private long totalCultivos;
    private long totalGanado;
    private long ganadoSaludable;
    private long totalActividadesPendientes;
    private long totalActividadesCompletadas;
    private long totalTratamientos;
    private long cultivosActivos;

    public DashboardStatsDTO() {
    }

    // Getters y Setters
    public long getTotalUsuarios() {
        return totalUsuarios;
    }

    public void setTotalUsuarios(long totalUsuarios) {
        this.totalUsuarios = totalUsuarios;
    }

    public long getTotalCultivos() {
        return totalCultivos;
    }

    public void setTotalCultivos(long totalCultivos) {
        this.totalCultivos = totalCultivos;
    }

    public long getTotalGanado() {
        return totalGanado;
    }

    public void setTotalGanado(long totalGanado) {
        this.totalGanado = totalGanado;
    }

    public long getGanadoSaludable() {
        return ganadoSaludable;
    }

    public void setGanadoSaludable(long ganadoSaludable) {
        this.ganadoSaludable = ganadoSaludable;
    }

    public long getTotalActividadesPendientes() {
        return totalActividadesPendientes;
    }

    public void setTotalActividadesPendientes(long totalActividadesPendientes) {
        this.totalActividadesPendientes = totalActividadesPendientes;
    }

    public long getTotalActividadesCompletadas() {
        return totalActividadesCompletadas;
    }

    public void setTotalActividadesCompletadas(long totalActividadesCompletadas) {
        this.totalActividadesCompletadas = totalActividadesCompletadas;
    }

    public long getTotalTratamientos() {
        return totalTratamientos;
    }

    public void setTotalTratamientos(long totalTratamientos) {
        this.totalTratamientos = totalTratamientos;
    }

    public long getCultivosActivos() {
        return cultivosActivos;
    }

    public void setCultivosActivos(long cultivosActivos) {
        this.cultivosActivos = cultivosActivos;
    }
}
