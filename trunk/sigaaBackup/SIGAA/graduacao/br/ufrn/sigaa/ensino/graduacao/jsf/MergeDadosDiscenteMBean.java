/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Criado em 19/04/2012
 *
 */
package br.ufrn.sigaa.ensino.graduacao.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;
import static br.ufrn.arq.util.ValidatorUtil.validateCPF_CNPJ;

import java.util.Collection;
import java.util.LinkedList;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.LimiteResultadosException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.ValidadorCPFCNPJ;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.DiscenteDao;
import br.ufrn.sigaa.arq.dao.UsuarioDao;
import br.ufrn.sigaa.arq.dao.graduacao.OrientacaoAcademicaDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.stricto.dominio.DiscenteStricto;
import br.ufrn.sigaa.pessoa.dominio.Discente;

/**
 * Controller respons�vel pelo "merge" de dados de pessoas em duplicidade. Os
 * dados de discente, usu�rio e servidor (se for o caso) ser�o unificados em um
 * �nico registro de pessoa.
 * 
 * @author �dipo Elder F. de Melo
 * 
 */
@Component("mergeDadosDiscenteMBean") @Scope("session")
public class MergeDadosDiscenteMBean extends SigaaAbstractController<Discente> {
	
	/** Indica se a busca por n�vel de ensino est� ou n�o marcada. */
	private boolean buscaNivelEnsino;
	
	/** Indica se a busca por Matr�cula especial est� ou n�o marcada. */
	private boolean buscaMatricula;

	/** Indica se a busca por Nome especial est� ou n�o marcada. */
	private boolean buscaNome;
	
	/** Indica se a busca por CPF est� ou n�o marcada. */
	private boolean buscaCpf;
	
	/** N�vel de Ensino que o usu�rio deseja buscar discentes. � utilizado para busca por discentes no m�dulo de Registro de Diplomas, onde o usu�rio pode operar discentes de n�veis de ensino diferentes. */
	private char nivelEnsinoEspecifico;
	
	/** Cole��o de discentes que ser�o unificados*/
	private Collection<Discente> discentes;
	
	/** Construtor padr�o. */
	public MergeDadosDiscenteMBean() {
		super();
	}
	
	/**
	 * Inicia a unifica��o de dados pessoais de dois discentes.<br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/graduacao/menus/cdp.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String iniciar() throws ArqException {
		checkChangeRole();
		setOperacaoAtiva(SigaaListaComando.MERGE_DADOS_DISCENTE.getId());
		prepareMovimento(SigaaListaComando.MERGE_DADOS_DISCENTE);
		nivelEnsinoEspecifico = getNivelEnsino();
		buscaNivelEnsino = true;
		obj = new Discente();
		discentes = new LinkedList<Discente>();
		resultadosBusca = null;
		return formBuscaDiscente();
	}

	/** Redireciona o usu�rio para o formul�rio de busca de discentes.
	 * @return
	 */
	public String formBuscaDiscente() {
		return forward("/graduacao/merge_discente/buscar.jsp");
	}
	
	/** Redireciona o usu�rio para o formul�rio de busca de discentes.
	 * @return
	 */
	public String formConfirmar() {
		return forward("/graduacao/merge_discente/confirmar.jsp");
	}
	
