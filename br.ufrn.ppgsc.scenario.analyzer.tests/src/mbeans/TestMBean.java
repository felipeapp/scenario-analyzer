package mbeans;

import java.util.Collections;
import java.util.List;

//@Component
public class TestMBean {

	private String nome;
	private int idade;
	private List<String> telefones;
	private boolean ativo;

	public TestMBean() {

	}

	public void imprimir() {
		setIdade(18);
		notificar();
		new TestMBean();
	}

	public void notificar() {
		System.out.println("bla!");
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public int getIdade() {
		return idade;
	}

	public void setIdade(int idade) {
		this.idade = idade;
	}

	public List<String> getTelefones() {
		return Collections.unmodifiableList(telefones);
	}

	public void setTelefones(List<String> telefones) {
		this.telefones = telefones;
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

}
