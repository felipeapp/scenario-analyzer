/*
* Universidade Federal do Rio Grande do Norte
* Superintendência de Informática
* Diretoria de Sistemas
*
 * Created on 21/08/2013
*/
package br.ufrn.sigaa.ensino_rede.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.faces.model.SelectItem;

import org.hibernate.HibernateException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.arq.vinculo.tipos.TipoVinculoCoordenacaoGeralRede;
import br.ufrn.sigaa.arq.vinculo.tipos.TipoVinculoCoordenadorUnidadeRede;
import br.ufrn.sigaa.dominio.CampusIes;
import br.ufrn.sigaa.ensino_rede.dao.DocenteRedeDao;
import br.ufrn.sigaa.ensino_rede.dao.SolicitacaoDocenteRedeDao;
import br.ufrn.sigaa.ensino_rede.dominio.DadosCursoRede;
import br.ufrn.sigaa.ensino_rede.dominio.DocenteRede;
import br.ufrn.sigaa.ensino_rede.dominio.ProgramaRede;
import br.ufrn.sigaa.ensino_rede.dominio.SituacaoDocenteRede;
import br.ufrn.sigaa.ensino_rede.dominio.SolicitacaoDocenteRede;
import br.ufrn.sigaa.ensino_rede.dominio.StatusSolicitacaoDocenteRede;
import br.ufrn.sigaa.ensino_rede.dominio.TipoDocenteRede;
import br.ufrn.sigaa.ensino_rede.jsf.SelecionaDocenteRedeMBean.ModoBusca;
import br.ufrn.sigaa.jsf.DadosPessoaisMBean;
import br.ufrn.sigaa.jsf.OperacaoDadosPessoais;
import br.ufrn.sigaa.jsf.OperadorDadosPessoais;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

/**
 * Managed bean responsável pelas solicitações de alteração do docente de ensino em rede.
 *
 * @author Diego Jácome
 *
 */
@SuppressWarnings("serial")
@Component("solicitacaoDocenteRedeMBean") @Scope("session")
public class SolicitacaoDocenteRedeMBean  extends EnsinoRedeAbstractController<SolicitacaoDocenteRede> implements OperadorDadosPessoais, SelecionaDocente{

	/** Atalho para o form de cadastro de docente. */
	private static final String JSP_CADASTRO_SOLICITACAO = "/ensino_rede/solicitacao_docente_rede/form.jsp";
	/** Atalho para a lista de instituições. */
	private static final String JSP_LISTAR_INSTITUICOES = "/ensino_rede/solicitacao_docente_rede/listarIes.jsp";
	/** Atalho para a lista de solicitações visualizadas pelo gestor de ensino em rede. */
	private static final String JSP_LISTAR_SOLICITACOES = "/ensino_rede/solicitacao_docente_rede/listarSolicitacoes.jsp";
	/** Atalho para a tela de visualização da solicitação. */
	private static final String JSP_VIEW_SOLICITACAO = "/ensino_rede/solicitacao_docente_rede/view.jsp";
	/** Atalho para a confirmação de homologação de solicitações. */
	private static final String JSP_CONFIRMAR = "/ensino_rede/solicitacao_docente_rede/confirmar.jsp";

	
	/** Lista de solicitações */
	private List<SolicitacaoDocenteRede> solicitacoes;
	
	/** Lista de solicitações que serão homologadas */
	private List<SolicitacaoDocenteRede> solicitacoesEscolhidas;
	
	/** Lista de instituições */
	private List<ItemCampusSolicitacao> itens;
	
	/** Novo status das solicitações que serão homologadas */
	private StatusSolicitacaoDocenteRede novoStatus;
	
	/** Observação das solicitações que serão homologadas */
	private String atendimentoGeral;
	
	/** Se está cadastrando uma solicitação */
	private boolean cadastrar;
			
	/**
	 * Construtor da Classe
	 */
	public SolicitacaoDocenteRedeMBean() {
		clear();
	}
	
