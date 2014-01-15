/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas
 * Superintendência de Informática - UFRN
 */
package br.ufrn.sigaa.cv.dominio;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.seguranca.log.CriadoPor;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.sigaa.ava.util.HumanName;
import br.ufrn.sigaa.dominio.Usuario;

/**
 * Classe que representa um conteúdo submetido pelo professor para o uso dos
 * alunos na turma virtual.
 * 
 * @author David Pereira
 * 
 */
@Entity
@HumanName(value = "Conteúdo", genero = 'M')
@Table(name = "conteudo", schema = "cv")
public class ConteudoComunidade extends MaterialComunidade implements DominioComunidadeVirtual {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "id_conteudo")
	private int id;

	private boolean ativo = true;

	private String titulo;
	private String conteudo;

	@CriadoPor
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_usuario_cadastro")
	private Usuario usuarioCadastro;

	@CriadoEm
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_cadastro")
	private Date dataCadastro;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_topico_comunidade")
	private TopicoComunidade topico = new TopicoComunidade();
	
	private boolean permanente;

	/**
	 * @return the conteudo
	 */
	public String getConteudo() {
		return conteudo;
	}

	/**
	 * @param conteudo
	 *            the conteudo to set
	 */
	public void setConteudo(String conteudo) {
		this.conteudo = conteudo;
	}

	/**
	 * @return the dataCadastro
	 */
	@Override
	public Date getDataCadastro() {
		return dataCadastro;
	}

	/**
	 * @param dataCadastro
	 *            the dataCadastro to set
	 */
	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	public Boolean getAtivo() {
		return this.ativo;
	}

	public void setAtivo(Boolean ativo) {
		this.ativo = ativo;
	}

	/**
	 * @return the titulo
	 */
	public String getTitulo() {
		return titulo;
	}

	/**
	 * @param titulo
	 *            the titulo to set
	 */
	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	/**
	 * @return the usuarioCadastro
	 */
	@Override
	public Usuario getUsuarioCadastro() {
		return usuarioCadastro;
	}

	/**
	 * @param usuarioCadastro
	 *            the usuarioCadastro to set
	 */
	public void setUsuarioCadastro(Usuario usuarioCadastro) {
		this.usuarioCadastro = usuarioCadastro;
	}

	@Override
	public String getNome() {
		return titulo;
	}
	
	/**
	 * Validação dos campos do próprio objeto
	 */
	public ListaMensagens validate() {

		ListaMensagens lista = new ListaMensagens();

		if (StringUtils.isEmpty(titulo))
			lista.addErro("Digite um título para o conteúdo.");

		if (StringUtils.isEmpty(conteudo))
			lista.addErro("O conteúdo é obrigatório.");

		if (topico == null || topico.getId() == 0)
			lista.addErro("Escolha um tópico de aula.");

		return lista;
	}

	public String getMensagemAtividade() {
		return "Novo conteúdo adicionado";
	}

	public TopicoComunidade getTopico() {
		return topico;
	}

	public void setTopico(TopicoComunidade topico) {
		this.topico = topico;
	}

	public ComunidadeVirtual getComunidade() {
		return null;
	}

	public void setComunidade(ComunidadeVirtual comunidade) {
	}

	public void setPermanente(boolean permanente) {
		this.permanente = permanente;
	}

	public boolean isPermanente() {
		return permanente;
	}
	
	@Override
	public String getTipoMaterial() {
		// TODO Auto-generated method stub
		return "Conteúdo";
	}

}
