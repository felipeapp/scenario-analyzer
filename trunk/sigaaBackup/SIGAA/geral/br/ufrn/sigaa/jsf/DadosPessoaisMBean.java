/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '27/02/2008'
 *
 */
package br.ufrn.sigaa.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;
import static br.ufrn.arq.util.ValidatorUtil.validateRequired;

import java.util.ArrayList;
import java.util.Collection;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Comando;
import br.ufrn.arq.dominio.ConstantesParametroGeral;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.erros.UFRNException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.Formatador;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.arq.util.ValidadorCPFCNPJ;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.comum.dominio.TipoNecessidadeEspecial;
import br.ufrn.sigaa.arq.dao.MunicipioDao;
import br.ufrn.sigaa.arq.dao.PessoaDao;
import br.ufrn.sigaa.arq.dao.ensino.DocenteExternoDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.PessoaMov;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.UnidadeFederativa;
import br.ufrn.sigaa.ensino_rede.academico.jsf.CadastroDiscenteRedeMBean;
import br.ufrn.sigaa.mensagens.MensagensGerais;
import br.ufrn.sigaa.parametros.dominio.MensagensStrictoSensu;
import br.ufrn.sigaa.parametros.dominio.ParametrosGraduacao;
import br.ufrn.sigaa.pessoa.dominio.Banco;
import br.ufrn.sigaa.pessoa.dominio.Endereco;
import br.ufrn.sigaa.pessoa.dominio.EstadoCivil;
import br.ufrn.sigaa.pessoa.dominio.Identidade;
import br.ufrn.sigaa.pessoa.dominio.Municipio;
import br.ufrn.sigaa.pessoa.dominio.Pais;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;
import br.ufrn.sigaa.pessoa.dominio.TipoLogradouro;
import br.ufrn.sigaa.pessoa.dominio.TipoRaca;
import br.ufrn.sigaa.pessoa.dominio.TituloEleitor;

/**
 * MBean genérico para ser usado em algum processo de cadastramento que envolva
 * dados pessoais
 *
 * @author Andre M Dantas
 *
 */
@Component("dadosPessoais") @Scope("session")
public class DadosPessoaisMBean extends SigaaAbstractController<Pessoa> implements OperadorDadosPessoais {

	/** Formulário para digitação dos dados pessoais. */
	public static final String JSP_FORM = "/geral/pessoa/dados_pessoais.jsp";

	/**
	 * Código da operação a ser realizada (Deve ser passado como parâmetro ou
	 * definido no Bean)
	 */
	private int codigoOperacao;

	/** Operação ativa */
	private OperacaoDadosPessoais operacao;

	/** Valor do botão submit no formulário. */
	private String submitButton = "Próximo Passo >>";

	/** Valor do botão back no formulário. */
	private String backButton;

	/** Lista de SelectItem para escolha do município de naturalidade. */
	private Collection<SelectItem> municipiosNaturalidade = new ArrayList<SelectItem>(0);

	/** Lista de SelectItem para escolha do município do endereço. */
	private Collection<SelectItem> municipiosEndereco = new ArrayList<SelectItem>(0);

	/** Mensagem de erro de validação do CPF. */
	private String erroCPF;

	/** Indica se deve exibir o painel para digitação do CPF. */
	private boolean exibirPainel;

	/** Indica se é permitido alterar o CPF no formulário. */
	private boolean passivelAlterarCpf = true;
	
	/** Cpf para busca */
	private String cpfBusca;
	
	/** Indica a ordem dos botões na visualização no momento do cadastro e alteração.
	 * Cadastro(FALSE): "Cancelar" "Continuar >>"
	 * Alteração(TRUE): "Confirmar" "<< Voltar" "Cancelar"
	 * **/
	private boolean ordemBotoes = true;

	/**
	 * Indica se o pais selecionado é o brasil.
	 * Se for false não é pra renderizar estado e nem município.
	 */
	private boolean brasil = true;

	/** Indica se o usuário poderá alterar a identidade. */
	private boolean permiteAlterarIdentidade;

	/** Indica se o usuário poderá alterar o nome. */
	private boolean permiteAlterarNome;

	/** Construtor padrão. Inicializa os atributos chamando o método {@link #initObj()}.
	 * 
	 */
	public DadosPessoaisMBean() {
		initObj();
	}

	/** Inicializa os atributos do controller.
 	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>Não Invocado por JSP.</li>
	 * </ul>
	 */
	public void initObj() {
		obj = new Pessoa();
		erroCPF = null;
		cpfBusca = "";
		obj.setTipoRedeEnsino(null);
		obj.getIdentidade().setUnidadeFederativa(new UnidadeFederativa(UnidadeFederativa.ID_UF_PADRAO));
		submitButton = "Próximo Passo >>";
		backButton = "";
		ordemBotoes = true;
		if ( obj.getTituloEleitor() == null) {
			obj.setTituloEleitor(new TituloEleitor());
			obj.getTituloEleitor().setUnidadeFederativa(new UnidadeFederativa(UnidadeFederativa.ID_UF_PADRAO));
		} else if (obj.getTituloEleitor().getUnidadeFederativa() == null) {
			obj.getTituloEleitor().setUnidadeFederativa(new UnidadeFederativa(UnidadeFederativa.ID_UF_PADRAO));
		}
		if (isEmpty(obj.getTipoNecessidadeEspecial()))
			obj.setTipoNecessidadeEspecial(new TipoNecessidadeEspecial());
		// quando zera o objeto pessoa, exibe novamente o pop-up do cpf
		this.exibirPainel = true;
		this.permiteAlterarIdentidade = true;
		this.permiteAlterarNome = true;
		setResultadosBusca(new ArrayList<Pessoa>());

	}


	/** Indica se deve exibir o painel para digitação do CPF. 
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>Não Invocado por JSP.</li>
	 * </ul>
	 * @return
	 */
	public boolean isExibirPainel() {
		return exibirPainel;
	}

	/**  
	 * Seta o valor de exibição do painel.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>Não Invocado por JSP.</li>
	 * </ul>
	 * @param exibirPainel
	 */
	public void setExibirPainel(boolean exibirPainel) {
		this.exibirPainel = exibirPainel;
	}

	/** Retorna a mensagem de erro do CPF.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>Não Invocado por JSP.</li>
	 * </ul>
	 * @return
	 */
	public String getErroCPF() {
		return erroCPF;
	}

	/** Seta a mensagem de erro do CPF.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>Não Invocado por JSP.</li>
	 * </ul>
	 * @param erroCPF
	 */
	public void setErroCPF(String erroCPF) {
		this.erroCPF = erroCPF;
	}

