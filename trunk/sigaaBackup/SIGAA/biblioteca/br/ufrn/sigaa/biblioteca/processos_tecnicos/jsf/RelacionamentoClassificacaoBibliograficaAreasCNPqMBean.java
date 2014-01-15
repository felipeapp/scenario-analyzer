/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 21/09/2010
 * 
 */
package br.ufrn.sigaa.biblioteca.processos_tecnicos.jsf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dao.AreaConhecimentoCNPqBibliotecaDao;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dao.ClassificacaoBibliograficaDao;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.ClassificacaoBibliografica;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.RelacionaClassificacaoBibliograficaAreaCNPq;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.negocio.MovimentoAtualizaRelacaoClassificacaoAreasCNPq;
import br.ufrn.sigaa.dominio.AreaConhecimentoCnpq;

/**
 *
 * <p>Mbean responsável por realizar o relacionamento entre as classificações bibliográfica cadastradas no sistema e as grandes áreas de conhecimento do CNPq.</p>
 *
 * <p> <i> Esse relacionamento deve ser configurado para podemos saber a área CNPq de um Título a partir do sua classificação bibliográfica. </i> </p>
 * 
 * @author jadson
 *
 */
@Component("relacionamentoClassificacaoBibliograficaAreasCNPqMBean")
@Scope("request")
public class RelacionamentoClassificacaoBibliograficaAreasCNPqMBean extends SigaaAbstractController<RelacionaClassificacaoBibliograficaAreaCNPq> {

	/** A página onde o administrador do sistema altera as informações do relacionamento entre classificação CDU/Black e áreas CNPq*/
	public static final String PAGINA_LISTA_RELACIONAMENTO_CLASSIFICACOES_AREAS_CNPQ = "/biblioteca/classificacao_bibliografica_biblioteca/listaRelacionamentoClassificacoesBibliograficasAreasCNPQ.jsp";
	
	
	/**Guardas as grandes áreas de conhecimento CNPq, cujos relacionamentos com as classificações da biblioteca serão alterados */
	private Collection<AreaConhecimentoCnpq> grandesAreas;
	
	/** Guarda a lista de classificações utilizadas no sistema */
	private List<ClassificacaoBibliografica> classificacoesUtilizadas;
	
	/** Guarda a informação do id da classificação escolhida pelo usuário */
	private int idClassificacaoEscolhida;
	
	/** Guarda as informações da classificação escolhida pelo usuário */
	private ClassificacaoBibliografica classificacoEscolhida = new ClassificacaoBibliografica();
	
	/** Guarda as informações dos relacionamentos entre as grandes áreas de conhecimento do CNPq e a classificação selecionada pelo usuário */
	private List<RelacionaClassificacaoBibliograficaAreaCNPq> relacionamentosSalvos;

	/**
	 * Flag que habilita ou desabilita o botão atualizar as alterações do relacionamento. Para evitar que o 
	 * usuário clique em "atualizar relacionamento" antes de escolher corretamente os dados o atualize várias vezes a mesma coisa, 
	 * já que a página de listar e alterar é a mesma.
	 */
	private boolean botaoAtualizarHabilitado = false;
	
	
	/**
	 *   <p>Inicia o caso de uso de alteração dos relacionamento entre  áreas CNPq as classificações utilizadas na biblioteca (CDU, black, CDD) </p>
	 *
	 * <br/>
	 * Chamado a partir da página: /sigaa.war/biblioteca/menus/cadastro.jsp
	 *
	 * @return
	 * @throws ArqException 
	 * @throws ArqException 
	 */
	public String iniciar() throws ArqException {
		
		ClassificacaoBibliograficaDao dao = null;
		AreaConhecimentoCNPqBibliotecaDao areaDao = null;
		try{
			dao = getDAO(ClassificacaoBibliograficaDao.class);
			areaDao = getDAO(AreaConhecimentoCNPqBibliotecaDao.class);
			
			classificacoesUtilizadas = dao.findAllAtivos("ordem", "asc", new String[]{ "descricao", "id", "ordem"}  );
			grandesAreas =areaDao.findGrandesAreasCNPqBibliotecaComProjecao();
			
			if(classificacoesUtilizadas.size() == 0){
				addMensagemErro("Não existem classificações bibliográficas cadastradas no sistema.");
				return null;
			}
			
			botaoAtualizarHabilitado = false;
		
		}finally{
			if(dao != null) dao.close();
			if(areaDao != null) areaDao.close();
		}
		
		prepareMovimento(SigaaListaComando.ATUALIZA_RELACAO_CLASSIFICACAO_AREAS_CNPq);
		
		return telaListaRelacionamentosClassificacoesAresCNPQ();
	}
	
	
	
	
	
	/**
	 * Busca os relacionamento com as áreas do CNPq quando o usuário escolhe a classificação bibliográfica.
	 *  
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *      <li>/sigaa.war/biblioteca/classificacao_bibliografica_biblioteca/listaRelacionamentoClassificacoesBibliograficasAreasCNPQ.jsp</li>
	 *   </ul>
	 *
	 *
	 * @return
	 * @throws DAOException 
	 * @throws ArqException 
	 */
	public void alterouClassificacao(ActionEvent e) throws DAOException{
	
		ClassificacaoBibliograficaDao dao = null;
		
		relacionamentosSalvos = new ArrayList<RelacionaClassificacaoBibliograficaAreaCNPq>();
		classificacoEscolhida = new ClassificacaoBibliografica();
		
		if(idClassificacaoEscolhida == -1) {
			botaoAtualizarHabilitado = false;
			
			return;
		}
		
		for (ClassificacaoBibliografica classificacao : classificacoesUtilizadas) {
			if(classificacao.getId() == idClassificacaoEscolhida){
				classificacoEscolhida = classificacao;
				break;
			}
		}
		
		try{
			dao = getDAO(ClassificacaoBibliograficaDao.class);
		
			relacionamentosSalvos = dao.findAllRelacionamentosClassificacaoAreasCNPqByClassificacao(idClassificacaoEscolhida);
			
			montaDadosRelacionamento();
			
			botaoAtualizarHabilitado = true;
			
		}finally{
			if (dao != null) dao.close();
		}
		
	}
	
	
	
