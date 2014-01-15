package br.ufrn.sigaa.ensino.graduacao.jsf;

import static br.ufrn.arq.mensagens.MensagensArquitetura.OPERACAO_SUCESSO;
import static br.ufrn.arq.util.ValidatorUtil.isEmpty;
import static br.ufrn.arq.util.ValidatorUtil.validateRequired;

import java.util.Collection;
import java.util.LinkedList;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.LimiteResultadosException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.dao.ensino.DisciplinaDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.vestibular.dominio.ObjectSeletor;

/**
 * Controller respons�vel por processar a inativa��o de componentes curriculares
 * de um departamento.
 * 
 * @author �dipo Elder F. de Melo
 * 
 */
@Component("inativarComponentesDepartamentoMBean")
@Scope("request")
public class InativarComponentesDepartamentoMBean extends SigaaAbstractController<ComponenteCurricular> {
	
	
	/** Lista de componentes curriculares selecion�veis para a inativa��o. */
	private Collection<ObjectSeletor<ComponenteCurricular>> componentes;
	
	/**  Construtor padr�o. */
	public InativarComponentesDepartamentoMBean() {
		obj = new ComponenteCurricular();
	}
	
	/**
	 * Inicia a inativa��o dos componentes curriculares de um departamento.<br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/graduacao/menus/cdp.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String iniciar() throws ArqException {
		checkChangeRole();
		prepareMovimento(SigaaListaComando.INATIVAR_COMPONENTES_CURRICULARES_DEPARTAMENTO);
		setOperacaoAtiva(SigaaListaComando.INATIVAR_COMPONENTES_CURRICULARES_DEPARTAMENTO.getId());
		return formBusca();
	}

	/**
	 * Busca por componentes curriculares de um departamento.<br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/graduacao/componente/inativar_em_lote/form.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws LimiteResultadosException
	 * @throws DAOException
	 * @throws SegurancaException
	 */
	public String buscarComponentes() throws LimiteResultadosException, DAOException, SegurancaException {
		checkChangeRole();
		if (!checkOperacaoAtiva(SigaaListaComando.INATIVAR_COMPONENTES_CURRICULARES_DEPARTAMENTO.getId()))
			return null;
		validateRequired(obj.getUnidade(), "Departamento", erros);
		if (hasErrors()) return null;
		DisciplinaDao dao = getDAO(DisciplinaDao.class);
		resultadosBusca = dao.findCompleto(null, null, null, null, null, null,
				null, null, null,obj.getUnidade(), getNivelEnsino(), false,
				false,false, null);
		if (isEmpty(resultadosBusca)) {
			addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
		} else {
			componentes = new LinkedList<ObjectSeletor<ComponenteCurricular>>();
			for (ComponenteCurricular componente : resultadosBusca) {
				ObjectSeletor<ComponenteCurricular> s = new ObjectSeletor<ComponenteCurricular>(componente);
				componentes.add(s);
			}
		}
		return null;
	}
	
	/**
	 * Verifica os componentes selecionados para o usu�rio e exibe para
	 * confirma��o da opera��o.<br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/graduacao/componente/inativar_em_lote/form.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws SegurancaException
	 */
	public String confirmarInativacao() throws SegurancaException {
		checkChangeRole();
		if (!checkOperacaoAtiva(SigaaListaComando.INATIVAR_COMPONENTES_CURRICULARES_DEPARTAMENTO.getId()))
			return null;
		Collection<ComponenteCurricular> selecionados = new LinkedList<ComponenteCurricular>();
		for (ObjectSeletor<ComponenteCurricular> seletor : componentes) {
			if (seletor.isSelecionado())
				selecionados.add(seletor.getObjeto());
		}
		if (isEmpty(selecionados)) {
			addMensagemErro("Selecione um ou mais componentes curriculares na lista.");
			return null;
		}
		resultadosBusca = selecionados;
		return formConfirmacao();
	}
	
	/** Inativa os componentes curriculares selecionados.<br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/graduacao/componente/inativar_em_lote/confirma.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public String inativar() throws ArqException {
		checkChangeRole();
		if (!checkOperacaoAtiva(SigaaListaComando.INATIVAR_COMPONENTES_CURRICULARES_DEPARTAMENTO.getId()))
			return null;
		if (!confirmaSenha()) return null;
		try {
			MovimentoCadastro mov = new MovimentoCadastro();
			mov.setColObjMovimentado(resultadosBusca);
			mov.setCodMovimento(SigaaListaComando.INATIVAR_COMPONENTES_CURRICULARES_DEPARTAMENTO);
			execute(mov);
			addMensagem(OPERACAO_SUCESSO);
		} catch (Exception e) {
			notifyError(e);
			addMensagemErroPadrao();
			return forward(formBusca());
		}
		removeOperacaoAtiva();
		return cancelar();
	}
	
	/**
	 * Verifica as permiss�es para inativar componentes curriculares.<br/>
	 * Pap�is autorizados: SigaaPapeis.CDP, SigaaPapeis.ADMINISTRADOR_DAE <br/>
	 * M�todo n�o invocado por JSP�s
	 * 
	 * @throws SegurancaException
	 */
	@Override
	public void checkChangeRole() throws SegurancaException {
		checkRole(SigaaPapeis.CDP, SigaaPapeis.ADMINISTRADOR_DAE);
	}
	
	/** Redireciona o usu�rio para o formul�rio de busca de componentes curriculares.<br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/graduacao/componente/inativar_em_lote/confirma.jsp</li>
	 * </ul>
	 * @return
	 */
	public String formBusca() {
		return forward("/graduacao/componente/inativar_em_lote/form.jsp");
	}
	
	/**Redireciona o usu�rio para o formul�rio de confirma��o de inativa��o de componentes curriculares.<br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/graduacao/componente/inativar_em_lote/form.jsp</li>
	 * </ul>
	 * @return
	 */
	public String formConfirmacao() {
		return forward("/graduacao/componente/inativar_em_lote/confirma.jsp");
	}

	/** Retorna a Redireciona o usu�rio para o formul�rio de busca de componentes curriculares.
	 * @return
	 */
	public Collection<ObjectSeletor<ComponenteCurricular>> getComponentes() {
		return componentes;
	}

	/** Seta a Redireciona o usu�rio para o formul�rio de busca de componentes curriculares.
	 * @param componentes
	 */
	public void setComponentes(Collection<ObjectSeletor<ComponenteCurricular>> componentes) {
		this.componentes = componentes;
	}
}
