/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 17/10/2011
 *
 */
package br.ufrn.sigaa.biblioteca.processos_tecnicos.jsf;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.faces.event.ActionEvent;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.biblioteca.jsf.PesquisaMateriaisInformacionaisMBean;
import br.ufrn.sigaa.biblioteca.jsf.PesquisarMateriaisInformacionais;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.MaterialInformacional;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.negocio.MovimentoAlterarMotivoBaixaVariosMateriais;

/**
 * MBean que gerencia a alteração do motivo de baixa de um ou vários materiais baixados.
 * 
 * @author felipe
 * @since 17/10/2011
 * @version 1.0 Criação da classe
 *
 */
@Component("alterarMotivoBaixaVariosMateriaisMBean")
@Scope("request")
public class AlterarMotivoBaixaVariosMateriaisMBean extends SigaaAbstractController<MaterialInformacional> implements PesquisarMateriaisInformacionais {

	/**  Página para onde o fluxo retorna ao final da operação */
	public static final String PAGINA_VOLTAR = "/biblioteca/index.jsp";

	/**  Página de busca e seleção dos materiais */
	public static final String PAGINA_BUSCA = "/biblioteca/pesquisaPadraoMaterialInformacional.jsp";
	
	/** Formulário para alterar o motivo da baixa */
	public static final String PAGINA_FORM = "/biblioteca/processos_tecnicos/outras_operacoes/paginaAlterarMotivoBaixaVariosMateriais.jsp";

	/** Página default do módulo da biblioteca. Chamada no final das operações. */
	public static final String PAGINA_INDEX = "/biblioteca/index.jsp";

	/** Se deve salvar as alteração e ficar na mesma página ou voltar a tela inicial da biblioteca.*/
	private String finalizarAlteracao;
	
	/**
	 * Materias selecionados para alteração do motivo de baixa
	 */
	private List<MaterialInformacional> materiaisSelecionados = new ArrayList<MaterialInformacional>();
	
	/**
	 * Materias selecionados para alteração do motivo de baixa
	 */
	private Set<String> titulosMateriais;
	
	public AlterarMotivoBaixaVariosMateriaisMBean() { 
		
	}
	
	/**
	 * 
	 * Método chamado para iniciar a pesquisa dos materiais para os quais se deseja alterar o motivo da baixa.
	 * <br/>
	 *  
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *    <li>/sigaa.war/biblioteca/menus/processos_tecnicos.jsp</li>
	 * </ul>
	 *
	 * @return
	 */
	public String iniciarBuscaMaterial() {
		String descricaoOperacao = " <p>Caro usuário,</p> " +
				" <p>Essa operação permite consertar erros ocorridos na digitação do motivo de baixa " +
				" ao realizar a baixa de um determinado material no acervo. </p> " +
				" <p> Por favor, busque os materiais para os quais se deseja alterar o motivo da baixa. </p> ";
		
		PesquisaMateriaisInformacionaisMBean pBean = getMBean ("pesquisaMateriaisInformacionaisMBean");
		return pBean.iniciarBusca(this, "Pesquisar Material para Alterar Motivo de Baixa", descricaoOperacao
				, "Alterar Motivo de Baixa", true);
	}

	@Override
	public void setMateriaisPesquisaPadraoMateriais(
			List<MaterialInformacional> materiais) throws ArqException {
		this.materiaisSelecionados = materiais;
	}

	@Override
	public String selecionouMateriaisPesquisaPadraoMateriais()
			throws ArqException {
		prepareMovimento(SigaaListaComando.ALTERAR_MOTIVO_BAIXA_VARIOS_MATERIAIS);	
		
		titulosMateriais = new HashSet<String>();
		
		for (MaterialInformacional mi : materiaisSelecionados) {
			titulosMateriais.add(mi.getInformacoesTituloMaterialBaixado());
		}
		
		return forward(PAGINA_FORM);
	}

	@Override
	public String voltarBuscaPesquisaPadraoMateriais() throws ArqException {
		return forward(PAGINA_INDEX);
	}
	
