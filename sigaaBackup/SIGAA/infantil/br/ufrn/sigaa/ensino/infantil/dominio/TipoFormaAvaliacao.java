/**
 * 
 */
package br.ufrn.sigaa.ensino.infantil.dominio;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.StringTokenizer;

import javax.faces.model.SelectItem;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dao.CampoAtivo;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;

/**
 * Enumera��o com as formas de avalia��o do ensino infantil. Usado nos formul�rios
 * de avalia��o da crian�a.
 * 
 * @author Leonardo Campos
 *
 */
@Entity
@Table(name="tipo_forma_avaliacao", schema="infantil", uniqueConstraints={})
public class TipoFormaAvaliacao implements Validatable {
	
	/** Constante que indica o tipo da forma de avalia��o */
	public static final int INDEFINIDA = -1;
	
	/** Chave prim�ria do tipo de forma de avalia��o */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_tipo_forma_avaliacao", unique = true, nullable = false, insertable = true, updatable = true)
	private int id;
	
	/** Op��o do tipo de Avalia��o */
	@Column(name = "opcoes", unique = false, nullable = false, insertable = true, updatable = true)
	private String opcoes;
	
	/** Legenda do tipo de avalia��o */
	@Column(name = "legenda", unique = false, nullable = false, insertable = true, updatable = true)
	private String legenda;

	/** Indica se deve ser exibido para a instru��o de preenchimento */
	@Column(name = "exibir", unique = false, nullable = false, insertable = true, updatable = true)
	private boolean exibir;
	
	/** Indica se a forma de avalia��o se encontra ativo */
	@Column(name = "ativo", unique = false, nullable = false, insertable = true, updatable = true)
	@CampoAtivo
	private boolean ativo;

	public TipoFormaAvaliacao() {
		// TODO Auto-generated constructor stub
	}

	public boolean isIndefinida(){
		return id == INDEFINIDA;
	}
	
	public TipoFormaAvaliacao( int id) {
		this.id = id;
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getOpcoes() {
		return opcoes;
	}

	public void setOpcoes(String opcoes) {
		this.opcoes = opcoes;
	}

	public String getLegenda() {
		return legenda;
	}

	public void setLegenda(String legenda) {
		this.legenda = legenda;
	}

	public boolean isExibir() {
		return exibir;
	}

	public void setExibir(boolean exibir) {
		this.exibir = exibir;
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	/** Monta a legenda das formas de avalia��o */
	@Transient
	public List<String> getHeadOpcoes(){
		List<String> result = new ArrayList<String>();
		if ( legenda != null ) {
			StringTokenizer st = new StringTokenizer(legenda, " ");
			while (st.hasMoreTokens()) {
				result.add(st.nextToken());
			}
		}
		return result;
	}
	
	@Transient
	public List<SelectItem> getAllFormasAva(){
		List<SelectItem> itens = new ArrayList<SelectItem>();
		for (int i = 0; i < getHeadOpcoes().size(); i++) {
			SelectItem select = new SelectItem(i, "");
			itens.add(select);
		}
		return itens;
	}
	
	/** N�veis que necessitam da defini�a� da forma de avalia��o */
	public static boolean niveisNecessitamFormaAva( int tipoAva ) {
		Collection<Integer> itens = null;
		itens = new ArrayList<Integer>();
		itens.add(ItemInfantilFormulario.CONTEUDO);
		itens.add(ItemInfantilFormulario.OBJETIVOS);
		return itens.contains( tipoAva );
	}

	@Override
	public ListaMensagens validate() {
		return null;
	}
	
}