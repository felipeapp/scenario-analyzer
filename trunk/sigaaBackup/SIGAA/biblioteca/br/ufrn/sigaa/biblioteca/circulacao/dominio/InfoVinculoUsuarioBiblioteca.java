/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em: 14/04/2011
 * 
 */
package br.ufrn.sigaa.biblioteca.circulacao.dominio;

import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.HashCodeUtil;
import br.ufrn.sigaa.biblioteca.dominio.VinculoUsuarioBiblioteca;

/**
 *
 * <p>Classe que guarda informa��es sobre o v�nculo o Usu�rio Biblioteca, utilizada na emiss�o do comprovante de quita��o 
 * para guardas as informa��es sobre v�nculos. E tamb�m no caso de um utilizado para verificar quais v�nculos o usu�rio pode utilizar na biblioteca. 
 * </p>
 *
 * 
 * @author jadson
 *
 */
public class InfoVinculoUsuarioBiblioteca {

	/**O v�nculo */
	private VinculoUsuarioBiblioteca vinculo;
	
	/** A identifica��o do v�nculo no sistema: idDiscente, idServidor, idBiblioteca, etc.... */
	private Integer identificacaoVinculo;
	
	/** O identificador do v�nculo para o usu�rio:  cpf, matricula, siape*/
	private String identificicadorVinculo;
	
	/** A descri��o do n�vel ou categoria do vinculo */
	private String nivelCategoria;
	
	/** A descri��o do tipo de vinculo*/
	private String tipo;
	
	/** O prazo at� quando o v�nculo � v�lido, utilizando quando o v�nculo tem prazo de validade*/
	private String prazo;
	
	/** A descri��o sobre o status do vinculo, pode ser matriculado, cancelado, aponsentado, depende do tipo de vinculo*/
	private String status;
	
	/** Indica se o v�nculo ainda est� ativo no sistema para a biblioteca, isto � se o usu�rio pode utilizar esse v�nculo para fazer empr�stimos*/
	private boolean podeFazerEmprestimos = true;

	/** Indica se o v�nculo est� quintado ou n�o, pode ser que o v�nculo possa ser usado para fazer empr�stimos mas esteja quitado pelo usu�rio.*/
	private boolean quitado = false;
	
	
	/**
	 * Construtor utilizado por casos de uso que retornem apenas os v�nculos ativos do usu�rio (para Cadastro no sistema)
	 * @param vinculo
	 * @param identificacaoVinculo
	 * @param podeFazerEmprestimos
	 */
	public InfoVinculoUsuarioBiblioteca(VinculoUsuarioBiblioteca vinculo, Integer identificacaoVinculo) {
		this.vinculo = vinculo;
		this.identificacaoVinculo = identificacaoVinculo;
	}

	
	
    /**
     * Construtor utilizado por casos de uso que retornem todos os v�nculos do usu�rio (para verifica��o e emiss�o da quita��o)
     * @param identificicadorVinculo
     * @param nivelCategoria
     * @param tipo
     * @param prazo
     * @param status
     * @param podeFazerEmprestimos
     */
	public InfoVinculoUsuarioBiblioteca(VinculoUsuarioBiblioteca vinculo, Integer identificacaoVinculo, String identificicadorVinculo, String nivelCategoria, String tipo, String prazo, String status, boolean podeFazerEmprestimos) {
		this(vinculo, identificacaoVinculo);
		this.identificicadorVinculo = identificicadorVinculo;
		this.nivelCategoria = nivelCategoria;
		this.tipo = tipo;
		this.prazo = prazo;
		this.status = status;
		this.podeFazerEmprestimos = podeFazerEmprestimos;
	}


	@Override
	public int hashCode() {
		return HashCodeUtil.hashAll(vinculo, identificacaoVinculo);
	}



	@Override
	public boolean equals(Object obj) {
		return EqualsUtil.testTransientEquals(this, obj, "vinculo", "identificacaoVinculo");
	}
	
	
	
	


	public VinculoUsuarioBiblioteca getVinculo() {
		return vinculo;
	}

	public Integer getIdentificacaoVinculo() {
		return identificacaoVinculo;
	}

	public String getIdentificicadorVinculo() {
		return identificicadorVinculo;
	}

	public String getNivelCategoria() {
		return nivelCategoria;
	}

	public String getTipo() {
		return tipo;
	}

	public String getPrazo() {
		return prazo;
	}
	
	public String getStatus() {
		return status;
	}
	
	public boolean isPodeFazerEmprestimos() {
		return podeFazerEmprestimos;
	}
	
	public boolean isAtivo() {
		return podeFazerEmprestimos;
	}
	
	public String getDescricaoVinculo(){
		return vinculo.getDescricao();
	}
	
	public String getIdVinculo(){
		return vinculo.getValor()+" "+identificacaoVinculo;
	}

	public boolean isQuitado() {
		return quitado;
	}

	public void setQuitado(boolean quitado) {
		this.quitado = quitado;
	}



	
}
