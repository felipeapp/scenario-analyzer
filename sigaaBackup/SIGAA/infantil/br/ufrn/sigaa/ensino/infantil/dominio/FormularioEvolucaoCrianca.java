/**
 * 
 */
package br.ufrn.sigaa.ensino.infantil.dominio;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;

/**
 * Entidade que agrega todas as informações contidas no formulário de avaliação da criança
 * utilizado no ensino infantil para medir o desempenho dos alunos nos diversos níveis.
 * Para cada nível infantil, existe um formulário de avaliação das crianças daquele nível. 
 * 
 * @author Leonardo Campos
 *
 */
@Entity
@Table(name="formulario_evolucao_crianca", schema="infantil", uniqueConstraints={})
public class FormularioEvolucaoCrianca implements Validatable {

	/** Informação quanto ao tipo de avaliação definida */
	public static final int TIPOAVAINDEFINIDA = -1;
	/** Bimestre inical do para o preenchimento */
	public static final int BIMESTRE_INICIAL = 1;
	
	/** Chave primária. */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	          parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") }) 
	@Column(name = "id_formulario_evolucao_crianca", unique = true, nullable = false, insertable = true, updatable = true)
	private int id;
	
	/** Data em que o formulário de evolução da criança foi registrado. */
	@Temporal(TemporalType.DATE)
	@Column(name = "data_cadastro")
	@CriadoEm
	private Date dataCadastro;
	
	/** Indica se o formulário foi removido da aplicação. */
	private boolean ativo = true;
	
	/** Lista de itens que compõem o formulário. */
	@OrderBy(value="ordem")
	@OneToMany(fetch = FetchType.EAGER, mappedBy = "formulario")
	private List<ItemInfantilFormulario> itens;
	
	/** Lista de itens que compõem o formulário. */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_componente_curricular", unique = false, nullable = true, insertable = true, updatable = true)
	private ComponenteCurricular componente;
	
	/** Período atual do preenchimento do formulário de Evolução */
	@Transient
	private int periodo = 1;
	
	public FormularioEvolucaoCrianca() {
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Date getDataCadastro() {
		return dataCadastro;
	}

	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	public ListaMensagens validate() {
		// TODO Auto-generated method stub
		return null;
	}

	public List<ItemInfantilFormulario> getItens() {
		return itens;
	}

	public void setItens(List<ItemInfantilFormulario> itens) {
		this.itens = itens;
	}

	/** Retorna uma lista com os itens do formulário de evolução da criança */
	@Transient
	public List<ItemInfantilFormulario> getItensPeriodo(FormularioEvolucaoCrianca formulario, boolean preenchinmento) {
		List<ItemInfantilFormulario> itensForm = new ArrayList<ItemInfantilFormulario>();
		if ( preenchinmento ) {
			boolean temObj = false;
			String obs = "";
			TipoFormaAvaliacao tipoAva = null;
			for (ItemInfantilFormulario item : formulario.getItens()) {
					
				if ( item.getPeriodo() == formulario.getPeriodo() && item.getItem().isAtivo() ) {
					if ( !hasNext( item ) ) {
						item.setExibirTextArea( temObj );
						if ( item.isExibirTextArea() ){
							item.getItemPeriodo().setObservacoes( obs );
						}	
						// Após Exibir o text area é necessário setar novamente para false o temObj.
						// Caso contrário, se o formulário não tiver completo, aparecerá um text area num item sem conteúdo.
						temObj = false;
					}
					if ( ItemInfantilFormulario.OBJETIVOS == item.getProfundidade() )
						item.getItem().setFormaAvaliacao(tipoAva);
					itensForm.add(item);
				
					if ( ItemInfantilFormulario.CONTEUDO == item.getProfundidade() ) {
						temObj = item.getItem().isTemObservacao();
						if ( temObj && item.getItemPeriodo().getObservacoes() != null )
							obs = item.getItemPeriodo().getObservacoes();
						else 
							obs = "";
						
						if ( item.getItem().getFormaAvaliacao() != null ) {
							tipoAva = new TipoFormaAvaliacao();
							tipoAva = UFRNUtils.deepCopy(item.getItem().getFormaAvaliacao());
						}
					}
				}
			}
		} else {
			for (ItemInfantilFormulario item : itens) {
				if ( item.getPeriodo() == formulario.getPeriodo() && item.getItem().isAtivo() ) {
					itensForm.add(item);
				}
			}
			return itensForm;
		}
		
		return itensForm;
	}

	@Transient
	private boolean hasNext(ItemInfantilFormulario item) {
		try {
			for (int i = 0; i < itens.size(); i++) {
				if ( itens.get(i).getId() == item.getId() 
						&& itens.get(i+1).getProfundidade() + 1 < item.getProfundidade() ) {
					return false;
				}
			}
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/** Serve para desabilitar a edição dos itens do formulário */
	@Transient
	public void desabilitarEdicao() {
		for (ItemInfantilFormulario item : itens) {
			item.setEditavel(false);
		}
	}
	
	public ComponenteCurricular getComponente() {
		return componente;
	}

	public void setComponente(ComponenteCurricular componente) {
		this.componente = componente;
	}
	
	public int getPeriodo() {
		return periodo;
	}

	public void setPeriodo(int periodo) {
		this.periodo = periodo;
	}

	public boolean isPrimeiroBimestre() {
		return periodo == BIMESTRE_INICIAL;
	}
	
	/** Serve para adicionar uma item ao formulário do bimestre atual */
	public void addItem(ItemInfantilFormulario item, int ordem, int periodo) {
		if(itens == null)
			itens = new ArrayList<ItemInfantilFormulario>();
		item.setEditavel(true);
		item.setFormulario(this);
		item.setPeriodo(periodo);
		item.getItem().setAtivo(true);
		if ( item.isConteudo() ) 
			item.getItem().setFormaAvaliacao( new TipoFormaAvaliacao(TIPOAVAINDEFINIDA) );
		itens.add(ordem, item);
	}

}