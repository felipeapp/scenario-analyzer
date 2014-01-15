/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 22/09/2006
 *
 */
package br.ufrn.sigaa.pesquisa.struts;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Comando;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.Formatador;
import br.ufrn.arq.util.RequestUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.rh.dominio.Formacao;
import br.ufrn.rh.dominio.Situacao;
import br.ufrn.sigaa.arq.dao.AreaConhecimentoCnpqDao;
import br.ufrn.sigaa.arq.dao.PessoaDao;
import br.ufrn.sigaa.arq.dao.ensino.DocenteExternoDao;
import br.ufrn.sigaa.arq.dao.pesquisa.AvaliacaoProjetoDao;
import br.ufrn.sigaa.arq.dao.pesquisa.CalendarioPesquisaDao;
import br.ufrn.sigaa.arq.dao.pesquisa.ColaboradorVoluntarioDao;
import br.ufrn.sigaa.arq.dao.pesquisa.EditalPesquisaDao;
import br.ufrn.sigaa.arq.dao.pesquisa.ProjetoPesquisaDao;
import br.ufrn.sigaa.arq.dao.projetos.HistoricoSituacaoProjetoDao;
import br.ufrn.sigaa.arq.dao.projetos.MembroProjetoDao;
import br.ufrn.sigaa.arq.negocio.PessoaMov;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.arq.struts.AbstractWizardAction;
import br.ufrn.sigaa.arq.struts.ConstantesCadastro;
import br.ufrn.sigaa.cv.jsf.CriarComunidadeVirtualProjetoMBean;
import br.ufrn.sigaa.dominio.AreaConhecimentoCnpq;
import br.ufrn.sigaa.dominio.InstituicoesEnsino;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.negocio.PessoaValidator;
import br.ufrn.sigaa.parametros.dominio.ParametrosPesquisa;
import br.ufrn.sigaa.pesquisa.dominio.AvaliacaoProjeto;
import br.ufrn.sigaa.pesquisa.dominio.CalendarioPesquisa;
import br.ufrn.sigaa.pesquisa.dominio.CategoriaProjetoPesquisa;
import br.ufrn.sigaa.pesquisa.dominio.EditalPesquisa;
import br.ufrn.sigaa.pesquisa.dominio.FinanciamentoProjetoPesq;
import br.ufrn.sigaa.pesquisa.dominio.GrupoPesquisa;
import br.ufrn.sigaa.pesquisa.dominio.LinhaPesquisa;
import br.ufrn.sigaa.pesquisa.dominio.ProjetoPesquisa;
import br.ufrn.sigaa.pesquisa.dominio.TipoNaturezaFinanciamento;
import br.ufrn.sigaa.pesquisa.form.AnoProjetoPesquisa;
import br.ufrn.sigaa.pesquisa.form.ProjetoPesquisaForm;
import br.ufrn.sigaa.pesquisa.form.TelaCronograma;
import br.ufrn.sigaa.pesquisa.negocio.MovimentoProjetoPesquisa;
import br.ufrn.sigaa.pesquisa.negocio.ProjetoPesquisaHelper;
import br.ufrn.sigaa.pesquisa.negocio.ProjetoPesquisaValidator;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.DocenteExterno;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;
import br.ufrn.sigaa.pessoa.dominio.Servidor;
import br.ufrn.sigaa.pessoa.dominio.TipoDocenteExterno;
import br.ufrn.sigaa.projetos.dominio.CategoriaMembro;
import br.ufrn.sigaa.projetos.dominio.CronogramaProjeto;
import br.ufrn.sigaa.projetos.dominio.EntidadeFinanciadora;
import br.ufrn.sigaa.projetos.dominio.FuncaoMembro;
import br.ufrn.sigaa.projetos.dominio.HistoricoSituacaoProjeto;
import br.ufrn.sigaa.projetos.dominio.MembroProjeto;
import br.ufrn.sigaa.projetos.dominio.Projeto;
import br.ufrn.sigaa.projetos.dominio.TipoSituacaoProjeto;
import br.ufrn.sigaa.projetos.jsf.MembroProjetoMBean;
import br.ufrn.sigaa.projetos.jsf.ProjetoBaseMBean;

/**
 * Action responsável pelo cadastro de projetos de pesquisa.
 *
 * @author Victor Hugo
 * @author Ricardo Wendell
 * @author Leonardo Campos
 */
public class ProjetoPesquisaAction extends AbstractWizardAction {

	/** FORM FORWARDS */
	/** Constante da view dos dados gerais */
	public static final String DADOS_GERAIS = "dadosGerais";
	/** Constante da view dos dados para renovação */
	public static final String DADOS_RENOVACAO = "dadosRenovacao";
	/** Constante da view da descrição */
	public static final String DESCRICAO = "descricao";
	/** Constante da view dos docentes */
	public static final String DOCENTES = "docentes";
	/** Constante da view dos financiamentos */
	public static final String FINANCIAMENTOS = "financiamentos";
	/** Constante da view do cronograma */
	public static final String CRONOGRAMA = "cronograma";
	/** Constante da view do resumo */
	public static final String RESUMO = "resumo";
	/** Constante da view do resumo finalizado */
	public static final String RESUMO_FINALIZACAO = "resumo_finalizacao";
	/** Constante da view do comprovante */
	public static final String COMPROVANTE = "comprovante";
	/** Constante da view da lista dos projetos passiveis de renovação */
	public static final String LISTA_PROJETOS_RENOVACAO = "lista_projetos_renovacao";
	/** Constante da view dos membros do projeto */
	public static final String LISTA_PROJETOS_MEMBRO = "lista_projetos_membro";
	/** Constante da view do resumo integrado */
	public static final String RESUMO_INTEGRADO = "resumo_integrado";
	/** Constante da view do resumo para impressão */
	public static final String RESUMO_IMPRESSAO = "resumo_impressao";
	/** Constante da view da lista dos membros */
	public static final String LISTA_MEMBROS = "lista_membros";
	/** Constante da view de popular as informações */
	public static final String POPULAR = "popular";
	/** Constante da view da gerência dos membros */
	public static final String GERENCIAR_MEMBROS = "gerenciarMembros";
	/** Constante da view da gerência a mudança de Edital */
	public static final String ALTERAR_EDITAL = "alterarEdital";

	/* Métodos de navegação entre as views */
	
	/**
	 * Encaminha para a tela dos dados gerais do projeto de pesquisa.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/WEB-INF/jsp/pesquisa/ProjetoPesquisa/descricao.jsp</li>
	 *	</ul>
	 */
	public ActionForward telaDadosGerais(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		ProjetoPesquisaForm projetoForm = (ProjetoPesquisaForm) form;
		if( isNotOperacaoAtiva(request, projetoForm) ){
			addMensagemErro("A operação não está mais ativa. Por favor reinicie a operação.", request);
			return mapping.findForward(getSubSistemaCorrente(request).getForward());
		}
		setStep(request, 1);
		return mapping.findForward(DADOS_GERAIS);
	}
	
	/**
	 * Encaminha para a tela dos membros do projeto de pesquisa.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/WEB-INF/jsp/pesquisa/ProjetoPesquisa/cronograma.jsp</li>
	 *	</ul>
	 */
	public ActionForward telaDocentes(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		ProjetoPesquisaForm projetoForm = (ProjetoPesquisaForm) form;
		if( isNotOperacaoAtiva(request, projetoForm) ){
			addMensagemErro("A operação não está mais ativa. Por favor reinicie a operação.", request);
			return mapping.findForward(getSubSistemaCorrente(request).getForward());
		}
		setStep(request, 3);
		return mapping.findForward(DOCENTES);
	}
	
	/**
	 * Encaminha para a tela do cronograma do projeto de pesquisa.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/WEB-INF/jsp/pesquisa/ProjetoPesquisa/resumo.jsp</li>
	 *	</ul>
	 */
	public ActionForward telaCronograma(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		ProjetoPesquisaForm projetoForm = (ProjetoPesquisaForm) form;
		if( isNotOperacaoAtiva(request, projetoForm) ){
			addMensagemErro("A operação não está mais ativa. Por favor reinicie a operação.", request);
			return mapping.findForward(getSubSistemaCorrente(request).getForward());
		}
		setStep(request, 4);
		return mapping.findForward(CRONOGRAMA);
	}
	
