/* 
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 * Criado em: 28/01/2008
 */
package br.ufrn.sigaa.ava.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.sigaa.arq.dao.ensino.TurmaDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ava.dao.TopicoAulaDao;
import br.ufrn.sigaa.ava.dao.TurmaVirtualDao;
import br.ufrn.sigaa.ava.dominio.AcaoAva;
import br.ufrn.sigaa.ava.dominio.AulaExtra;
import br.ufrn.sigaa.ava.dominio.EntidadeRegistroAva;
import br.ufrn.sigaa.ava.dominio.TopicoAula;
import br.ufrn.sigaa.ava.negocio.FrequenciaHelper;
import br.ufrn.sigaa.ava.negocio.TopicoAulaHelper;
import br.ufrn.sigaa.ava.validacao.Notification;
import br.ufrn.sigaa.ava.validacao.Specification;
import br.ufrn.sigaa.ava.validacao.TurmaVirtualException;
import br.ufrn.sigaa.dominio.VinculoUsuario;
import br.ufrn.sigaa.ensino.dominio.DocenteTurma;
import br.ufrn.sigaa.ensino.dominio.FrequenciaAluno;
import br.ufrn.sigaa.ensino.dominio.PlanoReposicaoAula;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.negocio.dominio.FrequenciaMov;
import br.ufrn.sigaa.mensagens.MensagensTurmaVirtual;

/**
 * Managed Bean para a gerência de aulas extras para as turmas virtuais.
 * 
 * @author David Pereira
 *
 */
@Component("aulaExtra") @Scope("request")
public class AulaExtraMBean extends CadastroTurmaVirtual<AulaExtra> {

	/** A turma a qual esta aula extra pertence. */
	private Turma turma = new Turma();
	
	/** Se deve ser enviado um e-mail para os alunos ao cadastrar uma nova aula extra. */
	private boolean notificar;
	
	/** Lista de itens contento os tipos de aula extra. */
	private List<SelectItem> tipos;
	
	/** Lista de itens contento as aulas para reposição da aula extra. */
	private List<SelectItem> aulaParaReposicao;
	
	/** Construtor padrão */
	public AulaExtraMBean (){
		object = new AulaExtra();
	}
	
	/**
	 * Retorna a lista com as aulas extras da turma.<br/><br/>
	 * 
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 * 	<li>/ava/AulaExtra/editar.jsp</li>
	 * 	<li>/ava/AulaExtra/listar.jsp</li>
	 *  <li>/ava/AulaExtra/mostrar.jsp</li>
	 *  <li>/ava/AulaExtra/novo.jsp</li>
	 *  <li>/ava/menu.jsp</li>
	 * </ul>
	 */
	@Override
	public List<AulaExtra> lista() {
		if ( !isEmpty( turma().getSubturmas() )) {
			return getDAO(TurmaVirtualDao.class).buscarAulasExtraSubTurmas(turma());
		} else {
			return getDAO(TurmaVirtualDao.class).buscarAulasExtra(turma());
		}
	}

	/**
	 * Seta a turma do objeto.<br/><br/>
	 * 
	 * Método não invocado por jsps. É public por causa da arquitetura.
	 */
	@Override
	public void antesPersistir() {
		if ( !isEmpty( turma().getSubturmas() )) {
			object.setTurma(turma);
		}
	}

	/**
	 * Pega a turma do objeto.<br/><br/>
	 * 
	 * Método não invocado por jsps. É public por causa da arquitetura.
	 */
	@Override
	public void instanciarAposEditar() {
		turma = object.getTurma();
	}

