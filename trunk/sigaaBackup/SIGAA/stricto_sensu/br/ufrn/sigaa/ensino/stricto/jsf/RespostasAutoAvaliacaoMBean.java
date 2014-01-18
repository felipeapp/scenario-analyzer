/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Criado em 12/08/2013
 *
 */
package br.ufrn.sigaa.ensino.stricto.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import org.apache.commons.validator.EmailValidator;
import org.hibernate.HibernateException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.email.Mail;
import br.ufrn.arq.email.MailBody;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.JasperReportsUtil;
import br.ufrn.sigaa.arq.dao.ensino.CoordenacaoCursoDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ensino.dominio.CoordenacaoCurso;
import br.ufrn.sigaa.ensino.stricto.dao.CalendarioAplicacaoAutoAvaliacaoDao;
import br.ufrn.sigaa.ensino.stricto.dao.RespostasAutoAvaliacaoDao;
import br.ufrn.sigaa.ensino.stricto.dominio.CalendarioAplicacaoAutoAvaliacao;
import br.ufrn.sigaa.ensino.stricto.dominio.MetaAcaoFormacaoProducaoAcademica;
import br.ufrn.sigaa.ensino.stricto.dominio.MetaAcaoFormacaoRH;
import br.ufrn.sigaa.ensino.stricto.dominio.ParecerAutoAvaliacao;
import br.ufrn.sigaa.ensino.stricto.dominio.RespostasAutoAvaliacao;
import br.ufrn.sigaa.ensino.stricto.dominio.SituacaoRespostasAutoAvaliacao;
import br.ufrn.sigaa.ensino.stricto.dominio.SugestaoMelhoriaPrograma;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;
import br.ufrn.sigaa.questionario.dominio.QuestionarioRespostas;
import br.ufrn.sigaa.questionario.dominio.TipoQuestionario;
import br.ufrn.sigaa.questionario.jsf.QuestionarioRespostasMBean;

/**
 * Controller respons�vel por coletar as respostas do docente na auto avalia��o
 * do stricto sensu.
 * 
 * @author �dipo Elder F. de Melo
 * 
 */
@Component("respostasAutoAvaliacaoMBean") @Scope("request")
public class RespostasAutoAvaliacaoMBean extends SigaaAbstractController<RespostasAutoAvaliacao> {
	
	/**
	 * Metas e a��es de forma��o de Recursos Humanos para o pr�ximo per�odo
	 * sugeridos na Auto Avalia��o do Programa.
	 */
	private MetaAcaoFormacaoRH metaAcaoFormacaoRH;
	
	/**
	 * Meta de forma��o acad�mica e de produ��o cient�fica para o ano avaliado
	 * de um docente permanente e colaborador do Programa.
	 */
	private MetaAcaoFormacaoProducaoAcademica metaAcaoFormacaoProducaoAcademica;
	
	/** Calend�rio de Aplica��o de Auto Avalia��o que est� sendo trabalhado. */
	private CalendarioAplicacaoAutoAvaliacao calendarioAutoAvaliacao;
	
	/** Parecer dado pela Comiss�o. */
	private ParecerAutoAvaliacao parecerAutoAvaliacao;
	
	/** ID do docente avaliado. */
	private int idDocente;
	
	/**
	 * Construtor padr�o.
	 */
	public RespostasAutoAvaliacaoMBean() {
		obj = new RespostasAutoAvaliacao();
		metaAcaoFormacaoRH = new MetaAcaoFormacaoRH();
		metaAcaoFormacaoProducaoAcademica = new MetaAcaoFormacaoProducaoAcademica();
		parecerAutoAvaliacao = new ParecerAutoAvaliacao();
	}
	
	/** Retorna para o usu�rio uma lista de calend�rios de auto avalia��o atualmente ativos e dentro do per�odo de preenchimento.
	 * <br /> M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/stricto/menu_coordenador.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException 
	 * @throws HibernateException 
	 */
	public String iniciarPreenchimento() {
		all = null;
		return forward("/stricto/auto_avaliacao/lista_responder.jsp");
	}
	
