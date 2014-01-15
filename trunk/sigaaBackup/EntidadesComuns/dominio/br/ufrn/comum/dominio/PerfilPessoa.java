/* 
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: Arq_UFRN
 * Criado em: 2007/09/27
 */
package br.ufrn.comum.dominio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.StringUtils;

/**
 * Perfil de uma pessoa nos sistemas da UFRN.
 * 
 * @author David Pereira
 *
 */
@Entity
@Table(name="perfil_pessoa", schema="comum")
public class PerfilPessoa implements Validatable {
	/** Largura da Foto */
	public static final int WIDTH_FOTO = 100;
	/** Altura da Foto */
	public static final int HEIGTH_FOTO = 125;
	/** Quantidade Máxima de Caracter */
	public static final int QTD_MAX_CARACTER = 150;
	/** Chave Primária */
	@Id
	@Column(name = "id_perfil", nullable = false)
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator")
	private int id;

	/** Descrição da pessoa no perfil */
	private String descricao;

	/** Formação da pessoa */
	private String formacao;

	/** Áreas de interesse */
	private String areas;

	/** Sala de trabalho na UFRN */
	private String sala;

	/** Endereço profissional na UFRN */
	private String endereco;
	
	/** Telefone da pessoa */
	private String telefone;
	
	/** Assinatura da pessoa (para caixa postal e chamados). */
	private String assinatura;
	
	/** URL para o currículo do servidor na Plataforma Lattes */
	@Column(name = "endereco_lattes")
	private String enderecoLattes;
	
	/** Servidor dono do perfil */
	@Column(name = "id_servidor")
	private Integer idServidor;
	
	/** Docente externo dono do perfil */
	@Column(name = "id_docente_externo")
	private Integer idDocenteExterno;
	
	/** Discente dono do perfil */
	@Column(name="id_discente")
	private Integer idDiscente;
	
	/** Tutor dono do perfil */
	@Column(name="id_tutor")
	private Integer idTutor;
	
	/** Coordenador de pólo dono do perfil */
	@Column(name="id_coord_polo")
	private Integer idCoordPolo;
	
	/** Pessoa dona do perfil */
	@Column(name = "id_pessoa")
	private Integer idPessoa;
	
	/** Indica se deve ou não ocultar o email na parte pública */
	@Column(name = "ocultar_email")
	private boolean ocultarEmail;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public String getFormacao() {
		return formacao;
	}
	
	public String getFormacaoResumida() {
		return  StringUtils.limitTxt(formacao, QTD_MAX_CARACTER);
	}

	public void setFormacao(String formacao) {
		this.formacao = formacao;
	}

	public String getAreas() {
		return areas;
	}

	public void setAreas(String areas) {
		this.areas = areas;
	}

	public String getSala() {
		return sala;
	}

	public void setSala(String sala) {
		this.sala = sala;
	}

	public String getEndereco() {
		return endereco;
	}

	public void setEndereco(String endereco) {
		this.endereco = endereco;
	}

	public String getEnderecoLattes() {
		return enderecoLattes;
	}

	public void setEnderecoLattes(String enderecoLattes) {
		this.enderecoLattes = enderecoLattes;
	}

	public Integer getIdServidor() {
		return idServidor;
	}

	public void setIdServidor(Integer idServidor) {
		this.idServidor = idServidor;
	}

	public Integer getIdDiscente() {
		return idDiscente;
	}

	public void setIdDiscente(Integer idDiscente) {
		this.idDiscente = idDiscente;
	}

	public Integer getIdTutor() {
		return idTutor;
	}

	public void setIdTutor(Integer idTutor) {
		this.idTutor = idTutor;
	}

	public ListaMensagens validate() {
		return null;
	}

	public String getTelefone() {
		return telefone;
	}

	public void setTelefone(String telefone) {
		this.telefone = telefone;
	}

	public Integer getIdPessoa() {
		return idPessoa;
	}

	public void setIdPessoa(Integer idPessoa) {
		this.idPessoa = idPessoa;
	}

	public Integer getIdDocenteExterno() {
		return idDocenteExterno;
	}

	public void setIdDocenteExterno(Integer idDocenteExterno) {
		this.idDocenteExterno = idDocenteExterno;
	}

	public Integer getIdCoordPolo() {
		return idCoordPolo;
	}

	public void setIdCoordPolo(Integer idCoordPolo) {
		this.idCoordPolo = idCoordPolo;
	}

	public String getAssinatura() {
		return assinatura;
	}

	public void setAssinatura(String assinatura) {
		this.assinatura = assinatura;
	}

	public boolean isOcultarEmail() {
		return ocultarEmail;
	}

	public void setOcultarEmail(boolean ocultarEmail) {
		this.ocultarEmail = ocultarEmail;
	}	
}
