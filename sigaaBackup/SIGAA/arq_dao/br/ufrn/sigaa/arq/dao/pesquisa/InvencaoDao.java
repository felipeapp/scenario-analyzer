/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '08/07/2008'
 *
 */
package br.ufrn.sigaa.arq.dao.pesquisa;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

import org.hibernate.Query;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.arq.dominio.SeqAno;
import br.ufrn.sigaa.pesquisa.dominio.Invencao;
import br.ufrn.sigaa.pesquisa.dominio.ProjetoPesquisa;
import br.ufrn.sigaa.projetos.dominio.EntidadeFinanciadora;

/**
 * Dao para efetuar as consultas de invenções de pesquisa
 * 
 * @author leonardo
 * 
 */
public class InvencaoDao extends GenericSigaaDAO {

	@SuppressWarnings("unchecked")
	public Collection<Invencao> findByUsuarioCadastro(int idUsuario)
			throws DAOException {
		Query q = getSession()
				.createQuery(
						"from Invencao i where i.criadoPor.usuario.id = :usuario and i.ativo = trueValue()");
		q.setInteger("usuario", idUsuario);
		return q.list();
	}

	/**
	 * Retorna as entidades financiadoras dos projetos passados como parâmetro
	 * 
	 * @param projetos
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<EntidadeFinanciadora> findEntidadesFinanciadoras(
			Collection<ProjetoPesquisa> projetos) throws DAOException {
		return getSession().createQuery(
				"select f.entidadeFinanciadora "
						+ "from FinanciamentoProjetoPesq f "
						+ "where f.projetoPesquisa.id in "
						+ UFRNUtils.gerarStringIn(projetos)).list();
	}
	
	/**
	 * Retorna o próximo número na sequência de códigos de notificações de invenção
	 * @return
	 * @throws DAOException
	 */
	public int findNextNumero() throws DAOException {
		return getNextSeq(SeqAno.SEQUENCIA_CODIGO_NOTIFICACAO_INVENCAO, 0);
	}
	
	@SuppressWarnings("unchecked")
	public Collection<Invencao> findByStatusLoginData(Integer status, String login, Date data)
			throws DAOException {
		StringBuilder consulta = new StringBuilder();
		Calendar calendar = Calendar.getInstance();
		consulta.append("from Invencao i where ");
		if (status != null)
			consulta.append("i.status = :status");
		if (status != null && login != null)
			consulta.append(" and ");
		if (login != null)
			consulta.append("i.criadoPor.usuario.login = :login");
		if (data != null && (status != null || login != null))
			consulta.append(" and ");
		if (data != null) {
			calendar.setTime(data);
			consulta.append("date_part('year', i.criadoEm) = :year" +
					" and date_part('month', i.criadoEm) = :month" +
					" and date_part('day', i.criadoEm) = :day");
		}
		
		Query q = getSession().createQuery(consulta.toString());
		
		if (status != null)
			q.setInteger("status", status);
		if (login != null)
			q.setString("login", login);
		if (data != null){
			q.setInteger("year", calendar.get(Calendar.YEAR));
			q.setInteger("month", calendar.get(Calendar.MONTH )+1);
			q.setInteger("day", calendar.get(Calendar.DAY_OF_MONTH));
		}
		
		return q.list();
	}
}
