/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 23/03/2009
 */
package br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.seguranca.log.AtualizadoEm;
import br.ufrn.arq.seguranca.log.AtualizadoPor;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.seguranca.log.CriadoPor;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.sigaa.biblioteca.dominio.Biblioteca;
import br.ufrn.sigaa.biblioteca.util.CatalogacaoUtil;

/**
 *       Guarda as informações sobre as assinaturas de periódicos. Assinatura não são tombadas no
 *  patrimônio da instituição.
 *
 * @author Jadson
 * @since 23/03/2009
 * @version 1.0 criação da classe
 *
 */
@Entity
@Table(name = "assinatura", schema = "biblioteca")
public class Assinatura implements Validatable {

	/** Modalidade de aquisição por compra. */
	public final static short MODALIDADE_COMPRA = 1;
	
	/** Modalidade de aquisição por doação. */
	public final static short MODALIDADE_DOACAO = 2;
	
	/** Id da assinatura. */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
           parameters={ @Parameter(name="sequence_name", value="biblioteca.material_informacional_sequence") })
	@Column(name = "id_assinatura")
	private int id;

	/**  Título da assinatura Exemplo = "veja"  */
	@Column(name = "titulo", nullable=false)
	private String titulo;
	
	/** Para consultas sem acento  */
	@Column(name = "titulo_ascii", nullable=false)
	private String tituloAscii;
	
	
	/**  O código que identifica a assinatura. Para assinatura de compra é o código do assinante.
	 *  Para assinaturas de doação o sistema vai gerar o número = ano + código identifica a biblioteca + número sequencial assinatura*/
	@Column(name = "codigo", nullable=false)
	private String codigo;

	
	/** Quando a assinatura vai começar */
	@Column(name="data_inicio_assinatura" )
	@Temporal(TemporalType.DATE)
	private Date dataInicioAssinatura;

	/** Quando a assinatura deve terminar  */
	@Column(name="data_termino_assinatura" )
	@Temporal(TemporalType.DATE)
	private Date dataTerminoAssinatura;

	/** o número do primeiro fascículo */
	@Column(name="numero_primeiro_fasciculo", nullable=false)
	private Integer numeroPrimeiroFasciculo;

	/** o número do último fascículo (opcional) */
	@Column(name="numero_ultimo_fasciculo" )
	private Integer numeroUltimoFasciculo;

	/**  o número do fascículo atualmente registrado, o sistema usa para sugerir o próximo número do fascículo */
	@Column(name="numero_fasciculo_atual" )
	private Integer numeroFasciculoAtual;

	/** o número do volume do primeiro item, se tiver (opcional) */
	@Column(name="numero_primeiro_volume" )
	private Integer numeroPrimeiroVolume;
	
	/** o número do volume do último item, se tiver (opcional) */
	@Column(name="numero_ultimo_volume" )
	private Integer numeroUltimoVolume;

	/** o número do volume atualmente registrado, o sistema usa para sugerir o próximo número do volume */
	@Column(name="numero_volume_atual" )
	private Integer numeroVolumeAtual;

	/** Corresponde ao campo 022$a bibliográfico. */
	@Column(name="issn")
	private String issn;
	
	/** Indica se os fascículos da assinatura foram comprados ou doados. */
	@Column(name="modalidade_aquisicao")
	private Short modalidadeAquisicao = MODALIDADE_COMPRA;

	
	
	
	/**  Número sequencial responsável por gerar uma parte do código de barras dos fascículo. O restante do
	 * código de barras é gerado pelo código da assinatura. */
	@Column(name = "numero_gerador_fasciculo", nullable=false)
	protected int numeroGeradorFasciculo;
	
	
	
	

	/** Informa a qual biblioteca os fascículos irão ser destinados */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "id_biblioteca", referencedColumnName = "id_biblioteca")
	private Biblioteca unidadeDestino;


	/** Guarda a data do último fascículo registrado, usado para otimizar relatórios */
	@Column(name="data_ultima_chegada_fasciculo" )
	private Date dataUltimaChegadaFasciculo;


	/** Indica se uma assinatura é internacional ou não, é usado em um relatório de periódicos */
	@Column(name="internacional", nullable=false)
	private Boolean internacional;

	
	/** Informa a frequência que os periódicos dessa assinatura vão ter, a partir de qual momento é considerado que eles deixaram de chegar na biblioteca. */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "id_frequencia_periodicos", referencedColumnName = "id_frequencia_periodicos")
	protected FrequenciaPeriodicos frequenciaPeriodicos;
	

	/** Os fascículos do título ao qual a assinatura pertence */
	@OneToMany(cascade = { CascadeType.REMOVE }, fetch = FetchType.LAZY, mappedBy = "assinatura")
	private List<Fasciculo> fasciculos;


	/** O título da assinatura */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="id_titulo_catalografico", referencedColumnName="id_titulo_catalografico")
	private TituloCatalografico tituloCatalografico;

	/** Uma assinatura de compra pode ser renovada várias vezes, o sistema deve guardar o histórico dessas renovações. */
	@OneToMany(cascade = { CascadeType.REMOVE }, fetch = FetchType.LAZY, mappedBy = "assinatura")
	private List<RenovacaoAssinatura> renovacoes;
	
	
	/**
	 *   Se as assinaturas não possuírem fascículos registros, nem fascículos registrados ativos (não baixados)
	 * , elas podem ser removidas do sistema.
	 */
	@Column(name="ativa" )
	private boolean ativa = true;
	
	
	
	/**  Guarda a quantidade de fascículos da assinatura temporariamente, para não precisar percorrer
	 * toda a lista de fascículos para contar. */
	@Transient
	private int quantidadeFasciculos;
	
	

	///////////////  INFORMACOES DE REFERENCIA NO SIPAC PARA POSTERIOR CONSULTA  ///////////////


	/**
	* Guarda o id do bem que gerou essa assinatura, nesse caso o que é tombando é a assinatura.
	* a partir desse id é possível obter informações como o preço da compra da assinatura;
	*/
	@Column(name="id_bem")
	private Integer idBem;



	////////////////////////////INFORMACOES AUDITORIA  ///////////////////////////////////////


	/** Registro de entrada de criação. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_criacao")
	@CriadoPor
	private RegistroEntrada registroCriacao;

	/**
	 * Data de cadastro.
	 */
	@CriadoEm
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_criacao")
	private Date dataCriacao;

	/**
	 * Registro entrada  do usuário que realizou a última atualização.
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_ultima_atualizacao")
	@AtualizadoPor
	private RegistroEntrada registroUltimaAtualizacao;

	/**
	 * Data da última atualização.
	 */
	@AtualizadoEm
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_ultima_atualizacao")
	private Date dataUltimaAtualizacao;

	//////////////////////////////////////////////////////////////////////////////////////////

	
	/** código da migração */
	private String codmerg;
	
	
	/** Guarda o nome do criador da assinatura, temporariamente */
	@Transient
	private String nomeCriador;
	
	
	/**
	 * Guarda se esse objeto foi selecionado ou não nas páginas do sistema
	 */
	@Transient
	private boolean selecionada;
	
	
	
	/**
	 * Para o hibernate e JSF
	 */
	public Assinatura(){
		// Construtor padrão.
	}
	
	/**
	 * Para o hibernate e JSF
	 */
	public Assinatura(int id){
		this.id = id;
	}
	
	/**
	 * Construtor usado para o hibernate usar na pesquisa de assinatura na parte de aquisição.
	 */
	public Assinatura(int id, String titulo, String codigo) {
		this(id);
		this.titulo = titulo;
		this.codigo = codigo;
	}
	
	


	
	
	
	/**
	 * Retorna a quantidade de fascículos "ativos" da assinatura. OS que devem aparecer para o usuário
	 */
	public int getQuantidadeFasciculosAtivos(){
		
		int quantidade = 0;
		
		if(fasciculos != null){
			for (Fasciculo f : fasciculos) {
				if( f.isIncluidoAcervo() && ! f.isDadoBaixa() && f.isAtivo() )
					quantidade++;
			}
		}
		
		return quantidade;
	}


	/**
	 * Retorna os fascículos
	 */
	public List<Fasciculo> getFasciculos() {
		return fasciculos;
	}
	
	
	
	/**
	 * Adiciona um novo fascículo
	 */
	public void addFasciculo(Fasciculo f){
		if(fasciculos == null)
			fasciculos = new ArrayList<Fasciculo>();

		fasciculos.add(f);
	}

	
	/**
	 * Verifica se a assinatura já venceu.
	 */
	public boolean isEstaVencida(){
		
		if(isAssinaturaDeCompra()){
			
			if(dataTerminoAssinatura == null)
				return false;
			
			long qtdDias = CalendarUtils.calculoDias(new Date(), dataTerminoAssinatura);
			
			if(qtdDias >= 0){
				return false;
			}else{
				return true;
			}
			
		}else{
			
			return false; // assinatura de doação nunca vencem, pois dificilmente tem data e término da assinatura
		}
		
	}
	
	
	
	
	/**
	 * Validações para a criação e edição de assinaturas.
	 * 
	 * @see br.ufrn.arq.negocio.validacao.Validatable#validate()
	 */
	@Override
	public ListaMensagens validate() {
		
		ListaMensagens erros = new ListaMensagens();
			
		if(dataInicioAssinatura != null && dataTerminoAssinatura != null){
			if(CalendarUtils.calculaQuantidadeDiasEntreDatasIntervaloFechado(dataInicioAssinatura, dataTerminoAssinatura) <= 1){
				erros.addErro("A data de término da assinatura deve ser maior que a data de início dela.");
			}
		}
		
		if( unidadeDestino.getId() == -1){
			erros.addErro("Escolha a unidade de destino dos fascículos dessa assinatura.");
		}
		
		if( frequenciaPeriodicos == null || frequenciaPeriodicos.getId() == -1){
			erros.addErro("Escolha a periodicidade dos fascículos dessa assinatura.");
		}
		
		if(titulo.isEmpty()){
			erros.addErro("É preciso informar o título da assinatura");
		}else{
			if(titulo.length() > 200 || titulo.length() < 3){
				erros.addErro("O tamanho para o título da assinatura deve estar entre 3 e 200 caracteres.");
			}
		}
		
		if( StringUtils.isEmpty(codigo)){
			erros.addErro(" Informe o código da Assinatura.");
		}else{
			
			if( codigo.length() > 40){
				erros.addErro("O tamanho máximo para o código da assinatura é de 40 caracteres.");
			}
		}
		
		
		if(numeroPrimeiroFasciculo == null ){
			erros.addErro("Informe o número do primeiro Fascículo");
		}
	
		
		if(numeroPrimeiroFasciculo != null && numeroUltimoFasciculo != null && numeroUltimoFasciculo < numeroPrimeiroFasciculo){
			erros.addErro("O número do último Fascículo precisa ser maior que o do primeiro");
		}

		
		if(numeroPrimeiroVolume != null && numeroUltimoVolume != null){
			if(numeroUltimoVolume < numeroPrimeiroVolume){
				erros.addErro("O número do último Volume precisa ser maior que o do primeiro");
			}
		}
		
		return erros;
	}
	
	
	
	
	/**
	 *   Testa se a assinatura é de compras
	 */
	public boolean isAssinaturaDeCompra(){
		if(modalidadeAquisicao == null)
			return false;
		return modalidadeAquisicao.equals(MODALIDADE_COMPRA);
	}
	
	
	
	/**
	 *   Testa se a assinatura é de doação
	 */
	public boolean isAssinaturaDeDoacao(){
		if(modalidadeAquisicao == null)
			return false;
		return modalidadeAquisicao.equals(MODALIDADE_DOACAO);
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
		Assinatura other = (Assinatura) obj;
		if (id != other.id)
			return false;
		return true;
	}
	
	
	
	// sets e gets


	@Override
	public int getId() {
		return id;
	}

	public TituloCatalografico getTituloCatalografico() {
		return tituloCatalografico;
	}


	public Integer getIdBem() {
		return idBem;
	}

	@Override
	public void setId(int id) {
		this.id = id;
	}
	
	public void setFasciculos(List<Fasciculo> fasciculos) {
		this.fasciculos = fasciculos;
	}

	public void setTituloCatalografico(TituloCatalografico tituloCatalografico) {
		this.tituloCatalografico = tituloCatalografico;
	}

	public Boolean getInternacional() {
		return internacional;
	}

	public void setInternacional(Boolean internacional) {
		this.internacional = internacional;
	}

	public void setIdBem(Integer idBem) {
		this.idBem = idBem;
	}

	public String getTitulo() {
		return titulo;
	}

	/** Seta o título e atualiza o campo <em>título ASCII</em>.*/
	public void setTitulo(String titulo) {
		this.titulo = titulo;
		if(titulo != null)
			this.tituloAscii = " "+CatalogacaoUtil.retiraPontuacaoCamposBuscas(StringUtils.toAsciiAndUpperCase(titulo))+" ";
	}

	public String getCodigo() {
		return codigo;
	}
	
	public String getCodigoTitulo() {
		return codigo+" - "+titulo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public Biblioteca getUnidadeDestino() {
		return unidadeDestino;
	}

	public void setUnidadeDestino(Biblioteca unidadeDestino) {
		this.unidadeDestino = unidadeDestino;
	}

	public Date getDataInicioAssinatura() {
		return dataInicioAssinatura;
	}

	public void setDataInicioAssinatura(Date dataInicioAssinatura) {
		this.dataInicioAssinatura = dataInicioAssinatura;
	}

	public Date getDataTerminoAssinatura() {
		return dataTerminoAssinatura;
	}

	public void setDataTerminoAssinatura(Date dataTerminoAssinatura) {
		this.dataTerminoAssinatura = dataTerminoAssinatura;
	}

	public Date getDataUltimaChegadaFasciculo() {
		return dataUltimaChegadaFasciculo;
	}

	public void setDataUltimaChegadaFasciculo(Date dataUltimaChegadaFasciculo) {
		this.dataUltimaChegadaFasciculo = dataUltimaChegadaFasciculo;
	}
	
	/// para mostrar na página
	public RegistroEntrada getRegistroCriacao() {
		return registroCriacao;
	}

	public void setRegistroCriacao(RegistroEntrada registroCriacao) {
		this.registroCriacao = registroCriacao;
	}

	public Date getDataCriacao() {
		return dataCriacao;
	}

	public void setDataCriacao(Date dataCriacao) {
		this.dataCriacao = dataCriacao;
	}

	public RegistroEntrada getRegistroUltimaAtualizacao() {
		return registroUltimaAtualizacao;
	}

	public void setRegistroUltimaAtualizacao(
			RegistroEntrada registroUltimaAtualizacao) {
		this.registroUltimaAtualizacao = registroUltimaAtualizacao;
	}

	public Date getDataUltimaAtualizacao() {
		return dataUltimaAtualizacao;
	}

	public void setDataUltimaAtualizacao(Date dataUltimaAtualizacao) {
		this.dataUltimaAtualizacao = dataUltimaAtualizacao;
	}

	public String getTituloAscii() {
		return tituloAscii;
	}

	public Integer getNumeroPrimeiroFasciculo() {
		return numeroPrimeiroFasciculo;
	}

	public void setNumeroPrimeiroFasciculo(Integer numeroPrimeiroFasciculo) {
		this.numeroPrimeiroFasciculo = numeroPrimeiroFasciculo;
	}

	public Integer getNumeroUltimoFasciculo() {
		return numeroUltimoFasciculo;
	}

	public void setNumeroUltimoFasciculo(Integer numeroUltimoFasciculo) {
		this.numeroUltimoFasciculo = numeroUltimoFasciculo;
	}

	public Integer getNumeroFasciculoAtual() {
		return numeroFasciculoAtual;
	}

	public void setNumeroFasciculoAtual(Integer numeroFasciculoAtual) {
		this.numeroFasciculoAtual = numeroFasciculoAtual;
	}

	public Integer getNumeroPrimeiroVolume() {
		return numeroPrimeiroVolume;
	}

	public void setNumeroPrimeiroVolume(Integer numeroPrimeiroVolume) {
		this.numeroPrimeiroVolume = numeroPrimeiroVolume;
	}

	public Integer getNumeroUltimoVolume() {
		return numeroUltimoVolume;
	}

	public void setNumeroUltimoVolume(Integer numeroUltimoVolume) {
		this.numeroUltimoVolume = numeroUltimoVolume;
	}

	public Integer getNumeroVolumeAtual() {
		return numeroVolumeAtual;
	}

	public void setNumeroVolumeAtual(Integer numeroVolumeAtual) {
		this.numeroVolumeAtual = numeroVolumeAtual;
	}

	public String getCodmerg() {
		return codmerg;
	}

	public void setCodmerg(String codmerg) {
		this.codmerg = codmerg;
	}

	public short getModalidadeCompra() {
		return MODALIDADE_COMPRA;
	}

	public short getModalidadeDoacao() {
		return MODALIDADE_DOACAO;
	}

	public Short getModalidadeAquisicao() {
		return modalidadeAquisicao;
	}

	public void setModalidadeAquisicao(Short modalidadeAquisicao) {
		this.modalidadeAquisicao = modalidadeAquisicao;
	}

	public int getNumeroGeradorFasciculo() {
		return numeroGeradorFasciculo;
	}

	public void setNumeroGeradorFasciculo(int numeroGeradorFasciculo) {
		this.numeroGeradorFasciculo = numeroGeradorFasciculo;
	}

	public int getQuantidadeFasciculos() {
		return quantidadeFasciculos;
	}

	public void setQuantidadeFasciulos(int quantidadeFasciculos) {
		this.quantidadeFasciculos = quantidadeFasciculos;
	}

	public String getNomeCriador() {
		return nomeCriador;
	}

	public void setNomeCriador(String nomeCriador) {
		this.nomeCriador = nomeCriador;
	}

	public boolean isSelecionada() {
		return selecionada;
	}

	public void setSelecionada(boolean selecionada) {
		this.selecionada = selecionada;
	}

	public List<RenovacaoAssinatura> getRenovacoes() {
		return renovacoes;
	}

	public void setRenovacoes(List<RenovacaoAssinatura> renovacoes) {
		this.renovacoes = renovacoes;
	}
	
	public boolean isAtiva() {
		return ativa;
	}

	public void setAtiva(boolean ativa) {
		this.ativa = ativa;
	}

	public String getIssn() {
		return issn;
	}

	public void setIssn(String issn) {
		this.issn = issn;
	}

	public FrequenciaPeriodicos getFrequenciaPeriodicos() {
		return frequenciaPeriodicos;
	}

	public void setFrequenciaPeriodicos(FrequenciaPeriodicos frequenciaPeriodicos) {
		this.frequenciaPeriodicos = frequenciaPeriodicos;
	}
	
}
