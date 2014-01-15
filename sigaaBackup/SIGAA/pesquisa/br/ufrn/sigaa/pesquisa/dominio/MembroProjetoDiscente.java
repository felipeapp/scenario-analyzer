/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 13/09/2006
 *
 */
package br.ufrn.sigaa.pesquisa.dominio;



import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

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

import org.apache.commons.lang.time.DateUtils;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.HashCodeUtil;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.TipoConta;
import br.ufrn.sigaa.prodocente.relatorios.dominio.ViewAtividadeBuilder;

/**
 * Classe que registra a participação de discentes em projetos de pesquisa, 
 * vinculados indiretamente através de um plano de trabalho
 * 
 * @author ricardo
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "membro_projeto_discente", schema = "pesquisa", uniqueConstraints = {})
public class MembroProjetoDiscente implements PersistDB,ViewAtividadeBuilder {

	// Fields

	/** Chave primária. */
	private int id;

	/** Plano de trabalho no qual o membro discente atua. */
	private PlanoTrabalho planoTrabalho;

	/** Discente associado ao membro. */
	private Discente discente;

	/** Início da participação do membro. */
	private Date dataInicio;

	/** Fim da participação do membro. */
	private Date dataFim;

	/** Data da indicação do membro. */
	private Date dataIndicacao;

	/** Data da finalização do membro. */
	private Date dataFinalizacao;
	
	/** Registro de entrada do usuário que cadastrou o membro */
	private RegistroEntrada registroEntrada;

	/** Referência ao membro anterior, utilizada em caso de substituição. */
	private MembroProjetoDiscente bolsistaAnterior;
	
	/** Motivo da substituição. */
	private String motivoSubstituicao;

	/** Tipo de bolsa que o membro possui. */
	private TipoBolsaPesquisa tipoBolsa;
	
	/** Atributo transiente utilizado para marcar os bolsistas selecionados nas operações de homologação e finalização de bolsistas */
	private boolean selecionado = false;
	
	/** Indica que o bolsista deve ser ignorado nas operações de homologação e finalização de bolsistas. Utilizado quando o gestor de pesquisa
	 * identifica alguma inconsistência, como um aluno finalizado por engano ou indicado para o mesmo plano de trabalho */
	private boolean ignorar = false;

	/** Tipo de Conta do bolsista */
	private Integer tipoConta = 0;

	/** Indica se o registro foi inativado no banco. */
	private boolean inativo = false;
	
	// Constructors

	/** default constructor */
	public MembroProjetoDiscente() {
		discente = new Discente();
	}

	/** minimal constructor */
	public MembroProjetoDiscente(int idEquipeProjetoDiscente) {
		this.id = idEquipeProjetoDiscente;
	}

	/** full constructor */
	public MembroProjetoDiscente(int idEquipeProjetoDiscente,
			PlanoTrabalho planoTrabalho,
			Discente discente,
			Date dataInicio,
			Date dataFim) {
		this.id = idEquipeProjetoDiscente;
		this.planoTrabalho = planoTrabalho;
		this.discente = discente;
		this.dataInicio = dataInicio;
		this.dataFim = dataFim;
	}

	// Property accessors
	@Id @GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	parameters={ @Parameter(name="hibernate_sequence", value="public.hibernate_sequence") })
	@Column(name = "id_membro_projeto_discente", unique = true, nullable = false, insertable = true, updatable = true)
	public int getId() {
		return this.id;
	}

	public void setId(int idEquipeProjetoDiscente) {
		this.id = idEquipeProjetoDiscente;
	}

	@ManyToOne(cascade = {}, fetch = FetchType.LAZY)
	@JoinColumn(name = "id_plano_trabalho", unique = false, nullable = true, insertable = true, updatable = true)
	public PlanoTrabalho getPlanoTrabalho() {
		return this.planoTrabalho;
	}

	public void setPlanoTrabalho(PlanoTrabalho planoTrabalho) {
		this.planoTrabalho = planoTrabalho;
	}

	@ManyToOne(cascade = {}, fetch = FetchType.EAGER)
	@JoinColumn(name = "id_discente", unique = false, nullable = true, insertable = true, updatable = true)
	public Discente getDiscente() {
		return this.discente;
	}

	public void setDiscente(Discente discente) {
		this.discente = discente;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "data_inicio", unique = false, nullable = true, insertable = true, updatable = true, length = 4)
	public Date getDataInicio() {
		return this.dataInicio;
	}

	public void setDataInicio(Date dataInicio) {
		this.dataInicio = dataInicio;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_fim", unique = false, nullable = true, insertable = true, updatable = true, length = 4)
	public Date getDataFim() {
		return this.dataFim;
	}

	public void setDataFim(Date dataFim) {
		this.dataFim = dataFim;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_indicacao", unique = false, nullable = true, insertable = true, updatable = true)
	public Date getDataIndicacao() {
		return dataIndicacao;
	}

	@ManyToOne(cascade = {}, fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_entrada", unique = false, nullable = true, insertable = true, updatable = true)
	public RegistroEntrada getRegistroEntrada() {
		return registroEntrada;
	}

	public void setDataIndicacao(Date dataIndicacao) {
		this.dataIndicacao = dataIndicacao;
	}

	public void setRegistroEntrada(RegistroEntrada registroEntrada) {
		this.registroEntrada = registroEntrada;
	}

	@Transient
	public boolean isAtivo() {
		return getDataFim() == null || getDataFim().after( DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH) );
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsUtil.testEquals(this, obj, "id", "discente");
	}

	@Override
	public int hashCode() {
		return HashCodeUtil.hashAll(id, discente);
	}

	@Transient
	public String getItemView() {
		return "  <td>"+getDiscente().getPessoa().getNome()+ "</td><td>Interno</td>";
	}

	@Transient
	public String getTituloView() {
		return  "    <td>Nome do Orientando</td><td>Tipo</td>";
	}

	/**
	 * Retorna o mapeamento dos atributos utilizado nas consultas de produção intelectual.
	 */
	@Transient
	public HashMap<String, String> getItens() {
		HashMap<String, String> itens = new HashMap<String, String>();
		itens.put("discente.pessoa.nome", "discenteNome");
		return itens;
	}

	/**
	 * Preenche o nome do discente para exibição nos relatórios de produção intelectual.
	 * @param nome
	 */
	@Transient
	public void setDiscenteNome( String nome ) {
		if ( this.getDiscente() == null ) {
			this.setDiscente( new Discente() );
		}
		this.getDiscente().getPessoa().setNome(nome);
	}

	@Transient
	public float getQtdBase() {
		return 1;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_bolsista_anterior")
	public MembroProjetoDiscente getBolsistaAnterior() {
		return bolsistaAnterior;
	}

	public void setBolsistaAnterior(MembroProjetoDiscente bolsistaAnterior) {
		this.bolsistaAnterior = bolsistaAnterior;
	}

	@Column(name = "motivo_substituicao")
	public String getMotivoSubstituicao() {
		return motivoSubstituicao;
	}

	public void setMotivoSubstituicao(String motivoSubstituicao) {
		this.motivoSubstituicao = motivoSubstituicao;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_finalizacao", unique = false, nullable = true, insertable = true, updatable = true)
	public Date getDataFinalizacao() {
		return dataFinalizacao;
	}

	public void setDataFinalizacao(Date dataFinalizacao) {
		this.dataFinalizacao = dataFinalizacao;
	}

	@ManyToOne(cascade = {}, fetch = FetchType.EAGER)
	@JoinColumn(name = "tipo_bolsa", unique = false, nullable = false, insertable = true, updatable = true)
	public TipoBolsaPesquisa getTipoBolsa() {
		return tipoBolsa;
	}

	public void setTipoBolsa(TipoBolsaPesquisa tipoBolsa) {
		this.tipoBolsa = tipoBolsa;
	}

	@Transient
	public String getTipoBolsaString() {
		return tipoBolsa.getDescricaoResumida();
	}

	@Transient
	public boolean isSelecionado() {
		return selecionado;
	}

	public void setSelecionado(boolean selecionado) {
		this.selecionado = selecionado;
	}

	@Column(name = "ignorar")
	public boolean isIgnorar() {
		return ignorar;
	}

	public void setIgnorar(boolean ignorar) {
		this.ignorar = ignorar;
	}
	
	@Column(name = "tipo_conta")
	public Integer getTipoConta() {
		return tipoConta;
	}

	public void setTipoConta(Integer tipoConta) {
		this.tipoConta = tipoConta;
	}

	@Transient
	public String getDescricaoTipoConta() {
		return tipoConta != null ? TipoConta.getDescricao(tipoConta) : "Indefinido";
	}

	@Column(name = "inativo")
	public boolean isInativo() {
		return inativo;
	}

	public void setInativo(boolean inativo) {
		this.inativo = inativo;
	}
	
}
