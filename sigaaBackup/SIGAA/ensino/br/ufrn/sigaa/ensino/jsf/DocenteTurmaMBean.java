/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Created on 27/05/2010
 *
 */
package br.ufrn.sigaa.ensino.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;
import static br.ufrn.arq.util.ValidatorUtil.validateRange;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.CollectionUtils;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.Formatador;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.rh.dominio.Ativo;
import br.ufrn.sigaa.arq.dao.avaliacao.AvaliacaoInstitucionalDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.dominio.ParametrosGestoraAcademica;
import br.ufrn.sigaa.ensino.dominio.DocenteTurma;
import br.ufrn.sigaa.ensino.dominio.HorarioDocente;
import br.ufrn.sigaa.ensino.dominio.HorarioTurma;
import br.ufrn.sigaa.ensino.dominio.SituacaoTurma;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.graduacao.dominio.SolicitacaoTurma;
import br.ufrn.sigaa.ensino.graduacao.jsf.TurmaGraduacaoMBean;
import br.ufrn.sigaa.ensino.latosensu.dominio.CorpoDocenteCursoLato;
import br.ufrn.sigaa.ensino.negocio.TurmaValidator;
import br.ufrn.sigaa.ensino.stricto.dominio.EquipePrograma;
import br.ufrn.sigaa.ensino.stricto.jsf.TurmaStrictoSensuMBean;
import br.ufrn.sigaa.pessoa.dominio.DocenteExterno;
import br.ufrn.sigaa.pessoa.dominio.Servidor;

/** Controller respons�vel por definir docentes em uma turma.
 * 
 * @author �dipo Elder F. Melo
 *
 */
@Component("docenteTurmaBean")
@Scope("request")
public class DocenteTurmaMBean extends SigaaAbstractController<Turma> {
	
	// TIPOS DE BUSCA DE DOCENTES NA TELA DE ADICIONAR DOCENTE
	/** Define se o usu�rio busca por docentes do programa de p�s gradua��o. */
	public static final int DOCENTE_PROGRAMA = 1;
	/** Define se o usu�rio busca por docentes externos ao programa. */
	public static final int DOCENTE_EXTERNO_PROGRAMA = 2;
	/** Define se o usu�rio busca por docentes externos. */
	public static final int DOCENTES_EXTERNOS = 3;
	/** Define se o usu�rio busca por docentes da institui��o. */
	public static final int DOCENTES_TURMA = 4;
	
	/** Define o link para o formul�rio de defini��o de docentes da turma.*/
	public static final String JSP_DOCENTES = "/ensino/turma/docentes.jsp";
	
	/** Define o link para o formul�rio de defini��o de docentes da turma do n�vel de gradua��o.*/
	public static final String JSP_DOCENTES_GRADUACAO = "/graduacao/turma/docentes_graduacao.jsp";
	
	/** Controller utilizado para definir os docentes da turma. */
	private OperadorDocenteTurma mBean;
	/** Nome da opera��o (Para utiliza��o no t�tulo da p�gina de busca do componente curricular) */
	private String tituloOperacao;
	
	/** Tipo de busca por docente a ser realizada. */
	private int tipoBuscaDocente;
	/** Docente que leciona na turma. */
	private DocenteTurma docenteTurma;
	/** DataModel usado na exibi��o dos docentes da turma */
	private DataModel modelDocentesTurmas = new ListDataModel();
	/** Docente, membro do programa, a ser adicionado � turma. */
	private EquipePrograma equipe = new EquipePrograma();
	/** Cole��o de turmas criadas para cada subunidade quando a disciplina � de bloco */
	private Collection<Turma> turmasSubunidades = new ArrayList<Turma>();
	/** Indica se o cadastro de turmas � de ensino a dist�ncia. */
	private boolean turmaEad;
	/** Chave prim�ria do Corpo Docente Lato Sensu, carregado no Combo */
	private int idCorpoDocente;
	
	/** Data de in�cio do per�odo letivo da turma. */
	private Date dataInicio;
	/** Data de fim do per�odo letivo da turma. */
	private Date dataFim;
	/** Mapa utilizado para auxiliar a montagem da String de hor�rio da turma. */
	private Map<String, Map<Short, Map<Character, Map<Short, HorarioTurma>>>> mapaHorarios;
	
	/** Mapa para controle dos grupos de docentes da turma. */
	private Map<Integer, Set<DocenteTurma>> mapaGrupoDocente;
	
	/** Mapa para controle dos grupos de docentes da turma em dataModel utilizados na View. */
	private Map<Integer, DataModel> mapaGrupoDocenteDataModel;
	
	/** Limpa os atributos deste controller. */
	protected void clear() {
		docenteTurma = new DocenteTurma();
		docenteTurma.setDocenteExterno(new DocenteExterno());
		modelDocentesTurmas = new ListDataModel();
		setReadOnly(false);
	}

	/**
	 * Cancela a opera��o e volta para a listagem das turmas, caso a opera��o
	 * seja de altera��o ou remo��o de uma turma.<br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/ensino/turma/docentes.jsp</li>
	 * </ul>
	 */
	public String cancelar() {
		TurmaGraduacaoMBean graduacaoMBeam = (TurmaGraduacaoMBean) getMBean("turmaGraduacaoBean");
		if(graduacaoMBeam.getOperacaoTurma() == TurmaGraduacaoMBean.ATENDER_SOLICITACAO_TURMA){
			return graduacaoMBeam.cancelar();
		}
		TurmaStrictoSensuMBean strictoBean = getMBean("turmaStrictoSensuBean");
		if(strictoBean.getConfirmButton().equals("Alterar")) {
			BuscaTurmaMBean mBean = getMBean("buscaTurmaBean");
			try {
				return mBean.buscarGeral();
			} catch (DAOException e) {
				return super.cancelar();
			}
		}
		return super.cancelar();
	}

