/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Created on 13/03/2009
 *
 */
package br.ufrn.sigaa.biblioteca.jsf;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.dao.biblioteca.BibliotecaDao;
import br.ufrn.sigaa.arq.dao.biblioteca.PoliticaEmprestimoDao;
import br.ufrn.sigaa.arq.dao.biblioteca.StatusMaterialInformacionalDao;
import br.ufrn.sigaa.arq.dao.biblioteca.TipoEmprestimoDao;
import br.ufrn.sigaa.arq.dao.biblioteca.TipoMaterialDAO;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.PoliticaEmprestimo;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.StatusMaterialInformacional;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.TipoEmprestimo;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.TipoMaterial;
import br.ufrn.sigaa.biblioteca.circulacao.negocio.MovimentoCadastraPoliticaEmprestimo;
import br.ufrn.sigaa.biblioteca.dominio.Biblioteca;
import br.ufrn.sigaa.biblioteca.dominio.VinculoUsuarioBiblioteca;
import br.ufrn.sigaa.biblioteca.util.BibliotecaUtil;
import br.ufrn.sigaa.parametros.dominio.ParametrosBiblioteca;

/**
 *      <p>MBean para gerenciamento do cadastro das pol�ticas de empr�stimo.<p>
 *   
 *      <p><i>Uma pol�tica de empr�stimo nunca � atualizada, sempre desativada e criada uma nova. 
 *      <p>Os empr�stimos antigos continuar�o apontando para a pol�tica desativa, os novos sempre para <br/>
 * pol�ticas ativas, para o c�lculo dos prazos dos empr�stimos permanecerem os mesmo do momento em que <br/>
 * o empr�stimo foi feito, mesmo que a pol�tica seja modificada posteriormente.</p> <br/></i></p>
 *
 * @author Jadson
 * @since 03/06/2009
 * @version 1.0 Cria��o da classe
 * @version 2.0 Jadson - 14/02/2013 - Alterando o cadastro de pol�ticas para comportar as regras abaixo: 
 * 
 * <p>
 * Situa��o atual: Tipo de empr�stimo e Status definindo o quantidade e dias: <br/><br/>
 *
 * NORMAL ->  REGULAR = quantidade e prazo <br/>
 * NORMAL ->  ESPECIAL = quantidade e prazo <br/>
 * ESPECIAL -> REGULAR = quantidade e prazo <br/>
 * ESPECIAL -> ESPECIAL = quantidade e prazo <br/>
 * FOTO_C�PIA -> REGULAR = quantidade e prazo <br/>
 * FOTO_C�PIA -> ESPECIAL = quantidade e prazo <br/>
 * <br/>
 *  Dever� ser assim: uma pol�tica de um determinado tipo de empr�stimo pode est� associado a 0 a N Status de Material e 0 a N tipos de materiais <br/>
 * <br/> <br/>
 * NORMAL  -> REGULAR (qualquer tipo de material )  = quantidade e prazo <br/>
 * ESPECIAL -> REGULAR e ESPECIAL  (qualquer tipo de material )  = quantidade e prazo <br/>
 * FOTO_C�PIA -> REGULAR e ESPECIAL  (qualquer tipo de material )   = quantidade e prazo <br/>
 * NOVO_TIPO ->  REGULAR e ESPECIAL (para o tipo de material disco) = quantidade e prazo  (usado apenas na biblioteca de m�sica) <br/>
 * 
 * </p>
 * 
 */

@Component("politicaEmprestimoMBean")
@Scope("request")
public class PoliticaEmprestimoMBean extends SigaaAbstractController<PoliticaEmprestimo> {
	
	/**
	 * O formul�rio para cria��o de novas pol�tica de de empr�stimos.
	 */
	public static final String PAGINA_CRIAR_NOVAS_POLITICAS = "/biblioteca/PoliticaEmprestimo/form.jsp";

	/**
	 * Lista as pol�ticas de empr�stimo existentes.
	 */
	public static final String PAGINA_LISTA_POLITICAS_EMPRESTIMO = "/biblioteca/PoliticaEmprestimo/lista.jsp";
	
	
	/**
	 * O formul�rio para cria��o de novas pol�tica de de empr�stimos.
	 */
	public static final String PAGINA_CONFIRMA_REMOCAO_POLITICAS = "/biblioteca/PoliticaEmprestimo/formConfirmaRemocao.jsp";

	
	
