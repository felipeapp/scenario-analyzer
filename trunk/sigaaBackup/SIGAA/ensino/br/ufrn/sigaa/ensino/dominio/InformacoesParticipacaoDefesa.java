/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 07/11/2011
 *
 */
package br.ufrn.sigaa.ensino.dominio;

import java.util.Collection;

/**
 * Informações sobre a participação de um docente em
 * uma banca 
 * @author Arlindo Rodrigues
 */
public class InformacoesParticipacaoDefesa {
	
	/** Unidade  */
	private String unidade;
	/** Curso */
	private String curso;
	/** Nome do docente participante da banca */
	private String participante;
	/** cpf ou passaporte do participante */
	private String cpfPassaporte;
	/** Tipo da participação da banca */
	private String tipoParticipacao;
	/** Nome do discente da banca */
	private String discente;
	/** Título do trabalho */
	private String titulo;
	/** Data da banca */
	private String dataBanca;
	/** Data de emissão da declaração */
	private String dataEmissao;
	/** Nome do coordenador do programa */
	private String coordenador;
	/** Tipo da banca */
	private String tipoBanca;
	/** Nível da banca (Mestrado ou Doutorado) */
	private String nivelBanca;
	/** Membros participantes da banca */
	private Collection<MembroBanca> membros;
	/** Sexo do participante */
	private boolean sexo;
	/** Identificador do registro de pessoa do participante. */
	private int registroPessoa;

	public String getUnidade() {
		return unidade;
	}

	public void setUnidade(String unidade) {
		this.unidade = unidade;
	}

	public String getCurso() {
		return curso;
	}

	public void setCurso(String curso) {
		this.curso = curso;
	}

	public String getParticipante() {
		return participante;
	}

	public void setParticipante(String participante) {
		this.participante = participante;
	}

	public String getDiscente() {
		return discente;
	}

	public void setDiscente(String discente) {
		this.discente = discente;
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public String getDataBanca() {
		return dataBanca;
	}

	public void setDataBanca(String dataBanca) {
		this.dataBanca = dataBanca;
	}

	public String getDataEmissao() {
		return dataEmissao;
	}

	public void setDataEmissao(String dataEmissao) {
		this.dataEmissao = dataEmissao;
	}

	public String getCoordenador() {
		return coordenador;
	}

	public void setCoordenador(String coordenador) {
		this.coordenador = coordenador;
	}

	public Collection<MembroBanca> getMembros() {
		return membros;
	}

	public void setMembros(Collection<MembroBanca> membros) {
		this.membros = membros;
	}

	public String getTipoParticipacao() {
		return tipoParticipacao;
	}

	public void setTipoParticipacao(String tipoParticipacao) {
		this.tipoParticipacao = tipoParticipacao;
	}

	public String getTipoBanca() {
		return tipoBanca;
	}

	public void setTipoBanca(String tipoBanca) {
		this.tipoBanca = tipoBanca;
	}

	public boolean getSexo() {
		return sexo;
	}

	public void setSexo(boolean sexo) {
		this.sexo = sexo;
	}

	public String getNivelBanca() {
		return nivelBanca;
	}

	public void setNivelBanca(String nivelBanca) {
		this.nivelBanca = nivelBanca;
	}

	public String getCpfPassaporte() {
		return cpfPassaporte;
	}

	public void setCpfPassaporte(String cpfPassaporte) {
		this.cpfPassaporte = cpfPassaporte;
	}

	public int getRegistroPessoa() {
		return registroPessoa;
	}

	public void setRegistroPessoa(int registroPessoa) {
		this.registroPessoa = registroPessoa;
	}

}
