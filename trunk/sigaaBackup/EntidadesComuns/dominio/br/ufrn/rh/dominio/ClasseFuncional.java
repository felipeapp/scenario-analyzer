/*
 * ClasseServidor.java
 *
 * Created on 14 de Maio de 2007, 16:30
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package br.ufrn.rh.dominio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.parametrizacao.ParametroHelper;

/**
 * ClasseServidor - Classes funcionais dos servidores. Ex.: auxiliar,
 * assistente, adjunto, etc.
 * 
 * @author Gleydson
 */
@Entity
@Table(name = "classe_funcional", schema = "rh")
public class ClasseFuncional implements PersistDB {

	/** Par�metro que representa o id da classe funcional 'Auxiliar' para docente ensino superior*/
	public static final int ID_DOCENTE3GRAU_AUXILIAR = ParametroHelper.getInstance().getParametroInt(ParametrosClasseFuncional.ID_DOCENTE3GRAU_AUXILIAR);
	/** Par�metro que representa o id da classe funcional 'Assistente' para docente ensino superior*/
	public static final int ID_DOCENTE3GRAU_ASSISTENTE = ParametroHelper.getInstance().getParametroInt(ParametrosClasseFuncional.ID_DOCENTE3GRAU_ASSISTENTE);
	/** Par�metro que representa o id da classe funcional 'Adjunto' para docente ensino superior*/
	public static final int ID_DOCENTE3GRAU_ADJUNTO = ParametroHelper.getInstance().getParametroInt(ParametrosClasseFuncional.ID_DOCENTE3GRAU_ADJUNTO);
	/** Par�metro que representa o id da classe funcional 'Associador' para docente ensino superior*/
	public static final int ID_DOCENTE3GRAU_ASSOCIADO = ParametroHelper.getInstance().getParametroInt(ParametrosClasseFuncional.ID_DOCENTE3GRAU_ASSOCIADO);
	/** Par�metro que representa o id da classe funcional 'Titular' para docente ensino superior*/
	public static final int ID_DOCENTE3GRAU_TITULAR = ParametroHelper.getInstance().getParametroInt(ParametrosClasseFuncional.ID_DOCENTE3GRAU_TITULAR);
	
	/** Par�metro que representa o id da classe funcional 'A' para docente ensino M�dio*/
	public static final int ID_DOCENTE12GRAU_A = ParametroHelper.getInstance().getParametroInt(ParametrosClasseFuncional.ID_DOCENTE12GRAU_A);
	/** Par�metro que representa o id da classe funcional 'B' para docente ensino M�dio*/
	public static final int ID_DOCENTE12GRAU_B = ParametroHelper.getInstance().getParametroInt(ParametrosClasseFuncional.ID_DOCENTE12GRAU_B);
	/** Par�metro que representa o id da classe funcional 'C' para docente ensino M�dio*/
	public static final int ID_DOCENTE12GRAU_C = ParametroHelper.getInstance().getParametroInt(ParametrosClasseFuncional.ID_DOCENTE12GRAU_C);
	/** Par�metro que representa o id da classe funcional 'D' para docente ensino M�dio*/
	public static final int ID_DOCENTE12GRAU_D = ParametroHelper.getInstance().getParametroInt(ParametrosClasseFuncional.ID_DOCENTE12GRAU_D);
	/** Par�metro que representa o id da classe funcional 'E' para docente ensino M�dio*/
	public static final int ID_DOCENTE12GRAU_E = ParametroHelper.getInstance().getParametroInt(ParametrosClasseFuncional.ID_DOCENTE12GRAU_E);	
	/** Par�metro que representa o id da classe funcional 'S' para docente ensino M�dio*/
	public static final int ID_DOCENTE12GRAU_S = ParametroHelper.getInstance().getParametroInt(ParametrosClasseFuncional.ID_DOCENTE12GRAU_S);
	
	/** Par�metro que representa o id da classe funcional 'DI' para docente ensino M�dio*/
	public static final int ID_DOCENTE12GRAU_DI = ParametroHelper.getInstance().getParametroInt(ParametrosClasseFuncional.ID_DOCENTE12GRAU_DI);
	/** Par�metro que representa o id da classe funcional 'DII' para docente ensino M�dio*/
	public static final int ID_DOCENTE12GRAU_DII = ParametroHelper.getInstance().getParametroInt(ParametrosClasseFuncional.ID_DOCENTE12GRAU_DII);
	/** Par�metro que representa o id da classe funcional 'DIII' para docente ensino M�dio*/
	public static final int ID_DOCENTE12GRAU_DIII = ParametroHelper.getInstance().getParametroInt(ParametrosClasseFuncional.ID_DOCENTE12GRAU_DIII);
	/** Par�metro que representa o id da classe funcional 'DIV' para docente ensino M�dio*/
	public static final int ID_DOCENTE12GRAU_DIV = ParametroHelper.getInstance().getParametroInt(ParametrosClasseFuncional.ID_DOCENTE12GRAU_DIV);
	/** Par�metro que representa o id da classe funcional 'DV' para docente ensino M�dio*/
	public static final int ID_DOCENTE12GRAU_DV = ParametroHelper.getInstance().getParametroInt(ParametrosClasseFuncional.ID_DOCENTE12GRAU_DV);

