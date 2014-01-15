/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '26/10/2006'
 *
 */
package br.ufrn.sigaa.prodocente.producao.dominio;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.Formatador;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.dominio.AreaConhecimentoCnpq;
import br.ufrn.sigaa.dominio.InstituicoesEnsino;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ensino.dominio.BancaDefesa;
import br.ufrn.sigaa.ensino.stricto.dominio.BancaPos;
import br.ufrn.sigaa.pessoa.dominio.Municipio;
import br.ufrn.sigaa.pessoa.dominio.Pais;
import br.ufrn.sigaa.prodocente.relatorios.dominio.ViewAtividadeBuilder;

/**
 * Entidade que registra as informações de bancas examinadoras que tiveram a participação
 * de docentes da instituição.
 *
 * @author Gleydson
 */
@Entity
@Table(name = "producao_banca", schema = "prodocente")
@PrimaryKeyJoinColumn(name = "id_producao_banca")
public class Banca extends Producao implements ViewAtividadeBuilder {
	
	/** Data em que a banca ocorreu */
	@Column(name = "data")
	@Temporal(TemporalType.DATE)
	private Date data;
	
	/** Unidade a qual a produção ou o servidor está vinculada*/
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_departamento")
	private Unidade departamento = new Unidade();
	
	/** Instituição de ensino a qual a banca se refere*/ 
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_instituicao")
	private InstituicoesEnsino instituicao = new InstituicoesEnsino();
	
	/** Município de ocorrência da banca */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_municipio")
	private Municipio municipio = new Municipio();
	
	/** País onde a banca ocorreu */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_pais")
	private Pais pais = new Pais();
	
	/** Autor do trabalho examinado */
	@Column(name = "autor")
	private String autor;

	/** Natureza do exame da banca */
	@JoinColumn(name = "id_natureza_exame", referencedColumnName = "id_natureza_exame")
	@ManyToOne(fetch = FetchType.EAGER)
	private NaturezaExame naturezaExame = new NaturezaExame();

	/** Categoria funcional do examinado */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_categoria_funcional")
	private CategoriaFuncional categoriaFuncional = new CategoriaFuncional();

	/** Tipo da banca examinadora: 
		CURSO = 1
		CONCURSO = 2'; */
	@JoinColumn(name = "id_tipo_banca", referencedColumnName = "id_tipo_banca")
	@ManyToOne(fetch = FetchType.EAGER)
	private TipoBanca tipoBanca = new TipoBanca();

	/*
	 * Campos Obrigatórios: Titulo, Natureza, Departamento, Data, Pais
	 *
	 */
	/** Banca de Pós relacinada a produção */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_banca_pos")
	private BancaPos bancaPos;
	
	/** Banca de Defesa (Graduação) relacinada a produção */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_banca_defesa")
	private BancaDefesa bancaDefesa;	
	
	/** Creates a new instance of Banca */
	public Banca() {
	}

	public String getAutor() {
		return autor;
	}

	public void setAutor(String autor) {
		this.autor = autor;
	}

	public Date getData() {
		return data;
	}

	/**
	 * Seta a data da banca
	 * @param data
	 */
	public void setData(Date data) {
		setAnoReferencia(CalendarUtils.getAno(data));
		this.data = data;
	}

	public Unidade getDepartamento() {
		return departamento;
	}

	public void setDepartamento(Unidade departamento) {
		this.departamento = departamento;
	}

	public InstituicoesEnsino getInstituicao() {
		return instituicao;
	}

	public void setInstituicao(InstituicoesEnsino instituicao) {
		this.instituicao = instituicao;
	}

	public Municipio getMunicipio() {
		return municipio;
	}

	public void setMunicipio(Municipio municipio) {
		this.municipio = municipio;
	}

	public NaturezaExame getNaturezaExame() {
		return naturezaExame;
	}

	public void setNaturezaExame(NaturezaExame naturezaExame) {
		this.naturezaExame = naturezaExame;
	}

	public Pais getPais() {
		return pais;
	}

	public void setPais(Pais pais) {
		this.pais = pais;
	}

	public TipoBanca getTipoBanca() {
		return tipoBanca;
	}

	public void setTipoBanca(TipoBanca tipoBanca) {
		this.tipoBanca = tipoBanca;
	}


	/*
	 * Campos Obrigatórios: Titulo, Natureza, Departamento, Data, Pais
	 *
	 */


	public CategoriaFuncional getCategoriaFuncional() {
		return categoriaFuncional;
	}

	public void setCategoriaFuncional(CategoriaFuncional categoriaFuncional) {
		this.categoriaFuncional = categoriaFuncional;
	}