	/** Ativa o controller para popular os docentes de uma turma, passada por par�metro.<br/>M�todo n�o invocado por JSP�s.
	 * @param mBean Bean para o qual dever� ser passado os docentes definidos na turma.
	 * @param tituloOperacao t�tulo da opera��o onde est� definindo o docente da turma. Exemplo: "Cadastro de Turma"
	 * @param turma turma para o qual se quer definir os docentes
	 * @param turmaEad indica se a turma � de ensino � dist�ncia
	 * @return
	 */
	public String populaDocentesTurma(OperadorDocenteTurma mBean, String tituloOperacao, Turma turma, boolean turmaEad){
		clear();
		this.mBean = mBean;
		this.tituloOperacao = tituloOperacao;
		this.obj = turma;
		this.turmaEad = turmaEad;
		ArrayList<DocenteTurma> listaDocenteturma = CollectionUtils.toList(obj.getDocentesTurmas());
		modelDocentesTurmas = new ListDataModel(listaDocenteturma);
		popularGrupoDocentes(listaDocenteturma);
		
		return formDocente();
	}
	
		/**
	 * M�todo respons�vel por atualizar o mapa com os grupos de docentes.
	 * @param listaDocenteturma 
	 */
	private void popularGrupoDocentes(ArrayList<DocenteTurma> listaDocenteturma) {
		mapaGrupoDocente = new HashMap<Integer, Set<DocenteTurma>>();
		mapaGrupoDocenteDataModel = new HashMap<Integer, DataModel>();
		
		for (int i = 1; i <= obj.getDisciplina().getNumMaxDocentes(); i++) {
			for (DocenteTurma d : listaDocenteturma){
				d.setGrupoDocente( d.getGrupoDocente()!=null?d.getGrupoDocente():i );	
				if (!mapaGrupoDocente.containsKey(d.getGrupoDocente()))
					mapaGrupoDocente.put(d.getGrupoDocente(), new HashSet<DocenteTurma>()); 
				
				mapaGrupoDocente.get(d.getGrupoDocente()).add(d); 
			}
		}	
		
		for (int i = 1; i <= mapaGrupoDocente.size(); i++) {
			if (!mapaGrupoDocenteDataModel.containsKey(i)) { 
				mapaGrupoDocenteDataModel.put(i, new ListDataModel(CollectionUtils.toList(mapaGrupoDocente.get(i)))); 
			}
		}	

	}
	
	/**
	 * @param evt 
	 * @throws DAOException 
	 * 
	 */
	public void atualizaCHDocenteGrupo(ActionEvent evt) throws DAOException {
		
		ListaMensagens lista = new ListaMensagens();
		
		Integer grupo = (Integer) evt.getComponent().getAttributes().get("grupo");
		
		DocenteTurma dtModificado = (DocenteTurma) mapaGrupoDocenteDataModel.get(grupo).getRowData();
	
		if (!obj.isGraduacao() && (dtModificado.getChDedicadaPeriodo() == null || dtModificado.getChDedicadaPeriodo() <= 0))
			return;
		
		ParametrosGestoraAcademica params = getParametrosAcademicos();
		boolean permiteCHCompartilhada = false;
		if (params != null)
			permiteCHCompartilhada = params.isPermiteChCompartilhada();
		
		if (!obj.isGraduacao())
			permiteCHCompartilhada = permiteCHCompartilhada || obj.getDisciplina().getDetalhes().isPermiteChCompartilhada();			
		
		TurmaValidator.validaCHDocenteTurma(obj, dtModificado, permiteCHCompartilhada, lista, false, null, grupo);
		
		if (!lista.isEmpty()) {
			addMensagensAjax(lista);
			return;
		}
		
		for (DocenteTurma dt : obj.getDocentesTurmas()) {
			if (dt.equals(dtModificado))
				dt.setChDedicadaPeriodo(dtModificado.getChDedicadaPeriodo());
		}
		
	}
	
