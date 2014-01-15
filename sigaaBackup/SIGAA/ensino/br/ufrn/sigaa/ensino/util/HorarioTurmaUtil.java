/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Created on 14/03/2007
 *
 */
package br.ufrn.sigaa.ensino.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.dao.DAOFactory;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.RuntimeNegocioException;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.Formatador;
import br.ufrn.sigaa.arq.dao.DiscenteDao;
import br.ufrn.sigaa.arq.dao.ensino.HorarioDao;
import br.ufrn.sigaa.arq.dao.ensino.TurmaDao;
import br.ufrn.sigaa.arq.dao.graduacao.SolicitacaoMatriculaDao;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.dominio.DocenteTurma;
import br.ufrn.sigaa.ensino.dominio.GrupoHorarios;
import br.ufrn.sigaa.ensino.dominio.Horario;
import br.ufrn.sigaa.ensino.dominio.HorarioDocente;
import br.ufrn.sigaa.ensino.dominio.HorarioTurma;
import br.ufrn.sigaa.ensino.dominio.PeriodoHorario;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.dominio.Turno;
import br.ufrn.sigaa.ensino.negocio.TurmaValidator;

/**
 * Classe utilizada para ajudar na formatação e conversão de objetos
 * HorarioTurma no código da UFRN (DD-TURNO-AULAS).
 * @author André
 * @author Victor Hugo
 */
public class HorarioTurmaUtil {


	/**
	 * Este método recebe uma String com uma descrição de horário e retorna uma coleção com os horários turmas relativos ao horário informado.
	 * @param codigo
	 * @param unidade
	 * @param nivel
	 * @param dao
	 * @return
	 * @throws DAOException
	 */
	public static List<HorarioTurma> parseCodigoHorarios(String codigo, int unidade, char nivel, HorarioDao dao) throws DAOException {
		ArrayList<HorarioTurma> horarios = new ArrayList<HorarioTurma>(0);
		
		if (codigo != null) {

			String[] split = codigo.split(",");
			
			for (String c : split) {
				SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
				Pattern p = Pattern.compile("\\d{2}/\\d{2}/\\d{4}");
				Matcher m = p.matcher(c);
				Date[] datas = new Date[2];
				int cont = 0;
				while (m.find()) {
					try {
						datas[cont++] = format.parse(m.group(0));
					} catch (ParseException e) {
						e.printStackTrace();
					}
//					datas[cont++] = new Date(m.group(0)); 
				}				
				
				StringBuilder sb = new StringBuilder();
				sb.append(c);
				int start = sb.indexOf("(");
				if (start != -1)
					sb.delete(start, sb.length());
				
				StringTokenizer st = new StringTokenizer(sb.toString().trim(), " ");
				while (st.hasMoreTokens()) {
					String cod = st.nextToken();
					char turno = 0;
					if (cod.indexOf("M") > 0)
						turno = 'M';
					else if (cod.indexOf("T") > 0)
						turno = 'T';
					else if (cod.indexOf("N") > 0)
						turno = 'N';

					int tPos = cod.indexOf(turno);
					for (int dia = 0; dia < cod.substring(0, tPos).length(); dia++) { // do inicio ao turno
						for (int ordem = 0; ordem < cod.substring(tPos+1, cod.length()).length(); ordem++) { // do turno ao final
							Horario h = dao.findByUnidade(unidade, nivel, turno, cod.charAt(tPos+1+ordem));
							horarios.add(new HorarioTurma(h, cod.charAt(dia), datas[0], datas[1]));
						}
					}
				}
			}
			

		}

		return horarios;
	}
	
