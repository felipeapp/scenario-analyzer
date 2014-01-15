/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em: 05/12/2012
 * 
 */
package br.ufrn.sigaa.extensao.negocio.inscricoes_atividades;

import java.rmi.RemoteException;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.sigaa.extensao.dominio.CadastroParticipanteAtividadeExtensao;

/**
 *
 * <p>Processador <strong> *** EXCLUSIVO *** </strong> para o cadastro e altera��o dos participantes de a��es de extens�o
 * realizadas pelos coordenadores ou gestores de extens�o. </p>
 *
 * <p>Para o cadastro:</p>
 * <p> <i> Diferente de quando o usu�rio se cadastra, O cadastro ser� salvo como confirmado, com senha gerada pelo sistema.
 * Depois de se logar o usu�rio poder� alterar esse senha gerada.</i> </p>
 * 
 * <p>Para o altera��o:</p>
 * <p> Na altera��o o que tem de diferente � que caso o gestor informe uma nova senha ela ser� atualiza, sen�o vai continuar 
 * com a senha antiga.</p>
 * 
 * 
 * @author jadson
 * @see ProcessadorCadastroParticipanteExtensao
 */
public class ProcessadorCadastroParticipanteExtensaoByGestores extends AbstractProcessador{

	/**
	 * Ver coment�rios da classe pai.<br/>
	 *
	 * @see br.ufrn.arq.negocio.ProcessadorComando#execute(br.ufrn.arq.dominio.Movimento)
	 */
	@Override
	public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {
		
		MovimentoCadastroParticipanteExtensao movimento = (MovimentoCadastroParticipanteExtensao) mov;
		
		validate(movimento);
		
		CadastroParticipanteAtividadeExtensao cadastro =  movimento.getCadastroParticipante();
		
		GenericDAO dao = null;
		
		try{
			dao = getGenericDAO(movimento);
		
			// INFORMA��ES IMPORTANTES QUE N�O PODEM SAIR DESSE PROCESSADOR //
			
			if(cadastro.getId() == 0 ){
				cadastro.setConfirmado(true);  // quando o coordenador cadastra, fica j� como confirmado
				cadastro.geraSenhaAutomatica(); 
				cadastro.setSenha(cadastro.getSenhaGerada()); // nesse caso  senha = senha gerada, at� o usu�rio trocar.
				cadastro.geraHashSenha();  // salva a senha informada pelo usu�rio em um Hash MD5
			}else{
				
				if(movimento.isRedefinidoSenha()){ // se � permitido alterar a senha
					if( StringUtils.isNotEmpty(cadastro.getSenha() ) ){ // se o gestor digitou a senha do participante na altera��o do cadastro
						cadastro.geraHashSenha();
					}else{                                             // se n�o, deixa a senha atual
						CadastroParticipanteAtividadeExtensao cBanco = dao.findByPrimaryKey(cadastro.getId() , CadastroParticipanteAtividadeExtensao.class, "senha");
						cadastro.setSenha(cBanco.getSenha());
						dao.detach(cBanco);
					}
				}
			}
		
		
		///////////////////////////////////////////////////////////////////
		
			cadastro.anulaDadosTransientes();
			
			dao.createOrUpdate(cadastro);
			
		}finally{
			if(dao != null) dao.close();
		}
		
		return cadastro;
	}

	
	
	/**
	 * Ver coment�rios da classe pai.<br/>
	 *
	 * @see br.ufrn.arq.negocio.ProcessadorComando#validate(br.ufrn.arq.dominio.Movimento)
	 */
	@Override
	public void validate(Movimento mov) throws NegocioException, ArqException {
		
		ListaMensagens erros = new ListaMensagens();
		
		MovimentoCadastroParticipanteExtensao movimento = (MovimentoCadastroParticipanteExtensao) mov;
		
		CadastroParticipanteAtividadeExtensao cadastro =  movimento.getCadastroParticipante();
	
		erros.addAll(cadastro.validateCadastroByGestor()); // Adiciona os erros de preencimento		
		
		
		if(movimento.isRedefinidoSenha()){ // se � permitido alterar a senha
			if( StringUtils.isNotEmpty(cadastro.getSenha() ) ){ // se o gestor digitou a senha do participante, na altera��o do cadastro
				ListaMensagens errosSenha = cadastro.validateSenha(); // s� para para validar nesse momento!
				checkValidation(errosSenha);
			}
		}
		
		checkValidation(erros); // Verifica se o usu�rio digitou alguma coisa errada, porque daqui para frente considera que as informa��es foram preenchidas
		
		
		if(cadastro.getId() == 0 && ! cadastro.getEmail().equals(movimento.getEmailConfirmacao()))
			erros.addErro("E-mails n�o conferem.");
		
		checkValidation(erros); // Retorna desse ponto se tiver erro 
		
		
		//////// Verifica os poss�veis cadastro repetidos /////////
		
		GenericDAO dao = null;
		
		try{
			dao = getGenericDAO(movimento);
			
			if(cadastro.isEstrangeiro()){
				
				int qtdCadastrosByPassaporte = dao.count(" FROM extensao.cadastro_participante_atividade_extensao cadastro " +
						" WHERE passaporte = '"+StringUtils.escapeBackSlash(cadastro.getPassaporte())
						+"' AND data_nascimento = '"+cadastro.getDataNascimento()
						+"' AND ativo = trueValue() AND confirmado = trueValue() AND cadastro.id_cadastro_participante_atividade_extensao <> "+cadastro.getId());
				
				if(qtdCadastrosByPassaporte > 0 )
					erros.addErro("J� existe um cadastro para o passaporte: "+cadastro.getPassaporte()+" e com a data de nascimento: "+cadastro.getDataNascimento());
			}else{
				
				int qtdCadastrosByCPF = dao.count(" FROM extensao.cadastro_participante_atividade_extensao cadastro WHERE cpf = '"+cadastro.getCpf()+"' "
						+" AND ativo = trueValue() AND confirmado = trueValue() AND cadastro.id_cadastro_participante_atividade_extensao <> "+cadastro.getId());
				
				if(qtdCadastrosByCPF > 0 )
					erros.addErro("J� existe um cadastro para o CPF: "+cadastro.getCpf());
			}
			
				
			int qtdCadastrosBYEmail = dao.count(" FROM extensao.cadastro_participante_atividade_extensao cadastro WHERE email = '"+StringUtils.escapeBackSlash(cadastro.getEmail())+"' "
					+" AND ativo = trueValue() AND confirmado = trueValue() AND cadastro.id_cadastro_participante_atividade_extensao <> "+cadastro.getId() );
			
			if(qtdCadastrosBYEmail > 0 ){
				erros.addErro("J� existe um cadastro para o E-mail: "+cadastro.getEmail());
			}
			
		}finally{
			
			checkValidation(erros); // Verifica as outras regras de neg�cio que precisam consultar a base.
			
			if(dao != null) dao.close();
		}
	}
	
}
