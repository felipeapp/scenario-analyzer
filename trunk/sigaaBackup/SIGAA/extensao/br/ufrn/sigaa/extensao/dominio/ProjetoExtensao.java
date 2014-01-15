/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
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
 * Representa uma ação de extensão do tipo projeto.Projetos de Extensão são
 * propostas de atuação na realidade social, de natureza acadêmica, com caráter
 * educativo, social, cultural, científico ou tecnológico, e que cumpram o
 * preceito da indissociabilidade entre ensino, pesquisa e extensão.<br/>
 * 
 * Projeto é um conjunto de ações processuais contínuas, de caráter educativo,
 * social, cultural, científico e tecnológico. Se um projeto se caracteriza por
 * uma relação contratual de prestação de serviços, deverá ser registrada como
 * prestação de serviços. Entretanto, se essa prestação é parte de um conjunto
 * de ações processuais contínuas, pelo menos de médio prazo, a ação deverá ser
 * registrada como Projeto.<br/>
 * 
 * Vale dizer que Cursos não devem ser registrados como Projetos, embora sua
 * elaboração envolva a existência de um projeto operacional (SDIE, p. 51).<br/>
 * 
 * Os projetos de extensão realizados em instituições, fora da Universidade,
 * deverão contar com a aquiescência expressa da instituição na qual as
 * atividades serão realizadas, assim como as condições de sua viabilização (Art
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

	//Grupos de pesquisa do projeto de extensão.
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