	/**
	 * Limpa os dados de sessão.
	 * Método não invocado por JSPs: 
	 * @return 
	 * @throws DAOException 
	 * @throws HibernateException 
	 */
	public void  clear() {
		obj = new SolicitacaoDocenteRede();
		obj.setDocente(new DocenteRede());
		obj.getDocente().setTipo(new TipoDocenteRede());
		obj.getDocente().setSituacao(new SituacaoDocenteRede());
		obj.setSituacaoRequerida(new SituacaoDocenteRede());
		obj.setTipoRequerido(new TipoDocenteRede());
		novoStatus = new StatusSolicitacaoDocenteRede();
		atendimentoGeral = null;
		solicitacoes = new ArrayList<SolicitacaoDocenteRede>();
		solicitacoesEscolhidas = new ArrayList<SolicitacaoDocenteRede>();
	}
	
	// OPERAÇÕES RESPONSÁVEIS PELA SOLICITAÇÃO DE CADASTRO DE DOCENTE
	
	/**
	 * Prepara o caso de uso de solicitar cadastro de docente
	 * <br/><br/>
	 * Método Chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/ensino_rede/portal/menu_coordenador_rede.jsp</li>
	 * </ul>
	 * @return 
	 * @throws DAOException 
	 * @throws HibernateException 
	 */
	public String iniciarCadastrar() throws ArqException {

		clear();
		prepareMovimento(SigaaListaComando.SOLICITACAO_CADASTRO_DOCENTE_REDE);
		setOperacaoAtiva(SigaaListaComando.SOLICITACAO_CADASTRO_DOCENTE_REDE.getId());
		setCadastrar(true);
		setConfirmButton("Solicitar Cadastro");

		TipoVinculoCoordenadorUnidadeRede vinculo = (TipoVinculoCoordenadorUnidadeRede) getUsuarioLogado().getVinculoAtivo().getTipoVinculo();
		DadosCursoRede dc = vinculo.getCoordenacao().getDadosCurso();
		obj.getDocente().setDadosCurso(dc);
		obj.getDocente().setSituacao(new SituacaoDocenteRede(SituacaoDocenteRede.PENDENTE));
		obj.getDocente().getSituacao().setDescricao(SituacaoDocenteRede.getDescricao(SituacaoDocenteRede.PENDENTE));
		obj.getDocente().setDadosCurso(dc);
		
		DadosPessoaisMBean dadosPessoaisMBean = (DadosPessoaisMBean) getMBean("dadosPessoais");
		dadosPessoaisMBean.initObj();
		dadosPessoaisMBean.setCodigoOperacao( OperacaoDadosPessoais.SOLICITACAO_CADASTRO_DOCENTE_REDE );
		return dadosPessoaisMBean.popular();
	}
	
	/**
	 * Faz a solicitação de cadastro de docente.
	 * <br/><br/>
	 * Método Chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/ensino_rede/solicitacao_docente_rede/form.jsp</li>
	 * </ul>
	 * @return 
	 * @throws DAOException 
	 * @throws HibernateException 
	 */
	public String cadastrarDocente() throws ArqException {
		
		validarSolicitacaoCadastro();
		
		if (hasErrors())
			return null;
		
		obj.setUsuario(getUsuarioLogado());
		obj.setRegistro(getRegistroEntrada());
		obj.setDataSolicitacao(new Date());
		obj.setStatus(new StatusSolicitacaoDocenteRede(StatusSolicitacaoDocenteRede.AGUARDANDO_ANALISE));
		
		MovimentoCadastro mov = new MovimentoCadastro();
		mov.setObjMovimentado(obj);
		mov.setCodMovimento(SigaaListaComando.SOLICITACAO_CADASTRO_DOCENTE_REDE);
			
		try {

			execute(mov);			
			addMessage("Solicitação de cadastro de docente realizado com sucesso!", TipoMensagemUFRN.INFORMATION);

		} catch (NegocioException e) {
			e.printStackTrace();
			addMensagens( e.getListaMensagens() );
		} finally {
			setOperacaoAtiva(null);
		}
		
		return cancelar();
	}

