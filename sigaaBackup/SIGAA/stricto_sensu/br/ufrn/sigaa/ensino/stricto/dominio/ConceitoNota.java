/* 
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 *
 */
package br.ufrn.sigaa.ensino.stricto.dominio;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.ensino.stricto.negocio.ConceitoNotaHelper;

/**
 * Classe de domínio que representa a relação entre os conceitos
 * usados na pós stricto-sensu e a média do discente
 * @author David Pereira
 */
@Entity
@Table(name="conceito_nota", schema="stricto_sensu")
public class ConceitoNota implements PersistDB {

	
	/*public static final int A = ConceitoNotaHelper.getValorConceito("A");
	public static final int B = ConceitoNotaHelper.getValorConceito("B");
	public static final int C = ConceitoNotaHelper.getValorConceito("C");
	public static final int D = ConceitoNotaHelper.getValorConceito("D");
	public static final int E = ConceitoNotaHelper.getValorConceito("E");*/

	/**
	 * Chave primária do conceito.
	 */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	          parameters={ @Parameter(name="sequence_name", value="public.hibernate_sequence") }) 
	private int id;

	/**
	 * conceito: A, B, C, D, E
	 */
	private String conceito;

	/**
	 * valor do conceito: 5, 4, 3, 2, 1 (respectivamente)
	 */
	private int valor;

	public String getConceito() {
		return conceito;
	}

	public void setConceito(String conceito) {
		this.conceito = conceito;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getValor() {
		return valor;
	}

	public void setValor(int valor) {
		this.valor = valor;
	}

	/**
	 * Retorna a descrição do conceito relacionado a média passada.
	 * 
	 * @param media
	 * @return
	 */
	public static String getDescricao(Double media) {
		String descricao = ConceitoNotaHelper.getDescricaoConceito(media);
		if(ValidatorUtil.isEmpty(descricao)) {
			return "-";
		}
		return descricao;
	}

}
