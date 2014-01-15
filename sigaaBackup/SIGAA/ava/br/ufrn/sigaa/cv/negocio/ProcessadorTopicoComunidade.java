package br.ufrn.sigaa.cv.negocio;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.rmi.RemoteException;
import java.util.List;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.cv.dominio.TopicoComunidade;

/**
 * Processador responsável por operações sobre tópicos da comunidade.
 * 
 * @author Diego Jácome.
 *
 */
public class ProcessadorTopicoComunidade  extends AbstractProcessador{

	@Override
	public Object execute(Movimento mov) throws NegocioException, ArqException,	RemoteException {

		if (mov.getCodMovimento().equals(SigaaListaComando.ESCONDE_TOPICOS_CV)) 
			return esconderTopicos(mov);
		else if  (mov.getCodMovimento().equals(SigaaListaComando.EXIBIR_TOPICOS_CV)) 
			return exibirTopicos(mov);
		
		return null;
	}
	

	/**
	 * Esconde tópico da comunidade, juntamente com seus filhos.
	 * @param mov
	 * @throws DAOException
	 *
	 */
	private Object esconderTopicos(Movimento mov) throws DAOException {

		MovimentoCadastroCv cmov = (MovimentoCadastroCv) mov;
		GenericDAO dao = null;
		
		try {
			dao = getGenericDAO(mov);
			TopicoComunidade topicoPai = cmov.getObjMovimentado();			
			esconderExibirTopicos(dao, topicoPai, false);		
			return null;
		}finally {
			if ( dao != null )
				dao.close();
		}	
	}

	/**
	 * Exibe tópico da comunidade, juntamente com seus filhos.
	 * @param mov
	 * @throws DAOException
	 *
	 */
	private Object exibirTopicos(Movimento mov) throws DAOException {

		MovimentoCadastroCv cmov = (MovimentoCadastroCv) mov;
		GenericDAO dao = null;
		
		try {
			dao = getGenericDAO(mov);
			TopicoComunidade topicoPai = cmov.getObjMovimentado();			
			esconderExibirTopicos(dao, topicoPai, true);		
			return null;
		}finally {
			if ( dao != null )
				dao.close();
		}	
	}

	/**
	 * Exibe ou esconde tópico da comunidade, juntamente com seus filhos.
	 * @param dao
	 * @param topicoPai
	 * @param visivel
	 * @throws DAOException
	 *
	 */
	private void esconderExibirTopicos(GenericDAO dao,TopicoComunidade topicoPai, boolean visivel) throws DAOException {
		List<TopicoComunidade> topicosFilhos = (List<TopicoComunidade>) dao.findByExactField(TopicoComunidade.class, "topicoPai.id", topicoPai.getId());
		
		if ( !isEmpty(topicosFilhos) ) 
			for (TopicoComunidade topicoFilho : topicosFilhos) 
				esconderExibirTopicos(dao, topicoFilho, visivel);	
		dao.updateField(TopicoComunidade.class, topicoPai.getId(), "visivel", visivel);

	}

	@Override
	public void validate(Movimento mov) throws NegocioException, ArqException {
		// TODO Auto-generated method stub
		
	}
}