	/**
	 * Submete a p�gina de controle de docentes da turma.<br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>sigaa.war/graduacao/turma/docentes.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String submeterDocentes() throws ArqException {
		validaDocentesTurma(erros);
		
		if (hasOnlyErrors())
			return null;
		
		if (  mBean instanceof TurmaGraduacaoMBean && ((TurmaGraduacaoMBean) mBean).getSolicitacao() != null ) {
			TurmaGraduacaoMBean turmaGradMBean= ((TurmaGraduacaoMBean) mBean);
			turmaGradMBean.setSolicitacao(
					getGenericDAO().findAndFetch(turmaGradMBean.getSolicitacao().getId(), 
							SolicitacaoTurma.class, "discentes") );
			turmaGradMBean.isExibeDiscentesSolicitantes();
		}
		return mBean.defineDocentesTurma(obj.getDocentesTurmas());
	}

	/**
	 * Retorna o controle ao controller anterior.<br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/ensino/turma/docentes.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public String voltarPassoAnterior() {
		return mBean.definicaoDocenteTurmaVoltar();
	}
	
	/** Valida os dados do docente inclu�do na turma.<br/>M�todo n�o invocado por JSP�s.
	 * @param erros
	 * @throws DAOException
	 */
	public void validaDocentesTurma(ListaMensagens erros) throws DAOException {
		if (!isTurmaEad())
			validarCHDocente(erros);
		
		if (erros.isErrorPresent())
			return;
		
		verificarNovosDocentes();

		
		// Validando se os hor�rios dos docentes da turma s�o compat�veis com os hor�rios das outras turmas do docente
		if( obj.isAberta() ){
			HashMap<Integer, ParametrosGestoraAcademica> cacheParam = new HashMap<Integer, ParametrosGestoraAcademica>();
			
			for( DocenteTurma dt : obj.getDocentesTurmas() ){
				TurmaValidator.validaHorariosDocenteTurma(obj, dt, erros, getUsuarioLogado(), cacheParam);
			}
		}

		if( obj.getDisciplina().isAceitaSubturma() && obj.getTurmaAgrupadora() != null ){

			// A valida��o da carga hor�ria dos docentes deve considerar a carga
			// hor�ria de aulas te�ricas da disciplina de forma comum e a carga
			// hor�ria de laborat�rio separada, para cada sub-turma relacionada.
			// Por exemplo, uma disciplina com 60h te�ricas e 90h de
			// laborat�rio, organizada em 3 sub-turmas deve ter a carga m�xima
			// geral dos docentes validada para n�o ultrapassar (60h + 3*90h). A cada nova
			// sub-turma cadastrada deve-se levar em conta a CH restante dentro do conjunto de sub-turmas.
			int maxHorarios = obj.getDisciplina().getDetalhes().getChAula();
			maxHorarios += obj.getDisciplina().getDetalhes().getChEad();
			
			obj.setTurmaAgrupadora(getGenericDAO().refresh( obj.getTurmaAgrupadora()));
			int quantidadeSubturmas = obj.getTurmaAgrupadora().getSubturmas().size();
			int totalHorariosDocentesSubturmas = 0;
			
			for( Turma subturma : obj.getTurmaAgrupadora().getSubturmas() ){
				// n�o contabiliza a turma atual, pois os valores da CH dos docentes pode ter sido alterado.
				if (subturma.getId() != obj.getId()) {
					for( DocenteTurma dt : subturma.getDocentesTurmas() ){
						totalHorariosDocentesSubturmas += dt.getChDedicadaPeriodo();
					}
				}
			}
			// contabiliza a CH dos docentes da turma editada
			for( DocenteTurma dt : obj.getDocentesTurmas() ){
				totalHorariosDocentesSubturmas += dt.getChDedicadaPeriodo();
			}
			// Para cadastro deve ser contabilizado o ch da nova
			if (obj.getId() == 0 || (!obj.getTurmaAgrupadora().getSubturmas().contains(obj))) {
				quantidadeSubturmas++;
			}
			
			maxHorarios += obj.getDisciplina().getDetalhes().getChLaboratorio() * (quantidadeSubturmas); 
			if( totalHorariosDocentesSubturmas > maxHorarios ){
				erros.addErro("A valida��o da carga hor�ria dos docentes de sub-turmas considera a carga hor�ria de aulas te�ricas " 
						+ "da disciplina de forma comum e a carga hor�ria de laborat�rio separada, para cada sub-turma relacionada. " 
						+ "Esta disciplina possui " 
						+ obj.getDisciplina().getChAula() + "h te�ricas" 
						+ (obj.getDisciplina().getDetalhes().getChEad() > 0 ? ", "+obj.getDisciplina().getDetalhes().getChEad()+"h de Ead " : "")
						+ " e " + obj.getDisciplina().getChLaboratorio() + "h de laborat�rio, " 
						+ "organizada em " + quantidadeSubturmas + " sub-turmas, portanto, a carga hor�ria " 
						+ "dos docentes de todas as sub-turmas n�o pode ultrapassar " 
						+ maxHorarios  
						+ "h ( " + obj.getDisciplina().getChAula() + "h + " + (obj.getDisciplina().getDetalhes().getChEad() > 0 ? obj.getDisciplina().getDetalhes().getChEad()+"h + " : "") + quantidadeSubturmas + " * " + obj.getDisciplina().getChLaboratorio() + "h). "
						+ "A cada nova sub-turma cadastrada deve-se "
						+ "levar em conta a CH restante dentro do conjunto de sub-turmas.");
			}
			
		}
		
		if (erros.isErrorPresent())
			return;

		if( isEmpty(obj.getSituacaoTurma()) || obj.isAberta() ){
			if (obj.getDocentesTurmas().isEmpty())
				obj.setSituacaoTurma(new SituacaoTurma(SituacaoTurma.A_DEFINIR_DOCENTE));
			else
				obj.setSituacaoTurma(new SituacaoTurma(SituacaoTurma.ABERTA));
		}
	}
	
