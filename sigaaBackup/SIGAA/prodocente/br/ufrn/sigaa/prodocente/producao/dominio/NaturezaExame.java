/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '26/10/2006'
 *
 */
package br.ufrn.sigaa.prodocente.producao.dominio;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.util.ValidatorUtil;

/**
 * Natureza de um exame do qual uma banca de docentes participa da avaliação
 *
 * @author Gleydson
 */
@Entity
@Table(name = "natureza_exame",schema = "prodocente")
public class NaturezaExame implements Serializable {

	public static final NaturezaExame TESE_DOUTORADO = new NaturezaExame(1);

	public static final NaturezaExame DISSERTACAO_MESTRADO = new NaturezaExame(2);

	public static final NaturezaExame MONOGRAFIA_ESPECIALIZACAO = new NaturezaExame(3);

	public static final NaturezaExame MONOGRAFIA_GRADUACAO = new NaturezaExame(4);

	public static final NaturezaExame QUALIFICACAO_DOUTORADO = new NaturezaExame(5);

	public static final NaturezaExame QUALIFICACAO_MESTRADO = new NaturezaExame(6);

	public static final NaturezaExame SELECAO_DOUTORADO = new NaturezaExame(7);

	public static final NaturezaExame SELECAO_MESTRADO = new NaturezaExame(8);

	public static final NaturezaExame SELECAO_ESPECIALIZACAO = new NaturezaExame(9);

    @Id
    @Column(name = "id_natureza_exame", nullable = false)
    private int id;

    @Column(name = "descricao")
    private String descricao;

    /** Creates a new instance of NaturezaExame */
    public NaturezaExame() {
    }

    public NaturezaExame(int idNaturezaExame) {
        this.id = idNaturezaExame;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int idNaturezaExame) {
        this.id = idNaturezaExame;
    }

    public String getDescricao() {
        return this.descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
    public Integer getIdObject() {
		return id;
	}

	public void setIdObject(Integer idObject) {
		if ( idObject != null ) {
			id = idObject;
		}
	}


    @Override
	public boolean equals(Object object) {
        if (!(object instanceof NaturezaExame)) {
            return false;
        }
        NaturezaExame other = (NaturezaExame)object;
        if (this.id != other.id && (this.id == 0 || this.id != other.id)) return false;
        return true;
    }

    @Override
	public String toString() {
        return "br.ufrn.sigaa.prodocente.producao.dominio.NaturezaExame[id=" + id + "]";
    }

    /*
     * Campos Obrigatorios: Natureza Exame, Descricao
     */

    public ListaMensagens validate(){

		ListaMensagens lista = new ListaMensagens();

		ValidatorUtil.validateRequired(getDescricao(), "Descrição da Natureza do Exame", lista.getMensagens());

		return lista;
	}

}
