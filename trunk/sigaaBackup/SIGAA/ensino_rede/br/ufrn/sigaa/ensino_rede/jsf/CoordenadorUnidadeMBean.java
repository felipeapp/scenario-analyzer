package br.ufrn.sigaa.ensino_rede.jsf;

import static br.ufrn.arq.util.ValidatorUtil.validateEmailRequired;
import static br.ufrn.arq.util.ValidatorUtil.validateRequiredId;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.hibernate.HibernateException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dominio.Comando;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.ConstantesErro;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.CampusIes;
import br.ufrn.sigaa.dominio.Telefone;
import br.ufrn.sigaa.ensino.dominio.CargoAcademico;
import br.ufrn.sigaa.ensino_rede.dao.CoordenadorUnidadeDao;
import br.ufrn.sigaa.ensino_rede.dao.DadosCursoRedeDao;
import br.ufrn.sigaa.ensino_rede.dominio.CoordenadorUnidade;
import br.ufrn.sigaa.ensino_rede.dominio.DadosCoordenacaoUnidade;
import br.ufrn.sigaa.ensino_rede.dominio.DadosCursoRede;
import br.ufrn.sigaa.ensino_rede.dominio.DocenteRede;
import br.ufrn.sigaa.ensino_rede.jsf.SelecionaDocenteRedeMBean.ModoBusca;
import br.ufrn.sigaa.jsf.DadosPessoaisMBean;
import br.ufrn.sigaa.jsf.OperacaoDadosPessoais;
import br.ufrn.sigaa.jsf.OperadorDadosPessoais;
import br.ufrn.sigaa.jsf.UsuarioMBean;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

/**
 * Controller respnsavel por cadastrar e alterar Coordenadores de Unidade
 * 
 * @author Joab
 *
 */
@SuppressWarnings("serial")
@Component("coordenadorUnidadeMBean") @Scope("session")
public class CoordenadorUnidadeMBean extends EnsinoRedeAbstractController<CoordenadorUnidade> implements SelecionaCampus,SelecionaDocente,OperadorDadosPessoais{

	/**Define se é uma Ação de:*/  
	public enum Acao {
		/**cadastro de coordenador,*/
	    CADASTRAR_COORDENADOR,
	    /**de alteração de coordenador*/
	    ALTERAR_COORDENADOR,
	    /**,remoção de coordenador*/
	    INATIVAR_COORDENADOR,
	    /**, cadastrar secretaria*/
	    CADASTRAR_SECRETARIA, 
	    /**,alterar secretaria.*/
	    ALTERAR_SECRETARIA,
	    /**,remoção de secretario(a)*/
	    INATIVAR_SECERTARIA,
	    /**,atualiza dados pessoais do secretari(a).*/
	    ATUALIZAR_DADOS_PESSOAIS
	}
	
	/**Ação que está sendo Executada*/
	private Acao acao;
		
	/**Identificador da Instituição usado na busca de coordenadores*/
	private int idInstituicao;
		
	/**Usado no cadastro de telefones da coordenação.*/
	private Telefone telefone;
		
	/**Constante que guarda o caminho da tela de cadastro de Coordenadores Unidade.*/
	private static final String FORM_COORDENACAO = "/ensino_rede/coordenador_unidade/form.jsp";
	
	/**Constante que guarda o caminho da tela de cadastro de Coordenadores Unidade.*/
	private static final String VIEW_COORDENACAO = "/ensino_rede/coordenador_unidade/view_coordenacao.jsp";
	
	/**Constante que guarda o caminho da tela que os Coordenadores de Unidade.*/
	private static final String LISTA_COORDENADORES = "/ensino_rede/coordenador_unidade/lista_coordenadores.jsp";
	
	/**Constante que guarda o caminho da tela que os Coordenadores de Unidade.*/
	private static final String LISTA_SECRETARIOS = "/ensino_rede/coordenador_unidade/lista_secretarios.jsp";
	
	public CoordenadorUnidadeMBean() {
		resultadosBusca = new ArrayList<CoordenadorUnidade>();
		clear();
	}
	