	/**
	 * Verifica se os docentes que foram inseridos na turma j� n�o estavam cadastrados,
	 * tendo sido removido e inserido apenas para realizar uma mudan�a de carga hor�ria.
	 * Caso isso aconte�a, o id original ser� setado na nova entidade DocenteTurma.
	 * S� DEVE SER TESTADO NA ALTERA��O.
	 * @param dao
	 * @throws DAOException
	 */
	private void verificarNovosDocentes() throws DAOException {

		if( obj.getId() == 0 )
			return;

		AvaliacaoInstitucionalDao dao = null;
		try {
			dao = getDAO( AvaliacaoInstitucionalDao.class );

			Turma turmaOriginal = dao.findByPrimaryKey( obj.getId() , Turma.class);
			if( isEmpty(turmaOriginal.getDocentesTurmas())  )
				return;
	
			// atribui o mesmo ID para o docente inserido, caso ele seja o mesmo j� cadastrado anteriormente
			// com isso, ao inv�s de inserir, ser� atualizado.
			if( obj.getId() > 0 && !isEmpty( obj.getDocentesTurmas() ) ){
				for (DocenteTurma dtNew : obj.getDocentesTurmas()) {
					for (DocenteTurma dtOrig : turmaOriginal.getDocentesTurmas()) {
						if( (dtNew.getDocente() != null && dtOrig.getDocente() != null)
								&& ( dtNew.getDocente().getId() == dtOrig.getDocente().getId() )
								&& (dtNew.getId() == 0 ) 
								|| (dtNew.getDocenteExterno() != null && dtOrig.getDocenteExterno() != null)
								&& ( dtNew.getDocenteExterno().getId() == dtOrig.getDocenteExterno().getId() )
								&& (dtNew.getId() == 0 ) ){
							dtNew.setId( dtOrig.getId() );
							break;
						}
					}
				}
			}
	
			// Verificando algum docente foi removido da cole��o
			// S� pode remover docentes que n�o possuem avalia��o institucional
			if( !isEmpty( turmaOriginal.getDocentesTurmas() ) ){
				List<DocenteTurma> lista = br.ufrn.arq.util.CollectionUtils.toList(obj.getDocentesTurmas());
				for( DocenteTurma dt : turmaOriginal.getDocentesTurmas() ){
					// a lista de docentes do banco bem com docente ou docenteExterno nulo.
					if (dt.getDocente() == null) dt.setDocente(new Servidor());
					if (dt.getDocenteExterno() == null) dt.setDocenteExterno(new DocenteExterno());
					// percorre a lista e verifica se o docente foi removido e reinserido.
					// n�o d� pra fazer !lista.contains(dt) pois os hor�rios podem ser diferentes e retornaria false
					// a verifica��o deve ser feita pelo ID do DocenteTurma, ou do Docente e DocenteExterno
					boolean contem = false;
					for (DocenteTurma dtNew : lista) {
						if( EqualsUtil.testEquals(dt, dtNew, "id") || 
								EqualsUtil.testEquals(dt.getDocente(), dtNew.getDocente(), "id") &&
								EqualsUtil.testEquals(dt.getDocenteExterno(), dtNew.getDocenteExterno(), "id"))
							contem = true;
					}
					// se o discente foi removido da turma, verifica se h� avalia��o preenchida para ele.
					if (!contem){
						long qtdAvaliacoes = dao.countAvaliacaoByDocenteTurmaAnoPeriodo(dt, obj.getAno(), obj.getPeriodo());
						if( qtdAvaliacoes > 0 ){
							if (isUserInRole(SigaaPapeis.ADMINISTRADOR_DAE))
								addMensagemWarning("Ao remover o docente " + dt.getDocenteNome() + " desta, remover� tamb�m as Avalia��es Institucionais do docente j� realizadas.");
							else
								addMensagemErro("N�o � poss�vel remover " + dt.getDocenteNome() + " desta turma pois j� foi realizado avalia��o institucional para ele.");
						}
					}
				}
			}else{
				for( DocenteTurma dt : obj.getDocentesTurmas() ){
					long qtdAvaliacoes = dao.countAvaliacaoByDocenteTurmaAnoPeriodo(dt, obj.getAno(), obj.getPeriodo());
					if( qtdAvaliacoes > 0 ){
						if (isUserInRole(SigaaPapeis.ADMINISTRADOR_DAE))
							addMensagemWarning("Ao remover o docente " + dt.getDocenteNome() + " desta, remover� tamb�m as Avalia��es Institucionais do docente j� realizadas.");
						else
							addMensagemErro("N�o � poss�vel remover " + dt.getDocenteNome() + " desta turma pois j� foi realizado avalia��o institucional para ele.");
					}
	
				}
			}
		} finally {
			if (dao != null) dao.close();
		}
	}
	
	/** Valida a carga hor�ria do docente.
	 * @throws DAOException
	 */
	private void validarCHDocente(ListaMensagens erros) throws DAOException {
		for (DocenteTurma dt : obj.getDocentesTurmas()) {
			if( dt.getChDedicadaPeriodo() == null || dt.getChDedicadaPeriodo() <= 0 ) {
				erros.addErro("Entre com a carga hor�ria do docente.");
				return;
			}
		}
		
		ParametrosGestoraAcademica params = getParametrosAcademicos();
		boolean permiteCHCompartilhada = false;
		if (params != null)
			permiteCHCompartilhada = params.isPermiteChCompartilhada();
		
		//verificando se a gestora ou o componente permite CH compartilhada 
		if (!obj.isGraduacao())
			permiteCHCompartilhada = permiteCHCompartilhada || obj.getDisciplina().getDetalhes().isPermiteChCompartilhada();		
		
		for (DocenteTurma dt : obj.getDocentesTurmas()) {
			TurmaValidator.validaCHDocenteTurma(obj, dt, permiteCHCompartilhada, erros, false, null, null);
		}
		
	}
	
	public boolean isPermiteHorarioDocenteFlexivel() {
		return !obj.isTurmaEnsinoIndividual() && obj.getDisciplina().isPermiteHorarioDocenteFlexivel();
	}
	
