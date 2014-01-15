 /*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Created on 18/01/2008
 *
 */
package br.ufrn.sigaa.ava.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.faces.event.ActionEvent;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.MensagemAviso;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.sigaa.arq.dao.ensino.TurmaDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ava.dao.TopicoAulaDao;
import br.ufrn.sigaa.ava.dao.TurmaVirtualDao;
import br.ufrn.sigaa.ava.dominio.ImportacaoDadosTurmasAnteriores;
import br.ufrn.sigaa.ava.dominio.IndicacaoReferencia;
import br.ufrn.sigaa.ava.dominio.TopicoAula;
import br.ufrn.sigaa.ava.negocio.MovimentoCadastroAva;
import br.ufrn.sigaa.ava.validacao.Notification;
import br.ufrn.sigaa.ava.validacao.Specification;
import br.ufrn.sigaa.ensino.dominio.PlanoCurso;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.graduacao.jsf.PlanoCursoMBean;

/**
 * Managed Bean para realizar a importa��o de dados de turmas anteriores.
 * 
 * @author David Pereira
 *
 */
@Component("importacaoDadosTurma") @Scope("session")
public class ImportacaoDadosTurmasAnterioresMBean extends ControllerTurmaVirtual {
	
	/** Objeto que auxilia a importa��o de dados */
	private ImportacaoDadosTurmasAnteriores object;// = new ImportacaoDadosTurmasAnteriores();
	
	/** A turma para onde os dados ser�o importados. */
	private Turma turma;
	
	/** Lista de turmas que podem importar seus dados. */
	private List<Turma> turmas;
	
	/** Lista de t�pico de aulas que poder�o ser importados */
	private List <TopicoAula> topicosDeAula;
	
	/** Lista de refer�ncias que poder�o ser importadas */
	private List <IndicacaoReferencia> referencias;
	
	/** Plano de curso que ser� importado */
	private PlanoCurso plano;
	
	/** Verifica se a importa��o � feita atrav�s de um plano de curso ou atrav�s da turma virtual. */ 
	boolean planoCurso;
	
	/**
	 * Inicia o caso de uso.<br/><br/>
	 * 
	 * Chamado pelo m�todo "importar()" no bean ImportacaoDadosTurmasAnterioresMBean<br/>
	 * N�o invocado por JSPs.
	 * 
	 * @return
	 */
	public String iniciar() {
		turma = null;
		object = new ImportacaoDadosTurmasAnteriores();
		return forward("/ava/ImportacaoDadosTurmasAnteriores/turmas.jsp");
	}
	
	/**
	 * Inicia o caso de uso pela tela de cadastro de plano de curso.<br/><br/>
	 * 
	 * M�todo n�o invocado por JSPs. � public porque � chamado pela classe PlanoCursoMBean.
	 * 
	 * @return
	 * @throws DAOException 
	 */
	public String iniciarPlanoCurso(Turma turma) throws DAOException {
		this.turma = turma;
		topicosDeAula = null;
		referencias = null;
		plano = null;
		
		if (getTurmasAnteriores().isEmpty()){
			addMensagemErro("N�o h� turmas anteriores a esta para o mesmo componente curricular que tenha dados que possam ser importados.");
			return null;
		}
		
		object = new ImportacaoDadosTurmasAnteriores();
		object.setTopicosDeAula(false);
		return forward("/ava/ImportacaoDadosTurmasAnteriores/turmasPlano.jsp");
	}

	/**
	 * Valida os dados para a importa��o.
	 * M�todo n�o invocado por JSPs
	 */
	private void validar() {
		
		if ( planoCurso ){
			if ( object.isTopicosDeAula() && isEmpty(topicosDeAula)  )
				addMensagemErro("N�o h� T�picos de Aula cadastrados para a turma selecionada.");
			if ( object.isReferencias() && isEmpty(referencias) )
				addMensagemErro("N�o h� Refer�ncias cadastradas para a turma selecionada.");
			if ( object.isPlanoCurso() && (plano == null || (plano != null && isEmpty(plano.getMetodologia()) && isEmpty(plano.getProcedimentoAvalicao()))))
				addMensagemErro("N�o h� Plano de Curso cadastradas para a turma selecionada.");
		}
		
	}
	