	/** Retorna para o usu�rio uma lista de calend�rios de auto avalia��o atualmente ativos e dentro do per�odo de preenchimento.
	 * <br /> M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/stricto/auto_avaliacao/lista_responder.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException 
	 * @throws HibernateException 
	 */
	public String responderFormulario() throws ArqException {
		if (!isUserInRole(SigaaPapeis.COORDENADOR_CURSO_STRICTO, SigaaPapeis.COORDENADOR_LATO)){
			addMensagemErro("Somente o Coordenador do Programa poder� preencher a Auto Avalia��o.");
			return null;
		}
		int id = getParameterInt("id");
		calendarioAutoAvaliacao = getGenericDAO().findByPrimaryKey(id, CalendarioAplicacaoAutoAvaliacao.class);
		QuestionarioRespostasMBean mBean = getMBean("questionarioRespostasBean");
		mBean.inicializar(calendarioAutoAvaliacao.getQuestionario());
		RespostasAutoAvaliacaoDao dao = getDAO(RespostasAutoAvaliacaoDao.class);
		RespostasAutoAvaliacao respostasAutoAvaliacao = null;
		if (isPortalCoordenadorLato())
			respostasAutoAvaliacao = dao.findByCurso(getCursoAtualCoordenacao().getId(), calendarioAutoAvaliacao.getId());
		else
			respostasAutoAvaliacao = dao.findByPrograma(getProgramaStricto().getId(), calendarioAutoAvaliacao.getId());
		if (respostasAutoAvaliacao == null) {
			obj = new RespostasAutoAvaliacao();
			obj.setCalendario(calendarioAutoAvaliacao);
			if (isPortalCoordenadorLato()) {
				obj.setCurso(getCursoAtualCoordenacao());
				obj.setUnidade(null);
			} else {
				obj.setCurso(null);
				obj.setUnidade(getProgramaStricto());
			}
			obj.setSituacao(SituacaoRespostasAutoAvaliacao.SALVO);
		} else {
			obj = respostasAutoAvaliacao;
			mBean.popularVizualicacaoRespostas(respostasAutoAvaliacao.getRespostas());
			// prepara para o preenchimento do formul�rio
			if (obj.getMetasAcoesFormacaoAcademica() == null)
				obj.setMetasAcoesFormacaoAcademica(new LinkedList<MetaAcaoFormacaoProducaoAcademica>());
			else
				obj.getMetasAcoesFormacaoAcademica().iterator();
			if (obj.getMetasAcoesFormacaoRH() == null)
				obj.setMetasAcoesFormacaoRH(new LinkedList<MetaAcaoFormacaoRH>());
			else
				obj.getMetasAcoesFormacaoRH().iterator();
			if (obj.getSugestoesMelhoriaPrograma() == null)
				obj.setSugestoesMelhoriaPrograma(new SugestaoMelhoriaPrograma());
		}
		switch(obj.getSituacao()) {
			case REJEITADO : addMensagemErro("A sua Auto Avalia��o foi REJEITADA e n�o poder� ser alterada."); break;
			case SUBMETIDO : addMensagemErro("A sua Auto Avalia��o foi SUBMETIDA e est� sob an�lise da Comiss�o."); break;
			case ACEITO : addMensagemErro("A sua Auto Avalia��o foi ACEITA e n�o poder� ser mais alterada."); break;
			default: break;
		}
		if (hasErrors()) return null;
		prepareMovimento(SigaaListaComando.CADASTRAR_RESPOSTAS_AUTO_AVALIACAO);
		setOperacaoAtiva(SigaaListaComando.CADASTRAR_RESPOSTAS_AUTO_AVALIACAO.getId());
		return forward("/stricto/auto_avaliacao/responder.jsp");
	}
	
