 /*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '18/07/2007'
 *
 */
 
package br.ufrn.sigaa.ensino.util;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang.time.DateUtils;
import org.joda.time.DateTime;

import br.ufrn.arq.dao.DAOFactory;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.DefaultExtensionPointFactory;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.comum.dao.FeriadoDao;
import br.ufrn.comum.dominio.Feriado;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.sigaa.arq.dao.MunicipioDao;
import br.ufrn.sigaa.arq.dao.ensino.AulaExtraDao;
import br.ufrn.sigaa.arq.dao.ensino.FrequenciaAlunoDao;
import br.ufrn.sigaa.arq.dao.ensino.NotaUnidadeDao;
import br.ufrn.sigaa.ava.dao.TopicoAulaDao;
import br.ufrn.sigaa.ava.dao.TurmaVirtualDao;
import br.ufrn.sigaa.ava.dominio.AulaExtra;
import br.ufrn.sigaa.ava.dominio.AulaFrequencia;
import br.ufrn.sigaa.ava.dominio.TopicoAula;
import br.ufrn.sigaa.ava.validacao.TurmaVirtualException;
import br.ufrn.sigaa.dominio.CalendarioAcademico;
import br.ufrn.sigaa.dominio.ParametrosGestoraAcademica;
import br.ufrn.sigaa.ead.dominio.MetodologiaAvaliacao;
import br.ufrn.sigaa.ead.negocio.MetodologiaAvaliacaoHelper;
import br.ufrn.sigaa.ensino.dominio.ComponenteDetalhes;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.dominio.HorarioTurma;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.dominio.NotaUnidade;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.negocio.ParametrosGestoraAcademicaHelper;
import br.ufrn.sigaa.ensino.negocio.consolidacao.EstrategiaConsolidacao;
import br.ufrn.sigaa.ensino.negocio.consolidacao.EstrategiaConsolidacaoFactory;
import br.ufrn.sigaa.parametros.dominio.ParametrosGerais;
import br.ufrn.sigaa.pessoa.dominio.Municipio;

/**
 * Métodos utilitários para operações com turmas. 
 * 
 * @author David Pereira
 *
 */
public class TurmaUtil {

	/**
	 * Retorna as datas de aula de uma turma sem considerar os dias que são feriado.
	 * @param turma
	 * @param calendarioVigente
	 * @param reload
	 * @return
	 * @throws DAOException 
	 */
	
	public static Set<Date> getDatasAulasSemFeriados(Turma turma, CalendarioAcademico calendarioVigente, boolean reload) throws DAOException {
		TurmaVirtualDao dao = new TurmaVirtualDao();
		Set<Date> datas = getDatasAulasTruncate(turma, calendarioVigente);
		
		try {
			datas.removeAll( TurmaUtil.getFeriados(turma) );
		} finally {
			dao.close();
		}
		
		return datas;
	}
	
	/** 
	 * Popula os feriados para o ano do calendário vigente,
	 *  município e unidade de federação da unidade associada a turma. 
	 */
	public static List<Date> getFeriados(Turma turma){
		
		MunicipioDao daoMunicipio = DAOFactory.getInstance().getDAO(MunicipioDao.class);
		FeriadoDao daoFeriado = DAOFactory.getInstance().getDAO(FeriadoDao.class);
		try {
			Municipio municipioTurma = new Municipio();
			municipioTurma = daoMunicipio.findByTurma(turma);
			List <Feriado> feriados =  null;
			
			/** Popula todos os feriados de acordo co mo municipio setado */	
			if (turma.getDataInicio() != null && turma.getDataFim() != null){
				// Caso a turma esteja entre dois anos
				if( municipioTurma != null )
					feriados =  daoFeriado.findByPeriodoFeriadosLocalidade( turma.getDataInicio(), turma.getDataFim(),
								municipioTurma.getId(), 
								municipioTurma.getUnidadeFederativa().getId() );
				else
					feriados =  daoFeriado.findByPeriodoFeriadosLocalidade( turma.getDataInicio(), turma.getDataFim(),null,null);
			}else {
				if( municipioTurma != null )
					feriados =  daoFeriado.findFeriadosPorAnoMunicipioEstado( turma.getAno(), 
								municipioTurma.getNome(), 
								municipioTurma.getUnidadeFederativa().getSigla() );
				else
					feriados =  daoFeriado.findFeriadosPorAnoMunicipioEstado( turma.getAno(), null, null );
			}
			
			/** Popula a lista de datas de feriado */
			List<Date> listaDataFeriado = new ArrayList<Date>();
			for ( Feriado feriado : feriados ) {
				listaDataFeriado.add( feriado.getDataFeriado() );
			}
			return listaDataFeriado;
		
		} catch (DAOException e) {
			throw new TurmaVirtualException(e);
		}finally{
			daoMunicipio.close();
			daoFeriado.close();
		}
		
	}
	
