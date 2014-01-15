/*
* Universidade Federal do Rio Grande do Norte
* Superintendência de Informática
* Diretoria de Sistemas
*
* Created on 27/06/2007
* 
*/
package br.ufrn.sigaa.ensino.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;
import static br.ufrn.arq.util.ValidatorUtil.isNotEmpty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.parametrizacao.RepositorioDadosInstitucionais;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.ensino.MatriculaImplantadaDao;
import br.ufrn.sigaa.arq.dao.graduacao.DiscenteGraduacaoDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.CalendarioAcademico;
import br.ufrn.sigaa.dominio.ParametrosGestoraAcademica;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.dominio.MetodoAvaliacao;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.graduacao.jsf.BuscaDiscenteMBean;
import br.ufrn.sigaa.ensino.graduacao.jsf.OperacaoDiscente;
import br.ufrn.sigaa.ensino.graduacao.jsf.OperadorDiscente;
import br.ufrn.sigaa.ensino.negocio.CalendarioAcademicoHelper;
import br.ufrn.sigaa.ensino.negocio.MatriculaComponenteHelper;
import br.ufrn.sigaa.ensino.negocio.ParametrosGestoraAcademicaHelper;
import br.ufrn.sigaa.ensino.negocio.dominio.ImplantarHistoricoMov;
import br.ufrn.sigaa.ensino.stricto.dominio.DiscenteStricto;
import br.ufrn.sigaa.ensino.tecnico.dao.DiscenteTecnicoDao;
import br.ufrn.sigaa.parametros.dominio.ParametrosStrictoSensu;
import br.ufrn.sigaa.pessoa.dominio.Discente;

/**
 * MBeam responsável por realizar a implantação de históricos de alunos que ainda não o possuem.
 * @author Victor Hugo
 */
@Component("implantarHistorico") @Scope("session")
public class ImplantarHistoricoMBean extends SigaaAbstractController<Object> implements OperadorDiscente {

	/** Link para o formulário de resumo dos dados de implantação do histórico. */
	private static final String JSP_RESUMO = "/ensino/implantar_historico/resumo.jsp";
	
	/** Link para o formulário dos dados de implantação do histórico. */
	private static final String JSP_FORM = "/ensino/implantar_historico/form.jsf";

	/** Discente que terá os componentes implantados no histórico. */
	private DiscenteAdapter discente;
	
	/** Referencia ao objeto discente stricto, existirá quando o discente selecionado for de stricto, essa referencia é necessária para acessar atributos que são específicos de discente stricto na validação */
	private DiscenteStricto discenteStricto;

	/** Matrícula em componente curricular que será implantada no histórico. */
	private MatriculaComponente matricula = new MatriculaComponente();

	/** Lista das matrículas em componente curricular que será implantada no histórico. */
	private List<MatriculaComponente> matriculas = new ArrayList<MatriculaComponente>();
	
	/** Lista das matrículas em componente curricular que foram implantadas no histórico seja através da implantação de histórico ou através de aproveitamento. 
	 * É necessário carregar as matrículas implantadas anteriormente para que o usuário possa altera-las. */
	private List<MatriculaComponente> matriculasAnteriores = new ArrayList<MatriculaComponente>();
	
	/** Essa lista armazena as matrículas que haviam sido implantadas e que o usuário selecionou para remoção */
	private List<MatriculaComponente> matriculasAnterioresParaRemover = new ArrayList<MatriculaComponente>();
	
	///** Essa lista armazena as matrículas que haviam sido implantadas e que o usuário selecionou para remoção */
	//private List<MatriculaComponente> matriculasAnterioresParaAlterar = new ArrayList<MatriculaComponente>();

	/** Parâmetros da gestora acadêmica do aluno selecionado. */
	private ParametrosGestoraAcademica parametros;
	
	/** Calendário acadêmico vigente, utilizado na validação */
	private CalendarioAcademico calendario;
	
	/** Ano de início para o cadastro de matrícula na implantação de histórico de discentes antigos.*/
	private final static int ANO_INICIO_STRICTO = ParametroHelper.getInstance().getParametroInt(ParametrosStrictoSensu.ANO_INICIO_MATRICULA_IMPLANTACAO_HISTORICO_STRICTO);
	
