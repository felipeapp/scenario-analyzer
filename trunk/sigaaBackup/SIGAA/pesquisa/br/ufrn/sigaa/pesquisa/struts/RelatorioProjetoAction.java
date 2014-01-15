/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 21/06/2007
 *
 */
package br.ufrn.sigaa.pesquisa.struts;

import java.util.Collection;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import br.ufrn.academico.dominio.MembroProjeto;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.validacao.MensagemAviso;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.pesquisa.CalendarioPesquisaDao;
import br.ufrn.sigaa.arq.dao.pesquisa.ProjetoPesquisaDao;
import br.ufrn.sigaa.arq.dao.pesquisa.RelatorioProjetoDao;
import br.ufrn.sigaa.arq.dao.projetos.MembroProjetoDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.arq.struts.AbstractCrudAction;
import br.ufrn.sigaa.arq.struts.ConstantesCadastro;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.pesquisa.dominio.CalendarioPesquisa;
import br.ufrn.sigaa.pesquisa.dominio.ProjetoPesquisa;
import br.ufrn.sigaa.pesquisa.dominio.RelatorioProjeto;
import br.ufrn.sigaa.pesquisa.form.RelatorioProjetoForm;
import br.ufrn.sigaa.pessoa.dominio.Servidor;

/**
 * Action respons�vel pelo controle dos relat�rios
 * finais de projetos de pesquisa
 *
 * @author Ricardo Wendell
 *
 */
public class RelatorioProjetoAction extends AbstractCrudAction {

