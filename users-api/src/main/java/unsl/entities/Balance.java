package unsl.entities;

import javax.persistence.*;

@Entity
@Table(name = "balance")
public class Balance {
	public static enum Money {
        PESO_AR,
        DOLAR,
		EURO
	}

	public static enum Status {
		ACTIVA,
		BAJA
	}


	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	private long titular;
	private double saldo = 0.0;

	@Enumerated(EnumType.STRING)
    private Money tipo_moneda;

	@Enumerated(EnumType.STRING)
	private Status estado = Status.ACTIVA;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getTitular() {
		return titular;
	}

	public void setTitular(long titular) {
		this.titular = titular;
	}

	public double getSaldo() {
		return saldo;
	}

	public void setSaldo(double saldo) {
		this.saldo = saldo;
	}

	public Money getTipo_moneda() {
		return tipo_moneda;
	}

	public void setTipo_moneda(Money tipo_moneda) {
		this.tipo_moneda = tipo_moneda;
	}

	public Status getEstado() {
		return estado;
	}

	public void setEstado(Status estado) {
		this.estado = estado;
	}
}
