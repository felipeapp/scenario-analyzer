/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '05/02/2007'
 *
 */
package br.ufrn.sigaa.prodocente.producao.dominio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;

/**
 * Tipos de categoria funcional que um docente pode assumir. Ex: Prof. Titular, Prof. Adjunto, etc.
 * 
 * @author Eric
 *
 */
@Entity
@Table(schema="prodocente",name="categoria_funcional")
public class CategoriaFuncional implements Validatable {

	public static final CategoriaFuncional PROF_TITULAR = new CategoriaFuncional(1);

	public static final CategoriaFuncional LIVRE_DOCENCIA = new CategoriaFuncional(2);

	public static final CategoriaFuncional PROF_ADJUNTO = new CategoriaFuncional(3);

	public static final CategoriaFuncional PROF_ASSISTENTE = new CategoriaFuncional(4);

	public static final CategoriaFuncional PROF_AUXILIAR = new CategoriaFuncional(5);

	public static final CategoriaFuncional PROF_SUBSTITUTO = new CategoriaFuncional(6);

	public static final CategoriaFuncional PROF_NIVEL_MEDIO = new CategoriaFuncional(7);

	public static final CategoriaFuncional TECNICO = new CategoriaFuncional(8);
	
	public static final CategoriaFuncional PROF_VISITANTE = new CategoriaFuncional(9);
	
	public static final CategoriaFuncional ASSOCIADO = new CategoriaFuncional(10);
	
	public static final CategoriaFuncional PROF_ENSINO_BASICO_TECNICO_TECNOLOGICO = new CategoriaFuncional(11);

	@Id @GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	parameters={ @Parameter(name="hibernate_sequence", value="public.hibernate_sequence") })
	@Column(name = "id_categoria_funcional", nullable = false)
	private int id;

	@Column(name = "descricao")
	private String descricao;


	public CategoriaFuncional(int id) {
		this.id = id;
	}

	public CategoriaFuncional() {
	}

	/* (non-Javadoc)
	 * @see br.ufrn.arq.validacao.Validatable#validate()
	 */
	public ListaMensagens validate() {
		return null;
	}

	/* (non-Javadoc)
	 * @see br.ufrn.arq.dominio.PersistDB#getId()
	 */
	public int getId() {
		return id;
	}

	/* (non-Javadoc)
	 * @see br.ufrn.arq.dominio.PersistDB#setId(int)
	 */
	public void setId(int id) {
		this.id = id;

	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

}