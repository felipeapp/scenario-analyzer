/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 09/01/2009
 *
 */
package br.ufrn.sigaa.ensino.graduacao.dominio;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.Formatador;
import br.ufrn.arq.util.HashCodeUtil;
import br.ufrn.arq.util.ValidatorUtil;


/**
 * Classe de dom�nio que representa as datas e hor�rios dispon�veis no processo seletivo de revalida��o de diplomas
 *
 * @author Mario Rizzi Rocha
 */
@Entity
@Table(name = "agenda_revalidacao_diploma", schema = "graduacao")
public class AgendaRevalidacaoDiploma implements Validatable {
		
	/** Determina o intervalo de tempo entre os agendamentos */
	public static final int INTERVALO_AGENDAMENTO = 30;
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "id_agenda_revalidacao_diploma", unique = true, nullable = false, insertable = true, updatable = true)
	private int id;
	
	/** Associa as datas e hor�rios a um edital de revalida��o de diploma*/
	@ManyToOne
	@JoinColumn(name = "id_edital_revalidacao_diploma", nullable = true)
	private EditalRevalidacaoDiploma editalRevalidacaoDiploma;
	
	/** Atribui uma data para o hor�rio informado. */
	private Date data;
	
	/** Atribui um hor�rio dispon�vel para data informada. */
	private String horario;
	
	/** Atribui o n�mero de vagas dispon�veis para data e hor�rio. */
	private Integer qtd;
	

	public AgendaRevalidacaoDiploma() {
		
	}	
	
	public AgendaRevalidacaoDiploma(Integer id) {
		this.id = id;
	}

	public Date getData() {
		return data;
	}
	
	public String getDataConvertida() {
		return Formatador.getInstance().formatarData(data);
	}

	public void setData(Date data) {
		this.data = data;
	}

	public String getHorario() {
		return horario;
	}

	public void setHorario(String horario) {
		this.horario = horario;
	}

	public Integer getQtd() {
		return qtd;
	}

	public void setQtd(Integer qtd) {
		this.qtd = qtd;
	}
	
	@Override
	public boolean equals(Object obj) {
		return EqualsUtil.testEquals(this, obj, "id");
	}

	@Override
	public int hashCode() {
		return HashCodeUtil.hashAll(id);
	}

	public ListaMensagens validate() {
		
		ListaMensagens erros = new ListaMensagens();
		ValidatorUtil.validateRequired(horario, "Nome", erros);
		ValidatorUtil.validateRequired(qtd, "Qtd", erros);
		return erros;
		
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public EditalRevalidacaoDiploma getEditalRevalidacaoDiploma() {
		return editalRevalidacaoDiploma;
	}

	public void setEditalRevalidacaoDiploma(
			EditalRevalidacaoDiploma editalRevalidacaoDiploma) {
		this.editalRevalidacaoDiploma = editalRevalidacaoDiploma;
	}

}
