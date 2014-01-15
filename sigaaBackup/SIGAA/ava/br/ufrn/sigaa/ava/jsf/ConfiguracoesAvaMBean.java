/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 31/01/2008
 *
 */

package br.ufrn.sigaa.ava.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.model.SelectItem;

import org.hibernate.HibernateException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.ead.EADDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ava.dao.AvaliacaoDao;
import br.ufrn.sigaa.ava.dao.TurmaVirtualDao;
import br.ufrn.sigaa.ava.dominio.ConfiguracoesAva;
import br.ufrn.sigaa.ava.validacao.NullSpecification;
import br.ufrn.sigaa.ead.dominio.MetodoAvaliacao;
import br.ufrn.sigaa.ensino.dominio.Avaliacao;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.jsf.ConsolidarTurmaMBean;
import br.ufrn.sigaa.ensino.util.TurmaUtil;

/**
 * Managed Bean para a configuração de parâmetros necessários para cada Turma Virtual.
 * 
 * @author David Pereira
 *
 */
@Component("configuracoesAva") @Scope("request")
public class ConfiguracoesAvaMBean extends ControllerTurmaVirtual {

	/** A turma da configuração */
	private Turma turma;
	
	/** Atributo que contem a configuração da turma virtual selecionada. */
	private ConfiguracoesAva config = new ConfiguracoesAva();
	
	/** Atributo que define se a configuração pertence ao cadastro da avaliação institucional	 */
	private boolean cadastroAvaliacao;
	
	/** Indica se após configurar a turma, é para voltar ao caso de uso de cadastra um questionário. */
	private boolean cadastroQuestionario;
	
	/** Indica se o usuário tem acesso as configurações pelo link Configurações > Publicar turma */
	private boolean opcaoCursosAbertos = false;
	
	/** Atributo que indica se a configuração está associda a chamada da turma biométrica */
	private boolean turmaChamadaBiometrica;
	
	/** Indica se está no Portal do Docente */
	private boolean portalDocente = false;
	
	/** Atributo que indica as opções de tamanho máximo dos arquivos que os alunos podem enviar */
	private List <SelectItem> tamanhosEnvioAluno = new ArrayList <SelectItem> ();

	/** Indica que a configuração foi acessada através do cadastro de uma tarefa. */
	private boolean cadastroTarefa;
	
	/** Indica que a configuração foi acessada através da edição de uma tarefa. */
	private boolean edicaoTarefa;
		
	/** Indica o número de unidades da turma. */
	private Integer numUnidades;
	
	/** Guarda a subturma passada pelo cadastro de avaliação na tela de consolidação */
	private Turma subturma;
	
	/** Construtor que inicializa os atributos envolvidos no caso de uso */
	public ConfiguracoesAvaMBean() throws HibernateException, DAOException {
		//turmaChamadaBiometrica = getDAO(TurmaVirtualDao.class).findEstacaoChamadaBiometricaByTurma(turma());
		
		tamanhosEnvioAluno.add(new SelectItem (10, "10"));
		tamanhosEnvioAluno.add(new SelectItem (20, "20"));
		tamanhosEnvioAluno.add(new SelectItem (50, "50"));
	}
	
	/**
	 * Direciona o usuário para a tela de configuração da turma virtual. <br/><br/>
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li> /ava/menu.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException 
	 */
	public String configurar () throws DAOException{
		return configurar(null);
	}
	
	/**
	 * Direciona o usuário para a tela de configuração da turma virtual.
	 * Método não invocado por JSP's.
	 * @param turma a turma virtual a se configurar.
	 * 
	 * @return
	 * @throws DAOException 
	 * @throws HibernateException 
	 */
	public String configurar(Turma turma) throws DAOException {
		
		if (isEmpty(turma))
			turma = turma();
		
		// A estação biométrica é para cada subturma, então salva o id passado para saber qual subturma está sendo acessada.
		int idParaEstacaoBiometrica = turma.getId();
		
		// Nunca configura uma subturma. A configuração sempre vai para a agrupadora.
		if (turma.getTurmaAgrupadora() != null){
			subturma = turma;
			turma = turma.getTurmaAgrupadora();
		}
		
		GenericDAO dao = null;
		
		try {
			dao = getGenericDAO();
			this.turma = dao.refresh(turma);
			
			this.turma.setDocentesTurmas(turma.getDocentesTurmas());
		} finally {
			if (dao != null)
				dao.close();
		}
		
		numUnidades = null;
		config = getDAO(TurmaVirtualDao.class).findConfiguracoes(turma);
		if (config == null) {
			config = new ConfiguracoesAva();
			config.setMostrarEstatisticaNotas(true);
		}
		
		turmaChamadaBiometrica = getDAO(TurmaVirtualDao.class).findEstacaoChamadaBiometricaByTurma(new Turma(idParaEstacaoBiometrica));

		return forward("/ava/ConfiguracoesAva/form.jsp");
	}
	
