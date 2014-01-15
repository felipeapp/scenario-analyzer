/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
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
 * <p>Processador <strong> *** EXCLUSIVO *** </strong> para o cadastro dos participantes de a��es de extens�o. </p>
 *
 * <p>O cadastro salva as informa��es digitadas pelo usu�rio, cada pessoa ter� 1 e apenas 1 cadastro no sistema. 
 * O cadastro ser� salvo como n�o confirmado. O usu�rio deve acessar a URL gerada pelo sistema enviada por email 
 * e confirmar o cadastro s� ai poder� acessar o sistema.<br/>
 * Isso garante que o e-mail informado n�o seja falso. 
 * </p>
 *
 * <p> <i> Por favor, n�o colocar outras opera��es nessa classe, criar outro precessador !!!!!!!!!  </i> </p>
 * 
 * @author jadson
 *
 */
public class ProcessadorCadastroParticipanteExtensao  extends AbstractProcessador{

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
		
		
		if(cadastro.getCpf() != null && cadastro.getCpf() <= 0){
			cadastro.setCpf(null);
		}
		
		
		// INFORMA��ES IMPORTANTES QUE N�O PODEM SAIR DESSE PROCESSADOR //
		
		if(cadastro.getId() == 0){ // se est� cadastrando um novo
			cadastro.setConfirmado(false);
			cadastro.geraCodigoAcessoConfirmacaoInscricao(); // gera o c�digo de confirma��o
		}
		
		cadastro.geraHashSenha();  // salva a senha informada pelo usu�rio em um Hash MD5
		
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
	 * Ver coment�rios da classe pai.<br/>
	 *
	 * @see br.ufrn.arq.negocio.ProcessadorComando#validate(br.ufrn.arq.dominio.Movimento)
	 */
	@Override
	public void validate(Movimento mov) throws NegocioException, ArqException {
		
		ListaMensagens erros = new ListaMensagens();
		
		MovimentoCadastroParticipanteExtensao movimento = (MovimentoCadastroParticipanteExtensao) mov;
		
		CadastroParticipanteAtividadeExtensao cadastro =  movimento.getCadastroParticipante();
	
		erros.addAll(cadastro.validate()); // Adiciona os erros de preencimento
		
		checkValidation(erros); // Verifica se o usu�rio digitou alguma coisa errada, porque daqui para frente considera que as informa��es foram preenchidas
		
		
		if(! cadastro.getEmail().equalsIgnoreCase(movimento.getEmailConfirmacao()))
			erros.addErro("E-mails n�o conferem.");
		
		if(! cadastro.getSenha().equals(movimento.getSenhaConfirmacao()))
			erros.addErro("Senhas n�o conferem.");
		
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
