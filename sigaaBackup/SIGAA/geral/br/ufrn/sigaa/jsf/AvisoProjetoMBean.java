/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 25/04/2007
 *
 */
package br.ufrn.sigaa.jsf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.email.Mail;
import br.ufrn.arq.email.MailBody;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.comum.dominio.notificacoes.Destinatario;
import br.ufrn.sigaa.arq.dao.DiscenteDao;
import br.ufrn.sigaa.arq.dao.monitoria.DiscenteMonitoriaDao;
import br.ufrn.sigaa.arq.dao.monitoria.EquipeDocenteDao;
import br.ufrn.sigaa.arq.dao.projetos.AvisoProjetoDao;
import br.ufrn.sigaa.arq.jsf.NotificacoesMBean;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.monitoria.dominio.DiscenteMonitoria;
import br.ufrn.sigaa.monitoria.dominio.EquipeDocente;
import br.ufrn.sigaa.monitoria.dominio.ProjetoEnsino;
import br.ufrn.sigaa.monitoria.jsf.ConstantesNavegacaoMonitoria;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.projetos.dominio.AvisoProjeto;
import br.ufrn.sigaa.projetos.dominio.Projeto;

/**
 * Responsável por enviar avisos aos participantes dos projeto de pesquisa, monitoria ou extensão.
 * 
 * @author ilueny
 *
 */
public class AvisoProjetoMBean extends SigaaAbstractController<AvisoProjeto> {
	
	/** Utilizado na visualização de todos os avisos do projeto. */
	private Collection<AvisoProjeto> avisosProjeto;
	
	/** Utilizado no cadastro de novos avisos para um determinado projeto.*/
	private int idProjeto;


	public AvisoProjetoMBean() {
		obj = new AvisoProjeto();
	}

	/** 
	 * Carrega o projeto e cria um aviso.
	 *  
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/monitoria/CadastrarAvisoProjeto/lista.jsp</li>
	 *	</ul>
	 * 
	 * @param id
	 * @throws SegurancaException 
	 */
	public void novoAviso() throws SegurancaException{

		if ((getAcessoMenu() != null) && (getAcessoMenu().isCoordenadorMonitoria())) {
				
				try {
					
					prepareMovimento(ArqListaComando.CADASTRAR);
					
					GenericDAO dao = getGenericDAO();
					Projeto proj = dao.findByPrimaryKey(idProjeto, Projeto.class);
					
					obj = new AvisoProjeto();
					obj.setProjeto(proj);
					obj.setDataCadastro(new Date());
					
					//A publicação do aviso nos portais foi substituída pelo envio de uma mensagem para todos os participantes ativos do projeto
					//data de validade e publicação são cadastrados por padrão
					obj.setDataValidade(new Date());					
					obj.setPublicar(false); //
					
					obj.setRegistroEntrada(getUsuarioLogado().getRegistroEntrada());
					
					setReadOnly(false);
					setConfirmButton("Cadastrar");
					
					prepareMovimento(ArqListaComando.CADASTRAR);			
					forward(ConstantesNavegacaoMonitoria.CADASTRARAVISOPROJETO_FORM);
		
				} catch (ArqException e) {
					notifyError(e);
				}
		}else
			throw new SegurancaException("Somente Coordenadores de Projetos ativos podem cadastrar avisos");
			
		
	}
	
