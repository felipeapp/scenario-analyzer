package br.ufrn.sigaa.arq.dao.ensino.latosensu;

import java.util.Collection;

import org.hibernate.Query;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.ensino.latosensu.dominio.SituacaoProposta;

public class SituacaoPropostaDao extends GenericSigaaDAO {

	/**
	 * Verifica se há alguma proposta com a situação informanda.
	 * 
	 * @param docente
	 * @param proposta
	 * @return
	 * @throws DAOException
	 */
	public boolean haPropostaSituacao(SituacaoProposta situacao) throws DAOException{
		Query q = getSession().createQuery("from PropostaCursoLato prop where prop.situacaoProposta = :situacao");
		q.setInteger("situacao", situacao.getId());
		if (q.list().isEmpty())
			return false;
		return true;
	}

	@SuppressWarnings("unchecked")
	public Collection<SituacaoProposta> situacoesValidas() throws DAOException{
		Query q = getSession().createQuery("from SituacaoProposta situa where situa.id <> :situacao");
		q.setInteger("situacao", SituacaoProposta.ACEITA);
		return q.list();
	}
	
}