/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
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
 * MBean usado na autorização das ações e Relatórios de Extensão pelos chefes
 * dos departamentos envolvidos na ação através dos membros da equipe. <br/>
 * 
 * Nas autorizações das propostas de extensão: O chefe do departamento lista
 * todas as ações que necessitam de autorização e após selecionar uma é exibido
 * um formulário com os dados da ação e a lista de membros do seu departamento
 * que participam do projeto. O chefe autoriza ou reprova a proposta. Em caso de
 * autorização a proposta segue para analise pelos membro do comitê de extensão,
 * em caso de reprovação a proposta é finalizada. <br/>
 * 
 * Nas autorizações de relatórios de projeto, cursos e eventos de extensão: O
 * chefe lista todas as ações pendentes de autorização e após a seleção de uma
 * delas será exibido um formulário com dados da ação e campos para o seu
 * parecer. Caso o relatório seja reprovado ele não segue para a PROEX e o
 * coordenador da ação deverá alterá-lo. O relatório só segue para a PROEX
 * depois que o chefe do departamento aprova.
 * 
 * @author Ilueny Santos
 * 
 ******************************************************************************/
@Scope("session")
@Component("autorizacaoDepartamento")
public class AutorizacaoDepartamentoMBean extends SigaaAbstractController<AutorizacaoDepartamento> {

	/** Atributo utilizado para armazenar uma Collection de autorizações */
	private Collection<AutorizacaoDepartamento> autorizacoes;
	/** Atributo utilizado para representar o relatório da Ação de Extensão */
	private RelatorioAcaoExtensao relatorio;
	/** Atributo utilizado para representar a unidade */
	private Unidade unidade = new Unidade(0);
	/** Atributo utilizado para representar uma Collection dos relatórios da Unidade */
	private Collection<RelatorioAcaoExtensao> relatoriosUnidade;

	/** 
	 * Construtor padrão
	 */
	public AutorizacaoDepartamentoMBean() {
		obj = new AutorizacaoDepartamento();

	}

