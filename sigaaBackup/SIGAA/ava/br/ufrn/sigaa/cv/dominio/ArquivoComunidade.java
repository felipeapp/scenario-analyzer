/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas
 * Superintendência de Informática - UFRN
 * 
 * Created on 31 de Outubro de 2006, 09:40
 *
 */
package br.ufrn.sigaa.cv.dominio;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.sigaa.ava.dominio.ArquivoUsuario;
import br.ufrn.sigaa.dominio.Usuario;

/**
 * Arquivos submetidos pelo usuário em seu porta-arquivos
 * 
 * @author Gleydson
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "arquivo_comunidade", schema = "cv")
public class ArquivoComunidade extends MaterialComunidade implements Validatable {

	/** Chave primária */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator", parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_arquivo_comunidade", nullable = false)
	private int id;

	/** Tópico no qual o arquivo está associado */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_topico")
	private TopicoComunidade topico;

	/** Arquivo do usuário no qual o arquivo da comunidade está associado */
	@ManyToOne(fetch = FetchType.EAGER, cascade=CascadeType.REMOVE)
	@JoinColumn(name = "id_arquivo")
	private ArquivoUsuario arquivo;

	/** Nome do arquivo */
	private String nome;

	/** Descrição do arquivo */
	private String descricao;

	/** Usuário que cadastrou o arquivo */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_usuario")
	private Usuario usuarioCadastro;

	/** Data de cadastro do arquivo */
	private Date data;

	/** Se é para notificar os usuário da comunidade após o cadastro do arquivo */
	@Transient
	private boolean notificarMembros;

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public ArquivoComunidade() {
	}

	public ArquivoUsuario getArquivo() {
		return arquivo;
	}

	public void setArquivo(ArquivoUsuario arquivo) {
		this.arquivo = arquivo;
	}

	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public ListaMensagens validate() {
		return null;
	}

	public TopicoComunidade getTopico() {
		return topico;
	}

	public void setTopico(TopicoComunidade topico) {
		this.topico = topico;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	@Override
	public Date getDataCadastro() {
		return data;
	}

	public Usuario getUsuarioCadastro() {
		return usuarioCadastro;
	}

	public void setUsuarioCadastro(Usuario usuarioCadastro) {
		this.usuarioCadastro = usuarioCadastro;
	}

	public boolean isNotificarMembros() {
		return notificarMembros;
	}

	public void setNotificarMembros(boolean notificarMembros) {
		this.notificarMembros = notificarMembros;
	}

	@Override
	public String getTipoMaterial() {
		// TODO Auto-generated method stub
		return "Arquivo";
	}

}