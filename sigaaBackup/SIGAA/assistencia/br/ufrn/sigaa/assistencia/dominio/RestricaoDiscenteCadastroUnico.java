package br.ufrn.sigaa.assistencia.dominio;

/**
 * Classe para mapear os filtros da busca
 * de discente do cadastro único
 * 
 * @author Rayron Victor
 *
 */
public class RestricaoDiscenteCadastroUnico {
	
	/** busca por curso? */
	private boolean buscaCurso;
	/** busca aluno sem bolsa? */
	private boolean buscaAlunosSemBolsa;
	/** busca área de interesse? */
	private boolean buscaAreaInteresse;
	
	public boolean isBuscaCurso() {
		return buscaCurso;
	}
	public void setBuscaCurso(boolean buscaCurso) {
		this.buscaCurso = buscaCurso;
	}
	public boolean isBuscaAlunosSemBolsa() {
		return buscaAlunosSemBolsa;
	}
	public void setBuscaAlunosSemBolsa(boolean buscaAlunosSemBolsa) {
		this.buscaAlunosSemBolsa = buscaAlunosSemBolsa;
	}
	public boolean isBuscaAreaInteresse() {
		return buscaAreaInteresse;
	}
	public void setBuscaAreaInteresse(boolean buscaAreaInteresse) {
		this.buscaAreaInteresse = buscaAreaInteresse;
	}
}
