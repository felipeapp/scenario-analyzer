/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 25/10/2012
 *
 */

package br.ufrn.sigaa.ensino.tecnico.dominio;

import java.util.Date;

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
import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.seguranca.log.CriadoPor;
import br.ufrn.sigaa.ensino.graduacao.dominio.CancelamentoConvocacao;
import br.ufrn.sigaa.ensino.graduacao.dominio.DiscenteGraduacao;
import br.ufrn.sigaa.vestibular.dominio.ConvocacaoProcessoSeletivo;
import br.ufrn.sigaa.vestibular.dominio.ConvocacaoProcessoSeletivoDiscente;
import br.ufrn.sigaa.vestibular.dominio.InscricaoVestibular;
import br.ufrn.sigaa.vestibular.dominio.ResultadoOpcaoCurso;

/**
 * Classe de domínio responsável pelo modelo da convocação do processo seletivo relacionado ao candidato e discente.
 * @author Rafael Gomes
 * @author Fred_Castro
 *
 */
@Entity
@Table(name = "convocacao_processo_seletivo_discente_tecnico", schema = "tecnico")
public class ConvocacaoProcessoSeletivoDiscenteTecnico implements PersistDB, Validatable, Comparable<ConvocacaoProcessoSeletivoDiscenteTecnico>{

	/** Chave primária. */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_convocacao_processo_seletivo_discente_tecnico", unique = true, nullable = false, insertable = true, updatable = true)
	private int id;
	
	/** {@link ConvocacaoProcessoSeletivo Convocação para o processo seletivo} */
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name = "id_convocacao_processo_seletivo")
	private ConvocacaoProcessoSeletivoTecnico convocacaoProcessoSeletivo;
	
	/** {@link InscricaoVestibular Inscrição do Vestibular} referente a esta convocação de discente. */
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name = "id_inscricao_processo_seletivo")
	private InscricaoProcessoSeletivoTecnico inscricaoProcessoSeletivo;
	
	/** {@link DiscenteGraduacao Discente de Graduação} convocado. */
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name = "id_discente")
	private DiscenteTecnico discente;
	
	/** {@link CancelamentoConvocacao Cancelamento de Convocação} */
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name = "id_cancelamento_convocacao", nullable=true)
	private CancelamentoConvocacaoTecnico cancelamento;
	
	/** {@link ConvocacaoProcessoSeletivoDiscente Convocação anterior} */
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name = "id_convocacao_anterior")
	private ConvocacaoProcessoSeletivoDiscenteTecnico convocacaoAnterior;
	
	/** {@link ResultadoOpcaoCurso Resultado do candidato} para a opção do curso que está sendo convocado. */
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name = "id_resultado")
	private ResultadoClassificacaoCandidatoTecnico resultado;
	
	/** Ano de ingresso para o qual o discente foi convocado. */
	private int ano;
	
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
	
	@Transient
	private boolean selecionado = false;
	
	/**	Constructor **/
	public ConvocacaoProcessoSeletivoDiscenteTecnico() {
		super();
	}
	
	public ConvocacaoProcessoSeletivoDiscenteTecnico(int id) {
		this();
		this.id = id;
	}
	

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public ConvocacaoProcessoSeletivoTecnico getConvocacaoProcessoSeletivo() {
		return convocacaoProcessoSeletivo;
	}

	public void setConvocacaoProcessoSeletivo(
			ConvocacaoProcessoSeletivoTecnico convocacaoProcessoSeletivo) {
		this.convocacaoProcessoSeletivo = convocacaoProcessoSeletivo;
	}

	public InscricaoProcessoSeletivoTecnico getInscricaoProcessoSeletivo() {
		return inscricaoProcessoSeletivo;
	}

	public void setInscricaoProcessoSeletivo(
			InscricaoProcessoSeletivoTecnico inscricaoProcessoSeletivo) {
		this.inscricaoProcessoSeletivo = inscricaoProcessoSeletivo;
	}

	public DiscenteTecnico getDiscente() {
		return discente;
	}

	public void setDiscente(DiscenteTecnico discente) {
		this.discente = discente;
	}

	public CancelamentoConvocacaoTecnico getCancelamento() {
		return cancelamento;
	}

	public void setCancelamento(CancelamentoConvocacaoTecnico cancelamento) {
		this.cancelamento = cancelamento;
	}

	public ConvocacaoProcessoSeletivoDiscenteTecnico getConvocacaoAnterior() {
		return convocacaoAnterior;
	}

	public void setConvocacaoAnterior(
			ConvocacaoProcessoSeletivoDiscenteTecnico convocacaoAnterior) {
		this.convocacaoAnterior = convocacaoAnterior;
	}

	public ResultadoClassificacaoCandidatoTecnico getResultado() {
		return resultado;
	}

	public void setResultado(ResultadoClassificacaoCandidatoTecnico resultado) {
		this.resultado = resultado;
	}

	public int getAno() {
		return ano;
	}

	public void setAno(int ano) {
		this.ano = ano;
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
	public int compareTo(ConvocacaoProcessoSeletivoDiscenteTecnico outro) {
		return outro.getInscricaoProcessoSeletivo().getPessoa().getNome().compareTo(getInscricaoProcessoSeletivo().getPessoa().getNome());
	}

	@Override
	public ListaMensagens validate() {
		return null;
	}

	public boolean isSelecionado() {
		return selecionado;
	}

	public void setSelecionado(boolean selecionado) {
		this.selecionado = selecionado;
	}
}
