/* 
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 * Criado em: 06/03/2008
 */
package br.ufrn.sigaa.ensino.stricto.dominio;

import java.util.Collection;

import javax.persistence.Column;

/**
 * Informações sobre a participação de um docente em
 * uma banca 
 * @author David Pereira
 */
public class InformacoesParticipacao {
	/** Unidade do programa */
	private String unidade;
	/** Programa de Pós-graduação */
	private String programa;
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
	private Collection<MembroBancaPos> membros;
	/** Sexo do participante */
	private boolean sexo;
	/** Identificador do registro de pessoa do participante. */
	private int registroPessoa;

	@Column(name="id_unidade")
	public String getUnidade() {
		return unidade;
	}

	public void setUnidade(String unidade) {
		this.unidade = unidade;
	}

	public String getPrograma() {
		return programa;
	}

	public void setPrograma(String programa) {
		this.programa = programa;
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

	public Collection<MembroBancaPos> getMembros() {
		return membros;
	}

	public void setMembros(Collection<MembroBancaPos> membros) {
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
