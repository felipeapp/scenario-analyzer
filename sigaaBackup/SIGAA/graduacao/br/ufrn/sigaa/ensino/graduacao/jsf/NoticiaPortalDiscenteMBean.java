/* 
 * Superintend�ncia de Inform�tica - Diretoria de Sistemas
 * Projeto: SIGAA
 *
 */
package br.ufrn.sigaa.ensino.graduacao.jsf;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.myfaces.custom.fileupload.UploadedFile;
import org.hibernate.HibernateException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.arquivos.EnvioArquivoHelper;
import br.ufrn.arq.dao.DAOFactory;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.email.Mail;
import br.ufrn.arq.email.MailBody;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.comum.dominio.NoticiaPortal;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.sigaa.arq.dao.CursoDao;
import br.ufrn.sigaa.arq.dao.DiscenteDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ensino.graduacao.negocio.MovimentoNoticiaPortalDiscente;
import br.ufrn.sigaa.portal.dao.NoticiaPortalAcademicoDAO;

/**
 * Insere uma Not�cia no portal do discente de gradua��o e stricto, atrav�s do coordenadores de curso e programa
 * Quando um coordenador cadastra a Not�cia, somente os alunos do curso (e programa) visualizam. 
 * Portanto a visualiza��o � restrita, n�o � para todos os discentes.
 * 
 * @author Henrique Andr�
 */

@Component("noticiaPortalDiscente") @Scope("session")
public class NoticiaPortalDiscenteMBean extends SigaaAbstractController<NoticiaPortal> {

	/** Arquivo de upload que pode ser associado a not�cia. */
	private UploadedFile arquivo;	
	/** Curso em que a not�cia ser� inserida. */
	private Curso curso;
	/** Programa de P�s que o usu�rio coordena. */
	private Unidade programa;
	/** Usu�rios do programa que foi cadastrada a nova not�cia. */
	private List<Map<String,Object>> programaPortal = new ArrayList<Map<String,Object>>();
	/** Resulado da busca das not�cias. */
	private Collection<NoticiaPortal> resultadoBusca;
	/** Link para o formul�rio onde o coordenador cadastra a not�cia do curso. */
	public String telaNoticiaNova = "/graduacao/portal_noticias/form.jsp";
	/** Link para a p�gina que exibe a lista de not�cias do curso. */
	public String telaListarNoticias = "/graduacao/portal_noticias/lista.jsp";
	/** Link para a p�gina que exibe os dados de uma not�cia do curso. */
	public String telaVerNoticia = "/graduacao/portal_noticias/view.jsp";
	/** Se os discentes devem ser notificados por e-mail sobre o cadastro da not�cia. */
	boolean notificar;

	/**
	 * Inicia o cadastro de uma Not�cia, inicializa objetos e redireciona pro form
	 * 
	 * JSP: /SIGAA/app/sigaa.ear/sigaa.war/graduacao/portal_noticias/form.jsp
	 * @return
	 * @throws ArqException
	 */
	public String iniciarNovaNoticia() throws ArqException {

		checkRole(SigaaPapeis.COORDENADOR_CURSO, SigaaPapeis.SECRETARIA_COORDENACAO,
				SigaaPapeis.COORDENADOR_CURSO_STRICTO, SigaaPapeis.SECRETARIA_POS);

		prepareMovimento(SigaaListaComando.ADICIONAR_NOTICIA_PORTAL_DISCENTE);
		
		obj = new NoticiaPortal();
		obj.setPortalDiscente(true);
		
		if (getSubSistema().equals(SigaaSubsistemas.PORTAL_COORDENADOR))
			iniciarGraduacao();
		else if (getSubSistema().equals(SigaaSubsistemas.PORTAL_COORDENADOR_STRICTO))
			iniciarStricto();
			
		setConfirmButton("Cadastrar");
		return forward(telaNoticiaNova);
	}

	/**
	 * Configura para gradua��o
	 * 
	 * @throws SegurancaException
	 * @throws ArqException
	 */
	private void iniciarGraduacao() throws SegurancaException, ArqException {
		obj.setIdCurso(getCursoAtualCoordenacao().getId());
		curso = getCursoAtualCoordenacao();
	}

	/**
	 * Configura para Stricto
	 * 
	 * @throws SegurancaException
	 * @throws ArqException
	 */
	private void iniciarStricto() throws SegurancaException, ArqException {
		obj.setIdPrograma(getProgramaStricto().getId());
		programa = getProgramaStricto();
	}	
	
