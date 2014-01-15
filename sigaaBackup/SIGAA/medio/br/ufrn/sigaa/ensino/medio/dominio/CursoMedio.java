/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 26/05/2011
 *
 */
package br.ufrn.sigaa.ensino.medio.dominio;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.parametrizacao.RepositorioDadosInstitucionais;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.Formatador;
import br.ufrn.arq.util.HashCodeUtil;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.dominio.CampusIes;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.SituacaoCursoHabil;
import br.ufrn.sigaa.ensino.dominio.SituacaoDiploma;
import br.ufrn.sigaa.ensino.dominio.TipoRegimeLetivo;
import br.ufrn.sigaa.ensino.dominio.TipoSistemaCurricular;
import br.ufrn.sigaa.ensino.dominio.Turno;

/**
 *
 * Essa classe representa não só o curso médio, mas também
 * os cursos dos ensinos infantil e médio
 *
 *@author Rafael Rodrigues
 */
@Entity
@Table(name = "curso_medio", schema = "medio", uniqueConstraints = {})
@PrimaryKeyJoinColumn(name="id_curso")
public class CursoMedio extends Curso implements Validatable{

	/** Indica a forma como o diploma é emitido. */
	private SituacaoDiploma situacaoDiploma = new SituacaoDiploma();

	/** Indica a data de início de funcionamento do Curso. */
	private Date dataInicio;

	/** Indica a data de extinção do Curso. */
	private Date dataExtincao;

	/** Campus da Instituição de Ensino na qual o Curso está vinculado. */
	private CampusIes campusIes;

	/** Indica a situação atual do Curso. */
	private SituacaoCursoHabil situacaoCursoHabil = new SituacaoCursoHabil();

	/** Turno no qual ocorrem as aulas do Curso. */
	private Turno turno = new Turno();

	/** Indica o tipo de Sistema Curricular do Curso. */
	private TipoSistemaCurricular tipoSistemaCurricular = new TipoSistemaCurricular();

	/** Indica o tipo de Regime Letivo do Curso. */
	private TipoRegimeLetivo tipoRegimeLetivo = new TipoRegimeLetivo();

	/** Indica o tipo de Modalidade do Curso. */
	private ModalidadeCursoMedio modalidadeCursoMedio = new ModalidadeCursoMedio();

	/** Indica todas as Estruturas Curriculares pertencentes ao Curso. */
	private Set<CurriculoMedio> estruturasCurriculares = new HashSet<CurriculoMedio>(0);
	
	// Constructors

	/** default constructor */
	public CursoMedio() {
	}

	public CursoMedio(int id) {
		setId(id);
	}

	/** full constructor */
	public CursoMedio(SituacaoDiploma situacaoDiploma) {
		this.situacaoDiploma = situacaoDiploma;
	}
	
	@ManyToOne(cascade = {}, fetch = FetchType.EAGER)
	@JoinColumn(name = "id_situacao_diploma", unique = false, nullable = true, insertable = true, updatable = true)
	public SituacaoDiploma getSituacaoDiploma() {
		return this.situacaoDiploma;
	}

	public void setSituacaoDiploma(SituacaoDiploma situacaoDiploma) {
		this.situacaoDiploma = situacaoDiploma;
	}

	@ManyToOne(cascade = {}, fetch = FetchType.EAGER)
	@JoinColumn(name = "id_campus", unique = false, nullable = true, insertable = true, updatable = true)
	public CampusIes getCampusIes() {
		return this.campusIes;
	}

	public void setCampusIes(CampusIes campusIes) {
		this.campusIes = campusIes;
	}

	@ManyToOne(cascade = {}, fetch = FetchType.EAGER)
	@JoinColumn(name = "id_situacao", unique = false, nullable = true, insertable = true, updatable = true)
	public SituacaoCursoHabil getSituacaoCursoHabil() {
		return this.situacaoCursoHabil;
	}

	public void setSituacaoCursoHabil(SituacaoCursoHabil situacaoCursoHabil) {
		this.situacaoCursoHabil = situacaoCursoHabil;
	}

	@ManyToOne(cascade = {}, fetch = FetchType.EAGER)
	@JoinColumn(name = "id_turno", unique = false, nullable = true, insertable = true, updatable = true)
	public Turno getTurno() {
		return this.turno;
	}

	public void setTurno(Turno turno) {
		this.turno = turno;
	}

