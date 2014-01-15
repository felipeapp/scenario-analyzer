/*
 * Universidade Federal do Rio Grande do Norte

 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em 10/02/2010
 */
package br.ufrn.integracao.dto;

import java.io.Serializable;
import java.util.Date;

/**
 * Data Transfer Object
 * Classe utilizada na solicitação do cadastro de bolsas no sipac
 * através do módulo de projetos do sigaa.
 * 
 * @author Ilueny Santos
 *
 */
public class InclusaoBolsaAcademicaDTO implements Serializable {
	
	private static final long serialVersionUID = -1158419680767637979L;
	
	/**
	 * IDs referentes ao tipo bolsa do SIPAC e também da classe: br.ufrn.sigaa.dominio.TipoBolsa
	 */
	public static final int BOLSA_AUXILIO_ALIMENTACAO = 149;
	public static final int BOLSA_AUXILIO_RESIDENCIA_POS = 150;
	public static final int BOLSA_AUXILIO_RESIDENCIA_GRADUACAO = 151;
	public static final int BOLSA_AUXILIO_TRANSPORTE = 91;
	
	/**
	 * Tipos de bolsas específicas do SIPAC 
	 */
	public static final int BOLSA_AUX_ALIMENTACAO_TIPO_1_SIPAC = 107;
	public static final int BOLSA_AUX_ALIMENTACAO_TIPO_2_SIPAC = 108;
	public static final int BOLSA_AUX_RESESIDENCIA_SIPAC = 100;
	
	private int idBolsa;
	private int idPessoa;
	private long matricula;
	private String nivel;
	private int idCurso;
	private int idRegistroEntrada;
	private int codigoBanco;
	private String agenciaBancaria;
	private String numeroContaBancaria;
	private String operacaoContaBancaria;
	private int tipoBolsa;
	private int idUnidadeLocalTrabalho;
	private int idUnidadeResponsavel;
	private Date dataInicio;
	private Date dataFim;
	private String justificativa;
	private String observacao;
	private int idUsuarioCadastro;
	private int idDiscenteProjeto;
	private boolean carente;
	private long cpf;
	
	public void setIdBolsa(int idBolsa) {
		this.idBolsa = idBolsa;
	}
	public int getIdBolsa() {
		return idBolsa;
	}
	public int getIdPessoa() {
		return idPessoa;
	}
	public void setIdPessoa(int idPessoa) {
		this.idPessoa = idPessoa;
	}
	public int getIdCurso() {
		return idCurso;
	}
	public void setIdCurso(int idCurso) {
		this.idCurso = idCurso;
	}
	public int getIdRegistroEntrada() {
		return idRegistroEntrada;
	}
	public void setIdRegistroEntrada(int idRegistroEntrada) {
		this.idRegistroEntrada = idRegistroEntrada;
	}
	public int getCodigoBanco() {
		return codigoBanco;
	}
	public void setCodigoBanco(int codigoBanco) {
		this.codigoBanco = codigoBanco;
	}
	public String getAgenciaBancaria() {
		return agenciaBancaria;
	}
	public void setAgenciaBancaria(String agenciaBancaria) {
		this.agenciaBancaria = agenciaBancaria;
	}
	public String getNumeroContaBancaria() {
		return numeroContaBancaria;
	}
	public void setNumeroContaBancaria(String numeroContaBancaria) {
		this.numeroContaBancaria = numeroContaBancaria;
	}
	public int getTipoBolsa() {
		return tipoBolsa;
	}
	public void setTipoBolsa(int tipoBolsa) {
		this.tipoBolsa = tipoBolsa;
	}
	public int getIdUnidadeLocalTrabalho() {
		return idUnidadeLocalTrabalho;
	}
	public void setIdUnidadeLocalTrabalho(int idUnidadeLocalTrabalho) {
		this.idUnidadeLocalTrabalho = idUnidadeLocalTrabalho;
	}
	public int getIdUnidadeResponsavel() {
		return idUnidadeResponsavel;
	}
	public void setIdUnidadeResponsavel(int idUnidadeResponsavel) {
		this.idUnidadeResponsavel = idUnidadeResponsavel;
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
	public String getJustificativa() {
		return justificativa;
	}
	public void setJustificativa(String justificativa) {
		this.justificativa = justificativa;
	}
	public String getObservacao() {
		return observacao;
	}
	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}
	public int getIdUsuarioCadastro() {
		return idUsuarioCadastro;
	}
	public void setIdUsuarioCadastro(int idUsuarioCadastro) {
		this.idUsuarioCadastro = idUsuarioCadastro;
	}
	public long getMatricula() {
		return matricula;
	}
	public void setMatricula(long matricula) {
		this.matricula = matricula;
	}
	public String getNivel() {
		return nivel;
	}
	public void setNivel(String nivel) {
		this.nivel = nivel;
	}
	/**
	 * Identifica o discente no projeto do SIGAA dependendo
	 * do tipo de bolsa.
	 * 
	 * @return
	 */
	public int getIdDiscenteProjeto() {
		return idDiscenteProjeto;
	}
	public void setIdDiscenteProjeto(int idDiscenteProjeto) {
		this.idDiscenteProjeto = idDiscenteProjeto;
	}
	
	/**
	 * Métodos utilizados quando se vai solicitar bolsas a partir do SAE
	 * 
	 */
	public boolean isBolsaAlimentacao() {
		if (tipoBolsa == BOLSA_AUXILIO_ALIMENTACAO)
			return true;
		else 
			return false;
	}
	
	public boolean isBolsaResidencia() {
		if (tipoBolsa == BOLSA_AUXILIO_RESIDENCIA_GRADUACAO || tipoBolsa == BOLSA_AUXILIO_RESIDENCIA_POS)
			return true;
		else
			return false;
	}
	
	public boolean isBolsaTransporte() {
		return tipoBolsa == BOLSA_AUXILIO_TRANSPORTE ? true : false;
	}
	
	public boolean isTipoBolsaEspecificaSIPAC() {
		if (tipoBolsa == BOLSA_AUX_ALIMENTACAO_TIPO_1_SIPAC || tipoBolsa == BOLSA_AUX_ALIMENTACAO_TIPO_2_SIPAC || tipoBolsa == BOLSA_AUX_RESESIDENCIA_SIPAC)
			return true;
		return false;
	}
	public boolean isCarente() {
		return carente;
	}
	public void setCarente(boolean carente) {
		this.carente = carente;
	}
	public long getCpf() {
		return cpf;
	}
	public void setCpf(long cpf) {
		this.cpf = cpf;
	}
	public String getOperacaoContaBancaria() {
		return operacaoContaBancaria;
	}
	public void setOperacaoContaBancaria(String operacaoContaBancaria) {
		this.operacaoContaBancaria = operacaoContaBancaria;
	}
}