	/**
	 * Cadastra uma Aula Extra e um tópico de aula associado a ela.<br/><br/>
	 * Método chamado pela seguinte JSP: /ava/AulaExtra/_form.jsp
	 * @throws ArqException 
	 * 
	 */
	@Override
	public String cadastrar() throws ArqException {

		registrarAcao(object.getDescricao(), EntidadeRegistroAva.AULA_EXTRA, AcaoAva.INICIAR_INSERCAO, object.getId());
		
		object.setTurma(turma());
		antesPersistir();
		prepare(SigaaListaComando.CADASTRAR_AVA);
		Notification notification = execute(SigaaListaComando.CADASTRAR_AVA, object, getEspecificacaoCadastro());
		
		if (notification.hasMessages()) {
			object.setId(0);
			return notifyView(notification);
		}
		
		// Registra inserção.
		registrarAcao(object.getDescricao(), EntidadeRegistroAva.AULA_EXTRA, AcaoAva.INSERIR, object.getId());
		
		if ( notificar )
		{
			TurmaVirtualMBean tBean = getMBean("turmaVirtual");
			String assunto = "Nova Aula Extra na turma: " + tBean.getTurma().getDescricaoSemDocente();
			String texto = "Uma nova aula extra foi cadastrada na turma virtual: " + tBean.getTurma().getDescricaoSemDocente() + "<p>Para visualizar mais informações acesse a turma virtual no SIGAA.</p>";
			notificarTurma(assunto, texto, ControllerTurmaVirtual.DISCENTE, ControllerTurmaVirtual.DOCENTE, ControllerTurmaVirtual.AUTORIZADO, ControllerTurmaVirtual.DOCENCIA_ASSISTIDA );
		}
		
		cadastrarTopico();
		
		flash("Aula Extra cadastrada com sucesso.");
		
		return redirect("/ava/index.jsf");

	}
	
	/**
	 * Cadastra um tópico de aula referente a uma aula extra.<br/><br/>
	 * Não invocado por jsp.
	 * @throws ArqException 
	 * 
	 */
	public void cadastrarTopico() throws ArqException
	{
		
		TopicoAulaMBean tBean = getMBean("topicoAula");
		
		List<String> cadastrarEm = new ArrayList<String>();
		cadastrarEm.add(String.valueOf(object.getTurma().getId()));
		tBean.setCadastrarEm(cadastrarEm);
			
		// Cria o tópico de aula apontando para o docente
		VinculoUsuario vinculoAtivo = getUsuarioLogado().getVinculoAtivo();
		
		TopicoAula t = TopicoAulaHelper.criar(object, vinculoAtivo.getServidor(), vinculoAtivo.getDocenteExterno());
		
		List<String> docentes = null;
		
		if(t.getDocentesTurma() != null && t.getDocentesTurma().size() > 0) {
			docentes = new ArrayList<String>();
			for (DocenteTurma dt : t.getDocentesTurma()) {
				if(!isEmpty(dt))
					docentes.add(String.valueOf(dt.getId()));
			}
		}

		tBean.setObject(t);
		tBean.setIdsDocentes(docentes);
		tBean.cadastrar();
		tBean.setListagem(null);
		tBean.setTopicosAulas(null);
		tBean.setDatasAulas(null);
		tBean.setItens(null);
	}
	
	/**
	 * Pega a turma do objeto.<br/><br/>
	 * 
	 * Método não invocado por jsps. É public por causa da arquitetura.
	 * @throws ArqException 
	 */
	@Override
	public void aposPersistir() {
		
		GenericDAO dao = null;
		
		try {
			verificaAlteracaoFrequencia();
		} catch (NegocioException e) {
			tratamentoErroPadrao(e);
		} catch (ArqException e) {
			tratamentoErroPadrao(e);
		}
		
		try {
			dao = getGenericDAO();
			
			
			// Atualização do Tópico de Aula referente a aula extra.

			TopicoAula t = dao.findByExactField(TopicoAula.class, "aulaExtra.id", object.getId(),true);
			if ( t != null ){
				prepareMovimento(SigaaListaComando.ATUALIZAR_AVA);
				TopicoAulaMBean tBean = getMBean("topicoAula");
				
				String tipoAula;
				if (object.getTipo().equals(AulaExtra.ENSINO_INDIVIDUAL))
					tipoAula = " - Aula de Ensino Individual";
				else {
					tipoAula = " - Aula Extra ";
					tipoAula += object.getTipo().equals(AulaExtra.ADICIONAL) ? "Adicional" : "de Reposição";
				}
				
				t.setDescricao(object.getDescricao() + tipoAula);
				
				if ( object.getObservacoes() != "" )
					t.setConteudo(object.getObservacoes());
				else
					t.setConteudo(object.getTipo().equals(AulaExtra.ENSINO_INDIVIDUAL) ? "Aula de Ensino Individual." : "Aula Extra.");
				
				t.setDataCadastro(object.getCriadoEm());
				t.setData(object.getDataAula());
				t.setFim(object.getDataAula());
				t.setVisivel(true);
				tBean.setObject(t);
				tBean.atualizar();
				tBean.setDatasAulas(null);
				tBean.setItens(null);
				tBean.setListagem(null);
				tBean.setTopicosAulas(null);
			}
		} catch (DAOException e) {
			e.printStackTrace();
		} catch (ArqException e) {
			e.printStackTrace();
		}finally {
			if ( dao != null )
				dao.close();
		}
		
	}
	