	/**
	 * Retorna a descrição do horário da turma no formato DIAS-TURNO-AULAS
	 *
	 * @param tu
	 * @return
	 */
	public static String formatarCodigoHorarios(Turma tu) {
		StringBuffer res = new StringBuffer();
		
		if (tu.getDisciplina().isPermiteHorarioFlexivel()) {
			List<GrupoHorarios> grupos = agruparHorarioPorPeriodo(tu.getHorarios());
			
			for (Iterator<GrupoHorarios> iterator = grupos.iterator(); iterator.hasNext();) {
				GrupoHorarios grupoHorario = iterator.next();
				String m = agruparHorarios('M', new ArrayList<HorarioTurma>( grupoHorario.getHorarios() ));
				String t = agruparHorarios('T', new ArrayList<HorarioTurma>( grupoHorario.getHorarios() ));
				String n = agruparHorarios('N', new ArrayList<HorarioTurma>( grupoHorario.getHorarios() ));
				res.append(m);
				if (!t.equals(""))
					res.append(" " + t);
				if (!n.equals(""))
					res.append(" " + n);
				
				
				res.append("(" + 
						Formatador.getInstance().formatarData(grupoHorario.getPeriodo().getInicio()) + " - " +
						Formatador.getInstance().formatarData(grupoHorario.getPeriodo().getFim()) + ")");
				
				if (iterator.hasNext())
					res.append(", ");
				
			}
		} else {
			String m = agruparHorarios('M', tu.getHorarios());
			String t = agruparHorarios('T', tu.getHorarios());
			String n = agruparHorarios('N', tu.getHorarios());
			
			res.append(m);
			if (!t.equals(""))
				res.append(" " + t);
			if (!n.equals(""))
				res.append(" " + n);
		}
		return res.toString().trim();
	}

	/**
	 * Agrupa os horários da coleção da turma por turno e baseado no agrupamento
	 * de horários por dia ATENÇÃO!: os objetos dentro da lista de HorarioTurma
	 * devem estar todos populados com os valores do banco.
	 *
	 * @param turno
	 * @param horariosTurma
	 * @return
	 */
	public static String agruparHorarios(char turno, List<HorarioTurma> horariosTurma) {
		// Vai agrupar todos os horários desse turno
		ArrayList<StringBuffer> grupoCodigos = new ArrayList<StringBuffer>(0);
		// Percorrer os dias da semana e criar grupos
		for (int dia = Calendar.SUNDAY; dia <= Calendar.SATURDAY; dia++) {
			// Se um dia possuir algum horário, é identificado um grupo
			ArrayList<Horario> grupoHorariosDia = new ArrayList<Horario>(0);
			for (HorarioTurma ht : horariosTurma) {
				if (new Integer(ht.getDia() + "") == dia && turno == ht.getHorario().getTurno().charAt(0))
					grupoHorariosDia.add(ht.getHorario());
			}

			if (!grupoHorariosDia.isEmpty()) {
				String aulas = "";
				for (Horario h : grupoHorariosDia) {
					aulas += h.getOrdem();
				}
				String cod = dia + "" + turno + aulas;

				// Antes de adicionar, tem q saber se não já existe um grupo com
				// essas aulas
				boolean naoTem = true;
				for (StringBuffer c : grupoCodigos) {
					if (c.substring(c.indexOf(turno + "") + 1, c.length()).equals(aulas)) {
						c.insert(c.indexOf(turno + ""), dia + "");
						naoTem = false;
						break;
					}
				}
				/*
				 * O grupo identificado só é adicionado se não já tiver sido
				 * adicionado um com as mesmas aulas
				 */
				if (naoTem)
					grupoCodigos.add(new StringBuffer(cod));
			}
		}
		StringBuffer codigoDesseTurno = new StringBuffer();
		for (StringBuffer c : grupoCodigos) {
			codigoDesseTurno.append(c + " ");
		}
		// O código final é resultado da concatenação dos grupos encontrados
		return codigoDesseTurno.toString();
	}

	/**
	 * Preenche os horários da turma passada por parâmetro de acordo com os
	 * horários escolhidos na jsp também passado por parâmetro (no formato
	 * h_DIA_POSICAOLISTA)
	 * 
	 * Se o componente nao tiver horaririos flexiveis, periodoInicial e periodoFinal recebem null
	 */
	@SuppressWarnings("unchecked")
	public static void formataHorarios(String[] horariosEscolhidos, Turma t, List<Horario> todosHorarios, Date periodoInicial, Date periodoFinal) {
		List<HorarioTurma> escolhidos = extrairHorariosEscolhidos(horariosEscolhidos, t,
				todosHorarios, periodoInicial, periodoFinal);
		// fazendo o merge
		Collection<HorarioTurma> praRemover = CollectionUtils.subtract(t.getHorarios(), escolhidos);
		t.getHorarios().removeAll(praRemover);
		Collection<HorarioTurma> praAdicionar = CollectionUtils.subtract(escolhidos, t.getHorarios());
		t.getHorarios().addAll(praAdicionar);
	}
	
