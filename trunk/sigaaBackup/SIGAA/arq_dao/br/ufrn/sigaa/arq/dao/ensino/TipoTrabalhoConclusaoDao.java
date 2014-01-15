/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 13/11/2007
 *
 */	
package br.ufrn.sigaa.arq.dao.ensino;

import java.util.Collection;

import org.hibernate.Criteria;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Order;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.ensino.dominio.TipoTrabalhoConclusao;

/**
 *
 * @author Ricardo Wendell
 *
 */
public class TipoTrabalhoConclusaoDao extends GenericSigaaDAO {

	/**
	 * Buscar todos dos tipos de trabalho de conclusão associados a um
	 * determinado nível de ensino
	 *
	 * @param nivel
	 * @return
	 * @throws DAOException
	 */
	public Collection<TipoTrabalhoConclusao> findByNivelEnsino(char nivel) throws DAOException {
		Criteria c = getSession().createCriteria(TipoTrabalhoConclusao.class);
		c.add( Expression.eq( getDescricaoCampo(nivel ), true) );
		c.addOrder( Order.asc("descricao") );
		return c.list();
	}

	/**
	 * @param nivel
	 * @return
	 */
	private String getDescricaoCampo(char nivel) {
		String campo = null;
		switch (nivel) {
			case NivelEnsino.DOUTORADO: campo = "doutorado"; break;
			case NivelEnsino.MESTRADO: campo = "mestrado"; break;
			case NivelEnsino.LATO: campo = "latoSensu"; break;
			case NivelEnsino.GRADUACAO: campo = "graduacao"; break;
			case NivelEnsino.TECNICO: campo = "tecnico"; break;
		}
		return campo != null ? campo : "graduacao";
	}

}