	/**
	 * Retorna uma lista contendo todos os dias de aula da turma passada.
	 * 
	 * @param turma
	 * @return
	 * @throws DAOException
	 */
	public static List <AulaFrequencia> getListagemAulas (Turma turma) throws DAOException{
		TurmaVirtualDao dao = null;
		TopicoAulaDao topDao = null;
		List <AulaFrequencia> listagemAulas = new ArrayList <AulaFrequencia> ();
		
		try {
			dao = DAOFactory.getInstance().getDAO(TurmaVirtualDao.class);
			topDao = DAOFactory.getInstance().getDAO(TopicoAulaDao.class);
			
			// Busca os horários da turma.
			List <HorarioTurma> horariosTurma = (List<HorarioTurma>) dao.findByExactField(HorarioTurma.class, "turma.id", turma.getId(), "asc", "dataInicio", "dia", "horaInicio");
			// Busca as aulas extra da turma.
			List <AulaExtra> aulasExtras = dao.buscarTodasAulasExtras(turma);
			// Busca todos os feriados
			List <Date> feriados = getFeriados(turma);
			// Busca as datas das aulas que o professor cancelou
			List<Date> aulasCanceladas = new ArrayList<Date>();
			if ( turma.getTurmaAgrupadora() == null )
				aulasCanceladas = topDao.findDatasDeTopicosSemAula(turma.getId());
			else
				aulasCanceladas = topDao.findDatasDeTopicosSemAula(turma.getTurmaAgrupadora().getId());
			
			// Guarda os períodos de aula no formato {inicio, fim, quantidade de aulas (no caso de aula extra)}
			List <Object []> periodos = new ArrayList <Object []> ();
			// Primeiro, lista as aulas extras
			for (AulaExtra a : aulasExtras){
				Object [] periodo = {a.getDataAula(), a.getDataAula(), null, a.getNumeroAulas()};
				periodos.add(periodo);
			}
			
			// Depois, lista os horários
			for (HorarioTurma h : horariosTurma){
				Object [] periodo = {h.getDataInicio(), h.getDataFim(), h.getDia(), null};
				periodos.add(periodo);
			}
			
			// Para todos os períodos encontrados
			for (Object [] p : periodos){
				Date inicio = (Date) p[0];
				Date fim = (Date) p[1];
				
				Calendar cal = Calendar.getInstance();
				cal.setTime(inicio);
				
				if (p[2] != null){
					cal.set(Calendar.DAY_OF_WEEK, Integer.parseInt("" + p[2]));
					
					// Se o horario inicia na metade da semana mas o dia da semana do horario é antes da data
					// inicial, então pula uma semana
					if (cal.getTime().getTime() < ((Date) p[0]).getTime())
						cal.add(Calendar.DAY_OF_MONTH, 7);
				}
					
					
				
				// Adiciona uma aula ao calendário por semana dentro do periodo.
				// Se a aula já existir, incrementa em um o número de horários para o dia.
				while (cal.getTime().getTime() <= fim.getTime()){
					
					int dia = cal.get(Calendar.DAY_OF_MONTH);
					int mes = cal.get(Calendar.MONTH) + 1;
					
					boolean inserir = true;
					
					// Adiciona a data em ordem crescente.
					int i = -1;
					for (AulaFrequencia a : listagemAulas)
						if (a.getData().getTime() > cal.getTime().getTime()){
							i = listagemAulas.indexOf(a);
							break;
						} else if (a.getData().getTime() == cal.getTime().getTime()){
							
							if ( p[3] != null )
								a.setAulasExtra(a.getAulasExtra() + Integer.parseInt("" + p[3]));
							else
								a.setAulas(a.getAulas() + 1);
							inserir = false;
							break;
						} 
					
					if (i == -1)
						i = listagemAulas.size();
					
					if (inserir){
						// Adiciona a aula para o dia, na posição correta
						AulaFrequencia aula = new AulaFrequencia();
						aula.setDia(dia);
						aula.setMes(mes);
						// Se p[3] == null, não é uma aula extra. Conta um horário.
						if (p[3] == null)
							aula.setAulas(1);
						// Se for null, é aula extra. A quantidade de horários será a que está definida na aula extra.
						else
							aula.setAulasExtra(Integer.parseInt("" + p[3]));

						aula.setData(cal.getTime());
						
						// Checa se há um feriado cadastrado para este dia.
						boolean feriado = false;
						for (Date f : feriados)
							if (f.getTime() == aula.getData().getTime()){
								feriado = true;
								break;
							}
						aula.setFeriado(feriado);						
						
						// Checa se há uma aula cancelada para este dia.
						boolean cancelada = false;
						for (Date ac : aulasCanceladas)
							if (ac.getTime() == aula.getData().getTime()){
								cancelada = true;
								break;
							}
						
						aula.setAulaCancelada(cancelada);
						
						listagemAulas.add(i, aula);
					}
					
					// Adiciona sete dias para preparar a próxima semana.
					cal.add(Calendar.DAY_OF_MONTH, 7);
				}
			}
		
		} finally {
			if (dao != null)
				dao.close();
			if (topDao != null)
				topDao.close();
		}
		
		return listagemAulas;
	}
	