	/**
	 * Adiciona um docente ao projeto de pesquisa.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/WEB-INF/jsp/pesquisa/ProjetoPesquisa/docentes.jsp</li>
	 *	</ul>
	 */
	public ActionForward adicionarMembro(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		ProjetoPesquisaForm projetoForm = (ProjetoPesquisaForm) form;
		if( isNotOperacaoAtiva(request, projetoForm) ){
			addMensagemErro("A operação não está mais ativa. Por favor reinicie a operação.", request);
			return mapping.findForward(getSubSistemaCorrente(request).getForward());
		}
		ProjetoPesquisa projeto = projetoForm.getObj();
		MembroProjeto membro = projetoForm.getMembroProjeto();
		CategoriaMembro categoria = projetoForm.getMembroProjeto().getCategoriaMembro();

		
		request.getAttribute("checkEstrangeiro");
		
		GenericDAO dao = getGenericDAO(request);

		// Popular dados de acordo com a categoria do membro
		switch (categoria.getId()) {
			case CategoriaMembro.DOCENTE:
			case CategoriaMembro.SERVIDOR:
				// Popular dados do servidor
				int idServidor;
				// Buscar servidor corretamente
				if (categoria.getId() == CategoriaMembro.DOCENTE ) {
					idServidor = membro.getServidor().getId();
				} else {
					idServidor = projetoForm.getServidorTecnico().getId();
				}
				Servidor servidor = dao.findByPrimaryKey(idServidor, Servidor.class);

				// Validar dados
				if (servidor == null ) {
					addMensagemErro("Informe corretamente o servidor a ser associado a este projeto", request);
				} else if(membro.getFuncaoMembro().getId() == FuncaoMembro.COORDENADOR
						&& servidor.getSituacaoServidor().getId() == Situacao.APOSENTADO
						&& !getDAO(ColaboradorVoluntarioDao.class, request).isColaboradorVoluntario(servidor)) {
					addMensagemErro("Servidores aposentados só podem coordenar projetos se forem " +
							"registrados no DAP como Colaborador Voluntário", request);
				} else {
					membro.setServidor(servidor);
					membro.setPessoa(servidor.getPessoa());
				}
				break;
			case CategoriaMembro.DISCENTE:
				// Popular dados do discente
				Discente discente = dao.findByPrimaryKey(membro.getDiscente().getId(), Discente.class);
				if (discente == null ) {
					addMensagemErro("Informe corretamente o discente a ser associado a este projeto", request);
				} else {
					membro.setDiscente(discente);
					membro.setPessoa(discente.getPessoa());
					membro.setFuncaoMembro(new FuncaoMembro(FuncaoMembro.COLABORADOR));
				}
				break;
			case CategoriaMembro.EXTERNO:
				PessoaDao pessoaDao = getDAO(PessoaDao.class, request);

				ListaMensagens erros = new ListaMensagens();
				if (membro.getDocenteExterno().getInstituicao().getId() == 0) {
					membro.getDocenteExterno().setInstituicao(null);
				}
				
				ProjetoPesquisaValidator.validarDocenteExterno(membro, projetoForm.getCpfExterno(), erros);
				
				//membro.setServidor(null);
				
				if ( !erros.isEmpty() ) {
					membro.getDocenteExterno().setInstituicao(new InstituicoesEnsino());
					addMensagens(erros.getMensagens(), request);
					return mapping.findForward(DOCENTES);
				}

				// Buscar pessoa pelo CPF
				Pessoa pessoa = null;
				if (membro.getDocenteExterno().getPessoa().isInternacional() || ( membro.getDocenteExterno().getPessoa().getId() == 0 && !pessoaDao.existePessoa(membro.getDocenteExterno().getPessoa() ))) {
					pessoa = membro.getDocenteExterno().getPessoa();
					pessoa.setTipo('F');
					Comando comando = getUltimoComando(request);
					try {
						ProjetoPesquisaValidator.limparDadosPessoa(pessoa);

						// Cadastrar a pessoa
						PessoaMov pessoaMov = new PessoaMov();
						pessoaMov.setPessoa(pessoa);
						pessoaMov.setTipoValidacao(PessoaValidator.DOCENTE_EXTERNO);
						pessoaMov.setCodMovimento(SigaaListaComando.CADASTRAR_PESSOA);

						prepareMovimento(SigaaListaComando.CADASTRAR_PESSOA, request);
						pessoa = (Pessoa) executeWithoutClosingSession(pessoaMov, request);
					} catch (NegocioException e) {
						addMensagens(e.getListaMensagens().getMensagens(), request);
						e.printStackTrace();
						return mapping.findForward(DOCENTES);
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						// Re-preparar o comando anterior
						prepareMovimento(comando, request);
					}
				} else {
					Collection<Pessoa> pessoas = pessoaDao.findByCpfCnpj(membro.getDocenteExterno().getPessoa().getCpf_cnpj());
					if(pessoas != null && !pessoas.isEmpty()){
						pessoa = pessoas.iterator().next();
						pessoa = pessoaDao.findByPrimaryKey(pessoa.getId(), Pessoa.class);
					}
				}
				
				if(pessoa == null){
					addMensagemErro("Não foi possível carregar a pessoa do membro externo. Por favor contacte o Suporte através do \"Abrir Chamado\"", request);
					return mapping.findForward(DOCENTES);
				}

				membro.setPessoa(pessoa);

				DocenteExternoDao externoDao = getDAO(DocenteExternoDao.class, request);
				DocenteExterno docenteExterno = externoDao.find(pessoa, membro.getDocenteExterno().getFormacao(), membro.getDocenteExterno().getInstituicao());
				if (docenteExterno != null) {
					membro.setDocenteExterno(docenteExterno);
				} else {
					membro.getDocenteExterno().setPessoa(pessoa);
				}

				break;
		}

		// Verificar se a pessoa informada já está na lista de membros
		boolean achou = false;
		Iterator<MembroProjeto> it = projeto.getMembrosProjeto().iterator();
		while ( it.hasNext() && !achou ) {
			if (it.next().getPessoa().getId() == membro.getPessoa().getId() ) {
				achou = true;
				addMensagemErro("Esta pessoa já foi adicionada à lista de membros do projeto", request);
			}
		}
		
		if (membro.getChDedicada() <= 0)	
			addMensagemErro("O campo \"CH dedicada ao projeto\" é obrigatório e deve ser maior do que zero.", request);
		
		// Verificar se a carga horária informada não ultrapassa o valor máximo determinado
		if ( membro.getChDedicada() > ProjetoPesquisaValidator.LIMITE_CH_DEDICADA ) {
			addMensagemErro(" O limite para a CH dedicada ao projeto é de " + ProjetoPesquisaValidator.LIMITE_CH_DEDICADA + " horas.", request);
		}

		// Verificar a ocorrência de erros
		if (flushOnlyErros(request)) {
			return mapping.findForward(DOCENTES);
		}

		// Salvar coordenador do projeto
		if (membro.getFuncaoMembro().getId() == FuncaoMembro.COORDENADOR) {
			if (membro.isCategoriaDocente()) {
				projeto.setCoordenador(membro.getServidor());
				projeto.setCoordenadorExterno(null);
			} else {
				projeto.setCoordenadorExterno(membro.getDocenteExterno());
				projeto.setCoordenador(null);
				projeto.getCoordenadorExterno().setServidor(null);
			}
		}

		// Limpar campos não utilizados
		ProjetoPesquisaValidator.prepararDadosColaborador(membro);

		// Popular os dados restantes e adicionar à lista de membros do projeto
		membro.setDataInicio(projeto.getDataInicio());
		membro.setDataFim(projeto.getDataFim());
		membro.setCategoriaMembro(dao.findByPrimaryKey(membro.getCategoriaMembro().getId(), CategoriaMembro.class));
		membro.setProjeto(projeto.getProjeto());
		if( membro.getFuncaoMembro() == null || membro.getFuncaoMembro().getId() == 0 ) {
			membro.setFuncaoMembro( new FuncaoMembro(FuncaoMembro.COLABORADOR) );
		}
		membro.setFuncaoMembro(dao.findByPrimaryKey(membro.getFuncaoMembro().getId(), FuncaoMembro.class));
		projeto.getMembrosProjeto().add( membro );

		// Limpar o formulário
		projetoForm.setCpfExterno("");
		projetoForm.setServidorTecnico(new Servidor() );
		projetoForm.setMembroProjeto( new MembroProjeto() );
		projetoForm.getMembroProjeto().setProjeto( projetoForm.getObj().getProjeto() );
		projetoForm.getMembroProjeto().setCategoriaMembro( new CategoriaMembro(membro.getCategoriaMembro().getId()) );
		projetoForm.getMembroProjeto().setFuncaoMembro( new FuncaoMembro(FuncaoMembro.COLABORADOR) );

		return mapping.findForward(DOCENTES);
	}

	/**
	 * Adiciona um financiamento ao projeto de pesquisa.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/WEB-INF/jsp/pesquisa/ProjetoPesquisa/financiamentos.jsp</li>
	 *	</ul>
	 */
	public ActionForward addFinanciamento(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		GenericDAO dao = getGenericDAO(request);

		ProjetoPesquisaForm projetoForm = (ProjetoPesquisaForm) form;
		if( isNotOperacaoAtiva(request, projetoForm) ){
			addMensagemErro("A operação não está mais ativa. Por favor reinicie a operação.", request);
			return mapping.findForward(getSubSistemaCorrente(request).getForward());
		}
		FinanciamentoProjetoPesq financiamento = projetoForm.getFinanciamentoProjetoPesq();
		ListaMensagens lista = new ListaMensagens();
		ProjetoPesquisaValidator.validaAdicionaFinanciamentos(financiamento, lista);
		if (lista.isErrorPresent()) {
			addMensagens(lista.getMensagens(), request);
			return mapping.findForward(FINANCIAMENTOS);
		}
		financiamento.setEntidadeFinanciadora( dao.findByPrimaryKey( financiamento.getEntidadeFinanciadora().getId(), EntidadeFinanciadora.class ));
		financiamento.setTipoNaturezaFinanciamento( dao.findByPrimaryKey( financiamento.getTipoNaturezaFinanciamento().getId(), TipoNaturezaFinanciamento.class ) );
		financiamento.setProjetoPesquisa(projetoForm.getObj());
		projetoForm.getObj().getFinanciamentosProjetoPesq().add( projetoForm.getFinanciamentoProjetoPesq() );

		// zerando variáveis
		projetoForm.setFinanciamentoProjetoPesq( new FinanciamentoProjetoPesq() );
		projetoForm.getFinanciamentoProjetoPesq().setProjetoPesquisa( projetoForm.getObj() );
		projetoForm.setDtInicioFinanciamento("");
		projetoForm.setDtFimFinanciamento("");

		return mapping.findForward(FINANCIAMENTOS);
	}

	/**
	 * Cancela a ação e sai do caso de uso.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/WEB-INF/jsp/pesquisa/ProjetoPesquisa/cronograma.jsp</li>
	 *		<li>sigaa.war/WEB-INF/jsp/pesquisa/ProjetoPesquisa/dados_gerais.jsp</li>
	 *		<li>sigaa.war/WEB-INF/jsp/pesquisa/ProjetoPesquisa/dados_renovacao.jsp</li>
	 *		<li>sigaa.war/WEB-INF/jsp/pesquisa/ProjetoPesquisa/descricao.jsp</li>
	 *		<li>sigaa.war/WEB-INF/jsp/pesquisa/ProjetoPesquisa/docentes.jsp</li>
	 *		<li>sigaa.war/WEB-INF/jsp/pesquisa/ProjetoPesquisa/financiamentos.jsp</li>
	 *		<li>sigaa.war/WEB-INF/jsp/pesquisa/ProjetoPesquisa/resumo_finalizacao.jsp</li>
	 *		<li>sigaa.war/WEB-INF/jsp/pesquisa/ProjetoPesquisa/resumo.jsp</li>
	 *	</ul>
	 */
	@Override
	public ActionForward cancelar(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		removeSession(mapping.getName(), request);
		clearSteps(request);
		return super.cancelar(mapping, form, request, response);

	}

