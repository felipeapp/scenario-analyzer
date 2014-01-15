/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '13/09/2006'
 *
 */
package br.ufrn.sigaa.ensino.latosensu.dominio;

// Generated 13/09/2006 08:49:22 by Hibernate Tools 3.1.0.beta5

import static br.ufrn.arq.util.ValidatorUtil.validateRequired;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.Formatador;
import br.ufrn.arq.util.HashCodeUtil;
import br.ufrn.sigaa.arq.dao.ensino.latosensu.TurmaEntradaLatoDao;
import br.ufrn.sigaa.dominio.CampusIes;
import br.ufrn.sigaa.ensino.dominio.Turno;
import br.ufrn.sigaa.pessoa.dominio.Municipio;

/**
 * Classe que agrupa a entrada de um grupo de alunos num curso lato sensu
 */
@Entity
@Table(name = "turma_entrada_lato", schema = "lato_sensu", uniqueConstraints = {})
public class TurmaEntradaLato implements Validatable {

	/** Chave primária. */
	private int id;
	
	/** Indica se a Turma de Entrada está ativa ou não. */
    private Boolean ativo = true;

    /** Campus da Intituição de Ensino na qual a Turma de Entrada está vinculada. */
	private CampusIes campusIes = new CampusIes();

	/** Turno no qual ocorrem as aulas. */
	private Turno turno = new Turno();

	/** Indica o tipo de periodicidade das aulas da Turma de Entrada. */
	private TipoPeriodicidadeAula tipoPeriodicidadeAula = new TipoPeriodicidadeAula();

	/** Curso no qual a Turma de Entrada está vinculada. */
	private CursoLato cursoLato = new CursoLato();

	/** Município onde funciona a Turma de Entrada. */
	private Municipio municipio = new Municipio();

	/** Data de início da Turma de Entrada. */
	private Date dataInicio;
	
	/** Data de encerramento da Turma de Entrada. */
	private Date dataFim;

	/** Número de vagas oferecidas pela Turma de Entrada. */
	private Integer vagas;

	/** Número de inscritos na Turma de Entrada. */
	private Integer inscritos;

	/** Código para identificação da Turma de Entrada. */
	private String codigo;

	/** Indica os Discentes que ingressaram na Turma de Entrada. */
	private Set<DiscenteLato> discentesLato = new HashSet<DiscenteLato>(0);

	// Constructors

	/** default constructor */
	public TurmaEntradaLato() {
	}

	/** default minimal constructor */
	public TurmaEntradaLato(int id) {
		this.id = id;
	}	

	/** minimal constructor */
	public TurmaEntradaLato(int idTurmaEntrada, Date dataInicio) {
		this.id = idTurmaEntrada;
		this.dataInicio = dataInicio;
	}

	public TurmaEntradaLato(String turnoSigla){
		setTurno(new Turno(turnoSigla));
	}
	
	// Property accessors
	@Id @GeneratedValue(strategy=GenerationType.SEQUENCE)
	@Column(name = "id_turma_entrada", unique = true, nullable = false, insertable = true, updatable = true)
	public int getId() {
		return this.id;
	}

	public void setId(int idTurmaEntrada) {
		this.id = idTurmaEntrada;
	}
	
	@Column(name = "ativo")
	public Boolean getAtivo() {
		return ativo;
	}

	public void setAtivo(Boolean ativo) {
		this.ativo = ativo;
	}

	@ManyToOne(cascade = {}, fetch = FetchType.LAZY)
	@JoinColumn(name = "id_campus", unique = false, nullable = true, insertable = true, updatable = true)
	public CampusIes getCampusIes() {
		return this.campusIes;
	}

	public void setCampusIes(CampusIes campusIes) {
		this.campusIes = campusIes;
	}

	@ManyToOne(cascade = {}, fetch = FetchType.LAZY)
	@JoinColumn(name = "id_turno", unique = false, nullable = true, insertable = true, updatable = true)
	public Turno getTurno() {
		return this.turno;
	}

	public void setTurno(Turno turno) {
		this.turno = turno;
	}

	@ManyToOne(cascade = {}, fetch = FetchType.LAZY)
	@JoinColumn(name = "id_tipo_periodicidade_aula", unique = false, nullable = true, insertable = true, updatable = true)
	public TipoPeriodicidadeAula getTipoPeriodicidadeAula() {
		return this.tipoPeriodicidadeAula;
	}

	public void setTipoPeriodicidadeAula(
			TipoPeriodicidadeAula tipoPeriodicidadeAula) {
		this.tipoPeriodicidadeAula = tipoPeriodicidadeAula;
	}

	@ManyToOne(cascade = {}, fetch = FetchType.EAGER)
	@JoinColumn(name = "id_curso", unique = false, nullable = true, insertable = true, updatable = true)
	public CursoLato getCursoLato() {
		return this.cursoLato;
	}

	public void setCursoLato(CursoLato cursoLato) {
		this.cursoLato = cursoLato;
	}

