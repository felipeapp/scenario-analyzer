/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 23/11/2007
 *
 */
package br.ufrn.sigaa.extensao.jsf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dao.UnidadeDAOImpl;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.parametrizacao.RepositorioDadosInstitucionais;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.comum.dominio.UnidadeGeral;
import br.ufrn.rh.dominio.NivelResponsabilidade;
import br.ufrn.sigaa.arq.dao.extensao.RelatorioAcaoExtensaoDao;
import br.ufrn.sigaa.arq.dao.projetos.AutorizacaoDepartamentoDao;
import br.ufrn.sigaa.arq.dao.projetos.MembroProjetoDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.extensao.dominio.AtividadeExtensao;
import br.ufrn.sigaa.extensao.dominio.TipoParecerAvaliacaoExtensao;
import br.ufrn.sigaa.extensao.relatorio.dominio.RelatorioAcaoExtensao;
import br.ufrn.sigaa.parametros.dominio.ParametrosExtensao;
import br.ufrn.sigaa.projetos.dominio.AutorizacaoDepartamento;
import br.ufrn.sigaa.projetos.dominio.CategoriaMembro;
import br.ufrn.sigaa.projetos.dominio.TipoAutorizacaoDepartamento;

/*******************************************************************************
 * MBean usado na autoriza��o das a��es e Relat�rios de Extens�o pelos chefes
 * dos departamentos envolvidos na a��o atrav�s dos membros da equipe. <br/>
 * 
 * Nas autoriza��es das propostas de extens�o: O chefe do departamento lista
 * todas as a��es que necessitam de autoriza��o e ap�s selecionar uma � exibido
 * um formul�rio com os dados da a��o e a lista de membros do seu departamento
 * que participam do projeto. O chefe autoriza ou reprova a proposta. Em caso de
 * autoriza��o a proposta segue para analise pelos membro do comit� de extens�o,
 * em caso de reprova��o a proposta � finalizada. <br/>
 * 
 * Nas autoriza��es de relat�rios de projeto, cursos e eventos de extens�o: O
 * chefe lista todas as a��es pendentes de autoriza��o e ap�s a sele��o de uma
 * delas ser� exibido um formul�rio com dados da a��o e campos para o seu
 * parecer. Caso o relat�rio seja reprovado ele n�o segue para a PROEX e o
 * coordenador da a��o dever� alter�-lo. O relat�rio s� segue para a PROEX
 * depois que o chefe do departamento aprova.
 * 
 * @author Ilueny Santos
 * 
 ******************************************************************************/
@Scope("session")
@Component("autorizacaoDepartamento")
public class AutorizacaoDepartamentoMBean extends SigaaAbstractController<AutorizacaoDepartamento> {

	/** Atributo utilizado para armazenar uma Collection de autoriza��es */
	private Collection<AutorizacaoDepartamento> autorizacoes;
	/** Atributo utilizado para representar o relat�rio da A��o de Extens�o */
	private RelatorioAcaoExtensao relatorio;
	/** Atributo utilizado para representar a unidade */
	private Unidade unidade = new Unidade(0);
	/** Atributo utilizado para representar uma Collection dos relat�rios da Unidade */
	private Collection<RelatorioAcaoExtensao> relatoriosUnidade;

	/** 
	 * Construtor padr�o
	 */
	public AutorizacaoDepartamentoMBean() {
		obj = new AutorizacaoDepartamento();

	}