	/**
	 * Exibe as op��es de importa��o para que o usu�rio selecione.<br/><br/>
	 * 
	 * M�todo chamado pela seguinte JSP: /sigaa.war/ava/ImportacaoDadosTurmasAnteriores/turmas.jsp
	 * @return
	 * @throws DAOException
	 */
	public String opcoesImportacao() throws DAOException {
		
		planoCurso = getParameterBoolean("planoCurso");
		Integer id = getParameterInt("id");
		Turma turma = getDAO(TurmaDao.class).findByPrimaryKeyOtimizado(id);
		object.setTurmaAnterior(turma);
		TopicoAulaDao dao = null;
		
		topicosDeAula = null;
		referencias = null;
	
		object.setTurmaAtual(getTurma());
		
		try {

			object.setTopicosDeAula(true);
			dao = getDAO(TopicoAulaDao.class);
			topicosDeAula = dao.findByTurma(object.getTurmaAnterior());

			object.setTopicosDeAula(true);
			for ( TopicoAula ta : topicosDeAula )
				ta.setSelecionado(true);
	
			object.setReferencias(false);
			
			return planoCurso ? forward("/ava/ImportacaoDadosTurmasAnteriores/opcoesPlano.jsp")
								: forward("/ava/ImportacaoDadosTurmasAnteriores/opcoes.jsp");
		}finally {
			if ( dao != null )
				dao.close();
		}
	}
	/**
	 * Realiza a valida��o do cadastro de tarefas para uma turma.<br/><br/>
	 * Utilizada apenas para guardar mensagem de warning.
	 * JSP: N�o � chamado por JSPs.
	 */
	public Specification getEspecificacaoImportacao() {
		return new Specification() {
			Notification notification = new Notification();
			
			public Notification getNotification() {
				return notification;
			}

			public boolean isSatisfiedBy(Object objeto) {
				return true;
			}
		
		};
	}
	
	/**
	 * Configura as datas no t�pico de aula e verifica se as datas s�o v�lidas.<br/><br/>
	 * 
	 * M�todo n�o invocado por JSPs.
	 * @return
	 */
	private void configurarDatas ()	{
		
		erros = new ListaMensagens();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		
		for (TopicoAula ta : topicosDeAula){
			
			Date dataInicio = new Date(ta.getDataInicio());
			Date dataFim = new Date(ta.getDataFim());
			
			if ( dataInicio.after(dataFim) ) {
				erros.addMensagem(MensagensArquitetura.DATA_POSTERIOR_A, "Data Inicial", "Data Final");
				return;
			}	
			
			if ( ta.getTopicoPai() != null ) {
				// Deve-se recuperar a nova data do t�pico pai.
				int index = topicosDeAula.indexOf(ta.getTopicoPai());
				
				// Garante que o t�pico pai existe na lista e evita nullpointer.
				if ( index != -1 ){
					TopicoAula pai = topicosDeAula.get(index);
					Date dataInicioPai = new Date(pai.getDataInicio());
					Date dataFimPai =  new Date(pai.getDataFim());
					
					if ( dataInicioPai.after(dataInicio) || dataFimPai.before(dataFim) ) {
						erros.addMensagem(new MensagemAviso(
										"T�pico: " +  ta.getDescricao() + " - " +
										"As datas do t�pico de aula devem estar contidas no intervalo de datas do t�pico pai (" + 
										sdf.format(dataInicioPai) + " a " + sdf.format(dataFimPai) + ")",TipoMensagemUFRN.ERROR ));
						return;
					}	
				}
			}
			
			ta.setData(dataInicio);
			ta.setFim(dataFim);
		}
	}
	
