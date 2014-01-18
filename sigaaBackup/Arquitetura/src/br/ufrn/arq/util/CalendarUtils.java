/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 08/07/2009
 */
package br.ufrn.arq.util;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.time.DateUtils;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Interval;
import org.joda.time.LocalDate;
import org.joda.time.Minutes;
import org.joda.time.Months;
import org.joda.time.Period;
import org.joda.time.Weeks;
import org.joda.time.Years;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

import br.ufrn.arq.dao.Database;
import br.ufrn.arq.dao.JdbcTemplate;
import br.ufrn.arq.erros.DAOException;

/**
 * Classe que implementa operações comuns sobre datas, tais como:
 * <ul>
 * <li>Comparar datas</li>
 * <li>Adicionar um dia à uma data</li>
 * <li>Calcular a quantidade de dias entre duas datas</li>
 * <li>Etc.</li>
 * </ul>
 *
 * @author Gleydson Lima
 * @author David Pereira
 */
public class CalendarUtils {

	
	/** Constante que define o número de horas em um dia. */
	public static final int HORAS = 24;
	
	/** Constante que define o número de minutos em uma hora. */
	public static final int MINUTOS = 60;
	
	/** Constante que define o número de segundos em um minuto. */
	public static final int SEGUNDOS = 60;
	
	/** Constante que define um milésimo de segundo. */
	public static final  int MILESIMOS = 1000;
	
	/**
	 * Incrementa uma data em um dia
	 * 
	 * @param data
	 * @return
	 */
	public static Date adicionaUmDia(Date data) {
		return adicionaDias(data, 1);
	}
	
	/**
	 * Incrementa uma data em uma quantidade de dias passada como parâmetro
	 * 
	 * @param data
	 * @return
	 */
	public static Date adicionaDias(Date data, int dias) {
		Calendar c = Calendar.getInstance();
		c.setTime(data);
		c.add(Calendar.DAY_OF_MONTH, dias);
		return c.getTime();
	}

	/**
	 * Compara dias datas sem considerar as horas, apenas dia,
	 * mês e ano.
	 * @param data1
	 * @param data2
	 * @return
	 */
	public static int compareTo(Date data1, Date data2) {
		return (int) -diferencaDias(data1, data2);
	}
	
	/**
	 * Calcula a quantidade de meses entre duas data no máximo de 12 meses (1 ano)
	 * 
	 * @param dataInicio
	 * @param dataFim
	 * @return
	 */
	public static int calculaMesAno(Date dataInicio, Date dataFim){
		int meses = calculoMeses(dataInicio,dataFim);
		if (meses > 12)
			meses = 12;
		return meses;
	}

	/**
	 * Método que calcula a quantidade de anos existentes entre duas datas
	 * 
	 * @param dataInicio
	 * @param dataFim
	 * @return
	 */
	public static int calculoAnos(Date dataInicio, Date dataFim){
		DateTime inicio = new DateTime(dataInicio.getTime());
		DateTime fim = new DateTime(dataFim.getTime());
		
		return Years.yearsBetween(inicio, fim).getYears();
	}

	/** Retorna a quantidade de dias existentes entre duas datas
	 * @param dataInicio
	 * @param dataFim
	 * @return
	 */
	public static int calculoDias (Date dataInicio, Date dataFim){
		DateTime inicio = new DateTime(dataInicio.getTime());
		DateTime fim = new DateTime(dataFim.getTime());
		
		return Days.daysBetween(inicio, fim).getDays();
	}

	/**
	 * Faz o cálculo da quantidade de dias existentes entre duas datas,
	 * Incluindo o dia de início e o dia de término na contagem
	 * @param dataInicio
	 * @param dataFim
	 * @return
	 * @deprecated Usar o método calculoDias()
	 */
	@Deprecated
	public static int calculoDiasIntervalosFechados(Date dataInicio, Date dataFim){
		return calculoDias(dataInicio, dataFim) + 1;
	}

	/**
	 * Método que calcula a quantidade de meses existentes entre duas datas
	 */
	public static int calculoMeses(Date dataInicio, Date dataFim){
		if(dataFim == null) dataFim = new Date();
		if(dataInicio == null) return 0;
		
		LocalDate inicio = new LocalDate(dataInicio.getTime());
		LocalDate fim = new LocalDate(dataFim.getTime());

		if (fim.dayOfMonth().withMaximumValue().equals(fim))
			return Months.monthsBetween(inicio, fim).getMonths() + 1;
		else
			return Months.monthsBetween(inicio, fim).getMonths();
			
	}

	/**
	 * 
	 * Método que configura a tempo de uma data para o tempo passado como argumento.
	 *
	 * @param data ??/??/???? xx:xx:xx:xxx
	 * @param hora HH
	 * @param minuto mm
	 * @param segundos ss
	 * @param milisegundos yyy
	 * 
	 * @return ??/??/???? HH:mm:ss:yyy
	 */
	public static Date configuraTempoDaData(Date data, int hora, int minuto, int segundos, int milisegundos){
		
		Calendar c = Calendar.getInstance();
		c.setTime(data);
		c.set(Calendar.HOUR_OF_DAY, hora);
		c.set(Calendar.MINUTE, minuto);
		c.set(Calendar.SECOND, segundos);
		c.set(Calendar.MILLISECOND, milisegundos);
		
		return c.getTime();
	}

	/**
	 * Retorna, dentre uma coleção de datas informadas, aquela mais próxima de uma data de referência
	 * 
	 * @param referencia
	 * @param datas
	 * @return
	 */
	public static Date dataMaisProxima(Date referencia, Collection<Date> datas) {
		Date melhorData = null;
		long diff = Long.MAX_VALUE;
	
		for (Date data : datas) {
			if (Math.abs(data.getTime() - referencia.getTime()) < diff) {
				diff = Math.abs(data.getTime() - referencia.getTime());
				melhorData = data;
			}
		}
	
		return melhorData;
	}
	