	public boolean isCadastroTarefa() {
		return cadastroTarefa;
	}

	public void setCadastroTarefa(boolean cadastroTarefa) {
		this.cadastroTarefa = cadastroTarefa;
	}

	/**
	 * Direciona o usuário para a tela de publicação de uma turma virtual.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li> /ava/turmasAbertas.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException 
	 * @throws HibernateException 
	 */
	public String publicarTurmaVirtual() throws HibernateException, DAOException { 
		TurmaVirtualMBean turmaMBean = (TurmaVirtualMBean) getMBean("turmaVirtual");
		turmaMBean.setTurma(null);
		
		config = getDAO(TurmaVirtualDao.class).findConfiguracoes(turma());
		if (config == null)
			config = new ConfiguracoesAva();
		
		turmaChamadaBiometrica = getDAO(TurmaVirtualDao.class).findEstacaoChamadaBiometricaByTurma(turma());
		return forward("/ava/publicarTurma.jsp");
	}	

	/**
	 * Valida senhas digitadas.
	 * Método não invocado por JSP's.
	 * @param senha1
	 * @param senha2
	 * @return
	 */
	private boolean validarSenhasDigitadas(String senha1, String senha2) {
		boolean semErros = true;
		
		if ( ValidatorUtil.isEmpty(senha1) || ValidatorUtil.isEmpty(senha1) ) {
			addMensagemErro("Campos de senha não podem ser vazios.");
			return false;
		}

		if (!senha1.equals( senha2) ) {
			addMensagemErro("As senhas digitadas NÃO são iguais!");
			semErros = false;
		}
		
		if (senha1.length() < 6 || senha2.length() < 6 ) {
			addMensagemErro("A senha precisa ter entre 6 e 8 dígitos");
			semErros = false;
		}
		
		return semErros;
	}
	
	/**
	 * Valida tempo de tolerância ( Limite de tempo máximo que permite mesmo os alunos chegando atrasados, 
	 * receberem presença completa  da aula correspondente, quando registram a presença
	 * por biometria).
	 * Método não invocado por JSP's.
	 * @param senha1
	 * @param senha2
	 * @return
	 */
	private boolean validarTempoTolerancia(int tempoTolernacia) {
		boolean semErros = true;
		
		if ( ValidatorUtil.isEmpty(tempoTolernacia) || tempoTolernacia < 5 || tempoTolernacia > 20) {
			addMensagemErro("Tempo de tolerância deve ser de 5 a 20 minutos.");
			return false;
		}
		return semErros;
	}
	
