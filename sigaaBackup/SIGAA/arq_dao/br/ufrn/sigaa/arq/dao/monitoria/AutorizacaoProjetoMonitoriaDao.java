/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '28/02/2007'
 *
 */
package br.ufrn.sigaa.arq.dao.monitoria;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.hibernate.Query;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.comum.dominio.UnidadeGeral;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.monitoria.dominio.AutorizacaoProjetoMonitoria;
import br.ufrn.sigaa.monitoria.dominio.EditalMonitoria;
import br.ufrn.sigaa.monitoria.dominio.ProjetoEnsino;
import br.ufrn.sigaa.projetos.dominio.TipoSituacaoProjeto;


/**
 * Dao para consultas sobre Autorização de projetos da monitoria
 * 
 * @author ilueny
 *
 */
public class AutorizacaoProjetoMonitoriaDao extends GenericSigaaDAO {

	/**
	 * Retorna todas as autorizações de projetos de acordo com a unidade informada
	 *
	 * @return Coleção de AutorizacaoProjetoMonitoria de acordo com a unidade
	 * @throws DAOException
	 *
	 * @author ilueny santos
	 */
	public Collection<AutorizacaoProjetoMonitoria> findByUnidades(Collection<UnidadeGeral> unidades)
			throws DAOException {

		try {
			ArrayList<AutorizacaoProjetoMonitoria> result = new ArrayList<AutorizacaoProjetoMonitoria>();
			
			if ((unidades != null) && (unidades.size() > 0)) {
			
				String hql = null;
				hql = "SELECT distinct au.id, au.dataAutorizacao, au.autorizado, p.id, p.projeto.ano, p.projeto.titulo  "
						+ " FROM AutorizacaoProjetoMonitoria au "
						+ " INNER JOIN au.projetoEnsino p "						 
						+ " WHERE au.unidade.id in (:idsUnidades) and au.ativo = trueValue() " +
							"AND p.projeto.situacaoProjeto.id != :situacaoRemovida";
				Query query = getSession().createQuery(hql);
				
				ArrayList<Integer> idsUnidades = new ArrayList<Integer>();
				for (UnidadeGeral u: unidades) 
					idsUnidades.add(u.getId());
				
				query.setParameterList("idsUnidades", idsUnidades);
				query.setParameter("situacaoRemovida", TipoSituacaoProjeto.MON_REMOVIDO);
	
				List lista = query.list();
	
				for (int a = 0; a < lista.size(); a++) {
					int col = 0;
					Object[] colunas = (Object[]) lista.get(a);
	
					AutorizacaoProjetoMonitoria au = new AutorizacaoProjetoMonitoria();
					au.setId((Integer) colunas[col++]);
					au.setDataAutorizacao((Date) colunas[col++]);
					au.setAutorizado((Boolean) colunas[col++]);
	
					ProjetoEnsino projEnsino = new ProjetoEnsino();
					projEnsino.setId((Integer) colunas[col++]);
					projEnsino.setAno((Integer) colunas[col++]);
					projEnsino.setTitulo((String) colunas[col++]);					
					au.setProjetoEnsino(projEnsino);
					result.add(au);
				}
			}
			
			return result;

		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}

	}
	
	
	

	
	/**
	 * Retorna todas as autorizações de projetos com 
	 * componentes curriculares das unidades informadas
	 *
	 * @return Colecão de AutorizacaoProjetoMonitoria do com componentes da unidade informada
	 * @throws DAOException
	 *
	 * @author ilueny santos
	 */
	public Collection<AutorizacaoProjetoMonitoria> findByUnidadeDoComponente(Collection<UnidadeGeral> unidades) throws DAOException {

		try {
			
			ArrayList<AutorizacaoProjetoMonitoria> result = new ArrayList<AutorizacaoProjetoMonitoria>();
			
			if ((unidades != null) && (unidades.size() > 0)) {
				
				String hql = null;
				hql = "SELECT distinct au.id, au.dataAutorizacao, au.autorizado, p.id, p.projeto.ano, p.projeto.titulo, edital "
						+ " FROM AutorizacaoProjetoMonitoria au "
						+ " INNER JOIN au.projetoEnsino p " 
						+ " LEFT JOIN p.editalMonitoria edital " 
						+ " WHERE au.unidade.id in (:idsUnidades) and au.ativo = trueValue()  " +
							"AND p.projeto.situacaoProjeto.id != :situacaoRemovida";
				Query query = getSession().createQuery(hql);			
				
				ArrayList<Integer> idsUnidades = new ArrayList<Integer>();
				for (UnidadeGeral u: unidades) 
					idsUnidades.add(new Integer(u.getId()));
				
				query.setParameterList("idsUnidades", idsUnidades);
				query.setParameter("situacaoRemovida", TipoSituacaoProjeto.MON_REMOVIDO);
	
				List lista = query.list();
	
				for (int a = 0; a < lista.size(); a++) {
					int col = 0;
					Object[] colunas = (Object[]) lista.get(a);
	
					AutorizacaoProjetoMonitoria au = new AutorizacaoProjetoMonitoria();
					au.setId((Integer) colunas[col++]);
					au.setDataAutorizacao((Date) colunas[col++]);
					au.setAutorizado((Boolean) colunas[col++]);
	
					ProjetoEnsino p = new ProjetoEnsino();
					p.setId((Integer) colunas[col++]);
					p.setAno((Integer) colunas[col++]);
					p.setTitulo((String) colunas[col++]);				
					p.setEditalMonitoria((EditalMonitoria) colunas[col++]);
					au.setProjetoEnsino(p);
					result.add(au);
				}
			}
			
			return result;

		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
		
	}
	
	

	
	/**
	 * Retorna total de autorizações dos departamentos pendentes para o projeto informado
	 *
	 * @param idProjeto
	 * @throws DAOException
	 * 
	 * @author ilueny
	 */
	public int totalAutorizacoesPendentes(int idProjeto) throws DAOException {

		String hql = " select count(*) from AutorizacaoProjetoMonitoria apm inner join apm.projetoEnsino pm " +
		" where (pm.id = :idProjeto) and (apm.dataAutorizacao is null) and (apm.ativo = trueValue())";

		Query query = getSession().createQuery(hql);
		query.setInteger( "idProjeto", idProjeto );

		return Integer.parseInt(query.uniqueResult().toString());
 		
	}

	/**
	 * Retorna total de autorizações reprovadas pelos departamentos para o projeto informado
	 *
	 * @param idProjeto
	 * @throws DAOException
	 * 
	 * @author ilueny
	 */
	public int totalReprovacoesPelosDepatamentos(int idProjeto) throws DAOException {

		String hql = " select count(*) from AutorizacaoProjetoMonitoria apm inner join apm.projetoEnsino pm " +
		" where (pm.id = :idProjeto) and (apm.dataAutorizacao is not null) and (autorizado = falseValue())";

		Query query = getSession().createQuery(hql);
		query.setInteger( "idProjeto", idProjeto );

		return Integer.parseInt(query.uniqueResult().toString());
 		
	}
	
	
	
	
	/**
	 * Autorizações do projeto com a unidade informada
	 * 
	 * @author ilueny
	 */
	public AutorizacaoProjetoMonitoria autorizacoesParaProjetoDaUnidade(int idProjeto, int idUnidade) throws DAOException {

		String hql = "select apm from AutorizacaoProjetoMonitoria apm inner join apm.projetoEnsino pm " +
		" where (pm.id = :idProjeto) and (apm.unidade.id = :idUnidade)  and (apm.ativo = trueValue())";

		Query query = getSession().createQuery(hql);
		query.setInteger( "idProjeto", idProjeto );
		query.setInteger( "idUnidade", idUnidade );

		return (AutorizacaoProjetoMonitoria)query.uniqueResult();
 		
	}

	
	/***
	 * Retorna o total de componentes no projeto com a unidade e projeto informado
	 * 
	 * @return
	 */
	public int totalComponentesNoProjetoComEssaUnidade(int idProjeto, int idUnidade)throws DAOException{

		String hql = " select count(*) from ComponenteCurricularMonitoria ccm inner join ccm.projetoEnsino pm " +
		" where (pm.id = :idProjeto) and (ccm.disciplina.unidade.id = :idUnidade)";

		Query query = getSession().createQuery(hql);
		query.setInteger( "idProjeto", idProjeto );
		query.setInteger( "idUnidade", idUnidade );

		return Integer.parseInt(query.uniqueResult().toString());
	}
	

	
	
	
}