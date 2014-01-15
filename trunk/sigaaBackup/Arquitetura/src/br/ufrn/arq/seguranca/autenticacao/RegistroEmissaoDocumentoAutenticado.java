/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 17/12/2008
 */
package br.ufrn.arq.seguranca.autenticacao;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.comum.dominio.UsuarioGeral;

/**
 * Representa a emissão ou re-emissão de um 
 * documento autenticado, como o histórico de um discente,
 * um atestado de matrícula, etc.
 * 
 * @author Gleydson Lima
 * 
 */
@Entity
@Table(name = "emissao_doc_auten_registro", schema="comum")
public class RegistroEmissaoDocumentoAutenticado implements PersistDB{

	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator")
	private int id;

	/** Informações sobre a emissão do documento */
	@ManyToOne
	@JoinColumn(name = "id_emissao")
	private EmissaoDocumentoAutenticado emissao;

	/** Usuário que gerou o documento */
	@ManyToOne(targetEntity = UsuarioGeral.class)
	@JoinColumn(name = "id_usuario")
	private UsuarioGeral usuario;

	/** Registro de entrada de quem emitiu o documento, caso se esteja na área interna do sistema */
	@Column(name = "id_registro_entrada")
	private Integer idRegistroEntrada;
	
	/** Registro de acesso público de quem emitiu o documento, caso se esteja na área pública */
	@Column(name = "id_registro_acesso_publico")
	private Integer idRegistroAcessoPublico;

	/** Data da emissão do documento */
	private Date data = new Date();

	/** Sistema através do qual o documento foi emitido */
	private int sistema;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public EmissaoDocumentoAutenticado getEmissao() {
		return emissao;
	}

	public void setEmissao(EmissaoDocumentoAutenticado emissao) {
		this.emissao = emissao;
	}

	public UsuarioGeral getUsuario() {
		return usuario;
	}

	public void setUsuario(UsuarioGeral usuario) {
		this.usuario = usuario;
	}

	public Integer getIdRegistroEntrada() {
		return idRegistroEntrada;
	}

	public void setIdRegistroEntrada(Integer idRegistroEntrada) {
		this.idRegistroEntrada = idRegistroEntrada;
	}

	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
	}

	public int getSistema() {
		return sistema;
	}

	public void setSistema(int sistema) {
		this.sistema = sistema;
	}

	public Integer getIdRegistroAcessoPublico() {
		return idRegistroAcessoPublico;
	}

	public void setIdRegistroAcessoPublico(Integer idRegistroAcessoPublico) {
		this.idRegistroAcessoPublico = idRegistroAcessoPublico;
	}
	
}