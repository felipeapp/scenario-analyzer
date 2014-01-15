/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 10/06/2009
 *
 */
package br.ufrn.sigaa.arq.dao.biblioteca;

import java.util.Collection;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.comum.dominio.UnidadeGeral;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.biblioteca.dominio.Biblioteca;

/**
 * Dao que gerencia as bibliotecas.
 *
 * @author Fred_Castro
 */
public class BibliotecaDao extends GenericSigaaDAO{

	
	
	/**
	 * <p>M�todo para verificar se uma determinada biblioteca realiza um servi�o ou n�o. </p>
	 * <p>O <strong>nomeServico</strong> � o nome do atributo na classe ServicoBiblioteca, exemplo: "realizaLevantamentoInfraEstrutura" </p>
	 *
	 * @param nomeServico
	 * @return
	 * @throws DAOException
	 */
	public boolean bibliotecaRealizaServico(String nomeServico, int idBiblioteca) throws DAOException{
		
		String hql = "SELECT s."+nomeServico+" FROM ServicosInformacaoReferenciaBiblioteca s "
			+ " WHERE s.biblioteca.id = :idBiblioteca ";
	
		Query q = getSession().createQuery(hql);
		q.setInteger("idBiblioteca", idBiblioteca);
		
		Boolean b = (Boolean) q.uniqueResult();
		return b != null ? b : false;
	}
	
	
	
	
	/**
	 * <p>Retorna os ids das bibliotecas que possuem acervo p�blico no sistema, este m�todo � utilizado nas buscas p�blicas do sistema,
	 * apenas materiais e t�tulo que est�o nas bibliotecas retornadas devem aparecer para os usu�rios.</p>
	 * 
	 * <p>Somente bibliotecas interna possuem acervo.</p>
	 */
	public boolean isBibliotecaDoMaterailComServicoDeEmprestimoAtivado(int idMaterial) throws DAOException {
		
		String hql = "SELECT m.biblioteca.servicoEmprestimosAtivos FROM MaterialInformacional m "
				+ " WHERE m.id = :idMaterial ";
		
		Query q = getSession().createQuery(hql);
		q.setInteger("idMaterial", idMaterial);
	
		Boolean b = (Boolean) q.uniqueResult();
		return b;
	}
	
	
	
	/**
	 * <p>M�todo utilizado para ativar e desativar os servi�os de empr�stimos da biblioteca.</p>
	 * 
	 * <p>Utilizando na prorroga��o de empr�stimos para bloquear a circula��o por um instante e conseguir cadastrar a interru��o.</p>
	 */
	public List<Integer> desativaServicoDeEmprestimoBibliotecas(List<Integer> idsBibliotecas) throws DAOException {
		
		if(idsBibliotecas == null || idsBibliotecas.size() == 0)
			return null;
		
		String hql = "SELECT b.id FROM Biblioteca b "
			+ " WHERE b.servicoEmprestimosAtivos = :true AND b.id IN "+UFRNUtils.gerarStringIn(idsBibliotecas);
		
		Query q1 = getSession().createQuery(hql);
		q1.setBoolean("true", true);
		@SuppressWarnings("unchecked")
		List<Integer> idBibliotecasServicosEmprestimosAtivo = q1.list();
		
		if(idBibliotecasServicosEmprestimosAtivo == null || idBibliotecasServicosEmprestimosAtivo.size() == 0)
			return null;
		
		Query q = getSession().createSQLQuery("UPDATE biblioteca.biblioteca SET servico_emprestimos_ativos = :false WHERE id_biblioteca IN "+UFRNUtils.gerarStringIn(idBibliotecasServicosEmprestimosAtivo));
		q.setBoolean("false", false);
		if (q.executeUpdate() < 1)
			throw new DAOException ("Ocorreu um erro ao alterar os servi�os de empr�stios das bibliotecas.");
		
		// Retorna os ids da bibliotecas que estavam com servi�o ativo antes deles serem desativados, para s� ativar novamente aqueles que estavam ativos antes.
		return idBibliotecasServicosEmprestimosAtivo;
	}
	
	
	/**
	 * <p>M�todo utilizado para ativar e desativar os servi�os de empr�stimos da biblioteca.</p>
	 * 
	 * <p>Utilizando na prorroga��o de empr�stimos para bloquear a circula��o por um instante e conseguir cadastrar a interru��o.</p>
	 */
	public void ativaServicoDeEmprestimoBibliotecas(List<Integer> idsBibliotecas) throws DAOException {
		
		if(idsBibliotecas == null || idsBibliotecas.size() == 0)
			return;
		
		Query q = getSession().createSQLQuery("UPDATE biblioteca.biblioteca SET servico_emprestimos_ativos = :true WHERE id_biblioteca IN "+UFRNUtils.gerarStringIn(idsBibliotecas));
		q.setBoolean("true", true);
		if (q.executeUpdate() < 1)
			throw new DAOException ("Ocorreu um erro ao alterar os servi�os de empr�stios das bibliotecas.");
	}
	
	
	
	
	
	
	/**
	 * <p>Retorna os ids das bibliotecas que possuem acervo p�blico no sistema, este m�todo � utilizado nas buscas p�blicas do sistema,
	 * apenas materiais e t�tulo que est�o nas bibliotecas retornadas devem aparecer para os usu�rios.</p>
	 * 
	 * <p>Somente bibliotecas interna possuem acervo.</p>
	 */
	public List<Integer>  findIdsBibliotecaAcervoPublico() throws DAOException {
		
		String hql = "SELECT b.id FROM Biblioteca b "
			+ " WHERE b.ativo = trueValue() AND b.unidade IS NOT NULL AND b.acervoPublico = trueValue() ";
		
		Query q = getSession().createQuery(hql);
		
		@SuppressWarnings("unchecked")
		List <Integer> lista = q.list();
		return lista;
	}

