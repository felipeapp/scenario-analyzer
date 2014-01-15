/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '22/10/2007'
 *
 */
package br.ufrn.sigaa.arq.dao.monitoria;

import java.util.Collection;

import org.hibernate.Query;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.monitoria.dominio.ParticipacaoSid;

/**
 * Dao responsável pelas consultas sobre os relatórios parciais e finais
 *  do projeto e do monitor
 * @author ilueny santos
 *
 */
public class ParticipacaoSidDao extends GenericSigaaDAO {

	
	/**
	 * retorna todas as participações de discentes no sid do projeto
	 * 
	 * @param idProjeto, anoSid
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<ParticipacaoSid> findByProjeto(Integer idProjeto, Integer anoSid, Boolean participou) throws DAOException{

		
		try {

			// Filtros para a busca
			if ((idProjeto != null)) {
				
				StringBuilder hqlConsulta = new StringBuilder();
				hqlConsulta.append(" SELECT participacao FROM ParticipacaoSid as participacao " +
						"INNER JOIN participacao.resumoSid as resumo " +
						"INNER JOIN resumo.projetoEnsino projeto ");

				hqlConsulta.append(" WHERE projeto.id = :idProjeto AND resumo.ativo = trueValue() and participacao.ativo = trueValue() ");
				
				if (anoSid != null)
					hqlConsulta.append(" AND resumo.anoSid = :anoSid ");
				
				
				if (participou != null)
					hqlConsulta.append(" AND participacao.participou = :participou ");

				
				hqlConsulta.append(" ORDER BY resumo.anoSid, projeto.projeto.titulo ");
				
				
				// Criando consulta
				Query queryConsulta = getSession().createQuery(hqlConsulta.toString());
				queryConsulta.setInteger("idProjeto", idProjeto);
				
				
				if (anoSid != null)
					queryConsulta.setInteger("anoSid", anoSid);
				
				if (participou != null)				
					queryConsulta.setBoolean("participou", participou);
				
				
				
				return queryConsulta.list();
				
			}else
				return null;		


		} catch (Exception ex) {
			throw new DAOException(ex.getMessage(), ex);
		}
		
		
	}

	
}