	/** Retorna para o usu�rio uma lista de calend�rios de auto avalia��o atualmente ativos e dentro do per�odo de preenchimento.
	 * <br /> M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/stricto/auto_avaliacao/lista.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException 
	 * @throws HibernateException 
	 */
	public String verRespostas() throws ArqException {
		int id = getParameterInt("id");
		calendarioAutoAvaliacao = getGenericDAO().findByPrimaryKey(id, CalendarioAplicacaoAutoAvaliacao.class);
		RespostasAutoAvaliacaoDao dao = getDAO(RespostasAutoAvaliacaoDao.class);
		RespostasAutoAvaliacao respostasAutoAvaliacao = null;
		if (isPortalPpg() || isPortalCoordenadorStricto())
			respostasAutoAvaliacao = dao.findByPrograma(getProgramaStricto().getId(), calendarioAutoAvaliacao.getId());
		else 
			respostasAutoAvaliacao = dao.findByCurso(getCursoAtualCoordenacao().getId(), calendarioAutoAvaliacao.getId());
		if (respostasAutoAvaliacao == null) {
			addMensagemErro("O question�rio de Auto Avalia��o ainda n�o foi preenchido.");
			return null;
		}
		obj = respostasAutoAvaliacao;
		QuestionarioRespostasMBean mBean = getMBean("questionarioRespostasBean");
		mBean.inicializar(obj.getCalendario().getQuestionario());
		mBean.popularVizualicacaoRespostas(respostasAutoAvaliacao.getRespostas());
		return forward("/stricto/auto_avaliacao/view_respostas.jsp");
	}
	
	/** Retorna para o usu�rio uma lista de calend�rios de auto avalia��o atualmente ativos e dentro do per�odo de preenchimento.
	 * <br /> M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/stricto/auto_avaliacao/lista_respostas.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException 
	 * @throws HibernateException 
	 */
	public String verRespostasPppg() throws ArqException {
		int id = getParameterInt("id");
		RespostasAutoAvaliacaoDao dao = getDAO(RespostasAutoAvaliacaoDao.class);
		RespostasAutoAvaliacao respostasAutoAvaliacao = dao.findByPrimaryKey(id, RespostasAutoAvaliacao.class);
		if (respostasAutoAvaliacao == null) {
			addMensagemErro("O question�rio de Auto Avalia��o ainda n�o foi preenchido.");
			return null;
		}
		obj = respostasAutoAvaliacao;
		QuestionarioRespostasMBean mBean = getMBean("questionarioRespostasBean");
		mBean.inicializar(obj.getCalendario().getQuestionario());
		mBean.popularVizualicacaoRespostas(respostasAutoAvaliacao.getRespostas());
		return forward("/stricto/auto_avaliacao/view_respostas.jsp");
	}
	
	/** Retorna para o usu�rio uma lista de calend�rios de auto avalia��o atualmente ativos e dentro do per�odo de preenchimento.
	 * <br /> M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/stricto/auto_avaliacao/lista_respostas.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException 
	 * @throws JRException 
	 * @throws IOException 
	 * @throws HibernateException 
	 */
	public String exportarRespostasPDF() throws ArqException, JRException, IOException {
		int id = getParameterInt("id");
		RespostasAutoAvaliacaoDao dao = getDAO(RespostasAutoAvaliacaoDao.class);
		RespostasAutoAvaliacao respostasAutoAvaliacao = dao.findByPrimaryKey(id, RespostasAutoAvaliacao.class);
		if (respostasAutoAvaliacao == null) {
			addMensagemErro("O question�rio de Auto Avalia��o ainda n�o foi preenchido.");
			return null;
		}
		if (respostasAutoAvaliacao.getMetasAcoesFormacaoRH() != null) respostasAutoAvaliacao.getMetasAcoesFormacaoRH().iterator();
		if (respostasAutoAvaliacao.getMetasAcoesFormacaoAcademica() != null) respostasAutoAvaliacao.getMetasAcoesFormacaoAcademica().iterator();
		List<RespostasAutoAvaliacao> dados = new LinkedList<RespostasAutoAvaliacao>();
		dados.add(respostasAutoAvaliacao);
		HashMap<String, Object> parametros = new HashMap<String, Object>();
		JRDataSource jrds = new JRBeanCollectionDataSource(dados);
		InputStream relatorio = JasperReportsUtil.getReportSIGAA("respostas_auto_avaliacao.jasper");
		JasperPrint prt = JasperFillManager.fillReport(relatorio, parametros, jrds);
		if (prt.getPages().size() == 0) {
			addMensagemErro("N�o h� dados para exportar");
			return null;
		}
		String nome = isEmpty(respostasAutoAvaliacao.getUnidade()) ? respostasAutoAvaliacao.getCurso().getNome() : respostasAutoAvaliacao.getUnidade().getNome(); 
		nome = nome.replace(",", " ").replace(":", " ");
		JasperReportsUtil.exportar(prt, nome + ".pdf", getCurrentRequest(), getCurrentResponse(), "pdf");
		FacesContext.getCurrentInstance().responseComplete();
		return null;
	}
	
