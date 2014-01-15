/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Created on 16/09/2008
 *
 */
package br.ufrn.sigaa.biblioteca.jsf;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.dao.biblioteca.EtiquetaDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.biblioteca.negocio.MovimentoCadastraAtualizaEtiquetasLocais;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.CampoDados;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.DescritorSubCampo;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Etiqueta;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.ValorIndicador;
import br.ufrn.sigaa.biblioteca.util.EtiquetasByTagTipoComparator;


/**
 *
 *   <p> MBean para o cadastro de uma etiqueta local. Somente etiquetas locais podem ser cadastradas.  </p>
 *   <p>  <i>( As outras fazem parte do padr�o MARC e suas informa��es n�o devem ser alteradas.)</i>  </p>
 *
 *   <p> <i><strong> Foi criado para o administrados do sistema, o caso de uso expecial de editar as informa��es dos campos MARC <br/>
 *   n�o locais, porque estava tendo muitos de erros principalmente na base de autoridades que n�o foi poss�vel cri�-la completa <br/>
 *   no sistema e estava ficando invi�vel alter isso no banco.
 *   </strong></i>
 *   </p>
 *
 * @author Gleydson
 * @author Jadson
 * @since 04/08/2008
 * @version
 *
 */
@Component(value="etiquetaMBean")
@Scope(value="request")
public class EtiquetaMBean extends SigaaAbstractController<Etiqueta> {

	public static final String PAGINA_FORMULARIO = "/biblioteca/Etiqueta/form.jsp";
	public static final String PAGINA_LISTAGEM = "/biblioteca/Etiqueta/lista.jsp";
	
	/** Guarda a lista de etiqueta locais ativas cadastradas no sistema. */
	private List<Etiqueta> listaEtiquetas; // Etiquetas mostras para o usu�rio 
	
	
	/** Data model para percorrer a lista de descritores e valores dos descritores de uma etiqueta na p�gina de cadastro*/
	private DataModel dataModelDescritores;
	private DataModel dataModelValores;
	
	/** As opera��es poss�vel de serem realizadas */
	public static final int CRIANDO = 1;
	public static final int ATUALIZANDO = 2;
	
	/** Qual opera��o o usu�rio escolheu */
	private int operacao;
	
	/* O administrados geral do sistema de biblitoecas vai poder altera todas as etiquetas MARC cadastradas no sistema.
	 * inclusive aquelas que foram cadastrada como n�o alter�vel
	 */
	private boolean permissaoAlteracaoTotal = false; 
	
	
	
	public EtiquetaMBean() {
		
	}
	
	/**
	 *   Encaminha para a p�gina que cria uma nova etiqueta
	 * 
	 *   Chamado a partir da p�gina: /sigaa.war/biblioteca/Etiqueta/lista.jsp
	 *  
	 * @return
	 * @throws ArqException 
	 */
	public String novaEtiqueta() throws ArqException{
		
		checkRole(SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_SISTEMA);
		
		prepareMovimento(SigaaListaComando.CADATRA_ATUALIZA_ETIQUETAS_LOCAIS);
		
		operacao = CRIANDO;
		
		obj = new Etiqueta();
		obj.setTipoCampo(CampoDados.IDENTIFICADOR_CAMPO);
		
		return forward(getFormPage());
	}
	
	
	/**
	 * M�todo que cadastra ou altera um objeto
	 * 
	 * Chamado a partir da p�gina: /sigaa.war/biblioteca/Etiqueta/form.jsp
	 * 
	 * @throws NegocioException 
	 */
	@Override
	public String cadastrar() throws ArqException {
		
		checkRole(SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_SISTEMA);
		
		EtiquetaDao dao = null;
		
		
		try {
			
			dao = getDAO(EtiquetaDao.class);
			
			MovimentoCadastraAtualizaEtiquetasLocais mov = new MovimentoCadastraAtualizaEtiquetasLocais(obj, false, ! permissaoAlteracaoTotal);
			
			mov.setCodMovimento(SigaaListaComando.CADATRA_ATUALIZA_ETIQUETAS_LOCAIS);
			execute(mov);
			
			addMensagemInformation("Campo \""+obj.getTag()+"\" da base "+(obj.isEtiquetaBibliografica() ? "Bibliogr�fica": "de Autoridades" )+" cadastrado com Sucesso");
			
			afterCadastrarAtualizar();
			
			if(permissaoAlteracaoTotal){
				listaEtiquetas = dao.findAllEtiquetasAtivas();
				Collections.sort(listaEtiquetas, new EtiquetasByTagTipoComparator());
			}else{
				listaEtiquetas = dao.findAllEtiquetasLocaisAtivas();
				Collections.sort(listaEtiquetas, new EtiquetasByTagTipoComparator());
			}
			
			return forward(getListPage());
			
		} catch (NegocioException ne) {
			addMensagens(ne.getListaMensagens());
			return forward(getFormPage());
		}finally{
			if(dao != null) dao.close();
			
		}
		
	}
	
