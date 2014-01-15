/*
 * Universidade Federal do Rio Grande do Norte

 * Superintend�ncia de Inform�tica
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
 * MBean respons�vel por controlar opera��es ligadas as not�cias das
 * turmas publicadas por docentes. 
 * 
 * @author Ilueny Santos
 *
 */
@Component("noticiaTurmaTouch")
@Scope("request")
public class NoticiaTurmaTouchMBean extends TurmaVirtualTouchMBean<NoticiaTurma> {
	
	/** Lista contendo as �ltimas 5 not�cias da turma virtual. */
	private List <NoticiaTurma> noticias = null;
	
	/** Bean auxiliar para gest�o de not�cias. */
	private TurmaVirtualTouchMBean<NoticiaTurma> portalTurmaBean;
	
	/** Representa a �ltima not�cia cadastrada.*/
	private NoticiaTurma ultimaNoticia;
	
	/** Se deve ser enviado um e-mail para os alunos ao cadastrar uma nova not�cia. */
	private boolean notificar;

	
	/** Construtor padr�o. */
	public NoticiaTurmaTouchMBean() {
		obj = new NoticiaTurma();
	}
	
	/**
	 * Carrega as �ltimas 5 not�cias cadastradas para a turma atual. 
	 * M�todo n�o invocado por JSP�s
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
	 * Carrega not�cias e redireciona para visualiza��o.
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
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
	 * Carrega not�cias e redireciona para visualiza��o.
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
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
			addMensagemErro("Erro ao listar not�cias.");
		}			
		return null;
	}


	/**
	 * Carrega not�cias e redireciona para visualiza��o.
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
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
			addMensagemErro("Erro ao listar not�cias.");
		}			
		return null;
	}

	/** 
	 * Inicia o cadastro de novas not�cias.
	 *  
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
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
			addMensagemErro("Somente docentes podem cadastrar not�cias.");
		}		
		return null;
	}
	
	
	/**
	 * Retorna uma especifica��o para a not�cia que ser� cadastrada.
	 * N�o � usado em jsps.
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
					notification.addError("O t�tulo � obrigat�rio");
				if (isEmpty(noticia.getNoticia()))
					notification.addError("O texto � obrigat�rio");
				if (isEmpty(noticia.getTurma()))
					notification.addError("Turma campo � obrigat�rio");				
				return !notification.hasMessages();
			}
		};
	}

	
	/** 
	 * Cadastra nova not�cias.
	 *  
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *    <li>sigaa.war/mobile/touch/ava/noticia/form.jsp</li>
	 * </ul>
	 * @throws ArqException 
	 */
	public String cadastrarNoticia() throws ArqException {
		portalTurmaBean = getMBean("portalDocenteTouch");
		
		if (portalTurmaBean.isDocenteLogado()) {
			if (StringUtils.isEmpty(obj.getDescricao()) || StringUtils.isEmpty(obj.getNoticia())){
				addMensagemErro ("Por favor, preencha todos os campos do formul�rio.");
				return forward("/mobile/touch/ava/noticia/form.jsf");
			} else {
				if (obj.getDescricao().length() > 200){
					addMensagemErro ("O t�tulo da not�cia ultrapassou o limite m�ximo de caracteres. Por favor, " +
							"digite um t�tulo menor.");
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
					
						//Registra inser��o.
						registrarAcao(obj.getDescricao(), EntidadeRegistroAva.NOTICIA, AcaoAva.INSERIR, obj.getId());
						notificarParticipantes();
					} catch (NegocioException e) {
						addMensagens(e.getListaMensagens());
					}
				}
			}
		} else {
			addMensagemErro("Somente docentes podem cadastrar not�cias.");
		}
			
		return listarNoticiasDocente();
	}
	
	
	/**
	 * Envia um email para os discente e docentes da turma, notificando-os de que
	 * uma nova not�cia foi cadastrada nela.
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
				
				String assunto = "Nova not�cia cadastrada para turma virtual: " + getObj().getTurma().getDescricaoSemDocente();				
				String descricao = "<p>" + getObj().getDescricao() + "</p>" + getObj().getNoticia();
				String texto = "Uma not�cia foi cadastrada na turma virtual: " + getObj().getTurma().getDescricaoSemDocente() + descricao;
				
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
	 * Carrega a �ltima not�cia da turma.
	 *  
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
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
			addMensagemErro("Erro ao carregar �ltima not�cia.");
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
