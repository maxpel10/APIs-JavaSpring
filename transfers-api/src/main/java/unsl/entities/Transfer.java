package unsl.entities;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "transfer")
public class Transfer {
	public static enum Status {
		PENDIENTE,
		PROCESADA,
		CANCELADA
	}


	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long nro_transferencia;

	private long id_cuenta_origen;
	private long id_cuenta_destino;
	private double monto;
	private Date fecha;


	@Enumerated(EnumType.STRING)
	private Status estado = Status.PENDIENTE;

	public long getNro_transferencia() {
		return nro_transferencia;
	}

	public void setNro_transferencia(long nro_transferencia) {
		this.nro_transferencia = nro_transferencia;
	}

	public long getId_cuenta_origen() {
		return id_cuenta_origen;
	}

	public void setId_cuenta_origen(long id_cuenta_origen) {
		this.id_cuenta_origen = id_cuenta_origen;
	}

	public long getId_cuenta_destino() {
		return id_cuenta_destino;
	}

	public void setId_cuenta_destino(long id_cuenta_destino) {
		this.id_cuenta_destino = id_cuenta_destino;
	}

	public double getMonto() {
		return monto;
	}

	public void setMonto(double monto) {
		this.monto = monto;
	}

	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	public Status getEstado() {
		return estado;
	}

	public void setEstado(Status estado) {
		this.estado = estado;
	}
}

