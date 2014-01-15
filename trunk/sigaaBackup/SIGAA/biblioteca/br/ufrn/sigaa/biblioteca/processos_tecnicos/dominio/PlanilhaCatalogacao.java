/*
 * PlanilhaCatalogacao.java
 *
 * UNIVERSIDADE FEDERAL DO RIO GRANDE DO NORTE
 * Superintendência de Informática
 * Diretoria de Sistemas
 * Campos Universitário Lagoa Nova
 * Natal - RN - Brasil
 */
package br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
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
import br.ufrn.arq.util.StringUtils;


/**
 * Planilhas são modelos para a catalogação de títulos ou autoridades.
 *
 * @author Fred
 * @since
 * @version 1.0 criacao da classe
 *
 */
@Entity
@Table(name="planilha_catalogacao",schema="biblioteca")
public class PlanilhaCatalogacao implements Validatable{
	
	/** o Id */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
           parameters={ @Parameter(name="sequence_name", value="biblioteca.hibernate_sequence") })
	@Column(name="id_planilha_catalogacao")
	private int id;

	/** Nome da planilha */
	@Column(name="nome", nullable = false)
	private String nome;

	/** Apenas a descrição para que a planilha serve  */
	private String descricao;

	/**
	 * Indica se é uma planilha BIBLIOGRAFICA, AUTORIDADE ou qualquer uma outra se um dia for utilizado
	 * Planilha BIBLIOGRAFICA deve possuir apenas etiquetas BIBLIOGRAFICAS e assim sucessivamente. 
	 */
	private short tipo;
	
	
	/** guarda o formato do título que vai ser catalogado */
	@Column(name="id_formato_titulo_catalografico", nullable = false)
	private int idFormato = 0;

	/**
	 * Lista de objetos que guarda as informações dos campos de controle que já vem 
	 * preenchidas no cadastro de títulos usando uma planilha 
	 */
	@OneToMany(fetch = FetchType.LAZY, cascade={CascadeType.ALL}, mappedBy="planilha")
	private Collection<ObjetoPlanilhaCatalogacaoControle> objetosPlanilhaCatalogacaoControle = new ArrayList<ObjetoPlanilhaCatalogacaoControle>();

	/**
	 * Lista de objetos que guarda as informações dos campos de dados que já vem 
	 * preenchidos no cadastro de títulos usando uma planilha 
	 */
	@OneToMany(fetch = FetchType.LAZY, cascade={CascadeType.ALL}, mappedBy="planilha")
	private Collection<ObjetoPlanilhaCatalogacaoDados> objetosPlanilhaCatalogacaoDados = new ArrayList<ObjetoPlanilhaCatalogacaoDados>();
	
	
	//////////////////////////// informacoes auditoria //////////////////////////////
	
	/**
	 * Registro de entrada do usuário que cadastrou
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_cadastro")
	@CriadoPor
	private RegistroEntrada registroCadastro;

	/**
	 * Data de cadastro
	 */
	@CriadoEm
	@Column(name="data_cadastro")
	private Date dataCadastro;

	/**
	 * Registro entrada do usuário que realizou a última atualização
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_atualizacao")
	@AtualizadoPor
	private RegistroEntrada registroAtualizacao;

	/**
	 * Data da última atualização
	 */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_atualizacao")
	@AtualizadoEm
	private Date dataAtualizacao;

	
	/////////////////////////////////////////////////////////////////////////
	
	
	/** Guarda temporariamente o formato de material para essa planilha */
	@Transient
	private FormatoMaterial formatoMaterial;
	
	
	/**
	 * 
	 * Adiciona um novo objeto de controle à planilha
	 *
	 * @param obj
	 */
	public void addObjetoPlanilhaCamposControle(ObjetoPlanilhaCatalogacaoControle obj){
		if(objetosPlanilhaCatalogacaoControle == null)
			objetosPlanilhaCatalogacaoControle = new ArrayList<ObjetoPlanilhaCatalogacaoControle>();

		obj.setPlanilha(this);
		objetosPlanilhaCatalogacaoControle.add(obj);
	}

	/**
	 * 
	 * Adiciona um novo objeto de dados à planiha
	 *
	 * @param obj
	 */
	public void addObjetoPlanilhaCamposDados(ObjetoPlanilhaCatalogacaoDados obj){
		if(objetosPlanilhaCatalogacaoDados == null)
			objetosPlanilhaCatalogacaoDados = new ArrayList<ObjetoPlanilhaCatalogacaoDados>();

		obj.setPlanilha(this);
		objetosPlanilhaCatalogacaoDados.add(obj);
	}

	/**
	 * 
	 * Retorna a quantidade de objetos de controle da planilha
	 *
	 * @return
	 */
	public int getQtdObjetosControle(){
		if (objetosPlanilhaCatalogacaoControle != null)
			return objetosPlanilhaCatalogacaoControle.size();
		else
			return 0;
	}

	/**
	 * 
	 * Retorna a quantidade de objetos de dados da planilha
	 *
	 * @return
	 */
	public int getQtdObjetosDados(){
		if (objetosPlanilhaCatalogacaoControle != null)
			return objetosPlanilhaCatalogacaoDados.size();
		else
			return 0;
	}

	/**
	 * Valida as informações principais no cadastro na planilha.
	 * 
	 * @see br.ufrn.arq.negocio.validacao.Validatable#validate()
	 */
	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();

		if(StringUtils.isEmpty(nome))
			lista.addErro("O nome da planilha deve ser informado");

		if(tipo == 0)
			lista.addErro("Informe o tipo da planilha");
			
		if(idFormato <= 0 && tipo == TipoCatalogacao.BIBLIOGRAFICA)
			lista.addErro("O Formato do Material deve ser informado");

		if(objetosPlanilhaCatalogacaoControle.size() == 0)
			lista.addErro("A planilha deve conter pelo menos um Campo de Controle");

		return lista;
	}

	/**
	 * Retorna os objetos da planilha ordenados pela tag da etiqueta que possuírem.
	 *
	 * @return
	 */
	public Collection<ObjetoPlanilhaCatalogacaoControle> getObjetosControlePlanilhaOrdenadosByTag() {
		
		if(objetosPlanilhaCatalogacaoControle == null)
			objetosPlanilhaCatalogacaoControle = new ArrayList<ObjetoPlanilhaCatalogacaoControle>();
		else{
			Collections.sort( (List<ObjetoPlanilhaCatalogacaoControle> ) objetosPlanilhaCatalogacaoControle);
			
		}
		
		return objetosPlanilhaCatalogacaoControle;
	}
	
	
	
	
	/**
	 * Retorna os objetos da planilha ordenados pela tag da etiqueta que possuírem.
	 *
	 * @return
	 */
	public Collection<ObjetoPlanilhaCatalogacaoDados> getObjetosDadosPlanilhaOrdenadosByTag() {
		
		if(objetosPlanilhaCatalogacaoDados == null)
			objetosPlanilhaCatalogacaoDados = new ArrayList<ObjetoPlanilhaCatalogacaoDados>();
		else{
			Collections.sort( (List<ObjetoPlanilhaCatalogacaoDados> ) objetosPlanilhaCatalogacaoDados);
			
		}
		
		return objetosPlanilhaCatalogacaoDados;
	}
	
	
	
	
	
	// Gets e Sets

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public void setObjetosPlanilhaCatalogacaoDados(
			Collection<ObjetoPlanilhaCatalogacaoDados> objetosPlanilhaCatalogacaoDados) {
		this.objetosPlanilhaCatalogacaoDados = objetosPlanilhaCatalogacaoDados;
	}

	public int getIdFormato() {
		return idFormato;
	}

	public void setIdFormato(int idFormato) {
		this.idFormato = idFormato;
	}

	public Collection<ObjetoPlanilhaCatalogacaoControle> getObjetosPlanilhaCatalogacaoControle() {
		return objetosPlanilhaCatalogacaoControle;
	}

	public void setObjetosPlanilhaCatalogacaoControle(Collection<ObjetoPlanilhaCatalogacaoControle> objetosPlanilhaCatalogacaoControle) {
		this.objetosPlanilhaCatalogacaoControle = objetosPlanilhaCatalogacaoControle;
	}

	public Collection<ObjetoPlanilhaCatalogacaoDados> getObjetosPlanilhaCatalogacaoDados() {
		return objetosPlanilhaCatalogacaoDados;
	}

	public void setObjetosPlanilhaCamposDados(Collection<ObjetoPlanilhaCatalogacaoDados> objetosPlanilhaCatalogacaoDados) {
		this.objetosPlanilhaCatalogacaoDados = objetosPlanilhaCatalogacaoDados;
	}

	public RegistroEntrada getRegistroCadastro() {
		return registroCadastro;
	}

	public void setRegistroCadastro(RegistroEntrada registroCadastro) {
		this.registroCadastro = registroCadastro;
	}

	public Date getDataCadastro() {
		return dataCadastro;
	}

	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	public RegistroEntrada getRegistroAtualizacao() {
		return registroAtualizacao;
	}

	public void setRegistroAtualizacao(RegistroEntrada registroAtualizacao) {
		this.registroAtualizacao = registroAtualizacao;
	}

	public Date getDataAtualizacao() {
		return dataAtualizacao;
	}

	public void setDataAtualizacao(Date dataAtualizacao) {
		this.dataAtualizacao = dataAtualizacao;
	}

	public short getTipo() {
		return tipo;
	}

	public void setTipo(short tipo) {
		this.tipo = tipo;
	}

	public FormatoMaterial getFormatoMaterial() {
		return formatoMaterial;
	}

	public void setFormatoMaterial(FormatoMaterial formatoMaterial) {
		this.formatoMaterial = formatoMaterial;
	}
	
}
