/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
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

/** Controller responsável por definir docentes em uma turma.
 * 
 * @author Édipo Elder F. Melo
 *
 */
@Component("docenteTurmaBean")
@Scope("request")
public class DocenteTurmaMBean extends SigaaAbstractController<Turma> {
	
	// TIPOS DE BUSCA DE DOCENTES NA TELA DE ADICIONAR DOCENTE
	/** Define se o usuário busca por docentes do programa de pós graduação. */
	public static final int DOCENTE_PROGRAMA = 1;
	/** Define se o usuário busca por docentes externos ao programa. */
	public static final int DOCENTE_EXTERNO_PROGRAMA = 2;
	/** Define se o usuário busca por docentes externos. */
	public static final int DOCENTES_EXTERNOS = 3;
	/** Define se o usuário busca por docentes da instituição. */
	public static final int DOCENTES_TURMA = 4;
	
	/** Define o link para o formulário de definição de docentes da turma.*/
	public static final String JSP_DOCENTES = "/ensino/turma/docentes.jsp";
	
	/** Define o link para o formulário de definição de docentes da turma do nível de graduação.*/
	public static final String JSP_DOCENTES_GRADUACAO = "/graduacao/turma/docentes_graduacao.jsp";
	
	/** Controller utilizado para definir os docentes da turma. */
	private OperadorDocenteTurma mBean;
	/** Nome da operação (Para utilização no título da página de busca do componente curricular) */
	private String tituloOperacao;
	
	/** Tipo de busca por docente a ser realizada. */
	private int tipoBuscaDocente;
	/** Docente que leciona na turma. */
	private DocenteTurma docenteTurma;
	/** DataModel usado na exibição dos docentes da turma */
	private DataModel modelDocentesTurmas = new ListDataModel();
	/** Docente, membro do programa, a ser adicionado à turma. */
	private EquipePrograma equipe = new EquipePrograma();
	/** Coleção de turmas criadas para cada subunidade quando a disciplina é de bloco */
	private Collection<Turma> turmasSubunidades = new ArrayList<Turma>();
	/** Indica se o cadastro de turmas é de ensino a distância. */
	private boolean turmaEad;
	/** Chave primária do Corpo Docente Lato Sensu, carregado no Combo */
	private int idCorpoDocente;
	
	/** Data de início do período letivo da turma. */
	private Date dataInicio;
	/** Data de fim do período letivo da turma. */
	private Date dataFim;
	/** Mapa utilizado para auxiliar a montagem da String de horário da turma. */
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
	 * Cancela a operação e volta para a listagem das turmas, caso a operação
	 * seja de alteração ou remoção de uma turma.<br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
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

