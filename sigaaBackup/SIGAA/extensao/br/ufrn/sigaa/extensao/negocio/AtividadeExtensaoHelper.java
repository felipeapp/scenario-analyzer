/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
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
 * Classe que auxilia a execução da tarefas comuns ao sistema de Extensão.
 * 
 * @author Ilueny Santos
 *
 */
public class AtividadeExtensaoHelper {

	/**
	 * Método utilizado na geração do código de uma ação de extensão.
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
		
		// Retorna a próxima seq. disponível
		int next = dao.findNextSequencia(ano, tipoAtividade);
		if (next == 0){
			next++;
			// cria nova seq. iniciando em 1
			dao.novaSequencia(ano, tipoAtividade);
		}					
		result = new Integer(next);
		
		// Atualiza o banco com o valor da próxima seq. disponível.
		dao.updateNextSequencia(ano, tipoAtividade, ++next);
		
		return result;
	}

}
