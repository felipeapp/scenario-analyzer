/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 03/07/2007
 *
 */
package br.ufrn.sigaa.pesquisa.struts;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.MensagemAviso;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.arq.util.JasperReportsUtil;
import br.ufrn.arq.util.RequestUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.AreaConhecimentoCnpqDao;
import br.ufrn.sigaa.arq.dao.pesquisa.AvaliacaoResumoDao;
import br.ufrn.sigaa.arq.dao.pesquisa.CalendarioPesquisaDao;
import br.ufrn.sigaa.arq.dao.pesquisa.CongressoIniciacaoCientificaDao;
import br.ufrn.sigaa.arq.dao.pesquisa.PlanoTrabalhoDao;
import br.ufrn.sigaa.arq.dao.pesquisa.ProjetoPesquisaDao;
import br.ufrn.sigaa.arq.dao.pesquisa.RelatorioBolsaFinalDao;
import br.ufrn.sigaa.arq.dao.pesquisa.ResumoCongressoDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.arq.struts.AbstractCrudAction;
import br.ufrn.sigaa.arq.struts.ConstantesCadastro;
import br.ufrn.sigaa.arq.struts.SigaaForm;
import br.ufrn.sigaa.dominio.InstituicoesEnsino;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.mensagens.MensagensPesquisa;
import br.ufrn.sigaa.parametros.dominio.ParametrosPesquisa;
import br.ufrn.sigaa.pesquisa.dominio.AutorResumoCongresso;
import br.ufrn.sigaa.pesquisa.dominio.CalendarioPesquisa;
import br.ufrn.sigaa.pesquisa.dominio.CongressoIniciacaoCientifica;
import br.ufrn.sigaa.pesquisa.dominio.PlanoTrabalho;
import br.ufrn.sigaa.pesquisa.dominio.RelatorioBolsaFinal;
import br.ufrn.sigaa.pesquisa.dominio.ResumoCongresso;
import br.ufrn.sigaa.pesquisa.form.ResumoCongressoForm;
import br.ufrn.sigaa.pesquisa.jsf.CertificadoCICMBean;
import br.ufrn.sigaa.pesquisa.negocio.ResumoCongressoValidator;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.Servidor;
import br.ufrn.sigaa.projetos.dominio.MembroComissao;

/**
 * Action responsável pela manutenção dos resumos do
 * congresso de iniciação científica
 *
 * @author Ricardo Wendell
 * @author Leonardo Campos
 *
 */
