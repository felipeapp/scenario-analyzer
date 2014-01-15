/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas - SIGAA
 * Superintendência de Informática - UFRN
 *
 * Created on '14/02/2007'
 *
 */
package br.ufrn.sigaa.dominio;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import br.ufrn.arq.dominio.PersistDB;

/**
 * Classe para guardar informações digitadas pelos
 * discentes durante o auto cadastro de usuários
 * @author David Pereira
 */
@Entity
@Table(schema="comum", name="auto_cadastro_discente")
public class AutoCadastroDiscente implements PersistDB {

	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE)
	private int id;
	
	/** nome do discente */
	private String nome;
	
	/** CPF do discente */
	private Long cpf;
	
	/**
	 * Passaporte do aluno estrangeiro
	 */
	private String passaporte;
	
	/** identidade do discente */
	private String identidade;

	/** data de nascimento do discente */
	@Column(name="data_nascimento")
	private Date dataNascimento;
	
	/** email do discente */
	private String email;
	
	/** matricula  do discente */
	private Long matricula;
	
	/** ano de ingresso  do discente */
	@Column(name="ano_ingresso")
	private int anoIngresso;
	
	/** período de ingresso  do discente */
	@Column(name="periodo_ingresso")
	private int periodoIngresso;
	
	/** login do discente */
	private String login;
	
	/** data do cadastro */
	@Column(name="data_hora")
	private Date dataHora;
	
	/** IP da máquina utilizada pelo discente */
	private String ip;
	
	/** nível do discente (G - Graduação, D - Doutorado, etc) */
	private char nivel;

	/**
	 * @return the anoIngresso
	 */
	public int getAnoIngresso() {
		return anoIngresso;
	}

	/**
	 * @param anoIngresso the anoIngresso to set
	 */
	public void setAnoIngresso(int anoIngresso) {
		this.anoIngresso = anoIngresso;
	}

	/**
	 * @return the cpf
	 */
	public Long getCpf() {
		return cpf;
	}

	/**
	 * @param cpf the cpf to set
	 */
	public void setCpf(Long cpf) {
		this.cpf = cpf;
	}

	public String getPassaporte() {
		return passaporte;
	}

	public void setPassaporte(String passaporte) {
		this.passaporte = passaporte;
	}

	/**
	 * @return the dataNascimento
	 */
	public Date getDataNascimento() {
		return dataNascimento;
	}

	/**
	 * @param dataNascimento the dataNascimento to set
	 */
	public void setDataNascimento(Date dataNascimento) {
		this.dataNascimento = dataNascimento;
	}

	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * @param email the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return the identidade
	 */
	public String getIdentidade() {
		return identidade;
	}

	/**
	 * @param identidade the identidade to set
	 */
	public void setIdentidade(String identidade) {
		this.identidade = identidade;
	}

	/**
	 * @return the login
	 */
	public String getLogin() {
		return login;
	}

	/**
	 * @param login the login to set
	 */
	public void setLogin(String login) {
		this.login = login;
	}

	/**
	 * @return the matricula
	 */
	public Long getMatricula() {
		return matricula;
	}

	/**
	 * @param matricula the matricula to set
	 */
	public void setMatricula(Long matricula) {
		this.matricula = matricula;
	}

	/**
	 * @return the nome
	 */
	public String getNome() {
		return nome;
	}

	/**
	 * @param nome the nome to set
	 */
	public void setNome(String nome) {
		this.nome = nome;
	}

	/**
	 * @return the periodoIngresso
	 */
	public int getPeriodoIngresso() {
		return periodoIngresso;
	}

	/**
	 * @param periodoIngresso the periodoIngresso to set
	 */
	public void setPeriodoIngresso(int periodoIngresso) {
		this.periodoIngresso = periodoIngresso;
	}

	/**
	 * @return the dataHora
	 */
	public Date getDataHora() {
		return dataHora;
	}

	/**
	 * @param dataHora the dataHora to set
	 */
	public void setDataHora(Date dataHora) {
		this.dataHora = dataHora;
	}

	/**
	 * @return the ip
	 */
	public String getIp() {
		return ip;
	}

	/**
	 * @param ip the ip to set
	 */
	public void setIp(String ip) {
		this.ip = ip;
	}

	/**
	 * @return the nivel
	 */
	public char getNivel() {
		return nivel;
	}

	/**
	 * @param nivel the nivel to set
	 */
	public void setNivel(char nivel) {
		this.nivel = nivel;
	}
	
}