	/** Retorna a lista para escolha do município do endereço.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>Não Invocado por JSP.</li>
	 * </ul>
	 * @return
	 */
	public Collection<SelectItem> getMunicipiosEndereco() {
		return municipiosEndereco;
	}

	/** Seta a lista de escolha do município do endereço.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>Não Invocado por JSP.</li>
	 * </ul>
	 * @param municipiosEndereco
	 */
	public void setMunicipiosEndereco(Collection<SelectItem> municipiosEndereco) {
		this.municipiosEndereco = municipiosEndereco;
	}

	/** Retorna a lista de escolha do município de naturalidade.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>Não Invocado por JSP.</li>
	 * </ul>
	 * @return
	 */
	public Collection<SelectItem> getMunicipiosNaturalidade() {
		return municipiosNaturalidade;
	}

	/** Seta a lista de escolha do município de naturalidade.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>Não Invocado por JSP.</li>
	 * </ul>
	 * @param municipiosNaturalidade
	 */
	public void setMunicipiosNaturalidade(Collection<SelectItem> municipiosNaturalidade) {
		this.municipiosNaturalidade = municipiosNaturalidade;
	}

	/** Retorna o valor do botão back.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>/SIGAA/app/sigaa.ear/sigaa.war/geral/pessoa/dados_pessoais.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public String getBackButton() {
		return backButton;
	}

	/** Seta o valor do botão back.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>Não Invocado por JSP.</li>
	 * </ul>
	 * @param backButton
	 */
	public void setBackButton(String backButton) {
		this.backButton = backButton;
	}

	/** Retorna o valor do botão submit.
	 * 
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>/SIGAA/app/sigaa.ear/sigaa.war/geral/pessoa/dados_pessoais.jsp</li>
	 * </ul>
	 * @return
	 */
	public String getSubmitButton() {
		return submitButton;
	}

	/** Seta o valor do botão submit.
	 * 
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>/SIGAA/app/sigaa.ear/sigaa.war/geral/pessoa/dados_pessoais.jsp</li>
	 * </ul>
	 * 
	 * @param submitButton
	 */
	public void setSubmitButton(String submitButton) {
		this.submitButton = submitButton;
	}

	/** Retorna o código da operação.
	 * 
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>Não Invocado por JSP.</li>
	 * </ul>
	 * 
	 * @return
	 */
	public int getCodigoOperacao() {
		return codigoOperacao;
	}

	/** Seta o código da operação.
	 * 
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>Não Invocado por JSP.</li>
	 * </ul>
	 * 
	 * @param codigoOperacao
	 */
	public void setCodigoOperacao(int codigoOperacao) {
		this.codigoOperacao = codigoOperacao;
	}

	/** Retorna o objeto OperacaoDadosPessoais.
	 * 
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>Não Invocado por JSP.</li>
	 * </ul>
	 * @return
	 */
	public OperacaoDadosPessoais getOperacao() {
		return operacao;
	}

	/** Seta o objeto OperacaoDadosPessoais.
	 * 
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>Não Invocado por JSP.</li>
	 * </ul>
	 * 
	 * @param operacao
	 */
	public void setOperacao(OperacaoDadosPessoais operacao) {
		this.operacao = operacao;
	}

	/** Informa se deve exibir o botão voltar.
	 * 
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>/SIGAA/app/sigaa.ear/sigaa.war/geral/pessoa/dados_pessoais.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public boolean getExibirVoltar() {
		return backButton != null && !backButton.equals("");
	}

	/**
	 * Exibe Formulário de dados pessoais.
 	 * 
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>Método não Invocado por JSP.</li>
	 * </ul>
	 * 
	 * @return {@value #JSP_FORM}
	 * @throws SegurancaException
	 * @throws DAOException
	 */
	public String popular() throws SegurancaException, DAOException {
		return popular(true);
	}
	
	/** Exibe Formulário de dados pessoais, podendo ou não ler do banco de dados as informações da pessoa antes de exibil-as no formulário. 
 	 * 
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>Método não Invocado por JSP.</li>
	 * </ul>
	 * 
	 * @param leDadosBanco
	 * @return
	 * @throws SegurancaException
	 * @throws DAOException
	 */
	public String popular(boolean leDadosBanco) throws SegurancaException, DAOException {
		operacao = OperacaoDadosPessoais.getOperacao(codigoOperacao);
		exibirPainel = (obj.getId() == 0);

		if ( leDadosBanco && obj.getId() != 0 ) {
			obj = getDAO(PessoaDao.class).findCompleto(obj.getId());
			setBackButton("<< Voltar");
		}

		obj.prepararDados();
		popularMudancaPais(obj.getPais().getId());
		
		return forward(JSP_FORM);
	}
	
	/**
	 * Volta para a página de busca dos discentes.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>/sigaa.war/geral/pessoa/dados_pessoais.jsp</li>
	 *		<li>/sigaa.war/graduacao/discente/form.jsp</li>
	 *	</ul>
	 * @return
	 */
	public String voltar(){
		switch (codigoOperacao) {
			case OperacaoDadosPessoais.DOCENTE_EXTERNO:
				return forward("/administracao/docente_externo/lista.jsp");
			case OperacaoDadosPessoais.DISCENTE_GRADUACAO :
			case OperacaoDadosPessoais.DISCENTE_LATO :
				return forward("/lato/discente/buscar.jsp");
			case OperacaoDadosPessoais.DISCENTE_STRICTO :
			case OperacaoDadosPessoais.DISCENTE_RESIDENCIA :
			case OperacaoDadosPessoais.DISCENTE_TECNICO :
				return forward("/WEB-INF/jsp/ensino/discente/lista.jsp");
			case OperacaoDadosPessoais.ALTERACAO_DADOS_DISCENTE :
				return forward("/graduacao/busca_discente.jsp");
			case OperacaoDadosPessoais.ALTERACAO_DADOS_DOCENTE_REDE :
				return forward("/ensino_rede/docente_rede/lista.jsp");
			case OperacaoDadosPessoais.ALTERAR_DADOS_DISCENTE_REDE:
				return forward("/ensino_rede/discente/lista.jsp");
			default :
				return forward("/ead/pessoa/lista.jsp");
		}
	}