	/**
	 * JSP: Não invocado por JSP.
	 */
	@Override
	public void beforeCadastrarAndValidate() throws NegocioException, SegurancaException, DAOException {
	    // Removendo comentários inseridos quando se copia e cola conteúdos de arquivos do Word.
	    obj.setDescricao(StringUtils.removerComentarios(obj.getDescricao()));
	    super.beforeCadastrarAndValidate();
	}

	
	/**
	 * Antes de realizar o cadastro do aviso, um e-mail é enviado para todos os
	 * docentes e discente ativos do projeto.
	 * <br>
	 * JSP: Não invocado por JSP.
	 * 
	 */
	@Override
	protected void afterCadastrar() throws ArqException {

		StringBuffer msg = new StringBuffer();
			msg.append("<b>TÍTULO DO PROJETO:</b> " +  obj.getProjeto().getTitulo());			
			msg.append("<br/><b>AVISO:</b> " + obj.getTitulo());			
			msg.append("<br/><b>CONTEÚDO:</b>" + obj.getDescricao() + "\n\n");
			msg.append("<br/><br/><b>AVISO CADASTRADO POR:</b> " + getUsuarioLogado().getNome());
			
			msg.append("<br/><br/><i>Esta mensagem foi gerada automaticamente pelo SIGAA, após o cadastro do aviso. Não é necessário respondê-la.</i>");
			msg.append("<br/>-----");
			msg.append("<br/>SIGAA - Sistema Integrado de Gestão de Atividades Acadêmicas");
			
		
		ProjetoEnsino projeto = getGenericDAO().findByExactField(ProjetoEnsino.class, "projeto.id", obj.getProjeto().getId(), true);	
			
		//enviando e-mail para todos membros ativos do projeto
			
		//********************   DISCENTES  **************************	
		DiscenteMonitoriaDao discenteDao = getDAO(DiscenteMonitoriaDao.class);			
		Collection<DiscenteMonitoria> discentes = discenteDao.findAtivosByProjeto(projeto.getId(), null, null);
		for (DiscenteMonitoria discente : discentes) {
			if (discente.getDiscente().getPessoa().getEmail() != null){

				MailBody email = new MailBody();
				email.setAssunto("[SIGAA] Aviso importante sobre o seu Projeto de Monitoria");
				email.setContentType(MailBody.HTML);
				
				email.setNome(discente.getDiscente().getNome());
				email.setEmail(discente.getDiscente().getPessoa().getEmail());
				
				email.setMensagem(msg.toString());
				Mail.send(email);
				
			}
		}		
		
		//************************** DOCENTES **********************
		EquipeDocenteDao docDao = getDAO(EquipeDocenteDao.class);	
		Collection<EquipeDocente> docentes =  docDao.findByProjeto(projeto.getId(), true);
		for (EquipeDocente doc : docentes) {
			if (doc.getServidor().getPessoa().getEmail() != null){
				
				MailBody email = new MailBody();
				email.setAssunto("[SIGAA] Aviso importante sobre o seu Projeto de Monitoria");
				email.setContentType(MailBody.HTML);

				email.setNome(doc.getServidor().getNome());
				email.setEmail(doc.getServidor().getPessoa().getEmail());
				
				email.setMensagem(msg.toString());
				Mail.send(email);
			}
		}		
	}
	
	/**
	 * JSP: Não invocado por JSP.
	 */
	@Override
	public String forwardCadastrar() {
		return getListPage();
	}
	
	
	/**
	 * Lista todos os avisos do projeto de ensino.
	 * 
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/monitoria/CadastrarAvisoProjeto/lista_projetos.jsp</li>
	 *	</ul>
	 * 
	 * @throws SegurancaException
	 */
	public void verAvisosDoProjeto() throws SegurancaException{		
		
		if ((getAcessoMenu() != null) && (getAcessoMenu().isCoordenadorMonitoria())) {
			try {
				
				int id = getParameterInt("id");
				
				AvisoProjetoDao dao = getDAO(AvisoProjetoDao.class);
				avisosProjeto = new ArrayList<AvisoProjeto>();
				avisosProjeto = dao.findByProjeto(id);
				
				Projeto proj = dao.findByPrimaryKey(id, Projeto.class);
				
				obj = new AvisoProjeto();
				obj.setProjeto(proj);
				
				forward(ConstantesNavegacaoMonitoria.CADASTRARAVISOPROJETO_LISTA);
				
				
			} catch (ArqException e) {
				notifyError(e);
			}
		}else
			throw new SegurancaException("Somente Coordenadores de Projetos ativos podem cadastrar avisos");

	}
	
