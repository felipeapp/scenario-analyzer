/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criada em: 30/10/2009
 *
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
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;

/**
 * Classe utilizada pelo Plano Individual do Docente - PID.
 * 
 * Representa as atividades complementares que são adicionadas 
 * pelo docente através de uma lista disponibilizada ao mesmo.
 *  
 * @author agostinho campos
 * 
 */

@Entity
@Table(name = "ch_atividades_complementares", schema = "pid")
public class CargaHorariaAtividadesComplementares implements PersistDB, Validatable {

	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_ch_atividades_complementares")
	private int id;
	
	@ManyToOne
	@JoinColumn(name="id_atividade_complementar")
	private AtividadeComplementarPID atividadeComplementarPID;

	@ManyToOne
	@JoinColumn(name="id_plano_individual_docente")
	private PlanoIndividualDocente planoIndividualDocente;
	
	@Column(name="observacao")
	private String observacao;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public PlanoIndividualDocente getPlanoIndividualDocente() {
		return planoIndividualDocente;
	}

	public void setPlanoIndividualDocente(
			PlanoIndividualDocente planoIndividualDocente) {
		this.planoIndividualDocente = planoIndividualDocente;
	}

	public AtividadeComplementarPID getAtividadeComplementarPID() {
		return atividadeComplementarPID;
	}

	public void setAtividadeComplementarPID(
			AtividadeComplementarPID atividadeComplementarPID) {
		this.atividadeComplementarPID = atividadeComplementarPID;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((atividadeComplementarPID == null) ? 0
						: atividadeComplementarPID.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CargaHorariaAtividadesComplementares other = (CargaHorariaAtividadesComplementares) obj;
		if (atividadeComplementarPID == null) {
			if (other.atividadeComplementarPID != null)
				return false;
		} else if (!atividadeComplementarPID
				.equals(other.atividadeComplementarPID))
			return false;
		return true;
	}

	/**
	 * Valida o preenchimento de alguns campos da classe 
	 */
	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();
		return lista;
	}

	public String getObservacao() {
		return observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}
	
}
