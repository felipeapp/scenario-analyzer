/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 28/09/2010
 */
package br.ufrn.sigaa.estagio.dominio;

import static br.ufrn.arq.util.ValidatorUtil.validateCPF_CNPJ;
import static br.ufrn.arq.util.ValidatorUtil.validateRequired;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.seguranca.log.CriadoPor;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.HashCodeUtil;

/**
 * Entidade que Representa Solicitação de Convênio de Estágio.
 * 
 * @author Arlindo Rodrigues
 */
@Entity
@Table(name = "convenio_estagio", schema = "estagio")
public class ConvenioEstagio implements Validatable {
	
	/** Chave primária */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="estagio.hibernate_sequence") })	
	@Column(name = "id_convenio_estagio")
	private int id;	
	
	/** Tipo do Convênio */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_tipo_convenio")
	private TipoConvenio tipoConvenio = new TipoConvenio();
	
	/** Situação atual do Convênio */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "status")
	private StatusConvenioEstagio status;
		
	/** Número do Convênio Cadastrado */
	@Column(name = "numero_convenio")
	private String numeroConvenio;	
	
	/** Indica se é Órgão Federal */
	@Column(name = "orgao_federal")
	private boolean orgaoFederal;
	
	/** Concedente de Conveniado */
	@OneToOne(mappedBy = "convenioEstagio", cascade = CascadeType.ALL, fetch=FetchType.EAGER)
	private ConcedenteEstagio concedente;
	
	/** Data do cadastro. */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_cadastro")
	@CriadoEm
	private Date dataCadastro;
	
	/** Data da Análise. */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_analise")
	private Date dataAnalise;	

	/** Registro entrada de quem cadastrou. */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="id_registro_entrada")
	@CriadoPor
	private RegistroEntrada registroCadastro;
	
	/** Registro entrada de quem realizou a análise. */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="id_registro_analise")
	private RegistroEntrada registroAnalise;	
	
	/** Motivo da Análise do Convênio em caso de negativa */
	@Column(name = "motivo_analise")
	private String motivoAnalise;
	
	/** Tipo de oferta da vaga (pela empresa, coordenação ou ambos). */
	@Column(name = "tipo_oferta_vaga")
	private TipoOfertaVaga tipoOfertaVaga;
	
	/** Indica que o tipo de convênio é realizado por um agente de integração interno à Instituição. */
	@Column(name = "agente_interno")
	private boolean agenteInterno;
	
	/** Indica que o tipo de convênio é realizado por um agente de integração interno à Instituição. */
	@Column(name = "agente_integrador")
	private boolean agenteIntegrador;
	
	/** ID do arquivo digitalizado do termo de convênio constando a assinatura das partes. */
	@Column(name = "id_arquivo_termo_convenio")
	private Integer idArquivoTermoConvenio;
	
	/** Construtor padrão. */
	public ConvenioEstagio() {
		concedente = new ConcedenteEstagio();
		status = new StatusConvenioEstagio(StatusConvenioEstagio.SUBMETIDO, "SUBMETIDO");
		tipoConvenio = new TipoConvenio();
		tipoOfertaVaga = TipoOfertaVaga.OFERTADO_PELA_COORDENACAO;
		orgaoFederal = false;
	}
	
	/** Construtor parametrizado. */
	public ConvenioEstagio(int id) {
		this();
		setId(id);
		
	}
	
	/**
	 * Verifica se o convênio está Aprovado
	 * @return
	 */
	public boolean isAprovado(){
		return status.getId() == StatusConvenioEstagio.APROVADO;
	}
	
	/**
	 * Verifica se o convênio está Rescusado
	 * @return
	 */
	public boolean isRecusado(){
		return status.getId() == StatusConvenioEstagio.RECUSADO;
	}	

	@Override
	public int getId() {
		return id;
	}

	@Override
	public void setId(int id) {
		this.id = id;
	}

	public TipoConvenio getTipoConvenio() {
		return tipoConvenio;
	}

	public void setTipoConvenio(TipoConvenio tipoConvenio) {
		this.tipoConvenio = tipoConvenio;
	}

	public StatusConvenioEstagio getStatus() {
		return status;
	}

	public void setStatus(StatusConvenioEstagio status) {
		this.status = status;
	}

	public Date getDataCadastro() {
		return dataCadastro;
	}

	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	public RegistroEntrada getRegistroCadastro() {
		return registroCadastro;
	}

	public void setRegistroCadastro(RegistroEntrada registroCadastro) {
		this.registroCadastro = registroCadastro;
	}
	
	public Date getDataAnalise() {
		return dataAnalise;
	}

	public void setDataAnalise(Date dataAnalise) {
		this.dataAnalise = dataAnalise;
	}

	public RegistroEntrada getRegistroAnalise() {
		return registroAnalise;
	}

	public void setRegistroAnalise(RegistroEntrada registroAnalise) {
		this.registroAnalise = registroAnalise;
	}

	public ConcedenteEstagio getConcedente() {
		return concedente;
	}

	public void setConcedente(ConcedenteEstagio concedente) {
		this.concedente = concedente;
	}

	public String getNumeroConvenio() {
		return numeroConvenio;
	}

	public void setNumeroConvenio(String numeroConvenio) {
		this.numeroConvenio = numeroConvenio;
	}

	public boolean isOrgaoFederal() {
		return orgaoFederal;
	}

	public void setOrgaoFederal(boolean orgaoFederal) {
		this.orgaoFederal = orgaoFederal;
	}

	public String getMotivoAnalise() {
		return motivoAnalise;
	}

	public void setMotivoAnalise(String motivoAnalise) {
		this.motivoAnalise = motivoAnalise;
	}

	/**
	 * Retorna o CNPJ Concatenado com o nome da Empresa (Agente de Estágio)
	 * @return
	 */
	@Transient
	public String getNomeCNPJEmpresa(){
		return concedente.getPessoa().getCpfCnpjFormatado() + " - " + concedente.getPessoa().getNome();
	}

	/**
	 * Verifica se ainda não foi analisado a solicitação de convênio de estágio
	 * @return
	 */
	@Transient
	public boolean isPendenteAnalise(){
		return status.getId() == StatusConvenioEstagio.SUBMETIDO || status.getId() == StatusConvenioEstagio.EM_ANALISE;
	}
	
	/**
	 * Indica se o convênio está com status submetido.
	 * @return
	 */
	@Transient
	public boolean isSubmetido(){
		return status.getId() == StatusConvenioEstagio.SUBMETIDO;
	}
	
	/** 
	 * Calcula e retorna o código hash deste objeto.
	 * @see java.lang.Object#hashCode()
	 */		
	@Override
	public int hashCode() {		
		return HashCodeUtil.hashAll(id);
	}

	/**
	 * Compara o ID e do estágio com o passado por parâmetro.
	 * @see java.lang.Object#equals(java.lang.Object)
	 */	
	@Override
	public boolean equals(Object obj) {
		return EqualsUtil.testEquals(this, obj, "id");
	}
	
	/**
	 * Valida os atributos do convênio. 
	 * @see br.ufrn.arq.negocio.validacao.Validatable#validate() 
	 */		
	@Override
	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();
		
		validateRequired(tipoConvenio, "Tipo do Convênio", lista);
		validateCPF_CNPJ(concedente.getPessoa().getCpf_cnpj(), "CPF/CNPJ", lista);
		validateRequired(concedente.getPessoa().getNome(), "Nome da Empresa", lista);
		validateRequired(concedente.getPessoa().getEnderecoContato().getCep(), "CEP", lista);
		validateRequired(concedente.getPessoa().getEnderecoContato().getLogradouro(), "Logradouro", lista);
		validateRequired(concedente.getPessoa().getEnderecoContato().getBairro(), "Bairro", lista);		

		return lista;
	}

	public TipoOfertaVaga getTipoOfertaVaga() {
		return tipoOfertaVaga;
	}

	public void setTipoOfertaVaga(TipoOfertaVaga tipoOfertaVaga) {
		this.tipoOfertaVaga = tipoOfertaVaga;
	}

	public boolean isAgenteInterno() {
		return agenteInterno;
	}

	public void setAgenteInterno(boolean agenteInterno) {
		this.agenteInterno = agenteInterno;
	}

	public boolean isAgenteIntegrador() {
		return agenteIntegrador;
	}

	public void setAgenteIntegrador(boolean agenteIntegrador) {
		this.agenteIntegrador = agenteIntegrador;
	}

	public Integer getIdArquivoTermoConvenio() {
		return idArquivoTermoConvenio;
	}

	public void setIdArquivoTermoConvenio(Integer idArquivoTermoConvenio) {
		this.idArquivoTermoConvenio = idArquivoTermoConvenio;
	}	
}