	/**
	 * Permite listar os tipos de autoriza��es poss�veis
	 * 
	 * Chamado por: sigaa.war/extensao/menu.jsp
	 * Chamado por: sigaa.war/extensao/AutorizacaoDepartamento/form.jsp
	 * 
	 * @return
	 */
	public List<SelectItem> getTiposAutorizacoesCombo() {

		try {
			GenericDAO dao = getGenericDAO();
			return toSelectItems(dao.findAll(TipoAutorizacaoDepartamento.class), "id",
					"descricao");
		} catch (DAOException e) {
			notifyError(e);
			return null;
		}
	}

	
	/**
	 * Lista de relat�rios de extens�o das unidades onde o usu�rio logado tem algum tipo de responsabilidade. 
	 * Utilizado na autoriza��o dos chefes de departamentos.
	 * <br />
	 * M�todo chamado pelas(s) JSP(s):
	 * <ul>
	 * 		<li>N�o � chamado por JSP(s)</li>
	 * </ul>
	 * 
	 * @return
	 * @throws SegurancaException
	 */
	public Collection<RelatorioAcaoExtensao> getAllRelatoriosUnidade() {
		
		UnidadeDAOImpl undDao = new UnidadeDAOImpl(Sistema.SIPAC);
		try {
			RelatorioAcaoExtensaoDao dao = getDAO(RelatorioAcaoExtensaoDao.class);
			Collection<UnidadeGeral> unidades = new ArrayList<UnidadeGeral>();			
			unidades.addAll( undDao.findUnidadesByResponsavel(getUsuarioLogado().getServidor().getId(), new Character[] {NivelResponsabilidade.CHEFE, NivelResponsabilidade.VICE, NivelResponsabilidade.SUPERVISOR_DIRETOR_ACADEMICO}));			
			if ((unidades == null) || (unidades.isEmpty())) {
				addMensagemErro("O usu�rio atual n�o possui unidades configuradas sob sua responsabilidade no " + RepositorioDadosInstitucionais.get("nomeSigaa") + ".");
			}				
			return dao.findByUnidades(unidades);
		} catch (DAOException e) {
			notifyError(e);
			return new ArrayList<RelatorioAcaoExtensao>();
		}finally {
			undDao.close();
		}
		
	}


	/**
	 * Lista de autoriza��es da unidade do usu�rio logado. 
	 * 
	 * Chamado por: /sigaa.war/extensao/AutorizacaoDepartamento/lista.jsp
	 * 
	 * @return
	 * @throws SegurancaException
	 */
	public Collection<AutorizacaoDepartamento> getAutorizacoesDepartamentoUsuarioLogado() throws SegurancaException {
		checkRole(SigaaPapeis.CHEFE_DEPARTAMENTO, SigaaPapeis.DIRETOR_CENTRO, SigaaPapeis.CHEFE_UNIDADE);
		
		UnidadeDAOImpl undDao = new UnidadeDAOImpl(Sistema.SIPAC);
		try {

			//todas as unidades onde o usu�rio atual � chefe
			AutorizacaoDepartamentoDao autorizacoeDao = getDAO(AutorizacaoDepartamentoDao.class);
			Collection<UnidadeGeral> unidades = new ArrayList<UnidadeGeral>();			
			unidades.addAll( undDao.findUnidadesByResponsavel(getUsuarioLogado().getServidor().getId(), new Character[] {NivelResponsabilidade.CHEFE, NivelResponsabilidade.VICE, NivelResponsabilidade.SUPERVISOR_DIRETOR_ACADEMICO}));			
			
			if ((unidades == null) || (unidades.isEmpty())) {
				addMensagemErro("O usu�rio atual n�o possui unidades configuradas sob sua responsabilidade no " + RepositorioDadosInstitucionais.get("nomeSigaa") + ".");
			}

			autorizacoes = autorizacoeDao.findByUnidades(unidades);
		} catch (DAOException e) {
			notifyError(e);
			autorizacoes = new ArrayList<AutorizacaoDepartamento>();
		}finally {
			undDao.close();
		}

		return autorizacoes;
	}

	

