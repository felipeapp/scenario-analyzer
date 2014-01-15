/*
 * Sistema Integrado de Gest�o de Atividades Acad�micas - SIGAA
 * Superintend�ncia de Inform�tica - UFRN
 *
 * Created on '29/12/2006'
 *
 */
package br.ufrn.sigaa.dominio;

import static br.ufrn.arq.util.ValidatorUtil.validateRequired;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.HashCodeUtil;

/**
 * Eventos de calend�rio acad�mico que n�o est�o vinculados ao sistema, s�o cadastrados pelos usu�rios
 * @author amdantas
 */
@Entity
@Table(schema="comum", name = "evento_extra_sistema")
public class EventoExtraSistema implements Validatable {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "id_evento_extra", unique = true, nullable = false, insertable = true, updatable = true)
	private int id;

	/** descri��o do evento */
	private String descricao;

	/**
	 * caso de feriados ou eventos acad�micos que suspende atividades acad�mica
	 * e/ou administrativas
	 */
	private boolean suspensaoAtividades;

	/** interesse de tipos de usu�rios */
	/** indica se o evento � de interesse dos alunos */
	private boolean alunos = true;
	/** indica se o evento � de interesse das coordena��es de curso */
	private boolean coordenacoes = true;
	/** indica se o evento � de interesse dos departamentos*/
	private boolean departamentos = true;
	/** indica se o evento � de interesse dos docentes*/
	private boolean docentes = true;

	/** referencia ao calend�rio ao qual este evento pertence */
	@ManyToOne(cascade = {}, fetch = FetchType.LAZY)
	@JoinColumn(name = "id_calendario", unique = false, nullable = true, insertable = true, updatable = true)
	private CalendarioAcademico calendario = new CalendarioAcademico();

	/** data de inicio do evento */
	private Date inicio;
	
	/** data de fim do evento */
	private Date fim;

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public Date getFim() {
		return fim;
	}

	public void setFim(Date fim) {
		this.fim = fim;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Date getInicio() {
		return inicio;
	}

	public void setInicio(Date inicio) {
		this.inicio = inicio;
	}

	public boolean isSuspensaoAtividades() {
		return suspensaoAtividades;
	}

	public void setSuspensaoAtividades(boolean suspensaoAtividades) {
		this.suspensaoAtividades = suspensaoAtividades;
	}

	public CalendarioAcademico getCalendario() {
		return calendario;
	}

	public void setCalendario(CalendarioAcademico calendario) {
		this.calendario = calendario;
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsUtil.testEquals(this, obj, "id", "descricao");
	}

	@Override
	public int hashCode() {
		return HashCodeUtil.hashAll(id, descricao);
	}

	public boolean isAlunos() {
		return alunos;
	}

	public void setAlunos(boolean alunos) {
		this.alunos = alunos;
	}

	public boolean isCoordenacoes() {
		return coordenacoes;
	}

	public void setCoordenacoes(boolean coordenacoes) {
		this.coordenacoes = coordenacoes;
	}

	public boolean isDepartamentos() {
		return departamentos;
	}

	public void setDepartamentos(boolean departamentos) {
		this.departamentos = departamentos;
	}

	public boolean isDocentes() {
		return docentes;
	}

	public void setDocentes(boolean docentes) {
		this.docentes = docentes;
	}

	@Override
	public ListaMensagens validate() {
		
		ListaMensagens lista = new ListaMensagens();
		validateRequired(descricao, "Evento", lista);		
		validateRequired(inicio, "Data In�cio do Evento", lista);
		validateRequired(fim, "Data Fim do Evento", lista);		
		
		if (inicio != null && fim != null && inicio.after(fim))
			lista.addErro("Per�odo Inv�lido: In�cio do evento deve ser antes Fim.");

		if(calendario == null || calendario.getId() == 0) {
			lista.addErro("O evento deve estar associado a um Calend�rio. Por Favor Selecione um Calend�rio.");
		}
		
		if(!isAlunos() && ! isCoordenacoes() && !isDepartamentos()  && !isDocentes()) {
			lista.addErro("Por favor selecione ao menos um interessado no evento.");
		}		
		
		return lista;
	}


}