	/**
	 * Monta uma lista com os Horarios escolhidos a partir de uma string
	 * 
	 * @param horariosEscolhidos
	 * @param t
	 * @param todosHorarios
	 * @param periodoInicial
	 * @param periodoFinal
	 * @return
	 */
	public static List<HorarioTurma> extrairHorariosEscolhidos(String[] horariosEscolhidos,
			Turma t, List<Horario> todosHorarios, Date periodoInicial,
			Date periodoFinal) {
		List<HorarioTurma> escolhidos = new ArrayList<HorarioTurma>(0);
		for (String h : horariosEscolhidos) {
			StringTokenizer st = new StringTokenizer(h, "_");
			st.nextToken();
			char dia = st.nextToken().charAt(0);
			Integer linha = Integer.parseInt(st.nextToken());
			Horario hr = todosHorarios.get(linha);
			
			HorarioTurma horario = null;
			if (periodoInicial == null && periodoFinal == null)
				horario = new HorarioTurma(hr, t);
			else
				horario = new HorarioTurma(hr, t, periodoInicial, periodoFinal);
			
			horario.setDia(dia);
			escolhidos.add(horario);
		}
		return escolhidos;
	}

	/**
	 * Uma lista de de horários de turma em alto nível é convertida em um array
	 * de String no formato h_DIA_POSICAOLISTA , para ser exibida na JSP com os
	 * checkboxes
	 *
	 * @param colecao
	 * @param todosHorarios
	 * @return
	 */
	public static String[] parseHorarios(List<HorarioTurma> colecao, List<Horario> todosHorarios) {
		// adaptação para o período de transição de horários
		// caso o horário não esteja na lista (horário inativo), acrescenta-o
		boolean reordena = false;
		for (HorarioTurma ht : colecao) {
			if (todosHorarios.indexOf(ht.getHorario()) < 0) {
				todosHorarios.add(ht.getHorario());
				reordena = true;
			}
		}
		// se houve inserção de horário, reordena a lista.
		if (reordena) Collections.sort(todosHorarios, new Comparator<Horario>() {
			public int compare(Horario o1, Horario o2) {
				return o1.getInicio().compareTo(o2.getInicio());
			}
		});
		String[] horarios = new String[colecao.size()];
		int i = 0;
		for (HorarioTurma ht : colecao) {
			int index = todosHorarios.indexOf(ht.getHorario());
			if (index < 0) todosHorarios.add(ht.getHorario());
			index = todosHorarios.indexOf(ht.getHorario());
			String h = "h_" + ht.getDia() + "_" + index;
			horarios[i] = h;
			i++;
		}
		return horarios;
	}

	/**
	 * Retorna uma lista de horários com o turno e ordem definidos
	 * @return
	 */
	public static List<Horario> getHorariosLato() {
		ArrayList<Horario> horarios = new ArrayList<Horario>();
		Short turno = Horario.MANHA;
		Calendar meioDia = Calendar.getInstance();
		meioDia.setTime(Formatador.getInstance().parseHora("11:59"));
		Calendar seisNoite = Calendar.getInstance();
		seisNoite.setTime(Formatador.getInstance().parseHora("17:59"));
		Calendar cal = Calendar.getInstance();
		cal.setTime(Formatador.getInstance().parseHora("07:00"));
		int ordemTurno = 1;
		for (int i = 1; i <= 16; i++) {
			Horario h = new Horario();
			h.setAtivo(true);
			h.setInicio(cal.getTime());
			cal.add(Calendar.HOUR_OF_DAY, 1);
			h.setFim(cal.getTime());
			h.setNivel(NivelEnsino.LATO);
			h.setTipo(turno);
			h.setOrdem(new Short(ordemTurno+""));
			horarios.add(h);
			ordemTurno++;
			if (turno < Horario.TARDE && cal.after(meioDia) && cal.before(seisNoite)) {
				ordemTurno = 1;
				turno = Horario.TARDE;
			} else if (turno < Horario.NOITE && cal.after(seisNoite)) {
				ordemTurno = 1;
				turno = Horario.NOITE;
			}
		}
		return horarios;
	}


	/**
	 * Verifica se há choque de DATAS entre as turmas informadas
	 * @param t1
	 * @param t2
	 * @return
	 */
	public static boolean chocaDatas(Turma t1, Turma t2) {
		
		if (t1.getDataInicio() != null && t1.getDataFim() != null && t2.getDataInicio() != null && t2.getDataFim() != null) {
			if ( t1.getDataInicio().compareTo(t2.getDataFim()) > 0  || t1.getDataFim().compareTo(t2.getDataInicio()) < 0) {
				return false;
			} else {
				return true;
			}
		}
		return false;
	}

