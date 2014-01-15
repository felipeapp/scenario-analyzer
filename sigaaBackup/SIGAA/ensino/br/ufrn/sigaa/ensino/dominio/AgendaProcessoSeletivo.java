/* 
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 *
 */
package br.ufrn.sigaa.ensino.dominio;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.EqualsUtil;

/**
 * Classe de domínio responsável em armazenar todas as datas de agendamento disponíveis
 * 
 * @author Mário Rizzi
 */
@Entity
@Table(name="agenda_processo_seletivo", schema="ensino")
public class AgendaProcessoSeletivo implements Validatable {

	@Id
	@Column(name="id_agenda_processo_seletivo")
	@GeneratedValue(strategy=GenerationType.SEQUENCE)
	private int id;
	
	@Column(name = "data_agendada", nullable = true)
	private Date dataAgenda;
	
	@ManyToOne
	@JoinColumn(name="id_edital_processo_seletivo")
	private EditalProcessoSeletivo editalProcessoSeletivo;
	
	@Column(name = "qtd", nullable = true)
	private int qtd;
	
	@Transient
	private Long qtdIncritosCadastrados;
	
	public int getId() {
		return id;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((dataAgenda == null) ? 0 : dataAgenda.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsUtil.testEquals(this, obj, "dataAgenda");
	}

	public void setId(int id) {
		this.id = id;
	}

	public Date getDataAgenda() {
		return dataAgenda;
	}
	
	public String getDataAgendaString(){
		if(this.dataAgenda != null)
			return CalendarUtils.format(this.dataAgenda, "dd/MM/yyyy");
		else
			return "";
	}
	
	public String getDiaSemana(){
		return CalendarUtils.diaSemana(this.dataAgenda, false); 
	}

	public void setDataAgenda(Date dataAgenda) {
		this.dataAgenda = dataAgenda;
	}

	public int getQtd() {
		return qtd;
	}

	public void setQtd(int qtd) {
		this.qtd = qtd;
	}

	public ListaMensagens validate() {
		return null;
	}

	public EditalProcessoSeletivo getEditalProcessoSeletivo() {
		return editalProcessoSeletivo;
	}

	public void setEditalProcessoSeletivo(
			EditalProcessoSeletivo editalProcessoSeletivo) {
		this.editalProcessoSeletivo = editalProcessoSeletivo;
	}

	public Long getQtdIncritosCadastrados() {
		return qtdIncritosCadastrados;
	}

	public void setQtdIncritosCadastrados(Long qtdIncritosCadastrados) {
		this.qtdIncritosCadastrados = qtdIncritosCadastrados;
	}

}