	/**Inicializa os Objetos deste Controller
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>Método não invocado por JSP.</li>
	 *	</ul>
	 * */
	public void clear(){
		obj = new CoordenadorUnidade();
		obj.setPessoa(new Pessoa());
		obj.setCargo(new CargoAcademico());
		obj.setDados(new DadosCoordenacaoUnidade());
		obj.getDados().setTelefones(new ArrayList<Telefone>());
		telefone = new Telefone();
		telefone.setTipo(Telefone.FIXO);
		idInstituicao = 0;
	}
	
	/**Inicia a ação de cadastro de Coordenador de unidade.
	 *
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/ensino_rede/modulo/menu.jsp</li>
	 *	</ul>
	 */
	public String iniciarCadastrarCoordenador() throws ArqException {
		clear();
		prepareMovimento(SigaaListaComando.CADASTRO_COORDENADOR_UNIDADE);
		setOperacaoAtiva(SigaaListaComando.CADASTRO_COORDENADOR_UNIDADE.getId());
		acao = Acao.CADASTRAR_COORDENADOR;
		SelecionaCampusIesMBean mBean = getMBean("selecionaCampusRedeMBean");
		mBean.setRequisitor(this);
		return mBean.iniciar();
	}
	
	/**Inicia a ação de cadastro de secretário(a) de unidade.
	 *
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/ensino_rede/modulo/menu.jsp</li>
	 *	</ul>
	 */
	public String iniciarCadastrarSecretaria() throws ArqException {
		clear();
		CoordenadorUnidadeDao dao = getDAO(CoordenadorUnidadeDao.class);
		CoordenadorUnidade secretario =null;
		if( getCampusIes() != null ){
			secretario = dao.findMembroCoordenacaoAtivoBycargo(getProgramaRede(), getCampusIes().getInstituicao().getId(),getCampusIes().getId(),CargoAcademico.SECRETARIA);
			if(secretario != null){
				addMensagemErro("Esta unidade já possui secretário(a) cadastrado(a)");
				return null;
			}
		}
			
		prepareMovimento(SigaaListaComando.CADASTRO_SECRETARIO_UNIDADE);
		setOperacaoAtiva(SigaaListaComando.CADASTRO_SECRETARIO_UNIDADE.getId());
		acao = Acao.CADASTRAR_SECRETARIA;
		setConfirmButton("Cadastrar");
		if(!isAcessoCoordenadorGeral()){
			setCampus(getCampusIes());
			return cadastrarPessoaSecretaria();
		}
		if(hasErrors())
			return null;
		SelecionaCampusIesMBean mBean = getMBean("selecionaCampusRedeMBean");
		mBean.setRequisitor(this);
		return mBean.iniciar();
	}
	
	/**
	 * Redireciona o usuário para a página de busca e listagem de Secretarios para alteração dos dados pessoais
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
		return listarSecretarios();
	}
	
	/**
	 * executa o cadastro/alteração do Coordenador da Unidade
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/ensino_rede/coordenador_unidade/form.jsp</li>
	 *	</ul>
	 */
	public String cadastrar() throws SegurancaException, ArqException, NegocioException {
		if(!isOperacaoAtiva(SigaaListaComando.CADASTRO_SECRETARIO_UNIDADE.getId(),SigaaListaComando.INATIVAR_COORDENADOR_UNIDADE.getId()
				,SigaaListaComando.CADASTRO_COORDENADOR_UNIDADE.getId(),SigaaListaComando.ALTERAR_COORDENADOR_UNIDADE.getId())){
			addMensagemErro("Atenção! Esta operação foi concluída anteriormente. Por favor, reinicie o processo.");
			return cancelar();			
		}
		validateRequiredId(obj.getCargo().getId(), "Cargo", erros);
		validateEmailRequired(obj.getDados().getEmail(), "E-mail", erros);
		
		if(hasErrors())
			return null;
		MovimentoCadastro mov = new MovimentoCadastro();
		mov.setCodMovimento(getComando());
		mov.setObjMovimentado(obj);
		
		try {
			execute(mov);
			setMensagemSucesso();
			
		} catch (NegocioException e) {
			e.printStackTrace();
			addMensagens(e.getListaMensagens());
			return null;
		} catch (ArqException arqE){
			arqE.printStackTrace();
			if (arqE.getCodErro() == ConstantesErro.SOLICITACAO_JA_PROCESSADA)
				addMensagemErro("Atenção! Esta operação foi concluída anteriormente. Por favor, reinicie o processo.");
		} catch (Exception ex){
			addMensagemErroPadrao();
		}
		return voltar();		
	}
	
