/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Created on 12/01/2010
 *
 */	
package br.ufrn.sigaa.ensino.dominio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.parametros.dominio.ParametrosStrictoSensu;

/**
 * Representa o modelo de forma��o dos cursos de gradua��o, podendo ser em um �nico ciclo
 * ou em dois ciclos.
 * 
 * @author R�mulo Augusto
 */
@Entity
@Table(name = "tipo_ciclo_formacao", schema = "ensino")
@Cache(usage=CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class TipoCicloFormacao implements Validatable {
	
	

	/**
	 * Tipo de ciclo de Forma��o �nico, utilizado em stricto sensu.
	 */
	public static final TipoCicloFormacao CICLO_UNICO = 
		new TipoCicloFormacao(ParametroHelper.getInstance().
		getParametroInt(ParametrosStrictoSensu.CICLO_UNICO), "Um Ciclo");
	
	/** Tipo de ciclo de forma��o referente aos cursos com Dois Ciclos */
	public static final TipoCicloFormacao DOIS_CICLOS = new TipoCicloFormacao(2);
	
	/** Construtor padr�o */
	public TipoCicloFormacao() {
		super();
	}
	
	/**
	 * Construtor passando somente o id.
	 * @param id
	 */
	public TipoCicloFormacao(int id){
		this.id = id;
	}

	/**
	 * Construtor passando o id e descri��o do tipo do ciclo de forma��o
	 * @param id
	 * @param descricao
	 */
	public TipoCicloFormacao(int id, String descricao) {
		this.id = id;
		this.descricao = descricao;
	}

	/**
	 * Chave prim�ria
	 */
	@Id
	@GeneratedValue(generator = "seqGenerator")
	@GenericGenerator(name = "seqGenerator", strategy = "br.ufrn.arq.dao.SequenceStyleGenerator",
					parameters = {@Parameter(name = "sequence_name", value = "ensino.hibernate_sequence")})
	@Column(name = "id_tipo_ciclo_formacao")
	private int id;
	
	/** Atributo referente a ao texto descritivo do tipo de ciclo de forma��o.*/
	private String descricao;

	/** M�todo de valida��o dos atributos obrigat�rio durante o cadastro do objeto {@link TipoCicloFormacao} */
	public ListaMensagens validate() {

		ListaMensagens erros = new ListaMensagens();
		
		ValidatorUtil.validateRequired(descricao, "Descri��o", erros);
		
		return erros;
	}
	
	/**
	 * Identifica se o tipo � de Dois Ciclos
	 * @return
	 */
	@Transient
	public boolean isDoisCiclos(){
		return (id == DOIS_CICLOS.getId());
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public String getDescricao() {
		return descricao;
	}
	
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
}
