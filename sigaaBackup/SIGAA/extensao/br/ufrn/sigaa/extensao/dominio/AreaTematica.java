/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 27/10/2006
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
import br.ufrn.arq.util.ValidatorUtil;

/*******************************************************************************
 * <p>
 * Todas as ações de extensão deverão sempre ser classificadas segundo a área
 * temática. Como grande numero desses programas e projetos podem ser
 * relacionados a mais de uma área, propõe-se que sejam classificados em área
 * temática principal (1) e complementar (2).
 * </p>
 * <p>
 * Atualmente, uma ação de extensão (AtividadeExtensao) possui apenas uma área
 * temática principal.
 * </p>
 * 
 * @author Victor Hugo
 * @author Gleydson
 * @author Ilueny Santos
 * 
 ******************************************************************************/
@Entity
@Table(schema = "extensao", name = "area_tematica")
public class AreaTematica implements Validatable {

    public static final int COMUNICACAO = 1;

    public static final int CULTURA = 2;

    public static final int DESENVOLVIMENTO_RURAL = 3;

    public static final int DIREITOS_HUMANOS = 4;

    public static final int EDUCACAO = 5;

    public static final int MEIO_AMBIENTE = 6;

    public static final int SAUDE = 7;

    public static final int TECNOLOGIA = 8;

    public static final int TRABALHO = 9;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id_area_tematica", nullable = false)
    private int id;

    @Column(name = "descricao", nullable = false)
    private String descricao;

    /** Creates a new instance of AreaTematica */
    public AreaTematica() {
    }

    public AreaTematica(int id) {
	this.id = id;
    }

    public AreaTematica(int id, String descricao) {
	this.id = id;
	this.descricao = descricao;
    }

    public int getId() {
	return this.id;
    }

    public void setId(int id) {
	this.id = id;
    }

    public String getDescricao() {
	return this.descricao;
    }

    public void setDescricao(String descricao) {
	this.descricao = descricao;
    }

    public String toString() {
	return "br.ufrn.sigaa.extensao.dominio.AreaTematica[id=" + id + "]";
    }

    public ListaMensagens validate() {
	ListaMensagens lista = new ListaMensagens();
	ValidatorUtil.validateRequired(descricao, "Descrição", lista);
	return lista;
    }

}
