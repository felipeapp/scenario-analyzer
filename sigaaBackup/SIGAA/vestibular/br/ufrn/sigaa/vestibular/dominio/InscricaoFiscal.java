/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 28/04/2008
 *
 */
package br.ufrn.sigaa.vestibular.dominio;

import static br.ufrn.arq.util.ValidatorUtil.validateRequiredId;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.IndexColumn;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.parametros.dominio.ParametrosVestibular;
import br.ufrn.sigaa.pessoa.dominio.ContaBancaria;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;
import br.ufrn.sigaa.pessoa.dominio.Servidor;

/**
 * Entidade InscricaoFiscal, respons�vel pelo armazenamento do pedido de
 * inscri��o para ser fiscal de Vestibular/Processos Seletivos
 * 
 * @author �dipo
 * 
 */
@Entity
@Table(name = "inscricao_fiscal", schema = "vestibular", uniqueConstraints = {})
public class InscricaoFiscal implements PersistDB, Validatable {

	/** Chave prim�ria. */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_inscricao_fiscal", unique = true, nullable = false, insertable = true, updatable = true)
	private int id;

	/** Lista de locais preferenciais a trabalhar. */
	@ManyToMany
	@JoinTable(name = "vestibular.local_aplicacao_inscricao_fiscal", joinColumns = { @JoinColumn(name = "id_inscricao_fiscal") }, inverseJoinColumns = { @JoinColumn(name = "id_local_aplicacao_prova") })
	@IndexColumn(name = "ordem")
	private List<LocalAplicacaoProva> localAplicacaoProvas;

	/** Processo Seletivo que vai trabalhar. */
	@ManyToOne
	@JoinColumn(name = "id_processo_seletivo")
	private ProcessoSeletivoVestibular processoSeletivoVestibular;

	/** Pessoa associada a inscri��o. */
	@ManyToOne
	@JoinColumn(name = "id_pessoa")
	private Pessoa pessoa;

	/** Servidor associado a inscri��o. */
	@ManyToOne
	@JoinColumn(name = "id_servidor")
	private Servidor servidor;

	/** Discente associado a inscri��o. */
	@ManyToOne(fetch = FetchType.LAZY, targetEntity=Discente.class)
	@JoinColumn(name = "id_discente")
	private DiscenteAdapter discente;

	/**Conta banc�ria para fins de pagamento. */
	@ManyToOne
	@JoinColumn(name = "id_conta_bancaria")
	private ContaBancaria contaBancaria;

	/** NIT, para fins de pagamento. */
	private String nit;

	/** Indica se o fiscal � novato. */
	private boolean novato = true;

	/** Indica se a inscri��o � um re-cadastramento. */
	private boolean recadastro = false;

	/** N�mero de inscri��o. */
	@Column(name = "numero_inscricao")
	private int numeroInscricao;

	/** Indica se tem disponibilidade para viajar para trabalhar em outros munic�pios. */
	@Column(name = "disponibilidade_outras_cidades")
	private boolean disponibilidadeOutrasCidades = false;

	/** Observa��es sobre a inscri��o. */
	private String observacao;
	
	/** ID do arquivo contendo a foto 3x4 do fiscal. */
	@Column(name = "id_foto")
	private Integer idFoto;
	
	/** Indica se o fiscal poder� alterar a foto 3x4. */
	@Column(name = "permite_alterar_foto")
	private boolean permiteAlterarFoto = true;
	
	/** Status da foto enviada pelo candidato. A foto � avaliada pela Comiss�o Permanente do Vestibular e validada, isto �, � realmente uma foto 3x4. */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_status_foto")
	private StatusFoto statusFoto = new StatusFoto(StatusFoto.NAO_ANALISADA);

	/** Novo status da foto enviado pelo candidato. A foto � avaliada pela Comiss�o Permanente do Vestibular e validada, isto �, � realmente uma foto 3x4. */
	@Transient
	private StatusFoto novoStatusFoto;
	