	/** A biblioteca que possui as pol�ticas que o usu�rio vai alterar */
	private Biblioteca bibliotecaDasPoliticas;
	
	/** O tipo de usu�rio que para quem as pol�ticas v�o valer. */ 
	private VinculoUsuarioBiblioteca vinculo; 
	
	/**
	 * Guarda o valor do v�nculo selecionado no combo box, j� que o selectOneMenu n�o consegue utilizar um enum.
	 */
	private Integer valorVinculoSelecionado;
	
	
	
	/** Indica quando o usu�rio escolheu a biblioteca */
	private boolean escolheuBiblioteca; 
	
	/** Indica quando o usu�rio escolheu o tipo de usu�rio */
	private boolean escolheuTipoUsuario; 

	
	
	/** A lista de pol�ticas de empr�stimo que o usu�rio vai configurar */
	private List<PoliticaEmprestimo> politicasEmprestimo; 
	
	
	/** Data model para ficar mais f�cil remover as pol�ticas selecionadas, mesmo as que ainda n�o tem "id" (foram adicionada � lista recentemente)*/
	private DataModel dataModelPoliticas = new ListDataModel();
	
	
	
	
	/**
	 * Utilizado para verificar se o operador utilizou o bot�o voltar do 
	 * navegador, j� que a p�gina de altera��o e formul�rio � a mesma.
	 */
	private boolean cancelado = true;
	
	
	/** Guarda os status de materiais que podem ser assiciados a pol�ticas de empr�stimos*/
	private List<StatusMaterialInformacional> statusQuePodemSerEmprestados = new ArrayList<StatusMaterialInformacional>();
	
	/** Guarda os tipos de materiais que podem ser assiciados a pol�ticas de empr�stimos*/
	private List<TipoMaterial> tiposMateriaisQuePodemSerEmprestados = new ArrayList<TipoMaterial>(); 

	/** Guarda os tipos de de empr�stimos ativos no sistema, s�o os que ele pode atribuir a pol�tica.*/
	private List<TipoEmprestimo> tiposEmprestimosAtivosPodemSerAssociadosAPoliticas =  new ArrayList<TipoEmprestimo>(); 
	
	
	/** O id do tipo de empr�stimo selecionado pelo usu�rio para ser atribu�do a novas pol�ticas. */
	private int idTipoEmprestimoSelecionadoNovasPoliticas = -1;
	
	
	/**
	 * Construtor padr�o do MBean
	 */
	public PoliticaEmprestimoMBean() {
		bibliotecaDasPoliticas = new Biblioteca(-1);
	}
	
	
	
	/**
	 *   <p>Inicia o caso de altera��o de pol�ticas de empr�stimo. </p>
	 * 
	 *     <p>Se a biblioteca escolhida n�o tiver a pol�tica ainda o sistema criar� com os valores zerados.</p>
	 *     <p>Se o usu�rio alterar algum valor, uma nova pol�tica � criada com os novos valores e a pol�tica 
	 *   com os valores antigos � desativada. </p>
	 *     <p>Remover � quando o usu�rio coloca os valores de uma pol�tica para zero. O sistema desativa as
	 *   antiga e n�o cria nenhuma nova.</p>
	 *
	 * <br/>
	 * Chamado a partir da p�gina: /sigaa.war/biblioteca/PoliticaEmprestimo/form.jsp
	 *
	 * @return
	 * @throws ArqException 
	 */
	public String iniciaAlteracaoPoliticas() {
		cancelado = false;
		return telaListaPoliticas();
	}
	
	
	
	/**
	 * Chamado quando o usu�rio escolheu a biblioteca para mostrar os tipos de usu�rios da biblioteca
	 *
	 * Chamado a partir da p�gina: /sigaa.war/biblioteca/PoliticaEmprestimo/lista.jsp
	 * @param e
	 */
	public void escolheuBibliotecaDasPoliticas(ActionEvent e){
		if(bibliotecaDasPoliticas.getId() != -1)
			escolheuBiblioteca = true;
		else
			escolheuBiblioteca = false;
		
		escolheuTipoUsuario = false;
		valorVinculoSelecionado = null;
		vinculo = null;
	}
	
	
	
