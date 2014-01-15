/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em: 29/10/2012
 * 
 */
package br.ufrn.sigaa.extensao.negocio.inscricoes_atividades;

import java.rmi.RemoteException;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.sigaa.extensao.dominio.CadastroParticipanteAtividadeExtensao;

/**
 * <p> Processador confirma altera��o da senha do usu�rio, gerando o hash da senha gerada pelo 
 * sistema e atualizando o campo senha com esse hash</p>
 * 
 * @author jadson
 *
 */
public class ProcessadorConfirmaAlteracaoSenhaCadastroParticipanteExtensao  extends AbstractProcessador{

	/**
	 * Ver coment�rios da classe pai.<br/>
	 *
	 * @see br.ufrn.arq.negocio.ProcessadorComando#execute(br.ufrn.arq.dominio.Movimento)
	 */
	@Override
	public Object execute(Movimento mov) throws NegocioException, ArqException,RemoteException {
		
		validate(mov);
		
		MovimentoCadastro movimento = (MovimentoCadastro) mov;
		
		CadastroParticipanteAtividadeExtensao cadastro =  movimento.getObjMovimentado();
		
		GenericDAO dao = null;
		
		/// Gera o hash da senha gerada pelo sistema e atualiza o campo senha que o usu�rio usa para se logar no sitema ///
		cadastro.setSenha(cadastro.getSenhaGerada());
		cadastro.geraHashSenha();
		//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		
		try{
			dao = getGenericDAO(movimento);
			
			// se o usu�rio confirmar a altera��o de senha, confirma tamb�m o cadastro caso n�o tenha feito isso ainda //
			dao.updateFields(CadastroParticipanteAtividadeExtensao.class, cadastro.getId(), new String[]{"senha", "confirmado"}, new Object[]{cadastro.getSenha(), true});
			
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
		
		MovimentoCadastro movimento = (MovimentoCadastro) mov;
		
		CadastroParticipanteAtividadeExtensao cadastro =  movimento.getObjMovimentado();

		if(! cadastro.isAtivo())
			erros.addErro("O cadastro do participante "+cadastro.getNome()+" n�o est� mais ativo");
		
		checkValidation(erros);
		
	}
	
}