	/**
	 * Carrega uma Not�cia e redireciona para form iniciando o processo de altera��o
	 * 
	 * JSP: /SIGAA/app/sigaa.ear/sigaa.war/graduacao/portal_noticias/lista.jsp
	 * @return
	 * @throws ArqException
	 */
	public String iniciarAlterarNoticia() throws ArqException {
		checkRole(SigaaPapeis.COORDENADOR_CURSO, SigaaPapeis.SECRETARIA_COORDENACAO,
				SigaaPapeis.COORDENADOR_CURSO_STRICTO, SigaaPapeis.SECRETARIA_POS);

		
		prepareMovimento(SigaaListaComando.ALTERAR_NOTICIA_PORTAL_DISCENTE);
		curso = getCursoAtualCoordenacao();
		programa = getProgramaStricto();
		
		carregarNoticia();
		if (obj == null){
			addMensagemErro("Opera��o j� processada. Isso ocorreu provavelmente porque voc� usou o bot�o voltar no navegador.");
			return listarNoticias();
		}
		else {
			setConfirmButton("Alterar");
			return forward(telaNoticiaNova);
		}
	}	
	
	/**
	 * Lista todas as Not�cias cadastradas
	 * 
	 * JSP:  /SIGAA/app/sigaa.ear/sigaa.war/stricto/menu_coordenador.jsp
	 * 
	 * @return
	 * @throws SegurancaException
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public String listarNoticias() throws SegurancaException, HibernateException, DAOException {
		checkRole(SigaaPapeis.COORDENADOR_CURSO, SigaaPapeis.SECRETARIA_COORDENACAO,
				SigaaPapeis.COORDENADOR_CURSO_STRICTO, SigaaPapeis.SECRETARIA_POS);

		
		NoticiaPortalAcademicoDAO dao = getDAO(NoticiaPortalAcademicoDAO.class, Sistema.COMUM);
		try {
			if (getSubSistema().equals(SigaaSubsistemas.PORTAL_COORDENADOR))
				resultadoBusca = dao.findAllPublicadasByCurso(getCursoAtualCoordenacao().getId());
			else if (getSubSistema().equals(SigaaSubsistemas.PORTAL_COORDENADOR_STRICTO))
				resultadoBusca = dao.findAllPublicadasByPrograma(getProgramaStricto().getId());
			
		} finally {
			dao.close();
		}		
		
		curso = getCursoAtualCoordenacao();
		programa = getProgramaStricto();
		
		return forward(telaListarNoticias);
	}
	
	/**
	 * Exibe detalhadamente a not�cia
	 * 
	 * JSP: /SIGAA/app/sigaa.ear/sigaa.war/graduacao/portal_noticias/lista.jsp
	 * 
	 * @return
	 * @throws DAOException
	 * @throws SegurancaException
	 */
	public String view() throws DAOException, SegurancaException {
		checkRole(SigaaPapeis.COORDENADOR_CURSO, SigaaPapeis.SECRETARIA_COORDENACAO,
				SigaaPapeis.COORDENADOR_CURSO_STRICTO, SigaaPapeis.SECRETARIA_POS);
		
		curso = getCursoAtualCoordenacao();
		programa = getProgramaStricto();
		
		carregarNoticia();	
		if (obj == null){
			addMensagemErro("Opera��o j� processada. Isso ocorreu provavelmente porque voc� usou o bot�o voltar no navegador.");
			return listarNoticias();
		}
		else return forward(telaVerNoticia);
	}

	/**
	 * Pega o id da Not�cia e procura no banco
	 * 
	 * @throws DAOException
	 * @throws SegurancaException
	 */
	private void carregarNoticia() throws DAOException, SegurancaException {
		checkRole(SigaaPapeis.COORDENADOR_CURSO, SigaaPapeis.SECRETARIA_COORDENACAO,
				SigaaPapeis.COORDENADOR_CURSO_STRICTO, SigaaPapeis.SECRETARIA_POS);
		
		int id = getParameterInt("id", 0);
		
		GenericDAO dao = DAOFactory.getGeneric(Sistema.COMUM);
		try {
			obj = dao.findByPrimaryKey(id, NoticiaPortal.class);
		}
		finally {
			dao.close();
		}
	}	
	