	/**
	 *  Chamado para o usu�rio escolher o tipo do usu�rio da biblioteca. Agora tem que carregar 
	 *  todas as pol�ticas de acordo com esse tipo. Se n�o existe no banco cria-se uma com o valor 
	 *  "0" para o prazo e quantidade.
	 *
	 * Chamado a partir da p�gina: /sigaa.war/biblioteca/PoliticaEmprestimo/lista.jsp
	 * @param e
	 * @throws ArqException 
	 */
	public void carregaPoliticasUsuario(ActionEvent e) throws ArqException{
		
		vinculo = VinculoUsuarioBiblioteca.getVinculo(valorVinculoSelecionado);
		
		if(vinculo == null || vinculo == VinculoUsuarioBiblioteca.INATIVO)
			escolheuTipoUsuario = false;
		else
			escolheuTipoUsuario = true;
		
		montaPoliticasParaUsuario();
	}
	
	
	
	/**
	 * <>Monta todas as pol�ticas poss�veis por tipo de usu�rio para serem mostradas na p�gina e o usu�rio
	 * poder adicionar valores a elas.
	 * 
	 */
	private void montaPoliticasParaUsuario() throws DAOException{
		
		PoliticaEmprestimoDao dao = null;
		
		
		try{
			dao = getDAO(PoliticaEmprestimoDao.class);
			
			// Busca todas cadastradas no banco
			politicasEmprestimo = dao.findPoliticasEmpretimoAtivasAlteraveisByBibliotecaEVinculo(bibliotecaDasPoliticas, vinculo);
			
			// ordena as pol�ticas pela ordem inversa das suas descricoes //
			Collections.sort(politicasEmprestimo, 
				new Comparator<PoliticaEmprestimo>(){
					public int compare(PoliticaEmprestimo o1, PoliticaEmprestimo o2) {
						return - o1.getTipoEmprestimo().getDescricao().compareTo(o2.getTipoEmprestimo().getDescricao());
					}	
				}
			
			);
			
			for (PoliticaEmprestimo politica : politicasEmprestimo) {
				
				// ordena os status da pol�tica  pela ordem inversa das suas descricoes
				if(politica.getStatusMateriais() != null){
					Collections.sort(politica.getStatusMateriais(), 
						new Comparator<StatusMaterialInformacional>(){
							public int compare(StatusMaterialInformacional s1, StatusMaterialInformacional s2) {
								return  - s1.getDescricao().compareTo(s2.getDescricao());
							}	
						}
					
					);
				}
				
				// ordena os tipos de materiais da pol�tica  pela ordem inversa das suas descricoes
				if(politica.getTiposMateriais() != null){
					Collections.sort(politica.getTiposMateriais(), 
						new Comparator<TipoMaterial>(){
							public int compare(TipoMaterial t1, TipoMaterial t2) {
								return  - t1.getDescricao().compareTo(t2.getDescricao());
							}	
						}
					
					);
				}
			}
			
			dataModelPoliticas.setWrappedData(politicasEmprestimo);
			
		}finally{
			if(dao != null) dao.close();
		}
		
	}
	
	
	
	
	////////////////////    A parte de criar uma nova pol�tica   ///////////////////////
	
	
	public void adicionarPoliticaVazia(ActionEvent evet) throws ArqException{
		PoliticaEmprestimo p = new PoliticaEmprestimo();
		
		if( ! escolheuTipoUsuario || ! escolheuBiblioteca ){
			addMensagemErro("Seleciona a biblioteca e v�nculo do usu�rio para poder criar uma nova pol�tica");
			return;
		}
		
		BibliotecaDao dao = null;
		try{
			dao = getDAO(BibliotecaDao.class);
			// A pol�tica vai ser criada para a biblioteca e vinculo do usu�rio escolhido //
			p.setBiblioteca(dao.findByPrimaryKey(bibliotecaDasPoliticas.getId(), Biblioteca.class, "id, descricao"));
			
		}finally{
			if(dao != null) dao.close();
		}
		
		p.setVinculo(vinculo);
		
		p.setQuantidadeMateriais(0);
		p.setPrazoEmprestimo(0);
		p.setQuantidadeRenovacoes(0);
		
		politicasEmprestimo.add(p);
		
		dataModelPoliticas.setWrappedData(politicasEmprestimo);
		
	}
	
	
	////////////////////////////////////////////////////////////////////////////////////
	
	
	
	
	
	
	
	
	
