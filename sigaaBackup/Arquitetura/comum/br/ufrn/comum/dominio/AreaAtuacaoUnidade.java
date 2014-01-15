/*
 * Sistema Integrado de Patrimônio e Administração de Contratos
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 17/05/2007
 *
 */
package br.ufrn.comum.dominio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.GenericTipo;


/**
 * Área de atuação de uma unidade. Ex.: meio fim
 * 
 * @author Raphaela Galhardo
 * @author Gleydson Lima
 *
 */
@Entity
@Table(name = "area_atuacao_unidade", schema="comum")
public class AreaAtuacaoUnidade extends GenericTipo{

	@Id
	@Column(name = "id_area_atuacao_unidade")
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
           parameters={ @Parameter(name="sequence_name", value="tipo_seq") })
	/** Identificador */
	public int getId() {
		return super.getId();
	}
	
	@Column(name = "denominacao")
	/** Nome de área */
	public String getDenominacao(){
		return super.getDenominacao();
	}
	
}

