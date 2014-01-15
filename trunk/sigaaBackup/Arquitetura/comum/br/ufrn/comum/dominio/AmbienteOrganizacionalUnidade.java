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
 * Ambiente organizacional de uma unidade. Ex.: administrato, saúde, informação, etc.
 * 
 * @author Raphaela Galhardo
 * @author Gleydson Lima
 *
 */
@Entity
@Table(name = "ambiente_org_unidade", schema="comum")
public class AmbienteOrganizacionalUnidade extends GenericTipo{

	@Id
	@Column(name = "id")
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	/** Identificador */
	public int getId() {
		return super.getId();
	}

	@Column( name = "denominacao")
	/** Nome do ambiente */
	public String getDenominacao() {
		return super.getDenominacao();
	}
}

