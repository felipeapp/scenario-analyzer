package br.ufrn.sigaa.complexohospitalar.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;
import java.util.Collection;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.dominio.Comando;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.arq.web.jsf.AbstractControllerCadastro;
import br.ufrn.sigaa.arq.dao.CursoDao;
import br.ufrn.sigaa.arq.dao.InstituicoesEnsinoDao;
import br.ufrn.sigaa.arq.dao.UnidadeDao;
import br.ufrn.sigaa.arq.dao.ensino.FormaIngressoDao;
import br.ufrn.sigaa.arq.dao.graduacao.EstruturaCurricularDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.complexohospitalar.dominio.DiscenteResidenciaMedica;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.InstituicoesEnsino;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.dominio.FormaIngresso;
import br.ufrn.sigaa.ensino.graduacao.dominio.Curriculo;
import br.ufrn.sigaa.ensino.graduacao.jsf.BuscaDiscenteMBean;
import br.ufrn.sigaa.ensino.graduacao.jsf.EstruturaCurricularMBean;
import br.ufrn.sigaa.ensino.graduacao.jsf.OperacaoDiscente;
import br.ufrn.sigaa.ensino.graduacao.jsf.OperadorDiscente;
import br.ufrn.sigaa.ensino.negocio.dominio.DiscenteMov;
import br.ufrn.sigaa.jsf.DadosPessoaisMBean;
import br.ufrn.sigaa.jsf.OperacaoDadosPessoais;
import br.ufrn.sigaa.jsf.OperadorDadosPessoais;
import br.ufrn.sigaa.jsf.UnidadeMBean;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

/**
 * MBean para cadastrar Discentes na Residência Médica
 * 
 * @author agostinho campos
 */
@Component("discenteResidenciaMedica")
@Scope("request")
public class DiscentesResidenciaMedicaMBean extends AbstractControllerCadastro<DiscenteResidenciaMedica> implements OperadorDadosPessoais, OperadorDiscente {

	/** Coleção de SelectItem dos possíveis locais de graduação para o discente. */
	private Collection<SelectItem> localGraduacao = new ArrayList<SelectItem>(0);
	
	/** Coleção de SelectItem dos programas de residência */
	private Collection<SelectItem> unidades = new ArrayList<SelectItem>(0);

	private ListaMensagens listaMsgs;
	/** MBean utilizado para carregar os combos dos cursos e currículos no formulário dos dados do discente.  */
	private EstruturaCurricularMBean curriculoMBean;
	
	public DiscentesResidenciaMedicaMBean() throws DAOException {
		clear();
		gerarGraduacaoCombo();
		gerarUnidadesCombo();
	}
	
	/**
	 * Inicializa os objetos de domínio
	 * @throws DAOException
	 */
	private void clear() throws DAOException {
		obj = new DiscenteResidenciaMedica();
		obj.setAnoIngresso( CalendarUtils.getAnoAtual() );
		obj.setPeriodoIngresso( getPeriodoAtual() );
		obj.setGestoraAcademica(new Unidade());
		obj.setLocalGraduacao(new InstituicoesEnsino() );
		obj.getDiscente().setCurriculo(new Curriculo());
		curriculoMBean = getMBean("curriculo");
	}
	
	/**
	 * Iniciar o cadastro de discentes novos.
	 *
	 * Chamado por /stricto/menus/discente.jsp
	 * Chamado por /stricto/menu_coordenador.jsp
	 * @return
	 * @throws ArqException
	 */
	public String iniciarCadastroDiscenteNovo() throws ArqException {
		checkRole(SigaaPapeis.GESTOR_COMPLEXO_HOSPITALAR, SigaaPapeis.SECRETARIA_RESIDENCIA,
				SigaaPapeis.COORDENADOR_PROGRAMA_RESIDENCIA);
		clear();
		obj.setStatus(StatusDiscente.ATIVO);
		obj.setNivel(NivelEnsino.RESIDENCIA);
		return popular();
	}
	
	/**
	 * Chama o operador de dados pessoais para realizar a verificação do CPF
	 * e redireciona para o cadastro do discente de pós-graduação.
	 * @return
	 * @throws ArqException
	 */
	private String popular() throws ArqException{
		checkRole(SigaaPapeis.GESTOR_COMPLEXO_HOSPITALAR, SigaaPapeis.SECRETARIA_RESIDENCIA,
				SigaaPapeis.COORDENADOR_PROGRAMA_RESIDENCIA);

		setConfirmButton("Cadastrar");
		prepareMovimento(SigaaListaComando.CADASTRAR_DISCENTE);

		DadosPessoaisMBean dadosPessoaisMBean = (DadosPessoaisMBean) getMBean("dadosPessoais");
		dadosPessoaisMBean.initObj();
		dadosPessoaisMBean.setCodigoOperacao( OperacaoDadosPessoais.DISCENTE_RESIDENCIA );
		return dadosPessoaisMBean.popular();
	}
	