	/** Inicia a an�lise de uma auto avalia��o.
	 * <br /> M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/stricto/auto_avaliacao/lista_respostas.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException 
	 * @throws HibernateException 
	 */
	public String analisarAutoAvaliacao() throws ArqException {
		checkRole(SigaaPapeis.ADMINISTRADOR_STRICTO,SigaaPapeis.GESTOR_LATO);
		int id = getParameterInt("id");
		RespostasAutoAvaliacaoDao dao = getDAO(RespostasAutoAvaliacaoDao.class);
		RespostasAutoAvaliacao respostasAutoAvaliacao = dao.findByPrimaryKey(id, RespostasAutoAvaliacao.class);
		if (respostasAutoAvaliacao == null) {
			addMensagemErro("O question�rio de Auto Avalia��o ainda n�o foi preenchido.");
			return null;
		}
		obj = respostasAutoAvaliacao;
		QuestionarioRespostasMBean mBean = getMBean("questionarioRespostasBean");
		mBean.inicializar(obj.getCalendario().getQuestionario());
		mBean.popularVizualicacaoRespostas(respostasAutoAvaliacao.getRespostas());
		if (hasErrors()) return null;
		parecerAutoAvaliacao = new ParecerAutoAvaliacao();
		return forward("/stricto/auto_avaliacao/analise_respostas.jsp");
	}
	
	/** Cadastra um parecer de uma auto avalia��o.
	 * <br /> M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/stricto/auto_avaliacao/analise_respostas.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException 
	 * @throws NegocioException 
	 * @throws HibernateException 
	 */
	public String emitirParecer() throws ArqException, NegocioException {
		if (parecerAutoAvaliacao.getSituacao() == obj.getSituacao()) {
			addMensagemErro("A situa��o indicada � a mesma da atualmente definida para a Auto Avalia��o. Por favor, informe outra situa��o para a Auto Avalia��o.");
			return null;
		}
		setOperacaoAtiva(SigaaListaComando.CADASTRAR_RESPOSTAS_AUTO_AVALIACAO.getId());
		prepareMovimento(SigaaListaComando.CADASTRAR_RESPOSTAS_AUTO_AVALIACAO);
		parecerAutoAvaliacao.setCadastradoEm(new Date());
		parecerAutoAvaliacao.setCadastradoPor(getRegistroEntrada());
		parecerAutoAvaliacao.setRespostasAutoAvaliacao(obj);
		obj.addParecerAutoAvaliacao(parecerAutoAvaliacao);
		obj.setSituacao(parecerAutoAvaliacao.getSituacao());
		MovimentoCadastro mov = new MovimentoCadastro();
		mov.setCodMovimento(getUltimoComando());
		mov.setObjMovimentado(obj);
		try {
			execute(mov);
			removeOperacaoAtiva();
		} catch (Exception e) {
			notifyError(e);
			addMensagemErroPadrao();
			e.printStackTrace();
		}
		if (parecerAutoAvaliacao.getSituacao() == SituacaoRespostasAutoAvaliacao.RETORNADO)
			notificaCoordenador();
		RespostasAutoAvaliacaoDao dao = getDAO(RespostasAutoAvaliacaoDao.class);
		resultadosBusca = dao.findByCalendarioAutoAvaliacao(calendarioAutoAvaliacao.getId());
		return formListaRespostas();
	}
	
