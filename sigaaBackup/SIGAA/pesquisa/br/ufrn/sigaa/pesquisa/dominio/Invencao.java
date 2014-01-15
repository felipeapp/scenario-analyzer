/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 20/05/2008
 *
 */

package br.ufrn.sigaa.pesquisa.dominio;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
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

import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.sigaa.dominio.AreaConhecimentoCnpq;
import br.ufrn.sigaa.dominio.Unidade;

/**
 * Classe de domínio que representa as invenções registradas pelos pesquisadores.
 * 
 * @author Leonardo Campos
 * 
 */
@Entity
@Table(name = "invencao", schema = "pesquisa", uniqueConstraints = {})
public class Invencao implements Validatable {

	/** Chave primária da Invenção */
	@Id @GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	parameters={ @Parameter(name="hibernate_sequence", value="public.hibernate_sequence") })
	@Column(name = "id_invencao", unique = true, nullable = false, insertable = true, updatable = true)
	private int id;

	/** Armazena o código de Notificação da Invenção */
	@Embedded
	private CodigoNotificacaoInvencao codigo;
	
	/** Armazena o título da Invenção */
	private String titulo;

	/** Responsável por armazenar o tipo de Invenção */
	@ManyToOne
	@JoinColumn(name = "id_tipo_invencao")
	private TipoInvencao tipo;

	/** Responsável por armazenar a categoria da Patente */
	@Column(name = "categoria_patente")
	private Integer categoriaPatente;

	/** Responsável por armazenar as palavras chave em português */
	@Column(name = "palavras_chave_portugues")
	private String palavrasChavePortugues;

	/** Responsável por armazenar as palavras chave em inglês */
	@Column(name = "palavras_chave_ingles")
	private String palavrasChaveIngles;

	/** Responsável por armazenar a descrição resumida da Invenção */
	@Column(name = "descricao_resumida")
	private String descricaoResumida;

	/** Responsável por armazenar o estado técnico da Invenção */
	@Column(name = "estado_tecnica")
	private String estadoTecnica;

	/** Responsável por armazenar o registro documentação */
	@Column(name = "registro_documentacao")
	private String registroDocumentacao;

	/** Responsável por armazenar as Vantagens e desvantagens */
	@Column(name = "vantagens_desvantagens")
	private String vantagensDesvantagens;

	/** Responsável por armazenar as próximas etapas */	
	@Column(name = "proximas_etapas")
	private String proximasEtapas;

	/** Responsável por armazenar os Mercados e Capacidade Tecnologica */
	@Column(name = "mercados_capacidade_tecnologica")
	private String mercadosCapacidadeTecnologica;

	/** Responsável por armazenar as empresas */
	@Column(name = "empresas")
	private String empresas;

	/** Responsável por armazenar o material utilizado */
	@Column(name = "utilizacao_material")
	private String utilizacaoMaterial;

	/** Responsável pelo armazenamento da utilização do software */
	@Column(name = "utilizacao_software")
	private String utilizacaoSoftware;

	/** Responsável por armazenar o estágio de desenvolvimento de Invenção */
	@ManyToOne
	@JoinColumn(name = "id_estagio_invencao")
	private EstagioDesenvolvimentoInvencao estagio;

	/** Responsável por armazenar o estágio Outro */
	@Column(name = "estagio_outro")
	private String estagioOutro;

	/** Responsável por armazenar se a Invenção obteve Patente ou não */
	@Column(name = "obteve_patente")
	private boolean obtevePatente;

	/** Responsável por informar se a Invenção está ativa ou não */
	private boolean ativo = true;

	/** Responsável por armazenar o status da Invenção */
	private int status;
	
	/** Responsável por armazenar o número do protocolo */
	@Column(name = "numero_protocolo")
	private Integer numeroProtocolo;
	
	/** Responsável por armazenar o ano do protocolo */
	@Column(name = "ano_protocolo")
	private Integer anoProtocolo;

	/** Responsável por armazenar a área de conhecimento CNPQ */
	@ManyToOne
	@JoinColumn(name = "id_area_conhecimento_cnpq")
	private AreaConhecimentoCnpq areaConhecimentoCnpq;
	
	/** Responsável por armazenar o centro */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_centro")
	private Unidade centro;
	
	/** Responsável por armazenar o registro de entrada */
	@ManyToOne
	@JoinColumn(name = "id_registro_entrada")
	private RegistroEntrada criadoPor;

	/** Responsável por armazenar a data do cadastro da Invenção */
	@Column(name = "data_cadastro")
	@Temporal(TemporalType.TIMESTAMP)
	private Date criadoEm;

	@OneToMany(cascade = { CascadeType.ALL }, fetch = FetchType.LAZY, mappedBy = "invencao")
	private Set<FinanciamentoInvencao> financiamentos = new HashSet<FinanciamentoInvencao>(0);

	@OneToMany(cascade = { CascadeType.ALL }, fetch = FetchType.LAZY, mappedBy = "invencao")
	private Set<ArquivoInvencao> arquivos = new HashSet<ArquivoInvencao>(0);

	@OneToMany(cascade = { CascadeType.ALL }, fetch = FetchType.LAZY, mappedBy = "invencao")
	private Set<Inventor> inventores = new HashSet<Inventor>(0);

	@OneToMany(cascade = { CascadeType.ALL }, fetch = FetchType.LAZY, mappedBy = "invencao")
	private Set<VinculoInvencao> vinculos = new HashSet<VinculoInvencao>(0);

	@OneToMany(cascade = { CascadeType.ALL }, fetch = FetchType.LAZY, mappedBy = "invencao")
	private Collection<HistoricoNotificacaoInvencao> historico = new ArrayList<HistoricoNotificacaoInvencao>();
	
	public Invencao() {
	}

	/** Responsável pela validação do cadastro de Invenção */
	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();
		return lista;
	}

	/** Responsável por retornar o id */
	public int getId() {
		return this.id;
	}
	
	/** Responsável por setar o id */
	public void setId(int id) {
		this.id = id;
	}
	
	/** Responsável por retornar o título */
	public String getTitulo() {
		return titulo;
	}
	
	/** Responsável por setar o titulo */
	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}
	
	/** Responsável por retornar o tipo */
	public TipoInvencao getTipo() {
		return tipo;
	}

	/** Responsável por setar o tipo */
	public void setTipo(TipoInvencao tipo) {
		this.tipo = tipo;
	}

	/** Responsável por retornar a categoria Patente */
	public Integer getCategoriaPatente() {
		return categoriaPatente;
	}

	/** Responsável por setar a categoria Patente */
	public void setCategoriaPatente(Integer categoriaPatente) {
		this.categoriaPatente = categoriaPatente;
	}

	/** Responsável por retornar a descrição da categoria Patente */
	@Transient
	public String getDescricaoCategoriaPatente(){
		return (categoriaPatente != null && categoriaPatente > 0) ? TipoInvencao.getDescricaoCategoria(categoriaPatente) : "Indefinido";
	}
	
	/** Responsável por retornar a descrição Resumida */
	public String getDescricaoResumida() {
		return descricaoResumida;
	}

	/** Responsável por setar a descrição resumida */
	public void setDescricaoResumida(String descricaoResumida) {
		this.descricaoResumida = descricaoResumida;
	}

	/** Responsável por retornar o estado Técnico */
	public String getEstadoTecnica() {
		return estadoTecnica;
	}

	/** Responsável por setar o estado Técnico */
	public void setEstadoTecnica(String estadoTecnica) {
		this.estadoTecnica = estadoTecnica;
	}

	/** Responsável por retornar as Vantagens e Desvantagens */
	public String getVantagensDesvantagens() {
		return vantagensDesvantagens;
	}

	/** Responsável por setar as Vantagens e Desvantagens */
	public void setVantagensDesvantagens(String vantagensDesvantagens) {
		this.vantagensDesvantagens = vantagensDesvantagens;
	}

	/** Responsável por retornar o estágio */
	public EstagioDesenvolvimentoInvencao getEstagio() {
		return estagio;
	}
	
	/** Responsável por setar o estágio */
	public void setEstagio(EstagioDesenvolvimentoInvencao estagio) {
		this.estagio = estagio;
	}

	/** Responsável por retornar o estagioOutro */
	public String getEstagioOutro() {
		return estagioOutro;
	}
	
	/** Responsável por setar o estagioOutro */
	public void setEstagioOutro(String estagioOutro) {
		this.estagioOutro = estagioOutro;
	}

	/** Responsável por retornar os Financiamentos */
	public Set<FinanciamentoInvencao> getFinanciamentos() {
		return financiamentos;
	}

	/** Responsável por setar os Financiamentos */
	public void setFinanciamentos(Set<FinanciamentoInvencao> financiamentos) {
		this.financiamentos = financiamentos;
	}

	/** Responsável por retornar os Arquivos */
	public Set<ArquivoInvencao> getArquivos() {
		return arquivos;
	}

	/** Responsável por setar os Arquivos */
	public void setArquivos(Set<ArquivoInvencao> arquivos) {
		this.arquivos = arquivos;
	}

	/** Responsável por retornar os Inventores */
	public Set<Inventor> getInventores() {
		return inventores;
	}

	/** Responsável por setar os Inventores */
	public void setInventores(Set<Inventor> inventores) {
		this.inventores = inventores;
	}

	/** Responsável por retornar um boleando informando se a Invenção obteve ou não Patente */
	public boolean isObtevePatente() {
		return obtevePatente;
	}

	/** Responsável por setar a patente */
	public void setObtevePatente(boolean obtevePatente) {
		this.obtevePatente = obtevePatente;
	}

	/** Responsável informar se a Invenção está em uso ou não */
	public boolean isAtivo() {
		return ativo;
	}

	/** Responsável por setar se a invenção o uso ou não */
	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	/** Responsável por retornar as palavras em Português */
	public String getPalavrasChavePortugues() {
		return palavrasChavePortugues;
	}

	/** Responsável por Setar as Palavras em Português */
	public void setPalavrasChavePortugues(String palavrasChavePortugues) {
		this.palavrasChavePortugues = palavrasChavePortugues;
	}

	/** Responsável por retornar as Palavras em Inglês */
	public String getPalavrasChaveIngles() {
		return palavrasChaveIngles;
	}
	
	/** Responsável por setar as Palavras em Inglês */
	public void setPalavrasChaveIngles(String palavrasChaveIngles) {
		this.palavrasChaveIngles = palavrasChaveIngles;
	}

	/**
	 * Adiciona um arquivo na lista de arquivos da invenção
	 * 
	 * @param arquivo
	 * @return
	 */
	public boolean addArquivo(ArquivoInvencao arquivo) {
		arquivo.setInvencao(this);
		return arquivos.add(arquivo);
	}

	/**
	 * Adiciona um inventor na lista de inventores da invenção
	 * 
	 * @param inventor
	 * @return
	 */
	public boolean addInventor(Inventor inventor) {
		inventor.setInvencao(this);
		return inventores.add(inventor);
	}

	/**
	 * Adicionar um Financiamento a Invenção
	 * 
	 * @param financiamento
	 * @return
	 */
	public boolean addFinanciamento(FinanciamentoInvencao financiamento) {
		financiamento.setInvencao(this);
		return financiamentos.add(financiamento);
	}

	/** Responsável por retornar a Utilização de Material */
	public String getUtilizacaoMaterial() {
		return utilizacaoMaterial;
	}

	/** Responsável por setar a Utilização de Material */
	public void setUtilizacaoMaterial(String utilizacaoMaterial) {
		this.utilizacaoMaterial = utilizacaoMaterial;
	}

	/** Responsável por retornar a UtilizaçãoSoftware */
	public String getUtilizacaoSoftware() {
		return utilizacaoSoftware;
	}

	/** Responsável por seta a UtilizaçãoSoftware */
	public void setUtilizacaoSoftware(String utilizacaoSoftware) {
		this.utilizacaoSoftware = utilizacaoSoftware;
	}

	/** Responsável por retornar os Vinculos */
	public Set<VinculoInvencao> getVinculos() {
		return vinculos;
	}

	/** Responsável por seta os Vinculos */
	public void setVinculos(Set<VinculoInvencao> vinculos) {
		this.vinculos = vinculos;
	}

	/**
	 * Adiciona um vinculo a Invenção
	 * 
	 * @param vinculo
	 * @return
	 */
	public boolean addVinculo(VinculoInvencao vinculo) {
		vinculo.setInvencao(this);
		return vinculos.add(vinculo);
	}

	/** Responsável por retornar o status da Invenção */
	public int getStatus() {
		return status;
	}

	/** Responsável por seta o status da Invenção */
	public void setStatus(int status) {
		this.status = status;
	}

	/** Responsável por retornar o status como String */
	@Transient
	public String getStatusString() {
		return TipoStatusNotificacaoInvencao.getDescricao(status);
	}

	/** Responsável por retornar o Registro Documentação */
	public String getRegistroDocumentacao() {
		return registroDocumentacao;
	}

	/** Responsável por seta o Registro Documentação */
	public void setRegistroDocumentacao(String registroDocumentacao) {
		this.registroDocumentacao = registroDocumentacao;
	}

	/** Responsável por retornar as próximas etapas */
	public String getProximasEtapas() {
		return proximasEtapas;
	}

	/** Responsável por seta as próximas etapas */
	public void setProximasEtapas(String proximasEtapas) {
		this.proximasEtapas = proximasEtapas;
	}

	/** Responsável por retornar os Mercados Capacidade Tecnologica */
	public String getMercadosCapacidadeTecnologica() {
		return mercadosCapacidadeTecnologica;
	}

	/** Responsável por seta os Mercados Capacidade Tecnologica */
	public void setMercadosCapacidadeTecnologica(
			String mercadosCapacidadeTecnologica) {
		this.mercadosCapacidadeTecnologica = mercadosCapacidadeTecnologica;
	}

	/** Responsável por retornar as empresas */
	public String getEmpresas() {
		return empresas;
	}

	/** Responsável por seta as empresas */
	public void setEmpresas(String empresas) {
		this.empresas = empresas;
	}
	
	/** Responsável por retornar o criador da invenção */
	public RegistroEntrada getCriadoPor() {
		return criadoPor;
	}

	/** Responsável por seta o criador da invenção */
	public void setCriadoPor(RegistroEntrada criadoPor) {
		this.criadoPor = criadoPor;
	}

	/** Responsável por retornar o dia em que a invenção foi cadastrada */
	public Date getCriadoEm() {
		return criadoEm;
	}

	/** Responsável por seta o dia em que a invenção foi cadastrada */
	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Invencao other = (Invencao) obj;
		if (id != other.id)
			return false;
		return true;
	}

	/** Responsável por retornar a area de conhecimento CNPQ */
	public AreaConhecimentoCnpq getAreaConhecimentoCnpq() {
		return areaConhecimentoCnpq;
	}

	/** Responsável por seta a area de conhecimento CNPQ */
	public void setAreaConhecimentoCnpq(AreaConhecimentoCnpq areaConhecimentoCnpq) {
		this.areaConhecimentoCnpq = areaConhecimentoCnpq;
	}

	/** Responsável por retornar o centro */
	public Unidade getCentro() {
		return centro;
	}

	/** Responsável por seta o centro */
	public void setCentro(Unidade centro) {
		this.centro = centro;
	}

	/** Responsável por retornar o codigo */
	public CodigoNotificacaoInvencao getCodigo() {
		return codigo;
	}

	/** Responsável por seta o codigo */
	public void setCodigo(CodigoNotificacaoInvencao codigo) {
		this.codigo = codigo;
	}

	/** Responsável por retornar o historico */
	public Collection<HistoricoNotificacaoInvencao> getHistorico() {
		return historico;
	}

	/** Responsável por seta o historico */
	public void setHistorico(Collection<HistoricoNotificacaoInvencao> historico) {
		this.historico = historico;
	}

	/** Responsável por retornar o numero do Procolo */
	public Integer getNumeroProtocolo() {
		return numeroProtocolo;
	}
	
	/** Responsável por seta o numero do Procolo */
	public void setNumeroProtocolo(Integer numeroProtocolo) {
		this.numeroProtocolo = numeroProtocolo;
	}

	/** Responsável por retornar o ano do Procolo */
	public Integer getAnoProtocolo() {
		return anoProtocolo;
	}

	/** Responsável por seta o ano do Procolo */
	public void setAnoProtocolo(Integer anoProtocolo) {
		this.anoProtocolo = anoProtocolo;
	}
	
	/** Responsável por retornar o Tipo Status Notificacao Invencao  */
	public boolean isSubmetida(){
		return status == TipoStatusNotificacaoInvencao.SUBMETIDA;
	}
	
	/** Responsável por retornar o Tipo Invencao */
	public boolean isPatente(){
		return tipo.getId() == TipoInvencao.PATENTE;
	}
}