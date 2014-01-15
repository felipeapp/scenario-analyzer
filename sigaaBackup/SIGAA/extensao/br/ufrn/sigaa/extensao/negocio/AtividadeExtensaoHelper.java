/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 * 
 * Created on 20/01/2010
 *
 */
package br.ufrn.sigaa.extensao.negocio;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.extensao.AtividadeExtensaoDao;
import br.ufrn.sigaa.extensao.dominio.AtividadeExtensao;

/**
 * Classe que auxilia a execu��o da tarefas comuns ao sistema de Extens�o.
 * 
 * @author Ilueny Santos
 *
 */
public class AtividadeExtensaoHelper {

	/**
	 * M�todo utilizado na gera��o do c�digo de uma a��o de extens�o.
	 * 
	 * @param acao
	 * @param dao
	 * @return
	 * @throws DAOException
	 */
	public static int gerarCodigoAcaoExtensao(AtividadeExtensao acao, AtividadeExtensaoDao dao) throws DAOException {
	    	int result = 0;
		int ano = acao.getAno();
		int tipoAtividade = acao.getTipoAtividadeExtensao().getId();
		
		// Retorna a pr�xima seq. dispon�vel
		int next = dao.findNextSequencia(ano, tipoAtividade);
		if (next == 0){
			next++;
			// cria nova seq. iniciando em 1
			dao.novaSequencia(ano, tipoAtividade);
		}					
		result = new Integer(next);
		
		// Atualiza o banco com o valor da pr�xima seq. dispon�vel.
		dao.updateNextSequencia(ano, tipoAtividade, ++next);
		
		return result;
	}

}