	/**
	 * Verifica se alguma frequência possui o número de faltas maior que o número máximo de aulas. Se tiver, atualiza o a frequência e avisa ao usuário. <br/><br/>
	 * 
	 * Método não invocado por jsps. 
	 * @throws NegocioException 
	 * @throws ArqException 
	 */
	private void verificaAlteracaoFrequencia() throws ArqException, NegocioException {
		
		TurmaDao tDao = null;
		
		try {
		
			tDao = getDAO(TurmaDao.class);
			
			// Verificação de frequências na data da aula extra
			ArrayList<FrequenciaAluno> frequencias =  (ArrayList<FrequenciaAluno>) tDao.findFrequenciasByTurma(object.getTurma(), object.getDataAula());
			short maxFaltas = FrequenciaHelper.getMaxFaltasData(object.getTurma(), object.getDataAula());
			boolean freqAlteradas = false;
			
			for (FrequenciaAluno f : frequencias){
				if (f.getFaltas() > maxFaltas) {
					f.setFaltas(maxFaltas);
					freqAlteradas = true;
				}	
			}
			
			if (freqAlteradas) {
				String desc = object.getDataAula() +" - Aula Extra";
				registrarAcao(null, EntidadeRegistroAva.FREQUENCIA, AcaoAva.INICIAR_ALTERACAO, turma().getId() );
				prepareMovimento(SigaaListaComando.LANCAR_FREQUENCIA);
				execute(new FrequenciaMov(frequencias), getCurrentRequest());
				addMensagem(MensagensArquitetura.OPERACAO_SUCESSO);
				registrarAcao(desc, EntidadeRegistroAva.FREQUENCIA, AcaoAva.ALTERAR, turma().getId());
				addMensagemWarning("Caro usuário, o número de faltas dos discentes foi reajustado automáticamente para não superar o novo número de aulas do dia.");
			}
		} finally {
			if (tDao != null)
				tDao.close();
		}
	}
	
	private void registrarAcao(Date dataAula, EntidadeRegistroAva frequencia,
			AcaoAva inserir, int id) {
		// TODO Auto-generated method stub
		
	}

	
	/**
	 * Remove o tópico de aula antes de remover
	 * 
	 * Método não invocado por JSPs.  
	 */
	public void antesRemocao(){
		GenericDAO dao = null;
		
		try {
			dao = getGenericDAO();
			TopicoAula t = dao.findByExactField(TopicoAula.class, "aulaExtra.id", object.getId(),true);
			if ( t != null ){
				TopicoAulaMBean tBean = getMBean("topicoAula");
				tBean.setObject(t);
				tBean.setRemocaoAulaExtra(true);
				tBean.remover();
			}
		} catch (DAOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			if ( dao != null )
				dao.close();
		}
		
	}
	
	@Override
	public String listar() {
		prepare(SigaaListaComando.REMOVER_AVA);
		return forward("/ava/" + getClasse().getSimpleName() + "/listar.jsp");
	}
	
