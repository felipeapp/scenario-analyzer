/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Criado em 08/02/2011
 *
 */
package br.ufrn.sigaa.ensino.graduacao.jsf;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dominio.Comando;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.negocio.dominio.ComponenteCurricularMov;

/**
 * Controller que permite a edi��o da Ementa e Refer�ncia bibiogr�fica de um
 * componente curricular de um programa de p�s, pelo coordenador do programa.
 * 
 * @author �dipo Elder F. Melo
 * 
 */
@Component("ementaComponenteCurricularMBean")
@Scope("session")
public class EditarEmentaComponenteCurricularMBean extends SigaaAbstractController<ComponenteCurricular> implements SeletorComponenteCurricular{

	/**
	 * Inicia o caso de uso de atualiza��o.<br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/stricto/menu_coordenador.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String iniciarAtualizacaoEmenta() throws ArqException {
		checkChangeRole();
		prepareMovimento(SigaaListaComando.ALTERAR_COMPONENTE_CURRICULAR);
		ComponenteCurricularMBean mBean = getMBean("componenteCurricular");
		return mBean.buscarComponente(this, "Ementas e Refer�ncias de Componentes Curriculares", getProgramaStricto(), false,false, null);
	}

	/**
	 * Cadastra um componente curricular.<br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/graduacao/componente/editar_ementa/form.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 * @throws NegocioException
	 */
	public String cadastrar() throws ArqException, NegocioException {
		checkChangeRole();
		
		if (!confirmaSenha())
			return null;

		if (obj.getTipoAtividadeComplementar() != null
				&& obj.getTipoAtividadeComplementar().getId() == 0)
			obj.setTipoAtividadeComplementar(null);

		erros = new ListaMensagens();
		erros.addAll(obj.validate().getMensagens());

		if (hasErrors())
			return null;

		if (ValidatorUtil.isEmpty(obj.getCurso()))
			obj.setCurso(null);
		
		Comando comando =  SigaaListaComando.ALTERAR_COMPONENTE_CURRICULAR;

		ComponenteCurricularMov mov = new ComponenteCurricularMov();
		mov.setProcessarExpressoes(true);
		mov.setCodMovimento(comando);
		mov.setObjMovimentado(obj);
		try {
			execute(mov);
			addMessage("Componente Curricular alterado com sucesso!", TipoMensagemUFRN.INFORMATION);
			return cancelar();
		} catch (NegocioException e) {
			tratamentoErroPadrao(e);
		} finally {
			removeOperacaoAtiva();
		}
		return null;
	}

	/** Seta o componente o qual ter� a ementa atualizada. <br/>M�todo n�o invocado por JSP�s.
	 * @see br.ufrn.sigaa.ensino.graduacao.jsf.SeletorComponenteCurricular#selecionaComponenteCurricular(br.ufrn.sigaa.ensino.dominio.ComponenteCurricular)
	 */
	@Override
	public String selecionaComponenteCurricular(ComponenteCurricular componente) throws ArqException {
		this.obj = componente;
		if (obj.getSubUnidades() != null) obj.getSubUnidades().iterator();
		return forward("/graduacao/componente/editar_ementa/form.jsp");
	}

	/** Valida o componente o qual ter� a ementa atualizada. <br/>M�todo n�o invocado por JSP�s.
	 * @see br.ufrn.sigaa.ensino.graduacao.jsf.SeletorComponenteCurricular#validarSelecaoComponenteCurricular(br.ufrn.sigaa.ensino.dominio.ComponenteCurricular)
	 */
	@Override
	public ListaMensagens validarSelecaoComponenteCurricular(ComponenteCurricular componente) throws ArqException {
		return null;
	}

	/** Valida se a opera��o � executada pela secretaria de p�s ou coordenador de curso stricto sensu.
	 *  <br/>M�todo n�o invocado por JSP�s.
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#checkChangeRole()
	 */
	@Override
	public void checkChangeRole() throws SegurancaException {
		checkRole(SigaaPapeis.SECRETARIA_POS, SigaaPapeis.COORDENADOR_CURSO_STRICTO);
	}

	@Override
	public String retornarSelecaoComponente() {
		// TODO redirecionar o usu�rio para o formul�rio que invocou a sele��o de componentes.
		return cancelar();
	}
}
