/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 19/07/2011
 *
 */	
package br.ufrn.sigaa.ensino.medio.dao;

import java.util.List;

import br.ufrn.arq.dao.dialect.SQLDialect;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.HibernateUtils;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.ensino.medio.dominio.DiscenteMedio;
import br.ufrn.sigaa.ensino.medio.dominio.MatriculaDiscenteSerie;
import br.ufrn.sigaa.ensino.medio.dominio.ObservacaoDiscenteSerie;

/**
 * Consultas de Observações cadastradas para um aluno em cada série, a serem exibidas em seu boletim escolar.
 * 
 * @author Arlindo
 *
 */
public class ObservacaoDiscenteSerieDao extends GenericSigaaDAO {
	
	/**
	 * Retorna as observações do discente.
	 * @see ObservacaoDiscenteSerie
	 * @param discente
	 * @param matriculaSerie
	 * @return
	 * @throws DAOException
	 */
	public List<ObservacaoDiscenteSerie> findByDiscenteAndSerie(DiscenteMedio discente, MatriculaDiscenteSerie matriculaSerie) throws DAOException {
		try {
			String projecao = "o.id, o.observacao, o.data, o.registro.usuario.pessoa.nome ";
			
			StringBuffer hql = new StringBuffer();
			hql.append("select "+projecao+ " from ObservacaoDiscenteSerie o ");
			hql.append("inner join o.matricula m ");
			hql.append("inner join o.matricula.discenteMedio d ");
			hql.append("inner join o.registro r ");
			hql.append("inner join o.registro.usuario u ");
			hql.append("inner join o.registro.usuario.pessoa p ");
			
			hql.append("where d.id = "+discente.getId());
			
			hql.append(" and o.ativo = "+SQLDialect.TRUE);
			
			if (matriculaSerie != null && matriculaSerie.getId() > 0)
				hql.append(" and m.id = "+matriculaSerie.getId());
			
			hql.append(" order by o.data asc ");
			
			@SuppressWarnings("unchecked")
			List<ObservacaoDiscenteSerie> result = (List<ObservacaoDiscenteSerie>) HibernateUtils.parseTo(
					getSession().createQuery(hql.toString()).list(), projecao, ObservacaoDiscenteSerie.class, "o");
			
			for (ObservacaoDiscenteSerie obs : result) {
				obs.setObservacao(StringUtils.divideEmLinhas(obs
						.getObservacao().replaceAll("\n", "").replaceAll("\r",
								""), 150));
			}
			
			return result;
		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}	

}
