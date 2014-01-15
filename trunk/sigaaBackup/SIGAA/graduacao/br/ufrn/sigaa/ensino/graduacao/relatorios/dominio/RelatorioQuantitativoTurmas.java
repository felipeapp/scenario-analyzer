/* 
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA - Sistema Integrado de Gestão de Atividades Acadêmicas
 * Criado em: 20/01/2009
 */
package br.ufrn.sigaa.ensino.graduacao.relatorios.dominio;

/** 
 * Esta entidade é utilizada para armazenar os dados do relatório de 
 * quantitativo de solicitações, turmas e matriculas por componente curricular
 * @author Victor Hugo
 */
public class RelatorioQuantitativoTurmas {

	private int idDisciplina;
	private String codigoDisciplina, nomeDisciplina;
	private int totalSolicitacoes, turmasNegadas, turmasNaoAtendidas, turmasAtendidasParcialmente, turmasAtendidas;
	private int matriculados, indeferidos;
	private int vagasSolicitadas, vagasAtendidas;
	
	public int getIdDisciplina() {
		return idDisciplina;
	}
	public void setIdDisciplina(int idDisciplina) {
		this.idDisciplina = idDisciplina;
	}
	public String getCodigoDisciplina() {
		return codigoDisciplina;
	}
	public void setCodigoDisciplina(String codigoDisciplina) {
		this.codigoDisciplina = codigoDisciplina;
	}
	public String getNomeDisciplina() {
		return nomeDisciplina;
	}
	public void setNomeDisciplina(String nomeDisciplina) {
		this.nomeDisciplina = nomeDisciplina;
	}
	public int getTotalSolicitacoes() {
		return totalSolicitacoes;
	}
	public void setTotalSolicitacoes(int totalSolicitacoes) {
		this.totalSolicitacoes = totalSolicitacoes;
	}
	public int getTurmasNegadas() {
		return turmasNegadas;
	}
	public void setTurmasNegadas(int turmasNegadas) {
		this.turmasNegadas = turmasNegadas;
	}
	public int getTurmasNaoAtendidas() {
		return turmasNaoAtendidas;
	}
	public void setTurmasNaoAtendidas(int turmasNaoAtendidas) {
		this.turmasNaoAtendidas = turmasNaoAtendidas;
	}
	public int getTurmasAtendidasParcialmente() {
		return turmasAtendidasParcialmente;
	}
	public void setTurmasAtendidasParcialmente(int turmasAtendidasParcialmente) {
		this.turmasAtendidasParcialmente = turmasAtendidasParcialmente;
	}
	public int getTurmasAtendidas() {
		return turmasAtendidas;
	}
	public void setTurmasAtendidas(int turmasAtendidas) {
		this.turmasAtendidas = turmasAtendidas;
	}
	public int getMatriculados() {
		return matriculados;
	}
	public void setMatriculados(int matriculados) {
		this.matriculados = matriculados;
	}
	public int getIndeferidos() {
		return indeferidos;
	}
	public void setIndeferidos(int indeferidos) {
		this.indeferidos = indeferidos;
	}
	public int getVagasSolicitadas() {
		return vagasSolicitadas;
	}
	public void setVagasSolicitadas(int vagasSolicitadas) {
		this.vagasSolicitadas = vagasSolicitadas;
	}
	public int getVagasAtendidas() {
		return vagasAtendidas;
	}
	public void setVagasAtendidas(int vagasAtendidas) {
		this.vagasAtendidas = vagasAtendidas;
	}
	
}
