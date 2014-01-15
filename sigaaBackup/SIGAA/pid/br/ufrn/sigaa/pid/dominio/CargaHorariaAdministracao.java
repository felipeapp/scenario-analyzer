/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Created on 29/10/2009
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
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.sigaa.rh.dominio.Designacao;

 
/**
 * Classe utilizada pelo Plano Individual do Docente - PID.
 * Representa a Carga Horária de Administração.
 * 
 * @author agostinho campos
 * 
 */

@Entity
@Table(name = "carga_horaria_administracao", schema = "pid")
public class CargaHorariaAdministracao implements PersistDB, Validatable {

	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
   	@Column(name = "id_carga_horaria_administracao")
	private int id;
	
	/**
	 * @NotFound(action=NotFoundAction.IGNORE)
	 * evita propagar erro de ObjectNotFoundException
	 * esse erro é tratado na aplicação e o registro que 
	 * faz referencia ao registro inexistente é removido do 
	 * banco.
	 * 
	 * Por exemplo quando uma turma é transferida para outro
	 * professor, é necessário remover essa turma do PID
	 */
	@NotFound(action=NotFoundAction.IGNORE)  
	@ManyToOne
	@JoinColumn(name="id_designacao")
	private Designacao designacao;
	
	@ManyToOne
	@JoinColumn(name="id_plano_individual_docente")
	private PlanoIndividualDocente planoIndividualDocente;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Designacao getDesignacao() {
		return designacao;
	}

	public void setDesignacao(Designacao designacao) {
		this.designacao = designacao;
	}

	public ListaMensagens validate() {
		return null;
	}

	public PlanoIndividualDocente getPlanoIndividualDocente() {
		return planoIndividualDocente;
	}

	public void setPlanoIndividualDocente(
			PlanoIndividualDocente planoIndividualDocente) {
		this.planoIndividualDocente = planoIndividualDocente;
	}

}