	/**
	 * Depois de cadastrar zera o objeto para poder cadastrar novamente
	 * 
	 */
	private void afterCadastrarAtualizar(){
		obj = new Etiqueta();
		obj.setTipoCampo(CampoDados.IDENTIFICADOR_CAMPO);
		
	}
	
	/**
	 * M�todo que evita NullPointerException quando o usu�rio tenta
	 * alterar um objeto que j� foi removido.
	 * 
	 * M�todo n�o chamado por nenhuma jsp.
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String preAtualizar () throws ArqException {
		
		checkRole(SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_SISTEMA);
		
		operacao = ATUALIZANDO;

		EtiquetaDao dao = null;
		
		try{
		
			dao = getDAO(EtiquetaDao.class);
				
			obj = dao.findDadosCompletosEtiquetaParaValidacao(new Etiqueta(getParameterInt("idEtiqueta")));
	
			if(! obj.isAtiva()){
				addMensagemErro("Campo \""+obj.getTag()+"\" da base "+(obj.isEtiquetaBibliografica() ? "Bibliogr�fica": "de Autoridades" )+" n�o est� mais ativo no sistema.");
				return null;
			}
			
			if(obj.isEtiquetaControle()){
				addMensagemErro("Campos de controle n�o podem ser alterados por esse caso de uso.");
				return null;
			}
			
			// IMPORTANTE:  Inicializa o valores da lista com o do set para poder interar na p�gina //
			obj.iniciaListaDescritoresSubCampo();
			obj.iniciaListaValoresIndicador();
			
			Collections.sort(obj.getDescritorSubCampoList(), new Comparator<DescritorSubCampo>(){
				public int compare(DescritorSubCampo o1, DescritorSubCampo o2) {
					return o1.getCodigo().compareTo(o2.getCodigo());
				}
			});
			
			Collections.sort(obj.getValoresIndicadorList(), new Comparator<ValorIndicador>(){
				public int compare(ValorIndicador o1, ValorIndicador o2) {
					int resultado = new Short(o1.getNumeroIndicador()).compareTo(new Short(o2.getNumeroIndicador()));
					
					if(resultado == 0 && o1.getValor() != null && o2.getValor() != null)
						return o1.getValor().compareTo(o2.getValor());
					else
						return resultado;
					
				}
			});
			
			dataModelDescritores = new ListDataModel(obj.getDescritorSubCampoList());
			dataModelValores = new ListDataModel(obj.getValoresIndicadorList());
			
			prepareMovimento(SigaaListaComando.CADATRA_ATUALIZA_ETIQUETAS_LOCAIS);

		}finally{
			if(dao != null) dao.close();
		}
		
		return forward(getFormPage());
	}
	
	
	
	/**
	 *    Atualiza os dados da etiqueta. O m�todo remover tamb�m chama esse m�todo passando para 
	 * desativar essa etiqueta.
	 * 
	 * Chamado a partir da p�gina: /sigaa.war/biblioteca/Etiqueta/form.jsp
	 * 
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#atualizar()
	 */
	@Override
	public String atualizar() throws ArqException {
		
		checkRole(SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_SISTEMA);
		
		EtiquetaDao dao = null;
		
		try{
		
			dao = getDAO(EtiquetaDao.class);
			
			MovimentoCadastraAtualizaEtiquetasLocais mov = new MovimentoCadastraAtualizaEtiquetasLocais(obj, true,  ! permissaoAlteracaoTotal);
			
			if(obj.isAtiva()){ // Est� atualizando
				mov = new MovimentoCadastraAtualizaEtiquetasLocais(obj, true, ! permissaoAlteracaoTotal);
			}else{
				mov = new MovimentoCadastraAtualizaEtiquetasLocais(true, obj);
			}
			
			mov.setCodMovimento(SigaaListaComando.CADATRA_ATUALIZA_ETIQUETAS_LOCAIS);
			execute(mov);
			
			if(obj.isAtiva())
				addMensagemInformation("Campo \""+obj.getTag()+"\" da base "+(obj.isEtiquetaBibliografica() ? "Bibliogr�fica": "de Autoridades" )+" atualizado com Sucesso");
			else
				addMensagemInformation("Campo \""+obj.getTag()+"\" da base "+(obj.isEtiquetaBibliografica() ? "Bibliogr�fica": "de Autoridades" )+" removido com Sucesso");
			
			
			if(permissaoAlteracaoTotal){
				listaEtiquetas = dao.findAllEtiquetasAtivas();
				Collections.sort(listaEtiquetas, new EtiquetasByTagTipoComparator());
			}else{
				listaEtiquetas = dao.findAllEtiquetasLocaisAtivas();
				Collections.sort(listaEtiquetas, new EtiquetasByTagTipoComparator());
			}
			
			afterCadastrarAtualizar();
			
		} catch (NegocioException ne) {
			addMensagens(ne.getListaMensagens());
			return forward(getFormPage());
			
		}finally{
			
			if(dao != null) dao.close();
			
		}
	
		return forward(getListPage());
	}

	
	/**
	 * M�todo que remove o objeto, verificando se o mesmo existe.
	 * 
	 * Chamado a partir da p�gina: /sigaa.war/biblioteca/Etiqueta/lista.jsp
	 * 
	 */
	@Override
	public String remover() throws ArqException {
		
		checkRole(SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_SISTEMA);
		
		GenericDAO dao = getGenericDAO();
		
		obj = dao.findByPrimaryKey(getParameterInt("idEtiquetaRemocao"), Etiqueta.class); 
		
		// Se o objeto a remover foi encontrado, desativa
		if (obj != null && obj.isAtiva()){
			prepareMovimento(SigaaListaComando.CADATRA_ATUALIZA_ETIQUETAS_LOCAIS);

			obj.setAtiva(false);
			
			this.atualizar();
			
		} else
			addMensagemErro("Este objeto j� foi removido");
		
		dao.close();
		
		return forward(getListPage());
	}
	
	
	/**
	 * Caso de uso padr�o que busca apenas as etiquetas locais e redireciona o usu�rio para a p�gina de listagem de etiquetas MARC do sistema.
	 * 
	 * <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/menus/cadastro.jsp</li>
	 *   </ul>
	 * 
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#listar()
	 */
	@Override
	public String listar() throws ArqException {
		
		EtiquetaDao dao = getDAO(EtiquetaDao.class);
		
		listaEtiquetas = dao.findAllEtiquetasLocaisAtivas();
		
		Collections.sort(listaEtiquetas, new EtiquetasByTagTipoComparator());
		
		dao.close();
		
		permissaoAlteracaoTotal = false;
		
		return forward(getListPage());
	}

