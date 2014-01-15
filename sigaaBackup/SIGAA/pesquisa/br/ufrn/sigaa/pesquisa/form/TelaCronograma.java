/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 27/09/2006
 *
 */

package br.ufrn.sigaa.pesquisa.form;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;

import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.projetos.dominio.CronogramaProjeto;
import br.ufrn.sigaa.projetos.dominio.IntervaloCronograma;

/**
 * Classe utilizada para a construção e analise da visualização de um cronograma
 *
 * @author ricardo
 *
 */
public class TelaCronograma {

	/** Período do cronograma. */
	/** Início do cronograma. */
	private Date dataInicio;	
	/** Fim do cronograma. */
	private Date dataFim;	
	/** Total de meses do cronograma. */
	private int numeroMeses;

	
	/** Arrays com as informações do formulário */
	/** Lista de atividades a serem realizadas no cronograma. */
	private String[] atividade = {};
	/** Lista de meses que o cronograma abrange. */
	private String[] calendario = {};

	
	/** String com todos os meses/ano concatenados para tratamento no formulário */
	private String mesesString;

	/** Lista com os objetos CronogramaPesquisa associados com o formulário */
	private List<CronogramaProjeto> cronogramas = new ArrayList<CronogramaProjeto>();

	/** Mapa com os meses utilizados a serem associados com cada ano do cronograma */
	private Map<Integer, ArrayList<String>> mesesAno = new TreeMap<Integer, ArrayList<String> >();

	/** Lista de abreviações de todos os meses do ano */
	private static ArrayList<String> mesesAbbr = new ArrayList<String>();
	static {
		mesesAbbr.add("Jan"); mesesAbbr.add("Fev");
		mesesAbbr.add("Mar"); mesesAbbr.add("Abr");
		mesesAbbr.add("Mai"); mesesAbbr.add("Jun");
		mesesAbbr.add("Jul"); mesesAbbr.add("Ago");
		mesesAbbr.add("Set"); mesesAbbr.add("Out");
		mesesAbbr.add("Nov"); mesesAbbr.add("Dez");
	}

	/** Construtor padrão. */
	public TelaCronograma() {

	}

	public TelaCronograma(Date dataInicio, Date dataFim, List<CronogramaProjeto> cronogramas) {
		this.dataInicio = dataInicio;
		this.dataFim = dataFim;
		this.cronogramas = cronogramas;
		inicializarCronograma();
	}

	/** Inicializar o cronograma a partir do período e da lista do cronograma. */
	public void inicializarCronograma() {
		inicializarCabecalho();
		popularCronograma(cronogramas);
	}

	/** Montar cabeçalho da tabela onde o cronograma será informado. */
	private void inicializarCabecalho() {
		Calendar c = Calendar.getInstance();

		if (dataInicio != null && dataFim != null) {
			// Extrair mês e ano iniciais
			c.setTime(dataInicio);
			int mesInicio = c.get(Calendar.MONTH);
			int anoInicio = c.get(Calendar.YEAR);

			// Extrair mês e ano finais
			c.setTime(dataFim);
			int mesFim = c.get(Calendar.MONTH);
			int anoFim = c.get(Calendar.YEAR);

			// Montar cabeçalho da tabela
			definirCalendario(mesInicio, anoInicio, mesFim, anoFim, -1);
		}
	}

	/** Define faixa de meses do cronograma. */
	private ArrayList<String> definirCalendario(int mesInicio, int anoInicio, int mesFim, int anoFim, int indice) {
		// Montar String como todos os meses/ano, no caso do cabeçalho
		StringBuilder mesesStringBuilder = new StringBuilder();
		ArrayList<String> listaCalendario = new ArrayList<String>();

		// Criar os meses/ano
		for (int ano = anoInicio; ano <= anoFim; ano++ ) {

			ArrayList<String> meses = new ArrayList<String>();

			int comecoPeriodo = mesInicio;
			int fimPeriodo = mesFim;

			// Definir faixa de meses dependendo do ano
			if (anoInicio != anoFim) {
				if (ano == anoInicio){
					fimPeriodo = 11; // Até dezembro
				} else {
					comecoPeriodo = 0; //  A partir de janeiro
					if (ano != anoFim){
						fimPeriodo = 11; // Janeiro a dezembro
					}
				}
			}

			// Popular meses
			for(int mes = comecoPeriodo; mes <= fimPeriodo; mes++) {
				if (indice < 0) {
					numeroMeses++;
					meses.add(mesesAbbr.get(mes));
					mesesStringBuilder.append(mesesAbbr.get(mes) + "_" + ano + ",");
				} else {
					listaCalendario.add(indice + "_" + mesesAbbr.get(mes) + "_" + ano);
				}
			}

			if (indice < 0)
				mesesAno.put(ano, meses);
		}

		if (indice < 0)
			mesesString = mesesStringBuilder.toString();

		return listaCalendario;
	}

