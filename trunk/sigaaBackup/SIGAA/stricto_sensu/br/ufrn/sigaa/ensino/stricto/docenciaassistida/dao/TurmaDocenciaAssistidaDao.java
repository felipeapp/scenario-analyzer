/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Criado em 16/09/2010
 *
 */
package br.ufrn.sigaa.ensino.stricto.docenciaassistida.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.HibernateException;
import org.hibernate.Query;

import br.ufrn.academico.dominio.TipoUnidadeAcademica;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.stricto.docenciaassistida.dominio.TurmaDocenciaAssistida;

/** Classe responsável por consultas específicas à {@link TurmaDocenciaAssistida Turma de Docência Assistida}.
 * 
 * @author Édipo Elder F. Melo
 *
 */
public class TurmaDocenciaAssistidaDao extends GenericSigaaDAO {
	
	/**
	 * Retorna uma coleção de turmas de docência assistida em que o discente
	 * está matriculado. 
	 * 
	 * @param idDiscente
	 * @param statusDocenciaAssitida
	 * @return
	 * @throws DAOException
	 * @throws HibernateException
	 */
	public Collection<TurmaDocenciaAssistida> findTurmasDocenciaAssistidaByDiscenteMatriculado(int idDiscente, int ano, int periodo, int... statusDocenciaAssitida) throws HibernateException, DAOException{
		StringBuilder hql = new StringBuilder("select distinct tda" +
				" from TurmaDocenciaAssistida tda" +
				" inner join tda.turma turmaDA," +
				" MatriculaComponente mc" +
				" inner join mc.turma turmaMC" +
				" where turmaDA.id = turmaMC.id" +
				" and turmaDA.ano = :ano" +
				" and turmaDA.periodo = :periodo" +
				" and mc.discente.id = :idDiscente" +
				" and mc.situacaoMatricula.id in " + UFRNUtils.gerarStringIn(SituacaoMatricula.getSituacoesMatriculadoOuConcluido()));
		if (statusDocenciaAssitida != null)
			hql.append(" and tda.planoDocenciaAssistida.status in " + UFRNUtils.gerarStringIn(statusDocenciaAssitida));
		Query q = getSession().createQuery(hql.toString());
		q.setInteger("ano", ano);
		q.setInteger("periodo", periodo);
		q.setInteger("idDiscente", idDiscente);
		@SuppressWarnings("unchecked")
		Collection<TurmaDocenciaAssistida> lista = q.list();
		if (lista == null)
			lista = new ArrayList<TurmaDocenciaAssistida>();
		return lista;
	}

	/**
	 * Retorna uma coleção de turmas do docente em que há bolsista de docência assistida. 
	 * 
	 * @param idDiscente
	 * @param statusDocenciaAssitida
	 * @return
	 * @throws DAOException
	 * @throws HibernateException
	 */
	public Collection<TurmaDocenciaAssistida> findTurmasDocenciaAssistidaByDocente(int idServidor, int ano, int periodo, int... statusDocenciaAssitida) throws HibernateException, DAOException{
		StringBuilder hql = new StringBuilder("select distinct tda" +
				" from TurmaDocenciaAssistida tda," +
				" MatriculaComponente mc," +
				" DocenteTurma dt" +
				" left outer join dt.docente docente" +
				" left outer join dt.docenteExterno docenteExterno" +
				" where tda.turma.id = mc.turma.id" +
				" and mc.turma.id = dt.turma.id" +
				" and mc.turma.ano = :ano" +
				" and mc.turma.periodo = :periodo" +
				" and (docente.id = :idServidor or docenteExterno.servidor.id = :idServidor)" +
				" and mc.situacaoMatricula.id in " + UFRNUtils.gerarStringIn(SituacaoMatricula.getSituacoesMatriculadoOuConcluido()));
		if (statusDocenciaAssitida != null)
			hql.append(" and tda.planoDocenciaAssistida.status in " + UFRNUtils.gerarStringIn(statusDocenciaAssitida));
		Query q = getSession().createQuery(hql.toString());
		q.setInteger("ano", ano);
		q.setInteger("periodo", periodo);
		q.setInteger("idServidor", idServidor);
		@SuppressWarnings("unchecked")
		Collection<TurmaDocenciaAssistida> lista = q.list();
		if (lista == null)
			lista = new ArrayList<TurmaDocenciaAssistida>();
		return lista;
	}
	
	
	/**
	 * Retorna uma coleção de turmas do docente externo em que há bolsista de docência assistida. 
	 * 
	 * @param idDiscente
	 * @param statusDocenciaAssitida
	 * @return
	 * @throws DAOException
	 * @throws HibernateException
	 */
	public Collection<TurmaDocenciaAssistida> findTurmasDocenciaAssistidaByDocenteExterno(int idDocenteExterno, int ano, int periodo, int... statusDocenciaAssitida) throws HibernateException, DAOException{
		StringBuilder hql = new StringBuilder("select distinct tda" +
				" from TurmaDocenciaAssistida tda," +
				" MatriculaComponente mc," +
				" DocenteTurma dt" +
				" inner join dt.docenteExterno docenteExterno" +
				" where tda.turma.id = mc.turma.id" +
				" and mc.turma.id = dt.turma.id" +
				" and mc.turma.ano = :ano" +
				" and mc.turma.periodo = :periodo" +
				" and docenteExterno.id = :idDocenteExterno" +
				" and mc.situacaoMatricula.id in " + UFRNUtils.gerarStringIn(SituacaoMatricula.getSituacoesMatriculadoOuConcluido()));
		if (statusDocenciaAssitida != null)
			hql.append(" and tda.planoDocenciaAssistida.status in " + UFRNUtils.gerarStringIn(statusDocenciaAssitida));
		Query q = getSession().createQuery(hql.toString());
		q.setInteger("ano", ano);
		q.setInteger("periodo", periodo);
		q.setInteger("idDocenteExterno", idDocenteExterno);
		@SuppressWarnings("unchecked")
		Collection<TurmaDocenciaAssistida> lista = q.list();
		if (lista == null)
			lista = new ArrayList<TurmaDocenciaAssistida>();
		return lista;
	}
	
