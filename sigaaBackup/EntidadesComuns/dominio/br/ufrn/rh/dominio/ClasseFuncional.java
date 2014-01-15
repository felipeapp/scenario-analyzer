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

	/** Parâmetro que representa o id da classe funcional 'Auxiliar' para docente ensino superior*/
	public static final int ID_DOCENTE3GRAU_AUXILIAR = ParametroHelper.getInstance().getParametroInt(ParametrosClasseFuncional.ID_DOCENTE3GRAU_AUXILIAR);
	/** Parâmetro que representa o id da classe funcional 'Assistente' para docente ensino superior*/
	public static final int ID_DOCENTE3GRAU_ASSISTENTE = ParametroHelper.getInstance().getParametroInt(ParametrosClasseFuncional.ID_DOCENTE3GRAU_ASSISTENTE);
	/** Parâmetro que representa o id da classe funcional 'Adjunto' para docente ensino superior*/
	public static final int ID_DOCENTE3GRAU_ADJUNTO = ParametroHelper.getInstance().getParametroInt(ParametrosClasseFuncional.ID_DOCENTE3GRAU_ADJUNTO);
	/** Parâmetro que representa o id da classe funcional 'Associador' para docente ensino superior*/
	public static final int ID_DOCENTE3GRAU_ASSOCIADO = ParametroHelper.getInstance().getParametroInt(ParametrosClasseFuncional.ID_DOCENTE3GRAU_ASSOCIADO);
	/** Parâmetro que representa o id da classe funcional 'Titular' para docente ensino superior*/
	public static final int ID_DOCENTE3GRAU_TITULAR = ParametroHelper.getInstance().getParametroInt(ParametrosClasseFuncional.ID_DOCENTE3GRAU_TITULAR);
	
	/** Parâmetro que representa o id da classe funcional 'A' para docente ensino Médio*/
	public static final int ID_DOCENTE12GRAU_A = ParametroHelper.getInstance().getParametroInt(ParametrosClasseFuncional.ID_DOCENTE12GRAU_A);
	/** Parâmetro que representa o id da classe funcional 'B' para docente ensino Médio*/
	public static final int ID_DOCENTE12GRAU_B = ParametroHelper.getInstance().getParametroInt(ParametrosClasseFuncional.ID_DOCENTE12GRAU_B);
	/** Parâmetro que representa o id da classe funcional 'C' para docente ensino Médio*/
	public static final int ID_DOCENTE12GRAU_C = ParametroHelper.getInstance().getParametroInt(ParametrosClasseFuncional.ID_DOCENTE12GRAU_C);
	/** Parâmetro que representa o id da classe funcional 'D' para docente ensino Médio*/
	public static final int ID_DOCENTE12GRAU_D = ParametroHelper.getInstance().getParametroInt(ParametrosClasseFuncional.ID_DOCENTE12GRAU_D);
	/** Parâmetro que representa o id da classe funcional 'E' para docente ensino Médio*/
	public static final int ID_DOCENTE12GRAU_E = ParametroHelper.getInstance().getParametroInt(ParametrosClasseFuncional.ID_DOCENTE12GRAU_E);	
	/** Parâmetro que representa o id da classe funcional 'S' para docente ensino Médio*/
	public static final int ID_DOCENTE12GRAU_S = ParametroHelper.getInstance().getParametroInt(ParametrosClasseFuncional.ID_DOCENTE12GRAU_S);
	
	/** Parâmetro que representa o id da classe funcional 'DI' para docente ensino Médio*/
	public static final int ID_DOCENTE12GRAU_DI = ParametroHelper.getInstance().getParametroInt(ParametrosClasseFuncional.ID_DOCENTE12GRAU_DI);
	/** Parâmetro que representa o id da classe funcional 'DII' para docente ensino Médio*/
	public static final int ID_DOCENTE12GRAU_DII = ParametroHelper.getInstance().getParametroInt(ParametrosClasseFuncional.ID_DOCENTE12GRAU_DII);
	/** Parâmetro que representa o id da classe funcional 'DIII' para docente ensino Médio*/
	public static final int ID_DOCENTE12GRAU_DIII = ParametroHelper.getInstance().getParametroInt(ParametrosClasseFuncional.ID_DOCENTE12GRAU_DIII);
	/** Parâmetro que representa o id da classe funcional 'DIV' para docente ensino Médio*/
	public static final int ID_DOCENTE12GRAU_DIV = ParametroHelper.getInstance().getParametroInt(ParametrosClasseFuncional.ID_DOCENTE12GRAU_DIV);
	/** Parâmetro que representa o id da classe funcional 'DV' para docente ensino Médio*/
	public static final int ID_DOCENTE12GRAU_DV = ParametroHelper.getInstance().getParametroInt(ParametrosClasseFuncional.ID_DOCENTE12GRAU_DV);

	/** Parâmetro que representa o id da classe funcional 'A' para Técnico Administrativo*/
	public static final int ID_TECNICO_ADMINISTRATIVO_A = ParametroHelper.getInstance().getParametroInt(ParametrosClasseFuncional.ID_TECNICO_ADMINISTRATIVO_A);
	/** Parâmetro que representa o id da classe funcional 'B' para Técnico Administrativo*/
	public static final int ID_TECNICO_ADMINISTRATIVO_B = ParametroHelper.getInstance().getParametroInt(ParametrosClasseFuncional.ID_TECNICO_ADMINISTRATIVO_B);
	/** Parâmetro que representa o id da classe funcional 'C' para Técnico Administrativo*/
	public static final int ID_TECNICO_ADMINISTRATIVO_C = ParametroHelper.getInstance().getParametroInt(ParametrosClasseFuncional.ID_TECNICO_ADMINISTRATIVO_C);
	/** Parâmetro que representa o id da classe funcional 'D' para Técnico Administrativo*/
	public static final int ID_TECNICO_ADMINISTRATIVO_D = ParametroHelper.getInstance().getParametroInt(ParametrosClasseFuncional.ID_TECNICO_ADMINISTRATIVO_D);
	/** Parâmetro que representa o id da classe funcional 'E' para Técnico Administrativo*/
	public static final int ID_TECNICO_ADMINISTRATIVO_E = ParametroHelper.getInstance().getParametroInt(ParametrosClasseFuncional.ID_TECNICO_ADMINISTRATIVO_E);
	/** Parâmetro que representa o id da classe funcional 'S' para Técnico Administrativo*/
	public static final int ID_TECNICO_ADMINISTRATIVO_S = ParametroHelper.getInstance().getParametroInt(ParametrosClasseFuncional.ID_TECNICO_ADMINISTRATIVO_S);
	/** Parâmetro que representa o id para servidores que não possuem classe funcional informada */
	public static final int NAO_INFORMADA = ParametroHelper.getInstance().getParametroInt(ParametrosClasseFuncional.NAO_INFORMADA);
	
	
	
	/** Identificador da entidade */
	@Id
	@Column(name = "id_classe_funcional", nullable = false)
	private int id;

	/** DenominaÃ§Ã£o da classe funcional **/
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
	 * Sigla da Classe Funcional. Utilizada como identificação na leitura do
	 * arquivo siape. A Classe pode vir identificada através do código ou
	 * através da Sigla
	 */
	@Column(name = "sigla")
	private String sigla;

	/**
	 * Visto que não existe ordem com o ID, fica necessário este campo que
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
	 * Retorna uma denominação formatada para combos
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