	/////////////////////////    A parte de altera��o   ////////////////////////////////
	
	/**
	 * Cadastra as pol�tica no banco.
	 *
	 * Chamado a partir da p�gina: /sigaa.war/biblioteca/PoliticaEmprestimo/lista.jsp
	 * @return
	 * @throws ArqException 
	 */
	public String gravarAlteracoesPoliticas() throws ArqException{
		
		checkRole(SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_LOCAL, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL);
		
		if (cancelado){ // Precisa dessa verifica��o porque a p�gina de listagem � a mesma p�gina de altera��o
			addMensagemErro("Esta opera��o foi cancelada. Reinicie o processo.");
			return forward("/biblioteca/index.jsp");
		}
		
		prepareMovimento(SigaaListaComando.CADASTRA_ALTERA_POLITICAS_EMPRESTIMOS);
		MovimentoCadastraPoliticaEmprestimo mov = new MovimentoCadastraPoliticaEmprestimo(politicasEmprestimo);
		mov.setCodMovimento(SigaaListaComando.CADASTRA_ALTERA_POLITICAS_EMPRESTIMOS);
		
		try {
			
			if (this.escolheuBiblioteca && this.escolheuTipoUsuario && politicasEmprestimo != null)
				execute(mov);
			else {
				addMensagemWarning("Selecione a biblioteca e o v�nculo do usu�rio da pol�tica.");
				return telaListaPoliticas();
			}
			
			montaPoliticasParaUsuario();
			
			addMensagemInformation("Pol�ticas atualizadas com sucesso.");
			
		} catch (NegocioException e) {
			e.printStackTrace();
			addMensagens(e.getListaMensagens());
		}
		
		
		return telaListaPoliticas();
	}
	
	
	///////////////////////////////////////////////////////////////////////
	
	
	
	
	
	
	////////////////////A parte de desativar uma nova pol�tica   ///////////////////////
	
	
	/**
	 * Remove a pol�tica selecionada pelo usu�rio da lista.
	 * 
	 * @param evet
	 * @throws ArqException
	 *  
	 *  <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s): 
	 * <ul>
	 * 		<li>/sigaa.war/biblioteca/PoliticaEmprestimo/lista.jsp</li>
	 * </ul>
	 *
	 */
	public void removerPoliticaSelecionada(ActionEvent evet) throws ArqException{
		
		int indeceSelecionado = dataModelPoliticas.getRowIndex();
	
		if(indeceSelecionado >= 0 && indeceSelecionado < politicasEmprestimo.size() ){
		
			politicasEmprestimo.remove(dataModelPoliticas.getRowIndex());
			dataModelPoliticas.setWrappedData(politicasEmprestimo);
		}
	}
	

	
	////////////////////////////////////////////////////////////////////////////////////
	
	
	
	/**
	 *  Verifica se o usu�rio tem permiss�o de altera��o da pol�tica de empr�stimo para exibir 
	 *  os dados habilitados ou n�o.
	 *
	 * Chamado a partir da p�gina:  /sigaa.war/biblioteca/PoliticaEmprestimo/lista.jsp
	 * @return
	 * @throws DAOException 
	 */
	public boolean getUsuarioTemPermissaoAlteracao() throws DAOException{
		
		if (isUserInRole(SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL))
			return true;


		if (isUserInRole(SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_LOCAL)){
			
			
			if (politicasEmprestimo != null && politicasEmprestimo.size() > 0){
				// S� precisa verificar a primeira pol�tica porque s�o sempre mostradas as pol�ticas da mesma biblioteca
				PoliticaEmprestimo politica = politicasEmprestimo.get(0);        
				
				try{
					if(politica.getBiblioteca().getUnidade() != null){
						// VERIFICA SE O USU�RIO TEM PERMISS�O DE ADMINISTRADOR DA BIBLIOTECA DA POL�TICA
						checkRole(politica.getBiblioteca().getUnidade(), SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_LOCAL);	
						return true;
					}
					
					return false;
				}catch (SegurancaException se) {
					return false;
				}
				
			}
		}

		return false;
	}
	
