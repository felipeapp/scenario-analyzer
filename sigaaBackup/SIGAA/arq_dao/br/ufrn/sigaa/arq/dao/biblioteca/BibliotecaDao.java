/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
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
	 * <p>Método para verificar se uma determinada biblioteca realiza um serviço ou não. </p>
	 * <p>O <strong>nomeServico</strong> é o nome do atributo na classe ServicoBiblioteca, exemplo: "realizaLevantamentoInfraEstrutura" </p>
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
	 * <p>Retorna os ids das bibliotecas que possuem acervo público no sistema, este método é utilizado nas buscas públicas do sistema,
	 * apenas materiais e título que estão nas bibliotecas retornadas devem aparecer para os usuários.</p>
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
	 * <p>Método utilizado para ativar e desativar os serviços de empréstimos da biblioteca.</p>
	 * 
	 * <p>Utilizando na prorrogação de empréstimos para bloquear a circulação por um instante e conseguir cadastrar a interrução.</p>
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
			throw new DAOException ("Ocorreu um erro ao alterar os serviços de empréstios das bibliotecas.");
		
		// Retorna os ids da bibliotecas que estavam com serviço ativo antes deles serem desativados, para só ativar novamente aqueles que estavam ativos antes.
		return idBibliotecasServicosEmprestimosAtivo;
	}
	
	
	/**
	 * <p>Método utilizado para ativar e desativar os serviços de empréstimos da biblioteca.</p>
	 * 
	 * <p>Utilizando na prorrogação de empréstimos para bloquear a circulação por um instante e conseguir cadastrar a interrução.</p>
	 */
	public void ativaServicoDeEmprestimoBibliotecas(List<Integer> idsBibliotecas) throws DAOException {
		
		if(idsBibliotecas == null || idsBibliotecas.size() == 0)
			return;
		
		Query q = getSession().createSQLQuery("UPDATE biblioteca.biblioteca SET servico_emprestimos_ativos = :true WHERE id_biblioteca IN "+UFRNUtils.gerarStringIn(idsBibliotecas));
		q.setBoolean("true", true);
		if (q.executeUpdate() < 1)
			throw new DAOException ("Ocorreu um erro ao alterar os serviços de empréstios das bibliotecas.");
	}
	
	
	
	
	
	
	/**
	 * <p>Retorna os ids das bibliotecas que possuem acervo público no sistema, este método é utilizado nas buscas públicas do sistema,
	 * apenas materiais e título que estão nas bibliotecas retornadas devem aparecer para os usuários.</p>
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
	 * Retorna a lista de todas as bibliotecas do sistema ativas, ordenadas pela descrição.
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
	 * Retorna a lista de todas as bibliotecas externas ativas, ordenadas pela descrição.
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
	 * Retorna a lista de todas as bibliotecas do sistema ativas, ordenadas pela descrição.
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
	 * Retorna a biblioteca que contém a unidade passada;
	 */
	public Biblioteca findBibliotecaByUnidade(UnidadeGeral unidade) throws DAOException {
		Criteria c = getSession().createCriteria(Biblioteca.class);
		c.add(Restrictions.eq("ativo", true));
		c.add(Restrictions.eq("unidade.id", unidade.getId()));

		// só era para ter uma unidade por biblioteca.
		return (Biblioteca) c.uniqueResult();
	}
	
	/**
	 * Retorna os id da biblioteca que contém a unidade passada;
	 */
	public Integer findIdBibliotecaByUnidade(UnidadeGeral unidade) throws DAOException {
		Criteria c = getSession().createCriteria(Biblioteca.class);
		c.setProjection(Projections.property("id") );
		c.add(Restrictions.eq("ativo", true));
		c.add(Restrictions.eq("unidade.id", unidade.getId()));

		// só era para ter uma unidade por biblioteca.
		return (Integer) c.uniqueResult();
	}
	
	
	/**
	 * Retorna os id da unidade da biblioteca passada se for uma biblioteca interna, senão retorna null.
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
	 *      Método que retorna a descrição de uma biblioteca ativa no sistema a partir do id
	 *   dela.
	 * 
	 * @param idBiblioteca
	 * @param interna se a biblioteca é interna ou externa
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
	 *      Método que retorna a descrição de uma biblioteca interna ativa no sistema a partir do id
	 *   dela.
	 *    <br/>
	 *      Método adaptador para  {@link br.ufrn.sigaa.arq.dao.biblioteca.BibliotecaDao#findDescricaoBibliotecaAtiva(int, boolean)}
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
	 * Método que verifica se o serviço de empréstimos passado está ativo para a biblioteca e tipo de empréstimo passado. 
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
			(idTipoEmprestimo != null ? "AND tipos.id = :idTipoEmprestimo " : " ")+  // e tipo de empréstimo
			" AND servico."+nomeServicoEmpretimo+" = trueValue() ";  // O serviço passado
	
		Query q = getSession().createQuery( hql );
		
		q.setInteger( "idBiblioteca", idBiblioteca );
		
		if(idTipoEmprestimo != null) // empréstimos institucional não tem tipo de empréstimo vinculado, por o tipo já é institucional
			q.setInteger( "idTipoEmprestimo", idTipoEmprestimo );
		
		Boolean retorno = (Boolean ) q.uniqueResult();
		
		if(retorno == null){ // Se está nulo é porque não tem o relacionamento com o tipo de empréstimos passado (então não empresta para esse tipo)
			return false;
		}
		
		return retorno; // Se tem o relacionamento com o tipo de empréstimos, retorna se está ativo ou não.
	}
	
}