	/**
	 * Valida o formulário de cadastro de discentes específico para Residência Médica 
	 * @throws NegocioException
	 * @throws SegurancaException
	 * @throws DAOException
	 */
	private void validarFormulario() throws NegocioException, SegurancaException, DAOException {
		listaMsgs = new ListaMensagens();
		ValidatorUtil.validateRequired(obj.getLocalGraduacao(), "Local de Graduação do Discente", listaMsgs);
		ValidatorUtil.validateRequired(obj.getUnidade(), "Programa", listaMsgs);
		ValidatorUtil.validateRequired(obj.getCurso(), "Curso", listaMsgs);
		ValidatorUtil.validateRequired(obj.getCurriculo(), "Currículo", listaMsgs);
		ValidatorUtil.validateRequired(obj.getAnoIngresso(), "Ano de Ingresso", listaMsgs);
		ValidatorUtil.validateRequired(obj.getPeriodoIngresso(), "Período de Ingresso", listaMsgs);
		ValidatorUtil.validaInt(obj.getCrm(), "Nº do Registro no Conselho Profissional", listaMsgs);
		
		if ( ValidatorUtil.isEmpty(obj.getNivelEntradaResidente()) == true )
			listaMsgs.addErro("Nível de Entrada do Discente: Campo obrigatório não informado.");
		if ( ValidatorUtil.isEmpty(obj.getMesEntrada()) == true )
			listaMsgs.addErro("Mês de entrada: Campo obrigatório não informado.");
		if ( ValidatorUtil.isEmpty(obj.getTipo()) == true )
			listaMsgs.addErro("Tipo do Discente: Campo obrigatório não informado.");
		
		addMensagens(listaMsgs);
	}
	
	/** Redireciona para a busca de discentes, a fim de realizar a operação de atualização de dados.
	 * Chamado por /stricto/menus/discente.jsp
	 * Chamado por /stricto/menu_coordenador.jsp
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#atualizar()
	 */
	@Override
	public String atualizar() throws ArqException {
		checkRole(SigaaPapeis.GESTOR_COMPLEXO_HOSPITALAR, SigaaPapeis.SECRETARIA_RESIDENCIA,
				SigaaPapeis.COORDENADOR_PROGRAMA_RESIDENCIA);
		prepareMovimento( SigaaListaComando.ALTERAR_DISCENTE_RESIDENCIA );
		clear();
		BuscaDiscenteMBean buscaDiscenteMBean = (BuscaDiscenteMBean) getMBean("buscaDiscenteGraduacao");
		
		buscaDiscenteMBean.setCodigoOperacao(OperacaoDiscente.ALTERAR_DISCENTE_RESIDENCIA_MEDICA);
		
		return buscaDiscenteMBean.popular();
	}
	
