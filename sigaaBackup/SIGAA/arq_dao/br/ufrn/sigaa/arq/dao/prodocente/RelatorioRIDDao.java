package br.ufrn.sigaa.arq.dao.prodocente;

import java.util.Collection;

import org.hibernate.Criteria;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Order;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.pessoa.dominio.Servidor;
import br.ufrn.sigaa.prodocente.producao.dominio.Producao;

/**
 * DAO responsável por consultas específicas do relatório RID.
 * 
 * @author Jean Guerethes
 */
public class RelatorioRIDDao extends GenericSigaaDAO {

	/**
	 * Procura as produções do Servidor filtrando por Válidas, Inválidas e
	 * Pendentes.
	 *
	 * @param validacao :
	 *            1 = valida, 0 = Invalida, -1 = pendente de validação,
	 *            !{1|0|-1} = todas
	 * @author Edson Anibal (ambar@info.ufrn.br)
	 */
	@SuppressWarnings("unchecked")
	public Collection<Producao> findByProducaoServidor(Servidor servidor,
			Class tipo, Integer validacao, int anoInicial, int anoFinal, int periodoInicial, int periodoFinal)
			throws DAOException {

		Criteria c = getSession().createCriteria(tipo);
		c.add(Expression.eq("servidor", servidor));
		// somente as produções que não foram "deletadas" (ativo!=falseValue())
		c.add(Expression.or(Expression.eq("ativo", true), Expression.isNull("ativo")));

		c.add(Expression.ge("dataProducao", CalendarUtils.createDate(1, periodoInicial == 1 ? 0 : 6, anoInicial)));
		c.add(Expression.le("dataProducao", CalendarUtils.createDate(periodoFinal == 1 ? 30 : 31, periodoFinal == 1 ? 6 : 11, anoFinal)));

		if (validacao != null) {
			// Produções Válidas
			if (validacao == 1)
				c.add(Expression.eq("validado", true));
			// Produções Inválidas
			else if (validacao == 0)
				c.add(Expression.eq("validado", false));
			// Produções Pendentes
			else if (validacao == -1)
				c.add(Expression.isNull("validado"));
		}
		c.addOrder(Order.desc("anoReferencia"));
		c.addOrder(Order.desc("dataProducao"));

		return c.list();

	}
	
}