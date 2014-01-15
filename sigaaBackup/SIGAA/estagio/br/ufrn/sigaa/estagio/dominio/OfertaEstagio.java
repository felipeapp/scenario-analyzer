/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 07/10/2010
 */
package br.ufrn.sigaa.estagio.dominio;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.seguranca.log.CriadoPor;
import br.ufrn.arq.util.Formatador;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.dominio.Curso;

/**
 * Entidade que representa as Ofertas de Estágio publicadas pelo concedente de estágio.
 * 
 * @author arlindo
 *
 */
@Entity
@Table(name = "oferta_estagio", schema = "estagio")
public class OfertaEstagio implements Validatable {
	
	/** Chave primária */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="estagio.hibernate_sequence") })	
	@Column(name = "id_oferta_estagio")		
	private int id;
	
	/** Concedente de estágio que anunciou a oferta */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_concedente_estagio")		
	private ConcedenteEstagio concedente;
	
	/**
	 * Cursos que terão as Ofertas de Estágio publicadas.
	 */
	@ManyToMany
	@JoinTable(name = "oferta_cursos", schema="estagio",
		    joinColumns = @JoinColumn (name="id_oferta_estagio"),
		    inverseJoinColumns = @JoinColumn(name="id_curso"))			
	private Collection<Curso> cursosOfertados;
	
	/**
	 * Interesses registrados para a oferta
	 */
	@OneToMany(mappedBy = "oferta", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<InteresseOferta> interesses = new ArrayList<InteresseOferta>();	
	
	/** Título da Oferta de estágio */
	private String titulo;
	
	/** Descrição detalhada da oferta de estágio */
	private String descricao;
	
	/** Quantidade de vagas disponíveis para a oferta */
	@Column(name="numero_vagas")
	private Integer numeroVagas;
	
	/** Valor da bolsa auxilio do estágio */
	@Column(name="valor_bolsa")
	private Double valorBolsa;
	
	/** Valor do auxilio de transporte do estágio */
	@Column(name="valor_aux_transporte")
	private Double valorAuxTransporte;	
	
	/** Data de início de publicação nos portais */
	@Column(name="data_inicio_publicacao")
	private Date dataInicioPublicacao;
	
	/** Data de fim de publicação nos portais */
	@Column(name="data_fim_publicacao")
	private Date dataFimPublicacao;
	
	/** Data do cadastro. */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_cadastro")
	@CriadoEm
	private Date dataCadastro;
	
	/** Registro entrada de quem cadastrou. */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="id_registro_entrada")
	@CriadoPor
	private RegistroEntrada registroCadastro;
	
	/** Status que indica a situação atual da oferta */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="status")
	private StatusOfertaEstagio status;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public ConcedenteEstagio getConcedente() {
		return concedente;
	}

	public void setConcedente(ConcedenteEstagio concedente) {
		this.concedente = concedente;
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public Integer getNumeroVagas() {
		return numeroVagas;
	}

	public void setNumeroVagas(Integer numeroVagas) {
		this.numeroVagas = numeroVagas;
	}

	public Double getValorBolsa() {
		return valorBolsa;
	}

	public void setValorBolsa(Double valorBolsa) {
		this.valorBolsa = valorBolsa;
	}

	public Date getDataInicioPublicacao() {
		return dataInicioPublicacao;
	}

	public void setDataInicioPublicacao(Date dataInicioPublicacao) {
		this.dataInicioPublicacao = dataInicioPublicacao;
	}

	public Date getDataFimPublicacao() {
		return dataFimPublicacao;
	}

	public void setDataFimPublicacao(Date dataFimPublicacao) {
		this.dataFimPublicacao = dataFimPublicacao;
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

	public Collection<Curso> getCursosOfertados() {
		return cursosOfertados;
	}

	public void setCursosOfertados(Collection<Curso> cursosOfertados) {
		this.cursosOfertados = cursosOfertados;
	}

	public StatusOfertaEstagio getStatus() {
		return status;
	}

	public void setStatus(StatusOfertaEstagio status) {
		this.status = status;
	}

	public List<InteresseOferta> getInteresses() {
		return interesses;
	}

	public void setInteresses(List<InteresseOferta> interesses) {
		this.interesses = interesses;
	}

	public Double getValorAuxTransporte() {
		return valorAuxTransporte;
	}

	public void setValorAuxTransporte(Double valorAuxTransporte) {
		this.valorAuxTransporte = valorAuxTransporte;
	}
	
	/**
	 * Verifica se a oferta de estágio está aprovada
	 * @return
	 */
	@Transient
	public boolean isAprovado(){
		return status.getId() == StatusOfertaEstagio.APROVADO;
	}
	
	/**
	 * Verifica se a oferta de estágio está pendente de análise.
	 * @return
	 */
	@Transient
	public boolean isPendenteAnalise(){
		return status.getId() == StatusOfertaEstagio.CADASTRADO;
	}

	/**
	 * Valida os atributos da oferta de estágio. 
	 * @see br.ufrn.arq.negocio.validacao.Validatable#validate() 
	 */	
	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();
		
		ValidatorUtil.validateRequired(titulo, "Título", lista);
		ValidatorUtil.validateRequired(descricao, "Descrição da Oferta", lista);
		ValidatorUtil.validateRequired(numeroVagas, "Número de Vagas", lista);
		
		if (concedente.getConvenioEstagio().isOrgaoFederal() && concedente.getConvenioEstagio().getTipoConvenio().getId() == TipoEstagio.ESTAGIO_CURRICULAR_NAO_OBRIGATORIO) {
			double minBolsa = ParametroHelper.getInstance().getParametroDouble(ParametrosEstagio.VALOR_MINIMO_BOLSA_ESTAGIO_NAO_OBRIGATORIO);
			if (valorBolsa < minBolsa)
				lista.addErro("Valor da Bolsa: O valor deve ser maior ou igual a R$ " + Formatador.getInstance().formatarMoeda(minBolsa));
			double minTransporte = ParametroHelper.getInstance().getParametroDouble(ParametrosEstagio.VALOR_MINIMO_TRANSPORTE_ESTAGIO_NAO_OBRIGATORIO);
			if (valorAuxTransporte < minTransporte)
				lista.addErro("Valor do Auxílio Transporte: O valor deve ser maior ou igual a R$ " + Formatador.getInstance().formatarMoeda(minTransporte));
		}
		
		ValidatorUtil.validateRequired(dataInicioPublicacao, "Início da Publicação", lista);
		ValidatorUtil.validateRequired(dataFimPublicacao, "Fim da Publicação", lista);		
		ValidatorUtil.validaInicioFim(dataInicioPublicacao, dataFimPublicacao, "Data de Publicação", lista);
				
		ValidatorUtil.validateEmptyCollection("Informe os Cursos para os quais as vagas serão Ofertadas", cursosOfertados, lista);
		
								
		return lista;
	}		
	
	/**
	 * Adiciona um curso à lista de cursos ofertados
	 * 
	 * @param curso
	 */
	public void adicionarCurso(Curso curso) {
		if (cursosOfertados == null) {
			cursosOfertados = new ArrayList<Curso>();	
		}
		if (!cursosOfertados.contains(curso)) {
			cursosOfertados.add(curso);
			Collections.sort((List<Curso>) cursosOfertados, new Comparator<Curso> () {
				public int compare(Curso c1, Curso c2) {
					return new CompareToBuilder()
						.append(c1.getDescricao(), c2.getDescricao()).toComparison();
				}
			});
		}
	}
	
	/**
	 * Remove um curso da lista de cursos ofertados
	 * 
	 * @param curso
	 */
	public void removerCurso(Curso curso) {
		if (cursosOfertados != null) {
			cursosOfertados.remove(curso);
		}
	}		
}