	/**
	 * M�todo respons�vel por popular, para que posteriormente ser realizado o envio dos dados do projeto.
	 * 
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	public ActionForward popularEnvio(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res) throws Exception {
		
		RelatorioProjetoForm relatorioForm = (RelatorioProjetoForm) form;
		CalendarioPesquisaDao calendarioDao = getDAO(CalendarioPesquisaDao.class, req);
		ProjetoPesquisaDao projetoDao = getDAO(ProjetoPesquisaDao.class, req);

		
		// Verificar se o usu�rio � um servidor
		Servidor servidor = ((Usuario) getUsuarioLogado(req)).getServidor();
		if (servidor == null) {
			addMensagemErro("Somente docentes podem acessar esta op��o", req);
			return getMappingSubSistema(req, mapping);
		}
		
		Integer idProjeto = getParameterInt(req, "idProjeto");
		if (idProjeto == null || idProjeto == 0)
			idProjeto = relatorioForm.getObj().getProjetoPesquisa().getId();
		
		ProjetoPesquisa projetoPesquisa = projetoDao.findByPrimaryKey(idProjeto, ProjetoPesquisa.class);
		
		// Validar per�odo de submiss�o de relat�rios finais
		CalendarioPesquisa calendario = calendarioDao.findVigente();
		if ( projetoPesquisa.isInterno() && !calendario.isPeriodoEnvioRelatorioAnualProjeto() ) {
			addMensagemErro("Aten��o! O sistema n�o est� aberto para envio de relat�rios anuais de projetos de pesquisa.", req);
			return getMappingSubSistema(req, mapping);
		}
		
		prepareMovimento(SigaaListaComando.ENVIAR_RELATORIO_FINAL_PROJETO, req);
	
		RelatorioProjeto relatorioProjeto = projetoPesquisa.getRelatorioProjeto();
		if (relatorioProjeto == null) {
			relatorioProjeto = new RelatorioProjeto();
			relatorioProjeto.setProjetoPesquisa(projetoPesquisa);
		}
		
		
		relatorioForm.setObj(relatorioProjeto);
		
		req.setAttribute(mapping.getName(), relatorioForm);
		
		return mapping.findForward(FORM);
	}
	
	/**
	 * Popular formul�rio de envio do relat�rio final
	 * com os dados necess�rios
	 *
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	public ActionForward listarRelatorios(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res) throws Exception {
		RelatorioProjetoForm relatorioForm = (RelatorioProjetoForm) form;
		Map<String, Object> mapa = relatorioForm.getReferenceData();

		ProjetoPesquisaDao projetoDao = getDAO(ProjetoPesquisaDao.class, req);

		// Verificar se o usu�rio � um servidor
		Servidor servidor = ((Usuario) getUsuarioLogado(req)).getServidor();
		if (servidor == null) {
			addMensagemErro("Somente docentes podem acessar esta op��o", req);
			return getMappingSubSistema(req, mapping);
		}

		// Popular lista de projetos pendentes de relat�rios finais
		Collection<ProjetoPesquisa> projetos = projetoDao.findPendentesRelatorioFinal( servidor );

		// Validar projetos encontrados
		if (projetos == null || projetos.isEmpty()) {
			addMensagemErro("N�o foi encontrado projeto sob sua coordena��o que esteja com relat�rio pendente.", req);
			return getMappingSubSistema(req, mapping);
		}

		// Preparar formul�rio de submiss�o
		mapa.put("projetos", projetos );

		return mapping.findForward("listar_relatorios");
	}

	/**
	 * Coloca o relat�rio com o status de enviado e grava
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	public ActionForward preEnviar(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res) throws Exception {
		
		RelatorioProjetoForm relatorioForm = (RelatorioProjetoForm) form;
		relatorioForm.getObj().setEnviado(Boolean.TRUE);
		
		return gravar(mapping, relatorioForm, req, res);
	}
	
	/**
	 * Cria um relat�rio, mas n�o submete. o usu�rio ainda pode alterar
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	public ActionForward preGravar(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res) throws Exception {
		
		RelatorioProjetoForm relatorioForm = (RelatorioProjetoForm) form;
		relatorioForm.getObj().setEnviado(Boolean.FALSE);
		
		return gravar(mapping, relatorioForm, req, res);
	}	
	
	/**
	 * Grava o relat�rio final
	 *
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	public ActionForward gravar(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res) throws Exception {
		RelatorioProjetoForm relatorioForm = (RelatorioProjetoForm) form;

		// Validar dados
		relatorioForm.validate(req);
		if ( flushErros(req) ) {
			if(getUsuarioLogado(req).isUserInRole(SigaaPapeis.GESTOR_PESQUISA) && getSubSistemaCorrente(req).equals(SigaaSubsistemas.PESQUISA))
				return popularEnvioPropesq(mapping, form, req, res);
			else
				return popularEnvio(mapping, form, req, res);
		}

		// Preparar persist�ncia do relat�rio
		MovimentoCadastro mov = new MovimentoCadastro();
		mov.setObjMovimentado( relatorioForm.getObj() );
		mov.setCodMovimento( SigaaListaComando.ENVIAR_RELATORIO_FINAL_PROJETO );

		// Chamar processador
		try {
			RelatorioProjeto relatorio = (RelatorioProjeto) execute(mov, req);

			ProjetoPesquisaDao projetoDao = getDAO(ProjetoPesquisaDao.class, req);
			relatorio.setProjetoPesquisa( projetoDao.findLeve(relatorio.getProjetoPesquisa().getId()) );
			req.setAttribute("relatorioProjeto", relatorio);
		} catch (NegocioException e) {
			addMensagens( e.getListaMensagens().getMensagens() , req);
			if(getUsuarioLogado(req).isUserInRole(SigaaPapeis.GESTOR_PESQUISA) && getSubSistemaCorrente(req).equals(SigaaSubsistemas.PESQUISA))
				return popularEnvioPropesq(mapping, form, req, res);
			else
				return popularEnvio(mapping, form, req, res);
		}

		addMensagem(new MensagemAviso("Relat�rio salvo com sucesso", TipoMensagemUFRN.INFORMATION), req);
		
		if (!relatorioForm.getObj().isEnviado())
			return popularEnvio(mapping, form, req, res);
		
		return mapping.findForward("comprovante");
	}

	/**
	 * Remo��o de um Relat�rio Final de Projeto
	 */
	@Override
	public ActionForward remove(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res) throws Exception {
		
		RelatorioProjetoForm relatorioForm = (RelatorioProjetoForm) form;
		relatorioForm.setObj(getGenericDAO(req).findByPrimaryKey(relatorioForm.getObj().getId(), RelatorioProjeto.class));

		prepareMovimento(SigaaListaComando.REMOVER_RELATORIO_FINAL_BOLSA_PESQUISA, req);

		// Preparar persist�ncia do relat�rio
		MovimentoCadastro mov = new MovimentoCadastro();
		mov.setObjMovimentado( relatorioForm.getObj() );
		mov.setCodMovimento( SigaaListaComando.ENVIAR_RELATORIO_FINAL_PROJETO );
		execute(mov, req);
		addMensagem(new MensagemAviso("Relat�rio Final removido.", TipoMensagemUFRN.INFORMATION), req);
		return getMappingSubSistema(req, mapping);
	}
	
