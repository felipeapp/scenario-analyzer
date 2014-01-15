/*
 * Sistema Integrado de Gest�o de Atividades Acad�micas
 * Superintend�ncia de Inform�tica - UFRN
 *
 * Created on 22/09/2006
 *
 */
package br.ufrn.sigaa.ensino.dominio;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.dominio.AbstractMovimento;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.Formatador;
import br.ufrn.arq.util.HashCodeUtil;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.dominio.Unidade;

/**
 * Essa Entidade possibilita o cadastro dos hor�rios das aulas para os diversos n�veis 
 * de ensino e unidades acad�micas, podendo cadastrar o turno no qual a aula ser� lecionada, bem como a hora inicial 
 * da aula e a hora final da mesma.
 * 
 * @author wendell
 *
 */
@Entity
@Table(schema="ensino")
@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
public class Horario extends AbstractMovimento implements Validatable, Comparable<Horario>, TipoHorario {

	/** Chave prim�ria */
	@Id
	@Column (name="id_horario")
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="public.hibernate_sequence") })
	private int id;

	/** Hora inicial do hor�rio */
	@Temporal(TemporalType.TIME)
	private Date inicio;

	/** Hora final do hor�rio */
	@Temporal(TemporalType.TIME)
	private Date fim;

	/** Unidade no qual se est� criando o hor�rio */
	@ManyToOne (fetch=FetchType.EAGER)
	@JoinColumn(name="id_unidade")
	private Unidade unidade = new Unidade();

	/** Ordem atribu�da ao hor�rio */
	private Short ordem;

	/** Tipo de hor�rio cadastrado (Ex.: Manh�, Tarde ou Noite) */
	private Short tipo; 

	/** Serve para indicar se o atributo est� sendo usado ou n�o */
	private boolean ativo;

	/** Para saber qual o n�vel do hor�rio que est� sendo cadastrado. */
	private char nivel;

