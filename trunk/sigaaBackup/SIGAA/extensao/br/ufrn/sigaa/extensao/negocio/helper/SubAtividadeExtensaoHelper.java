/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 20/08/2012
 * 
 */
package br.ufrn.sigaa.extensao.negocio.helper;

import java.text.SimpleDateFormat;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.comum.dominio.UsuarioGeral;
import br.ufrn.sigaa.extensao.dominio.SubAtividadeExtensao;

/**
 *
 * <p> Métodos auxiliares para gerenciar sub atividade de extensão. </p>
 *
 * <p> <i> </i> </p>
 * 
 * @author jadson
 *
 */
public class SubAtividadeExtensaoHelper {

	/** Mensagem padrão quando o usuário tenta alterar uma mini atividade para a qual ele não é coordenador */
	public static final String MENSAGEM_PADRAO_PERMISSAO_COORDENADOR = "O senhor(a) não é o coordenador dessa Mini Atividade, " +
			"ou o período de coordenação expirou. Somente o coordenador da Mini Atividade pode alterá-la ";
	
	
	/** Verifica se o usuário é o coordendor da mini atividade para deixar remover ou alterar os dados.*/
	public static boolean isCoordenadorMiniAtividade(SubAtividadeExtensao  subAtividade, UsuarioGeral usuarioAtual){
		if ( subAtividade.getAtividade().getCoordenacao() == null 
				|| ! subAtividade.getAtividade().getCoordenacao().isCoordenadorAtivo()
				|| subAtividade.getAtividade().getCoordenacao().getPessoa().getId() != usuarioAtual.getPessoa().getId() ){
			return false;
		}
		return true;
	}
	
	
	/** Retorna as mensagens de erros na alteração ou remoção de uma mini atividade. */
	public static ListaMensagens monstaMensagensErroAlteracaoMiniAtividade(SubAtividadeExtensao  subAtividade, UsuarioGeral usuarioAtual){
		
		ListaMensagens erros = new ListaMensagens();
		
		if ( subAtividade.getAtividade().getCoordenacao() == null 
				|| ! subAtividade.getAtividade().getCoordenacao().isCoordenadorAtivo()
				|| subAtividade.getAtividade().getCoordenacao().getPessoa().getId() != usuarioAtual.getPessoa().getId() ){
			
			SimpleDateFormat fomat = new SimpleDateFormat("dd/MM/yyyy");
			
			StringBuilder mensagem = new StringBuilder();
			mensagem.append("Coordenador da Ação: "+subAtividade.getAtividade().getCoordenacao().getPessoa().getNome()+".");
			
			if(subAtividade.getAtividade().getCoordenacao().getDataInicio() != null && subAtividade.getAtividade().getCoordenacao().getDataFim() != null){
				mensagem.append(" Durante o período de "+fomat.format(subAtividade.getAtividade().getCoordenacao().getDataInicio())+" a "+fomat.format(subAtividade.getAtividade().getCoordenacao().getDataFim())+"." );
			}else{
				mensagem.append(" Período da coordenação expirado! ");
			}
			
			erros.addErro(mensagem.toString());
			erros.addErro(MENSAGEM_PADRAO_PERMISSAO_COORDENADOR);
		}
		
		return erros;
	}
	
	
}
