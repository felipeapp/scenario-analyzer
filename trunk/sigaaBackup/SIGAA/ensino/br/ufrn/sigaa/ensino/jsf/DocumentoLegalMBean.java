/*
 * Sistema Integrado de Patrimônio e Administração de Contratos
 * Superintendência de Informática - UFRN
 *
 * Created on 06/07/2010
 *
 */
package br.ufrn.sigaa.ensino.jsf;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.ensino.dominio.DocumentoLegal;

/**
 * Managed Bean para o cadastro de Documentos Legais.
 * 
 * @author Jean Guerethes
 */
@Component @Scope("request")
public class DocumentoLegalMBean extends SigaaAbstractController<DocumentoLegal> {

	/** Construtor padrão */
	public DocumentoLegalMBean() {
		clear();
	}

	/** Limpa os atributos deste controller. */
	protected void clear() {
		obj = new DocumentoLegal();
		obj.setCurso(new Curso());
	}

	/**
	 * Armazena o diretório no qual as view´s estão situadas.
	 * 
	 * JSP: Não invocado po jsp.
	 */
	@Override
	public String getDirBase() {
		return "/ensino/documentoLegal";
	}
	
	/**
	 * Direciona para a tela de listagem.
	 * 
	 * JSP: Não invocado por jsp.
	 */
	@Override
	public String forwardCadastrar() {
		return getListPage();
	}

	/**
	 * Inicializa todo os atributos a serem utilizados no cadastro.
	 * 
	 * JSP: /SIGAA/app/sigaa.ear/sigaa.war/WEB-INF/jsp/ensino/tecnico/menu/curso.jsp
	 */
	@Override
	public String preCadastrar() throws ArqException, NegocioException {
		checkRole(SigaaPapeis.GESTOR_TECNICO);
		clear();
		return super.preCadastrar();
	}
	
	/**
	 * Verificar se o Documento já foi removido anteriormente.
	 * 
	 * JSP: /SIGAA/app/sigaa.ear/sigaa.war/ensino/documentoLegal/lista.jsp
	 */
	@Override
	public String remover() throws ArqException {
		checkRole(SigaaPapeis.GESTOR_TECNICO);
		obj.setId(getParameterInt("id"));
		setObj(getGenericDAO().findByPrimaryKey(obj.getId(), DocumentoLegal.class));
		if (obj == null) {
			forward("/ensino/menus/menu_tecnico.jsf");
			addMensagem(MensagensArquitetura.NAO_HA_OBJETO_REMOCAO);
			return null;
		}else
			prepareMovimento(ArqListaComando.REMOVER);
		return super.remover();
	}
	
	/**
	 * Serve para atualizar as informações do documento legal.
	 * 
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/ensino/documentoLegal/lista.jsp</li>
	 * </ul>
	 */
	@Override
	public String atualizar() throws ArqException {
		checkRole(SigaaPapeis.GESTOR_TECNICO);
		obj.setId(getParameterInt("id"));
		setObj(getGenericDAO().findByPrimaryKey(obj.getId(), DocumentoLegal.class));
		if (obj == null) {
			forward("/ensino/menus/menu_tecnico.jsf");
			addMensagem(MensagensArquitetura.OBJETO_SELECIONADO_FOI_REMOVIDO);
			return null;
		}else
			prepareMovimento(ArqListaComando.REMOVER);
		return super.atualizar();
	}
	
}