	/**
	 * Adiciona um docente selecionado � uma turma, realiza tamb�m as valida��es
	 * necess�rias de choque de hor�rio, etc.<br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>sigaa.war/graduacao/turma/docentes.jsp</li>
	 * </ul>
	 * 
	 * @param evt
	 * @throws DAOException
	 */
	public String adicionarDocenteTurma(ActionEvent evt) throws DAOException {
		
		ListaMensagens mensagens = new ListaMensagens();
		
		GenericDAO dao = null;
		try{
			dao = getGenericDAO();
			boolean latoMesmoPrograma = isLato() && idCorpoDocente != 0;
			
			//verificando se a gestora ou o componente permite CH compartilhada 
			boolean permiteCHCompartilhada = false;
			if (getParametrosAcademicos() != null)
				permiteCHCompartilhada = getParametrosAcademicos().isPermiteChCompartilhada();			
			if (!obj.isGraduacao())
				permiteCHCompartilhada = permiteCHCompartilhada || obj.getDisciplina().getDetalhes().isPermiteChCompartilhada();
			
			// Valida��o dos dados do formul�rio 
			if (isTurmaEad()) {
				docenteTurma.setChDedicadaPeriodo(0);
			} // Turmas de gradua��o cadastram a porcentagem da CH. 
			else if (obj.isGraduacao() && docenteTurma.getChDedicadaPeriodo() == null) {
				addMensagemErro("N�o foi poss�vel calcular o valor real da carga hor�ria dedicada ao per�odo.");	
			} else if( docenteTurma.getChDedicadaPeriodo() == null || docenteTurma.getChDedicadaPeriodo() <= 0 )				
				addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Carga Hor�ria");	
			
			if( tipoBuscaDocente == DOCENTE_EXTERNO_PROGRAMA && docenteTurma.getDocente().getId() <= 0 && !latoMesmoPrograma )
				addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Docentes");
			if( tipoBuscaDocente == DOCENTE_PROGRAMA && equipe.getId() <= 0 && !latoMesmoPrograma)
				addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Docente");
			if( tipoBuscaDocente == DOCENTES_EXTERNOS && docenteTurma.getDocenteExterno().getId() <= 0 && !latoMesmoPrograma )
				addMensagemErro("Selecione um docente externo.");
			if( docenteTurma.getDocente().getId() <= 0 && !isSelecionarDocentesPrograma())
				addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Docente");
			if (isPermiteHorarioDocenteFlexivel() && !isTurmaEad() && !latoMesmoPrograma ) {
				ValidatorUtil.validateRequired(dataInicio, "In�cio do Per�odo", erros);
				ValidatorUtil.validateRequired(dataFim, "Fim do Per�odo", erros);
				ValidatorUtil.validateMinValue(dataInicio, obj.getDataInicio(), "Data Inicial do Per�odo", erros);
				ValidatorUtil.validateMaxValue(dataInicio, obj.getDataFim(), "Data Inicial do Per�odo", erros);
				ValidatorUtil.validateMinValue(dataFim, obj.getDataInicio(), "Data Final do Per�odo", erros);
				ValidatorUtil.validateMaxValue(dataFim, obj.getDataFim(), "Data Final do Per�odo", erros);
				ValidatorUtil.validateMinValue(dataFim, dataInicio, "Data Final do Per�odo", erros);
			}
			
			if (!mensagens.isEmpty())
				addMensagens(mensagens);
			
			if (hasErrors())
				return null;
			
			if ( latoMesmoPrograma ) {
				CorpoDocenteCursoLato corpoDoceLato = dao.findByPrimaryKey(idCorpoDocente, CorpoDocenteCursoLato.class);
				if (corpoDoceLato.getServidor() != null) {
					docenteTurma.setDocente( corpoDoceLato.getServidor() );
					docenteTurma.setDocenteExterno(null);
				} else {
					docenteTurma.setDocenteExterno( corpoDoceLato.getDocenteExterno() );
					docenteTurma.setDocente( null );
				}
			}
		
			String tipoDocente = getParameter("tipoAjaxDocente_1");
			if( getAcessoMenu().isAlgumUsuarioStricto() && tipoBuscaDocente == DOCENTE_PROGRAMA && !isLato() ) {
				equipe = dao.findByPrimaryKey( equipe.getId() , EquipePrograma.class);
				if( equipe.isServidorUFRN() ){
					docenteTurma.setDocente(equipe.getServidor());
					docenteTurma.setDocenteExterno(null);
				}else{
					docenteTurma.setDocenteExterno( equipe.getDocenteExterno() );
					docenteTurma.setDocente(null);
				}
			} else if( tipoBuscaDocente == DOCENTES_EXTERNOS && docenteTurma.getDocenteExterno().getId() > 0 ) {
				docenteTurma.setDocenteExterno(dao.refresh (docenteTurma.getDocenteExterno()));
				docenteTurma.setDocente(null);
			} else if( "externo".equals(tipoDocente) ){
				docenteTurma.setDocenteExterno( new DocenteExterno( docenteTurma.getDocente().getId() ) );
				docenteTurma.setDocente( null );
				docenteTurma.setDocenteExterno( dao.refresh(docenteTurma.getDocenteExterno()) );
			}
	
			if( (docenteTurma.getDocente() == null && docenteTurma.getDocenteExterno() == null)
					|| (docenteTurma.getDocente() != null && docenteTurma.getDocente().getId() == 0 )
					&& (docenteTurma.getDocenteExterno() != null && docenteTurma.getDocenteExterno().getId() == 0 )){
				addMensagemErro("Selecione um docente.");
				return null;
			}
	
			// hor�rio do docente
			List<HorarioDocente> horarios = new ArrayList<HorarioDocente>(0);
			if (!isTurmaEad()) {
				// define o hor�rio do docente informado pelo usu�rio, caso seja permitido hor�rio flex�vel do docente
				if (isPermiteHorarioDocenteFlexivel()) {
					for (String periodo : mapaHorarios.keySet()) {
						Map<Short, Map<Character, Map<Short, HorarioTurma>>> grupoPeriodo = mapaHorarios.get(periodo);
						for (Short tipo : grupoPeriodo.keySet()) {
							Map<Character, Map<Short, HorarioTurma>> grupoTurno = grupoPeriodo.get(tipo);
							for (Character dia : grupoTurno.keySet()) {
								Map<Short, HorarioTurma> grupoDia = grupoTurno.get(dia);
								for (Short ordem : grupoDia.keySet()) {
									HorarioTurma ht = grupoDia.get(ordem);
									if (ht.isSelecionado()
											&& CalendarUtils.isIntervalosDeDatasConflitantes(ht.getDataInicio(), ht.getDataFim(), dataInicio, dataFim)) {
										HorarioDocente hd = new HorarioDocente();
										hd.setDataFim(ht.getDataFim().before(dataFim) ? ht.getDataFim() : dataFim);
										hd.setDataInicio(ht.getDataInicio().before(dataInicio) ? dataInicio : ht.getDataInicio());
										hd.setDia(ht.getDia());
										hd.setHorario(ht.getHorario());
										hd.setTipo(ht.getTipo());
										horarios.add(hd);
									}
								}
							}
						}
					}
					ValidatorUtil.validateRequired(horarios, "Hor�rios no Per�odo", erros);
				} else {
					// hor�rio do docente � o hor�rio da turma, caso n�o seja permitido hor�rio flex�vel do docente
					for (HorarioTurma ht : obj.getHorarios()){
						HorarioDocente hd = new HorarioDocente();
						hd.setDataFim(ht.getDataFim());
						hd.setDataInicio(ht.getDataInicio());
						hd.setDia(ht.getDia());
						hd.setHorario(ht.getHorario());
						hd.setTipo(ht.getTipo());
						horarios.add(hd);
					}
				}
			}
			if (hasErrors())
				return null;
			// Valida o hor�rio do docente
			docenteTurma.setDocente( dao.refresh( docenteTurma.getDocente() ) );
			obj.setDistancia(isTurmaEad());
			TurmaValidator.validaDocenteInserido(obj, docenteTurma, permiteCHCompartilhada, mensagens, horarios);
			
			Servidor docente = null;
			if( docenteTurma.getDocente() != null ){
				docente = dao.findByPrimaryKey(docenteTurma.getDocente().getId(), Servidor.class);
				if( obj.getId() == 0 && Ativo.EXCLUIDO == docente.getAtivo().getId() )
					mensagens.addErro("O v�nculo de docente selecionado est� inativo. Por favor, selecione outro v�nculo.");
			} else if( obj.getId() == 0 && docenteTurma.getDocenteExterno() != null &&
					!docenteTurma.getDocenteExterno().isAtivo() ) {
					mensagens.addErro("O v�nculo de docente externo selecionado est� inativo. Por favor, selecione outro v�nculo.");
			}
			
			if (mensagens.isEmpty()) {
				docenteTurma.setHorarios(horarios);
				if( docenteTurma.getDocente() != null ){
					docenteTurma.setDocente(docente);
				} else if( docenteTurma.getDocenteExterno() != null ){
					docenteTurma.setDocenteExterno( dao.findByPrimaryKey(docenteTurma.getDocenteExterno().getId(), DocenteExterno.class) );
				}
				if (obj.addDocenteTurma(docenteTurma)) {
					addMessage("Docente adicionado com sucesso.", TipoMensagemUFRN.INFORMATION);
					idCorpoDocente = 0;
				} else {
					addMessage("Docente n�o pode ser adicionado.", TipoMensagemUFRN.ERROR);
				}
				docenteTurma = new DocenteTurma();
				docenteTurma.setDocenteExterno(new DocenteExterno());
			} else {
				addMensagens(mensagens);
			}
			if (hasErrors()) {
				// Retorna para o ajax, o nome do docente.
				String nome = "";
				if (docenteTurma.getDocenteExterno() != null && docenteTurma.getDocenteExterno().getId() > 0)
					nome = (dao.refresh(docenteTurma.getDocenteExterno())).getNome();
				else if (docenteTurma.getDocente() != null && docenteTurma.getDocente().getId() > 0)
					nome = (dao.refresh(docenteTurma.getDocente())).getNome();
				getCurrentRequest().setAttribute("nomeDocente", nome);
				return null;
			}
			
			equipe = new EquipePrograma();
			dataInicio = null;
			dataFim = null;
			mapaHorarios = null;
			ArrayList<DocenteTurma> listaDocenteturma = br.ufrn.arq.util.CollectionUtils.toList(obj.getDocentesTurmas());
//			modelDocentesTurmas = new ListDataModel(listaDocenteturma);
			popularGrupoDocentes(listaDocenteturma);
			return formDocente();
		} finally {
			 if (dao != null) dao.close();
		}
	}
	
