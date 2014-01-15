/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 24/12/2008
 *
 */
package br.ufrn.sigaa.extensao.jsf; 

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.seguranca.autenticacao.EmissaoDocumentoAutenticado;
import br.ufrn.sigaa.arq.dao.extensao.DiscenteExtensaoDao;
import br.ufrn.sigaa.arq.dao.extensao.ParticipanteAcaoExtensaoDao;
import br.ufrn.sigaa.arq.dao.projetos.MembroProjetoDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.extensao.dominio.AtividadeExtensao;
import br.ufrn.sigaa.extensao.dominio.AvaliacaoAtividade;
import br.ufrn.sigaa.extensao.dominio.DiscenteExtensao;
import br.ufrn.sigaa.extensao.dominio.ParticipanteAcaoExtensao;
import br.ufrn.sigaa.projetos.dominio.CategoriaMembro;
import br.ufrn.sigaa.projetos.dominio.MembroProjeto;

/*******************************************************************************
 * MBean respons�vel por gerar unificar alguns m�todos utilizados em MBeans de
 * gera��o de documentos autenticados <br/>
 * 
 * A autentica��o dos documentos eh realizada pelo SIGAA atrav�s da gera��o de
 * um c�digo de documento e c�digo de verifica��o que pode ser validado atrav�s
 * do portal publico do SIGAA.
 * 
 * @author Ilueny Santos
 * 
 ******************************************************************************/
@Scope("session")
@Component("documentosAutenticadosExtensao")
public class DocumentosAutenticadosExtensaoMBean extends SigaaAbstractController<MembroProjeto> {

	
	/** lista os membros do projeto selecionado */
	public static final String PAGINA_LISTA_PROJETO = "/extensao/DocumentosAutenticados/lista.jsp";
	
	/** form para buscar os projetos para emitir certificado */
	public static final String PAGINA_BUSCA_PROJETO = "/extensao/DocumentosAutenticados/form.jsp";
	
	/** form para buscar os projetos para emitir certificado */
	public static final String PAGINA_LISTA_MEMBROS_PROJETO = "/extensao/DocumentosAutenticados/membros.jsp";
	
	private Collection<AtividadeExtensao> atividades = new ArrayList<AtividadeExtensao>();

	//Usado para armazenar informa��es de busca dos membros da equipe organizadora das a��es de extens�o.
	//Com essas informa��es podemos decidir se um membro tem direito a emiss�o certos documentos.
	private Collection<MembroProjeto> membros = new ArrayList<MembroProjeto>();
	
	//Usado para armazenar informa��es de busca dos Discentes de Extens�o das a��es de extens�o.
	//Com essas informa��es podemos decidir se um discente tem direito a emiss�o certos documentos.
	private Collection<DiscenteExtensao> discentesExtensao =  new ArrayList<DiscenteExtensao>();
	
	//Usado para armazenar informa��es de busca dos Participantes das a��es de extens�o.
	//Com essas informa��es podemos decidir se um Participante tem direito a emiss�o certos documentos.
	private Collection<ParticipanteAcaoExtensao> participantes = new HashSet<ParticipanteAcaoExtensao>();
	
	private Collection<AvaliacaoAtividade> avaliacoes = new ArrayList<AvaliacaoAtividade>();

	
	//Usado para preencher dados do membro a ser emitido certificado.
	private MembroProjeto membro = new MembroProjeto();

	//Comprovante de emiss�o de documento. Usado na hora de emitir o docmento.
	private EmissaoDocumentoAutenticado comprovante;

	//Usado para informar se o documento esta sendo emitido ou se esta sendo validado na �rea p�blica do sigaa.
	private boolean verificando = false;

	/**
	 * Exibe os membros da atividade selecionada para emiss�o dos certificados
	 * por gestor de extens�o (membro da pr�-reitoria de extens�o)
	 * 
	 * @throws DAOException
	 */	
	public String selecionarMembro() throws DAOException {
		int id = Integer.parseInt(getParameter("id"));
		
		MembroProjetoDao dao= null; 
		
		try{
			dao = getDAO(MembroProjetoDao.class);
			membros = dao.findAtivosByProjeto(id, CategoriaMembro.DOCENTE, CategoriaMembro.SERVIDOR);
			
		}finally{
			if(dao != null) dao.close();
		}
		
		return getListPage();
	}

	
	
	
	@Override
	public String getFormPage() {
		return forward(PAGINA_BUSCA_PROJETO);
	}




	@Override
	public String getListPage() {
		return forward(PAGINA_LISTA_PROJETO);
	}


	public String getMembrosListPage() {
		return forward(PAGINA_LISTA_MEMBROS_PROJETO);
	}

	
	

