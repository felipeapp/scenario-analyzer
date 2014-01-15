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
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.Formatador;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.dominio.AreaConhecimentoCnpq;
import br.ufrn.sigaa.pessoa.dominio.Pais;
import br.ufrn.sigaa.pessoa.dominio.Servidor;
import br.ufrn.sigaa.prodocente.relatorios.dominio.ViewAtividadeBuilder;

/**
 * Entidade que registra as informações referentes à Formação Acadêmica de um servidor
 *
 * Deprecated devido ao Refactoring em Formação Acadêmica, que será consultado utilizando o serviço disponível no SIGRH,
 * através do Spring HTTP Invoker. 
 * 
 * @author Gleydson
 */
@Entity
@Table(name = "formacao_academica", schema = "prodocente")
@Deprecated
public class FormacaoAcademica implements Validatable, ViewAtividadeBuilder {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "id_formacao_academica", nullable = false)
	private int id;
	

	/**
	 * Ao remover as produções as mesmas não serão removidas da base de dados,
	 * apenas o campo ativo será marcado como FALSE
	 */
	@Column(name = "ativo")
	private Boolean ativo;

	@JoinColumn(name = "id_pais", referencedColumnName = "id_pais")
	@ManyToOne
	private Pais pais;

	@JoinColumn(name = "id_servidor", referencedColumnName = "id_servidor")
	@ManyToOne
	private Servidor servidor;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_formacao")
	private FormacaoTese formacao;

	@Column(name = "grau")
	private String grau;

	@Column(name = "titulo")
	private String titulo;

	@Column(name = "entidade")
	private String entidade;

	@Column(name = "orientador")
	private String orientador;

	@Column(name = "palavra_chave_1")
	private String palavraChave1;

	@Column(name = "palavra_chave_2")
	private String palavraChave2;

	@Column(name = "palavra_chave_3")
	private String palavraChave3;

	@Column(name = "data_inicio")
	@Temporal(TemporalType.DATE)
	private Date dataInicio;

	@Column(name = "data_fim")
	@Temporal(TemporalType.DATE)
	private Date dataFim;

	@Column(name = "informacoes")
	private String informacoes;

	@JoinColumn(name = "id_tipo_orientacao", referencedColumnName = "id_tipo_orientacao")
	@ManyToOne
	private TipoOrientacao tipoOrientacao;

	@JoinColumn(name = "id_area", referencedColumnName = "id_area_conhecimento_cnpq")
	@ManyToOne
	private AreaConhecimentoCnpq area;

	@JoinColumn(name = "id_subarea", referencedColumnName = "id_area_conhecimento_cnpq")
	@ManyToOne
	private AreaConhecimentoCnpq subArea;

	@JoinColumn(name = "id_especialidade", referencedColumnName = "id_area_conhecimento_cnpq")
	@ManyToOne
	private AreaConhecimentoCnpq especialidade;

	/** Creates a new instance of FormacaoAcademica */
	public FormacaoAcademica() {
	}

	/**
	 * Creates a new instance of FormacaoAcademica with the specified values.
	 *
	 * @param id
	 *            the id of the FormacaoAcademica
	 */
	public FormacaoAcademica(Integer id) {
		this.id = id;
	}

	/**
	 * Gets the id of this FormacaoAcademica.
	 *
	 * @return the id
	 */
	public int getId() {
		return this.id;
	}

	/**
	 * Sets the id of this FormacaoAcademica to the specified value.
	 *
	 * @param id
	 *            the new id
	 */
	public void setId(int id) {
		this.id = id;
	}
	
	/**
	 * Ao remover as produções as mesmas não serão removidas da base de dados,
	 * apenas o campo ativo será marcado como FALSE
	 * @author Edson Anibal (ambar@info.ufrn.br)
	 */
	public Boolean getAtivo()
	{
		return this.ativo;
	}

	/**
	 * Ao remover as produções as mesmas não serão removidas da base de dados,
	 * apenas o campo ativo será marcado como FALSE
	 * @author Edson Anibal (ambar@info.ufrn.br)
	 */
	public void setAtivo(Boolean ativo)
	{
		this.ativo = ativo;
	}

	/**
	 * Gets the pais of this FormacaoAcademica.
	 *
	 * @return the pais
	 */
	public Pais getPais() {
		return this.pais;
	}

	/**
	 * Sets the pais of this FormacaoAcademica to the specified value.
	 *
	 * @param pais
	 *            the new pais
	 */
	public void setPais(Pais pais) {
		this.pais = pais;
	}

	/**
	 * Gets the servidor of this FormacaoAcademica.
	 *
	 * @return the servidor
	 */
	public Servidor getServidor() {
		return this.servidor;
	}

	/**
	 * Sets the servidor of this FormacaoAcademica to the specified value.
	 *
	 * @param servidor
	 *            the new servidor
	 */
	public void setServidor(Servidor servidor) {
		this.servidor = servidor;
	}

	/**
	 * Gets the grau of this FormacaoAcademica.
	 *
	 * @return the grau
	 */
	public String getGrau() {
		return this.grau;
	}

	/**
	 * Sets the grau of this FormacaoAcademica to the specified value.
	 *
	 * @param grau
	 *            the new grau
	 */
	public void setGrau(String grau) {
		this.grau = grau;
	}

	/**
	 * Gets the titulo of this FormacaoAcademica.
	 *
	 * @return the titulo
	 */
	public String getTitulo() {
		return this.titulo;
	}

	/**
	 * Sets the titulo of this FormacaoAcademica to the specified value.
	 *
	 * @param titulo
	 *            the new titulo
	 */
	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	/**
	 * Gets the entidade of this FormacaoAcademica.
	 *
	 * @return the entidade
	 */
	public String getEntidade() {
		return this.entidade;
	}

	/**
	 * Sets the entidade of this FormacaoAcademica to the specified value.
	 *
	 * @param entidade
	 *            the new entidade
	 */
	public void setEntidade(String entidade) {
		this.entidade = entidade;
	}

	/**
	 * Gets the orientador of this FormacaoAcademica.
	 *
	 * @return the orientador
	 */
	public String getOrientador() {
		return this.orientador;
	}

	/**
	 * Sets the orientador of this FormacaoAcademica to the specified value.
	 *
	 * @param orientador
	 *            the new orientador
	 */
	public void setOrientador(String orientador) {
		this.orientador = orientador;
	}

	/**
	 * Gets the palavraChave1 of this FormacaoAcademica.
	 *
	 * @return the palavraChave1
	 */
	public String getPalavraChave1() {
		return this.palavraChave1;
	}

	/**
	 * Sets the palavraChave1 of this FormacaoAcademica to the specified value.
	 *
	 * @param palavraChave1
	 *            the new palavraChave1
	 */
	public void setPalavraChave1(String palavraChave1) {
		this.palavraChave1 = palavraChave1;
	}

	/**
	 * Gets the palavraChave2 of this FormacaoAcademica.
	 *
	 * @return the palavraChave2
	 */
	public String getPalavraChave2() {
		return this.palavraChave2;
	}

	/**
	 * Sets the palavraChave2 of this FormacaoAcademica to the specified value.
	 *
	 * @param palavraChave2
	 *            the new palavraChave2
	 */
	public void setPalavraChave2(String palavraChave2) {
		this.palavraChave2 = palavraChave2;
	}

	/**
	 * Gets the palavraChave3 of this FormacaoAcademica.
	 *
	 * @return the palavraChave3
	 */
	public String getPalavraChave3() {
		return this.palavraChave3;
	}

	/**
	 * Sets the palavraChave3 of this FormacaoAcademica to the specified value.
	 *
	 * @param palavraChave3
	 *            the new palavraChave3
	 */
	public void setPalavraChave3(String palavraChave3) {
		this.palavraChave3 = palavraChave3;
	}

	/**
	 * Gets the dataInicio of this FormacaoAcademica.
	 *
	 * @return the dataInicio
	 */
	public Date getDataInicio() {
		return this.dataInicio;
	}

	/**
	 * Sets the dataInicio of this FormacaoAcademica to the specified value.
	 *
	 * @param dataInicio
	 *            the new dataInicio
	 */
	public void setDataInicio(Date dataInicio) {
		this.dataInicio = dataInicio;
	}

	/**
	 * Gets the dataFim of this FormacaoAcademica.
	 *
	 * @return the dataFim
	 */
	public Date getDataFim() {
		return this.dataFim;
	}

	/**
	 * Sets the dataFim of this FormacaoAcademica to the specified value.
	 *
	 * @param dataFim
	 *            the new dataFim
	 */
	public void setDataFim(Date dataFim) {
		this.dataFim = dataFim;
	}

	/**
	 * Gets the informacoes of this FormacaoAcademica.
	 *
	 * @return the informacoes
	 */
	public String getInformacoes() {
		return this.informacoes;
	}

	/**
	 * Sets the informacoes of this FormacaoAcademica to the specified value.
	 *
	 * @param informacoes
	 *            the new informacoes
	 */
	public void setInformacoes(String informacoes) {
		this.informacoes = informacoes;
	}

	/**
	 * Gets the area of this FormacaoAcademica.
	 *
	 * @return the area
	 */
	public AreaConhecimentoCnpq getArea() {
		return this.area;
	}

	/**
	 * Sets the area of this FormacaoAcademica to the specified value.
	 *
	 * @param area
	 *            the new area
	 */
	public void setArea(AreaConhecimentoCnpq area) {
		this.area = area;
	}

	/**
	 * Gets the subArea of this FormacaoAcademica.
	 *
	 * @return the subArea
	 */
	public AreaConhecimentoCnpq getSubArea() {
		return this.subArea;
	}

	/**
	 * Sets the subArea of this FormacaoAcademica to the specified value.
	 *
	 * @param subArea
	 *            the new subArea
	 */
	public void setSubArea(AreaConhecimentoCnpq subArea) {
		this.subArea = subArea;
	}

	/**
	 * Gets the especialidade of this FormacaoAcademica.
	 *
	 * @return the especialidade
	 */
	public AreaConhecimentoCnpq getEspecialidade() {
		return this.especialidade;
	}

	/**
	 * Sets the especialidade of this FormacaoAcademica to the specified value.
	 *
	 * @param especialidade
	 *            the new especialidade
	 */
	public void setEspecialidade(AreaConhecimentoCnpq especialidade) {
		this.especialidade = especialidade;
	}

	/**
	 * Determines whether another object is equal to this FormacaoAcademica. The
	 * result is <code>true</code> if and only if the argument is not null and
	 * is a FormacaoAcademica object that has the same id field values as this
	 * object.
	 *
	 * @param object
	 *            the reference object with which to compare
	 * @return <code>true</code> if this object is the same as the argument;
	 *         <code>false</code> otherwise.
	 */
	@Override
	public boolean equals(Object object) {
		if (!(object instanceof FormacaoAcademica)) {
			return false;
		}
		FormacaoAcademica other = (FormacaoAcademica) object;
		if (this.id != other.id || (this.id == 0))
			return false;
		return true;
	}

	/**
	 * Returns a string representation of the object. This implementation
	 * constructs that representation based on the id fields.
	 *
	 * @return a string representation of the object.
	 */
	@Override
	public String toString() {
		return "br.ufrn.sigaa.prodocente.dominio.FormacaoAcademica[id=" + id
				+ "]";
	}

	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();
		ValidatorUtil.validateRequiredId(formacao.getId(), "Tipo da Formação",
				lista.getMensagens());
		ValidatorUtil.validateRequired(dataInicio, "Data de Início", lista
				.getMensagens());
		
		if (formacao.getId() != FormacaoTese.GRADUACAO) {
			ValidatorUtil.validateRequired(titulo, "Título do Trabalho é obrigatório para Pós-graduação", lista.getMensagens());
			ValidatorUtil.validateRequired(orientador, "Orientador é obrigatório para Pós-graduação", lista.getMensagens());			
		}

		ValidatorUtil.validateRequired(grau, "Grau Acadêmico", lista
				.getMensagens());
		ValidatorUtil.validateRequired(entidade, "Entidade/Instituição", lista
				.getMensagens());

		ValidatorUtil.validateRequired(area.getId(), "Área de Conhecimento",
				lista.getMensagens());
		ValidatorUtil.validateRequiredId(subArea.getId(),
				"Sub Área de Conhecimento", lista.getMensagens());
		ValidatorUtil.validateRequiredId(pais.getId(), "País", lista.getMensagens());
		setEspecialidade(null);
		setTipoOrientacao(null);
		return lista;
	}

	public TipoOrientacao getTipoOrientacao() {
		return tipoOrientacao;
	}

	public void setTipoOrientacao(TipoOrientacao tipoOrientacao) {
		this.tipoOrientacao = tipoOrientacao;
	}

	public String getItemView() {
		return "  <td>" + getTitulo() + "</td>" + "  <td>"
				+ Formatador.getInstance().formatarData(dataInicio) + " - "
				+ Formatador.getInstance().formatarData(dataFim) + "</td>";
	}

	public String getTituloView() {
		return "    <td>Atividade</td>" + "    <td>Data</td>";
	}

	public HashMap<String, String> getItens() {
		HashMap<String, String> itens = new HashMap<String, String>();
		itens.put("titulo", null);
		itens.put("dataInicio", null);
		itens.put("dataFim", null);
		return itens;
	}

	public FormacaoTese getFormacao() {
		return formacao;
	}

	public void setFormacao(FormacaoTese formacao) {
		this.formacao = formacao;
	}

	public float getQtdBase() {
		return 1;
	}

}