	/**
	 * Validação do cadastro da banca de produção 
	 */
	@Override
	public ListaMensagens validate(){

		ListaMensagens lista = new ListaMensagens();
		ValidatorUtil.validateRequired(getTitulo(),"Título", lista);
		ValidatorUtil.validateRequired(getData(), "Data da Banca", lista);
		ValidatorUtil.validateRequired(getAnoReferencia(), "Ano de Referência", lista);
//		ValidatorUtil.validateRequiredId(getPais().getId(),"País", lista);
		ValidatorUtil.validateRequired(getDepartamento(), "Departamento", lista);
		ValidatorUtil.validateRequired(getArea(), "Área", lista);
		ValidatorUtil.validateRequired(getSubArea(), "Sub-Área", lista);
		ValidatorUtil.validateRequired(getPais(), "Pais", lista);
		ValidatorUtil.validateRequired(getInstituicao(), "Instituição", lista);
		ValidatorUtil.validateRequired(getDataProducao(), "Data da Publicação", lista);
		
		// Validar município somente para bancas no brasil
		if (pais != null && pais.isBrasil()) {
			ValidatorUtil.validateRequired(getMunicipio(), "Município", lista);
		} 
		
		ValidatorUtil.validateRequired(getTipoBanca(), "Tipo de Banca", lista);
		if (getTipoBanca().getId() == TipoBanca.CURSO) {
			ValidatorUtil.validateRequiredId(getNaturezaExame() != null ? getNaturezaExame().getId() : 0, "Natureza do Exame", lista);
			categoriaFuncional = null;
		} else if (getTipoBanca().getId() == TipoBanca.CONCURSO) {
			ValidatorUtil.validateRequired(getCategoriaFuncional(), "Categoria Funcional", lista);
			naturezaExame = null;
		}
		return lista;
	}

	/**
	 * Retorna o itemView contendo o Título da banca
	 */
	public String getItemView() {
		return "  <td>"+getTitulo()+ "</td>" +
			   "  <td style=\"text-align:center\">"+Formatador.getInstance().formatarData(data);

	}

	/**
	 * Retorna a Titulo
	 */
	public String getTituloView() {
		return  "    <td>Atividade</td>" +
				"    <td style=\"text-align:center\">Data</td>";
	}

	/**
	 * Verifica se é banca de Concurso
	 * @return
	 */
	public Boolean isBancaConcurso() {
		if (tipoBanca != null) {
			return tipoBanca.getId() == TipoBanca.CONCURSO;
		}
		return null;
	}

	/**
	 * Retorna os itens da caregoria Funcional
	 */
	public HashMap<String, String> getItens() {
		HashMap<String, String> itens = new HashMap<String, String>();
		itens.put("titulo", null);
		itens.put("data", null);
		itens.put("categoriaFuncional.descricao", "categoriaNome");
		return itens;
	}

	/**
	 * Seta o nome da categoria
	 * @param nomeCategoria
	 */
	@Transient
	public void setCategoriaNome( String nomeCategoria ) {
		if ( this.getCategoriaFuncional() == null ) {
			this.setCategoriaFuncional( new CategoriaFuncional() );
		}
		this.getCategoriaFuncional().setDescricao(nomeCategoria);
	}
	
	public float getQtdBase() {
		return 1;
	}

	public BancaPos getBancaPos() {
		return bancaPos;
	}

	public void setBancaPos(BancaPos bancaPos) {
		this.bancaPos = bancaPos;
	}

	/**
	 * Inicia os atributos
	 */
	public void iniciarNulos(){
		if (getArea() == null ) {
			setArea(new AreaConhecimentoCnpq());
		}
		if (getSubArea() == null) {
			setSubArea(new AreaConhecimentoCnpq());
		}
		
		if( instituicao == null ) {
			instituicao = new InstituicoesEnsino();
		}
		if( municipio == null ) {
			municipio = new Municipio();
		}
		if( pais == null ) {
			pais = new Pais();
		}
		if( departamento == null ) {
			departamento = new Unidade();
		}
		if (naturezaExame == null) {
			naturezaExame = new NaturezaExame();
		}
	}

	/**
	 * Retorna o semestre de acordo com a data da banca
	 */
	public String getSemestre(){
		String periodo = " ";
		if(data != null){
			Calendar c = Calendar.getInstance();
			c.setTime(data);
			periodo = c.get(Calendar.MONTH) <= Calendar.JUNE ? "1" : "2";
		}
		if(getAnoReferencia() != null)
			return getAnoReferencia().toString() + "." + periodo;
		else
			return "Indefinido";
	}

	public BancaDefesa getBancaDefesa() {
		return bancaDefesa;
	}

	public void setBancaDefesa(BancaDefesa bancaDefesa) {
		this.bancaDefesa = bancaDefesa;
	}
}
