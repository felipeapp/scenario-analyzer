/*
 * DadosRelatorioUsuarioEmAtrasao.java
 *
 * UNIVERSIDADE FEDERAL DO RIO GRANDE DO NORTE
 * Superintendencia de Informatica
 * Diretoria de Sistemas
 * Campos Universitario Lagoa Nova
 * Natal - RN - Brasil
 *
 * Este software eh confidencial e de propriedade intelectual da
 * UFRN - Universidade Federal do Rio Grande no Norte
 * Nao se deve utilizar este produto em desacordo com as normas
 * da referida instituicao.
 */
package br.ufrn.sigaa.biblioteca.controle_estatistico.dominio;

import java.util.ArrayList;
import java.util.List;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.Emprestimo;
import br.ufrn.sigaa.biblioteca.dominio.InformacoesUsuarioBiblioteca;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.CacheEntidadesMarc;
import br.ufrn.sigaa.biblioteca.util.BibliotecaUtil;

/**
 *
 * Classe auxiliar para agrupar os dados do relatório de usuário com empréstimos atrasados.
 *
 * @author jadson
 * @since 20/11/2008
 * @version 1.0 criação da classe
 *
 */
public class DadosRelatorioUsuarioEmAtraso {

	/**
	 * Informações do usuário geralmente utilizado para visualização
	 */
	private InformacoesUsuarioBiblioteca infoUsuario;
	
	/**
	 * os empréstimos atrasados do usuário
	 */
	private List<Emprestimo> emprestimosAtrasados = new ArrayList<Emprestimo>();

	/**
	 * Guarda as informações dos títulos dos materiais <br/>
	 *  
	 * IMPORTANTE  guarda os títulos na mesma posição do materiais na coleção de empréstimos 
	 */
	private List<CacheEntidadesMarc> titulos = new ArrayList<CacheEntidadesMarc>(); 
	
	/**
	 * Cria um objeto apenas com o id do usuário para ser usado em comparação dentro de um ArrayList.
	 * 
	 * @param idUsuario
	 */
	public DadosRelatorioUsuarioEmAtraso(int idUsuario){
		this.infoUsuario = new InformacoesUsuarioBiblioteca(idUsuario, null, null, null, null, null, 0, null, null, null, null, null);
	}
	
	/**
	 * Construtor "real"
	 * 
	 * @param usuario
	 * @param emprestimo
	 */
	public DadosRelatorioUsuarioEmAtraso(InformacoesUsuarioBiblioteca infoUsuario, Emprestimo e){
		this.infoUsuario = infoUsuario;
		this.emprestimosAtrasados.add(e);
	}
	
	/**
	 * Adiciona um empréstimos a lista de empréstimos atrasados do usuário
	 *
	 * @param e
	 */
	public void addEmprestimo(Emprestimo e){
		this.emprestimosAtrasados.add(e);
	}
	
	/**
	 *  Monta as informações do título para mostrar para o usuário 
	 * 
	 *  IMPORTANTE não esquecer de chamar antes de ir para página do relatório, senão as informações
	 *  dos títulos dos materiais não serão mostradas.
	 * @throws DAOException 
	 */
	public void montaInformacoesTitulos() throws DAOException{
		
		// importante colocar na mesma posição dos itens na lista de empréstimos para depois poder
		// pegar corretamente na página do relatório
		for (int i = 0; i < emprestimosAtrasados.size(); i++) {
			titulos.add(i, BibliotecaUtil.obtemDadosTituloCache(
					emprestimosAtrasados.get(i).getMaterial().getTituloCatalografico().getId()));
		}
	}
	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ( infoUsuario != null ? infoUsuario.getIdUsuarioBiblioteca() : 0) ;
		return result;
	}

	/**
	 * Dois objetos são iguais se eles pertencem ao mesmo usuário.
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DadosRelatorioUsuarioEmAtraso other = (DadosRelatorioUsuarioEmAtraso) obj;
		
		if(infoUsuario == null || other.getInfoUsuario() == null)
			return false;
		
		if (infoUsuario.getIdUsuarioBiblioteca() != other.getInfoUsuario().getIdUsuarioBiblioteca())
			return false;
		return true;
	}

	
	public List<Emprestimo> getEmprestimosAtrasados() {
		return emprestimosAtrasados;
	}

	public InformacoesUsuarioBiblioteca getInfoUsuario() {
		return infoUsuario;
	}

	public List<CacheEntidadesMarc> getTitulos() {
		return titulos;
	}
	
	
}