	/**
	 * Este método verifica se há choque de horários entra a turma t e as turmas passadas na coleção turmas
	 * @param t
	 * @param turmas
	 * @param verificaDocentes se for TRUE só acusa choque entre turmas tiver choque de docentes
	 * @return retorna as turmas que houve choque de horário
	 */
	public static Collection<Turma> verificarChoqueHorario(Turma t, Collection<Turma> turmas, boolean verificaDocentes) {
		return verificarChoqueHorario(t, turmas, verificaDocentes, false, false);
	}
	
	/**
	 * Este método verifica se há choque de horários entra a turma t e as turmas passadas na coleção turmas
	 * @param t
	 * @param turmas
	 * @param verificaDocentes se for TRUE só acusa choque entre turmas tiver choque de docentes
	 * @return retorna as turmas que houve choque de horário
	 */
	public static Collection<Turma> verificarChoqueHorario(Turma t, Collection<Turma> turmas, boolean verificaDocentes, boolean ignoraTurmaMultiploDocentes, boolean ignoraTurmaAgrupadora) {
		ArrayList<Turma> comChoques = new ArrayList<Turma>(0);
		for (Turma turma : turmas) {
			// no caso de turmas de stricto sensu que tiveram a solicitação de matrícula negada,
			// não será verificado se há choque de horário
			if (turma.getSolicitacao() != null && turma.getSolicitacao().isNegada())
				continue;
			
			boolean validaHorario = true;
			
			if (turma.getParametros() != null) {
				validaHorario = turma.getParametros().isImpedeChoqueHorarios();
			}			
			
			//verifica choque de horário se não for a mesma turma, for turmas do mesmo ano.período E houver intercessão de período de ocorrência da turma.
			if (validaHorario && t.getId() != turma.getId() && chocaDatas(turma, t) &&
					t.getAno() == turma.getAno() && t.getPeriodo() == turma.getPeriodo())  {
			boolean choque = false;
				
				for (HorarioTurma ht : turma.getHorarios()) {
					if( t.temHorario(ht)  ){
						if (verificaDocentes) {
							for(DocenteTurma dt : t.getDocentesTurmas()) {
								//Se o horário do docente já estiver reservado em outra turma ministrada pelo docente, ocorre choque.									
								for(DocenteTurma dt2 : turma.getDocentesTurmas()) {
									if( TurmaValidator.validaMesmaPessoaDocenteTurma(dt,dt2)) { 
										for(HorarioDocente hd: dt.getHorarios()) {
											for(HorarioDocente hd2: dt2.getHorarios()) {
												if ( hd.getHorario().checarConflitoHorario(hd2.getHorario()) 
														&& hd.getDia() == hd2.getDia() 
														&& CalendarUtils.isIntervalosDeDatasConflitantes(hd.getDataInicio(), hd.getDataFim(), hd2.getDataInicio(), hd2.getDataFim())) {
													choque = true;
													break;
												}
											}
											
										}
									}
								}
							}
							// Não valida se ambas possuírem mais de um professor
							if (!ignoraTurmaMultiploDocentes && ((t.getDocentesTurmas() != null || turma.getDocentesTurmas() != null)
									&& (t.getDocentesTurmas().size() > 1 && turma.getDocentesTurmas().size() > 1)))
								choque = false;
							// Não acusa choque se ambas as turmas forem subturmas de uma mesma turma agrupadora
							if(!ignoraTurmaAgrupadora && ( t.getTurmaAgrupadora() != null && turma.getTurmaAgrupadora() != null 
									&& t.getTurmaAgrupadora().getId() == turma.getTurmaAgrupadora().getId() ))
								choque = false;
						} else {
							choque = true;
						}
					}
				}
				if (choque) {
					comChoques.add(turma);
					//break;
				}
				
			}
		}
		return comChoques;
	}