	/** Inicializa os atributos do controller. */
	private void init(){
		matricula = new MatriculaComponente();
		matricula.setComponente( new ComponenteCurricular() );
		matricula.setSituacaoMatricula( new SituacaoMatricula() );
		setConfirmButton("Adicionar");
	}

	/**
	 * Inicia o caso de uso de implantar histórico
	 * <br>
	 * Método chamado pela(s) JSP(s):
	 * <ul>
	 * 	<li>/app/sigaa.ear/sigaa.war/stricto/menus/discente.jsp
	 * 	<li>/app/sigaa.ear/sigaa.war/graduacao/menus/aluno.jsp
	 *  <li> /sigaa.war/stricto/menu_coordenador.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public String iniciar() throws ArqException{
		checkRole( SigaaPapeis.DAE, SigaaPapeis.CDP, SigaaPapeis.GESTOR_TECNICO, SigaaPapeis.SECRETARIA_TECNICO,
				SigaaPapeis.PPG, SigaaPapeis.GESTOR_COMPLEXO_HOSPITALAR, SigaaPapeis.SECRETARIA_RESIDENCIA,
				SigaaPapeis.COORDENADOR_PROGRAMA_RESIDENCIA, SigaaPapeis.GESTOR_FORMACAO_COMPLEMENTAR,
				SigaaPapeis.SECRETARIA_POS, SigaaPapeis.COORDENADOR_CURSO_STRICTO);
		
		if( isPortalCoordenadorStricto() && !ParametroHelper.getInstance().getParametroBoolean(ParametrosStrictoSensu.PERMITE_PROGRAMA_POS_IMPLANTAR_HISTORICO_DISCENTE_ANTIGO) ){
			addMensagemErro("A operação de implantar histórico na pós-graduação stricto-sensu está configurada para ser realizado apenas pela " + RepositorioDadosInstitucionais.get("siglaUnidadeGestoraPosGraduacao"));
			return null;
		}
		
		prepareMovimento(SigaaListaComando.IMPLANTAR_HISTORICO);

		discenteStricto = null;
		calendario = null;
		
		init();

		BuscaDiscenteMBean buscaDiscenteMBean = (BuscaDiscenteMBean) getMBean("buscaDiscenteGraduacao");
		buscaDiscenteMBean.setCodigoOperacao( OperacaoDiscente.IMPLANTAR_HISTORICO );
		return buscaDiscenteMBean.popular();
	}

	/**
	 * Adiciona uma matrícula na lista de matrículas
	 * <br>
	 * Método chamado pela(s) JSP(s):
	 * <ul>
	 * 	<li>/app/sigaa.ear/sigaa.war/ensino/implantar_historico/form.jsp
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public String adicionarMatricula() throws ArqException{
		DiscenteGraduacaoDao dao = getDAO(DiscenteGraduacaoDao.class);
		
		matricula.setMetodoAvaliacao( parametros.getMetodoAvaliacao() );
		String conceito = getParameter( "conceito_selecionado" );
		if (conceito != null) {
			Double conceitoD = Double.parseDouble(conceito);
			if (conceitoD < 0) {
				matricula.setConceito(null);
			} else {
				matricula.setConceito(conceitoD);
			}
		}
		
		if( matricula == null || matricula.getComponente() == null || matricula.getComponente().getId() == 0 )
			addMensagemErro("Selecione um componente curricular.");
		else {
			matricula.setComponente(dao.findAndFetch(matricula.getComponente().getId(), ComponenteCurricular.class, "unidade"));
			matricula.setDetalhesComponente( matricula.getComponente().getDetalhes());
		}	
		if( isNota() && (matricula.getMediaFinal() == null || matricula.getMediaFinal() < 0  || matricula.getMediaFinal() > 10 ))
			addMensagemErro("Nota inválida.");
		
		if( isConceito() && (matricula.getConceito() == null || matricula.getConceito() < 1 || matricula.getConceito() > 5 ) )
			addMensagemErro("Conceito inválido. Selecione um conceito válido.");
		
		if( isCompetencia() && matricula.getApto() == null)
			addMensagemErro("Competência inválida. Selecione uma competência válida.");
		
		if (discente.isGraduacao() || discente.isTecnico()) {
			if( matricula.getAno() == null || matricula.getAno() <= 1950 )
				addMensagemErro("Ano inválido.");
	
			if( matricula.getPeriodo() == null || matricula.getPeriodo() <= 0 || matricula.getPeriodo() >= 5 )
				addMensagemErro("Período inválido.");
		}

		if( hasErrors() )
			return null;
		
		if (discente.isStricto()) {
			
			if( matricula.getMes() <= 0 )
				addMensagemErro("Mês inicial: campo obrigatório não informado.");
			
			if( matricula.getAno() <= 0 )
				addMensagemErro("Ano inicial: campo obrigatório não informado.");
			
			if( matricula.getMesFim() <= 0 )
				addMensagemErro("Mês fnial: campo obrigatório não informado.");
			
			if( matricula.getAnoFim() <= 0 )
				addMensagemErro("Ano final: campo obrigatório não informado.");
			

			int anoMesFim = matricula.getAnoFim() * 10 + matricula.getMesFim();
			int anoMesInicio = matricula.getAno() * 10 + matricula.getMes();
			int anoMesIngresso = discenteStricto.getAnoIngresso() * 10 + discenteStricto.getMesEntrada();
			int anoAtual = calendario.getAno();
			
			if (anoMesFim - anoMesInicio < 0)
				addMensagemErro("O mês/ano final não pode ser anterior ao mês/ano inicial.");
			
			if( anoMesInicio < anoMesIngresso )
				addMensagemErro("O mês/ano inicial não pode ser anterior ao de ingresso do aluno.");
			
			if( anoMesFim < anoMesIngresso )
				addMensagemErro("O mês/ano final não pode ser anterior ao de ingresso do aluno.");

			if( matricula.getAno() > anoAtual )
				addMensagemErro("O ano inicial não pode ser posterior do que o ano vigente.");
			
			if( matricula.getAnoFim() > anoAtual )
				addMensagemErro("O ano final não pode ser posterior do que o ano vigente.");
			
			
		} else if( discente.isGraduacao() ){
			
			int anoPeriodoMatricula = Integer.parseInt( matricula.getAno() + "" + matricula.getPeriodo() ) ;
			int anoPeriodoAtual = Integer.parseInt( calendario.getAno() + ""  + calendario.getPeriodo() ) ;
			int anoPeriodoIngresso = Integer.parseInt( discente.getAnoIngresso() + ""  + discente.getPeriodoIngresso() ) ;
			
			if( anoPeriodoMatricula > anoPeriodoAtual )
				addMensagemErro("O ano-período não pode ser posterior do que o ano-período vigente (" + calendario.getAno() + "."  + calendario.getPeriodo() + " )" );
			
			if( anoPeriodoMatricula < anoPeriodoIngresso )
				addMensagemErro("O ano-período não pode ser anterior do que o ano-período de ingresso do discente ( " +discente.getAnoIngresso() + "."  + discente.getPeriodoIngresso() + " )" ) ;
			
		}
		
		
		
		
		if( matricula.getSituacaoMatricula() == null || matricula.getSituacaoMatricula().getId() == 0)
			addMensagemErro("Selecione uma situação.");

		// validar se pertence a algum currículo do curso
		TreeSet<Integer> componentesCursoIds =  new TreeSet<Integer>();
		
		if (discente.isRegular()) {
			if(discente.isTecnico() || discente.isFormacaoComplementar())
				componentesCursoIds = getDAO(DiscenteTecnicoDao.class).findComponentesDoCursoByDiscente(discente.getCurso().getId());
			else
				componentesCursoIds = dao.findComponentesDoCursoByDiscente(discente.getCurso().getId());
		} else {
			if (discente.isStricto())
				componentesCursoIds = dao.findComponentesDoCursoByNivelPrograma(discente.getNivel(), discente.getUnidade().getId());
		}
		
		if (!discente.isGraduacao() && !discente.isTecnico() && !componentesCursoIds.contains(matricula.getComponente().getId()))  {
			addMensagemErro("O componente implantado deve pertencer a algum currículo do curso");
		}
		
		if(isPermiteInserirFrequencia()) {
			if(matricula.getFrequenciaImplantadaHistorico() == null) {
				addMensagemErro("Por favor informe a frequência do discente.");
			} else if(matricula.getFrequenciaImplantadaHistorico() < 0 || matricula.getFrequenciaImplantadaHistorico() > 100) {
				addMensagemErro("A frequência deve ser maior ou igual a 0 e menor ou igual a 100.");
			}						
		}
		
		if( hasErrors() )
			return null;		
		
		ParametrosGestoraAcademica parametros = ParametrosGestoraAcademicaHelper.getParametros(matricula.getComponente());
		if(isPermiteInserirFrequencia()) {			
			matricula.setNumeroFaltasPorFrequencia(matricula.getFrequenciaImplantadaHistorico(),parametros.getMinutosAulaRegular());			
		}		

		
		if( "Alterar".equals( getConfirmButton() ) && matricula.getId() > 0 ){ //se for adição de nova matrícula
		
			for( MatriculaComponente mc : matriculasAnteriores ){
				if( mc.equals(matricula) ){
					
					mc.setConceito(matricula.getConceito());
					mc.setAno( matricula.getAno() );
					mc.setAnoFim( matricula.getAnoFim() );
					mc.setMes( matricula.getMes() );
					mc.setMesFim( matricula.getMesFim() );
					mc.setPeriodo( matricula.getPeriodo() );
					mc.setFrequenciaImplantadaHistorico( matricula.getFrequenciaImplantadaHistorico() );
					mc.setNumeroFaltasPorFrequencia(matricula.getFrequenciaImplantadaHistorico(),parametros.getMinutosAulaRegular());
					mc.setSituacaoMatricula( dao.findByPrimaryKey(matricula.getSituacaoMatricula().getId(), SituacaoMatricula.class)  );
					mc.setComponente( matricula.getComponente() );
					mc.setDetalhesComponente( matricula.getDetalhesComponente() );
					mc.setRegistroEntrada( matricula.getRegistroEntrada() );
					matricula.setSelected(true);
					mc.setSelected(true);
				}
			}
			
		}else if( "Alterar".equals( getConfirmButton() ) && matricula.getId() == 0 && matricula.getComponente().getId() > 0 ){ //se for alteração de registro de implantação de matrícula transiente, verificar pelo id do componente
			
			int idComponente = matricula.getComponente().getId();
			
			for (Iterator it = matriculas.iterator(); it.hasNext();) {
				MatriculaComponente mat = (MatriculaComponente) it.next();
				
				if( mat.getComponente().getId() == idComponente ){
					
					MatriculaComponenteHelper.validarMatriculaComponente(discente, matricula, erros);

					if( hasErrors() )
						return null;
					
					it.remove(); //remove o registro q tinha sido adicionado anteriormente (ainda transiente)
					
				}
				
			}
			
			matricula.setDiscente(discente.getDiscente());
			matricula.setSituacaoMatricula( dao.findByPrimaryKey(matricula.getSituacaoMatricula().getId(), SituacaoMatricula.class) );
			matriculas.add(matricula); //adiciona o novo registro
			
			
		} else{ //se for adição de nova matrícula na implantação

			MatriculaComponenteHelper.validarMatriculaComponente(discente, matricula, erros);

			for( MatriculaComponente mc : matriculas ){
				if( mc.getComponente().getId() == matricula.getComponente().getId() ){
					addMensagemErro("O componente selecionado já foi adicionado para implantação.");
					break;
				}
			}
			
			if( hasErrors() )
				return null;
			
			matricula.setDiscente(discente.getDiscente());
			matricula.setSituacaoMatricula( dao.findByPrimaryKey(matricula.getSituacaoMatricula().getId(), SituacaoMatricula.class) );
			matriculas.add(matricula);
			
		}
		
	

		init();

		return null;
	}

	/**
	 * Cancela a edição de um objeto e limpa o formulário.
	 * <br/><br/>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>/app/sigaa.ear/sigaa.war/ensino/implantar_historico/form.jsp
	 * </ul>
	 * @return
	 */
	public String cancelarEdicao(){
		init();
		return forward( getFormPage() );
	}