	/**
	 * 
	 * @return A quantidade de pol�ticas encontradas.
	 *  
	 *  <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s): 
	 * <ul>
	 * 		<li>/sigaa.war/biblioteca/PoliticaEmprestimo/lista.jsp</li>
	 * </ul>
	 *
	 */
	public int getQuantidadePoliticasEmprestimo(){
		if(politicasEmprestimo == null) return 0;
		return politicasEmprestimo.size();
	}
	
	
	
	//////////////////////////////////////////////////////////
	
	
	
	
	/////////////    M�todos dos Model Panel   /////////////////
	
	
	/**
	 ** <p>M�todo que carrega os status de materiais que podem ser emprestados, no caso todos os ativos e que permiteEmprestimo = true.</p>
	 *  
	 * <p>S�o os status que podem ser associados � pol�ticas de empr�stimo.</p>
	 * 
	 *  
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *      <li>/sigaa.war/biblioteca/PoliticaEmprestimo/lista.jsp</li>
	 *   </ul>
	 *
	 * @param event
	 * @throws DAOException
	 */
	public void carregaStatusEmprestaveis(ActionEvent event) throws DAOException{
		
	
		
		StatusMaterialInformacionalDao dao = null;
		
		try{
			dao = getDAO(StatusMaterialInformacionalDao.class);
			// Aqui n�o h� problema em recuperar todos sem proje��o, s�o poucos dados e n�o tem rela��o 
			// com outros objetos, os outros objetos que tem rela��o com tipo de material 
			statusQuePodemSerEmprestados = dao.findStatusMaterialAtivosPermiteEmprestimo();
			
		} finally{
			if(dao != null) dao.close();
		}
		
		// Selecina os status que j� ext�o na politica //
		
		obj = (PoliticaEmprestimo) dataModelPoliticas.getRowData();
		
		List<Integer> idsStatusPolitica = new ArrayList<Integer>();
		
				
		if(obj.getStatusMateriais() != null)
		for (StatusMaterialInformacional statusNaPolitica : obj.getStatusMateriais()) {
			idsStatusPolitica.add(statusNaPolitica.getId());
		}
		
		
		for (StatusMaterialInformacional statusExistentes : statusQuePodemSerEmprestados) {
			if(idsStatusPolitica.contains(statusExistentes.getId()))
				statusExistentes.setSelecionado(true);
		} 
	}
	
	
	