	/**
	 * Realiza a importa��o dos dados para a turma atual.<br/><br/>
	 * 
	 * M�todo chamado pela seguinte JSP: /sigaa.war/ava/ImportacaoDadosTurmasAnteriores/opcoes.jsp
	 * @return
	 * @throws ArqException 
	 */
	public String importar() throws ArqException {
		
		try {
		
			validar();
			configurarDatas();
		
			if (hasErrors())
				return null;
			
			prepareMovimento(SigaaListaComando.IMPORTAR_DADOS_TURMAS_ANTERIORES);
			
			MovimentoCadastroAva mov = new MovimentoCadastroAva();
			mov.setObjMovimentado(object);
			mov.setTurma(getTurma());
			mov.setCodMovimento(SigaaListaComando.IMPORTAR_DADOS_TURMAS_ANTERIORES);
			mov.setUsuario(getUsuarioLogado());
			mov.setSpecification(getEspecificacaoImportacao());			
			
			//Se for para um plano de curso.
			mov.setParaPlanoCurso(this.isPlanoCurso());
			
			mov.setTopicosDeAula(topicosDeAula);
			mov.setReferencias(referencias);
			
			execute(mov);
			
			addMensagens(mov.getSpecification().getNotification().getMessages());
			
			TopicoAulaMBean tbean = getMBean("topicoAula");
				tbean.setListagem(null);
				
			if ( !mov.getSpecification().getNotification().getMessages().isErrorPresent() ){
				flash("ATEN��O: Todos os materiais importados est�o com as datas configuradas de acordo com o " +
						" in�cio e fim do per�odo da turma atual. Voc� pode alterar essas datas de acordo com suas " +
						" necessidades.");
			
				flash("Importa��o realizada com sucesso!");
			}
		} catch (NegocioException e) {
			addMensagemErro(e.getMessage());
			return null;
		}


		notificarTurma(getTurma(), "Novo arquivo Inserido (" + getTurma().getDescricaoCodigo() + ")", "<html>Ol�! Um novo material foi adicionado nesta turma.<br>Acesse o SIGAA para proceder o download do material.</html>", ControllerTurmaVirtual.DISCENTE, ControllerTurmaVirtual.DOCENTE, ControllerTurmaVirtual.AUTORIZADO);
		
		if (!this.isPlanoCurso())
			return iniciar();
		
		PlanoCursoMBean pcMBean = getMBean ("planoCurso");
		pcMBean.setTurma(getTurma());
		pcMBean.setCarregadaImportacao(true);
		return pcMBean.gerenciarPlanoCurso();
	}
	
	/**
	 * Prepara os t�picos de aula para serem importados para turma.
	 * 
	 * M�todo chamado pela seguinte JSP: /sigaa.war/ava/ImportacaoDadosTurmasAnteriores/opcoesPlano.jsp
	 * @return
	 * @throws ArqException 
	 */
	public void preparaTopicosDeAula (ActionEvent e) throws DAOException {
		
		if (topicosDeAula == null || topicosDeAula.isEmpty()){
			
			TopicoAulaDao dao = null;
			
			try {
				dao = getDAO(TopicoAulaDao.class);
				topicosDeAula = dao.findByTurma(object.getTurmaAnterior());
				for (TopicoAula ta : topicosDeAula)
					ta.setSelecionado(true);
				
			} finally {
				if (dao != null)
					dao.close();
			}
		}
	}

	/**
	 * Prepara refer�ncias para serem importadas para turma.
	 * 
	 * M�todo chamado pela seguinte JSP: /sigaa.war/ava/ImportacaoDadosTurmasAnteriores/opcoesPlano.jsp
	 * @return
	 * @throws ArqException 
	 */
	public void preparaReferencias (ActionEvent e) throws DAOException {
		
		if (referencias == null || referencias.isEmpty()){
			
			TurmaVirtualDao dao = null;
			
			try {
				dao = getDAO(TurmaVirtualDao.class);
				referencias = dao.findReferenciasTurma(object.getTurmaAnterior());
				
				for (IndicacaoReferencia r : referencias)
					r.setSelecionada(true);
			} finally {
				if (dao != null)
					dao.close();
			}
		}
	}
	