	/**
	 * Remove uma matrícula da lista de matrículas em componentes curriculares a
	 * serem implementados no histórico do discente. 
	 * <br/><br/>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>/app/sigaa.ear/sigaa.war/ensino/implantar_historico/form.jsp
	 * </ul>
	 * 
	 * @return
	 */
	public String removerMatricula(){

		Integer idComponente = getParameterInt("idComponente");
		if( idComponente == null ){
			addMensagemErro("Nenhuma matrícula selecionada para remoção.");
			return null;
		}
		Iterator<MatriculaComponente> it = matriculas.iterator();
		while (it.hasNext()) {
			MatriculaComponente matricula = it.next();
			if (matricula.getComponente().getId() == idComponente)
				it.remove();
		}

		setConfirmButton("Adicionar");
		return null;
	}
	
	
	/**
	 * Adiciona a matrícula selecionada na lista de matrículas para remover.
	 * <br/><br/>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>/app/sigaa.ear/sigaa.war/ensino/implantar_historico/form.jsp
	 * </ul>
	 * @return
	 */
	public String removerImplantacaoAnterior(){
		
		Integer idMatricula = getParameterInt("idMatriculaAnterior");
		if( idMatricula == null ){
			addMensagemErro("Nenhuma matrícula selecionada para remoção.");
			return null;
		}
		Iterator<MatriculaComponente> it = matriculasAnteriores.iterator();
		while (it.hasNext()) {
			MatriculaComponente mat = it.next();
			if (mat.getId() == idMatricula){
				
				if( matriculasAnterioresParaRemover == null )
					matriculasAnterioresParaRemover =  new ArrayList<MatriculaComponente>();
				
				matriculasAnterioresParaRemover.add(mat);
				
				it.remove();
				
			}
		}

		return null;
		
	}
	