	/** Retorna ao formulário de dados pessoais. 
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/graduacao/discente/form.jsp</li>
	 *	</ul>
	 * @return {@value #JSP_FORM}
	 * @throws DAOException 
	 */
	public String voltarDadosPessoais() throws DAOException, SegurancaException {
		obj.prepararDados();
		carregarMunicipiosEndereco(obj.getEnderecoContato().getUnidadeFederativa().getId());
		carregarMunicipios();
		return popular();
	}

	/** Submete o CPF digitado no painel. O MBean realiza uma busca por pessoas já cadastradas
	 * que possuam o mesmo CPF e retorna os dados para o formulário.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/geral/pessoa/dados_pessoais.jsp</li>
	 *	</ul>
	 * @param event
	 * @throws ArqException
	 */
	public void submeterCPF(ActionEvent event) throws ArqException {
		try {
			ListaMensagens erros = new ListaMensagens();
			if (!obj.isInternacional())
				ValidatorUtil.validateRequired(obj.getCpf_cnpj(), "CPF", erros);
			if ( obj.getCpf_cnpj() != null && obj.getCpf_cnpj() > 0 )
				ValidatorUtil.validateCPF_CNPJ(obj.getCpf_cnpj(), "CPF", erros);
			if (!erros.isEmpty()) {
				erroCPF = "Por favor, informe um CPF válido";
				return;
			}
			if ( obj.getCpf_cnpj() != null ) {
				PessoaDao dao = getDAO(PessoaDao.class);
				Pessoa pessoa = dao.findByCpf(obj.getCpf_cnpj());
				if ( pessoa != null ) {
					obj = dao.findCompleto( pessoa.getId() );
					if (codigoOperacao == OperacaoDadosPessoais.PESSOAS_EAD)
						setSubmitButton("Atualizar Dados");
					getCurrentRequest().setAttribute("msgPessoa", " ( Esse CPF já está associado a uma pessoa. ) ");
					prepareMovimento(SigaaListaComando.ALTERAR_PESSOA);
					if (obj.getMunicipio() != null && !obj.getMunicipio().isAtivo())
						addMensagem(MensagensGerais.ATUALIZE_MUNICIPIO);
				} else {
					prepareMovimento(SigaaListaComando.CADASTRAR_PESSOA);
				}
			} else {
				obj.setCpf_cnpj(null);
			}
			exibirPainel = false;
			ordemBotoes = false;
	
			getCurrentSession().setAttribute(OperadorDadosPessoais.OPERACAO_ATIVA_SESSION, SigaaListaComando.CADASTRAR_DISCENTE.getId()+"");
			// carregando selects de municípios
			carregarMunicipios();
	
			// valores default
			obj.prepararDados();
			popularMudancaPais( obj.isInternacional() ? Pais.NAO_INFORMADO : Pais.BRASIL );
			setDefaultValues();
			if (!isUserInRole(SigaaPapeis.PPG) && getCurrentSession().getAttribute("mensagemStricto") != null){
				addMensagemWarning(UFRNUtils.getMensagem(MensagensStrictoSensu.NEGAR_CADASTRO_DISCENTE_REGULAR_FORA_OP_VESTIBULAR).getMensagem());
				getCurrentSession().removeAttribute("mensagemStricto");
			}
			
		} catch (Exception e) {
			tratamentoErroPadrao(e);
			throw new ArqException(e);
		}
	}

	/** Carrega a lista de municípios de naturalidade e endereço.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/geral/pessoa/dados_pessoais.jsp</li>
	 *	</ul>
	 * @throws DAOException
	 */
	public void carregarMunicipios() throws DAOException {
		MunicipioDao dao = getDAO(MunicipioDao.class);
		int uf = UnidadeFederativa.ID_UF_PADRAO;
		
		if (obj.getUnidadeFederativa() != null && obj.getUnidadeFederativa().getId() > 0)
			uf = obj.getUnidadeFederativa().getId();
		UnidadeFederativa ufNatur = dao.findByPrimaryKey(uf, UnidadeFederativa.class);

		Collection<Municipio> municipios = dao.findByUF(uf);
		municipiosNaturalidade = new ArrayList<SelectItem>(0);
		// caso a UF seja ID = 99 (ignorado/exterior), não tem capital
		if (ufNatur.getCapital() != null)
			municipiosNaturalidade.add(new SelectItem(ufNatur.getCapital().getId(), ufNatur.getCapital().getNome()));
		municipiosNaturalidade.addAll(toSelectItems(municipios, "id", "nome"));
		obj.setUnidadeFederativa(ufNatur);
		
		if (obj.getMunicipio() == null) {
			addMensagem(MensagensGerais.ATUALIZE_MUNICIPIO);
			obj.setMunicipio(new Municipio());
		} else if( obj.getMunicipio().getId() <= 0 ){
			if (ufNatur.getCapital() != null)
				obj.setMunicipio(ufNatur.getCapital());
			else 
				obj.setMunicipio(new Municipio());
		}

		uf = UnidadeFederativa.ID_UF_PADRAO;
		
		if (obj.getEnderecoContato() != null && obj.getEnderecoContato().getUnidadeFederativa() != null && obj.getEnderecoContato().getUnidadeFederativa().getId() > 0)
			uf = obj.getEnderecoContato().getUnidadeFederativa().getId();
		UnidadeFederativa ufEnd = dao.findByPrimaryKey(uf, UnidadeFederativa.class);

		municipios = dao.findByUF(uf);
		municipiosEndereco = new ArrayList<SelectItem>(0);
		if (ufEnd.getCapital() != null)
			municipiosEndereco.add(new SelectItem(ufEnd.getCapital().getId(), ufEnd.getCapital().getNome()));
		municipiosEndereco.addAll(toSelectItems(municipios, "id", "nome"));
		if (obj.getEnderecoContato() == null) {
			obj.setEnderecoContato(new Endereco());
		}
		obj.getEnderecoContato().setUnidadeFederativa(ufEnd);
		if(isEmpty(obj.getEnderecoContato().getMunicipio()))
			obj.getEnderecoContato().setMunicipio(ufEnd.getCapital());
	}

