/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em: 21/03/2013
 * 
 */
package br.ufrn.sigaa.extensao.jsf.inscricoes_atividades;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.extensao.dominio.CadastroParticipanteAtividadeExtensao;
import br.ufrn.sigaa.extensao.dominio.ParticipanteAcaoExtensao;

/**
 * <p>Caso de uso criado para os gestores de extens�o alterarem os dados dos cadastros de participantes e permitir 
 * consertar cadastros com dados incorretos. Geralmente cadastros migrados. </p>
 * 

 * @author jadson - jadson@info.ufrn.br
 * @vesion 1.0 - cria��o da classe.
 * @since 21/03/2013
 *
 */
@Component("gerenciarCadastrosParticipantesMBean")
@Scope("request")
public class GerenciarCadastrosParticipantesMBean extends SigaaAbstractController<ParticipanteAcaoExtensao> implements PesquisarParticipanteExtensao {
	
	/**
	 * <p>Inicia o caso de uso para alterar os dados de algum cadastro do participante de extens�o.</p>
	 * 
	 * 
	 * @return
	 *  
	 *  <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s): 
	 * <ul>
	 * 		<li>/sigaa.war/extensao/menu.jsp</li>
	 * </ul>
	 * @throws SegurancaException 
	 *
	 */
	public String iniciarAlteracaoCadastroParticipante() throws SegurancaException{
	
		checkRole(SigaaPapeis.COORDENADOR_CURSOS_EVENTOS_PRODUTOS_EXTENSAO, SigaaPapeis.PRESIDENTE_COMITE_EXTENSAO, SigaaPapeis.ADMINISTRADOR_SIGAA );
		
		BuscaPadraoParticipanteExtensaoMBean mBean = (BuscaPadraoParticipanteExtensaoMBean) getMBean("buscaPadraoParticipanteExtensaoMBean");
		return mBean.iniciaBuscaSelecaoParticipanteExtensao(this, "Alterar Dados do Participante",
				" <p>Caro (a) Gestor (a), </p>"
				+" <p>Esta op��o permite alterar os dados pessoais dos participantes para os cursos e eventos de extens�o. </p>"
				+" <p>Normalmente esses dados n�o precisam ser alterados, contudo devido ao grande n�mero de cadastros migrados com erro e inconsist�ncias de informa��es" +
				", foi criada essa opera��o no sistema que permite realizar consertos para que o participante consiga acessar o sistema. </p>"+
				" <br/>", true, true);
		
	}
	
	
	
	
	@Override
	public void setParticipanteExtensao(CadastroParticipanteAtividadeExtensao participante) {
		
	}

	@Override
	public String selecionouParticipanteExtensao() throws ArqException {
		BuscaPadraoParticipanteExtensaoMBean mBean = (BuscaPadraoParticipanteExtensaoMBean) getMBean("buscaPadraoParticipanteExtensaoMBean");
		mBean.buscarParticipante(null);
		return mBean.telaBuscaPadraoParticipantesExtensao();
		
	}

	@Override
	public String cancelarPesquiasParticipanteExtensao() {
		return cancelar();
	}


}
