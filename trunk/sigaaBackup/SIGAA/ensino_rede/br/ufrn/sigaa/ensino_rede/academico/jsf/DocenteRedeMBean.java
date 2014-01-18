/*
* Universidade Federal do Rio Grande do Norte
* Superintendência de Informática
* Diretoria de Sistemas
*
 * Created on 14/08/2013
*/
package br.ufrn.sigaa.ensino_rede.academico.jsf;

import static br.ufrn.arq.util.ValidatorUtil.validateRequiredId;

import org.hibernate.HibernateException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.sigaa.arq.negocio.PessoaMov;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.CampusIes;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino_rede.dao.DadosCursoRedeDao;
import br.ufrn.sigaa.ensino_rede.dao.DocenteRedeDao;
import br.ufrn.sigaa.ensino_rede.dominio.DadosCursoRede;
import br.ufrn.sigaa.ensino_rede.dominio.DocenteRede;
import br.ufrn.sigaa.ensino_rede.dominio.SituacaoDocenteRede;
import br.ufrn.sigaa.ensino_rede.dominio.TipoDocenteRede;
import br.ufrn.sigaa.ensino_rede.jsf.EnsinoRedeAbstractController;
import br.ufrn.sigaa.ensino_rede.jsf.SelecionaCampus;
import br.ufrn.sigaa.ensino_rede.jsf.SelecionaCampusIesMBean;
import br.ufrn.sigaa.ensino_rede.jsf.SelecionaDocente;
import br.ufrn.sigaa.ensino_rede.jsf.SelecionaDocenteRedeMBean;
import br.ufrn.sigaa.ensino_rede.jsf.SelecionaDocenteRedeMBean.ModoBusca;
import br.ufrn.sigaa.jsf.DadosPessoaisMBean;
import br.ufrn.sigaa.jsf.OperacaoDadosPessoais;
import br.ufrn.sigaa.jsf.OperadorDadosPessoais;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

/**
 * Managed bean responsável pelas operações em docente de ensino em rede.
 *
 * @author Henrique André
 *
 */
@SuppressWarnings("serial")
@Component("docenteRedeMBean") @Scope("session")
public class DocenteRedeMBean extends EnsinoRedeAbstractController<DocenteRede> implements SelecionaCampus, OperadorDadosPessoais, SelecionaDocente {

	/** Atalho para o form de cadastro de docente. */
	private static final String TELA_DOCENTE = "/ensino_rede/docente_rede/form.jsp";

	/** Possíveis operações que o usuário pode executar */
	public enum Acao {
		/**Ação cadastrar docente.*/
	    CADASTRAR_DOCENTE,
	    /**Ação alterar docente.*/
	    ALTERAR_DOCENTE,
	    /**Ação atualizar dados pessoais do docente.*/
	    ATUALIZAR_DADOS_PESSOAIS
	}
	
	/** Operação que o usuário está executando */
	private Acao acao;
	
	/** Usuário que será cadastrado para o docente*/
	private Usuario usuario;
	
	/** Contrutor Padrão */
	public DocenteRedeMBean() {
		clear();
	}

	/**
	 * Limpa os dados de sessão.
	 * @return 
	 * @throws DAOException 
	 * @throws HibernateException 
	 */
	private void clear() {
		obj = new DocenteRede();
		obj.setPessoa(new Pessoa());
		obj.setTipo(new TipoDocenteRede());
		obj.setSituacao(new SituacaoDocenteRede(SituacaoDocenteRede.PENDENTE));
		
		usuario = new Usuario();
		acao = null;
	}
	
	/**
	 * Inícia o cadastro do docente
	 * Método Chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/ensino_rede/modulo/menu.jsp</li>
	 * </ul>
	 * @return 
	 * @throws DAOException 
	 * @throws HibernateException 
	 */
	public String iniciarCadastrar() throws ArqException {
		clear();
		
		
		acao = Acao.CADASTRAR_DOCENTE;
		
		prepareMovimento(SigaaListaComando.CADASTRAR_DOCENTE_REDE);
		setOperacaoAtiva(SigaaListaComando.CADASTRAR_DOCENTE_REDE.getId());
		
		SelecionaCampusIesMBean mBean = getMBean("selecionaCampusRedeMBean");
		mBean.setRequisitor(this);
		return mBean.iniciar();
	}