	/** 
	 * Inicializa o objeto Pessoa com valores padrões.
	 * 
	 */
	private void setDefaultValues() {
		
		int idUFPadrao = UnidadeFederativa.ID_UF_PADRAO;
		int idMunicipioPadrao = Municipio.ID_MUNICIPIO_PADRAO;
		int cepPadrao =  ParametroHelper.getInstance().getParametroInt( ConstantesParametroGeral.CEP_PADRAO );
		int dddPadrao = ParametroHelper.getInstance().getParametroInt( ConstantesParametroGeral.DDD_PADRAO );
		
		if (obj.getSexo() != 'M' && obj.getSexo() != 'F')
			obj.setSexo('M');
		if (obj.getUnidadeFederativa() == null || obj.getUnidadeFederativa().getId() == 0)
			obj.setUnidadeFederativa(new UnidadeFederativa(idUFPadrao)); //UF
		if (obj.getMunicipio() == null || obj.getMunicipio().getId() == 0)
			obj.setMunicipio(new Municipio(idMunicipioPadrao));//MUNICIPIO
		if (obj.getEnderecoContato() == null || obj.getEnderecoContato().getId() == 0) {
			obj.setEnderecoContato(new Endereco());
			obj.getEnderecoContato().setUnidadeFederativa(new UnidadeFederativa(idUFPadrao));//UF
			obj.getEnderecoContato().setCep(cepPadrao + "");//CEP
			obj.getEnderecoContato().setTipoLogradouro(new TipoLogradouro(TipoLogradouro.RUA));
			obj.setCodigoAreaNacionalTelefoneCelular((short) dddPadrao); //DDD
			obj.setCodigoAreaNacionalTelefoneFixo((short) dddPadrao);//DDD
			obj.getEnderecoContato().setMunicipio(new Municipio(idMunicipioPadrao));//municipio
		}
		if (obj.getPais() == null || obj.getPais().getId() == 0)
			obj.setPais(new Pais(Pais.BRASIL));
		brasil = (obj.getPais() != null && obj.getPais().getId() == Pais.BRASIL);
		//|| obj.getIdentidade().getId() == 0
		if (obj.getIdentidade() == null ) {
			obj.setIdentidade(new Identidade());
			obj.getIdentidade().setUnidadeFederativa(new UnidadeFederativa(idUFPadrao));//UF
		}
		if (obj.getTipoRaca() == null)
			obj.setTipoRaca(new TipoRaca());
		if (obj.getEstadoCivil() == null)
			obj.setEstadoCivil(new EstadoCivil());
		if( obj.getTipoNecessidadeEspecial() == null )
			obj.setTipoNecessidadeEspecial(new TipoNecessidadeEspecial());
		
		if ( obj.getTituloEleitor() == null) {
			obj.setTituloEleitor(new TituloEleitor());
			obj.getTituloEleitor().setUnidadeFederativa(new UnidadeFederativa(UnidadeFederativa.ID_UF_PADRAO));
		} 
		obj.setValido(true);
		obj.setTipo(Pessoa.PESSOA_FISICA);
		obj.getPais().getNome();
		obj.getEstadoCivil().getDescricao();
		obj.getTipoRaca().getDescricao();
	}

	/** Recebe os dados do formulário, valida-os, formata-os, e, caso não tenha erros de validação,
	 *  redireciona para o MBean que faz uso deste.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/geral/pessoa/dados_pessoais.jsp</li>
	 *	</ul>
	 * @return {@link #redirecionarDadosPessoais(Pessoa)}
	 * @throws DAOException
	 */
	public String submeterDados() throws DAOException {
		// validar dados
		erros = new ListaMensagens();

		validarDados();
		formatarDados();
		
		if (hasErrors())
			return null;
		
		//populando conta bancária
		GenericDAO dao = getGenericDAO();
		obj.getContaBancaria().setBanco(dao.findByPrimaryKey(obj.getContaBancaria().getBanco().getId(), Banco.class));
		//populando municípios de naturalidade e do endereço de contato
		if ( obj.getMunicipio() != null )
			obj.setMunicipio(dao.findAndFetch(obj.getMunicipio().getId(), Municipio.class, "unidadeFederativa"));
		if ( obj.getEnderecoContato() != null && obj.getEnderecoContato().getMunicipio() != null )
			obj.getEnderecoContato().setMunicipio(dao.findByPrimaryKey(obj.getEnderecoContato().getMunicipio().getId(), Municipio.class));
		
		return redirecionarDadosPessoais(obj);
	}

	/** Formata os dados pessoais, realizando operações de trim() e upperCase() em nome, nomeMae, nomePai, endereço;
	 *  e configura se a pessoa é estrangeira (anula o município e a UF).
	 * 
	 */
	private void formatarDados() {
		// Remove espaços em brancos entre as palavras.
		if (obj.getNome() != null) {
			String nome = "";
			String[] nomePartes = obj.getNome().split(" ");
			for (int i = 0; nomePartes.length -1 >= i; i++){
				if (!nomePartes[i].trim().isEmpty())
					if (i == nomePartes.length - 1)
						nome += nomePartes[i].trim();
					else
						nome += nomePartes[i].trim()+" ";
			}					
			obj.setNome(nome);			
		}
		if (obj.getNome() != null) {
			obj.setNome( obj.getNome().toUpperCase().trim() );
		}
		if (obj.getNomeMae() != null) {
			obj.setNomeMae( obj.getNomeMae().toUpperCase().trim() );
		}
		if (obj.getNomePai() != null) {
			obj.setNomePai( obj.getNomePai().toUpperCase().trim() );
		}
		if (obj.getEnderecoContato() != null && obj.getEnderecoContato().getLogradouro() != null) {
			obj.getEnderecoContato().setLogradouro( obj.getEnderecoContato().getLogradouro().toUpperCase().trim() );
		}
		if (obj.getEnderecoContato() != null && obj.getEnderecoContato().getBairro() != null) {
			obj.getEnderecoContato().setBairro( obj.getEnderecoContato().getBairro().toUpperCase().trim() );
		}

		if (obj.getPais().getId() != Pais.BRASIL && obj.getPais().getId() != Pais.NAO_INFORMADO) {
			obj.setInternacional(true);
			obj.setMunicipio(null);
			obj.setUnidadeFederativa(null);
		}
	}