	/**
	 * Carrega o objeto selecionado no formulário para iniciar a alteração
	 * <br/><br/>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>/app/sigaa.ear/sigaa.war/ensino/implantar_historico/form.jsp
	 * </ul>
	 * @return
	 * @throws DAOException
	 */
	public String alterarImplantacaoAnterior() throws DAOException{
		
		Integer idMatricula = getParameterInt("idMatriculaAnterior");
		Integer idComponente = getParameterInt("idComponenteAnterior");
		if( idMatricula == null && idComponente == null ){
			addMensagemErro("Nenhuma matrícula selecionada para alteração.");
			return null;
		}
		
		if( idMatricula != null && idMatricula > 0 ){
			
			matricula = null;
			
			for( MatriculaComponente mc : matriculasAnteriores ){
				if( mc.getId() == idMatricula && mc.isSelected() ){
					matricula = mc;
					break;
				}
			}
			
			if( matricula == null ){
				matricula = getGenericDAO().findByPrimaryKey(idMatricula, MatriculaComponente.class);
				matricula.getComponente().getDetalhes().getChTotal();
				//matricula.setDetalhesComponente( getGenericDAO().findByPrimaryKey(matricula.getDetalhesComponente().getId(), ComponenteDetalhes.class) );
				matricula.getDetalhesComponente().getChTotal();
				matricula.setFrequenciaPorNumeroFaltas( matricula.getNumeroFaltas() );
			}
		} else if( idComponente != null ){
			
			for( MatriculaComponente mc : matriculas ){
				if( mc.getComponente().getId() == idComponente ){
					matricula = mc;
					break;
				}
			}
			
		}

		setConfirmButton("Alterar");
		
		return forward( getFormPage() );
		
	}
	
