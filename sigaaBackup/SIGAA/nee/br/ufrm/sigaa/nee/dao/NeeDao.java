/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em 05/10/2010
 * Autor: Rafael Gomes
 */

package br.ufrm.sigaa.nee.dao;

import java.util.Collection;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.nee.dominio.SolicitacaoApoioNee;
import br.ufrn.sigaa.nee.dominio.StatusAtendimento;
import br.ufrn.sigaa.nee.dominio.TipoNecessidadeSolicitacaoNee;
import br.ufrn.sigaa.pessoa.dominio.PessoaNecessidadeEspecial;

/**
 * DAO responsável por gerenciar o acesso e consulta a dados do {@link SolicitacaoApoioNee}.
 * @author Rafael Gomes
 *
 */
public class NeeDao extends GenericSigaaDAO {
	
	/**
	 * Retorna o valor do próximo código de discente com NEE a ser cadastrado no sistema.
	 * @return
	 */
	public int nextValueCodigoDiscenteNee(){
		return getSimpleJdbcTemplate().queryForInt("(select nextval('nee.codigo_discente_nee_seq'))");
	}
	
	/**
	 * Retorna a coleção de necessidades especiais por Solicitação de Apoio a alunos de NEE.
	 * @param idSolicitacaoNee
	 * @return
	 * @throws DAOException
	 */
	public Collection<TipoNecessidadeSolicitacaoNee> findNecessidadesEspeciaisBySolicitacaoNee(int idSolicitacaoNee) throws DAOException {
		
		try {
			Criteria c = getSession().createCriteria(TipoNecessidadeSolicitacaoNee.class);
			c.add(Restrictions.eq("solicitacaoApoioNEE.id", idSolicitacaoNee));
			c.add(Restrictions.eq("ativo", true));				

			@SuppressWarnings("unchecked")
			List<TipoNecessidadeSolicitacaoNee> lista = c.list();
			
			return lista;
		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}
	
	/**
	 * Retorna a coleção de solicitações por discente. 
	 * @param idDiscente
	 * @return
	 * @throws DAOException
	 */
	public Collection<SolicitacaoApoioNee> findSolicitacaoApoioNeeByDiscente(int idDiscente) throws DAOException {
		
		try {
			Criteria c = getSession().createCriteria(SolicitacaoApoioNee.class);
			c.add(Restrictions.eq("discente.id", idDiscente));
			
			@SuppressWarnings("unchecked")
			List<SolicitacaoApoioNee> lista = c.list();
			
			return lista;
		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}
	
	/**
	 * Retorna um boolean verificando se o discente já possui solicitação de apoio de NEE 
	 * ativas com status "Submetido a CAENE", "Em Atendimento", "Em Analise". 
	 * @param idDiscente
	 * @return
	 * @throws DAOException
	 */
	public boolean existeSolicitacaoAtivasByDiscente(int idDiscente) throws DAOException {
		
		try {
			Criteria c = getSession().createCriteria(SolicitacaoApoioNee.class);
			c.add(Restrictions.eq("discente.id", idDiscente));
			c.add(Restrictions.eq("ativo", true));	
			c.add(Restrictions.in("statusAtendimento.id", StatusAtendimento.ativos()));
			
			@SuppressWarnings("unchecked")
			List<SolicitacaoApoioNee> lista = c.list();
			
			return lista.size() > 0;
		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}
	
	/**
	 * Retorna todas as solicitações de apoio de NEE pendentes de parecer acadêmico sobre sua necessidade educacional.
	 * 
	 * @throws DAOException
	 */
	public Collection<SolicitacaoApoioNee> findAllPendentesParecer() throws DAOException {
		Criteria c = getSession().createCriteria(SolicitacaoApoioNee.class);
		c.add(Restrictions.eq("ativo", true));
		c.add(Restrictions.eq("statusAtendimento.id", StatusAtendimento.SUBMETIDO));
		
		Criteria discente = c.createCriteria("discente");
		discente.createCriteria("curso").addOrder(Order.asc("nome"));
		discente.createCriteria("pessoa").addOrder(Order.asc("nome"));
		
		@SuppressWarnings("unchecked")
		List<SolicitacaoApoioNee> lista = c.list();
		return lista;
	}
	
	/**
	 * Retorna todas as solicitações de apoio de NEE com status ativos.
	 * 
	 * @throws DAOException
	 */
	public Collection<SolicitacaoApoioNee> findAllSolicitacaoNee(boolean isNee, Curso curso, Unidade programaStricto) throws DAOException {
		Criteria c = getSession().createCriteria(SolicitacaoApoioNee.class);
		c.add(Restrictions.eq("ativo", true));
		if ( ValidatorUtil.isEmpty(curso) && ValidatorUtil.isEmpty(programaStricto) )
			c.add(Restrictions.eq("statusAtendimento.id", StatusAtendimento.EM_ATENDIMENTO));
				
		Criteria discente = c.createCriteria("discente");
		Criteria crCurso = discente.createCriteria("curso");
		if ( !ValidatorUtil.isEmpty(curso) )
			crCurso.add(Restrictions.eq("id", curso.getId()));
		
		if ( !ValidatorUtil.isEmpty(programaStricto) ){
			crCurso.add(Restrictions.eq("unidade", programaStricto));
		}	
		crCurso.addOrder(Order.asc("nome"));
		discente.createCriteria("pessoa").addOrder(Order.asc("nome"));
		

		@SuppressWarnings("unchecked")
		List<SolicitacaoApoioNee> lista = c.list();
		return lista;
	}
	
	/**
	 * Retorna todas as solicitações de apoio de NEE com status ativos.
	 * 
	 * @throws DAOException
	 */
	public Collection<SolicitacaoApoioNee> findAllSolicitacaoNeeByCurso(Curso curso) throws DAOException {
		Criteria c = getSession().createCriteria(SolicitacaoApoioNee.class);
		c.add(Restrictions.eq("ativo", true));
		c.add(Restrictions.in("statusAtendimento.id", StatusAtendimento.ativos()));
		
		Criteria discente = c.createCriteria("discente");
		if ( !ValidatorUtil.isEmpty(curso) )
			discente.add(Restrictions.eq("curso", curso));
		discente.createCriteria("curso").addOrder(Order.asc("nome"));
		discente.createCriteria("pessoa").addOrder(Order.asc("nome"));
		

		@SuppressWarnings("unchecked")
		List<SolicitacaoApoioNee> lista = c.list();
		return lista;
	}
	
	/**
	 * Atualiza o a situação de leitura atual da solicitação.
	 * @param processo
	 */
	public void atualizarSolicitacaoLida(SolicitacaoApoioNee solicitacaoNee) {
		getJdbcTemplate().update("update nee.solicitacao_apoio_nee set lida = ? where id_solicitacao_apoio_nee = ?", 
				new Object[]{ solicitacaoNee.isLida(), solicitacaoNee.getId() });
	}

	/**
	 * Retorna a coleção de necessidades especiais por discente.
	 * @param idSolicitacaoNee
	 * @return
	 * @throws DAOException
	 */
	public Collection<PessoaNecessidadeEspecial> findNecessidadesEspeciaisByDiscente(DiscenteAdapter discente) throws DAOException {
		
		try {
			Criteria c = getSession().createCriteria(PessoaNecessidadeEspecial.class);
			c.add(Restrictions.eq("pessoa.id", discente.getPessoa().getId()));
			
			@SuppressWarnings("unchecked")
			List<PessoaNecessidadeEspecial> lista = c.list();
			
			return lista;
		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}
}
