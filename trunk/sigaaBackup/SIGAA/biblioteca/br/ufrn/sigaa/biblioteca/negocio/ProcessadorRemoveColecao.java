/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
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
 * <p>Processador que remove um coleção do sistema </p>
 *
 * <p> <i> Deve migrar todos os materiais para a nova coleção passada</i> </p>
 * 
 * @author jadson
 *
 */
public class ProcessadorRemoveColecao  extends AbstractProcessador{

	/**
	 * Ver comentários da classe pai.<br/>
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
			
			
			// Migra os materias da coleção removida para nova //
			dao.update("UPDATE biblioteca.material_informacional SET id_colecao = ? WHERE id_colecao  = ? ", new Object[]{novaColecao.getId(), colecao.getId()});
			
		}finally{
			if(dao != null) dao.close();
		}
			
		return null;
	}

	/**
	 * Ver comentários da classe pai.<br/>
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
			erros.addErro("Escolha a coleção para a qual os materiais serão migrados");
		else
			if(! colecaoASerRemovida.isAtivo())
				erros.addErro("A coleção já foi removida.");
		
		
		if (novaColecao != null && colecaoASerRemovida != null && colecaoASerRemovida.getId() == novaColecao.getId())
			erros.addErro("Os materiais não podem ser migrados para a mesma coleção que está sendo removida.");

		checkValidation(erros);
		
	}

}
