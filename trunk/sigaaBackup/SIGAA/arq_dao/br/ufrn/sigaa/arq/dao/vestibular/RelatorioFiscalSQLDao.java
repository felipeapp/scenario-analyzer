/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Created on '13/05/2008'
 *
 */
package br.ufrn.sigaa.arq.dao.vestibular;

import java.util.List;
import java.util.Map;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.graduacao.AbstractRelatorioSqlDao;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.vestibular.dominio.ProcessoSeletivoVestibular;

/**
 * Class que implementa consultas para gera��o de relat�rios relativos � fiscais
 *
 * @author �dipo Elder F. de Melo
 *
 */
public class RelatorioFiscalSQLDao extends AbstractRelatorioSqlDao {

	/**
	 * M�todo que retorna o Lista de Inscri��o de Fiscal
	 *
	 * @param centro
	 * @param departamento
	 * @return
	 * @throws DAOException 
	 */
	public List<Map<String, Object>> findListaInscricaoFiscalPorCurso(
			ProcessoSeletivoVestibular processoSeletivoVestibular, Curso curso) throws DAOException {
		// sql de consulta para discente
		StringBuilder sqlconsulta = new StringBuilder(
				"select"
						+ " pessoa.nome as nome,"
						+ " curso.nome as curso"
						+ " from vestibular.inscricao_fiscal inner join vestibular.processo_seletivo using (id_processo_seletivo_vestibular)"
						+ " inner join comum.pessoa using (id_pessoa)"
						+ " inner join discente using (id_discente)"
						+ " inner join curso using (id_curso)"
						+ " where processo_seletivo.id_processo_seletivo = " + processoSeletivoVestibular.getId());

		if (curso != null && curso.getId() != 0)
			sqlconsulta.append(" curso.id = " + curso.getId());
		sqlconsulta
				.append(" order by curso.nome, pessoa.nome");

		List<Map<String, Object>> result;

		try {
			result = executeSql(sqlconsulta.toString());
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOException(e);			
		}

		return result;
	}

}