	/**
	 * Retorna a diferença em dias entre duas datas
	 * 
	 * @param inicio
	 * @param fim
	 * @return
	 * @deprecated Usar o método calculoDias()
	 */
	@Deprecated
	public static long diferencaDias(Date inicio, Date fim) {
		return calculoDias(inicio, fim);
	}
	
	
	/**
	 * Retorna a diferença em meses entre duas datas
	 * 
	 * @param inicio
	 * @param fim
	 * @return
	 * @deprecated Usar o método calculoMeses()
	 */
	@Deprecated
	public static long diferencaMeses(Date inicio, Date fim) {
		return calculoMeses(inicio, fim);
	}
	
	
	/**
	 * Retorna o ano atual
	 *
	 * @param data
	 * @return
	 */
	public static Integer getAno(Date data) {
		Calendar c = Calendar.getInstance();
		if (data != null) {
			c.setTime(data);
			return c.get(Calendar.YEAR);
		} else
			return null;
	}

	/**
	 * Retorna o ano corrente
	 * 
	 * @return
	 */
	public static int getAnoAtual() {
		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		return c.get(Calendar.YEAR);
	}

	/**
	 * Retorna uma data contendo o último dia do mês e ano passados
	 *
	 * @param mes
	 * @param ano
	 * @return
	 */
	public static Date getMaximoDia(int mes, int ano) {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, ano);
		cal.set(Calendar.MONTH, mes > 0 ? mes - 1 : mes);
		// primeiro dia do mês
		cal.set(Calendar.DAY_OF_MONTH, 1);
		
		Calendar novo = Calendar.getInstance();
		novo.setTime(cal.getTime());
		int max = novo.getActualMaximum(Calendar.DAY_OF_MONTH);
		
		cal.set(Calendar.DAY_OF_MONTH, max);
		