	/**
	 * Verifica se a operação está ativa.
	 * <br/><br/>
	 * Método Chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/ensino_rede/solicitacao_docente_rede/form.jsp</li>
	 * </ul>
	 * @return 
	 * @throws DAOException 
	 * @throws HibernateException 
	 */
	public String getVerificarOperacao() {
		if(isOperacaoAtiva(SigaaListaComando.SOLICITACAO_CADASTRO_DOCENTE_REDE.getId()) ||
		   isOperacaoAtiva(SigaaListaComando.SOLICITACAO_ALTERACAO_DOCENTE_REDE.getId())	){
			return null;
		} else {
			addMensagemErro("Atenção! Esta operação foi concluída anteriormente. Por favor, reinicie o processo.");
			redirectJSF(getSubSistema().getLink());			
			return null;
		}
	}
	
	/**
	 * Valida a solicitação de cadastro de docente
	 * <br/><br/>
	 * @return 
	 * @throws DAOException 
	 * @throws HibernateException 
	 */
	private void validarSolicitacaoCadastro() {

		if (isEmpty(obj.getDocente().getDadosCurso()))
			addMensagemErro("Não foi possível selecionar os dados do curso para o cadastro do docente");
		if (isEmpty(obj.getDocente().getTipo()))
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Tipo");
		if (isEmpty(obj.getObservacao()))
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Justificativa");	
	}

	// OPERAÇÕES PARA LISTAR / ALTERAR SITUAÇÃO DOCENTES

	/**
	 * Prepara o caso de uso de solicitar alteração de docente
	 * <br/><br/>
	 * Método Chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/ensino_rede/portal/menu_coordenador_rede.jsp</li>
	 * </ul>
	 * @return 
	 * @throws DAOException 
	 * @throws HibernateException 
	 */
	public String listarDocentes() throws NegocioException, ArqException{

		clear();
		prepareMovimento(SigaaListaComando.SOLICITACAO_ALTERACAO_DOCENTE_REDE);
		setOperacaoAtiva(SigaaListaComando.SOLICITACAO_ALTERACAO_DOCENTE_REDE.getId());
		setCadastrar(false);
		setConfirmButton("Solicitar Alteração");
		
		TipoVinculoCoordenadorUnidadeRede vinculo = (TipoVinculoCoordenadorUnidadeRede) getUsuarioLogado().getVinculoAtivo().getTipoVinculo();
		DadosCursoRede dc = vinculo.getCoordenacao().getDadosCurso();
		
		SelecionaDocenteRedeMBean mBean = getMBean("selecionaDocenteRedeMBean");
		mBean.setRequisitor(this);
		mBean.setCampus(dc.getCampus());
		mBean.setModoBusca(ModoBusca.DETALHADA);
		mBean.setExibirComboCampus(false);
		mBean.setConsultar(false);
		return mBean.executar();
	}
	
	/**
	 * Faz a solicitação de alteração de docente.
	 * <br/><br/>
	 * Método Chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/ensino_rede/solicitacao_docente_rede/form.jsp</li>
	 * </ul>
	 * @return 
	 * @throws DAOException 
	 * @throws HibernateException 
	 */
	public String alterarDocente() throws ArqException, NegocioException {

		validarSolicitacaoCadastro();
		
		if (hasErrors())
			return null;
		
		prepareMovimento(SigaaListaComando.SOLICITACAO_ALTERACAO_DOCENTE_REDE);
		
		obj.setUsuario(getUsuarioLogado());
		obj.setRegistro(getRegistroEntrada());
		obj.setDataSolicitacao(new Date());
		obj.setStatus(new StatusSolicitacaoDocenteRede(StatusSolicitacaoDocenteRede.AGUARDANDO_ANALISE));
		
		MovimentoCadastro mov = new MovimentoCadastro();
		mov.setObjMovimentado(obj);
		mov.setCodMovimento(SigaaListaComando.SOLICITACAO_ALTERACAO_DOCENTE_REDE);
			
		try {

			execute(mov);			
			addMensagem(MensagensArquitetura.OPERACAO_SUCESSO);

		} finally {
			setOperacaoAtiva(null);
		}
		
		return cancelar();
	}
		
