/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 30/05/2007
 *
 */	
package br.ufrn.sigaa.arq.dao.ensino;

import static org.hibernate.criterion.Order.asc;
import static org.hibernate.criterion.Restrictions.eq;
import static org.hibernate.criterion.Restrictions.or;

import java.util.Collection;

import org.hibernate.Criteria;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Order;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.ensino.dominio.GrupoMovimentacaoAluno;
import br.ufrn.sigaa.ensino.dominio.TipoMovimentacaoAluno;

/**
 *
 * DAO para acessar dados de tipos de movimentações de alunos
 * @author André
 *
 */
public class TipoMovimentacaoAlunoDao extends GenericSigaaDAO {

	/**
	 * Busca todos os tipos de movimentação ativos
	 * @return
	 * @throws DAOException
	 */
	public Collection<TipoMovimentacaoAluno> findAtivos() throws DAOException {
		return findAtivos(null);
	}
	
	/**
	 * Busca todos os tipos de movimentação ativos do nível informado
	 * @param nivel
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<TipoMovimentacaoAluno> findAtivos(Character nivel) throws DAOException {
		Criteria c = getSession().createCriteria(TipoMovimentacaoAluno.class);
		c.add(eq("ativo", true));
		c.addOrder(asc("descricao"));
		
		if (nivel != null)
			c.add(or(eq("todos", true), eq(descNivel(nivel), true)));
		
		return c.list();
	}
	
	/**
	 * Busca todos os tipos de movimentação ativos apenas do nível informado
	 * @param nivel
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<TipoMovimentacaoAluno> findAtivosApenasNivel(Character nivel) throws DAOException {
		Criteria c = getSession().createCriteria(TipoMovimentacaoAluno.class);
		c.add(eq("ativo", true));
		c.addOrder(asc("descricao"));		
		c.add(eq(descNivel(nivel), true));		
		return c.list();
	}	

	/**
	 * Busca todos os tipos de movimentação que são permanentes
	 * @param permanente
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<TipoMovimentacaoAluno> findAtivos(boolean permanente) throws DAOException {
		try {
			Criteria c = getSession().createCriteria(TipoMovimentacaoAluno.class).
			add(Expression.eq("ativo", true));
			if (permanente)
				c.add(Expression.eq("grupo", GrupoMovimentacaoAluno.AFASTAMENTO_PERMANENTE));
			else
				c.add(Expression.ne("grupo", GrupoMovimentacaoAluno.AFASTAMENTO_PERMANENTE));
			return c.addOrder(Order.asc("descricao")).list();
		} catch (Exception e) {
			throw new  DAOException(e);
		}
	}

	/**
	 * Busca todos os tipos de movimentação com status e nível informados
	 * @param status
	 * @param nivel
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<TipoMovimentacaoAluno> findAtivosByStatusDiscente(int status, Character nivel) throws DAOException {
		Criteria c = getSession().createCriteria(TipoMovimentacaoAluno.class);
		c.add(Expression.eq("ativo", true));
		c.add(Expression.eq("statusDiscente", status));
		c.addOrder(Order.asc("descricao"));
		
		if (nivel != null)
			c.add(or(eq("todos", true), eq(descNivel(nivel), true)));
			
		return c.list();
	}

	/**
	 * Descrição dos campos de cada nível
	 * @param nivel
	 * @return
	 */
	private String descNivel(Character nivel) {
		if (NivelEnsino.GRADUACAO == nivel)
			return "graduacao";
		else if (NivelEnsino.STRICTO == nivel)
			return "stricto";
		else if (NivelEnsino.TECNICO == nivel)
			return "tecnico";
		else if (NivelEnsino.MEDIO == nivel)
			return "medio";
		else			
			return "todos";
	}

}
