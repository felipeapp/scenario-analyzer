package br.ufrn.sigaa.ensino.stricto.relatorios.jsf;


/**
 * Entidade de domínio utilizada para o relatório de concluintes da pós.
 * @author victor
 *
 */
public class RelatorioConcluintePos {

	private int idUnidade;
	
	private String nome;
	
	private Long mestrado1sem, mestrado2sem, doutorado1sem, doutorado2sem;

	public int getIdUnidade() {
		return idUnidade;
	}

	public void setIdUnidade(int idUnidade) {
		this.idUnidade = idUnidade;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public Long getMestrado1sem() {
		return mestrado1sem;
	}

	public void setMestrado1sem(Long mestrado1sem) {
		this.mestrado1sem = mestrado1sem;
	}

	public Long getMestrado2sem() {
		return mestrado2sem;
	}

	public void setMestrado2sem(Long mestrado2sem) {
		this.mestrado2sem = mestrado2sem;
	}

	public Long getDoutorado1sem() {
		return doutorado1sem;
	}

	public void setDoutorado1sem(Long doutorado1sem) {
		this.doutorado1sem = doutorado1sem;
	}

	public Long getDoutorado2sem() {
		return doutorado2sem;
	}

	public void setDoutorado2sem(Long doutorado2sem) {
		this.doutorado2sem = doutorado2sem;
	}

}