	/**
	 * Utilizado para verificar se deve inserir frequência ao implantar componente curricular no histórico. 
	 * 
	 * Chamado por:
	 * /sigaa.war/ensino/implantar_historico/form.jsp
	 * @return
	 */
	public boolean isPermiteInserirFrequencia() {		
		if(!ValidatorUtil.isEmpty(discente) && (!ValidatorUtil.isEmpty(discente.getCurso()) || discente.getTipo() == Discente.ESPECIAL )) {
			//Pois não faz sentido frequência para alunos de EAD.
			return ! discente.isDiscenteEad();
		}		
		return false;	
	}
	

	/**
     * Redireciona para a tela com o resultado de discente da busca de discentes.<br/><br/>
	 * 
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 * 		<li>/sigaa.war/ensino/implantar_historico/form.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public String voltarBuscaDiscente() {
		if (getNivelEnsino() != 'L')
			return forward("/graduacao/busca_discente.jsp");
		else 
			return forward("/lato/discente/buscar.jsp");
	}
	
	/**
     * Redireciona para o formulário de dados da implantação de histórico.<br/><br/>
	 * 
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 * 		<li>/sigaa.war/ensino/implantar_historico/resumo.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public String voltar() {
		return forward(JSP_FORM);
	}

	/**
	 * Redireciona para a página de resumo.
	 * <br/><br/>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>/app/sigaa.ear/sigaa.war/ensino/implantar_historico/form.jsp
	 * </ul>
	 * @return
	 */
	public String submeterDados(){

		if( isEmpty(matriculas) && isEmpty(matriculasAnterioresParaRemover) && !possuiMatriculaAlterada(matriculasAnteriores) ){
			addMensagemErro("É necessário adicionar/alterar pelo menos uma matrícula para continuar a implantação.");
			return null;
		}

		return forward( JSP_RESUMO );
	}

