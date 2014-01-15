/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 25/08/2009
 */
package br.ufrn.sigaa.diploma.jsf;

import static br.ufrn.arq.mensagens.MensagensArquitetura.OPERACAO_SUCESSO;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.dominio.Parametro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.parametrizacao.ParametroDao;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.PessoaDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.parametros.dominio.ParametrosGraduacao;
import br.ufrn.sigaa.parametros.dominio.ParametrosStrictoSensu;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

/** Controller responsável pela edição dos parâmetros utilizados no Registro de Diplomas.
 * @author Édipo Elder F. Melo
 *
 */
@Component("parametrosRegistroDiploma")
@Scope("request")
public class ParametrosRegistroDiplomaMBean extends SigaaAbstractController<Object> {
	
	/** Parâmetro: anoInicioRegistro. */
	private Parametro anoInicioRegistro;
	/** Parâmetro: periodoInicioRegistro. */
	private Parametro semestreInicioRegistro;
	/** Parâmetro: diretorDRED. */
	private Parametro diretorDRED;
	/** Parâmetro: diretorDAE. */
	private Parametro diretorDAE;
	/** Parâmetro: Pró-Reitor de Pós-Graduação. */
	private Parametro proReitorPosGraduacao;
	
	/** Diretor do DAE. */
	private Pessoa pessoaDiretorDAE;
	/** Diretor do DRED. */
	private Pessoa pessoaDiretorDRED;
	/** Pró-Reitor de Pós-Graduação. */
	private Pessoa pessoaProReitorPosGraduacao;
	
	/** Lista de parâmetros a editar. */
	private Collection<Parametro> listaParametros;
	
	/** Construtor padrão. */
	public ParametrosRegistroDiplomaMBean() {
	}
	
	/**
	 * Inicializa os parâmetros, a serem editados, utilizados nos registros de
	 * diplomas de cursos de graduação.
	 * 
	 * @throws DAOException
	 */
	private void initParametrosGraduacao() throws DAOException {
		ParametroDao dao = getDAO(ParametroDao.class);
		anoInicioRegistro = dao.findByPrimaryKey(ParametrosGraduacao.ANO_INICIO_REGISTRO_DIPLOMA);
		semestreInicioRegistro = dao.findByPrimaryKey(ParametrosGraduacao.SEMESTRE_INICIO_REGISTRO_DIPLOMA);
		diretorDAE = dao.findByPrimaryKey(ParametrosGraduacao.ID_PESSOA_DIRETOR_DAE);
		diretorDRED = dao.findByPrimaryKey(ParametrosGraduacao.ID_PESSOA_DIRETOR_DRED);
		// seta os diretores.
		ParametroHelper helper = ParametroHelper.getInstance();
		pessoaDiretorDAE = getPessoa(helper.getParametroInt(diretorDAE.getCodigo()));
		pessoaDiretorDRED = getPessoa(helper.getParametroInt(diretorDRED.getCodigo()));
		// lista de parâmetros;
		listaParametros = new ArrayList<Parametro>();
		listaParametros.add(anoInicioRegistro);
		listaParametros.add(semestreInicioRegistro);
		listaParametros.add(diretorDAE);
		listaParametros.add(diretorDRED);
	}
	
	/**
	 * Inicializa os parâmetros, a serem editados, utilizados nos registros de
	 * diplomas de cursos de strico sensu.
	 * 
	 * @throws DAOException
	 */
	private void initParametrosStrictoSensu() throws DAOException {
		ParametroDao dao = getDAO(ParametroDao.class);
		anoInicioRegistro = dao.findByPrimaryKey(ParametrosGraduacao.ANO_INICIO_REGISTRO_DIPLOMA);
		semestreInicioRegistro = dao.findByPrimaryKey(ParametrosGraduacao.SEMESTRE_INICIO_REGISTRO_DIPLOMA);
		proReitorPosGraduacao = dao.findByPrimaryKey(ParametrosStrictoSensu.ID_PESSOA_PRO_REITOR_POS_GRADUACAO);
		// seta os diretores.
		ParametroHelper helper = ParametroHelper.getInstance();
		pessoaProReitorPosGraduacao = getPessoa(helper.getParametroInt(proReitorPosGraduacao.getCodigo()));
		// lista de parâmetros;
		listaParametros = new ArrayList<Parametro>();
		listaParametros.add(anoInicioRegistro);
		listaParametros.add(semestreInicioRegistro);
		listaParametros.add(proReitorPosGraduacao);
	}
	
	/** Retorna uma pessoa dado o ID
	 * @param idPessoa
	 * @return
	 * @throws DAOException
	 */
	private Pessoa getPessoa(int idPessoa) throws DAOException {
		PessoaDao dao = getDAO(PessoaDao.class);
		Pessoa pessoa = dao.findByPrimaryKey(idPessoa, Pessoa.class);
		if (pessoa == null) pessoa = new Pessoa();
		return pessoa;
	}