	/**
	 * Retorna o comando com operação ativa
	 * <br />
	 * Método não chamado por JSP(s):
	 */
	private Comando getComando()  {
		switch (acao) {
		case CADASTRAR_COORDENADOR:
			return SigaaListaComando.CADASTRO_COORDENADOR_UNIDADE;
		case ALTERAR_COORDENADOR:
			return SigaaListaComando.ALTERAR_COORDENADOR_UNIDADE;
		case INATIVAR_COORDENADOR:
			return SigaaListaComando.INATIVAR_COORDENADOR_UNIDADE;
		case CADASTRAR_SECRETARIA:
			return SigaaListaComando.CADASTRO_SECRETARIO_UNIDADE;
		case ALTERAR_SECRETARIA:
			return SigaaListaComando.CADASTRO_SECRETARIO_UNIDADE;
		case INATIVAR_SECERTARIA:
			return SigaaListaComando.INATIVAR_COORDENADOR_UNIDADE;
		default:
			addMensagemErro("Comando não definido");
			return null;
		}		
	}
	
	/**
	 * Retorna o comando com operação ativa
	 * <br />
	 * Método não chamado por JSP(s):
	 */
	private void setMensagemSucesso()  {
		switch (acao) {
		case CADASTRAR_COORDENADOR:
			addMensagem(MensagensArquitetura.CADASTRADO_COM_SUCESSO, "Coordenador");
			break;
		case ALTERAR_COORDENADOR:
			addMensagem(MensagensArquitetura.ALTERADO_COM_SUCESSO, "Coordenador");
			break;
		case INATIVAR_COORDENADOR:
			addMensagem(MensagensArquitetura.REMOCAO_EFETUADA_COM_SUCESSO, "Coordenador(a)");
			break;
		case CADASTRAR_SECRETARIA:
			addMensagem(MensagensArquitetura.CADASTRADO_COM_SUCESSO, "Secretário(a)");
			break;
		case ALTERAR_SECRETARIA:
			addMensagem(MensagensArquitetura.ALTERADO_COM_SUCESSO, "Secretário(a)");
			break;
		case INATIVAR_SECERTARIA:
			addMensagem(MensagensArquitetura.REMOCAO_EFETUADA_COM_SUCESSO, "Secretário(a)");
			break;
		default:
			addMensagem(MensagensArquitetura.OPERACAO_SUCESSO);
		}		
		
	}
	
	/**
	 * Define se o acesso é de um coordenador geral
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/ensino_rede/coordenador_unidade/form.jsp</li>
	 *	</ul>
	 */
	public boolean isAcessoCoordenadorGeral()  {
		return getUsuarioLogado().getVinculoAtivo().getTipoVinculo().isCoordenadorGeralRede();
	}
	
	/**
	 * Define se o acesso é de um coordenador de unidade.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/ensino_rede/coordenador_unidade/menu_coordenador_rede.jsp</li>
	 *	</ul>
	 */
	public boolean isAcessoCoordenadorUnidade()  {
		return getUsuarioLogado().getVinculoAtivo().getTipoVinculo().isCoordenadorUnidadeRede();
	}
	
	@Override
	public void setCampus(CampusIes campus) throws ArqException {
		DadosCursoRedeDao dao = getDAO(DadosCursoRedeDao.class);
		DadosCursoRede dadosCurso = dao.findByCampusPrograma(campus, getProgramaRede());
		obj.setDadosCurso(dadosCurso);
		obj.setCampus(dadosCurso.getCampus());
	}

	@Override
	public void setDadosPessoais(Pessoa pessoa) {
		obj.setPessoa(pessoa);
	}

