package br.ufrn.sigaa.ensino.graduacao.relatorios.jsf;

import java.util.Date;

import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.HashCodeUtil;

/**
 * Classe responsável por gerar Relatório de Prazo máximo de Bolsas dos Alunos.
 * @author Arlindo Rodrigues
 *
 */
public class DiscentesBolsas {
	/**
	 * id do discente
	 */
	private int idDiscente;
	
	/**
	 * Matrícula do aluno
	 */
	private String matricula;
	
	/**
	 * Nome do aluno
	 */
	private String nome;
	
	/**
	 * Nível no qual o aluno se encontra.
	 */
	private String nivel;
	
	/**
	 * Tipo da bolsa do aluno (Pego no SIPAC)
	 */
	private String tipoBolsa;
	
	/**
	 * Data inicio da Bolsa do aluno (Pego no SIPAC)
	 */
	private Date dataInicio;
	
	/**
	 * Data fim da bolsa do aluno (Pego no SIPAC)
	 */
	private Date dataFim;
	
	/**
	 * Data que a bolsa foi finalizada
	 */
	private Date dataFinalizacao;
	
	/**
	 * Status do discente
	 */
	private String statusDiscente;

	public int getIdDiscente() {
		return idDiscente;
	}

	public void setIdDiscente(int idDiscente) {
		this.idDiscente = idDiscente;
	}

	public String getMatricula() {
		return matricula;
	}

	public void setMatricula(String matricula) {
		this.matricula = matricula;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getNivel() {
		return nivel;
	}

	public void setNivel(String nivel) {
		this.nivel = nivel;
	}

	public String getTipoBolsa() {
		return tipoBolsa;
	}

	public void setTipoBolsa(String tipoBolsa) {
		this.tipoBolsa = tipoBolsa;
	}

	public Date getDataInicio() {
		return dataInicio;
	}

	public void setDataInicio(Date dataInicio) {
		this.dataInicio = dataInicio;
	}

	public Date getDataFim() {
		return dataFim;
	}

	public void setDataFim(Date dataFim) {
		this.dataFim = dataFim;
	}

	public boolean equals(Object obj) {
		return EqualsUtil.testEquals(this, obj, "idDiscente");
	}

	public String getStatusDiscente() {
		return statusDiscente;
	}

	public void setStatusDiscente(String statusDiscente) {
		this.statusDiscente = statusDiscente;
	}

	public Date getDataFinalizacao() {
		return dataFinalizacao;
	}

	public void setDataFinalizacao(Date dataFinalizacao) {
		this.dataFinalizacao = dataFinalizacao;
	}

	@Override
	public int hashCode() {
		return HashCodeUtil.hashAll(idDiscente);
	}	
	
	public String getStatusBolsa() {
		
		Date hoje = new Date();
		
		if (dataFinalizacao != null)
			return "FINALIZADA";
		
		if (hoje.after(dataFim))
			return "INATIVA";
		
		return "ATIVA";
	}
	
}
