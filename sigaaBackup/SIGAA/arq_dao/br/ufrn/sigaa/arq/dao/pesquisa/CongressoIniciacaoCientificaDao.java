/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 03/07/2007
 *
 */
package br.ufrn.sigaa.arq.dao.pesquisa;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.arq.dominio.SeqAno;
import br.ufrn.sigaa.dominio.AreaConhecimentoCnpq;
import br.ufrn.sigaa.pesquisa.dominio.AvaliadorCIC;
import br.ufrn.sigaa.pesquisa.dominio.CongressoIniciacaoCientifica;
import br.ufrn.sigaa.pesquisa.dominio.OrganizacaoPaineisCIC;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;
import br.ufrn.sigaa.pessoa.dominio.Servidor;

/**
 * Dao para consultas a Congressos e Avaliadores de Iniciação Científica
 *
 * @author Ricardo Wendell
 *
 */
public class CongressoIniciacaoCientificaDao extends GenericSigaaDAO {

	/**
	 * Retorna o Congresso de Iniciação Científica atual
	 * @return
	 * @throws DAOException
	 */
	public CongressoIniciacaoCientifica findAtivo() throws DAOException {
        try {
        	return (CongressoIniciacaoCientifica) getSession().createCriteria(CongressoIniciacaoCientifica.class)
        		.add( Expression.eq("ativo", true))
        		.setFetchMode("restricoes", FetchMode.JOIN)
        		.uniqueResult();
        } catch (Exception e) {
            throw new DAOException(e.getMessage(), e);
        }
	}
	
	/**
	 * Retorna a organização de apresentação de painéis mais recentemente cadastrada para o congresso informado.
	 * @param congresso
	 * @return
	 * @throws DAOException
	 */
	public OrganizacaoPaineisCIC findOrganizacaoMaisRecente(CongressoIniciacaoCientifica congresso) throws DAOException {
		try {
			return (OrganizacaoPaineisCIC) getSession().createCriteria(OrganizacaoPaineisCIC.class)
			.add( Restrictions.eq("congresso.id", congresso.getId()))
			.addOrder(Order.desc("dataCadastro"))
			.setMaxResults(1)
			.uniqueResult();
		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}

	/**
	 * Retorna o próximo número da sequência dos resumos de um determinado congresso
	 * @param congresso
	 * @return
	 * @throws DAOException
	 */
	public int getProximoCodigo(CongressoIniciacaoCientifica congresso) throws DAOException {
		return getNextSeq(SeqAno.SEQUENCIA_RESUMO_CIC, congresso.getAno());
	}

	/**
	 * Verifica se o docente foi cadastrado como avaliador de um determinado congresso para a área de conhecimento informada 
	 * @param congresso
	 * @param docente
	 * @param area
	 * @return
	 * @throws DAOException
	 */
	public boolean isAvaliador(CongressoIniciacaoCientifica congresso, Servidor docente, AreaConhecimentoCnpq area) throws DAOException {
		Criteria c = getSession().createCriteria(AvaliadorCIC.class);
		c.add(Expression.eq("congresso", congresso));
		c.add(Expression.eq("docente", docente));
		c.add(Expression.eq("area", area));
		return c.list().isEmpty();
	}
	
	/**
	 * Realizar um busca dos avaliadores de resumo, apresentação ou ambos no congresso de iniciação Científica 
	 * selecionado, podendo também filtrar a consulta pela área de conhecimento. 
	 * 
	 */
	@SuppressWarnings("unchecked")
	public Collection<AvaliadorCIC> findByCongresso(CongressoIniciacaoCientifica congresso, Boolean avaliadorResumo, Boolean avaliadorApresentacao, AreaConhecimentoCnpq area) throws DAOException {
		StringBuilder hql = new StringBuilder();
		hql.append("select a.id, a.avaliadorResumo, a.avaliadorApresentacao, a.docente.pessoa.nome, a.area.nome, a.presenca ");
		hql.append(" from AvaliadorCIC a ");
		hql.append(" where a.congresso.id = " + congresso.getId());
		if(avaliadorResumo != null)
			hql.append(" and a.avaliadorResumo = " + avaliadorResumo);
		if(avaliadorApresentacao != null)
			hql.append(" and a.avaliadorApresentacao = " + avaliadorApresentacao);
		if(area != null)
			hql.append(" and a.area.id = " + area.getId());
		hql.append(" order by a.area.nome, a.docente.pessoa.nome");
		
		List lista = getSession().createQuery(hql.toString()).list();
		
		List<AvaliadorCIC> result = new ArrayList<AvaliadorCIC>();
		
		for (int a = 0; a < lista.size(); a++) {
			int col = 0;
			Object[] colunas = (Object[]) lista.get(a);
			
			AvaliadorCIC avaliador = new AvaliadorCIC();
			avaliador.setId((Integer) colunas[col++]);
			avaliador.setAvaliadorResumo((Boolean) colunas[col] == null ? false : (Boolean) colunas[col++]);
			avaliador.setAvaliadorApresentacao((Boolean) colunas[col] == null ? false : (Boolean) colunas[col++]);
			
			Pessoa pessoa = new Pessoa();
			pessoa.setNome((String) colunas[col++]);
			Servidor docente = new Servidor();
			docente.setPessoa(pessoa);
			avaliador.setDocente(docente);
			
			AreaConhecimentoCnpq areaConhecimento = new AreaConhecimentoCnpq();
			areaConhecimento.setNome((String) colunas[col++]);
			avaliador.setArea(areaConhecimento);
			
			avaliador.setPresenca((Boolean) colunas[col++]);
			
			result.add(avaliador);
		}
		return result;
	}
	
}