	/**
	 * <p>M�todo que carrega os tipo de materiais que podem ser emprestados, no caso todos os ativos.</p>
	 *  
	 * <p>S�o os tipos de materiais que podem ser associados � pol�ticas de empr�stimo.</p>
	 * 
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *      <li>/sigaa.war/biblioteca/PoliticaEmprestimo/lista.jsp</li>
	 *   </ul>
	 *
	 * @param event
	 * @throws DAOException
	 */
	public void carregaTipoMateriaisEmprestaveis(ActionEvent event) throws DAOException{
		
		TipoMaterialDAO dao = null;
		
		
		try{
			dao = getDAO(TipoMaterialDAO.class);
			// Aqui n�o h� problema em recuperar todos sem proje��o, s�o poucos dados e n�o tem rela��o 
			// com outros objetos, os outros objetos que tem rela��o com tipo de material 
			tiposMateriaisQuePodemSerEmprestados = (List<TipoMaterial>) dao.findAllAtivos(TipoMaterial.class, "descricao");
			
		} finally{
			if(dao != null) dao.close();
		}
		
		
		// Selecina os tipos de materiais que j� ext�o na politica //
		obj = (PoliticaEmprestimo) dataModelPoliticas.getRowData();
		
		List<Integer> idsTipoMaterialPolitica = new ArrayList<Integer>();
		
		
		if(obj.getTiposMateriais() != null)
		for (TipoMaterial tipoDaPolitica : obj.getTiposMateriais() ) {
			idsTipoMaterialPolitica.add(tipoDaPolitica.getId());
		}		
				
		
		for (TipoMaterial tipoMaterialExistentes : tiposMateriaisQuePodemSerEmprestados) {
			if(idsTipoMaterialPolitica.contains(tipoMaterialExistentes.getId()))
				tipoMaterialExistentes.setSelecionado(true);
		} 
		
	}
	
	
	/**
	 * <p>M�todo que carrega os tipo de materiais que podem ser emprestados, no caso todos os ativos.</p>
	 *  
	 * <p>S�o os tipos de materiais que podem ser associados � pol�ticas de empr�stimo.</p>
	 * 
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *      <li>/sigaa.war/biblioteca/PoliticaEmprestimo/lista.jsp</li>
	 *   </ul>
	 *
	 * @param event
	 * @throws DAOException
	 */
	public void carregaTipoEmprestimosQuePodemSerAssociadosAPoliticas(ActionEvent event) throws DAOException{
			
		TipoEmprestimoDao dao = null;
		try{
			dao = getDAO(TipoEmprestimoDao.class);
			tiposEmprestimosAtivosPodemSerAssociadosAPoliticas = dao.findTipoEmprestimosAtivosComPoliticaDeEmprestimo(false, false);
		}finally{
			if(dao != null) dao.close();
		}
		
		
		// Marca os tipos de emprestimos que j� est� na pol�tica //
	
		obj = (PoliticaEmprestimo) dataModelPoliticas.getRowData();
		
		// percorre as pol�ticas

		if(obj.getTipoEmprestimo() != null)
		for (TipoEmprestimo tipoEmprestimo : tiposEmprestimosAtivosPodemSerAssociadosAPoliticas ) {
			
			if(tipoEmprestimo.getId() == obj.getTipoEmprestimo().getId()){
				tipoEmprestimo.setSelecionado(true);
				break; // uma pol�tica s� pode ter 1
			}
		}
		
			
		
		
		
	}
	
	
	/**
	 * 
	 *  <p>Esse m�todo � chamado de dentro do model panel de status de materiais da pol�tica.</p>
	 * 
	 * @param event
	 *  
	 *  <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s): 
	 * <ul>
	 * 		<li>/sigaa.war/biblioteca/PoliticaEmprestimo/lista.jsp</li>
	 * </ul>
	 *
	 */
	public void atualizarStatusMateriaisPolitica(ActionEvent event){
	
		// obj cont�m a pol�tica selecionada pelo usu�rio.
		obj.setStatusMateriais(null);
		
		for (StatusMaterialInformacional statusMaterial : statusQuePodemSerEmprestados) {
			if(statusMaterial.isSelecionado() ){
				obj.adicionaStatusMaterial(statusMaterial);
			}
		} 
				
		
	}
	
	
	/**
	 * <p>Atualiza na pol�tica a lista de tipos de materiais selecionado pelo usu�rio.</p>
	 * 
	 * <p>Esse m�todo � chamado de dentro do model panel de tipos de materiais da pol�tica.</p>
	 * 
	 * @param event
	 *  
	 *  <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s): 
	 * <ul>
	 * 		<li>/sigaa.war/biblioteca/PoliticaEmprestimo/lista.jsp</li>
	 * </ul>
	 *
	 */
	public void atualizarTiposMateriaisPolitica(ActionEvent event){
		
		// obj cont�m a pol�tica selecionada pelo usu�rio.
		obj.setTiposMateriais(null);
		
		for (TipoMaterial tipoMaterial : tiposMateriaisQuePodemSerEmprestados) {
			if(tipoMaterial.isSelecionado() ){
				obj.adicionaTipoMaterial(tipoMaterial);
			}
		} 
		
	}
	
	
	/**
	 * <p>Atualiza na pol�tica a lista de tipos de materiais selecionado pelo usu�rio.</p>
	 * 
	 * <p>Esse m�todo � chamado de dentro do model panel de tipos de materiais da pol�tica.</p>
	 * 
	 * @param event
	 *  
	 *  <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s): 
	 * <ul>
	 * 		<li>/sigaa.war/biblioteca/PoliticaEmprestimo/lista.jsp</li>
	 * </ul>
	 *
	 */
	public void atualizarTipoEmprestimoPolitica(ActionEvent event){
		
		// obj cont�m a pol�tica selecionada pelo usu�rio.
		obj.setTipoEmprestimo(null);
		
		for (TipoEmprestimo tipoEmprestimo : tiposEmprestimosAtivosPodemSerAssociadosAPoliticas) {
			if(tipoEmprestimo.getId() == idTipoEmprestimoSelecionadoNovasPoliticas ){
				obj.setTipoEmprestimo(tipoEmprestimo);
				break; // s� pode ter um tipo de empr�stimo a pol�tica.
			}
		} 
		
	}
	
	
	////////////////////////////////////////////////////////////////////////////////////
	
	
	
	
	