	/**
	 * M�todo respons�vel pelo retorno do usu�rio para o formul�rio de docentes compat�vel com o n�vel de ensino.
	 * @return
	 */
	public String formDocente(){
		return obj.isGraduacao() ? forward(JSP_DOCENTES_GRADUACAO) : forward(JSP_DOCENTES);
	}
	
	/**
	 * Remove um docente da turma.<br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>sigaa.war/graduacao/turma/docentes.jsp</li>
	 * </ul>
	 * 
	 * @param evt
	 * @throws DAOException
	 */
	public String removerDocenteTurma(ActionEvent evt) throws DAOException {
		AvaliacaoInstitucionalDao dao = null;
		try {
			dao = getDAO( AvaliacaoInstitucionalDao.class );
			
			Integer grupo = (Integer) evt.getComponent().getAttributes().get("grupo");
			
			DocenteTurma dt = (DocenteTurma) mapaGrupoDocenteDataModel.get(grupo).getRowData();
			
			long qtdAvaliacoes = dao.countAvaliacaoByDocenteTurmaAnoPeriodo(dt, obj.getAno(), obj.getPeriodo());
			if( qtdAvaliacoes > 0 ){
				addMensagemWarning("ATEN��O! O docente " + dt.getDocenteNome() + " n�o pode ser removido desta turma pois j� foi realizado avalia��o institucional para ele. Por�m � poss�vel alterar a carga hor�ria dele nesta turma. Para alterar a carga hor�ria voc� deve adicion�-lo na turma com uma carga hor�ria diferente. ");
			}
	
			for (Iterator<DocenteTurma> iterator = obj.getDocentesTurmas().iterator(); iterator.hasNext();) {
				DocenteTurma docenteTurma = iterator.next();
				if (docenteTurma.getId() == dt.getId())
					iterator.remove();
			}
			
			addMessage("Docente removido com sucesso.", TipoMensagemUFRN.INFORMATION);
			
			ArrayList<DocenteTurma> listaDocenteTurma = CollectionUtils.toList(obj.getDocentesTurmas());
//			modelDocentesTurmas = new ListDataModel(listaDocenteTurma);
			popularGrupoDocentes(listaDocenteTurma);
			return formDocente();
		} finally {
			if (dao != null) dao.close();
		}
	}
	