	/**
	 * Permite listar os tipos de autorizações possíveis
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
	 * Lista de relatórios de extensão das unidades onde o usuário logado tem algum tipo de responsabilidade. 
	 * Utilizado na autorização dos chefes de departamentos.
	 * <br />
	 * Método chamado pelas(s) JSP(s):
	 * <ul>
	 * 		<li>Não é chamado por JSP(s)</li>
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
				addMensagemErro("O usuário atual não possui unidades configuradas sob sua responsabilidade no " + RepositorioDadosInstitucionais.get("nomeSigaa") + ".");
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
	 * Lista de autorizações da unidade do usuário logado. 
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

			//todas as unidades onde o usuário atual é chefe
			AutorizacaoDepartamentoDao autorizacoeDao = getDAO(AutorizacaoDepartamentoDao.class);
			Collection<UnidadeGeral> unidades = new ArrayList<UnidadeGeral>();			
			unidades.addAll( undDao.findUnidadesByResponsavel(getUsuarioLogado().getServidor().getId(), new Character[] {NivelResponsabilidade.CHEFE, NivelResponsabilidade.VICE, NivelResponsabilidade.SUPERVISOR_DIRETOR_ACADEMICO}));			
			
			if ((unidades == null) || (unidades.isEmpty())) {
				addMensagemErro("O usuário atual não possui unidades configuradas sob sua responsabilidade no " + RepositorioDadosInstitucionais.get("nomeSigaa") + ".");
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
	 * Chefe de departamento ou diretor de centro escolhe um relatório de
	 * projeto que ainda não foi analisado.
	 * 
	 * Este método é chamado na jsp	sigaa.war/extensao/AutorizacaoDepartamento/lista_relatorio.jsp
	 * Este método é chamado na jsp	sigaa.war/extensao/ValidacaoRelatorioProex/lista.jsp
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
			relatorio.setTipoParecerDepartamento(getGenericDAO().findByPrimaryKey(TipoParecerAvaliacaoExtensao.APROVADO, TipoParecerAvaliacaoExtensao.class)); //padrão
			prepareMovimento(SigaaListaComando.VALIDAR_RELATORIO_ACAO_EXTENSAO_DEPARTAMENTO);
			setConfirmButton("Confirmar Validação");
			obj.setAtividade(relatorio.getAtividade());
		} catch (Exception e) {
			notifyError(e);
			addMensagemErro(e.getMessage());
		}

		//chama o visualizar específico dependendo do tipo de relatório
		if (relatorio.getAtividade().isTipoProjeto()) {
			return forward(ConstantesNavegacao.AUTORIZACAORELATORIO_FORM_PROJETO);

		} else if (relatorio.getAtividade().isTipoCurso() || relatorio.getAtividade().isTipoEvento()) {
			return forward(ConstantesNavegacao.AUTORIZACAORELATORIO_FORM_CURSO_EVENTO);

		} else if (relatorio.getAtividade().isTipoProduto()) {
			return forward(ConstantesNavegacao.AUTORIZACAO_RELATORIO_FORM_PRODUTO);

		} else if (relatorio.getAtividade().isTipoPrograma()) {
			return forward(ConstantesNavegacao.AUTORIZACAO_RELATORIO_FORM_PROGRAMA);

		} else {
			addMensagemErro("Relatório ainda não definido para o tipo de ação selecionada.");
			return null;
		}

	}
	
	/**
	 * Permite ao Chefe de departamento ou diretor re-imprimir
	 * recibo do projeto da ação de extensão.	  
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
			addMensagemErro("Ação de extensão não encontrada");
			return null;
		}
		
		return forward(ConstantesNavegacao.AUTORIZACAOATIVIDADE_RECIBO);		
	}

	/**
	 * Chefe de departamento ou diretor de centro autoriza uma ação de extensão
	 * 
	 * Escolhe uma autorização que ainda não foi analisada por um chefe de
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
				// evitar null pointer no select do tipo de autorização
				// (ad-referendum, reunião, etc)
				if (obj.getTipoAutorizacao() == null) {
					obj.setTipoAutorizacao(new TipoAutorizacaoDepartamento());
				}

				// Inicilizar atributos evitar erro de lazy
				obj.getAtividade().setMembrosEquipe(dao.findAtivosByProjeto(obj.getAtividade().getProjeto().getId(), CategoriaMembro.DISCENTE,
						CategoriaMembro.DOCENTE,CategoriaMembro.EXTERNO,CategoriaMembro.SERVIDOR));
				// Sugere autorizado. Setado por padrão a pedido a pro reitoria de extensão
				if (obj.getDataAutorizacao() == null) {
					obj.setAutorizado(true);
				}

				// verifica data limite para envio de autorizações...
				// só verifica ações que concorrem ao edital faex
				if ( obj.getAtividade().isFinancProex() && !obj.getAtividade().isRegistro() ) {
					//gestores de extensão podem autorizar propostas a qualquer momento
					if (obj.getAtividade().getEditalExtensao().isPermitidoAutorizacaoChefe() 
						|| getUsuarioLogado().isUserInRole(SigaaPapeis.GESTOR_EXTENSAO)) {

						prepareMovimento(SigaaListaComando.AUTORIZAR_ATIVIDADE_EXTENSAO);
						setOperacaoAtiva(SigaaListaComando.AUTORIZAR_ATIVIDADE_EXTENSAO.getId());
						setConfirmButton("Confirmar");
						return forward(ConstantesNavegacao.AUTORIZACAOATIVIDADE_FORM);

					} else {
						addMensagemErro("O prazo para autorização de propostas de ações de extensão expirou.");
					}

					// sem financiamento interno ou só registro, autoriza a qualquer
					// tempo
				} else {
					prepareMovimento(SigaaListaComando.AUTORIZAR_ATIVIDADE_EXTENSAO);
					setOperacaoAtiva(SigaaListaComando.AUTORIZAR_ATIVIDADE_EXTENSAO.getId());
					setConfirmButton("Confirmar");
					return forward(ConstantesNavegacao.AUTORIZACAOATIVIDADE_FORM);
				}

		} else {
			addMensagemErro("Selecione uma ação de extensão para autorizar.");
		}

		return null;

	}
	
	
	
	
	/**
	 * Autoriza uma ação de extensão analisada pelo departamento ou diretor de centro.
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
				addMensagemErro("Parecer: Campo obrigatório não informado");
				return null;
			}
	
			obj.setDataAutorizacao(new Date());
			obj.setRegistroEntrada(getUsuarioLogado().getRegistroEntrada());

			ListaMensagens lista = obj.validate();
			if (lista != null && lista.getMensagens().size() > 0) {
				addMensagens(lista);
				return forward(ConstantesNavegacao.AUTORIZACAOATIVIDADE_FORM);
			}
			// operador escolheu não autorizar
			// na view, o select sendo 0 = não autorizar
			if (obj.getTipoAutorizacao().getId() == TipoAutorizacaoDepartamento.NAO_AUTORIZAR) {
				obj.setTipoAutorizacao(null);
			}
			
			MovimentoCadastro mov = new MovimentoCadastro();
			mov.setObjMovimentado(obj);
			mov.setAcao(MovimentoCadastro.ACAO_ALTERAR);
			mov.setCodMovimento(SigaaListaComando.AUTORIZAR_ATIVIDADE_EXTENSAO);

			obj = (AutorizacaoDepartamento) execute(mov, getCurrentRequest());
			removeOperacaoAtiva();

			// carregando autorizações e atividades
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
	 * Método utilizado para cancelar uma o precesso de autorização
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
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
	 * Método utilizado para remover uma avaliação
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>Não é chamado por JSP(s)</li>
	 * </ul>
	 * 
	 * @return
	 */
	public String removerAvaliacao() {
		return null;
	}

