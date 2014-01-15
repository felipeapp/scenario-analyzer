/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '29/05/2007'
 *
 */
package br.ufrn.sigaa.arq.dao.graduacao;

import java.util.List;
import java.util.Map;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.ensino.dominio.GrauAcademico;

/**
 * Classe responsável pelas consultas associadas a Habilitação (Ênfase)
 * @author Eric Moura (Eriquim)
 *
 */
public class RelatorioHabilitacaoSqlDao extends AbstractRelatorioSqlDao {

	/**
	 * Método que retorna a Relação de ênfase -Curso+Modalidade+Ênfase
	 * @param curso
	 * @param modalidade
	 * @return
	 * @throws DAOException 
	 */
	public List<Map<String,Object>> findListaEnfase(Curso curso, GrauAcademico modalidade) throws DAOException{
		//sql de consulta
		StringBuilder sqlconsulta = new StringBuilder(" select u.sigla_academica as unidade_sigla, c.nome as curso_nome,t.sigla as turno, " +
				" ga.descricao as modalidade_nome, h.nome as habilitacao_nome " +
				" from unidade u, curso c, graduacao.matriz_curricular mc, " +
				" graduacao.habilitacao h, ensino.turno t, ensino.grau_academico ga " +
				" where u.id_unidade = c.id_unidade " +
				" and   c.id_curso = mc.id_curso " +
				" and   mc.id_turno = t.id_turno " +
				" and   mc.id_habilitacao = h.id_habilitacao " +
				" and   mc.id_grau_academico = ga.id_grau_academico ");

		if(curso !=null && curso.getId()!=0)
			sqlconsulta.append("     and c.id_curso = "+curso.getId());

		if(modalidade !=null && modalidade.getId()!=0)
			sqlconsulta.append(" and ga.id_grau_academico = "+modalidade.getId());

		sqlconsulta.append(" order by u.sigla_academica as unidade_sigla, c.nome as curso_nome,t.sigla as turno, " +
				" ga.descricao as modalidade_nome, h.nome as habilitacao_nome ");

		List<Map<String,Object>> result;

		try {
			result = executeSql(sqlconsulta.toString());
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOException(e);			
		}

		return result;
	}


}
