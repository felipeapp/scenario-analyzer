/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
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
 * <p>Processador <strong> *** EXCLUSIVO *** </strong> para o cadastro e alteração dos participantes de ações de extensão
 * realizadas pelos coordenadores ou gestores de extensão. </p>
 *
 * <p>Para o cadastro:</p>
 * <p> <i> Diferente de quando o usuário se cadastra, O cadastro será salvo como confirmado, com senha gerada pelo sistema.
 * Depois de se logar o usuário poderá alterar esse senha gerada.</i> </p>
 * 
 * <p>Para o alteração:</p>
 * <p> Na alteração o que tem de diferente é que caso o gestor informe uma nova senha ela será atualiza, senão vai continuar 
 * com a senha antiga.</p>
 * 
 * 
 * @author jadson
 * @see ProcessadorCadastroParticipanteExtensao
 */
public class ProcessadorCadastroParticipanteExtensaoByGestores extends AbstractProcessador{

	/**
	 * Ver comentários da classe pai.<br/>
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
		
			// INFORMAÇÕES IMPORTANTES QUE NÃO PODEM SAIR DESSE PROCESSADOR //
			
			if(cadastro.getId() == 0 ){
				cadastro.setConfirmado(true);  // quando o coordenador cadastra, fica já como confirmado
				cadastro.geraSenhaAutomatica(); 
				cadastro.setSenha(cadastro.getSenhaGerada()); // nesse caso  senha = senha gerada, até o usuário trocar.
				cadastro.geraHashSenha();  // salva a senha informada pelo usuário em um Hash MD5
			}else{
				
				if(movimento.isRedefinidoSenha()){ // se é permitido alterar a senha
					if( StringUtils.isNotEmpty(cadastro.getSenha() ) ){ // se o gestor digitou a senha do participante na alteração do cadastro
						cadastro.geraHashSenha();
					}else{                                             // se não, deixa a senha atual
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
	 * Ver comentários da classe pai.<br/>
	 *
	 * @see br.ufrn.arq.negocio.ProcessadorComando#validate(br.ufrn.arq.dominio.Movimento)
	 */
	@Override
	public void validate(Movimento mov) throws NegocioException, ArqException {
		
		ListaMensagens erros = new ListaMensagens();
		
		MovimentoCadastroParticipanteExtensao movimento = (MovimentoCadastroParticipanteExtensao) mov;
		
		CadastroParticipanteAtividadeExtensao cadastro =  movimento.getCadastroParticipante();
	
		erros.addAll(cadastro.validateCadastroByGestor()); // Adiciona os erros de preencimento		
		
		
		if(movimento.isRedefinidoSenha()){ // se é permitido alterar a senha
			if( StringUtils.isNotEmpty(cadastro.getSenha() ) ){ // se o gestor digitou a senha do participante, na alteração do cadastro
				ListaMensagens errosSenha = cadastro.validateSenha(); // só para para validar nesse momento!
				checkValidation(errosSenha);
			}
		}
		
		checkValidation(erros); // Verifica se o usuário digitou alguma coisa errada, porque daqui para frente considera que as informações foram preenchidas
		
		
		if(cadastro.getId() == 0 && ! cadastro.getEmail().equals(movimento.getEmailConfirmacao()))
			erros.addErro("E-mails não conferem.");
		
		checkValidation(erros); // Retorna desse ponto se tiver erro 
		
		
		//////// Verifica os possíveis cadastro repetidos /////////
		
		GenericDAO dao = null;
		
		try{
			dao = getGenericDAO(movimento);
			
			if(cadastro.isEstrangeiro()){
				
				int qtdCadastrosByPassaporte = dao.count(" FROM extensao.cadastro_participante_atividade_extensao cadastro " +
						" WHERE passaporte = '"+StringUtils.escapeBackSlash(cadastro.getPassaporte())
						+"' AND data_nascimento = '"+cadastro.getDataNascimento()
						+"' AND ativo = trueValue() AND confirmado = trueValue() AND cadastro.id_cadastro_participante_atividade_extensao <> "+cadastro.getId());
				
				if(qtdCadastrosByPassaporte > 0 )
					erros.addErro("Já existe um cadastro para o passaporte: "+cadastro.getPassaporte()+" e com a data de nascimento: "+cadastro.getDataNascimento());
			}else{
				
				int qtdCadastrosByCPF = dao.count(" FROM extensao.cadastro_participante_atividade_extensao cadastro WHERE cpf = '"+cadastro.getCpf()+"' "
						+" AND ativo = trueValue() AND confirmado = trueValue() AND cadastro.id_cadastro_participante_atividade_extensao <> "+cadastro.getId());
				
				if(qtdCadastrosByCPF > 0 )
					erros.addErro("Já existe um cadastro para o CPF: "+cadastro.getCpf());
			}
			
				
			int qtdCadastrosBYEmail = dao.count(" FROM extensao.cadastro_participante_atividade_extensao cadastro WHERE email = '"+StringUtils.escapeBackSlash(cadastro.getEmail())+"' "
					+" AND ativo = trueValue() AND confirmado = trueValue() AND cadastro.id_cadastro_participante_atividade_extensao <> "+cadastro.getId() );
			
			if(qtdCadastrosBYEmail > 0 ){
				erros.addErro("Já existe um cadastro para o E-mail: "+cadastro.getEmail());
			}
			
		}finally{
			
			checkValidation(erros); // Verifica as outras regras de negócio que precisam consultar a base.
			
			if(dao != null) dao.close();
		}
	}
	
}