		return cal.getTime();
	}

	/**
	 * Retorna o mês de uma data em seu formado de descrição abreviado
	 * 
	 * @param d
	 * @return
	 */
	public static String getMesAbreviado(Date d) {
		if (d != null) {
			// MMM é o formato para o nome do mês, abreviado.  
			DateFormat df = new SimpleDateFormat("MMM");
			Calendar cal = Calendar.getInstance();
			cal.setTime(d);
			return df.format(cal.getTime()).toUpperCase();
		}
	
		return null;
	}

	/**
	 * Retorna o nome abreviado do mês. Deve-se passar mes entre 1 e 23
	 * @param mes
	 * @return
	 */
	public static String getMesAbreviado(int mes) {
		Calendar cal = Calendar.getInstance();
		mes--;
		cal.set(Calendar.MONTH, mes);
		cal.set(Calendar.DATE, 1);
		return getMesAbreviado(cal.getTime());
	}
	
	/**
	 * Retorna o mês de uma data em seu formado de descrição abreviado
	 * 
	 * @param d
	 * @return
	 */
	public static String getNomeMes(Date d) {
		if (d != null) {
			// MMMMM é o formato para o nome do mês por extenso
			DateFormat df = new SimpleDateFormat("MMMMM");
			Calendar cal = Calendar.getInstance();
			cal.setTime(d);
			return df.format(cal.getTime());
		}
	
		return null;
	}

	/**
	 * Retorna o nome por extenso do mês. Deve-se passar mes entre 1 e 23
	 * @param mes
	 * @return
	 */
	public static String getNomeMes(int mes) {
		Calendar cal = Calendar.getInstance();
		mes--;
		cal.set(Calendar.MONTH, mes);
		// seta o calendário para o primeiro dia do mês
		cal.set(Calendar.DATE, 1);
		return getNomeMes(cal.getTime());
	}

	/**
	 * Retorna o mês atual
	 * 
	 * @return
	 */
	public static int getMesAtual() {
		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		return c.get(Calendar.MONTH);
	}
	
	/** Retorna a ordem do mês atual. Ex.: Janeiro = 1, Maio = 5, Dezembro = 12.
	 * @return
	 */
	public static int getMesAtual1a12() {
		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		// adiciona 1 ao mês pois o Java trata os meses iniciando de 0
		return c.get(Calendar.MONTH) + 1;
	}

	/**
	 * Retorna o mês de uma determinada data. Se a data passada for nula, retorna -1.
	 * @param d
	 * @return
	 */
	public static int getMesByData(Date d) {
		if (d != null) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(d);
			return cal.get(Calendar.MONTH);
		}
		return -1;
	}
	
	/**
	 * Retorna o dia de uma determinada data. Se a data passada for nula, retorna -1.
	 * @param d
	 * @return
	 */
	public static int getDiaByData(Date d) {
		if (d != null) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(d);
			return cal.get(Calendar.DATE);
		}
		return -1;
	}	

	/**
	 * Retorna o dia da semana de uma data. Se a data passada for nula, retorna -1.
	 * 
	 * @param d
	 * @return
	 */
	public static int getDiaSemanaByData(Date d) {
		if (d != null) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(d);
			return cal.get(Calendar.DAY_OF_WEEK);
		}
		return -1;
	}
	
	/**
	 * Verifica se a data atual está dentro do período informado
	 * 
	 * @param inicio
	 * @param fim
	 * @return
	 */
	public static boolean isDentroPeriodo(Date inicio, Date fim) {
		return isDentroPeriodo(inicio, fim, new Date());
	}

	/**
	 * Verifica se a data atual está dentro do período informado
	 * 
	 * @param inicio
	 * @param fim
	 * @return
	 */
	public static boolean isDentroPeriodo(Date inicio, Date fim, Date dataAtual) {
		if (inicio == null || fim == null || dataAtual == null)
			return false;
		dataAtual = CalendarUtils.descartarHoras(dataAtual);
		
		return !(CalendarUtils.compareTo(dataAtual, CalendarUtils.descartarHoras(fim)) > 0 || CalendarUtils.compareTo(dataAtual, CalendarUtils.descartarHoras(inicio))< 0);
	}	
	
	/**
	 * Semelhante a {@link #isDentroPeriodo(Date, Date)}, a diferenÁa que este mÈtodo È mais completo e valida perÌodos em abertos. 
	 * Ex: 
	 * <li> inicio == null => Valida somente o periodo final
	 * <li> fim    == null => Valida somente o periodo inicial
	 * <li> inicio && fim != null => Valida somente o periodo entre as duas datas
	 * 
	 * @param inicio
	 * @param fim
	 * @param dataAtual
	 * @return
	 */
	public static boolean isDentroPeriodoAberto(Date inicio, Date fim) {
		return isDentroPeriodoAberto(inicio, fim, new Date());
	}
	
	/**
	 * Semelhante a {@link #isDentroPeriodo(Date, Date, Date), a diferenÁa que este mÈtodo È mais completo e valida perÌodos em abertos. 
	 * Ex: 
	 * <li> inicio == null => Valida somente o periodo final
	 * <li> fim    == null => Valida somente o periodo inicial
	 * <li> inicio && fim != null => Valida somente o periodo entre as duas datas
	 * 
	 * @param inicio
	 * @param fim
	 * @param dataAtual
	 * @return
	 */
	public static boolean isDentroPeriodoAberto(Date inicio, Date fim, Date dataAtual) {
		
		dataAtual = CalendarUtils.descartarHoras(dataAtual);
		
		if (inicio != null && fim != null)
			return !(CalendarUtils.compareTo(dataAtual, CalendarUtils.descartarHoras(fim)) > 0 || CalendarUtils.compareTo(dataAtual, CalendarUtils.descartarHoras(inicio))< 0);
		else if (inicio != null)
			return !(CalendarUtils.compareTo(dataAtual, CalendarUtils.descartarHoras(inicio))< 0);
		else if (fim != null)
			return !(CalendarUtils.compareTo(dataAtual, CalendarUtils.descartarHoras(fim)) > 0);

		return false;
	}	
	
	/**
	 * Método responsável por verificar se dois períodos informados concomitam entre si.<br />
	 * OBS: Caso alguma das datas seja nula, será considerado que é um intervalo aberto, ou seja,
	 * considerando que:
	 *  - inicio1 = 01/05/2008; termino1 = 31/05/2008; inicio2 = null; termino2 = 05/05/2008;
	 * será considerado que inicio2 é anterior a inicio1, e como termino2 é posterior a inicio1 a data conflitará.
	 * @param inicio1
	 * @param termino1
	 * @param inicio2
	 * @param termino2
	 * @return
	 */
	public static boolean isIntervalosDeDatasConflitantes(Date inicio1, Date termino1, Date inicio2, Date termino2){

		if(inicio2 == null && termino2 == null){
			return true;
		}

		if(inicio1 == null && termino1 == null){
			return true;
		}

		if(inicio1 == null && inicio2 == null)
			return true;

		if(termino1 == null && termino2 == null)
			return true;

		if(inicio1 == null && termino2 == null){
			if(termino1 != null && !termino1.before(inicio2))
				return true;
		}

		if(termino1 == null && inicio2 == null){
			if(inicio1 != null && !inicio1.after(termino2))
				return true;
		}

		if(termino1 == null){
			if(inicio1 != null && !inicio1.after(termino2))
				return true;
		}else if(termino2 == null){
			if(!termino1.before(inicio2))
				return true;
		}else if(inicio1 == null){
			if(!termino1.before(inicio2))
				return true;
		}else if(inicio2 == null){
			if(!termino2.before(inicio1))
				return true;
		}else{
			if(!termino1.after(termino2) && !termino1.before(inicio2))
				return true;
			if(inicio1.before(inicio2) && termino1.after(termino2))
				return true;
			if(!inicio1.after(termino2) && !inicio1.before(inicio2))
				return true;
			if (inicio1.equals(inicio2) && termino1.equals(termino2))
				return true;
		}

		return false;
	}

	/**
	 * Converte uma data em String no formato yyyy-MM-dd para um objeto Date 
	 * 
	 * @param data
	 * @return
	 * @throws ParseException
	 */
	public static Date parseDate(String data) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		return sdf.parse(data);
	}

	/**
	 * Converte uma data em formato String para um objeto Date, de acordo com a máscara informada
	 * 
	 * @param data
	 * @param formato
	 * @return
	 * @throws ParseException
	 */
	public static Date parseDate(String data, String formato) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat(formato);
		return sdf.parse(data);
	}

	/**
	 * Retorna a quantidade de finais de semana entre duas datas.
	 * @param dataInicio
	 * @param dataFim
	 * @return
	 */
	public static int quantidadeFds(Date dataInicio, Date dataFim) {
		int total = 0;

		Calendar dataInicial = Calendar.getInstance();
		dataInicial.setTime(dataInicio);
		
		Calendar dataFinal = Calendar.getInstance();
		dataFinal.setTime(dataFim);
		dataFinal.add(Calendar.DAY_OF_MONTH, 1);

		//contando o número de fds.
		while(!DateUtils.isSameDay(dataInicial, dataFinal)) {
			// se é sábado ou domingo, adiciona um dia à data
			if (isWeekend(dataInicial)) total++;
			dataInicial.add(Calendar.DAY_OF_MONTH, 1);
		}

		return total;
	}

	/**
	 * Adiciona uma quantidade de meses a uma data
	 * 
	 * @param data
	 * @param qtdMeses
	 * @return
	 */
	public static Date somaMeses(Date data, int qtdMeses) {
		Calendar c = Calendar.getInstance();
		c.setTime(data);
		c.add(Calendar.MONTH, qtdMeses);
		return c.getTime();
	}

	/**
	 * Retorna instância de Calendar
	 * 
	 * @param dia
	 * @param mes
	 * @param ano
	 * @param hora
	 * @param min
	 * @return
	 */
	public static Calendar getInstance(int dia, int mes, int ano, int hora, int min) {
		Calendar cal = Calendar.getInstance();
		// subtrai 1 do mês, pois o Java trata os meses iniciando em 0.
		cal.set(ano, mes - 1, dia, hora, min, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal;
	}

	/**
	 * Retorna instância de Calendar
	 * 
	 * @param dia
	 * @param mes
	 * @param ano
	 * @return
	 */
	public static Calendar getInstance(int dia, int mes, int ano) {
		Calendar cal = Calendar.getInstance();
		// subtrai 1 do mês, pois o Java trata os meses iniciando em 0.
		cal.set(ano, mes - 1, dia, 0, 0, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal;
	}

	/**
	 * Retorna uma string formatada de uma data.
	 * 
	 * @param cal
	 * @param formatoSimples
	 * @return
	 */
	public static String format(Calendar cal, boolean formatoSimples) {
		if (formatoSimples) {
			DateFormat df = new SimpleDateFormat();
			return df.format(cal.getTime());
		} else {
			DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy HH:mm");
			return df.format(cal.getTime());
		}
	}

	/**
	 * Formata uma data de acordo com o pattern desejado
	 * 
	 * @param cal
	 * @param formato
	 * @return
	 */
	public static String format(Calendar cal, String formato) {
		DateFormat df = new SimpleDateFormat(formato);
		return df.format(cal.getTime());
	}
	
	/**
	 * Retorna String com data formata
	 * @param data
	 * @param formato
	 * @return
	 */
	public static String format(Date data, String formato) {
		DateFormat df = new SimpleDateFormat(formato);
		return df.format(data);
	}

	/**
	 * Retorna instancia de Calendar
	 * 
	 * @param pattern
	 * @param valor
	 * @return
	 */
	public static Calendar getInstance(String pattern, String valor)  {
		DateFormat df = new SimpleDateFormat(pattern);
		Calendar cal = Calendar.getInstance();
		try {
			cal.setTime(df.parse(valor));
			cal.set(Calendar.MILLISECOND, 0);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return cal;
	}

	/**
	 * Diz se é fim de semana
	 * 
	 * @param cal
	 * @return
	 */
	public static boolean isWeekend(Calendar cal) {
		int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
		return (dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY);
	}
	
	/**
	 * Retorna true se a data passada como parâmetro for um fim de semana.
	 * @param data
	 * @return
	 */
	public static boolean isWeekend(Date data) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(data);
		return isWeekend(cal);
	}	

	/**
	 * Adiciona uma quantidade de dia
	 * @param cal
	 * @param dayWeek
	 */
	public static void setNextDayWeek(Calendar cal, int dayWeek) {
		// verifica a diferença de dias entre as duas semanas
		int dif = dayWeek - cal.get(Calendar.DAY_OF_WEEK);
		// se for positiva, adiciona os dias ao calendário
		if (dif > 0) {
			cal.add(Calendar.DAY_OF_WEEK, dif);
		} else {
			// se for negativa, adiciona uma semana ao calendário (7 dias) 
			cal.add(Calendar.DAY_OF_WEEK, 7 + dif);
		} 
	}

	/**
	 * Adiciona uma quantidade de dia
	 * 
	 * @param c
	 * @param dayWeek
	 * @return
	 */
	public static Calendar getNextDayWeek(Calendar c, int dayWeek) {
		Calendar cal = (Calendar) c.clone();
		// verifica a diferença de dias entre as duas semanas
		int dif = dayWeek - cal.get(Calendar.DAY_OF_WEEK);
		// se for positiva, adiciona os dias ao calendário
		if (dif > 0) {
			cal.add(Calendar.DAY_OF_WEEK, dif);
		} else {
			// se for negativa, adiciona uma semana ao calendário (7 dias) 
			cal.add(Calendar.DAY_OF_WEEK, 7 + dif);
		} 
		return cal;
	}

	/**
	 * 
	 * Compara se a data já passou do prazo, levando em consideração apenas 
	 * o dia/mês/ano.
	 * 
	 * Usado muito na comparação de prazo para devolução de empréstimos.
	 *
	 * @param prazo
	 * @param dataComparacao normalmente a data atual
	 * @return
	 */
	public static boolean estorouPrazo(Date prazo, Date comparacao ){
		Calendar cPrazo = Calendar.getInstance();
		cPrazo.setTime(prazo);
		
		Calendar cComparacao = Calendar.getInstance();
		cComparacao.setTime(comparacao);
		
		// Já está atrasado.
		if (cComparacao.get(Calendar.YEAR) > cPrazo.get(Calendar.YEAR) ) return true;
		
		// Ainda está adiantado.
		if (cComparacao.get(Calendar.YEAR) < cPrazo.get(Calendar.YEAR) ) return false;
		
		// Se for igual, olha o mês e assim sucessivamente.
		if (cComparacao.get(Calendar.MONTH) > cPrazo.get(Calendar.MONTH) ) return true;
		
		if (cComparacao.get(Calendar.MONTH) < cPrazo.get(Calendar.MONTH) ) return false;

		if (cComparacao.get(Calendar.DAY_OF_MONTH) > cPrazo.get(Calendar.DAY_OF_MONTH) ) return true;
		
		if (cComparacao.get(Calendar.DAY_OF_MONTH) < cPrazo.get(Calendar.DAY_OF_MONTH) ) return false;
		
		// Se o dia for igual, ainda está no prazo.
		if (cComparacao.get(Calendar.DAY_OF_MONTH) == cPrazo.get(Calendar.DAY_OF_MONTH) ) return false;

		
		return false; // Nunca era para chegar aqui.
	}

	/**
	 * Indica se a data já passou do prazo, levando em consideração o dia/mês/ano hora:minutos.
	 * 
	 * Para o caso do prazo ser contado em horas, não em dias
	 * 
	 * @param prazo
	 * @param dataComparacao
	 *            normalmente a data atual
	 * @return
	 */
	public static boolean estorouPrazoConsiderandoHoras(Date prazo, Date dataComparacao ){
		
		Calendar cPrazo = Calendar.getInstance();
		cPrazo.setTime(prazo);
		
		Calendar cComparacao = Calendar.getInstance();
		cComparacao.setTime(dataComparacao);
		
		
		// já esta atrasado
		if(cComparacao.get(Calendar.YEAR) > cPrazo.get(Calendar.YEAR) ) return true;
		
		// ainda esta adiantado
		if(cComparacao.get(Calendar.YEAR) < cPrazo.get(Calendar.YEAR) ) return false;
		
		// se for igual olha o mês e assim sucessivamente
		if(cComparacao.get(Calendar.YEAR) == cPrazo.get(Calendar.YEAR) ){
		
			if(cComparacao.get(Calendar.MONTH) > cPrazo.get(Calendar.MONTH) ) return true;
			
			if(cComparacao.get(Calendar.MONTH) < cPrazo.get(Calendar.MONTH) ) return false;
		
			if(cComparacao.get(Calendar.MONTH) == cPrazo.get(Calendar.MONTH) ){
				
				if(cComparacao.get(Calendar.DAY_OF_MONTH) > cPrazo.get(Calendar.DAY_OF_MONTH) ) return true;
				
				if(cComparacao.get(Calendar.DAY_OF_MONTH) < cPrazo.get(Calendar.DAY_OF_MONTH) ) return false;
				
				if(cComparacao.get(Calendar.DAY_OF_MONTH) == cPrazo.get(Calendar.DAY_OF_MONTH) ) {
					
					if(cComparacao.get(Calendar.HOUR_OF_DAY) > cPrazo.get(Calendar.HOUR_OF_DAY)) return true;
					
					if(cComparacao.get(Calendar.HOUR_OF_DAY) < cPrazo.get(Calendar.HOUR_OF_DAY)) return false;
					
					if(cComparacao.get(Calendar.HOUR_OF_DAY) == cPrazo.get(Calendar.HOUR_OF_DAY)){
						
						if(cComparacao.get(Calendar.MINUTE) > cPrazo.get(Calendar.MINUTE)) return true;
						
						if(cComparacao.get(Calendar.MINUTE) < cPrazo.get(Calendar.MINUTE)) return false;
						
						// se for igual ainda está no prazo
						if(cComparacao.get(Calendar.MINUTE) == cPrazo.get(Calendar.MINUTE)) return false;
						
					}
					
				}
				
			}
			
		}
		
		return false; // nunca era para chegar aqui
		
	}

	/**
	 * Método que calcula a quantidade de horas existentes entre duas datas
	 *
	 * @param dataInicio
	 * @param dataFim
	 * @return
	 */
	public static int calculaQuantidadeHorasEntreDatas(Date dataInicio, Date dataFim){
	
		if(dataFim == null)
			dataFim = new Date();
	
		long horas;
	
		if(dataFim.getTime() > dataInicio.getTime()){
			// diferença entre as duas datas em milisegundos.
			horas= dataFim.getTime() - dataInicio.getTime();
			// dividindo por:
			horas/=1000; // obtemos os segundos entre as datas
			horas/=60; // obtemos os minutos entre as datas
			horas/=60; // obtemos os horas entre as datas
			
		}else{
			horas = 0;
		}
	
		return (int) horas;
	}

	/**
	 * No intervalo eu considero o primeiro dia. <br>
	 * Ex 1: entre 18 e 18 = 1 dia; <br>
	 * Ex 2: entre 17 e 20 = (18,19,20) 3 dias, mas eu considero que o dia 17 é um dia útil, então fica (17,18,19,20) 4 dias.
	 * 
	 * @param dataInicio
	 * @param dataFim
	 * @return
	 * @deprecated Usar o método calculaQuantidadeDiasEntreDatasIntervaloFechado()
	 */
	@Deprecated
	public static int calculaQuantidadeDiasPeriodo(Date dataInicio, Date dataFim){
		return calculoDias(dataInicio, dataFim) + 1;
	}

	/**
	 * Retorna a quantidade de dias entre duas datas, arredondando o número de horas entre as datas para cima.<br/>
	 * Ex.: se a data inicial for 01/01/2001 15:00 e a final for 02/01/2001 09:00, retornará um dia.
	 * 
	 * @param dataInicio
	 * @param dataFim
	 * @return
	 */
	public static int calculaQuantidadeDiasEntreDatasIntervaloFechado(Date dataInicio, Date dataFim){
		return calculoDias(dataInicio, dataFim) + 1;
	}

	/** Indica se o ano é bissexto.
	 * @param ano
	 * @return
	 */
	public static boolean isAnoBissexto(int ano) {
		// se o ano for divisível por 4, não for divisível por 100 e for divisível por 400, é bissexto
		return ano%4==0 && ( ano%400 == 0 || ano%100 != 0);
	}

	/**
	 * Retorna a quantidade de semanas entre duas datas.
	 * 
	 * @param dataInicio
	 * @param dataFim
	 * @return
	 */
	public static int calculaQuantidadeSemanasEntreDatasIntervaloFechado(Date dataInicio, Date dataFim){
		DateTime inicio = new DateTime(dataInicio.getTime());
		DateTime fim = new DateTime(dataFim.getTime());
		
		return Weeks.weeksBetween(inicio, fim).getWeeks();
	}

	/**
	 * Retorna a quantidade de meses, em intervalo fechado, entre duas datas.<br/>
	 * Exempplo: entre as datas 10/01/2008 e 01/12/2009 existem 24 meses.
	 * 
	 * @param dataInicio
	 * @param dataFim
	 * @return
	 */
	public static int calculaQuantidadeMesesEntreDatasIntervaloFechado(Date dataInicio, Date dataFim){
		return calculoMeses(dataInicio, dataFim) + 1;
	}

	/**
	 * Retorna a quantidade de anos entre duas data com intervalo fechado.
	 *
	 * @param dataInicio
	 * @param dataFim
	 * @return
	 */
	public static int calculaQuantidadeAnosEntreDatasIntervaloFechado(Date dataInicio, Date dataFim){
		return calculoAnos(dataInicio, dataFim) + 1;
	}

	/**
	 * Adiciona os dias do fim semana à um calendário.<br/>
	 * Método que SEMPRE deve ser chamado quando o prazo de um empréstimo
	 * precisar ser criado ou atualizado para verificar se o dia do prazo cai
	 * num sábado ou domingo. Nete caso, ele seta o prazo para a segunda-feira
	 * próxima, pois o usuário não pode devolver nada nos finais de semana.
	 * 
	 * @param calendar
	 */
	public static void adicionaDiasFimDeSemana(Calendar calendar){
		// se o dia de semana é sábado, adiciona dois dias
		if(calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY){
			calendar.add(Calendar.DAY_OF_WEEK, 2);
		}else{
			// se o dia de semana é domingo, adiciona um dia.
			if(calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY){
				calendar.add(Calendar.DAY_OF_WEEK, 1);
			}
		}
	}

	/**
	 * Diminui a data passada em um ano.
	 *
	 * @param data
	 * @return
	 */
	public static Date diminuiDataEmUmAno(Date data){
		Calendar c = Calendar.getInstance();
		c.setTime(data);
		// seta o calendário para o ano anterior
		c.roll(Calendar.YEAR, false);
		return c.getTime();
	}
	
	/**
	 * Retorna a data do primeiro dia do mês.
	 * @param mes
	 * @return
	 */
	public static Date primeiroDataMes(int mes){
		
		Calendar c = Calendar.getInstance();
		
		c.set(Calendar.MONTH, mes);
		// seta o dia do calendário para o primeiro dia do mês
		c.set(Calendar.DAY_OF_MONTH, 1);
		
		return c.getTime();
	}
	
	/**
	 * Retorna a data do último dia do mês.
	 * @param mes
	 * @return
	 */
	public static Date ultimaDataMes(int mes){
		
		Calendar c = Calendar.getInstance();
		// calcula o mês seguinte ao mês passado no parâmetro
		if(mes == Calendar.DECEMBER){
			c.set(Calendar.MONTH, 0);
			c.set(Calendar.YEAR, c.get(Calendar.YEAR) + 1);
		}else{
			c.set(Calendar.MONTH, mes+1);
		}
		// seta a data do mês seguinte para o dia primeiro
		c.set(Calendar.DAY_OF_MONTH, 1);
		// subtrai um dia do calendário. Assim, temos o último dia do mês
		c.add(Calendar.DAY_OF_MONTH,-1);
		
		return c.getTime();
	}
	
	/**
	 * Cria uma data a partir do dia, mês e ano passados como parâmetro.
	 * @param dia
	 * @param mes
	 * @param ano
	 * @return
	 */
	public static Date createDate(int dia, int mes, int ano) {
		Calendar c = Calendar.getInstance();
		c.set(Calendar.YEAR, ano);
		c.set(Calendar.MONTH, mes);
		c.set(Calendar.DAY_OF_MONTH, dia);
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		return c.getTime();
	}

	/**
	 * Deixa um objeto Date apenas com os campos de dia, mês e ano.
	 * @param data
	 * @return
	 */
	public static Date descartarHoras(Date data) {
		Calendar c = Calendar.getInstance();
		c.setTime(data);
		c = descartarHoras(c);
		return c.getTime();
	}
	
	/**
	 * Deixa um objeto Date apenas com os campos de dia, mês e ano.
	 * @param data
	 * @return
	 */
	public static Calendar descartarHoras(Calendar c) {
		c.set(Calendar.AM_PM, Calendar.AM);
		c.set(Calendar.HOUR, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);

		return c;
	}
	
	/**
	 * Deixa um objeto Date com hora, minutos e segundos ao fim do dia.
	 * @param data
	 * @return
	 */
	public static Date incluirHoraFimDia(Date data) {
		Calendar c = Calendar.getInstance();
		c.setTime(data);
		// seta a hora do calendário para 23:59:59 (última hora do dia)
		c.set(Calendar.AM_PM, Calendar.AM);
		c.set(Calendar.HOUR, 23);
		c.set(Calendar.MINUTE, 59);
		c.set(Calendar.SECOND, 59);

		return c.getTime();
	}

	/**
	 * Retorna uma string com o dia da semana correspondente à data
	 * passada como parâmetro. A string pode estar em formato completo
	 * ou abreviado, dependendo do parâmetro abreviado.
	 * 
	 * @param data
	 * @param abreviado
	 * @return
	 */
	public static String diaSemana(Date data, boolean abreviado) {
		return new SimpleDateFormat(abreviado ? "EEE" : "EEEEE").format(data);
	}

	/**
	 * Adiciona uma quantidade de meses à data informada e retorna a nova data 
	 * @param data
	 * @param qtdMeses
	 * @return
	 */
	public static Date adicionaMeses(Date data, Integer qtdMeses) {
		Calendar c = Calendar.getInstance();
		c.setTime(data);
		c.add(Calendar.MONTH, qtdMeses);
		return c.getTime();
	}
	
	/**
	 * Subtrai uma quantidade de meses à data informada e retorna a nova data 
	 * @param data
	 * @param qtdMeses
	 * @return
	 */
	public static Date subtrairMeses(Date data, Integer qtdMeses) {
		Calendar c = Calendar.getInstance();
		c.setTime(data);
		c.add(Calendar.MONTH, -qtdMeses);
		return c.getTime();
	}

	/**
	 * Identifica se duas datas correspondem ao mesmo dia do ano.
	 * @param data1
	 * @param data2
	 * @return
	 */
	public static boolean isSameDay(Date data1, Date data2) {
		
		if (data1 == null && data2 == null) {
			return true;
		} else if ((data1 == null && data2 != null) || (data1 != null && data2 == null)) {
			return false;
		} else {
			return DateUtils.isSameDay(data1, data2);
		}
		
	}

	/**
	 * Subtrai uma determinada quantidade de dias de uma data.
	 * @param date
	 * @param i
	 * @return
	 */
	public static Date subtraiDias(Date data, int dias) {
		Calendar c = Calendar.getInstance();
		c.setTime(data);
		c.add(Calendar.DAY_OF_MONTH, -dias);
		return c.getTime();
	}

	/**
	 *Adiciona a uma determinada data a quantidade de anos informada.
	 * 
	 * @param data
	 * @param anos
	 * @return
	 */
	public static Date adicionarAnos (Date data, int anos) {
		Calendar c = Calendar.getInstance();
		c.setTime(data);
		c.add(Calendar.YEAR, anos);
		return c.getTime();		
	}

	/**
	 * Remove de uma determinada data a quantidade de anos informada.
	 * @param data
	 * @param anos
	 * @return
	 */
	public static Date subtrairAnos (Date data, int anos) {
		Calendar c = Calendar.getInstance();
		c.setTime(data);
		c.add(Calendar.YEAR, -anos);
		return c.getTime();	
	}
	
	/**
	 * Retorna a hora atual do sistema.
	 * @return
	 */
	public static Integer getHoraAtual() {
		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		return c.get(Calendar.HOUR_OF_DAY);
	}
	
	/**
	 * Verifica se os meses das duas datas informadas são consecutivos (ex: data = janeiro/2010, outraData = fevereiro/2010 e vice-versa.
	 * 
	 * @param data
	 * @param outraData
	 * @return
	 */
	public static boolean isMesesConsecutivos(Date data, Date outraData){
		Calendar calData = Calendar.getInstance();
		calData.setTime(CalendarUtils.descartarHoras(data));
		Calendar calOutraData = Calendar.getInstance();
		calOutraData.setTime(CalendarUtils.descartarHoras(outraData));
		// seta os dias nos calendários para o primeiro dia do mês
		calData.set(Calendar.DAY_OF_MONTH, 1);
		calOutraData.set(Calendar.DAY_OF_MONTH, 1);
		// retorna um mês no calendário
		calData.add(Calendar.MONTH, -1);
		// se as datas são iguais, retorna true
		if(calData.equals(calOutraData))
			return true;
		// avança um mês no calendário 
		calData.add(Calendar.MONTH, 1);
		// retorna um mês no outro calendário
		calOutraData.add(Calendar.MONTH, -1);
		// se as datas são iguais, retorna true
		if(calData.equals(calOutraData))
			return true;
		return false;
	}
	
	/**
	 * Converte um número de 2 dígitos representando um ano para sua versão
	 * completa de 4 dígitos.
	 *
	 * @param ano
	 * @return
	 */
	public static int completaAno(int ano) {
		// se for um ano menor que 100. Exemplos: 95; 5
		if (ano < 100) {
			Calendar c = Calendar.getInstance();
			String anoAtual = String.valueOf(c.get(Calendar.YEAR));
			// adiciona o milênio e o centenário atual ao ano. Exemplos: 1900 + 95; 2000 + 95; 2100 + 05
			// se o ano passado por parâmetro for menor que 10, adicionar um zero à frente. Exemplo: 2000 + "0" + 5
			return Integer.parseInt(anoAtual.substring(0, 2) + (ano >= 10 ? ano : ("0" + ano)));
		}

		throw new IllegalArgumentException(
				"Só é possível completar o valor de anos menores que 100.");
	}
	
	/**
	 * Retorna a quantidade de dias úteis entre duas datas, tirando os fins
	 * de semana e feriados.
	 * @return
	 */
	public static int getDiasUteisEntreDatas(Date inicio, Date fim) {
		int total = calculaQuantidadeDiasPeriodo(inicio, fim);
		int fds = quantidadeFds(inicio, fim);
		int qtdFeriados = 0;

		List<Date> feriados = getDatasFeriados(inicio, fim, true, null, null);
		if (!isEmpty(feriados)) {
			for (Date f : feriados) {
				if (!isWeekend(f))
					qtdFeriados++;
			}
		}
		
		return total - fds - qtdFeriados;
	}
	
	/**
	 * Retorna a quantidade de dias úteis entre duas datas, tirando os fins
	 * de semana e feriados considerando cidade e estados..
	 * @return
	 */
	public static int getDiasUteisEntreDatas(Date inicio, Date fim,Integer cidade, Integer estado) {
		int total = calculaQuantidadeDiasEntreDatasIntervaloFechado(inicio, fim);
		int fds = quantidadeFds(inicio, fim);
		int qtdFeriados = 0;

		List<Date> feriados = getDatasFeriados(inicio, fim, false, cidade, estado);
		if (!isEmpty(feriados)) {
			for (Date f : feriados) {
				if (!isWeekend(f))
					qtdFeriados++;
			}
		}
		
		return total - fds - qtdFeriados;
	}
	
	/**
	 * Adiciona uma determinada quantidade de dias úteis (sem considerar
	 * fins de semana e feriados) a uma data.
	 * 
	 * @param data
	 * @param qtdDiasUteis
	 * @return
	 */
	public static Date adicionarDiasUteis(Date data, int qtdDiasUteis) {
		int diasAdicionados = 0;
		Date dataAtual = data;
		
		while (diasAdicionados < qtdDiasUteis) {
			dataAtual = adicionaDias(dataAtual, 1);
			if (!isWeekend(dataAtual) && !isFeriado(dataAtual))
				diasAdicionados++;
		}
		
		return dataAtual;
	}
	
	/**
	 * Retorna true se uma data for feriado e false caso contrário.
	 * @param data
	 * @return
	 */
	public static boolean isFeriado(Date data) {
		Collection<Date> feriados = getDatasFeriados(data, data, true, null, null);
		return !isEmpty(feriados);
	}

	/**
	 * Subtrai uma determinada quantidade de dias úteis (sem considerar
	 * fins de semana e feriados) a uma data.
	 * 
	 * @param data
	 * @param qtdDiasUteis
	 * @return
	 */
	public static Date subtrairDiasUteis(Date data, int qtdDiasUteis) {
		int diasSubtraidos = 0;
		Date dataAtual = data;
		
		while (diasSubtraidos < qtdDiasUteis) {
			// subtrai um dia da data atual
			dataAtual = subtraiDias(dataAtual, 1);
			if (!isWeekend(dataAtual) && !isFeriado(dataAtual))
				diasSubtraidos++;
		}
		
		return dataAtual;
	}

	/**
	 * Define o horário (horas e minutos) de uma data a partir do horário de uma segunda data
	 * 
	 * @param data
	 * @param horario
	 * @return
	 */
	public static Date definirHorario(Date data, Date horario) {
		Calendar calendarData = Calendar.getInstance();
		calendarData.setTime(data);
		
		Calendar calendarHorario = Calendar.getInstance();
		calendarHorario.setTime(horario);
		
		calendarData.set(Calendar.HOUR_OF_DAY, calendarHorario.get(Calendar.HOUR_OF_DAY));
		calendarData.set(Calendar.MINUTE, calendarHorario.get(Calendar.MINUTE));
		calendarData.set(Calendar.SECOND, calendarHorario.get(Calendar.SECOND));
		
		return calendarData.getTime();
	}
	
	/**
	 * Verifica se o valor passado como parâmetro é uma data válida seguindo os padrões:
	 * <ul>
	 * <li>dd/MM/yy</li>
	 * <li>dd/MM/yyyy</li>
	 * </ul>
	 * @param data
	 * @return
	 */
	public static boolean isFormatoDataValida(String data){
		Pattern padraoData = Pattern.compile("^(0[1-9]|[12][0-9]|3[01])/(0[1-9]|1[012])/(\\d{2}?|\\d{4}?)$");
		
		Matcher matcher = padraoData.matcher(data);
		if(matcher.find()){
			return true;
		}
		return false;
	}
	
	/**
	 * Retorna um Set contendo todos os dias úteis (contando os Sábados) entre as datas passadas, inclusive.
	 * 
	 * @param dataInicio
	 * @param dataFim
	 * @return
	 * @throws DAOException
	 */
	public static Set<Date> getDiasUteis(Date dataInicio, Date dataFim) throws DAOException {
		Set<Date> datas = new TreeSet<Date>();
		Date dataAuxiliar = dataInicio;
		while(!dataAuxiliar.after(dataFim)) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(dataAuxiliar);
			if(cal.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY)
				datas.add(dataAuxiliar);
					
			dataAuxiliar = adicionaUmDia(dataAuxiliar);
		}
		
		List<Date> feriados = getDatasFeriados(dataInicio, dataFim, true, null, null);
		if(!isEmpty(feriados)) {
			for (Date feriado : feriados) {
				datas.remove(feriado);
			}
		}
		
		return datas;
	}
	
	/**
	 * Método que retorna um String com a duração do intervalo entre as datas inicio e fim, no formato: <[99]d> <[99]h> <[99]min>  
	 * @param inicio
	 * @param fim
	 * @return
	 */
	public static String getQtdDiasHorasMinutosIntervalo(Date inicio, Date fim){
		
		if(inicio.after(fim)){
			return "Inválido";
		}
		
		DateTime dtInicio = new DateTime(inicio);
		DateTime dtFim = new DateTime(fim);
		Interval interval = new Interval(dtInicio,dtFim);
		
		PeriodFormatter diasHorasMinutos = new PeriodFormatterBuilder()
		.appendYears()
		.appendSuffix("a ")
		.appendMonths()
		.appendSuffix("m ")
		.appendWeeks()
		.appendSuffix("s ")
	    .appendDays()
	    .appendSuffix("d ")
	    .appendHours()
	    .appendSuffix("h ")
	    .appendMinutes()
	    .appendSuffix("min")
	    .toFormatter();

		Period periodo = interval.toPeriod();
		
		
		
		return diasHorasMinutos.print(periodo);
		
	}
	
	/**
	 * Método que calcula a diferença de minutos entre datas
	 * @param dataInicio
	 * @param dataFim
	 * @return
	 */
	public static int calculaMinutos(Date dataInicio, Date dataFim){
		  DateTime inicio = new DateTime(dataInicio.getTime());
		  DateTime fim = new DateTime(dataFim.getTime());
		  
		  return Minutes.minutesBetween(inicio, fim).getMinutes();
	 }
	
	/**
	 * Busca datas de feriados para cálculos de dias úteis.
	 * @param dataInicio
	 * @param dataFim
	 * @param todosFeriados
	 * @param municipio
	 * @param estado
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private static List<Date> getDatasFeriados(Date dataInicio, Date dataFim, boolean todosFeriados, Integer municipio, Integer estado) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		String sql = "SELECT f.data_feriado FROM comum.feriado f " 
				+ "LEFT JOIN comum.municipio m using (id_municipio) "
				+ "LEFT JOIN comum.unidade_federativa e on (e.id_unidade_federativa = f.id_unidade_federativa) "
				+ "WHERE f.data_feriado >= '" + sdf.format(dataInicio) + "' and f.data_feriado <= '" + sdf.format(dataFim) + "'";
		
		
		if (!todosFeriados) {
			sql += "AND ( ((m.id_municipio IS NULL AND e.id_unidade_federativa IS NULL)) ";
				
			if(municipio != null)
				sql += "OR (m.id_municipio = " + municipio + ") ";
				
			if(estado != null)
				sql += "OR (e.id_unidade_federativa = " + estado + ") ";
				
			sql += ")";
		}
		
		JdbcTemplate jt = new JdbcTemplate(Database.getInstance().getComumDs());
		
		return jt.queryForList(sql, Date.class);
	}
	
	
	/** Retorna a quantidade de dias existentes entre duas datas arredondando para a quantidade mais próxima da diferença entre os períodos.
	 * @param dataInicio
	 * @param dataFim
	 * @return
	 */
	public static int calculoDiasArredondado(Date dataInicio, Date dataFim){
		Date a = ( new Date( dataFim.getTime() - dataInicio.getTime() ) );
		double diferencaDiasMilisegundos = a.getTime(); 
		return (int) Math.round((diferencaDiasMilisegundos / (HORAS * MINUTOS * SEGUNDOS * MILESIMOS))); 
	}

	
	/**
	 * Retorna a quantidade de dias úteis entre duas datas, tirando os fins de semana e feriados considerando cidade e estados.
	 * A soma das horas dos dias parametrizados será arredondado para a quantidade mais próxima. 
	 * <br /><br />
	 * Exemplo: 01/01/2000 01:00 à 03/01/2000 00:00 = 47 horas, será arredondada para 2 dia.
	 * @return
	 */
	public static int getDiasUteisEntreDatasArrendondado(Date inicio, Date fim,Integer cidade, Integer estado) {
		int total = calculoDiasArredondado(inicio, fim);
		int fds = quantidadeFds(inicio, fim);
		int qtdFeriados = 0;
		List<Date> feriados = getDatasFeriados(inicio, fim, false, cidade, estado);
		if (!isEmpty(feriados)) {
			for (Date f : feriados) {
				if (!isWeekend(f))
					qtdFeriados++;
			}
		}
		return total - fds - qtdFeriados;
	}
	
}
