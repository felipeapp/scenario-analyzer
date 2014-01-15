/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Created on 06/12/2006
 *
 */
package br.ufrn.sigaa.ensino.latosensu.dominio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.HashCodeUtil;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.pessoa.dominio.DocenteExterno;
import br.ufrn.sigaa.pessoa.dominio.Servidor;

/**
 * Classe que mapeia a tabela que relaciona disciplinas aos docentes
 * que as ministram e suas respectivas cargas hor�rias no momento
 * da proposta, na cria��o do curso poder� ser outro.
 *
 * @author Leonardo
 *
 */
@Entity
@Table(name = "equipe_lato", schema = "lato_sensu", uniqueConstraints = { })
public class CorpoDocenteDisciplinaLato implements PersistDB{

	//	 Fields

	/** Chave prim�ria */
	private int id;
	
	/** Docente do corpo docente da disciplina */
	private Servidor docente = new Servidor();

	/** Docente Externo do corpo docente da disciplina */
	private DocenteExterno docenteExterno = new DocenteExterno();

	/** Disciplina do corpo docente da disciplina */
	private ComponenteCurricular disciplina;

	/** Proposta do corpo docente da disciplina */
	private PropostaCursoLato proposta;

	/** Carga Hor�ria do corpo docente da disciplina */
	private Integer cargaHoraria;
	
	/** Nome do docente do corpo docente da disciplina */
	private String nomeDocente;

	// Constructors

	/** default constructor */
	public CorpoDocenteDisciplinaLato() {
	}

	/** minimal constructor */
	public CorpoDocenteDisciplinaLato(int idEquipe) {
		this.id = idEquipe;
	}

	/** full constructor */
	public CorpoDocenteDisciplinaLato(int idEquipe, Servidor docente,DocenteExterno docenteExterno,
			ComponenteCurricular disciplina, PropostaCursoLato proposta, Integer cargaHoraria) {
		this.id = idEquipe;
		this.docente = docente;
		this.docenteExterno = docenteExterno;
		this.disciplina = disciplina;
		this.proposta = proposta;
		this.cargaHoraria = cargaHoraria;
	}

	// Property accessors
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator")
	@Column(name = "id_equipe_lato", unique = true, nullable = false, insertable = true, updatable = true)
	public int getId() {
		return this.id;
	}

	public void setId(int idEquipe) {
		this.id = idEquipe;
	}

	@ManyToOne(cascade = { }, fetch = FetchType.EAGER)
	@JoinColumn(name = "id_disciplina", unique = false, nullable = true, insertable = true, updatable = true)
	public ComponenteCurricular getDisciplina() {
		return disciplina;
	}

	public void setDisciplina(ComponenteCurricular disciplina) {
		this.disciplina = disciplina;
	}

	@ManyToOne(cascade = { }, fetch = FetchType.EAGER)
	@JoinColumn(name = "id_servidor", unique = false, nullable = true, insertable = true, updatable = true)
	public Servidor getDocente() {
		return docente;
	}

	public void setDocente(Servidor docente) {
		this.docente = docente;
	}

	@ManyToOne(cascade = { }, fetch = FetchType.EAGER)
	@JoinColumn(name = "id_docente_externo", unique = false, nullable = true, insertable = true, updatable = true)
	public DocenteExterno getDocenteExterno() {
		return docenteExterno;
	}

	public void setDocenteExterno(DocenteExterno docenteExterno) {
		this.docenteExterno = docenteExterno;
	}

	/**
	 * Implementa��o do m�todo equals comparando o docente, o docente Externo, a disciplina, a proposta, 
	 * a carga hor�ria, e o nome do Docente, da SecretariaUnidade atual com a passada como par�metro.
	 */
	@Override
	public boolean equals(Object obj) {
		return EqualsUtil.testEquals(this, obj, "docente", "docenteExterno", 
				"disciplina", "proposta", "cargaHoraria", "nomeDocente");
	}

	/**
	 * Implementa��o do m�todo hashCode comparando o docente, o docente Externo, a disciplina, a proposta, 
	 * a carga hor�ria, e o nome do Docente, da SecretariaUnidade atual com a passada como par�metro.
	 */
	@Override
	public int hashCode() {
		return HashCodeUtil.hashAll(docente, docenteExterno, disciplina, 
				proposta, cargaHoraria, nomeDocente);
	}

	@ManyToOne(cascade = { }, fetch = FetchType.LAZY)
	@JoinColumn(name = "id_proposta", unique = false, nullable = true, insertable = true, updatable = true)
	public PropostaCursoLato getProposta() {
		return proposta;
	}

	public void setProposta(PropostaCursoLato proposta) {
		this.proposta = proposta;
	}

	@Column(name = "carga_horaria", unique = false, nullable = true, insertable = true, updatable = true)
	public Integer getCargaHoraria() {
		return cargaHoraria;
	}

	public void setCargaHoraria(Integer cargaHoraria) {
		this.cargaHoraria = cargaHoraria;
	}

	/** Retorna se h� docente externo ou n�o */
	@Transient
	public boolean getExterno() {
		if( getDocenteExterno() != null )
			return true;
		return false;
	}

	public void setNome(String nomeDocente) {
		this.nomeDocente = nomeDocente;
	}

	@Transient
	public String getNomeDocente() {
		return nomeDocente;
	}
	
	/** Seta um componente curricular informando o id. */
	public void setComponente(int id) {
		disciplina = new ComponenteCurricular();
		disciplina.setId(id);
	}
	
	public void setCh(Integer ch) {
		setCargaHoraria(ch);
	}

}
