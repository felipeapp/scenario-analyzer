/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '08/03/2007'
 *
 */
package br.ufrn.sigaa.arq.dao.monitoria;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Projections;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.LimiteResultadosException;
import br.ufrn.arq.util.HibernateUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.monitoria.dominio.EquipeDocente;
import br.ufrn.sigaa.monitoria.dominio.Orientacao;
import br.ufrn.sigaa.projetos.dominio.TipoSituacaoProjeto;

public class EquipeDocenteDao extends GenericSigaaDAO {

	private static final long LIMITE_RESULTADOS = 1000;
	
	/**
	 * Retorna o EquipeDocente do servidor informado no projeto informado
	 *
	 * @param idServidor Servidor
	 * @param idProjeto Projeto de Monitoria
	 * @throws DAOException
	 * 
	 * @author ilueny
	 */
	public EquipeDocente findByServidorProjeto(int idServidor, int idProjeto) throws DAOException {

		if ((idServidor > 0) && (idProjeto > 0)) {

			Criteria c = getSession().createCriteria(EquipeDocente.class);
			c.createCriteria("servidor").add(Expression.eq("id", idServidor));
			c.createCriteria("projetoEnsino").add(Expression.eq("id", idProjeto));
			
			c.setMaxResults(1);
			return (EquipeDocente)c.uniqueResult();
			
		}else{
			return null;
		}

	}

	
	/**
	 * Retorna total de componentes curriculares associados ao EquipeDocente
	 *
	 * @param idEquipeDocente
	 * @throws DAOException
	 * 
	 * @author ilueny
	 */
	public int totalComponentesAssociados(int idEquipeDocente) throws DAOException {

		String hql = " select count(*) from EquipeDocenteComponente edc inner join edc.equipeDocente ed " +
		" where ed.id = :idEquipeDocente";

		Query query = getSession().createQuery(hql);
		query.setInteger( "idEquipeDocente", idEquipeDocente );

		return Integer.parseInt(query.uniqueResult().toString());
 		
	}

	
	
	/**
	 * Retorna lista de EquipeDocente do projeto informado que estão 
	 * sem componentes curriculares associados.
	 *
	 * @param idEquipeDocente
	 * @throws DAOException
	 * 
	 * @author ilueny
	 */
	@SuppressWarnings("unchecked")
	public Collection<EquipeDocente> findByEquipeDocenteSemComponentes(int idProjeto) throws DAOException {
		String hql = " select id from EquipeDocente ed " +
		" where ed.projetoEnsino.id = :idProjeto and ed.docentesComponentes is empty ";
		Query query = getSession().createQuery(hql);
		query.setInteger( "idProjeto", idProjeto);
		return HibernateUtils.parseTo(query.list(), "id", EquipeDocente.class); 		
	}
	

	
	
	/**
	 * Retorna lista de Orientações ativas do docente
	 *
	 * @param idEquipeDocente
	 * @throws DAOException
	 * 
	 * @author ilueny
	 */
	@SuppressWarnings("unchecked")
	public Collection<Orientacao> findByOrientacoesAtivas(int idEquipeDocente) throws DAOException {

		Criteria c = getSession().createCriteria(Orientacao.class);		
			c.add(Expression.isNotNull("dataInicio"));			
			c.add(Expression.isNotNull("dataFim"));
			c.add(Expression.ge("dataFim", new Date()));
			c.add(Expression.eq("ativo", true)); //orientação  não foi excluída		
			c.createCriteria("equipeDocente").add(Expression.eq("id", idEquipeDocente));
			c.setProjection(Projections.property("id"));
			
		return c.list();
 		
	}

	
	