	/**
	 * Chefe de departamento ou diretor de centro escolhe um relat�rio de
	 * projeto que ainda n�o foi analisado.
	 * 
	 * Este m�todo � chamado na jsp	sigaa.war/extensao/AutorizacaoDepartamento/lista_relatorio.jsp
	 * Este m�todo � chamado na jsp	sigaa.war/extensao/ValidacaoRelatorioProex/lista.jsp
	 * 
	 * @return
	 * @throws SegurancaException
	 * 
	 */
	public String analisarRelatorio() throws SegurancaException {
		checkRole(SigaaPapeis.CHEFE_DEPARTAMENTO, SigaaPapeis.DIRETOR_CENTRO,
				SigaaPapeis.GESTOR_EXTENSAO, SigaaPapeis.CHEFE_UNIDADE);

		try {

			relatorio = getGenericDAO().findByPrimaryKey(getParameterInt("idRelatorio"), RelatorioAcaoExtensao.class);
			relatorio.setTipoParecerDepartamento(getGenericDAO().findByPrimaryKey(TipoParecerAvaliacaoExtensao.APROVADO, TipoParecerAvaliacaoExtensao.class)); //padr�o
			prepareMovimento(SigaaListaComando.VALIDAR_RELATORIO_ACAO_EXTENSAO_DEPARTAMENTO);
			setConfirmButton("Confirmar Valida��o");
			obj.setAtividade(relatorio.getAtividade());
		} catch (Exception e) {
			notifyError(e);
			addMensagemErro(e.getMessage());
		}

		//chama o visualizar espec�fico dependendo do tipo de relat�rio
		if (relatorio.getAtividade().isTipoProjeto()) {
			return forward(ConstantesNavegacao.AUTORIZACAORELATORIO_FORM_PROJETO);

		} else if (relatorio.getAtividade().isTipoCurso() || relatorio.getAtividade().isTipoEvento()) {
			return forward(ConstantesNavegacao.AUTORIZACAORELATORIO_FORM_CURSO_EVENTO);

		} else if (relatorio.getAtividade().isTipoProduto()) {
			return forward(ConstantesNavegacao.AUTORIZACAO_RELATORIO_FORM_PRODUTO);

		} else if (relatorio.getAtividade().isTipoPrograma()) {
			return forward(ConstantesNavegacao.AUTORIZACAO_RELATORIO_FORM_PROGRAMA);

		} else {
			addMensagemErro("Relat�rio ainda n�o definido para o tipo de a��o selecionada.");
			return null;
		}

	}
	
	/**
	 * Permite ao Chefe de departamento ou diretor re-imprimir
	 * recibo do projeto da a��o de extens�o.	  
	 * 
	 * Chamado por: path	sigaa.ear/sigaa.war/extensao/AutorizacaoDepartamento/lista.jsp
	 * 
	 * @return
	 * @throws SegurancaException
	 * 
	 */	
	public String reciboAcaoExtensao() throws SegurancaException {
		checkRole(SigaaPapeis.CHEFE_DEPARTAMENTO, SigaaPapeis.DIRETOR_CENTRO,
				SigaaPapeis.GESTOR_EXTENSAO, SigaaPapeis.CHEFE_UNIDADE);

		int id = getParameterInt("id_autorizacao", 0);

		if (id > 0) {
			
			try {
				obj = getDAO(AutorizacaoDepartamentoDao.class)
						.findByPrimaryKey(id, AutorizacaoDepartamento.class);				
			}catch (Exception e) {
				notifyError(e);
				addMensagemErro(e.getMessage());
			}

		} else {
			addMensagemErro("A��o de extens�o n�o encontrada");
			return null;
		}
		
		return forward(ConstantesNavegacao.AUTORIZACAOATIVIDADE_RECIBO);		
	}

