/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 08/10/2008
 *
 */

package br.ufrn.sigaa.pesquisa.dominio;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.seguranca.log.CriadoPor;
import br.ufrn.arq.util.ValidatorUtil;

/**
 * Parecer emitido pelo NIT sobre as invenções cadastradas
 * 
 * @author Leonardo Campos
 *
 */
@Entity
@Table(name = "parecer_invencao", schema = "pesquisa", uniqueConstraints = {})
public class ParecerInvencao implements Validatable {

	@Id @GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	parameters={ @Parameter(name="hibernate_sequence", value="public.hibernate_sequence") })
	@Column(name = "id_parecer_invencao", unique = true, nullable = false, insertable = true, updatable = true)
	private int id;

	@ManyToOne
	@JoinColumn(name = "id_invencao")
	private Invencao invencao;
	
	@CriadoEm
	@Temporal(TemporalType.TIMESTAMP)
	private Date data;
	
	@CriadoPor
	@ManyToOne
	@JoinColumn(name = "id_registro_entrada")
	private RegistroEntrada registroEntrada;
	
	/** Indica se o parecer foi favorável ou não */
	private boolean favoravel = true;
	/** Texto do parecer */
	private String texto;
	/** status para o qual a invenção passou a ser após ser dado o parecer */
	private Integer status;
	
	@Transient
	private boolean notificarEmail;
	
	public ParecerInvencao() {
		
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Invencao getInvencao() {
		return invencao;
	}

	public void setInvencao(Invencao invencao) {
		this.invencao = invencao;
	}

	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
	}

	public RegistroEntrada getRegistroEntrada() {
		return registroEntrada;
	}

	public void setRegistroEntrada(RegistroEntrada registroEntrada) {
		this.registroEntrada = registroEntrada;
	}

	public boolean isFavoravel() {
		return favoravel;
	}

	public void setFavoravel(boolean favoravel) {
		this.favoravel = favoravel;
	}

	public String getTexto() {
		return texto;
	}

	public void setTexto(String texto) {
		this.texto = texto;
	}
	
	public boolean isNotificarEmail() {
		return notificarEmail;
	}

	public void setNotificarEmail(boolean notificarEmail) {
		this.notificarEmail = notificarEmail;
	}

	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();
		ValidatorUtil.validateRequired(invencao, "Invenção", lista);
		ValidatorUtil.validateRequired(favoravel, "Posição do Parecer", lista);
		ValidatorUtil.validateRequired(texto, "Texto do Parecer", lista);
		return lista;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}
}