	/**
	 * 
	 *  <p>Caso de uso que apenas o administrador do sistema vai poder usar. </p>
	 *  <p>Esse caso de uso permite altera as informa��es de qualquer campo MARC do sistema. </p>
	 *  <p>Idealmente esse caso de uso n�o precisaria existir, porque todos os dados deveriam est� no sistema, mas como a base 
	 *  de autoridades possui uma quantidade de dados incompletos muito grande estava ficando invi�vel alterar isso no banco.</p>
	 *  
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/menus/cadastro.jsp</li>
	 *   </ul>
	 *
	 * @return
	 * @throws DAOException
	 * @throws SegurancaException 
	 */
	public String listarAllEtiquetasMARC() throws DAOException, SegurancaException{

		checkRole(SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_SISTEMA);
		
		EtiquetaDao dao = getDAO(EtiquetaDao.class);
		
		listaEtiquetas = dao.findAllEtiquetasAtivas();
		
		Collections.sort(listaEtiquetas, new EtiquetasByTagTipoComparator());
		
		dao.close();
		
		permissaoAlteracaoTotal = true;
		
		return forward(getListPage());
	}
	
	
	
	/**
	 * Digita o novo sub campo. Usado para validar e mostrar a ajuda do campo local
	 *
	 * Chamado a partir da p�gina: /sigaa.war/biblioteca/Etiqueta/form.jsp
	 * @param event
	 */
	public void novoDescritorSubCampo(ActionEvent event){
		this.obj.addDescritorSubCampoList(new DescritorSubCampo(obj));
		
		dataModelDescritores = new ListDataModel(obj.getDescritorSubCampoList());
	}

	
	/**
	 * Removedor do descritor do sub campos. S� permitido se n�o foi salvo ainda.
	 *
	 * Chamado a partir da p�gina: /sigaa.war/biblioteca/Etiqueta/form.jsp
	 * @param event
	 */
	public void removerDescritorSubCampo(ActionEvent event){
		if(obj.getDescritorSubCampo() != null && obj.getDescritorSubCampo().size() > 0) // s� tem dados quando est� alterando a etiqueta 
			obj.getDescritorSubCampo().remove(dataModelDescritores.getRowData()); 
		obj.getDescritorSubCampoList().remove(dataModelDescritores.getRowIndex()); 
	}

	
	/**
	 * Digita o novo valor indicado do campo. Usado para validar e mostrar a ajuda do campo local
	 *
	 * Chamado a partir da p�gina: /sigaa.war/biblioteca/Etiqueta/form.jsp
	 * @param event
	 */
	public void  novoValorIndicador (ActionEvent event){
		this.obj.addValorIndicadorList(new ValorIndicador(obj));
		
		dataModelValores = new ListDataModel(obj.getValoresIndicadorList());
	}
	
	
	/**
	 * Removo o valor adicionado. S� permitido se n�o foi salvo ainda.
	 *
	 * Chamado a partir da p�gina: /sigaa.war/biblioteca/Etiqueta/form.jsp
	 * @param event
	 */
	public void removerValorIndicador(ActionEvent event){
		if(obj.getValoresIndicador()  != null && obj.getValoresIndicador().size() > 0 ) // s� tem dados quando est� alterando a etiqueta 
			obj.getValoresIndicador().remove(dataModelValores.getRowData());
		
		obj.getValoresIndicadorList().remove(dataModelValores.getRowIndex()); 
	}
	
	
	/**
	 *   Apenas retorna para a p�gina de Listagem
	 *
	 * Chamado a partir da p�gina: /sigaa.war/biblioteca/Etiqueta/form.jsp
	 * @return
	 */
	public String voltarPaginaListagem(){
		return forward(getListPage());
	}
	
	
	/**
	 * 
	 *   M�todo que busca todas as etiquetas locais cadastradas no banco.
	 *   Usado na jsp /biblioteca/Etiqueta/lista.jsp
	 *
	 * @return
	 * @throws DAOException se deu algum erro na consulta
	 */
	public Collection<Etiqueta> getAllEtiquetas(){
		return listaEtiquetas;
	}
	
	// Tela de navega��o
	
	@Override
	public String getFormPage() {
		return PAGINA_FORMULARIO; 
	}

	@Override
	public String getListPage() {
		return PAGINA_LISTAGEM;
	}
	
	// Set e gets
	
	public boolean isAtualizando(){
		return operacao == ATUALIZANDO;
	}
	
	public boolean isCriando(){
		return operacao == CRIANDO;
	}

	public int getOperacao() {
		return operacao;
	}

	public void setOperacao(int operacao) {
		this.operacao = operacao;
	}

	public DataModel getDataModelDescritores() {
		return dataModelDescritores;
	}

	public void setDataModelDescritores(DataModel dataModelDescritores) {
		this.dataModelDescritores = dataModelDescritores;
	}

	public DataModel getDataModelValores() {
		return dataModelValores;
	}

	public void setDataModelValores(DataModel dataModelValores) {
		this.dataModelValores = dataModelValores;
	}

	public boolean isPermissaoAlteracaoTotal() {
		return permissaoAlteracaoTotal;
	}

	public void setPermissaoAlteracaoTotal(boolean permissaoAlteracaoTotal) {
		this.permissaoAlteracaoTotal = permissaoAlteracaoTotal;
	}
	
	
}
