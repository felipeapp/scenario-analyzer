/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 27/10/2006
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
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.HashCodeUtil;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.dominio.ElementoDespesa;

/*******************************************************************************
 * <p>
 * Representa um item do or�amento. Informado durante o cadastro de uma a��o de
 * extens�o <br/>
 * Uma a��o de extens�o deve informar todos os detalhes do or�amento utilizado
 * em sua execu��o. Na solicita��o de recursos do FAEx o detalhamento do
 * or�amento � importante no sentido de que ele ser� utilizado pelos membros do
 * comit� na an�lise da proposta e libera��o de recursos solicitados para a
 * a��o.
 * </p>
 * 
 * @author Victor Hugo
 * @author Gleydson
 * @author Ilueny Santos
 ******************************************************************************/
@Entity
@Table(schema = "projetos", name = "orcamento_detalhado")
public class OrcamentoDetalhado implements Validatable {

	/** Atributo utilizado para representar o Id do Or�amento */
	@Id @GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
			parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name="id_orcamento_detalhado")
	private int id;
	
	/** Atributo utilizado para representar a discrimina��o */
    @Column(name = "discriminacao")
    private String discriminacao;

    /** Atributo utilizdao para representar a quantidade */
    @Column(name = "quantidade")
    private Double quantidade = 1d;

    /** Atributo utilizado para representar o Valor Unit�rio */
    @Column(name = "valor_unitario")
    private Double valorUnitario = 0.0;
    
    /** Atributo utilizado para representar se h� material de licita��o */
    @Column (name = "material_licitado")
    private boolean materialLicitado = false;

    /** Atributo utilizado pare representar o Projeto */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_projeto")
    private Projeto projeto;

    /** Atributo utilizado para representar o elemento de Despesa */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_elemento_despesa", referencedColumnName = "id_elemento_despesa")
    private ElementoDespesa elementoDespesa = new ElementoDespesa();

    /** Atributo utilizado para representar se o or�amento est� ativo */
    @Column(name = "ativo")
    private boolean ativo;

    /**
     * utilizado apenas para remover o or�amento das listas de objetos transientes.
     */
    @Transient
    private int posicao;

    /** Creates a new instance of OrcamentoDetalhado */
    public OrcamentoDetalhado() {
    }

    /** Creates a new instance of OrcamentoDetalhado */
    public OrcamentoDetalhado(int id) {
	this.id = id;
    }

    public int getId() {
	return this.id;
    }

    public void setId(int id) {
	this.id = id;
    }

    /**
     * M�todo utilizado para informar a discrimina��o do or�amento
     * @return
     */
    public String getDiscriminacao() {
	return discriminacao;
    }

    /**
     * M�todo utilizado para setar a discrimina��o do or�amento
     * @param discriminacao
     */
    public void setDiscriminacao(String discriminacao) {
	this.discriminacao = discriminacao;
    }

    public Double getQuantidade() {
    	return this.quantidade;
    }

    public void setQuantidade(Double quantidade) {
    	this.quantidade = quantidade;
    }

    public Double getValorUnitario() {
    	return this.valorUnitario;
    }

    public void setValorUnitario(Double valorUnitario) {
    	this.valorUnitario = valorUnitario;
    }

    /**
     * M�todo utilizado para infomrar a descri��o do or�amento em forma de String
     */
    public String toString() {
	return elementoDespesa != null ? elementoDespesa.getDescricao() : "";
    }

    public ElementoDespesa getElementoDespesa() {
	return elementoDespesa;
    }

    public void setElementoDespesa(ElementoDespesa elementoDespesa) {
	this.elementoDespesa = elementoDespesa;
    }

    public int getPosicao() {
	return posicao;
    }

    public void setPosicao(int posicao) {
	this.posicao = posicao;
    }

    /**
     * M�todo utilizado para validar os atributos
     */
    public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();
		ValidatorUtil.validateRequired(projeto, "Projeto", lista);
		ValidatorUtil.validateRequired(elementoDespesa, "Elemento de Despesa", lista);
		ValidatorUtil.validateRequired(quantidade, "Quantidade", lista);
		ValidatorUtil.validateRequired(valorUnitario, "Valor Unit�rio", lista);
		
		if(elementoDespesa != null && ! elementoDespesa.isPermiteValorFracionado()){
			if ( quantidade % 1 != 0 ){
				lista.addErro("A quantidade n�o pode ser um valor fracionado.");
			}
		}
		
		return lista;
    }

    public double getValorTotal() {
    	double result = (valorUnitario != null ? valorUnitario : 0) * (quantidade != null ? quantidade : 0);
    	return UFRNUtils.truncateDouble(result, 2);
    }

    public boolean isAtivo() {
	return ativo;
    }

    public void setAtivo(boolean ativo) {
	this.ativo = ativo;
    }

    @Override
    public boolean equals(Object obj) {
	return EqualsUtil.testEquals(this, obj, "id");
    }

    public Projeto getProjeto() {
	return projeto;
    }

    public void setProjeto(Projeto projeto) {
	this.projeto = projeto;
    }

	@Override
	public int hashCode() {
		return HashCodeUtil.hashAll(id);
	}

	public boolean isMaterialLicitado() {
	    return materialLicitado;
	}

	public void setMaterialLicitado(boolean materialLicitado) {
	    this.materialLicitado = materialLicitado;
	}

}