	/**
	 * Chefe de departamento ou diretor de centro autoriza uma a��o de extens�o
	 * 
	 * Escolhe uma autoriza��o que ainda n�o foi analisada por um chefe de
	 * departamento ou diretor de centro.
	 * 
	 * Chamado por: sigaa.ear/sigaa.war/extensao/menu.jsp
	 * 
	 * @return
	 * @throws ArqException 
	 * 
	 */
	public String escolherAutorizacao() throws ArqException {
		checkRole(SigaaPapeis.CHEFE_DEPARTAMENTO, SigaaPapeis.DIRETOR_CENTRO,
				SigaaPapeis.GESTOR_EXTENSAO, SigaaPapeis.CHEFE_UNIDADE);

		int id = getParameterInt("id_autorizacao", 0);

		if (id > 0) {
			
				obj = getDAO(AutorizacaoDepartamentoDao.class).findByPrimaryKey(id, AutorizacaoDepartamento.class);
				MembroProjetoDao dao = getDAO(MembroProjetoDao.class);
				// evitar null pointer no select do tipo de autoriza��o
				// (ad-referendum, reuni�o, etc)
				if (obj.getTipoAutorizacao() == null) {
					obj.setTipoAutorizacao(new TipoAutorizacaoDepartamento());
				}

				// Inicilizar atributos evitar erro de lazy
				obj.getAtividade().setMembrosEquipe(dao.findAtivosByProjeto(obj.getAtividade().getProjeto().getId(), CategoriaMembro.DISCENTE,
						CategoriaMembro.DOCENTE,CategoriaMembro.EXTERNO,CategoriaMembro.SERVIDOR));
				// Sugere autorizado. Setado por padr�o a pedido a pro reitoria de extens�o
				if (obj.getDataAutorizacao() == null) {
					obj.setAutorizado(true);
				}

				// verifica data limite para envio de autoriza��es...
				// s� verifica a��es que concorrem ao edital faex
				if ( obj.getAtividade().isFinancProex() && !obj.getAtividade().isRegistro() ) {
					//gestores de extens�o podem autorizar propostas a qualquer momento
					if (obj.getAtividade().getEditalExtensao().isPermitidoAutorizacaoChefe() 
						|| getUsuarioLogado().isUserInRole(SigaaPapeis.GESTOR_EXTENSAO)) {

						prepareMovimento(SigaaListaComando.AUTORIZAR_ATIVIDADE_EXTENSAO);
						setOperacaoAtiva(SigaaListaComando.AUTORIZAR_ATIVIDADE_EXTENSAO.getId());
						setConfirmButton("Confirmar");
						return forward(ConstantesNavegacao.AUTORIZACAOATIVIDADE_FORM);

					} else {
						addMensagemErro("O prazo para autoriza��o de propostas de a��es de extens�o expirou.");
					}

					// sem financiamento interno ou s� registro, autoriza a qualquer
					// tempo
				} else {
					prepareMovimento(SigaaListaComando.AUTORIZAR_ATIVIDADE_EXTENSAO);
					setOperacaoAtiva(SigaaListaComando.AUTORIZAR_ATIVIDADE_EXTENSAO.getId());
					setConfirmButton("Confirmar");
					return forward(ConstantesNavegacao.AUTORIZACAOATIVIDADE_FORM);
				}

		} else {
			addMensagemErro("Selecione uma a��o de extens�o para autorizar.");
		}

		return null;

	}
	
	
	
	
	/**
	 * Autoriza uma a��o de extens�o analisada pelo departamento ou diretor de centro.
	 *
	 * Chamado por: sigaa.ear/sigaa.war/extensao/AutorizacaoDepartamento/form.jsp
	 * 
	 * @return
	 * @throws SegurancaException
	 * 
	 */
	public String autorizar() throws SegurancaException {
		checkRole(SigaaPapeis.CHEFE_DEPARTAMENTO, SigaaPapeis.DIRETOR_CENTRO,
				SigaaPapeis.GESTOR_EXTENSAO, SigaaPapeis.CHEFE_UNIDADE);
		
		
		checkOperacaoAtiva(SigaaListaComando.AUTORIZAR_ATIVIDADE_EXTENSAO.getId());
		try {
			
			if (obj.getAutorizado() == null) {
				addMensagemErro("Parecer: Campo obrigat�rio n�o informado");
				return null;
			}
	
			obj.setDataAutorizacao(new Date());
			obj.setRegistroEntrada(getUsuarioLogado().getRegistroEntrada());

			ListaMensagens lista = obj.validate();
			if (lista != null && lista.getMensagens().size() > 0) {
				addMensagens(lista);
				return forward(ConstantesNavegacao.AUTORIZACAOATIVIDADE_FORM);
			}
			// operador escolheu n�o autorizar
			// na view, o select sendo 0 = n�o autorizar
			if (obj.getTipoAutorizacao().getId() == TipoAutorizacaoDepartamento.NAO_AUTORIZAR) {
				obj.setTipoAutorizacao(null);
			}
			
			MovimentoCadastro mov = new MovimentoCadastro();
			mov.setObjMovimentado(obj);
			mov.setAcao(MovimentoCadastro.ACAO_ALTERAR);
			mov.setCodMovimento(SigaaListaComando.AUTORIZAR_ATIVIDADE_EXTENSAO);

			obj = (AutorizacaoDepartamento) execute(mov, getCurrentRequest());
			removeOperacaoAtiva();

			// carregando autoriza��es e atividades
			obj.setAtividade(getGenericDAO().findByPrimaryKey(obj.getAtividade().getId(), AtividadeExtensao.class));

			// Inicializar atributos
			obj.getAtividade().getMembrosEquipe().iterator();

		} catch (Exception e) {
			addMensagemErro(e.getMessage());
			notifyError(e);
			return null;
		}

		return forward(ConstantesNavegacao.AUTORIZACAOATIVIDADE_RECIBO);
	}
	
