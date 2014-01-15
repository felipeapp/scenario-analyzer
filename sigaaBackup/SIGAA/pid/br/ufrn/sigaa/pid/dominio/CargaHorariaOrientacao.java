/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criada em: 29/10/2009
 *
 */


package br.ufrn.sigaa.pid.dominio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
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
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.pessoa.dominio.Discente;

/**
 * Classe utilizada pelo Plano Individual do Docente - PID.
 * Representa a CH de orientação cadastrada pelo Docente.
 *  
 * @author agostinho campos
 * 
 */

@Entity
@Table(name = "carga_horaria_orientacao", schema = "pid")
public class CargaHorariaOrientacao implements PersistDB, Validatable {

	/** Chave Primária. */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_carga_horaria_orientacao")
	private int id;

	/** Tipo de Orientação. */
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="id_tipo_orientacao")
	private TipoOrientacaoPID tipoOrientacao = new TipoOrientacaoPID();
	
	/** Discente de pós graduação orientado. */
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="id_discente")
	private Discente discente;
	
	/** Matrícula no Componente Curricular da orientação de atividade. */
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="id_matricula_componente")
	private MatriculaComponente matriculaComponente;
	
	/** Carga Horária dedicada pelo docente à orientação da atividade. */
	@Column(name="ch_dedicada")
	private int chDedicada;
	
	/** Plano individual do docente. */
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="id_plano_individual_docente")
	private PlanoIndividualDocente planoIndividualDocente;
	
	public CargaHorariaOrientacao() {
		super();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public TipoOrientacaoPID getTipoOrientacao() {
		return tipoOrientacao;
	}

	public void setTipoOrientacao(TipoOrientacaoPID tipoOrientacao) {
		this.tipoOrientacao = tipoOrientacao;
	}

	public Discente getDiscente() {
		return discente;
	}

	public void setDiscente(Discente discente) {
		this.discente = discente;
	}
	
	public PlanoIndividualDocente getPlanoIndividualDocente() {
		return planoIndividualDocente;
	}

	public void setPlanoIndividualDocente(
			PlanoIndividualDocente planoIndividualDocente) {
		this.planoIndividualDocente = planoIndividualDocente;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((getDiscente() == null) ? 0 : getDiscente().hashCode());
		result = prime * result
				+ ((tipoOrientacao == null) ? 0 : tipoOrientacao.hashCode());
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
		CargaHorariaOrientacao other = (CargaHorariaOrientacao) obj;
		if (getDiscente() == null) {
			if (other.getDiscente() != null)
				return false;
		} else if (!getDiscente().equals(other.getDiscente()))
			return false;
		if (tipoOrientacao == null) {
			if (other.tipoOrientacao != null)
				return false;
		} else if (!tipoOrientacao.equals(other.tipoOrientacao))
			return false;
		return true;
	}
	
	/**
	 * Valida o preenchimento de alguns campos da classe 
	 */
	public ListaMensagens validate() {
		return null;
	}

	public MatriculaComponente getMatriculaComponente() {
		return matriculaComponente;
	}

	public void setMatriculaComponente(MatriculaComponente matriculaComponente) {
		this.matriculaComponente = matriculaComponente;
	}

	public int getChDedicada() {
		return chDedicada;
	}
	
	public double getChDedicadaSemanal() {
		return new Double( Math.round((double) chDedicada / 45 * 100d) / 100d); 
	}

	public void setChDedicada(int chDedicada) {
		this.chDedicada = chDedicada;
	}
	
}