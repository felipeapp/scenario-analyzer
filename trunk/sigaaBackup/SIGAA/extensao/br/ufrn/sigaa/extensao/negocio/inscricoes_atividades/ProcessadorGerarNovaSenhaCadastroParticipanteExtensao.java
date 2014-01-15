/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
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
 *
 * <p> Gera uma nova senha para o usuário </p>
 *
 * <p> <i> A geração de uma nova senha consiste em atualizar o código de acesso e gerar uma nova senha automática para o usuário.
 * Se o usuário confirmar essa alteração é que vai ser gerado o hash da senha automática e atualizado o campo senha para o 
 * usuário pode acessa o sistema com a nova senha</i> </p>
 * 
 * @author jadson
 * @see ProcessadorConfirmaAlteracaoSenhaCadastroParticipanteExtensao
 */
public class ProcessadorGerarNovaSenhaCadastroParticipanteExtensao extends AbstractProcessador{

	
	/**
	 * Ver comentários da classe pai.<br/>
	 *
	 * @see br.ufrn.arq.negocio.ProcessadorComando#execute(br.ufrn.arq.dominio.Movimento)
	 */
	@Override
	public Object execute(Movimento mov) throws NegocioException, ArqException,	RemoteException {
		
		validate(mov);
		
		MovimentoCadastro movimento = (MovimentoCadastro) mov;
		
		CadastroParticipanteAtividadeExtensao cadastro =  movimento.getObjMovimentado();
		
		GenericDAO dao = null;
		
		try{
			
			cadastro.geraCodigoAcessoConfirmacaoInscricao(); // gera novo código de acesso
			cadastro.geraSenhaAutomatica();
			
			dao = getGenericDAO(movimento);
			dao.updateFields(CadastroParticipanteAtividadeExtensao.class, cadastro.getId()
					, new String[]{"codigoAcessoConfirmacao", "senhaGerada"}
				, new Object[]{cadastro.getCodigoAcessoConfirmacao(), cadastro.getSenhaGerada()});
		}finally{
			if(dao != null) dao.close();
		}
		
		return cadastro;
	}

	
	/**
	 * Ver comentários da classe pai.<br/>
	 *
	 * @see br.ufrn.arq.negocio.ProcessadorComando#validate(br.ufrn.arq.dominio.Movimento)
	 */
	@Override
	public void validate(Movimento mov) throws NegocioException, ArqException {
		

		ListaMensagens erros = new ListaMensagens();
		
		MovimentoCadastro movimento = (MovimentoCadastro) mov;
		
		CadastroParticipanteAtividadeExtensao cadastro =  movimento.getObjMovimentado();
		if(! cadastro.isAtivo())
			erros.addErro("O cadastro do participante "+cadastro.getNome()+" não está mais ativo");
		
		checkValidation(erros);
		
	}

}
