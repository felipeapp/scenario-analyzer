package br.ufrn.sigaa.monitoria.dominio;

// Generated 09/10/2006 10:44:38 by Hibernate Tools 3.1.0.beta5

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;

/*******************************************************************************
 * <p>
 * Relaciona um docente com um componente curricular de um projeto de monitoria.
 * Torna o docente responsável por determinado componente curricular no projeto.
 * </p>
 * 
 * 
 * @author Ilueny Santos
 * 
 ******************************************************************************/
@Entity
@Table(name = "equipe_docente_componente", schema = "monitoria")
public class EquipeDocenteComponente implements Validatable {

	// Fields

	private int id;

	private Date dataVinculacao;

	private EquipeDocente equipeDocente = new EquipeDocente();

	private ComponenteCurricularMonitoria componenteCurricularMonitoria = new ComponenteCurricularMonitoria();
	
	private boolean ativo;

	// Constructors

	/** default constructor */
	public EquipeDocenteComponente() {
	}

	public EquipeDocenteComponente(int id) {
		this.id = id;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "id_equipe_docente_componente", unique = true, insertable = true, updatable = true)
	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "data_vinculacao", unique = false, insertable = true, updatable = true, length = 8)
	public Date getDataVinculacao() {
		return this.dataVinculacao;
	}

	public void setDataVinculacao(Date dataVinculacao) {
		this.dataVinculacao = dataVinculacao;
	}

	@ManyToOne(cascade = {}, fetch = FetchType.EAGER)
	@JoinColumn(name = "id_equipe_docente", unique = false, insertable = true, updatable = true)
	public EquipeDocente getEquipeDocente() {
		return this.equipeDocente;
	}

	public void setEquipeDocente(EquipeDocente equipeDocente) {
		this.equipeDocente = equipeDocente;
	}

	@ManyToOne(cascade = {}, fetch = FetchType.EAGER)
	@JoinColumn(name = "id_componente_curricular_monitoria", unique = false, insertable = true, updatable = true)
	public ComponenteCurricularMonitoria getComponenteCurricularMonitoria() {
		return componenteCurricularMonitoria;
	}

	public void setComponenteCurricularMonitoria(
			ComponenteCurricularMonitoria componenteCurricularMonitoria) {
		this.componenteCurricularMonitoria = componenteCurricularMonitoria;
	}

	public ListaMensagens validate() {
		return null;
	}

	@Column(name = "ativo")
	public boolean isAtivo() {
		return ativo;
	}
	@Column(name = "ativo")
	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

}