	/** Comparador utilizado quando se deseja ordenar uma lista de fiscais por ordem alfab�tica de nome. */
	@Transient
	public static final Comparator<InscricaoFiscal> nomeComparator= new Comparator<InscricaoFiscal>(){
		public int compare(InscricaoFiscal o1, InscricaoFiscal o2) {
			return o1.getPessoa().getNomeAscii().compareTo(o2.getPessoa().getNomeAscii());
		}
	};
	
	@Transient
	public boolean isStatusFotoAlterado() {
		return statusFoto.getId() != novoStatusFoto.getId();
	}
	
	/** Retorna o novo status da foto do fiscal, dado durante a verifica��o se realmente � uma foto no padr�o 3x4. 
	 * @return
	 */
	public StatusFoto getNovoStatusFoto() {
		return novoStatusFoto;
	}
	
	public void setNovoStatusFoto(StatusFoto novoStatusFoto) {
		this.novoStatusFoto = novoStatusFoto;
	}

	/** Construtor padr�o. */
	public InscricaoFiscal() {
		localAplicacaoProvas = new ArrayList<LocalAplicacaoProva>();
		contaBancaria = new ContaBancaria();
		// 5 locais de prova a serem escolhidos
		for (int i = 0; i < 5; i++) {
			localAplicacaoProvas.add(new LocalAplicacaoProva());
		}
		processoSeletivoVestibular = new ProcessoSeletivoVestibular();
		
		novoStatusFoto = new StatusFoto(0);
	}

	/**
	 * Construtor para inicializar como inscri��o de fiscal de um servidor.
	 * 
	 * @param id
	 *            ID do objeto InscricaoFiscal
	 * @param idProcessoSeletivoVestibular
	 *            ID do objeto ProcessoSeletivoVestibular associado
	 * @param idServidor
	 *            ID do objeto Servidor associado
	 * @param idPessoa
	 *            ID do objeto Pessoa associado
	 */
	public InscricaoFiscal(int id, int idProcessoSeletivoVestibular,
			int idServidor, int idPessoa) {
		this.processoSeletivoVestibular = new ProcessoSeletivoVestibular(
				idProcessoSeletivoVestibular);
		this.pessoa = new Pessoa(idPessoa);
		this.servidor = new Servidor(idServidor);
		this.id = id;
	}

	/**
	 * Construtor para inicializar como inscri��o de fiscal de um discente.
	 * 
	 * @param id
	 *            ID do objeto InscricaoFiscal
	 * @param idProcessoSeletivoVestibular
	 *            ID do objeto ProcessoSeletivoVestibular associado
	 * @param idDiscente
	 *            ID do objeto Discente associado
	 * @param idCurso
	 *            ID do objeto Curso associado
	 * @param idPessoa
	 *            ID do objeto Pessoa associado
	 */
	public InscricaoFiscal(int id, int idProcessoSeletivoVestibular,
			int idDiscente, int idCurso, int idPessoa) {
		this.processoSeletivoVestibular = new ProcessoSeletivoVestibular(
				idProcessoSeletivoVestibular);
		this.discente = new Discente(idDiscente);
		this.discente.getCurso().setId(idCurso);
		this.pessoa = new Pessoa(idPessoa);
		this.id = id;
	}

	/**
	 * Construtor para inicializar com id, idProcessoSeletivo, idPessoa,
	 * idServidor, idDiscente, novato, recadastro.
	 * 
	 * @param id
	 *            ID do objeto InscricaoFiscal
	 * @param idProcessoSeletivoVestibular
	 *            ID do objeto ProcessoSeletivoVestibular associado
	 * @param idPessoa
	 *            ID do objeto Pessoa associado
	 * @param isServidor
	 *            ID do objeto Servidor associado
	 * @param idDiscente
	 *            ID do objeto Discente associado
	 * @param novato
	 *            indica se � novato
	 * @param recadastro
	 *            indica se � recadastro
	 */
	public InscricaoFiscal(int id, int idProcessoSeletivoVestibular,
			Pessoa pessoa, Servidor servidor, Discente discente,
			boolean novato, boolean recadastro) {
		super();
		this.id = id;
		this.processoSeletivoVestibular = new ProcessoSeletivoVestibular(
				idProcessoSeletivoVestibular);
		this.pessoa = pessoa;
		this.servidor = servidor;
		this.discente = discente;
		this.novato = novato;
		this.recadastro = recadastro;
	}