public class ResumoCongressoAction extends AbstractCrudAction {

	
	/**
	 * Popular a lista de planos de trabalho a partir dos quais o aluno pode iniciar o envio
	 * de um resumo do CIC. 
	 * JSP: /portais/discente/menu_discente.jsp
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	public ActionForward popularInicioEnvio(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res) throws Exception {

		
		PlanoTrabalhoDao planoTrabalhoDao = getDAO(PlanoTrabalhoDao.class, req);
		CongressoIniciacaoCientificaDao congressoDao = getDAO(CongressoIniciacaoCientificaDao.class, req);
		CongressoIniciacaoCientifica congresso = congressoDao.findAtivo();
		
		Collection<PlanoTrabalho> planos = null;
		
		DiscenteAdapter discente = ((Usuario) getUsuarioLogado(req)).getDiscenteAtivo();
		if (discente != null) {
			planos = planoTrabalhoDao.findPassiveisSubmissaoResumoCongresso(discente, congresso.getRestricoes());
		}
		
		req.setAttribute("planos", planos);
		req.setAttribute("congresso_", congresso);
		return mapping.findForward("selecionaPlanoTrabalho");
	}
	
	/**
	 * Popular formulário de envio do resumo
	 * JSP: /WEB-INF/jsp/pesquisa/ResumoCongresso/seleciona_plano_trabalho.jsp
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	public ActionForward iniciarEnvio(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res) throws Exception {
		ResumoCongressoForm resumoForm = (ResumoCongressoForm) form;
		ResumoCongresso resumo = resumoForm.getObj();
		resumoForm.clearReferenceData();
		Map<String, Object> reference = resumoForm.getReferenceData();

		Usuario usuario = (Usuario) getUsuarioLogado(req);
		DiscenteAdapter discente = usuario.getDiscenteAtivo();

		// DAOs
		GenericDAO dao = getGenericDAO(req);
		RelatorioBolsaFinalDao relatorioDao = getDAO(RelatorioBolsaFinalDao.class, req);
		PlanoTrabalhoDao planoDao = getDAO(PlanoTrabalhoDao.class, req);
		CongressoIniciacaoCientificaDao congressoDao = getDAO(CongressoIniciacaoCientificaDao.class, req);
		AreaConhecimentoCnpqDao areaDao = getDAO(AreaConhecimentoCnpqDao.class, req);
		ResumoCongressoDao resumoDao = getDAO(ResumoCongressoDao.class, req);
		AvaliacaoResumoDao avaliacaoDao = getDAO(AvaliacaoResumoDao.class, req);

		// Calendário ativo
		CalendarioPesquisaDao calendarioDao = getDAO(CalendarioPesquisaDao.class, req);
		CalendarioPesquisa calendario = calendarioDao.findVigente();

		// Buscar congresso ativo
		CongressoIniciacaoCientifica congresso = congressoDao.findAtivo();
		resumo.setCongresso(congresso);

		// Verificar permissão de acesso
		boolean gestor = discente == null;
		
		if ( req.getParameter("idResumo") != null ) {

			resumo = dao.findByPrimaryKey(getParameterInt(req, "idResumo"), ResumoCongresso.class);
			if ( resumo != null ) {
		
				if ( discente != null ) {
		
					// Verificar se pode editar resumos
					if ( resumo.getStatus() != ResumoCongresso.AGUARDANDO_AUTORIZACAO 
							&& resumo.getStatus() != ResumoCongresso.NECESSITA_CORRECOES
							&& resumo.getStatus() != ResumoCongresso.RECUSADO_NECESSITA_CORRECOES) {
						addMensagem(req, MensagensPesquisa.PERIODO_SUBMISSAO_NAO_VIGENTE);
						return getMappingSubSistema(req, mapping);
					}
		
					req.setAttribute("fimPeriodo", calendario.getFimRelatorioFinalBolsa());
		
				} else {
					checkRole(SigaaPapeis.GESTOR_PESQUISA, req);
					gestor = true;
				}
				
				req.setAttribute("avaliacao", avaliacaoDao.findByResumo(resumo.getId()));
			}
		}

		// Preparar operação
		prepareMovimento(SigaaListaComando.ENVIAR_RESUMO_CONGRESSO_IC, req);

		// Verificar se é um novo resumo ou a edição de um já cadastrado
		if ( req.getParameter("idResumo") != null ) {

			resumo = dao.findByPrimaryKey(getParameterInt(req, "idResumo"), ResumoCongresso.class);
			if ( resumo != null ) {
				AutorResumoCongresso autor = resumo.getAutor();

				// Verificar se o usuário é o discente do resumo
				if ( !gestor
						&& autor != null
						&& autor.getDiscente() != null
						&& discente != null
						&& autor.getDiscente().getId() != discente.getId()) {
					addMensagem(req, MensagensPesquisa.SOMENTE_AUTOR_PODE_EDITAR);
					return getMappingSubSistema(req, mapping);
				}

				resumoForm.setObj(resumo);
			} else {
				addMensagem(req, MensagensPesquisa.RESUMO_NAO_ENCONTRADO);
				return getMappingSubSistema(req, mapping);
			}

		} else {
			RelatorioBolsaFinal relatorio = null;
			PlanoTrabalho plano = null;

			// Adaptação para permitir que gestores de pesquisa possam submeter resumos a partir de relatórios finais
			if (gestor) {
				if ( req.getParameter("idRelatorio") == null ) {
					addMensagem(req, MensagensPesquisa.NECESSARIO_SELECIONAR_RELATORIO_FINAL);
					return getMappingSubSistema(req, mapping);
				}
				relatorio = relatorioDao.findByPrimaryKey( getParameterInt(req, "idRelatorio"), RelatorioBolsaFinal.class);

				if (relatorio == null ) {
					addMensagem(req, MensagensPesquisa.RELATORIO_NAO_ENCONTRADO);
					return getMappingSubSistema(req, mapping);
				}

				discente = relatorio.getMembroDiscente().getDiscente();
			} else {
				if( req.getParameter("idPlanoTrabalho") != null ){
					plano = planoDao.findByPrimaryKey(getParameterInt(req, "idPlanoTrabalho"), PlanoTrabalho.class);
					if(plano != null){
						ListaMensagens lista = newListaMensagens(req);
						ResumoCongressoValidator.validarPeriodoEnvio(plano, lista);
						ResumoCongressoValidator.validarRestricoes(plano, congresso, lista);
						if ( flushErros(req) )
							return popularInicioEnvio(mapping, resumoForm, req, res);
						reference.put("inicioSubmissao", plano.getTipoBolsa().getInicioEnvioResumoCongresso());
						reference.put("fimSubmissao", plano.getTipoBolsa().getFimEnvioResumoCongresso());
						Collection<RelatorioBolsaFinal> rels = relatorioDao.findByExactField(RelatorioBolsaFinal.class, "planoTrabalho.id", plano.getId());
						if( rels != null && !rels.isEmpty() )
							relatorio = rels.iterator().next();			
					}
				}
			}

			// Verificar se o discente não possui um resumo enviado
			AutorResumoCongresso autor = new AutorResumoCongresso();
			if(discente != null)
				autor.defineDiscente(discente.getDiscente());
			if ( resumoDao.findByAutorPrincipal(autor, congresso) != null && discente != null) {
				addMensagem(req, MensagensPesquisa.AUTOR_DUPLICADO, discente.getNome(), congresso.getEdicao());
				return getMappingSubSistema(req, mapping);
			}

			if ( relatorio != null && resumo != null) {
				// No caso de um novo resumo, verificar se existe um relatório final de bolsa cadastrado
				// para o plano de trabalho selecionado. Neste caso, popular o resumo com os dados do relatório

				if (  relatorio.getPlanoTrabalho().getProjetoPesquisa().getAreaConhecimentoCnpq() == null ) {
					addMensagemErro(" A área de conhecimento do projeto de pesquisa ao qual o plano de trabalho está associado está indefinida." +
							" Por favor, contate a Pró-Reitoria de Pesquisa para solucionar este problema.", req);
					return getMappingSubSistema(req, mapping);
				}

				resumo.setTitulo( relatorio.getPlanoTrabalho().getTitulo() );
				resumo.setResumo( relatorio.getResumo() );
				resumo.setPalavrasChave( relatorio.getPalavrasChave() );
				resumo.setAreaConhecimentoCnpq( relatorio.getPlanoTrabalho().getProjetoPesquisa().getAreaConhecimentoCnpq().getGrandeArea() );
				popularAutores( resumo,
						discente,
						relatorio.getPlanoTrabalho().getOrientador());

			} else if (plano != null && resumo != null) {
				// Caso não seja encontrado o relatório final, tenta popular com
				// os dados do plano de trabalho selecionado

				if ( plano.getProjetoPesquisa().getAreaConhecimentoCnpq() == null ) {
					addMensagemErro(" A área de conhecimento do projeto de pesquisa ao qual o plano de trabalho está associado está indefinida." +
							" Por favor, contacte a Pró-Reitoria de Pesquisa para solucionar este problema.", req);
					return getMappingSubSistema(req, mapping);
				}

				resumo.setTitulo( plano.getTitulo() ) ;
				resumo.setAreaConhecimentoCnpq( plano.getProjetoPesquisa().getAreaConhecimentoCnpq().getGrandeArea() );
				popularAutores( resumo,
							discente,
							plano.getOrientador());
			} else {
				// Se não selecionou nenhum plano, é um resumo isolado
				
				// Validar parâmetro do módulo de pesquisa se permite o envio de resumos isolados (independentes)
				if( !ParametroHelper.getInstance().getParametroBoolean(ParametrosPesquisa.ENVIO_RESUMO_INDEPENDENTE) ){
					addMensagemErro("O sistema não está aberto para recebimento de Resumos Independentes.<br/>" +
							"Somente alunos com vinculação a um plano de trabalho estão habilitados a enviar resumos para o CIC.", req);
					return getMappingSubSistema(req, mapping);
				}
				
				if( !ResumoCongresso.isPeriodoSubmissao(calendario) ){
					addMensagem(req, MensagensPesquisa.PERIODO_SUBMISSAO_NAO_VIGENTE);
					return getMappingSubSistema(req, mapping);
				}
				
				reference.put("inicioSubmissao", calendario.getInicioResumoCIC());
				reference.put("fimSubmissao", calendario.getFimResumoCIC());
				
				resumoForm.setIsolado(true);
				popularAutores( resumo,
						discente,
						null);

			}
			if(resumo != null)
				autor = resumo.getAutor();
			if ( autor.getCpf() == 0 ) {
				resumoForm.getReferenceData().put("exigirCPF", true);
			}
		}

		resumoForm.getReferenceData().put( "grandesAreas" , areaDao.findGrandeAreasConhecimentoCnpq() );
		return mapping.findForward("formEnvio");
	}

	/**
	 * Popular autor e orientador
	 *
	 * @param resumo
	 * @param discente
	 * @param servidor
	 */
	private void popularAutores(ResumoCongresso resumo, DiscenteAdapter discente, Servidor servidor) {
		resumo.setAutores( new ArrayList<AutorResumoCongresso>() );

		// Definir autor
		AutorResumoCongresso autor = new AutorResumoCongresso();
		autor.defineDiscente( discente.getDiscente() );
		autor.setTipoParticipacao(AutorResumoCongresso.AUTOR);
		autor.setInstituicaoEnsino( new InstituicoesEnsino(InstituicoesEnsino.UFRN));
		autor.setResumo(resumo);
		resumo.getAutores().add(autor);

		// Definir orientador
		if (servidor != null) {
			AutorResumoCongresso orientador = new AutorResumoCongresso();
			orientador.defineDocente( servidor );
			orientador.setTipoParticipacao(AutorResumoCongresso.ORIENTADOR);
			orientador.setInstituicaoEnsino( new InstituicoesEnsino(InstituicoesEnsino.UFRN));
			orientador.setResumo(resumo);
			resumo.getAutores().add(orientador);
		}
	}