	/**
	 * M�todo utilizado para cancelar uma o precesso de autoriza��o
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>/sigaa.war/extensao/AutorizacaoDepartamento/form.jsp</li>
	 * 		<li>/sigaa.war/extensao/AutorizacaoDepartamento/form_busca_autorizar_acoes.jsp</li>
	 * 		<li>/sigaa.war/extensao/AutorizacaoDepartamento/form_busca_autorizar_relatorios.jsp</li>
	 * 		<li>/sigaa.war/extensao/AutorizacaoDepartamento/form_relatorio_curso_evento.jsp</li>
	 * 		<li>/sigaa.war/extensao/AutorizacaoDepartamento/form_relatorio_projeto.jsp</li>
	 * </ul>
	 */
	@Override
	public String cancelar() {		
		try {
			redirectJSF(getSubSistema().getLink());
		} catch (Exception e) {
			e.printStackTrace();
		}		
		return null;
	}

	/**
	 * M�todo utilizado para remover uma avalia��o
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>N�o � chamado por JSP(s)</li>
	 * </ul>
	 * 
	 * @return
	 */
	public String removerAvaliacao() {
		return null;
	}

	/**
	 * Inicia a chamada para autoriza��o das a��es de extens�o pelo chefe de
	 * departamento
	 * 
	 * Este m�todo � chamado na jsp	sigaa.ear/sigaa.war/extensao/AutorizacaoDepartamento/recibo_autorizacao.jsp
	 * Este m�todo � chamado na jsp	sigaa.ear/sigaa.war/extensao/EditalExtensao/form.jsp	  
	 * @return
	 * @throws ArqException
	 */
	public String autorizacaoChefe() throws ArqException {
		checkRole(SigaaPapeis.CHEFE_DEPARTAMENTO, SigaaPapeis.DIRETOR_CENTRO, SigaaPapeis.CHEFE_UNIDADE);
		prepareMovimento(SigaaListaComando.REEDITAR_ATIVIDADE_EXTENSAO);
		if (!ValidatorUtil.isEmpty(unidade)){
			listarAutorizacoesAcoes();
		}
		return forward(ConstantesNavegacao.AUTORIZACAOATIVIDADE_LISTA);
	}