	/**
	 * Chamado após a seleção do campus
	 * Método não invocado por JSPs:
	 * @return 
	 * @throws ArqException 
	 */
	public String selecionaCampus() throws ArqException {
		 switch (acao) {
         	case CADASTRAR_DOCENTE:
         		return cadastrarPessoaDocente();
         	default :
     			return null;
		 } 
		
	}
	
	/**
	 * Direciona o usuário para tela de seleção dos dados pessoais.
	 * Método não invocado por JSPs:
	 * @return 
	 * @throws ArqException 
	 */
	private String cadastrarPessoaDocente() throws SegurancaException, DAOException {
		DadosPessoaisMBean dadosPessoaisMBean = (DadosPessoaisMBean) getMBean("dadosPessoais");
		dadosPessoaisMBean.initObj();
		dadosPessoaisMBean.setCodigoOperacao( OperacaoDadosPessoais.CADASTRO_DOCENTE_REDE );
		return dadosPessoaisMBean.popular();
	}

	/**
	 * Redireciona o usuário para tela de seleção dos dados pessoais, utilizando o botão voltar
	 * Método Chamado pelas seguintes JSP(s):
	 * <ul>
	 *   <li>/sigaa.war/ensino_rede/docente_turma/form.jsp</li>
	 * </ul>
	 * @return 
	 * @throws ArqException 
	 */
	public String telaDadosPessoais() {
		obj.getPessoa().prepararDados();
		return forward( "/geral/pessoa/dados_pessoais.jsp");
	}

	/**
	 * Redireciona o usuário para tela de seleção dos dados pessoais, utilizando o botão voltar
	 * Método Chamado pelas seguintes JSP(s):
	 * <ul>
	 *   <li>/sigaa.war/ensino_rede/docente_turma/form.jsp</li>
	 * </ul>
	 * @return 
	 * @throws ArqException 
	 */
	public String cadastrar() throws SegurancaException, ArqException, NegocioException {
		
		if( isOperacaoAtiva(SigaaListaComando.CADASTRAR_DOCENTE_REDE.getId()) ||
			 isOperacaoAtiva(SigaaListaComando.ALTERAR_DOCENTE_REDE.getId())){
				
			validateRequiredId(obj.getTipo().getId(), "Tipo", erros);
			validateRequiredId(obj.getSituacao().getId(), "Situação", erros);
			
			if (hasErrors())
				return null;
			
			MovimentoCadastro mov = new MovimentoCadastro();
			
			if (acao == Acao.CADASTRAR_DOCENTE)
				mov.setCodMovimento(SigaaListaComando.CADASTRAR_DOCENTE_REDE);
			else if (acao == Acao.ALTERAR_DOCENTE)
				mov.setCodMovimento(SigaaListaComando.ALTERAR_DOCENTE_REDE);
			else
				throw new NegocioException("Comando não definido");
			
			mov.setObjMovimentado(obj);
			
			try {
				execute(mov);
				
				if (acao == Acao.CADASTRAR_DOCENTE)
					addMensagem(MensagensArquitetura.CADASTRADO_COM_SUCESSO, "Docente");
				else if (acao == Acao.ALTERAR_DOCENTE){
					addMensagem(MensagensArquitetura.ALTERADO_COM_SUCESSO, "Docente");
					return listarDocentes();
				}
				
			} catch (NegocioException ex) {
				addMensagemErroPadrao();
			} finally {
				setOperacaoAtiva(null);
			} 
		
		} else {
			addMensagemErro("Atenção! Esta operação foi concluída anteriormente. Por favor, reinicie o processo.");
			redirectJSF(getSubSistema().getLink());			
			return null;
		}	
		
		return cancelar();
	}
	
