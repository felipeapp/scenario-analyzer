package br.ufrn.sigaa.apedagogica.dao;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;
import static org.hibernate.criterion.Restrictions.eq;

import java.text.ParseException;
import java.util.Collection;
import java.util.Date;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.sigaa.apedagogica.dominio.RegistroParticipacaoAtualizacaoPedagogica;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;

/**
 * Classe de acesso aos dados dos registro de participação do docente.
 * @author Mário Rizzi
 *
 */
public class RegistroParticipacaoAtualizacaoPedagogicaDAO  extends GenericSigaaDAO {

	/**
	 * Busca geral de registros
	 * @param docente
	 * @param idGrupoAtividade
	 * @param idAtividade
	 * @param situacao
	 * @return
	 * @throws DAOException
	 */
	public Collection<RegistroParticipacaoAtualizacaoPedagogica> 
		findGeral(Integer idDocente, Integer idAtividade, Integer anoInicio, Integer periodoInicio, Integer anoFim, Integer periodoFim) throws DAOException {
		
		Criteria c = getSession().createCriteria(RegistroParticipacaoAtualizacaoPedagogica.class);
		Date dataInicio = null;
		Date dataFim = null;
		
		try {
			dataInicio = CalendarUtils.parseDate( anoInicio + (periodoInicio == 1?"-01-01":"-07-01") );
			dataFim = CalendarUtils.parseDate( anoFim + (periodoFim == 1?"-06-30":"-12-31") );
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if( !isEmpty(idDocente) )
			c.createCriteria("servidor").add(eq("id", idDocente));
		if( !isEmpty(idAtividade) )
			c.createCriteria("atividade").add(eq("id", idAtividade));
		
		c.add( Restrictions.not(Restrictions.gt("dataInicio", dataFim)) );
		c.add( Restrictions.not(Restrictions.lt("dataFim", dataInicio)) );
	
		c.addOrder(Order.asc("dataInicio"));

		return c.list();
	}
	
}
