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
 * Classe de domínio responsável pelo modelo da convocação do processo seletivo.
 * @author Rafael Gomes
 * @author Fred_Castro
 *
 */
@Entity
@Table(name = "convocacao_processo_seletivo_tecnico", schema = "tecnico")
public class ConvocacaoProcessoSeletivoTecnico implements PersistDB, Validatable{

	/** Chave primária. */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_convocacao_processo_seletivo_tecnico", unique = true, nullable = false, insertable = true, updatable = true)
	private int id;
	
	/** Descrição da convocação (ex.: 1º Chamada, 2º Chamada, etc.) */
	@Column(name = "descricao")
	private String descricao;
	
	/** Data em que os alunos foram convocados para o preenchimento de vagas. */
	@Column(name = "data_convocacao")
	private Date dataConvocacao;
	
	/** Processo Seletivo referente à esta convocação. */
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name = "id_processo_seletivo")
	private ProcessoSeletivoTecnico processoSeletivo = new ProcessoSeletivoTecnico ();
	
	/** Objeto que corresponde a opção pólo grupo que pode ser vinculado ou não a uma convocação*/
	@ManyToOne
	@JoinColumn(name="id_opcao")
	private OpcaoPoloGrupo opcao;
	
	/** Objeto que corresponde ao grupo de reserva de vagas que pode ser vinculado ou não a uma convocação*/
	@ManyToOne
	@JoinColumn(name="id_reserva_vaga_grupo")
	private ReservaVagaGrupo grupo;

	/** Registro de Entrada do usuário que cadastrou a convocação. */
	@CriadoPor
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name = "id_registro_entrada")
	private RegistroEntrada convocadoPor;
	
	/** Data de cadastro da convocação. */
	@CriadoEm
	@Column(name = "data_cadastro")
	private Date dataCadastro;
	
	/** Quantidade de discentes com reserva de vagas */
	@Column(name="quantidade_discentes_com_reserva")
	private Integer quantidadeDiscentesComReserva;
	
	/** Quantidade de discentes sem reserva de vagas */
	@Column(name="quantidade_discentes_sem_reserva")
	private Integer quantidadeDiscentesSemReserva;
	
	/** Indica se a convocação irá ou não convocar todos os aprovados */
	@Column(name="todos_aprovados")
	private boolean todosAprovados;
	
	/**	Constructor padrão. **/
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
	 * Descrição completa da convocação com a descrição da convocação e a data da convocação
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
