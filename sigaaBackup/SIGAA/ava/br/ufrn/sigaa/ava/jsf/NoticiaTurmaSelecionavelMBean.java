/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 27/11/2008
 *
 */
package br.ufrn.sigaa.ava.jsf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.LimiteResultadosException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.sigaa.arq.dao.ensino.TurmaDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ava.dao.TurmaVirtualDao;
import br.ufrn.sigaa.ava.dominio.NoticiaTurma;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.mensagens.MensagensTurmaVirtual;

/**
 * Managed bean responsável pelo cadastro e notificações de turmas, permitindo
 * a seleção da turma desejada para a notificação.
 * 
 * Será utilizado fora do contexto da turma virtual.
 * 
 * @author wendell
 *
 */
@Component("noticiaTurmaSelecionavelBean") @Scope("request")
public class NoticiaTurmaSelecionavelMBean extends SigaaAbstractController<NoticiaTurma> {

	/** Notificar participantes da turma por e-mail. */
	boolean enviarEmail = false;

	/** Notícias da turma. */
	private Collection<NoticiaTurma> noticias;
	
	/** Lista de turmas de acordo com o departamento da chefia. */
	private List<Turma> listaTurma;
	
	/** Lista de turmas que serão cadastrados os objetos */
	private List<String> cadastrarEm;
	
	/** Turma selecionada */
	private Turma turma;
	
	public NoticiaTurmaSelecionavelMBean() {
		obj = new NoticiaTurma();
		obj.setTurma(new Turma());
	}
	
	/**
	 * Esse método tem como finalidade listar todas as turma para que o chefe de departamento possa enviar uma mensagem 
	 * para qual quer uma das turma do seu departamento.
	 * <br />
	 * Chamado pela JSP:
	 * <ul>
	 * <li>/sigaa.war/ensino/turma/busca_turma.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws LimiteResultadosException
	 * @throws DAOException
	 */
	public String exibirTodasAsTurmas() throws DAOException {
		TurmaDao dao = null;
		
		try {
			dao = getDAO(TurmaDao.class);
			listaTurma = dao.findByDepartamento(getUsuarioLogado().getVinculoAtivo().getUnidade(), CalendarUtils.getAnoAtual(), getPeriodoAtual());
			return forward("/graduacao/envio_mensagem/form_selecao_turma.jsf");
		}finally{
			if ( dao != null )
				dao.close();
		}	
	}
	
	/**
	 * Exibe o formulário para selecionar a turma para cadastrar uma notícia.<br/><br/>
	 * 
	 * Método chamado pela seguinte JSP: /ensino/turma/busca_turma.jsp
	 * 
	 * @return
	 * @throws ArqException 
	 * @throws NegocioException 
	 */
	public String selecionarTurma() throws ArqException, NegocioException {
		checkRole(SigaaPapeis.COORDENADOR_CURSO, SigaaPapeis.DAE, SigaaPapeis.ADMINISTRADOR_DAE, 
					SigaaPapeis.PPG, SigaaPapeis.COORDENADOR_CURSO_STRICTO, SigaaPapeis.SECRETARIA_POS, 
					SigaaPapeis.CHEFE_DEPARTAMENTO, SigaaPapeis.GESTOR_TECNICO);

		clear();
		getTurma().setId( getParameterInt("id", 0) );
		
		// Validar acesso do usuário à turma, no caso de coordenadores
		TurmaDao turmaDao = getDAO(TurmaDao.class);
		Curso curso = getCursoAtualCoordenacao();
		
		if (isUserInRole(SigaaPapeis.COORDENADOR_CURSO) && curso == null)
			throw new NegocioException("Não foi possível definir qual o curso do qual você é coordenador ou secretário. Por favor, acesse a consulta de turmas a partir do Portal do Coordenador.");
		
		if (isUserInRole(SigaaPapeis.COORDENADOR_CURSO) && !turmaDao.existsAlunosCurso(getTurma(), curso)){
			addMensagem(MensagensTurmaVirtual.NAO_HA_ALUNOS_DO_CURSO_MATRICULADOS, curso.getDescricao());
			return null;
		}
		
		// Popular dados da turma selecionada
		turma = getGenericDAO().refresh(getTurma());
		if (turma == null) {
			addMensagem(MensagensTurmaVirtual.NENHUMA_TURMA_SELECIONADA);
			return null;
		}
		
		obj.setTurma(turma);
		popularNoticiasTurma();
		cadastrarEm = new ArrayList <String> ();
		cadastrarEm.add(""+turma.getId());
		
		setOperacaoAtiva(SigaaListaComando.CADASTRAR_AVA.getId());
		return forward(getFormPage());
	}
	