	/**
	 * Notifica o coordenador que o parecer foi retornado para adequa��o 
	 * @throws DAOException 
	 */
	private void notificaCoordenador() throws DAOException {
		CoordenacaoCursoDao dao = getDAO(CoordenacaoCursoDao.class);
		CoordenacaoCurso coordenacao = dao.findUltimaByPrograma(obj.getUnidade());
		if (coordenacao != null && EmailValidator.getInstance().isValid(coordenacao.getServidor().getPessoa().getEmail())) {
			Pessoa p = coordenacao.getServidor().getPessoa();
			MailBody mail = new MailBody();
			mail.setContentType(MailBody.HTML);
			mail.setEmail( p.getEmail() );
			mail.setNome( p.getNome() );
			mail.setAssunto("Retorno de Formul�rio de Auto Avalia��o");
			String saudacao = "";
			if (p.getSexo() == Pessoa.SEXO_MASCULINO)
				saudacao = "Caro " + p.getNome() +", ";
			else 
				saudacao = "Cara " + p.getNome() +", ";
			mail.setMensagem("<html><body>"+saudacao +
					"<p>Sua Auto Avalia��o da P�s Gradua��o foi retornada pela Comiss�o" +
					( isEmpty(parecerAutoAvaliacao.getParecer()) ? " para corre��es." :  " com a seguinte oberva��o:") +
					"</p><p>" +
					parecerAutoAvaliacao.getParecer() +
					"</p>"+
					"Por favor, acesse o SIGAA para realizar as altera��es necess�rias e submeta novamente a sua Auto Avalia��o.</p>" +
					"</body></html>");
			//usu�rio que esta enviando e email para resposta.
			mail.setFromName("SIGAA - Pr� Reitoria de P�s Gradua��o");
			Mail.send(mail);
		}
	}

	/** Retorna para o usu�rio uma lista de calend�rios de auto avalia��o atualmente ativos e dentro do per�odo de preenchimento.
	 * <br /> M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/stricto/auto_avaliacao/lista.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException 
	 * @throws HibernateException 
	 */
	public String listarRespostas() throws ArqException {
		int id = getParameterInt("id");
		calendarioAutoAvaliacao = getGenericDAO().findByPrimaryKey(id, CalendarioAplicacaoAutoAvaliacao.class);
		RespostasAutoAvaliacaoDao dao = getDAO(RespostasAutoAvaliacaoDao.class);
		resultadosBusca = dao.findByCalendarioAutoAvaliacao(calendarioAutoAvaliacao.getId());
		if (isEmpty(resultadosBusca)) {
			addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
			return null;
		}
		return formListaRespostas();
	}
	
	/** Redireciona o usu�rio para a lista de resposta � um calend�rio de auto avalia��o.<br /> M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/stricto/auto_avaliacao/responder.jsp</li>
	 * </ul>
	 * @return
	 */
	public String formListaRespostas() {
		return forward("/stricto/auto_avaliacao/lista_respostas.jsp");
	}

	/** Adiciona uma {@link MetaAcaoFormacaoRH} � Auto Avalia��o
	 * <br /> M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/stricto/auto_avaliacao/reponder.jsp</li>
	 * </ul>
	 * @return
	 */
	public String adicionarMetaAcaoFormacaoRH() {
		obj.addMetaAcaoFormacaoRH(metaAcaoFormacaoRH);
		metaAcaoFormacaoRH = new MetaAcaoFormacaoRH();
		return null;
	}
	
