/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 04/05/2009
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
import br.ufrn.arq.util.ValidatorUtil;

/**
 * Classe para armazenar informações de tipos de material
 * de treinamento (manuais, video-aulas, etc.)
 * 
 * @author David Pereira
 * @author Gleydson Lima
 *
 */
@Entity
@Table(name="tipo_material_treinamento",schema="comum") 
public class TipoMaterialTreinamento implements Validatable{
	
	/** Identificador do tipo de material */
	@Id
	@Column(name="id_tipo_material")
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator")
	private int id;
	
	/** Nome do tipo de Material */
	@Column(name="denominacao")
	private String denominacao;

	public TipoMaterialTreinamento(){
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getDenominacao() {
		return denominacao;
	}

	public void setDenominacao(String denominacao) {
		this.denominacao = denominacao;
	}

	public ListaMensagens validate() {
		
		ListaMensagens lista = new ListaMensagens();
		ValidatorUtil.validateRequired(getDenominacao(), "Nome", lista);
		return lista;
	}

}