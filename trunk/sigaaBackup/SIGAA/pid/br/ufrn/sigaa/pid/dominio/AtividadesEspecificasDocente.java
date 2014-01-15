/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em 23/02/2010
 */
package br.ufrn.sigaa.pid.dominio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.PersistDB;

/**
 * Classe utilizada pelo Plano Individual do Docente - PID.
 * 
 * Representa as atividades "complementares" que o docente adicionada 
 * manualmente de acordo com suas necessidades.  
 *  
 * @author agostinho campos
 * 
 */
@Entity
@Table(name="atividades_especificas_docente", schema="pid")
public class AtividadesEspecificasDocente implements PersistDB {

	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
   	@Column(name = "id_atividades_especificas_docente")
	private int id;
	
	private String denominacao; 
	
	@ManyToOne
	@JoinColumn(name="id_plano_individual_docente")
	private PlanoIndividualDocente planoIndividualDocente;
	
	/**
	 * Ensino
	 * Pesquisa e Produção Técnico-Científica
	 * Extensão e outras atividades técnicas
	 * Administração
	 * Outras Atividades
	 * 
	 */
	@ManyToOne
	@JoinColumn(name="id_tipo_atividade")
	private TipoAtividadeComplementarPID tipoAtividadeComplementar;

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

	public PlanoIndividualDocente getPlanoIndividualDocente() {
		return planoIndividualDocente;
	}

	public void setPlanoIndividualDocente(
			PlanoIndividualDocente planoIndividualDocente) {
		this.planoIndividualDocente = planoIndividualDocente;
	}

	public TipoAtividadeComplementarPID getTipoAtividadeComplementar() {
		return tipoAtividadeComplementar;
	}

	public void setTipoAtividadeComplementar(
			TipoAtividadeComplementarPID tipoAtividadeComplementar) {
		this.tipoAtividadeComplementar = tipoAtividadeComplementar;
	}
	
}