	/** Retorna a conta banc�ria para fins de pagamento.
	 * @return
	 */
	public ContaBancaria getContaBancaria() {
		return contaBancaria;
	}

	/** Retorna o discente associado a inscri��o.
	 * @return
	 */
	public DiscenteAdapter getDiscente() {
		return discente;
	}

	/**
	 * Retorna a chave prim�ria
	 */
	public int getId() {
		return id;
	}

	/** Retorna a lista de locais preferenciais a trabalhar.
	 * @return
	 */
	public List<LocalAplicacaoProva> getLocalAplicacaoProvas() {
		return localAplicacaoProvas;
	}

	/** Retorna o NIT, para fins de pagamento.
	 * @return
	 */
	public String getNit() {
		return nit;
	}

	/** Retorna o n�mero de inscri��o.
	 * @return
	 */
	public int getNumeroInscricao() {
		return numeroInscricao;
	}

	/** Retorna a pessoa associada a inscri��o.
	 * @return
	 */
	public Pessoa getPessoa() {
		return pessoa;
	}

	/** Retorna o Processo Seletivo que vai trabalhar.
	 * @return
	 */
	public ProcessoSeletivoVestibular getProcessoSeletivoVestibular() {
		return processoSeletivoVestibular;
	}

	/** Retorna o Servidor associado a inscri��o.
	 * @return
	 */
	public Servidor getServidor() {
		return servidor;
	}

	/** Indica se tem disponibilidade para viajar para trabalhar em outros munic�pios.
	 * @return
	 */
	public boolean isDisponibilidadeOutrasCidades() {
		return disponibilidadeOutrasCidades;
	}

	/** Indica se o fiscal � novato.
	 * @return
	 */
	public boolean isNovato() {
		return novato;
	}

	/** Indica se a inscri��o � um re-cadastramento.
	 * @return
	 */
	public boolean isRecadastro() {
		return recadastro;
	}

	/** Seta a conta banc�ria para fins de pagamento.
	 * @param contaBancaria
	 */
	public void setContaBancaria(ContaBancaria contaBancaria) {
		this.contaBancaria = contaBancaria;
	}

	/** Seta o Discente associado a inscri��o.
	 * @param discente
	 */
	public void setDiscente(DiscenteAdapter discente) {
		this.discente = discente;
	}

	/** Seta se tem disponibilidade para viajar para trabalhar em outros munic�pios.
	 * @param disponibilidadeOutrasCidades
	 */
	public void setDisponibilidadeOutrasCidades(
			boolean disponibilidadeOutrasCidades) {
		this.disponibilidadeOutrasCidades = disponibilidadeOutrasCidades;
	}

	/**
	 * Seta a chave prim�ria
	 */
	public void setId(int id) {
		this.id = id;
	}

	/** Seta a lista de locais preferenciais a trabalhar.
	 * @param localAplicacaoProvas
	 */
	public void setLocalAplicacaoProvas(
			List<LocalAplicacaoProva> localAplicacaoProvas) {
		this.localAplicacaoProvas = localAplicacaoProvas;
	}

	/** Seta o NIT, para fins de pagamento.
	 * @param nit
	 */
	public void setNit(String nit) {
		this.nit = nit;
	}

	/** Seta se o fiscal � novato.
	 * @param novato
	 */
	public void setNovato(boolean novato) {
		this.novato = novato;
	}

	/** Seta o N�mero de inscri��o.
	 * @param numeroInscricao
	 */
	public void setNumeroInscricao(int numeroInscricao) {
		this.numeroInscricao = numeroInscricao;
	}

	/** Seta a pessoa associada a inscri��o.
	 * @param pessoa
	 */
	public void setPessoa(Pessoa pessoa) {
		this.pessoa = pessoa;
	}