	/**
	 *   Método que copia o valor digitado no campo motivo de baixa para os demais abaixo que vêem 
	 *   depois dele na lista. <br/> 
	 *
	 *  Chamado a partir da página: /sigaa.war/biblioteca/processos_tecnicos/outras_operacoes/paginaAlterarMotivBaixaVariosMateriais.jsp
	 * @throws DAOException 
	 *
	 */
	public void copiaValorCamposAbaixo(ActionEvent evt) throws DAOException {
			
		int idMaterialOrigemDado = getParameterInt("idMaterialOrigemDado");
		
		boolean encontrouMaterial = false;
		MaterialInformacional materialSelecionado = null;
		
		int indexSelecionado = -1;
		
		for (int ptr = 0; ptr < materiaisSelecionados.size();  ptr ++) {
			
			if(encontrouMaterial == false){  // enquando não encontra o material na lista, não faz nada
				if(materiaisSelecionados.get(ptr).getId() == idMaterialOrigemDado){
					encontrouMaterial = true;
					materialSelecionado = materiaisSelecionados.get(ptr);
				}
				
				indexSelecionado ++;
				
			}else{  // quando encontrou
				if (materialSelecionado != null) {
					materiaisSelecionados.get(ptr).setMotivoBaixa(materialSelecionado.getMotivoBaixa());
				}
			}			
		}
		
	}
	
	/**
	 *    Realiza a ação de atualizar o motivo de baixa dos materiais que foram selecionados pelo usuário.
	 *
	 * Chamado a partir da página: /sigaa.war/biblioteca/processos_tecnicos/outras_operacoes/paginaAlterarMotivoBaixaVariosMateriais.jsp
	 *
	 * @return
	 * @throws ArqException 
	 */
	public String realizarAlteracaoMateriais() throws ArqException{
		
		checkRole(SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL, SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_GERENCIAR_MATERIAIS);
		
		GenericDAO dao = null;
		
		try{
		
			dao = getGenericDAO();
					
			try{
				
				MovimentoAlterarMotivoBaixaVariosMateriais mov = new MovimentoAlterarMotivoBaixaVariosMateriais(materiaisSelecionados);
				
				mov.setCodMovimento(SigaaListaComando.ALTERAR_MOTIVO_BAIXA_VARIOS_MATERIAIS);
				
				execute(mov);
					
				addMensagemInformation("Alteração realizada com sucesso.");
				
			}catch (NegocioException ne) {
				addMensagens(ne.getListaMensagens());
				ne.printStackTrace();
				return null; // continua na página
			}
			
			
			if("true".equalsIgnoreCase(finalizarAlteracao))
				return cancelar();  // volta tela inicial da biblioteca
			else{
				prepareMovimento(SigaaListaComando.ALTERAR_MOTIVO_BAIXA_VARIOS_MATERIAIS);
				return null; // continua na página
			}
		
		
		}finally{
			if(dao != null ) dao.close();
		}
		
	}

	/**
	 * Redireciona o fluxo para a página de seleção dos materiais.
	 * 
	 * Usado na JSP: /sigaa.war/biblioteca/processos_tecnicos/outras_operacoes/paginaAlterarMotivoBaixaVariosMateriais.jsp
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String telaPaginaBuscaMateriais() throws ArqException {
		return forward(PAGINA_BUSCA);
	}

	public List<MaterialInformacional> getMateriaisSelecionados() {
		return materiaisSelecionados;
	}

	public void setMateriaisSelecionados(
			List<MaterialInformacional> materiaisSelecionados) {
		this.materiaisSelecionados = materiaisSelecionados;
	}

	public Set<String> getTitulosMateriais() {
		return titulosMateriais;
	}

	public void setTitulosMateriais(
			Set<String> titulosMateriais) {
		this.titulosMateriais = titulosMateriais;
	}

	public String getFinalizarAlteracao() {
		return finalizarAlteracao;
	}

	public void setFinalizarAlteracao(String finalizarAlteracao) {
		this.finalizarAlteracao = finalizarAlteracao;
	}
	
}
