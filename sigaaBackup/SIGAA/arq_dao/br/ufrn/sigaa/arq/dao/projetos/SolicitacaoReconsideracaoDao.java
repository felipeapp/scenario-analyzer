/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 28/04/2008
 *
 */
package br.ufrn.sigaa.arq.dao.projetos;

import java.util.Collection;

import org.hibernate.Query;
import org.hibernate.transform.AliasToBeanResultTransformer;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.projetos.dominio.SolicitacaoReconsideracao;


/**
 * Dao responsável pelas consultas sobre as reconsiderações de avaliação dos projetos.
 * 
 * @author UFRN
 */
public class SolicitacaoReconsideracaoDao extends GenericSigaaDAO {

	public SolicitacaoReconsideracaoDao() {		
	}	

	/***
	 * Lista os solicitações de reconsideração pendentes de análise
	 * do tipo informado.  
	 *  
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<SolicitacaoReconsideracao> findAllSolicitacoesPendentes(Integer idTipoSolicitacao) throws DAOException{
	    try {
		String hql ="select sr.id as id, atv as atividade, sr.projeto as projeto, pm as projetoMonitoria, " +
				" sr.tipo as tipo, sr.dataSolicitacao as dataSolicitacao," +
				" sr.dataParecer as dataParecer, sr.aprovado as aprovado, sr.ativo as ativo " +
				" from SolicitacaoReconsideracao sr  " +
				" left join sr.atividade atv " +
				" left join sr.projetoMonitoria pm " +
		"where (sr.ativo = trueValue()) and (sr.dataParecer is null) and (sr.tipo.id = :idTipo)";
		Query query = getSession().createQuery(hql);
		query.setInteger("idTipo", idTipoSolicitacao);
		return query.setResultTransformer(new AliasToBeanResultTransformer(SolicitacaoReconsideracao.class)).list();
	    } catch (Exception e) {
		throw new DAOException(e.getMessage(), e);
	    }
	}
	
}
