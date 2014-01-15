/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
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
 * <p>Classe que guarda informações sobre o vínculo o Usuário Biblioteca, utilizada na emissão do comprovante de quitação 
 * para guardas as informações sobre vínculos. E também no caso de um utilizado para verificar quais vínculos o usuário pode utilizar na biblioteca. 
 * </p>
 *
 * 
 * @author jadson
 *
 */
public class InfoVinculoUsuarioBiblioteca {

	/**O vínculo */
	private VinculoUsuarioBiblioteca vinculo;
	
	/** A identificação do vínculo no sistema: idDiscente, idServidor, idBiblioteca, etc.... */
	private Integer identificacaoVinculo;
	
	/** O identificador do vínculo para o usuário:  cpf, matricula, siape*/
	private String identificicadorVinculo;
	
	/** A descrição do nível ou categoria do vinculo */
	private String nivelCategoria;
	
	/** A descrição do tipo de vinculo*/
	private String tipo;
	
	/** O prazo até quando o vínculo é válido, utilizando quando o vínculo tem prazo de validade*/
	private String prazo;
	
	/** A descrição sobre o status do vinculo, pode ser matriculado, cancelado, aponsentado, depende do tipo de vinculo*/
	private String status;
	
	/** Indica se o vínculo ainda está ativo no sistema para a biblioteca, isto é se o usuário pode utilizar esse vínculo para fazer empréstimos*/
	private boolean podeFazerEmprestimos = true;

	/** Indica se o vínculo está quintado ou não, pode ser que o vínculo possa ser usado para fazer empréstimos mas esteja quitado pelo usuário.*/
	private boolean quitado = false;
	
	
	/**
	 * Construtor utilizado por casos de uso que retornem apenas os vínculos ativos do usuário (para Cadastro no sistema)
	 * @param vinculo
	 * @param identificacaoVinculo
	 * @param podeFazerEmprestimos
	 */
	public InfoVinculoUsuarioBiblioteca(VinculoUsuarioBiblioteca vinculo, Integer identificacaoVinculo) {
		this.vinculo = vinculo;
		this.identificacaoVinculo = identificacaoVinculo;
	}

	
	
    /**
     * Construtor utilizado por casos de uso que retornem todos os vínculos do usuário (para verificação e emissão da quitação)
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
