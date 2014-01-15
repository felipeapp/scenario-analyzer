/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
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
 * Classe de dom�nio que representa as inven��es registradas pelos pesquisadores.
 * 
 * @author Leonardo Campos
 * 
 */
@Entity
@Table(name = "invencao", schema = "pesquisa", uniqueConstraints = {})
public class Invencao implements Validatable {

	/** Chave prim�ria da Inven��o */
	@Id @GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	parameters={ @Parameter(name="hibernate_sequence", value="public.hibernate_sequence") })
	@Column(name = "id_invencao", unique = true, nullable = false, insertable = true, updatable = true)
	private int id;

	/** Armazena o c�digo de Notifica��o da Inven��o */
	@Embedded
	private CodigoNotificacaoInvencao codigo;
	
	/** Armazena o t�tulo da Inven��o */
	private String titulo;

	/** Respons�vel por armazenar o tipo de Inven��o */
	@ManyToOne
	@JoinColumn(name = "id_tipo_invencao")
	private TipoInvencao tipo;

	/** Respons�vel por armazenar a categoria da Patente */
	@Column(name = "categoria_patente")
	private Integer categoriaPatente;

	/** Respons�vel por armazenar as palavras chave em portugu�s */
	@Column(name = "palavras_chave_portugues")
	private String palavrasChavePortugues;

	/** Respons�vel por armazenar as palavras chave em ingl�s */
	@Column(name = "palavras_chave_ingles")
	private String palavrasChaveIngles;

	/** Respons�vel por armazenar a descri��o resumida da Inven��o */
	@Column(name = "descricao_resumida")
	private String descricaoResumida;

	/** Respons�vel por armazenar o estado t�cnico da Inven��o */
	@Column(name = "estado_tecnica")
	private String estadoTecnica;

	/** Respons�vel por armazenar o registro documenta��o */
	@Column(name = "registro_documentacao")
	private String registroDocumentacao;

	/** Respons�vel por armazenar as Vantagens e desvantagens */
	@Column(name = "vantagens_desvantagens")
	private String vantagensDesvantagens;

	/** Respons�vel por armazenar as pr�ximas etapas */	
	@Column(name = "proximas_etapas")
	private String proximasEtapas;

	/** Respons�vel por armazenar os Mercados e Capacidade Tecnologica */
	@Column(name = "mercados_capacidade_tecnologica")
	private String mercadosCapacidadeTecnologica;

	/** Respons�vel por armazenar as empresas */
	@Column(name = "empresas")
	private String empresas;

	/** Respons�vel por armazenar o material utilizado */
	@Column(name = "utilizacao_material")
	private String utilizacaoMaterial;

	/** Respons�vel pelo armazenamento da utiliza��o do software */
	@Column(name = "utilizacao_software")
	private String utilizacaoSoftware;

	/** Respons�vel por armazenar o est�gio de desenvolvimento de Inven��o */
	@ManyToOne
	@JoinColumn(name = "id_estagio_invencao")
	private EstagioDesenvolvimentoInvencao estagio;

	/** Respons�vel por armazenar o est�gio Outro */
	@Column(name = "estagio_outro")
	private String estagioOutro;

	/** Respons�vel por armazenar se a Inven��o obteve Patente ou n�o */
	@Column(name = "obteve_patente")
	private boolean obtevePatente;

	/** Respons�vel por informar se a Inven��o est� ativa ou n�o */
	private boolean ativo = true;

	/** Respons�vel por armazenar o status da Inven��o */
	private int status;
	
	/** Respons�vel por armazenar o n�mero do protocolo */
	@Column(name = "numero_protocolo")
	private Integer numeroProtocolo;
	
	/** Respons�vel por armazenar o ano do protocolo */
	@Column(name = "ano_protocolo")
	private Integer anoProtocolo;

	/** Respons�vel por armazenar a �rea de conhecimento CNPQ */
	@ManyToOne
	@JoinColumn(name = "id_area_conhecimento_cnpq")
	private AreaConhecimentoCnpq areaConhecimentoCnpq;
	
	/** Respons�vel por armazenar o centro */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_centro")
	private Unidade centro;
	
	/** Respons�vel por armazenar o registro de entrada */
	@ManyToOne
	@JoinColumn(name = "id_registro_entrada")
	private RegistroEntrada criadoPor;

	/** Respons�vel por armazenar a data do cadastro da Inven��o */
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

	/** Respons�vel pela valida��o do cadastro de Inven��o */
	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();
		return lista;
	}

	/** Respons�vel por retornar o id */
	public int getId() {
		return this.id;
	}
	
	/** Respons�vel por setar o id */
	public void setId(int id) {
		this.id = id;
	}
	
	/** Respons�vel por retornar o t�tulo */
	public String getTitulo() {
		return titulo;
	}
	
	/** Respons�vel por setar o titulo */
	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}
	
	/** Respons�vel por retornar o tipo */
	public TipoInvencao getTipo() {
		return tipo;
	}

	/** Respons�vel por setar o tipo */
	public void setTipo(TipoInvencao tipo) {
		this.tipo = tipo;
	}

	/** Respons�vel por retornar a categoria Patente */
	public Integer getCategoriaPatente() {
		return categoriaPatente;
	}

	/** Respons�vel por setar a categoria Patente */
	public void setCategoriaPatente(Integer categoriaPatente) {
		this.categoriaPatente = categoriaPatente;
	}

	/** Respons�vel por retornar a descri��o da categoria Patente */
	@Transient
	public String getDescricaoCategoriaPatente(){
		return (categoriaPatente != null && categoriaPatente > 0) ? TipoInvencao.getDescricaoCategoria(categoriaPatente) : "Indefinido";
	}
	
	/** Respons�vel por retornar a descri��o Resumida */
	public String getDescricaoResumida() {
		return descricaoResumida;
	}

	/** Respons�vel por setar a descri��o resumida */
	public void setDescricaoResumida(String descricaoResumida) {
		this.descricaoResumida = descricaoResumida;
	}

	/** Respons�vel por retornar o estado T�cnico */
	public String getEstadoTecnica() {
		return estadoTecnica;
	}

	/** Respons�vel por setar o estado T�cnico */
	public void setEstadoTecnica(String estadoTecnica) {
		this.estadoTecnica = estadoTecnica;
	}

	/** Respons�vel por retornar as Vantagens e Desvantagens */
	public String getVantagensDesvantagens() {
		return vantagensDesvantagens;
	}

	/** Respons�vel por setar as Vantagens e Desvantagens */
	public void setVantagensDesvantagens(String vantagensDesvantagens) {
		this.vantagensDesvantagens = vantagensDesvantagens;
	}

	/** Respons�vel por retornar o est�gio */
	public EstagioDesenvolvimentoInvencao getEstagio() {
		return estagio;
	}
	
	/** Respons�vel por setar o est�gio */
	public void setEstagio(EstagioDesenvolvimentoInvencao estagio) {
		this.estagio = estagio;
	}

	/** Respons�vel por retornar o estagioOutro */
	public String getEstagioOutro() {
		return estagioOutro;
	}
	
	/** Respons�vel por setar o estagioOutro */
	public void setEstagioOutro(String estagioOutro) {
		this.estagioOutro = estagioOutro;
	}

	/** Respons�vel por retornar os Financiamentos */
	public Set<FinanciamentoInvencao> getFinanciamentos() {
		return financiamentos;
	}

	/** Respons�vel por setar os Financiamentos */
	public void setFinanciamentos(Set<FinanciamentoInvencao> financiamentos) {
		this.financiamentos = financiamentos;
	}

	/** Respons�vel por retornar os Arquivos */
	public Set<ArquivoInvencao> getArquivos() {
		return arquivos;
	}

	/** Respons�vel por setar os Arquivos */
	public void setArquivos(Set<ArquivoInvencao> arquivos) {
		this.arquivos = arquivos;
	}

	/** Respons�vel por retornar os Inventores */
	public Set<Inventor> getInventores() {
		return inventores;
	}

	/** Respons�vel por setar os Inventores */
	public void setInventores(Set<Inventor> inventores) {
		this.inventores = inventores;
	}

	/** Respons�vel por retornar um boleando informando se a Inven��o obteve ou n�o Patente */
	public boolean isObtevePatente() {
		return obtevePatente;
	}

	/** Respons�vel por setar a patente */
	public void setObtevePatente(boolean obtevePatente) {
		this.obtevePatente = obtevePatente;
	}

	/** Respons�vel informar se a Inven��o est� em uso ou n�o */
	public boolean isAtivo() {
		return ativo;
	}

	/** Respons�vel por setar se a inven��o o uso ou n�o */
	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	/** Respons�vel por retornar as palavras em Portugu�s */
	public String getPalavrasChavePortugues() {
		return palavrasChavePortugues;
	}

	/** Respons�vel por Setar as Palavras em Portugu�s */
	public void setPalavrasChavePortugues(String palavrasChavePortugues) {
		this.palavrasChavePortugues = palavrasChavePortugues;
	}

	/** Respons�vel por retornar as Palavras em Ingl�s */
	public String getPalavrasChaveIngles() {
		return palavrasChaveIngles;
	}
	
	/** Respons�vel por setar as Palavras em Ingl�s */
	public void setPalavrasChaveIngles(String palavrasChaveIngles) {
		this.palavrasChaveIngles = palavrasChaveIngles;
	}

	/**
	 * Adiciona um arquivo na lista de arquivos da inven��o
	 * 
	 * @param arquivo
	 * @return
	 */
	public boolean addArquivo(ArquivoInvencao arquivo) {
		arquivo.setInvencao(this);
		return arquivos.add(arquivo);
	}

	/**
	 * Adiciona um inventor na lista de inventores da inven��o
	 * 
	 * @param inventor
	 * @return
	 */
	public boolean addInventor(Inventor inventor) {
		inventor.setInvencao(this);
		return inventores.add(inventor);
	}

	/**
	 * Adicionar um Financiamento a Inven��o
	 * 
	 * @param financiamento
	 * @return
	 */
	public boolean addFinanciamento(FinanciamentoInvencao financiamento) {
		financiamento.setInvencao(this);
		return financiamentos.add(financiamento);
	}

	/** Respons�vel por retornar a Utiliza��o de Material */
	public String getUtilizacaoMaterial() {
		return utilizacaoMaterial;
	}

	/** Respons�vel por setar a Utiliza��o de Material */
	public void setUtilizacaoMaterial(String utilizacaoMaterial) {
		this.utilizacaoMaterial = utilizacaoMaterial;
	}

	/** Respons�vel por retornar a Utiliza��oSoftware */
	public String getUtilizacaoSoftware() {
		return utilizacaoSoftware;
	}

	/** Respons�vel por seta a Utiliza��oSoftware */
	public void setUtilizacaoSoftware(String utilizacaoSoftware) {
		this.utilizacaoSoftware = utilizacaoSoftware;
	}

	/** Respons�vel por retornar os Vinculos */
	public Set<VinculoInvencao> getVinculos() {
		return vinculos;
	}

	/** Respons�vel por seta os Vinculos */
	public void setVinculos(Set<VinculoInvencao> vinculos) {
		this.vinculos = vinculos;
	}

	/**
	 * Adiciona um vinculo a Inven��o
	 * 
	 * @param vinculo
	 * @return
	 */
	public boolean addVinculo(VinculoInvencao vinculo) {
		vinculo.setInvencao(this);
		return vinculos.add(vinculo);
	}

	/** Respons�vel por retornar o status da Inven��o */
	public int getStatus() {
		return status;
	}

	/** Respons�vel por seta o status da Inven��o */
	public void setStatus(int status) {
		this.status = status;
	}

	/** Respons�vel por retornar o status como String */
	@Transient
	public String getStatusString() {
		return TipoStatusNotificacaoInvencao.getDescricao(status);
	}

	/** Respons�vel por retornar o Registro Documenta��o */
	public String getRegistroDocumentacao() {
		return registroDocumentacao;
	}

	/** Respons�vel por seta o Registro Documenta��o */
	public void setRegistroDocumentacao(String registroDocumentacao) {
		this.registroDocumentacao = registroDocumentacao;
	}

	/** Respons�vel por retornar as pr�ximas etapas */
	public String getProximasEtapas() {
		return proximasEtapas;
	}

	/** Respons�vel por seta as pr�ximas etapas */
	public void setProximasEtapas(String proximasEtapas) {
		this.proximasEtapas = proximasEtapas;
	}

	/** Respons�vel por retornar os Mercados Capacidade Tecnologica */
	public String getMercadosCapacidadeTecnologica() {
		return mercadosCapacidadeTecnologica;
	}

	/** Respons�vel por seta os Mercados Capacidade Tecnologica */
	public void setMercadosCapacidadeTecnologica(
			String mercadosCapacidadeTecnologica) {
		this.mercadosCapacidadeTecnologica = mercadosCapacidadeTecnologica;
	}

	/** Respons�vel por retornar as empresas */
	public String getEmpresas() {
		return empresas;
	}

	/** Respons�vel por seta as empresas */
	public void setEmpresas(String empresas) {
		this.empresas = empresas;
	}
	
	/** Respons�vel por retornar o criador da inven��o */
	public RegistroEntrada getCriadoPor() {
		return criadoPor;
	}

	/** Respons�vel por seta o criador da inven��o */
	public void setCriadoPor(RegistroEntrada criadoPor) {
		this.criadoPor = criadoPor;
	}

	/** Respons�vel por retornar o dia em que a inven��o foi cadastrada */
	public Date getCriadoEm() {
		return criadoEm;
	}

	/** Respons�vel por seta o dia em que a inven��o foi cadastrada */
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

	/** Respons�vel por retornar a area de conhecimento CNPQ */
	public AreaConhecimentoCnpq getAreaConhecimentoCnpq() {
		return areaConhecimentoCnpq;
	}

	/** Respons�vel por seta a area de conhecimento CNPQ */
	public void setAreaConhecimentoCnpq(AreaConhecimentoCnpq areaConhecimentoCnpq) {
		this.areaConhecimentoCnpq = areaConhecimentoCnpq;
	}

	/** Respons�vel por retornar o centro */
	public Unidade getCentro() {
		return centro;
	}

	/** Respons�vel por seta o centro */
	public void setCentro(Unidade centro) {
		this.centro = centro;
	}

	/** Respons�vel por retornar o codigo */
	public CodigoNotificacaoInvencao getCodigo() {
		return codigo;
	}

	/** Respons�vel por seta o codigo */
	public void setCodigo(CodigoNotificacaoInvencao codigo) {
		this.codigo = codigo;
	}

	/** Respons�vel por retornar o historico */
	public Collection<HistoricoNotificacaoInvencao> getHistorico() {
		return historico;
	}

	/** Respons�vel por seta o historico */
	public void setHistorico(Collection<HistoricoNotificacaoInvencao> historico) {
		this.historico = historico;
	}

	/** Respons�vel por retornar o numero do Procolo */
	public Integer getNumeroProtocolo() {
		return numeroProtocolo;
	}
	
	/** Respons�vel por seta o numero do Procolo */
	public void setNumeroProtocolo(Integer numeroProtocolo) {
		this.numeroProtocolo = numeroProtocolo;
	}

	/** Respons�vel por retornar o ano do Procolo */
	public Integer getAnoProtocolo() {
		return anoProtocolo;
	}

	/** Respons�vel por seta o ano do Procolo */
	public void setAnoProtocolo(Integer anoProtocolo) {
		this.anoProtocolo = anoProtocolo;
	}
	
	/** Respons�vel por retornar o Tipo Status Notificacao Invencao  */
	public boolean isSubmetida(){
		return status == TipoStatusNotificacaoInvencao.SUBMETIDA;
	}
	
	/** Respons�vel por retornar o Tipo Invencao */
	public boolean isPatente(){
		return tipo.getId() == TipoInvencao.PATENTE;
	}
}