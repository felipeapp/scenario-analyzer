/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em: 19/05/2011
 * 
 */
package br.ufrn.sigaa.biblioteca.negocio;

import java.rmi.RemoteException;

import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.sigaa.arq.dao.biblioteca.ColecaoDao;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.Colecao;

/**
 *
 * <p>Processador que remove um cole��o do sistema </p>
 *
 * <p> <i> Deve migrar todos os materiais para a nova cole��o passada</i> </p>
 * 
 * @author jadson
 *
 */
public class ProcessadorRemoveColecao  extends AbstractProcessador{

	/**
	 * Ver coment�rios da classe pai.<br/>
	 *
	 * @see br.ufrn.arq.negocio.ProcessadorComando#execute(br.ufrn.arq.dominio.Movimento)
	 */
	@Override
	public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {
		validate(mov);
		
		MovimentoRemoveColecao movimento = (MovimentoRemoveColecao) mov;
		
		ColecaoDao dao = null;
		
		try{
			
			dao =  getDAO(ColecaoDao.class, mov);
			
			Colecao colecao = movimento.getColecao();
			Colecao novaColecao = movimento.getNovaColecao();
			dao.updateField(Colecao.class, colecao.getId(), "ativo", false);
			
			
			// Migra os materias da cole��o removida para nova //
			dao.update("UPDATE biblioteca.material_informacional SET id_colecao = ? WHERE id_colecao  = ? ", new Object[]{novaColecao.getId(), colecao.getId()});
			
		}finally{
			if(dao != null) dao.close();
		}
			
		return null;
	}

	/**
	 * Ver coment�rios da classe pai.<br/>
	 *
	 * @see br.ufrn.arq.negocio.ProcessadorComando#validate(br.ufrn.arq.dominio.Movimento)
	 */
	@Override
	public void validate(Movimento mov) throws NegocioException, ArqException {

		ListaMensagens erros = new ListaMensagens();

		MovimentoRemoveColecao movimento = (MovimentoRemoveColecao) mov;

		Colecao colecaoASerRemovida = movimento.getColecao();
		Colecao novaColecao = movimento.getNovaColecao();

		if (novaColecao == null || novaColecao.getId() == -1)
			erros.addErro("Escolha a cole��o para a qual os materiais ser�o migrados");
		else
			if(! colecaoASerRemovida.isAtivo())
				erros.addErro("A cole��o j� foi removida.");
		
		
		if (novaColecao != null && colecaoASerRemovida != null && colecaoASerRemovida.getId() == novaColecao.getId())
			erros.addErro("Os materiais n�o podem ser migrados para a mesma cole��o que est� sendo removida.");

		checkValidation(erros);
		
	}

}
