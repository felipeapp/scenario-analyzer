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
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.sigaa.extensao.dominio.CadastroParticipanteAtividadeExtensao;

/**
 *
 * <p>Processador <strong> *** EXCLUSIVO *** </strong> para o cadastro dos participantes de ações de extensão. </p>
 *
 * <p>O cadastro salva as informações digitadas pelo usuário, cada pessoa terá 1 e apenas 1 cadastro no sistema. 
 * O cadastro será salvo como não confirmado. O usuário deve acessar a URL gerada pelo sistema enviada por email 
 * e confirmar o cadastro só ai poderá acessar o sistema.<br/>
 * Isso garante que o e-mail informado não seja falso. 
 * </p>
 *
 * <p> <i> Por favor, não colocar outras operações nessa classe, criar outro precessador !!!!!!!!!  </i> </p>
 * 
 * @author jadson
 *
 */
public class ProcessadorCadastroParticipanteExtensao  extends AbstractProcessador{

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
		
		
		if(cadastro.getCpf() != null && cadastro.getCpf() <= 0){
			cadastro.setCpf(null);
		}
		
		
		// INFORMAÇÕES IMPORTANTES QUE NÃO PODEM SAIR DESSE PROCESSADOR //
		
		if(cadastro.getId() == 0){ // se está cadastrando um novo
			cadastro.setConfirmado(false);
			cadastro.geraCodigoAcessoConfirmacaoInscricao(); // gera o código de confirmação
		}
		
		cadastro.geraHashSenha();  // salva a senha informada pelo usuário em um Hash MD5
		
		///////////////////////////////////////////////////////////////////
		
		GenericDAO dao = null;
		
		try{
			dao = getGenericDAO(movimento);
			
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
	
		erros.addAll(cadastro.validate()); // Adiciona os erros de preencimento
		
		checkValidation(erros); // Verifica se o usuário digitou alguma coisa errada, porque daqui para frente considera que as informações foram preenchidas
		
		
		if(! cadastro.getEmail().equalsIgnoreCase(movimento.getEmailConfirmacao()))
			erros.addErro("E-mails não conferem.");
		
		if(! cadastro.getSenha().equals(movimento.getSenhaConfirmacao()))
			erros.addErro("Senhas não conferem.");
		
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