	/**
	 * Busca os discentes de acordo com os crit�rios de busca informados.<br/>
	 * M�todo chamado pela seguinte JSP:
	 * <ul>
	 * 	<li>/graduacao/busca_discente.jsp</li>
	 * </ul>
	 * 
	 * @see br.ufrn.arq.jsf.AbstractControllerCadastro#buscar()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public String buscar() throws ArqException{

		Long matricula = null;
		String nome = null;
		String nomeCurso = null;
		Long cpf = null;
		Integer tipoNecessidadeEspecial = null;
		char nivel = '0';
		
		if (!buscaMatricula && !buscaNome && !buscaCpf ) {
			addMensagemErro("� necess�rio selecionar um dos crit�rios de busca: Matricula, Nome ou CPF");
			return null;
		}
		// Verificar os crit�rios de busca utilizados
		if (buscaMatricula) {
			ValidatorUtil.validateRequired(obj.getMatricula(), "Matr�cula", erros);
			matricula = obj.getMatricula();
		}
		if (buscaNome) {
			ValidatorUtil.validateRequired(obj.getPessoa().getNome(), "Nome do Discente", erros);
			nome = obj.getPessoa().getNome().trim().toUpperCase();
		}
		if (buscaCpf) {
			cpf = obj.getPessoa().getCpf_cnpj();
			ValidatorUtil.validateRequired(cpf, "CPF", erros);
			if (cpf != null)
				ValidatorUtil.validateCPF_CNPJ(obj.getPessoa().getCpf_cnpj(), "CPF", erros);
		}
		if (buscaNivelEnsino)
			nivel = nivelEnsinoEspecifico;

		if (hasErrors())
			return null;

		// Realizar a consulta
		DiscenteDao discenteDao = getDAO(DiscenteDao.class);
		try {
			int unidade = 0;
			resultadosBusca = (Collection<Discente>) discenteDao.findOtimizado(cpf, matricula, nome, nomeCurso, tipoNecessidadeEspecial, null, null, null,
													unidade, nivel, !getAcessoMenu().isAdministradorDAE() );

			if (isEmpty(resultadosBusca)) {
				addMessage("N�o foram encontrados discentes de acordo com os crit�rios de busca informados.", TipoMensagemUFRN.ERROR);
				return null;
			}

		} catch (LimiteResultadosException e) {
			return tratamentoErroPadrao(e, e.getMessage());
		} catch (DAOException e) {
			return tratamentoErroPadrao(e);
		}

		return formBuscaDiscente();
	}
	
	/** Confirma a senha do usu�rio e, estando correta, unifica os dados pessoais dos dois discentes.<br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/graduacao/merge_discente/form.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public String confirmar() throws ArqException {
		if (!checkOperacaoAtiva(SigaaListaComando.MERGE_DADOS_DISCENTE.getId())) return cancelar();
		// valida��o b�sica
		if (discentes.size() < 2)
			addMensagemErro("Selecione mais de um discente para unificar os dados pessoais.");
		Discente discenteSelecionado = null;
		for (Discente d : discentes) {
			if (d.isSelecionado()) {discenteSelecionado = d; break;}
		}
		if (discenteSelecionado == null)
			addMensagemErro("Selecione os dados pessoais de um discente para o qual o restante ser� unificado.");
		else {
			Long cpf = discenteSelecionado.getPessoa().getCpf_cnpj();
			if (cpf == null || cpf == 0 || !ValidadorCPFCNPJ.getInstance().validaCpfCNPJ(cpf))
				addMensagemErro("O discente selecionado n�o possui um CPF v�lido.");
		}
		if (hasErrors())
			return null;
		return formConfirmar();
	}
	
	/** Confirma a senha do usu�rio e, estando correta, unifica os dados pessoais dos dois discentes.<br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/graduacao/merge_discente/form.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public String cadastrar() throws ArqException {
		if (!checkOperacaoAtiva(SigaaListaComando.MERGE_DADOS_DISCENTE.getId())) return cancelar();
		if( !confirmaSenha() ) return null;
		// realiza o merge
		MovimentoCadastro mov = new MovimentoCadastro();
		mov.setColObjMovimentado(discentes);
		mov.setCodMovimento(SigaaListaComando.MERGE_DADOS_DISCENTE);
		try {
			execute(mov);
		} catch (Exception e) {
			addMensagemErroPadrao();
			notifyError(e);
			return null;
		}
		addMensagem(MensagensArquitetura.OPERACAO_SUCESSO);
		removeOperacaoAtiva();
		return cancelar();
	}
	
	/** Seta o discente que foi escolhido pelo usu�rio.<br />
	 * M�todo n�o invocado por JSP�s
	 * @see br.ufrn.sigaa.ensino.graduacao.jsf.OperadorDiscente#selecionaDiscente()
	 */
	public String selecionaDiscente() throws ArqException {
		if (!checkOperacaoAtiva(SigaaListaComando.MERGE_DADOS_DISCENTE.getId())) return cancelar();
		int id = getParameterInt("idDiscente", 0);
		GenericDAO dao = getGenericDAO();
		Discente discente = dao.findByPrimaryKey(id, Discente.class);		
		if (isEmpty(discente)) {
			addMensagem(MensagensArquitetura.OBJETO_NAO_FOI_SELECIONADO, "discente");
			return null;
		}
		if (discentes.contains(discente)){
			addMensagemErro("O discente j� foi selecionado anteriormente.");
			return null;
		}
		for (Discente adicionado : discentes)
			if (discente.getPessoa().getId() == adicionado.getPessoa().getId()) {
				addMensagemErro("O discente selecionado j� est� unificado com o discente " + adicionado.getMatriculaNome());
				return null;
			}
		discente.getPessoa().prepararDados();
		discentes.add(discente);
		return formularioUnificacao();
	}
	
