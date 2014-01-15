/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 27/10/2006
 *
 */
package br.ufrn.sigaa.extensao.dominio;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import javax.persistence.CascadeType;
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
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.ValidatorUtil;

/*******************************************************************************
 * <p>
 * Representa os objetivos de uma ação de extensão. Atualmente somente projetos
 * de extensão possuem objetivos. <br/>
 * 
 * Os Coordenadores devem declarar os objetivos do projeto de forma bem definida
 * durante o cadastro da proposta. Estes objetivos serão utilizados na análise
 * do proposta pelo comitê de extensão.
 * </p>
 * 
 * @author Gleydson
 * @author Victor Hugo
 * @author Ilueny Santos
 ******************************************************************************/
@Entity
@Table(schema = "extensao", name = "objetivo")
public class Objetivo implements Validatable {

	/** Chave primária */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
			parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
    @Column(name = "id_objetivo", nullable = false)
    private int id;

    
    /** Descrição do objetivo */
    @Column(name = "objetivo")
    private String objetivo;

    /** Dados quantitativos */
    @Column(name = "quantitativos")
    private String quantitativos;

    /** Dados qualitativos */
    @Column(name = "qualitativos")
    private String qualitativos;

    /** Projeto de Extensão */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_atividade")
    private AtividadeExtensao atividadeExtensao;

    /** Atributo que representa se o Objetivo está ou não ativo no sistema */
    @Column(name = "ativo")
    private boolean ativo;

    /** Atividades principais. */
    @OneToMany(mappedBy = "objetivo", cascade = CascadeType.ALL)
    @OrderBy(value = "dataInicio")
    private Collection<ObjetivoAtividades> atividadesPrincipais = new HashSet<ObjetivoAtividades>();

    /** Dados qualitativos */
    @Column(name = "observacao_execucao")
    private String observacaoExecucao;
    
    /**
     * utilizado apenas para remover o objetivo das listas quando não se tem o id.
     */
    @Transient
    private int posicao = 0;

    /** Creates a new instance of Objetivo */
    public Objetivo() {
    }

    public Objetivo(int id) {
	this.id = id;
    }

    public int getId() {
	return this.id;
    }

    public void setId(int id) {
	this.id = id;
    }

    public String getObjetivo() {
	return this.objetivo;
    }

    public void setObjetivo(String objetivo) {
	this.objetivo = objetivo;
    }

    public String getQuantitativos() {
	return this.quantitativos;
    }

    public void setQuantitativos(String quantitativos) {
	this.quantitativos = quantitativos;
    }

    public String getQualitativos() {
	return this.qualitativos;
    }

    public void setQualitativos(String qualitativos) {
	this.qualitativos = qualitativos;
    }

    public Collection<ObjetivoAtividades> getAtividadesPrincipais() {
	return atividadesPrincipais;
    }

    public Collection<ObjetivoAtividades> getAtividades() {
    	return new ArrayList<ObjetivoAtividades>(atividadesPrincipais);
    }

    public void setAtividadesPrincipais(
	    Collection<ObjetivoAtividades> atividadesPrincipais) {
	this.atividadesPrincipais = atividadesPrincipais;
    }

	public AtividadeExtensao getAtividadeExtensao() {
		return atividadeExtensao;
	}

	public void setAtividadeExtensao(AtividadeExtensao atividadeExtensao) {
		this.atividadeExtensao = atividadeExtensao;
	}

	public int getPosicao() {
	return posicao;
    }

    public void setPosicao(int posicao) {
	this.posicao = posicao;
    }

    /** 
     * Método que retorna a String do nome do domínio
     */
    public String toString() {
	return "br.ufrn.sigaa.extensao.dominio.Objetivo[id=" + id + "]";
    }

    public ListaMensagens validate() {
	ListaMensagens lista = new ListaMensagens();
	ValidatorUtil.validateRequired(objetivo, "Objetivos do Projeto", lista);
	ValidatorUtil.validateEmptyCollection("É necessário adicionar pelo menos uma atividade.", getAtividadesPrincipais(), lista);
	return lista;
    }

    /**
     * adiciona atividades ao objetivo
     * 
     * @return
     */
    public boolean addAtividadesPrincipais(ObjetivoAtividades atividades) {
	atividades.setObjetivo(this);
	return this.atividadesPrincipais.add(atividades);
    }

    public boolean isAtivo() {
	return ativo;
    }

    public void setAtivo(boolean ativo) {
	this.ativo = ativo;
    }

    public String getObservacaoExecucao() {
		return observacaoExecucao;
	}

	public void setObservacaoExecucao(String observacaoExecucao) {
		this.observacaoExecucao = observacaoExecucao;
	}

	@Override
    public boolean equals(Object obj) {
	return EqualsUtil.testEquals(this, obj, "id", "posicao");
    }

}