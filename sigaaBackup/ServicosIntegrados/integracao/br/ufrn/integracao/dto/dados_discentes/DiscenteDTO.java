/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em 25/06/2013
 * Autor: Diego Jácome
 */
package br.ufrn.integracao.dto.dados_discentes;

import java.io.Serializable;
import java.util.Date;

import fundacao.integracao.academico.CursoDTO;


/**
 * DTO para recepção dos dados de discente via o serviço do SIGAA.
 * 
 * @author Diego Jácome
 *
 */
public class DiscenteDTO implements Serializable{

	public static final int ATIVO = 1;
	public static final int CADASTRADO = 2;
	public static final int CONCLUIDO = 3;
	public static final int AFASTADO = 4;
	public static final int TRANCADO = 5;
	public static final int CANCELADO = 6;
	public static final int JUBILADO = 7;
	public static final int FORMANDO = 8;
	public static final int GRADUANDO = 9;
	public static final int EXCLUIDO = 10;
	public static final int EM_HOMOLOGACAO = 11;
	
	
	/** Identificador do registro do discente. */
	private int idDiscente;

	/** Cpf do discente Nacional. */
	private Long cpf;

	/** Número de matrícula do discente. */
	private String matricula;

	/** Passaporte do discente estrangeiro. */
	private String passaporte;

	/** Nome do discente. */
	private String nome;

	/** Data de nascimento do discente. */
	private Date dataNascimento;

	/** Curso do discente. */
	private CursoDTO curso;

	/** Sexo do discente. */
	private Character sexo;
	
	/** Status do discente atual. */
	private Integer status;

	/** Nível de ensino do discente. */
	private String nivel;
	
	/** Data do cadastro do discente.*/
	private Date dataCadastro;  

	/** Data da alteração do status do discente.*/
	private Date dataAlteracaoStatus;

	/** Data da última atualização da pessoa. */
	private Date ultimaAtualizacao;

	/** Ano de ingresso do discente. */
	private Integer anoIngresso;
	
	/** Período de ingresso do discente */
	private Integer periodoIngresso;

	/** Ano de conclusão do discente */
	private Integer anoSaida;
	
	/** Período de conclusão do discente */
	private Integer periodoSaida;
	
	/** Data de colação de grau do discente */
	private Date dataColacaoGrau;
	
	public int getIdDiscente() {
		return idDiscente;
	}

	public void setIdDiscente(int idDiscente) {
		this.idDiscente = idDiscente;
	}

	public Long getCpf() {
		return cpf;
	}

	public void setCpf(Long cpf) {
		this.cpf = cpf;
	}

	public String getMatricula() {
		return matricula;
	}

	public void setMatricula(String matricula) {
		this.matricula = matricula;
	}

	public String getPassaporte() {
		return passaporte;
	}

	public void setPassaporte(String passaporte) {
		this.passaporte = passaporte;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public Date getDataNascimento() {
		return dataNascimento;
	}

	public void setDataNascimento(Date dataNascimento) {
		this.dataNascimento = dataNascimento;
	}

	public CursoDTO getCurso() {
		return curso;
	}

	public void setCurso(CursoDTO curso) {
		this.curso = curso;
	}

	public Character getSexo() {
		return sexo;
	}

	public void setSexo(Character sexo) {
		this.sexo = sexo;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getNivel() {
		return nivel;
	}

	public void setNivel(String nivel) {
		this.nivel = nivel;
	}

	public Date getDataCadastro() {
		return dataCadastro;
	}

	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	public Date getDataAlteracaoStatus() {
		return dataAlteracaoStatus;
	}

	public void setDataAlteracaoStatus(Date dataAlteracaoStatus) {
		this.dataAlteracaoStatus = dataAlteracaoStatus;
	}

	public Date getUltimaAtualizacao() {
		return ultimaAtualizacao;
	}

	public void setUltimaAtualizacao(Date ultimaAtualizacao) {
		this.ultimaAtualizacao = ultimaAtualizacao;
	}

	public void setDataColacaoGrau(Date dataColacaoGrau) {
		this.dataColacaoGrau = dataColacaoGrau;
	}

	public Date getDataColacaoGrau() {
		return dataColacaoGrau;
	}

	public void setAnoIngresso(Integer anoIngresso) {
		this.anoIngresso = anoIngresso;
	}

	public Integer getAnoIngresso() {
		return anoIngresso;
	}

	public void setPeriodoIngresso(Integer periodoIngresso) {
		this.periodoIngresso = periodoIngresso;
	}

	public Integer getPeriodoIngresso() {
		return periodoIngresso;
	}

	public void setAnoSaida(Integer anoSaida) {
		this.anoSaida = anoSaida;
	}

	public Integer getAnoSaida() {
		return anoSaida;
	}

	public void setPeriodoSaida(Integer periodoSaida) {
		this.periodoSaida = periodoSaida;
	}

	public Integer getPeriodoSaida() {
		return periodoSaida;
	}
	
	public String getEntrada(){
		return periodoIngresso + "/" + anoIngresso;		
	}
	
	public String getSaida(){
		return periodoSaida + "/" + anoSaida;		
	}
	
	public String getPeriodoCurso(){
		return getEntrada() + " a " + getSaida();
	}
	
	public String getSituacao(){
		String situacao = "";
		if (status.equals(ATIVO)) {
			situacao = "ATIVO";
		}
		if (status.equals(CADASTRADO)) {
			situacao = "CADASTRADO";
		}
		if (status.equals(CONCLUIDO)) {
			situacao = "CONCLUIDO";
		}
		if (status.equals(AFASTADO)) {
			situacao = "AFASTADO";
		}
		if (status.equals(TRANCADO)) {
			situacao = "TRANCADO";
		}
		if (status.equals(CANCELADO)) {
			situacao = "CANCELADO";
		}
		
		if (status.equals(JUBILADO)) {
			situacao = "JUBILADO";
		}
		if (status.equals(FORMANDO)) {
			situacao = "FORMANDO";
		}
		if (status.equals(GRADUANDO)) {
			situacao = "GRADUANDO";
		}
		if (status.equals(EXCLUIDO)) {
			situacao = "EXCLUIDO";
		}
		if (status.equals(EM_HOMOLOGACAO)) {
			situacao = "EM_HOMOLOGACAO";
		}

		return situacao;
	}
	
}