	/**
	 * Adicionar autor a lista de autores do resumo
	 * JSP: /WEB-INF/jsp/pesquisa/ResumoCongresso/form_envio.jsp
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	public ActionForward adicionarAutor(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res) throws Exception {
		ResumoCongressoForm resumoForm = (ResumoCongressoForm) form;
		ResumoCongresso resumo = resumoForm.getObj();
		// Popular dados de acordo com a categoria do autor
		ListaMensagens erros = new ListaMensagens();

		if(verificarSessaoInvalida(resumoForm)){
			erros.addErro("Sessão invalidada. Por favor, reinicie a operação.");
			return cancelar(mapping, form, req, res);
		}
		
		GenericDAO dao = getGenericDAO(req);

		// Verificar tipo de participação do autor
		AutorResumoCongresso autor = resumoForm.getAutor();
		if (autor.getTipoParticipacao() == 0) {
			autor.setTipoParticipacao(AutorResumoCongresso.CO_AUTOR);
		}

		// Validar duplicidade de autores
		if (resumo.contains(autor) ) {
			erros.addErro("Esta pessoa já está inserida na lista de autores");
		}
		
		if ( resumo.getAutores() != null && resumo.getAutores().size() >= ResumoCongressoValidator.LIMITE_AUTORES ) {
			erros.addErro("Um resumo deve conter, no máximo, " + ResumoCongressoValidator.LIMITE_AUTORES + " autores.");
		}
		
		// Verificar a ocorrência de erros
		if (erros.isErrorPresent()){
			addMensagens(erros.getMensagens(), req);
			resumoForm.setAutor(new AutorResumoCongresso());
			return mapping.findForward("formEnvio");
		}

		switch ( autor.getCategoria() ) {
			case AutorResumoCongresso.DISCENTE:
				// Buscar discente
				Discente discente = dao.findByPrimaryKey(autor.getDiscente().getId(), Discente.class);

				if (discente != null) {
					autor.defineDiscente(discente);
					autor.setResumo(resumo);
					autor.setInstituicaoEnsino(new InstituicoesEnsino(InstituicoesEnsino.UFRN));
					resumo.getAutores().add(autor);
				}
				erros.addAll( autor.validate().getMensagens() );
				addMensagens(erros.getMensagens(), req);
				resumoForm.setExibirFormCoAutor(true);
				break;
			case AutorResumoCongresso.DOCENTE:
				Servidor docente = dao.findByPrimaryKey(autor.getDocente().getId(), Servidor.class);

				if (docente != null) {
					autor.defineDocente(docente);
					autor.setResumo(resumo);
					autor.setInstituicaoEnsino(new InstituicoesEnsino(InstituicoesEnsino.UFRN));
					resumo.getAutores().add(autor);
				}
				erros.addAll( autor.validate().getMensagens() );
				addMensagens(erros.getMensagens(), req);
				resumoForm.setExibirFormCoAutor(true);
				break;
			case AutorResumoCongresso.EXTERNO:
				autor.setDocente(null);
				autor.setDiscente(null);
				if(resumoForm.getCpf() != null && !resumoForm.getCpf().trim().equals(""))
					autor.setCpf( Long.parseLong(resumoForm.getCpf()) );
				erros.addAll( autor.validate().getMensagens() );
				addMensagens(erros.getMensagens(), req);
				if ( !erros.isErrorPresent() ) {
					autor.setCpf( Long.parseLong(resumoForm.getCpf()) );
					autor.setResumo(resumo);
					autor.setInstituicaoEnsino(new InstituicoesEnsino(InstituicoesEnsino.UFRN));
					resumo.getAutores().add(autor);
					resumoForm.setExibirFormCoAutor(true);
				}
				break;
		}
		
		resumoForm.setAutor(new AutorResumoCongresso());
		return mapping.findForward("formEnvio");
	}

	/**
	 * Verifica se a sessão foi invalidada.
	 * @param resumoForm
	 * @return
	 */
	private boolean verificarSessaoInvalida(ResumoCongressoForm resumoForm) {
		return resumoForm.getObj().getAutores() == null || resumoForm.getObj().getAutores().isEmpty();
	}

