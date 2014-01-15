/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Created on 29/01/2013
 *
 */
package br.ufrn.sigaa.ensino.dominio;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.pessoa.dominio.Discente;

/**
 * Classe que representa a valida��o do v�nculo do discente ingressante
 * @author Diego J�come
 */
@Entity
@Table(name="validacao_vinculo", schema="ensino")
public class ValidacaoVinculo implements PersistDB{

	/** Chave prim�ria. */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
		           parameters={ @Parameter(name="sequence_name", value="ensino.validacao_vinculo_sequence") })
    @Column(name = "id_validacao_vinculo")
	private int id;
	
	/** Discente que teve o v�nculo validado */
	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_discente")
	private Discente discente;
	
	/** Usu�rio que validou o v�nculo do discente */
	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_usuario_validacao")
	private Usuario usuarioValidacao;
	
	/**
	 * Data em que o v�nculo foi validado
	 */
	@Column(name="data_validacao")
	private Date dataValidacao;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Date getDataValidacao() {
		return dataValidacao;
	}

	public void setDataValidacao(Date dataValidacao) {
		this.dataValidacao = dataValidacao;
	}

	public void setUsuarioValidacao(Usuario usuarioValidacao) {
		this.usuarioValidacao = usuarioValidacao;
	}

	public Usuario getUsuarioValidacao() {
		return usuarioValidacao;
	}

	public void setDiscente(Discente discente) {
		this.discente = discente;
	}

	public Discente getDiscente() {
		return discente;
	}
}