	/**
	 * Todas as participa��es do servidor do usu�rio logado em a��es de extens�o v�lidas
	 * (aprovadas pelo comit� de extens�o) para emiss�o de certificados e declara��es
	 * 
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * sigaa.war/extensao/menu_ta.jsp
	 * sigaa.war/extensao/menu.jsp
	 * sigaa.war/portais/docente/menu_docente.jsp
	 * 
	 * @return
	 * @throws DAOException
	 * @throws SegurancaException
	 */
	public String participacoesServidorUsuarioLogado()throws DAOException, SegurancaException {

		if ((getUsuarioLogado() != null) && (getUsuarioLogado().getPessoa() != null)){
			MembroProjetoDao dao = getDAO(MembroProjetoDao.class);
			membros = dao.findMembrosExtensaoByPessoa(getUsuarioLogado().getPessoa().getId());
			
			ParticipanteAcaoExtensaoDao dao2 = getDAO(ParticipanteAcaoExtensaoDao.class);
			participantes = dao2.findInformacoesParticipanteByPessoaEmissaoCertificados(getUsuarioLogado().getPessoa().getId());
			
		}else{
			membros = new ArrayList<MembroProjeto>();
			participantes = new ArrayList<ParticipanteAcaoExtensao>();
			avaliacoes = new ArrayList<AvaliacaoAtividade>();	
		}
		
		return forward("/extensao/DocumentosAutenticados/lista.jsp");
	}

	/**
	 * Todas as participa��es do discente do usu�rio logado em a��es de extens�o v�lidas
	 * (aprovadas pelo comit� de extens�o) para emiss�o de certificados e declara��es
	 * 
	 * M�todo chamado pelas seguinte(s) JSP(s):
	 * sigaa.war/portais/discente/menu_discente.jsp
	 * 
	 * @return
	 * @throws DAOException
	 * @throws SegurancaException
	 */
	public String participacoesDiscenteUsuarioLogado() throws DAOException, SegurancaException {

		if ((getUsuarioLogado() != null) && (getUsuarioLogado().getPessoa() != null)){
			MembroProjetoDao dao = getDAO(MembroProjetoDao.class);
			membros = dao.findMembrosExtensaoByPessoa(getDiscenteUsuario().getPessoa().getId());			

			ParticipanteAcaoExtensaoDao dao2 = getDAO(ParticipanteAcaoExtensaoDao.class);
			participantes = dao2.findInformacoesParticipanteByPessoaEmissaoCertificados(getUsuarioLogado().getPessoa().getId());
			
			DiscenteExtensaoDao dao3 = getDAO(DiscenteExtensaoDao.class);
			discentesExtensao = dao3.findByDiscenteComPlanoTrabalho(getDiscenteUsuario().getId(), null);
		}else{
			membros = new ArrayList<MembroProjeto>();
			participantes = new ArrayList<ParticipanteAcaoExtensao>();
			discentesExtensao = new ArrayList<DiscenteExtensao>();
		}
		
		return forward("/extensao/DocumentosAutenticados/lista.jsp");
	}
	
	
	public Collection<AtividadeExtensao> getAtividades() {
		return atividades;
	}

	public void setAtividades(Collection<AtividadeExtensao> atividades) {
		this.atividades = atividades;
	}

	public Collection<MembroProjeto> getMembros() {
		return membros;
	}

	public void setMembros(Collection<MembroProjeto> membros) {
		this.membros = membros;
	}

	public MembroProjeto getMembro() {
		return membro;
	}

	public void setMembro(MembroProjeto membro) {
		this.membro = membro;
	}

	public EmissaoDocumentoAutenticado getComprovante() {
		return comprovante;
	}

	public void setComprovante(EmissaoDocumentoAutenticado comprovante) {
		this.comprovante = comprovante;
	}

	public boolean isVerificando() {
		return verificando;
	}

	public void setVerificando(boolean verificando) {
		this.verificando = verificando;
	}

	public Collection<ParticipanteAcaoExtensao> getParticipantes() {
		return participantes;
	}

	public void setParticipantes(Collection<ParticipanteAcaoExtensao> participantes) {
		this.participantes = participantes;
	}

	public Collection<AvaliacaoAtividade> getAvaliacoes() {
		return avaliacoes;
	}

	public void setAvaliacoes(Collection<AvaliacaoAtividade> avaliadores) {
		this.avaliacoes = avaliadores;
	}

	public Collection<DiscenteExtensao> getDiscentesExtensao() {
	    return discentesExtensao;
	}

	public void setDiscentesExtensao(Collection<DiscenteExtensao> discentesExtensao) {
	    this.discentesExtensao = discentesExtensao;
	}

}