	/** Ativa o controller para popular os docentes de uma turma, passada por parâmetro.<br/>Método não invocado por JSP´s.
	 * @param mBean Bean para o qual deverá ser passado os docentes definidos na turma.
	 * @param tituloOperacao título da operação onde está definindo o docente da turma. Exemplo: "Cadastro de Turma"
	 * @param turma turma para o qual se quer definir os docentes
	 * @param turmaEad indica se a turma é de ensino à distância
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
	 * Método responsável por atualizar o mapa com os grupos de docentes.
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
	 * Submete a página de controle de docentes da turma.<br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
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
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/ensino/turma/docentes.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public String voltarPassoAnterior() {
		return mBean.definicaoDocenteTurmaVoltar();
	}
	
	/** Valida os dados do docente incluído na turma.<br/>Método não invocado por JSP´s.
	 * @param erros
	 * @throws DAOException
	 */
	public void validaDocentesTurma(ListaMensagens erros) throws DAOException {
		if (!isTurmaEad())
			validarCHDocente(erros);
		
		if (erros.isErrorPresent())
			return;
		
		verificarNovosDocentes();

		
		// Validando se os horários dos docentes da turma são compatíveis com os horários das outras turmas do docente
		if( obj.isAberta() ){
			HashMap<Integer, ParametrosGestoraAcademica> cacheParam = new HashMap<Integer, ParametrosGestoraAcademica>();
			
			for( DocenteTurma dt : obj.getDocentesTurmas() ){
				TurmaValidator.validaHorariosDocenteTurma(obj, dt, erros, getUsuarioLogado(), cacheParam);
			}
		}

		if( obj.getDisciplina().isAceitaSubturma() && obj.getTurmaAgrupadora() != null ){

			// A validação da carga horária dos docentes deve considerar a carga
			// horária de aulas teóricas da disciplina de forma comum e a carga
			// horária de laboratório separada, para cada sub-turma relacionada.
			// Por exemplo, uma disciplina com 60h teóricas e 90h de
			// laboratório, organizada em 3 sub-turmas deve ter a carga máxima
			// geral dos docentes validada para não ultrapassar (60h + 3*90h). A cada nova
			// sub-turma cadastrada deve-se levar em conta a CH restante dentro do conjunto de sub-turmas.
			int maxHorarios = obj.getDisciplina().getDetalhes().getChAula();
			maxHorarios += obj.getDisciplina().getDetalhes().getChEad();
			
			obj.setTurmaAgrupadora(getGenericDAO().refresh( obj.getTurmaAgrupadora()));
			int quantidadeSubturmas = obj.getTurmaAgrupadora().getSubturmas().size();
			int totalHorariosDocentesSubturmas = 0;
			
			for( Turma subturma : obj.getTurmaAgrupadora().getSubturmas() ){
				// não contabiliza a turma atual, pois os valores da CH dos docentes pode ter sido alterado.
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
				erros.addErro("A validação da carga horária dos docentes de sub-turmas considera a carga horária de aulas teóricas " 
						+ "da disciplina de forma comum e a carga horária de laboratório separada, para cada sub-turma relacionada. " 
						+ "Esta disciplina possui " 
						+ obj.getDisciplina().getChAula() + "h teóricas" 
						+ (obj.getDisciplina().getDetalhes().getChEad() > 0 ? ", "+obj.getDisciplina().getDetalhes().getChEad()+"h de Ead " : "")
						+ " e " + obj.getDisciplina().getChLaboratorio() + "h de laboratório, " 
						+ "organizada em " + quantidadeSubturmas + " sub-turmas, portanto, a carga horária " 
						+ "dos docentes de todas as sub-turmas não pode ultrapassar " 
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
	 * Verifica se os docentes que foram inseridos na turma já não estavam cadastrados,
	 * tendo sido removido e inserido apenas para realizar uma mudança de carga horária.
	 * Caso isso aconteça, o id original será setado na nova entidade DocenteTurma.
	 * SÓ DEVE SER TESTADO NA ALTERAÇÃO.
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
	
			// atribui o mesmo ID para o docente inserido, caso ele seja o mesmo já cadastrado anteriormente
			// com isso, ao invés de inserir, será atualizado.
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
	
			// Verificando algum docente foi removido da coleção
			// Só pode remover docentes que não possuem avaliação institucional
			if( !isEmpty( turmaOriginal.getDocentesTurmas() ) ){
				List<DocenteTurma> lista = br.ufrn.arq.util.CollectionUtils.toList(obj.getDocentesTurmas());
				for( DocenteTurma dt : turmaOriginal.getDocentesTurmas() ){
					// a lista de docentes do banco bem com docente ou docenteExterno nulo.
					if (dt.getDocente() == null) dt.setDocente(new Servidor());
					if (dt.getDocenteExterno() == null) dt.setDocenteExterno(new DocenteExterno());
					// percorre a lista e verifica se o docente foi removido e reinserido.
					// não dá pra fazer !lista.contains(dt) pois os horários podem ser diferentes e retornaria false
					// a verificação deve ser feita pelo ID do DocenteTurma, ou do Docente e DocenteExterno
					boolean contem = false;
					for (DocenteTurma dtNew : lista) {
						if( EqualsUtil.testEquals(dt, dtNew, "id") || 
								EqualsUtil.testEquals(dt.getDocente(), dtNew.getDocente(), "id") &&
								EqualsUtil.testEquals(dt.getDocenteExterno(), dtNew.getDocenteExterno(), "id"))
							contem = true;
					}
					// se o discente foi removido da turma, verifica se há avaliação preenchida para ele.
					if (!contem){
						long qtdAvaliacoes = dao.countAvaliacaoByDocenteTurmaAnoPeriodo(dt, obj.getAno(), obj.getPeriodo());
						if( qtdAvaliacoes > 0 ){
							if (isUserInRole(SigaaPapeis.ADMINISTRADOR_DAE))
								addMensagemWarning("Ao remover o docente " + dt.getDocenteNome() + " desta, removerá também as Avaliações Institucionais do docente já realizadas.");
							else
								addMensagemErro("Não é possível remover " + dt.getDocenteNome() + " desta turma pois já foi realizado avaliação institucional para ele.");
						}
					}
				}
			}else{
				for( DocenteTurma dt : obj.getDocentesTurmas() ){
					long qtdAvaliacoes = dao.countAvaliacaoByDocenteTurmaAnoPeriodo(dt, obj.getAno(), obj.getPeriodo());
					if( qtdAvaliacoes > 0 ){
						if (isUserInRole(SigaaPapeis.ADMINISTRADOR_DAE))
							addMensagemWarning("Ao remover o docente " + dt.getDocenteNome() + " desta, removerá também as Avaliações Institucionais do docente já realizadas.");
						else
							addMensagemErro("Não é possível remover " + dt.getDocenteNome() + " desta turma pois já foi realizado avaliação institucional para ele.");
					}
	
				}
			}
		} finally {
			if (dao != null) dao.close();
		}
	}
	
	/** Valida a carga horária do docente.
	 * @throws DAOException
	 */
	private void validarCHDocente(ListaMensagens erros) throws DAOException {
		for (DocenteTurma dt : obj.getDocentesTurmas()) {
			if( dt.getChDedicadaPeriodo() == null || dt.getChDedicadaPeriodo() <= 0 ) {
				erros.addErro("Entre com a carga horária do docente.");
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
	 * Adiciona um docente selecionado à uma turma, realiza também as validações
	 * necessárias de choque de horário, etc.<br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
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
			
			// Validação dos dados do formulário 
			if (isTurmaEad()) {
				docenteTurma.setChDedicadaPeriodo(0);
			} // Turmas de graduação cadastram a porcentagem da CH. 
			else if (obj.isGraduacao() && docenteTurma.getChDedicadaPeriodo() == null) {
				addMensagemErro("Não foi possível calcular o valor real da carga horária dedicada ao período.");	
			} else if( docenteTurma.getChDedicadaPeriodo() == null || docenteTurma.getChDedicadaPeriodo() <= 0 )				
				addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Carga Horária");	
			
			if( tipoBuscaDocente == DOCENTE_EXTERNO_PROGRAMA && docenteTurma.getDocente().getId() <= 0 && !latoMesmoPrograma )
				addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Docentes");
			if( tipoBuscaDocente == DOCENTE_PROGRAMA && equipe.getId() <= 0 && !latoMesmoPrograma)
				addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Docente");
			if( tipoBuscaDocente == DOCENTES_EXTERNOS && docenteTurma.getDocenteExterno().getId() <= 0 && !latoMesmoPrograma )
				addMensagemErro("Selecione um docente externo.");
			if( docenteTurma.getDocente().getId() <= 0 && !isSelecionarDocentesPrograma())
				addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Docente");
			if (isPermiteHorarioDocenteFlexivel() && !isTurmaEad() && !latoMesmoPrograma ) {
				ValidatorUtil.validateRequired(dataInicio, "Início do Período", erros);
				ValidatorUtil.validateRequired(dataFim, "Fim do Período", erros);
				ValidatorUtil.validateMinValue(dataInicio, obj.getDataInicio(), "Data Inicial do Período", erros);
				ValidatorUtil.validateMaxValue(dataInicio, obj.getDataFim(), "Data Inicial do Período", erros);
				ValidatorUtil.validateMinValue(dataFim, obj.getDataInicio(), "Data Final do Período", erros);
				ValidatorUtil.validateMaxValue(dataFim, obj.getDataFim(), "Data Final do Período", erros);
				ValidatorUtil.validateMinValue(dataFim, dataInicio, "Data Final do Período", erros);
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
	
			// horário do docente
			List<HorarioDocente> horarios = new ArrayList<HorarioDocente>(0);
			if (!isTurmaEad()) {
				// define o horário do docente informado pelo usuário, caso seja permitido horário flexível do docente
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
					ValidatorUtil.validateRequired(horarios, "Horários no Período", erros);
				} else {
					// horário do docente é o horário da turma, caso não seja permitido horário flexível do docente
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
			// Valida o horário do docente
			docenteTurma.setDocente( dao.refresh( docenteTurma.getDocente() ) );
			obj.setDistancia(isTurmaEad());
			TurmaValidator.validaDocenteInserido(obj, docenteTurma, permiteCHCompartilhada, mensagens, horarios);
			
			Servidor docente = null;
			if( docenteTurma.getDocente() != null ){
				docente = dao.findByPrimaryKey(docenteTurma.getDocente().getId(), Servidor.class);
				if( obj.getId() == 0 && Ativo.EXCLUIDO == docente.getAtivo().getId() )
					mensagens.addErro("O vínculo de docente selecionado está inativo. Por favor, selecione outro vínculo.");
			} else if( obj.getId() == 0 && docenteTurma.getDocenteExterno() != null &&
					!docenteTurma.getDocenteExterno().isAtivo() ) {
					mensagens.addErro("O vínculo de docente externo selecionado está inativo. Por favor, selecione outro vínculo.");
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
					addMessage("Docente não pode ser adicionado.", TipoMensagemUFRN.ERROR);
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
	 * Método responsável pelo retorno do usuário para o formulário de docentes compatível com o nível de ensino.
	 * @return
	 */
	public String formDocente(){
		return obj.isGraduacao() ? forward(JSP_DOCENTES_GRADUACAO) : forward(JSP_DOCENTES);
	}
	
	/**
	 * Remove um docente da turma.<br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
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
				addMensagemWarning("ATENÇÃO! O docente " + dt.getDocenteNome() + " não pode ser removido desta turma pois já foi realizado avaliação institucional para ele. Porém é possível alterar a carga horária dele nesta turma. Para alterar a carga horária você deve adicioná-lo na turma com uma carga horária diferente. ");
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
	 * Método utilizado para atualizar carga horária do docente via ajax.<br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
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

	/** Retorna o docente, membro do programa, a ser adicionado à turma.
	 * @return
	 */
	public EquipePrograma getEquipe() {
		if (equipe == null)
			equipe = new EquipePrograma();
		return equipe;
	}

	/** SEta o docente, membro do programa, a ser adicionado à turma.  
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
	
	/** Usado no para secretária de programa de pós */
	public boolean isSelecionarDocentesPrograma() {
		return getSubSistema().equals(SigaaSubsistemas.PORTAL_COORDENADOR_STRICTO) || obj.isStricto() 
			|| getSubSistema().equals(SigaaSubsistemas.PORTAL_COORDENADOR_LATO) || obj.isLato();
	}

	/** Verifica se o usuário está em algum dos módulos de Lato Sensu */
	public boolean isLato(){
		return isPortalCoordenadorLato() || isLatoSensu();
	}
	
	/** Indica se o usuário pode selecionar qualquer componente para a criação da turma.
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
	
	/** Retorna um mapa de horários agrupados por: período, turno (tipo), dia da semana, ordem do horário.<br/>Método não invocado por JSP´s.
	 * @return
	 */
	public Map<String, Map<Short, Map<Character, Map<Short, HorarioTurma>>>> getMapaHorariosTurma() {
		if (mapaHorarios == null) { 
			mapaHorarios = criaMapaHorarioTurma(obj);
		}
		return mapaHorarios;
	}

	/**
	 * Agrupa os horários por período, retornando um mapa a ser utilizado na
	 * visualização.<br/>
	 * Método não invocado por JSP´s.
	 * 
	 * @param turma
	 * @return
	 */
	public Map<String, Map<Short, Map<Character, Map<Short, HorarioTurma>>>> criaMapaHorarioTurma(Turma turma) {
		Map<String, Map<Short, Map<Character, Map<Short, HorarioTurma>>>> mapaHorarios = new LinkedHashMap<String, Map<Short,Map<Character,Map<Short,HorarioTurma>>>>();
		//agrupa os horários por período, turno, ordem
		Formatador fmt = Formatador.getInstance(); 
		for (HorarioTurma ht : turma.getHorarios()) {
			// período
			String periodo = "de " + fmt.formatarData(ht.getDataInicio()) + " a " + fmt.formatarData(ht.getDataFim());
			Map<Short, Map<Character, Map<Short, HorarioTurma>>> grupoPeriodo = mapaHorarios.get(periodo);
			if (grupoPeriodo == null) grupoPeriodo = new TreeMap<Short, Map<Character,Map<Short,HorarioTurma>>>();
			// turno (tipo)
			Map<Character, Map<Short, HorarioTurma>> grupoTurno = grupoPeriodo.get(ht.getHorario().getTipo());
			if (grupoTurno == null) grupoTurno = new TreeMap<Character, Map<Short,HorarioTurma>>();
			// dia da semana
			Map<Short, HorarioTurma> grupoDia = grupoTurno.get(ht.getDia());
			if (grupoDia == null) grupoDia = new TreeMap<Short, HorarioTurma>();
			// ordem do horário.
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
	 * Retorna uma coleção de selectItem de grupo de docentes possíveis para o componente curricular.
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