	/**
	 * M�todo utilizado para atualizar carga hor�ria do docente via ajax.<br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>sigaa.war/graduacao/turma/docentes.jsp</li>
	 * </ul>
	 * 
	 * @param evt
	 * @throws DAOException
	 */
	public void atualizaCHDocente(ActionEvent evt) throws DAOException {
		
		ListaMensagens lista = new ListaMensagens();
		
		DocenteTurma dtModificado = (DocenteTurma) modelDocentesTurmas.getRowData();

		if (dtModificado.getChDedicadaPeriodo() == null || dtModificado.getChDedicadaPeriodo() <= 0)
			return;
		
		ParametrosGestoraAcademica params = getParametrosAcademicos();
		boolean permiteCHCompartilhada = false;
		if (params != null)
			permiteCHCompartilhada = params.isPermiteChCompartilhada();
		
		//verificando se a gestora ou o componente permiti CH compartilhada 
		if (!obj.isGraduacao())
			permiteCHCompartilhada = permiteCHCompartilhada || obj.getDisciplina().getDetalhes().isPermiteChCompartilhada();		

		TurmaValidator.validaCHDocenteTurma(obj, dtModificado, permiteCHCompartilhada, lista, false, null, null);
		
		if (!lista.isEmpty()) {
			addMensagensAjax(lista);
			return;
		}
		
		for (DocenteTurma dt : obj.getDocentesTurmas()) {
			if (dt.equals(dtModificado))
				dt.setChDedicadaPeriodo(dtModificado.getChDedicadaPeriodo());
		}
		
	}
	
	/** Retorna o docente que leciona na turma.
	 * @return
	 */
	public DocenteTurma getDocenteTurma() {
		return docenteTurma;
	}

	/** Seta o docente que leciona na turma. 
	 * @param docenteTurma
	 */
	public void setDocenteTurma(DocenteTurma docenteTurma) {
		this.docenteTurma = docenteTurma;
	}
	
	/** Retorna o tipo de busca por docente a ser realizada. 
	 * @return
	 */
	public int getTipoBuscaDocente() {
		return tipoBuscaDocente;
	}

	/** Seta o tipo de busca por docente a ser realizada.
	 * @param tipoBuscaDocente
	 */
	public void setTipoBuscaDocente(int tipoBuscaDocente) {
		this.tipoBuscaDocente = tipoBuscaDocente;
	}

	/** Retorna o docente, membro do programa, a ser adicionado � turma.
	 * @return
	 */
	public EquipePrograma getEquipe() {
		if (equipe == null)
			equipe = new EquipePrograma();
		return equipe;
	}

	/** SEta o docente, membro do programa, a ser adicionado � turma.  
	 * @param equipe
	 */
	public void setEquipe(EquipePrograma equipe) {
		this.equipe = equipe;
	}
	
	public DataModel getModelDocentesTurmas() {
		return modelDocentesTurmas;
	}

	public void setModelDocentesTurmas(DataModel modelDocentesTurmas) {
		this.modelDocentesTurmas = modelDocentesTurmas;
	}

	public String getTituloOperacao() {
		return tituloOperacao;
	}

	public void setTituloOperacao(String tituloOperacao) {
		this.tituloOperacao = tituloOperacao;
	}
	
	/** Usado no para secret�ria de programa de p�s */
	public boolean isSelecionarDocentesPrograma() {
		return getSubSistema().equals(SigaaSubsistemas.PORTAL_COORDENADOR_STRICTO) || obj.isStricto() 
			|| getSubSistema().equals(SigaaSubsistemas.PORTAL_COORDENADOR_LATO) || obj.isLato();
	}