	/**
	 * Inicia a chamada para autorização das ações de extensão pelo chefe de
	 * departamento
	 * 
	 * Este método é chamado na jsp	sigaa.ear/sigaa.war/extensao/AutorizacaoDepartamento/recibo_autorizacao.jsp
	 * Este método é chamado na jsp	sigaa.ear/sigaa.war/extensao/EditalExtensao/form.jsp	  
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
	 * Inicia a chamada para autorização dos relatórios pelo chefe de
	 * departamento
	 * 
	 * Este método é chamado na jsp	sigaa.ear/sigaa.war/extensao/menu.jsp 
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
	 * Inicia a chamada para autorização dos relatórios pela proex como
	 *  chefe de departamento
	 * 
	 * Este método é chamado na jsp	sigaa.ear/sigaa.war/extensao/menu.jsp 
	 * 
	 * @return
	 * @throws SegurancaException
	 */
	public String listarAutorizacoesRelatorioDepartamentoProex() throws SegurancaException {
		checkRole(SigaaPapeis.GESTOR_EXTENSAO);		
		return forward(ConstantesNavegacao.AUTORIZACAORELATORIO_BUSCA);
	}

	
	/**
	 * Inicia a chamada para autorização das ações de extensão pela proex o
	 * objetivo deste tipo de autorização é auxiliar a aprovação de ações que
	 * ficam 'presas' nos departamentos aguardando indefinidamente a autorização
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
	 * Chefe de departamento ou diretor de centro autoriza um relatório de
	 * projeto extensão
	 * 
	 * Este método é chamado na jsp sigaa.ear/sigaa.war/extensao/AutorizacaoDepartamento/form_relatorio_curso_evento.jsp
	 * Este método é chamado na jsp	sigaa.war/extensao/AutorizacaoDepartamento/form_relatorio_projeto.jsp
	 *  
	 * @return
	 * @throws ArqException
	 */
	public String autorizarRelatorio() throws ArqException {
		checkRole(SigaaPapeis.CHEFE_DEPARTAMENTO, SigaaPapeis.DIRETOR_CENTRO, 
				SigaaPapeis.CHEFE_UNIDADE, SigaaPapeis.GESTOR_EXTENSAO);
		try {

			
			if (ValidatorUtil.isEmpty(relatorio.getTipoParecerDepartamento())) {
				addMensagemErro("Parecer: Campo obrigatório não informado");
				return null;
			}
			
			// Verifica se é necessário ter a justificativa
			relatorio.setDataValidacaoDepartamento(new Date());
			if(!relatorio.isAprovadoDepartamento() && ValidatorUtil.isEmpty(relatorio.getParecerDepartamento())){
				addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Justificativa");
				return null;
			}
			
			//Tipo de autorização do departamento.
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
	 * Lista todas as autorizações para a unidade informada
	 * Utilizado quando a PROEX valida ações como chefe de departamento
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
	 * Lista todos os relatórios para a unidade informada
	 * Utilizado quando a PROEX valida relatórios como chefe de departamento
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
	 * Método usado para permitir a consulta automática ao mudar unidades
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>Não é chamado por JSP(s)</li>
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