	/**
	 * Popula o formulário a partir da lista do cronograma informada
	 * @param cronogramas
	 */
	private void popularCronograma(List<CronogramaProjeto> cronogramas) {
		setCronogramas(cronogramas);

		ArrayList<String> listaAtividades = new ArrayList<String>();
		ArrayList<String> listaCalendario = new ArrayList<String>();

		int indice = 0;
		// Percorrer cronogramas
		for(CronogramaProjeto cronograma : getCronogramas()) {

			// Adicionar a descrição da atividade
			listaAtividades.add(cronograma.getDescricao());

			// Percorrer os intervalos e montar os valores para os checkboxes/células do cronograma
			for(IntervaloCronograma intervalo : cronograma.getIntervalos()) {
				listaCalendario.addAll(
						definirCalendario(
								intervalo.getMesInicio(), intervalo.getAnoInicio(),
								intervalo.getMesFim(), intervalo.getAnoFim(),
								indice)
					);
			}
			indice++;
		}

		atividade = listaAtividades.toArray(atividade);
		calendario = listaCalendario.toArray(calendario);
	}

	/**
	 * Retornar o número da String informada.
	 * 
	 * @param camposItem
	 * @return
	 * @throws Exception 
	 */
	private Integer clearFieldNotNumeric(String camposItem) throws Exception{
		if (camposItem != null) {
			camposItem = camposItem.replace(',', ' ');
			try {	
				return Integer.parseInt( camposItem.trim() );
			} catch (Exception e) {
				throw new Exception("Nenhum Número Inteiro foi encontrado."); 
			}
		}
		return null;
	}
	