	/** Par�metro que representa o id da classe funcional 'A' para T�cnico Administrativo*/
	public static final int ID_TECNICO_ADMINISTRATIVO_A = ParametroHelper.getInstance().getParametroInt(ParametrosClasseFuncional.ID_TECNICO_ADMINISTRATIVO_A);
	/** Par�metro que representa o id da classe funcional 'B' para T�cnico Administrativo*/
	public static final int ID_TECNICO_ADMINISTRATIVO_B = ParametroHelper.getInstance().getParametroInt(ParametrosClasseFuncional.ID_TECNICO_ADMINISTRATIVO_B);
	/** Par�metro que representa o id da classe funcional 'C' para T�cnico Administrativo*/
	public static final int ID_TECNICO_ADMINISTRATIVO_C = ParametroHelper.getInstance().getParametroInt(ParametrosClasseFuncional.ID_TECNICO_ADMINISTRATIVO_C);
	/** Par�metro que representa o id da classe funcional 'D' para T�cnico Administrativo*/
	public static final int ID_TECNICO_ADMINISTRATIVO_D = ParametroHelper.getInstance().getParametroInt(ParametrosClasseFuncional.ID_TECNICO_ADMINISTRATIVO_D);
	/** Par�metro que representa o id da classe funcional 'E' para T�cnico Administrativo*/
	public static final int ID_TECNICO_ADMINISTRATIVO_E = ParametroHelper.getInstance().getParametroInt(ParametrosClasseFuncional.ID_TECNICO_ADMINISTRATIVO_E);
	/** Par�metro que representa o id da classe funcional 'S' para T�cnico Administrativo*/
	public static final int ID_TECNICO_ADMINISTRATIVO_S = ParametroHelper.getInstance().getParametroInt(ParametrosClasseFuncional.ID_TECNICO_ADMINISTRATIVO_S);
	/** Par�metro que representa o id para servidores que n�o possuem classe funcional informada */
	public static final int NAO_INFORMADA = ParametroHelper.getInstance().getParametroInt(ParametrosClasseFuncional.NAO_INFORMADA);
	
	
	
	/** Identificador da entidade */
	@Id
	@Column(name = "id_classe_funcional", nullable = false)
	private int id;

	/** Denominação da classe funcional **/
	@Column(name = "denominacao")
	private String denominacao;

	/**
	 * Identificador da categoria do servidor ao qual a classe funcional
	 * pertence
	 **/
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_tipo_categoria", referencedColumnName = "id_categoria")
	private Categoria categoria;

	/**
	 * Sigla da Classe Funcional. Utilizada como identifica��o na leitura do
	 * arquivo siape. A Classe pode vir identificada atrav�s do c�digo ou
	 * atrav�s da Sigla
	 */
	@Column(name = "sigla")
	private String sigla;

	/**
	 * Visto que n�o existe ordem com o ID, fica necess�rio este campo que
	 * apresenta a ordem de menor para maior classes dependendo do tipo de
	 * servidor.
	 */
	@Column(name = "ordenacao_classe_funcional")
	private Integer ordenacaoClasseFuncional;

	/** Creates a new instance of ClasseServidor */
	public ClasseFuncional() {
	}

	public ClasseFuncional(int id) {
		this.id = id;
	}

	public String getDenominacao() {
		return denominacao;
	}

	public void setDenominacao(String denominacao) {
		this.denominacao = denominacao;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Categoria getCategoria() {
		return categoria;
	}

	public void setCategoria(Categoria categoria) {
		this.categoria = categoria;
	}

	public String getSigla() {
		return sigla;
	}

	public void setSigla(String sigla) {
		this.sigla = sigla;
	}

	/**
	 * Retorna uma denomina��o formatada para combos
	 * */
	public String getDenominacaoFormatada() {
		return "(" + sigla + ") " + denominacao;
	}

	public Integer getOrdenacaoClasseFuncional() {
		return ordenacaoClasseFuncional;
	}

	public void setOrdenacaoClasseFuncional(Integer ordenacaoClasseFuncional) {
		this.ordenacaoClasseFuncional = ordenacaoClasseFuncional;
	}

}
