/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 26/10/2012
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
 * <p>Processador exclusivo para confirma o cadastro do participante.</p>
 *
 * <p> <i> </i> </p>
 * 
 * @author jadson
 *
 */
public class ProcessadorConfirmaCadastroParticipanteExtensao  extends AbstractProcessador{

	/**
	 * Ver comentários da classe pai.<br/>
	 *
	 * @see br.ufrn.arq.negocio.ProcessadorComando#execute(br.ufrn.arq.dominio.Movimento)
	 */
	
	@Override
	public Object execute(Movimento mov) throws NegocioException, ArqException,RemoteException {
		
		validate(mov);
		
		MovimentoCadastro movimento = (MovimentoCadastro) mov;
		
		CadastroParticipanteAtividadeExtensao cadastro =  movimento.getObjMovimentado();
		
		GenericDAO dao = null;
		
		try{
			dao = getGenericDAO(movimento);
			dao.updateField(CadastroParticipanteAtividadeExtensao.class, cadastro.getId(), "confirmado", true); // confirma o cadastro
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
		
		MovimentoCadastro movimento = (MovimentoCadastro) mov;
		
		CadastroParticipanteAtividadeExtensao cadastro =  movimento.getObjMovimentado();
		
		if(cadastro.isConfirmado())
			erros.addErro("O cadastro do participante "+cadastro.getNome()+" já foi confirmado");
		

		if(! cadastro.isAtivo())
			erros.addErro("O cadastro do participante "+cadastro.getNome()+" não está mais ativo");
		
		checkValidation(erros);
		
	}

}
