/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '26/10/2006'
 *
 */
package br.ufrn.sigaa.prodocente.producao.dominio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.ValidatorUtil;

/**
 * Entidade que registra os tipos de produção tecnológica que um docente pode cadastrar no sistema
 *
 * @author Gleydson
 */
@Entity
@Table(name = "tipo_producao_tecnologica", schema="prodocente")
public class TipoProducaoTecnologica implements Validatable {

    public static final int SOFTWARE = 7;
    public static final int MAQUETE = 5;
    public static final int CARTA_MAPA = 4;
    public static final TipoProducaoTecnologica NENHUMA = new TipoProducaoTecnologica(99);
	@Id @GeneratedValue(strategy=GenerationType.SEQUENCE)
    @Column(name = "id_tipo_producao_tecnologica", nullable = false)
    private int id;

    @Column(name = "descricao")
    private String descricao;

    private boolean ativo;
    
    /** Creates a new instance of TipoProducaoTecnologica */
    public TipoProducaoTecnologica() {
    }

    public TipoProducaoTecnologica(int id) {
    	this.id=id;
    }
	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Integer getIdObject() {
		return id;
	}

	public void setIdObject(Integer idObject) {
		if ( idObject != null ) {
			id = idObject;
		}
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	/*
	 * Campo Obrigatorio: Descricao
	 */
	public ListaMensagens validate(){

		ListaMensagens lista = new ListaMensagens();

		ValidatorUtil.validateRequired(getDescricao(),"Descrição", lista);

		return lista;
	}

}