/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas
 * Superintendência de Informática - UFRN
 */
package br.ufrn.sigaa.ava.dominio;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.seguranca.log.AtualizadoEm;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.seguranca.log.CriadoPor;
import br.ufrn.sigaa.ava.util.HumanName;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.dominio.Turma;

/**
 * Notícia da Turma Virtual.
 *
 */
@Entity
@Table(name = "noticia_turma", schema = "ava")
@HumanName(value="Notícia", genero='F')
public class NoticiaTurma implements DominioTurmaVirtual {

	/**Chave primária. */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator", parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_noticia_turma", nullable = false)
	private int id;

	/**Título da notícia. */
	private String descricao;

	/**Conteúdo da primária. */
	private String noticia;

	/**Turma na qual a notícia está associada. */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_turma")
	private Turma turma;

	/**Data em que a notícia foi criada. */
	@CriadoEm
	private Date data;
	
	/**
	 * Data da última atualização
	 */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_atualizacao")
	@AtualizadoEm
	private Date dataAtualizacao;
		
	/**Usuário que cadastrou a notícia. */
	@CriadoPor @ManyToOne(fetch = FetchType.EAGER) @JoinColumn(name="id_usuario_cadastro")
	private Usuario usuarioCadastro;

	/** Se a notícia foi enviada pelo e-mail. */
	private Boolean notificar;
	
	public Turma getTurma() {
		return turma;
	}

	public void setTurma(Turma turma) {
		this.turma = turma;
	}

	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getNoticia() {
		return noticia;
	}

	public void setNoticia(String noticia) {
		this.noticia = noticia;
	}

	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();

		if (descricao == null || "".equals(descricao.trim()))
			lista.addErro("Digite um título para a notícia");

		if (noticia == null || "".equals(noticia.trim()))
			lista.addErro("Digite algum texto para a notícia");

		return lista;
	}

	public Usuario getUsuarioCadastro() {
		return usuarioCadastro;
	}

	public void setUsuarioCadastro(Usuario usuarioCadastro) {
		this.usuarioCadastro = usuarioCadastro;
	}

	public String getMensagemAtividade() {
		return "Nova Notícia: " + descricao;
	}
	
	/**Quantidade de dias. */
	@Transient
	public int getQtdDias() {
		
		if ( dataAtualizacao != null )
			return (int) ( (System.currentTimeMillis() - dataAtualizacao.getTime())/ (1000*60*60*24)  );

		if ( data != null ) 
			return (int) ( (System.currentTimeMillis() - data.getTime())/ (1000*60*60*24)  );
		
		
		return 0;
	}

	public void setDataAtualizacao(Date dataAtualizacao) {
		this.dataAtualizacao = dataAtualizacao;
	}

	public Date getDataAtualizacao() {
		return dataAtualizacao;
	}

	public void setNotificar(Boolean notificar) {
		this.notificar = notificar;
	}

	public Boolean isNotificar() {
		return notificar;
	}
	
}