	/** Valida os dados digitados. A validação é feita sobre o nome, CPF, passaporte (caso extrangeiro),
	 * sexo, nomeMae, nomePai, dataNascimento, email.
	 * @throws DAOException
	 */
	private void validarDados() throws DAOException {
		// Validações de CPF
		if ( deveValidarCpf() ) {
			ValidatorUtil.validateRequired(obj.getCpf_cnpj(), "CPF", erros);
			if( obj.getCpf_cnpj() != null) {
				ValidatorUtil.validateCPF_CNPJ(obj.getCpf_cnpj(), "CPF", erros);
				PessoaDao dao = getDAO(PessoaDao.class);
				Integer id = dao.findIdByCpf(obj.getCpf_cnpj());
				if (id > 0 && id != obj.getId())
					erros.addErro("Já existe outro cadastro com este CPF.");
			}
		}

		ValidatorUtil.validateRequired(obj.getNome(), "Nome", erros);
		ValidatorUtil.validateAbreviacao(obj.getNome(), "Nome", erros);
		ValidatorUtil.validateRequired(obj.getNomeOficial(), "Nome Oficial", erros);
		ValidatorUtil.validateAbreviacao(obj.getNomeOficial(), "Nome Oficial", erros);

		if( obj.isInternacional() ) {
			validateRequired(obj.getPassaporte(), "Passaporte", erros);
			if( obj.getPais().getId() > 0 && obj.getPais().getId() == Pais.BRASIL )
				addMensagem( MensagensGerais.DISCENTE_ESTRANGEIRO_PAIS_BRASIL );
		}

		ValidatorUtil.validateRequired(obj.getSexo(), "Sexo", erros);
		ValidatorUtil.validateRequired(obj.getNomeMae(), "Nome da Mãe", erros);
		ValidatorUtil.validateAbreviacao(obj.getNomeMae(), "Nome da Mãe", erros);
		ValidatorUtil.validateAbreviacao(obj.getNomePai(), "Nome do Pai", erros);
		ValidatorUtil.validateRequired(Formatador.getInstance().formatarData(obj.getDataNascimento()), "Data de Nascimento", erros);
		ValidatorUtil.validateBirthday(obj.getDataNascimento(), "Data de Nascimento", erros);
		
		if ( !isEmpty(obj.getIdentidade()) ){
			/** Verifica se a data de expedição do rg é inferior a data de nascimento */
			if ( !isEmpty(obj.getDataNascimento()) && !isEmpty(obj.getIdentidade().getDataExpedicao())
					&& obj.getIdentidade().getDataExpedicao().before(obj.getDataNascimento())) {
				erros.addMensagem(MensagensArquitetura.DATA_POSTERIOR_A, 
					"Data de Expedição do RG", CalendarUtils.format(obj.getDataNascimento(), "dd/MM/yyyy"));
			}
		}

		ValidatorUtil.validateRG(obj.getIdentidade().getNumero(), "RG", erros);
		
		ValidatorUtil.validateCEP(obj.getEnderecoContato().getCep(), "CEP", erros);
		ValidatorUtil.validateTelefone(obj.getTelefone(), "Tel. Fixo", erros);
		ValidatorUtil.validateTelefone(obj.getCelular(), "Tel. Celular", erros);
		ValidatorUtil.validateEmail(obj.getEmail(), "E-Mail", erros);

		if( !isEmpty( obj.getAnoConclusaoEnsinoMedio() ) && !StringUtils.isNumeric(obj.getAnoConclusaoEnsinoMedio() ) )
				erros.addMensagem(MensagensArquitetura.FORMATO_INVALIDO, "Ano de Conclusão");
	}

	/**
	 * Verificar se o cpf deve ser validado.
	 * As regras foram definidas separadamente para facilitar a
	 * compreensão.
	 *
	 * @return
	 * @throws DAOException
	 */
	private boolean deveValidarCpf() throws DAOException {

		// CPFs de estrangeiros são opcionais
		if (obj.isInternacional() && isEmpty(obj.getCpf_cnpj()) ) {
			return false;
		}

		// Se for um novo cadastro
		if ( obj.getId() == 0 ) {
			return true;
		}

		// Se a pessoa não possui um cpf cadastrado.+
		Long cpfCadastrado = getGenericDAO().refresh(obj).getCpf_cnpj();
		if (cpfCadastrado == null) {
			return true;
		}

		// Se a pessoa possui um cpf cadastrado inválido e o novo cpf é diferente dele
		if ( !ValidadorCPFCNPJ.getInstance().validaCpfCNPJ(cpfCadastrado) && !cpfCadastrado.equals(obj.getCpf_cnpj()) ) {
			return true;
		}

		return true;
	}

	/** Redireciona para o MBean que chamou este controller.
	 * @param pessoa
	 * @return 
	 */
	private String redirecionarDadosPessoais(Pessoa pessoa) {
		// Redirecionar para a operação especificada
		operacao = OperacaoDadosPessoais.getOperacao(codigoOperacao);
		OperadorDadosPessoais mBean = (OperadorDadosPessoais) getMBean(operacao.getMBean());
		mBean.setDadosPessoais(pessoa);
		return mBean.submeterDadosPessoais();
	}

	/** Listener responsável por carregar a lista de municípios de naturalidade ou endereço, caso o valor
	 * da UF seja alterado no formulário.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/geral/pessoa/dados_pessoais.jsp</li>
	 *	</ul>
	 * @param e
	 * @throws DAOException
	 */
	public void carregarMunicipios(ValueChangeEvent e) throws DAOException {
		String selectId = e.getComponent().getId();
		if (selectId != null && e.getNewValue() != null) {
			Integer ufId = (Integer) e.getNewValue();
			if (selectId.toLowerCase().contains("natur")) {
				carregarMunicipiosNaturalidade(ufId);
			} else if (selectId.toLowerCase().contains("end")) {
				carregarMunicipiosEndereco(ufId);
			}
		}
	}

	/** Carrega a lista de municípios de naturalidade de uma determinada UF.
 	 * 
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>Método não Invocado por JSP.</li>
	 * </ul>
	 * 
	 * @param idUf
	 * @throws DAOException
	 */
	public void carregarMunicipiosNaturalidade(Integer idUf) throws DAOException {
		if ( idUf == null ) {
			idUf = obj.getUnidadeFederativa().getId();
		}

		MunicipioDao dao = getDAO(MunicipioDao.class);
		UnidadeFederativa uf = dao.findByPrimaryKey(idUf, UnidadeFederativa.class);
		obj.setUnidadeFederativa(uf);
		Collection<Municipio> municipios = dao.findByUF(idUf);
		municipiosNaturalidade = new ArrayList<SelectItem>(0);
		if (uf != null && uf.getCapital() != null) {
			municipiosNaturalidade.add(new SelectItem(uf.getCapital().getId(), uf.getCapital().getNome()));
			municipiosNaturalidade.addAll(toSelectItems(municipios, "id", "nome"));
			obj.setMunicipio(uf.getCapital());
		}
	}