	/**
	 * Carrega um projeto para alteração ou remoção.
	 * Não invocado por JSPs.
	 *
	 * @param id
	 * @param req
	 * @return
	 * @throws ArqException
	 */
	public ProjetoPesquisaForm carregaObject(int id, HttpServletRequest req)
			throws ArqException {

		GenericDAO dao = getGenericDAO(req);
		ProjetoPesquisaForm pForm = (ProjetoPesquisaForm) req.getSession().getAttribute("projetoPesquisaForm");

		ProjetoPesquisa projeto = dao.findByPrimaryKey(id, ProjetoPesquisa.class);
		
		if(projeto.getEdital() != null) {
			projeto.setEdital(dao.refresh(projeto.getEdital()));
			projeto.getEdital().setCota(dao.refresh(projeto.getEdital().getCota()));
		}
		
		if (projeto.getRegistroEntrada() != null ) {
			projeto.getRegistroEntrada().getUsuario().getId();
		}

		pForm.setExibirAnos(true);
		
		Iterator<MembroProjeto> it = projeto.getMembrosProjeto().iterator();
		while (it.hasNext()) {
			MembroProjeto membro = it.next();
			if (membro.getCategoriaMembro() != null) {
				membro.getCategoriaMembro().getDescricao();
			}
		}

		projeto.getHistoricoSituacao().iterator().hasNext();

		if ( projeto.getFinanciamentosProjetoPesq() != null && !projeto.getFinanciamentosProjetoPesq().isEmpty()) {
			projeto.getFinanciamentosProjetoPesq().iterator();
		}

		if ( projeto.getCronogramas() != null && !projeto.getCronogramas().isEmpty() ) {
			projeto.getCronogramas().iterator();
		}

		if ( projeto.getHistoricoSituacao() != null) {
			for (HistoricoSituacaoProjeto historico : projeto.getHistoricoSituacao() ) {
				if(historico.getRegistroEntrada() != null) {
					historico.getRegistroEntrada().getUsuario().getNome();
				}
				if(historico.getSituacaoProjeto() != null) {
					historico.getSituacaoProjeto().getDescricao();
				}
			}
		}

		if( projeto.getFinanciamentosProjetoPesq().size() > 0 ) {
			pForm.setFinanciado(true);
		}


		// Setando as areas nos selects
		if (projeto.getAreaConhecimentoCnpq() != null) {
			projeto.setAreaConhecimentoCnpq( dao.findByPrimaryKey( projeto.getAreaConhecimentoCnpq().getId(), AreaConhecimentoCnpq.class) );
			pForm.setGrandeArea( projeto.getAreaConhecimentoCnpq().getGrandeArea() );
		}

		AreaConhecimentoCnpq area = projeto.getAreaConhecimentoCnpq();
		if (area != null) {
			pForm.setArea( area.getArea() );
			if( area.getSubarea() != null ){
				pForm.setSubarea( projeto.getAreaConhecimentoCnpq().getSubarea() );
			}
			if( area.getEspecialidade() != null ){
				pForm.setEspecialidade( projeto.getAreaConhecimentoCnpq().getEspecialidade() );
			}
		}

		if ( projeto.getLinhaPesquisa() == null ) {
			projeto.setLinhaPesquisa(new LinhaPesquisa());
		}

		if (projeto.getLinhaPesquisa().getGrupoPesquisa() == null ) {
			pForm.setIsolado( true );
			projeto.getLinhaPesquisa().setGrupoPesquisa( new GrupoPesquisa() );
		} else {
			projeto.getLinhaPesquisa().getNome();
			projeto.getLinhaPesquisa().getGrupoPesquisa().getCodigo();
		}

		pForm.setObj( projeto );
		addSession("projetoPesquisaForm", pForm, req);

		return pForm;
	}

	/**
	 * Invoca o Processador para registrar ou submeter um projeto de pesquisa(ProcessadorProjetoPesquisa).
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * 	<ul>
	 *		<li>sigaa.war/WEB-INF/jsp/pesquisa/ProjetoPesquisa/resumo.jsp</li>
	 *	</ul> 
	 */
	public ActionForward chamaModelo(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		
		ProjetoPesquisaForm projetoForm = (ProjetoPesquisaForm) form;
		
		if( isNotOperacaoAtiva(request, projetoForm) ){
			addMensagemErro("A operação não está mais ativa. Por favor reinicie a operação.", request);
			return mapping.findForward(getSubSistemaCorrente(request).getForward());
		}
		
		ListaMensagens lista = new ListaMensagens();
		if ( !projetoForm.getObj().isConcordanciaTermo() )
			lista.addErro("Para realizar o envio do Projeto de Pesquisa é necessário aceitar o termo de concordância.");

		if( projetoForm.isCoordenadorColaborador() && !projetoForm.isCheckClausula() )
			lista.addErro("Você só pode submeter o projeto se concordar com a cláusula condicionante.");
		
		if (lista.isErrorPresent()) {
			addMensagens(lista.getMensagens(), request);
			return mapping.findForward(RESUMO);
		}
		
		if( projetoForm.isIsolado() ) {
			projetoForm.getObj().getLinhaPesquisa().setGrupoPesquisa(null);
		}

		try {
			MovimentoProjetoPesquisa movProjeto = new MovimentoProjetoPesquisa();

			movProjeto.setCodMovimento(projetoForm.getObj().getProjeto().getCodMovimento());
			movProjeto.setProjeto(projetoForm.getObj());
			// Definir dados do arquivo
			if (projetoForm.getArquivoProjeto() != null) {
				movProjeto.setNomeArquivo(projetoForm.getArquivoProjeto().getFileName());
				movProjeto.setContentType(projetoForm.getArquivoProjeto().getContentType());
				movProjeto.setDadosArquivo(projetoForm.getArquivoProjeto().getFileData());
			}
			execute(movProjeto, request);
		} catch (NegocioException e) {
			if( projetoForm.getObj().getProjeto().getCodMovimento().equals(SigaaListaComando.REMOVER_PROJETO_PESQUISA) ){
				request.setAttribute("remove", Boolean.TRUE);
			}

			addMensagens(e.getListaMensagens().getMensagens(), request);
			return mapping.findForward(RESUMO);
		}

		clearSteps(request);
		removeSession(mapping.getName(), request);
		prepareMovimento(projetoForm.getObj().getProjeto().getCodMovimento(), request);

		if( projetoForm.getObj().getProjeto().getCodMovimento() == SigaaListaComando.ENVIAR_PROJETO_PESQUISA ){
			addInformation("Projeto Cadastrado com Sucesso", request);
			
			// Se for projeto associado redirecionar direto para a tela de pendências
			if(projetoForm.isAnexoProjetoBase()){
				ProjetoBaseMBean bean = getMBean("projetoBase", request, response);
				request.setAttribute("id", projetoForm.getObj().getProjeto().getId());
				bean.verificarPendencias();
				return mapping.findForward(RESUMO_INTEGRADO);
			}
			
			request.setAttribute(mapping.getName(), projetoForm);
			return mapping.findForward(COMPROVANTE);

		}
		else if ( projetoForm.getObj().getProjeto().getCodMovimento() == SigaaListaComando.REMOVER_PROJETO_PESQUISA ) {

			addInformation("Projeto Removido com Sucesso", request);
			if (isUserInRole(SigaaPapeis.GESTOR_PESQUISA, request) && getSubSistemaAtual(request) == SigaaSubsistemas.PESQUISA.getId()) {
				return mapping.findForward(getSubSistemaCorrente(request).getForward());
			} else {
				return listByMembro(mapping, form, request, response);
			}

		}
		else if ( projetoForm.getObj().getProjeto().getCodMovimento() == SigaaListaComando.ALTERAR_PROJETO_PESQUISA ){
			addInformation("Projeto Alterado com Sucesso", request);
			request.setAttribute(mapping.getName(), projetoForm);
			return mapping.findForward(COMPROVANTE);

		}
		else if ( projetoForm.getObj().getProjeto().getCodMovimento() == SigaaListaComando.RENOVAR_PROJETO_PESQUISA ){
			addInformation("Projeto Renovado com Sucesso", request);
			request.setAttribute(mapping.getName(), projetoForm);
			return mapping.findForward(COMPROVANTE);

		}

		return mapping.findForward(getSubSistemaCorrente(request).getForward());
	}

	/**
	 * Verifica se a operação ainda se encontra ativa.
	 * Retorna verdadeiro caso a operação não esteja mais ativa. 
	 * Utilizado para evitar problemas quando o usuário volta pelo navegador.
	 * 
	 * @param request
	 * @param projetoForm
	 * @return
	 */
	private boolean isNotOperacaoAtiva(HttpServletRequest request,
			ProjetoPesquisaForm projetoForm) {
		return request.getSession().getAttribute("steps") == null && 
				(projetoForm == null || projetoForm.getObj() == null || projetoForm.getObj().getProjeto().getCodMovimento() == null ||
						!projetoForm.getObj().getProjeto().getCodMovimento().equals(SigaaListaComando.REMOVER_PROJETO_PESQUISA));
	}

	/**
	 * Configura os passos do cadastro de projeto dependendo da escolha de financiamento
	 * Não invocado por JSPs.
	 */
	public void configureSteps( ProjetoPesquisaForm projetoForm, HttpServletRequest request ){

		if ( !projetoForm.isAlteracaoEdital() ) {
			//Adiciona passos do caso de uso
			if ( !projetoForm.isRenovacao() ) {
				addStep(request, "Projeto de Pesquisa > Dados Iniciais", "/pesquisa/projetoPesquisa/criarProjetoPesquisa",	DADOS_GERAIS);
				if(!projetoForm.isAnexoProjetoBase())
					addStep(request, "Descrição", "/pesquisa/projetoPesquisa/criarProjetoPesquisa", DESCRICAO);
				if( projetoForm.isFinanciado() ){
					addStep(request, "Financiamentos", "/pesquisa/projetoPesquisa/criarProjetoPesquisa", FINANCIAMENTOS);
				}
			}
			if(!projetoForm.isAnexoProjetoBase()){
				addStep(request, "Membros", "/pesquisa/projetoPesquisa/criarProjetoPesquisa", DOCENTES);
				addStep(request, "Cronograma", "/pesquisa/projetoPesquisa/criarProjetoPesquisa", CRONOGRAMA);
			}
			addStep(request, "Resumo", "/pesquisa/projetoPesquisa/criarProjetoPesquisa", RESUMO);
		} else {
			addStep(request, "Alterar Edital", "/pesquisa/projetoPesquisa/criarProjetoPesquisa", ALTERAR_EDITAL);
		}
		
	}

	/**
	 * Vai para a página de cronograma do caso de uso.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * 	<ul>
	 *		<li>sigaa.war/WEB-INF/jsp/pesquisa/ProjetoPesquisa/docentes.jsp</li>
	 *	</ul>
	 */
	public ActionForward cronograma(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		if(request.getSession().getAttribute("steps") == null){
			addMensagemErro("A operação não está mais ativa. Por favor reinicie a operação.", request);
			return mapping.findForward(getSubSistemaCorrente(request).getForward());
		}
		ProjetoPesquisaForm projetoForm = (ProjetoPesquisaForm) form;
		ListaMensagens lista = new ListaMensagens();
		
		ProjetoPesquisaValidator.validaDocentes( projetoForm.getObj(), projetoForm, lista );
		if (lista.isErrorPresent()) {
			addMensagens(lista.getMensagens(), request);
			return mapping.findForward(DOCENTES);
		}

		//Inicializar tela do cronograma
		TelaCronograma cronograma = new TelaCronograma(
				projetoForm.getObj().getDataInicio(),
				projetoForm.getObj().getDataFim(),
				projetoForm.getObj().getCronogramas()
			);
		projetoForm.setTelaCronograma(cronograma);

		//informa o passo atual
		if( projetoForm.isFinanciado() ) {
			setStep(request, 5);
		} else {
			setStep(request, 4);
		}

		return mapping.findForward(CRONOGRAMA);
	}

