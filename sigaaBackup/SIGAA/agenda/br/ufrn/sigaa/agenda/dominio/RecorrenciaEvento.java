package br.ufrn.sigaa.agenda.dominio;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.joda.time.LocalDate;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.sigaa.agenda.util.AgendaUtils;

import com.google.ical.compat.jodatime.LocalDateIteratorFactory;
import com.google.ical.values.DateValue;
import com.google.ical.values.Frequency;
import com.google.ical.values.RRule;
import com.google.ical.values.Weekday;
import com.google.ical.values.WeekdayNum;

/**
 * <p>Entidade que armazena a regra de recorr�ncia associada a um evento. </p>
 * <p>Esta regra ir� definir como um evento se repete durante um certo per�do, ou por um certo n�mero de vezes) e ser� persistida 
 * por uma String atrav�s de sua representa��o no formato definido na RFC 2445. Exemplos de representa��o: <br>
 * <ul>
 *    <li> "RRULE:FREQ=DAILY;UNTIL=20021231T000000Z"                ==>  <i>Daily for the rest of 2002 </i> </li>
 *    <li> "RRULE:FREQ=DAILY;INTERVAL=10"                           ==>  <i>Every 10th day forever </i> </li>
 *    <li> "RRULE:FREQ=WEEKLY;BYDAY=TU,TH;UNTIL=20021031T000000Z"   ==>  <i>Every Tuesday and Thursday until the end of October </i> </li>
 *    <li> "RRULE:FREQ=YEARLY;INTERVAL=2;BYYEARDAY=243"             ==>  <i>Every other year on the 243rd day </i> </li>
 * </ul>
 * </p>
 * 
 * <p>Observa��o: A classe auxiliar <code>RRule</code> ajunda a montar o padr�o definido na RFC 2445 </p>
 * 
 * @author wendell
 *
 */