	///////////// M�todos de combo box ///////////////////////
	
	/** 
	 * Retorna todos as bibliotecas ativas no sistema
	 *
	 * @return
	 * @throws DAOException
	 */
	public Collection<SelectItem> getBibliotecasAtivas() throws DAOException{
		
		
		
		Collection<Biblioteca> bs = new ArrayList<Biblioteca>();
		
		if(isSistemaPermiteConfigurarPoliticasDiferentePorBiblioteca()){ // v�o poder ser configuradas um pol�tica diferente para cada biblioteca
			
			BibliotecaDao dao = null;
			try{
				dao = getDAO(BibliotecaDao.class);
				bs = dao.findAllBibliotecasInternasAtivas();
				
			}finally{
				if(dao != null) dao.close();
			}
			
		}else{
			Biblioteca central = BibliotecaUtil.getBibliotecaCentral();
			bs.add( central );
		}
		
		return toSelectItems(bs, "id", "descricao");
	}

	
	/**
	 * Retorna se o sistema permite configurar pol�ticas diferentes para cada biblioteca do sistema.
	 * 
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *      <li>/sigaa.war/biblioteca/circulacao/paginaMostraInformacoesPoliticaEmprestimo.jsp</li>
	 *      <li>/sigaa.war/biblioteca/PolticaEmprestimo/form.jsp</li>
	 *   </ul> 
	 * 
	 */
	public boolean isSistemaPermiteConfigurarPoliticasDiferentePorBiblioteca(){
		return ParametroHelper.getInstance().getParametroBoolean(ParametrosBiblioteca.SISTEMA_PERMITE_CONFIGURAR_POLITICAS_DIFERENTES_POR_BIBLIOTECA);
	}
	
	
	/**
	 * Retorna todos os tipos de usu�rios ativos no sistema
	 *
	 * @return
	 * @throws DAOException
	 */
	public Collection <SelectItem> getVinculosUsuarioBiblioteca(){
		Collection<VinculoUsuarioBiblioteca> vinculos = Arrays.asList( VinculoUsuarioBiblioteca.getVinculosPodeRealizarEmprestimos());	
		return toSelectItems(vinculos, "valor", "descricao");
	}
	
	

	/**
	 * Retorna os tipos de Prazo do empr�stimos. 
	 *
	 * @return
	 * @throws DAOException
	 */
	public Collection <SelectItem> getTiposPrazo(){
		Collection<SelectItem> si = new ArrayList<SelectItem>();
		si.add(new SelectItem(PoliticaEmprestimo.TIPO_PRAZO_EM_DIAS, "DIAS"));
		si.add(new SelectItem(PoliticaEmprestimo.TIPO_PRAZO_EM_HORAS, "HORAS"));
		return si;
	}
	
	
	//////////////////// p�ginas de navega��o ////////////////////////
	
	/**
	 * Redireciona para a tela que altera os valores das pol�ticas.
	 * <br/>
	 *    <p>M�todo n�o chamado por nenhuma p�gina jsp.</p>
	 */
	public String telaListaPoliticas(){
		return forward(PAGINA_LISTA_POLITICAS_EMPRESTIMO);
	}
	
	
	/**
	 * Redireciona para a tela que altera os valores das pol�ticas.
	 * <br/>
	 *    <p>M�todo n�o chamado por nenhuma p�gina jsp.</p>
	 */
	public String telaCriaPoliticas(){
		return forward(PAGINA_CRIAR_NOVAS_POLITICAS);
	}
	
	
	/**
	 * Redireciona para a tela que altera os valores das pol�ticas.
	 * <br/>
	 *    <p>M�todo n�o chamado por nenhuma p�gina jsp.</p>
	 */
	public String telaConfirmaRemocaoPoliticas(){
		return forward(PAGINA_CONFIRMA_REMOCAO_POLITICAS);
	}
	
	
	///////// Set's e get's  /////////////
	