	/**
	 * Cadastra o discente residente.
	 * Chamado por: <br/>
	 * <ul> 
	 * 	<li> /sigaa.war/complexo_hospitalar/Discente/form.jsp </li>
	 * </ul>
	 */
	@Override
	public String cadastrar() throws SegurancaException, ArqException, NegocioException {

		obj.setTipo(Discente.REGULAR);
		validarFormulario();
		if (listaMsgs != null && !listaMsgs.isEmpty())
			return forward("/complexo_hospitalar/Discente/form.jsf");
			
		// removendo atributos transientes
		if( obj.getPessoa().getEstadoCivil() != null &&
				obj.getPessoa().getEstadoCivil().getId() == 0 )
			obj.getPessoa().setEstadoCivil(null);

		if( obj.getPessoa().getTipoRaca()!= null &&
				obj.getPessoa().getTipoRaca().getId() == 0 )
			obj.getPessoa().setTipoRaca(null);

		Unidade unidade = getDAO(UnidadeDao.class).findByPrimaryKey(obj.getUnidade().getId(), Unidade.class);
		Curso curso = getDAO(CursoDao.class).findByPrimaryKey(obj.getCurso().getId(), Curso.class); 
		Curriculo curriculo = getDAO(EstruturaCurricularDao.class).findByPrimaryKey(obj.getCurriculo().getId(), Curriculo.class);
		
		obj.setGestoraAcademica(unidade);
		obj.setCurso(curso);
		obj.setCurriculo(curriculo);
		obj.setLocalGraduacao( getDAO(InstituicoesEnsinoDao.class).findByPrimaryKey(obj.getLocalGraduacao().getId(), InstituicoesEnsino.class) );
		obj.setNivel(NivelEnsino.RESIDENCIA);
		
		// FIXME: qual tipo de seleção??
		FormaIngresso formaIngresso= getDAO(FormaIngressoDao.class).findByPrimaryKey(FormaIngresso.SELECAO_POS_GRADUACAO.getId(), FormaIngresso.class);
		obj.setFormaIngresso(formaIngresso);
		
		Comando comando = SigaaListaComando.CADASTRAR_DISCENTE;
		if( obj.getId() > 0 )
			comando = SigaaListaComando.ALTERAR_DISCENTE_RESIDENCIA;
		
		DiscenteMov mov = new DiscenteMov(comando, obj);
		mov.setDiscenteAntigo( false );
		
		try {
			prepareMovimento(SigaaListaComando.CADASTRAR_DISCENTE);
			execute(mov);
			if( comando.equals(SigaaListaComando.CADASTRAR_DISCENTE) ){
				addMessage( "Discente "+obj.getNome()+" cadastrado com sucesso, e associado com o número de  matrícula " + obj.getMatricula(), TipoMensagemUFRN.INFORMATION);
			}else{
				addMessage( "O discente "+obj.toString()+" foi atualizado com sucesso.", TipoMensagemUFRN.INFORMATION);
			}
		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens());
			return null;
		}
		
		return forward("/complexo_hospitalar/index.jsp");
	}
	
	/**
	 * Volta para a página de busca dos discentes.
	 * @return
	 */
	public String voltar(){
		if (obj.getId() > 0)
			return forward("/graduacao/busca_discente.jsp");
		else
			return forward("/geral/pessoa/dados_pessoais.jsp");
	}

	/**
	 * Seta os dados da pessoa no discente da residência 
	 */
	public void setDadosPessoais(Pessoa pessoa) {
		obj.setPessoa(pessoa);
	}

	/**
	 * Redireciona para a tela com dados específicos da residência médica   
	 */
	public String submeterDadosPessoais() {
		return telaDadosDiscente();
	}

	/** Retorna o formulário de dados do discente.
	 * @return /complexo_hospitalar/Discente/form.jsp
	 */
	private String telaDadosDiscente() {
		return forward( "/complexo_hospitalar/Discente/form.jsp");
	}
	
	/**
	 * Método a ser chamado após a seleção do discente
	 */
	public String selecionaDiscente() throws ArqException {
		carregaCombosDadosDiscente();
		return telaDadosDiscente();
	}
	
	/**
	 * Carrega os combos dos cursos e currículo para operação
	 * {@link #atualizar}
	 * @throws DAOException 
	 */
	private void carregaCombosDadosDiscente() throws DAOException{
		if( !isEmpty(obj) ){
			curriculoMBean = getMBean("curriculo");
			curriculoMBean.setObj(obj.getCurriculo());
			curriculoMBean.selecionarPrograma(null);
			curriculoMBean.carregarCurriculos(obj.getCurso().getId(), obj.getNivel());
		}
	}
			
	
	/**
	 * Método a ser chamado após a seleção do discente
	 */
	public void setDiscente(DiscenteAdapter discente) throws ArqException {
		obj = (DiscenteResidenciaMedica) discente;
	}

	private void gerarGraduacaoCombo() throws DAOException {
		InstituicoesEnsinoDao dao = getDAO(InstituicoesEnsinoDao.class);
		localGraduacao = toSelectItems(dao.findAll(), "id", "nome");
	}
	private void gerarUnidadesCombo() {
		UnidadeMBean unidadeMBean = getMBean("unidade");
		unidades = unidadeMBean.getAllProgramaResidenciaCombo();
	}

	public Collection<SelectItem> getLocalGraduacao() {
		return localGraduacao;
	}

	public void setLocalGraduacao(Collection<SelectItem> localGraduacao) {
		this.localGraduacao = localGraduacao;
	}

	public Collection<SelectItem> getUnidades() {
		return unidades;
	}

	public void setUnidades(Collection<SelectItem> unidades) {
		this.unidades = unidades;
	}
}