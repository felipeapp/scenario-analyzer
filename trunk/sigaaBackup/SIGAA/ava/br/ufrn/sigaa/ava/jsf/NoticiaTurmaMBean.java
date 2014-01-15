/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas
 * Superintendência de Informática - UFRN
 */
package br.ufrn.sigaa.ava.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.ava.dao.TurmaVirtualDao;
import br.ufrn.sigaa.ava.dominio.NoticiaTurma;
import br.ufrn.sigaa.ava.validacao.Notification;
import br.ufrn.sigaa.ava.validacao.Specification;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.negocio.OperacaoTurmaValidator;

/**
 * Managed bean para cadastro de notícias na turma
 * virtual.
 * 
 * @author David Pereira
 */
@Component("noticiaTurma") @Scope("request")
public class NoticiaTurmaMBean extends CadastroTurmaVirtual<NoticiaTurma> {
	
	/** Tipos de titulo de email */
	/** Tipo indicando que o titulo e o padrao*/
	public static final char TITULO_EMAIL_COMUN = 'C';
	
	/**Tipo indicando que o titulo do email e personalizado(titulo da noticia)*/
	public static final char TITULO_EMAIL_PERSONALIZADO = 'P';
	
	/** Se deve ser enviado um e-mail para os alunos ao cadastrar uma nova notícia. */
	private boolean notificar;
	
	/** Indica se o titulo da notificacao sera o padrao ou titulo da noticia cadastrada pelo docente. */
	private char tipoTituloNotificacao;
	
	/** Lista de notícias */
	private List <NoticiaTurma> lista;
	
	/**
	 * Construtor Padrão, que inicializa o object.
	 */
	public NoticiaTurmaMBean (){
		object = new NoticiaTurma();
		List <String> cadastrarEm = new ArrayList <String> ();
		cadastrarEm.add(""+turma().getId());
		setCadastrarEm(cadastrarEm);
		notificar = true;
		tipoTituloNotificacao = TITULO_EMAIL_COMUN;
	}
	
	/**
	 * Retorna a lista de notícias da turma.<br /><br/>
	 * 
	 * Chamado pelo método "getListagem()" do bean CadastroTurmaVirtual.<br/>
	 * Não invocado por JSPs.
	 * 
	 */
	@Override
	public List<NoticiaTurma> lista() {
		
		if (lista == null){
			TurmaVirtualDao dao = null;
			
			try {
				dao = getDAO(TurmaVirtualDao.class);
				lista = dao.findNoticiasByTurma(turma());
			} finally {
				if (dao != null)
					dao.close();
			}
		}
		
		return lista;
	}

	public boolean isNotificar() {
		return notificar;
	}

	public void setNotificar(boolean notificar) {
		object.setNotificar(notificar);
		this.notificar = notificar;
	}
	
	/**
	 * Retorna uma especificação para a turma.
	 * Não é usado em jsps.
	 */
	@Override
	public Specification getEspecificacaoCadastro() {
		return new Specification() {
			Notification notification = new Notification();
			
			public Notification getNotification() {
				return notification;
			}

			public boolean isSatisfiedBy(Object objeto) {
				NoticiaTurma noticia = (NoticiaTurma) objeto;
				if (isEmpty(noticia.getDescricao()))
					notification.addError("Título: Campo obrigatório não informado.");
				if (isEmpty(noticia.getNoticia()))
					notification.addError("Texto: Campo obrigatório não informado.");
				return !notification.hasMessages();
			}
		};
	}
	
	/**
	 * Envia um email para os alunos da turma, notificando-os de que
	 * uma nova notícia foi cadastrada nela.
	 */
	@Override
	protected void aposPersistir() {
		
		TurmaVirtualMBean tBean = getMBean("turmaVirtual");
		try {
			tBean.carregarNoticias();
		} catch (DAOException e) {
			tratamentoErroPadrao(e);
		}
		
		if (isNotificar()) { // Envia e-mail para os alunos
			try {
				
				List <Turma> ts = tBean.getTurmasSemestre();
				
				if (isUserInRole(SigaaPapeis.GESTOR_TECNICO)) {
					if (!ts.contains(tBean.getTurma()))
						ts.add(tBean.getTurma());
				}
				
				// Para todas as turmas selecionadas, adiciona seu nome ao assunto do email.
				String nomeTurmas = "";
				for (Turma t : ts)
					if (cadastrarEm.contains(""+t.getId()))
						nomeTurmas += (nomeTurmas.equals("") ? "" : ", ") + t.getDescricaoSemDocente();
				
				String assunto;
				if (tipoTituloNotificacao == TITULO_EMAIL_COMUN)
					assunto = "Nova notícia na(s) turma(s) " + nomeTurmas;
				else
					assunto = object.getDescricao();
				
				String descricao = "<p>" + object.getDescricao() + "</p>" + object.getNoticia();
				String texto = "Uma notícia foi cadastrada na turma virtual: " + nomeTurmas + descricao;
				notificarTurmas(cadastrarEm, assunto, texto, ControllerTurmaVirtual.DISCENTE, ControllerTurmaVirtual.DOCENTE, ControllerTurmaVirtual.AUTORIZADO , ControllerTurmaVirtual.DOCENCIA_ASSISTIDA);
				
			} catch (DAOException e){
				tratamentoErroPadrao(e);
			}
		}
	}
	
	/**
	 * Recarrega as notícias da turma virtual. 
	 */
	@Override
	protected void aposRemocao() {
		TurmaVirtualMBean tBean = getMBean("turmaVirtual");
		try {
			tBean.carregarNoticias();
		} catch (DAOException e) {
			tratamentoErroPadrao(e);
		}
	}
	
	/**
	 * Indica ao ControllerTurmaVirtual que é para cadastrar em todas as turmas selecionadas.
	 */
	@Override
	protected boolean cadastrarEmVariasTurmas() {
		return true;
	}
	
	public char getTipoTituloNotificacao() {
		return tipoTituloNotificacao;
	}

	public void setTipoTituloNotificacao(char tipoTituloNotificacao) {
		this.tipoTituloNotificacao = tipoTituloNotificacao;
	}
	
	/**
	 * Verifica se o usuário logado pode cadastrar uma notícia para a turma trabalhada.
	 * <br />
	 * Método invocado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/ava/NoticiaTurma/listar.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public boolean isPermiteCadastrarNoticia() {
		TurmaVirtualMBean tv = getMBean("turmaVirtual");
		PermissaoAvaMBean perm = getMBean("permissaoAva");
		
		return tv.isDocente() 
				|| perm.getPermissaoUsuario() != null && perm.getPermissaoUsuario().isDocente()
				|| OperacaoTurmaValidator.verificarNivelTecnico(turma());
	}
	
}
