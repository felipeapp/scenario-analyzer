/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 10/02/2010
 */
package br.ufrn.arq.templates;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.negocio.validacao.annotations.Required;

/**
 * Identifica os tipos de documento para os quais podem
 * ser criados templates.
 * @see TemplateDocumento
 * 
 * @author David Pereira
 * @author Gleydson Lima
 *
 */
@Entity @Table(name="tipo_documento_template", schema="comum")
public class TipoDocumentoTemplate implements PersistDB {

	@Id 
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator")
	private int id;
	
	@Required
	private String nome;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}
	
}