	/**
	 * Inicia a chamada para autoriza��o dos relat�rios pelo chefe de
	 * departamento
	 * 
	 * Este m�todo � chamado na jsp	sigaa.ear/sigaa.war/extensao/menu.jsp 
	 * 
	 * @return
	 * @throws SegurancaException
	 */
	public String autorizacaoRelatorioChefe() throws SegurancaException {
		checkRole(SigaaPapeis.CHEFE_DEPARTAMENTO, SigaaPapeis.DIRETOR_CENTRO, SigaaPapeis.CHEFE_UNIDADE);
		relatoriosUnidade = getAllRelatoriosUnidade();
		return forward(ConstantesNavegacao.AUTORIZACAORELATORIO_LISTA);
	}

	
	/**
	 * Inicia a chamada para autoriza��o dos relat�rios pela proex como
	 *  chefe de departamento
	 * 
	 * Este m�todo � chamado na jsp	sigaa.ear/sigaa.war/extensao/menu.jsp 
	 * 
	 * @return
	 * @throws SegurancaException
	 */
	public String listarAutorizacoesRelatorioDepartamentoProex() throws SegurancaException {
		checkRole(SigaaPapeis.GESTOR_EXTENSAO);		
		return forward(ConstantesNavegacao.AUTORIZACAORELATORIO_BUSCA);
	}

	
	/**
	 * Inicia a chamada para autoriza��o das a��es de extens�o pela proex o
	 * objetivo deste tipo de autoriza��o � auxiliar a aprova��o de a��es que
	 * ficam 'presas' nos departamentos aguardando indefinidamente a autoriza��o
	 * de seus chefes
	 * 
	 * Chamado por: sigaa.ear/sigaa.war/extensao/menu.jsp
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String listarAutorizacoesAcoesDepartamentosProex() throws ArqException {
		checkRole(SigaaPapeis.GESTOR_EXTENSAO);
		prepareMovimento(SigaaListaComando.REEDITAR_ATIVIDADE_EXTENSAO);
		if (!ValidatorUtil.isEmpty(unidade)){
			listarAutorizacoesAcoes();
		}
		return forward(ConstantesNavegacao.AUTORIZACAOATIVIDADE_BUSCA);
	}

	/**
	 * Chefe de departamento ou diretor de centro autoriza um relat�rio de
	 * projeto extens�o
	 * 
	 * Este m�todo � chamado na jsp sigaa.ear/sigaa.war/extensao/AutorizacaoDepartamento/form_relatorio_curso_evento.jsp
	 * Este m�todo � chamado na jsp	sigaa.war/extensao/AutorizacaoDepartamento/form_relatorio_projeto.jsp
	 *  
	 * @return
	 * @throws ArqException
	 */
	public String autorizarRelatorio() throws ArqException {
		checkRole(SigaaPapeis.CHEFE_DEPARTAMENTO, SigaaPapeis.DIRETOR_CENTRO, 
				SigaaPapeis.CHEFE_UNIDADE, SigaaPapeis.GESTOR_EXTENSAO);
		try {

			
			if (ValidatorUtil.isEmpty(relatorio.getTipoParecerDepartamento())) {
				addMensagemErro("Parecer: Campo obrigat�rio n�o informado");
				return null;
			}
			
			// Verifica se � necess�rio ter a justificativa
			relatorio.setDataValidacaoDepartamento(new Date());
			if(!relatorio.isAprovadoDepartamento() && ValidatorUtil.isEmpty(relatorio.getParecerDepartamento())){
				addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Justificativa");
				return null;
			}
			
			//Tipo de autoriza��o do departamento.
			relatorio.setTipoParecerDepartamento(getGenericDAO().findByPrimaryKey(relatorio.getTipoParecerDepartamento().getId(), TipoParecerAvaliacaoExtensao.class));
			MovimentoCadastro mov = new MovimentoCadastro();
			mov.setObjMovimentado(relatorio);
			mov.setCodMovimento(SigaaListaComando.VALIDAR_RELATORIO_ACAO_EXTENSAO_DEPARTAMENTO);
			execute(mov, getCurrentRequest());
			addMensagem(MensagensArquitetura.OPERACAO_SUCESSO);

		} catch (Exception e) {
			addMensagemErro(e.getMessage());
			notifyError(e);
			return null;
		}

		if (getAcessoMenu().isExtensao()){
			unidade = new Unidade();
			return listarAutorizacoesRelatorioDepartamentoProex();
		}else{ 
			return autorizacaoRelatorioChefe();
		}
	}