	/**
	 * Cria uma configuracao padrão para a turma.
	 * Método não invocado por JSP's.
	 * @param turma a turma virtual a se configurar.
	 * 
	 * @return
	 * @throws DAOException 
	 * @throws HibernateException 
	 */
	public ConfiguracoesAva criar(Turma turma) throws DAOException {
		
		if (isEmpty(turma))
			turma = turma();
				
		// Nunca configura uma subturma. A configuração sempre vai para a agrupadora.
		if (turma.getTurmaAgrupadora() != null)
			turma = turma.getTurmaAgrupadora();
		
		this.turma = turma;
		
		config = getDAO(TurmaVirtualDao.class).findConfiguracoes(turma);
		if (config == null)
			config = new ConfiguracoesAva();

		config.setTurma(this.turma);
		config.setTipoMediaAvaliacoes1(ConfiguracoesAva.TIPO_AVALIACAO_MEDIA_PONDERADA);
		config.setTipoMediaAvaliacoes2(ConfiguracoesAva.TIPO_AVALIACAO_MEDIA_PONDERADA);
		config.setTipoMediaAvaliacoes3(ConfiguracoesAva.TIPO_AVALIACAO_MEDIA_PONDERADA);
		config.setTipoVisualizacaoNota(ConfiguracoesAva.TIPO_VISUALIZACAO_NOTA_INDIVIDUAL);
		config.setEstiloVisualizacaoTopicos(ConfiguracoesAva.ESTILO_TOPICOS_LISTA);
		
		return config;

	}

	
	/**
	 * Salva as configurações de uma turma.<br/><br/>
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>  /ava/ConfiguracoesAva/form.jsp</li>
	 * </ul>
	 * @return
	 * @throws Exception
	 */
	public String salvar() throws Exception {
		
		String senha1 = getParameter("senha1");
		String senha2 = getParameter("senha2");
		int tempoTolerancia = getParameterInt("tempoTolerancia",0);
		
		//Popula a data em que a turma virtual foi publicada para listagem no portal público dos cursos abertos
		if(config.isPermiteVisualizacaoExterna())
			config.setDataVisualizacaoExterna(new Date());
		
		if (turmaChamadaBiometrica)
			if ( validarSenhasDigitadas(senha1, senha2) && validarTempoTolerancia(tempoTolerancia)) {
				config.setSenhaChamadaBiometrica( UFRNUtils.toMD5(senha1) );
				config.setTempoTolerancia(tempoTolerancia);
			}
			else
				return null;
		
		if(!opcaoCursosAbertos){
			try {
				temProblemaMudancaMetodoCalculoMedia();
			} catch(NegocioException e) {
				addMensagemErro(e.getMessage());
				return null;
			}
		}
		
		// SOLUÇÃO PARA BUG
		if (config.getId() == 0) {
			prepare(SigaaListaComando.CADASTRAR_AVA);
		} else {
			prepare(SigaaListaComando.ATUALIZAR_AVA);
		}
		
		if (isEmpty(turma))
			turma = turma();
		
		config.setTurma(turma);
		
		execute(config.getId() == 0 ? SigaaListaComando.CADASTRAR_AVA : SigaaListaComando.ATUALIZAR_AVA, config, new NullSpecification());
		flash("Configurações salvas com sucesso.");
		
		// Atualiza o objeto que armazena as configurações no TurmaVirtualMBean
		TurmaVirtualMBean tBean = getMBean("turmaVirtual");
		tBean.reiniciarConfiguracoes();
		
		if (portalDocente)
			return forward("/portais/docente/docente.jsf");
		else if (cadastroQuestionario)
			return forward("/ava/QuestionarioTurma/formDadosQuestionario.jsf");
		else if (cadastroTarefa){
			cadastroTarefa = false;
			return forward("/ava/TarefaTurma/novo.jsf");
		} else if (edicaoTarefa){
			edicaoTarefa = false;
			return forward("/ava/TarefaTurma/editar.jsf");
		} else if (!cadastroAvaliacao && !cadastroTarefa && !edicaoTarefa) {
			return forward("/ava/ConfiguracoesAva/form.jsf");
		} else {
			ConsolidarTurmaMBean consolidarTurma = getMBean("consolidarTurma");
			if (subturma!=null)
				consolidarTurma.setTurma(subturma);
			return consolidarTurma.consolidaTurmaPortal();
		}
	}
	
