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
import javax.persistence.Transient;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.seguranca.log.CriadoPor;
import br.ufrn.sigaa.ava.util.HumanName;
import br.ufrn.sigaa.dominio.Usuario;

/**
 * Entidade que representa uma Notícia na Comunidade Virtual
 * 
 * @author Agostinho
 */
@Entity
@Table(name = "noticia_comunidade", schema = "cv")
@HumanName(value="Notícia", genero='F')
public class NoticiaComunidade implements DominioComunidadeVirtual {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "id", nullable = false)
	private int id;

	private String descricao;

	private String noticia;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_comunidade")
	private ComunidadeVirtual comunidade;

	@CriadoEm
	private Date data;
	
	@CriadoPor @ManyToOne @JoinColumn(name="id_usuario_cadastro")
	private Usuario usuarioCadastro;

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

	/**
	 * Exibe mensagem quando a notícia é cadastrada
	 */
	public String getMensagemAtividade() {
		return "Nova notícia cadastrada";
	}
	
	/**
	 * Quantidade de dias que a notícia está publicada
	 */
	@Transient
	public int getQtdDias() {
		if ( data != null ) {
			return (int) ( (System.currentTimeMillis() - data.getTime())/ (1000*60*60*24)  );
		}
		return 0;
	}

	public ComunidadeVirtual getComunidade() {
		return comunidade;
	}

	public void setComunidade(ComunidadeVirtual comunidade) {
		this.comunidade = comunidade;
	}
	
}