	/**
     * Cadastra a data da avaliação.<br/><br/>
	 * 
	 * Método chamado pelas seguintes JSPs:
     * <ul>
     * 		<li>/ava/NoticiaTurma/form_selecionavel.jsp</li>
     * </ul>
	 * @throws NegocioException 
     */
	@Override
	public String cadastrar() throws ArqException, NegocioException {
		if (isOperacaoAtiva(SigaaListaComando.CADASTRAR_AVA.getId())){
			setOperacaoAtiva(null);
			TurmaVirtualMBean tBean = getMBean ("turmaVirtual");
			turma = getGenericDAO().findAndFetch(turma.getId(), Turma.class, "docentesTurmas"); 
			cadastrarEm.add(String.valueOf(turma.getId()));
			tBean.setTurma(turma);
			NoticiaTurmaMBean nBean = getMBean ("noticiaTurma");
			nBean.setCadastrarEm(cadastrarEm);
			nBean.setObject(obj);
			nBean.setNotificar(false);
			nBean.cadastrar();
			afterCadastrar();
			popularNoticiasTurma();
		} else {
			addMensagemErro("O procedimento que você tentou realizar já foi processado anteriormente." +
			" Para realizá-lo novamente, reinicie o processo utilizando os links oferecidos pelo sistema.");
		}
		return voltarTurma();
	}
	
	/**
	 * Retorna todas as turmas do componente no ano e semestre atual<br/>
	 * 
	 * Método chamado pela seguinte JSP: /ensino/turma/busca_turma.jsp
	 * 
	 * @return
	 * @throws ArqException 
	 * @throws NegocioException 
	 */
	public List<Turma> getTurmasMesmaDisciplinaSemestre() throws ArqException, NegocioException {

		TurmaDao dao = null;
				
		try {
			dao = getDAO(TurmaDao.class);
			Turma t = turma;
			
			List<Turma> turmas = new ArrayList<Turma>();
			turmas = (List<Turma>) dao.findByDisciplinaAnoPeriodo(t.getDisciplina(),t.getAno(),t.getPeriodo(),0,(char)0);
			turmas = getTurmasSemestre(turmas);
			
			turmas.remove(t);
			
			Collections.sort(turmas, new Comparator<Turma>(){
				public int compare(Turma t1, Turma t2) {
					int retorno = 0;
					retorno = t2.getAno() - t1.getAno();
					if( retorno == 0 ) {
						retorno = t2.getPeriodo() - t1.getPeriodo();
						if ( retorno == 0 ){
							String nome = StringUtils.toAscii(t1.getDescricaoComDocentes());
							return nome.compareToIgnoreCase(StringUtils.toAscii( t2.getDescricaoComDocentes()));
						}
					}	
					return retorno;
				}
			});
			
			return turmas;
		}finally {
			if ( dao != null )
				dao.close();
		}
	}
	
	/**
	 * Retorna somente as turmas ou turmas agrupadoras.
	 * 
	 * @return
	 * @throws DAOException
	 */
	public List<Turma> getTurmasSemestre (List<Turma> turmas) throws DAOException {
		List <Turma> rs = new ArrayList <Turma> ();
		
		for (Turma t : turmas){
			if (t.getTurmaAgrupadora() != null){
				t.setId(t.getTurmaAgrupadora().getId());
				t.setCodigo(t.getTurmaAgrupadora().getCodigo());
			}
			
			if (!rs.contains(t))
				rs.add(t);
		}
		
		return rs;
	}
	
