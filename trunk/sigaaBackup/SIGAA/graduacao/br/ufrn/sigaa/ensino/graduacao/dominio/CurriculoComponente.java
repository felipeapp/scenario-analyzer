/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 10/01/2007
 *
 */
package br.ufrn.sigaa.ensino.graduacao.dominio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.HashCodeUtil;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.stricto.dominio.AreaConcentracao;

/**
 * Entidade que representa o relacionamento NxN entre Componente Curricular e Currículos.
 * Um currículo possui vários componentes, enquanto cada componente curricular 
 * pode pertencer a vários currículos diferentes.
 *
 * @author Gleydson
 */
@Entity
@Table(name = "curriculo_componente", schema = "graduacao")
public class CurriculoComponente implements Validatable, Comparable<CurriculoComponente> {

	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_curriculo_componente", nullable = false)
	private int id;

	@Column(name = "semestre_oferta")
	/** Separa o currículo em semestres (graduação) */
	private int semestreOferta;

	@Column(name = "obrigatoria")
	private Boolean obrigatoria;

	/** Categorização usada para stricto */
	@JoinColumn(name = "id_area_concentracao")
	@ManyToOne (fetch=FetchType.EAGER)
	private AreaConcentracao areaConcentracao;

	@JoinColumn(name = "id_componente_curricular")
	@ManyToOne (fetch=FetchType.EAGER)
	private ComponenteCurricular componente = new ComponenteCurricular();

	@JoinColumn(name = "id_curriculo")
	@ManyToOne (fetch=FetchType.EAGER)
	private Curriculo curriculo;

	/**
	 * Auxilia no cadastro para Indicar se o CurriculoComponente está selecionado.
	 */
	@Transient
	private boolean selecionado;

	/** Creates a new instance of CurriculoComponente */
	public CurriculoComponente() {
	}

	public CurriculoComponente(int id2) {
		id = id2;
	}

	public CurriculoComponente(int id, int semestre, boolean obrigatoria, int idComponente, String codigo, String nomeComponente, int chTotal) {
		setId(id);
		setSemestreOferta(semestre);
		setObrigatoria(obrigatoria);
		componente.setId(idComponente);
		componente.setCodigo(codigo);
		componente.setNome(nomeComponente);
		componente.setChTotal(chTotal);
		componente.setChTotal(chTotal);
	}

	public CurriculoComponente(int id, int semestre, boolean obrigatoria, int idCurriculo, int idComponente, String codigo, String nomeComponente, int chTotal) {
		this(id, semestre, obrigatoria, idComponente, codigo, nomeComponente, chTotal);
		curriculo = new Curriculo(idCurriculo);
	}

	public CurriculoComponente(int id, int semestre, boolean obrigatoria, int idCurriculo, int idComponente, String codigo,
			String nomeComponente, int chTotal, Integer idArea, String denominacaoArea) {
		this(id, semestre, obrigatoria, idComponente, codigo, nomeComponente, chTotal);
		this.curriculo = new Curriculo(idCurriculo);
		if( idArea != null ){
			this.areaConcentracao = new AreaConcentracao();
			this.areaConcentracao.setId( idArea );
			this.areaConcentracao.setDenominacao( denominacaoArea );
		}
	}
	
	public CurriculoComponente(int id, int semestre, boolean obrigatoria, int idCurriculo, int idComponente, String codigo,
			String nomeComponente, int chTotal, Integer idArea, String denominacaoArea, String equivalencia) {
		this(id, semestre, obrigatoria, idComponente, codigo, nomeComponente, chTotal);
		this.curriculo = new Curriculo(idCurriculo);
		if( idArea != null ){
			this.areaConcentracao = new AreaConcentracao();
			this.areaConcentracao.setId( idArea );
			this.areaConcentracao.setDenominacao( denominacaoArea );
		}
		this.componente.setEquivalencia(equivalencia);
	}
	
	public CurriculoComponente(ComponenteCurricular componente) {
		this.componente = componente;
	}
	