	/**
	 * Volta para a lista de avisos do projeto selecionado.
	 * 
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/monitoria/CadastrarAvisoProjeto/mostrar.jsp</li>
	 *	</ul>
	 * 
	 * @throws SegurancaException
	 */
	public void voltarListaAviso() throws SegurancaException{			
		forward(ConstantesNavegacaoMonitoria.CADASTRARAVISOPROJETO_LISTA);
	}
	
	/**
	 * Mostra as infprmações do aviso do projeto de ensino.
	 * 
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/monitoria/CadastrarAvisoProjeto/lista.jsp</li>
	 *	</ul>
	 * 
	 * @throws SegurancaException
	 */
	public void visualizarAviso() throws SegurancaException{		
		
		if ((getAcessoMenu() != null) && (getAcessoMenu().isCoordenadorMonitoria())) {
			try {
				
				int id = getParameterInt("id");
				
				AvisoProjetoDao dao = getDAO(AvisoProjetoDao.class);
				obj = dao.findByPrimaryKey(id,AvisoProjeto.class);
				forward(ConstantesNavegacaoMonitoria.MOSTRAR_AVISOPROJETO);
				
				
			} catch (ArqException e) {
				notifyError(e);
			}
		}else
			throw new SegurancaException("Somente Coordenadores de Projetos ativos podem cadastrar avisos");

		
	}
	
	/**
	 * Invoca o MBean responsável por enviar email/mensagem para um usuário.
	 * 
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>sigaa.war/WEB-INF/jsp/monitoria/AtividadeMonitor/lista_avaliar_atividade.jsp</li>
	 * 	<li>sigaa.war/WEB-INF/jsp/extensao/PlanoTrabalho/planos_coordenador.jsp.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws NumberFormatException
	 * @throws ArqException
	 */
	public String enviarMensagem() throws NumberFormatException, ArqException{
		
		Integer idDiscente = getParameterInt("idDiscente", 0);
				
		String nome = null;
		String email = null;
		int idUsuario = 0;
		
		Discente monitor = getGenericDAO().findByPrimaryKey(idDiscente, Discente.class);
		Usuario usuario = getDAO(DiscenteDao.class).findByUsuario(monitor.getPessoa().getId());
		String remetente =  getUsuarioLogado().getNome();
		
		nome = monitor.getPessoa().getNome();
		email = monitor.getPessoa().getEmail();
		idUsuario = usuario.getId() ;
		
		NotificacoesMBean notificacao = getMBean("notificacoes");
		
		ArrayList<Destinatario> destinatarios = new ArrayList<Destinatario>();	
			
		Destinatario destinatario = new Destinatario(nome, email);
		destinatario.setIdusuario(idUsuario);			
		destinatarios.add(destinatario);
		
		notificacao.setDestinatarios(destinatarios);
		
		notificacao.setRemetente( remetente );
		
		notificacao.setTitulo("Enviar mensagem ao Discente");
		
		notificacao.iniciar();
		
		return null;
	}

	public Collection<AvisoProjeto> getAvisosProjeto() {
		return avisosProjeto;
	}

	public void setAvisosProjeto(Collection<AvisoProjeto> avisosProjeto) {
		this.avisosProjeto = avisosProjeto;
	}

	
	@Override
	public String getListPage() {
		return ConstantesNavegacaoMonitoria.CADASTRARAVISOPROJETO_LISTA_PROJETOS;
	}
	
	@Override
	public String getDirBase() {
		return "/monitoria/CadastrarAvisoProjeto";
	}
	
	
	public int getIdProjeto() {
		return idProjeto;
	}

	public void setIdProjeto(int idProjeto) {
		this.idProjeto = idProjeto;
	}

}