/*
*
* Universidade Federal do Rio Grande do Norte
* Superintendência de Informática
* Diretoria de Sistemas
* 
* Criado em: 14/06/2011
* 
*/
package br.ufrn.sigaa.biblioteca.controle_estatistico.dao;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.hibernate.Query;

import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.TipoProrrogacaoEmprestimo;
import br.ufrn.sigaa.biblioteca.controle_estatistico.dominio.ConstantesRelatorioBiblioteca;

/**
*
* <p>DAO utilizado exclusivamente para o relatório de empréstimos por turno. </p>
* 
* @author jadson
*
*/
public class RelatorioEmprestimosPorTurnoDAO  extends GenericSigaaDAO{
	
	/**
	 * Retorna a quantidade de empréstimos e renovações prensencias ativos, agrupados por turno e por biblioteca através de um mapa.
	 * 
	 * @param bibliotecasID As bibliotecas que se deseja filtrar a busca. Se nulo ou vazio, considera todas as bibliotecas.
	 * @param turno O turno que se filtrar a busca. Se nulo considera todos os turnos.
	 * @param inicioPeriodo A data início para o filtro de período. É informação obrigatória.
	 * @param fimPeriodo A data fim para o filtro de período. É informação obrigatória.
	 * @return
	 * @throws DAOException
	 */
	public Map<String, Map<Integer, Integer>> findQtdEmprestimosPorTurno(
			Collection<Integer> bibliotecasID, Integer turno, Date inicioPeriodo,
			Date fimPeriodo) throws DAOException {
		Map<String, Map<Integer, Integer>> resultados = new TreeMap<String, Map<Integer, Integer>>();
		
		StringBuilder sqlEmprestimos = new StringBuilder("" +
			"SELECT " +
				"b.descricao as biblioteca, " +
				"e.data_emprestimo as data " +
			"FROM biblioteca.emprestimo e " +
			"INNER JOIN biblioteca.material_informacional mi ON (e.id_material = mi.id_material_informacional) " +
			"INNER JOIN biblioteca.biblioteca b ON (mi.id_biblioteca = b.id_biblioteca) " +
			"WHERE " + 
				"e.ativo = trueValue() ");
			
		StringBuilder sqlRenovacoes = new StringBuilder("" +
			"SELECT " +
				"b.descricao as biblioteca, " +
				"pe.data_cadastro as data " +
			"FROM biblioteca.prorrogacao_emprestimo pe " + 
			"INNER JOIN biblioteca.emprestimo e ON (e.id_emprestimo = pe.id_emprestimo) " +
			"INNER JOIN comum.registro_entrada re ON (re.id_entrada = pe.id_registro_cadastro) " +
			"INNER JOIN biblioteca.material_informacional mi ON (e.id_material = mi.id_material_informacional) " +
			"INNER JOIN biblioteca.biblioteca b ON (mi.id_biblioteca = b.id_biblioteca) " +
			"WHERE " + 
				"pe.tipo = " + TipoProrrogacaoEmprestimo.RENOVACAO + " AND " +
				"e.ativo = trueValue() AND " +
				"re.canal = '" + RegistroEntrada.CANAL_DESKTOP + "' ");
		
		Map<String, Object> parametros = new TreeMap<String, Object>();

		if (bibliotecasID != null && bibliotecasID.size() > 0) {
			sqlEmprestimos.append("AND mi.id_biblioteca IN (:bibliotecasID) ");
			sqlRenovacoes.append("AND mi.id_biblioteca IN (:bibliotecasID) ");
			
			parametros.put("bibliotecasID", bibliotecasID.toArray());
		}
		
		if (turno != ConstantesRelatorioBiblioteca.Turnos.TODOS.getValor()) {
			String horaInicioTurno;
			String horaFimTurno;

			if( turno == ConstantesRelatorioBiblioteca.Turnos.MANHA.getValor()){
				horaInicioTurno = "00:00:00.000";
				horaFimTurno = "11:59:59.999";
			}else{
				if( turno == ConstantesRelatorioBiblioteca.Turnos.TARDE.getValor()){
					horaInicioTurno = "12:00:00.000";
					horaFimTurno = "17:59:59.999";
				}else{
					if( turno == ConstantesRelatorioBiblioteca.Turnos.NOITE.getValor()){
						horaInicioTurno = "18:00:00.000";
						horaFimTurno = "23:59:59.999";
					}else{
						throw new IllegalArgumentException("Valor de turno inválido.");
					}
				}
			}
			
			//BDUtils
			sqlEmprestimos.append("AND (e.data_emprestimo >= (CAST(e.data_emprestimo AS DATE) + INTERVAL '" + horaInicioTurno + "') AND e.data_emprestimo < (CAST(e.data_emprestimo AS DATE) + INTERVAL '" + horaFimTurno + "')) ");
			sqlRenovacoes.append("AND (pe.data_cadastro >= (CAST(pe.data_cadastro AS DATE) + INTERVAL '" + horaInicioTurno + "') AND pe.data_cadastro < (CAST(pe.data_cadastro AS DATE) + INTERVAL '" + horaFimTurno + "')) ");
		}

		if (inicioPeriodo != null) {
			sqlEmprestimos.append("AND e.data_emprestimo >= :inicioPeriodo ");
			sqlRenovacoes.append("AND pe.data_cadastro >= :inicioPeriodo ");
			
			parametros.put("inicioPeriodo", inicioPeriodo);
		}

		if (fimPeriodo != null) {
			sqlEmprestimos.append("AND e.data_emprestimo <= :fimPeriodo ");
			sqlRenovacoes.append("AND pe.data_cadastro <= :fimPeriodo ");
			
			parametros.put("fimPeriodo", fimPeriodo);
		}

		try {
			Query query = getSession().createSQLQuery(sqlEmprestimos.toString() + "UNION ALL " + sqlRenovacoes.toString());

			for (String key : parametros.keySet()) {
				if (parametros.get(key) instanceof Object[]) {
					query.setParameterList(key, (Object[])parametros.get(key));
				}
				else {
					query.setParameter(key, parametros.get(key));
				}
			}
			
			@SuppressWarnings("unchecked")
			/**
			 * Retorna uma lista onde cada objeto é:
			 * 
			 *     Object[0] = biblioteca
			 *     Object[1] = data do empréstimos
			 */
			
			List<Object> preResultado = query.list();

			Object[] objArray = null;
			String nomeBiblioteca = null;
			Date dataEmprestimo = null;
			boolean isManha = false;
			boolean isTarde = false;
			Date horarioFimManha = CalendarUtils.getInstance(0, 0, 0, 12, 0).getTime();
			Date horarioFimTarde = CalendarUtils.getInstance(0, 0, 0, 18, 0).getTime();
			Date horarioFimNoite = CalendarUtils.getInstance(0, 0, 0, 0, 0).getTime();
			Calendar dataComparacao = Calendar.getInstance();
			
			for (Object obj : preResultado) {
				objArray = (Object[])obj;
				nomeBiblioteca = (String)objArray[0];
				dataEmprestimo = new Date(((Timestamp)objArray[1]).getTime());
				
				if (!resultados.containsKey(nomeBiblioteca)) {
					Map<Integer, Integer> bibliotecaMap = new TreeMap<Integer, Integer>();

					bibliotecaMap.put(ConstantesRelatorioBiblioteca.Turnos.MANHA.getValor(), 0);
					bibliotecaMap.put(ConstantesRelatorioBiblioteca.Turnos.TARDE.getValor(), 0);
					bibliotecaMap.put(ConstantesRelatorioBiblioteca.Turnos.NOITE.getValor(), 0);
					
					resultados.put(nomeBiblioteca, bibliotecaMap);
				}
				
				dataComparacao.setTime(dataEmprestimo);

				isManha = dataEmprestimo.compareTo(CalendarUtils.definirHorario(dataComparacao.getTime(), horarioFimNoite)) >= 0 && 
							dataEmprestimo.compareTo(CalendarUtils.definirHorario(dataComparacao.getTime(), horarioFimManha)) < 0;
				isTarde = dataEmprestimo.compareTo(CalendarUtils.definirHorario(dataComparacao.getTime(), horarioFimManha)) >= 0 && 
							dataEmprestimo.compareTo(CalendarUtils.definirHorario(dataComparacao.getTime(), horarioFimTarde)) < 0;

				if (isManha) {
					resultados.get(nomeBiblioteca).put(ConstantesRelatorioBiblioteca.Turnos.MANHA.getValor(), resultados.get(nomeBiblioteca).get(ConstantesRelatorioBiblioteca.Turnos.MANHA.getValor()) + 1);
				}
				else if (isTarde) {
					resultados.get(nomeBiblioteca).put(ConstantesRelatorioBiblioteca.Turnos.TARDE.getValor(), resultados.get(nomeBiblioteca).get(ConstantesRelatorioBiblioteca.Turnos.TARDE.getValor()) + 1);
				}
				else {
					resultados.get(nomeBiblioteca).put(ConstantesRelatorioBiblioteca.Turnos.NOITE.getValor(), resultados.get(nomeBiblioteca).get(ConstantesRelatorioBiblioteca.Turnos.NOITE.getValor()) + 1);
				}
			}
	
			return resultados;
		} catch (Exception ex) {
			throw new DAOException(ex);
		}
	}
}
