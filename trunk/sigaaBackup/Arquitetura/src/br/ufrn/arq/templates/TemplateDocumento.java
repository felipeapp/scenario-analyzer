/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 19/11/2009
 */
package br.ufrn.arq.templates;

import java.io.Serializable;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import br.ufrn.arq.negocio.validacao.annotations.Required;

/**
 * Template para mensagens de e-mail. Contém textos
 * padrão para assunto e mensagem com variáveis que podem
 * ser substituídas por valores definidos pelos usuários.
 * 
 * @author David Pereira
 * @author Gleydson Lima
 *
 */
@Entity @Table(name="template_documento",schema="comum")
public class TemplateDocumento implements Serializable {
	
	private static final long serialVersionUID = -1L;

	/** Código de controle de template de email, atributo utilizado para localização de templates. */
	@Id
	@Required
	private String codigo;
	
	/** Atributo referente ao assunto do template de email. */
	@Required
	private String titulo;
	
	/** Atributo referente a mensagem do template de email. */
	@Required
	private String texto;
	
	@ManyToOne @JoinColumn(name="id_tipo_documento")
	private TipoDocumentoTemplate tipo = new TipoDocumentoTemplate();

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public String getTexto() {
		return texto;
	}

	public void setTexto(String texto) {
		this.texto = texto;
	}

	public TipoDocumentoTemplate getTipo() {
		return tipo;
	}

	public void setTipo(TipoDocumentoTemplate tipo) {
		this.tipo = tipo;
	}

	public void substituirVariaveis(Map<String, String> params) {
		if (params != null) {
			for (Entry<String, String> entry : params.entrySet()) {
				String key = entry.getKey();
				if (!key.startsWith("##")) key = "##" + key;
				if (!key.endsWith("##")) key = key + "##";
				
				texto = texto.replace(key, entry.getValue());
				titulo = titulo.replace(key, entry.getValue());
			}
		}
	}
	
}