	@ManyToOne(cascade = {}, fetch = FetchType.EAGER)
	@JoinColumn(name = "id_tipo_sistema_curricular", unique = false, nullable = true, insertable = true, updatable = true)
	public TipoSistemaCurricular getTipoSistemaCurricular() {
		return this.tipoSistemaCurricular;
	}

	public void setTipoSistemaCurricular(
			TipoSistemaCurricular tipoSistemaCurricular) {
		this.tipoSistemaCurricular = tipoSistemaCurricular;
	}

	@ManyToOne(cascade = {}, fetch = FetchType.EAGER)
	@JoinColumn(name = "id_tipo_regime_letivo", unique = false, nullable = true, insertable = true, updatable = true)
	public TipoRegimeLetivo getTipoRegimeLetivo() {
		return this.tipoRegimeLetivo;
	}

	public void setTipoRegimeLetivo(TipoRegimeLetivo tipoRegimeLetivo) {
		this.tipoRegimeLetivo = tipoRegimeLetivo;
	}

	@ManyToOne(cascade = {}, fetch = FetchType.EAGER)
	@JoinColumn(name = "id_modalidade_curso_medio", unique = false, nullable = true, insertable = true, updatable = true)
	public ModalidadeCursoMedio getModalidadeCursoMedio() {
		return modalidadeCursoMedio;
	}

	public void setModalidadeCursoMedio(ModalidadeCursoMedio modalidadeCursoMedio) {
		this.modalidadeCursoMedio = modalidadeCursoMedio;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "data_inicio", unique = false, nullable = true, insertable = true, updatable = true)
	public Date getDataInicio() {
		return this.dataInicio;
	}

	public void setDataInicio(Date dataInicio) {
		this.dataInicio = dataInicio;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "data_extincao", unique = false, nullable = true, insertable = true, updatable = true, length = 4)
	public Date getDataExtincao() {
		return this.dataExtincao;
	}

	public void setDataExtincao(Date dataExtincao) {
		this.dataExtincao = dataExtincao;
	}

	@OneToMany(cascade = { }, fetch = FetchType.LAZY, mappedBy = "cursoMedio")
	public Set<CurriculoMedio> getEstruturasCurriculares() {
		return estruturasCurriculares;
	}

	public void setEstruturasCurriculares(Set<CurriculoMedio> estruturasCurriculares) {
		this.estruturasCurriculares = estruturasCurriculares;
	}

	/** Retorna a descrição do domínio */
	@Transient
	public String getDescricaoDominio(){
		return getNome();
	}
	
	@Override
	public boolean equals(Object obj) {
		return EqualsUtil.testEquals(this, obj, "id");
	}

	@Override
	public int hashCode() {
		return HashCodeUtil.hashAll(getId());
	}

	@Override
	public ListaMensagens validate() {
		ListaMensagens erros = new ListaMensagens();
		ValidatorUtil.validateRequired(getCodigo(), "Código na "+RepositorioDadosInstitucionais.get("siglaInstituicao"), erros);
		ValidatorUtil.validateRequired(getNome(), "Nome", erros);
		ValidatorUtil.validaData(Formatador.getInstance().formatarData(getDataInicio()), "Data de Início do Funcionamento", erros);
		ValidatorUtil.validateRequired(getModalidadeCursoMedio(), "Modalidade", erros);
		ValidatorUtil.validateRequired(getTurno(), "Turno", erros);
		ValidatorUtil.validateRequired(getModalidadeEducacao(), "Forma de Participação do Aluno", erros);
		return erros;
	}

	/**
	 * Retornar a Estrutura Curricular Ativa para o curso.
	 * @return
	 */
	@Transient
	public CurriculoMedio getEstruturaAtiva(){
		for (CurriculoMedio cm : getEstruturasCurriculares()) {
			if ( cm.isAtivo() ) return cm;
		}
		return null;
	}

	/**
	 * Retornar a lista das disciplinas da estrutura curricular ativa no curso.
	 */
	@Transient
	public Set<ComponenteCurricular> getDisciplinas() {
		if (getEstruturaAtiva() == null) return new HashSet<ComponenteCurricular>();
		return getEstruturaAtiva().getDisciplinas();
	}

	@Transient
	public String getCodigoNome() {
		return getCodigo() + " - " + getNome() + (getTurno() != null ? " (" + getTurno().getSigla() + ")" : "" );
	}
	
	@Transient
	public String getNomeCompleto() {
		return this.getCodigoNome();
	}
	
	/** Retorna a descrição da situação do objeto.*/
	@Transient
	public String getAtivoToString(){
		return isAtivo() ? "ATIVO" : "INATIVO";
	}
}