	/**
	 * Limpa o cache do mbean.
	 * Método não invocado por JSPs.
	 * @return
	 * @throws DAOException
	 */
	public void clear () {
		clearForm();
		listaTurma = null;
		noticias = null;
		turma = null;
	}

	/**
	 * Limpa o cache do formulário.
	 * Método não invocado por JSPs.
	 * @return
	 * @throws DAOException
	 */
	private void clearForm() {
		obj = new NoticiaTurma();
		obj.setTurma(new Turma());
		enviarEmail = false;
	}
	
	/**
	 * Encaminha o usuário para a página do subsistema atual após a operação de cadastrar.<br/><br/>
	 * Método não invocado por JSPs. É public por causa da arquitetura.
	 */
	@Override
	public String forwardCadastrar() {
		return getSubSistema().getLink();
	}
	
	/**
	 * Volta para a busca de turmas.<br/><br/>
	 * Método chamado pela seguinte JSP: /ensino/turma/form_selecionavel.jsp
	 */
	public String voltarTurma() {
		return forward("/ensino/turma/busca_turma.jsp");
	}
	
	@Override
	public String getFormPage() {
		return "/ava/NoticiaTurma/form_selecionavel.jsf";
	}
	
	/**
	 * Após realiar um cadastro, se o operador optar por isso, envia um email para os participantes da turma, avisando-os sobre a nova notícia.<br/>
	 */
	@Override
	protected void afterCadastrar() throws ArqException {
		
		TurmaDao dao = null;
		
		try {
			dao = getDAO(TurmaDao.class);
			// Envia e-mail para os participantes da turma
			if (enviarEmail) { 
				for (String tid : cadastrarEm){
					Turma t = dao.findByPrimaryKeyOtimizado(Integer.valueOf(tid));
					
					getTurmaVirtualMBean().notificarTurma(t, "Notícia da turma virtual - " 
							+  t.getNome() , 
							"<h1>" + obj.getDescricao() + "</h1>" + obj.getNoticia(), ControllerTurmaVirtual.DISCENTE, ControllerTurmaVirtual.DOCENTE, ControllerTurmaVirtual.AUTORIZADO);
				}
			}	
		} finally {
			if (dao!=null)
				dao.close();
		}
	}
	
	/**
	 * Verifica se os dados digitados estão corretos.<br/>
	 * Método não invocado por JSPs. É public por causa da arquitetura.
	 */
	@Override
	public void beforeCadastrarAndValidate() throws NegocioException, SegurancaException, DAOException {
		super.beforeCadastrarAndValidate();
		
		// Validar dados do formulário e repopular campos em caso de erro
		ListaMensagens erros = obj.validate();
		if (!erros.isEmpty()) {
			getGenericDAO().initialize(getTurma());
			popularNoticiasTurma();
			addMensagens(erros);
		}
	}

	/**
	 * Busca as notícias da turma.
	 */
	private void popularNoticiasTurma() {
		noticias = getDAO(TurmaVirtualDao.class).findNoticiasByTurma(getTurma());
	}
	
	private Turma getTurma() {
		return obj.getTurma();
	}
	
	private TurmaVirtualMBean getTurmaVirtualMBean() {
		return getMBean("turmaVirtual");
	}

	public boolean isEnviarEmail() {
		return enviarEmail;
	}

	public void setEnviarEmail(boolean enviarEmail) {
		this.enviarEmail = enviarEmail;
	}

	public Collection<NoticiaTurma> getNoticias() {
		return noticias;
	}

	public void setNoticias(Collection<NoticiaTurma> noticias) {
		this.noticias = noticias;
	}

	public List<Turma> getListaTurma() {
		return listaTurma;
	}

	public void setListaTurma(List<Turma> listaTurma) {
		this.listaTurma = listaTurma;
	}

	public void setCadastrarEm(List<String> cadastrarEm) {
		this.cadastrarEm = cadastrarEm;
	}

	public List<String> getCadastrarEm() {
		return cadastrarEm;
	}

	public void setTurma(Turma turma) {
		this.turma = turma;
	}

}