/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 09/07/2007
 *
 */
package br.ufrn.sigaa.monitoria.jsf; 

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.monitoria.dominio.EnvioFrequencia;

/**
 * Classe responsável por controlar o cadastro das configurações
 * para liberação de envio de freqüências pelos monitores.
 * Através deste cadastro a Pró-Reitoria de graduação informa quais os monitores
 * que podem enviar freqüências.
 * 
 * @author Ilueny Santos.
 *
 */
@Component("envioFrequencia") @Scope("session")
public class EnviarFrequenciaMBean extends SigaaAbstractController<EnvioFrequencia> {	

    /**
     * Construtor padrão.
     * 
     */
    public EnviarFrequenciaMBean() {
	obj = new EnvioFrequencia();
    }   

    /**
     * Inicia o cadastro das configurações de frequência que o monitor pode cadastrar.
     * <br />
     * Método chamado pela(s) seguinte(s) JSP(s):
     * <ul>
     *  <li>sigaa.war/monitoria/index.jsp</li>
     * </ul>
     * 
     * @return formulário para o cadastro das novas configurações para envio das freqüências
     * @throws ArqException Prepara o processador o cadastro da configuração de frequência. 
     */
    public String iniciarEnvioFrequencia() throws ArqException {
		prepareMovimento(SigaaListaComando.CADASTRAR_ENVIO_FREQUENCIA);	
		EnvioFrequencia envioFrequencia = getGenericDAO().findAllAtivos(EnvioFrequencia.class, "id").iterator().next();	
		if(ValidatorUtil.isEmpty(envioFrequencia)) {
			this.obj = new EnvioFrequencia();
		} else {	
			this.obj = envioFrequencia;
		}	
		return forward(getFormPage());
    }

    /**
     * Cadastra a configuração da frequência.
     * <br />
     * Método chamado pela(s) seguinte(s) JSP(s):
     * <ul>
     * <li>sigaa.war/monitoria/CancelarBolsas/lista.jsp</li>
     * </ul>
     *  @return Retorna tela mensagem de sucesso ou erro no cadastro.
     * @throws ArqException Operação realizada por membros da Pró-Reitoria de Graduação. 
     */
    public String cadastrar() throws ArqException {
	checkRole(SigaaPapeis.GESTOR_MONITORIA);
	try {
	    ListaMensagens mensagens = obj.validate();	    
	    if (!mensagens.isEmpty()) {
		addMensagens(mensagens);
		return null;
	    }
	    MovimentoCadastro mov = new MovimentoCadastro();
	    mov.setCodMovimento(SigaaListaComando.CADASTRAR_ENVIO_FREQUENCIA);
	    mov.setObjMovimentado(obj);
	    execute(mov, getCurrentRequest());	    
	    addMensagem(MensagensArquitetura.OPERACAO_SUCESSO);
	    return forward("/monitoria/index.jsf");
	} catch (NegocioException e) {
	    addMensagemErro(e.getMessage());
	    return null;
	}
    }	

    /**
     * Retorna o diretório principal do caso de uso.
     * 
     * Não é chamado por JSPs.
     * 
     * @return Página inicial do caso de uso.
     */
    public String getDirBase() {
	return "/monitoria/EnvioFrequencia";
    }

    /**
     * Verifica se o usuário possui papel de gestor monitoria.
     * Não é chamado por JSPs.
     *  @throws SegurancaException somente gestores de monitoria podem realizar esta operação.
     */
    public void checkChangeRole() throws SegurancaException {
	checkRole(SigaaPapeis.GESTOR_MONITORIA);
    }
    
}
