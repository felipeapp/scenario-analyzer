/*
 * Universidade Federal do Rio Grande do Norte

 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '11/11/2011'
 *
 */
package br.ufrn.sigaa.mobile.touch.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ava.dao.TurmaVirtualDao;
import br.ufrn.sigaa.ava.dominio.AcaoAva;
import br.ufrn.sigaa.ava.dominio.EntidadeRegistroAva;
import br.ufrn.sigaa.ava.dominio.NoticiaTurma;
import br.ufrn.sigaa.ava.jsf.ControllerTurmaVirtual;
import br.ufrn.sigaa.ava.jsf.TurmaVirtualMBean;
import br.ufrn.sigaa.ava.negocio.MovimentoCadastroAva;
import br.ufrn.sigaa.ava.validacao.Notification;
import br.ufrn.sigaa.ava.validacao.Specification;
import br.ufrn.sigaa.ensino.dominio.Turma;

/**
 * MBean responsável por controlar operações ligadas as notícias das
 * turmas publicadas por docentes. 
 * 
 * @author Ilueny Santos
 *
 */
@Component("noticiaTurmaTouch")
@Scope("request")
public class NoticiaTurmaTouchMBean extends TurmaVirtualTouchMBean<NoticiaTurma> {
	
	/** Lista contendo as últimas 5 notícias da turma virtual. */
	private List <NoticiaTurma> noticias = null;
	
	/** Bean auxiliar para gestão de notícias. */
	private TurmaVirtualTouchMBean<NoticiaTurma> portalTurmaBean;
	
	/** Representa a última notícia cadastrada.*/
	private NoticiaTurma ultimaNoticia;
	
	/** Se deve ser enviado um e-mail para os alunos ao cadastrar uma nova notícia. */
	private boolean notificar;

	
	/** Construtor padrão. */
	public NoticiaTurmaTouchMBean() {
		obj = new NoticiaTurma();
	}
	
	/**
	 * Carrega as últimas 5 notícias cadastradas para a turma atual. 
	 * Método não invocado por JSP´s
	 * 
	 * @throws DAOException 
	 */
	private void carregarNoticias() throws DAOException {
		noticias = new ArrayList<NoticiaTurma>();
		if (getTurma() != null) {
			noticias = getDAO(TurmaVirtualDao.class).findUltimasNoticiasByTurma(getTurma());
		}
	}
	

	/**
	 * Carrega notícias e redireciona para visualização.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *    <li>sigaa.war/mobile/touch/ava/noticia/form.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException 
	 */
	public String listarNoticias() throws ArqException {
		if (portalTurmaBean.isDiscenteLogado()) {
			return listarNoticiasDiscente();
		}
		if (portalTurmaBean.isDocenteLogado()) {
			return listarNoticiasDocente();
		}
		return null;
	}

	
	/**
	 * Carrega notícias e redireciona para visualização.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *    <li>sigaa.war/mobile/touch/ava/topico_aula_discente.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException 
	 */
	public String listarNoticiasDiscente() throws ArqException {
		portalTurmaBean = getMBean("portalDiscenteTouch");
		if (portalTurmaBean.isDiscenteLogado()) {
			setTurma(portalTurmaBean.getTurma());
			
			carregarNoticias();
			return forward("/mobile/touch/ava/noticia/lista_discente.jsf");
		} else {
			addMensagemErro("Erro ao listar notícias.");
		}			
		return null;
	}


	/**
	 * Carrega notícias e redireciona para visualização.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *    <li>sigaa.war/mobile/touch/ava/topico_aula_docente.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException 
	 */
	public String listarNoticiasDocente() throws ArqException {
		portalTurmaBean = getMBean("portalDocenteTouch");
		if (portalTurmaBean.isDocenteLogado()) {
			setTurma(portalTurmaBean.getTurma());
			
			carregarNoticias();
			return forward("/mobile/touch/ava/noticia/lista_docente.jsf");
		} else {
			addMensagemErro("Erro ao listar notícias.");
		}			
		return null;
	}

