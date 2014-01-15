/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em 20/09/2011
 * Autor: Rafael Gomes
 */
package br.ufrn.sigaa.ensino.medio.negocio;

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
import br.ufrn.sigaa.ensino.medio.dao.ForumMedioDao;
import br.ufrn.sigaa.ensino.medio.dominio.ForumMensagemMedio;

/**
 * Processador que existe para implementar a regra de negócio do cadastro de uma
 * mensagem de fórum do nível médio. Basicamente é contar a quantidade de respostas associadas.
 * 
 * @author Rafael Gomes
 * 
 */
public class ProcessadorMensagemForumMedio extends ProcessadorCadastro {

	@Override
	public Object execute(Movimento movimento) throws NegocioException, ArqException, RemoteException {
		MovimentoCadastro mov = (MovimentoCadastro) movimento;
		validate(mov);
		Object obj = null;

		if (mov.getCodMovimento().equals(SigaaListaComando.CADASTRAR_TOPICO_FORUM_MEDIO)) {
			obj = criar(mov);
		} else if (mov.getCodMovimento().equals(ArqListaComando.ALTERAR)) {
			obj = alterar(mov);
		} else if (mov.getCodMovimento().equals(ArqListaComando.REMOVER)) {
			obj = remover(mov);
		} else if (mov.getCodMovimento().equals(ArqListaComando.DESATIVAR)) {
			obj = desativar(mov);
		}

		return obj;
	}
	
	@Override
	protected Object criar(MovimentoCadastro mov) throws DAOException,
			NegocioException, ArqException {
		calculaTotalRespostas((ForumMensagemMedio) mov.getObjMovimentado(), mov);
		return super.criar(mov);
	}

	/**
	 * Calcula o total de respostas do tópico do fórum
	 * @param topico
	 * @param mov
	 * @throws DAOException
	 */
	public void calculaTotalRespostas(ForumMensagemMedio mensagem, Movimento mov) throws DAOException {

		ForumMedioDao dao = getDAO(ForumMedioDao.class, mov);
		
		if ( mensagem.getTopico() != null ) {
			// mensagens com tópico != null é que são comentários de tópicos
			dao.updateField(ForumMensagemMedio.class, mensagem.getTopico().getId(), "respostas", 
					dao.findCountMensagensByTopico(mensagem.getTopico().getId()));
			dao.updateField(ForumMensagemMedio.class, mensagem.getTopico().getId(), "ultimaPostagem", new Date());
			
		}
		
		dao.close();
	}

}
