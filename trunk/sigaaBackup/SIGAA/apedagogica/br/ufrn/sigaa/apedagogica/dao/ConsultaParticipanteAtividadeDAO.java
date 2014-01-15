package br.ufrn.sigaa.apedagogica.dao;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;
import static org.hibernate.criterion.Restrictions.eq;

import java.util.Collection;

import org.hibernate.Criteria;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.sigaa.apedagogica.dominio.ParticipanteAtividadeAtualizacaoPedagogica;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;

/**
 * Classe de acesso aos dados dos particpantes das atividades de atualização pedagógica.
 * @author Mário Rizzi
 *
 */
public class ConsultaParticipanteAtividadeDAO  extends GenericSigaaDAO {
	
	/**
	 * Busca geral de participantes. 
	 * @param docente
	 * @param idGrupoAtividade
	 * @param idAtividade
	 * @param situacao
	 * @return
	 * @throws DAOException
	 */
	public Collection<ParticipanteAtividadeAtualizacaoPedagogica> findGeral(String nome, Integer idDocente, Integer idGrupoAtividade,
				Integer idAtividade, Integer[] situacao) throws DAOException {
		
		Criteria c = getSession().createCriteria(ParticipanteAtividadeAtualizacaoPedagogica.class);
		
		if( !isEmpty(nome) )
			c.createCriteria("docente").createCriteria("pessoa").
				add(Restrictions.ilike("nomeAscii", StringUtils.toAscii(StringUtils.upperCase(nome)) + "%"));
		if( !isEmpty(idDocente) )
			c.createCriteria("docente").add(eq("id", idDocente));
		if( !isEmpty(idGrupoAtividade) ){
			c.createCriteria("atividade").createCriteria("grupoAtividade").add(Expression.eq("id", idGrupoAtividade));
			if( !isEmpty(idAtividade) )
				c.add(eq("atividade.id", idAtividade));
		}else if( !isEmpty(idAtividade) )
			c.createCriteria("atividade").add(eq("id", idAtividade));
		if( !isEmpty(situacao) )
			c.add(Restrictions.in("situacao", situacao));
			c.addOrder(Order.asc("situacao"));
		
		return c.list();
	}

}
