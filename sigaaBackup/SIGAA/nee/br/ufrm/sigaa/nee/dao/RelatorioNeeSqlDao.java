/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em 28/02/2011
 * Autor: Rafael Gomes
 */

package br.ufrm.sigaa.nee.dao;

import static br.ufrn.arq.util.UFRNUtils.gerarStringIn;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.comum.dominio.TipoNecessidadeEspecial;
import br.ufrn.sigaa.arq.dao.graduacao.AbstractRelatorioSqlDao;
import br.ufrn.sigaa.ensino.dominio.FormaIngresso;
import br.ufrn.sigaa.vestibular.dominio.ProcessoSeletivoVestibular;

/**
 * DAO responsável por consultas personalizadas de Alunos com NEE. 
 * Essas Consultas tem fins específicos para cada formato de relatório. 
 * 
 * @author Rafael Gomes
 *
 */
public class RelatorioNeeSqlDao extends AbstractRelatorioSqlDao{

	 /** Constante para o filtro ANO_INGRESSO utilizado nos relatórios. */ 
	 public static final int ANO_INGRESSO = 18;
	 /** Constante para o filtro ANO_INGRESSO utilizado nos relatórios. */ 
	 public static final int PROCESSO_SELETIVO = 19;
	 /** Constante para o filtro TIPO_NECESSIDADE utilizado nos relatórios. */ 
	 public static final int TIPO_NECESSIDADE = 20;
	
	 /**
	  * Método que retorna o Relatório de Alunos com Necessidades Educacionais Especiais por Processo Seletivo
	  * utilizando Ano Ingresso, Tipo de Necessidade Educacional Especial e Curso como filtros.
	  * @param processoSeletivo
	  * @param tipoNecessidade
	  * @param nomeCurso
	  * @param anoIngresso
	  * @param filtros
	  * @return
	  * @throws DAOException
	  */
	public List<Map<String,Object>> findAlunosNeePorProcessoSeletivo(ProcessoSeletivoVestibular processoSeletivo, 
			TipoNecessidadeEspecial tipoNecessidade, String nomeCurso, Integer anoIngresso, HashMap<Integer,Boolean> filtros) throws DAOException{
		//sql de consulta
		StringBuilder sqlconsulta = new StringBuilder(
				" SELECT d.matricula," +
				" p.nome," +
				" UPPER(tne.descricao) AS tipo_necessidade," +
				" c.nome AS curso, mun.nome AS municipio," +
				" ga.descricao AS grauAcademico, " +
				" hab.nome AS habilitacao," +
				" d.ano_ingresso AS anoIngresso," +
				" d.periodo_ingresso AS periodoIngresso," +
				" c.nome || ' - ' || mun.nome || ' - ' || ga.descricao || ' - ' || CASE WHEN  hab.nome  IS NOT NULL THEN hab.nome || ' - ' ELSE '' END || COALESCE(turn.sigla,'') AS descricao_curso" +
				" FROM discente d" +
				" JOIN comum.pessoa p USING (id_pessoa)" +
				" JOIN comum.tipo_necessidade_especial tne USING (id_tipo_necessidade_especial)" +
				" JOIN curso c ON c.id_curso = d.id_curso" +
				" JOIN comum.municipio mun ON mun.id_municipio = c.id_municipio" +
				" JOIN graduacao.curriculo curric ON curric.id_curriculo = d.id_curriculo" +
				" LEFT JOIN graduacao.matriz_curricular mcu ON mcu.id_matriz_curricular = curric.id_matriz" +
				" LEFT JOIN ensino.turno turn ON turn.id_turno = mcu.id_turno" +
				" LEFT JOIN ensino.grau_academico ga ON ga.id_grau_academico = mcu.id_grau_academico" +
				" LEFT JOIN graduacao.habilitacao hab ON hab.id_habilitacao = mcu.id_habilitacao" +
				" LEFT JOIN vestibular.convocacao_processo_seletivo_discente cpsd using(id_discente)" +
				" LEFT JOIN vestibular.convocacao_processo_seletivo cps using(id_convocacao_processo_seletivo)" +
				" LEFT JOIN vestibular.processo_seletivo ps using(id_processo_seletivo)" +
				" WHERE d.nivel = '" + NivelEnsino.GRADUACAO + "'" +
				" AND d.id_forma_ingresso = " + FormaIngresso.VESTIBULAR.getId() +
				" AND d.status IN "+ gerarStringIn( new int[]{StatusDiscente.ATIVO, StatusDiscente.CADASTRADO}) );
		        
		if( filtros.get(PROCESSO_SELETIVO) )
			sqlconsulta.append(" AND  ps.id_processo_seletivo = " + processoSeletivo.getId());
		
		if( filtros.get(TIPO_NECESSIDADE) )
			sqlconsulta.append(" AND  tne.id_tipo_necessidade_especial = " + tipoNecessidade.getId());
		
		if( filtros.get(CURSO) )
			sqlconsulta.append(" AND " + UFRNUtils.convertUtf8UpperLatin9("c.nome_ascii") + "like '" 
					+ UFRNUtils.trataAspasSimples(StringUtils.toAscii(nomeCurso.toUpperCase())) + "%'");
		
		if( filtros.get(ANO_INGRESSO) )
			sqlconsulta.append(" AND  d.ano_ingresso = " + anoIngresso);
		
		sqlconsulta.append(" ORDER BY c.nome, ga.descricao, hab.nome, turn.sigla, p.nome ASC" );

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
