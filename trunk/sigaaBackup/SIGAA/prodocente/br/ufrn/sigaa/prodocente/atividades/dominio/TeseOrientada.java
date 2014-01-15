/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '05/12/2006'
 *
 */
package br.ufrn.sigaa.prodocente.atividades.dominio;

import java.util.Date;
import java.util.HashMap;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import br.ufrn.arq.dao.CampoAtivo;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.MensagemAviso;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.Formatador;
import br.ufrn.arq.util.HibernateUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.dominio.AreaConhecimentoCnpq;
import br.ufrn.sigaa.dominio.InstituicoesEnsino;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ensino.stricto.dominio.DadosDefesa;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.Servidor;
import br.ufrn.sigaa.prodocente.relatorios.dominio.ViewAtividadeBuilder;
import br.ufrn.sigaa.projetos.dominio.EntidadeFinanciadora;

/**
 * Entidade que controla a orientação de pós-graduação do docente. É a partir desta entidade que são
 * contabilizados os pontos de produção intelectual.
 *
 * @author eric
 */
@Entity
@Table(name = "tese_orientada", schema = "prodocente")
public class TeseOrientada implements Validatable, ViewAtividadeBuilder {

	/** Chave primária. */
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "id_tese", nullable = false)
	private int id;

	/** Instituição de ensino onde a tese foi produzida. */
	@ManyToOne
	@JoinColumn(name = "id_instituicao_ensino")
	private InstituicoesEnsino instituicaoEnsino;

	/** Instituição de ensino onde a tese foi produzida (dados históricos). */
	@Column(name = "instituicao")
	private String instituicao;

	/** Programa de pós-graduação associado à produção da tese. */
	@ManyToOne
	@JoinColumn(name = "id_programa_pos")
	private Unidade programaPos;

	/**
	 * Indica se o registro é ativo. Ao remover as produções as mesmas não serão
	 * removidas da base de dados, apenas o campo ativo será marcado como FALSE
	 */
	@Column(name = "ativo")
	@CampoAtivo
	private Boolean ativo;

	/** Área de conhecimento no CNPq da teste. */
	@JoinColumn(name = "id_area", referencedColumnName = "id_area_conhecimento_cnpq")
	@ManyToOne
	private AreaConhecimentoCnpq area;

	/** Sub-Área de conhecimento no CNPq da teste. */
	@JoinColumn(name = "id_subarea", referencedColumnName = "id_area_conhecimento_cnpq")
	@ManyToOne
	private AreaConhecimentoCnpq subArea;

	/** Tipo de orientação da teste ('O' = Orientador, 'C' = CoOrientador). */
	@Column(name = "orientacao")
	private Character orientacao;

	/** Título da tese. */
	@Column(name = "titulo", columnDefinition= HibernateUtils.TEXT_COLUMN_DEFINITION)
	private String titulo;

	/** Usado somente para dados migrados ou externos */
	@Column(name = "orientando")
	private String orientando;

	/** indica se o aluno é externo a UFRN e assim informado no campo orientando */
	@Column(name = "discente_externo")
	private Boolean DiscenteExterno;

	/* -- -- -- */
	/**
	 * Apenas para verificar se o aluno foi migrado do antigo prodocente
	 *
	 * @author Edson Anibal
	 */
	@Transient
	private Boolean discente_migrado;
	/* -- -- -- */

	@Column(name = "periodo_inicio")
	@Temporal(TemporalType.DATE)
	private Date periodoInicio;

	@Column(name = "data_publicacao")
	@Temporal(TemporalType.DATE)
	private Date dataPublicacao;

	@Column(name = "paginas")
	private Integer paginas;

	@Column(name = "informacao")
	private String informacao;

	@JoinColumn(name = "id_servidor", referencedColumnName = "id_servidor")
	@ManyToOne
	private Servidor servidor;

	@JoinColumn(name = "id_discente", referencedColumnName = "id_discente")
	@ManyToOne
	private Discente orientandoDiscente;

	@Column(name = "periodo_fim")
	@Temporal(TemporalType.DATE)
	private Date periodoFim;

	/** Usado somente para cursos de lato-sensu pagos */
	@Column(name = "pago")
	private Boolean pago;

	@Column(name = "programa")
	private String programa;

	@JoinColumn(name = "id_entidade_financiadora", referencedColumnName = "id_entidade_financiadora")
	@ManyToOne
	private EntidadeFinanciadora entidadeFinanciadora;

	@JoinColumn(name = "id_tipo_orientacao", referencedColumnName = "id_tipo_orientacao")
	@ManyToOne
	private TipoOrientacao tipoOrientacao;

	@Column(name = "desligado")
	private Boolean desligado;
	
	@JoinColumn(name = "id_dados_defesa", referencedColumnName = "id_dados_defesa")
	@ManyToOne
	private DadosDefesa dadosDefesa;
	
	/** Total de Orientadores para a tese em questão */
	@Transient
	private Integer totalOrientadores = 1;
	
	/** Cria uma nova instância de TeseOrientada */
	public TeseOrientada() {
	}

	/**
	 * Cria uma nova instância de TeseOrientada com o valor especificado.
	 *
	 * @param id
	 *            the id of the Tese
	 */
	public TeseOrientada(Integer id) {
		this.id = id;
	}

	/**
	 * Apenas para verificar se o aluno foi migrado do antigo prodocente
	 *
	 * @author Edson Anibal
	 */
	public Boolean getDiscenteMigrado() {
		return this.discente_migrado;
	}

	/**
	 * Apenas para verificar se o aluno foi migrado do antigo prodocente
	 *
	 * @author Edson Anibal
	 */
	public void setDiscenteMigrado(Boolean migrado) {
		this.discente_migrado = migrado;
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Ao remover as produções as mesmas não serão removidas da base de dados, apenas o campo ativo será marcado como
	 * FALSE
	 *
	 * @author Edson Anibal (ambar@info.ufrn.br)
	 */
	public Boolean getAtivo() {
		return this.ativo;
	}

	/**
	 * Ao remover as produções as mesmas não serão removidas da base de dados, apenas o campo ativo será marcado como
	 * FALSE
	 *
	 * @author Edson Anibal (ambar@info.ufrn.br)
	 */
	public void setAtivo(Boolean ativo) {
		this.ativo = ativo;
	}

	public AreaConhecimentoCnpq getArea() {
		return this.area;
	}

	public void setArea(AreaConhecimentoCnpq area) {
		this.area = area;
	}

	public AreaConhecimentoCnpq getSubArea() {
		return this.subArea;
	}

	public void setSubArea(AreaConhecimentoCnpq subArea) {
		this.subArea = subArea;
	}

	public Character getOrientacao() {
		return this.orientacao;
	}

	public void setOrientacao(Character orientacao) {
		this.orientacao = orientacao;
	}

	public String getInstituicao() {
		return this.instituicao;
	}

	public void setInstituicao(String instituicao) {
		this.instituicao = instituicao;
	}

	public String getTitulo() {
		return this.titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public String getOrientando() {
		return this.orientando;
	}

	public void setOrientando(String orientando) {
		this.orientando = orientando;
	}

	public Boolean getDiscenteExterno() {
		return this.DiscenteExterno;
	}

	public void setDiscenteExterno(Boolean discenteExterno) {
		this.DiscenteExterno = discenteExterno;
	}

	public Date getPeriodoInicio() {
		return this.periodoInicio;
	}

	public void setPeriodoInicio(Date periodoInicio) {
		this.periodoInicio = periodoInicio;
	}

	public Date getDataPublicacao() {
		return this.dataPublicacao;
	}

	public void setDataPublicacao(Date dataPublicacao) {
		this.dataPublicacao = dataPublicacao;
	}

	public Integer getPaginas() {
		return this.paginas;
	}

	public void setPaginas(Integer paginas) {
		this.paginas = paginas;
	}

	public String getInformacao() {
		return this.informacao;
	}

	public void setInformacao(String informacao) {
		this.informacao = informacao;
	}

	public Servidor getServidor() {
		return this.servidor;
	}

	public void setServidor(Servidor servidor) {
		this.servidor = servidor;
	}

	public Date getPeriodoFim() {
		return this.periodoFim;
	}

	public void setPeriodoFim(Date periodoFim) {
		this.periodoFim = periodoFim;
	}

	public Boolean getPago() {
		return this.pago;
	}

	public void setPago(Boolean pago) {
		this.pago = pago;
	}

	public String getPrograma() {
		return this.programa;
	}

	public void setPrograma(String programa) {
		this.programa = programa;
	}

	public EntidadeFinanciadora getEntidadeFinanciadora() {
		return this.entidadeFinanciadora;
	}

	public void setEntidadeFinanciadora(EntidadeFinanciadora entidadeFinanciadora) {
		this.entidadeFinanciadora = entidadeFinanciadora;
	}

	public TipoOrientacao getTipoOrientacao() {
		return this.tipoOrientacao;
	}

	public void setTipoOrientacao(TipoOrientacao tipoOrientacao) {
		this.tipoOrientacao = tipoOrientacao;
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof TeseOrientada)) {
			return false;
		}
		TeseOrientada other = (TeseOrientada) object;
		if (this.id != other.id && this.id == 0)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "br.ufrn.sigaa.prodocente.dominio.Tese[id=" + id + "]";
	}

	public ListaMensagens validate() {
		
		ListaMensagens lista = new ListaMensagens();

		ValidatorUtil.validateRequiredAjaxId(getServidor().getId(), "Servidor", lista);
		ValidatorUtil.validateRequired(getTitulo(), "Título", lista);
		ValidatorUtil.validateRequiredId(getArea().getId(), "Área", lista);
		ValidatorUtil.validateRequiredId(getSubArea().getId(), "Sub-Área", lista);
		ValidatorUtil.validateRequiredId(getEntidadeFinanciadora().getId(), "Agência Financiadora", lista);
		ValidatorUtil.validateRequiredId(getTipoOrientacao().getId(), "Tipo de Orientação", lista);
		ValidatorUtil.validateRequiredId(getOrientacao(), "Orientador/Co-Orientador", lista);
		if (orientacao.charValue() == ' ') {
			MensagemAviso m = new MensagemAviso("Tipo de Orientacao Docente" + ": Campo obrigatório não informado",
					TipoMensagemUFRN.ERROR);
			lista.addMensagem(m);
		}
		ValidatorUtil.validateRequired(getInstituicaoEnsino(), "Instituição", lista);

		ValidatorUtil.validaInicioFim(getPeriodoInicio(), getPeriodoFim(), "Período da Orientação", lista);
		
		if (ValidatorUtil.isEmpty(programa))
			ValidatorUtil.validateRequired(getProgramaPos(), "Programa de Pós", lista);
		else ValidatorUtil.validateRequired(getPrograma(), "Programa", lista);

		if (getDiscenteExterno()!=null && !getDiscenteExterno())
			ValidatorUtil.validateRequired(getOrientandoDiscente(), "Orientando", lista);
		// se for discente externo só precisa da string do nome
		else
			ValidatorUtil.validateRequired(getOrientando(), "Orientando", lista);

		return lista;
	}

	public Discente getOrientandoDiscente() {
		return orientandoDiscente;
	}

	public void setOrientandoDiscente(Discente orientandoDiscente) {
		this.orientandoDiscente = orientandoDiscente;
	}

	public String getItemView() {
		String orientando = (getOrientandoDiscente() != null ? getOrientandoDiscente().getNome() : getOrientando());

		Date dataAtual = new Date();
		
		if ( ValidatorUtil.isNotEmpty(periodoFim) && CalendarUtils.descartarHoras(periodoFim).getTime() > CalendarUtils.descartarHoras(dataAtual).getTime() ){
			periodoFim = dataAtual;
		}
		
		return "<td>" + (orientando != null ? orientando : "-") + "</td>" + "<td nowrap style=\"text-align:center\">"
				+ Formatador.getInstance().formatarData(periodoInicio) + " - " + Formatador.getInstance().formatarData(periodoFim)
				+ "</td>" + "<td style=\"text-align:center\">" + Formatador.getInstance().formatarData(dataPublicacao) + "</td>" 
 				+ "<td style=\"text-align:right\">"	+ CalendarUtils.calculoMeses(periodoInicio, periodoFim) + "</td>" 
				+ "<td style=\"text-align:center\">" + (getPago() != null ? (getPago() ? "Sim" : "Não") : "Não") + "</td>";
	}

	public String getTituloView() {
		return "<td>Nome do Orientando</td><td style=\"text-align:center\">Período</td><td style=\"text-align:center\">Defesa</td>" +
				"<td style=\"text-align:right\">Meses</td><td style=\"text-align:center\">Remunerado</td>";
	}

	public HashMap<String, String> getItens() {
		HashMap<String, String> itens = new HashMap<String, String>();
		itens.put("orientando", null);
		itens.put("periodoInicio", null);
		itens.put("periodoFim", null);
		itens.put("dataPublicacao", null);
		itens.put("pago", null);
		itens.put("orientandoDiscente", null);
		return itens;
	}

	public float getQtdBase() {
		return CalendarUtils.calculoMeses(periodoInicio, periodoFim);
	}

	public InstituicoesEnsino getInstituicaoEnsino() {
		return instituicaoEnsino;
	}

	public void setInstituicaoEnsino(InstituicoesEnsino instituicaoEnsino) {
		this.instituicaoEnsino = instituicaoEnsino;
	}

	public Unidade getProgramaPos() {
		return programaPos;
	}

	public void setProgramaPos(Unidade programaPos) {
		this.programaPos = programaPos;
	}

	public Boolean getDesligado() {
		return this.desligado;
	}

	public void setDesligado(Boolean desligado) {
		this.desligado = desligado;
	}

	public DadosDefesa getDadosDefesa() {
		return dadosDefesa;
	}

	public void setDadosDefesa(DadosDefesa dadosDefesa) {
		this.dadosDefesa = dadosDefesa;
	}
	
	public Integer getTotalOrientadores() {
		return totalOrientadores;
	}

	public void setTotalOrientadores(Integer totalOrientadores) {
		this.totalOrientadores = totalOrientadores;
	}

}