	/**
	 * M�todo chamado na JSP na hora de cadastrar/alterar
	 * 
	 * JSP: /SIGAA/app/sigaa.ear/sigaa.war/graduacao/portal_noticias/form.jsp
	 * @throws ArqException
	 */
	public void persistir() throws ArqException {
		
		checkRole(SigaaPapeis.COORDENADOR_CURSO, SigaaPapeis.SECRETARIA_COORDENACAO,
				SigaaPapeis.COORDENADOR_CURSO_STRICTO, SigaaPapeis.SECRETARIA_POS);
		if (obj.getId() == 0) 
			cadastrar();
		else
			alterar();
	}
	
	/**
	 * Insere nova Not�cia checando so pap�is necess�rio para a realiza��o da mesma.
	 * 
	 * JSP: N�o invocado por JSP
	 */
	public String cadastrar() throws ArqException {
		
		checkRole(SigaaPapeis.COORDENADOR_CURSO, SigaaPapeis.SECRETARIA_COORDENACAO,
				SigaaPapeis.COORDENADOR_CURSO_STRICTO, SigaaPapeis.SECRETARIA_POS);
		
		// Campos para valida��o
		obj.setPortalDiscente(true);
		obj.setPortalCoordGraduacao(true);

		obj.setPortalDocente(false);
		obj.setPortalAdministrativo(false);
		obj.setPortalAvaliacaoInstitucional(false);
		obj.setPortalChefiaUnidade(false);
		obj.setPortalConsultor(false);
		obj.setPortalCoordLato(false);
		obj.setPortalCoordStricto(false);
		obj.setPortalPlanoSaude(false);
		obj.setPortalPublicoSigaa(false);
		obj.setPortalPublicoSigrh(false);
		obj.setPortalPublicoSipac(false);
		obj.setCriadoPor(getUsuarioLogado());
		
		ListaMensagens lista = new ListaMensagens();
		erros = obj.validate();
		
		//Obrigat�rio a deifni��o da data de expira��o se not�cia for de destaque.
		if( obj.getExpirarEm() == null ){
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Publicar at�");
		}else{
			Date hoje = new Date();
			ValidatorUtil.validateMinValue(CalendarUtils.adicionaUmDia(obj.getExpirarEm()),hoje, "Publicar at�", lista);
		}

		if ( hasErrors() ) {
			return null;
		}
		MovimentoNoticiaPortalDiscente mov = new MovimentoNoticiaPortalDiscente();
		mov.setCodMovimento(SigaaListaComando.ADICIONAR_NOTICIA_PORTAL_DISCENTE);
		
		movimentoAndProcessador(mov); 
		
		addMensagemInformation("Not�cia cadastrada com sucesso");
		
		envioEmailPrograma();
		return listarNoticias();
	}

	/**
	 * Essa m�todo tem como funcionalidade Alterar as Not�cias, verificando antes se 
	 * o usu�rio logado tem permiss�o para realizar tal opera��o. 
	 * 
	 * JSP: N�o invocado por jsp;
	 * @return
	 * @throws ArqException
	 */
	public String alterar() throws ArqException {
		checkRole(SigaaPapeis.COORDENADOR_CURSO, SigaaPapeis.SECRETARIA_COORDENACAO,
				SigaaPapeis.COORDENADOR_CURSO_STRICTO, SigaaPapeis.SECRETARIA_POS);

		ListaMensagens lista = new ListaMensagens();
		lista = obj.validate();
		
		if (!lista.isEmpty()) {
			addMensagens(lista);
			return null;
		}
		MovimentoNoticiaPortalDiscente mov = new MovimentoNoticiaPortalDiscente();
		mov.setCodMovimento(SigaaListaComando.ALTERAR_NOTICIA_PORTAL_DISCENTE);
		
		movimentoAndProcessador(mov); 	
		
		addMensagemInformation("Not�cia alterada com sucesso");
		
		envioEmailPrograma();
		
		return listarNoticias();
	}
	
