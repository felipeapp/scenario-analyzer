/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
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
 *       Guarda as informa��es sobre as assinaturas de peri�dicos. Assinatura n�o s�o tombadas no
 *  patrim�nio da institui��o.
 *
 * @author Jadson
 * @since 23/03/2009
 * @version 1.0 cria��o da classe
 *
 */
@Entity
@Table(name = "assinatura", schema = "biblioteca")
public class Assinatura implements Validatable {

	/** Modalidade de aquisi��o por compra. */
	public final static short MODALIDADE_COMPRA = 1;
	
	/** Modalidade de aquisi��o por doa��o. */
	public final static short MODALIDADE_DOACAO = 2;
	
	/** Id da assinatura. */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
           parameters={ @Parameter(name="sequence_name", value="biblioteca.material_informacional_sequence") })
	@Column(name = "id_assinatura")
	private int id;

	/**  T�tulo da assinatura Exemplo = "veja"  */
	@Column(name = "titulo", nullable=false)
	private String titulo;
	
	/** Para consultas sem acento  */
	@Column(name = "titulo_ascii", nullable=false)
	private String tituloAscii;
	
	
	/**  O c�digo que identifica a assinatura. Para assinatura de compra � o c�digo do assinante.
	 *  Para assinaturas de doa��o o sistema vai gerar o n�mero = ano + c�digo identifica a biblioteca + n�mero sequencial assinatura*/
	@Column(name = "codigo", nullable=false)
	private String codigo;

	
	/** Quando a assinatura vai come�ar */
	@Column(name="data_inicio_assinatura" )
	@Temporal(TemporalType.DATE)
	private Date dataInicioAssinatura;

	/** Quando a assinatura deve terminar  */
	@Column(name="data_termino_assinatura" )
	@Temporal(TemporalType.DATE)
	private Date dataTerminoAssinatura;

	/** o n�mero do primeiro fasc�culo */
	@Column(name="numero_primeiro_fasciculo", nullable=false)
	private Integer numeroPrimeiroFasciculo;

	/** o n�mero do �ltimo fasc�culo (opcional) */
	@Column(name="numero_ultimo_fasciculo" )
	private Integer numeroUltimoFasciculo;

	/**  o n�mero do fasc�culo atualmente registrado, o sistema usa para sugerir o pr�ximo n�mero do fasc�culo */
	@Column(name="numero_fasciculo_atual" )
	private Integer numeroFasciculoAtual;

	/** o n�mero do volume do primeiro item, se tiver (opcional) */
	@Column(name="numero_primeiro_volume" )
	private Integer numeroPrimeiroVolume;
	
	/** o n�mero do volume do �ltimo item, se tiver (opcional) */
	@Column(name="numero_ultimo_volume" )
	private Integer numeroUltimoVolume;

	/** o n�mero do volume atualmente registrado, o sistema usa para sugerir o pr�ximo n�mero do volume */
	@Column(name="numero_volume_atual" )
	private Integer numeroVolumeAtual;

	/** Corresponde ao campo 022$a bibliogr�fico. */
	@Column(name="issn")
	private String issn;
	
	/** Indica se os fasc�culos da assinatura foram comprados ou doados. */
	@Column(name="modalidade_aquisicao")
	private Short modalidadeAquisicao = MODALIDADE_COMPRA;

	
	
	
	/**  N�mero sequencial respons�vel por gerar uma parte do c�digo de barras dos fasc�culo. O restante do
	 * c�digo de barras � gerado pelo c�digo da assinatura. */
	@Column(name = "numero_gerador_fasciculo", nullable=false)
	protected int numeroGeradorFasciculo;
	
	
	
	

	/** Informa a qual biblioteca os fasc�culos ir�o ser destinados */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "id_biblioteca", referencedColumnName = "id_biblioteca")
	private Biblioteca unidadeDestino;


	/** Guarda a data do �ltimo fasc�culo registrado, usado para otimizar relat�rios */
	@Column(name="data_ultima_chegada_fasciculo" )
	private Date dataUltimaChegadaFasciculo;


	/** Indica se uma assinatura � internacional ou n�o, � usado em um relat�rio de peri�dicos */
	@Column(name="internacional", nullable=false)
	private Boolean internacional;

	
	/** Informa a frequ�ncia que os peri�dicos dessa assinatura v�o ter, a partir de qual momento � considerado que eles deixaram de chegar na biblioteca. */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "id_frequencia_periodicos", referencedColumnName = "id_frequencia_periodicos")
	protected FrequenciaPeriodicos frequenciaPeriodicos;
	

	/** Os fasc�culos do t�tulo ao qual a assinatura pertence */
	@OneToMany(cascade = { CascadeType.REMOVE }, fetch = FetchType.LAZY, mappedBy = "assinatura")
	private List<Fasciculo> fasciculos;


	/** O t�tulo da assinatura */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="id_titulo_catalografico", referencedColumnName="id_titulo_catalografico")
	private TituloCatalografico tituloCatalografico;

	/** Uma assinatura de compra pode ser renovada v�rias vezes, o sistema deve guardar o hist�rico dessas renova��es. */
	@OneToMany(cascade = { CascadeType.REMOVE }, fetch = FetchType.LAZY, mappedBy = "assinatura")
	private List<RenovacaoAssinatura> renovacoes;
	
	
	/**
	 *   Se as assinaturas n�o possu�rem fasc�culos registros, nem fasc�culos registrados ativos (n�o baixados)
	 * , elas podem ser removidas do sistema.
	 */
	@Column(name="ativa" )
	private boolean ativa = true;
	
	
	
	/**  Guarda a quantidade de fasc�culos da assinatura temporariamente, para n�o precisar percorrer
	 * toda a lista de fasc�culos para contar. */
	@Transient
	private int quantidadeFasciculos;
	
	

	///////////////  INFORMACOES DE REFERENCIA NO SIPAC PARA POSTERIOR CONSULTA  ///////////////


	/**
	* Guarda o id do bem que gerou essa assinatura, nesse caso o que � tombando � a assinatura.
	* a partir desse id � poss�vel obter informa��es como o pre�o da compra da assinatura;
	*/
	@Column(name="id_bem")
	private Integer idBem;



	////////////////////////////INFORMACOES AUDITORIA  ///////////////////////////////////////


	/** Registro de entrada de cria��o. */
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
	 * Registro entrada  do usu�rio que realizou a �ltima atualiza��o.
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_ultima_atualizacao")
	@AtualizadoPor
	private RegistroEntrada registroUltimaAtualizacao;

	/**
	 * Data da �ltima atualiza��o.
	 */
	@AtualizadoEm
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_ultima_atualizacao")
	private Date dataUltimaAtualizacao;

	//////////////////////////////////////////////////////////////////////////////////////////

	
	/** c�digo da migra��o */
	private String codmerg;
	
	
	/** Guarda o nome do criador da assinatura, temporariamente */
	@Transient
	private String nomeCriador;
	
	
	/**
	 * Guarda se esse objeto foi selecionado ou n�o nas p�ginas do sistema
	 */
	@Transient
	private boolean selecionada;
	
	
	
	/**
	 * Para o hibernate e JSF
	 */
	public Assinatura(){
		// Construtor padr�o.
	}
	
	/**
	 * Para o hibernate e JSF
	 */
	public Assinatura(int id){
		this.id = id;
	}
	
	/**
	 * Construtor usado para o hibernate usar na pesquisa de assinatura na parte de aquisi��o.
	 */
	public Assinatura(int id, String titulo, String codigo) {
		this(id);
		this.titulo = titulo;
		this.codigo = codigo;
	}
	
	


	
	
	
	/**
	 * Retorna a quantidade de fasc�culos "ativos" da assinatura. OS que devem aparecer para o usu�rio
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
	 * Retorna os fasc�culos
	 */
	public List<Fasciculo> getFasciculos() {
		return fasciculos;
	}
	
	
	
	/**
	 * Adiciona um novo fasc�culo
	 */
	public void addFasciculo(Fasciculo f){
		if(fasciculos == null)
			fasciculos = new ArrayList<Fasciculo>();

		fasciculos.add(f);
	}

	
	/**
	 * Verifica se a assinatura j� venceu.
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
			
			return false; // assinatura de doa��o nunca vencem, pois dificilmente tem data e t�rmino da assinatura
		}
		
	}
	
	
	
	
	/**
	 * Valida��es para a cria��o e edi��o de assinaturas.
	 * 
	 * @see br.ufrn.arq.negocio.validacao.Validatable#validate()
	 */
	@Override
	public ListaMensagens validate() {
		
		ListaMensagens erros = new ListaMensagens();
			
		if(dataInicioAssinatura != null && dataTerminoAssinatura != null){
			if(CalendarUtils.calculaQuantidadeDiasEntreDatasIntervaloFechado(dataInicioAssinatura, dataTerminoAssinatura) <= 1){
				erros.addErro("A data de t�rmino da assinatura deve ser maior que a data de in�cio dela.");
			}
		}
		
		if( unidadeDestino.getId() == -1){
			erros.addErro("Escolha a unidade de destino dos fasc�culos dessa assinatura.");
		}
		
		if( frequenciaPeriodicos == null || frequenciaPeriodicos.getId() == -1){
			erros.addErro("Escolha a periodicidade dos fasc�culos dessa assinatura.");
		}
		
		if(titulo.isEmpty()){
			erros.addErro("� preciso informar o t�tulo da assinatura");
		}else{
			if(titulo.length() > 200 || titulo.length() < 3){
				erros.addErro("O tamanho para o t�tulo da assinatura deve estar entre 3 e 200 caracteres.");
			}
		}
		
		if( StringUtils.isEmpty(codigo)){
			erros.addErro(" Informe o c�digo da Assinatura.");
		}else{
			
			if( codigo.length() > 40){
				erros.addErro("O tamanho m�ximo para o c�digo da assinatura � de 40 caracteres.");
			}
		}
		
		
		if(numeroPrimeiroFasciculo == null ){
			erros.addErro("Informe o n�mero do primeiro Fasc�culo");
		}
	
		
		if(numeroPrimeiroFasciculo != null && numeroUltimoFasciculo != null && numeroUltimoFasciculo < numeroPrimeiroFasciculo){
			erros.addErro("O n�mero do �ltimo Fasc�culo precisa ser maior que o do primeiro");
		}

		
		if(numeroPrimeiroVolume != null && numeroUltimoVolume != null){
			if(numeroUltimoVolume < numeroPrimeiroVolume){
				erros.addErro("O n�mero do �ltimo Volume precisa ser maior que o do primeiro");
			}
		}
		
		return erros;
	}
	
	
	
	
	/**
	 *   Testa se a assinatura � de compras
	 */
	public boolean isAssinaturaDeCompra(){
		if(modalidadeAquisicao == null)
			return false;
		return modalidadeAquisicao.equals(MODALIDADE_COMPRA);
	}
	
	
	
	/**
	 *   Testa se a assinatura � de doa��o
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

	/** Seta o t�tulo e atualiza o campo <em>t�tulo ASCII</em>.*/
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
	
	/// para mostrar na p�gina
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
