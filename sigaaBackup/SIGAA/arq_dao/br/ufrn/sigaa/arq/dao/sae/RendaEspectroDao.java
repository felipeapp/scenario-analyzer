/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '18/03/2009'
 *
 */
package br.ufrn.sigaa.arq.dao.sae;

import java.util.List;
import java.util.Map;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.graduacao.AbstractRelatorioSqlDao;

/**
 * Dao responsável por realizar espectros de rendas
 * 
 * @author Jean Guerethes
 *
 */
public class RendaEspectroDao extends AbstractRelatorioSqlDao{

	/**
	 * Lista todos as renda dos alunos.
	 * @param gestora
	 * @return
	 * @throws DAOException
	 */
	public List<Map<String,Object>> findRendaEspectro() throws DAOException {
		StringBuilder sqlconsulta = new StringBuilder("select round(cast(( renda_familiar / 415 ) as numeric), 0) as Numero_Salarios, count(*) as Qtd_Salario"+
								" from  sae.situacao_socio_economica_discente"+
								" where ( renda_familiar / 415 ) >= 1"+
								" group by round(cast(( renda_familiar / 415 ) as numeric), 0)"+
								" union"+
								" select 0, count(*)"+
								" from sae.situacao_socio_economica_discente"+
								" where ( renda_familiar / 415 ) < 1 ");
		
		List<Map<String,Object>> result;

		try {
			result = executeSql(sqlconsulta.toString());
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOException(e);			
		}
		return result;
	}
	
	/**
	 * Retorna o espectro de renda com base nas informações fornecidas pelos alunos na matrícula
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Map contabilizarEspectroRendaMatricula(double salario) {
		String sql = "select count(case when renda_familiar > 30*"  + salario + " then 1 end) as a, " +
				" count(case when renda_familiar >= 15*" + salario + " and renda_familiar <= 30* "  + salario + " then 2 end) as b, " +
				" count(case when renda_familiar >= 5*"  + salario + " and renda_familiar < 15*"  + salario + " then 3 end) as c, " +
				" count(case when renda_familiar < 5*"   + salario + " then 4 end) as d, " +
				" count(renda_familiar) as total " +
				" from sae.situacao_socio_economica_discente s"+
				" inner join public.discente d on d.id_discente = s.id_discente"+
				" where d.nivel = '"+NivelEnsino.GRADUACAO+"'";
		
		return getJdbcTemplate().queryForMap(sql);
		
	}
}
