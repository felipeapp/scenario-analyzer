/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 03/07/2008
 *
 */
package br.ufrn.sigaa.projetos.dominio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.ValidatorUtil;

/*******************************************************************************
 * Representa o tipo de v�nculo do discente com uma a��o de extens�o. Comp�e o
 * DiscenteExtensao, dependendo do tipo de v�nculo, direitos e deveres
 * diferentes s�o dados ao discente de extens�o.
 * 
 * @author Ilueny Santos
 * 
 ******************************************************************************/
@Entity
@Table(name = "tipo_vinculo_discente", schema = "projetos")
public class TipoVinculoDiscente implements Validatable {

	///// EXTENS�O
	/** Discente volunt�rio. Que n�o possui bolsa remunerada. */
	public static final int EXTENSAO_VOLUNTARIO = 1;
	/** Bolsistas remunerados com recursos da universidade. */
	public static final int EXTENSAO_BOLSISTA_INTERNO = 2;
	/** Discentes em atividade curricular. */
	public static final int EXTENSAO_EM_ATIVIDADE_CURRICULAR = 3;
	/** Bolsistas remunerados com recursos externos � universidade. Ex. S�o bolsista financiados pela Petrobras, Minist�rio da Sa�de, etc.	 */
	public static final int EXTENSAO_BOLSISTA_EXTENO = 4;
	
	///// MONITORIA
	/** Discente volunt�rio. Que n�o possui bolsa remunerada. */
	public static final int MONITORIA_VOLUNTARIO = 10;
	/** Bolsistas remunerados com bolsas da universidade. */
	public static final int MONITORIA_BOLSISTA = 20;
	/** Discente n�o classificados na prova seletiva. */
	public static final int MONITORIA_NAO_CLASSIFICADO = 30;
	/** Discente na lista de espera da prova seletiva. Na finaliza��o de um bolsista ativo ele assume.*/
	public static final int MONITORIA_EM_ESPERA = 40;
	
	//// A��ES ACAD�MICAS ASSOCIADAS
	/** Bolsista interno de A��o Acad�mica Associada */
	public static final int ACAO_ASSOCIADA_BOLSISTA_INTERNO = 300;
	/** Bolsista externo de A��o Acad�mica Associada */
	public static final int ACAO_ASSOCIADA_BOLSISTA_EXTERNO = 301;
	/** Volunt�rio de A��o Acad�mica Associada */
	public static final int ACAO_ASSOCIADA_VOLUNTARIO 		= 302;


	/** Identifica��o �nica para objetos desta classe. */
	@Id @GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	parameters={ @Parameter(name="hibernate_sequence", value="public.hibernate_sequence") })
	@Column(name = "id_tipo_vinculo_discente", unique = true, nullable = false)
	private int id;

	/** Descreve o tipo de V�nculo do discente. */
	@Column(name = "descricao", length = 50)
	private String descricao;

	/** Indica se o Tipo de Bolsa Pesquisa est� em uso */
	private boolean ativo = true;
	
	/** Armazena o tipo de bolsa que est� associado no SIPAC */
	@Column(name = "id_tipo_bolsa_sipac")
	private Integer tipoBolsaSipac;

	/** Informa se � um tipo de v�nculo remunerado. */
	private boolean remunerado;
	
	/** Discente atualmente associado ao plano de trabalho do projeto. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_tipo_projeto")    	
	private TipoProjeto tipoProjeto;


	/** default constructor */
	public TipoVinculoDiscente() {
	}

	/** minimal constructor */
	public TipoVinculoDiscente(int id) {
		this.id = id;
	}

	/** full constructor */
	public TipoVinculoDiscente(int id, String descricao) {
		this.id = id;
		this.descricao = descricao;
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getDescricao() {
		return this.descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();
		ValidatorUtil.validateRequired(descricao, "Descri��o", lista);
		return lista;
	}

	public boolean isAtivo() {
	    return ativo;
	}

	public void setAtivo(boolean ativo) {
	    this.ativo = ativo;
	}

	public Integer getTipoBolsaSipac() {
	    return tipoBolsaSipac;
	}

	public void setTipoBolsaSipac(Integer tipoBolsaSipac) {
	    this.tipoBolsaSipac = tipoBolsaSipac;
	}

	public boolean isRemunerado() {
		return remunerado;
	}

	public void setRemunerado(boolean remunerado) {
		this.remunerado = remunerado;
	}

	public TipoProjeto getTipoProjeto() {
		return tipoProjeto;
	}

	public void setTipoProjeto(TipoProjeto tipoProjeto) {
		this.tipoProjeto = tipoProjeto;
	}

	public boolean isExtensaoInterno() {
		return id == EXTENSAO_BOLSISTA_INTERNO;
	}
	
}