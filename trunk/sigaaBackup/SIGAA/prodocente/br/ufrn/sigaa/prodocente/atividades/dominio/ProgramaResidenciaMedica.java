/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '24/04/2007'
 *
 */
package br.ufrn.sigaa.prodocente.atividades.dominio;

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
import br.ufrn.sigaa.dominio.Unidade;

/**
 * Entidade que representa um programa de residência médica, uma modalidade de ensino de pós-graduação 
 * destinada a médicos, sob a forma de curso de especialização. Funciona em instituições de saúde, 
 * sob a orientação de profissionais médicos de elevada qualificação ética e profissional, 
 * sendo considerada o "padrão ouro" da especialização médica. 
 *
 * @author Mario
 */
@Entity
@Table(name = "programa_residencia_medica",schema="prodocente")
public class ProgramaResidenciaMedica implements Validatable {

	@Id @GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	parameters={ @Parameter(name="hibernate_sequence", value="public.hibernate_sequence") })
    @Column(name = "id_programa_residencia_medica", nullable = false)
    private int id;

    @Column(name = "nome")
    private String nome;

    @ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_hospital", unique = false, nullable = false, insertable = true, updatable = true)
    private Unidade hospital;

    /** Tem como finalidade indicar se o programa de Residência Médica está ou não ativo. */
    private Boolean ativo;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_unidade_programa", unique = false, nullable = false, insertable = true, updatable = true)
    private Unidade unidadePrograma;
    
    public ProgramaResidenciaMedica(){

	}

	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();
		ValidatorUtil.validateRequired(getNome(), "Nome", lista);
		ValidatorUtil.validateRequired(getHospital(), "Hospital", lista);
		ValidatorUtil.validateRequired(getUnidadePrograma(), "Unidade do Programa", lista);
		return lista;
	}

	/**
	 * @return the descricao
	 */
	public String getNome() {
		return nome;
	}

	/**
	 * @param descricao the descricao to set
	 */
	public void setNome(String nome) {
		if (nome != null) nome  = nome.toUpperCase();
		this.nome = nome;
	}

	public String getDescricao() {
		return getHospital().getSigla() + " - " +  this.nome;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	public Unidade getHospital() {
		return this.hospital;
	}

	public void setHospital(Unidade hospital) {
		this.hospital = hospital;
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	public Boolean getAtivo() {
		return ativo;
	}

	public void setAtivo(Boolean ativo) {
		this.ativo = ativo;
	}

	public Unidade getUnidadePrograma() {
		return unidadePrograma;
	}

	public void setUnidadePrograma(Unidade unidadePrograma) {
		this.unidadePrograma = unidadePrograma;
	}

}
