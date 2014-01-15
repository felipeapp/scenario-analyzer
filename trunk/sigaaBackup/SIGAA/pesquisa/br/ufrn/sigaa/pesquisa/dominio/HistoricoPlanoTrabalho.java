/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 18/01/2007
 *
 */
package br.ufrn.sigaa.pesquisa.dominio;

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
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;

/**
 * Classe utilizada para registrar o histórico de alterações em planos de trabalho
 * 
 * @author Ricardo Wendell
 *
 */
@Entity
@Table(name = "historico_plano_trabalho", schema = "pesquisa", uniqueConstraints = {})
public class HistoricoPlanoTrabalho implements Validatable{

	private int id;
	/** Plano de trabalho ao qual o histórico se refere */
	private PlanoTrabalho planoTrabalho;
	/** status do plano de trabalho (constantes na aplicação) */
	private Integer status;
	/** Data de registro da alteração de status */
	private Date data;
	/** Registro de entrada do usuário que efetuou a operação e alterou o status do plano de trabalho */
	private RegistroEntrada registroEntrada;
    /** Tipo de Bolsa desejada para o Edital em questão. */
	private TipoBolsaPesquisa tipoBolsa;
	
	public HistoricoPlanoTrabalho() {

	}

	@Transient
	public String getDescricaoStatus() {
		return TipoStatusPlanoTrabalho.getDescricao(status);
	}
	
	@Transient
	public boolean isAvaliacao(){
		return status == TipoStatusPlanoTrabalho.APROVADO || 
				status == TipoStatusPlanoTrabalho.APROVADO_COM_RESTRICOES ||
				status == TipoStatusPlanoTrabalho.NAO_APROVADO;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data", unique = false, nullable = true, insertable = true, updatable = true)
	public Date getData() {
		return data;
	}

	@Id @GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	parameters={ @Parameter(name="hibernate_sequence", value="public.hibernate_sequence") })
	@Column(name = "id_historico_plano_trabalho", unique = true, nullable = false, insertable = true, updatable = true)
	public int getId() {
		return id;
	}

	@ManyToOne(cascade = {}, fetch = FetchType.LAZY)
	@JoinColumn(name = "id_plano_trabalho", unique = false, nullable = false, insertable = true, updatable = true)
	public PlanoTrabalho getPlanoTrabalho() {
		return planoTrabalho;
	}

	@Column(name = "status", unique = false, nullable = true, insertable = true, updatable = true, length = 20)
	public Integer getStatus() {
		return status;
	}

	@ManyToOne(cascade = {}, fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_entrada", unique = false, nullable = false, insertable = true, updatable = true)
	public RegistroEntrada getRegistroEntrada() {
		return registroEntrada;
	}

	public void setData(Date data) {
		this.data = data;
	}

	@ManyToOne(cascade = {}, fetch = FetchType.LAZY)
	@JoinColumn(name = "id_tipo_bolsa", unique = false, nullable = true, insertable = true, updatable = true)
	public TipoBolsaPesquisa getTipoBolsa() {
		return tipoBolsa;
	}

	public void setTipoBolsa(TipoBolsaPesquisa tipoBolsa) {
		this.tipoBolsa = tipoBolsa;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}
	
	public void setPlanoTrabalho(PlanoTrabalho planoTrabalho) {
		this.planoTrabalho = planoTrabalho;
	}

	public void setRegistroEntrada(RegistroEntrada registroEntrada) {
		this.registroEntrada = registroEntrada;
	}

	public ListaMensagens validate() {
		return null;
	}

}