/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
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
 * Processador que existe para implementar a regra de neg�cio do cadastro de uma
 * mensagem de f�rum do n�vel m�dio. Basicamente � contar a quantidade de respostas associadas.
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
	 * Calcula o total de respostas do t�pico do f�rum
	 * @param topico
	 * @param mov
	 * @throws DAOException
	 */
	public void calculaTotalRespostas(ForumMensagemMedio mensagem, Movimento mov) throws DAOException {

		ForumMedioDao dao = getDAO(ForumMedioDao.class, mov);
		
		if ( mensagem.getTopico() != null ) {
			// mensagens com t�pico != null � que s�o coment�rios de t�picos
			dao.updateField(ForumMensagemMedio.class, mensagem.getTopico().getId(), "respostas", 
					dao.findCountMensagensByTopico(mensagem.getTopico().getId()));
			dao.updateField(ForumMensagemMedio.class, mensagem.getTopico().getId(), "ultimaPostagem", new Date());
			
		}
		
		dao.close();
	}

}
