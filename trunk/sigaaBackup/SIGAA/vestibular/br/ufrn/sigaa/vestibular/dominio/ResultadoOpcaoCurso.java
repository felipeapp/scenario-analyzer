/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em 08/12/2010
 * Autor: Rafael Gomes
 */

package br.ufrn.sigaa.vestibular.dominio;

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

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.sigaa.ensino.graduacao.dominio.MatrizCurricular;

/**
 * Classe de domínio responsável pelo modelo do resultado por opção de curso do candidato no processo seletivo.
 * @author Rafael Gomes
 *
 */
@Entity
@Table(name = "resultado_opcao_curso", schema = "vestibular")
public class ResultadoOpcaoCurso implements PersistDB, Validatable{
	
	/** Constante correspondente à primeira opção */
	public static final int PRIMEIRA_OPCAO = 1; 
	/** Constante correspondente à segunda opção */
	public static final int SEGUNDA_OPCAO = 2; 
	
	/** Chave primária. */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_resultado_opcao_curso", unique = true, nullable = false, insertable = true, updatable = true)
	private int id;
	
	/** Ordem da Opção do Candidato (1, 2, ...). */
	@Column(name = "ordem_opcao")
	private Integer ordemOpcao;
	
	/** Classificação do candidato naquela opção. */
	@Column(name = "classificacao", nullable = true)
	private Integer classificacao;
	
	/** Argumetno Final (nota) do candidato naquela opção. */
	@Column(name = "argumento_final")
	private double argumentoFinal;
	
	/** Argumetno Final (nota) sem benefício de inclusão do candidato naquela opção. */
	@Column(name = "argumento_final_sem_beneficio")
	private double argumentoFinalSemBeneficio;
	
	/** {@link ResultadoClassificacaoCandidato} ao qual este resultado pertence. */
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name = "id_resultado_classificacao_candidato")
	private ResultadoClassificacaoCandidato resultadoClassificacaoCandidato;
	
	/** Matriz Curricular que o candidato optou. */
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name = "id_matriz_curricular")
	private MatrizCurricular matrizCurricular;
	
	/** Convocacao anterior do candidato. */
	@Transient
	private ConvocacaoProcessoSeletivoDiscente convocacaoAnterior;
	
	/** Constructor **/
	public ResultadoOpcaoCurso() {
		super();
	}

	/**  Getters and Setters **/
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Integer getOrdemOpcao() {
		return ordemOpcao;
	}

	public void setOrdemOpcao(Integer ordemOpcao) {
		this.ordemOpcao = ordemOpcao;
	}

	public Integer getClassificacao() {
		return classificacao;
	}

	public void setClassificacao(Integer classificacao) {
		this.classificacao = classificacao;
	}

	public double getArgumentoFinal() {
		return argumentoFinal;
	}

	public void setArgumentoFinal(double argumentoFinal) {
		this.argumentoFinal = argumentoFinal;
	}

	public double getArgumentoFinalSemBeneficio() {
		return argumentoFinalSemBeneficio;
	}

	public void setArgumentoFinalSemBeneficio(double argumentoFinalSemBeneficio) {
		this.argumentoFinalSemBeneficio = argumentoFinalSemBeneficio;
	}

	public ResultadoClassificacaoCandidato getResultadoClassificacaoCandidato() {
		return resultadoClassificacaoCandidato;
	}

	public void setResultadoClassificacaoCandidato(
			ResultadoClassificacaoCandidato resultadoClassificacaoCandidato) {
		this.resultadoClassificacaoCandidato = resultadoClassificacaoCandidato;
	}

	public MatrizCurricular getMatrizCurricular() {
		return matrizCurricular;
	}

	public void setMatrizCurricular(MatrizCurricular matrizCurricular) {
		this.matrizCurricular = matrizCurricular;
	}

	/* (non-Javadoc)
	 * @see br.ufrn.arq.negocio.validacao.Validatable#validate()
	 */
	@Override
	public ListaMensagens validate() {
		// TODO Auto-generated method stub
		return null;
	}

	public ConvocacaoProcessoSeletivoDiscente getConvocacaoAnterior() {
		return convocacaoAnterior;
	}

	public void setConvocacaoAnterior(ConvocacaoProcessoSeletivoDiscente convocacaoAnterior) {
		this.convocacaoAnterior = convocacaoAnterior;
	}

	
}