	// OPERAÇÕES PARA HOMOLOGAR DOCENTES
	
	/**
	 * Retorna o número total de solicitações de docentes do programa.
	 * <br/><br/>
	 * Método Chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/ensino_rede/modulo/menu.jsp</li>
	 * </ul>
	 * @return 
	 * @throws DAOException 
	 * @throws HibernateException 
	 */
	public Integer getNumSolicitacoesPrograma () throws HibernateException, DAOException {
		
		SolicitacaoDocenteRedeDao dao = null;
		
		try {
		
			dao = getDAO(SolicitacaoDocenteRedeDao.class);
			
			TipoVinculoCoordenacaoGeralRede vinculo = (TipoVinculoCoordenacaoGeralRede) getUsuarioLogado().getVinculoAtivo().getTipoVinculo();
			ProgramaRede programa = vinculo.getCoordenacao().getProgramaRede();
		
			Integer res = dao.countSolicitacoesByPrograma(programa);
			return res;
			
		} finally {
			if (dao != null)
				dao.close();
		}
		
	}
	
	/**
	 * Busca os números de solicitações de cada instituição do programa e redireciona para tela de listagem das instituições.
	 * <br/><br/>
	 * Método Chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/ensino_rede/modulo/menu.jsp</li>
	 * </ul>
	 * @return 
	 * @throws DAOException 
	 * @throws HibernateException 
	 */
	public String iniciarHomologacaoDocente () throws HibernateException, ArqException {
		
		SolicitacaoDocenteRedeDao dao = null;
		
		try {
			
			checkRole(SigaaPapeis.COORDENADOR_GERAL_REDE);
			clear();
			
			TipoVinculoCoordenacaoGeralRede vinculo = (TipoVinculoCoordenacaoGeralRede) getUsuarioLogado().getVinculoAtivo().getTipoVinculo();
			ProgramaRede programa = vinculo.getCoordenacao().getProgramaRede();
			
			dao = getDAO(SolicitacaoDocenteRedeDao.class);
			HashMap<CampusIes,Integer> hash = dao.countNumSolicitacoesCampusByPrograma(programa);
			Set<CampusIes> campus = hash.keySet();
			itens = new ArrayList<ItemCampusSolicitacao>();
			
			for (CampusIes c : campus){
				
				Integer numSol = hash.get(c);
				
				if (!isEmpty(numSol)){
					ItemCampusSolicitacao item = new ItemCampusSolicitacao();
					item.setIdCampus(c.getId());
					item.setNome(c.getNome());
					item.setSigla(c.getInstituicao().getSigla());
					item.setQuantidade(numSol);
					itens.add(item);
				}
			}
			
			if (isEmpty(itens)){
				addMensagemErro("Não existe solicitações pendentes.");
				return null;
			}
			
		} finally {
			if (dao != null)
				dao.close();
		}
		
		return forward(JSP_LISTAR_INSTITUICOES);
	}
	
