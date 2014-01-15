/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica 
 * Diretoria de Sistemas
 * 
 * Criado em: 06/10/2010
 */
package br.ufrn.sigaa.projetos.negocio;

import java.util.List;

import javax.faces.component.UIData;

import br.ufrn.arq.dominio.Comando;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.projetos.dominio.Avaliacao;
import br.ufrn.sigaa.projetos.dominio.AvaliadorProjeto;
import br.ufrn.sigaa.projetos.dominio.DistribuicaoAvaliacao;
import br.ufrn.sigaa.projetos.dominio.Projeto;

/**
 * Interface utilizada na implementa��o das diversas
 * estrat�gias de distribui��o de projetos. Devem existir
 * diversas implementa��es dessa interface como, por exemplo,
 * para distribui��o de projetos de ensino, pesquisa e extens�o. 
 *  
 * @author ilueny santos
 *
 */
public interface EstrategiaDistribuicaoProjetos {
	
    public void distribuir() throws DAOException, NegocioException;
    
    //Lista todos os Avaliadores dispon�veis para o projeto.
    public List<AvaliadorProjeto> getAvaliadoresDisponiveis() throws DAOException, NegocioException;
    
    //Lista todos os projetos dispon�veis para distribui��o. Se o total de avalia��es realizadas for null, retornar� todos os projetos avaliados.
    public List<Projeto> getProjetosParaDistribuir() throws DAOException, NegocioException;

    //Lista todos os projetos avaliados. Prontos para finaliza��o da distribui��o com o total de avalia��es realizadas. Se o total de avalia��es realizadas for null, retornar� todos os projetos avaliados.
    public List<Projeto> getProjetosAvaliados() throws DAOException, NegocioException;
    
    //Distribui o projeto para avalia��o do usu�rio informado.
    public void adicionarAvaliacao(Usuario avaliador) throws DAOException, NegocioException;
    
    //Remove o projeto da avalia��o do usu�rio informado.
    public void removerAvaliacao(Avaliacao avaliacao) throws DAOException, NegocioException;
    
    public void setDistribuicao(DistribuicaoAvaliacao distribuicao);
    
    public Projeto getProjeto();
    
    public void setProjeto(Projeto projeto);
    
    public void setTotalAvaliacoesRealizadas(Integer totalAvaliacoesRealizadas);
    
    public void setNumeroAvaliadoresProjeto(Integer numeroAvaliadoresProjeto);
    
    public String formularioProjetos();
    
    public String formularioAvaliadores();
    
    public Comando getComandoDistribuicao();
    
    public void selecionarProjetos(UIData dataTable) throws DAOException, NegocioException;
    
    public List<Projeto> getProjetos();
}