	/**
	 * Retorna a lista de todas as bibliotecas do sistema ativas, ordenadas pela descri��o.
	 */
	public List <Biblioteca> findAllBibliotecasInternasAtivas() throws DAOException {
		Criteria c = getSession().createCriteria(Biblioteca.class);
		c.add(Restrictions.eq("ativo", true));
		c.add(Restrictions.isNotNull("unidade"));
		c.addOrder(Order.asc("descricao"));
		
		@SuppressWarnings("unchecked")
		List<Biblioteca> lista = c.list();
		return lista;
	}
	
	
	/**
	 * Retorna a lista de todas as bibliotecas externas ativas, ordenadas pela descri��o.
	 */
	public List<Biblioteca> findAllBibliotecasExternasAtivas () throws DAOException{
		Criteria c = getSession().createCriteria(Biblioteca.class);
		c.add(Restrictions.eq("ativo", true));
		c.add(Restrictions.isNull("unidade"));
		c.addOrder(Order.asc("descricao"));
		
		@SuppressWarnings("unchecked")
		List<Biblioteca> lista = c.list();
		return lista;
	}

	
	
	/**
	 * Retorna a lista de todas as bibliotecas do sistema ativas, ordenadas pela descri��o.
	 */
	public List <Biblioteca> findAllBibliotecasInternasAtivasPorUnidade(List<Integer> idUnidades) throws DAOException {
		Criteria c = getSession().createCriteria(Biblioteca.class);
		c.add(Restrictions.eq("ativo", true));
		c.add(Restrictions.isNotNull("unidade"));
		if(idUnidades != null && idUnidades.size() > 0)
			c.add(Restrictions.in("unidade.id", idUnidades));
		else 
			c.add(Restrictions.eq("unidade.id", -1));
		c.addOrder(Order.asc("descricao"));
		
		@SuppressWarnings("unchecked")
		List<Biblioteca> lista = c.list();
		return lista;
	}
	
	/**
	 * Retorna a biblioteca que cont�m a unidade passada;
	 */
	public Biblioteca findBibliotecaByUnidade(UnidadeGeral unidade) throws DAOException {
		Criteria c = getSession().createCriteria(Biblioteca.class);
		c.add(Restrictions.eq("ativo", true));
		c.add(Restrictions.eq("unidade.id", unidade.getId()));

		// s� era para ter uma unidade por biblioteca.
		return (Biblioteca) c.uniqueResult();
	}
	