	public Biblioteca getBibliotecaDasPoliticas() {
		return bibliotecaDasPoliticas;
	}


	public void setBibliotecaDasPoliticas(Biblioteca bibliotecaDasPoliticas) {
		this.bibliotecaDasPoliticas = bibliotecaDasPoliticas;
	}


	public VinculoUsuarioBiblioteca getVinculo() {
		return vinculo;
	}

	public void setVinculo(VinculoUsuarioBiblioteca vinculo) {
		this.vinculo = vinculo;
	}

	public boolean isEscolheuBiblioteca() {
		return escolheuBiblioteca;
	}

	public boolean isEscolheuTipoUsuario() {
		return escolheuTipoUsuario;
	}

	public void setEscolheuBiblioteca(boolean escolheuBiblioteca) {
		this.escolheuBiblioteca = escolheuBiblioteca;
	}

	public void setEscolheuTipoUsuario(boolean escolheuTipoUsuario) {
		this.escolheuTipoUsuario = escolheuTipoUsuario;
	}

	public List<PoliticaEmprestimo> getPoliticasEmprestimo() {
		if(politicasEmprestimo == null)
			politicasEmprestimo = new ArrayList<PoliticaEmprestimo>();
		return politicasEmprestimo;
	}

	public void setPoliticasEmprestimo(List<PoliticaEmprestimo> politicasEmprestimo) {
		this.politicasEmprestimo = politicasEmprestimo;
	}

	public boolean isCancelado() {
		return cancelado;
	}

	public void setCancelado(boolean cancelado) {
		this.cancelado = cancelado;
	}

	public Integer getValorVinculoSelecionado() {
		return valorVinculoSelecionado;
	}

	public void setValorVinculoSelecionado(Integer valorVinculoSelecionado) {
		this.valorVinculoSelecionado = valorVinculoSelecionado;
	}

	public List<StatusMaterialInformacional> getStatusQuePodemSerEmprestados() {
		return statusQuePodemSerEmprestados;
	}

	public void setStatusQuePodemSerEmprestados(List<StatusMaterialInformacional> statusQuePodemSerEmprestados) {
		this.statusQuePodemSerEmprestados = statusQuePodemSerEmprestados;
	}

	public List<TipoMaterial> getTiposMateriaisQuePodemSerEmprestados() {
		return tiposMateriaisQuePodemSerEmprestados;
	}

	public void setTiposMateriaisQuePodemSerEmprestados(List<TipoMaterial> tiposMateriaisQuePodemSerEmprestados) {
		this.tiposMateriaisQuePodemSerEmprestados = tiposMateriaisQuePodemSerEmprestados;
	}

	public DataModel getDataModelPoliticas() {
		return dataModelPoliticas;
	}

	public void setDataModelPoliticas(DataModel dataModelPoliticas) {
		this.dataModelPoliticas = dataModelPoliticas;
	}

	public List<TipoEmprestimo> getTiposEmprestimosAtivosPodemSerAssociadosAPoliticas() {
		return tiposEmprestimosAtivosPodemSerAssociadosAPoliticas;
	}

	public void setTiposEmprestimosAtivosPodemSerAssociadosAPoliticas(List<TipoEmprestimo> tiposEmprestimosAtivosPodemSerAssociadosAPoliticas) {
		this.tiposEmprestimosAtivosPodemSerAssociadosAPoliticas = tiposEmprestimosAtivosPodemSerAssociadosAPoliticas;
	}

	public int getIdTipoEmprestimoSelecionadoNovasPoliticas() {
		return idTipoEmprestimoSelecionadoNovasPoliticas;
	}

	public void setIdTipoEmprestimoSelecionadoNovasPoliticas(int idTipoEmprestimoSelecionadoNovasPoliticas) {
		this.idTipoEmprestimoSelecionadoNovasPoliticas = idTipoEmprestimoSelecionadoNovasPoliticas;
	}
	
	
	
}