	/**
	 * Vai para a página de descrição do projeto.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * 	<ul>
	 *		<li>sigaa.war/WEB-INF/jsp/pesquisa/ProjetoPesquisa/dados_gerais.jsp</li>
	 *	</ul>
	 *
	 */
	public ActionForward descricao(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		if(request.getSession().getAttribute("steps") == null){
			addMensagemErro("A operação não está mais ativa. Por favor reinicie a operação.", request);
			return mapping.findForward(getSubSistemaCorrente(request).getForward());
		}
		
		GenericDAO dao = getGenericDAO(request);
		ProjetoPesquisaForm projetoForm = (ProjetoPesquisaForm) form;
		ProjetoPesquisa projeto = projetoForm.getObj();

		if (projetoForm.isRenovacao()) {
			ListaMensagens lista = new ListaMensagens();
			ProjetoPesquisaValidator.validaDadosBasicos( projetoForm.getObj(), projetoForm, lista );
			if (lista.isErrorPresent()) {
				addMensagens(lista.getMensagens(), request);
				return mapping.findForward(DADOS_RENOVACAO);
			}
		} else {

			// Setando a última área selecionada no form
			if( projetoForm.getEspecialidade().getId() != 0 ){
			    projeto.setAreaConhecimentoCnpq( dao.findByPrimaryKey( projetoForm.getEspecialidade().getId(), AreaConhecimentoCnpq.class ) );
			} else if ( projetoForm.getSubarea().getId() != 0 ){
			    projeto.setAreaConhecimentoCnpq( dao.findByPrimaryKey( projetoForm.getSubarea().getId(), AreaConhecimentoCnpq.class ) );
			} else if ( projetoForm.getArea().getId() != 0 ){
			    projeto.setAreaConhecimentoCnpq( dao.findByPrimaryKey( projetoForm.getArea().getId(), AreaConhecimentoCnpq.class ) );
			    if(projeto.getAreaConhecimentoCnpq().getGrandeArea().getId() != projetoForm.getGrandeArea().getId())
			        projeto.setAreaConhecimentoCnpq( null );
			} else {
			    projeto.setAreaConhecimentoCnpq( null );
			}
	
			// Buscar dados da linha de pesquisa, caso ela seja uma linha existente
			if (projetoForm.getObj().getLinhaPesquisa().getId() > 0) {
				LinhaPesquisa linha = dao.findByPrimaryKey( projeto.getLinhaPesquisa().getId(), LinhaPesquisa.class );
	
				if ( !projetoForm.isIsolado() ){
					GrupoPesquisa grupo = dao.findByPrimaryKey( projeto.getLinhaPesquisa().getGrupoPesquisa().getId(), GrupoPesquisa.class );
					if (grupo != null) {
						linha.setGrupoPesquisa(grupo);
					}
				}
				
				if(projeto.getLinhaPesquisa().getNome().trim().equals(linha.getNome().trim()))
					projeto.setLinhaPesquisa( linha );
				else {
					String nomeLinha = projeto.getLinhaPesquisa().getNome();
					projeto.setLinhaPesquisa( new LinhaPesquisa() );
					projeto.getLinhaPesquisa().setNome(nomeLinha);
					projeto.getLinhaPesquisa().setGrupoPesquisa(linha.getGrupoPesquisa());
				}
	
				if ( linha.getGrupoPesquisa() == null ) {
					linha.setGrupoPesquisa( new GrupoPesquisa() );
				}
			}
	
			// Definir centro do projeto
			projeto.setCentro( dao.findByPrimaryKey( projeto.getCentro().getId(), Unidade.class) );
	
			// Validar dados iniciais
			ListaMensagens lista = new ListaMensagens();
			ProjetoPesquisaValidator.validaDadosBasicos( projetoForm.getObj(), projetoForm, lista );
			if (lista.isErrorPresent()) {
				addMensagens(lista.getMensagens(), request);
				return mapping.findForward(DADOS_GERAIS);
			}
	
			// Buscar a linha de pesquisa, caso tenha selecionada uma pré-cadastrada
			if (projetoForm.getObj().getLinhaPesquisa().getGrupoPesquisa() != null && projetoForm.getObj().getLinhaPesquisa().getGrupoPesquisa().getId() > 0) {
				GrupoPesquisa grupo = dao.findByPrimaryKey( projeto.getLinhaPesquisa().getGrupoPesquisa().getId(), GrupoPesquisa.class );
				grupo.getNomeCompleto();
				projeto.getLinhaPesquisa().setGrupoPesquisa( grupo );
			}
	
			if( !isEmpty( projeto.getCategoria() ) ){
				projeto.setCategoria( dao.findByPrimaryKey( projeto.getCategoria().getId(),
						CategoriaProjetoPesquisa.class) );
			}
		
			// Se o projeto é interno, popular os dados relacionados
			if (projeto.isInterno() && projeto.getEdital() != null &&  projeto.getEdital().getInicioExecucaoProjetos() != null && projeto.getEdital().getFimExecucaoProjetos() != null) {
				projeto.setEdital( dao.findByPrimaryKey( projeto.getEdital().getId(), EditalPesquisa.class  ) );
				projeto.getEdital().setCota(dao.refresh(projeto.getEdital().getCota()));
				projeto.setDataInicio( projeto.getEdital().getInicioExecucaoProjetos() );
				projeto.setDataFim( CalendarUtils.adicionarAnos(projeto.getEdital().getFimExecucaoProjetos(), projeto.getTempoEmAnoProjeto()));
				projeto.setCategoria( projeto.getEdital().getCategoria() );
			}
		}

		if(!projetoForm.isAnexoProjetoBase()){
			// Continuar o wizard
			setStep(request, 2);
			return mapping.findForward(DESCRICAO);
		} else {
			return resumo(mapping, form, request, response);
		}

	}

	/**
	 * Vai para a página de docentes do caso de uso.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * 	<ul>
	 *		<li>sigaa.war/WEB-INF/jsp/pesquisa/ProjetoPesquisa/descricao.jsp</li>
	 *		<li>sigaa.war/WEB-INF/jsp/pesquisa/ProjetoPesquisa/dados_renovacao.jsp</li>
	 *	</ul>
	 *
	 */
	public ActionForward docentes(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		
		if(request.getSession().getAttribute("steps") == null){
			addMensagemErro("A operação não está mais ativa. Por favor reinicie a operação.", request);
			return mapping.findForward(getSubSistemaCorrente(request).getForward());
		}
		ProjetoPesquisaForm projetoForm = (ProjetoPesquisaForm) form;
		
		// Validar dados
		ListaMensagens lista = new ListaMensagens();
		if (projetoForm.isRenovacao()) {
			ProjetoPesquisaValidator.validaDadosBasicos( projetoForm.getObj(), projetoForm, lista );
			ProjetoPesquisaValidator.validaDetalhes(projetoForm.getObj(), projetoForm, lista, (Usuario) getUsuarioLogado(request) );
		} else {
			ProjetoPesquisaValidator.validaDetalhes(projetoForm.getObj(), projetoForm, lista, (Usuario) getUsuarioLogado(request)  );
		}

		if ( projetoForm.isFinanciado() && projetoForm.getObj().getFinanciamentosProjetoPesq().isEmpty() ) 
			lista.addErro("É necessário informar ao menos um financiamento.");
		
		if (projetoForm.isRenovacao() && projetoForm.getObj().isInterno()) {
			projetoForm.getObj().setEdital( getGenericDAO(request).findAndFetch(
					projetoForm.getObj().getEdital().getId(), EditalPesquisa.class, "cota") );
			projetoForm.getObj().setDataFim( projetoForm.getObj().getEdital().getFimExecucaoProjetos() );
		}
		
		if (lista.isErrorPresent()) {
			addMensagens(lista.getMensagens(), request);
			if (projetoForm.isRenovacao()) {
				return mapping.findForward(DADOS_RENOVACAO);
			} else if ( projetoForm.isFinanciado() ) {
				return mapping.findForward( FINANCIAMENTOS );
			} else {
				return mapping.findForward(DESCRICAO);
			}
		}

		setStep(request, 3);
		projetoForm.setMembroProjeto( new MembroProjeto() );
		projetoForm.getMembroProjeto().setProjeto( projetoForm.getObj().getProjeto() );
		projetoForm.getMembroProjeto().setCategoriaMembro( new CategoriaMembro(CategoriaMembro.DOCENTE) );
		projetoForm.getMembroProjeto().setFuncaoMembro( new FuncaoMembro(FuncaoMembro.COLABORADOR) );
		return mapping.findForward(DOCENTES);
	}

	/**
	 * Popula os dados do projeto de pesquisa para serem alterados.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * 	<ul>
	 *		<li>sigaa.war/WEB-INF/jsp/pesquisa/ProjetoPesquisa/lista_projetos_membro.jsp</li>
	 *	</ul>
	 *
	 */
	@Override
	public ActionForward edit(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res) throws Exception {
		String paramId = req.getParameter("id");
		Integer id;
		if(paramId == null)
			id = (Integer) req.getAttribute("id");
		else
			id = Integer.parseInt(paramId);
		ProjetoPesquisaForm pForm = carregaObject(id, req);

		req.setAttribute("alteracao", true);
		popular(mapping, pForm, req, res);
		ProjetoPesquisa projeto = pForm.getObj();

		if (projeto.getSituacaoProjeto().getId() == TipoSituacaoProjeto.GRAVADO) {
			prepareMovimento(SigaaListaComando.ENVIAR_PROJETO_PESQUISA, req);
			pForm.getObj().getProjeto().setCodMovimento( SigaaListaComando.ENVIAR_PROJETO_PESQUISA );
		} else {
			prepareMovimento(SigaaListaComando.ALTERAR_PROJETO_PESQUISA, req);
			pForm.getObj().getProjeto().setCodMovimento( SigaaListaComando.ALTERAR_PROJETO_PESQUISA );
		}

		// Inicializando form
		pForm.setDataInicio( Formatador.getInstance().formatarData( pForm.getObj().getDataInicio() ) );
		pForm.setDataFim( Formatador.getInstance().formatarData( pForm.getObj().getDataFim() ) );

		// Configurando passos
		pForm.setFinanciado( !projeto.isInterno() );
		configureSteps( pForm, req );

		return mapping.findForward(DADOS_GERAIS);
	}


	/**
	 * Vai para a página de financiamentos do caso de uso.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * 	<ul>
	 *		<li>sigaa.war/WEB-INF/jsp/pesquisa/ProjetoPesquisa/descricao.jsp</li>
	 *	</ul>
	 *
	 */
	public ActionForward financiamentos(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		if(request.getSession().getAttribute("steps") == null){
			addMensagemErro("A operação não está mais ativa. Por favor reinicie a operação.", request);
			return mapping.findForward(getSubSistemaCorrente(request).getForward());
		}
		ProjetoPesquisaForm projetoForm = (ProjetoPesquisaForm) form;
		ListaMensagens lista = new ListaMensagens();
		ProjetoPesquisaValidator.validaDetalhes(projetoForm.getObj(), projetoForm, lista, (Usuario) getUsuarioLogado(request));

		if (lista.isErrorPresent()) {
			addMensagens(lista.getMensagens(), request);
			return mapping.findForward(DESCRICAO);
		}

		//informa o passo atual
		setStep(request, 4);
		return mapping.findForward( FINANCIAMENTOS );
	}

	/**
	 * Retorna a tela atual em que o usuário se encontra.
	 * 
	 * @param req
	 * @param projetoForm
	 * @return
	 */
	private String getCurrentView(HttpServletRequest req, ProjetoPesquisaForm projetoForm) {
		int step = getCurrentStep(req);

		if (!projetoForm.isFinanciado() && step > 2)
			step++;

		switch (step) {
			case 1: return projetoForm.isAlteracaoEdital() ? RESUMO : DADOS_GERAIS;
			case 2: return DESCRICAO;
			case 3: return FINANCIAMENTOS;
			case 4: return DOCENTES;
			case 5: return CRONOGRAMA;
			case 6: return RESUMO;
			default: return null;
		}
	}