	/**
	 * Retorna os id da biblioteca que cont�m a unidade passada;
	 */
	public Integer findIdBibliotecaByUnidade(UnidadeGeral unidade) throws DAOException {
		Criteria c = getSession().createCriteria(Biblioteca.class);
		c.setProjection(Projections.property("id") );
		c.add(Restrictions.eq("ativo", true));
		c.add(Restrictions.eq("unidade.id", unidade.getId()));

		// s� era para ter uma unidade por biblioteca.
		return (Integer) c.uniqueResult();
	}
	
	
	/**
	 * Retorna os id da unidade da biblioteca passada se for uma biblioteca interna, sen�o retorna null.
	 */
	public Integer findIdUnidadeByBiblioteca(Biblioteca biblioteca) throws DAOException {
		
		String hql  = "SELECT b.unidade.id "+
		" FROM Biblioteca b "+
		" WHERE b.id = :idBiblioteca ";
		
		Query q = getSession().createQuery(hql);
		q.setInteger("idBiblioteca", biblioteca.getId());
		q.setMaxResults(1);
		
		return (Integer) q.uniqueResult();
	}
	
	
	/**
	 *      M�todo que retorna a descri��o de uma biblioteca ativa no sistema a partir do id
	 *   dela.
	 * 
	 * @param idBiblioteca
	 * @param interna se a biblioteca � interna ou externa
	 */
	public String findDescricaoBibliotecaAtiva(int idBiblioteca, boolean interna) throws DAOException {
		
		Criteria c = getSession().createCriteria(Biblioteca.class);
		c.setProjection( Projections.projectionList().add( Projections.property("descricao"), "descricao" ));
		c.add(Restrictions.eq("ativo", true));
		c.add(Restrictions.eq("id", idBiblioteca));
		
		if(interna)
			c.add(Restrictions.isNotNull("unidade"));
		else
			c.add(Restrictions.isNull("unidade"));
			
		return (String) c.uniqueResult();
	}
	
	
	/**
	 *      M�todo que retorna a descri��o de uma biblioteca interna ativa no sistema a partir do id
	 *   dela.
	 *    <br/>
	 *      M�todo adaptador para  {@link br.ufrn.sigaa.arq.dao.biblioteca.BibliotecaDao#findDescricaoBibliotecaAtiva(int, boolean)}
	 */
	public String findDescricaoBibliotecaInternaAtiva(int  idBiblioteca)throws DAOException {
		return findDescricaoBibliotecaAtiva(idBiblioteca, true);
	}
	
	
	/**
	 * Retorna todas as bibliotecas associadas ao curso passado.
	 */
	public Collection<Biblioteca> findBibliotecasAssociadasAoCurso( int idCurso ) throws DAOException {
		String query =
				"SELECT sb.biblioteca " +
				"FROM " +
				"	ServicosInformacaoReferenciaBiblioteca AS sb " + 
				"INNER JOIN sb.cursosAssociados ca " + 
				"WHERE ca.id = :idCurso ";
		
		Query q = getSession().createQuery( query );
		
		q.setInteger( "idCurso", idCurso );
		
		@SuppressWarnings("unchecked")
		Collection<Biblioteca> a = q.list();
		
		return a;
	}




	/**
	 * M�todo que verifica se o servi�o de empr�stimos passado est� ativo para a biblioteca e tipo de empr�stimo passado. 
	 *  
	 *
	 * @param id
	 * @param id2
	 * @param string
	 * @return
	 */
	public Boolean isServicoEmpretimosAtivo(int idBiblioteca, Integer idTipoEmprestimo, String nomeServicoEmpretimo) throws DAOException  {
		
		String hql =" SELECT servico.ativo " +
			" FROM ServicosEmprestimosBiblioteca servico " +
			(idTipoEmprestimo != null ? " INNER JOIN servico.tiposEmprestimos tipos ": " ") +  // para a biblioteca 
			" WHERE servico.biblioteca.id = :idBiblioteca "+
			(idTipoEmprestimo != null ? "AND tipos.id = :idTipoEmprestimo " : " ")+  // e tipo de empr�stimo
			" AND servico."+nomeServicoEmpretimo+" = trueValue() ";  // O servi�o passado
	
		Query q = getSession().createQuery( hql );
		
		q.setInteger( "idBiblioteca", idBiblioteca );
		
		if(idTipoEmprestimo != null) // empr�stimos institucional n�o tem tipo de empr�stimo vinculado, por o tipo j� � institucional
			q.setInteger( "idTipoEmprestimo", idTipoEmprestimo );
		
		Boolean retorno = (Boolean ) q.uniqueResult();
		
		if(retorno == null){ // Se est� nulo � porque n�o tem o relacionamento com o tipo de empr�stimos passado (ent�o n�o empresta para esse tipo)
			return false;
		}
		
		return retorno; // Se tem o relacionamento com o tipo de empr�stimos, retorna se est� ativo ou n�o.
	}
	
}
