/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
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
 * Classe respons�vel por controlar o cadastro das configura��es
 * para libera��o de envio de freq��ncias pelos monitores.
 * Atrav�s deste cadastro a Pr�-Reitoria de gradua��o informa quais os monitores
 * que podem enviar freq��ncias.
 * 
 * @author Ilueny Santos.
 *
 */
@Component("envioFrequencia") @Scope("session")
public class EnviarFrequenciaMBean extends SigaaAbstractController<EnvioFrequencia> {	

    /**
     * Construtor padr�o.
     * 
     */
    public EnviarFrequenciaMBean() {
	obj = new EnvioFrequencia();
    }   

    /**
     * Inicia o cadastro das configura��es de frequ�ncia que o monitor pode cadastrar.
     * <br />
     * M�todo chamado pela(s) seguinte(s) JSP(s):
     * <ul>
     *  <li>sigaa.war/monitoria/index.jsp</li>
     * </ul>
     * 
     * @return formul�rio para o cadastro das novas configura��es para envio das freq��ncias
     * @throws ArqException Prepara o processador o cadastro da configura��o de frequ�ncia. 
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
     * Cadastra a configura��o da frequ�ncia.
     * <br />
     * M�todo chamado pela(s) seguinte(s) JSP(s):
     * <ul>
     * <li>sigaa.war/monitoria/CancelarBolsas/lista.jsp</li>
     * </ul>
     *  @return Retorna tela mensagem de sucesso ou erro no cadastro.
     * @throws ArqException Opera��o realizada por membros da Pr�-Reitoria de Gradua��o. 
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
     * Retorna o diret�rio principal do caso de uso.
     * 
     * N�o � chamado por JSPs.
     * 
     * @return P�gina inicial do caso de uso.
     */
    public String getDirBase() {
	return "/monitoria/EnvioFrequencia";
    }

    /**
     * Verifica se o usu�rio possui papel de gestor monitoria.
     * N�o � chamado por JSPs.
     *  @throws SegurancaException somente gestores de monitoria podem realizar esta opera��o.
     */
    public void checkChangeRole() throws SegurancaException {
	checkRole(SigaaPapeis.GESTOR_MONITORIA);
    }
    
}