	/**
	 * Retorna as datas de aula de uma turma mesmo tendo dias que são feriado. Pode ou não solicitar o
	 * recarregamento da coleção de horários da turma.
	 * @param turma
	 * @param calendarioVigente
	 * @param reload
	 * @return
	 * @throws DAOException 
	 */
	public static Set<Date> getDatasAulas (Turma turma, CalendarioAcademico calendarioVigente, boolean reload) throws DAOException {
		
		TurmaVirtualDao dao = DAOFactory.getInstance().getDAO(TurmaVirtualDao.class);
		
		try{
			// Busca os horários da turma.
			List <HorarioTurma> horariosAulas = (List<HorarioTurma>) dao.findByExactField(HorarioTurma.class, "turma.id", turma.getId(), "asc", "dataInicio", "dia", "horaInicio");
			// Busca as aulas extra da turma.
			List <AulaExtra> aulasExtra = dao.buscarTodasAulasExtras(turma);
			
			List <AulaExtra> auxAE = new ArrayList <AulaExtra> ();
			
			// Datas em que a turma tem aula.
			List <Date> listagemAulas = new ArrayList <Date> ();
			
			// Separa as datas que têm aula.
			for (HorarioTurma aux : horariosAulas){
				Calendar cal = Calendar.getInstance();
				Calendar calFinal = Calendar.getInstance();
				// Tratando turmas migradas do PONTO A 
				if ( aux.getDataInicio() != null ) {
					cal.setTime(aux.getDataInicio());
					cal.set(Calendar.DAY_OF_WEEK, Integer.parseInt(""+aux.getDia()));
					
					// Se o horario inicia na metade da semana mas o dia da semana do horario é antes da data
					// inicial, então pula uma semana
					if (cal.getTime().getTime() < aux.getDataInicio().getTime())
						cal.add(Calendar.DAY_OF_MONTH, 7);
				
					if (dateNotInList(cal, listagemAulas)) {
						
						DateTime dti = new DateTime(aux.getHoraInicio());
						cal.set(Calendar.HOUR_OF_DAY, dti.getHourOfDay());
						cal.set(Calendar.MINUTE, dti.getMinuteOfHour());
						cal.set(Calendar.SECOND, dti.getSecondOfMinute());
						
						// Seta a data e hora final, para funcionar na iteração do laço
						DateTime dtf = new DateTime(aux.getHoraFim());
						calFinal.setTime(aux.getDataFim());
						calFinal.set(Calendar.HOUR_OF_DAY, dtf.getHourOfDay());
						calFinal.set(Calendar.MINUTE, dtf.getMinuteOfHour());
						calFinal.set(Calendar.SECOND, dtf.getSecondOfMinute());	
						
						// Enquanto estiver no período do horario, adiciona os dias da semana que ele representa
						while (cal.getTime().getTime() <= calFinal.getTime().getTime()){
							adicionarDataNaOrdem (listagemAulas, cal.getTime());
							//Vai para a próxima semana para verificar se continua no horário
							cal.add(Calendar.DAY_OF_MONTH, 7);
						}
					}
				}	
			}
			
			// Separa as datas que têm aula extra.
			for (AulaExtra ae : aulasExtra){
				boolean adicionar = true;
				
				for (AulaExtra aux : auxAE)
					if (ae.getDataAula() == aux.getDataAula()){
						adicionar = false;
						break;
					}
				
				if (adicionar){
					auxAE.add(ae);
					adicionarDataNaOrdem (listagemAulas, ae.getDataAula());
				}
			}
			
			Set <Date> rs = new TreeSet<Date>();
			
			for (Date d : listagemAulas)
				rs.add(d);
			
			return rs;
		} finally {
			if (dao != null)
				dao.close();
		}
	}
	