	/** 
	 * Inicia o cadastro de novas notícias.
	 *  
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *    <li>sigaa.war/mobile/touch/ava/noticia/lista_docente.jsp</li>
	 * </ul>
	 * @throws ArqException 
	 */
	public String iniciarCadastroNoticia() throws ArqException {
		portalTurmaBean = getMBean("portalDocenteTouch");
		if (portalTurmaBean.isDocenteLogado()) {
			setTurma(portalTurmaBean.getTurma());
			
			obj = new NoticiaTurma();
			obj.setTurma(getTurma());
			obj.setData(new Date());
			obj.setUsuarioCadastro(getUsuarioLogado());
			
			prepareMovimento(SigaaListaComando.CADASTRAR_AVA);		
			return forward("/mobile/touch/ava/noticia/form.jsf");
		}else {
			addMensagemErro("Somente docentes podem cadastrar notícias.");
		}		
		return null;
	}
	
	
	/**
	 * Retorna uma especificação para a notícia que será cadastrada.
	 * Não é usado em jsps.
	 */
	private Specification getEspecificacaoCadastro() {
		return new Specification() {
			Notification notification = new Notification();

			public Notification getNotification() {
				return notification;
			}

			public boolean isSatisfiedBy(Object objeto) {
				NoticiaTurma noticia = (NoticiaTurma) objeto;
				if (isEmpty(noticia.getDescricao()))
					notification.addError("O título é obrigatório");
				if (isEmpty(noticia.getNoticia()))
					notification.addError("O texto é obrigatório");
				if (isEmpty(noticia.getTurma()))
					notification.addError("Turma campo é obrigatório");				
				return !notification.hasMessages();
			}
		};
	}

	
	/** 
	 * Cadastra nova notícias.
	 *  
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *    <li>sigaa.war/mobile/touch/ava/noticia/form.jsp</li>
	 * </ul>
	 * @throws ArqException 
	 */
	public String cadastrarNoticia() throws ArqException {
		portalTurmaBean = getMBean("portalDocenteTouch");
		
		if (portalTurmaBean.isDocenteLogado()) {
			if (StringUtils.isEmpty(obj.getDescricao()) || StringUtils.isEmpty(obj.getNoticia())){
				addMensagemErro ("Por favor, preencha todos os campos do formulário.");
				return forward("/mobile/touch/ava/noticia/form.jsf");
			} else {
				if (obj.getDescricao().length() > 200){
					addMensagemErro ("O título da notícia ultrapassou o limite máximo de caracteres. Por favor, " +
							"digite um título menor.");
					return forward("/mobile/touch/ava/noticia/form.jsf");
				} else {
					try {
						// Registra a tentativa de cadastrar algum objeto.
						registrarAcao(obj.getDescricao(), EntidadeRegistroAva.NOTICIA, AcaoAva.INICIAR_INSERCAO, null);
		
						MovimentoCadastroAva mov = new MovimentoCadastroAva();
						mov.setCodMovimento(SigaaListaComando.CADASTRAR_AVA);
						mov.setObjMovimentado(obj);
						mov.setTurma(obj.getTurma());
						mov.setSpecification(getEspecificacaoCadastro());
						mov.setMensagem(obj.getMensagemAtividade());
						mov.setCadastrarEm(null);
						mov.setCadastrarEmVariasTurmas(false);
					
						execute(mov, getCurrentRequest());
					
						//Registra inserção.
						registrarAcao(obj.getDescricao(), EntidadeRegistroAva.NOTICIA, AcaoAva.INSERIR, obj.getId());
						notificarParticipantes();
					} catch (NegocioException e) {
						addMensagens(e.getListaMensagens());
					}
				}
			}
		} else {
			addMensagemErro("Somente docentes podem cadastrar notícias.");
		}
			
		return listarNoticiasDocente();
	}
	
	
	/**
	 * Envia um email para os discente e docentes da turma, notificando-os de que
	 * uma nova notícia foi cadastrada nela.
	 */
	protected void notificarParticipantes() {
		
		if (isNotificar()) { // Envia e-mail para os envolvidos
			try {
				
				TurmaVirtualMBean tBean = getMBean("turmaVirtual");
				tBean.setTurma(getObj().getTurma());
				List <Turma> ts = tBean.getTurmasSemestre();
				
				if (isUserInRole(SigaaPapeis.GESTOR_TECNICO)) {
					if (!ts.contains(tBean.getTurma()))
						ts.add(tBean.getTurma());
				}
				
				String assunto = "Nova notícia cadastrada para turma virtual: " + getObj().getTurma().getDescricaoSemDocente();				
				String descricao = "<p>" + getObj().getDescricao() + "</p>" + getObj().getNoticia();
				String texto = "Uma notícia foi cadastrada na turma virtual: " + getObj().getTurma().getDescricaoSemDocente() + descricao;
				
				List<String> idsTurmas = new ArrayList<String>();
				idsTurmas.add(String.valueOf(getObj().getTurma().getId()));
				tBean.notificarTurmas(idsTurmas, assunto, texto, ControllerTurmaVirtual.DISCENTE, ControllerTurmaVirtual.DOCENTE, ControllerTurmaVirtual.AUTORIZADO , ControllerTurmaVirtual.DOCENCIA_ASSISTIDA);
				
			} catch (DAOException e){
				tratamentoErroPadrao(e);
			}
		}
	}


	public List<NoticiaTurma> getNoticias() {
		return noticias;
	}

	public void setNoticias(List<NoticiaTurma> noticias) {
		this.noticias = noticias;
	}

	/** 
	 * Carrega a última notícia da turma.
	 *  
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *    <li>sigaa.war/mobile/touch/ava/topico_aula_discente.jsp</li>
	 * </ul>
	 */
	public NoticiaTurma getUltimaNoticia() {
		try {
			carregarNoticias();
			if (ValidatorUtil.isNotEmpty(noticias)) {
				ultimaNoticia = noticias.iterator().next();
			}
			return ultimaNoticia;
		} catch (DAOException e) {
			addMensagemErro("Erro ao carregar última notícia.");
		}
		return null;
	}

	public void setUltimaNoticia(NoticiaTurma ultimaNoticia) {
		this.ultimaNoticia = ultimaNoticia;
	}

	public boolean isNotificar() {
		return notificar;
	}

	public void setNotificar(boolean notificar) {
		this.notificar = notificar;
	}

}