	/**
	 * Remover uma aula extra se não estiver associada com um plano de aula.<br/><br/>
	 * 
	 * Método chamado pela seguinte JSP: /ava/AulaExtra/listar.jsp
	 */
	public String remover() {
		
//		PlanoReposicaoAulaDao dao = null;
//		
//		try {
//			dao = getDAO(PlanoReposicaoAulaDao.class);
//			PlanoReposicaoAula planoAula = dao.findByAulaExtra(getParameterInt("id"));
//			
//			if (planoAula != null) {
//				addMensagem(MensagensTurmaVirtual.AULA_EXTA_ASSOCIADA_AO_PLANO_DE_AULA);
//				return null;
//			}
//		} catch (DAOException e) {
//			tratamentoErroPadrao(e);
//			return null;
//		} finally {
//			if (dao != null)
//				dao.close();
//		}
		
		try {
			object = getGenericDAO().findByPrimaryKey(getParameterInt("id"), getClasse());
			
			if ( object != null )
			{	
				ArrayList<PlanoReposicaoAula> planoAula = (ArrayList<PlanoReposicaoAula>) getGenericDAO().findByExactField(PlanoReposicaoAula.class, "aulaExtra.id", object.getId());
				
				if (planoAula != null && planoAula.size() > 0) {
					addMensagem(MensagensTurmaVirtual.AULA_EXTRA_ASSOCIADA_AO_PLANO_DE_AULA);
					return null;
				}
				
				registrarAcao(object.getDescricao(), EntidadeRegistroAva.AULA_EXTRA, AcaoAva.INICIAR_REMOCAO, object.getId());
				antesRemocao();
				
				Notification notification = null;
				if ( object != null ) {
					notification = execute(SigaaListaComando.REMOVER_AVA, object, getEspecificacaoRemocao());	
				}
	
				if (notification != null && notification.hasMessages())
					return notifyView(notification);
				
				registrarAcao(object.getDescricao(), EntidadeRegistroAva.AULA_EXTRA, AcaoAva.REMOVER, object.getId());
				listagem = null;		
				flash("Aula Extra removida com sucesso.");
				
				if (paginaOrigem != null && paginaOrigem.equals("portalPrincipal")){
					TurmaVirtualMBean tBean = getMBean ("turmaVirtual");
					return tBean.retornarParaTurma();
				}
			}else
				addMensagem(MensagensArquitetura.OBJETO_SELECIONADO_FOI_REMOVIDO);
		} catch (DAOException e) {
			throw new TurmaVirtualException(e);
		}
		
		return listar();
		
	}
	
	/**
	 * Realiza a validação do cadastro de aulas extras.<br/><br/>
	 * 
	 * Método chamado pela seguinte JSP: Não é chamado por JSPs.
	 */
	@Override
	public Specification getEspecificacaoCadastro() {
		return new Specification() {
			Notification notification = new Notification();
			
			public Notification getNotification() {
				return notification;
			}

			public boolean isSatisfiedBy(Object objeto) {
				AulaExtra aula = (AulaExtra) objeto;
				if (isEmpty(aula.getDataAula()))
					notification.addError("Data: Erro de formato de data ou data inválida.");
				if (!turma().isInfantil()){
				if (!isEmpty(aula.getDataAula()) && aula.getDataAula().before(turma().getDataInicio()))
					notification.addError("Data: A data precisa ser posterior ao início letivo da turma.");
				}else {
					if (!isEmpty(aula.getDataAula())){
						Calendar cal = Calendar.getInstance();
						cal.setTime(aula.getDataAula());
						int ano = cal.get(Calendar.YEAR);
						if (ano < turma().getAno())
						notification.addError("Data: A data precisa ser posterior ao início letivo da turma.");
					}	
				}
				if (isEmpty(aula.getNumeroAulas()))
					notification.addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Número de Aulas");
				if (isEmpty(aula.getDescricao()))
					notification.addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Descrição");
				return !notification.hasMessages();
			}
		};
	}