	/**
	 * Esse m�todo tem como funcionalidade remover uma not�cia, por�m antes 
	 * verifica se o usu�rio possue permiss�o para realizar tal opera��o.
	 * 
	 * JSP: /SIGAA/app/sigaa.ear/sigaa.war/graduacao/portal_noticias/lista.jsp
	 * @return
	 * @throws ArqException
	 */
	public String removerNoticia() throws ArqException {
		checkRole(SigaaPapeis.COORDENADOR_CURSO, SigaaPapeis.SECRETARIA_COORDENACAO,
				SigaaPapeis.COORDENADOR_CURSO_STRICTO, SigaaPapeis.SECRETARIA_POS);
		
		prepareMovimento(SigaaListaComando.REMOVER_NOTICIA_PORTAL_DISCENTE);
		
		carregarNoticia();
		
		if (obj == null){
			addMensagemErro("Opera��o j� processada. Isso ocorreu provavelmente porque voc� usou o bot�o voltar no navegador.");
		}
		else {
				
			MovimentoNoticiaPortalDiscente mov = new MovimentoNoticiaPortalDiscente();
			mov.setCodMovimento(SigaaListaComando.REMOVER_NOTICIA_PORTAL_DISCENTE);
			
			movimentoAndProcessador(mov); 	
			
			addMensagemInformation("Not�cia removida com sucesso");
		}
		return listarNoticias();
	}
	
	/**
	 * Serve para retirar a not�cia do ar, por�m antes verificando se o usu�rio possui o papel 
	 * necess�rio para a realiza��o da opera��o. 
	 * 
	 * JSP: /SIGAA/app/sigaa.ear/sigaa.war/graduacao/portal_noticias/lista.jsp
	 * @return
	 * @throws ArqException
	 */
	public String despublicar() throws ArqException {
		checkRole(SigaaPapeis.COORDENADOR_CURSO, SigaaPapeis.SECRETARIA_COORDENACAO,
				SigaaPapeis.COORDENADOR_CURSO_STRICTO, SigaaPapeis.SECRETARIA_POS);

		
		prepareMovimento(SigaaListaComando.DESPUBLICAR_NOTICIA_PORTAL_DISCENTE);
		
		carregarNoticia();
		if (obj == null){
			addMensagemErro("Opera��o j� processada. Isso ocorreu provavelmente porque voc� usou o bot�o voltar no navegador.");
		}
		else {
			obj.setPublicada(false);
	
			MovimentoNoticiaPortalDiscente mov = new MovimentoNoticiaPortalDiscente();
			mov.setCodMovimento(SigaaListaComando.DESPUBLICAR_NOTICIA_PORTAL_DISCENTE);
			
			movimentoAndProcessador(mov);
			
			addMensagemInformation("Status de publica��o alterado com sucesso");
		}
		return listarNoticias();
		
	}	

	/**
	 * Serve para p�blicar uma not�cia, verifica se o usu�rio possui permiss�o para tal opera��o, 
	 * em seguida p�blicar a noticia no portal.
	 * 
	 * JSP: /SIGAA/app/sigaa.ear/sigaa.war/graduacao/portal_noticias/lista.jsp
	 * @return
	 * @throws ArqException
	 */
	public String publicar() throws ArqException {
		checkRole(SigaaPapeis.COORDENADOR_CURSO, SigaaPapeis.SECRETARIA_COORDENACAO,
				SigaaPapeis.COORDENADOR_CURSO_STRICTO, SigaaPapeis.SECRETARIA_POS);
		
		prepareMovimento(SigaaListaComando.PUBLICAR_NOTICIA_PORTAL_DISCENTE);
		
		carregarNoticia();
		if (obj == null){
			addMensagemErro("Opera��o j� processada. Isso ocorreu provavelmente porque voc� usou o bot�o voltar no navegador.");
		}
		else {
			obj.setPublicada(true);
			
			MovimentoNoticiaPortalDiscente mov = new MovimentoNoticiaPortalDiscente();
			mov.setCodMovimento(SigaaListaComando.PUBLICAR_NOTICIA_PORTAL_DISCENTE);
	
			movimentoAndProcessador(mov);
			
			addMensagemInformation("Status de publica��o alterado com sucesso");
		}
		return listarNoticias();
		
	}		
	
