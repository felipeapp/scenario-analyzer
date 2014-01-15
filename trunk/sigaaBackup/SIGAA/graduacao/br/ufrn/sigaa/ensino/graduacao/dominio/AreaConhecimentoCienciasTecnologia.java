/* 
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 *
 */
package br.ufrn.sigaa.ensino.graduacao.dominio;

import static br.ufrn.arq.util.ValidatorUtil.validateRequired;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import br.ufrn.arq.dominio.GenericTipo;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;

/**
 * Entidade que representa uma área de conhecimento relacionada
 * ao curso de Bacharelado em Ciências e Tecnologia 
 * 
 * @author wendell
 *
 */
@Entity
@Table(name="area_conhecimento_ciencias_tecnologia", schema="graduacao")
public class AreaConhecimentoCienciasTecnologia extends GenericTipo implements Validatable {

	private boolean ativo = true;
	
	@Override @Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "id_area_conhecimento_ciencias_tecnologia")
	public int getId() {
		return super.getId();
	}
	
	@Override
	public String getDenominacao() {
		return super.getDenominacao();
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	public ListaMensagens validate() {
		ListaMensagens erros = new ListaMensagens();
		validateRequired(getDenominacao(), "Denominação", erros);
		
		return erros;
	}
}