	/**
	 * Popular lista do cronograma a partir dos dados do formulário
	 * @param req
	 * @throws Exception 
	 * @throws NumberFormatException 
	 */
	public void definirCronograma(HttpServletRequest req) throws NumberFormatException, Exception {
		cronogramas = new ArrayList<CronogramaProjeto>();
		TreeMap<Integer, ArrayList<Date>> itensCalendario = new TreeMap<Integer, ArrayList<Date>>();

		Calendar c = Calendar.getInstance();
		// Percorrer os valores enviados dos checkboxes e extrair os meses informados
		Map<String, String> indices = new HashMap<String, String>();
		int ultimoIndice = -1;

		if (calendario != null) {
			for (int i = 0; i < calendario.length; i++) {
				String[] camposItem = calendario[i].split("_");
	
				if ( !itensCalendario.containsKey( clearFieldNotNumeric(camposItem[0]) )) {
					itensCalendario.put( clearFieldNotNumeric(camposItem[0]), new ArrayList<Date>());
				}
	
				ArrayList<Date> listaPeriodos = itensCalendario.get( clearFieldNotNumeric(camposItem[0]) );
	
				c.clear();
				c.set(Calendar.MONTH, mesesAbbr.indexOf(camposItem[1]));
				c.set(Calendar.YEAR, Integer.valueOf(camposItem[2]));
				listaPeriodos.add(c.getTime());
			}
		}

		Integer[] arrayItens = {};
		arrayItens = itensCalendario.keySet().toArray(arrayItens);

		// Criar os cronogramas com seus intervalos
		if (atividade != null) {
			for (int i = 0; i < atividade.length; i++) {
				atividade[i] = atividade[i].trim().toUpperCase();
	
					CronogramaProjeto cronograma = new CronogramaProjeto();
					cronograma.setDescricao(atividade[i]);
	
					Calendar c2 = Calendar.getInstance();
	
					ArrayList<Date> listaPeriodos = null;
					if (i < arrayItens.length) {
						listaPeriodos = itensCalendario.get(arrayItens[i]);
					}
	
					// Se foi informado o cronograma para a atividade corrente, definir os intervalos
					if (listaPeriodos != null && !listaPeriodos.isEmpty()) {
						c.setTime(listaPeriodos.get(0));
						c2.setTime(listaPeriodos.get(listaPeriodos.size()-1));
	
						// Criar novo intervalo
						IntervaloCronograma intervalo = new IntervaloCronograma();
						intervalo.setCronograma(cronograma);
						intervalo.setMesInicio(c.get(Calendar.MONTH));
						intervalo.setAnoInicio(c.get(Calendar.YEAR));
	
						//	Inicializar variáveis auxiliares
						int mesAnterior = c.get(Calendar.MONTH);
						int anoAnterior = c.get(Calendar.YEAR);
						int mesAtual = c2.get(Calendar.MONTH);
						int anoAtual = c2.get(Calendar.YEAR);
	
						// Número de meses marcados
						int numMeses = ((anoAtual - anoAnterior) * 12) + mesAtual - mesAnterior + 1;
	
						// Se não for um intervalo contínuo
						if (numMeses != listaPeriodos.size()) {
							// Percorrer os meses para determinar os intervalos contínuos
							for (int j = 1; j < listaPeriodos.size(); j++) {
								c.setTime(listaPeriodos.get(j));
								mesAtual = c.get(Calendar.MONTH);
								anoAtual = c.get(Calendar.YEAR);
	
								// Não são meses seguidos
								if ( (((anoAtual - anoAnterior) * 12) + mesAtual - mesAnterior) != 1  ) {
									// Definir o fim do intervalo corrente
									intervalo.setMesFim(mesAnterior);
									intervalo.setAnoFim(anoAnterior);
									cronograma.getIntervalos().add(intervalo);
	
									// Iniciar um novo intervalo com o mês atual
									intervalo = new IntervaloCronograma();
									intervalo.setCronograma(cronograma);
									intervalo.setMesInicio(mesAtual);
									intervalo.setAnoInicio(anoAtual);
								}
	
								// Armazenar o mês e anos correntes
								mesAnterior = mesAtual;
								anoAnterior = anoAtual;
	
							}
						}
	
						// Concluir último intervalo
						intervalo.setMesFim(mesAtual);
						intervalo.setAnoFim(anoAtual);
						cronograma.getIntervalos().add(intervalo);
					}
	
					// Adicionar item de cronograma, definindo sua ordem
					if (!cronogramas.contains(cronograma)) {
						cronogramas.add(cronograma);
						cronograma.setOrdem(cronogramas.size());
					} else {
						int indice = cronogramas.indexOf(cronograma);
						cronograma.setOrdem(indice + 1);
						cronogramas.set(indice, cronograma);
					}
				}
		}
	}

	public Date getDataFim() {
		return dataFim;
	}

	public void setDataFim(Date dataFim) {
		this.dataFim = dataFim;
	}

	public Date getDataInicio() {
		return dataInicio;
	}

	public void setDataInicio(Date dataInicio) {
		this.dataInicio = dataInicio;
	}

	public Map<Integer, ArrayList<String>> getMesesAno() {
		return mesesAno;
	}

	public void setMesesAno(Map<Integer, ArrayList<String>> mesesAno) {
		this.mesesAno = mesesAno;
	}

	public int getNumeroMeses() {
		return numeroMeses;
	}

	public void setNumeroMeses(int numeroMeses) {
		this.numeroMeses = numeroMeses;
	}

	public String[] getAtividade() {
		return atividade;
	}

	public void setAtividade(String[] atividade) {
		this.atividade = atividade;
	}

	public String[] getCalendario() {
		return calendario;
	}

	public void setCalendario(String[] calendario) {
		this.calendario = calendario;
	}

	public String getMesesString() {
		return mesesString;
	}

	public void setMesesString(String mesesString) {
		this.mesesString = mesesString;
	}

	public List<CronogramaProjeto> getCronogramas() {
		return cronogramas;
	}

	public void setCronogramas(List<CronogramaProjeto> cronogramas) {
		this.cronogramas = cronogramas;
	}

}