	@Override
	public String selecionaCampus() throws ArqException, NegocioException {		
		 CoordenadorUnidade secretaria = null;
		 CoordenadorUnidadeDao dao = getDAO(CoordenadorUnidadeDao.class);
		switch (acao) {
      	case CADASTRAR_COORDENADOR:
      		return listarDocentes();
      	case CADASTRAR_SECRETARIA:
      		secretaria = dao.findMembroCoordenacaoAtivoBycargo(getProgramaRede(), obj.getCampus().getInstituicao().getId(),obj.getCampus().getId(),CargoAcademico.SECRETARIA);
			if(secretaria != null){
				addMensagemErro("Esta unidade já possui secretário(a) cadastrado(a)");
				return null;
			}
      		return cadastrarPessoaSecretaria();
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
	private String cadastrarPessoaSecretaria() throws SegurancaException, DAOException {
		DadosPessoaisMBean dadosPessoaisMBean = (DadosPessoaisMBean) getMBean("dadosPessoais");
		dadosPessoaisMBean.initObj();
		dadosPessoaisMBean.setCodigoOperacao( OperacaoDadosPessoais.CADASTRAR_SECRETARIA_UNIDADE_REDE );
		return dadosPessoaisMBean.popular();
	}

	/**
	 * Escolhe a ação a ser realizada após submeter os dados pessoais.
	 * Método não invocado por JSPs:
	 * @return 
	 * @throws DAOException 
	 * @throws HibernateException 
	 */
	public String submeterDadosPessoais() {
		
		if( isOperacaoAtiva(SigaaListaComando.CADASTRO_SECRETARIO_UNIDADE.getId())){
			switch (acao) {
	     	case CADASTRAR_SECRETARIA:
	    		return submeterDadosCadastro();
//	     	case ATUALIZAR_DADOS_PESSOAIS:
//	     		return submeterDadosAlteracao();
	     	default :
	 			return null;
			}	
		} 
		else {
			addMensagemErro("Atenção! Esta operação foi concluída anteriormente. Por favor, reinicie o processo.");
			cancelar();			
			return null;
		}		
	}
	
	/**
	 * Redireciona o usuário para  a tela de cadastro de secretario(a).
	 * Método não invocado por JSPs:
	 * @return 
	 * @throws DAOException 
	 * @throws HibernateException 
	 */
	private String submeterDadosCadastro() {
		CoordenadorUnidadeDao dao = getDAO(CoordenadorUnidadeDao.class);
		CoordenadorUnidade secretario;
		
		try {
			secretario = dao.findSecretarioAtivoByCPF(obj.getPessoa().getCpf_cnpj());
			
			if (secretario != null) {
				addMensagemErro("O Secretário(a) " + secretario.getPessoa().getNome() + " associado com este CPF já possui um vinculo de secretário(a) em outra unidade.");
				return null;
			}
			
		} catch (DAOException e) {
			return tratamentoErroPadrao(e);
		}
		obj.getCargo().setId(CargoAcademico.SECRETARIA);
		return forward(FORM_COORDENACAO);
	}
	
	/**Inicia o listar Coordenadores de unidade..
	 *
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/ensino_rede/modulo/menu.jsp</li>
	 *	</ul>
	 * */
	public String listarCoordenadores() throws ArqException {
		clear();
		setResultadosBusca(new ArrayList<CoordenadorUnidade>());
		acao = Acao.ALTERAR_COORDENADOR; 
		prepareMovimento(SigaaListaComando.INATIVAR_COORDENADOR_UNIDADE);
		return forward(LISTA_COORDENADORES);
	}
	
	/**Inicia o listar Secretários de unidade..
	 *
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/ensino_rede/modulo/menu.jsp</li>
	 *	</ul>
	 * */
	public String listarSecretarios() throws ArqException {
		clear();
		setResultadosBusca(new ArrayList<CoordenadorUnidade>());
		acao = Acao.ALTERAR_SECRETARIA;
		return forward(LISTA_SECRETARIOS);
	}
	
	/**
	 * Executa a busca por Coordenadores da Unidade
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/ensino_rede/coordenador_unidade/lista_coordenadores.jsp</li>
	 *	</ul>
	 */
	public void buscarCoordenacao() throws DAOException, NegocioException  {
		CoordenadorUnidadeDao dao = getDAO(CoordenadorUnidadeDao.class);
		Collection<CoordenadorUnidade> listaCordenacao = new ArrayList<CoordenadorUnidade>();
		if(acao == Acao.ALTERAR_COORDENADOR || acao == Acao.INATIVAR_COORDENADOR)
			listaCordenacao = dao.findCoordenacaoByIesCargos(getProgramaRede(), idInstituicao,null,CargoAcademico.COORDENACAO,CargoAcademico.VICE_COORDENACAO);
		else 
			listaCordenacao = dao.findCoordenacaoByIesCargos(getProgramaRede(), idInstituicao,null,CargoAcademico.SECRETARIA);

		if(listaCordenacao.isEmpty()){
			listaCordenacao = new ArrayList<CoordenadorUnidade>();
			addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
			return;
		}else
			setResultadosBusca(listaCordenacao);
	}
	
	/**
	 * adiciona um telefone a lista de telefones da coordenacao
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/ensino_rede/coordenador_unidade/form.jsp</li>
	 *	</ul>
	 * @throws DAOException 
	 */
	public void addTelefone() throws DAOException  {
		ValidatorUtil.validateRequired(telefone.getCodigoArea(), "Código de Área", erros);
		if(telefone.getTipo() == Telefone.CELULAR)
			telefone.setRamal(null);
		ValidatorUtil.validateMinLength(telefone.getNumero(),8,"Número do Telefone",erros);
		if(telefone.getRamal() != null)
			ValidatorUtil.validateMinLength(telefone.getRamal().toString(),4,"Ramal",erros);
		if(obj.getDados().getTelefones().contains(telefone))
			addMensagemErro("Este telefone já foi adicionado");
		
		if(hasErrors())
			return;
		getGenericDAO().createOrUpdate(telefone);
		obj.getDados().getTelefones().add(telefone);
		telefone = new Telefone();
		telefone.setTipo(Telefone.FIXO);
	}
	
	/**
	 * verifica se esta é uma operação de cadastro/alteração de secretario(a).
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/ensino_rede/coordenador_unidade/form.jsp</li>
	 *		<li>sigaa.war/ensino_rede/coordenador_unidade/lista_coordenadores.jsp</li>
	 *	</ul>
	 */
	public boolean isCadastroSecretaria(){
		if(acao == Acao.CADASTRAR_SECRETARIA || acao == Acao.ALTERAR_SECRETARIA)
			return true;
		else 
			return false;
	}
	
	/**
	 * adiciona um telefone a lista de telefones da coordenacao
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/ensino_rede/coordenador_unidade/form.jsp</li>
	 *	</ul>
	 */
	public String removerTelefone(ActionEvent e)  {
		int index = getParameterInt("indice",-1);	
		boolean removeu  = false;
		if ( index > 0 && !obj.getDados().getTelefones().isEmpty() ) {
			for (Iterator<Telefone> it = obj.getDados().getTelefones().iterator(); it.hasNext(); ) {
				Telefone t = it.next();
				if(t.getId() == index){
					it.remove();
					removeu = true;					
				}
			}
		}
		if(!removeu){
			addMensagemErro("Atenção! Esta operação foi concluída anteriormente. Por favor, reinicie o processo.");
			return cancelar();
		}
		return null;
	}
	
	/**
	 * retorna para a lista de docentes.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/ensino_rede/coordenador_unidade/form.jsp</li>
	 *	</ul>
	 * @throws ArqException 
	 * @throws NegocioException 
	 */
	public String voltar() throws ArqException, NegocioException  {
		if(!isAcessoCoordenadorGeral())
			return cancelar();
		if (acao == Acao.ALTERAR_COORDENADOR  || acao == Acao.INATIVAR_COORDENADOR ){	
			buscarCoordenacao();
			return forward(LISTA_COORDENADORES);
		}
		if ( acao == Acao.ALTERAR_SECRETARIA || acao == Acao.INATIVAR_SECERTARIA){
			buscarCoordenacao();
			return forward(LISTA_SECRETARIOS);
		}
		return cancelar();
	}

	/**
	 * Redireciona para a tela de alteração de Coordenador da Unidade
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/ensino_rede/coordenador_unidade/menu_coordenador_rede.jsp</li>
	 *	</ul>
	 * @throws ArqException 
	 */
	public void atualizarCoordenacao() throws ArqException{
		obj.getCargo().setId(CargoAcademico.COORDENACAO);
		alterarCoordenador();
	}
	
	/**
	 * Redireciona para a tela de alteração de Coordenador da Unidade
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/ensino_rede/coordenador_unidade/menu_coordenador_rede.jsp</li>
	 *	</ul>
	 * @throws ArqException 
	 */
	public void atualizarViceCoordenacao() throws ArqException{
		obj.getCargo().setId(CargoAcademico.VICE_COORDENACAO);
		alterarCoordenador();
	}
	
	/**
	 * Redireciona para a tela de alteração de Coordenador da Unidade
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/ensino_rede/coordenador_unidade/lista_coordenadores.jsp</li>
	 *	</ul>
	 */
	public void alterarCoordenador() throws ArqException {
		CoordenadorUnidadeDao dao = getDAO(CoordenadorUnidadeDao.class);		
		Integer idCoordenador = getParameterInt("idMembroCoordenacao");
		if(idCoordenador != null)
		 obj = dao.findByPrimaryKey(idCoordenador, CoordenadorUnidade.class);
		else if(obj.getCargo() != null && getCampusIes() != null)
			obj = dao.findMembroCoordenacaoAtivoBycargo(getProgramaRede(), getCampusIes().getInstituicao().getId(), getCampusIes().getId(), obj.getCargo().getId());			
		
		if(obj == null){
			addMensagemErro("Esta Unidade não possui cordenador(a) ativo.");
			cancelar();
			return;
		}
			
		if(!obj.isAtivo()){
			addMensagemErro("Atenção! Esta operação foi concluída anteriormente. Por favor, reinicie o processo.");
			cancelar();
		}
		prepareMovimento(SigaaListaComando.ALTERAR_COORDENADOR_UNIDADE);
		setOperacaoAtiva(SigaaListaComando.ALTERAR_COORDENADOR_UNIDADE.getId());
		acao = Acao.ALTERAR_COORDENADOR;
		setConfirmButton("Alterar");
		forward(FORM_COORDENACAO);
	}
	
	/**
	 * Redireciona para a tela de alteração de Secretario(a) da Unidade
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/ensino_rede/coordenador_unidade/lista_coordenadores.jsp</li>
	 *	</ul>
	 */
	public String alterarSecretaria() throws ArqException {
		CoordenadorUnidadeDao dao = getDAO(CoordenadorUnidadeDao.class);
		
		Integer id = getParameterInt("idMembroCoordenacao");
		if(id == null){
			
			CoordenadorUnidade secretario = null;
			if(getCampusIes() != null)
				secretario = dao.findMembroCoordenacaoAtivoBycargo(getProgramaRede(),getCampusIes().getInstituicao().getId(),getCampusIes().getId(),CargoAcademico.SECRETARIA);
			if(secretario != null)
				obj = dao.findByPrimaryKey(secretario.getId(), CoordenadorUnidade.class);
			else{
				addMensagemErro("Unidade não possui secretário(a) cadastrado(a).");
				return null;
			}
			setCampus(getCampusIes());
		}else
			obj = dao.findByPrimaryKey(id, CoordenadorUnidade.class);

		if(!obj.isAtivo()){
			addMensagemErro("Atenção! Esta operação foi concluída anteriormente. Por favor, reinicie o processo.");
			return cancelar();
		}
		prepareMovimento(SigaaListaComando.CADASTRO_SECRETARIO_UNIDADE);
		setOperacaoAtiva(SigaaListaComando.CADASTRO_SECRETARIO_UNIDADE.getId());
		acao = Acao.ALTERAR_SECRETARIA;
		setConfirmButton("Alterar");
		return forward(FORM_COORDENACAO);
	}

	/**
	 * Redireciona para a tela de alteração de Secretario(a) da Unidade
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/ensino_rede/coordenador_unidade/lista_coordenadores.jsp</li>
	 *  	<li>sigaa.war/ensino_rede/coordenador_unidade/lista_secretarios.jsp</li>
	 *	</ul>
	 * @throws DAOException 
	 */
	public String viewCoordenacao() throws DAOException {
		CoordenadorUnidadeDao dao = getDAO(CoordenadorUnidadeDao.class);
		
		Integer id = getParameterInt("idMembroCoordenacao");
		if(id != null)
			obj = dao.findByPrimaryKey(id, CoordenadorUnidade.class);
		
		return forward(VIEW_COORDENACAO);
	}
	
	/**
	 * Remove um Coordenador de Unidade
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/ensino_rede/coordenador_unidade/lista_coordenadores.jsp</li>
	 *	</ul>
	 * @throws NegocioException 
	 */
	public String inativarMembroCoordenacao() throws ArqException, NegocioException {		
		if(acao == Acao.ALTERAR_SECRETARIA)
			acao = Acao.INATIVAR_SECERTARIA;
		if(acao == Acao.ALTERAR_COORDENADOR)
			acao = Acao.INATIVAR_COORDENADOR;
		
		Integer id = getParameterInt("idMembroCoordenacao");
		if(id != null)
			obj = getDAO(CoordenadorUnidadeDao.class).findByPrimaryKey(id, CoordenadorUnidade.class);
		if(obj != null && !obj.isAtivo()){
			addMensagemErro("Atenção! Esta operação foi concluída anteriormente. Por favor, reinicie o processo.");
			return cancelar();
		}
		for (Iterator<CoordenadorUnidade> it = getResultadosBusca().iterator(); it.hasNext(); ) {
			CoordenadorUnidade coordenador = it.next();
			if (coordenador.getId() == obj.getId()){
				it.remove();
				break;
			}
		}
		prepareMovimento(SigaaListaComando.INATIVAR_COORDENADOR_UNIDADE);
		setOperacaoAtiva(SigaaListaComando.INATIVAR_COORDENADOR_UNIDADE.getId());
		return cadastrar();
	}
	
	/**
	 * Inicia a seleção de  Docente para o cadastro de Coordenador da Unidade
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>Método não invocado por JSP.</li>
	 *	</ul>
	 * @throws NegocioException 
	 */
	public String listarDocentes() throws DAOException, NegocioException {
		SelecionaDocenteRedeMBean mBean = getMBean("selecionaDocenteRedeMBean");
		mBean.setRequisitor(this);
		mBean.setCampus(obj.getDadosCurso().getCampus());
		mBean.setModoBusca(ModoBusca.SIMPLES);
		mBean.setExibirComboCampus(true);
		mBean.setConsultar(false);
		return mBean.executar();
	}
	
	/**
	 * Retorna uma lista com os cargos que o usuário irá exercer na coordenação.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/ensino_rede/coordenador_unidade/form.jsp</li>
	 *	</ul>
	 */
	public Collection<SelectItem> getAllCargoAcademico(){
		ArrayList<SelectItem> cargos = new ArrayList<SelectItem>();
		cargos.add(new SelectItem(CargoAcademico.COORDENACAO, "COORDENAÇÃO"));
		cargos.add(new SelectItem(CargoAcademico.VICE_COORDENACAO, "VICE-COORDENAÇÃO"));
		if(acao == Acao.CADASTRAR_SECRETARIA || acao == Acao.ALTERAR_SECRETARIA)
			cargos.add(new SelectItem(CargoAcademico.SECRETARIA,"SECRETARIA"));
		return cargos;
	}
	
	/**
	 * Permite um gestor de Ensino Rede, logar com outro usuário, desde que o mesmo seja um
	 * Coordenador de uma unidade de Ensino Rede.
	 * 
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/ensino_rede/coordenador_unidade/lista_coordenadores.jsp</li>
	 *	</ul>
	 * 
	 */
	public String logarComo() throws ArqException, RemoteException{		
		String login = null;
		UsuarioMBean mBean = getMBean("userBean");

		if (getParameter("login") == null|| getParameter("login").equals("")) {
			addMensagemErro((isCadastroSecretaria()?"Secretário(a)":"Coordenador(a)")+" não possui usuário cadastrado.");
			return null;
		
		} else {
			login = getParameter("login");
		
			mBean.getObj().setLogin( login );
			return mBean.logarComo();
		}
	}
	
	@Override
	public void setDocenteRede(DocenteRede docenteRede) throws ArqException {
		obj.setPessoa(docenteRede.getPessoa());
	}
	
	@Override
	public String selecionaDocenteRede() throws ArqException {
		setConfirmButton("Cadastrar");
		return forward(FORM_COORDENACAO);
	}	
		
	public Acao getAcao() {
		return acao;
	}

	public void setAcao(Acao acao) {
		this.acao = acao;
	}

	public int getIdInstituicao() {
		return idInstituicao;
	}

	public void setIdInstituicao(int idInstituicao) {
		this.idInstituicao = idInstituicao;
	}

	public Telefone getTelefone() {
		return telefone;
	}

	public void setTelefone(Telefone telefone) {
		this.telefone = telefone;
	}
}
