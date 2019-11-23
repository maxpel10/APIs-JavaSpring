package unsl.entities;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.persistence.*;


public class User {
    public static enum Status {
        ACTIVO,
        BAJA
    }

    private long id;

    private long dni;

    private String nombre;

    private String apellido;

    private Status estado = Status.ACTIVO;

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public void setDni(Long dni) {
        this.dni = dni;
    }

    public Long getDni() {
        return dni;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public Status getEstado() { return estado; }

    public void setEstado(Status estado) {
        this.estado = estado;
    }
}