	/**
	 * Realiza a alteração do cadastro de aulas extras.<br/><br/>
	 * 
	 * Método chamado pela seguinte JSP: Não é chamado por JSPs.
	 */
	@Override
	public Specification getEspecificacaoAtualizacao() {
		return new Specification() {
			Notification notification = new Notification();
			
			public Notification getNotification() {
				return notification;
			}

			public boolean isSatisfiedBy(Object objeto) {
				AulaExtra aula = (AulaExtra) objeto;
				if (isEmpty(aula.getDataAula()))
					notification.addError("Data: Erro de formato de data ou data inválida.");
				if (!isEmpty(aula.getDataAula()) && aula.getDataAula().before(turma().getDataInicio()))
					notification.addError("Data: A data precisa ser posterior ao início letivo da turma.");
				if (isEmpty(aula.getNumeroAulas()))
					notification.addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Número de Aulas");
				if (isEmpty(aula.getDescricao()))
					notification.addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Descrição");
				return !notification.hasMessages();
			}
		};
	}
	
	/**
	 * Cria uma lista de SelectItem que representam as possíveis aulas
	 * que poderão ser repostas por uma aula extra. <br/><br/>
	 * 
	 * Método chamado pela seguinte JSP: sigaa.war/ava/AulaExtra/_form.jsp
	 */
	public List<SelectItem> getAulasReposicao() throws DAOException{
		
		TopicoAulaDao topDao = null;		
		
		try{
			topDao = getDAO(TopicoAulaDao.class);
			List<Date> aulasReposicao = topDao.findDatasDeTopicosSemAula(turma().getId());
			aulaParaReposicao = new ArrayList<SelectItem>();
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			
			aulaParaReposicao.add(new SelectItem(null,"--"));
						
			if ( aulasReposicao != null ) 
				for ( Date d : aulasReposicao ){
					aulaParaReposicao.add(new SelectItem(d.getTime(),sdf.format(d)));
				}	
			
			return aulaParaReposicao;
		}finally{
			if ( topDao != null )
				topDao.close();
		}

	}
	
	/**
	 * Retorna os tipos de aulas extras para serem usados em um combo box.<br/><br/>
	 * 
	 * Método chamado pela seguinte JSP: /ava/AulaExtra/_form.jsp
	 * @return
	 */
	public List<SelectItem> getTiposAulaExtra() {

		tipos = new ArrayList<SelectItem>();
		
		tipos.add(new SelectItem(AulaExtra.ADICIONAL, AulaExtra.nomeTipo(AulaExtra.ADICIONAL)));
		tipos.add(new SelectItem(AulaExtra.REPOSICAO, AulaExtra.nomeTipo(AulaExtra.REPOSICAO)));

		return tipos;
	}

	public Turma getTurma() {
		return turma;
	}

	public void setTurma(Turma turma) {
		this.turma = turma;
	}

	public void setNotificar(boolean notificar) {
		this.notificar = notificar;
	}

	public boolean isNotificar() {
		return notificar;
	}

	/**
	 * Seta a data de resposta do tópico.
	 * 
	 * @param time
	 */
	public void setDataReposta (Long time){
		if ( time != null )
			object.setDataAulaReposta((new Date(time)));
	}
	
	/**
	 * Retorna a data da aula que está sendo reposta pela aula extra.<br/><br/>
	 * 
	 * Método chamado pela seguinte JSP: sigaa.war/ava/AulaExtra/_form.jsp
	 * @return
	 */
	public Long getDataReposta(){
		
		if (object != null && object.getDataAulaReposta() != null)
			return object.getDataAulaReposta().getTime();
		
		return null;
	}

	public void setTipos(List<SelectItem> tipos) {
		this.tipos = tipos;
	}

	public List<SelectItem> getTipos() {
		return tipos;
	}

	public void setAulaParaReposicao(List<SelectItem> aulaParaReposicao) {
		this.aulaParaReposicao = aulaParaReposicao;
	}

}