	/**
	 * Diz se existe alguma matricula alterada na lista de matriculas anteriores que foi carregada
	 * <br/><br/>
	 * Não é invocado de jsp..
	 * @param matriculas
	 * @return
	 */
	public boolean possuiMatriculaAlterada(List<MatriculaComponente> matriculas){
		for( MatriculaComponente mc : matriculas ){
			if( mc.isSelected() )
				return true;
		}

		return false;
	}
	
	/**
	 * Chama a camada de negócio para persistir o histórico cadastrado.
	 * <br/><br/>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>/app/sigaa.ear/sigaa.war/ensino/implantar_historico/resumo.jsp
	 * </ul>
	 * @throws ArqException 
	 */
	@Override
	public String cadastrar() throws ArqException {

		//setOperacaoAtiva( SigaaListaComando.IMPLANTAR_HISTORICO.getId() );
		//checkOperacaoAtiva( SigaaListaComando.IMPLANTAR_HISTORICO.getId() );
		if (!checkOperacaoAtiva( SigaaListaComando.IMPLANTAR_HISTORICO.getId() )) return cancelar();
		
		if( !confirmaSenha() )
			return null;

		try {
			ImplantarHistoricoMov mov = new ImplantarHistoricoMov();
			
			if( isNotEmpty( matriculasAnterioresParaRemover ) )
				mov.setMatriculasParaRemover(matriculasAnterioresParaRemover);
			
			if( isNotEmpty( matriculasAnteriores ) )
				mov.setMatriculasParaAlterar( matriculasAnteriores );
			
			mov.setCodMovimento(SigaaListaComando.IMPLANTAR_HISTORICO);
			mov.setMatriculas(matriculas);
			mov.setDiscente(discente.getDiscente());
			execute( mov, getCurrentRequest() );
			addMessage("Implantação de histórico realizada com sucesso", TipoMensagemUFRN.INFORMATION );
		} catch (NegocioException e) {
			addMensagens( e.getListaMensagens() );
			e.printStackTrace();
			return null;
		} catch (Exception e) {
			notifyError(e);
			e.printStackTrace();
			addMensagemErro("Erro de execução: " + e.getMessage());
			return null;
		}

		return cancelar();

	}