	/** Remove uma {@link MetaAcaoFormacaoRH} � Auto Avalia��o
	 * <br /> M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/stricto/auto_avaliacao/responder.jsp</li>
	 * </ul>
	 * @return
	 */
	public String removerMetaAcaoFormacaoRH() {
		int hash = getParameterInt("item", 0);
		Iterator<MetaAcaoFormacaoRH> iterator = obj.getMetasAcoesFormacaoRH().iterator();
		while (iterator.hasNext()) {
			if (iterator.next().hashCode() == hash) {
				iterator.remove();
				break;
			}
		}
		return null;
	}
	
	/** Adiciona uma {@link MetaAcaoFormacaoProducaoAcademica} � Auto Avalia��o
	 * <br /> M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/stricto/auto_avaliacao/responder.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException 
	 */
	public String adicionarMetasAcoesFormacaoAcademica() throws DAOException {
		GenericDAO dao = getGenericDAO();
		if (isPortalCoordenadorStricto()) {
			metaAcaoFormacaoProducaoAcademica.getEquipePrograma().setId(idDocente);
			metaAcaoFormacaoProducaoAcademica.setCorpoDocente(null);
			dao.initialize(metaAcaoFormacaoProducaoAcademica.getEquipePrograma());
			for (MetaAcaoFormacaoProducaoAcademica meta : obj.getMetasAcoesFormacaoAcademica())
				if (meta.getEquipePrograma().getId() == idDocente) {
					addMensagemErro("O docente do programa j� foi adicionado. Por favor, selecione outro docente.");
				}
		} else {
			metaAcaoFormacaoProducaoAcademica.setEquipePrograma(null);
			metaAcaoFormacaoProducaoAcademica.getCorpoDocente().setId(idDocente);
			dao.initialize(metaAcaoFormacaoProducaoAcademica.getCorpoDocente());
			for (MetaAcaoFormacaoProducaoAcademica meta : obj.getMetasAcoesFormacaoAcademica())
				if (meta.getCorpoDocente().getId() == idDocente) {
					addMensagemErro("O docente do curso j� foi adicionado. Por favor, selecione outro docente.");
				}
			
		}
		erros.addAll(metaAcaoFormacaoProducaoAcademica.validate());
		if (hasErrors()) return null;
		obj.addMetaAcaoFormacaoProducaoAcademica(metaAcaoFormacaoProducaoAcademica);
		metaAcaoFormacaoProducaoAcademica = new MetaAcaoFormacaoProducaoAcademica();
		return null;
	}
	
	/** Remove uma {@link MetaAcaoFormacaoProducaoAcademica} � Auto Avalia��o
	 * <br /> M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/stricto/auto_avaliacao/responder.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException 
	 */
	public String removerMetasAcoesFormacaoAcademica() throws DAOException {
		int hash = getParameterInt("item", 0);
		Iterator<MetaAcaoFormacaoProducaoAcademica> iterator = obj.getMetasAcoesFormacaoAcademica().iterator();
		while (iterator.hasNext()) {
			if (iterator.next().hashCode() == hash) {
				iterator.remove();
				break;
			}
		}
		return null;
	}
	
	/** Cadastra as respostas da auto avalia��o
	 * <br /> M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/stricto/auto_avaliacao/responder.jsp</li>
	 * </ul>
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#cadastrar()
	 */
	@Override
	public String cadastrar() throws SegurancaException, ArqException, NegocioException {
		checkOperacaoAtiva(SigaaListaComando.CADASTRAR_RESPOSTAS_AUTO_AVALIACAO.getId());
		if (hasErrors()) return null;
		MovimentoCadastro mov = new MovimentoCadastro();
		mov.setCodMovimento(getUltimoComando());
		mov.setObjMovimentado(obj);
		try {
			execute(mov);
			removeOperacaoAtiva();
			resetBean();
		} catch (Exception e) {
			notifyError(e);
			addMensagemErroPadrao();
			e.printStackTrace();
		}
		return iniciarPreenchimento();
	}
	