	/**
	 * Identifica se é possível mudar o tipo de cálculo de média de uma unidade
	 * da turma. É possível se não existirem avaliações cadastradas para a unidade.
	 * Método não invocado por JSP's.
	 * @throws NegocioException
	 * @throws DAOException
	 */
	private void temProblemaMudancaMetodoCalculoMedia() throws NegocioException, DAOException {
		AvaliacaoDao dao = getDAO(AvaliacaoDao.class);

		TurmaVirtualDao tvDao = getDAO(TurmaVirtualDao.class);
		ConfiguracoesAva configDB = tvDao.findConfiguracoes(turma());
		
		if (configDB != null && configDB.getTipoMediaAvaliacoes1() != null && !config.getTipoMediaAvaliacoes1().equals(configDB.getTipoMediaAvaliacoes1())) {
			List<Avaliacao> avalUnid = dao.findByTurmaUnidade(turma(), 1);
			if (!isEmpty(avalUnid)) {
				throw new NegocioException("Não é possível mudar o tipo de cálculo de média da unidade 1 porque já existem avaliações cadastradas. Remova as avaliações antes.");
			}
		}
		
		if (configDB != null && configDB.getTipoMediaAvaliacoes2() != null && !config.getTipoMediaAvaliacoes2().equals(configDB.getTipoMediaAvaliacoes2())) {
			List<Avaliacao> avalUnid = dao.findByTurmaUnidade(turma(), 2);
			if (!isEmpty(avalUnid)) {
				throw new NegocioException("Não é possível mudar o tipo de cálculo de média da unidade 2 porque já existem avaliações cadastradas. Remova as avaliações antes.");
			}
		}
		
		if (configDB != null && configDB.getTipoMediaAvaliacoes3() != null && !config.getTipoMediaAvaliacoes3().equals(configDB.getTipoMediaAvaliacoes3())) {
			List<Avaliacao> avalUnid = dao.findByTurmaUnidade(turma(), 3);
			if (!isEmpty(avalUnid)) {
				throw new NegocioException("Não é possível mudar o tipo de cálculo de média da unidade 3 porque já existem avaliações cadastradas. Remova as avaliações antes.");
			}
		}
	}