	/**
	 * Autocomplete utilizado pelo suggestion box para setar o ID da pessoa.<br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/diplomas/parametros_registro_diploma/form.jsp</li>
	 * </ul>
	 * 
	 * @param event
	 * @return
	 * @throws DAOException
	 */
	public Collection<Pessoa> autocompleteNomePessoa(Object event) throws DAOException {
		PessoaDao dao = getDAO(PessoaDao.class);
		
		String nome = event.toString(); //Nome da pessoa digitada no autocomplete
		
		Collection<Pessoa> pessoas = dao.findByNomeTipo(nome, Pessoa.PESSOA_FISICA, null);
		return pessoas;
	}
	
	/**
	 * Lista os parâmetros de acordo com o sistema selecionado.<br>
	 * 
	 * JSP: admin.war/configuracoes/parametros/lista.jsp
	 * @throws ArqException 
	 */
	@Override
	public String listar() throws ArqException {
		return atualizar();
	}

	/**
	 * Carrega um parâmetro para que ele seja atualizado posteriormente pelo método cadastrar().<br>
	 * 
	 * JSP: admin.war/configuracoes/parametros/lista.jsp
	 */
	@Override
	public String atualizar() throws ArqException {
		if (isUserInRole(SigaaPapeis.GESTOR_DIPLOMAS_GRADUACAO))
			initParametrosGraduacao();
		if (isUserInRole(SigaaPapeis.GESTOR_DIPLOMAS_LATO, SigaaPapeis.GESTOR_DIPLOMAS_STRICTO)) 
			initParametrosStrictoSensu();
		setConfirmButton("Alterar");
		return forward(getFormPage());
	}
	
	/**
	 * Cadastra/Atualiza um parâmetro.<br>
	 * 
	 * JSP: admin.war/configuracoes/parametros/form.jsp
	 */
	@Override
	public String cadastrar() throws SegurancaException, ArqException, NegocioException {
		validacaoDados(erros);
		if (hasErrors()) {
			return null;
		}
		
		for (Parametro parametro : listaParametros) {
			prepareMovimento(SigaaListaComando.ALTERAR_PARAMETROS);
			setOperacaoAtiva(SigaaListaComando.ALTERAR_PARAMETROS.getId());
			MovimentoCadastro mov = new MovimentoCadastro();
			mov.setObjAuxiliar(parametro);
			mov.setCodMovimento(SigaaListaComando.ALTERAR_PARAMETROS);
			execute(mov);
		}
		
		addMensagem(OPERACAO_SUCESSO);

		return cancelar();
	}
	
	/** Valida os dados: Diretor DAE, Diretor DRED, Ano, Período.
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#validacaoDados(br.ufrn.arq.negocio.validacao.ListaMensagens)
	 */
	@Override
	public boolean validacaoDados(ListaMensagens mensagens) {
		PessoaDao dao = getDAO(PessoaDao.class); 
		Pessoa pessoa = null;
		// valida os IDs dos funcionários diretores.
		try {
			if (isUserInRole(SigaaPapeis.GESTOR_DIPLOMAS_GRADUACAO)) {
				diretorDAE.setValor(String.valueOf(pessoaDiretorDAE.getId()));
				diretorDRED.setValor(String.valueOf(pessoaDiretorDRED.getId()));
				if (pessoaDiretorDAE.getId() == 0) {
					mensagens.addMensagem(MensagensArquitetura.AUTOCOMPLETE_OBRIGATORIO, "Diretor do DAE");
				}
				if (pessoaDiretorDRED.getId() == 0) {
					mensagens.addMensagem(MensagensArquitetura.AUTOCOMPLETE_OBRIGATORIO, "Diretor do DRED");
				}
				pessoa = dao.findByPrimaryKey(pessoaDiretorDAE.getId(), Pessoa.class);
				if (pessoa == null || !pessoa.getNome().equals(pessoaDiretorDAE.getNome()))
					mensagens.addMensagem(MensagensArquitetura.AUTOCOMPLETE_OBRIGATORIO, "Diretor do DAE");
				pessoa = dao.findByPrimaryKey(pessoaDiretorDRED.getId(), Pessoa.class);
				if (pessoa == null || !pessoa.getNome().equals(pessoaDiretorDRED.getNome()))
					mensagens.addMensagem(MensagensArquitetura.AUTOCOMPLETE_OBRIGATORIO, "Diretor do DRED");
			} 
			if (isUserInRole(SigaaPapeis.GESTOR_DIPLOMAS_LATO, SigaaPapeis.GESTOR_DIPLOMAS_STRICTO)) {
				proReitorPosGraduacao.setValor(String.valueOf(pessoaProReitorPosGraduacao.getId()));
				if (pessoaProReitorPosGraduacao.getId() == 0) {
					mensagens.addMensagem(MensagensArquitetura.AUTOCOMPLETE_OBRIGATORIO, "Pro Reitor de Pós-Graduação");
				}
				pessoa = dao.findByPrimaryKey(pessoaProReitorPosGraduacao.getId(), Pessoa.class);
				if (pessoa == null || !pessoa.getNome().equals(pessoaProReitorPosGraduacao.getNome()))
					mensagens.addMensagem(MensagensArquitetura.AUTOCOMPLETE_OBRIGATORIO, "Pro Reitor de Pós-Graduação");
			}
		} catch (DAOException e) {
			addMensagemErroPadrao();
		}
		// valida o ano/período
		int ano = 0, periodo = 0;
		try {
			ano = Integer.parseInt(anoInicioRegistro.getValor());
			periodo = Integer.parseInt(semestreInicioRegistro.getValor());
		} catch (Exception e) {
			// nada a fazer
		}
		ValidatorUtil.validateMinValue(ano, 1900, "Ano", mensagens);
		ValidatorUtil.validateRange(periodo, 1, 2, "Período", mensagens);
		return mensagens.isEmpty();
	}
	
