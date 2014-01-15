/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '15/05/2007'
 *
 */
package br.ufrn.sigaa.arq.dao.graduacao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.rh.dominio.Ativo;
import br.ufrn.rh.dominio.Categoria;
import br.ufrn.sigaa.dominio.Unidade;

/**
 * Classe responsável por consultas específicas para Relatórios de Docentes 
 * @author ericm
 */

public class RelatorioDocenteSqlDao extends AbstractRelatorioSqlDao {


	/**
	 * Método que retorna da consulta a lista de docentes por departamentos e centros
	 * @param centro
	 * @param departamento
	 * @return
	 * @throws DAOException 
	 */
	
	public List<Map<String,Object>> findListaDocenteDepartamento(Unidade centro, Unidade departamento) throws DAOException{
		//sql de consulta
		StringBuilder sqlconsulta = new StringBuilder("select u.nome_capa as centro," +
				" s.siape as siape, p.nome as docente, c.denominacao " +
				" from rh.servidor s " +
				" inner join comum.unidade u on (u.id_unidade =  s.id_unidade)" +
				" inner join comum.pessoa p on ( s.id_pessoa = p.id_pessoa )" +
				" inner join rh.ativo a on (a.id_ativo = s.id_ativo)" +		
				" inner join rh.cargo c on (c.id = s.id_cargo) " +
				" where a.id_ativo  = " + Ativo.SERVIDOR_ATIVO +
				" and s.id_categoria = " + Categoria.DOCENTE );

		if(centro!=null && centro.getId()!=0 )
			sqlconsulta.append("		and u.id_unidade="+centro.getId());
		if(departamento!=null && departamento.getId()!=0 )
			sqlconsulta.append("		and u.id_unidade="+departamento.getId());
		sqlconsulta.append("		order by u.nome, p.nome, c.denominacao");

		ArrayList<Map<String, Object>> result = new ArrayList<Map<String,Object>>();
		
		try {
			result = (ArrayList<Map<String, Object>>) executeSql(sqlconsulta.toString());
		} catch (Exception e) {
			throw new DAOException(e);
		}

		return result;
	}

	/**
	 * Método que retorna uma lista de quantitativos de docentes por departamento
	 * @param departamento
	 * @return
	 * @throws DAOException 
	 */
	
	public List<Map<String,Object>> findQuantitativoDocenteDepartamento(Unidade departamento) throws DAOException{
		StringBuilder sqlconsulta = new StringBuilder("select depto.nome as depto, count (doc.siape) as qtd " +
				"from rh.servidor doc, comum.unidade depto " +
				"where doc.id_unidade =  depto.id_unidade " +
				"and doc.id_cargo in (60001,60002,60003,60011,60012,60013) "); //cargo de professor

		if(departamento!=null && departamento.getId()!=0 )
			sqlconsulta.append("		and depto.id_unidade="+departamento.getId());
		sqlconsulta.append("		group by depto.nome");
		sqlconsulta.append("		order by depto.nome");

		ArrayList<Map<String, Object>> result = new ArrayList<Map<String,Object>>();

		try {
			result = (ArrayList<Map<String, Object>>) executeSql(sqlconsulta.toString());
		} catch (Exception e) {
			throw new DAOException(e);
		}

		return result;
	}

	/**
	 * Método que retorna uma Lista de disciplinas ministradas por docentes num dado ano semestre
	 * @param departamento
	 * @return
	 * @throws DAOException 
	 */

	public List<Map<String,Object>> findDisciplinaDocenteAnoSemestre(Unidade departamento, int ano, int periodo) throws DAOException{
		StringBuilder sqlconsulta = new StringBuilder("select depto.nome as depto, doc.siape, p.nome as docente_nome, " +
				" dt.ch_dedicada_periodo as ch_docente_turma, t.ano, t.periodo, " +
				" cd.codigo as disciplina_codigo, cd.nome as disciplina,cd.cr_aula as credito_aula, " +
				" cd.cr_estagio as credito_estagio, cd.cr_laboratorio as credito_laboratorio, " +
				" cd.ch_total as ch_disciplina " +
				" from ensino.turma t, ensino.componente_curricular cc, " +
				" ensino.componente_curricular_detalhes cd, ensino.docente_turma dt, comum.unidade depto, " +
				" rh.servidor doc, comum.pessoa p " +
				" where p.id_pessoa = doc.id_pessoa " +
				" and   doc.id_unidade = depto.id_unidade " +
				" and   doc.id_servidor = dt.id_docente " +
				" and   dt.id_turma = t.id_turma " +
				" and   t.id_disciplina = cc.id_disciplina " +
				" and   cc.id_detalhe = cd.id_componente_detalhes "); //cargo de professor

		if(departamento!=null && departamento.getId()!=0 )
			sqlconsulta.append(" and depto.id_unidade="+departamento.getId());
		if(ano!=0)
			sqlconsulta.append(" and t.ano="+ano);
		if(periodo!=0)
			sqlconsulta.append(" and t.periodo="+periodo);

		sqlconsulta.append("order by t.ano, t.periodo, depto.nome, p.nome");

		List<Map<String,Object>> result = new ArrayList<Map<String,Object>>();

		try {
			result = executeSql(sqlconsulta.toString());
		} catch (Exception e) {
			throw new DAOException(e);
		}

		return result;
	}





}