	/** Cadastra as respostas da auto avalia��o e submete para avalia��o da PPPg.
	 * <br /> M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/stricto/auto_avaliacao/responder.jsp</li>
	 * </ul>
	 * @throws NegocioException 
	 * @throws ArqException 
	 * @throws SegurancaException 
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#cadastrar()
	 */
	public String submeter() throws SegurancaException, ArqException, NegocioException {
		submeterFormulario();
		erros.addAll(obj.getRespostas().validate());
		if (hasErrors()) return null;
		obj.setSituacao(SituacaoRespostasAutoAvaliacao.SUBMETIDO);
		String retorno = cadastrar();
		addMensagemInformation("Formul�rio submetido para avalia��o com sucesso!");
		return retorno;
	}
	
	/** Cadastra as respostas da auto avalia��o<br /> M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/stricto/auto_avaliacao/responder.jsp</li>
	 * </ul>
	 * @throws NegocioException 
	 * @throws ArqException 
	 * @throws SegurancaException 
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#cadastrar()
	 */
	public String salvar() throws SegurancaException, ArqException, NegocioException {
		submeterFormulario();
		obj.setSituacao(SituacaoRespostasAutoAvaliacao.SALVO);
		String retorno = cadastrar();
		addMensagemInformation("Formul�rio salvo com sucesso!");
		return retorno;
	}
	
	
	/**
	 * Prepara os dados para persistir as respostas.
	 */
	private void submeterFormulario() {
		QuestionarioRespostasMBean mBean = getMBean("questionarioRespostasBean");
		QuestionarioRespostas respostas = mBean.getObj();
		obj.setRespostas(respostas);
		if (!obj.getSugestoesMelhoriaPrograma().isPreenchido())
			obj.setSugestoesMelhoriaPrograma(null);
		Date hoje = new Date();
		respostas.setDataCadastro(hoje);
		if (obj.getId() == 0) {
			obj.setCadastradoEm(hoje);
		} else {
			obj.setAtualizadoEm(hoje);
		}
	}
	
	/** Redireciona o us�rio para a lista de calend�rios de auto avalia��o
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#getListPage()
	 */
	@Override
	public String getListPage() {
		if (isPortalPpg())
			return "/stricto/auto_avaliacao/lista.jsp";
		else
			return "/stricto/auto_avaliacao/lista_responder.jsp";
	}

	/** Retorna uma cole��o de SelectItem de situa��es de uma Auto Avalia��o
	 * @return
	 */
	public Collection<SelectItem> getSituacaoCombo() {
		Collection<SelectItem> lista = new LinkedList<SelectItem>();
		for (SituacaoRespostasAutoAvaliacao situacao : SituacaoRespostasAutoAvaliacao.values())
			lista.add(new SelectItem(situacao, situacao.toString()));
		return lista;
	}
	
	/** Retorna uma cole��o de SelectItem de situa��es utilizadas na an�lise de uma Auto Avalia��o
	 * @return
	 */
	public Collection<SelectItem> getSituacaoParecerCombo() {
		Collection<SelectItem> lista = new LinkedList<SelectItem>();
		SituacaoRespostasAutoAvaliacao situacoes[] = {SituacaoRespostasAutoAvaliacao.ACEITO,
				SituacaoRespostasAutoAvaliacao.REJEITADO, SituacaoRespostasAutoAvaliacao.RETORNADO};
		for (SituacaoRespostasAutoAvaliacao situacao : situacoes)
			lista.add(new SelectItem(situacao, situacao.toString()));
		return lista;
	}
	