@Entity
@Table(schema = "agenda", name = "recorrencia_evento")
public class RecorrenciaEvento  implements PersistDB{

	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator", 
			parameters={ @Parameter(name="sequence_name", value="agenda.recorrencia_sequence") })
	@Column(name="id_recorrencia_evento") 
	private int id;
	
	/**  O evento que essa regra de recorr�ncia faz parte */
	@ManyToOne
	@JoinColumn(name = "id_evento", referencedColumnName ="id_evento", nullable=false)
	private Evento evento;
	
	/** A String no padr�o da RFC 2445 que cont�m as regras de fequ�ncia de ocorrencia do evento */
	@Column(name="i_cal")
	private String iCal;

	/** Classe auxiliar para a gera��o da string no padr�o da RFC 2445*/
	@Transient
	private RRule rRule;

	
	/* ********************************************************************************
	 *  Dados auxiliares para criar as regras de recorencia 
	 *  *******************************************************************************/
	
	/** O evento n�o vai ocorrer em nenhum dia espec�fico da semana */
	@Transient
	private boolean ocorreTodosOsDias = true;
	
	/** O evento vai ocorres aos domingos */
	@Transient
	private boolean ocorreAosDomingos = false;
	
	/** O evento vai ocorres �s segundas */
	@Transient
	private boolean ocorreAsSegundas = false;
	
	/** O evento vai ocorres �s ter�as */
	@Transient
	private boolean ocorreAsTercas = false;
	
	/** O evento vai ocorres �s quartas */
	@Transient
	private boolean ocorreAsQuartas = false;
	
	/** O evento vai ocorres �s quintas */
	@Transient
	private boolean ocorreAsQuintas = false;
	
	/** O evento vai ocorres �s sextas */
	@Transient
	private boolean ocorreAsSextas = false;
	
	/** O evento vai ocorres aos s�bados */
	@Transient
	private boolean ocorreAosSabados = false;
	
	
	
	/* ***********************************************************************************/
	
	
	/**
	 * Cria uma recorr�ncia vazia. Com as regras de recorr�ncia padr�o (Frequencia = Diaria, Intervalo repeti��o = 1 )
	 */
	public RecorrenciaEvento() {
		super();
		rRule = new RRule();
		this.rRule.setInterval(1);             // por padr�o o intervalo � 1
		this.rRule.setFreq(Frequency.DAILY);   // por padr�o a frequencia � diaria
		this.iCal = rRule.toIcal();
	}
	
	/**
	 * Cria uma recorencia a partir da String com as regras de recurs�o passadas
	 * @param iCal
	 * @throws ParseException
	 */
	public RecorrenciaEvento(String iCal) throws ParseException {
		this.iCal = iCal;
		this.rRule = new RRule(iCal);
	}
	
	/**
	 * Cria uma recorrencia a parte das regras de recurs�o passadas
	 * @param rRule
	 */
	public RecorrenciaEvento(RRule rRule) {
		this.rRule = rRule;
		this.iCal = rRule.toIcal();
	}
	
	
	/**
	 * <p>M�todo que cont�m as regras para cria��o dos eventos que possuem alguma recorrencia cadastrada.</p>
	 * 
	 * @param inicio
	 * @param fim
	 * @return
	 * @throws ParseException 
	 */
	public List<Evento> getOcorrencias() throws ParseException {
		
		List<Evento> eventosRecorrentes = new ArrayList<Evento>();
		
		this.rRule = new RRule(iCal);
		
		/*
		 *  Avan�a a data de in�cio do evento para a primeira ocorr�ncia 
		 *  
		 *  Usado nos casos em que o dia da semana (Dom, Seg,..., Sab) em que o evento ocorre n�o coincide com o dia da semana da data inicial do evento.
		 *  
		 *  Por exemplo O usu�rio cadastrou que o evento ocorre toda quarta-feita, mas a data inical do evento cadastrada � uma segunda feita. Neste caso
		 *  o sistema avan�a at� a pr�xima quarta, se for um evento com dia espac�fico da semana para ocorrer.  
		 */
		Date inicio = evento.getDataInicio();
		
		while (!inicio.after(evento.getRecorrencia().getRepetirAte())) {
			Weekday diaSemana = Weekday.valueOf(AgendaUtils.toDateValue(inicio));
			if (evento.getRecorrencia().getDiasSemana().contains(new WeekdayNum(0, diaSemana)) || evento.getRecorrencia().isOcorreTodosOsDias()) 
				break;
			inicio = CalendarUtils.adicionaUmDia(inicio);
		}
		
		
		
		Calendar cDataInicioEvento = Calendar.getInstance();
		cDataInicioEvento.setTime(CalendarUtils.configuraTempoDaData(inicio, 0, 0, 0, 0));
		
		LocalDate start = new LocalDate(cDataInicioEvento.get(Calendar.YEAR)
				 , cDataInicioEvento.get(Calendar.MONTH)+1, cDataInicioEvento.get(Calendar.DAY_OF_MONTH));  // O m�s em Java � baseado em Zero, mas o LocalDate � baseado em 1

		for (LocalDate dataVirtualDoEvento : LocalDateIteratorFactory.createLocalDateIterable(iCal, start, true)) {
			
			Calendar cCorrecaoData = Calendar.getInstance();
			
			cCorrecaoData.set(Calendar.DAY_OF_MONTH, dataVirtualDoEvento.getDayOfMonth());
			cCorrecaoData.set(Calendar.MONTH , dataVirtualDoEvento.getMonthOfYear()-1);     // O m�s em Java � baseado em Zero, mas o LocalDate � baseado em 1
			cCorrecaoData.set(Calendar.YEAR, dataVirtualDoEvento.getYear());
			cCorrecaoData.setTime(CalendarUtils.configuraTempoDaData(cCorrecaoData.getTime(), 0, 0, 0, 0));
			eventosRecorrentes.add( criaOcorrencia(cCorrecaoData.getTime()) );
			
		}
		
		return eventosRecorrentes;
		
	}

	/**
	 *   <p>Cria eventos virtuais para agenda.</p>
	 *   <p>Cria eventos virtuais s�o aqueles que s�o visualizados pelos usu�rios mais n�o existem no sistema, s�o criados a partir das suas regras de recurs�o</p>
	 *
	 * @param dataVirtualDoEvento
	 * @return
	 * @throws ParseException 
	 */
	private Evento criaOcorrencia(Date dataVirtualDoEvento) throws ParseException{
		
		/* *******************************************************************
		 *  Configurar a hora do evento que est� na data real do evento
		 * ******************************************************************/
		Date dataInicioEvento = null;
		Date dataFinalEvento = null;
		
		if(! this.evento.isDiaTodo()){
			
			Calendar c = Calendar.getInstance();
			c.setTime(evento.getDataInicio());
			
			dataInicioEvento =  CalendarUtils.configuraTempoDaData(dataVirtualDoEvento,  c.get(Calendar.HOUR_OF_DAY)
					, c.get(Calendar.MINUTE), c.get(Calendar.SECOND), 0);
			
			Calendar c2 = Calendar.getInstance();
			c2.setTime(evento.getDataFim());
			
			dataFinalEvento =  CalendarUtils.configuraTempoDaData(dataVirtualDoEvento,  c2.get(Calendar.HOUR_OF_DAY)
					, c2.get(Calendar.MINUTE), c2.get(Calendar.SECOND), 0);
		}else{
			dataInicioEvento = dataVirtualDoEvento;
			dataFinalEvento = dataVirtualDoEvento;
		}
		
		Evento eventoVirtual = new Evento(this.getEvento().getId(), this.getEvento().getTitulo(), dataInicioEvento
				, dataFinalEvento
				, this.getEvento().isDiaTodo()
				, this.getEvento().getDescricao()         // a mesma descri��o do evento que o gerou
				, this.getEvento().getLocal()             // o mesmo local do evento que o gerou
				, this.getEvento().getRecorrencia() != null ? this.getEvento().getRecorrencia().getICal() : ""
				, this.getEvento().getExcecaoRecorrencia() != null ? this.getEvento().getExcecaoRecorrencia().getICal() : "");                                   // N�o possui regra de n�o recorr�ncia
		
		
		// Guarda as data do evento que o gerou apenas para fins de visualizar as informa��es pelo usu�rio //
		eventoVirtual.setDataInicioOriginal(evento.getDataInicio());
		eventoVirtual.setDataFimOriginal(evento.getDataFim());
		
		eventoVirtual.setStatus(this.getEvento().getStatus());  // o mesmo status do evento que o gerou
		
		return eventoVirtual;
		
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Evento getEvento() {
		return evento;
	}

	public void setEvento(Evento evento) {
		this.evento = evento;
	}

	public String getICal() {
		return iCal;
	}

	public void setICal(String cal) {
		iCal = cal;
		try {
			this.rRule = new RRule(cal);
		} catch (ParseException e) {
			this.rRule = null;
		}
	}

	public RRule getRRule() {
		return rRule;
	}

	public void setRRule(RRule rule) {
		rRule = rule;
		if (rRule != null) 
			iCal = rRule.toIcal();
		else
			iCal = null;
	}
	
	public Frequency getFrequencia() {
		return rRule.getFreq();
	}
	
	
	/**
	 * 
	 * Configura a freq�ncia do evento.
	 *  
	 *  <br/>
	 *  Valores definidos nas constantes
	 *   <ul>
	 *     <li>Frequency.DAILY</li>
	 *     <li>Frequency.WEEKLY</li>
	 *     <li>Frequency.MONTHLY</li>
	 *     <li>Frequency.YEARLY</li>
	 *   </ul>
	 *
	 * @param freq
	 * @return
	 */
	public void setFrequencia( Frequency freq ) {
		rRule.setFreq(freq);
		iCal = rRule.toIcal();
	}
	
	/**
	 * 
	 * Configura a intervalo de repeti��o de um evento
	 *   </ul>
	 *
	 * @param freq
	 * @return
	 */
	public void setIntervalo( int intervalo ) {
		rRule.setInterval(intervalo);
	}
	
	
	public RecorrenciaEvento configuraFrequencia( Frequency freq ) {
		rRule.setFreq(freq);
		iCal = rRule.toIcal();
		return this;
	}
	
	
	/**
	 * 
	 *  Configura os dias da semana espec�ficos em que o evento deve ocorrer
	 *  
	 *  <br/>
	 *  Valores definidos nas constantes
	 *   <ul>
	 *    	<li>Weekday.SU</li>
	 *      <li>Weekday.MO</li>
	 *  	<li>Weekday.TU</li>
	 *      <li>Weekday.WE</li>
	 *      <li>Weekday.TH</li>
	 *      <li>Weekday.FR</li>
	 *  	<li>Weekday.SA</li>
	 *   </ul>
	 *
	 * @param freq
	 * @return
	 */
	public RecorrenciaEvento setDiasSemana(List<WeekdayNum> dias) {
		rRule.setByDay(dias);
		iCal = rRule.toIcal();
		return this;
	}
	
	
	/**
	 * 
	 *  Retorna os dias da semana espec�ficos em que o evento deve ocorrer
	 *  
	 *  <br/>
	 *  Valores definidos nas constantes
	 *   <ul>
	 *    	<li>Weekday.SU</li>
	 *      <li>Weekday.MO</li>
	 *  	<li>Weekday.TU</li>
	 *      <li>Weekday.WE</li>
	 *      <li>Weekday.TH</li>
	 *      <li>Weekday.FR</li>
	 *  	<li>Weekday.SA</li>
	 *   </ul>
	 *
	 * @param freq
	 * @return
	 */
	public List<WeekdayNum> getDiasSemana() {
		return rRule.getByDay();
	}
	
	/**
	 * 
	 * Cria os dias da semana espac�ficos em que o evento ocorre dependendo do que o usu�rio escolheu na tela.
	 *  
	 */
	public RecorrenciaEvento configuraDiasSemana() {
		
		List<WeekdayNum> listaDiasSemanaOcorreEvento = new ArrayList<WeekdayNum>();
		
		if(ocorreTodosOsDias)
			return this;
		
		if(ocorreAosDomingos)
			listaDiasSemanaOcorreEvento.add( new WeekdayNum(0, Weekday.SU));  // todos os domingos at� a data final
		if(ocorreAsSegundas)
			listaDiasSemanaOcorreEvento.add( new WeekdayNum(0, Weekday.MO )); // todos as segundas at� a data final
		if(ocorreAsTercas)
			listaDiasSemanaOcorreEvento.add( new WeekdayNum(0, Weekday.TU));  // todos as ter�as at� a data final
		if(ocorreAsQuartas)
			listaDiasSemanaOcorreEvento.add( new WeekdayNum(0, Weekday.WE));  // todos as quartas at� a data final
		if(ocorreAsQuintas)
			listaDiasSemanaOcorreEvento.add( new WeekdayNum(0, Weekday.TH));  // todos as quintas at� a data final
		if(ocorreAsSextas)
			listaDiasSemanaOcorreEvento.add( new WeekdayNum(0, Weekday.FR));  // todos as sextas at� a data final
		if(ocorreAosSabados)
			listaDiasSemanaOcorreEvento.add( new WeekdayNum(0, Weekday.SA));  // todos as s�bados at� a data final
		
		setDiasSemana(listaDiasSemanaOcorreEvento);
		
		return this;
		
	}
	
	
	/**
	 *  Configura as vari�veis qeu mostra em que dia da semana o vento ocorre a partir dos dias da semana guardados na express�o do iCal.
	 *  
	 */
	public void recuperaDiasSemana() {
		
		List<WeekdayNum> listaDiasSemanaOcorreEvento = getDiasSemana();
		
		for (WeekdayNum weekdayNum : listaDiasSemanaOcorreEvento) {
			
			if(weekdayNum.wday == Weekday.SU)
				ocorreAosDomingos = true;
			if(weekdayNum.wday == Weekday.MO)
				ocorreAsSegundas = true;
			if(weekdayNum.wday == Weekday.TU)
				ocorreAsTercas = true;
			if(weekdayNum.wday == Weekday.WE)
				ocorreAsQuartas = true;
			if(weekdayNum.wday == Weekday.TH)
				ocorreAsQuintas = true;
			if(weekdayNum.wday == Weekday.FR)
				ocorreAsSextas = true;
			if(weekdayNum.wday == Weekday.SA)
				ocorreAosSabados = true;
			
		}
		
		if(ocorreAosDomingos == false && ocorreAsSegundas == false && ocorreAsTercas == false 
				&& ocorreAsQuartas == false && ocorreAsQuintas == false && ocorreAsSextas == false && ocorreAosSabados == false ){
			ocorreTodosOsDias = true;
		}else
			ocorreTodosOsDias = false;
		
	}
	
	
	
	public Date getRepetirAte() {
		DateValue until = rRule.getUntil();
		if (until != null) {
			// em java o m�s come�a com 0
			return CalendarUtils.createDate(until.day(), until.month()-1, until.year());
		} else {
			return null;
		}
	}
	
	public void setRepetirAte(Date data) {
		if(data != null) {
			rRule.setUntil( AgendaUtils.toDateValue(data) );
			iCal = rRule.toIcal();
		}
	}
	
	public RecorrenciaEvento configuraRepetirAte(Date data) {
		if(data != null) {
			rRule.setUntil( AgendaUtils.toDateValue(data) );
			iCal = rRule.toIcal();
		}
		return this;
	}

	public boolean isOcorreTodosOsDias() {
		return ocorreTodosOsDias;
	}

	public void setOcorreTodosOsDias(boolean ocorreTodosOsDias) {
		
		if(ocorreTodosOsDias == true){
			ocorreAosDomingos = false;
			ocorreAsSegundas = false;
			ocorreAsTercas = false;
			ocorreAsQuartas = false;
			ocorreAsQuintas = false;
			ocorreAsSextas = false;
			ocorreAosSabados = false;
		}
		
		this.ocorreTodosOsDias = ocorreTodosOsDias;
	}

	public boolean isOcorreAosDomingos() {
		return ocorreAosDomingos;
	}

	public void setOcorreAosDomingos(boolean ocorreAosDomingos) {
		
		if(ocorreAosDomingos == true){
			this.ocorreTodosOsDias = false;
		}
		
		this.ocorreAosDomingos = ocorreAosDomingos;
	}

	public boolean isOcorreAsSegundas() {
		return ocorreAsSegundas;
	}

	public void setOcorreAsSegundas(boolean ocorreAsSegundas) {
		if(ocorreAsSegundas == true){
			this.ocorreTodosOsDias = false;
		}
		this.ocorreAsSegundas = ocorreAsSegundas;
	}

	public boolean isOcorreAsTercas() {
		return ocorreAsTercas;
	}

	public void setOcorreAsTercas(boolean ocorreAsTercas) {
		
		if(ocorreAsTercas == true){
			this.ocorreTodosOsDias = false;
		}
		
		this.ocorreAsTercas = ocorreAsTercas;
	}

	public boolean isOcorreAsQuartas() {
		return ocorreAsQuartas;
	}

	public void setOcorreAsQuartas(boolean ocorreAsQuartas) {
		
		if(ocorreAsQuartas == true){
			this.ocorreTodosOsDias = false;
		}
		
		this.ocorreAsQuartas = ocorreAsQuartas;
	}

	public boolean isOcorreAsQuintas() {
		return ocorreAsQuintas;
	}

	public void setOcorreAsQuintas(boolean ocorreAsQuintas) {
		if(ocorreAsQuintas == true){
			this.ocorreTodosOsDias = false;
		}
		this.ocorreAsQuintas = ocorreAsQuintas;
	}

	public boolean isOcorreAsSextas() {
		return ocorreAsSextas;
	}

	public void setOcorreAsSextas(boolean ocorreAsSextas) {
		if(ocorreAsSextas == true){
			this.ocorreTodosOsDias = false;
		}
		this.ocorreAsSextas = ocorreAsSextas;
	}

	public boolean isOcorreAosSabados() {
		return ocorreAosSabados;
	}

	public void setOcorreAosSabados(boolean ocorreAosSabados) {
		if(ocorreAosSabados == true){
			this.ocorreTodosOsDias = false;
		}
		this.ocorreAosSabados = ocorreAosSabados;
	}
	
}
