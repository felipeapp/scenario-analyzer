/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em 05/10/2010
 * Autor: Rafael Gomes
 */

package br.ufrn.sigaa.nee.dominio;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dao.CampoAtivo;
import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.comum.dominio.TipoNecessidadeEspecial;

/**
 * Entidade responsável por relacionar tipos de necessidades educacionais especiais ao discente.
 * @author Rafael Gomes
 *
 */
@Entity
@Table(schema = "nee", name = "tipo_necessidade_solicitacao_nee")
public class TipoNecessidadeSolicitacaoNee implements PersistDB{

	@Id @GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	parameters={ @Parameter(name="hibernate_sequence", value="public.hibernate_sequence") })
	@Column(name = "id_tipo_necessidade_solicitacao_nee", unique = true, nullable = false)
	private int id;
	
	/** Discente */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_solicitacao_apoio_nee")
    private SolicitacaoApoioNee solicitacaoApoioNEE = new SolicitacaoApoioNee();
    
	/** Tipo de Necessidades Educacionais Especiais a ser relacionado ao discente. */
	@ManyToOne
	@JoinColumn(name = "id_tipo_necessidade_especial")
	private TipoNecessidadeEspecial tipoNecessidadeEspecial = new TipoNecessidadeEspecial();
	
	/** Campo destinado ao armazenamento de outra(s) Necessidade(s) Especial(s). */
	@Column(name = "observacao")
	private String observacao;
	
	/** Atributo responsável por informar se a necessidade especial do discente está ativa ou inativa. */
    @CampoAtivo
    @Column(name = "ativo")
    private boolean ativo;

    /** Data quando o tipo de necessidade especial foi cadastrada. */
	@CriadoEm
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_cadastro")
	private Date dataCadastro;
	
	/** Constructors **/
	
	public TipoNecessidadeSolicitacaoNee() {
		super();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public SolicitacaoApoioNee getSolicitacaoApoioNEE() {
		return solicitacaoApoioNEE;
	}

	public void setSolicitacaoApoioNEE(SolicitacaoApoioNee solicitacaoApoioNEE) {
		this.solicitacaoApoioNEE = solicitacaoApoioNEE;
	}

	public TipoNecessidadeEspecial getTipoNecessidadeEspecial() {
		return tipoNecessidadeEspecial;
	}

	public void setTipoNecessidadeEspecial(
			TipoNecessidadeEspecial tipoNecessidadeEspecial) {
		this.tipoNecessidadeEspecial = tipoNecessidadeEspecial;
	}

	public String getObservacao() {
		return observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	public Date getDataCadastro() {
		return dataCadastro;
	}

	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

}
