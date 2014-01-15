/* 
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 *
 */
package br.ufrn.sigaa.ensino.graduacao.negocio;

import java.util.List;

import br.ufrn.arq.dominio.AbstractMovimentoAdapter;
import br.ufrn.sigaa.ensino.dominio.FormaIngresso;
import br.ufrn.sigaa.ensino.dominio.OfertaVagasCurso;

/** Classe utilizada para encapsular os dados para o cadastro de ofertas de vagas de cursos.
 * @author Édipo Elder F. Melo
 *
 */
public class MovimentoCadastroOfertaVagasCurso extends AbstractMovimentoAdapter {

	/** Lista de ofertas de vagas para cadastrar. */
	private List<OfertaVagasCurso> listaOfertaVagasCurso;
	
	/** Ano da oferta de vagas. */
	private int ano;
	
	/** Forma de ingresso das ofertas de vagas. */
	private FormaIngresso formaIngresso;
	
	/** Retorna o ano da oferta de vagas. 
	 * @return Ano da oferta de vagas. 
	 */
	public int getAno() {
		return ano;
	}

	/** Retorna a forma de ingresso das ofertas de vagas. 
	 * @return Forma de ingresso das ofertas de vagas. 
	 */
	public FormaIngresso getFormaIngresso() {
		return formaIngresso;
	}

	/** Retorna a lista de ofertas de vagas para cadastrar. 
	 * @return Lista de ofertas de vagas para cadastrar. 
	 */
	public List<OfertaVagasCurso> getListaOfertaVagasCurso() {
		return listaOfertaVagasCurso;
	}

	/** Seta o ano da oferta de vagas. 
	 * @param ano Ano da oferta de vagas. 
	 */
	public void setAno(int ano) {
		this.ano = ano;
	}

	/** Retorna a forma de ingresso das ofertas de vagas.
	 * @param formaIngresso Forma de ingresso das ofertas de vagas.
	 */
	public void setFormaIngresso(FormaIngresso formaIngresso) {
		this.formaIngresso = formaIngresso;
	}

	/** Seta a lista de ofertas de vagas para cadastrar. 
	 * @param listaOfertaVagasCurso Lista de ofertas de vagas para cadastrar. 
	 */
	public void setListaOfertaVagasCurso(
			List<OfertaVagasCurso> listaOfertaVagasCurso) {
		this.listaOfertaVagasCurso = listaOfertaVagasCurso;
	}

}
