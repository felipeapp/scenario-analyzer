/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em 08/12/2010
 * Autor: Rafael Gomes
 */

package br.ufrn.sigaa.vestibular.dominio;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.seguranca.log.CriadoPor;
import br.ufrn.sigaa.ensino.graduacao.dominio.CancelamentoConvocacao;
import br.ufrn.sigaa.ensino.graduacao.dominio.DiscenteGraduacao;
import br.ufrn.sigaa.ensino.graduacao.dominio.MatrizCurricular;

/**
 * Classe de domínio responsável pelo modelo da convocação do processo seletivo relacionado ao candidato e discente.
 * @author Rafael Gomes
 *
 */
@Entity
@Table(name = "convocacao_processo_seletivo_discente", schema = "vestibular")
public class ConvocacaoProcessoSeletivoDiscente implements PersistDB, Validatable, Comparable<ConvocacaoProcessoSeletivoDiscente>{

	/** Chave primária. */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_convocacao_processo_seletivo_discente", unique = true, nullable = false, insertable = true, updatable = true)
	private int id;
	
	/** {@link ConvocacaoProcessoSeletivo Convocação para o processo seletivo} */
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name = "id_convocacao_processo_seletivo")
	private ConvocacaoProcessoSeletivo convocacaoProcessoSeletivo;
	
	/** {@link InscricaoVestibular Inscrição do Vestibular} referente a esta convocação de discente. */
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name = "id_inscricao_vestibular")
	private InscricaoVestibular inscricaoVestibular;
	
	/** {@link DiscenteGraduacao Discente de Graduação} convocado. */
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name = "id_discente")
	private DiscenteGraduacao discente;
	
	/** {@link TipoConvocacao Tipo de Convocação} realizado. */
	@Enumerated
	private TipoConvocacao tipo;

	/** {@link CancelamentoConvocacao Cancelamento de Convocação} */
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name = "id_cancelamento_convocacao", nullable=true)
	private CancelamentoConvocacao cancelamento;
	
	/** {@link ConvocacaoProcessoSeletivoDiscente Convocação anterior} */
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name = "id_convocacao_anterior")
	private ConvocacaoProcessoSeletivoDiscente convocacaoAnterior;
	
	/** {@link ResultadoOpcaoCurso Resultado do candidato} para a opção do curso que está sendo convocado. */
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name = "id_resultado")
	private ResultadoOpcaoCurso resultado;
	
	/** Matriz curricular para a qual o discente foi convocado. */
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name = "id_matriz_curricular")
	private MatrizCurricular matrizCurricular;
	
	/** Ano de ingresso para o qual o discente foi convocado. */
	private int ano;
	
	/** Período de ingresso para o qual o discente foi convocado. */
	private int periodo;
	
	/** Registro de Entrada do usuário que cadastrou a convocação. */
	@CriadoPor
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name = "id_registro_entrada")
	private RegistroEntrada convocadoPor;
	
	/** Data de cadastro da convocação. */
	@CriadoEm
	@Column(name = "data_cadastro")
	private Date dataCadastro;
	
	/** Indica se deve cancelar ou não o discente e as matrículas em componentes. */
	@Column(name = "pendente_cancelamento")
	private Boolean pendenteCancelamento;
	
	/** Indica se o discente foi convocado dentro do número de vagas, ou está no percentual chamado a mais. */
	@Column(name = "dentro_numero_vagas")
	private Boolean dentroNumeroVagas;
	
	/** Grupo de Cotas pelo qual o discente foi convocado. */
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name = "id_grupo_cota_vaga_curso")
	private GrupoCotaVagaCurso grupoCotaConvocado;
	
	/** Indica que o candidato foi classificado para preenchimento de vagas remanescente de grupo de cotas. */
	@Column(name = "grupo_cota_remanejado")
	private Boolean grupoCotaRemanejado;
	
	/**	Constructor **/
	public ConvocacaoProcessoSeletivoDiscente() {
		super();
		inscricaoVestibular = new InscricaoVestibular();
	}
	
	public ConvocacaoProcessoSeletivoDiscente(int id) {
		this();
		this.id = id;
	}
	

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public ConvocacaoProcessoSeletivo getConvocacaoProcessoSeletivo() {
		return convocacaoProcessoSeletivo;
	}

	public void setConvocacaoProcessoSeletivo(
			ConvocacaoProcessoSeletivo convocacaoProcessoSeletivo) {
		this.convocacaoProcessoSeletivo = convocacaoProcessoSeletivo;
	}

	public InscricaoVestibular getInscricaoVestibular() {
		return inscricaoVestibular;
	}

	public void setInscricaoVestibular(InscricaoVestibular inscricaoVestibular) {
		this.inscricaoVestibular = inscricaoVestibular;
	}

	public DiscenteGraduacao getDiscente() {
		return discente;
	}

	public void setDiscente(DiscenteGraduacao discente) {
		this.discente = discente;
	}

	/* (non-Javadoc)
	 * @see br.ufrn.arq.negocio.validacao.Validatable#validate()
	 */
	@Override
	public ListaMensagens validate() {
		return null;
	}

	public TipoConvocacao getTipo() {
		return tipo;
	}

	public void setTipo(TipoConvocacao tipo) {
		this.tipo = tipo;
	}

	public String getTipoDesc(){
		return tipo.getLabel();
	}

	public CancelamentoConvocacao getCancelamento() {
		return cancelamento;
	}

	public void setCancelamento(CancelamentoConvocacao cancelamento) {
		this.cancelamento = cancelamento;
	}

	public ConvocacaoProcessoSeletivoDiscente getConvocacaoAnterior() {
		return convocacaoAnterior;
	}

	public void setConvocacaoAnterior(
			ConvocacaoProcessoSeletivoDiscente convocacaoAnterior) {
		this.convocacaoAnterior = convocacaoAnterior;
	}

	public ResultadoOpcaoCurso getResultado() {
		return resultado;
	}

	public void setResultado(ResultadoOpcaoCurso resultado) {
		this.resultado = resultado;
	}

	public MatrizCurricular getMatrizCurricular() {
		return matrizCurricular;
	}

	public void setMatrizCurricular(MatrizCurricular matrizCurricular) {
		this.matrizCurricular = matrizCurricular;
	}

	public int getAno() {
		return ano;
	}

	public void setAno(int ano) {
		this.ano = ano;
	}

	public int getPeriodo() {
		return periodo;
	}

	public void setPeriodo(int periodo) {
		this.periodo = periodo;
	}

	public RegistroEntrada getConvocadoPor() {
		return convocadoPor;
	}

	public void setConvocadoPor(RegistroEntrada convocadoPor) {
		this.convocadoPor = convocadoPor;
	}

	public Date getDataCadastro() {
		return dataCadastro;
	}

	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	public Boolean getPendenteCancelamento() {
		return pendenteCancelamento;
	}
	
	public void setPendenteCancelamento(Boolean pendenteCancelamento) {
		this.pendenteCancelamento = pendenteCancelamento;
	}
	
	@Override
	public int compareTo(ConvocacaoProcessoSeletivoDiscente outro) {
		// TODO: Verificar se este método está comparando corretamente, conforme o comparador implementado em ConvocacaoVestibularMBean
		MatrizCurricular matriz1 = this.getDiscente().getMatrizCurricular();
		MatrizCurricular matriz2 = outro.getDiscente().getMatrizCurricular();
		if (matriz1.getCurso().getNome().compareTo(matriz2.getCurso().getNome()) != 0) {
			return matriz1.getCurso().getNome().compareTo(matriz2.getCurso().getNome());
		} else if (matriz1.getHabilitacao() != null && matriz2.getHabilitacao() != null 
				&& matriz1.getHabilitacao().getNome().compareTo(matriz2.getHabilitacao().getNome()) != 0) {
			return matriz1.getHabilitacao().getNome().compareTo(matriz2.getHabilitacao().getNome());
		} else if (matriz1.getTurno().getSigla().compareTo(matriz2.getTurno().getSigla()) != 0) {
			 return matriz1.getTurno().getSigla().compareTo(matriz2.getTurno().getSigla());
		} else {
			return matriz1.getGrauAcademico().getDescricao().compareTo(matriz2.getGrauAcademico().getDescricao());
		}
	}

	public Boolean getDentroNumeroVagas() {
		return dentroNumeroVagas;
	}

	public void setDentroNumeroVagas(Boolean dentroNumeroVagas) {
		this.dentroNumeroVagas = dentroNumeroVagas;
	}

	public GrupoCotaVagaCurso getGrupoCotaConvocado() {
		return grupoCotaConvocado;
	}

	public void setGrupoCotaConvocado(GrupoCotaVagaCurso grupoCotaConvocado) {
		this.grupoCotaConvocado = grupoCotaConvocado;
	}

	public Boolean getGrupoCotaRemanejado() {
		return grupoCotaRemanejado;
	}

	public void setGrupoCotaRemanejado(Boolean grupoCotaRemanejado) {
		this.grupoCotaRemanejado = grupoCotaRemanejado;
	}

}