	@ManyToOne(cascade = {}, fetch = FetchType.EAGER)
	@JoinColumn(name = "id_municipio", unique = false, nullable = true, insertable = true, updatable = true)
	public Municipio getMunicipio() {
		return this.municipio;
	}

	public void setMunicipio(Municipio municipio) {
		this.municipio = municipio;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "data_inicio", unique = false, nullable = false, insertable = true, updatable = true, length = 4)
	public Date getDataInicio() {
		return this.dataInicio;
	}

	public void setDataInicio(Date dataInicio) {
		this.dataInicio = dataInicio;
	}

	@Column(name = "vagas", unique = false, nullable = true, insertable = true, updatable = true)
	public Integer getVagas() {
		return this.vagas;
	}

	public void setVagas(Integer vagas) {
		this.vagas = vagas;
	}

	@Column(name = "inscritos", unique = false, nullable = true, insertable = true, updatable = true)
	public Integer getInscritos() {
		return this.inscritos;
	}

	public void setInscritos(Integer inscritos) {
		this.inscritos = inscritos;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "data_fim", unique = false, nullable = true, insertable = true, updatable = true, length = 4)
	public Date getDataFim() {
		return this.dataFim;
	}

	public void setDataFim(Date dataFim) {
		this.dataFim = dataFim;
	}

	@Column(name = "codigo", unique = false, nullable = true, insertable = true, updatable = true, length = 7)
	public String getCodigo() {
		return this.codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	@OneToMany(cascade = { }, fetch = FetchType.LAZY, mappedBy = "turmaEntrada")
	public Set<DiscenteLato> getDiscentesLato() {
		return this.discentesLato;
	}

	public void setDiscentesLato(Set<DiscenteLato> discenteLatos) {
		this.discentesLato = discenteLatos;
	}

	@Transient
	public String getDescricao(){
		return getCodigo() + " - " + getMunicipio().getNome();
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsUtil.testTransientEquals(this, obj, "id","codigo","municipio");
	}

	@Override
	public int hashCode() {
		return HashCodeUtil.hashAll(id, codigo, municipio);
	}

	public ListaMensagens validate() {
		
		ListaMensagens lista = new ListaMensagens();
		try {
		
		
		validateRequired(getCodigo(), "Código", lista);
		validateRequired(getDataInicio(), "Data de Início", lista);
		validateRequired(getDataFim(), "Data de Fim", lista);
		validateRequired(getVagas(), "Vagas", lista);
		validateRequired(getTurno(), "Turno", lista);
		validateRequired(getTipoPeriodicidadeAula(), "Tipo Periodicidade Aula", lista);
		validateRequired(getMunicipio(), "Município", lista);
		validateRequired(getCampusIes(), "Campus", lista);

		Date dataInicioCurso = cursoLato.getDataInicio();
		Date dataFimCurso = cursoLato.getDataFim();
		int vagasCurso = cursoLato.getNumeroVagas();

		if( getDataInicio() != null )
			if( getDataInicio().before(dataInicioCurso) )
				lista.addErro("O início da Turma de Entrada não pode ser antes do Início do Curso ("+Formatador.getInstance().formatarData( dataInicioCurso )+")");

		if( getDataFim() != null )
			if( getDataFim().after(dataFimCurso) )
				lista.addErro("O fim da Turma de Entrada não pode ser depois do Fim do Curso ("+Formatador.getInstance().formatarData( dataFimCurso )+")");

		if( getVagas()!= null && getVagas() != 0 ){
			int totalAteAgora = 0;
			TurmaEntradaLatoDao dao = new TurmaEntradaLatoDao();
			
			//Ver a possibilidade de alterar cursoLato.getTurmasEntrada() para Collection 
			Collection<TurmaEntradaLato> turmasEntrada = dao.findByCursoLato(cursoLato.getId(), true);
			cursoLato.setTurmasEntrada(new HashSet<TurmaEntradaLato>());
			
			
			for(TurmaEntradaLato t : turmasEntrada) {
				//Caso o usuário esteja alterando uma turma de entrada não pode somar, logo não adiciona na coleção.
				if(t.getId() != this.getId())
					cursoLato.getTurmasEntrada().add(t);
			}		
			
			
			
			for( TurmaEntradaLato t: cursoLato.getTurmasEntrada() )
				totalAteAgora += t.getVagas();
			if( totalAteAgora + getVagas() > vagasCurso )
				lista.addErro("Turma de Entrada com esse número de vagas não pode ser adicionada.<br>" +
						"O número de vagas total excede o número de vagas do Curso ("+vagasCurso+")");
		}
		
		if( getDataInicio() != null && getDataFim() != null )
			if( getDataInicio().after(getDataFim()) )
				lista.addErro("Data de Início da Turma de Entrada não pode ser depois da Data de Fim");
		
		
		
		}		
		catch (Exception e) {
			e.printStackTrace();
			lista.addErro("Erro na validação da turma de entrada.");
		}		
		
		return lista;
	}
	
	

}