	/**
	 * Este método verifica se há algum horário (o mesmo horário) que tem choque entre todas as turmas da coleção.
	 * @param turmas
	 * @return TRUE caso tenha um horário comum em todas as turmas, FALSE caso contrário
	 */
	public static boolean possuiChoqueMesmoHorario(Collection<Turma> turmas) {
		Map<HorarioTurma, Integer> mapa = new HashMap<HorarioTurma, Integer>();
		for( Turma t : turmas ){
			for (Turma turma : turmas) {
				
				for (HorarioTurma ht : turma.getHorarios()) {
					if (t.getId() != turma.getId() && t.getAno() == turma.getAno() && t.getPeriodo() == turma.getPeriodo()
							&& chocaDatas(turma, t) && t.temHorario(ht) )  {
						Integer count = mapa.get(ht);
						if( count == null )
							mapa.put(ht, 1);
						else
							mapa.put(ht, count + 1 );
					}
				}
				
			}
		}
		
		for( Integer i : mapa.values() ){
			int size = turmas.size();
			if( i == size * (size - 1) )
				return true;
		}
		
		return false;
	}
	
	/**
	 * verifica se há choque de horário dos docentes entre a turma passada e a coleção de turmas
	 * @param t
	 * @param turmas
	 * @return
	 */
	public static Collection<Turma> verificarChoqueHorarioDocentes(Turma t, Collection<Turma> turmas) {
		return verificarChoqueHorario(t, turmas, true);
	}

	/**
	 * Verifica se há choque de horário entre as turmas passada sem considerar os docentes envolvidos
	 * @param t
	 * @param turmas
	 * @return
	 */
	public static Collection<Turma> verificarChoqueHorarioDiscentes(Turma t, Collection<Turma> turmas) {
		return verificarChoqueHorario(t, turmas, false);
	}

	/**
	 * Verifica se há choque de horário entre as disciplinas de uma turma de ensino médio, sem considerar os docentes envolvidos
	 * @param t
	 * @param turmas
	 * @return
	 */
	public static Collection<Turma> verificarChoqueHorarioTurmaMedio(Turma t, Collection<Turma> turmas) {
		return verificarChoqueHorario(t, turmas, false);
	}
	
	/**
	 * Verifica se existe choque de horário entre algumas das turmas da coleção de turmas
	 * @param turmas
	 * @return
	 */
	public static Collection<Turma> verificarChoqueHorario( Collection<Turma> turmas ){

		HashSet<Turma> comChoques = new HashSet<Turma>(0);
		for( Turma turmaComparar : turmas ){
			for( Turma turma : turmas ){
				if (turmaComparar.getId() != turma.getId() ) {
					for (HorarioTurma ht : turma.getHorarios()) {
						if (turmaComparar.temHorario(ht))  {
							comChoques.add(turma);
							comChoques.add(turmaComparar);
							break;
						}
					}
				}
			}
		}

		return comChoques;
	}

	/**
	 * Retorna o total de ocorrências de um dia da semana entre duas datas 
	 * @param diaSemana
	 * @param ini
	 * @param f
	 * @return
	 */
	public static int getTotalDias(int diaSemana, Date ini, Date f) {
		Calendar inicio = Calendar.getInstance();
		inicio.setTime(ini);
		Calendar fim = Calendar.getInstance();
		fim.setTime(f);
		int dias = 0;
		while (inicio.before(fim) || (inicio.get(Calendar.DATE) == fim.get(Calendar.DATE) && inicio.get(Calendar.MONTH) == fim.get(Calendar.MONTH)
				&& inicio.get(Calendar.YEAR) == fim.get(Calendar.YEAR))) {
			if (inicio.get(Calendar.DAY_OF_WEEK) == diaSemana)
				dias++;
			inicio.add(Calendar.DAY_OF_YEAR, 1);
		}
		return dias;
	}

	/**
	 * Retorna o total de ocorrências de um dia da semana entre duas datas 
	 * @param diaSemana
	 * @param ini
	 * @param f
	 * @return
	 */
	public static int getTotalDiasSemFeriados(int diaSemana, Date ini, Date f, Collection<Date> feriados) {
		Calendar inicio = Calendar.getInstance();
		inicio.setTime(ini);
		Calendar fim = Calendar.getInstance();
		fim.setTime(f);
		int dias = 0;
		while (inicio.before(fim) || (inicio.get(Calendar.DATE) == fim.get(Calendar.DATE) 
				&& inicio.get(Calendar.MONTH) == fim.get(Calendar.MONTH)
				&& inicio.get(Calendar.YEAR) == fim.get(Calendar.YEAR))) {
			
			if (inicio.get(Calendar.DAY_OF_WEEK) == diaSemana) { 
				// Se não tiver feriados adiciona o dia
				if (feriados == null || feriados.isEmpty()){ 
					dias++;
				} // Se possuir feriados, verifica se o dia cai num. 
				else if (!feriados.contains(inicio.getTime())) {
					dias++;	
				}	
			}
			inicio.add(Calendar.DAY_OF_YEAR, 1);
			
		}
		return dias;
	}
	
