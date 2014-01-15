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
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;

/*******************************************************************************
 * <p>
 * Representa uma ação de extensão do tipo programa. <br/>
 * 
 * Os Programas de Extensão devem ser entendidos como o agrupamento
 * coerentemente articulado de, no mínimo 03 (três) projetos, podendo
 * incorporar, além de projetos, outras atividades passíveis de serem
 * enquadradas nas modalidades Projetos, Cursos, Eventos e Produtos. 
 * </p>
 * 
 * @author Gleydson
 * @author Victor Hugo
 * @author Ilueny Santos
 * 
 ******************************************************************************/
@Entity
@Table(schema = "extensao", name = "programa")
public class ProgramaExtensao implements Validatable {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "id_programa", nullable = false)
	private int id;

	public ProgramaExtensao() {
	}

	public ProgramaExtensao(int id) {
		this.id = id;
	}

	public String toString() {
		return "br.ufrn.sigaa.extensao.dominio.ProgramaExtensao[id=" + this.getId() + "]";
	}

	public ListaMensagens validate() {
		return null;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

}