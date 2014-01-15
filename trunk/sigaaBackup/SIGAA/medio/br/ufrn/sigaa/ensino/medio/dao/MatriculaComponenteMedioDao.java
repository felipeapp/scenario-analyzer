/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em 01/09/2011
 * Autor: Rafael Gomes
 */

package br.ufrn.sigaa.ensino.medio.dao;

import static br.ufrn.arq.util.UFRNUtils.gerarStringIn;
import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.hibernate.Query;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.dao.Database;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.HibernateUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.ComponenteDetalhes;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.dominio.DocenteTurma;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.dominio.TipoComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.medio.dominio.CursoMedio;
import br.ufrn.sigaa.ensino.medio.dominio.MatriculaDiscenteSerie;
import br.ufrn.sigaa.ensino.medio.dominio.Serie;
import br.ufrn.sigaa.ensino.medio.dominio.SituacaoMatriculaSerie;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.DocenteExterno;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

/**
 * Classe de Dao com consultas da entidade MatriculaComponente para os casos de ensino médio.
 *  
 * @author Rafael Gomes
 *
 */
public class MatriculaComponenteMedioDao extends GenericSigaaDAO{

	/**
	 * Retorna todas as matrículas de discentes matrícula em série passada por parâmetro, 
	 * conforme as situações de matrícula informadas.
	 * @param discente
	 * @return
	 * @throws DAOException
	 */
	public Collection<MatriculaComponente> findMatriculasByMatriculaDiscenteSerie(MatriculaDiscenteSerie matriculaSerie, boolean otimizado, Collection<SituacaoMatricula> situacoes) throws DAOException{
		Collection<MatriculaDiscenteSerie> colMatSerie = new ArrayList<MatriculaDiscenteSerie>();
		colMatSerie.add(matriculaSerie);
		return findMatriculasByMatriculaDiscenteSerie(colMatSerie, otimizado, situacoes);
	}
	