	/**
	 * Retorna uma coleção de turmas atendidas pela docência assistida
	 * 
	 * @param idDiscente
	 * @param statusDocenciaAssitida
	 * @return
	 * @throws DAOException
	 * @throws HibernateException
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> findRelatorioByAnoPeriodo(int ano, int periodo) throws HibernateException, DAOException{
		
		StringBuilder hql = new StringBuilder("SELECT cc.codigo, dt.turma.codigo, u.nome, ccd.nome, pd.nome, pde.nome");

		hql.append(" FROM TurmaDocenciaAssistida tda, DocenteTurma dt INNER JOIN tda.componenteCurricular cc ");
		hql.append(" INNER JOIN cc.detalhes ccd INNER JOIN tda.planoDocenciaAssistida pda LEFT OUTER JOIN cc.unidade u ");
		hql.append(" LEFT OUTER JOIN dt.docente d LEFT JOIN d.pessoa pd ");
		hql.append(" LEFT OUTER JOIN dt.docenteExterno de LEFT JOIN de.pessoa pde ");
		
		hql.append(" WHERE tda.turma.id = dt.turma.id AND tda.turma.ano = :ano AND u.tipoAcademica = :tipoAcademica ");
		hql.append(" AND tda.turma.periodo = :periodo AND pda.ativo = trueValue() ");
		
		hql.append(" ORDER BY u.nome, ccd.nome, dt.turma.codigo, pd.nome, pde.nome ");
			
		Query q = getSession().createQuery(hql.toString());
		q.setInteger("ano", ano);
		q.setInteger("periodo", periodo);
		q.setInteger("tipoAcademica", TipoUnidadeAcademica.DEPARTAMENTO);
		
		List<Map<String, Object>> lista = new ArrayList<Map<String,Object>>();
		List<Object[]> itens = q.list();
		
		String docente = null;
		String codigoAnt = null;
		String departamentoAnt = null;
		
		Set<String> listaDocente = new HashSet<String>();
		List<Map<String, Object>> listaTurma = new ArrayList<Map<String,Object>>();
		Map<String, Object> mapa = new HashMap<String, Object>();
		Map<String, Object> mapaTurma = new HashMap<String, Object>();
		
		for (Object[] item : itens) {
			
			mapaTurma.put("codigoComponente", item[0]);
			mapaTurma.put("componente", item[3]);
		
			if(item[4] != null)
				docente = (String) item[4];
			else
				docente = (String) item[5];
			
			listaDocente.add(docente);
			mapaTurma.put("codigo",item[1]);
			mapaTurma.put("docentes",listaDocente); 	
			
			if( (codigoAnt != null && !codigoAnt.equals((String) item[0]+(String) item[1])) ||  codigoAnt == null){
				listaTurma.add(mapaTurma);
				mapaTurma = new HashMap<String, Object>();
				listaDocente = new HashSet<String>();
			}
			
			
			if( (departamentoAnt == null ) || ( departamentoAnt != null && !departamentoAnt.equals(item[2]) ) ){
				
				mapa.put("turmas", listaTurma);
				mapa.put("departamento", item[2]);
				lista.add(mapa);
				mapa = new HashMap<String, Object>();
				listaTurma = new ArrayList<Map<String,Object>>();
			
			}
			
			
			departamentoAnt = (String) item[2];
			codigoAnt = (String) item[0]+(String) item[1];	
		}
		
		return lista;
	}

}