	/**
	 * Remover autor da lista de autores do resumo
	 * JSP: /WEB-INF/jsp/pesquisa/ResumoCongresso/form_envio.jsp
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	public ActionForward removerAutor(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res) throws Exception {
		ResumoCongressoForm resumoForm = (ResumoCongressoForm) form;
		ResumoCongresso resumo = resumoForm.getObj();

		if(verificarSessaoInvalida(resumoForm)){
			addMensagemErro("Sessão invalidada. Por favor, reinicie a operação.", req);
			return cancelar(mapping, form, req, res);
		}
		
		// Remover autor selecionado
		int pos = getParameterInt(req, "indice");
		removePorPosicao(resumo.getAutores(), pos);

		return mapping.findForward("formEnvio");
	}

	/**
	 * Persistir dados do resumo e exibir comprovante
	 * JSP: /WEB-INF/jsp/pesquisa/ResumoCongresso/form_envio.jsp
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	public ActionForward enviar(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res) throws Exception {
		ResumoCongressoForm resumoForm = (ResumoCongressoForm) form;
		ResumoCongresso resumo = resumoForm.getObj();

		if(verificarSessaoInvalida(resumoForm)){
			addMensagemErro("Sessão invalidada. Por favor, reinicie a operação.", req);
			return cancelar(mapping, form, req, res);
		}
		
		if ( resumoForm.isIsolado() || resumoForm.isPermissaoGestor() ) {
			if(resumoForm.getOrientador().getDocente().getId() <= 0){
				addMensagemErro("Informe corretamente o orientador do resumo.", req);
				return mapping.findForward("formEnvio");
			}
			
			// Validar dados do orientador e adicioná-lo a lista de autores do resumo
			GenericDAO dao = getGenericDAO(req);
			Servidor docente = dao.findByPrimaryKey(resumoForm.getOrientador().getDocente().getId(), Servidor.class);
			
			Servidor orientadorBD = null;
			ResumoCongresso resumoGravado = dao.findByPrimaryKey(resumo.getId(), ResumoCongresso.class);
			if(resumoGravado != null && resumoGravado.getOrientador() != null)
				orientadorBD = resumoGravado.getOrientador().getDocente();

			if ( docente != null ) {
				AutorResumoCongresso orientador = resumo.getOrientador();
				boolean novo = false;
				if (orientador == null) {
					novo = true;
					orientador = new AutorResumoCongresso();
				} else if(orientadorBD != null && orientadorBD.getId() != docente.getId()){
					novo = true;
					orientador = new AutorResumoCongresso();
					resumo.getAutores().remove(resumo.getOrientador());
				}
				orientador.defineDocente( docente );
				orientador.setTipoParticipacao(AutorResumoCongresso.ORIENTADOR);
				orientador.setInstituicaoEnsino( new InstituicoesEnsino(InstituicoesEnsino.UFRN));
				orientador.setResumo(resumo);

				if (novo) {
					resumo.getAutores().add(orientador);
				}
			}

		}

		// Validar dados
		ListaMensagens erros = new ListaMensagens();

		// Verificar se o CPF informado do autor está correto, caso necessário
		if ( resumoForm.getReferenceData().get("exigirCPF") != null ) {
			resumo.getAutor().setCpf( ValidatorUtil.validateCPF_CNPJ(resumoForm.getCpfAutor() , "CPF do Autor", erros) );
		}

		erros.addAll(resumo.validate());
		addMensagens(erros.getMensagens(), req);
		
		// Verificar a ocorrência de erros
		if (flushOnlyErros(req)) {
			return mapping.findForward("formEnvio");
		}

		boolean cadastro = (resumo.getId() == 0);

		// Persistir resumo
		try {
			resumo.setCodMovimento(SigaaListaComando.ENVIAR_RESUMO_CONGRESSO_IC);
			resumo = (ResumoCongresso) execute(resumo, req);

			req.getSession().removeAttribute(mapping.getName());
			req.setAttribute("resumo", resumo);
		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens().getMensagens(), req);
			return mapping.findForward("formEnvio");
		}

		if(getUsuarioLogado(req).isUserInRole(SigaaPapeis.GESTOR_PESQUISA)
				&& getSubSistemaCorrente(req).equals(SigaaSubsistemas.PESQUISA)
				&& !cadastro){
			addMensagem(new MensagemAviso("Resumo atualizado com sucesso!", TipoMensagemUFRN.INFORMATION), req);
			req.setAttribute("alteracao", "gestor");
			return view(mapping, form, req, res);
		}
 		return  mapping.findForward("comprovante");
	}

	/**
	 * Listar todos os resumos de um determinado autor
	 * JSPs:
	 *     /portais/discente/menu_discente.jsp
	 *     /portais/docente/menu_docente.jsp
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	public ActionForward listarResumosAutor(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res) throws Exception {

		// Inicializar DAOs
		ResumoCongressoDao resumoDao = getDAO(ResumoCongressoDao.class, req);
		CongressoIniciacaoCientificaDao congressoDao = getDAO(CongressoIniciacaoCientificaDao.class, req);

		Usuario usuario = (Usuario) getUsuarioLogado(req);
		DiscenteAdapter discente = usuario.getDiscenteAtivo();
		Servidor docente = usuario.getServidor();

		Collection<Map<String, Object>> lista = null;

		// Definir autor do resumo
		if (discente != null) {
			lista = resumoDao.findResumoAndParticipacaoByAutor(discente);
		} else if ( docente != null) {
			lista = resumoDao.findResumoAndParticipacaoByAutor(docente);
		}

		// Verificar existência de resultados
		if (lista == null || lista.isEmpty() ) {
			addMensagemErro("Não foram encontrados resumos em que você participe como autor", req);
			return getMappingSubSistema(req, mapping);
		}

		req.setAttribute("congressoAtual", congressoDao.findAtivo());
		req.setAttribute("lista", lista);

		return mapping.findForward("listaAutor");
	}

	/**
	 * Lista os resumos utilizando vários filtros.
	 * JSP: /WEB-INF/jsp/pesquisa/ResumoCongresso/relatorio.jsp
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	public ActionForward relatorio(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res) throws Exception {
		checkRole(new int[] {SigaaPapeis.GESTOR_PESQUISA, MembroComissao.MEMBRO_COMISSAO_PESQUISA}, req);

		ResumoCongressoForm resumoForm = (ResumoCongressoForm) form;
		ResumoCongressoDao dao = getDAO(ResumoCongressoDao.class, req);
		AreaConhecimentoCnpqDao areaDao = getDAO(AreaConhecimentoCnpqDao.class, req);
		ProjetoPesquisaDao unidadeDao = getDAO(ProjetoPesquisaDao.class, req);

		req.setAttribute("congressos", dao.findAll(CongressoIniciacaoCientifica.class, "ano", "desc"));
		req.setAttribute("areasConhecimento", areaDao.findGrandeAreasConhecimentoCnpq());
		req.setAttribute("tiposStatus", new ResumoCongresso().getTiposStatus());
		if ( isUserInRole(req, SigaaPapeis.GESTOR_PESQUISA) ) {
			req.setAttribute("centros", unidadeDao.findCentrosPesquisa());
		} else {
			resumoForm.setCentro( ((Usuario)getUsuarioLogado(req)).getVinculoAtivo().getUnidade().getGestora() );
		}

		if (req.getParameter("popular") != null || req.getAttribute("popular") != null) {
			return mapping.findForward("relatorio");
		}

		Collection<ResumoCongresso> lista = null;
		Integer idCongresso = null;
		Integer idAreaConhecimento = null;
		Unidade centro = null;
		String nomeAutor = null;
		Long cpfAutor = null;
		Integer status = null;
		String codigo = null;
		String nomeOrientador = null;

		ListaMensagens erros = new ListaMensagens();

		idCongresso = resumoForm.getIdCongresso();
		ValidatorUtil.validateRequiredId(idCongresso, "Congresso", erros);

		for(int filtro: resumoForm.getFiltros()){
			switch(filtro){
			case ResumoCongressoForm.BUSCA_AREA_CONHECIMENTO:
				idAreaConhecimento = resumoForm.getObj().getAreaConhecimentoCnpq().getId();
				ValidatorUtil.validateRequiredId(idAreaConhecimento, "Área de Conhecimento", erros);
				break;
			case ResumoCongressoForm.BUSCA_CENTRO:
				centro = resumoForm.getCentro();
				ValidatorUtil.validateRequiredId(centro.getId(), "Centro/Unidade", erros);
				break;
			case ResumoCongressoForm.BUSCA_NOME_AUTOR:
				nomeAutor = resumoForm.getAutor().getNome();
				ValidatorUtil.validateRequired(nomeAutor, "Nome do Autor", erros);
				break;
			case ResumoCongressoForm.BUSCA_CPF_AUTOR:
				cpfAutor = ValidatorUtil.validateCPF_CNPJ(resumoForm.getCpf(), "CPF do Autor", erros);
				break;
			case ResumoCongressoForm.BUSCA_STATUS:
				status = resumoForm.getObj().getStatus();
				ValidatorUtil.validateRequiredId(status, "Status", erros);
				break;
			case ResumoCongressoForm.BUSCA_CODIGO:
				codigo = resumoForm.getObj().getCodigo();
				ValidatorUtil.validateRequired(codigo, "Código", erros);
				break;
			case ResumoCongressoForm.BUSCA_ORIENTADOR:
				nomeOrientador = resumoForm.getOrientador().getNome();
				ValidatorUtil.validateRequired(nomeOrientador, "Nome do Orientador", erros);
				break;
			}
		}

		if(erros.isEmpty()){
			lista = dao.filter(idCongresso, idAreaConhecimento, null, nomeAutor, cpfAutor, status, codigo, nomeOrientador, null, null, centro != null ? Arrays.asList(centro.getId()) : null);
			req.setAttribute("resumos", lista);
		}else {
			addMensagens(erros.getMensagens(), req);
			return mapping.findForward("relatorio");
		}

		// Verificar resultado
		if ( lista == null || lista.isEmpty()) {
			addMensagemErro("Não foram encontrados resumos de acordo com os critérios de busca informados.", req);
			return mapping.findForward("relatorio");
		}

		// Gerar relatório, se selecionado
		if ( resumoForm.isRelatorio() ) {
						
			
			HashMap<String, Object> parametros = new HashMap<String, Object>();
			parametros.put("congresso", dao.findByPrimaryKey(idCongresso, CongressoIniciacaoCientifica.class).getDescricao());
			
			JRDataSource jrds = new JRBeanCollectionDataSource(lista);
			JasperPrint prt = JasperFillManager.fillReport(JasperReportsUtil.getReportSIGAA("resumos_cic.jasper"), parametros, jrds);

			res.setContentType("application/pdf");
			res.addHeader("Content-Disposition", "attachment; filename=resumos_cic.pdf");
			JasperExportManager.exportReportToPdfStream(prt,res.getOutputStream());

			return null;
		}

		return mapping.findForward("relatorio");
	}

	/**
	 * Realiza a remoção do Resumo do congresso
	 * 
	 * JSP: /WEB-INF/jsp/pesquisa/ResumoCongresso/relatorio.jsp
	 */
	@SuppressWarnings("unchecked")
	@Override
	public ActionForward remove(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res) throws Exception {
		checkRole(SigaaPapeis.GESTOR_PESQUISA, req);

		SigaaForm<ResumoCongressoForm> sigaaForm =  (SigaaForm<ResumoCongressoForm>) form;
		
		prepareMovimento(ArqListaComando.DESATIVAR, req);
		
		GenericDAO dao = getGenericDAO(req);
		PersistDB obj = null;
		try {
			obj = dao.findByPrimaryKey(sigaaForm.getObjAsPersistDB().getId(), sigaaForm.getCommandClass());
		} finally {
			dao.close();
		}

		MovimentoCadastro mov = new MovimentoCadastro();
		mov.setObjMovimentado(obj);
		mov.setCodMovimento(ArqListaComando.DESATIVAR);

		try {
			execute(mov, req);
		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens().getMensagens(), req);
			req.setAttribute(mapping.getName(), form);
			return edit(mapping, form, req, res);
		}