	/**
	 * Gets the semestreOferta of this CurriculoComponente.
	 *
	 * @return the semestreOferta
	 */
	public int getSemestreOferta() {
		return this.semestreOferta;
	}

	/**
	 * Sets the semestreOferta of this CurriculoComponente to the specified
	 * value.
	 *
	 * @param semestreOferta
	 *            the new semestreOferta
	 */
	public void setSemestreOferta(int semestreOferta) {
		this.semestreOferta = semestreOferta;
	}

	/**
	 * Gets the obrigatoria of this CurriculoComponente.
	 *
	 * @return the obrigatoria
	 */
	public Boolean getObrigatoria() {
		return this.obrigatoria;
	}

	public String getDescricaoObrigatoria() {
		if (obrigatoria != null)
			return obrigatoria ? "Obrig. Currículo":"Optativa";
		else
			return "";
	}
	
	/**
	 * Sets the obrigatoria of this CurriculoComponente to the specified value.
	 *
	 * @param obrigatoria
	 *            the new obrigatoria
	 */
	public void setObrigatoria(Boolean obrigatoria) {
		this.obrigatoria = obrigatoria;
	}

	public String getDescricao() {
		return componente.getCodigoNome();
	}
	
	public ComponenteCurricular getComponente() {
		return componente;
	}

	public void setComponente(ComponenteCurricular componente) {
		this.componente = componente;
	}

	public Curriculo getCurriculo() {
		return curriculo;
	}

	public void setCurriculo(Curriculo curriculo) {
		this.curriculo = curriculo;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public ListaMensagens validate() {
		ListaMensagens erros = new ListaMensagens();
		ValidatorUtil.validateRequired(componente, "Componente Curricular", erros);
		ValidatorUtil.validateRequired(obrigatoria, "Obrigatório / Complementar", erros);
		ValidatorUtil.validaInt(semestreOferta, "Semestre Oferta", erros);
		return erros;
	}

	public ListaMensagens validate(char nivel) {
		ListaMensagens erros = new ListaMensagens();
		if (nivel == NivelEnsino.GRADUACAO || nivel == NivelEnsino.RESIDENCIA )
			return validate();

		ValidatorUtil.validateRequired(componente, "Disciplina", erros);
		ValidatorUtil.validateRequired(obrigatoria, "Obrigatória", erros);
		if (areaConcentracao.getId() < 0)
			ValidatorUtil.validateRequired(areaConcentracao, "Área de Concentração", erros);
		return erros;
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsUtil.testEquals(this, obj, "componente");
	}

	@Override
	public int hashCode() {
		return HashCodeUtil.hashAll(componente);
	}

	public boolean isSelecionado() {
		return selecionado;
	}

	public void setSelecionado(boolean selecionado) {
		this.selecionado = selecionado;
	}
	public AreaConcentracao getAreaConcentracao() {
		return areaConcentracao;
	}
	
	@Transient
	public String getAreaConcentracaoDescricao(){
		return (areaConcentracao != null ? areaConcentracao.getDenominacao() : "COMUM AS OUTRAS ÁREAS");
	}

	public void setAreaConcentracao(AreaConcentracao areaConcentracao) {
		this.areaConcentracao = areaConcentracao;
	}

	public int compareTo(CurriculoComponente cc) {
		if (areaConcentracao == null)
			return semestreOferta - cc.getSemestreOferta();
		Integer i = areaConcentracao.getId();
		if( i != null && cc.getAreaConcentracao() != null )
			return i.compareTo(cc.getAreaConcentracao().getId());

		return 0;
	}

	@Override
	public String toString() {
		
		StringBuilder sb = new StringBuilder();
		
		sb.append(obrigatoria != null && obrigatoria ? "Obrigatoria" : "Complementar");
		sb.append(" - ");
		sb.append(semestreOferta + "º");
		
		if (componente != null)
			sb.append(" - " + componente.getDescricao());
		
		
		return sb.toString();
	}
	
}