	/**  Carrega a lista de municípios de endereço de uma determinada UF.
	 * <br><br>
	 * Método não invocado por JSP
	 * @param idUf
	 * @throws DAOException
	 */
	public void carregarMunicipiosEndereco(Integer idUf) throws DAOException {
		int idUFPadrao = UnidadeFederativa.ID_UF_PADRAO;
		
		if ( idUf == null || idUf == 0) 
			idUf = obj.getEnderecoContato().getUnidadeFederativa().getId() == 0
			? idUFPadrao : obj.getEnderecoContato().getUnidadeFederativa().getId();

		MunicipioDao dao = getDAO(MunicipioDao.class);
		UnidadeFederativa uf = dao.findByPrimaryKey(idUf, UnidadeFederativa.class);
		Collection<Municipio> municipios = dao.findByUF(idUf);
		obj.getEnderecoContato().setUnidadeFederativa(uf);
		municipiosEndereco = new ArrayList<SelectItem>(0);
		municipiosEndereco.add(new SelectItem(uf.getCapital().getId(), uf.getCapital().getNome()));
		municipiosEndereco.addAll(toSelectItems(municipios, "id", "nome"));

		obj.getEnderecoContato().setMunicipio(uf.getCapital());
	}

	/**
	 * Caso este controller seja utilizado diretamente para cadastro, seta o valor do objeto Pessoa.
	 * Se este controller estiver servindo como primeiro passo de um outro cadastro, o MBean que o chama
	 * deve implementar esse método com o comportamento adequado ao seu caso de uso.
	 * 
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>Método não Invocado por JSP.</li>
	 * </ul>
	 */
	public void setDadosPessoais(Pessoa pessoa) {
		obj = pessoa;
	}

	/** Caso este controller seja utilizando diretamente para cadastro, captura os dados pessoais do formulário e cadastra.
	 * Se este controller estiver servindo como primeiro passo de um outro cadastro, o MBean que o chama
	 * deve implementar esse método com o comportamento adequado ao seu caso de uso.
	 * 
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>Método não Invocado por JSP.</li>
	 * </ul>
	 * 
	 */
	public String submeterDadosPessoais() {
		try {
			return cadastrar();
		} catch (Exception e) {
			addMensagemErro(e.getMessage());
			return null;
		}
	}

	/** Método usado no cadastro direto de pessoa.
	 * <br/>Método não invocado por JSP´s.
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#preCadastrar()
	 */
	@Override
	public String preCadastrar() throws ArqException {
		checkChangeRole();
		int codigoOperacao = getParameterInt("codigoOperacao", 0);
		return preCadastrar(codigoOperacao);
	}

	/**
	 * Método responsável pelo redirecionamento no cadastro de pessoas.
	 * 
	 * @param codigoOperacao
	 * @return
	 * @throws SegurancaException
	 * @throws DAOException
	 */
	private String preCadastrar(int codigoOperacao) throws SegurancaException, DAOException {
	    DadosPessoaisMBean mbean = (DadosPessoaisMBean) getMBean("dadosPessoais");
	    mbean.setCodigoOperacao(codigoOperacao);
	    setSubmitButton("Cadastrar");
	    setBackButton("");
	    return mbean.popular();
	}
	
	/** Método usado no cadastro direto de pessoa.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/ead/menu.jsp</li>
	 *	</ul>
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#preCadastrar()
	 */
	public String preCadastrarEad() throws ArqException {
		getCurrentRequest().setAttribute("codigoOperacao", OperacaoDadosPessoais.PESSOAS_EAD);
		return preCadastrar(OperacaoDadosPessoais.PESSOAS_EAD);
	}

	/** Método usado no cadastro direto de pessoa.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/administracao/docente_externo/lista.jsp</li>
	 *		<li>sigaa.war/ead/pessoa/lista.jsp</li>
	 *	</ul>
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#atualizar()
	 */
	@Override
	public String atualizar() throws ArqException {
			checkChangeRole();
			
			DocenteExternoDao dao = getDAO(DocenteExternoDao.class);
			Integer id = getParameterInt("id");
			Pessoa pessoa = dao.findByPrimaryKey(id, Pessoa.class);
			if( isUserInRole(SigaaPapeis.SECRETARIA_POS, SigaaPapeis.COORDENADOR_CURSO_STRICTO, SigaaPapeis.PPG) ) {
				if (!getSubSistema().equals(SigaaSubsistemas.SEDIS)) {
					if( isEmpty(dao.findAtivosByPessoa(pessoa)) ){
						addMensagem(MensagensGerais.SOMENTE_ATUALIZAR_DADOS_PESSOAIS_DOCENTE_EXTERNO_ATIVO);
						return null;
					}
				}
			}

			exibirPainel = false;
			prepareMovimento(SigaaListaComando.ALTERAR_PESSOA);
			setOperacaoAtiva(SigaaListaComando.ALTERAR_PESSOA.getId());
			DadosPessoaisMBean mbean = (DadosPessoaisMBean) getMBean("dadosPessoais");
			int codigoOperacao = getParameterInt("codigoOperacao", 0);
			mbean.setCodigoOperacao(codigoOperacao);
			setSubmitButton("Alterar");
			mbean.setObj(pessoa);
			mbean.setExibirPainel(true);

			setDefaultValues();
			carregarMunicipios();
			return mbean.popular();
	}

	/** Método usado no cadastro direto de pessoa.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/ead/pessoa/lista.jsp</li>
	 *	</ul>
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#preRemover()
	 */
	@Override
	public String preRemover()  {
		exibirPainel = false;
		try {
			checkRole(SigaaPapeis.SEDIS,SigaaPapeis.GESTOR_LATO);
			prepareMovimento(SigaaListaComando.REMOVER_PESSOA);
			DadosPessoaisMBean mbean = (DadosPessoaisMBean) getMBean("dadosPessoais");
			mbean.setCodigoOperacao(OperacaoDadosPessoais.PESSOAS_EAD);
			setSubmitButton("Remover");
			setReadOnly(true);
			GenericDAO dao = getGenericDAO();
			Integer id = getParameterInt("id");
			mbean.setObj(dao.findByPrimaryKey(id, Pessoa.class));
			setDefaultValues();
			return mbean.popular();
		} catch (SegurancaException e) {
			addMensagem(MensagensGerais.USUARIO_NAO_AUTORIZADO);
			e.printStackTrace();
			return null;
		} catch (ArqException e) {
			e.printStackTrace();
			return tratamentoErroPadrao(e);
		}
	}