	/**
	 * Retorna as situações de matrícula que o usuário pode selecionar
	 * na operação de implantar histórico.
	 * <br/><br/>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>/app/sigaa.ear/sigaa.war/ensino/implantar_historico/form.jsp
	 * </ul>
	 * @return
	 */
	public Collection<SelectItem> getSituacoesMatriculas(){
		ArrayList<SelectItem> situacoes = new ArrayList<SelectItem>();
		situacoes.add( new SelectItem( SituacaoMatricula.APROVADO.getId(), SituacaoMatricula.APROVADO.getDescricao()  )  );
		situacoes.add( new SelectItem( SituacaoMatricula.APROVEITADO_CUMPRIU.getId(), SituacaoMatricula.APROVEITADO_CUMPRIU.getDescricao()  )  );
		situacoes.add( new SelectItem( SituacaoMatricula.APROVEITADO_DISPENSADO.getId(), SituacaoMatricula.APROVEITADO_DISPENSADO.getDescricao()  )  );
		situacoes.add( new SelectItem( SituacaoMatricula.APROVEITADO_TRANSFERIDO.getId(), SituacaoMatricula.APROVEITADO_TRANSFERIDO.getDescricao()  )  );
		situacoes.add( new SelectItem( SituacaoMatricula.TRANCADO.getId(), SituacaoMatricula.TRANCADO.getDescricao()  )  );
		situacoes.add( new SelectItem( SituacaoMatricula.REPROVADO_FALTA.getId(), SituacaoMatricula.REPROVADO_FALTA.getDescricao()  )  );
		situacoes.add( new SelectItem( SituacaoMatricula.REPROVADO_MEDIA_FALTA.getId(), SituacaoMatricula.REPROVADO_MEDIA_FALTA.getDescricao()  )  );
		situacoes.add( new SelectItem( SituacaoMatricula.REPROVADO.getId(), SituacaoMatricula.REPROVADO.getDescricao()  )  );
		return situacoes;
	}
	
	/** 
	 * Método invocado pelo MBean de busca de discente, após setar o discente.<br /><br />
	 * Não invocado por JSP.
	 * @see br.ufrn.sigaa.ensino.graduacao.jsf.OperadorDiscente#selecionaDiscente()
	 */
	public String selecionaDiscente() throws ArqException {
		init();
		setOperacaoAtiva( SigaaListaComando.IMPLANTAR_HISTORICO.getId() );
		
		discenteStricto = null;
		calendario = null;
		
		matriculas = new ArrayList<MatriculaComponente>();
		parametros = ParametrosGestoraAcademicaHelper.getParametros(discente);
		
		MatriculaImplantadaDao dao = getDAO(MatriculaImplantadaDao.class);
		
		calendario = CalendarioAcademicoHelper.getCalendario(discente);
		discenteStricto = dao.findByPrimaryKey(discente.getId(), DiscenteStricto.class) ;
		
		matriculasAnteriores = (List<MatriculaComponente>) dao.findImplantacoesByDiscente(discente, true);
		
		return forward( getFormPage() );
	}

	/**
	 *  Define o diretório base dos formulários.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>Não Invocado por JSP.</li>
	 * </ul>
	 * 
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#getDirBase()
	 */
	@Override
	public String getDirBase() {
		return "/ensino/implantar_historico";
	}

	/**
	 * Retorna o discente que terá os componentes implantados no histórico.
	 * 
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>Não Invocado por JSP.</li>
	 * </ul>
	 * 
	 * @return
	 */
	public DiscenteAdapter getDiscente() {
		return discente;
	}

	/** 
	 * Seta o discente que terá os componentes implantados no histórico. Invocado pelo MBean de busca de discente.
	 *  
	 * @see br.ufrn.sigaa.ensino.graduacao.jsf.OperadorDiscente#setDiscente(br.ufrn.sigaa.pessoa.dominio.Discente)
	 */
	public void setDiscente(DiscenteAdapter discente) {
		this.discente = discente;
	}

	/** 
	 * Retorna a lista das matrículas em componente curricular que será implantada no histórico.
	 * 
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>Não Invocado por JSP.</li>
	 * </ul>
	 * 
	 * @return
	 */
	public List<MatriculaComponente> getMatriculas() {
		return matriculas;
	}
	
	
	/** 
	 * Retorna o dataModel da lista das matrículas em componente curricular que será implantada no histórico.
	 * 
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>Não Invocado por JSP.</li>
	 * </ul>
	 * 
	 * @return
	 */
	public ListDataModel getMatriculasDataModel() {
		return new ListDataModel(matriculas);
	}
	
	public ListDataModel getMatriculasAnterioresDataModel() {
		return new ListDataModel(matriculasAnteriores);
	}

