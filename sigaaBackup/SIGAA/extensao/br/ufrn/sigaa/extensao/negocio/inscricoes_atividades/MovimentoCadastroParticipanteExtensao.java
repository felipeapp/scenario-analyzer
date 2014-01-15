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

import br.ufrn.arq.dominio.AbstractMovimentoAdapter;
import br.ufrn.sigaa.extensao.dominio.CadastroParticipanteAtividadeExtensao;

/**
 * <p> Passa os dados para o processador que o próprio usuário se cadastra e também para 
 * o precesador que o coordenador cadastra o usuário.</p>
 * 
 * @author jadson
 *
 */
public class MovimentoCadastroParticipanteExtensao extends AbstractMovimentoAdapter{

	/** Os dados do participante que será cadastrado no sistema.*/
	private CadastroParticipanteAtividadeExtensao cadastroParticipante;

	/** O email e senha de confirmado informados pelo participante no cadastro. Para gerantir que informarão corretamente.*/
	private  String emailConfirmacao, senhaConfirmacao;
	
	/** Os gestores do sistema podem alterar o cadastro do participante*/
	private boolean alterandoCadastro = false;
	
	/** Se o gestor pode redifinir a senha, nesse caso tem que validar se ele digitou algo, se deixou em branco continua a senha atual. */
	private boolean isRedefinidoSenha = false;
	
	
	public MovimentoCadastroParticipanteExtensao(CadastroParticipanteAtividadeExtensao cadastroParticipante, String emailConfirmacao, String senhaConfirmacao) {
		this.cadastroParticipante = cadastroParticipante;
		this.emailConfirmacao = emailConfirmacao;
		this.senhaConfirmacao = senhaConfirmacao;
		alterandoCadastro = false;
	}
	
	/***
	 * Construtor quando os gestores do sisteam vão alterar um cadastros já existente.
	 * @param cadastroParticipante
	 */
	public MovimentoCadastroParticipanteExtensao(CadastroParticipanteAtividadeExtensao cadastroParticipante,  boolean isRedefinidoSenha) {
		this.cadastroParticipante = cadastroParticipante;
		alterandoCadastro = true;
		this.isRedefinidoSenha = isRedefinidoSenha;
	}

	
	public CadastroParticipanteAtividadeExtensao getCadastroParticipante() {
		return cadastroParticipante;
	}

	public String getEmailConfirmacao() {
		return emailConfirmacao;
	}

	public String getSenhaConfirmacao() {
		return senhaConfirmacao;
	}

	public boolean isAlterandoCadastro() {
		return alterandoCadastro;
	}

	public boolean isRedefinidoSenha() {
		return isRedefinidoSenha;
	}
	
}
