/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
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
 * <p>Mbean respons�vel por realizar o relacionamento entre as classifica��es bibliogr�fica cadastradas no sistema e as grandes �reas de conhecimento do CNPq.</p>
 *
 * <p> <i> Esse relacionamento deve ser configurado para podemos saber a �rea CNPq de um T�tulo a partir do sua classifica��o bibliogr�fica. </i> </p>
 * 
 * @author jadson
 *
 */
@Component("relacionamentoClassificacaoBibliograficaAreasCNPqMBean")
@Scope("request")
public class RelacionamentoClassificacaoBibliograficaAreasCNPqMBean extends SigaaAbstractController<RelacionaClassificacaoBibliograficaAreaCNPq> {

	/** A p�gina onde o administrador do sistema altera as informa��es do relacionamento entre classifica��o CDU/Black e �reas CNPq*/
	public static final String PAGINA_LISTA_RELACIONAMENTO_CLASSIFICACOES_AREAS_CNPQ = "/biblioteca/classificacao_bibliografica_biblioteca/listaRelacionamentoClassificacoesBibliograficasAreasCNPQ.jsp";
	
	
	/**Guardas as grandes �reas de conhecimento CNPq, cujos relacionamentos com as classifica��es da biblioteca ser�o alterados */
	private Collection<AreaConhecimentoCnpq> grandesAreas;
	
	/** Guarda a lista de classifica��es utilizadas no sistema */
	private List<ClassificacaoBibliografica> classificacoesUtilizadas;
	
	/** Guarda a informa��o do id da classifica��o escolhida pelo usu�rio */
	private int idClassificacaoEscolhida;
	
	/** Guarda as informa��es da classifica��o escolhida pelo usu�rio */
	private ClassificacaoBibliografica classificacoEscolhida = new ClassificacaoBibliografica();
	
	/** Guarda as informa��es dos relacionamentos entre as grandes �reas de conhecimento do CNPq e a classifica��o selecionada pelo usu�rio */
	private List<RelacionaClassificacaoBibliograficaAreaCNPq> relacionamentosSalvos;

	/**
	 * Flag que habilita ou desabilita o bot�o atualizar as altera��es do relacionamento. Para evitar que o 
	 * usu�rio clique em "atualizar relacionamento" antes de escolher corretamente os dados o atualize v�rias vezes a mesma coisa, 
	 * j� que a p�gina de listar e alterar � a mesma.
	 */
	private boolean botaoAtualizarHabilitado = false;
	
	
	/**
	 *   <p>Inicia o caso de uso de altera��o dos relacionamento entre  �reas CNPq as classifica��es utilizadas na biblioteca (CDU, black, CDD) </p>
	 *
	 * <br/>
	 * Chamado a partir da p�gina: /sigaa.war/biblioteca/menus/cadastro.jsp
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
				addMensagemErro("N�o existem classifica��es bibliogr�ficas cadastradas no sistema.");
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
	 * Busca os relacionamento com as �reas do CNPq quando o usu�rio escolhe a classifica��o bibliogr�fica.
	 *  
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
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
	 * Percorre a lista, se a lista n�o tiver alguma �rea para a classifica��o escolhida adiciona essa �reas
	 * 
	 * Esse caso s� ocorre ao executar o caso de uso pela primeira vez quando os dados n�o existem no banco, ou caso seja criada uma nova grande �rea.
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
	 * M�todo que atualiza o relacionamento das classifica��es utilizados nas biblioteca com as grandes �reas CNPq
	 * e ativa tamb�m a thread que vai ficar respons�vel por atualizar o cache das �reas CNPq no acervo.
	 *  
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
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
	 * Retorna todas as classifica��es bibliogr�ficas cadastradas para exibi��o em um combo box
	 *
	 * @return
	 * @throws DAOException
	 *
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
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
	
	
	
	
	//////////////////////////////// Tela de Navega��o /////////////////////////////
	
	
	/**
	 *   Retorna a p�gina onde o usu�rio visualiza os relacionamento entre as �reas CNPq existente no sistema e 
	 *   as classifica��es utilizadas na biblioteca (CDU, black, CDD)
	 *
	 *   M�todo n�o chamado por nenhuma p�gina jsp.
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