	/** Retorna o link para o formulário de edição dos parâmetros.
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#getFormPage()
	 */
	@Override
	public String getFormPage() {
		return "/diplomas/parametros_registro_diploma/form.jsp";
	}

	/** Retorna a página de listagem dos parâmetros.
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#getListPage()
	 */
	@Override
	public String getListPage() {
		return "/diplomas/parametros_registro_diploma/lista.jsp";
	}
	
	/** Verifica os papéis: ADMINISTRADOR_DAE
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#checkChangeRole()
	 */
	@Override
	public void checkChangeRole() throws SegurancaException {
		checkRole(SigaaPapeis.ADMINISTRADOR_DAE);
		super.checkChangeRole();
	}

	/** Retorna o parâmetro: anoInicioRegistro.
	 * @return Parâmetro: anoInicioRegistro. 
	 */
	public Parametro getAnoInicioRegistro() {
		return anoInicioRegistro;
	}

	/** Seta o parâmetro: anoInicioRegistro. 
	 * @param anoInicioRegistro Parâmetro: anoInicioRegistro. 
	 */
	public void setAnoInicioRegistro(Parametro anoInicioRegistro) {
		this.anoInicioRegistro = anoInicioRegistro;
	}

	/** Retorna o parâmetro: periodoInicioRegistro.
	 * @return Parâmetro: periodoInicioRegistro. 
	 */
	public Parametro getSemestreInicioRegistro() {
		return semestreInicioRegistro;
	}

	/** Seta o parâmetro: periodoInicioRegistro. 
	 * @param semestreInicioRegistro Parâmetro: periodoInicioRegistro. 
	 */
	public void setSemestreInicioRegistro(Parametro semestreInicioRegistro) {
		this.semestreInicioRegistro = semestreInicioRegistro;
	}

	/** Retorna o parâmetro: diretorDRED.
	 * @return Parâmetro: diretorDRED. 
	 */
	public Parametro getDiretorDRED() {
		return diretorDRED;
	}

	/** Seta o parâmetro: diretorDRED. 
	 * @param diretorDRED Parâmetro: diretorDRED. 
	 */
	public void setDiretorDRED(Parametro diretorDRED) {
		this.diretorDRED = diretorDRED;
	}

	/** Retorna o parâmetro: diretorDAE.
	 * @return Parâmetro: diretorDAE. 
	 */
	public Parametro getDiretorDAE() {
		return diretorDAE;
	}

	/** Seta o parâmetro: diretorDAE. 
	 * @param diretorDAE Parâmetro: diretorDAE. 
	 */
	public void setDiretorDAE(Parametro diretorDAE) {
		this.diretorDAE = diretorDAE;
	}

	/** Retorna o Diretor do DAE. 
	 * @return Diretor do DAE. 
	 */
	public Pessoa getPessoaDiretorDAE() {
		return pessoaDiretorDAE;
	}

	/** Seta o Diretor do DAE. 
	 * @param pessoaDiretorDAE
	 */
	public void setPessoaDiretorDAE(Pessoa pessoaDiretorDAE) {
		this.pessoaDiretorDAE = pessoaDiretorDAE;
	}

	/** Retorna o Diretor do DRED. 
	 * @return Diretor do DRED. 
	 */
	public Pessoa getPessoaDiretorDRED() {
		return pessoaDiretorDRED;
	}

	/** Seta o Diretor do DRED. 
	 * @param pessoaDiretorDRED Diretor do DRED. 
	 */
	public void setPessoaDiretorDRED(Pessoa pessoaDiretorDRED) {
		this.pessoaDiretorDRED = pessoaDiretorDRED;
	}

	public Parametro getProReitorPosGraduacao() {
		return proReitorPosGraduacao;
	}

	public void setProReitorPosGraduacao(Parametro proReitorPosGraduacao) {
		this.proReitorPosGraduacao = proReitorPosGraduacao;
	}

	public Pessoa getPessoaProReitorPosGraduacao() {
		return pessoaProReitorPosGraduacao;
	}

	public void setPessoaProReitorPosGraduacao(Pessoa pessoaProReitorPosGraduacao) {
		this.pessoaProReitorPosGraduacao = pessoaProReitorPosGraduacao;
	}
}
