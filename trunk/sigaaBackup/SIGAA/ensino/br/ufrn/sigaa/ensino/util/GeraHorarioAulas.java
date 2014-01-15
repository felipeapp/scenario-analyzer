/*
 * Sistema Integrado de Patrimônio e Administração de Contratos
 * Superintendência de Informática - UFRN
 */
package br.ufrn.sigaa.ensino.util;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.ensino.dominio.HorarioTurma;
import br.ufrn.sigaa.ensino.dominio.Turma;

/**
 * Classe responsável por gerar os horários de aula de uma turma no seu período
 * de realização
 *
 * @author Gleydson
 *
 */
public class GeraHorarioAulas {

	/**
	 * Retorna
	 * @param t
	 * @return
	 * @throws DAOException
	 */
	public static Collection<DiaAula> getHorarios(Turma t) throws DAOException {

		GenericDAO dao = new GenericSigaaDAO();
		Turma turma = dao.findByPrimaryKey(t.getId(), Turma.class);

//		Date inicio = turma.getDataInicio();
//		Date fim = turma.getDataFim();

		ArrayList<DiaAula> aulas = new ArrayList<DiaAula>();

		Calendar c = Calendar.getInstance();

		// Data de início das aulas
		Date dataBase = turma.getDataInicio();
		c.setTime(dataBase);
		// vai para domingo (início da semana)
		c.add(Calendar.DAY_OF_MONTH, c.get(Calendar.DAY_OF_WEEK) * -1);

//		for (HorarioTurma h : turma.getHorarios()) {

			/*TODO: CONSIDERAR NOVA ESTRUTURA DE HORARIO
			 * List<Integer> diasHorario = h.getListDias();

			// Enquanto a data base de cálculo for anterior ao final da turma
			// repita
			while (dataBase.before(fim)) {
				int dia = c.get(Calendar.DAY_OF_WEEK);

				if (diasHorario.contains(dia) && c.getTime().after(inicio)
						&& c.getTime().before(fim)) {
					DiaAula aula = new DiaAula();
					aula.setData(c.getTime());
					aula.setHoraInicio(h.getInicio().getInicio());
					aula.setHoraFim(h.getFim().getFim());
					aulas.add(aula);
				}
				c.add(Calendar.DAY_OF_MONTH, 1);

				// incrementar o período de repeticação
				c.add(Calendar.DAY_OF_WEEK, 7 * h.getPeriodoRepeticao());
				dataBase = c.getTime();
			}*/
//		}
		return aulas;
	}

	/**
	 * Retorna a quantidade de horas de aulas para uma turma, dado o início, o fim e os horários
	 * @param horarios
	 * @param inicio
	 * @param fim
	 * @return
	 * @throws DAOException
	 */
	public static double calculaTotalHoras(Collection<HorarioTurma> horarios, Date inicio, Date fim) throws DAOException {

		int total = 0;

		Calendar c = Calendar.getInstance();

		// Data de início das aulas
		Date dataBase = inicio;
		c.setTime(dataBase);
		// vai para domingo (início da semana)
		c.add(Calendar.DAY_OF_MONTH, c.get(Calendar.DAY_OF_WEEK) * -1);

//		for (HorarioTurma h : horarios) {

			/*TODO: CONSIDERAR NOVA ESTRUTURA DE HORARIO
			 * List<Integer> diasHorario = h.getListDias();

			// Enquanto a data base de cálculo for anterior ao final da turma
			// repita
			while (dataBase.before(fim)) {
				int dia = c.get(Calendar.DAY_OF_WEEK);

				if (diasHorario.contains(dia) && c.getTime().after(inicio)
						&& c.getTime().before(fim)) {
					DiaAula aula = new DiaAula();
					aula.setData(c.getTime());
					aula.setHoraInicio(h.getInicio().getInicio());
					aula.setHoraFim(h.getFim().getFim());

					int intervaloTempo = (int) (h.getFim().getFim().getTime() - h.getInicio().getInicio().getTime());
					total += intervaloTempo;

				}
				c.add(Calendar.DAY_OF_MONTH, 1);

				// incrementar o período de repeticação
				c.add(Calendar.DAY_OF_WEEK, 7 * h.getPeriodoRepeticao());
				dataBase = c.getTime();
			}*/
//		}
		return ((total / 1000.0) / 60) / 60;
	}
}