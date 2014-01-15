/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
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
 * <p>Caso de uso criado para os gestores de extensão alterarem os dados dos cadastros de participantes e permitir 
 * consertar cadastros com dados incorretos. Geralmente cadastros migrados. </p>
 * 

 * @author jadson - jadson@info.ufrn.br
 * @vesion 1.0 - criação da classe.
 * @since 21/03/2013
 *
 */
@Component("gerenciarCadastrosParticipantesMBean")
@Scope("request")
public class GerenciarCadastrosParticipantesMBean extends SigaaAbstractController<ParticipanteAcaoExtensao> implements PesquisarParticipanteExtensao {
	
	/**
	 * <p>Inicia o caso de uso para alterar os dados de algum cadastro do participante de extensão.</p>
	 * 
	 * 
	 * @return
	 *  
	 *  <br />
	 * Método chamado pela(s) seguinte(s) JSP(s): 
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
				+" <p>Esta opção permite alterar os dados pessoais dos participantes para os cursos e eventos de extensão. </p>"
				+" <p>Normalmente esses dados não precisam ser alterados, contudo devido ao grande número de cadastros migrados com erro e inconsistências de informações" +
				", foi criada essa operação no sistema que permite realizar consertos para que o participante consiga acessar o sistema. </p>"+
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