	/**
	 * Lista todas as autoriza��es para a unidade informada
	 * Utilizado quando a PROEX valida a��es como chefe de departamento
	 * 
	 * Chamado por: sigaa.ear/sigaa.war/extensao/menu.jsp  
	 * @throws SegurancaException
	 *  
	 */
	public String listarAutorizacoesAcoes() throws SegurancaException {
		checkRole(SigaaPapeis.GESTOR_EXTENSAO);

		try {
			
			if (ValidatorUtil.isEmpty(unidade)){
				addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Unidade Proponente");
				return null;
			}
			
			AutorizacaoDepartamentoDao autorizacoeDao = getDAO(AutorizacaoDepartamentoDao.class);
			Collection<UnidadeGeral> unidades = new ArrayList<UnidadeGeral>();
			unidades.add(unidade);
			autorizacoes = autorizacoeDao.findByUnidades(unidades);

		} catch (DAOException e) {
			notifyError(e);
			autorizacoes = new ArrayList<AutorizacaoDepartamento>();
		}
		return null;
	}
	
	
	/**
	 * Lista todos os relat�rios para a unidade informada
	 * Utilizado quando a PROEX valida relat�rios como chefe de departamento
	 * 
	 * Chamado por: sigaa.ear/sigaa.war/extensao/menu.jsp  
	 * @throws SegurancaException
	 *  
	 */
	public String listarRelatoriosAcoes() throws SegurancaException {
		checkRole(SigaaPapeis.GESTOR_EXTENSAO);
		try {
			 
			if (ValidatorUtil.isEmpty(unidade)){
				addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Unidade Proponente");
				return null;
			}
							
			RelatorioAcaoExtensaoDao dao = getDAO(RelatorioAcaoExtensaoDao.class);
			ArrayList<UnidadeGeral> unidades = new ArrayList<UnidadeGeral>();
			unidades.add(unidade);
			
			relatoriosUnidade = dao.findByUnidades(unidades);
		} catch (DAOException e) {
			notifyError(e);
			relatoriosUnidade = new ArrayList<RelatorioAcaoExtensao>();
		}
		return null;
	}

	public Collection<AutorizacaoDepartamento> getAutorizacoes() {
		return autorizacoes;
	}

	public void setAutorizacoes(Collection<AutorizacaoDepartamento> autorizacoes) {
		this.autorizacoes = autorizacoes;
	}

	public Unidade getUnidade() {
		return unidade;
	}

	public void setUnidade(Unidade unidade) {
		this.unidade = unidade;
	}

	public RelatorioAcaoExtensao getRelatorio() {
		return relatorio;
	}

	public void setRelatorio(RelatorioAcaoExtensao relatorio) {
		this.relatorio = relatorio;
	}

	public Collection<RelatorioAcaoExtensao> getRelatoriosUnidade() {
		return relatoriosUnidade;
	}

	public void setRelatoriosUnidade(
			Collection<RelatorioAcaoExtensao> relatoriosUnidade) {
		this.relatoriosUnidade = relatoriosUnidade;
	}
	
	
	/**
	 * M�todo usado para permitir a consulta autom�tica ao mudar unidades
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>N�o � chamado por JSP(s)</li>
	 * </ul>
	 * 
	 * @throws DAOException
	 * @throws SegurancaException 
	 */
	public void changeCentroListaTodos(ValueChangeEvent evt) throws DAOException, SegurancaException {
		checkRole(SigaaPapeis.GESTOR_EXTENSAO);

		try {
			int idUnidade = getParameterInt("idUnidade", 0);
			if (idUnidade > 0) {
				unidade = new Unidade(idUnidade);
			}
			AutorizacaoDepartamentoDao autorizacoeDao = getDAO(AutorizacaoDepartamentoDao.class);
			Collection<UnidadeGeral> unidades = new ArrayList<UnidadeGeral>();
			unidades.add(unidade);
			autorizacoes = autorizacoeDao.findByUnidades(unidades);

		} catch (DAOException e) {
			notifyError(e);
			autorizacoes = new ArrayList<AutorizacaoDepartamento>();
		}
	}

	public boolean isExibeNovoRelatorio(){
		return obj.getAtividade().getProjeto().getDataCadastro().after(ParametroHelper.getInstance()
				.getParametroDate(ParametrosExtensao.DATA_LIMITE_ALTERACAO_CH_EQUIPE_EXECUTORA));
	}
	
}