	/**
	 * Seleciona a instituição escolhida e redireciona o usuário para tela de listagem de instituições.
	 * <br/><br/>
	 * Método Chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/ensino_rede/solicitacao_docente_rede/listarIes.jsp</li>
	 * </ul>
	 * @return 
	 * @throws HibernateException 
	 * @throws ArqException 
	 */
	public String selecionarInstituicao () throws HibernateException, ArqException {
		
		Integer idCampus = getParameterInt("idCampus");
		SolicitacaoDocenteRedeDao dao = null;

		try {
			
			atendimentoGeral = null;
			
			setOperacaoAtiva(SigaaListaComando.HOMOLOGAR_SOLICITACAO_DOCENTES_REDE.getId());
			prepareMovimento(SigaaListaComando.HOMOLOGAR_SOLICITACAO_DOCENTES_REDE);
			
			TipoVinculoCoordenacaoGeralRede vinculo = (TipoVinculoCoordenacaoGeralRede) getUsuarioLogado().getVinculoAtivo().getTipoVinculo();
			ProgramaRede programa = vinculo.getCoordenacao().getProgramaRede();
			
			dao = getDAO(SolicitacaoDocenteRedeDao.class);
			solicitacoes = dao.findSolicitacoesByCampusPrograma(idCampus, programa.getId());
			
		} finally {
			if (dao != null)
				dao.close();
		}		
		
		return forward(JSP_LISTAR_SOLICITACOES);
	}
	
	/**
	 * Seleciona as solicitações que serão homologadas e direciona o usuário para tela de confirmação.
	 * <br/><br/>
	 * Método Chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/ensino_rede/solicitacao_docente_rede/listarSolicitacoes.jsp</li>
	 * </ul>
	 * @return 
	 * @throws DAOException 
	 * @throws HibernateException 
	 */
	public String selecionarSolicitacoes () {
		
		if(!isOperacaoAtiva(SigaaListaComando.HOMOLOGAR_SOLICITACAO_DOCENTES_REDE.getId())){
			addMensagemErro("Atenção! Esta operação foi concluída anteriormente. Por favor, reinicie o processo.");
			redirectJSF(getSubSistema().getLink());			
			return null;			
		}
		
		novoStatus.setDescricao(StatusSolicitacaoDocenteRede.getDescricao(novoStatus.getId()));
		
		String[] selecionados = getParameterValues("selecionados");
		if (selecionados == null || solicitacoesEscolhidas == null)
			solicitacoesEscolhidas = new ArrayList<SolicitacaoDocenteRede>();
		
		if (selecionados != null){
			
			// Remove as solicitações que não foram escolhidas.
			if (!isEmpty(solicitacoesEscolhidas)){
				
				Iterator<SolicitacaoDocenteRede> it = solicitacoesEscolhidas.iterator(); 
				while (it.hasNext()){
					boolean escolhida = false;
					SolicitacaoDocenteRede s = it.next();
					for (int i = 0; i < selecionados.length; i++) {
						int id = Integer.parseInt(selecionados[i]);
						if (s.getId() == id)
							escolhida = true;
					}
					if (!escolhida)
						it.remove();
				}
			}
			
			for (int i = 0; i < selecionados.length; i++) {
				
				int id = Integer.parseInt(selecionados[i]);
				boolean possuiSolicitacao = false;
				
				for (SolicitacaoDocenteRede s : solicitacoesEscolhidas)
					if (s.getId() == id)
						possuiSolicitacao = true;
				
				if (!possuiSolicitacao){
					for (SolicitacaoDocenteRede s : solicitacoes) 
						if (s.getId() == id)
							solicitacoesEscolhidas.add(s);		
				}

			}			
		}
		
		if (isEmpty(solicitacoesEscolhidas)){
			addMensagemErro("Nenhuma solicitação foi escolhida.");
			return null;
		}
		
		return forward(JSP_CONFIRMAR);
	}
	
	/**
	 * Direciona o usuário para a tela de visualização de uma solicitação.
	 * <br/><br/>
	 * Método Chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/ensino_rede/solicitacao_docente_rede/listarSolicitacoes.jsp</li>
	 * </ul>
	 * @return 
	 * @throws DAOException 
	 * @throws HibernateException 
	 */
	public String viewSolicitacao () {
		
		Integer idSolicitacao = getParameterInt("idSolicitacao");
		novoStatus.setDescricao(StatusSolicitacaoDocenteRede.getDescricao(novoStatus.getId()));
		
		if (solicitacoesEscolhidas == null)
			solicitacoesEscolhidas = new ArrayList<SolicitacaoDocenteRede>();
		
		solicitacoesEscolhidas.clear();
		
		for (SolicitacaoDocenteRede s : solicitacoes)
			if (s.getId() == idSolicitacao)
				solicitacoesEscolhidas.add(s);
		
		return forward(JSP_VIEW_SOLICITACAO);
	}
	