	/**
	 * Cancela a operação de configurar uma turma virtual.<br/><br/>
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>  /ava/ConfiguracoesAva/form.jsp<;li>
	 * </ul>
	 */
	@Override
	public String cancelar() {
		if (!cadastroAvaliacao) {
			return super.cancelar();
		} else {
			try {
				ConsolidarTurmaMBean consolidarTurma = getMBean("consolidarTurma");
				return consolidarTurma.consolidaTurmaPortal();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}
	
	
	/**
	 * Retorna a configuração da turma virtual.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li> /sigaa.war/ava/ConfiguracoesAva/form.jsp</li>
	 * </ul>
	 * @return
	 */
	public ConfiguracoesAva getConfig() {
		return config;
	}
	
	/**
	 * Define a configuração da turma virtual.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li> /sigaa.war/ava/publicarTurma.jsp</li>
	 * </ul>
	 * @param config
	 */
	public void setConfig(ConfiguracoesAva config) {
		this.config = config;
	}
	
	/**
	 * Retorna os possíveis tipos de cálculo de média das avaliações.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li> /sigaa.war/ava/ConfiguracoesAva/form.jsp</li>
	 * </ul>
	 * @return
	 */
	public List<SelectItem> getTiposMediaAvaliacoes() {
		List<SelectItem> result = new ArrayList<SelectItem>();
		result.add(new SelectItem(Character.valueOf('P'), "Média ponderada das avaliações"));
		result.add(new SelectItem(Character.valueOf('A'), "Média aritmética das avaliações"));
		result.add(new SelectItem(Character.valueOf('S'), "Soma das notas das avaliações"));
		return result;
	}
	
	/**
	 * Retorna os possíveis tipos de visualização de nota no relatório de notas da turma virtual.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li> /sigaa.war/ava/ConfiguracoesAva/form.jsp</li>
	 * </ul>
	 * @return
	 */
	public List<SelectItem> getTiposVisualizacaoNota() {
		List<SelectItem> result = new ArrayList<SelectItem>();
		result.add(new SelectItem(Character.valueOf('I'), "Apenas a sua nota"));
		result.add(new SelectItem(Character.valueOf('A'), "As notas de todos os alunos"));
		return result;
	}

	/**
	 * Retorna o numero de unidades da turma
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li> /sigaa.war/ava/ConfiguracoesAva/form.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException 
	 */
	public Integer getNumeroUnidadesTurma() throws ArqException {

		if ( numUnidades == null  ) {
			
			EADDao dao = null;
			try {
	
				dao = getDAO(EADDao.class);
				
				if ( turma == null ){
					numUnidades = 0;
					addMensagemErro("Por favor, reinicie o processo utilizando os links oferecidos pelo sistema.");
					redirectJSF(getSubSistema().getLink());
					return null;
				}
				
				if ( !turma.isEad() ) {
					numUnidades = TurmaUtil.getNumUnidadesDisciplina(turma);
					return numUnidades;
				}	
				
				Integer metodologia = dao.findMetodologiaAvaliacaoByTurma(turma.getId());
				
				if ( metodologia == null ) {
					numUnidades = TurmaUtil.getNumUnidadesDisciplina(turma);
					return numUnidades;
				}
				
				if ( metodologia == MetodoAvaliacao.UMA_PROVA )
					numUnidades = 1;
				else if ( metodologia == MetodoAvaliacao.DUAS_PROVAS_RECUPERACAO )
					numUnidades = 2;
				else
					numUnidades = TurmaUtil.getNumUnidadesDisciplina(turma);
				
			} catch (NegocioException e) {
				// TODO Auto-generated catch block
				tratamentoErroPadrao(e);
			} finally {
				if ( dao != null )
					dao.close();
			}
		}
		return numUnidades;
	}

	
	/**
	 * Define se a configuração está associda ao cadastro da avaliação.
	 * Método não invocado por JSP.
	 * @param cadastroAvaliacao
	 */
	public void setCadastroAvaliacao(boolean cadastroAvaliacao) {
		this.cadastroAvaliacao = cadastroAvaliacao;
	}

	/**
	 * Retorna se a configuração está associda ao cadastro da avaliação.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li> /sigaa.war/ava/ConfiguracoesAva/form.jsp</li>
	 * </ul>
	 * @return
	 */
	public boolean isCadastroAvaliacao() {
		return cadastroAvaliacao;
	}

	/**
	 * Retorna se a configuração está associada aos cursos abertos.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li> /sigaa.war/ava/ConfiguracoesAva/form.jsp</li>
	 * </ul>
	 * @return
	 */
	public boolean isOpcaoCursosAbertos() {
		return opcaoCursosAbertos;
	}

	/**
	 * Define se a configuração está associada aos cursos abertos.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li> /sigaa.war/ava/ConfiguracoesAva/form.jsp</li>
	 * </ul>
	 * @param opcaoCursosAbertos
	 */
	public void setOpcaoCursosAbertos(boolean opcaoCursosAbertos) {
		this.opcaoCursosAbertos = opcaoCursosAbertos;
	}

	/**
	 * Retorna se a turma virtual possui chamada biométrica.
	 * @return
	 */
	public boolean isTurmaChamadaBiometrica() {
		return turmaChamadaBiometrica;
	}

	/**
	 * Define se a turma virtual possui chamada biométrica.
	 * @return
	 */
	public void setTurmaChamadaBiometrica(boolean turmaChamadaBiometrica) {
		this.turmaChamadaBiometrica = turmaChamadaBiometrica;
	}

	/**
	 * Retorna todas as opções de tamanho arquivos que os alunos podem enviar .
	 * @return
	 */
	public List<SelectItem> getTamanhosEnvioAluno() {
		return tamanhosEnvioAluno;
	}

	/**
	 * Define todas as opções de tamanho arquivos que os alunos podem enviar .
	 * @return
	 */
	public void setTamanhosEnvioAluno(List<SelectItem> tamanhosEnvioAluno) {
		this.tamanhosEnvioAluno = tamanhosEnvioAluno;
	}

	/**
	 * Retorna se a turma virtual está no Portal do Docente
	 * @return
	 */
	public boolean isPortalDocente() {
		return portalDocente;
	}

	/**
	 * Define se a turma virtual está no Portal do Docente
	 * @param portalDocente
	 */
	public void setPortalDocente(boolean portalDocente) {
		this.portalDocente = portalDocente;
	}

	/**
	 * Retorna a turma que está associada a configuração.
	 * @return
	 */
	public Turma getTurma() {
		return turma;
	}
	
	/**
	 * Define a turma que está associada a configuração.
	 * @param turma
	 */
	public void setTurma(Turma turma) {
		this.turma = turma;
	}

	public boolean isCadastroQuestionario() {
		return cadastroQuestionario;
	}

	public void setCadastroQuestionario(boolean cadastroQuestionario) {
		this.cadastroQuestionario = cadastroQuestionario;
	}

	public void setEdicaoTarefa(boolean edicaoTarefa) {
		this.edicaoTarefa = edicaoTarefa;
	}

	public boolean isEdicaoTarefa() {
		return edicaoTarefa;
	}

	public void setSubturma(Turma subturma) {
		this.subturma = subturma;
	}

	public Turma getSubturma() {
		return subturma;
	}
}