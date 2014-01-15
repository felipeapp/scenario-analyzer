package br.ufrn.boletim.dominio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import br.ufrn.arq.dominio.PersistDB;

/**
 * Armazena templates de boletim para preencher de acordo com o tipo de ato
 * à ser publicado.
 * 
 *<br/><br/>
 *<b>DEPRECATED</b>. Substituído pela implementação mais genérica {@link br.ufrn.arq.templates.TemplateDocumento TemplateDocumento}
 * 
 * @author Cezar Miranda
 *
 */
@Entity
@Table(schema="comum", name="template_ato")
@Deprecated
public class TemplateAto implements PersistDB{

	public static final int AFASTAMENTO = 1;
	
	@Id
	@Column(name="id_template_ato")
	private int id;
	
	/**
	 * O template a ser preenchido
	 */
	@Column(name="texto")
	private String texto;
	
	/**
	 * Um exemplo de preenchimento
	 */
	@Column(name="exemplo")
	private String exemplo;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id=id;		
	}

	public String getTexto(Object... args) {
		return String.format(texto, args).replace("null", "<b>[PREENCHER]</b>").replace("\n", "<br/>");
	}
	
	public String getExemplo() {
		//return exemplo.replaceAll("\\[", "\\#{").replaceAll("\\]", "}");
		return exemplo;
	}
}