	/** Método usado no cadastro direto de pessoa.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/graduacao/discente/form.jsp</li>
	 *		<li>/sigaa.war/geral/pessoa/dados_pessoais.jsp</li>
	 *	</ul>
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#cadastrar()
	 */
	@Override
	public String cadastrar() throws NegocioException, ArqException {
		if( !permiteCoordGraduacaoAlteracaoCompleta() ){
			checkChangeRole();
		}
		
		validarDados();
		
		if(hasErrors())
			return null;
		
		Comando comando = SigaaListaComando.CADASTRAR_PESSOA;
		if ("alterar".equalsIgnoreCase(getSubmitButton()) || "atualizar dados".equalsIgnoreCase(getSubmitButton()))
			comando = SigaaListaComando.ALTERAR_PESSOA;
		else if ("remover".equalsIgnoreCase(getSubmitButton()))
			comando = SigaaListaComando.REMOVER_PESSOA;

		prepareMovimento(comando);
		PessoaMov mov = new PessoaMov();
		mov.setCodMovimento(comando);
		mov.setPessoa(obj);
		try {
			executeWithoutClosingSession(mov, getCurrentRequest());
		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens());
			return null;
		} catch (Exception e) {
			addMensagemErro("Erro Inesperado: " + e.getMessage());
			e.printStackTrace();
			return null;
		}
		getCurrentSession().removeAttribute(OperadorDadosPessoais.OPERACAO_ATIVA_SESSION);
		String nome = obj.getNome();
		super.afterCadastrar();
		initObj();
		if( isUserInRole(SigaaPapeis.SECRETARIA_POS, SigaaPapeis.COORDENADOR_CURSO_STRICTO, SigaaPapeis.PPG, SigaaPapeis.GESTOR_INFANTIL, 
				SigaaPapeis.COORDENADOR_GERAL_REDE, SigaaPapeis.COORDENADOR_UNIDADE_REDE) ) {
			addMensagem(MensagensGerais.DADOS_PESSOAIS_ATUALIZADOS_SUCESSO, nome);
			return cancelar();
		}else if (comando.equals(SigaaListaComando.CADASTRAR_PESSOA)) {
			addMensagem(MensagensGerais.DADOS_PESSOAIS_CADASTRADOS_SUCESSO, nome);
			return cancelar();
		} else if (comando.equals(SigaaListaComando.ALTERAR_PESSOA)) {
			addMensagem(MensagensGerais.DADOS_PESSOAIS_ATUALIZADOS_SUCESSO, nome);
			return forward(getListPage());
		} else if (comando.equals(SigaaListaComando.REMOVER_PESSOA)) {
			addMensagem(MensagensGerais.DADOS_PESSOAIS_REMOVIDOS_SUCESSO, nome);
			return forward(getListPage());
		}
		return null;
	}

	/** Cancela a operação corrente.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/geral/pessoa/dados_pessoais.jsp</li>
	 *	</ul>
	 * @see br.ufrn.arq.web.jsf.AbstractController#cancelar()
	 */
	@Override
	public String cancelar() {
		getCurrentSession().removeAttribute(OperadorDadosPessoais.OPERACAO_ATIVA_SESSION);
		return super.cancelar();
	}
	
	/** Método usado no cadastro direto de pessoa.
	 * 
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>Método não Invocado por JSP.</li>
	 * </ul>
	 * 
	 * dadosPessoais.listPage
	 */
	@Override
	public String getListPage() {
		initObj();
		if (isLatoSensu() || isTecnico())			
			return "/administracao/docente_externo/lista.jsf";
		else if (isPortalCoordenadorEnsinoRede() || isPortalEnsinoRede()) {
			CadastroDiscenteRedeMBean mBean = getMBean("cadastroDiscenteRedeMBean");
			try {
				return mBean.iniciarAtualizarDadosPessoais();
			} catch (UFRNException e) {
				e.printStackTrace();
				notifyError(e);
			}
		}
		return "/ead/pessoa/lista.jsp";
	}

	/** Método usado no cadastro direto de pessoa.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/ead/pessoa/lista.jsp</li>
	 *	</ul>
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#buscar()
	 */
	@Override
	public String buscar() throws Exception {
		String param = getParameter("paramBusca");
		if (param == null) {
			addMensagemErro("Selecione um tipo de busca e digite o parâmetro de busca");
			return null;
		}

		PessoaDao dao = getDAO(PessoaDao.class);
		if ("nome".equalsIgnoreCase(param)){
			if (obj.getNome().trim().isEmpty()){
				addMensagemErro("Informe o Nome.");
				return null;
			}			
			setResultadosBusca(dao.findByNomeTipo(obj.getNome(), Pessoa.PESSOA_FISICA, null));
		} else if ("cpf".equalsIgnoreCase(param)){
			if (cpfBusca.trim().isEmpty()){
				addMensagemErro("Informe o CPF.");
				return null;
			}
			setResultadosBusca(dao.findByCpfCnpjTipo(Long.parseLong((StringUtils.notEmpty(cpfBusca)?  cpfBusca.replaceAll("\\D","") : cpfBusca)), Pessoa.PESSOA_FISICA, null));
		} else			
			setResultadosBusca(null);

		if((getResultadosBusca()!=null && getResultadosBusca().isEmpty()) || getResultadosBusca()==null) {
			addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
		}
		return null;
	}

	/** Listener responsável por atualizar o valor do atributo {@link #brasil}, caso o valor de país seja
	 * alterado no formulário.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/geral/pessoa/dados_pessoais.jsp</li>
	 *	</ul>
	 * @param e
	 * @throws DAOException
	 */
	public void alterarPais(ValueChangeEvent e) throws DAOException {
		Integer idPais = (Integer) e.getNewValue();
		popularMudancaPais(idPais);
	}
	
	/** 
	 * Método responsável em atualizar {@link Pessoa#setPais(Pais)} de acordo com
	 * o valor {@link Pessoa#isInternacional()}.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/geral/pessoa/dados_pessoais.jsp</li>
	 *	</ul>
	 * @param e
	 * @throws DAOException
	 */
	public void resetPais(ValueChangeEvent e) throws DAOException {
		obj.setInternacional( (Boolean) e.getNewValue() );
		popularMudancaPais( obj.isInternacional() ? Pais.NAO_INFORMADO : Pais.BRASIL );
	}

	/** Atualiza os atributos (municípios, UF, extrangeiro) correspondentes à mudança do 
	 * valor do atributo País no formulário.
	 * @throws DAOException 
	 *
	 */
	private void popularMudancaPais(Integer idPais) throws DAOException {
		
		Pais pais = getGenericDAO().findByPrimaryKey(idPais, Pais.class);
		
		if( pais != null ){
		
			brasil = pais.getId() == Pais.BRASIL;
			obj.setPaisNacionalidade(pais.getNacionalidade());
			obj.setPais(pais);
			Pessoa pessoaBanco = getGenericDAO().findByPrimaryKey(obj.getId(), Pessoa.class);
			if( !isEmpty(pessoaBanco) && idPais == pessoaBanco.getPais().getId() )
				obj.setMunicipioNaturalidadeOutro(pessoaBanco.getMunicipioNaturalidadeOutro());
			else 
				obj.setMunicipioNaturalidadeOutro(null);
		
			if (brasil && obj.getMunicipio() == null) {
				obj.setMunicipio(new Municipio());
				obj.setUnidadeFederativa( new UnidadeFederativa() );
			}
			
		}
		
	}

	/** Indica se a pessoa é do Brasil.
	 * 
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>/SIGAA/app/sigaa.ear/sigaa.war/geral/discente/dados_pessoais.jsp</li>
	 *   <li>/SIGAA/app/sigaa.ear/sigaa.war/geral/pessoa/dados_pessoais.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public boolean isBrasil() {
		return brasil;
	}

 	/** Seta o valor de Brasil.
	 * 
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>/SIGAA/app/sigaa.ear/sigaa.war/geral/discente/dados_pessoais.jsp</li>
	 *   <li>/SIGAA/app/sigaa.ear/sigaa.war/geral/pessoa/dados_pessoais.jsp</li>
	 * </ul>
	 * 
	 * @param brasil
	 */
	public void setBrasil(boolean brasil) {
		this.brasil = brasil;
	}

	/** Indica se é possível alterar o valor do CPF no formulário.
	 * 
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>/SIGAA/app/sigaa.ear/sigaa.war/geral/pessoa/dados_pessoais.jsp</li>
	 * </ul>
	 * 
	 * dadosPessoais.passivelAlterarCpf
	 * @return
	 */
	public boolean isPassivelAlterarCpf() {
		return passivelAlterarCpf;
	}

	/** Seta se é possível alterar o valor do CPF no formulário.
	 * 
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>/SIGAA/app/sigaa.ear/sigaa.war/geral/pessoa/dados_pessoais.jsp</li>
	 * </ul>
	 */
	public void setPassivelAlterarCpf(boolean passivelAlterarCpf) {
		this.passivelAlterarCpf = passivelAlterarCpf;
	}

	/** Retorna uma lista de SelectItem de tipos de necessidades especiais.
	 * 
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>/SIGAA/app/sigaa.ear/sigaa.war/geral/pessoa/dados_pessoais.jsp</li>
	 *   <li>/SIGAA/app/sigaa.ear/sigaa.war/graduacao/relatorios/form_relatorios.jsp</li>
	 * </ul>
	 */
	public Collection<SelectItem> getAllTipoNecessidadeEspecialCombo(){
	    return getAll( TipoNecessidadeEspecial.class, "id", "descricao");
	}

	public String getCpfBusca() {
		return cpfBusca;
	}

	public void setCpfBusca(String cpfBusca) {
		this.cpfBusca = cpfBusca;
	}

	public boolean isOrdemBotoes() {
		return ordemBotoes;
	}

	public void setOrdemBotoes(boolean ordemBotoes) {
		this.ordemBotoes = ordemBotoes;
	}
	
	/**
	 * Verificar se o usuário com perfil de coordenador de curso de graduação 
	 * possui permissão para alterar todos os dados pessoais do discente.
	 *
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>Método não Invocado por JSP.</li>
	 * </ul>
	 * @return
	 */
	public boolean permiteCoordGraduacaoAlteracaoCompleta(){
		return getSubSistema().equals(SigaaSubsistemas.PORTAL_COORDENADOR) && (isUserInRole(SigaaPapeis.COORDENADOR_CURSO) 
			&& ParametroHelper.getInstance().getParametroBoolean(ParametrosGraduacao.PERMITE_ALTERACAO_COMPLETA_DADOS_PESSOAIS));
	}
	/**
	 * Método responsável para verificar se a operação está sendo realizada por meio de algum módulo do ensino médio.
	 * @return
	 */
	public boolean isMedio(){
		return getNivelEnsino() == NivelEnsino.MEDIO;
	}
	
	@Override
	public void checkChangeRole() throws SegurancaException {
		
		/*
		 * *** ATENÇÃO *** O cadastro de pessoa é usado de várias partes do sistema, então evite acrescentar alguma restrição aqui.
		 * 
		 * Se for altamente necessesário, lembre-se de perguntar a todos onde esse método é usado, verificar
		 * suas ocorrência e testar, senão você estará provavelmente introduzindo um erro no sistema.
		 */
		
		checkRole(SigaaPapeis.SEDIS, SigaaPapeis.ADMINISTRADOR_DAE, SigaaPapeis.SECRETARIA_POS, SigaaPapeis.GESTOR_INFANTIL, 
				SigaaPapeis.GESTOR_LATO, SigaaPapeis.COORDENADOR_CURSO_STRICTO, SigaaPapeis.PPG, SigaaPapeis.SECRETARIA_DEPARTAMENTO,
				SigaaPapeis.GESTOR_TECNICO, SigaaPapeis.DAE, SigaaPapeis.COORDENADOR_LATO, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL, 
				SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO_BIBLIOTECARIO, SigaaPapeis.GESTOR_NEE, SigaaPapeis.SECRETARIA_LATO,
				SigaaPapeis.GESTOR_MEDIO, SigaaPapeis.COORDENADOR_MEDIO, SigaaPapeis.GESTOR_FORMACAO_COMPLEMENTAR, 
				SigaaPapeis.SECRETARIA_RESIDENCIA, SigaaPapeis.COORDENADOR_TECNICO, SigaaPapeis.GESTOR_EXTENSAO,
				SigaaPapeis.COORDENADOR_GERAL_REDE, SigaaPapeis.COORDENADOR_UNIDADE_REDE);
	}
	
	/** Indica se o usuário pode alterar o nome no cadastro de dados pessoais.
	 * @return
	 */
	public boolean isPermiteAlterarNome() {
		return permiteAlterarNome && !isReadOnly();
	}
	
	/** Indica se o usuário pode alterar a identidade no cadastro de dados pessoais.
	 * @return
	 */
	public boolean isPermiteAlterarIdentidade() {
		return permiteAlterarIdentidade && !isReadOnly();
	}

	public void setPermiteAlterarIdentidade(boolean permiteAlterarIdentidade) {
		this.permiteAlterarIdentidade = permiteAlterarIdentidade;
	}

	public void setPermiteAlterarNome(boolean permiteAlterarNome) {
		this.permiteAlterarNome = permiteAlterarNome;
	}
}
