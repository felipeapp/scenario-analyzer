/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
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
import br.ufrn.arq.util.CalendarUtils;

/**
 * Classe de dom�nio respons�vel pelo modelo da convoca��o do processo seletivo.
 * @author Rafael Gomes
 *
 */
@Entity
@Table(name = "convocacao_processo_seletivo", schema = "vestibular")
public class ConvocacaoProcessoSeletivo implements PersistDB, Validatable{

	/** Chave prim�ria. */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_convocacao_processo_seletivo", unique = true, nullable = false, insertable = true, updatable = true)
	private int id;
	
	/** Descri��o da convoca��o (ex.: 1� Chamada, 2� Chamada, etc.) */
	@Column(name = "descricao")
	private String descricao;
	
	/** Data em que os alunos foram convocados para o preenchimento de vagas. */
	@Column(name = "data_convocacao")
	private Date dataConvocacao;
	
	/** Processo Seletivo referente � esta convoca��o. */
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name = "id_processo_seletivo")
	private ProcessoSeletivoVestibular processoSeletivo = new ProcessoSeletivoVestibular();

	/** Registro de Entrada do usu�rio que cadastrou a convoca��o. */
	@CriadoPor
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name = "id_registro_entrada")
	private RegistroEntrada convocadoPor;
	
	/** Data de cadastro da convoca��o. */
	@CriadoEm
	@Column(name = "data_cadastro")
	private Date dataCadastro;
	
	/** Semestre(s) em que os aprovados foram convocados. */
	@Enumerated
	@Column(name="semestre_convocacao")
	private SemestreConvocacao semestreConvocacao;
	
	/** Percentual a convocar al�m do n�mero de vagas. */
	@Enumerated
	@Column(name="percentual_adicional_vagas")
	private int percentualAdicionalVagas;
	
	/**	Constructor padr�o. **/
	public ConvocacaoProcessoSeletivo() {
		super();
		percentualAdicionalVagas = 0;
		processoSeletivo = new ProcessoSeletivoVestibular();
	}
	
	/**	Constructor parametrizado. **/
	public ConvocacaoProcessoSeletivo(int id) {
		this();
		this.id = id;
	}

	/** Getters and Setters **/
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public Date getDataConvocacao() {
		return dataConvocacao;
	}

	public void setDataConvocacao(Date dataConvocacao) {
		this.dataConvocacao = dataConvocacao;
	}

	public ProcessoSeletivoVestibular getProcessoSeletivo() {
		return processoSeletivo;
	}

	public void setProcessoSeletivo(ProcessoSeletivoVestibular processoSeletivo) {
		this.processoSeletivo = processoSeletivo;
	}

	/**
	 * Descri��o completa da convoca��o com a descri��o da convoca��o e a data da convoca��o
	 */
	public String getDescricaoCompleta(){
		return descricao + " (" + CalendarUtils.format(dataConvocacao, "dd/MM/yyyy") + ")";
	}
	
	/* (non-Javadoc)
	 * @see br.ufrn.arq.negocio.validacao.Validatable#validate()
	 */
	@Override
	public ListaMensagens validate() {
		return null;
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

	public SemestreConvocacao getSemestreConvocacao() {
		return semestreConvocacao;
	}

	public void setSemestreConvocacao(SemestreConvocacao semestreConvocacao) {
		this.semestreConvocacao = semestreConvocacao;
	}

	@Override
	public String toString() {
		return this.descricao;
	}

	public int getPercentualAdicionalVagas() {
		return percentualAdicionalVagas;
	}

	public void setPercentualAdicionalVagas(int percentualAdicionalVagas) {
		this.percentualAdicionalVagas = percentualAdicionalVagas;
	}
	
}