	/**
	 * Homologa solicitações de docente.
	 * <br/><br/>
	 * Método Chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/ensino_rede/solicitacao_docente_rede/confirmar.jsp</li>
	 * </ul>
	 * @return 
	 * @throws DAOException 
	 * @throws HibernateException 
	 */
	public String confirmarHomologacao () throws NegocioException, ArqException {
		
		if(!isOperacaoAtiva(SigaaListaComando.HOMOLOGAR_SOLICITACAO_DOCENTES_REDE.getId())){
			addMensagemErro("Atenção! Esta operação foi concluída anteriormente. Por favor, reinicie o processo.");
			return null;			
		}
		
		for (SolicitacaoDocenteRede s : solicitacoesEscolhidas){
			s.setAtendimento(atendimentoGeral);
			s.setDataAtendimento(new Date());
			s.setUsuarioAtendimento(getUsuarioLogado());
			s.setRegistroAtendimento(getUsuarioLogado().getRegistroEntrada());
			s.setStatus(novoStatus);
		}
		
		MovimentoCadastro mov = new MovimentoCadastro();
		mov.setColObjMovimentado(solicitacoesEscolhidas);
		mov.setCodMovimento(SigaaListaComando.HOMOLOGAR_SOLICITACAO_DOCENTES_REDE);
		
		try {
			
			execute(mov);		
			addMessage("Solicitação Homologada com sucesso!", TipoMensagemUFRN.INFORMATION);
			
			if (getNumSolicitacoesPrograma() == 0)
				return cancelar();

		} finally {
			setOperacaoAtiva(null);
		}
		
		return iniciarHomologacaoDocente();
	}
	
	// OPERAÇÕES RESPONSÁVEIS PELO MBEAN DE SELEÇÃO DE DOCENTES
	
	@Override
	public String selecionaDocenteRede() throws ArqException {
		
		if (!cadastrar){
			obj.setSituacaoRequerida(obj.getDocente().getSituacao());
			obj.setTipoRequerido(obj.getDocente().getTipo());
		}
		
		return forward(JSP_CADASTRO_SOLICITACAO);
	}

	@Override
	public void setDocenteRede(DocenteRede docenteRede) throws ArqException {
		obj.setDocente(docenteRede);
	}
	
	// OPERAÇÕES RESPONSÁVEIS PELO MBEAN DOS DADOS PESSOAIS
	
	@Override
	public void setDadosPessoais(Pessoa pessoa) {
		obj.getDocente().setPessoa(pessoa);		
	}
	
	@Override
	public String submeterDadosPessoais() {
		
		if( isOperacaoAtiva(SigaaListaComando.SOLICITACAO_CADASTRO_DOCENTE_REDE.getId()) ||
			isOperacaoAtiva(SigaaListaComando.SOLICITACAO_ALTERACAO_DOCENTE_REDE.getId())	){			
		
			DocenteRedeDao dao = getDAO(DocenteRedeDao.class);
			DocenteRede docente;
			
			try {
				
				docente = dao.findDocenteByCampusAndCPF(obj.getDocente().getPessoa().getCpf_cnpj(), obj.getDocente().getDadosCurso().getCampus());
				
				if (docente != null) {
					addMensagemErro("O docente " + docente.getNome() + " está associado com este CPF e a este campus.");
					return null;
				}
				
			} catch (DAOException e) {
				return tratamentoErroPadrao(e);
			}
		
		} else {
			addMensagemErro("Atenção! Esta operação foi concluída anteriormente. Por favor, reinicie o processo.");
			redirectJSF(getSubSistema().getLink());			
			return null;
		}
		
		return forward(JSP_CADASTRO_SOLICITACAO);
	}
	
