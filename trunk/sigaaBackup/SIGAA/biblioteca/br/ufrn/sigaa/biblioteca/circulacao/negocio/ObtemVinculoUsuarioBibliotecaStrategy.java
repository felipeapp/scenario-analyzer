/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
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
 * <p> Interface que contém os métodos que vão ser utilizados para recuperar as informações sobre o vínculo que o usuário 
 *     vai utilizar para realizar os empréstimos no sistema. </p>
 *
 * <p> <i> É criada uma conta no sistema com o vínculo preferêncial no momento do cadastro do usuário no sistema. Essa conta é finalizada no momento 
 * da emissão da quitação.  Para possuir uma nova conta, é necessário fazer um recadastro no sistema.
 * O vínculo preferencial é calculado automaticamente pelo sistema.
 * </i> </p>
 *
 * <p> <i> Deve ser implementado de acordo com as regras de obteção os vínculos de cada instituição </i> </p>
 * 
 * @author jadson
 *
 */
public abstract class ObtemVinculoUsuarioBibliotecaStrategy {
	
	
	/**
	 * <p>Implementar as regras para retornar uma lista de todos os  vínculos do usuário, mesmo os que já extão expirados. </p>
	 * 
	 * <p> <i> Utilizando APENAS para emitir o documento de quitação <i> </p>
	 * 
	 * @param usuarioBiblioteca
	 * @return
	 */
	public abstract  List<InfoVinculoUsuarioBiblioteca> getVinculos(int idPessoa) throws DAOException, NegocioException;
	
	
	/**
	 * <p>Retorna uma lista de vínculos ativos do usuário ordenados de acordo com os tipos de usuários existentes utilizados na biblioteca </p>
	 * 
	 * <p> <i> Utilizado no momento do cadastro <i> </p>
	 * 
	 * @param usuarioBiblioteca
	 * @return
	 */
	public abstract  List<InfoVinculoUsuarioBiblioteca> getVinculosAtivos(int idPessoa) throws DAOException, NegocioException;
	
	
	
	/**
	 * <p>Método usado para obter informações mais detalhadas sobre o vínculo do usuário como unidade ou curso. </p>
	 * 
	 * <p>  <i> Usado para distinguir os vários vínculos na página de emissão do documento de quitação  </i> </p>
	 * 
	 * @param usuarioBiblioteca
	 * @return
	 */
	public abstract String recuperaInformacoesCompletasVinculos(VinculoUsuarioBiblioteca vinculo,  Integer idDentificacaoVinculo) throws DAOException;
	
	
	/**
	 * <p>Obtém as informações do usuário, como endereço de contado, curso, departamento, telefone, etc...</p>
	 * 
	 * <p> <i> Utilizado para mostrar as informações do usuário selecionado nas disversas páginas do sistemas</i> </p>
	 * 
	 * @param usuarioBiblioteca
	 * @return
	 */
	public abstract InformacoesUsuarioBiblioteca getInformacoesUsuario(UsuarioBiblioteca usuarioBiblioteca, Integer idPessoa, Integer idBiblioteca) throws DAOException, NegocioException;
	
	
	/**
	 *  <p>Verifica se o vínculo passado ainda está ativo para a pessoa.</p>
	 *
	 *  <p> <i> Método usado no momento do empréstimos, para verificar se o vínculo que o usuário possui ainda é válido ou não. Caso não seja,
	 *   o usuário vai precisar quitar o vínculo e fazer um recadastro para obter um novo vínculo, caso ele tenha. </i> </p>
	 *
	 * @param idPessoa
	 * @param vinculo
	 * @param idDentificacaoVinculo
	 * 
	 * @return Se o vínculo está ativo ou não
	 * @throws DAOException
	 * @throws NegocioException
	 */
	public abstract boolean isVinculoAtivo(int idPessoa, VinculoUsuarioBiblioteca vinculo,  Integer idDentificacaoVinculo) throws DAOException, NegocioException;
	
	
	/** Método que recupera as informações dos vinculos de discente do sistema, dizendo para cada vínculo se ele está ativo ou não de acordo 
	 *  com as regras especificadas na classe que implementar essa interface */
	public abstract List<InfoVinculoUsuarioBiblioteca> montaInformacoesVinculoDiscenteBiblioteca(int idPessoa)throws DAOException;
	
	
	
	/** Método que recupera as informações dos vinculos de servidor do sistema, dizendo para cada vínculo se ele está ativo ou não de acordo 
	 *  com as regras especificadas na classe que implementar essa interface */
	public abstract List<InfoVinculoUsuarioBiblioteca> montaInformacoesVinculoServidorBiblioteca(int idPessoa)throws DAOException;
	
	
	
	/** Método que recupera as informações dos vinculos de usuário externo do sistema, dizendo para cada vínculo se ele está ativo ou não de acordo 
	 *  com as regras especificadas na classe que implementar essa interface */
	public abstract List<InfoVinculoUsuarioBiblioteca> montaInformacoesVinculoUsuarioExternoBiblioteca(int idPessoa)throws DAOException;
	
	
	
	/** Método que recupera as informações dos vinculos de docente externo do sistema, dizendo para cada vínculo se ele está ativo ou não de acordo 
	 *  com as regras especificadas na classe que implementar essa interface */
	public abstract List<InfoVinculoUsuarioBiblioteca> montaInformacoesVinculoDocenteExternoBiblioteca(int idPessoa)throws DAOException;
	
	
	/** Retorna os status dos discentes que permitem utilizar a biblioteca.  Implementado nas classes filhas de acordo com a Estrategia configurada.
	 */
	public abstract Integer[] getStatusDiscenteUtilizarBiblioteca();
	
	/** Retorna os status dos servidores que permitem utilizar a biblioteca.  Implementado nas classes filhas de acordo com a Estrategia configurada.*/
	public abstract Integer[] getStatusServidorUtilizarBiblioteca();

	/** Retorna as categorias que são consideradas alunos de pós-graduação para a biblioteca. Implementado nas classes filhas de acordo com a Estrategia configurada. */
	public abstract Character[] getNiveisAlunosPosGraduacaoBiblioteca();

	/** Retorna as categorias que são consideradas alunos de graduação para a biblioteca. Implementado nas classes filhas de acordo com a Estrategia configurada. */
	public abstract Character[] getNiveisAlunosGraduacaoBiblioteca();

	/** Retorna as categorias que são consideradas alunos médio/tecnicos para a biblioteca. Implementado nas classes filhas de acordo com a Estrategia configurada. */
	public abstract Character[] getNiveisAlunosMedioTecnicoBiblioteca();

	/** Retorna as categorias que são consideradas alunos infantil para a biblioteca. Implementado nas classes filhas de acordo com a Estrategia configurada. */
	public abstract Character[] getNiveisAlunosInfantilBiblioteca();
	
	/** Retorna as categorias que são consideradas servidores para a biblioteca. Implementado nas classes filhas de acordo com a Estrategia configurada. */
	public abstract Integer[] getCategoriaServidorBiblioteca();


	/** Retorna as categorias que são consideradas docente para a biblioteca. Implementado nas classes filhas de acordo com a Estrategia configurada. */
	public abstract Integer[] getCategoriaDocenteBiblioteca();
	
	/** Retorna as categorias que são consideradas discente para a biblioteca. Implementado nas classes filhas de acordo com a Estrategia configurada. */
	public abstract Integer[] getTiposDiscenteBiblioteca();
	
}