	/**
	 * Calcula a quantidade de horas determinadas pelo conjunto de horários
	 * @param horarios
	 * @return
	 */
	public static int calcularTotalHoras(Collection<HorarioTurma> horarios) {
		/*int total = 0;
		for (HorarioTurma ht : horarios) {
			int dias = getTotalDias(new Integer(ht.getDia()+""), ht.getDataInicio(), ht.getDataFim());
			long minutosPorDia = ht.getHoraFim().getTime() - ht.getHoraInicio().getTime();
			minutosPorDia = ((minutosPorDia / 1000) / 60) ;
			total = (int) (total + ( minutosPorDia * dias));
		}*/
		//return total / 60;
		return calcularTotalHoras( horarios, null, null );
	}

	/**
	 * Calcula a quantidade de horas determinadas pelo conjunto de horários
	 * utilizado na solicitação de turma onde não estão setadas as datas de início e fim da turma,
	 * então devem ser passadas as datas de início e fim a serem consideradas.
	 * @param horarios
	 * @return
	 */
	public static int calcularTotalHoras(Collection<HorarioTurma> horarios, Date dataInicio, Date dataFim) {

		if( dataInicio == null && dataFim == null ){
			int total = 0;
			for (HorarioTurma ht : horarios) {
				int dias = getTotalDias(new Integer(ht.getDia()+""), ht.getDataInicio(), ht.getDataFim());
				long minutosPorDia = ht.getHoraFim().getTime() - ht.getHoraInicio().getTime();
				minutosPorDia = ((minutosPorDia / 1000) / 60) ;
				total = (int) (total + ( minutosPorDia * dias));
			}
			return total / 60;
		}else{
			int total = 0;
			for (HorarioTurma ht : horarios) {
				int dias = getTotalDias(new Integer(ht.getDia()+""), dataInicio, dataFim);
				long minutosPorDia = ht.getHoraFim().getTime() - ht.getHoraInicio().getTime();
				minutosPorDia = ((minutosPorDia / 1000) / 60) ;
				total = (int) (total + ( minutosPorDia * dias));
			}
			return total / 60;
		}


	}

	/**
	 * Calcula o número de aulas determinadas pelo conjunto de horários
	 * @param horarios
	 * @return
	 * @throws NegocioException 
	 */
	public static int calcularNumAulas(Collection<HorarioTurma> horarios, Collection<Date> feriados) {
		return calcularNumAulas( horarios, null, null, feriados );
	}
	
	/**
	 * Calcula o número de aula determinadas pelo conjunto de horários
	 * utilizado na solicitação de turma onde não estão setadas as datas de início e fim da turma,
	 * então devem ser passadas as datas de início e fim a serem consideradas.
	 * @param horarios
	 * @return
	 * @throws NegocioException 
	 */
	public static int calcularNumAulas(Collection<HorarioTurma> horarios, Date dataInicio, Date dataFim, Collection<Date> feriados) {
		
		if( dataInicio == null && dataFim == null ){
			int total = 0;
			for (HorarioTurma ht : horarios) {
				int dias = getTotalDiasSemFeriados(new Integer(ht.getDia()+""), ht.getDataInicio(), ht.getDataFim(), feriados);
				total += dias;
			}
			return total;
		}else{
			int total = 0;
			for (HorarioTurma ht : horarios) {
				int dias = getTotalDiasSemFeriados(new Integer(ht.getDia()+""), dataInicio, dataFim, feriados);
				total += dias;
			}
			return total;
		}


	}
	
	/**
	 * Calcula o número de aula determinadas pelo conjunto de horários flexíveis
	 * utilizado na solicitação de turma onde não estão setadas as datas de início e fim da turma,
	 * então devem ser passadas as datas de início e fim da turma a serem consideradas.
	 * @param horarios
	 * @return
	 */
	public static int calcularNumAulasTurmaHorarioFlexivel(Collection<HorarioTurma> horarios, Date dataInicio, Date dataFim) {

		if( dataInicio == null || dataFim == null ){
			int total = 0;
			for (HorarioTurma ht : horarios) {
				int dias = getTotalDias(new Integer(ht.getDia()+""), ht.getDataInicio(), ht.getDataFim());
				total += dias;
			}
			return total;
		}else if ( dataInicio != null && dataFim != null ){
			// Verifica se existe um horário flexível antes do início da turma ou depois do seu fim.
			int total = 0;
			for (HorarioTurma ht : horarios) {
				Date inicio = ht.getDataInicio().getTime() < dataInicio.getTime() ? dataInicio : ht.getDataInicio();
				Date fim = ht.getDataFim().getTime() > dataFim.getTime() ? dataFim : ht.getDataFim();
				int dias = getTotalDias(new Integer(ht.getDia()+""), inicio, fim);
				total += dias;
			}
			return total;
		}
		return 0;


	}
	
