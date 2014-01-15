/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 21/08/2009
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

/**
 * Classe utilizada para registrar o hist�rico de altera��es em notifica��es de inven��o
 * 
 * @author Leonardo Campos
 *
 */
@Entity
@Table(name="historico_notificacao_invencao", schema = "pesquisa", uniqueConstraints = {})
public class HistoricoNotificacaoInvencao implements Validatable {

	@Id @GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	parameters={ @Parameter(name="hibernate_sequence", value="public.hibernate_sequence") })
	@Column(name = "id_historico_notificacao_invencao")
	private int id;
	
	/** Inven��o cuja entrada do hist�rico faz refer�ncia */
	@ManyToOne
	@JoinColumn(name="id_invencao")
	private Invencao invencao;
	
	/** Data da altera��o */
	@Temporal(TemporalType.TIMESTAMP)
	private Date data;
	
	/** Status da notifica��o de inven��o para o qual foi alterado */
	private int status;
	
	/** Registro de entrada do usu�rio que realizou a modifica��o */
	@ManyToOne
	@JoinColumn(name = "id_registro_entrada")
	private RegistroEntrada registroEntrada;
	
	public HistoricoNotificacaoInvencao() {
		
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

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public RegistroEntrada getRegistroEntrada() {
		return registroEntrada;
	}

	public void setRegistroEntrada(RegistroEntrada registroEntrada) {
		this.registroEntrada = registroEntrada;
	}
	
	public ListaMensagens validate() {
		return null;
	}
	
	@Transient
	public String getStatusString() {
		return TipoStatusNotificacaoInvencao.getDescricao(status);
	}
}
