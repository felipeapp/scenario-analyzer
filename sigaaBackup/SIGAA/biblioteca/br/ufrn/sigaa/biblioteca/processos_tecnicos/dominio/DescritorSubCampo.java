/*
 * DescritorSubCampo.java
 *
 * UNIVERSIDADE FEDERAL DO RIO GRANDE DO NORTE
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * Campos Universit�rio Lagoa Nova
 * Natal - RN - Brasil
 */
package br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio;

import static br.ufrn.arq.util.StringUtils.unescapeHTML;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.HashCodeUtil;
import br.ufrn.arq.util.StringUtils;

/**
 *
 *    <p>Descreve TODOS os poss�veis subcampos e seus valores  </p> 
 *    <p> Exemplo: Etiqueta 411 pode ter os seguintes subcampos</p> 
 *    <p>
 *                          codigo = a , descricao = "Nome do evento ou nome da jurisdi��o' <br/>
 *                          codigo = d , descricao = "Data do Evento' <br/>
 *                          codigo = c , descricao = "Local do evento' <br/>
 *                          etc.. <br/>
 *    </p>                     
 *
 * @author jadson
 * @since 28/07/2008
 * @version
 *
 */
@Entity
@Table(name = "descritor_sub_campo", schema = "biblioteca")
public class DescritorSubCampo  implements Validatable{

	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
           parameters={ @Parameter(name="sequence_name", value="biblioteca.hibernate_sequence") })	
	@Column(name="id_descritor_sub_campo")
	private int id;

	/**
	 *  O valor
	 */	
	@Column(name = "codigo", nullable=false)
	private Character codigo;

	/**
	 * a descri��o como nos outros casos
	 */
	@Column(name = "descricao", nullable=false)
	private String descricao;

	/** informa��es do subcampo para auxiliar ao usu�rio */
	private String info;
	
	/**
	 * Um subcampo como uma etiqueta tambem pode ser repetida durante um registro ou n�o. 
	 */
	@Column(name = "repetivel", nullable=false)
	private boolean repetivel = false;


	@ManyToOne (fetch=FetchType.LAZY)
	@JoinColumn(name="id_etiqueta", referencedColumnName="id_etiqueta")
	private Etiqueta etiqueta;

	/*
	 * Em alguns casos esse descritor tem um conjunto de valores pr� definidos. 
	 * Ele s�o guardados aqui.
	 */
	@OneToMany(mappedBy="descritorSubCampo", cascade={CascadeType.REMOVE }, fetch = FetchType.LAZY) 
	private Set<ValorDescritorSubCampo> valoresDescritorSubCampo;

	@Transient // para usar nos casos de uso do sistema, antes de salvar tem que atribuir esse valor ao SET.
	private List<ValorDescritorSubCampo> valoresDescritorSubCampoList;
	
	/**
	 * Construtor default
	 */
	public DescritorSubCampo(){
		
	}


	/**
	 * Construtor passando a etiqueta
	 * 
	 * @param codigo
	 * @param etiqueta
	 */
	public DescritorSubCampo(Etiqueta etiqueta) {
		this.etiqueta = etiqueta;
	}
	
	/**
	 * Construtor passando o c�digo e etiqueta
	 * 
	 * @param codigo
	 * @param etiqueta
	 */
	public DescritorSubCampo(char codigo, Etiqueta etiqueta) {
		this(etiqueta);
		this.codigo = codigo;
	}

	
	
	
	/**
	 *  Retorna os valores desse descritor como uma lista.
	 * @return
	 */
	public List<ValorDescritorSubCampo> getValoresDescritorSubCampoList() {
		if( valoresDescritorSubCampoList == null)
			valoresDescritorSubCampoList = new ArrayList<ValorDescritorSubCampo>(getValoresDescritorSubCampo());
		
		return valoresDescritorSubCampoList;
	}
	
	

	@Override
	public String toString() {
		return "DescritorSubCampo [id=" + id + ", codigo=" + codigo
				+ ", descricao=" + descricao + ", info=" + info
				+ ", repetivel=" + repetivel + ", etiqueta=" + etiqueta
				+ ", valoresDescritorSubCampo=" + valoresDescritorSubCampo
				+ ", valoresDescritorSubCampoList="
				+ valoresDescritorSubCampoList + "]";
	}

	
	
	// sets e gets


	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;		
	}

	public Character getCodigo() {
		return codigo;
	}

	public void setCodigo(Character codigo) {
		this.codigo = codigo;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = unescapeHTML(descricao);
	}

	public boolean isRepetivel() {
		return repetivel;
	}

	public void setRepetivel(boolean repetivel) {
		this.repetivel = repetivel;
	}



	public Etiqueta getEtiqueta() {
		return etiqueta;
	}

	public void setEtiqueta(Etiqueta etiqueta) {
		this.etiqueta = etiqueta;
	}

	public Set<ValorDescritorSubCampo> getValoresDescritorSubCampo() {
		return valoresDescritorSubCampo;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = unescapeHTML(info);
	}


	public void setValoresDescritorSubCampo(Set<ValorDescritorSubCampo> valoresDescritorSubCampo) {
		this.valoresDescritorSubCampo = valoresDescritorSubCampo;
	}

	
	/**
	 * dois descritores sub campo s�o iguais se tiverem o mesmo c�digo para mesma etiqueta
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return HashCodeUtil.hashAll(this.codigo, this.etiqueta.getTag(), this.getEtiqueta().getTipo());
	}

	
	/**
	 * dois descritores sub campo s�o iguais se tiverem o mesmo c�digo para mesma etiqueta
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		return EqualsUtil.testEquals(this, obj, "codigo", "etiqueta.tag", "etiqueta.tipo");
	}


	/**
	 * Valida��o para Descritores de etiquetas locais
	 * 
	 * @see br.ufrn.arq.negocio.validacao.Validatable#validate()
	 */
	public ListaMensagens validate() {
		
		ListaMensagens mensagens = new ListaMensagens();
		
		if(this.etiqueta == null){
			mensagens.addErro("O sub campo precisa possuir uma etiqueta para ser salvo.");
			return mensagens; // n�o d� para validar mais nada
		}	
		
		// valida��es para etiquetas locais, pois somente etiqueta locais podem ser cadastradas.
		if ( codigo == null)
			mensagens.addErro("O c�digo do sub campo � obrigat�rio.");
		
		if ( StringUtils.isEmpty(descricao)){
			mensagens.addErro("A descri��o do sub campo � obrigat�ria.");
		}
		
		return mensagens;
	}
	
	
	
	
	/**
	 * M�todo de valida��o que deve ser chamdo apenas do cadastro de etiquetas locais
	 *
	 * @return
	 */
	public ListaMensagens validateCampoLocal() {

		ListaMensagens mensagens = new ListaMensagens();
		
		if( ! etiqueta.isEquetaLocal()){
			mensagens.addErro("S� podem ser criados sub campos para campos locais.");
		}
			
		return mensagens;
	}
	
	
	
}