package entities;

public class Field {
	
	private String nome;
	private String valor;
	private String tipo;
	
	
	public Field(String nome, String tipo, String valor) {
		super();
		this.nome = nome;
		this.tipo = tipo;
		this.valor = valor;
	}
	
	public Field() {}

	public String getNome() {
		return nome;
	}
	
	public String getValor() {
		return valor;
	}
	
	public void setValor(String valor) {
		this.valor = valor;
	}
	
	public void setNome(String nome) {
		this.nome = nome;
	}
	
	public String getTipo() {
		return tipo;
	}
	
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
	
	@Override
	public String toString() {
		return "[ "+ getNome() + ", " + getTipo() + ", " + getValor() + " ]";
	}
	
	
}