	/**
	 * Verifica se a data está na lista, descartando as horas
	 * @param cal
	 * @param listagemAulas
	 * @return
	 */
	private static boolean dateNotInList(Calendar cal, List<Date> listagemAulas) {
		for (Date date : listagemAulas) {
			if ( CalendarUtils.descartarHoras(date).equals(CalendarUtils.descartarHoras(cal.getTime())) ) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Adiciona a data em ordem crescente.
	 * 
	 * @param listagemAulas
	 * @param data
	 */
	private static void adicionarDataNaOrdem (List <Date> listagemAulas, Date data){
		int i = -1;
		for (Date d : listagemAulas)
			if (d.getTime() > data.getTime()){
				i = listagemAulas.indexOf(d);
				break;
			}
		
		if (i == -1)
			i = listagemAulas.size();
		
		Date inserir = data; 
		
		listagemAulas.add(i, inserir);
	}
	
	/**
	 * Retorna as datas de aula de uma turma mesmo tendo dias que são feriado. 
	 * As datas são truncado a hora, os minutos e os segundos, para comparação com a frequencia do aluno.
	 * Não solicita o recarregamento da coleção de horários da turma. 
	 * @param turma
	 * @param calendarioVigente
	 * @return
	 * @throws DAOException 
	 */
	public static Set<Date> getDatasAulasTruncate(Turma turma, CalendarioAcademico calendarioVigente) throws DAOException {
		Set<Date> res = new TreeSet<Date>();
		Set<Date> datasAulas = getDatasAulas(turma, calendarioVigente, false);
		
		for ( Date aula : datasAulas){
			Date dt = new Date();
			dt = DateUtils.truncate(aula, Calendar.DATE);
			res.add(dt);
		}
		return res;
	}

	
	/**
	 * Calcula a porcentagem de dias de frequência lançados em relação ao
	 * número total de aulas da turma.
	 * @param aulas
	 * @param turma
	 * @return
	 * @throws NegocioException 
	 */
	public static double getPorcentagemFrequenciaLancada(List <AulaFrequencia> aulas, Turma turma, ParametrosGestoraAcademica param) {
		FrequenciaAlunoDao faDao = null;
		try {
			faDao = DAOFactory.getInstance().getDAO(FrequenciaAlunoDao.class);
			List<Date> datasComFrequencia = faDao.getDatasComFrequencia(turma);
			
			int aulasComFrequencia = 0;
			int aulasExtras = 0;
			
			for (AulaFrequencia a : aulas){
				for (Date d : datasComFrequencia){
					Calendar cal = Calendar.getInstance();
					cal.setTime(d);
					
					if (cal.get(Calendar.DAY_OF_MONTH) == a.getDia() && cal.get(Calendar.MONTH) + 1 == a.getMes()){
						aulasComFrequencia += a.getAulas() + a.getAulasExtra();
						break;
					}
				}
				
				if (a.getAulasExtra() > 0)
					aulasExtras += a.getAulasExtra();
			}
			
			float cargaHoraria = getCargaHorariaCorrigida(turma, param);
			
			return 100 * ((float) aulasComFrequencia) / (cargaHoraria + aulasExtras);
			
		} finally {
			if (faDao != null)
				faDao.close();
		}
	}

	/**
	 * Calcula a porcentagem de tópicos de aulas lançados em relação ao
	 * número total de aulas da turma.
	 * @param aulas
	 * @param turma
	 * @param topicosTransientes uma lista com tópicos que estão para ser adicionados à turma
	 * @return
	 * @throws DAOException 
	 * @throws NegocioException 
	 */
	public static double getPorcentagemAulasLancadas(List <AulaFrequencia> aulas, Turma turma, ParametrosGestoraAcademica param) throws DAOException{
		TopicoAulaDao dao = null;
		try {
			dao = DAOFactory.getInstance().getDAO(TopicoAulaDao.class);
			
			List<TopicoAula> topicos = null;
			
			if (turma.isSubTurma()) {
				turma.setTurmaAgrupadora(dao.findByPrimaryKey(turma.getId(), Turma.class, "turmaAgrupadora.id").getTurmaAgrupadora());
				topicos = dao.findByTurma(turma.getTurmaAgrupadora());
			} else
				topicos = dao.findByTurma(turma);
			
			int aulasComTopico = 0;
			int aulasExtras = 0;
			
			for (AulaFrequencia aula : aulas){
				for (TopicoAula topico : topicos){
					if (CalendarUtils.isDentroPeriodo(topico.getData(), topico.getFim(), aula.getData())) {
						
						if (!aula.isAulaCancelada() && !aula.isFeriado()){
							aulasComTopico += aula.getAulas();
						}
						
						aulasExtras += aula.getAulasExtra();
						break;
					}
				}
			}
			
			float cargaHoraria = getCargaHorariaCorrigida(turma, param);
			
			return 100 * ((float) aulasComTopico + aulasExtras) / (cargaHoraria * 60 / param.getMinutosAulaRegular());
		} finally {
			if (dao != null)
				dao.close();
		}
	}
	
	/**
	 * Retorna a carga horária total corrigida da turma.
	 * 
	 * @param turma
	 * @param param
	 * @return
	 * @throws NegocioException 
	 */
	private static float getCargaHorariaCorrigida (Turma turma, ParametrosGestoraAcademica param) {
		ComponenteDetalhes detalhes = turma.getDisciplina().getDetalhes();
		
		try {
			if ( param.getHorasCreditosLaboratorio() == null )
				throw new NegocioException("Impossível determinar o valor do parâmetro, que determina a quantidade de horas aula de laboratório equivalente a um crédito.");
			if ( param.getHorasCreditosAula() == null ) 
				throw new NegocioException("Impossível determinar o valor do parâmetro, que determina a quantidade de horas aula equivalente a um crédito.");
		} catch (NegocioException e) {
			e.printStackTrace();
		}
		
		// Em contato telefônico com Mirza, esta disse que a carga horária de estágio poderia ser ignorada para cálculo de frequência mínima.
		float razaoLaboratorio = param.getHorasCreditosLaboratorio() / param.getHorasCreditosAula();
		
		return	detalhes.getChAula()
				+ detalhes.getChLaboratorio() / razaoLaboratorio;
	}
	
	/**
	 * Retorna as datas das aulas canceladas e que não existe aulas extras.
	 * 
	 * @param turma
	 * @return
	 * @throws DAOException 
	 */
	public static List<Date> getDatasCanceladas ( int idTurma ) throws DAOException {
	
		TopicoAulaDao topicoDao = null;
		AulaExtraDao aulaExtraDao = null;
		
		try{
			topicoDao = DAOFactory.getInstance().getDAO(TopicoAulaDao.class);
			aulaExtraDao = DAOFactory.getInstance().getDAO(AulaExtraDao.class);
			
			List<TopicoAula> aulasCanceladas = topicoDao.findTopicosSemAula(idTurma);
			List<Integer> idsTurmas = new ArrayList<Integer>();
			idsTurmas.add(idTurma);
			List<AulaExtra> aulasExtras = aulaExtraDao.findByIdsTurmas(idsTurmas); 
			List<Date> aulasSemFaltas = new ArrayList<Date>();

			if (aulasCanceladas != null)
				for (TopicoAula ac : aulasCanceladas) {
					boolean inserir = true;
					if (aulasExtras != null)
						for (AulaExtra ae : aulasExtras) {
							if ( ae.getDataAula().getTime() == ac.getData().getTime() && 
								 ae.getCriadoEm().getTime() > ac.getDataCadastro().getTime() ) {
								inserir = false;
								break;
							}	
						}
					if (inserir)
						aulasSemFaltas.add(ac.getData());
				}
			
			/** Verifica se as datas não estão se repitindo. */
			List<Date> result = new ArrayList<Date>();
			if (aulasSemFaltas != null)
				for (Date d : aulasSemFaltas) {
					if (!result.contains(d))
						result.add(d);
				}			
			
			return result;

		}finally {
			if (topicoDao != null)
				topicoDao.close();
			if (aulaExtraDao != null)
				aulaExtraDao.close();
		}	
	
	}

	/**
	 * Retorna a quantidade de unidades de uma turma.
	 * 
	 * @param turma
	 * @return
	 * @throws ArqException
	 * @throws NegocioException 
	 * @throws NegocioException 
	 */
	public static Integer getNumUnidadesDisciplina(Turma turma) throws ArqException, NegocioException {
		
		NotaUnidadeDao nDao = null;
		
		try {
			if (turma == null || turma.getDisciplina() == null)
				throw new ArqException("Impossível determinar a quantidade de unidades da disciplina.");
			
			if ( turma.isGraduacao() && turma.isEad() && !turma.isEstagioEad() ){
				if ( isEadUmaProva(turma) )
					return 1;
				if ( isEadDuasProvas(turma) )
					return 2;
			}	
			
			ParametrosGestoraAcademica param = ParametrosGestoraAcademicaHelper.getParametros(turma);
			
			if (turma.isConsolidada() && param.getMetodoAvaliacao() == br.ufrn.sigaa.ensino.dominio.MetodoAvaliacao.NOTA){
				nDao = DAOFactory.getInstance().getDAO(NotaUnidadeDao.class);
				return new Integer(nDao.countNotasUnidadesConsolidadasByTurma(turma));
			}
			
			if (turma.getDisciplina().getPrograma() != null && turma.getDisciplina().getPrograma().getNumUnidades() != null) 
				return new Integer(turma.getDisciplina().getPrograma().getNumUnidades());
			else if (turma.getDisciplina().getNumUnidades() != null)
				return new Integer(turma.getDisciplina().getNumUnidades());
			
			return new Integer(param.getQtdAvaliacoes()); 
		} finally {
			if (nDao!=null)
				nDao.close();
		}
	}
	   
	/**
	 * Retorna se a turma é EAD e se a metodologia é Uma Prova.
	 * @return
	 * @throws DAOException 
	 * @throws NegocioException 
	 * @throws NegocioException 
	 */
	private static boolean isEadUmaProva(Turma turma) throws DAOException, NegocioException {
		DiscenteAdapter discenteEad = (!turma.getMatriculasDisciplina().isEmpty() ? turma.getMatriculasDisciplina().iterator().next().getDiscente() : null);
		// Lato EAD e Estágio EAD não possuem metodologia de avaliação
		if ( discenteEad != null && discenteEad.getCurso() != null){
			MetodologiaAvaliacao metodologia = MetodologiaAvaliacaoHelper.getMetodologia(discenteEad.getCurso(), turma.getAno(), turma.getPeriodo());
			
			if (metodologia == null)
				throw new NegocioException("Nenhuma metodologia de Avaliação foi encontrada para este curso.");

			if (turma.isEad() && !turma.isLato() && metodologia.isUmaProva() )
				return true;
		}
		return false;
	}
	/**
	 * Retorna se a turma é EAD e se a metodologia é Duas Prova.
	 * @return
	 * @throws DAOException 
	 * @throws NegocioException 
	 * @throws NegocioException 
	 */
	private static boolean isEadDuasProvas(Turma turma) throws DAOException, NegocioException {
		DiscenteAdapter discenteEad = (!turma.getMatriculasDisciplina().isEmpty() ? turma.getMatriculasDisciplina().iterator().next().getDiscente() : null);
		// Lato EAD e Estágio EAD não possuem metodologia de avaliação
		if ( discenteEad != null && discenteEad.getCurso() != null) {
			MetodologiaAvaliacao metodologia = MetodologiaAvaliacaoHelper.getMetodologia(discenteEad.getCurso(), turma.getAno(), turma.getPeriodo());
			
			if (metodologia == null)
				throw new NegocioException("Nenhuma metodologia de Avaliação foi encontrada para este curso.");
			
			if (turma.isEad() && !turma.isLato() && metodologia.isDuasProvas() )
				return true;
		}
		return false;
	}
	
	/**
	 * Retorna os pesos das unidades de uma turma.
	 * 
	 * @param turma
	 * @return
	 * @throws ArqException
	 * @throws NegocioException 
	 * @throws NegocioException 
	 */
	public static String[] getArrayPesosUnidades( Turma turma ) throws ArqException, NegocioException {
		
		if (turma == null || turma.getDisciplina() == null)
			throw new ArqException("Impossível determinar os pesos das unidades da disciplina.");
		
		Integer numUnidades = getNumUnidadesDisciplina(turma);
		
		String [] pesos2Unidades = ParametrosGestoraAcademicaHelper.getParametros(turma).getArrayPesosAvaliacoes2Unidades();
		
		if ( numUnidades == 2 && pesos2Unidades != null)
				return pesos2Unidades;
			
		return ParametrosGestoraAcademicaHelper.getParametros(turma).getArrayPesosAvaliacoes();
		
	}
	
	
	/**
	 * Configura os pessos das notas e seta na matricula a estratégia de consolidação 
	 * a ser considerada para o calculo das notas. Faz isso apenas se não
	 * foram setados anteriormente.
	 * 
	 * @param turma
	 * @throws ArqException
	 * @throws NegocioException 
	 */
	public static void setarPesosNotaUnidadeEstrategiaConolidacao(Turma turma) throws ArqException, NegocioException {		
		
		ParametrosGestoraAcademica param = ParametrosGestoraAcademicaHelper.getParametros(turma);
		String[] pesosAvaliacoes = TurmaUtil.getArrayPesosUnidades(turma);
		String[] pesoMediaPesoRec = param.getArrayPesosMediaRec();
		EstrategiaConsolidacaoFactory factory = (EstrategiaConsolidacaoFactory) DefaultExtensionPointFactory.getImplementation(ParametrosGerais.IMPLEMENTACAO_ESTRATEGIA_CONSOLIDACAO_FACTORY);
		EstrategiaConsolidacao estrategia = factory.getEstrategia(turma, param);
				
		if (turma.getMatriculasDisciplina() != null) {
			for (MatriculaComponente matricula : turma.getMatriculasDisciplina()) {				
				
				if ( matricula.isConsolidada() )
					continue;
				
				if (matricula.getEstrategia() == null)
					matricula.setEstrategia(estrategia);
				
				if (matricula.getPesoMedia() == null)
					matricula.setPesoMedia(new Integer(pesoMediaPesoRec[0].trim()));
				
				if (matricula.getPesoRecuperacao() == null)
					matricula.setPesoRecuperacao(new Integer(pesoMediaPesoRec[1].trim()));
				
				if (matricula.getNotas() != null) {
					int unidade = 0;
					for (NotaUnidade nota : matricula.getNotas()) {
						unidade = nota.getUnidade()-1;
						if (ValidatorUtil.isEmpty(nota.getPeso())) {
							nota.setPeso(pesosAvaliacoes[unidade].trim());							
						}
					}
				}	
			}
		}
		
	}
	
	/**
	 * Reordena a lista de turma, usado quando a lista é formada por um Map
	 * 
	 * @param turma
	 * @throws ArqException
	 */
	public static void ordenarTurmas(List<Turma> turmas)  {		
		
		if (turmas == null)
			return;
		
		Collections.sort(turmas, new Comparator<Turma>(){
			public int compare(Turma t1, Turma t2) {
				
				int retorno = 0;
				// Verifica o ano
				retorno = t2.getAno() - t1.getAno();				
				// Verifica o período
				if( retorno == 0 )
					retorno = t2.getPeriodo() - t1.getPeriodo();
				
				if (retorno == 0){					
					// Verificando a disciplina
					if ( t1.getDisciplina() != null && t2.getDisciplina() != null ){						
						// Verifica o nível
						retorno = t1.getDisciplina().getNivel() - t2.getDisciplina().getNivel();						
						// Verifica o nome
						if( retorno == 0 )
							retorno = t1.getDisciplina().getNome().compareTo(t2.getDisciplina().getNome());
					}						
					// Verifica o código da turma
					if( retorno == 0 )
						retorno = t1.getCodigo().compareTo(t2.getCodigo());
				}
					
				return retorno;
			}
		});		
	}
}