	/**
	 * Retorna todas os Status de Solicitação de Docente para o combo.
	 * <br/><br/>
	 * Método Chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/ensino_rede/solicitacao_docente_rede/listarSolicitacoes.jsp</li>
	 * </ul>
	 * @return 
	 * @throws DAOException 
	 * @throws HibernateException 
	 */
	public List<SelectItem> getStatusCombo() throws HibernateException, DAOException {
		return toSelectItems(StatusSolicitacaoDocenteRede.getStatusDeferimento(), "id", "descricao");
	}		
	
	/**
	 * Retorna para a tela de dados pessoais.
	 * <br/><br/>
	 * Método não invocado por JSPs:
	 * @return 
	 * @throws DAOException 
	 * @throws HibernateException 
	 */
	public String telaDadosPessoais() {
		obj.getDocente().getPessoa().prepararDados();
		return forward( "/geral/pessoa/dados_pessoais.jsp");
	}
	
	/**
	 * Retorna para tela de listar instituições.
	 * <br/><br/>
	 * Método Chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/ensino_rede/solicitacao_docente_rede/listarSolicitacoes.jsp</li>
	 *   <li>/sigaa.war/ensino_rede/solicitacao_docente_rede/confirmar.jsp</li>
	 * </ul>
	 * @return 
	 */
	public String telaInstituicoes () {
		return forward(JSP_LISTAR_INSTITUICOES);
	}
	
	/**
	 * Retorna para tela de listar solicitações.
	 * <br/><br/>
	 * Método Chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/ensino_rede/solicitacao_docente_rede/view.jsp</li>
	 * </ul>
	 * @return 
	 */
	public String telaSolicitacoes () {
		return forward(JSP_LISTAR_SOLICITACOES);
	}

	public void setCadastrar(boolean cadastrar) {
		this.cadastrar = cadastrar;
	}

	public boolean isCadastrar() {
		return cadastrar;
	}
	
	public void setItens(List<ItemCampusSolicitacao> itens) {
		this.itens = itens;
	}

	public List<ItemCampusSolicitacao> getItens() {
		return itens;
	}

	public void setSolicitacoes(List<SolicitacaoDocenteRede> solicitacoes) {
		this.solicitacoes = solicitacoes;
	}

	public List<SolicitacaoDocenteRede> getSolicitacoes() {
		return solicitacoes;
	}

	public void setNovoStatus(StatusSolicitacaoDocenteRede novoStatus) {
		this.novoStatus = novoStatus;
	}

	public StatusSolicitacaoDocenteRede getNovoStatus() {
		return novoStatus;
	}

	public void setAtendimentoGeral(String atendimentoGeral) {
		this.atendimentoGeral = atendimentoGeral;
	}

	public String getAtendimentoGeral() {
		return atendimentoGeral;
	}

	public void setSolicitacoesEscolhidas(List<SolicitacaoDocenteRede> solicitacoesEscolhidas) {
		this.solicitacoesEscolhidas = solicitacoesEscolhidas;
	}

	public List<SolicitacaoDocenteRede> getSolicitacoesEscolhidas() {
		return solicitacoesEscolhidas;
	}

	/** Item de Registro da ação. */
	public class ItemCampusSolicitacao {
		
		/** Identificador do campus. */
		private int idCampus;
		
		/** Quantidade de solicitações. */
		private int quantidade;

		/** Sigla da instituição. */
		private String sigla;
		
		/** Nome do campus. */
		private String nome;

		public void setIdCampus(int idCampus) {
			this.idCampus = idCampus;
		}

		public int getIdCampus() {
			return idCampus;
		}

		public void setQuantidade(int quantidade) {
			this.quantidade = quantidade;
		}

		public int getQuantidade() {
			return quantidade;
		}

		public void setNome(String nome) {
			this.nome = nome;
		}

		public String getNome() {
			return nome;
		}
		
		public void setSigla(String sigla) {
			this.sigla = sigla;
		}

		public String getSigla() {
			return sigla;
		}
	}

}