		addMessage(req, "Remoção realizada com sucesso!",
				TipoMensagemUFRN.INFORMATION);

		// Limpar formulário, caso ele esteja em sessão
		req.getSession(false).removeAttribute(mapping.getName());
		
		return relatorio(mapping, form, req, res);
	}

	/**
	 * Realizar a edição do resumo do congresso
	 * 
	 * JSP: /WEB-INF/jsp/pesquisa/ResumoCongresso/relatorio.jsp
	 */
	@Override
	public ActionForward edit(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res) throws Exception {
		checkRole(SigaaPapeis.GESTOR_PESQUISA, req);

		ResumoCongressoForm resumoForm = (ResumoCongressoForm) form;
		ResumoCongresso resumo = resumoForm.getObj();
		Map<String, Object> reference = resumoForm.getReferenceData();

		GenericDAO dao = getGenericDAO(req);
		AreaConhecimentoCnpqDao areaDao = getDAO(AreaConhecimentoCnpqDao.class, req);
		CongressoIniciacaoCientificaDao congressoDao = getDAO(CongressoIniciacaoCientificaDao.class, req);

		// Calendário ativo
		CalendarioPesquisaDao calendarioDao = getDAO(CalendarioPesquisaDao.class, req);
		CalendarioPesquisa calendario = calendarioDao.findVigente();

		reference.put("inicioSubmissao", calendario.getInicioResumoCIC());
		reference.put("fimSubmissao", calendario.getFimResumoCIC());

		// Buscar congresso ativo
		CongressoIniciacaoCientifica congresso = congressoDao.findAtivo();
		resumo.setCongresso(congresso);

		int id = RequestUtils.getIntParameter(req, "id");
		resumo = dao.findByPrimaryKey(id, ResumoCongresso.class);
		resumoForm.setObj(resumo);
		resumoForm.setOrientador(resumo.getOrientador());

		prepareMovimento(SigaaListaComando.ENVIAR_RESUMO_CONGRESSO_IC, req);
		resumoForm.getReferenceData().put( "grandesAreas" , areaDao.findGrandeAreasConhecimentoCnpq() );

		resumoForm.setPermissaoGestor(true);
		resumoForm.setExibirFormCoAutor(false);
		
		req.setAttribute(mapping.getName(), resumoForm);
		return mapping.findForward("formEnvio");
	}
	
	/**
	 * Carrega as informações para avaliar um resumo, podendo alterar sua situação.
	 * JSP: /WEB-INF/jsp/pesquisa/ResumoCongresso/relatorio.jsp
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public ActionForward avaliar(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res) throws Exception {
		GenericDAO dao = getGenericDAO(req);

		getForm(form).checkRole(req);
		prepareMovimento(SigaaListaComando.ENVIAR_RESUMO_CONGRESSO_IC, req);
		
		int id = RequestUtils.getIntParameter(req, "id");

		Object obj = dao.findByPrimaryKey(id, getForm(form).getCommandClass());
		req.setAttribute(ConstantesCadastro.ATRIBUTO_VISUALIZACAO, obj);
		getForm(form).setObj(obj);
		
		req.setAttribute("tiposStatus", new ResumoCongresso().getTiposStatus());

		return mapping.findForward("avaliacao");
	}
	
	/**
	 * Submete a avaliação persistindo a nova situação do resumo.
	 * JSP: /WEB-INF/jsp/pesquisa/ResumoCongresso/avaliacao.jsp
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	public ActionForward submeterAvaliacao(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res) throws Exception {
		ResumoCongressoForm resumoForm = (ResumoCongressoForm) form;
		ResumoCongresso resumo = resumoForm.getObj();

		// Definido a data da avaliação do resumo
		resumo.setDataParecer(new Date());
		
		// Persistir resumo
		try {
			resumo.setCodMovimento(SigaaListaComando.ENVIAR_RESUMO_CONGRESSO_IC);
			resumo = (ResumoCongresso) execute(resumo, req);

			req.getSession().removeAttribute(mapping.getName());
			req.setAttribute("resumo", resumo);
		} catch (NegocioException e) {
			req.setAttribute("tiposStatus", new ResumoCongresso().getTiposStatus());
			addMensagens(e.getListaMensagens().getMensagens(), req);
			return mapping.findForward("avaliacao");
		}

		addMensagem(new MensagemAviso("Alteração de Status realizada com sucesso!", TipoMensagemUFRN.INFORMATION), req);
		req.setAttribute("popular", "true");
		return relatorio(mapping, form, req, res);
	}
	
	/**
	 * Emite o certificado para os autores do resumo selecionado.
	 * JSPs: 
	 *     /WEB-INF/jsp/pesquisa/ResumoCongresso/relatorio.jsp
	 *     /WEB-INF/jsp/pesquisa/ResumoCongresso/lista_autor.jsp
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	public ActionForward emitirCertificado(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res) throws Exception {
		
		ResumoCongressoForm resumoForm = (ResumoCongressoForm) form;
		ResumoCongresso resumo = resumoForm.getObj();
		
		try {
		
			int id = RequestUtils.getIntParameter(req, "id");
			
			// Buscar dados para o preenchimento do certificado
			GenericDAO dao = getGenericDAO(req);
			
			resumo = dao.findByPrimaryKey(id, ResumoCongresso.class);
			resumo.getCongresso().getAno();
			resumo.getCongresso().getEdicao();
			
			// valida se o resumo está aprovado para que possa ser gerado o certificado
			if(resumo.getStatus() != ResumoCongresso.APRESENTADO){
				addMensagemErro("O certificado só pode ser emitido para resumos aprovados e apresentados.", req);
				req.setAttribute("popular", "true");
				return relatorio(mapping, form, req, res);
			}
			
			CertificadoCICMBean mBean = getMBean("certificadoCIC", req, res);
			mBean.setResumo(resumo);
			mBean.emitirCertificado();
			
			return null;
			
		} catch (Exception e) {
			addMensagemErro("Erro ao buscar as informações do certificado.", req);
			throw new ArqException(e);
		}	
	}
	
	/**
	 * JSPs: 
     *     /WEB-INF/jsp/pesquisa/ResumoCongresso/relatorio.jsp
     *     /WEB-INF/jsp/pesquisa/ResumoCongresso/lista_autor.jsp
	 */
	@Override
	@SuppressWarnings("unchecked")
	public ActionForward view(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res) throws Exception {
		AvaliacaoResumoDao dao = getDAO(AvaliacaoResumoDao.class, req);

		getForm(form).checkRole(req);

		int id = RequestUtils.getIntParameter(req, "id");

		Object obj = dao.findByPrimaryKey(id, getForm(form).getCommandClass());
		req.setAttribute(ConstantesCadastro.ATRIBUTO_VISUALIZACAO, obj);
		
		if(getUsuarioLogado(req).isUserInRole(SigaaPapeis.GESTOR_PESQUISA)){
			req.setAttribute("avaliacao", dao.findByResumo(id));
		}

		return mapping.findForward(VIEW);
	}
}
