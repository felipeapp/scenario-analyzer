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

import org.apache.commons.validator.UrlValidator;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.seguranca.log.CriadoPor;
import br.ufrn.sigaa.ava.util.HumanName;
import br.ufrn.sigaa.dominio.Usuario;

/**
 * Recomendação de material, pode ser referencia bibliográfica ou site na
 * internet.
 * 
 * @author Gleydson
 */
@Entity
@Table(name = "indicacao_referencia", schema = "cv")
@HumanName(value = "Referência", genero = 'F')
public class IndicacaoReferenciaComunidade extends MaterialComunidade implements DominioComunidadeVirtual {

	@Id
	@Column(name = "id_indicacao_referencia", nullable = false)
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private int id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_comunidade")
	private ComunidadeVirtual comunidade;

	private String url;
	private String titulo;
	private String descricao;

	private char tipo; // Artigo - A; Livro - L; Revista - R; Site - S; Outros

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_topico_comunidade")
	private TopicoComunidade topico = new TopicoComunidade();

	@Column(name = "data_cadastro")
	@CriadoEm
	private Date dataCadastro;

	@ManyToOne
	@JoinColumn(name = "id_usuario")
	@CriadoPor
	private Usuario usuarioCadastro;

	@Transient
	public boolean isSite() {
		return tipo == 'S';
	}

	public IndicacaoReferenciaComunidade() {
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUrl() {

		if (url != null && !url.startsWith("http://")) {
			return "http://" + url;
		} else {
			return url;
		}
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public TopicoComunidade getTopico() {
		return topico;
	}

	public void setTopico(TopicoComunidade topico) {
		this.topico = topico;
	}

	/**
	 * Valida os campos do próprio objeto
	 * @return
	 */
	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();

		if (titulo == null || "".equals(titulo.trim()))
			lista.addErro("Digite o nome/título da referência.");

		if (tipo == '-')
			lista.addErro("Selecione um tipo de referência.");

		if (tipo == 'S') {
			UrlValidator val = new UrlValidator();
			if (!val.isValid(url)) {
				lista.addErro("A url digitada não é válida!");
			}
		}

		return lista;
	}

	public char getTipo() {
		return tipo;
	}

	public void setTipo(char tipo) {
		this.tipo = tipo;
	}

	@Override
	public Date getDataCadastro() {
		return dataCadastro;
	}

	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	@Override
	public Usuario getUsuarioCadastro() {
		return usuarioCadastro;
	}

	public void setUsuarioCadastro(Usuario usuarioCadastro) {
		this.usuarioCadastro = usuarioCadastro;
	}

	@Override
	public String getNome() {
		return titulo;
	}

	/**
	 * Retorna uma descrição legível de acordo com o tipo
	 * @return
	 */
	public String getTipoDesc() {
		switch (tipo) {
		case 'A':
			return "ARTIGO";
		case 'L':
			return "LIVRO";
		case 'R':
			return "REVISTA";
		case 'S':
			return "SITE";
		default:
			return "OUTROS";
		}
	}

	/**
	 * Retorna uma mensagem sobre o tipo de referência
	 */
	public String getMensagemAtividade() {
		return "Indicação de um " + getTipoDesc() + " como referência";
	}

	public ComunidadeVirtual getComunidade() {
		return comunidade;
	}

	public void setComunidade(ComunidadeVirtual comunidade) {
		this.comunidade = comunidade;
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	
	@Override
	public String getTipoMaterial() {
		// TODO Auto-generated method stub
		return "Referência";
	}

}