	/**
	 * Redireciona o usuário para a página de busca e listagem de docentes para alteração
	 * Método Chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/ensino_rede/modulo/menu.jsp</li>
	 * </ul>
	 * @return 
	 * @throws DAOException 
	 * @throws HibernateException 
	 */
	public String listarDocentes() throws ArqException, NegocioException {
		clear();
		acao = Acao.ALTERAR_DOCENTE;		
		return invocaSelecionadorDocente(true);
	}

	/**
	 * Redireciona o usuário para a página de busca e listagem de docentes para alteração dos dados pessoais
	 * Método Chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/ensino_rede/modulo/menu.jsp</li>
	 * </ul>
	 * @return 
	 * @throws DAOException 
	 * @throws HibernateException 
	 */
	public String iniciarAtualizarDadosPessoais() throws ArqException, NegocioException {
		clear();			
		acao = Acao.ATUALIZAR_DADOS_PESSOAIS;
		setOperacaoAtiva(SigaaListaComando.ALTERAR_PESSOA.getId());
		return invocaSelecionadorDocente(true);
	}

	/**
	 * Redireciona o usuário para a página de busca e listagem de docentes para alteração dos dados pessoais
	 * Método Chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/ensino_rede/modulo/menu.jsp</li>
	 * </ul>
	 * @return 
	 * @throws DAOException 
	 * @throws HibernateException 
	 */
	public String iniciarAtualizarDadosPessoaisCoordenador() throws ArqException, NegocioException {
		clear();			
		acao = Acao.ATUALIZAR_DADOS_PESSOAIS;	
		setOperacaoAtiva(SigaaListaComando.ALTERAR_PESSOA.getId());
		return invocaSelecionadorDocente(false);
	}

	
	/**
	 * Invoca o MBean de selecionar docente
	 * Método não invocado por JSPs:
	 * @return 
	 * @throws DAOException 
	 * @throws HibernateException 
	 */
	private String invocaSelecionadorDocente(boolean exibirComboCampus) throws DAOException, NegocioException {
		SelecionaDocenteRedeMBean mBean = getMBean("selecionaDocenteRedeMBean");
		mBean.setRequisitor(this);
		mBean.setCampus(getCampusIes());
		mBean.setModoBusca(ModoBusca.DETALHADA);
		mBean.setExibirComboCampus(exibirComboCampus);
		mBean.setConsultar(false);
		return mBean.executar();
	}
	
	/**
	 * Redireciona o usuário para o formulário de dados pessoais.
	 * Método não invocado por JSPs:
	 * @return 
	 * @throws DAOException 
	 * @throws HibernateException 
	 */
	public String atualizarDadosPessoais() throws ArqException {
		DadosPessoaisMBean dadosPessoaisMBean = (DadosPessoaisMBean) getMBean("dadosPessoais");
		dadosPessoaisMBean.initObj();
		dadosPessoaisMBean.setObj(obj.getPessoa());
		dadosPessoaisMBean.setSubmitButton("Alterar");
		dadosPessoaisMBean.carregarMunicipios();
		dadosPessoaisMBean.setCodigoOperacao( OperacaoDadosPessoais.ALTERACAO_DADOS_DOCENTE_REDE );
		return dadosPessoaisMBean.popular();
	}
	
	@Override
	public void setCampus(CampusIes campus) throws ArqException {
		
		DadosCursoRedeDao dao = getDAO(DadosCursoRedeDao.class);
		DadosCursoRede dadosCurso = dao.findByCampusPrograma(campus, getProgramaRede());
		obj.setDadosCurso(dadosCurso);
	}

	@Override
	public void setDadosPessoais(Pessoa pessoa) {
		obj.setPessoa(pessoa);
	}

