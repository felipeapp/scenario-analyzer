/*
 * EntidadesMarcadasParaExportacao.java
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
 *    Guarda os título e autoridades que foram marcados para exportação na catalogação.
 *    Assim o bibliotecário pode exportar vários título de uma única vez, sem precisar buscá-los.
 *    Igual como ocorre hoje com o catbib
 *    
 *
 * @author jadson
 * @since 08/06/2009
 * @version 1.0 criacao da classe
 *
 */
@Entity
@Table(name="entidades_marcadas_para_exportacao", schema="biblioteca")
public class EntidadesMarcadasParaExportacao implements PersistDB{


	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
           parameters={ @Parameter(name="sequence_name", value="biblioteca.hibernate_sequence") })	
	@Column(name="id_entidades_marcadas_para_exportacao")
	private int id;

	/**
	 * Campo para verificar se o que foi marcado para exportação foi um título 
	 */
	@Column(name="id_titulo_catalografico")
	private Integer idTituloCatalografico;
	
	
	/**
	 * Campo para verificar se o que foi marcado para exportação foi uma autoridade
	 */
	@Column(name="id_autoridade")
	private Integer idAutoridade;
	
	
	/** 
	* guarda o usuário que marcou o título/autoridade para exportação.                       
	* Como ocorre frequentemente, se existir mais de um usuário catalogando ao mesmo tempo   
	* o usuário só vai exportar os que ele catalogou. Evitando duplicação exportação.        
	*/
	@Column(name="id_usuario_marcou_exportacao")
	private Integer idUsuarioMarcouExportacao;
	
	
	
	/**
	 * Construtor padrão
	 */
	public EntidadesMarcadasParaExportacao() {
		
	}


	/**
	 * Construtor para marcar a exportação de Títulos
	 * 
	 * @param tituloCatalografico
	 * @param autoridade
	 * @param usuarioGerouArquivo
	 * @param exportado
	 */
	public EntidadesMarcadasParaExportacao( TituloCatalografico tituloCatalografico, Usuario usuarioMarcouExportacao) {
		this.idTituloCatalografico = tituloCatalografico.getId();
		this.idUsuarioMarcouExportacao = usuarioMarcouExportacao.getId();
	}

	
	/**
	 * Construtor para marcar a exportação de Autoridades
	 * 
	 * @param tituloCatalografico
	 * @param autoridade
	 * @param usuarioGerouArquivo
	 * @param exportado
	 */
	public EntidadesMarcadasParaExportacao( Autoridade autoridade, Usuario usuarioMarcouExportacao) {
		this.idAutoridade = autoridade.getId();
		this.idUsuarioMarcouExportacao = usuarioMarcouExportacao.getId();
	}
	
	
	

	public Integer getIdTituloCatalografico() {
		return idTituloCatalografico;
	}


	public void setIdTituloCatalografico(Integer idTituloCatalografico) {
		this.idTituloCatalografico = idTituloCatalografico;
	}


	public Integer getIdAutoridade() {
		return idAutoridade;
	}


	public void setIdAutoridade(Integer idAutoridade) {
		this.idAutoridade = idAutoridade;
	}


	public Integer getIdUsuarioMarcouExportacao() {
		return idUsuarioMarcouExportacao;
	}


	public void setIdUsuarioMarcouExportacao(Integer idUsuarioMarcouExportacao) {
		this.idUsuarioMarcouExportacao = idUsuarioMarcouExportacao;
	}


	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
}