	/**
	 * Percorre a lista, se a lista não tiver alguma área para a classificação escolhida adiciona essa áreas
	 * 
	 * Esse caso só ocorre ao executar o caso de uso pela primeira vez quando os dados não existem no banco, ou caso seja criada uma nova grande área.
	 *
	 */
	private void montaDadosRelacionamento() {
		
		for (AreaConhecimentoCnpq area : grandesAreas) {
			
			RelacionaClassificacaoBibliograficaAreaCNPq temp 
				= new RelacionaClassificacaoBibliograficaAreaCNPq(new ClassificacaoBibliografica(idClassificacaoEscolhida), area);
			
			if ( ! relacionamentosSalvos.contains(temp)){
				relacionamentosSalvos.add(temp);
			}
			
		}
		
	}





	/**
	 *
	 * Método que atualiza o relacionamento das classificações utilizados nas biblioteca com as grandes áreas CNPq
	 * e ativa também a thread que vai ficar responsável por atualizar o cache das áreas CNPq no acervo.
	 *  
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/classificacao_bibliografica_biblioteca/listaRelacionamentoClassificacoesBibliograficasAreasCNPQ.jsp</li>
	 *   </ul>
	 *
	 * @return
	 * @throws ArqException 
	 * @throws  
	 */
	public String atualizarRelacionamento() throws ArqException{
		
		checkRole(SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL);
		
		MovimentoAtualizaRelacaoClassificacaoAreasCNPq movimento 
			= new MovimentoAtualizaRelacaoClassificacaoAreasCNPq(relacionamentosSalvos);
		movimento.setCodMovimento(SigaaListaComando.ATUALIZA_RELACAO_CLASSIFICACAO_AREAS_CNPq);
		
		
		try {
			execute(movimento);
			
			addMensagemInformation("Relacionamentos foram atualizados. ");
			
			return iniciar();
			
		} catch (NegocioException ne) {
			addMensagens(ne.getListaMensagens());
			return null;
		}
		
	}
	
	
	/**
	 * Retorna todas as classificações bibliográficas cadastradas para exibição em um combo box
	 *
	 * @return
	 * @throws DAOException
	 *
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/classificacao_bibliografica_biblioteca/listaRelacionamentoClassificacoesBibliograficasAreasCNPQ.jsp</li>
	 *   </ul>
	 *
	 * @return
	 * @throws DAOException
	 */
	public Collection <SelectItem> getAllClassificacoesCombo() throws DAOException{
		
		Collection <SelectItem> colecao = new ArrayList<SelectItem>();
		
		// Cria manualmente para manter a ordem
		for (ClassificacaoBibliografica classificacao : classificacoesUtilizadas) {
			colecao.add(new SelectItem(classificacao.getId(), classificacao.getDescricao()));
		}
		
		return colecao;
	}
	
	
	
	
	//////////////////////////////// Tela de Navegação /////////////////////////////
	
	
	/**
	 *   Retorna a página onde o usuário visualiza os relacionamento entre as áreas CNPq existente no sistema e 
	 *   as classificações utilizadas na biblioteca (CDU, black, CDD)
	 *
	 *   Método não chamado por nenhuma página jsp.
	 *
	 * @return
	 */
	public String telaListaRelacionamentosClassificacoesAresCNPQ(){
		return forward(PAGINA_LISTA_RELACIONAMENTO_CLASSIFICACOES_AREAS_CNPQ);
	}

	
	//////////////// gets  e sets ///////////////
	
	public Collection<AreaConhecimentoCnpq> getGrandesAreas() {
		return grandesAreas;
	}

	public void setGrandesAreas(Collection<AreaConhecimentoCnpq> grandesAreas) {
		this.grandesAreas = grandesAreas;
	}

	public int getIdClassificacaoEscolhida() {
		return idClassificacaoEscolhida;
	}

	public void setIdClassificacaoEscolhida(int idClassificacaoEscolhida) {
		this.idClassificacaoEscolhida = idClassificacaoEscolhida;
	}

	public List<RelacionaClassificacaoBibliograficaAreaCNPq> getRelacionamentosSalvos() {
		return relacionamentosSalvos;
	}

	public void setRelacionamentosSalvos(List<RelacionaClassificacaoBibliograficaAreaCNPq> relacionamentosSalvos) {
		this.relacionamentosSalvos = relacionamentosSalvos;
	}

	public ClassificacaoBibliografica getClassificacoEscolhida() {
		return classificacoEscolhida;
	}

	public void setClassificacoEscolhida(ClassificacaoBibliografica classificacoEscolhida) {
		this.classificacoEscolhida = classificacoEscolhida;
	}





	public boolean isBotaoAtualizarHabilitado() {
		return botaoAtualizarHabilitado;
	}





	public void setBotaoAtualizarHabilitado(boolean botaoAtualizarHabilitado) {
		this.botaoAtualizarHabilitado = botaoAtualizarHabilitado;
	}
	
	
	
}