	/** Seta o Processo Seletivo que vai trabalhar.
	 * @param processoSeletivoVestibular
	 */
	public void setProcessoSeletivoVestibular(
			ProcessoSeletivoVestibular processoSeletivoVestibular) {
		this.processoSeletivoVestibular = processoSeletivoVestibular;
	}

	
	/** Seta se a inscri��o � um re-cadastramento.
	 * @param recadastro
	 */
	public void setRecadastro(boolean recadastro) {
		this.recadastro = recadastro;
	}

	/** Seta o Servidor associado a inscri��o.
	 * @param servidor
	 */
	public void setServidor(Servidor servidor) {
		this.servidor = servidor;
	}

	/** Valida se o usu�rio escolheu todos os locais de prova preferenciais.*/
	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();
		for (int i = 0; i < getLocalAplicacaoProvas().size(); i++) {
			validateRequiredId(getLocalAplicacaoProvas().get(i).getId(), "Local " + (i + 1), lista);
		}
		return lista;
	}

	/** Retorna observa��es sobre a inscri��o.
	 * @return
	 */
	public String getObservacao() {
		return observacao;
	}

	/**Seta observa��es sobre a inscri��o.
	 * @param observacao
	 */
	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}

	/**
	 * Adiciona uma observa��o �s existentes, acrescentado-a ao final do texto,
	 * com uma quebra de linha.
	 * 
	 * @param observacao
	 *            Observa��o a incluir
	 */
	public void addObservacao(String observacao) {
		if (this.observacao == null)
			this.observacao = observacao;
		else
			this.observacao += "\n" + observacao;
	}

	/** Compara os IDs da inscri��o para determinar e as s�o iguais. */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof InscricaoFiscal
				&& ((InscricaoFiscal) obj).getId() == this.id)
			return true;
		return false;
	}

	/**
	 * Representa uma inscri��o como string no formato [id, pessoa.nome,
	 * cadastro|recadastro]
	 * 
	 */
	@Override
	public String toString() {
		return id + ", " + getPessoa().getNome() + ", " +
				(discente.isGraduacao() ? discente.getDiscente().getIndice(ParametroHelper.getInstance().getParametroInt(ParametrosVestibular.INDICE_ACADEMICO_SELECAO_FISCAL_GRADUACAO)).getValor() : "P�S-GRADUA��O")
				+ ", " + (recadastro ? "RECADASTRO" : "CADASTRO");
	}

	/** Retorna o c�digo hash deste objeto
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return super.hashCode();
	}

	/** Retorna o ID do arquivo contendo a foto 3x4 do fiscal. 
	 * @return
	 */
	public Integer getIdFoto() {
		return idFoto;
	}

	/** Seta o ID do arquivo contendo a foto 3x4 do fiscal. 
	 * @param idFoto
	 */
	public void setIdFoto(Integer idFoto) {
		this.idFoto = idFoto;
	}

	/** Indica se o fiscal poder� alterar a foto 3x4. 
	 * @return
	 */
	public boolean isPermiteAlterarFoto() {
		return permiteAlterarFoto;
	}

	/** Seta se o fiscal poder� alterar a foto 3x4. 
	 * @param permiteAlterarFoto
	 */
	public void setPermiteAlterarFoto(boolean permiteAlterarFoto) {
		this.permiteAlterarFoto = permiteAlterarFoto;
	}

	/** Retorna o status da foto enviada pelo candidato. A foto � avaliada pela Comiss�o Permanente do Vestibular e validada, isto �, � realmente uma foto 3x4.
	 * @return
	 */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_status_foto")
	public StatusFoto getStatusFoto() {
		return statusFoto;
	}

	/** Seta o status da foto enviada pelo candidato. A foto � avaliada pela Comiss�o Permanente do Vestibular e validada, isto �, � realmente uma foto 3x4.
	 * @param statusFoto
	 */
	public void setStatusFoto(StatusFoto statusFoto) {
		this.statusFoto = statusFoto;
	}
	
}