	/**
	 * Método main.
	 * 
	 * @param args
	 * @throws ParseException
	 */
	public static void main(String[] args) throws ParseException {
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
		// Diz quantas QUINTAS tem entre essas datas
		System.out.println(getTotalDias(5, df.parse("04/01/2007"), df.parse("01/02/2007")));

	}

	/**
	 * Retorna o total de horários do dia da semana e turno especificado
	 * se o turno for nulo retorna o total de horários do dia da semana especificado
	 * @param dia
	 * @param turno
	 * @param horarios
	 * @return
	 */
	public static int getTotalHorariosDiaTurno( int dia, Short turno, Collection<HorarioTurma> horarios ){

		int total = 0;

		for( HorarioTurma ht : horarios ){
			String diaHorario = ht.getDia() + "";
			String diaParam = dia + "";

			if( diaHorario.equals( diaParam ) ){
				if( turno == null ){
					total++;
				}else if( turno.equals( ht.getHorario().getTipo() ) ){
					total++;
				}
			}
		}

		return total;

	}
	/**
	 * Verifica se o turno engloba todos os horários passados
	 *
	 * @param turno
	 * @param horarios
	 * @return
	 */
	public static boolean hasTurnoTodosHorarios(Turno turno, List<HorarioTurma> horarios) {
		for (HorarioTurma ht : horarios) {
			if (turno.getSigla().indexOf( ht.getHorario().getTurnoChar() ) == -1) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Verificar se existe choque de horários da turma informada
	 * com as turmas matriculadas ou em espera de um discente
	 *
	 * @param turma
	 * @param turmaOrigem Não deve ser considerado choque de horário com esta turma
	 * @param discente
	 * @return
	 * @throws DAOException
	 */
	public static boolean hasChoqueHorarios(Turma turma, DiscenteAdapter discente, Turma turmaOrigem) throws DAOException {

		DiscenteDao discenteDao = DAOFactory.getInstance().getDAO( DiscenteDao.class );
		TurmaDao turmaDao = DAOFactory.getInstance().getDAO( TurmaDao.class );
		SolicitacaoMatriculaDao solicitacaoMatriculaDao = DAOFactory.getInstance().getDAO( SolicitacaoMatriculaDao.class );

		try {
			// Turmas matriculadas
			Collection<Turma> turmasSemestre =  discenteDao.findTurmasMatriculadas(discente.getId());
			// Turmas solicitadas
			turmasSemestre.addAll( solicitacaoMatriculaDao.findTurmasSolicitadasEmEspera(discente, turma.getAno(), turma.getPeriodo()) ) ;

			// Remover a turma que verifica o choque de horário
			boolean removido = true;
			while (removido) {
				removido = turmasSemestre.remove(turmaOrigem);
			}

			// Verificar choque de horários
			Collection<Turma> turmasChoque = HorarioTurmaUtil.verificarChoqueHorarioDiscentes(turma, turmasSemestre);
			if( turmasChoque != null && !turmasChoque.isEmpty() ){
				return true;
			}

		} finally {
			discenteDao.close();
			turmaDao.close();
			solicitacaoMatriculaDao.close();
		}
		return false;
	}

	/** 
	 * Definição de um comparator para {@link GrupoHorarios}, que leva em consideração o 
	 * início do período.
	 * 
	 */
	public static Comparator<GrupoHorarios> comparatorGrupoHorarios = new Comparator<GrupoHorarios>(){

		public int compare(GrupoHorarios o1, GrupoHorarios o2) {
			return o1.getPeriodo().getInicio().compareTo(o2.getPeriodo().getInicio());
		}
	};	
	
	/**
	 * Retorna uma lista contendo os horários agrupados por período
	 * 
	 * @see GrupoHorarios
	 * @see PeriodoHorario
	 */
	@SuppressWarnings("unchecked")
	public static List<GrupoHorarios> agruparHorarioPorPeriodo(List<HorarioTurma> horarios){
		
		if (horarios == null)
			throw new NullPointerException("horarios não pode ser nulo");
		
		List<GrupoHorarios> grupoHorarios = new ArrayList<GrupoHorarios>();
		
		/**
		 * Captura o período dos horários (data inicio e fim), Set é usado pra não ter duplicidade
		 */
		Set<PeriodoHorario> listaPeriodos = new HashSet<PeriodoHorario>();
		for (HorarioTurma ht : horarios) {
				if (ht.getDataInicio() == null || ht.getDataFim() == null)
					throw new RuntimeNegocioException("Deve-se informar o Período do Horário para as turmas de componentes com horário flexível.");
				PeriodoHorario p = new PeriodoHorario();
				p.setInicio(ht.getDataInicio());
				p.setFim(ht.getDataFim());
				listaPeriodos.add(p);
				
		}
		
		// Itera sobre os periodo encontrados
		for (final PeriodoHorario ph : listaPeriodos) {
			// Localiza na colecao de horarios, todos os horarios de um periodo.
			// Por exemplo, todos os horarios que possuem: data incio em 10/08/2009 e data fim 10/09/2009
			Collection<HorarioTurma> horariosAgrupadosPorData = CollectionUtils.select(horarios,new Predicate(){
				public boolean evaluate(Object arg) {
					HorarioTurma ht = (HorarioTurma) arg;
					if (ht.getDataInicio().equals(ph.getInicio()) && ht.getDataFim().equals(ph.getFim()))
						return true;
					return false;
				}
			});
			
			GrupoHorarios gh = new GrupoHorarios();
			gh.setHorarios(horariosAgrupadosPorData);
			gh.setPeriodo(ph);
			grupoHorarios.add(gh);
			
			Collections.sort(grupoHorarios, comparatorGrupoHorarios);			
			
		}
		
		return grupoHorarios;
	}
	
	/**
	 * Verificar se existe choque de horários da turma informada
	 * com as turmas matriculadas ou em espera de um discente
	 *
	 * @param turma
	 * @param turmasOrigem Não deve ser considerado choque de horário com as disciplinas desta turma
	 * @param discente
	 * @return
	 * @throws DAOException
	 */
	public static boolean hasChoqueHorariosTurmas(Turma turma, DiscenteAdapter discente, Collection<Turma> turmasOrigem) throws DAOException {

		DiscenteDao discenteDao = DAOFactory.getInstance().getDAO( DiscenteDao.class );
		TurmaDao turmaDao = DAOFactory.getInstance().getDAO( TurmaDao.class );
		SolicitacaoMatriculaDao solicitacaoMatriculaDao = DAOFactory.getInstance().getDAO( SolicitacaoMatriculaDao.class );

		try {
			// Turmas matriculadas
			Collection<Turma> turmasSemestre =  discenteDao.findTurmasMatriculadas(discente.getId());
			// Turmas solicitadas
			turmasSemestre.addAll( solicitacaoMatriculaDao.findTurmasSolicitadasEmEspera(discente, turma.getAno(), turma.getPeriodo()) ) ;

			// Remover as turmas que verifica o choque de horário
			for (Turma turmaOrigem : turmasOrigem) {
				boolean removido = true;
				while (removido) {
					removido = turmasSemestre.remove(turmaOrigem);
				}
			}

			// Verificar choque de horários
			Collection<Turma> turmasChoque = HorarioTurmaUtil.verificarChoqueHorarioDiscentes(turma, turmasSemestre);
			if( turmasChoque != null && !turmasChoque.isEmpty() ){
				return true;
			}

		} finally {
			discenteDao.close();
			turmaDao.close();
			solicitacaoMatriculaDao.close();
		}
		return false;
	}

	/**
	 * Retorna o número total de horários de um determinado dia de aula de uma turma.
	 * 
	 * @param turma
	 * @param diaSemana
	 * @return
	 */
	public static short getPresencasDia(Turma turma, Date data) {
		
		int diaSemana = CalendarUtils.getDiaSemanaByData(data);
		
		short presencas = 0;
		for( HorarioTurma ht: turma.getHorarios()){
			if(ht.getDataInicio().getTime() <= data.getTime() && ht.getDataFim().getTime() >= data.getTime() && diaSemana == Character.getNumericValue(ht.getDia()) ){
				presencas++;
			}
		}

		return presencas;
	}
}
