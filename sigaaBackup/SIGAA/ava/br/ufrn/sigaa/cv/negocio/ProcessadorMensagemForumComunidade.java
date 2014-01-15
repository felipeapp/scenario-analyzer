/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas
 * Superintendência de Informática - UFRN
 */
package br.ufrn.sigaa.cv.negocio;

import java.rmi.RemoteException;
import java.util.Date;

import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.negocio.ProcessadorCadastro;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ava.cv.dao.ForumComunidadeDao;
import br.ufrn.sigaa.cv.dominio.ForumMensagemComunidade;

/**
 * Processador que existe para implementar a regra de negócio do cadastro de uma
 * mensagem. Basicamente é contar a quantidade de respostas associadas.
 * 
 * @author Gleydson
 * 
 */
public class ProcessadorMensagemForumComunidade extends ProcessadorCadastro {

	@Override
	public Object execute(Movimento movimento) throws NegocioException, ArqException, RemoteException {
		MovimentoCadastro mov = (MovimentoCadastro) movimento;
		validate(mov);
		Object obj = null;

		if (mov.getCodMovimento().equals(SigaaListaComando.CADASTRAR_TOPICO_FORUM_COMUNIDADE)) {
			obj = criar(mov);
		} else if (mov.getCodMovimento().equals(ArqListaComando.ALTERAR)) {
			obj = alterar(mov);
		} else if (mov.getCodMovimento().equals(SigaaListaComando.REMOVER_TOPICO_FORUM_COMUNIDADE)) {
			obj = remover(mov);
		} else if (mov.getCodMovimento().equals(ArqListaComando.DESATIVAR)) {
			obj = desativar(mov);
		}

		return obj;
	}
	
	@Override
	protected Object remover(MovimentoCadastro mov) throws DAOException, NegocioException, ArqException {
		calculaTotalRespostas((ForumMensagemComunidade) mov.getObjMovimentado(), mov, false);
		return super.remover(mov);
	}
	
	@Override
	protected Object criar(MovimentoCadastro mov) throws DAOException, NegocioException, ArqException {
		calculaTotalRespostas((ForumMensagemComunidade) mov.getObjMovimentado(), mov, true);
		return super.criar(mov);
	}

	/**
	 * Calcula o total de respostas do tópico do fórum
	 * @param topico
	 * @param mov
	 * @throws DAOException
	 */
	public void calculaTotalRespostas(ForumMensagemComunidade mensagem, Movimento mov, boolean adionarRemover) throws DAOException {

		ForumComunidadeDao dao = getDAO(ForumComunidadeDao.class, mov);
		
		if ( mensagem.getTopico() != null ) {
			// mensagens com tópico != null é que são comentários de tópicos
			if (adionarRemover)
				dao.updateField(ForumMensagemComunidade.class, mensagem.getTopico().getId(), "respostas", dao.findCountMensagensByTopicoAdicao(mensagem.getTopico().getId()));
			else
				dao.updateField(ForumMensagemComunidade.class, mensagem.getTopico().getId(), "respostas", dao.findCountMensagensByTopicoRemocao(mensagem.getTopico().getId()));
					
			dao.updateField(ForumMensagemComunidade.class, mensagem.getTopico().getId(), "ultimaPostagem", new Date());
		}
		
		dao.close();
	}

}
