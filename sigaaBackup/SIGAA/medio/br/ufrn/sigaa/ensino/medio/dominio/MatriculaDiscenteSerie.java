/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em 24/05/2011
 * Autor: Rafael Gomes
 */

package br.ufrn.sigaa.ensino.medio.dominio;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.seguranca.log.CriadoPor;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.pessoa.dominio.Discente;

/**
 * Entidade responsável pelo armazenamento da matrícula anual do discente de nível médio, 
 * no qual esta entidade é responsável por distinguir a turma e série do discente.
 * 
 * @author Rafael Gomes
 *
 */
@Entity
@Table(name = "matricula_discente_serie", schema = "medio", uniqueConstraints = {})
public class MatriculaDiscenteSerie implements Validatable{
	
	/** Chave Primária */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_matricula_discente_serie", nullable = false)
	private int id;
	
	/** Objeto de relacionamento do discente de médio com a classe {@link Discente} utilizada pela implementação de {@link DiscenteAdapter}. */
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name = "id_discente", nullable = false)
	private DiscenteMedio discenteMedio;
	
	/** Atributo responsável por descrever a situação do aluno na matrícula. Sendo utilizado os valores de {@link SituacaoMatriculaSerie}. */
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name = "id_situacao_matricula_serie")
	private SituacaoMatriculaSerie situacaoMatriculaSerie;
	
	/** Objeto de relacionamento do discente de ensino médio com a turma matriculada. */
	@ManyToOne(fetch=FetchType.LAZY) 
	@JoinColumn(name = "id_turma_serie", nullable = false)
	private TurmaSerie turmaSerie;
	
	/** Objeto de relacionamento do discente de ensino médio com o currículo designado ao mesmo. */
	@ManyToOne() 
	@JoinColumn(name = "id_curriculo_medio")
	private CurriculoMedio curriculoMedio;
	
	/** Atributo responsável por informar se o discente encontra-se em dependência por disciplinas reprovadas na série anterior.*/
	@Column(name = "dependencia", nullable = false)
	private boolean dependencia = false;

	/** Registro de Entrada do usuário de cadastrou a matrícula do aluno. */	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="id_registro_entrada")
	@CriadoPor
	private RegistroEntrada registroEntrada;

	/** Data de cadastro da matrícula do aluno. */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_cadastro")
	@CriadoEm
	private Date dataCadastro;
	
	/** Atributo transiente usado em chekboxes de data tables */
	@Transient
	private boolean selected;
	
	/** Nova situação de matrícula para alteração da situação atual do discente. */
	@Transient
	private SituacaoMatriculaSerie novaSituacaoMatricula;
	
	/**Indica se o aluno é repetente.*/
	@Transient
	private boolean repetente;
	
	/** Coleção preenchida com as disciplina pagas pelo discente relacionadas a esta matrícula em série.*/
	@Transient
	private Collection<MatriculaComponente> matriculasDisciplinas  = new ArrayList<MatriculaComponente>();
	
	/**
	 * Número do discente na chamada. 
	 */
	@Column(name="num_chamada")
	private Integer numeroChamada;
	
	// Constructors
	
	/** default constructor */
	public MatriculaDiscenteSerie() {
	}

	/** default minimal constructor */
	public MatriculaDiscenteSerie(int id) {
		this.id = id;
	}
	
	/** minimal constructor */
	public MatriculaDiscenteSerie(int id, DiscenteMedio discenteMedio) {
		this.id = id;
		this.discenteMedio = discenteMedio;
	}

	
	// Getters and Setters
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public DiscenteMedio getDiscenteMedio() {
		return discenteMedio;
	}	
	public void setDiscenteMedio(DiscenteMedio discenteMedio) {
		this.discenteMedio = discenteMedio;
	}
	public SituacaoMatriculaSerie getSituacaoMatriculaSerie() {
		return situacaoMatriculaSerie;
	}
	public void setSituacaoMatriculaSerie(
			SituacaoMatriculaSerie situacaoMatriculaSerie) {
		this.situacaoMatriculaSerie = situacaoMatriculaSerie;
	}
	public TurmaSerie getTurmaSerie() {
		return turmaSerie;
	}
	public void setTurmaSerie(TurmaSerie turmaSerie) {
		this.turmaSerie = turmaSerie;
	}
	public CurriculoMedio getCurriculoMedio() {
		return curriculoMedio;
	}
	public void setCurriculoMedio(CurriculoMedio curriculoMedio) {
		this.curriculoMedio = curriculoMedio;
	}
	public boolean isDependencia() {
		return dependencia;
	}
	public void setDependencia(boolean dependencia) {
		this.dependencia = dependencia;
	}
	public RegistroEntrada getRegistroEntrada() {
		return registroEntrada;
	}
	public void setRegistroEntrada(RegistroEntrada registroEntrada) {
		this.registroEntrada = registroEntrada;
	}
	public Date getDataCadastro() {
		return dataCadastro;
	}
	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}
	public boolean isSelected() {
		return selected;
	}
	public void setSelected(boolean selected) {
		this.selected = selected;
	}
	public SituacaoMatriculaSerie getNovaSituacaoMatricula() {
		return novaSituacaoMatricula;
	}
	public void setNovaSituacaoMatricula(SituacaoMatriculaSerie novaSituacaoMatricula) {
		this.novaSituacaoMatricula = novaSituacaoMatricula;
	}
	public Collection<MatriculaComponente> getMatriculasDisciplinas() {
		return matriculasDisciplinas;
	}

	public void setMatriculasDisciplinas(
			Collection<MatriculaComponente> matriculasDisciplinas) {
		this.matriculasDisciplinas = matriculasDisciplinas;
	}

	public Integer getNumeroChamada() {
		return numeroChamada;
	}
	public void setNumeroChamada(Integer numeroChamada) {
		this.numeroChamada = numeroChamada;
	}

	@Override
	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();
		ValidatorUtil.validateRequired(discenteMedio, "Discente de Nível Médio", lista);
		ValidatorUtil.validateRequired(situacaoMatriculaSerie, "Situação do Discente", lista);
		return lista;
	}

	
	/**
	 * Retorna a descrição da situação do discente na série
	 * @return
	 */
	@Transient
	public String getDescricaoSituacao(){
		if (situacaoMatriculaSerie == null)
			return "-";
		return SituacaoMatriculaSerie.getSituacao( situacaoMatriculaSerie.getId() ).getDescricao();
	}
	
	/**
	 * Retorna o tipo da matrícula, se é Regular ou Dependência
	 * @return
	 */
	@Transient
	public String getTipoMatricula(){
		return (isDependencia() ? "Dependência" : "Regular");
	}

	public boolean isRepetente() {
		return repetente;
	}

	public void setRepetente(boolean repetente) {
		this.repetente = repetente;
	}
	
}