	/** Redireciona o usu�rio para o formul�rio de unifica��o de discentes
	 * 
	 * @return
	 */
	public String formularioUnificacao() {
		return forward("/graduacao/merge_discente/form.jsp");
	}

	/**
	 * Popular dados do discente selecionado para exibi��o
	 *
	 * @param event
	 * @throws DAOException
	 */
	public DiscenteAdapter getDadosDiscente() {
		obj.setId(getParameterInt("idDiscente",0));
		DiscenteAdapter dadosDiscente = null;
		if (obj.getId() > 0) {
			try {
				dadosDiscente = getDAO(DiscenteDao.class).findByPK(obj.getId());

				if (dadosDiscente == null){
					return null;
				}
				obj = dadosDiscente.getDiscente();
				obj.setUsuario(getDAO(UsuarioDao.class).findByDiscente(obj.getDiscente()));

				// Popular dados espec�ficos de discentes stricto
				if (obj.isStricto()) {
					OrientacaoAcademicaDao orientacaoDao = getDAO(OrientacaoAcademicaDao.class);
					DiscenteStricto stricto = (DiscenteStricto) dadosDiscente;
					stricto.setOrientacao( orientacaoDao.findOrientadorAtivoByDiscente(obj.getId()) );
				}

			} catch (DAOException e) {
				notifyError(e);
			}
		}
		return dadosDiscente;
	}
	
	/** Verifica as permiss�es do usu�rio.<br />
	 * M�todo n�o invocado por JSP�s
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#checkChangeRole()
	 */
	@Override
	public void checkChangeRole() throws SegurancaException {
		checkRole(SigaaPapeis.ADMINISTRADOR_DAE, SigaaPapeis.ADMINISTRADOR_SIGAA);
	}
	
	/** Permite ao usu�rio selecionar o n�vel de ensino.
	 * @return
	 */
	public boolean isPermiteSelecionarNivel() {
		return isUserInRole(SigaaPapeis.ADMINISTRADOR_SIGAA);
	}

	public boolean isBuscaMatricula() {
		return buscaMatricula;
	}

	public void setBuscaMatricula(boolean buscaMatricula) {
		this.buscaMatricula = buscaMatricula;
	}

	public boolean isBuscaNome() {
		return buscaNome;
	}

	public void setBuscaNome(boolean buscaNome) {
		this.buscaNome = buscaNome;
	}

	public boolean isBuscaCpf() {
		return buscaCpf;
	}

	public void setBuscaCpf(boolean buscaCpf) {
		this.buscaCpf = buscaCpf;
	}

	public char getNivelEnsinoEspecifico() {
		return nivelEnsinoEspecifico;
	}

	public void setNivelEnsinoEspecifico(char nivelEnsinoEspecifico) {
		this.nivelEnsinoEspecifico = nivelEnsinoEspecifico;
	}

	public boolean isBuscaNivelEnsino() {
		return buscaNivelEnsino;
	}

	public void setBuscaNivelEnsino(boolean buscaNivelEnsino) {
		this.buscaNivelEnsino = buscaNivelEnsino;
	}

	public Collection<Discente> getDiscentes() {
		return discentes;
	}

	public void setDiscentes(Collection<Discente> discentes) {
		this.discentes = discentes;
	}

}
