/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
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

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.seguranca.log.CriadoPor;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.sigaa.ensino.metropoledigital.dominio.ReservaVagaGrupo;

/**
 * Classe de dom�nio respons�vel pelo modelo da convoca��o do processo seletivo.
 * @author Rafael Gomes
 * @author Fred_Castro
 *
 */
@Entity
@Table(name = "convocacao_processo_seletivo_tecnico", schema = "tecnico")
public class ConvocacaoProcessoSeletivoTecnico implements PersistDB, Validatable{

	/** Chave prim�ria. */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_convocacao_processo_seletivo_tecnico", unique = true, nullable = false, insertable = true, updatable = true)
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
	private ProcessoSeletivoTecnico processoSeletivo = new ProcessoSeletivoTecnico ();
	
	/** Objeto que corresponde a op��o p�lo grupo que pode ser vinculado ou n�o a uma convoca��o*/
	@ManyToOne
	@JoinColumn(name="id_opcao")
	private OpcaoPoloGrupo opcao;
	
	/** Objeto que corresponde ao grupo de reserva de vagas que pode ser vinculado ou n�o a uma convoca��o*/
	@ManyToOne
	@JoinColumn(name="id_reserva_vaga_grupo")
	private ReservaVagaGrupo grupo;

	/** Registro de Entrada do usu�rio que cadastrou a convoca��o. */
	@CriadoPor
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name = "id_registro_entrada")
	private RegistroEntrada convocadoPor;
	
	/** Data de cadastro da convoca��o. */
	@CriadoEm
	@Column(name = "data_cadastro")
	private Date dataCadastro;
	
	/** Quantidade de discentes com reserva de vagas */
	@Column(name="quantidade_discentes_com_reserva")
	private Integer quantidadeDiscentesComReserva;
	
	/** Quantidade de discentes sem reserva de vagas */
	@Column(name="quantidade_discentes_sem_reserva")
	private Integer quantidadeDiscentesSemReserva;
	
	/** Indica se a convoca��o ir� ou n�o convocar todos os aprovados */
	@Column(name="todos_aprovados")
	private boolean todosAprovados;
	
	/**	Constructor padr�o. **/
	public ConvocacaoProcessoSeletivoTecnico () {
		super();
		opcao = new OpcaoPoloGrupo();
		grupo = new ReservaVagaGrupo();
	}
	
	/**	Constructor parametrizado. **/
	public ConvocacaoProcessoSeletivoTecnico (int id) {
		this();
		this.id = id;
		this.quantidadeDiscentesSemReserva = null;
		this.quantidadeDiscentesComReserva = null;
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

	public ProcessoSeletivoTecnico getProcessoSeletivo() {
		return processoSeletivo;
	}

	public void setProcessoSeletivo(ProcessoSeletivoTecnico processoSeletivo) {
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

	@Override
	public String toString() {
		return this.descricao;
	}

	public Integer getQuantidadeDiscentesComReserva() {
		return quantidadeDiscentesComReserva;
	}

	public void setQuantidadeDiscentesComReserva(Integer quantidadeDiscentesComReserva) {
		this.quantidadeDiscentesComReserva = quantidadeDiscentesComReserva;
	}

	public Integer getQuantidadeDiscentesSemReserva() {
		return quantidadeDiscentesSemReserva;
	}

	public void setQuantidadeDiscentesSemReserva(Integer quantidadeDiscentesSemReserva) {
		this.quantidadeDiscentesSemReserva = quantidadeDiscentesSemReserva;
	}

	public OpcaoPoloGrupo getOpcao() {
		return opcao;
	}

	public void setOpcao(OpcaoPoloGrupo opcao) {
		this.opcao = opcao;
	}

	public boolean isTodosAprovados() {
		return todosAprovados;
	}

	public void setTodosAprovados(boolean todosAprovados) {
		this.todosAprovados = todosAprovados;
	}

	public ReservaVagaGrupo getGrupo() {
		return grupo;
	}

	public void setGrupo(ReservaVagaGrupo grupo) {
		this.grupo = grupo;
	}

	
}