	/**
	 * Busca todas as matrículas de um discente de uma determinada série, 
	 * conforme as situações de matrícula informadas.
	 *
	 * @param idDiscente
	 * @param otimizado
	 * @return
	 * @throws DAOException
	 */
	public Collection<MatriculaComponente> findMatriculasByMatriculaDiscenteSerie(DiscenteAdapter discente, 
			MatriculaDiscenteSerie matriculaDiscenteSerie, boolean otimizado, Integer... situacaoMatricula ) 
			throws DAOException {
		
		try {
			String projecao;
			StringBuffer hql = new StringBuffer();
			
			if (otimizado) {
				projecao = "mc.id, mc.turma.id, mc.turma.disciplina.id, mc.turma.disciplina.detalhes.nome, mc.turma.disciplina.codigo, mc.componente.id, mc.componente.detalhes.nome, mc.componente.codigo ";
			} else {
				projecao = "id, turma ";
			}
			hql.append("select " + projecao);
			hql.append(" from MatriculaComponente mc ");
			
			if (!otimizado) {
				hql.append(" left join fetch mc.turma.horarios ");
			}
			hql.append(" where mc.discente.id = :idDiscente and "
					 + " mc.situacaoMatricula.id "
					 + " in "+UFRNUtils.gerarStringIn(situacaoMatricula));
			hql.append(" and  mc.turma.id in ( "
						+ "	select tsa.turma.id from TurmaSerieAno tsa "
						+ " inner join tsa.turma t "
						+ "	inner join tsa.turmaSerie ts "
						+ "	where ts.id = :idTurmaSerie )");
			
			Query q = getSession().createQuery(hql.toString());
			q.setInteger("idDiscente", discente.getId());
			q.setInteger("idTurmaSerie", matriculaDiscenteSerie.getTurmaSerie().getId());
			
			if (!otimizado) {
				@SuppressWarnings("unchecked")
				Collection<MatriculaComponente> lista = q.list();
				return lista;
			} else {
				@SuppressWarnings("unchecked")
		        List <Object[]> rs = q.list();
		        Collection<MatriculaComponente> lista = HibernateUtils.parseTo(rs, projecao, MatriculaComponente.class, "mc");
		        return lista;
			}

		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}
	
	/**
	 * Busca todas as matrículas de discentes das séries passadas por parâmetro, 
	 * conforme as situações de matrícula informadas.
	 *
	 * @param idDiscente
	 * @param otimizado
	 * @return
	 * @throws DAOException
	 */
	public Collection<MatriculaComponente> findMatriculasByMatriculaDiscenteSerie(Collection<MatriculaDiscenteSerie> matriculasSerie, 
			boolean otimizado, Collection<SituacaoMatricula> situacoesMatricula ) throws DAOException {
		
		try {
			
			List<Integer> idsMatriculasSerie = new ArrayList<Integer>();
			for (MatriculaDiscenteSerie mat : matriculasSerie){
				idsMatriculasSerie.add(mat.getId());
			}
			
			String projecao;
			
			StringBuffer hql = new StringBuffer();
			
			hql.append("select ");
			if (otimizado) {
				projecao = "mc.id, t.id as mc.turma.id, cc.id as mc.turma.disciplina.id, ccd.nome as mc.turma.disciplina.detalhes.nome, " +
						"cc.codigo as mc.turma.disciplina.codigo, cc.id as mc.componente.id, ccd.nome as mc.componente.detalhes.nome, " +
						"cc.codigo as mc.componente.codigo, mc.situacaoMatricula.id";
				hql.append(HibernateUtils.removeAliasFromProjecao(projecao));
			} else {
				projecao = " mc.id, d.id, d.pessoa.id, d.pessoa.nome, d.pessoa.nomeAscii, " +
				 " mc.situacaoMatricula.id, mc.situacaoMatricula.descricao, mc.ano, "+ 
				 " mc.componente.id, ccd.nome, s.id, " +
				 " s.numero, s.descricao, mc.mediaFinal, mc.numeroFaltas, mc.recuperacao, " +
				 " s.cursoMedio.id, t.id ";
				hql.append(projecao);
			}
			hql.append(" from MatriculaComponente mc, MatriculaDiscenteSerie mds, TurmaSerieAno tsa, MatriculaComponenteSerie mcs " 
						+ " inner join mc.turma t "
						+ " inner join mds.turmaSerie ts " 
						+ " inner join mds.discenteMedio.discente d "
						+ " inner join mc.serie s "
						+ " inner join mc.componente cc " 
						+ " inner join cc.detalhes ccd " 
			);
			
			hql.append(" where 1 = 1 " );
			if (ValidatorUtil.isNotEmpty(situacoesMatricula)){
				hql.append(" and mc.situacaoMatricula.id in "+UFRNUtils.gerarStringIn(situacoesMatricula));
			}	
			
			hql.append(" and mds.id in "+UFRNUtils.gerarStringIn(idsMatriculasSerie));
			hql.append(" and mc.serie.id = ts.serie.id ");
			hql.append(" and mc.discente.id = d.id ");
			hql.append(" and tsa.turma.id = t.id");
			hql.append(" and tsa.turmaSerie.id = ts.id");
			hql.append(" and mcs.matriculaDiscenteSerie.id = mds.id");
			hql.append(" and mc.id = mcs.matriculaComponente.id");
			
			Query q = getSession().createQuery(hql.toString());
			
			if (!otimizado) {
				@SuppressWarnings("unchecked")
				Collection<Object[]> res = q.list();
				List<MatriculaComponente> matriculas = new ArrayList<MatriculaComponente>(0);
				if (res != null ) {
		        	for (Object[] reg : res) {
		        		int i = 0;
		        		MatriculaComponente mat = new MatriculaComponente((Integer) reg[i++]);
		        		
		        		mat.setDiscente(new Discente((Integer) reg[i++]));
		        		mat.getDiscente().setPessoa(new Pessoa((Integer) reg[i++]));
		        		mat.getDiscente().getPessoa().setNome((String) reg[i++]);
		        		mat.getDiscente().getPessoa().setNomeAscii((String) reg[i++]);
		        		
		        		mat.setSituacaoMatricula(new SituacaoMatricula());
		        		mat.getSituacaoMatricula().setId((Integer) reg[i++]);
		        		mat.getSituacaoMatricula().setDescricao((String) reg[i++]);
		        		mat.setAno((Short)reg[i++]);
		        		
		        		mat.setComponente(new ComponenteCurricular());
		        		mat.getComponente().setId((Integer) reg[i++]);
		        		
		        		mat.getComponente().setDetalhes(new ComponenteDetalhes());
		        		mat.getComponente().getDetalhes().setNome((String) reg[i++]);
		        		
		        		mat.setDetalhesComponente(mat.getComponente().getDetalhes());
		        		
		        		mat.setSerie(new Serie());
		        		mat.getSerie().setId((Integer) reg[i++]);
		        		mat.getSerie().setNumero((Integer) reg[i++]);
		        		mat.getSerie().setDescricao((String) reg[i++]);
		        		mat.setMediaFinal((Double) reg[i++]);
		        		
		        		Integer faltas = (Integer) reg[i++];
		        		if (faltas == null)
		        			faltas = 0;
		        		
		        		mat.setNumeroFaltas(faltas);
		        		mat.setRecuperacao((Double) reg[i++]);        		
		        		mat.getSerie().setCursoMedio(new CursoMedio((Integer) reg[i++]));
		        		
		        		Integer idTurma = (Integer) reg[i++];
		        		if ( idTurma != null && idTurma > 0)
		        			mat.setTurma(new Turma(idTurma));
		        		
		        		matriculas.add(mat);
					}
				}	
				return matriculas;
			} else {
				@SuppressWarnings("unchecked")
		        List <Object[]> rs = q.list();
		        Collection<MatriculaComponente> lista = HibernateUtils.parseTo(rs, projecao, MatriculaComponente.class, "mc");
		        return lista;
			}

		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}
	
	
	/**
	 * Retorna as matriculas de um discente de nível médio em um ano/período com o status MATRICULADO ou EM_ESPERA
	 * 
	 * @param discente
	 * @param ano
	 * @param periodo
	 * @return
	 * @throws DAOException
	 */
	public Collection<MatriculaComponente> findMatriculadasByDiscenteAnoPeriodo(DiscenteAdapter discente, Integer ano, Integer periodo, Collection<TipoComponenteCurricular> tipos) throws DAOException {
		Connection con = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		try {

			String sql = "select mc.id_matricula_componente,mc.id_turma, sit.id_situacao_matricula as ID_SITUACAO, sit.descricao as SITUACAO, ccd.nome as COMPONENTE_NOME , " +
			"cc.codigo as COMPONENTE_CODIGO, t.codigo as COD_TURMA, t.descricao_horario as DESCRICAO_HORARIO, t.local as LOCAL, " +
			"p1.nome as DOCENTE_NOME, dt.id_docente as ID_DOCENTE, ccd.ch_total as COMPONENTE_CH_TOTAL, " +
			"mc.ano as ANO, mc.periodo as PERIODO, t.id_turma_bloco, cc.id_tipo_componente, " +
			"dt.id_docente_externo as ID_DOCENTE_EXTERNO, p2.nome as DOCENTE_NOME_EXTERNO, t.data_inicio, t.data_fim, tcc.descricao as tipoComponenteCurricular, " +
			"serie.id_serie as ID_SERIE, serie.descricao as SERIE_DESCRICAO, serie.numero as SERIE_NUMERO, mds.dependencia as DEPENDENCIA " +
			"FROM discente d " +
			" join ensino.matricula_componente mc on (mc.id_discente=d.id_discente)" +
			" join ensino.componente_curricular cc on (mc.id_componente_curricular=cc.id_disciplina)" +
			" join ensino.tipo_componente_curricular tcc on ( cc.id_tipo_componente = tcc.id_tipo_disciplina )" + 
			" join ensino.situacao_matricula sit on (mc.id_situacao_matricula = sit.id_situacao_matricula)" +
			" join ensino.componente_curricular_detalhes ccd on (cc.id_detalhe=ccd.id_componente_detalhes)" +
			" left join ensino.turma t on (mc.id_turma = t.id_turma)" +
			" left join ensino.docente_turma dt on (t.id_turma = dt.id_turma)" +
			" left join rh.servidor s on (dt.id_docente=s.id_servidor)" +
			" left join comum.pessoa p1 on (s.id_pessoa = p1.id_pessoa) " +
			" left join ensino.docente_externo de on (dt.id_docente_externo = de.id_docente_externo) " +
			" left join comum.pessoa p2 on (de.id_pessoa = p2.id_pessoa) " +
			" inner join medio.serie serie on (serie.id_serie = mc.id_serie) " +  
			" inner join medio.turma_serie ts on (ts.id_serie = serie.id_serie) " +
			" inner join medio.turma_Serie_ano tsa on (tsa.id_turma_serie = ts.id_turma_serie and tsa.id_turma = t.id_turma) " +
			" inner join medio.matricula_discente_serie mds on (mds.id_turma_serie = ts.id_turma_serie and mds.id_discente = mc.id_discente) " +
			"WHERE d.id_discente=? and mds.id_situacao_matricula_serie IN (?, ?, ?, ?) " +
			" AND mc.id_situacao_matricula in " + gerarStringIn(SituacaoMatricula.getSituacoesAtivas());
			if( ano != null )
				sql += "AND mc.ano =? ";
			if( periodo != null )
				sql += "AND mc.periodo =? ";
			if (!isEmpty(tipos))
				sql += " AND cc.id_tipo_componente in " + gerarStringIn(tipos);
			
			
			sql += "ORDER BY mc.ano desc, serie.numero asc, t.codigo asc, ccd.nome asc, mc.data_cadastro asc";

			con = Database.getInstance().getSigaaConnection();
			st = con.prepareStatement(sql);

			st.setInt(1, discente.getId());
			st.setInt(2, SituacaoMatriculaSerie.MATRICULADO.getId());
			st.setInt(3, SituacaoMatriculaSerie.APROVADO.getId());
			st.setInt(4, SituacaoMatriculaSerie.REPROVADO.getId());
			st.setInt(5, SituacaoMatriculaSerie.TRANCADO.getId());
			if( ano != null )
				st.setInt(6, ano);
			if( periodo != null )
				st.setInt(7, periodo);

			rs = st.executeQuery();

			List<MatriculaComponente> matriculas = new ArrayList<MatriculaComponente>(0);
			int idAtual = 0;
			while(rs.next()) {
				MatriculaComponente mc = new MatriculaComponente();
				mc.setId(rs.getInt("id_matricula_componente"));
				if (idAtual == mc.getId() && idAtual > 0)
					mc = matriculas.get(matriculas.size()-1);
				else
					mc.setTurma(new Turma());
				DocenteTurma dt = new DocenteTurma();
				if (rs.getInt("ID_DOCENTE") > 0) {
					dt.getDocente().setId(rs.getInt("ID_DOCENTE"));
					dt.getDocente().getPessoa().setNome(rs.getString("DOCENTE_NOME"));
				} else {
					dt.setDocenteExterno(new DocenteExterno(rs.getInt("ID_DOCENTE_EXTERNO")));
					dt.getDocenteExterno().getPessoa().setNome(rs.getString("DOCENTE_NOME_EXTERNO"));
				}
				mc.getTurma().getDocentesTurmas().add(dt);
				if (idAtual == mc.getId() && idAtual > 0)
					continue;

				mc.setSituacaoMatricula(new SituacaoMatricula(rs.getInt("ID_SITUACAO")));
				mc.getSituacaoMatricula().setDescricao(rs.getString("SITUACAO"));
				mc.setAno((short)rs.getInt("ANO"));
				mc.setPeriodo((byte)rs.getInt("PERIODO"));

				ComponenteCurricular cc = new ComponenteCurricular();
				cc.setNome(rs.getString("COMPONENTE_NOME"));
				cc.getTipoComponente().setId(rs.getInt("id_tipo_componente"));
				cc.getTipoComponente().setDescricao(rs.getString("tipoComponenteCurricular"));
				cc.setCodigo(rs.getString("COMPONENTE_CODIGO"));
				cc.setChTotal(rs.getInt("COMPONENTE_CH_TOTAL"));
				cc.getDetalhes().setCodigo(rs.getString("COMPONENTE_CODIGO"));
				mc.getTurma().setDisciplina(cc);
				mc.setComponente(cc);
				mc.setDetalhesComponente(cc.getDetalhes());

				mc.getTurma().setId(rs.getInt("ID_TURMA"));
				mc.getTurma().setCodigo(rs.getString("COD_TURMA"));
				mc.getTurma().setDescricaoHorario(rs.getString("DESCRICAO_HORARIO"));
				mc.getTurma().setLocal(rs.getString("LOCAL"));
				mc.getTurma().setDataInicio(rs.getDate("data_inicio"));
				mc.getTurma().setDataFim(rs.getDate("data_fim"));
				mc.getTurma().setDisciplina(cc);
				
				if ( rs.getInt("ID_SERIE") > 0 ){
					mc.setSerie(new Serie());
					mc.getSerie().setId(rs.getInt("ID_SERIE"));
					mc.getSerie().setDescricao(rs.getString("SERIE_DESCRICAO"));
					mc.getSerie().setNumero(rs.getInt("SERIE_NUMERO"));
				}
				
				mc.setDependencia(rs.getBoolean("DEPENDENCIA"));
				
				idAtual = mc.getId();
				matriculas.add(mc);
			}

			return matriculas;
		} catch(Exception e) {
			throw new DAOException(e);
		} finally {
			closeResultSet(rs);
			closeStatement(st);
			Database.getInstance().close(con);
		}
	}
	
	/**
	 * Busca todas as matrícula de ensino médio de um discente
	 * para a turma virtual.
	 * @param tarefa
	 * @return
	 * @throws DAOException
	 */
	public List<MatriculaComponente> findAllMatriculasByDiscente(DiscenteAdapter discente) throws DAOException {
		try {
		
			Query  q = getSession().createSQLQuery(
					" select  m.id_matricula_componente , m.ano , m.tipo_integralizacao , m.id_situacao_matricula , sm.descricao , " +
					" c.id_disciplina , c.codigo, cd.id_componente_detalhes , cd.nome , cd.ch_total , s.descricao as descSerie, s.numero , " +
					" s.id_serie , t.id_turma , t.codigo as tcod, t.observacao " +
					" from ensino.matricula_componente m "+
					" join ensino.situacao_matricula sm on m.id_situacao_matricula = sm.id_situacao_matricula "+
					" join ensino.componente_curricular c on c.id_disciplina = m.id_componente_curricular "+
					" join ensino.componente_curricular_detalhes cd on c.id_detalhe = cd.id_componente_detalhes "+
					" left join ensino.turma t on t.id_turma = m.id_turma " +
					" join medio.serie s on m.id_serie = s.id_serie "+
					" where m.id_discente = "+discente.getId()+" and c.nivel = '"+NivelEnsino.MEDIO+"' "+
					" order by s.descricao, s.numero desc, m.ano desc, cd.nome, sm.descricao" 
					);

			List<MatriculaComponente> matriculas = new ArrayList<MatriculaComponente>();
			
			@SuppressWarnings("unchecked")
			List<Object[]> result = q.list();
			
			if ( result != null )
			{	
				for (Object[] linha : result) {
					Integer i = 0;
					MatriculaComponente m = new MatriculaComponente();
					m.setId((Integer) linha[i++]);
					m.setAno((Short) linha[i++]);
					m.setTipoIntegralizacao((String) linha[i++]);
					SituacaoMatricula sm = new SituacaoMatricula();
					sm.setId((Integer) linha[i++]);
					sm.setDescricao((String) linha[i++]);
					m.setSituacaoMatricula(sm);
					ComponenteCurricular c = new ComponenteCurricular();
					c.setId((Integer) linha[i++]);
					c.setCodigo((String) linha[i++]);
					ComponenteDetalhes cd = new ComponenteDetalhes();
					cd.setId((Integer) linha[i++]);
					cd.setNome((String) linha[i++]);
					cd.setChTotal((Short) linha[i++]);
					c.setDetalhes(cd);
					m.setComponente(c);
					Serie s = new Serie();
					s.setDescricao((String) linha[i++]);
					s.setNumero((Integer) linha[i++]);
					s.setId((Integer) linha[i++]);
					m.setSerie(s);
					Integer idTurma = (Integer) linha[i++];
					Turma t = new Turma();
					if (idTurma != null){
						t.setId(idTurma);
						t.setCodigo((String) linha[i++]);
						t.setObservacao((String) linha[i++]);
					}	
					m.setTurma(t);
					matriculas.add(m);
				}
				
				return matriculas;
			}	
		
			return null;
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}
	
}
