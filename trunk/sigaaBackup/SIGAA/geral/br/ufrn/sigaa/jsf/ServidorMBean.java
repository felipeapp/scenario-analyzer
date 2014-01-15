/*
 * Sistema Integrado de Gest�o de Atividades Acad�micas - SIGAA
 * Superintend�ncia de Inform�tica - UFRN
 *
 * Created on '03/01/2007'
 *
 */
package br.ufrn.sigaa.jsf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.rh.dominio.Cargo;
import br.ufrn.sigaa.arq.dao.rh.ServidorDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.pessoa.dominio.Servidor;

/**
 * Managed-Bean para busca de servidores na �rea de administra��o e em outras
 * localidades necess�rias
 *
 * @author Gleydson Lima
 *
 */
public class ServidorMBean extends SigaaAbstractController<Unidade> {
	/** Caminho da JSP de resumo */
	private static final String JSP_RESUMO = "/administracao/cadastro/Servidor/resumo.jsp";
	/** Atributo com a Lista de servidores */
	private ArrayList<Servidor> lista = new ArrayList<Servidor>();
	/** Auxilia para identificar qual o servidor est� selecionado */
	private Servidor servidorSelecionado;
	/** Auxilia para guardar o nome do servidor selecionado */
	private String nome;
	/** Atributo utilizado para exibir mensagem no suggestion box*/
	private String textoSuggestionBox;	

	/**
	 * Filtra pelo nome
	 *
	 * @param evt
	 * @throws DAOException
	 * @throws SegurancaException
	 */
	public void buscar(ActionEvent evt) throws ArqException {

		checkRole(new int[]{SigaaPapeis.DAE, SigaaPapeis.ADMINISTRADOR_SIGAA, SigaaPapeis.CONSULTADOR_ACADEMICO, SigaaPapeis.GESTOR_PESQUISA , SigaaPapeis.PORTAL_PLANEJAMENTO});
		if(nome == null || nome.isEmpty()){
			addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
			return;
			
		}
		
		ServidorDao servDao = getDAO(ServidorDao.class);
		lista = (ArrayList<Servidor>) servDao.findByLikeField(Servidor.class, "pessoa.nome", nome);
		
		Collections.sort(lista, new Comparator<Servidor>(){
			public int compare(Servidor o1, Servidor o2) {
				String nome = StringUtils.toAscii(o1.getNome());
				return nome.compareToIgnoreCase(StringUtils.toAscii( o2.getNome() ));
			}
		});
	}
	
	/**
	 * AutoComplete que busca os Docentes pelo Nome
	 * @param nome
	 * @return
	 * @throws DAOException 
	 */
	public Collection<Servidor> autocompleteDocente(Object nome) throws DAOException {
		return getBuscaDocente(nome, 0, null);
	}
	
	
	/**
	 * Verifica se o usu�rio tem permiss�o para consultar dados de Servidores.
	 * 
	 * Chamado por:
	 * sigaa.war/graduacao/menus/consultas.jsp
	 */
	public boolean isPermissaoConsultarServidor() {
		return isUserInRole(SigaaPapeis.DAE, SigaaPapeis.ADMINISTRADOR_SIGAA, SigaaPapeis.CONSULTADOR_ACADEMICO);
	}
	
	/** 
	 * N�o chamado por JSPs.
	 */
	public String getDirBase() {
		return "/administracao/cadastro/Servidor";
	}
	
	
	/**
	 * Realiza a busca de docente conforme os par�metros passados.
	 * @param campo
	 * @param idUnidade
	 * @param cargos
	 * @return
	 * @throws DAOException
	 */
	private Collection<Servidor> getBuscaDocente(Object campo, Integer idUnidade, List<Integer> cargos) throws DAOException{
		String nome = campo.toString(); //Nome do item digitado no autocomplete
		Long siape = StringUtils.getNumerosIniciais(nome);
		Integer intCodigo = null;
		if (siape != null){
			nome = null; //consulta pelo siape apenas
			intCodigo = siape.intValue();
		}							
		ServidorDao servidorDao = getDAO(ServidorDao.class);
		Collection<Servidor> listaBusca = new ArrayList<Servidor>();
		try {
			textoSuggestionBox = "";
			if (nome != null && StringUtils.isAlpha(nome) && nome.length() < 3)
				textoSuggestionBox = "Pesquisa pelo Nome ou SIAPE, informe pelo menos 3 caracteres.";		
			
			if ((nome != null && nome.length() >= 3) || (!ValidatorUtil.isEmpty(intCodigo)))
				listaBusca = servidorDao.findByDocente(nome, intCodigo, idUnidade, false, null, false, cargos);
			
			if (ValidatorUtil.isEmpty(lista) && ((nome != null && nome.length() >= 3) || !ValidatorUtil.isEmpty(intCodigo)))
				textoSuggestionBox = "Docente n�o encontrado.";									
		} finally {
			if (servidorDao != null)
				servidorDao.close();			
		}	
		return listaBusca;
	}
	
	/**
	 * AutoComplete que busca os Docentes pelo nome, do departamento do usu�rio logado.
	 * @param campo
	 * @return
	 * @throws DAOException 
	 */
	public Collection<Servidor> autocompleteDocenteDepartamento(Object campo) throws DAOException {
		return getBuscaDocente(campo, getUsuarioLogado().getVinculoAtivo().getUnidade().getId(), null);
	}	
	
	/**
	 * AutoComplete que busca os Docentes Substitutos pelo nome, do departamento do usu�rio logado.
	 * @param campo
	 * @return
	 * @throws DAOException 
	 */
	public Collection<Servidor> autocompleteDocenteSubstitutoDepartamento(Object campo) throws DAOException {
		return getBuscaDocente(campo, getUsuarioLogado().getVinculoAtivo().getUnidade().getId(), Cargo.DOCENTE_SUBSTITUTO);
	}	
	
	/**
	 * Busca os dados do docente e redireciona para a tela de exibi��o.
	 * <br/><br/>
	 * M�todo chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/administracao/cadastro/Servidor/lista.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public String exibirDadosPessoais() throws ArqException {

		ServidorDao servDao = getDAO(ServidorDao.class);				
		servidorSelecionado = servDao.findByPrimaryKey(getParameterInt("id"), Servidor.class);

		return forward(JSP_RESUMO);
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public ArrayList<Servidor> getLista() {
		return lista;
	}

	public void setLista(ArrayList<Servidor> lista) {
		this.lista = lista;
	}
	
	/**
	 * Retorna uma Cole��o de SelectItem com todos os docentes do departamento do usu�rio logado. 
	 * M�todo n�o chamado por JSP.
	 * @return
	 */
	public Collection<SelectItem> getAllDocenteDepartamentoCombo(){
		ServidorDao dao = getDAO(ServidorDao.class);
		try {
			return toSelectItems(dao.findByDocente(getUsuarioLogado().getVinculoAtivo().getUnidade().getId()), "id", "nome");
		} catch (Exception e) {
			notifyError(e);
			return new ArrayList<SelectItem>();
		}
	}

	public Servidor getServidorSelecionado() {
		return servidorSelecionado;
	}

	public void setServidorSelecionado(Servidor servidorSelecionado) {
		this.servidorSelecionado = servidorSelecionado;
	}

	public String getTextoSuggestionBox() {
		return textoSuggestionBox;
	}

	public void setTextoSuggestionBox(String textoSuggestionBox) {
		this.textoSuggestionBox = textoSuggestionBox;
	}
}
