/*
 * MateriaisMarcadosParaGerarEtiquetas.java
 *
 * UNIVERSIDADE FEDERAL DO RIO GRANDE DO NORTE
 * Superintendência de Informática
 * Diretoria de Sistemas
 * Campos Universitário Lagoa Nova
 * Natal - RN - Brasil
 */
package br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.sigaa.dominio.Usuario;

/**
 *
 *   Na hora da inclusão de exemplares ou fascículos o usuário pode selecionar se o material 
 * terá etiqueta gerada para ele. O sistema vai salvar nessa entidade e depois na tela de impressão
 * de etiquetas o usuário pode gerar etiquetas para esses materiais, sem precisar realizar uma buscar. 
 *   Igual como ocorre com o ALEPH.
 *
 * @author jadson
 * @since 08/06/2009
 * @version 1.0 criacao da classe
 *
 */
@Entity
@Table(name="materiais_marcados_para_gerar_etiquetas", schema="biblioteca")
public class MateriaisMarcadosParaGerarEtiquetas  implements PersistDB{

	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
           parameters={ @Parameter(name="sequence_name", value="biblioteca.hibernate_sequence") })	
	@Column(name="id_materiais_marcados_para_gerar_etiquetas")
	private int id;

	/**
	 * O exemplar marcado para se gerar a etiqueta
	 */
	@Column(name="id_exemplar")
	private Integer idExemplar;
	
	/**
	 * O fascículo marcado para se gerar a etiqueta
	 */
	@Column(name="id_fasciculo")
	private Integer idFasciculo;
	
	
	/** 
	 * Guarda o usuário que gerou o arquivo para o caso de existir mais de um usuário catalogando ao mesmo tempo 
	 * o usuário só vai exportar os que ele catalogou. Evitando duplicação na exportação.
	 */                   
	@Column(name="id_usuario_marcou_geracao_etiqueta")
	private Integer idUsuarioMarcouGeracaoEtiqueta;
	
	
	
	/**
	 * Construtor padrão
	 */
	public MateriaisMarcadosParaGerarEtiquetas() {
		
	}



	/**
	 *   Construtor default
	 * 
	 * @param material
	 * @param usuarioMarcouGeracaoEtiqueta
	 * @param geradoEtiqueta
	 */
	public MateriaisMarcadosParaGerarEtiquetas(MaterialInformacional material, Usuario usuarioMarcouGeracaoEtiqueta) {
		if(material instanceof Exemplar)
			this.idExemplar = material.getId();
		else
			this.idFasciculo = material.getId();
		this.idUsuarioMarcouGeracaoEtiqueta = usuarioMarcouGeracaoEtiqueta.getId();
	}


	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Integer getIdExemplar() {
		return idExemplar;
	}


	public void setIdExemplar(Integer idExemplar) {
		this.idExemplar = idExemplar;
	}

	public Integer getIdFasciculo() {
		return idFasciculo;
	}


	public void setIdFasciculo(Integer idFasciculo) {
		this.idFasciculo = idFasciculo;
	}


	public Integer getIdUsuarioMarcouGeracaoEtiqueta() {
		return idUsuarioMarcouGeracaoEtiqueta;
	}

	public void setIdUsuarioMarcouGeracaoEtiqueta(Integer idUsuarioMarcouGeracaoEtiqueta) {
		this.idUsuarioMarcouGeracaoEtiqueta = idUsuarioMarcouGeracaoEtiqueta;
	}

	
	
}
