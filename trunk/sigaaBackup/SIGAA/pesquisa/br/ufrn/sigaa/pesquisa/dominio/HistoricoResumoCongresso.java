/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 25/11/2009
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

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;

/**
 * Classe utilizada para registrar o histórico de alterações em resumos de congressos
 * 
 * @author Geyson Karlos
 *
 */
@Entity
@Table(name = "historico_resumo_congresso", schema = "pesquisa", uniqueConstraints = {})
public class HistoricoResumoCongresso implements Validatable {
	
	private int id;
	
	private ResumoCongresso resumoCongresso;
	
	private int status;

	private Date data;

	private RegistroEntrada registroEntrada;
	
	public HistoricoResumoCongresso(){
		
	}

	@Id @GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	parameters={ @Parameter(name="hibernate_sequence", value="public.hibernate_sequence") })
	@Column(name = "id_historico_resumo_congresso", unique = true, nullable = false, insertable = true, updatable = true)
	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@ManyToOne(cascade = {}, fetch = FetchType.LAZY)
	@JoinColumn(name = "id_resumo_congresso", unique = false, nullable = false, insertable = true, updatable = true)
	public ResumoCongresso getResumoCongresso() {
		return resumoCongresso;
	}

	public void setResumoCongresso(ResumoCongresso resumoCongresso) {
		this.resumoCongresso = resumoCongresso;
	}

	@Column(name = "status", unique = false, nullable = true, insertable = true, updatable = true, length = 20)
	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data", unique = false, nullable = true, insertable = true, updatable = true)
	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
	}

	@ManyToOne(cascade = {}, fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_entrada", unique = false, nullable = false, insertable = true, updatable = true)
	public RegistroEntrada getRegistroEntrada() {
		return registroEntrada;
	}

	public void setRegistroEntrada(RegistroEntrada registroEntrada) {
		this.registroEntrada = registroEntrada;
	}
	
	public ListaMensagens validate() {
		return null;
	}
	
	
	
}