	/** Verifica se o usu�rio est� em algum dos m�dulos de Lato Sensu */
	public boolean isLato(){
		return isPortalCoordenadorLato() || isLatoSensu();
	}
	
	/** Indica se o usu�rio pode selecionar qualquer componente para a cria��o da turma.
	 * @return
	 */
	public boolean isSelecionarQualquerComponente() {
		return (getAcessoMenu().isDae() || getAcessoMenu().isEad() || obj.getCurso() != null)
		&& !getSubSistema().equals(SigaaSubsistemas.PORTAL_COORDENADOR_STRICTO);
	}
	public Collection<Turma> getTurmasSubunidades() {
		return turmasSubunidades;
	}

	public void setTurmasSubunidades(Collection<Turma> turmasSubunidades) {
		this.turmasSubunidades = turmasSubunidades;
	}

	public boolean isTurmaEad() {
		return turmaEad;
	}
	
	public boolean isTurmaGraduacao() {
		return obj != null && obj.isGraduacao();
	}

	public void setTurmaEad(boolean turmaEad) {
		this.turmaEad = turmaEad;
	}
	
	/** Retorna um mapa de hor�rios agrupados por: per�odo, turno (tipo), dia da semana, ordem do hor�rio.<br/>M�todo n�o invocado por JSP�s.
	 * @return
	 */
	public Map<String, Map<Short, Map<Character, Map<Short, HorarioTurma>>>> getMapaHorariosTurma() {
		if (mapaHorarios == null) { 
			mapaHorarios = criaMapaHorarioTurma(obj);
		}
		return mapaHorarios;
	}

	/**
	 * Agrupa os hor�rios por per�odo, retornando um mapa a ser utilizado na
	 * visualiza��o.<br/>
	 * M�todo n�o invocado por JSP�s.
	 * 
	 * @param turma
	 * @return
	 */
	public Map<String, Map<Short, Map<Character, Map<Short, HorarioTurma>>>> criaMapaHorarioTurma(Turma turma) {
		Map<String, Map<Short, Map<Character, Map<Short, HorarioTurma>>>> mapaHorarios = new LinkedHashMap<String, Map<Short,Map<Character,Map<Short,HorarioTurma>>>>();
		//agrupa os hor�rios por per�odo, turno, ordem
		Formatador fmt = Formatador.getInstance(); 
		for (HorarioTurma ht : turma.getHorarios()) {
			// per�odo
			String periodo = "de " + fmt.formatarData(ht.getDataInicio()) + " a " + fmt.formatarData(ht.getDataFim());
			Map<Short, Map<Character, Map<Short, HorarioTurma>>> grupoPeriodo = mapaHorarios.get(periodo);
			if (grupoPeriodo == null) grupoPeriodo = new TreeMap<Short, Map<Character,Map<Short,HorarioTurma>>>();
			// turno (tipo)
			Map<Character, Map<Short, HorarioTurma>> grupoTurno = grupoPeriodo.get(ht.getHorario().getTipo());
			if (grupoTurno == null) grupoTurno = new TreeMap<Character, Map<Short,HorarioTurma>>();
			// dia da semana
			Map<Short, HorarioTurma> grupoDia = grupoTurno.get(ht.getDia());
			if (grupoDia == null) grupoDia = new TreeMap<Short, HorarioTurma>();
			// ordem do hor�rio.
			ht.setSelecionado(false);
			grupoDia.put(ht.getHorario().getOrdem(), ht);
			// agrupa tudo de volta
			grupoTurno.put(ht.getDia(), grupoDia);
			grupoPeriodo.put(ht.getHorario().getTipo(),grupoTurno);
			mapaHorarios.put(periodo, grupoPeriodo);
		}
		return mapaHorarios;
	}
	
	/**
	 * Retorna uma cole��o de selectItem de grupo de docentes poss�veis para o componente curricular.
	 * @return
	 */
	public Collection<SelectItem> getGruposDocenteCombo() {
		Collection<SelectItem> combo = new ArrayList<SelectItem>();
		for (int i = 1; i <= obj.getDisciplina().getNumMaxDocentes(); i++) {
			combo.add(new SelectItem(i, new Integer(i).toString()));
		}
		return combo;
	}
	
	public Date getDataInicio() {
		return dataInicio;
	}

	public void setDataInicio(Date dataInicio) {
		this.dataInicio = dataInicio;
	}

	public Date getDataFim() {
		return dataFim;
	}

	public void setDataFim(Date dataFim) {
		this.dataFim = dataFim;
	}

	public int getIdCorpoDocente() {
		return idCorpoDocente;
	}

	public void setIdCorpoDocente(int idCorpoDocente) {
		this.idCorpoDocente = idCorpoDocente;
	}

	public Map<Integer, Set<DocenteTurma>> getMapaGrupoDocente() {
		return mapaGrupoDocente;
	}

	public void setMapaGrupoDocente(
			Map<Integer, Set<DocenteTurma>> mapaGrupoDocente) {
		this.mapaGrupoDocente = mapaGrupoDocente;
	}

	public Map<Integer, DataModel> getMapaGrupoDocenteDataModel() {
		return mapaGrupoDocenteDataModel;
	}

	public void setMapaGrupoDocenteDataModel(
			Map<Integer, DataModel> mapaGrupoDocenteDataModel) {
		this.mapaGrupoDocenteDataModel = mapaGrupoDocenteDataModel;
	}

}