	/**
	 * Grava no banco os dados informados pelo usuário até o momento.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * 	<ul>
	 *		<li>sigaa.war/WEB-INF/jsp/pesquisa/ProjetoPesquisa/cronograma.jsp</li>
	 *		<li>sigaa.war/WEB-INF/jsp/pesquisa/ProjetoPesquisa/descricao.jsp</li>
	 *		<li>sigaa.war/WEB-INF/jsp/pesquisa/ProjetoPesquisa/docentes.jsp</li>
	 *		<li>sigaa.war/WEB-INF/jsp/pesquisa/ProjetoPesquisa/financiamentos.jsp</li>
	 *		<li>sigaa.war/WEB-INF/jsp/pesquisa/ProjetoPesquisa/resumo.jsp</li>
	 *	</ul>
	 *  
	 */
	public ActionForward gravar(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res)
			throws Exception {
		if(req.getSession().getAttribute("steps") == null){
			addMensagemErro("A operação não está mais ativa. Por favor reinicie a operação.", req);
			return mapping.findForward(getSubSistemaCorrente(req).getForward());
		}
		ProjetoPesquisaForm projetoForm = (ProjetoPesquisaForm) form;

		// Preparar para gravação
		if( projetoForm.isIsolado() && projetoForm.getObj().getLinhaPesquisa() != null ) {
			projetoForm.getObj().getLinhaPesquisa().setGrupoPesquisa(null);
		}

		if (projetoForm.getTelaCronograma() != null) {
			// Obter objetos cronogramas a partir dos dados do formulário
			projetoForm.getTelaCronograma().definirCronograma(req);

			// Obter atividades do cronograma a partir do formulário
			List<CronogramaProjeto> cronogramas = projetoForm.getTelaCronograma().getCronogramas();
			for (CronogramaProjeto cronograma : cronogramas) {
				cronograma.setProjeto(projetoForm.getObj().getProjeto());
			}
			projetoForm.getObj().setCronogramas(cronogramas);
		}
		
		ListaMensagens lista = new ListaMensagens();
		ProjetoPesquisaValidator.validaTamanhoDetalhes(projetoForm.getObj(), projetoForm, lista);

		if (lista.isErrorPresent()) {
			addMensagens(lista.getMensagens(), req);
			return mapping.findForward(getCurrentView(req, projetoForm));
		}

		Comando comandoAnterior = getUltimoComando(req);

		// Gravar projeto de pesquisa
		try {
			prepareMovimento(SigaaListaComando.GRAVAR_PROJETO_PESQUISA, req);

			MovimentoProjetoPesquisa movProjeto = new MovimentoProjetoPesquisa();
			movProjeto.setCodMovimento(SigaaListaComando.GRAVAR_PROJETO_PESQUISA);

			movProjeto.setProjeto(projetoForm.getObj());
			// Definir dados do arquivo
			if (projetoForm.getArquivoProjeto() != null) {
				movProjeto.setNomeArquivo(projetoForm.getArquivoProjeto().getFileName());
				movProjeto.setContentType(projetoForm.getArquivoProjeto().getContentType());
				movProjeto.setDadosArquivo(projetoForm.getArquivoProjeto().getFileData());
			}

			execute(movProjeto, req);

			if ( projetoForm.isAlteracaoEdital() )
				addInformation("Edital alterado com sucesso.", req);
			else
				addInformation("Projeto de Pesquisa gravado com sucesso!", req);
			
		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens().getMensagens(), req);
		} catch (DAOException e) {
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			String stacktrace = sw.toString();

			// Tratar problema de sessão suja
			if (e.getCause() != null && stacktrace.indexOf("org.hibernate.StaleStateException") != -1) {
				addMensagemErro("Atenção! Você já salvou este projeto de pesquisa. Caso deseje editá-lo, reinicie o processo.", req);
				return cancelar(mapping, form, req, res);
			} else {
				throw e;
			}
		}

		// Voltar para o comando anterior
		prepareMovimento(comandoAnterior, req);
		projetoForm.getObj().getProjeto().setCodMovimento(comandoAnterior);

		if ( projetoForm.getObj().getLinhaPesquisa().getGrupoPesquisa() == null ) {
			projetoForm.getObj().getLinhaPesquisa().setGrupoPesquisa(new GrupoPesquisa());
		}

		forceCloseSession(req);