/*	@ManyToOne
	@JoinColumn(name="id_curso_lato")
	private CursoLato cursoLato;

	private String descricao;
*/

	public Horario() {
	}
	
	public Horario(int iden) {
		id = iden;
	}
	/**
	 * Serve para formatar o hora para o formato hora/minuto
	 * Ex.: 12:00
	 *  
	 * @param i
	 * @param f
	 */
	public Horario(String i, String f) {
		DateFormat df = new SimpleDateFormat("HH:mm");
		try {
			inicio = df.parse(i);
			fim = df.parse(f);
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	/** Retorna Hora final do hor�rio */ 
	public Date getFim() {
		return fim;
	}

	/** Pega Hora final do hor�rio */
	public void setFim(Date fim) {
		this.fim = fim;
	}

	/** Retorna o n�vel do Hor�rio */
	public char getNivel() {
		return nivel;
	}

	/** Seta o n�vel do Hor�rio */
	public void setNivel(char nivel) {
		this.nivel = nivel;
	}

	/** Retorna a chave prim�ria */
	public int getId() {
		return id;
	}

	/** Seta a chave prim�ria */
	public void setId(int id) {
		this.id = id;
	}

	/** Retorna a hora inicial  */
	public Date getInicio() {
		return inicio;
	}

	/** Seta hora inicial */
	public void setInicio(Date inicio) {
		this.inicio = inicio;
	}

	/** Retorna a unidade para qual o Hor�rio foi cadastrado */
	public Unidade getUnidade() {
		return unidade;
	}

	/** Seta a unidade para qual o hor�rio foi cadastrado */
	public void setUnidade(Unidade unidade) {
		this.unidade = unidade;
	}

	/** Retorna para saber se o hor�rio est� ativo ou n�o */
	public boolean isAtivo() {
		return ativo;
	}

	/** Seta se o hor�rio est� ativo ou n�o */
	public void setAtivo(boolean status) {
		this.ativo = status;
	}

	/**
	 * Serve para Formatar a Hora Inicial para HH:mm
	 * @return
	 */
	@Transient
	public String getDescInicio() {
		try {
			return Formatador.getInstance().formatarHora(getInicio());
		} catch (Exception e) {
			return null;
		}
	}

	/** Serve para formatar a Hora Final para HH:mm */
	@Transient
	public String getDescFim() {
		try {
			return Formatador.getInstance().formatarHora(getFim());
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Retorna uma string com a hora inicial e a final.
	 * @return
	 */
	@Transient
	public String getHoursDesc() {
		return getDescInicio() + " - " + getDescFim();
	}

	/**
	 * Retorna uma string concatenando a {@link #getHoursDesc()} com o turno e a ordem do hor�rio.
	 * @return
	 */
	@Transient
	public String getDescricao() {
		return getHoursDesc() + " / " + getTurno() + "("+ordem+")";
	}
	
	@Transient
	public String getDescricaoNivel(){
		return NivelEnsino.getDescricao(nivel);
	}
	
	/** Retorna o tipo de hor�rio cadastrado ex.: Manha, tarde ou noite*/
	public Short getTipo() {
		return tipo;
	}

	/** Seta o tipo de hor�rio ex.: Manh�, tarde ou noite */
	public void setTipo(Short tipo) {
		this.tipo = tipo;
	}

	@Transient
	public String getOrdemFormatado(){
		return ordem + "� hor�rio"; 
	}
	
	/** Retorna o Turno do hor�rio cadastrado. */
	@Transient
	public String getTurno() {
		if (tipo == null)
			return " ";
		if (tipo == MANHA)
			return "Manh�";
		else if (tipo == TARDE)
			return "Tarde";
		else if (tipo == NOITE)
			return "Noite";
		return " ";
	}

	/** Retorna inicial do tipo de hor�rio */
	@Transient
	public char getTurnoChar() {
		if (tipo == null)
			return 'I';
		if (tipo == MANHA)
			return 'M';
		else if (tipo == TARDE)
			return 'T';
		else if (tipo == NOITE)
			return 'N';
		return 'I';
	}

	/** Seta o Turno do hor�rio cadastrado */
	public void setTurno(char t) {
		if (tipo == 'M')
			tipo = MANHA;
		else if (tipo == 'T')
			tipo = TARDE;
		else if (tipo == 'N')
			tipo = NOITE;
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsUtil.testEquals(this, obj, "id");
	}

	@Override
	public int hashCode() {
		return HashCodeUtil.hashAll(id);
	}

	public Short getOrdem() {
		return ordem;
	}

	public void setOrdem(Short ordem) {
		this.ordem = ordem;
	}

	/** Valida��o dos campos obrigat�rios e valida��o da data. */
	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();
		
		if (getUnidade().getId() == 0) {
			ValidatorUtil.validateRequired(null, "Unidade Respons�vel", lista);	
		}
		
		if (getNivel() == '0') {
			ValidatorUtil.validateRequired(null, "N�vel de Ensino", lista);
		}
		
		ValidatorUtil.validateRequired(getDescFim(), "Hora de In�cio", lista);
		ValidatorUtil.validateRequired(getDescInicio(), "Hora de Fim", lista);
		
		if (inicio != null && getDescInicio().compareTo(getDescFim()) > 0 ){
			lista.addErro("Hora de Fim deve ser superior a Hora de In�cio.");
			setInicio(this.inicio);
		}

		ValidatorUtil.validateRequiredId(tipo, "Turno", lista);
		
		ValidatorUtil.validateRequired(ordem, "Ordem", lista);
		
		return lista;
	}
	
	
	/**
	 * Verifica se o hor�rio passado como argumento entra em conflito de hora
	 * 
	 * @param h
	 * @return
	 */
	public boolean checarConflitoHorario(Horario h) {
		return !(h.inicio.before(this.inicio) && !h.fim.after(inicio)
			|| h.fim.after(this.fim) && !h.inicio.before(this.fim));
//		return CalendarUtils.isIntervalosDeDatasConflitantes(inicio, fim, h.getInicio(), h.getFim());
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return getDescricao();
	}

	@Override
	public int compareTo(Horario other) {
		long cmp = NivelEnsino.tabela.get(this.nivel) - NivelEnsino.tabela.get(other.nivel);
		if (cmp == 0)
			cmp = this.inicio.getTime() - other.inicio.getTime();
		if (cmp == 0)
			cmp = this.id - other.id;
		// convertendo long para int, para retornar um valor 
		return cmp == 0? 0 : cmp > 1l ? 1 : -1;
	}
}