	/**
	 * Lista todos os relat�rios submetidos de projetos
	 * que um docente participa
	 *
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	public ActionForward listarMembroProjeto(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res) throws Exception {

		// Verificar se o usu�rio � um servidor
		Servidor servidor = ((Usuario) getUsuarioLogado(req)).getServidor();
		if (servidor == null) {
			addMensagemErro("Somente docentes podem acessar esta op��o", req);
			return getMappingSubSistema(req, mapping);
		}

		RelatorioProjetoDao relatorioDao = getDAO(RelatorioProjetoDao.class, req);
		Collection<RelatorioProjeto> relatorios = relatorioDao.findByMembroProjeto( servidor );

		CalendarioPesquisaDao calendarioDao = getDAO(CalendarioPesquisaDao.class, req);

		// Verificar se existem relat�rios pass�veis de edi��o
		CalendarioPesquisa calendario = calendarioDao.findVigente();
		
		for ( RelatorioProjeto relatorio : relatorios ) {
			if ( isCoordenador(servidor, relatorio.getProjetoPesquisa(), req)
					&& (calendario.isPeriodoEnvioRelatorioAnualProjeto() || !relatorio.getProjetoPesquisa().isInterno())
					&& !ValidatorUtil.isEmpty(relatorio.getProjetoPesquisa().getDataFim()) 
					&& CalendarUtils.getAno(relatorio.getProjetoPesquisa().getDataFim()) == CalendarUtils.getAnoAtual()) {
				relatorio.setEditavel(true);
			}

		}
		
		req.setAttribute( "anoCalendario" , calendario.getAno() - 1);
		req.setAttribute(ConstantesCadastro.ATRIBUTO_LISTAGEM, relatorios);
		return mapping.findForward("listaMembroDocente");
	}

	/**
	 * Verifica se para o Projeto selecionado o Docente � o Coordenador
	 * 
	 * @param servidor
	 * @param projetoPesquisa
	 * @param req
	 * @return
	 * @throws ArqException
	 */
	private boolean isCoordenador( Servidor servidor, ProjetoPesquisa projetoPesquisa, HttpServletRequest req ) throws ArqException {
		
			
		if ( ValidatorUtil.isNotEmpty(projetoPesquisa.getCoordenador()) ) {
			return servidor.getId() == projetoPesquisa.getCoordenador().getId();
		
		} else {

			MembroProjetoDao dao = getDAO(MembroProjetoDao.class, req);
			try {
				MembroProjeto membro = new MembroProjeto();
				membro.setIdServidor( dao.findCoordenadorAtualProjetoPesquisa(projetoPesquisa.getId()) );
				if ( ValidatorUtil.isEmpty(membro.getIdServidor()) )
					return false;
				else
					return servidor.getId() == membro.getIdServidor();
			} finally {
				dao.close();
			}
		}
	}
	
	/**
	 * Gera uma listagem dos relat�rios de projeto
	 */
	@Override
	public ActionForward list(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res) throws Exception {
		checkRole(SigaaPapeis.GESTOR_PESQUISA, req);

		return super.list(mapping, form, req, res);
	}

	/**
	 * Realiza a edi��o de um relat�rio de projeto
	 */
	@Override
	public ActionForward edit(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res) throws Exception {
		prepareMovimento(SigaaListaComando.ENVIAR_RELATORIO_FINAL_PROJETO, req);
		return super.edit(mapping, form, req, res);
	}

	/**
	 * Esse m�todo � respons�vel por popular o envio dos dados para a PROPESQ 
	 * 
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	public ActionForward popularEnvioPropesq(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res) throws Exception {
		RelatorioProjetoForm relatorioForm = (RelatorioProjetoForm) form;
		
		prepareMovimento(SigaaListaComando.ENVIAR_RELATORIO_FINAL_PROJETO, req);
		
		int idProjeto = getParameterInt(req, "idProjeto");
		if(idProjeto > 0){
			Collection<RelatorioProjeto> relatorios = getGenericDAO(req).findByExactField(RelatorioProjeto.class, "projetoPesquisa.id", idProjeto);
			
			RelatorioProjeto rel = null;
			
			if(relatorios != null && !relatorios.isEmpty()){
				rel = relatorios.iterator().next();
			}
			
			
			if(rel != null){
				relatorioForm.setObj(rel);
			}
		} else {
			idProjeto = relatorioForm.getObj().getProjetoPesquisa().getId();
		}
		
		relatorioForm.getObj().setProjetoPesquisa(getGenericDAO(req).findByPrimaryKey(idProjeto, ProjetoPesquisa.class));
		
		return mapping.findForward(FORM);
	}
}