	/**
	 * Retorna todos os EquipeDocente do projeto passado por parâmetro.
	 * o parâmetro ativo é opcional e define se aquele EquipeDocente deve estar ativo ou não
	 * 
	 * @param idProjeto id do projeto
	 * @param ativo se o membro está ativo ou não.
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<EquipeDocente> findByProjeto(int idProjeto, Boolean ativo) throws DAOException {

		String hql = " select docente from EquipeDocente docente inner join docente.projetoEnsino projeto " +
				"where projeto.id = :idProjeto ";
		
		if( ativo != null )
			hql += " and docente.ativo = :ativo ";
		
		hql += " order by docente";

		Query query = getSession().createQuery(hql);
		
		query.setInteger( "idProjeto", idProjeto );
		if( ativo != null )
			query.setBoolean("ativo", ativo);

		return query.list();
 		
	}	

	
	
	/**
	 * Método para buscar os docentes de projetos de monitoria de acordo com
     * uma série de filtros opcionais
     *
	 * @param idProjeto id do projeto
	 * @param idOrientador id do servidor
	 * @param idSituacaoMonitor id da situação do DiscenteMonitoria
	 * @param tipoMonitoria tipo de vinculo com a monitoria
	 * @param anoProjeto - usado para o Prodocente
	 * @param paginacao
	 * @return
	 * @throws DAOException
	 */
	public Collection<EquipeDocente> filter(
	    	String tituloProjeto,
	    	Integer idOrientador,
	    	Integer anoProjeto) throws DAOException{

    	try {

    		StringBuilder hqlCount = new StringBuilder();
    		hqlCount.append( "  SELECT " +
    						 "  count(distinct ed.id) FROM EquipeDocente ed " +
    						 "  LEFT OUTER JOIN ed.orientacoes as orientacoes " );
			hqlCount.append( "  WHERE 1 = 1 " );


    		StringBuilder hqlConsulta = new StringBuilder();
    		hqlConsulta.append( " SELECT distinct ed.id, ed.dataEntradaProjeto, ed.dataSaidaProjeto, " +
    				"ed.servidor.siape, ed.servidor.pessoa.nome, ed.projetoEnsino.projeto.ano, ed.projetoEnsino.projeto.titulo, ed.ativo  " +
    							" FROM EquipeDocente ed " +
    							" LEFT OUTER JOIN ed.orientacoes as orientacoes " );
    		hqlConsulta.append( " WHERE 1 = 1 " );

			StringBuilder hqlFiltros = new StringBuilder();
			// Filtros para a busca
			if( tituloProjeto != null ){
				hqlFiltros.append( " AND "
						    + UFRNUtils.toAsciiUpperUTF8("ed.projetoEnsino.projeto.titulo") + " like "
						    + UFRNUtils.toAsciiUTF8(":tituloProjeto"));
			}
			
			if( idOrientador != null ){
				hqlFiltros.append( " AND orientacoes.equipeDocente.servidor.id = :idOrientador " );
			}
			if ( anoProjeto != null ) {
				hqlFiltros.append( " AND ed.projetoEnsino.projeto.ano = :anoProjeto " );
			}

			//não mostra os excluídos ...
			hqlFiltros.append( " AND ed.excluido = :excluido " );



			hqlCount.append( hqlFiltros.toString() );
			hqlConsulta.append( hqlFiltros.toString() );

			// Criando consulta
			Query queryCount = getSession().createQuery( hqlCount.toString() );
			Query queryConsulta = getSession().createQuery( hqlConsulta.toString() );

			
			//não mostrar nem conta com os excluídos
			queryCount.setBoolean("excluido", Boolean.FALSE);
			queryConsulta.setBoolean("excluido", Boolean.FALSE);
			

			
			// Populando os valores dos filtros
			if( tituloProjeto != null ){
				queryCount.setString("tituloProjeto", tituloProjeto.toUpperCase());
				queryConsulta.setString("tituloProjeto", "%"+tituloProjeto.toUpperCase()+"%");
			}
					
			
			if( idOrientador != null ){
				queryCount.setInteger("idOrientador", idOrientador);
				queryConsulta.setInteger("idOrientador", idOrientador);
			}

			if ( anoProjeto != null ) {
				queryCount.setInteger("anoProjeto", anoProjeto);
				queryConsulta.setInteger("anoProjeto", anoProjeto);
			}

			Long total = (Long) queryCount.uniqueResult();
			if( total > LIMITE_RESULTADOS ) throw new
			  LimiteResultadosException("A consulta retornou " + total + " resultados. Por favor, restrinja mais a busca.");
			@SuppressWarnings("unchecked")
			List<Object> lista = queryConsulta.list();

			ArrayList<EquipeDocente> result = new ArrayList<EquipeDocente>();
			for (int a = 0; a < lista.size(); a++) {
				int col = 0;
				Object[] colunas = (Object[]) lista.get(a);
				EquipeDocente ed = new EquipeDocente();
				ed.setId((Integer) colunas[col++]);
				ed.setDataEntradaProjeto((Date) colunas[col++]);
				ed.setDataSaidaProjeto((Date) colunas[col++]);
				ed.getServidor().setSiape((Integer) colunas[col++]);
				ed.getServidor().getPessoa().setNome((String) colunas[col++]);
				ed.getProjetoEnsino().setAno((Integer) colunas[col++]);
				ed.getProjetoEnsino().setTitulo((String) colunas[col++]);
				ed.setAtivo((Boolean) colunas[col++]);
				result.add(ed);
			}
			return result;
			
			

    	}catch (Exception ex) {
            throw new DAOException(ex.getMessage(), ex);
        }
    }

