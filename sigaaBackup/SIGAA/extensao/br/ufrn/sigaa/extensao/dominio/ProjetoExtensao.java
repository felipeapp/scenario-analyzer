/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 17/11/2006
 *
 */
package br.ufrn.sigaa.extensao.dominio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.sigaa.pesquisa.dominio.GrupoPesquisa;

/*******************************************************************************
 * <p>
 * Representa uma a��o de extens�o do tipo projeto.Projetos de Extens�o s�o
 * propostas de atua��o na realidade social, de natureza acad�mica, com car�ter
 * educativo, social, cultural, cient�fico ou tecnol�gico, e que cumpram o
 * preceito da indissociabilidade entre ensino, pesquisa e extens�o.<br/>
 * 
 * Projeto � um conjunto de a��es processuais cont�nuas, de car�ter educativo,
 * social, cultural, cient�fico e tecnol�gico. Se um projeto se caracteriza por
 * uma rela��o contratual de presta��o de servi�os, dever� ser registrada como
 * presta��o de servi�os. Entretanto, se essa presta��o � parte de um conjunto
 * de a��es processuais cont�nuas, pelo menos de m�dio prazo, a a��o dever� ser
 * registrada como Projeto.<br/>
 * 
 * Vale dizer que Cursos n�o devem ser registrados como Projetos, embora sua
 * elabora��o envolva a exist�ncia de um projeto operacional (SDIE, p. 51).<br/>
 * 
 * Os projetos de extens�o realizados em institui��es, fora da Universidade,
 * dever�o contar com a aquiesc�ncia expressa da institui��o na qual as
 * atividades ser�o realizadas, assim como as condi��es de sua viabiliza��o (Art
 * 7o da Res. 070/2004-CONSEPE).
 * </p>
 * 
 * @author Victor Hugo
 * @author Ilueny Santos
 * 
 ******************************************************************************/
@Entity
@Table(schema = "extensao", name = "projeto")
public class ProjetoExtensao implements Validatable {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "id_projeto_extensao", nullable = false)
	private int id;

	//Grupos de pesquisa do projeto de extens�o.
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_grupo_pesquisa")
	private GrupoPesquisa grupoPesquisa = new GrupoPesquisa();
	
	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public GrupoPesquisa getGrupoPesquisa() {
		return grupoPesquisa;
	}

	public void setGrupoPesquisa(GrupoPesquisa grupoPesquisa) {
		this.grupoPesquisa = grupoPesquisa;
	}

	public ListaMensagens validate() {
		return null;
	}

}