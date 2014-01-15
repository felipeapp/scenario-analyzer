/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 15/02/2012
 * 
 */
package br.ufrn.sigaa.biblioteca.processos_tecnicos.jsf;

import java.util.Collection;
import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dao.AreaConhecimentoCNPqBibliotecaDao;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.InformacoesAreaCNPQBiblioteca;
import br.ufrn.sigaa.dominio.AreaConhecimentoCnpq;

/**
 * <p>Configura as informações das áreas CNPq para a biblioteca </p>
 * 
 * @author jadson
 *
 */
@Component("configuraInformacoesAreasCNPqBibliotecaMBean")
@Scope("request")
public class ConfiguraInformacoesAreasCNPqBibliotecaMBean  extends SigaaAbstractController<InformacoesAreaCNPQBiblioteca>{

	/** Página que lista e configura as inforamções das áreas CNPq utilizadas na biblioteca. */
	public final static String PAGINA_LISTA_INFORMACOES_AREA_CNPQ_BIBLIOTECA = "/biblioteca/classificacao_bibliografica_biblioteca/listaInformacoesAreasCNPQBiblioteca.jsp";
	
	/**Guardas as grandes áreas de conhecimento CNPq, cujos relacionamentos com as classificações da biblioteca serão alterados */
	private Collection<AreaConhecimentoCnpq> grandesAreas;
	
	/** Guarda a lista de associações que foram feitas */
	private List<InformacoesAreaCNPQBiblioteca> informacoesAreasBiblioteca;
	
	
	
	/**
	 * Inicia o caso de uso para configurar qual biblioteca vai utilizar qual classificação bibliográfica.
	 *  
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/menus/cadastros.jsp</li>
	 *   </ul>
	 *   
	 * @return
	 * @throws ArqException 
	 */
	public String iniciar() throws ArqException{
		
		AreaConhecimentoCNPqBibliotecaDao areaDao = null;
		
		try{
			areaDao = getDAO(AreaConhecimentoCNPqBibliotecaDao.class);
			
			grandesAreas = areaDao.findGrandeAreasConhecimentoCnpqComProjecao(new String[]{"id", "sigla", "nome"});
			informacoesAreasBiblioteca = areaDao.findAllInformacoesAreasCNPqBiblioteca();
			
			if(grandesAreas.size() == 0){
				addMensagemErro("Não existem Áreas CNPq configuradas no sistema.");
				return null;
			}
			
			montaDadosExibicaoUsuario();
		
		}finally{
			if(areaDao != null) areaDao.close();
		}
		
		prepareMovimento(SigaaListaComando.CADASTRA_INFORMACOES_AREAS_CNPQ_BIBLIOTECA);
		
		return telaListaBibliotecasESuasClassificacoes();
	}
	
	
	
	/**
	 * Monta os dados para exibição para o usuário.
	 *
	 */
	private void montaDadosExibicaoUsuario() {
			
	
		for (AreaConhecimentoCnpq area : grandesAreas) {
			
			InformacoesAreaCNPQBiblioteca infoTemp = new InformacoesAreaCNPQBiblioteca(area);
			
			if(! informacoesAreasBiblioteca.contains(infoTemp)){
				informacoesAreasBiblioteca.add(infoTemp);
			}
		}
	}

	/**
	 * Atualiza os relacionamentos entre biblioteca e classificação utilizada no banco.
	 *  
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/classificacao_bibliografica_biblioteca/listaInformacoesAreasCNPQBiblioteca.jsp</li>
	 *   </ul>
	 *
	 * @return
	 * @throws ArqException 
	 */
	public String atualizarInformacoesArea() throws ArqException{
		
		MovimentoCadastro mov = new MovimentoCadastro();
		mov.setCodMovimento(SigaaListaComando.CADASTRA_INFORMACOES_AREAS_CNPQ_BIBLIOTECA);
		mov.setColObjMovimentado(informacoesAreasBiblioteca);
		
		try{
			execute(mov);
			addMensagemInformation("Informações cadastradas com sucesso!");
		}catch (NegocioException ne) {
			addMensagens(ne.getListaMensagens());
			return null;
		}
		
		return iniciar();
	}
	

	
	/**
	 * Redireciona para a tela do caso de uso.
	 *
	 *  <p>Método não chamado por nenhuma página jsp.</p>
	 *
	 * @return
	 */
	public String telaListaBibliotecasESuasClassificacoes(){
		return forward(PAGINA_LISTA_INFORMACOES_AREA_CNPQ_BIBLIOTECA);
	}



	
	
	///// sets e gets /////
	
	
	public List<InformacoesAreaCNPQBiblioteca> getInformacoesAreasBiblioteca() {
		return informacoesAreasBiblioteca;
	}
	
	public void setInformacoesAreasBiblioteca(List<InformacoesAreaCNPQBiblioteca> informacoesAreasBiblioteca) {
		this.informacoesAreasBiblioteca = informacoesAreasBiblioteca;
	}
	
	
}