	/**
	 * Retorna Lista de EquipeDocente onde a pessoa informada 
	 * participa ou participou de algum projeto válido
	 * (aprovado pela comissão de extensão).
	 * 
	 * @param servidor 
	 * @param funcao
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<EquipeDocente> findByPessoa(int idPessoa) throws DAOException {

		try {

			String hql = "select eq from EquipeDocente eq " +
						" inner join eq.projetoEnsino p " +
						" inner join eq.servidor s " +
						" inner join s.pessoa pe " +
						" inner join p.projeto pj " +
						" inner join pj.situacaoProjeto sit " +
						" where pe.id = :idPessoa and eq.ativo = trueValue() " +							
						" and pj.ativo = trueValue() " +
						" and pj.situacaoProjeto.id in (:situacoes) " + 
						" order by p.id";

			Query query = getSession().createQuery(hql);
			query.setInteger( "idPessoa", idPessoa );
			query.setParameterList( "situacoes", TipoSituacaoProjeto.MON_GRUPO_ATIVO );
			

			return query.list();

		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}

	}
	
	
	/**
	 * Retorna Lista de EquipeDocente onde a pessoa informada 
	 * participa ou participou de algum projeto válido
	 * (aprovado pela comissão de extensão).
	 * 
	 * @param servidor 
	 * @param funcao
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<EquipeDocente> findByEquipeDocente(int idProjetoEnsino) throws DAOException {

		try {

			String hql = "select eq.id, eq.dataInicioCoordenador, eq.dataFimCoordenador," +
					" eq.dataEntradaProjeto, eq.dataSaidaProjeto, eq.ativo, eq.coordenador," +
					" s.id, s.siape, pe.id, pe.nome from EquipeDocente eq " +
						"inner join eq.projetoEnsino p " +
						"inner join eq.servidor s " +
						"inner join s.pessoa pe " +
						"inner join p.projeto pj " +
						"inner join pj.situacaoProjeto sit " +
							"where p.id = :idProjetoEnsino and eq.ativo = trueValue() " +							
							"and pj.ativo = trueValue() order by p.id";

			Query query = getSession().createQuery(hql);
			query.setInteger( "idProjetoEnsino", idProjetoEnsino );
			

			List<Object> lista = query.list();

			ArrayList<EquipeDocente> result = new ArrayList<EquipeDocente>();
			for (int a = 0; a < lista.size(); a++) {
				int col = 0;
				Object[] colunas = (Object[]) lista.get(a);
				EquipeDocente ed = new EquipeDocente();
				ed.setId((Integer) colunas[col++]);
				ed.setDataInicioCoordenador((Date) colunas[col++]);
				ed.setDataFimCoordenador((Date) colunas[col++]);
				ed.setDataEntradaProjeto((Date) colunas[col++]);
				ed.setDataSaidaProjeto((Date) colunas[col++]);
				ed.setAtivo((Boolean) colunas[col++]);
				ed.setCoordenador((Boolean) colunas[col++]);
				ed.getServidor().setId((Integer) colunas[col++]);
				ed.getServidor().setSiape((Integer) colunas[col++]);
				ed.getServidor().getPessoa().setId((Integer) colunas[col++]);
				ed.getServidor().getPessoa().setNome((String) colunas[col++]);
				
				
				result.add(ed);
			}
			return result;

		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}

	}

	
	
	
	
}