	@Override
	public Collection<RespostasAutoAvaliacao> getAll() throws ArqException {
		if (all == null) {
			all = new LinkedList<RespostasAutoAvaliacao>();
			RespostasAutoAvaliacaoDao dao = getDAO(RespostasAutoAvaliacaoDao.class);
			CalendarioAplicacaoAutoAvaliacaoDao calDao = getDAO(CalendarioAplicacaoAutoAvaliacaoDao.class);
			Collection<RespostasAutoAvaliacao> respostas = null;
			int idPrograma = 0;
			int idCurso = 0;
			int tipoAutoAvaliacao = 0;
			if (isPortalCoordenadorStricto()) {
				respostas = dao.findByPrograma(getProgramaStricto().getId());
				tipoAutoAvaliacao = TipoQuestionario.AUTO_AVALIACAO_STRICTO_SENSU;
				idPrograma = getProgramaStricto().getId();
			} else if (isPortalCoordenadorLato()) {
				respostas = dao.findByCurso(getCursoAtualCoordenacao().getId());
				tipoAutoAvaliacao = TipoQuestionario.AUTO_AVALIACAO_LATO_SENSU;
				idCurso = getCursoAtualCoordenacao().getId();
			}
			if (!isEmpty(respostas)) all.addAll(respostas);
			// calend�rios n�o respondidos
			Collection<CalendarioAplicacaoAutoAvaliacao> calendarios = calDao.findAllOtimizado(tipoAutoAvaliacao, idPrograma, idCurso);
			if (!isEmpty(calendarios)) {
				for (CalendarioAplicacaoAutoAvaliacao cal : calendarios) {
					boolean existe = false;
					for (RespostasAutoAvaliacao resposta : all)
						if (resposta.getCalendario().getId() == cal.getId()) {
							existe = true;
							break;
						}
					if (!existe) {
						RespostasAutoAvaliacao resp = new RespostasAutoAvaliacao();
						resp.setCalendario(cal);
						all.add(resp);
					}
				}
			}
			// ordena por data
			Collections.sort((List<RespostasAutoAvaliacao>) all, new Comparator<RespostasAutoAvaliacao>() {
				@Override
				public int compare(RespostasAutoAvaliacao o1, RespostasAutoAvaliacao o2) {
					return (int) (o2.getCalendario().getDataInicio().getTime() - o1.getCalendario().getDataInicio().getTime());
				}
			});
		}
		return all;
	}
	
	public MetaAcaoFormacaoRH getMetaAcaoFormacaoRH() {
		return metaAcaoFormacaoRH;
	}

	public void setMetaAcaoFormacaoRH(MetaAcaoFormacaoRH metaAcaoFormacaoRH) {
		this.metaAcaoFormacaoRH = metaAcaoFormacaoRH;
	}

	public MetaAcaoFormacaoProducaoAcademica getMetaAcaoFormacaoProducaoAcademica() {
		return metaAcaoFormacaoProducaoAcademica;
	}

	public void setMetaAcaoFormacaoProducaoAcademica(
			MetaAcaoFormacaoProducaoAcademica metaAcaoFormacaoProducaoAcademica) {
		this.metaAcaoFormacaoProducaoAcademica = metaAcaoFormacaoProducaoAcademica;
	}

	public CalendarioAplicacaoAutoAvaliacao getCalendarioAutoAvaliacao() {
		return calendarioAutoAvaliacao;
	}

	public void setCalendarioAutoAvaliacao(
			CalendarioAplicacaoAutoAvaliacao calendarioAutoAvaliacao) {
		this.calendarioAutoAvaliacao = calendarioAutoAvaliacao;
	}

	public ParecerAutoAvaliacao getParecerAutoAvaliacao() {
		return parecerAutoAvaliacao;
	}

	public void setParecerAutoAvaliacao(ParecerAutoAvaliacao parecerAutoAvaliacao) {
		this.parecerAutoAvaliacao = parecerAutoAvaliacao;
	}

	public int getIdDocente() {
		return idDocente;
	}

	public void setIdDocente(int idDocente) {
		this.idDocente = idDocente;
	}

}
