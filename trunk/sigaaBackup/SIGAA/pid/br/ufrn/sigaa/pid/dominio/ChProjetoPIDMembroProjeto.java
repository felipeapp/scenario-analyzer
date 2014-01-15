/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em 03/03/2010
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
import br.ufrn.sigaa.projetos.dominio.MembroProjeto;

/**
 * Entidade que relaciona a CHProjeto do PID com MembroProjeto, 
 * pois o PID do Docente pode ter vários projetos.
 * 
 * @author agostinho campos
 *
 */
@Entity
@Table(name = "ch_projeto_pid_membro_projeto", schema = "pid")
public class ChProjetoPIDMembroProjeto implements PersistDB, Validatable {
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
   	@Column(name = "id_ch_projeto_pid_membro_projeto")
	private int id;
	
	/**
	 * @NotFound(action=NotFoundAction.IGNORE)
	 * evita propagar erro de ObjectNotFoundException
	 * para a view do usuário. esse erro é tratado na 
	 * aplicação e o registro que faz referencia ao 
	 * registro inexistente é removido do banco.
	 * 
	 * Por exemplo quando projetos são removidos do docente
	 * ocorre um ObjectNotFoundException
	 */
	@NotFound(action=NotFoundAction.IGNORE)  
	@ManyToOne
	@JoinColumn(name="id_membro_projeto")
	private MembroProjeto membroProjeto;
	
	@ManyToOne
	@JoinColumn(name="id_carga_horaria_projeto")
	private CargaHorariaProjeto cargaHorariaProjeto;
	
	public MembroProjeto getMembroProjeto() {
		return membroProjeto;
	}

	public void setMembroProjeto(MembroProjeto membroProjeto) {
		this.membroProjeto = membroProjeto;
	}

	public CargaHorariaProjeto getCargaHorariaProjeto() {
		return cargaHorariaProjeto;
	}

	public void setCargaHorariaProjeto(CargaHorariaProjeto cargaHorariaProjeto) {
		this.cargaHorariaProjeto = cargaHorariaProjeto;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public ListaMensagens validate() {
		return null;
	}

}
