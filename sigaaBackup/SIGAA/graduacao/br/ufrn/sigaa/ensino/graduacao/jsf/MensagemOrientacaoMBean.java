package br.ufrn.sigaa.ensino.graduacao.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;
import java.util.Collection;

import javax.faces.model.SelectItem;

import org.hibernate.HibernateException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.sigaa.arq.dao.graduacao.MensagemOrientacaoDao;
import br.ufrn.sigaa.arq.dao.graduacao.OrientacaoAcademicaDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.graduacao.dominio.MensagemOrientacao;
import br.ufrn.sigaa.ensino.graduacao.dominio.OrientacaoAcademica;
import br.ufrn.sigaa.pessoa.dominio.Discente;

/**
 * MBean responsável pela listagem e busca das mensagens de orientação acadêmica.
 * 
 * @author bernardo
 *
 */
@Component("mensagemOrientacao")
@Scope("request")
public class MensagemOrientacaoMBean extends SigaaAbstractController<MensagemOrientacao> {
	
	/** Define o número máximo de registros a serem mostrados por página. */
	private static final int QUANTIDADE_MAXIMA_POR_PAGINA = 20;
	
	/** Link para a página de listagem de mensagens. */
	public static final String JSP_LISTA = "/graduacao/mensagem_orientacao/listar.jsp";

	/** Armazena as mensagens de orientação do usuário. */
	private Collection<MensagemOrientacao> mensagens;
	
	/** Armazena as últimas mensagens enviadas pelo usuário. */
	private Collection<MensagemOrientacao> ultimasMensagens;
	
	/** Discente escolhido na busca de mensagens. */
	private DiscenteAdapter discente;
	
	/** Indica se o acesso à busca de mensagens foi feito pela tela de orientações acadêmicas. */
	private boolean permiteVoltar;
	
	private boolean buscaOrientandos;
	
	private boolean buscaTodos;
	
	/** Construtor padrão. */
	public MensagemOrientacaoMBean() {
		super();
		init();
	}
	
	/**
	 * Inicia os dados do MBean.
	 */
	private void init() {
		discente = new Discente();
		ultimasMensagens = null;
		mensagens = null;
		permiteVoltar = false;
	}

	/**
	 * Método responsável por listar todas as mensagens enviadas pelo orientador.
	 * <br /><br />
	 * Chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/graduacao/orientacao_academica/lista.jsp</li>
	 * <li>/sigaa.war/portais/docente/menu_docente.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String listarMensagens() throws DAOException {
		popularMensagensTodosOrientandos();
		
		buscaTodos = true;

		return forward(JSP_LISTA);
	}

	private void popularMensagensTodosOrientandos() throws DAOException {
		setTamanhoPagina(QUANTIDADE_MAXIMA_POR_PAGINA);
		MensagemOrientacaoDao dao = getDAO(MensagemOrientacaoDao.class);
		
		try {
			mensagens = dao.findAllByOrientador(getServidorUsuario(), getPaginacao());
		} finally {
			dao.close();
		}
	}
	
	/**
	 * Realiza a busca de mensagens de orientação que estejam dentro dos parâmetros estabelecidos.
	 * <br /><br />
	 * Chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/graduacao/mensagem_orientacao/listar.jsp</li>
	 * </ul>
	 * 
	 */
	public String buscar() throws DAOException {
		String param = getParameter("paramBusca");
		
		if("todos".equals(param)) {
			buscaTodos = true;
			buscaOrientandos = false;
			
			popularMensagensTodosOrientandos();
		} else if ("orientandos".equals(param)) {
			buscaOrientandos = true;
			buscaTodos = false;
			
			if(isEmpty(discente)) {
				addMensagemErro("Selecione um discente.");
				return null;
			}
				
			popularMensagensOrientandoEscolhido();
		} else {
			addMensagemErro("Selecione um filtro para realizar a busca.");
			mensagens = null;
			return null;
		}
		
		if(isEmpty(mensagens))
			addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
		
		return forward(JSP_LISTA);
	}

	private void popularMensagensOrientandoEscolhido() throws DAOException {
		MensagemOrientacaoDao dao = getDAO(MensagemOrientacaoDao.class);
		
		try {
			mensagens = dao.findByOrientando(getServidorUsuario(), discente, getPaginacao(), false);setTamanhoPagina(QUANTIDADE_MAXIMA_POR_PAGINA);
		} finally {
			dao.close();
		}
	}
	
	/**
	 * Retorna o combo a ser utilizado para filtrar a busca de mensagens por discente.
	 * 
	 * <br /><br />
	 * Chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/graduacao/mensagem_orientacao/listar.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException 
	 * @throws HibernateException 
	 */
	public Collection<SelectItem> getAllOrientandosCombo() throws HibernateException, DAOException {
		OrientacaoAcademicaDao dao = getDAO(OrientacaoAcademicaDao.class);
		
		Collection<OrientacaoAcademica> orientandos = dao.findAllOrientandosByServidor(getServidorUsuario());
		
		Collection<SelectItem> orientandosCombo = new ArrayList<SelectItem>();
		
		for (OrientacaoAcademica o : orientandos) {
			String label = o.getDiscente().getMatriculaNome() + " (";
			
			label += o.isAtivo() ? "ATIVA)" : "INATIVA)";
			
			orientandosCombo.add(new SelectItem(o.getDiscente().getId(), label));
		}
		
		return orientandosCombo;
	}
	
	public Collection<MensagemOrientacao> getMensagens() throws DAOException {
		return mensagens;
	}

	public void setMensagens(Collection<MensagemOrientacao> mensagens) {
		this.mensagens = mensagens;
	}

	public Collection<MensagemOrientacao> getUltimasMensagens() throws DAOException {
		if(isEmpty(ultimasMensagens)) {
			MensagemOrientacaoDao dao = getDAO(MensagemOrientacaoDao.class);
			try {
				ultimasMensagens = dao.getUltimasMensagens(getServidorUsuario());
			} finally {
				dao.close();
			}
		}
		return ultimasMensagens;
	}

	public void setUltimasMensagens(Collection<MensagemOrientacao> ultimasMensagens) {
		this.ultimasMensagens = ultimasMensagens;
	}

	public DiscenteAdapter getDiscente() {
		return discente;
	}

	public void setDiscente(DiscenteAdapter discente) {
		this.discente = discente;
	}

	public boolean isPermiteVoltar() {
		return permiteVoltar;
	}

	public void setPermiteVoltar(boolean permiteVoltar) {
		this.permiteVoltar = permiteVoltar;
	}

	public boolean isBuscaOrientandos() {
		return buscaOrientandos;
	}

	public void setBuscaOrientandos(boolean buscaOrientandos) {
		this.buscaOrientandos = buscaOrientandos;
	}

	public boolean isBuscaTodos() {
		return buscaTodos;
	}

	public void setBuscaTodos(boolean buscaTodos) {
		this.buscaTodos = buscaTodos;
	}

}