		// Voltar à tela em que o usuário estava
		return mapping.findForward(getCurrentView(req, projetoForm));
	}

	/**
	 * Prepara a lista de projetos do coordenador (usuário logado).
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/WEB-INF/jsp/pesquisa/ProjetoPesquisa/dados_renovacao.jsp</li>
	 *		<li>sigaa.war/portais/docente/menu_docente.jsp</li>
	 *	</ul>
	 */
	public ActionForward listaRenovacao(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res) throws Exception {

		if ( !getAcessoMenu(req).isDocente() ) {
			addMensagemErro(" Somente docentes podem acessar esta operação ", req);
			return cancelar(mapping,form,req,res);
		}

		ProjetoPesquisaDao daoProjeto = getDAO( ProjetoPesquisaDao.class, req );
		Collection<ProjetoPesquisa> projetos = daoProjeto.findParaRenovacao( ( (Usuario) getUsuarioLogado(req)).getServidor() );

		if( projetos == null || projetos.size() == 0 ){
			addMensagemErro(" Não há projetos de pesquisa sob sua coordenação passíveis de renovação", req);
			return mapping.findForward( "portalDocente" );
		}

		ParametroHelper helper = ParametroHelper.getInstance();

		req.setAttribute("siglaNomeGestoraPesquisa", helper.getParametro(ParametrosPesquisa.SIGLA_NOME_PRO_REITORIA_PESQUISA));
		req.setAttribute("qtdMaximaRenovacoes", helper.getParametroInt(ParametrosPesquisa.NUMERO_MAXIMO_RENOVACOES_PROJETO));
		req.setAttribute( "lista", projetos );
		req.setAttribute( "renovacao", true );

		return mapping.findForward(LISTA_PROJETOS_RENOVACAO);

	}

	/**
	 * Verifica se está no período de renovação de projetos de pesquisa.
	 * 
	 * @param calendario
	 * @param req
	 * @return
	 */
	private boolean isPeriodoRenovacao(CalendarioPesquisa calendario, HttpServletRequest req) {
		return CalendarUtils.isDentroPeriodo(calendario.getInicioEnvioProjetos(), calendario.getFimEnvioProjetos()) ||
				CalendarUtils.isDentroPeriodo(calendario.getInicioSegundaRenovacao(), calendario.getFimSegundaRenovacao());
	}

	/**
	 * Prepara a lista de projetos do coordenador (usuário logado).
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * 	<ul>
	 *		<li>sigaa.war/portais/docente/menu_docente.jsp</li>
	 *	</ul>
	 *
	 */
	public ActionForward listByMembro(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res) throws Exception {

		Usuario usuario = (Usuario) getUsuarioLogado(req);

		Servidor servidor = usuario.getVinculoAtivo().getServidor();
		DocenteExterno docenteExterno = usuario.getVinculoAtivo().getDocenteExterno();

		if ( servidor == null && docenteExterno == null) {
			addMensagemErro("Você não possui acesso a esta operação", req);
			return cancelar(mapping,form,req,res);
		}

		Pessoa pessoa = servidor != null ? servidor.getPessoa() : docenteExterno.getPessoa();

		ProjetoPesquisaDao daoProjeto = getDAO( ProjetoPesquisaDao.class, req );
		HistoricoSituacaoProjetoDao dao = getDAO( HistoricoSituacaoProjetoDao.class, req );
		try {
			Collection<ProjetoPesquisa> projetosGravados = daoProjeto.findGravadosByUsuario(pessoa);
			Collection<ProjetoPesquisa> projetos = daoProjeto.findByMembro( pessoa );
			
			Collection<Projeto> projetosBase = new ArrayList<Projeto>(); 
			for (ProjetoPesquisa projetoPesquisa : projetos)
				 projetosBase.add( projetoPesquisa.getProjeto() );
			
			Collection<Integer> projetosJaEmExecucao = dao.projetoJaEmExecucao(projetosBase);
			for (ProjetoPesquisa projetoPesquisa : projetos) {
				if ( !projetosJaEmExecucao.contains(projetoPesquisa.getProjeto().getId()) && projetoPesquisa.isInterno() 
						&& ( projetoPesquisa.getProjeto().getSituacaoProjeto().getId() == TipoSituacaoProjeto.CADASTRADO || 
								projetoPesquisa.getProjeto().getSituacaoProjeto().getId() == TipoSituacaoProjeto.SUBMETIDO ))
					projetoPesquisa.setPermiteAlterarEdital( true );
			}
			
			// Não mostrar duas vezes o mesmo projeto
			for (ProjetoPesquisa projeto : projetosGravados) {
				if (projetos.contains(projeto)) {
					projetos.remove(projeto);
				}
			}
			
			req.setAttribute("projetos", projetos);
			req.setAttribute("listaGravados", projetosGravados);
			
		} finally {
			daoProjeto.close();
		}

		return mapping.findForward(LISTA_PROJETOS_MEMBRO);

	}
	
	/**
	 * Popula o cadastro de um projeto de pesquisa associado a um projeto-base.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * 	<ul>
	 *		<li>sigaa.war/projetos/ProjetoBase/resumo_integrado.jsp</li>
	 *	</ul>
	 *
	 */
	public ActionForward popularProjetoBase(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res)
			throws Exception {
		int idProjetoBase = getParameterInt(req, "idProjetoBase", 0);
		if(idProjetoBase == 0)
			return mapping.findForward(getSubSistemaCorrente(req).getForward());
		req.setAttribute("idProjetoBase", idProjetoBase);
		Collection<ProjetoPesquisa> projetos = getGenericDAO(req).findByExactField(ProjetoPesquisa.class, "projeto.id", idProjetoBase);
		if(projetos != null && !projetos.isEmpty()){
			ProjetoPesquisa projetoPesquisa = projetos.iterator().next();
			req.setAttribute("id", projetoPesquisa.getId());
			return edit(mapping, form, req, res);
		} else {
			return popular(mapping, form, req, res);
		}
	}

	/**
	 * Popula os atributos de sessão para serem usados em todo o caso de uso.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * 	<ul>
	 *		<li>sigaa.war/portais/docente/menu_docente.jsp</li>
	 *	</ul>
	 *
	 */
	@SuppressWarnings("unchecked")
	public ActionForward popular(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res)
			throws Exception {
		ProjetoPesquisaForm projetoForm = (ProjetoPesquisaForm) form;
		if (req.getAttribute("renovacao") == null && req.getAttribute("alteracao") == null) {
			projetoForm = new ProjetoPesquisaForm();
		}
		ProjetoPesquisa projeto = projetoForm.getObj();

		String paramInterno = req.getParameter("interno");
		Boolean interno;
		if ( paramInterno == null ) {
			interno = (Boolean)  req.getAttribute("interno");
		} else {
			interno = Boolean.parseBoolean( paramInterno );
		}

		if (projeto.getId() == 0) {
			projeto.setInterno( interno != null ? interno : false );
		}
		
		Integer idProjetoBase = (Integer) req.getAttribute("idProjetoBase");
		if(idProjetoBase != null){
			GenericDAO dao = getGenericDAO(req);
			projeto.setProjeto(dao.findByPrimaryKey(idProjetoBase, Projeto.class));
			projeto.getProjeto().getEquipe().iterator();
			projeto.getProjeto().getCronograma().iterator();
			if(projeto.getAreaConhecimentoCnpq() == null || projeto.getAreaConhecimentoCnpq().getId() == 0)
				projeto.setAreaConhecimentoCnpq(projeto.getProjeto().getAreaConhecimentoCnpq());
			if (projeto.getAreaConhecimentoCnpq() != null) {
				projeto.setAreaConhecimentoCnpq( dao.findByPrimaryKey( projeto.getAreaConhecimentoCnpq().getId(), AreaConhecimentoCnpq.class) );
				projetoForm.setGrandeArea( projeto.getAreaConhecimentoCnpq().getGrandeArea() );
			}
			Iterator<HistoricoSituacaoProjeto> it = projeto.getProjeto().getHistoricoSituacao().iterator();
			while(it.hasNext()){
				HistoricoSituacaoProjeto historico = it.next();
				historico.getSituacaoProjeto().getDescricao();
				historico.getRegistroEntrada().getUsuario().getPessoa().getNome();
				historico.getRegistroEntrada().getUsuario().getLogin();
			}
			//Inicializar tela do cronograma
			TelaCronograma cronograma = new TelaCronograma(
					projeto.getProjeto().getDataInicio(),
					projeto.getProjeto().getDataFim(),
					projeto.getProjeto().getCronograma()
				);
			projetoForm.setTelaCronograma(cronograma);
		}
		
		// Define se o projeto é um anexo de um projeto base
		projetoForm.setAnexoProjetoBase(projeto.isProjetoAssociado());

		// Projetos internos não possuem financiamento externo
		projetoForm.setFinanciado( !projeto.isInterno() );

		// Popular dados auxiliares do cadastro
		popularDadosAuxiliares(projetoForm, req);

		// Verificar se existem editais com período de submissão aberto
		Collection<EditalPesquisa> editais = (Collection<EditalPesquisa>) projetoForm.getReferenceData().get("editaisAbertos");
		if ((editais == null || editais.isEmpty()) && projeto.isInterno()) {

			// Verificar o prazo de envio
			CalendarioPesquisaDao calendarioDao = getDAO(CalendarioPesquisaDao.class, req);
			CalendarioPesquisa calendario = calendarioDao.findVigente();

			EditalPesquisa edital = getDAO(EditalPesquisaDao.class, req).findUltimoEditalDistribuicaoCotas();

			// Verificar se está no segundo período de submissão
			if ( isPeriodoRenovacao(calendario, req) && edital != null) {
				projeto.setEdital(edital);
				projetoForm.setSegundaChamada(true);
			} else {
				addMensagemErro("Não há editais de pesquisa com período de submissão aberto", req);
				return mapping.findForward(getSubSistemaCorrente(req).getForward());
			}
			
		}

		// Preparar movimento de envio de projeto
		prepareMovimento(SigaaListaComando.ENVIAR_PROJETO_PESQUISA,req);
		projetoForm.getObj().getProjeto().setCodMovimento(SigaaListaComando.ENVIAR_PROJETO_PESQUISA);

		// Evitar erros de objetos nulos
		if (projeto.getLinhaPesquisa() == null ) {
			projeto.setLinhaPesquisa( new LinhaPesquisa() );
		}
		if(  projeto.getLinhaPesquisa().getGrupoPesquisa() == null ) {
			projeto.getLinhaPesquisa().setGrupoPesquisa( new GrupoPesquisa() );
		}

		// Definir a unidade do projeto
		Usuario usuario = (Usuario) getUsuarioLogado(req);
		Unidade unidade = projeto.getUnidade();
		if (unidade == null || unidade.getId() <= 0) {
			projeto.setUnidade( usuario.getVinculoAtivo().getUnidade() );
			unidade = projeto.getUnidade();
		}
		if (projeto.getCentro() == null || projeto.getCentro().getId() <= 0) {
			projeto.setCentro( unidade.getGestora() );
		}

		// Configurar passos do wizard
		clearSteps(req);
		configureSteps( projetoForm, req );
		setStep(req, 1);

		req.getSession().setAttribute(mapping.getName(), projetoForm);
		return mapping.findForward(DADOS_GERAIS);
	}

	/**
	 * Método para popular as coleções auxiliares a serem utilizadas durante o cadastro
	 *
	 */
	private void popularDadosAuxiliares(ProjetoPesquisaForm projetoForm, HttpServletRequest req) throws ArqException {

		ProjetoPesquisaDao dao = getDAO(ProjetoPesquisaDao.class, req);
		EditalPesquisaDao editalDao = getDAO(EditalPesquisaDao.class, req);
		AreaConhecimentoCnpqDao areaConhecimentoDao = getDAO(AreaConhecimentoCnpqDao.class, req);

		Map<String, Object> referenceData = projetoForm.getReferenceData();
		referenceData.put( "centros", dao.findCentrosUnidadesPesquisa());

		// Dados utilizados na tela de dados básicos do projeto
		referenceData.put( "editaisAbertos", (projetoForm.isAnexoProjetoBase() && !projetoForm.isRenovacao()) ? editalDao.findAllAssociados(projetoForm.getObj().getProjeto().getEdital()) : 
			isUserInRole(SigaaPapeis.GESTOR_PESQUISA, req) && getSubSistemaCorrente(req).equals(SigaaSubsistemas.PESQUISA) ? editalDao.findAllAtivos() : editalDao.findAllAbertos());
		
		if(projetoForm.getObj().isInterno()){
			Collection<EditalPesquisa> editais = new ArrayList<EditalPesquisa>();
			if (projetoForm.getObj() != null && projetoForm.getObj().getEdital() != null)
				editais.add(projetoForm.getObj().getEdital());
			else {
				editais.addAll((Collection<EditalPesquisa>) referenceData.get("editaisAbertos"));
				projetoForm.getObj().setInterno(false);
			}	
			if (editais.size() == 1) {
				for (EditalPesquisa editalAtual : editais) {
					EditalPesquisa editalPesquisa = dao.findByPrimaryKey(editalAtual.getId(), EditalPesquisa.class);
					projetoForm.setExibirAnos(false);
					if (!ValidatorUtil.isEmpty(editalPesquisa)) {
						projetoForm.getObj().getEdital().setInicioExecucaoProjetos(editalPesquisa.getInicioExecucaoProjetos());
						projetoForm.getObj().getEdital().setFimExecucaoProjetos(editalPesquisa.getFimExecucaoProjetos());
						projetoForm.getObj().getEdital().setCota(dao.refresh(editalPesquisa.getCota()));
						
						if (!ValidatorUtil.isEmpty(editalPesquisa.getFimExecucaoProjetos())) {
							Collection<AnoProjetoPesquisa> anos = new ArrayList<AnoProjetoPesquisa>(); 
							for (int i = -2; i < 1; i++) {
								Date fimExecucao = CalendarUtils.adicionarAnos(editalPesquisa.getFimExecucaoProjetos(), i);
								if (fimExecucao.after(editalPesquisa.getInicioExecucaoProjetos())) {
									AnoProjetoPesquisa anoProjeto = new AnoProjetoPesquisa();
									anoProjeto.setFimExecucao(fimExecucao);
									anoProjeto.setQntAnos(i);
									anos.add(anoProjeto);
								}
							}
							
							projetoForm.setExibirAnos(true);
							projetoForm.getReferenceData().put("anos", anos);
						}
					}
				}
			} else{
				projetoForm.setExibirAnos(false);
			}
		} else{
			projetoForm.setExibirAnos(false);
		}				
			
			
		
		referenceData.put( "grandeAreasCnpq" , areaConhecimentoDao.findGrandeAreasConhecimentoCnpq() );
		referenceData.put( "gruposPesquisa" , dao.findAllProjection(GrupoPesquisa.class, "nome", "asc", new String[]{"id", "nome", "codigo"}) );
		addSession( "categorias", dao.findAllAtivos(CategoriaProjetoPesquisa.class, "ordem"), req);

		// Dados utilizados na tela de financiamentos
		addSession( "entidadesFinanciadoras", dao.findAllProjection(EntidadeFinanciadora.class, "nome", "asc", new String[]{"id", "nome"}), req);
		addSession( "tiposNaturezaFinanciamento", dao.findAll( TipoNaturezaFinanciamento.class, "descricao", "asc" ), req);

		// Dados utilizados para cadastro de membros externos
		referenceData.put( "formacoes", dao.findAllProjection(Formacao.class, new String[]{"id", "denominacao"}));
		referenceData.put( "tipos", dao.findAllProjection(TipoDocenteExterno.class, new String[]{"id", "denominacao"}));
		referenceData.put( "instituicoes", dao.findAllProjection(InstituicoesEnsino.class, "nome", "asc", new String[]{"id", "nome", "sigla"}));
	}

	/**
	 * Método responsável pela remoção de um projeto de pesquisa.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * 	<ul>
	 *		<li>sigaa.war/WEB-INF/jsp/pesquisa/ProjetoPesquisa/lista_projetos_membro.jsp</li>
	 *	</ul>
	 *
	 */
	@Override
	public ActionForward remove(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res) throws Exception {

		ProjetoPesquisaForm projetoForm = (ProjetoPesquisaForm) form;
		req.setAttribute("remove", Boolean.TRUE);
		Integer id = new Integer(req.getParameter("id"));

		projetoForm = carregaObject(id, req);
		prepareMovimento(SigaaListaComando.REMOVER_PROJETO_PESQUISA, req);
		projetoForm.getObj().getProjeto().setCodMovimento( SigaaListaComando.REMOVER_PROJETO_PESQUISA );
		return mapping.findForward(RESUMO);
	}

	/**
	 * Remove um docente do projeto de pesquisa.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * 	<ul>
	 *		<li>sigaa.war/WEB-INF/jsp/pesquisa/ProjetoPesquisa/docentes.jsp</li>
	 *	</ul>
	 *
	 */
	public ActionForward removeMembro(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		ProjetoPesquisaForm projetoForm = (ProjetoPesquisaForm) form;
		removePorPosicao(projetoForm.getObj().getMembrosProjeto(), projetoForm.getPosLista());
		return mapping.findForward(DOCENTES);

	}

	/**
	 * Remove um financiamento do projeto de pesquisa.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * 	<ul>
	 *		<li>sigaa.war/WEB-INF/jsp/pesquisa/ProjetoPesquisa/financiamentos.jsp</li>
	 *	</ul>
	 *
	 */
	public ActionForward removeFinanciamento(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		ProjetoPesquisaForm projetoForm = (ProjetoPesquisaForm) form;
		removePorPosicao(projetoForm.getObj().getFinanciamentosProjetoPesq(), projetoForm.getPosLista());
		return mapping.findForward(FINANCIAMENTOS);

	}

	/**
	 * Ação para renovar projetos de pesquisa.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * 	<ul>
	 *		<li>sigaa.war/WEB-INF/jsp/pesquisa/ProjetoPesquisa/lista_projetos_renovacao.jsp</li>
	 *	</ul>
	 *
	 */
	public ActionForward renovar(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res)
			throws Exception {

		ProjetoPesquisaForm projetoForm = (ProjetoPesquisaForm) form;
		projetoForm = carregaObject(projetoForm.getObj().getId(), req);
		ProjetoPesquisa projeto = projetoForm.getObj();

		// Validar período de renovação
		CalendarioPesquisaDao calendarioDao = getDAO(CalendarioPesquisaDao.class, req);
		CalendarioPesquisa calendario = calendarioDao.findVigente();

		if ( projeto.isInterno() && !isPeriodoRenovacao(calendario, req) ) {
			addMensagemErro("Atenção! O sistema não está aberto para renovação de projetos de pesquisa internos", req);
			return listaRenovacao(mapping, form, req, res);
		}

		ListaMensagens lista = new ListaMensagens();
		ProjetoPesquisaValidator.validaRenovacao( projeto, lista );
		if (lista.isErrorPresent()) {
			addMensagens(lista.getMensagens(), req);
			return listaRenovacao(mapping, form, req, res);
		}

		// Preparar dados para a renovação




		// Realizar verificações de Projetos Internos
		if ( projeto.isInterno() ) {
			EditalPesquisa edital = getDAO(EditalPesquisaDao.class, req).findUltimoEditalDistribuicaoCotas();
			projeto.setEdital( edital );
			projeto.setDataFim( edital.getCota().getFim() );
		}

		// Preparar dados
		req.setAttribute( "interno", projetoForm.getObj().isInterno() );
		projetoForm.setRenovacao(true);
		req.setAttribute("renovacao", true);
		popular(mapping, form, req, res);

		projetoForm = (ProjetoPesquisaForm) req.getSession().getAttribute(mapping.getName());
		projeto = projetoForm.getObj();

		if (projeto.getCentro() == null || projeto.getCentro().getId() <= 0) {
			projeto.setCentro((Unidade) projeto.getUnidade().getUnidadeGestora());
		}

		Collection<EditalPesquisa> editais =  (Collection<EditalPesquisa>) projetoForm.getReferenceData().get("editaisAbertos");
		
		if ( editais.isEmpty() && projeto.isInterno() ) {
			addMensagemErro("Não há Editais abertos.", req);
			return listaRenovacao(mapping, form, req, res);
		}
		
		prepareMovimento( SigaaListaComando.RENOVAR_PROJETO_PESQUISA, req );
		projetoForm.getObj().getProjeto().setCodMovimento(SigaaListaComando.RENOVAR_PROJETO_PESQUISA);

		req.getSession().setAttribute(mapping.getName(), projetoForm);
		return mapping.findForward(DADOS_RENOVACAO);
	}

	/**
	 * Vai para a página de resumo do caso de uso.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * 	<ul>
	 *		<li>sigaa.war/WEB-INF/jsp/pesquisa/ProjetoPesquisa/cronograma.jsp</li>
	 *	</ul>
	 *
	 */
	public ActionForward resumo(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		if(request.getSession().getAttribute("steps") == null){
			addMensagemErro("A operação não está mais ativa. Por favor reinicie a operação.", request);
			return mapping.findForward(getSubSistemaCorrente(request).getForward());
		}
		ProjetoPesquisaForm projetoForm = (ProjetoPesquisaForm) form;
		
		if(projetoForm.isAnexoProjetoBase()){
			projetoForm.getObj().getMembrosProjeto().iterator();
			projetoForm.getObj().getCronogramas().iterator();
		}
		
		//informa o passo atual
		if( projetoForm.isFinanciado() ) {
			setStep(request, 6);
		} else {
			setStep(request, 5);
		}

		addMessage(request, "Confira todos os dados informados e lembre-se de clicar no botão 'Gravar e Enviar' " +
				"ao final desta tela para confirmar o envio do projeto.", TipoMensagemUFRN.WARNING);
		return mapping.findForward(RESUMO);

	}

	/**
	 * Submete e valida os dados do cronograma do projeto.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * 	<ul>
	 *		<li>sigaa.war/WEB-INF/jsp/pesquisa/ProjetoPesquisa/cronograma.jsp</li>
	 *	</ul>
	 */
	public ActionForward submeterCronograma(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res)
			throws Exception {
		if(req.getSession().getAttribute("steps") == null){
			addMensagemErro("A operação não está mais ativa. Por favor reinicie a operação.", req);
			return mapping.findForward(getSubSistemaCorrente(req).getForward());
		}
		ProjetoPesquisaForm projetoForm = (ProjetoPesquisaForm) form;

		// Obter objetos cronogramas a partir dos dados do formulário
		projetoForm.getTelaCronograma().definirCronograma(req);

		// Obter atividades do cronograma a partir do formulário
		List<CronogramaProjeto> cronogramas = projetoForm.getTelaCronograma().getCronogramas();
		for (CronogramaProjeto cronograma : cronogramas) {
			cronograma.setProjeto(projetoForm.getObj().getProjeto());
		}
		projetoForm.getObj().setCronogramas(cronogramas);

		// Validar dados do cronograma
		ListaMensagens lista = new ListaMensagens();
		ProjetoPesquisaValidator.validaCronograma(projetoForm.getObj(), lista);
//		lista.addAll(CronogramaProjetoHelper.validate(projetoForm.getTelaCronograma()));
		if (lista.isErrorPresent()) {
			addMensagens(lista.getMensagens(), req);
			return mapping.findForward(CRONOGRAMA);
		}

		// verifica se o coordenador é um colaborador voluntário para exibir a cláusula condicionante
		ColaboradorVoluntarioDao dao = getDAO(ColaboradorVoluntarioDao.class, req);
		boolean colaboradorVoluntario = projetoForm.getObj().getCoordenadorExterno() != null
			|| dao.isColaboradorVoluntario(projetoForm.getObj().getCoordenador());

		projetoForm.setCoordenadorColaborador(colaboradorVoluntario);

		addMessage(req, "Confira todos os dados informados e lembre-se de clicar no botão 'Gravar e Enviar' " +
				"ao final desta tela para confirmar o envio do projeto.", TipoMensagemUFRN.WARNING);
		//informa o passo atual
        if( projetoForm.isFinanciado() ) {
            setStep(req, 6);
        } else {
            setStep(req, 5);
        }
        
        if(!projetoForm.isRenovacao())
        	projetoForm.getObj().getProjeto().setCodMovimento( SigaaListaComando.ENVIAR_PROJETO_PESQUISA );
        return mapping.findForward(RESUMO);
	}

	/**
	 * Visualiza o projeto de pesquisa a partir do projeto-base.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * 	<ul>
	 *		<li>sigaa.war/projetos/ProjetoBase/resumo_integrado.jsp</li>
	 *	</ul>
	 *
	 */
	public ActionForward viewProjetoBase(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res) throws Exception {
		int idProjetoBase = getParameterInt(req, "idProjetoBase", 0);
		if(idProjetoBase == 0)
			return mapping.findForward(getSubSistemaCorrente(req).getForward());
		Collection<ProjetoPesquisa> projetos = getGenericDAO(req).findByExactField(ProjetoPesquisa.class, "projeto.id", idProjetoBase);
		if(projetos != null && !projetos.isEmpty()){
			ProjetoPesquisa projetoPesquisa = projetos.iterator().next();
			req.setAttribute("id", projetoPesquisa.getId());
			return view(mapping, form, req, res);
		} else {
			addMensagemErro("Não há um projeto de pesquisa associado a esse projeto base.", req);
			return mapping.findForward(getSubSistemaCorrente(req).getForward());
		}
	}
	
	/**
	 * Método responsável pela visualização de um projeto de pesquisa. <br /><br />
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * 	<ul>
	 *		<li>sigaa.war/WEB-INF/jsp/pesquisa/ProjetoPesquisa/lista_projetos_membro.jsp</li>
	 *	</ul>
	 *
	 */
	@Override
	public ActionForward view(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res) throws Exception {
		carregarInformacoes(mapping, form, req, res);
		return mapping.findForward(VIEW);
	}
	
	/**
	 * Redireciona para o MBean criar uma comunidade com base no projeto existente.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * 	<ul>
	 *		<li>sigaa.war/WEB-INF/jsp/pesquisa/ProjetoPesquisa/lista_projetos_membro.jsp</li>
	 *	</ul>
	 * 
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	public ActionForward criarComunidadeVirtualPesquisa(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		CriarComunidadeVirtualProjetoMBean criarComunidadeVirtualProjetoMBean = getMBean("criarComunidadeVirtualProjetoMBean", request, response);
		Integer idProjetoPesquisa = new Integer(request.getParameter("id"));
		criarComunidadeVirtualProjetoMBean.criarComunidadeVirtualPesquisa(idProjetoPesquisa);
		return listByMembro(mapping, form, request, response);
	}

	/**
	 * Prepara um projeto para finalização, encaminhando para sua tela de resumo.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * 	<ul>
	 *		<li>sigaa.war/WEB-INF/jsp/pesquisa/ProjetoPesquisa/lista_projetos_membro.jsp</li>
	 *	</ul>
	 * 
	 */
	public ActionForward preFinalizar(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res) throws Exception {

		ProjetoPesquisaForm projetoForm = (ProjetoPesquisaForm) form;
		Integer id = new Integer(req.getParameter("id"));
		projetoForm = carregaObject(id, req);
		
		if(projetoForm.getObj().getCoordenador() != null && projetoForm.getObj().getCoordenador().getPessoa() != null
				&& projetoForm.getObj().getCoordenador().getPessoa().getId() == getUsuarioLogado(req).getPessoa().getId()){

			addMessage(req, "Confirma finalização do projeto?", TipoMensagemUFRN.WARNING);
			prepareMovimento(SigaaListaComando.FINALIZAR_PROJETO_PESQUISA, req);
			projetoForm.getObj().getProjeto().setCodMovimento( SigaaListaComando.FINALIZAR_PROJETO_PESQUISA );
			return mapping.findForward(RESUMO_FINALIZACAO);
		}else{
			addMensagemErro("Apenas o coordenador do projeto pode finalizá-lo.", req);
			return listByMembro(mapping, projetoForm, req, res);
		}
	}
	
	/**
	 * Finaliza um projeto de pesquisa.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * 	<ul>
	 *		<li>sigaa.war/WEB-INF/jsp/pesquisa/ProjetoPesquisa/resumo.jsp</li>
	 *	</ul>
	 */
	public ActionForward finalizar(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		ProjetoPesquisaForm projetoForm = (ProjetoPesquisaForm) form;
		ProjetoPesquisa projeto = projetoForm.getObj();

		projeto.setSituacaoProjeto(new TipoSituacaoProjeto(TipoSituacaoProjeto.FINALIZADO));

		// Executar finalização
		try  {
			MovimentoProjetoPesquisa movProjeto = new MovimentoProjetoPesquisa();
			movProjeto.setCodMovimento(SigaaListaComando.FINALIZAR_PROJETO_PESQUISA);
			movProjeto.setProjeto(projeto);
			projeto = (ProjetoPesquisa) execute(movProjeto, request);
		} catch (NegocioException e) {
			projetoForm.setObj( projeto );
			request.setAttribute(mapping.getName(), projetoForm);
			addMensagens(e.getListaMensagens().getMensagens(), request);
			return mapping.findForward(RESUMO_FINALIZACAO);
		}

		addInformation("Projeto " + projeto.getCodigo() + " finalizado com sucesso!", request);
		return listByMembro(mapping, form, request, response);

	}
	
	/**
	 * Tem como finalidade carregar as informações do projeto de pesquisa, para a sua visualização ou
	 * para a sua impressão.
	 */
	private void carregarInformacoes(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res) throws Exception {
		int id = RequestUtils.getIntParameter(req, "id");
		if(id == 0)
			id = (Integer) req.getAttribute("id");
		
		ProjetoPesquisaDao dao = getDAO(ProjetoPesquisaDao.class, req);
		AvaliacaoProjetoDao avaDao = getDAO(AvaliacaoProjetoDao.class, req);
		
		ProjetoPesquisa obj = dao.findByPrimaryKey(id, ProjetoPesquisa.class);
		if(isEmpty(obj))
			obj = dao.findByIdProjetoBase(id);
		
		req.setAttribute(ConstantesCadastro.ATRIBUTO_VISUALIZACAO, obj);

		ProjetoPesquisaForm projetoForm = (ProjetoPesquisaForm) form;
		projetoForm.setObj(obj);

		// Inicializar tela do cronograma 
		TelaCronograma cronograma = new TelaCronograma(
				projetoForm.getObj().getDataInicio(),
				projetoForm.getObj().getDataFim(),
				projetoForm.getObj().getCronogramas()
			);
		projetoForm.setTelaCronograma(cronograma);
        
		Set<AvaliacaoProjeto> set = new HashSet<AvaliacaoProjeto>(avaDao.findByProjeto(obj));

        projetoForm.getObj().setAvaliacoesProjeto(set);
		
		req.setAttribute(mapping.getName(), projetoForm);
	}
 
	/**
	 * Esse método tem como finalidade de gerar um relatório com todos as informações do projeto de pesquisa
	 * selecionado pelo usuário.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * 	<ul>
	 *		<li>sigaa.war/WEB-INF/jsp/pesquisa/ProjetoPesquisa/lista_projetos_membro.jsp</li>
	 *	</ul>
	 * 
	 */
	public ActionForward imprimirProjetoPesquisa(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res) throws Exception {
		carregarInformacoes(mapping, form, req, res);
		return mapping.findForward(RESUMO_IMPRESSAO);
	}

	/**
	 * Tem com finalidade Listar todos os membros de um projeto.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * 	<ul>
	 *		<li>sigaa.war/WEB-INF/jsp/pesquisa/ProjetoPesquisa/lista_membros.jsp</li>
	 *	</ul>
	 */
	public ActionForward listarMembroByProjeto(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		ProjetoPesquisaForm projetoForm = (ProjetoPesquisaForm) form;
		
		projetoForm.setObj(getGenericDAO(request).findByPrimaryKey(projetoForm.getObj().getId(), ProjetoPesquisa.class));
		
		if (projetoForm.getObj().getSituacaoProjeto().getId() != TipoSituacaoProjeto.EM_ANDAMENTO 
				&& projetoForm.getObj().getSituacaoProjeto().getId() != TipoSituacaoProjeto.FINALIZADO 
				&& projetoForm.getObj().getSituacaoProjeto().getId() != TipoSituacaoProjeto.RENOVADO){ 
			
			addMensagemErro("Não é possível emitir a declaração para esse projeto.", request);
			request.setAttribute("popular", true);
			request.getAttribute("popular");
			return mapping.findForward(POPULAR);
		}else{
			MembroProjetoDao dao = getDAO(MembroProjetoDao.class, request);
			Collection<MembroProjeto> membros = dao.findMembroProjetoAtivoByProjetoPesquisa(projetoForm.getObj().getProjeto().getId(), false);
			request.setAttribute("membros", membros);
			return mapping.findForward(LISTA_MEMBROS);
		}
		
	}

	/**
	 * Invoca o Managed-Bean responsável pelo gerenciamento dos membros do projeto. <br /><br />
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * 	<ul>
	 *		<li>sigaa.war/WEB-INF/jsp/pesquisa/ProjetoPesquisa/lista.jsp</li>
	 *	</ul>
	 *
	 */
	public ActionForward gerenciarMembros(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res) throws Exception {
		int id = RequestUtils.getIntParameter(req, "id");
				
		ProjetoPesquisa obj = getGenericDAO(req).findByPrimaryKey(id, ProjetoPesquisa.class);
		
		MembroProjetoMBean bean = getMBean("membroProjeto", req, res);
		bean.setProjeto(obj.getProjeto());
		bean.gerenciarMembrosProjetoPesquisa();
		
		return null;
	}
	
	/**
	 * Método utilizado para carregar as possíveis data fim da execução dos projetos, ficando a cargo do usuário escolher.
	 * 
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * 	<ul>
	 *		<li>/sigaa.war/WEB-INF/jsp/pesquisa/ProjetoPesquisa/dados_gerais.jsp</li>
	 *	</ul>
	 * 
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @return
	 * @throws DAOException
	 * @throws ArqException
	 */
	public ActionForward carregarDatas(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res) throws DAOException, ArqException {
		GenericDAO dao = getGenericDAO(req);
		String caminho = null;
		try {
			ProjetoPesquisaForm projetoForm = (ProjetoPesquisaForm) form;
			EditalPesquisa editalPesquisa = dao.findByPrimaryKey(projetoForm.getObj().getEdital().getId(), EditalPesquisa.class);
			projetoForm.setExibirAnos(false);
			if (!ValidatorUtil.isEmpty(editalPesquisa)) {
				projetoForm.getObj().getEdital().setInicioExecucaoProjetos(editalPesquisa.getInicioExecucaoProjetos());
				projetoForm.getObj().getEdital().setFimExecucaoProjetos(editalPesquisa.getFimExecucaoProjetos());
				projetoForm.getObj().getEdital().setCota(dao.refresh(editalPesquisa.getCota()));
				
				if (!ValidatorUtil.isEmpty(editalPesquisa.getFimExecucaoProjetos())) {
					Collection<AnoProjetoPesquisa> anos = new ArrayList<AnoProjetoPesquisa>(); 
					for (int i = -2; i < 1; i++) {
						Date fimExecucao = CalendarUtils.adicionarAnos(editalPesquisa.getFimExecucaoProjetos(), i);
						if (fimExecucao.after(editalPesquisa.getInicioExecucaoProjetos())) {
							AnoProjetoPesquisa anoProjeto = new AnoProjetoPesquisa();
							anoProjeto.setFimExecucao(fimExecucao);
							anoProjeto.setQntAnos(i);
							anos.add(anoProjeto);
						}
					}
		
					projetoForm.setExibirAnos(true);
					projetoForm.getReferenceData().put("anos", anos);
				}
			}

			projetoForm.getObj().setEdital( dao.findByPrimaryKey(projetoForm.getObj().getEdital().getId(), EditalPesquisa.class));
			if (projetoForm.isAlteracaoEdital())
				caminho = ALTERAR_EDITAL;
			else 
				caminho = DADOS_GERAIS;
			
			popularDadosAuxiliares(projetoForm, req);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}
		
		return mapping.findForward(caminho);
	}
	
	/**
	 * Mudar status do Projeto de Pesquisa de aprovado para em andamento.
	 * 
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li> /SIGAA/app/sigaa.ear/sigaa.war/WEB-INF/jsp/pesquisa/ProjetoPesquisa/lista_projetos_membro.jsp </li>
	 *	</ul>
	 */
	public ActionForward executarProjeto(ActionMapping mapping, ActionForm form, 
			HttpServletRequest request, HttpServletResponse response) throws Exception {

		int id = RequestUtils.getIntParameter(request, "id");
		if(id == 0)
			id = (Integer) request.getAttribute("id");
		
		ProjetoPesquisa obj = getGenericDAO(request).findByPrimaryKey(id, ProjetoPesquisa.class);
		obj.getProjeto().setUsuarioLogado( getUsuarioLogado(request) );
		
		ProjetoPesquisaHelper.gravarAlterarSituacaoProjeto(getGenericDAO(request), TipoSituacaoProjeto.EM_ANDAMENTO, obj);

		addMessage(request, "O Projeto " + obj.getCodigo() + " foi inicializado.", TipoMensagemUFRN.INFORMATION);
		
		return listByMembro(mapping, form, request, response);
	}

	/**
	 * Serve para mudar o edital vínculado, desde que o projeto não tenha entrado "em andamento".
	 * 
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li> /SIGAA/app/sigaa.ear/sigaa.war/WEB-INF/jsp/pesquisa/ProjetoPesquisa/lista_projetos_membro.jsp </li>
	 *	</ul>
	 */
	public ActionForward alterarEdital(ActionMapping mapping, ActionForm form, 
			HttpServletRequest request, HttpServletResponse response) throws Exception {

		String paramId = request.getParameter("id");
		Integer id;
		if(paramId == null)
			id = (Integer) request.getAttribute("id");
		else
			id = Integer.parseInt(paramId);
		ProjetoPesquisaForm pForm = carregaObject(id, request);

		request.setAttribute("alteracao", true);
		popular(mapping, pForm, request, response);
		ProjetoPesquisa projeto = pForm.getObj();

		if (projeto.getSituacaoProjeto().getId() == TipoSituacaoProjeto.GRAVADO) {
			prepareMovimento(SigaaListaComando.ENVIAR_PROJETO_PESQUISA, request);
			pForm.getObj().getProjeto().setCodMovimento( SigaaListaComando.ENVIAR_PROJETO_PESQUISA );
		} else {
			prepareMovimento(SigaaListaComando.ALTERAR_PROJETO_PESQUISA, request);
			pForm.getObj().getProjeto().setCodMovimento( SigaaListaComando.ALTERAR_PROJETO_PESQUISA );
		}

		// Inicializando form
		pForm.setDataInicio( Formatador.getInstance().formatarData( pForm.getObj().getDataInicio() ) );
		pForm.setDataFim( Formatador.getInstance().formatarData( pForm.getObj().getDataFim() ) );

		// Configurando passos
		pForm.setFinanciado( !projeto.isInterno() );
		pForm.setAlteracaoEdital(true);
		configureSteps( pForm, request );

		return mapping.findForward(ALTERAR_EDITAL);
	}
	
}