	/**
	 * Prepara o plano de curso para ser importado para turma.
	 * 
	 * M�todo chamado pela seguinte JSP: /sigaa.war/ava/ImportacaoDadosTurmasAnteriores/opcoesPlano.jsp
	 * @return
	 * @throws ArqException 
	 */
	public void prepararPlanoCurso (ActionEvent e) throws DAOException {
		
		if (plano == null ){
			
			GenericDAO dao = null;
			
			try {
				dao = getGenericDAO();
				plano = dao.findByExactField(PlanoCurso.class, "turma.id", object.getTurmaAnterior().getId(), true);
				if (plano != null &&  isEmpty(plano.getMetodologia()) && isEmpty(plano.getProcedimentoAvalicao()) ){
					plano = null;
				}	
				
			} finally {
				if (dao != null)
					dao.close();
			}
		}
	}
	
	/**
	 * Retorta a lista com as turmas anteriores da turma cujo o plano de curso est� sendo cadastrado. 
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>sigaa.war\ava\ImportacaoDadosTurmasAnteriores\turmas.jsp</li>
	 *    <li>sigaa.war\ava\ImportacaoDadosTurmasAnteriores\turmasPlano.jsp</li>
	 *   </ul>
	 * @return
	 * @throws DAOException 
	 */
	public List<Turma> getTurmasAnteriores() throws DAOException {
		
		TurmaVirtualDao tDao = null;
		
		try {
			tDao = getDAO(TurmaVirtualDao.class);

			turmas = tDao.findTurmasAnteriores(getTurma(), getUsuarioLogado().getVinculoAtivo().getServidor(), getUsuarioLogado().getVinculoAtivo().getDocenteExterno());
			List<Turma> turmasAgrupadoras = tDao.findTurmasAgrupadorasImportacao(getTurma(), getUsuarioLogado().getVinculoAtivo().getServidor(), getUsuarioLogado().getVinculoAtivo().getDocenteExterno());
	
			if ( turmas == null && turmasAgrupadoras != null )
				turmas = turmasAgrupadoras;
			else if ( turmas != null && turmasAgrupadoras != null )
				turmas.addAll(turmasAgrupadoras);
			
			if ( turmas != null ) {
				Collections.sort(turmas, new Comparator<Turma>(){
					public int compare(Turma t1, Turma t2) {
						int retorno = 0;
						retorno = t2.getAno() - t1.getAno();
						if( retorno == 0 )
							retorno = t2.getPeriodo() - t1.getPeriodo();
						return retorno;
					}
				});
			}	
		} finally {
			if ( tDao != null )
				tDao.close();
		}	
		return turmas;
	}
		
	/**
	 * Voltar para p�gina anterior.<br/><br/>
	 * 
	 * M�todo chamado pela seguinte JSP: /sigaa.war/ava/ImportacaoDadosTurmasAnteriores/opcoes.jsp
	 * @return
	 * @throws ArqException 
	 */
	public String voltar () {
		return forward("/ava/ImportacaoDadosTurmasAnteriores/turmas.jsp");
	}

	public ImportacaoDadosTurmasAnteriores getObject() {
		return object;
	}

	public void setObject(ImportacaoDadosTurmasAnteriores object) {
		this.object = object;
	}

	public void setTurmas(List<Turma> turmas) {
		this.turmas = turmas;
	}

	/**
	 * Retorna a turma atual do caso de uso.
	 * 
	 * @return
	 */
	public Turma getTurma() {
		if (turma == null)
			turma = turma();
		
		return turma;
	}

	public void setTurma(Turma turma) {
		this.turma = turma;
	}

	public boolean isPlanoCurso() {
		return planoCurso;
	}

	public void setPlanoCurso(boolean planoCurso) {
		this.planoCurso = planoCurso;
	}
	
	public List<TopicoAula> getTopicosDeAula() {
		return topicosDeAula;
	}

	public void setTopicosDeAula(List<TopicoAula> topicosDeAula) {
		this.topicosDeAula = topicosDeAula;
	}

	public List<IndicacaoReferencia> getReferencias() {
		return referencias;
	}

	public void setReferencias(List<IndicacaoReferencia> referencias) {
		this.referencias = referencias;
	}

	public void setPlano(PlanoCurso plano) {
		this.plano = plano;
	}

	public PlanoCurso getPlano() {
		return plano;
	}
	
}