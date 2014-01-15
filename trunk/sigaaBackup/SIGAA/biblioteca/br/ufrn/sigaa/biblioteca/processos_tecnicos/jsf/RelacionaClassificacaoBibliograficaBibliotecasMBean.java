/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 13/02/2012
 * 
 */
package br.ufrn.sigaa.biblioteca.processos_tecnicos.jsf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.sigaa.arq.dao.biblioteca.BibliotecaDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.biblioteca.dominio.Biblioteca;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dao.ClassificacaoBibliograficaDao;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.ClassificacaoBibliografica;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.RelacionaClassificacaoBibliograficaBiblioteca;

/**
 *
 * <p>MBean para configurar quais as classificações bibliográficas que um determinada biblioteca trabalha.
 * 
 * <p><i> Esse relacionamento vai ser utilizado para poder validar e só deixar incluir materiais caso o Título possua a 
 * classificação utilizada na biblioteca do material. Por exemplo, a classificação BLACK só é utilizada por 
 * bibliotecas de odontologia. Então se um Título do sistema tiver a classificação CDU preenchida e alguém tentar 
 * incluir um material da biblioteca de odontologia, o sistema não pode deixar, primeiro tem que preencher os dados da classificação BLACK.
 * <i></p>
 * 
 * @author jadson
 *
 */
@Component("relacionaClassificacaoBibliograficaBibliotecasMBean")
@Scope("request")
public class RelacionaClassificacaoBibliograficaBibliotecasMBean extends SigaaAbstractController<RelacionaClassificacaoBibliograficaBiblioteca>{

	
	/** Página que lista as bibliotecas e sua classificações, também é a página na qual o usuário altera o relacionamento. */
	public final static String PAGINA_LISTA_BIBLIOTECAS_E_SUAS_CLASSIFICACOES = "/biblioteca/classificacao_bibliografica_biblioteca/listaBibliotecasESuasClassificacoes.jsp";
	
	/** Guarda a lista de classificações utilizadas no sistema */
	private List<ClassificacaoBibliografica> classificacoesUtilizadas;
	
	/** Guarda a lista de bibliotecas internas do sistema */
	private List<Biblioteca> bibliotecasInternas;
	
	/** Guarda a lista de associações que foram feitas */
	private List<RelacionaClassificacaoBibliograficaBiblioteca> relacionamentosConfigurados;
	
	
	
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
		
		ClassificacaoBibliograficaDao dao = null;
		BibliotecaDao bibliotecaDao = null;
		
		try{
			dao = getDAO(ClassificacaoBibliograficaDao.class);
			bibliotecaDao = getDAO(BibliotecaDao.class);
			
			classificacoesUtilizadas = dao.findAllAtivos("ordem", "asc", new String[]{ "descricao", "id", "ordem"}  );
			bibliotecasInternas = bibliotecaDao.findAllBibliotecasInternasAtivas();
			relacionamentosConfigurados = dao.findAllRelacionamentosClassificacaoBiblioteca();
			
			if(classificacoesUtilizadas.size() == 0){
				addMensagemErro("Não existem classificações bibliográficas cadastradas no sistema.");
				return null;
			}
			
			if(bibliotecasInternas.size() == 0){
				addMensagemErro("Não existem bibliotecas cadastradas no sistema.");
				return null;
			}	
			
			montaDadosExibicaoUsuario();
		
		}finally{
			if(dao != null) dao.close();
			if(bibliotecaDao != null) bibliotecaDao.close();
		}
		
		prepareMovimento(SigaaListaComando.ATUALIZA_RELACIONAMENTO_CLASSIFICACAO_BIBLIOTECA);
		
		return telaListaBibliotecasESuasClassificacoes();
	}
	
	
	
	/**
	 * Monta os dados para exibição para o usuário.
	 *
	 */
	private void montaDadosExibicaoUsuario() {
			
		// Para cada classificação configura no banco monta os dados para exibição na página para o usuário.
		for (Biblioteca biblioteca : bibliotecasInternas) {
			
			RelacionaClassificacaoBibliograficaBiblioteca relacionamentoTemp = new RelacionaClassificacaoBibliograficaBiblioteca(biblioteca);
			
			// Se não foi configurado para a biblioteca em questão, então adiciona a biblioteca.
			// Só ocorre esse caso, se o usuário tentar executar a caso de uso pela primeira vez 
			// quando as classificações que a biblioteca trabalha não estão configuradas
			if(! relacionamentosConfigurados.contains(relacionamentoTemp)){
				relacionamentoTemp.setClassificacao(new ClassificacaoBibliografica(-1));
				relacionamentosConfigurados.add(relacionamentoTemp);
			}
		}
	}

	/**
	 * Atualiza os relacionamentos entre biblioteca e classificação utilizada no banco.
	 *  
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/classificacao_bibliografica_biblioteca/listaBibliotecasESuasClassificacoes.jsp</li>
	 *   </ul>
	 *
	 * @return
	 * @throws ArqException 
	 */
	public String atualizarRelacionamentos() throws ArqException{
		
		MovimentoCadastro mov = new MovimentoCadastro();
		mov.setCodMovimento(SigaaListaComando.ATUALIZA_RELACIONAMENTO_CLASSIFICACAO_BIBLIOTECA);
		mov.setColObjMovimentado(relacionamentosConfigurados);
		
		try{
			execute(mov);
			addMensagemInformation("Relacionamentos atualizados com sucesso!");
		}catch (NegocioException ne) {
			addMensagens(ne.getListaMensagens());
			return null;
		}
		
		return iniciar();
	}
	

	/**
	 * Retorna todas as classificações bibliográficas cadastradas para exibição em um combo box
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
	
	
	
	/**
	 * Redireciona para a tela do caso de uso.
	 *
	 *  <p>Método não chamado por nenhuma página jsp.</p>
	 *
	 * @return
	 */
	public String telaListaBibliotecasESuasClassificacoes(){
		return forward(PAGINA_LISTA_BIBLIOTECAS_E_SUAS_CLASSIFICACOES);
	}
	
	
	///// sets e gets /////
	

	public List<RelacionaClassificacaoBibliograficaBiblioteca> getRelacionamentosConfigurados() {
		return relacionamentosConfigurados;
	}

	public void setRelacionamentosConfigurados(List<RelacionaClassificacaoBibliograficaBiblioteca> relacionamentosConfigurados) {
		this.relacionamentosConfigurados = relacionamentosConfigurados;
	}
	
}