	/**
	 * Escolhe a ação a ser realizada após submeter os dados pessoais.
	 * Método não invocado por JSPs:
	 * @return 
	 * @throws DAOException 
	 * @throws HibernateException 
	 */
	public String submeterDadosPessoais() {
		
		if( isOperacaoAtiva(SigaaListaComando.CADASTRAR_DOCENTE_REDE.getId()) ||
			 isOperacaoAtiva(SigaaListaComando.ALTERAR_DOCENTE_REDE.getId()) ||	
			 isOperacaoAtiva(SigaaListaComando.ALTERAR_PESSOA.getId())
			){
			
			switch (acao) {
	     	case CADASTRAR_DOCENTE:
	    		return submeterDadosCadastro();
	     	case ATUALIZAR_DADOS_PESSOAIS:
	     		return submeterDadosAlteracao();
	     	default :
	 			return null;
			}	
		
		} 
		else {
			addMensagemErro("Atenção! Esta operação foi concluída anteriormente. Por favor, reinicie o processo.");
			redirectJSF(getSubSistema().getLink());			
			return null;
		}		
	}

	/**
	 * Redireciona o usuário para o a tela de cadastro de docente.
	 * Método não invocado por JSPs:
	 * @return 
	 * @throws DAOException 
	 * @throws HibernateException 
	 */
	private String submeterDadosCadastro() {
		DocenteRedeDao dao = getDAO(DocenteRedeDao.class);
		DocenteRede docente;
		
		try {
			
			obj.getSituacao().setId(0);
			
			docente = dao.findDocenteByCampusAndCPF(obj.getPessoa().getCpf_cnpj(), obj.getDadosCurso().getCampus());
			
			if (docente != null) {
				addMensagemErro("O docente " + docente.getNome() + " esta associado com este CPF e a este campus.");
				return null;
			}
			
		} catch (DAOException e) {
			return tratamentoErroPadrao(e);
		}

		return redirectTelaDocente();
	}
	
	/**
	 * Altera os dados pessoais do docente.
	 * Método não invocado por JSPs:
	 * @return 
	 * @throws DAOException 
	 * @throws HibernateException 
	 */
	private String submeterDadosAlteracao() {

		PessoaMov mov = new PessoaMov();
		mov.setCodMovimento(SigaaListaComando.ALTERAR_PESSOA);
		mov.setPessoa(obj.getPessoa());
		try {
			executeWithoutClosingSession(mov, getCurrentRequest());
		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens());
			return null;
		} catch (Exception e) {
			addMensagemErro("Erro Inesperado: " + e.getMessage());
			notifyError(e);
			e.printStackTrace();
			return null;
		}
		String nome = obj.getPessoa().getNome();
		addMessage("Dados pessoais de " + nome + " atualizados com sucesso!", TipoMensagemUFRN.INFORMATION);
		return cancelar();
	}
	
	/**
	 * Redireciona para o formulário do docente.
	 */
	private String redirectTelaDocente() {
		return forward(TELA_DOCENTE);
	}

	@Override
	public String getDirBase() {
		return "/ensino_rede/docente_rede/";
	}

	@Override
	public void setDocenteRede(DocenteRede docenteRede) throws ArqException {
		obj = docenteRede;
	}

	@Override
	public String selecionaDocenteRede() throws ArqException {
		
		if (acao == Acao.ATUALIZAR_DADOS_PESSOAIS)
			return atualizarDadosPessoais();
		if (acao == Acao.ALTERAR_DOCENTE)
			return alterarDocente();
		
		return null;
	}

	/**
	 * Redireciona o usuário para o formulário de alteção de docente.
	 * Método não invocado por JSPs:
	 * @return 
	 * @throws DAOException 
	 * @throws HibernateException 
	 */
	private String alterarDocente() throws ArqException {
		prepareMovimento(SigaaListaComando.ALTERAR_DOCENTE_REDE);
		setOperacaoAtiva(SigaaListaComando.ALTERAR_DOCENTE_REDE.getId());
		setConfirmButton("Alterar");
		
		return redirectTelaDocente();
	}

	/**
	 * Verifica se a ação atual é de alteração.
	 * Método não invocado por JSPs: 
	 * @return
	 */
	public boolean isAcaoAlterar () {
		
		if (acao != null && acao == Acao.ALTERAR_DOCENTE)
			return true;
		else
			return false;

	}
	
	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public Usuario getUsuario() {
		return usuario;
	}
	
}
