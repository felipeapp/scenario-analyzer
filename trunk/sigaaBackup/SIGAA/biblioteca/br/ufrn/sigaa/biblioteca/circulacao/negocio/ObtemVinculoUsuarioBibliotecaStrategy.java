/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em: 12/04/2011
 * 
 */
package br.ufrn.sigaa.biblioteca.circulacao.negocio;

import java.util.List;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.InfoVinculoUsuarioBiblioteca;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.UsuarioBiblioteca;
import br.ufrn.sigaa.biblioteca.dominio.InformacoesUsuarioBiblioteca;
import br.ufrn.sigaa.biblioteca.dominio.VinculoUsuarioBiblioteca;

/**
 *
 * <p> Interface que cont�m os m�todos que v�o ser utilizados para recuperar as informa��es sobre o v�nculo que o usu�rio 
 *     vai utilizar para realizar os empr�stimos no sistema. </p>
 *
 * <p> <i> � criada uma conta no sistema com o v�nculo prefer�ncial no momento do cadastro do usu�rio no sistema. Essa conta � finalizada no momento 
 * da emiss�o da quita��o.  Para possuir uma nova conta, � necess�rio fazer um recadastro no sistema.
 * O v�nculo preferencial � calculado automaticamente pelo sistema.
 * </i> </p>
 *
 * <p> <i> Deve ser implementado de acordo com as regras de obte��o os v�nculos de cada institui��o </i> </p>
 * 
 * @author jadson
 *
 */
public abstract class ObtemVinculoUsuarioBibliotecaStrategy {
	
	
	/**
	 * <p>Implementar as regras para retornar uma lista de todos os  v�nculos do usu�rio, mesmo os que j� ext�o expirados. </p>
	 * 
	 * <p> <i> Utilizando APENAS para emitir o documento de quita��o <i> </p>
	 * 
	 * @param usuarioBiblioteca
	 * @return
	 */
	public abstract  List<InfoVinculoUsuarioBiblioteca> getVinculos(int idPessoa) throws DAOException, NegocioException;
	
	
	/**
	 * <p>Retorna uma lista de v�nculos ativos do usu�rio ordenados de acordo com os tipos de usu�rios existentes utilizados na biblioteca </p>
	 * 
	 * <p> <i> Utilizado no momento do cadastro <i> </p>
	 * 
	 * @param usuarioBiblioteca
	 * @return
	 */
	public abstract  List<InfoVinculoUsuarioBiblioteca> getVinculosAtivos(int idPessoa) throws DAOException, NegocioException;
	
	
	
	/**
	 * <p>M�todo usado para obter informa��es mais detalhadas sobre o v�nculo do usu�rio como unidade ou curso. </p>
	 * 
	 * <p>  <i> Usado para distinguir os v�rios v�nculos na p�gina de emiss�o do documento de quita��o  </i> </p>
	 * 
	 * @param usuarioBiblioteca
	 * @return
	 */
	public abstract String recuperaInformacoesCompletasVinculos(VinculoUsuarioBiblioteca vinculo,  Integer idDentificacaoVinculo) throws DAOException;
	
	
	/**
	 * <p>Obt�m as informa��es do usu�rio, como endere�o de contado, curso, departamento, telefone, etc...</p>
	 * 
	 * <p> <i> Utilizado para mostrar as informa��es do usu�rio selecionado nas disversas p�ginas do sistemas</i> </p>
	 * 
	 * @param usuarioBiblioteca
	 * @return
	 */
	public abstract InformacoesUsuarioBiblioteca getInformacoesUsuario(UsuarioBiblioteca usuarioBiblioteca, Integer idPessoa, Integer idBiblioteca) throws DAOException, NegocioException;
	
	
	/**
	 *  <p>Verifica se o v�nculo passado ainda est� ativo para a pessoa.</p>
	 *
	 *  <p> <i> M�todo usado no momento do empr�stimos, para verificar se o v�nculo que o usu�rio possui ainda � v�lido ou n�o. Caso n�o seja,
	 *   o usu�rio vai precisar quitar o v�nculo e fazer um recadastro para obter um novo v�nculo, caso ele tenha. </i> </p>
	 *
	 * @param idPessoa
	 * @param vinculo
	 * @param idDentificacaoVinculo
	 * 
	 * @return Se o v�nculo est� ativo ou n�o
	 * @throws DAOException
	 * @throws NegocioException
	 */
	public abstract boolean isVinculoAtivo(int idPessoa, VinculoUsuarioBiblioteca vinculo,  Integer idDentificacaoVinculo) throws DAOException, NegocioException;
	
	
	/** M�todo que recupera as informa��es dos vinculos de discente do sistema, dizendo para cada v�nculo se ele est� ativo ou n�o de acordo 
	 *  com as regras especificadas na classe que implementar essa interface */
	public abstract List<InfoVinculoUsuarioBiblioteca> montaInformacoesVinculoDiscenteBiblioteca(int idPessoa)throws DAOException;
	
	
	
