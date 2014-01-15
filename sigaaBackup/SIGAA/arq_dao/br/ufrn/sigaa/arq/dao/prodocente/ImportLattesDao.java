/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '18/04/2007'
 *
 */
package br.ufrn.sigaa.arq.dao.prodocente;

import java.util.List;

import org.hibernate.Query;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.dominio.AreaConhecimentoCnpq;
import br.ufrn.sigaa.pessoa.dominio.Pais;
import br.ufrn.sigaa.pessoa.dominio.Servidor;
import br.ufrn.sigaa.prodocente.producao.dominio.Producao;

/**
 * DAO para busca de informações para a importação do currículo Lattes
 * @author davidpereira
 *
 */
public class ImportLattesDao extends GenericSigaaDAO {

	/**
	 * Busca area de conhecimento por nome
	 * @param area
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public AreaConhecimentoCnpq findSubAreaConhecimento(String area) throws DAOException {
		
		try {
			Query q = getSession().createQuery("from AreaConhecimentoCnpq a where upper(a.nome) = :area ");
			q.setString("area", area.toUpperCase());
			List<AreaConhecimentoCnpq> result = q.list();
			
			if (result != null && !result.isEmpty())
				return result.iterator().next();
			else
				return null;
			
		} catch(Exception e) {
			throw new DAOException(e);
		}
		
	}
	
	/**
	 * Busca de país por nome
	 * @param area
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Pais findPais(String pais) throws DAOException {
		
		try {
			Query q = getSession().createQuery("from Pais p where upper(p.nome) = :pais");
			q.setString("pais", pais.toUpperCase());
			List<Pais> result = q.list();
			
			if (result != null && !result.isEmpty())
				return result.iterator().next();
			else
				return null;
			
		} catch(Exception e) {
			throw new DAOException(e);
		}
		
	}
	
	/**
	 * Verifica se a produção passada como parâmetro já foi importada anteriormente.
	 */
	public Producao isProducaoImportada(Producao p, Servidor s) throws DAOException {
		try {
			Query q = getSession().createQuery("from " + p.getClass().getSimpleName()
					+ " p where p.servidor.id = :servidor" +
					" and p.sequenciaProducao is not null"
					+ " and p.sequenciaProducao = :seq" 
					+ " AND (p.ativo = trueValue() OR p.ativo is null) ");
			q.setInteger("servidor", s.getId());
			q.setInteger("seq", p.getSequenciaProducao());
			q.setMaxResults(1);
			
			return (Producao) q.uniqueResult();
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}
	
}
