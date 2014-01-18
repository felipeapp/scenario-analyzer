/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 13/09/2006
 *
 */
package br.ufrn.sigaa.pesquisa.dominio;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.faces.model.SelectItem;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.seguranca.log.AtualizadoEm;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.seguranca.log.CriadoPor;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.HashCodeUtil;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.dominio.AreaConhecimentoCnpq;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.pessoa.dominio.Servidor;
import br.ufrn.sigaa.prodocente.relatorios.dominio.ViewAtividadeBuilder;

/**
 * Classe que representa os grupos de pesquisa
 * 
 * @author Ricardo Wendell
 */
@Entity
@Table(name = "grupo_pesquisa", schema = "pesquisa", uniqueConstraints = {})
public class GrupoPesquisa implements Validatable, ViewAtividadeBuilder {

	/** Chave primária */
	@Id @GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	parameters={ @Parameter(name="hibernate_sequence", value="public.hibernate_sequence") })
	@Column(name = "id_grupo_pesquisa", unique = true, nullable = false, insertable = true, updatable = true)
	private int id;

	/** Área de conhecimento do grupo de pesquisa */
	@ManyToOne(cascade = {}, fetch = FetchType.LAZY)
	@JoinColumn(name = "id_area_conhecimento_cnpq", unique = false, nullable = true, insertable = true, updatable = true)
	private AreaConhecimentoCnpq areaConhecimentoCnpq = new AreaConhecimentoCnpq();

	/** Código do grupo de pesquisa */
	@Column(name = "codigo", unique = false, nullable = true, insertable = true, updatable = true, length = 20)
	private String codigo;

	/** Nome do grupo de pequisa */
	@Column(name = "nome", unique = false, nullable = true, insertable = true, updatable = true, length = 150)
	private String nome;

	/** Página do grupo de pesquisa */
	@Column(name = "home_page", unique = false, nullable = true, insertable = true, updatable = true, length = 120)
	private String homePage;

	/** Email do grupo de pesquisa */
	@Column(name = "email", unique = false, nullable = true, insertable = true, updatable = true, length = 80)
	private String email;

	/** Status que o grupos de pesquisa. Ex.: Consolidade, em construção ou júnior */
	@Column(name = "status", unique = false, nullable = false, insertable = true, updatable = true)
	private int status;
	
	/** Informa se o grupo de pesquisa está ativo ou não */
	@Column(name = "ativo", unique = false, nullable = true, insertable = true, updatable = true)
	private boolean ativo = true;

	/** Coordenador do grupo de pesquisa */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_coordenador", unique = false, nullable = true, insertable = true, updatable = true)
	private Servidor coordenador;
	
	/** Vice-coordenador do grupo de pesquisa */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_vice_coordenador", unique = false, nullable = true, insertable = true, updatable = true)
	private Servidor viceCoordenador;

	/** Data que o grupo de pesquisa foi criado */
	@Temporal(TemporalType.DATE)
	@Column(name = "data_criacao", unique = false, nullable = true, insertable = true, updatable = true, length = 4)
	@CriadoEm
	private Date dataCriacao;

	/** Usuário que criou o grupo de pesquisa. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_usuario_criacao", unique = false, nullable = true, insertable = true, updatable = true)
	@CriadoPor
	private Usuario usuarioCriacao;
	
	/** Data da última atualização realizada no grupo de pesquisa */
	@Temporal(TemporalType.DATE)
	@Column(name = "data_ultima_atualizacao", unique = false, nullable = true, insertable = true, updatable = true, length = 4)
	@AtualizadoEm
	private Date dataUltimaAtualizacao;

	/** As repercussões dos trabalhos realizados pelo grupo de pesquisa */
	@Column(name = "repercursoes_trab_grupo", unique = false, nullable = true, insertable = true, updatable = true)
	private String repercursoesTrabGrupo;
	
	/** Justificativa histórica e teórica para formação do grupo. */
	private String justificativa;
	
	/** Instituições com as quais colabora e descrição de intercâmbio com pesquisadores locais ou de outras instituições. */
	@Column(name = "instituicoes_intercambio")
	private String instituicoesIntercambio;
	
	/** Descrição da infra-estrutura disponível. */
	private String infraestrutura;
	
	/** Laboratórios vinculados ao grupo. */
	private String laboratorios;

	/** Linhas de pesquisa que o grupo de pesquisa atua */
	@OneToMany(cascade = { }, fetch = FetchType.LAZY, mappedBy = "grupoPesquisa")
	private Set<LinhaPesquisa> linhasPesquisa = new HashSet<LinhaPesquisa>(0);

	/** Equipes que compõem do grupo de pesquisa */
	@OneToMany(cascade = { CascadeType.ALL }, fetch = FetchType.LAZY, mappedBy = "grupoPesquisa")
	private Set<MembroGrupoPesquisa> equipesGrupoPesquisa  = new HashSet<MembroGrupoPesquisa>(0);

	/** Parecer sobre o grupo de Pesquisa. */
	private String parecer;
	
	/** Serve pra indicar se um determinado docente é o coordenador do grupos de pesquisa */
	@Transient	
	private boolean coordenaGrupo;
	
	/** Proejeto de Pesquisa lidado ao Grupo de Pesquisa */
	@Transient
	private ProjetoPesquisa projPesquisa = new ProjetoPesquisa();
	
	/** Responsável por armazenar o número do protocolo */
	@Column(name = "numero_protocolo")
	private Integer numeroProtocolo;
	
	/** Responsável por armazenar o ano do protocolo */
	@Column(name = "ano_protocolo")
	private Integer anoProtocolo;
	
	/** Construtor padrão. */
	public GrupoPesquisa() {
	}

	/** Construtor mínimo. */
	public GrupoPesquisa(int idGrupoPesquisa) {
		this.id = idGrupoPesquisa;
	}

	/** Se o nome for diferente de nulo retorna o nome mais o código do grupo de pesquisa */
	public String getNomeCompleto() {
		return (nome != null ? nome + ( codigo != null && codigo.trim().length() > 0 ? " (" + codigo + ")" : "" ) : "" );
	}

	/** Retorna o nome de forma compacta, retornando até no máximo 60 caracteres */
	public String getNomeCompacto() {
		String nomeCompacto = (nome != null ? nome.toUpperCase() : "");
		if (nomeCompacto != null && nomeCompacto.length() > 60 ) {
			nomeCompacto = nomeCompacto.substring(0, 60) + "...";
		}
		return nomeCompacto + ( codigo != null && codigo.trim().length() > 0 ? " (" + codigo + ")" : "" );
	}

	@Override
	public String toString() {
		return getNomeCompleto();
	}

	public int getId() {
		return this.id;
	}

	public void setId(int idGrupoPesquisa) {
		this.id = idGrupoPesquisa;
	}

	public AreaConhecimentoCnpq getAreaConhecimentoCnpq() {
		return this.areaConhecimentoCnpq;
	}

	public void setAreaConhecimentoCnpq(
			AreaConhecimentoCnpq areaConhecimentoCnpq) {
		this.areaConhecimentoCnpq = areaConhecimentoCnpq;
	}

	public String getCodigo() {
		return this.codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public String getNome() {
		return this.nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getHomePage() {
		return this.homePage;
	}

	public void setHomePage(String homePage) {
		this.homePage = homePage;
	}

	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public int getStatus() {
		return this.status;
	}

	public void setStatus(int status) {
		this.status = status;
	}
	
	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	public Date getDataCriacao() {
		return this.dataCriacao;
	}

	public void setDataCriacao(Date dataCriacao) {
		this.dataCriacao = dataCriacao;
	}

	public Date getDataUltimaAtualizacao() {
		return this.dataUltimaAtualizacao;
	}

	public void setDataUltimaAtualizacao(Date dataUltimaAtualizacao) {
		this.dataUltimaAtualizacao = dataUltimaAtualizacao;
	}

	public String getRepercursoesTrabGrupo() {
		return this.repercursoesTrabGrupo;
	}

	public void setRepercursoesTrabGrupo(String repercursoesTrabGrupo) {
		this.repercursoesTrabGrupo = repercursoesTrabGrupo;
	}

	public Set<LinhaPesquisa> getLinhasPesquisa() {
		return this.linhasPesquisa;
	}

	/** Retorna uma coleção com as linhas de pesquisa */
	public Collection<LinhaPesquisa> getLinhasPesquisaCol(){
		return new ArrayList<LinhaPesquisa>(this.linhasPesquisa);
	}
	
	/** Cria um combo com todas as linhas de pesquisa */
	public Collection<SelectItem> getLinhasPesquisaCombo(){
		ArrayList<SelectItem> combo = new ArrayList<SelectItem>();
		for (LinhaPesquisa linhaPesq : getLinhasPesquisaCol())
			combo.add(new SelectItem(linhaPesq.getId() , linhaPesq.getNome()));
		return combo;
	}
	
	public void setLinhasPesquisa(Set<LinhaPesquisa> linhaPesquisas) {
		this.linhasPesquisa = linhaPesquisas;
	}

	public Set<MembroGrupoPesquisa> getEquipesGrupoPesquisa() {
		return this.equipesGrupoPesquisa;
	}

	/** Retorna os membros do grupo de pesquisa ordenado. */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Collection<MembroGrupoPesquisa> getEquipesGrupoPesquisaCol() {
		Collection<MembroGrupoPesquisa> membros = new ArrayList(this.equipesGrupoPesquisa);
		Collections.sort((List<MembroGrupoPesquisa>) membros, new Comparator<MembroGrupoPesquisa>() {
			@Override
			public int compare(MembroGrupoPesquisa arg0,
					MembroGrupoPesquisa arg1) {
				return Collator.getInstance().compare(arg0.getPessoa().getNome(), arg1.getPessoa().getNome());
			}
		});
		return membros;
	}
	
	public Servidor getCoordenador() {
		return coordenador;
	}

	public void setCoordenador(Servidor coordenador) {
		this.coordenador = coordenador;
	}

	public void setEquipesGrupoPesquisa(
			Set<MembroGrupoPesquisa> equipeGrupoPesquisas) {
		this.equipesGrupoPesquisa = equipeGrupoPesquisas;
	}

	/**
	 * Serve para adicionar uma linha de pesquisa a um Grupo de pesquisa.
	 * @param obj
	 * @return
	 */
	public boolean addLinhaPesquisa(LinhaPesquisa obj) {
		obj.setGrupoPesquisa(this);
		return linhasPesquisa.add(obj);
	}

	/**
	 * Serve para remover uma linha de pesquisa de um grupo de pesquisa.
	 * @param obj
	 * @return
	 */
	public boolean removeLinhaPesquisa(LinhaPesquisa obj) {
		obj.setGrupoPesquisa(null);
		return linhasPesquisa.remove(obj);
	}

	/**
	 * Serve pra adicionar uma Equipe a um grupo de pesquisa
	 * @param obj
	 * @return
	 */
	public boolean addEquipeGrupoPesquisa(MembroGrupoPesquisa obj) {
		obj.setGrupoPesquisa(this);
		return equipesGrupoPesquisa.add(obj);
	}

	/**
	 * Serve pra remover uma Equipe de uma grupo de pesquisa
	 * @param obj
	 * @return
	 */
	public boolean removeEquipeGrupoPesquisa(MembroGrupoPesquisa obj) {
		obj.setGrupoPesquisa(null);
		return equipesGrupoPesquisa.remove(obj);
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsUtil.testEquals(this, obj, "id", "nome");
	}

	@Override
	public int hashCode() {
		return HashCodeUtil.hashAll(id, nome);
	}

	public String getItemView() {
		return "<td>"+getNome()+ "</td>";
	}

	public HashMap<String, String> getItens() {
		return null;
	}

	public String getTituloView() {
		return "<td>Grupo de Pesquisa</td>";
	}

	@Transient
	public float getQtdBase() {
		for (MembroGrupoPesquisa membro : getEquipesGrupoPesquisa()) {
			if ( membro.getDataFim() == null )
				membro.setDataFim(CalendarUtils.adicionaUmDia(new Date()));
			if ( membro.isCoordenador() && membro.isAtivo() && 
					CalendarUtils.isDentroPeriodo(membro.getDataInicio(), membro.getDataFim()) ) {
				return CalendarUtils.calculaQuantidadeMesesEntreDatasIntervaloFechado(membro.getDataInicio(), membro.getDataFim());
			}
		}
		return 0;
	}

	public String getStatusString(){
		return StatusGrupoPesquisa.getTiposStatus().get(status);
	}
	
	public boolean isCoordenaGrupo() {
		return coordenaGrupo;
	}

	public void setCoordenaGrupo(boolean coordenaGrupo) {
		this.coordenaGrupo = coordenaGrupo;
	}
	
	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();
		
		ValidatorUtil.validateRequired(getCodigo(), "Código", lista);
		ValidatorUtil.validateRequired(getNome(), "Nome", lista);
		ValidatorUtil.validateRequiredId(getCoordenador().getId(), "Coordenador", lista);
		ValidatorUtil.validateEmail(getEmail(), "Email", lista);
		ValidatorUtil.validateRequiredId(getAreaConhecimentoCnpq().getId(), "Área de Conhecimento", lista);
		if(status < 0) lista.addErro("Selecione um status.");
		
		return lista;
	}

	public Servidor getViceCoordenador() {
		return viceCoordenador;
	}

	public void setViceCoordenador(Servidor viceCoordenador) {
		this.viceCoordenador = viceCoordenador;
	}

	public String getJustificativa() {
		return justificativa;
	}

	public void setJustificativa(String justificativa) {
		this.justificativa = justificativa;
	}

	public String getInstituicoesIntercambio() {
		return instituicoesIntercambio;
	}

	public void setInstituicoesIntercambio(String instituicoesIntercambio) {
		this.instituicoesIntercambio = instituicoesIntercambio;
	}

	public String getInfraestrutura() {
		return infraestrutura;
	}

	public void setInfraestrutura(String infraestrutura) {
		this.infraestrutura = infraestrutura;
	}

	public String getLaboratorios() {
		return laboratorios;
	}

	public void setLaboratorios(String laboratorios) {
		this.laboratorios = laboratorios;
	}

	/**
	 * Retorna uma lista com os membros permanentes do grupo de pesquisa
	 * @return
	 */
	public List<MembroGrupoPesquisa> getMembrosPermanentes(){
		List<MembroGrupoPesquisa> result = new ArrayList<MembroGrupoPesquisa>();
		for(MembroGrupoPesquisa m: getEquipesGrupoPesquisa()){
			if(m.isPermanente())
				result.add(m);
		}
		return result;
	}
	
	/**
	 * Serve pra indicar se existe algum campo nulo no objeto
	 * @param obj
	 * @return
	 */
	public static boolean containsNull(Object[] obj){
		for (Object object : obj) {
			if ( object == null ) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Retorna uma lista com os membros associados do grupo de pesquisa
	 * @return
	 */
	public List<MembroGrupoPesquisa> getMembrosAssociados(){
		List<MembroGrupoPesquisa> result = new ArrayList<MembroGrupoPesquisa>();
		for(MembroGrupoPesquisa m: getEquipesGrupoPesquisa()){
			if(m.isAssociado())
				result.add(m);
		}
		return result;
	}

	public Usuario getUsuarioCriacao() {
		return usuarioCriacao;
	}

	public void setUsuarioCriacao(Usuario usuarioCriacao) {
		this.usuarioCriacao = usuarioCriacao;
	}

	public ProjetoPesquisa getProjPesquisa() {
		return projPesquisa;
	}

	public void setProjPesquisa(ProjetoPesquisa projPesquisa) {
		this.projPesquisa = projPesquisa;
	}

	public String getParecer() {
		return parecer;
	}

	public void setParecer(String parecer) {
		this.parecer = parecer;
	}
	
	@Transient
	public boolean isExibeBotoes() {
		return status == StatusGrupoPesquisa.CADASTRO_EM_ANDAMENTO 
		    || status == StatusGrupoPesquisa.NECESSITA_CORRECAO;
	}

	public Integer getNumeroProtocolo() {
		return numeroProtocolo;
	}

	public void setNumeroProtocolo(Integer numeroProtocolo) {
		this.numeroProtocolo = numeroProtocolo;
	}

	public Integer getAnoProtocolo() {
		return anoProtocolo;
	}

	public void setAnoProtocolo(Integer anoProtocolo) {
		this.anoProtocolo = anoProtocolo;
	}
	
}