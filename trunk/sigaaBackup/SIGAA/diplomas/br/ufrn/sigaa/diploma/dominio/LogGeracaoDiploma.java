/*
 * Sistema Integrado de Gest�o de Atividades Acad�micas - SIGAA
 * Superintend�ncia de Inform�tica - UFRN
 *
 * Created on '26/08/2009'
 *
 */
package br.ufrn.sigaa.diploma.dominio;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.Collection;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.CalendarUtils;

/** Dados do registro da emiss�o/visualiza��o do diploma, para fins de auditoria.
 * @author �dipo Elder F. Melo
 *
 */
@Entity
@Table(schema = "diploma", name = "log_geracao_diploma")
public class LogGeracaoDiploma implements Validatable {

	/** Chave prim�ria. */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_log_geracao_diploma")
	private int id;
	
	/** Usu�rio que gerou o(s) diploma(s).*/
	@ManyToOne
	@JoinColumn(name = "id_registro_entrada")
	private RegistroEntrada registroEntrada;
	
	/** Data da gera��o do(s) diploma(s).*/
	private Date data;
	
	/** Lista separada por v�rgula dos n�meros de registro de diplomas gerados. */
	private String registros;
	
	/** Indica se o diploma gerado � de segunda via. */
	@Column(name = "segunda_via")
	private boolean segundaVia;
	
	/** Construtor padr�o. */
	public LogGeracaoDiploma() {
		segundaVia = false;
	}
	
	/** Construtor parametrizado. */
	public LogGeracaoDiploma(int id) {
		this.id = id;
	}
	
	/** Valida os dados: registroEntrada, dataVisualiza��o, registros
	 * @see br.ufrn.arq.negocio.validacao.Validatable#validate()
	 */
	public ListaMensagens validate() {
		return null;
	}

	/** Retorna a chave prim�ria.
	 * @see br.ufrn.arq.dominio.PersistDB#getId()
	 */
	public int getId() {
		return id;
	}

	/** Seta a chave prim�ria.
	 * @see br.ufrn.arq.dominio.PersistDB#setId(int)
	 */
	public void setId(int id) {
		this.id = id;
	}

	/** Retorna o usu�rio que gerou o(s) diploma(s).
	 * @return Usu�rio que gerou o(s) diploma(s).
	 */
	public RegistroEntrada getRegistroEntrada() {
		return registroEntrada;
	}

	/** Seta o usu�rio que gerou o(s) diploma(s).
	 * @param registroEntrada Usu�rio que gerou o(s) diploma(s).
	 */
	public void setRegistroEntrada(RegistroEntrada registroEntrada) {
		this.registroEntrada = registroEntrada;
	}

	/** Retorna a data da gera��o do(s) diploma(s).
	 * @return Data da gera��o do(s) diploma(s).
	 */
	public Date getData() {
		return data;
	}

	/** Seta a data da gera��o do(s) diploma(s). 
	 * @param data Data da gera��o do(s) diploma(s).
	 */
	public void setData(Date data) {
		this.data = data;
	}

	/** Retorna a lista separada por v�rgula dos n�meros de registro de diplomas gerados. 
	 * @return Lista separada por v�rgula dos n�meros de registro de diplomas gerados. 
	 */
	public String getRegistros() {
		return registros;
	}

	/** Seta a lista separada por v�rgula dos n�meros de registro de diplomas gerados.
	 * @param registros Lista separada por v�rgula dos n�meros de registro de diplomas gerados. 
	 */
	public void setRegistros(String registros) {
		this.registros = registros;
	}
	
	/** Seta a lista dos n�meros de registro de diplomas gerados.
	 * @param registros Lista separada por v�rgula dos n�meros de registro de diplomas gerados. 
	 */
	public void setRegistros(Collection<Integer> registros) {
		if (isEmpty(registros)) return;
		StringBuilder listaRegistros = new StringBuilder();
		int k = 0;
		for (Integer i : registros) {
			listaRegistros.append(i.intValue());
			if (++k < registros.size()) listaRegistros.append(", ");
		}
		setRegistros(listaRegistros.toString());
	}
	
	/**
	 * Retorna uma representa��o textual deste registro no formato: data de
	 * gera��o, seguido de '-', seguido do nome do usu�rio, seguido de ':'
	 * seguido da lista de n�mero de registros dos diplomas gerados.
	 */
	@Override
	public String toString() {
		return CalendarUtils.format(data, "dd/MM/yyyy hh:mm:ss") + " - "
				+ registroEntrada.getUsuario().getNome() + registros;
	}

	public boolean isSegundaVia() {
		return segundaVia;
	}

	public void setSegundaVia(boolean segundaVia) {
		this.segundaVia = segundaVia;
	}

}