	/**
	 * Esse m�todo e respons�vel por processar o cadastro da nova not�cia, verificando antes se o usu�rio 
	 * possui permiss�o para a realiza��o de tal opera��o. 
	 * 
	 * @param mov
	 * @throws ArqException
	 */
	private void movimentoAndProcessador(MovimentoNoticiaPortalDiscente mov) throws ArqException {

		checkRole(SigaaPapeis.COORDENADOR_CURSO, SigaaPapeis.SECRETARIA_COORDENACAO,
				SigaaPapeis.COORDENADOR_CURSO_STRICTO, SigaaPapeis.SECRETARIA_POS);
		
		if (arquivo != null) {
			try {
				int idArquivo = EnvioArquivoHelper.getNextIdArquivo();
				EnvioArquivoHelper.inserirArquivo(idArquivo, arquivo.getBytes(), arquivo.getContentType(), arquivo.getName());
				obj.setIdArquivo(idArquivo);
			} catch (IOException e) {
				tratamentoErroPadrao(e);
			}
		}
		
		mov.setNoticia(obj);
		
		try {
			executeWithoutClosingSession(mov, getCurrentRequest());
		} catch (NegocioException e) {
			tratamentoErroPadrao(e);
		}
	}	
	
	
	/**
	 * Visualiza o arquivo enviado na not�cia.<br/><br/>
	 * <br/><br/>M�todo chamado pela(s) JSP(s):
	 * <ul>
	 * 		<li>sigaa.war/graduacao/portal_noticias/lista.jsp</li>
	 * </ul>
	 * @return
	 */
	public String visualizarArquivo (){

		int idArquivo = getParameterInt("idArquivo");
		String idArquivoKey = UFRNUtils.generateArquivoKey(idArquivo);
		
		String nomeArquivo = EnvioArquivoHelper.recuperaNomeArquivo(idArquivo);

		if ( nomeArquivo == null )
		{	
			addMensagemErro("Arquivo n�o encontrado!");
			return forward(telaListarNoticias); 
		}
		else
			 return redirect("/sigaa/verProducao?idProducao=" +idArquivo+ "&key="+idArquivoKey);
		
	}
	
	/**
	 * Envio de um email avisando as discentes que uma nova not�cia foi cadastrada no 
	 * programa do mesmo.
	 * 
	 *  JSP: N�o � invocada por nenhuma jsp.
	 */
	@SuppressWarnings("unchecked")
	private void envioEmailPrograma() throws DAOException {
		DiscenteDao dao = getDAO(DiscenteDao.class);		
		CursoDao cDao = getDAO(CursoDao.class);
		
		try{			
			if (programa != null){
				programaPortal = dao.findByNoticiaPublicadaProgramaEmail(programa.getId());				
			} 
			
			if ( notificar ) {
				if ( obj.getIdCurso() != null && curso != null ){
					ArrayList<String> emailDiscentes = cDao.findEmailDiscenteByCurso(obj.getIdCurso());
				
					for ( String enderecoEmail : emailDiscentes )
					{
						MailBody email = new MailBody();
						email.setAssunto("[SIGAA] Comunicado do Curso: " + obj.getTitulo());
						email.setContentType(MailBody.TEXT_PLAN);
						email.setEmail(enderecoEmail);
						email.setMensagem(obj.getDescricao());
						
						Mail.send(email);
					}	
				}	
			}
			
			if (programaPortal.size() >= 1){
				Iterator<Map<String, Object>> it = programaPortal.iterator();
				while(it.hasNext()){
					Map<String, String> mapa = (Map) it.next();
					String nome = mapa.get("nome");
					String enderecoEmail = mapa.get("email");
					
					MailBody email = new MailBody();
					email.setAssunto("[SIGAA] Comunicado " + programa.getSigla() + ": " + obj.getTitulo());
					email.setContentType(MailBody.TEXT_PLAN);
					email.setEmail(enderecoEmail);
					email.setNome(nome);
					email.setMensagem(obj.getDescricao());
					
					Mail.send(email);
				}									
			}
		} finally {
			if (dao != null)
				dao.close();			
		}
	}
	
	public Curso getCurso() {
		return curso;
	}

	public void setCurso(Curso curso) {
		this.curso = curso;
	}

	public UploadedFile getArquivo() {
		return arquivo;
	}

	public void setArquivo(UploadedFile arquivo) {
		this.arquivo = arquivo;
	}

	public Collection<NoticiaPortal> getResultadoBusca() {
		return resultadoBusca;
	}

	public void setResultadoBusca(Collection<NoticiaPortal> resultadoBusca) {
		this.resultadoBusca = resultadoBusca;
	}

	public Unidade getPrograma() {
		return programa;
	}

	public void setPrograma(Unidade programa) {
		this.programa = programa;
	}

	public boolean isNotificar() {
		return notificar;
	}

	public void setNotificar(boolean notificar) {
		this.notificar = notificar;
	}
	
}