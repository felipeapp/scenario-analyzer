/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
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
 * Controller responsável pelo "merge" de dados de pessoas em duplicidade. Os
 * dados de discente, usuário e servidor (se for o caso) serão unificados em um
 * único registro de pessoa.
 * 
 * @author Édipo Elder F. de Melo
 * 
 */
@Component("mergeDadosDiscenteMBean") @Scope("session")
public class MergeDadosDiscenteMBean extends SigaaAbstractController<Discente> {
	
	/** Indica se a busca por nível de ensino está ou não marcada. */
	private boolean buscaNivelEnsino;
	
	/** Indica se a busca por Matrícula especial está ou não marcada. */
	private boolean buscaMatricula;

	/** Indica se a busca por Nome especial está ou não marcada. */
	private boolean buscaNome;
	
	/** Indica se a busca por CPF está ou não marcada. */
	private boolean buscaCpf;
	
	/** Nível de Ensino que o usuário deseja buscar discentes. É utilizado para busca por discentes no módulo de Registro de Diplomas, onde o usuário pode operar discentes de níveis de ensino diferentes. */
	private char nivelEnsinoEspecifico;
	
	/** Coleção de discentes que serão unificados*/
	private Collection<Discente> discentes;
	
	/** Construtor padrão. */
	public MergeDadosDiscenteMBean() {
		super();
	}
	
	/**
	 * Inicia a unificação de dados pessoais de dois discentes.<br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
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

	/** Redireciona o usuário para o formulário de busca de discentes.
	 * @return
	 */
	public String formBuscaDiscente() {
		return forward("/graduacao/merge_discente/buscar.jsp");
	}
	
	/** Redireciona o usuário para o formulário de busca de discentes.
	 * @return
	 */
	public String formConfirmar() {
		return forward("/graduacao/merge_discente/confirmar.jsp");
	}
	
	/**
	 * Busca os discentes de acordo com os critérios de busca informados.<br/>
	 * Método chamado pela seguinte JSP:
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
			addMensagemErro("É necessário selecionar um dos critérios de busca: Matricula, Nome ou CPF");
			return null;
		}
		// Verificar os critérios de busca utilizados
		if (buscaMatricula) {
			ValidatorUtil.validateRequired(obj.getMatricula(), "Matrícula", erros);
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
				addMessage("Não foram encontrados discentes de acordo com os critérios de busca informados.", TipoMensagemUFRN.ERROR);
				return null;
			}

		} catch (LimiteResultadosException e) {
			return tratamentoErroPadrao(e, e.getMessage());
		} catch (DAOException e) {
			return tratamentoErroPadrao(e);
		}

		return formBuscaDiscente();
	}
	
	/** Confirma a senha do usuário e, estando correta, unifica os dados pessoais dos dois discentes.<br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/graduacao/merge_discente/form.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public String confirmar() throws ArqException {
		if (!checkOperacaoAtiva(SigaaListaComando.MERGE_DADOS_DISCENTE.getId())) return cancelar();
		// validação básica
		if (discentes.size() < 2)
			addMensagemErro("Selecione mais de um discente para unificar os dados pessoais.");
		Discente discenteSelecionado = null;
		for (Discente d : discentes) {
			if (d.isSelecionado()) {discenteSelecionado = d; break;}
		}
		if (discenteSelecionado == null)
			addMensagemErro("Selecione os dados pessoais de um discente para o qual o restante será unificado.");
		else {
			Long cpf = discenteSelecionado.getPessoa().getCpf_cnpj();
			if (cpf == null || cpf == 0 || !ValidadorCPFCNPJ.getInstance().validaCpfCNPJ(cpf))
				addMensagemErro("O discente selecionado não possui um CPF válido.");
		}
		if (hasErrors())
			return null;
		return formConfirmar();
	}
	
	/** Confirma a senha do usuário e, estando correta, unifica os dados pessoais dos dois discentes.<br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
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
	
	/** Seta o discente que foi escolhido pelo usuário.<br />
	 * Método não invocado por JSP´s
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
			addMensagemErro("O discente já foi selecionado anteriormente.");
			return null;
		}
		for (Discente adicionado : discentes)
			if (discente.getPessoa().getId() == adicionado.getPessoa().getId()) {
				addMensagemErro("O discente selecionado já está unificado com o discente " + adicionado.getMatriculaNome());
				return null;
			}
		discente.getPessoa().prepararDados();
		discentes.add(discente);
		return formularioUnificacao();
	}
	
	/** Redireciona o usuário para o formulário de unificação de discentes
	 * 
	 * @return
	 */
	public String formularioUnificacao() {
		return forward("/graduacao/merge_discente/form.jsp");
	}

	/**
	 * Popular dados do discente selecionado para exibição
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

				// Popular dados específicos de discentes stricto
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
	
	/** Verifica as permissões do usuário.<br />
	 * Método não invocado por JSP´s
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#checkChangeRole()
	 */
	@Override
	public void checkChangeRole() throws SegurancaException {
		checkRole(SigaaPapeis.ADMINISTRADOR_DAE, SigaaPapeis.ADMINISTRADOR_SIGAA);
	}
	
	/** Permite ao usuário selecionar o nível de ensino.
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