	/** 
	 * Seta a lista das matrículas em componente curricular que será implantada no histórico.
	 * 
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>Não Invocado por JSP.</li>
	 * </ul>
	 * 
	 * @param matriculas
	 */
	public void setMatriculas(List<MatriculaComponente> matriculas) {
		this.matriculas = matriculas;
	}

	/** 
	 * Retorna a matrícula em componente curricular que será implantada no histórico. 
	 * 
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>Não Invocado por JSP.</li>
	 * </ul>
	 * 
	 * @return
	 */
	public MatriculaComponente getMatricula() {
		return matricula;
	}

	/** 
	 * Seta a matrícula em componente curricular que será implantada no histórico. 
	 * 
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>Não Invocado por JSP.</li>
	 * </ul>
	 * 
	 * @param matricula
	 */
	public void setMatricula(MatriculaComponente matricula) {
		this.matricula = matricula;
	}

	public ParametrosGestoraAcademica getParametros() {
		return parametros;
	}

	public void setParametros(ParametrosGestoraAcademica parametros) {
		this.parametros = parametros;
	}

	/**
	 * Método responsável por retornar a descrição em forma de texto da metodologia de avaliação 
	 * utilizada nos componentes curriculares.
	 * <br/><br/>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>/SIGAA/app/sigaa.ear/sigaa.war/ensino/implantar_historico/form.jsp
	 * 	<li>/SIGAA/app/sigaa.ear/sigaa.war/ensino/implantar_historico/resumo.jsp
	 * </ul>
	 * @return
	 */
	public String getDescricaoMetodoAvaliacao(){
		if(parametros != null){
			if(parametros.getMetodoAvaliacao() == MetodoAvaliacao.NOTA)
				return "Nota";
			else if(parametros.getMetodoAvaliacao() == MetodoAvaliacao.CONCEITO)
				return "Conceito";
			else if(parametros.getMetodoAvaliacao() == MetodoAvaliacao.COMPETENCIA)
				return "Competência";
		}
		return "";
	}
	
	/**
	 * Coleção de SelectItems padrão para ano na implantação de matrículas no histórico do discente.
	 * 
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>Não Invocado por JSP.</li>
	 * </ul>
	 * 
	 */
	@Override
	public List<SelectItem> getAnos() throws Exception {
		List<SelectItem> anos = new ArrayList<SelectItem>();
		
		for (int i = calendario.getAno() + 1; i >= ANO_INICIO_STRICTO; i--) {
			anos.add(new SelectItem(String.valueOf(i), String.valueOf(i)));
		}
		return anos;
	}
	
	
	/** 
	 * Método responsável por verificar se o método de avaliação do discente é por nota
	 *
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>Não Invocado por JSP.</li>
	 * </ul>
	 * 
	 */
	public boolean isNota(){
		return parametros != null && parametros.getMetodoAvaliacao() == MetodoAvaliacao.NOTA;
	}
	
	/** Método responsável por verificar se o método de avaliação do discente é por conceito*/
	public boolean isConceito(){
		return parametros != null && parametros.getMetodoAvaliacao() == MetodoAvaliacao.CONCEITO;
	}
	
	/** Método responsável por verificar se o método de avaliação do discente é por competência
	 * 
 	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>Não Invocado por JSP.</li>
	 * </ul>
	 * 
	 */
	public boolean isCompetencia(){
		return parametros != null && parametros.getMetodoAvaliacao() == MetodoAvaliacao.COMPETENCIA;
	}

	public List<MatriculaComponente> getMatriculasAnteriores() {
		return matriculasAnteriores;
	}

	public void setMatriculasAnteriores(
			List<MatriculaComponente> matriculasAnteriores) {
		this.matriculasAnteriores = matriculasAnteriores;
	}

	public List<MatriculaComponente> getMatriculasAnterioresParaRemover() {
		return matriculasAnterioresParaRemover;
	}

	public void setMatriculasAnterioresParaRemover(
			List<MatriculaComponente> matriculasAnterioresParaRemover) {
		this.matriculasAnterioresParaRemover = matriculasAnterioresParaRemover;
	}


}
