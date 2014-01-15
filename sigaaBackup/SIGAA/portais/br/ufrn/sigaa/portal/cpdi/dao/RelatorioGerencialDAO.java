package br.ufrn.sigaa.portal.cpdi.dao;

import static br.ufrn.arq.util.UFRNUtils.gerarStringIn;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.Query;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.academico.dominio.TipoUnidadeAcademica;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.rh.dominio.Ativo;
import br.ufrn.rh.dominio.Cargo;
import br.ufrn.rh.dominio.Categoria;
import br.ufrn.rh.dominio.Formacao;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.ensino.dominio.GrupoMovimentacaoAluno;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.latosensu.dominio.SituacaoProposta;
import br.ufrn.sigaa.ensino.stricto.dominio.BancaPos;
import br.ufrn.sigaa.pesquisa.dominio.StatusGrupoPesquisa;
import br.ufrn.sigaa.projetos.dominio.TipoSituacaoProjeto;

public class RelatorioGerencialDAO extends GenericSigaaDAO {

	/** Retorna a quantidade de discentes de graduação ativos e matriculados por centro.
	 * @param idCentro
	 * @param idDepartamento
	 * @param ano
	 * @param periodo
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public List<Map<String, Object>> findQtdAtivosMatriculadosByCentro
		(Integer idCentro, Integer ano, Integer periodo, Character[] niveis) throws DAOException {
		
		List<Map<String, Object>> resultado = new ArrayList<Map<String, Object>>();
		
		//JOIN's comuns AS SubQuery's dos discentes ativos e matriculados 
		String sqlJOINComumDiscente = "	SELECT COUNT(distinct d.id_discente) "
			+ " FROM discente d "
			+ " INNER JOIN curso c ON(c.id_curso = d.id_curso) "
			+ " LEFT JOIN ensino.movimentacao_aluno ma ON(ma.id_discente = d.id_discente)  "
			+ " LEFT JOIN ensino.tipo_movimentacao_aluno tma USING(id_tipo_movimentacao_aluno) ";
		
		//WHERE's comuns AS SubQuery's dos discentes ativos e matriculados 
		String sqlWHEREComumDiscente = " c.nivel IN %4$s AND d.nivel IN %4$s ";
		if( ano != null && periodo!=null){
			sqlWHEREComumDiscente +=
					" AND ( " +
					" 	(	ma.id_discente IS NOT NULL " +
					"		AND (" +
					"				( tma.grupo = '%7$s' AND ma.data_estorno IS NULL ";
			if( periodo == 2 ){
				sqlWHEREComumDiscente += " AND  ma.ano_ocorrencia > %2$s  ";
			}else{
				sqlWHEREComumDiscente += " AND ( ( ma.ano_ocorrencia = %2$s AND  ma.periodo_ocorrencia = 2 )" +
						" OR ma.ano_ocorrencia > %2$s  ) ";
			}
			
			sqlWHEREComumDiscente +=
					"				) " +
					"				OR  ( tma.grupo <> '%7$s' AND d.status IN  %1$s )" +
					"		)	" +
					" 	)OR ( " +
					" 		ma.id_discente IS  NULL	AND d.status IN  %1$s ";
			if( periodo == 2 ){
				sqlWHEREComumDiscente += " AND  d.ano_ingresso <= %2$s  ";
			}else{
				sqlWHEREComumDiscente += " AND ( ( d.ano_ingresso = %2$s AND  d.periodo_ingresso = 1 )" +
						" OR d.ano_ingresso < %2$s  ) ";
			}		
			sqlWHEREComumDiscente +=
					" 	) " +
					" )  ";
		}else{
			sqlWHEREComumDiscente += " AND d.status IN %1$s ";
		}
		if( !niveis[0].equals(NivelEnsino.GRADUACAO) ){
			sqlWHEREComumDiscente += " AND c.id_unidade = p.id_unidade ";
		}else{
			sqlWHEREComumDiscente += " AND c.id_unidade = u.id_unidade ";
		}
		
		//SubQuery que retorna o quantitativo de discentes ativos
		String sqlAtivos = " ( " + sqlJOINComumDiscente + " WHERE "	+ sqlWHEREComumDiscente + " ) AS ativos";
		
		//SubQuery que retorna o quantitativo de discentes matriculados
		String sqlMatriculados =  " ( " + sqlJOINComumDiscente 
				+ " INNER JOIN ensino.matricula_componente mc ON(mc.id_discente = d.id_discente) "
				+ " WHERE  mc.id_situacao_matricula IN %5$s  ";
		if( ano != null && periodo!=null){
			sqlMatriculados +=  " AND mc.ano = %2$s AND mc.periodo = %3$s ";
		}
		sqlMatriculados +=  " AND " + sqlWHEREComumDiscente + " ) AS matriculados ";
		
		//Query completa juntamente com AS SubQuery's
		String sql = " SELECT id_unidade, nome, SUM(ativos) AS ativos, SUM(matriculados) AS matriculados "
				+ " FROM ( "
				+ "			SELECT DISTINCT u.id_unidade, u.nome, " 
				+			sqlAtivos + ", "
				+ 			sqlMatriculados
				+ " 		FROM comum.unidade u ";
		if( !niveis[0].equals(NivelEnsino.GRADUACAO) ){
			sql	+= "		INNER JOIN comum.unidade p ON( p.unidade_responsavel = u.id_unidade) ";
		}
		sql	+= " 			WHERE u.id_unidade = %6$s ";
		sql	+= " 	) AS sub GROUP BY id_unidade, nome";
		
		Query q = getSession().createSQLQuery( String.format( sql, gerarStringIn( StatusDiscente.getAtivos() ), 
				ano, periodo, gerarStringIn( niveis ), gerarStringIn( SituacaoMatricula.getSituacoesAtivas() ), idCentro
				,GrupoMovimentacaoAluno.AFASTAMENTO_PERMANENTE ) );
	
		@SuppressWarnings("unchecked")
		List<Object[]> lista = q.list();
		for (Object obj[] : lista) {
			HashMap<String, Object> linha = new HashMap<String, Object>();
			linha.put("id", obj[0]);
			linha.put("nome", obj[1]);
			linha.put("ativos", obj[2]);
			linha.put("matriculados", obj[3]);
			resultado.add(linha);
		}
		
		return resultado;
	}
	
	/** Retorna a quantidade de departamentos e cursos de graduação de um centro.
	 * @param idCentro
	 * @param idDepartamento
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public List<Map<String, Object>> findQtdDepartamentoCursoGraduacaoByCentro(Integer idCentro, Integer ano, Integer periodo) throws HibernateException, DAOException {
		List<Map<String, Object>> resultado = new ArrayList<Map<String, Object>>();
		StringBuilder sql = new StringBuilder("SELECT"
				+ " centro.id_unidade,"
				+ " centro.codigo_unidade AS codigo,"
				+ " centro.nome,"
				+ " centro.sigla,"
				+ " ( SELECT COUNT(distinct c.id_curso) FROM curso c " 
				+ " 		WHERE c.id_unidade = centro.id_unidade ");
		if( ano != null && periodo != null ){		
			sql.append(generateRestrictionDataByAnoPeriodo("c.data_cadastro",null, ano, periodo));
		}				
		sql.append(" ) AS cursos,"
				+ " ( SELECT COUNT(distinct d.id_unidade) FROM comum.unidade d "
				+ "		WHERE d.unidade_responsavel = centro.id_unidade " 
				+ "		AND d.tipo_academica = :tipoDepartamento ");
		if( ano != null && periodo != null ){		
			sql.append(generateRestrictionDataByAnoPeriodo("d.data_criacao", "d.data_extincao", ano, periodo));
		}				
		sql.append("  ) AS departamentos"
				+ " FROM comum.unidade centro"
				+ " WHERE 1 = 1 ");
		
		if (idCentro != null) 
			sql.append(" AND centro.id_unidade = :idCentro");
		sql.append(" GROUP BY centro.id_unidade, centro.codigo_unidade, centro.nome, centro.sigla");
		sql.append(" ORDER BY centro.nome");
		
		Query q = getSession().createSQLQuery(sql.toString());
		
		if (idCentro != null) 
			q.setInteger("idCentro", idCentro);
		
		q.setInteger("tipoDepartamento", TipoUnidadeAcademica.DEPARTAMENTO);
		@SuppressWarnings("unchecked")
		List<Object[]> lista = q.list();
		for (Object obj[] : lista) {
			HashMap<String, Object> linha = new HashMap<String, Object>();
			linha.put("id_unidade", obj[0]);
			linha.put("codigo", obj[1]);
			linha.put("nome", obj[2]);
			linha.put("sigla", obj[3]);
			linha.put("cursos", obj[4]);
			linha.put("departamentos", obj[5]);
			resultado.add(linha);
		}
		return resultado;
	}
	
	/** Retorna a quantidade de cursos ativos por centro. 
	 * @param idCentro Limita a consulta ao ID do centro
	 * @param idDepartamento Limita a consulta ao ID do departamento
	 * @param ano Ano de referência
	 * @param periodo Período de referência
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> findQtdCursosPosByCentro(Integer idCentro, Integer ano, Integer periodo) 
			throws HibernateException, DAOException {
		List<Map<String, Object>> resultado = new ArrayList<Map<String, Object>>();
		StringBuilder sql = new StringBuilder(" SELECT"
				+ " centro.id_unidade,"
				+ " centro.codigo_unidade,"
				+ " centro.nome,"
				+ " centro.sigla,"
				+ " sum (CASE WHEN ( curso.nivel = :lato AND pcl.id_situacao_proposta = :situacaoProposta");
		if (ano != null && periodo != null){
			sql.append( generateRestrictionDataByAnoPeriodo("cl.data_inicio", "cl.data_fim", ano, periodo) );
		}	
		sql.append(" ) then 1 ELSE 0 END) AS cursos_especializacao,"
				+ " sum (CASE WHEN ( curso.nivel = :mestrado ");
		if (ano != null && periodo != null){
			sql.append( generateRestrictionDataByAnoPeriodo("curso.datadecreto", null, ano, periodo) );
		}			
		sql.append(" ) then 1 ELSE 0 END) AS cursos_mestrado,"
				+ " sum (CASE WHEN ( curso.nivel = :doutorado ");
		if (ano != null && periodo != null){
			sql.append( generateRestrictionDataByAnoPeriodo("curso.datadecreto", null, ano, periodo) );
		}			
		sql.append(" ) then 1 ELSE 0 END) AS cursos_doutorado"
				+ " FROM curso "
				+ " LEFT JOIN lato_sensu.curso_lato cl ON(curso.id_curso=cl.id_curso)"
				+ " LEFT JOIN lato_sensu.proposta_curso_lato pcl on (pcl.id_proposta = cl.id_proposta) "
				+ " INNER JOIN comum.unidade AS departamento using (id_unidade)"
				+ " INNER JOIN comum.unidade centro on (centro.id_unidade = departamento.unidade_responsavel)"
				+ " WHERE 1=1 ");
		if (idCentro != null) 
			sql.append(" AND centro.id_unidade = :idCentro");
		
		sql.append(" GROUP BY centro.id_unidade, centro.codigo_unidade, centro.nome, centro.sigla");
		sql.append(" ORDER BY nome");
		Query q = getSession().createSQLQuery(sql.toString());
		q.setCharacter("lato", NivelEnsino.LATO);
		q.setInteger("situacaoProposta", SituacaoProposta.ACEITA);
		q.setCharacter("mestrado", NivelEnsino.MESTRADO);
		q.setCharacter("doutorado", NivelEnsino.DOUTORADO);
		if (idCentro != null) 
			q.setInteger("idCentro", idCentro);

		List<Object[]> lista = q.list();
		for (Object obj[] : lista) {
			HashMap<String, Object> linha = new HashMap<String, Object>();
			linha.put("id_unidade", obj[0]);
			linha.put("codigo_unidade", obj[1]);
			linha.put("nome", obj[2]);
			linha.put("sigla", obj[3]);
			linha.put("cursos_especializacao", obj[4]);
			linha.put("cursos_mestrado", obj[5]);
			linha.put("cursos_doutorado", obj[6]);
			resultado.add(linha);
		}
		return resultado;
	}
	
	/** Retorna a quantidade de dissertações e teses defendidas por centro. 
	 * @param idCentro Limita a consulta ao ID do centro
	 * @param idDepartamento Limita a consulta ao ID do departamento
	 * @param ano Ano de referência
	 * @param periodo Período de referência
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public List<Map<String, Object>> findQtdDefesasTeses(Integer idCentro, Integer ano, Integer periodo) throws HibernateException, DAOException {
		List<Map<String, Object>> resultado = new ArrayList<Map<String, Object>>();
		StringBuilder sql = new StringBuilder("SELECT"
				+ " centro.id_unidade,"
				+ " centro.codigo_unidade AS codigo,"
				+ " centro.nome,"
				+ " centro.sigla,"
				+ " sum (CASE WHEN discente.nivel = :mestrado then 1 ELSE 0 END) AS dissertacoes,"
				+ " sum (CASE WHEN discente.nivel = :doutorado then 1 ELSE 0 END) AS teses"
				+ " FROM stricto_sensu.homologacao_trabalho_final"
				+ " INNER JOIN stricto_sensu.banca_pos on (id_banca_pos = id_banca)"
				+ " INNER JOIN stricto_sensu.dados_defesa using (id_dados_defesa)"
				+ " INNER JOIN discente using (id_discente)"
				+ " INNER JOIN curso using (id_curso)"
				+ " INNER JOIN comum.unidade programa on (curso.id_unidade = programa.id_unidade)"
				+ " INNER JOIN comum.unidade centro on (programa.unidade_responsavel = centro.id_unidade)"
				+ " WHERE banca_pos.tipobanca = :tipoBanca AND banca_pos.status = :statusBanca");
		if (ano != null && periodo != null) {
			sql.append( generateRestrictionDataByAnoPeriodo("banca_pos.data", null, ano, periodo) );
		}
		if (idCentro != null) 
			sql.append(" AND centro.id_unidade = :idCentro");
		
		sql.append(" GROUP BY centro.id_unidade, centro.codigo_unidade, centro.nome, centro.sigla");
		sql.append(" ORDER BY nome");
		
		Query q = getSession().createSQLQuery(sql.toString());
		if (idCentro != null) 
			q.setInteger("idCentro", idCentro);

		q.setCharacter("mestrado", NivelEnsino.MESTRADO);
		q.setCharacter("doutorado", NivelEnsino.DOUTORADO);
		q.setInteger("tipoBanca", BancaPos.BANCA_DEFESA);
		q.setInteger("statusBanca", BancaPos.ATIVO);

		@SuppressWarnings("unchecked")		
		List<Object[]> lista = q.list();
		
		for (Object obj[] : lista) {
			HashMap<String, Object> linha = new HashMap<String, Object>();
			linha.put("id_unidade", obj[0]);
			linha.put("codigo", obj[1]);
			linha.put("nome", obj[2]);
			linha.put("sigla", obj[3]);
			linha.put("dissertacoes", obj[4]);
			linha.put("teses", obj[5]);
			resultado.add(linha);
		}
		return resultado;
	}
	
	/** Retorna o total de discentes (ativos), docentes (ativos, não substitutos) e servidores técnico administrativo da instituição.
	 * @param idCentro
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public List<Map<String, Object>> findQtdPerfilByCentro(Integer idCentro, Integer ano, Integer periodo) throws HibernateException, DAOException {
		List<Map<String, Object>> resultado = new ArrayList<Map<String, Object>>();
		StringBuilder sql = new StringBuilder(
				" SELECT 'Discentes', COUNT( DISTINCT d.id_discente)"
				+ " FROM discente d" 
				+ " INNER JOIN curso using (id_curso)" 
				+ " INNER JOIN comum.unidade using (id_unidade)"
				+ " LEFT JOIN ensino.movimentacao_aluno ma ON(ma.id_discente = d.id_discente)  "
				+ " LEFT JOIN ensino.tipo_movimentacao_aluno tma USING(id_tipo_movimentacao_aluno) "
				+ " WHERE 1 = 1 ");
		
		if (ano != null && periodo != null){
		
			sql.append(" AND ( " +
					" 			(	ma.id_discente IS NOT NULL " +
					"				AND ( ( tma.grupo = '" + GrupoMovimentacaoAluno.AFASTAMENTO_PERMANENTE + "' AND ma.data_estorno IS NULL ");
			if( periodo == 2 ){
				sql.append( " 				AND  ma.ano_ocorrencia > " + ano + "  ");
			}else{
				sql.append( " 				AND ( ( ma.ano_ocorrencia = " + ano + 
							" 						AND  ma.periodo_ocorrencia = 2 ) OR ma.ano_ocorrencia > " + ano + "  ) ");
			}
				sql.append(	" 		) OR  ( tma.grupo <> '" + GrupoMovimentacaoAluno.AFASTAMENTO_PERMANENTE + 
											"' AND d.status IN   " + UFRNUtils.gerarStringIn(StatusDiscente.getAtivos()) + " )" +
					"				)  " +
					"	)OR ( ma.id_discente IS  NULL	AND d.status IN  " + UFRNUtils.gerarStringIn(StatusDiscente.getAtivos()) + 
					" 		 ");
			if( periodo == 2 ){
				sql.append( " AND  d.ano_ingresso <= "+ano+"  ");
			}else{
				sql.append( " AND ( ( d.ano_ingresso = "+ano+" AND  d.periodo_ingresso = 1 ) OR d.ano_ingresso < "+ano+"  ) ");
			}		
			sql.append(" 	)  " +
					"	)  ");
		
		}else{
			sql.append(" AND d.status IN " + UFRNUtils.gerarStringIn(StatusDiscente.getAtivos()) );
		}
			
		if (idCentro != null)
			sql.append(" AND (curso.id_unidade = :idCentro OR unidade.unidade_responsavel = :idCentro)");
		sql.append(" GROUP BY 1" 
				+ " union" 
				+ " SELECT 'Docentes', COUNT(*)"
				+ " FROM rh.servidor" 
				+ " INNER JOIN comum.unidade using (id_unidade)"
				+ " WHERE servidor.id_categoria = " + Categoria.DOCENTE
				+ " AND servidor.id_cargo not in " + UFRNUtils.gerarStringIn(Cargo.DOCENTE_SUBSTITUTO)
				+ " AND servidor.id_ativo = :ativo");
		if (ano != null && periodo != null)
			sql.append( generateRestrictionDataByAnoPeriodo("admissao", "data_desligamento", ano, periodo) );
		if (idCentro != null)
			sql.append(" AND unidade.unidade_responsavel = :idCentro");
		
		sql.append(" GROUP BY 1" 
				+ " union"
				+ " SELECT 'Técnicos Administrativos', COUNT(*)"
				+ " FROM rh.servidor" 
				+ " INNER JOIN comum.unidade using (id_unidade)"
				+ " WHERE servidor.id_categoria = "	+ Categoria.TECNICO_ADMINISTRATIVO
				+ " AND servidor.id_ativo = :ativo");
		if (ano != null && periodo != null)
			sql.append( generateRestrictionDataByAnoPeriodo("admissao", "data_desligamento", ano, periodo) );
		if (idCentro != null)
			sql.append(" AND unidade.unidade_responsavel = :idCentro");
		sql.append(" GROUP BY 1");
		
		Query q = getSession().createSQLQuery(sql.toString());
		
		q.setInteger("ativo", Ativo.SERVIDOR_ATIVO);
			
		if (idCentro != null)
			q.setInteger("idCentro", idCentro);
		@SuppressWarnings("unchecked")
		List<Object[]> lista = q.list();
		for (Object obj[] : lista) {
			HashMap<String, Object> linha = new HashMap<String, Object>();
			linha.put((String) obj[0], obj[1]);
			resultado.add(linha);
		}
		return resultado;
	}
	
	/** Retorna a quantidade de docentes, com titulação, por centro.  
	 * @param idCentro Limita a consulta ao ID do centro
	 * @param idDepartamento Limita a consulta ao ID do departamento
	 * @param ano Ano de referência
	 * @param periodo Período de referência
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public List<Map<String, Object>> findQtdDocenteByCentro(Integer idCentro, Integer ano, Integer periodo) throws HibernateException, DAOException {
		List<Map<String, Object>> resultado = new ArrayList<Map<String, Object>>();
		StringBuilder sql = new StringBuilder("SELECT"
				+ " centro.id_unidade,"
				+ " centro.codigo_unidade AS codigo,"
				+ " centro.nome,"
				+ " centro.sigla,"
				+ " SUM(CASE WHEN servidor.id_formacao = :graduacao OR servidor.id_formacao = :padrao then 1 ELSE 0 END) AS graduacao,"
				+ " SUM(CASE WHEN servidor.id_formacao = :especialista then 1 ELSE 0 END) AS especialista,"
				+ " SUM(CASE WHEN servidor.id_formacao = :mestre then 1 ELSE 0 END) AS mestre,"
				+ " SUM(CASE WHEN servidor.id_formacao = :doutor then 1 ELSE 0 END) AS doutor"
				+ " FROM comum.unidade departamento "
				+ " INNER JOIN comum.unidade centro ON(departamento.unidade_responsavel = centro.id_unidade)"
				+ " INNER JOIN rh.servidor ON(servidor.id_unidade = departamento.id_unidade)"
				+ " WHERE servidor.id_categoria = :categoria");
		sql.append(" AND servidor.id_cargo NOT IN "+UFRNUtils.gerarStringIn(Cargo.DOCENTE_SUBSTITUTO));
		sql.append(" AND servidor.id_ativo = :ativo");
		if (idCentro != null) 
			sql.append(" AND centro.id_unidade = :idCentro");
		if (ano != null && periodo != null)
			sql.append( generateRestrictionDataByAnoPeriodo("admissao","data_desligamento", ano, periodo) );
		sql.append(" GROUP BY centro.id_unidade, centro.codigo_unidade, centro.nome, centro.sigla");
		sql.append(" ORDER BY nome");
		
		Query q = getSession().createSQLQuery(sql.toString());
		
		if (idCentro != null) 
			q.setInteger("idCentro", idCentro);
		
		q.setInteger("ativo", Ativo.SERVIDOR_ATIVO);
		q.setInteger("padrao", Formacao.FORMACAO_PADRAO);
		q.setInteger("graduacao", Formacao.GRADUADO);
		q.setInteger("especialista", Formacao.ESPECIALISTA);
		q.setInteger("mestre", Formacao.MESTRE);
		q.setInteger("doutor", Formacao.DOUTOR);
		q.setInteger("categoria", Categoria.DOCENTE);
		@SuppressWarnings("unchecked")
		List<Object[]> lista = q.list();
		for (Object obj[] : lista) {
			HashMap<String, Object> linha = new HashMap<String, Object>();
			linha.put("id_unidade", obj[0]);
			linha.put("codigo", obj[1]);
			linha.put("nome", obj[2]);
			linha.put("sigla", obj[3]);
			linha.put("graduacao", obj[4]);
			linha.put("especialista", obj[5]);
			linha.put("mestrado", obj[6]);
			linha.put("doutorado", obj[7]);
			resultado.add(linha);
		}
		return resultado;
	}
	
	/** Retorna a quantidade de pesquisas ativas no centro/departamento informados.
	 * @param idCentro
	 * @param idDepartamento
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public List<Map<String, Object>> findQtdPesquisasAtivasByUnidade(Integer idCentro, Integer ano, Integer periodo) throws HibernateException, DAOException {
		List<Map<String, Object>> resultado = new ArrayList<Map<String, Object>>();
		StringBuilder sql = new StringBuilder("SELECT"
				+ " centro.id_unidade,"
				+ " centro.codigo_unidade AS codigo,"
				+ " centro.nome,"
				+ " centro.sigla,"
				+ " COUNT(id_grupo_pesquisa) AS pesquisas"
				+ " FROM comum.unidade departamento "
				+ " INNER JOIN comum.unidade centro ON(departamento.unidade_responsavel = centro.id_unidade)"
				+ " INNER JOIN rh.servidor ON(departamento.id_unidade = servidor.id_unidade)"
				+ " INNER JOIN pesquisa.grupo_pesquisa ON(id_coordenador = id_servidor)"
				+ " WHERE grupo_pesquisa.ativo = trueValue() AND grupo_pesquisa.status IN " 
				+ gerarStringIn( 	StatusGrupoPesquisa.getAllCertificados() ) );
		if (idCentro != null) 
			sql.append(" AND centro.id_unidade = :idCentro");
	
		if (ano != null && periodo != null) 
			sql.append( generateRestrictionDataByAnoPeriodo("grupo_pesquisa.data_criacao", null, ano, periodo) );
		
		sql.append(" GROUP BY centro.id_unidade, centro.codigo_unidade, centro.nome, centro.sigla");
		sql.append(" ORDER BY centro.nome");
		
		Query q = getSession().createSQLQuery(sql.toString());
		if (idCentro != null) 
			q.setInteger("idCentro", idCentro);
	
		@SuppressWarnings("unchecked")
		List<Object[]> lista = q.list();
		for (Object obj[] : lista) {
			HashMap<String, Object> linha = new HashMap<String, Object>();
			linha.put("id_unidade", obj[0]);
			linha.put("codigo", obj[1]);
			linha.put("nome", obj[2]);
			linha.put("sigla", obj[3]);
			linha.put("pesquisas", obj[4]);
			resultado.add(linha);
		}
		return resultado;
	}
	
	/** Retorna quantidade de projetos de extensão ativos de acordo com o centro ou departamento.
	 * @param idCentro
	 * @param idDepartamento
	 * @param ano
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */

	public List<Map<String, Object>> findQtdProjetosExtensaoAtivosByUnidade(Integer idCentro, Integer idDepartamento, Integer ano, Integer periodo) throws HibernateException, DAOException {
		List<Map<String, Object>> resultado = new ArrayList<Map<String, Object>>();
		
		
		Integer[] tiposSituacaoProjeto = new Integer[]{TipoSituacaoProjeto.EXTENSAO_EM_EXECUCAO, 
					TipoSituacaoProjeto.EXTENSAO_CONCLUIDO, 
					TipoSituacaoProjeto.PROJETO_BASE_APROVADO_COM_RECURSOS,
					TipoSituacaoProjeto.PROJETO_BASE_APROVADO_SEM_RECURSOS}; 
		
		StringBuilder sql = new StringBuilder("SELECT "
				+ " CAST (COUNT(DISTINCT atividade.id_projeto) AS INTEGER) AS projetos "
				+ " FROM extensao.atividade "
				+ " INNER JOIN extensao.tipo_atividade_extensao using (id_tipo_atividade_extensao)"
				+ " INNER JOIN projetos.projeto on (projeto.id_projeto = atividade.id_projeto)"
				+ " INNER JOIN comum.unidade u on (projeto.id_unidade = u.id_unidade)"
				+ " WHERE projeto.id_tipo_situacao_projeto IN " + gerarStringIn(tiposSituacaoProjeto));
		if (ano != null && periodo != null){
			sql.append( generateRestrictionDataByAnoPeriodo("data_inicio", "data_fim", ano, periodo) );
		}	
		if (idCentro != null) 
			sql.append(" AND (u.unidade_responsavel = :idUnidade or u.id_unidade = :idUnidade )");
		
		Query q = getSession().createSQLQuery(sql.toString());
		if (idCentro != null) 
			q.setInteger("idUnidade", idCentro);
	

		@SuppressWarnings("unchecked")
		Integer qtdProjetosExtensao = (Integer) q.list().iterator().next();
		HashMap<String, Object> linha = new HashMap<String, Object>();
		linha.put("projetos", qtdProjetosExtensao);
		resultado.add(linha);

		return resultado;
	}

	/**
	 * Retorna a condição da data ou intervalo de acordo com o ano e período passado.
	 * @param campoInicio
	 * @param campoFim
	 * @param ano
	 * @param periodo
	 * @return
	 */
	private String generateRestrictionDataByAnoPeriodo(String campoInicio, String campoFim, int ano, int periodo){
		//Define a data de comparação de acordo com o período informado.
		String data = (ano+1) + "-01-01";
		if( periodo == 1 )
			data = ano + "-08-01";
		//Considera também a data final na condição
		if( campoFim != null)
			return String.format(" AND (%1$s < '%3$s' OR %1$s IS NULL) AND (%2$s < '%3$s' OR %2$s IS NULL) ", campoInicio, campoFim, data);
		return String.format(" AND (%1$s < '%2$s' OR %1$s IS NULL) ", campoInicio, data);
    }

	
}
