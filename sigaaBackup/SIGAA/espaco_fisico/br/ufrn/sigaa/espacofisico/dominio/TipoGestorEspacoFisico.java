/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 08/12/2008
 *
 */
package br.ufrn.sigaa.espacofisico.dominio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import br.ufrn.arq.dominio.GenericTipo;

/**
 * Entidade que caracteriza os diferentes tipos de gestor do espaço físico
 * 
 * @author Henrique Andre
 * @author wendell
 * 
 */
@Entity
@Table(name = "tipo_gestor_espaco_fisico", schema = "espaco_fisico")
public class TipoGestorEspacoFisico extends GenericTipo {

	public static final TipoGestorEspacoFisico GESTAO_COMPLETA = new TipoGestorEspacoFisico(1);
	public static final TipoGestorEspacoFisico GESTAO_RESERVAS = new TipoGestorEspacoFisico(2);
	
	public TipoGestorEspacoFisico() {
		super();
	}
	
	public TipoGestorEspacoFisico(int id) {
		this();
		setId(id);
	}
	
	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "id_tipo_gestor_espaco_fisico")
	public int getId() {
		return super.getId();
	}

	public String getDenominacao() {
		return super.getDenominacao();
	}
	
}