	/** M�todo que recupera as informa��es dos vinculos de servidor do sistema, dizendo para cada v�nculo se ele est� ativo ou n�o de acordo 
	 *  com as regras especificadas na classe que implementar essa interface */
	public abstract List<InfoVinculoUsuarioBiblioteca> montaInformacoesVinculoServidorBiblioteca(int idPessoa)throws DAOException;
	
	
	
	/** M�todo que recupera as informa��es dos vinculos de usu�rio externo do sistema, dizendo para cada v�nculo se ele est� ativo ou n�o de acordo 
	 *  com as regras especificadas na classe que implementar essa interface */
	public abstract List<InfoVinculoUsuarioBiblioteca> montaInformacoesVinculoUsuarioExternoBiblioteca(int idPessoa)throws DAOException;
	
	
	
	/** M�todo que recupera as informa��es dos vinculos de docente externo do sistema, dizendo para cada v�nculo se ele est� ativo ou n�o de acordo 
	 *  com as regras especificadas na classe que implementar essa interface */
	public abstract List<InfoVinculoUsuarioBiblioteca> montaInformacoesVinculoDocenteExternoBiblioteca(int idPessoa)throws DAOException;
	
	
	/** Retorna os status dos discentes que permitem utilizar a biblioteca.  Implementado nas classes filhas de acordo com a Estrategia configurada.
	 */
	public abstract Integer[] getStatusDiscenteUtilizarBiblioteca();
	
	/** Retorna os status dos servidores que permitem utilizar a biblioteca.  Implementado nas classes filhas de acordo com a Estrategia configurada.*/
	public abstract Integer[] getStatusServidorUtilizarBiblioteca();

	/** Retorna as categorias que s�o consideradas alunos de p�s-gradua��o para a biblioteca. Implementado nas classes filhas de acordo com a Estrategia configurada. */
	public abstract Character[] getNiveisAlunosPosGraduacaoBiblioteca();

	/** Retorna as categorias que s�o consideradas alunos de gradua��o para a biblioteca. Implementado nas classes filhas de acordo com a Estrategia configurada. */
	public abstract Character[] getNiveisAlunosGraduacaoBiblioteca();

	/** Retorna as categorias que s�o consideradas alunos m�dio/tecnicos para a biblioteca. Implementado nas classes filhas de acordo com a Estrategia configurada. */
	public abstract Character[] getNiveisAlunosMedioTecnicoBiblioteca();

	/** Retorna as categorias que s�o consideradas alunos infantil para a biblioteca. Implementado nas classes filhas de acordo com a Estrategia configurada. */
	public abstract Character[] getNiveisAlunosInfantilBiblioteca();
	
	/** Retorna as categorias que s�o consideradas servidores para a biblioteca. Implementado nas classes filhas de acordo com a Estrategia configurada. */
	public abstract Integer[] getCategoriaServidorBiblioteca();


	/** Retorna as categorias que s�o consideradas docente para a biblioteca. Implementado nas classes filhas de acordo com a Estrategia configurada. */
	public abstract Integer[] getCategoriaDocenteBiblioteca();
	
	/** Retorna as categorias que s�o consideradas discente para a biblioteca. Implementado nas classes filhas de acordo com a Estrategia configurada. */
	public abstract Integer[] getTiposDiscenteBiblioteca();
	
}
