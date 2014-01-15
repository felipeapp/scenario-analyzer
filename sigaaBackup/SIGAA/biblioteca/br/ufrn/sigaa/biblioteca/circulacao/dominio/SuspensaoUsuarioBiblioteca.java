package br.ufrn.sigaa.biblioteca.circulacao.dominio;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.HashCodeUtil;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.sigaa.biblioteca.util.CirculacaoUtil;

/**
 * <p>Entidade que guarda as informa��es do per�odo de suspens�o um usu�rio biblioteca.</p>
 * 
 * <p>Suspens�es podem ser de dois tipo:
 * 		<ul>
 * 			<li>Autom�ricas:  Criadas pelo sistema, est�o ligadas ao empr�stimo que a gerou.</li>
 * 			<li>Manuais: Criadas pelo usu�rio: est�o ligas diretamente ao usu�rio biblioteca</li>
 * 		</ul>
 * </p>
 * 
 * @author Fred_Castro
 * 
 */

@Entity
@Table(name = "suspensao_usuario_biblioteca", schema = "biblioteca")
public class SuspensaoUsuarioBiblioteca extends PunicaoAtrasoEmprestimoBiblioteca implements Validatable{

	/**
	 * O id
	 */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
           parameters={ @Parameter(name="sequence_name", value="biblioteca.penalidades_usuario_sequence") })
	@Column(name = "id_suspensao_usuario_biblioteca", nullable = false)
	protected int id;
	
	
	/** Quando o Usu�rio foi suspenso. */
	@Temporal(TemporalType.DATE)
	@Column(name = "data_inicio")
	private Date dataInicio;
	
	/** Quando a suspens�o acaba. */
	@Temporal(TemporalType.DATE)
	@Column(name = "data_fim")
	private Date dataFim;
	
	
	public SuspensaoUsuarioBiblioteca(){}
	
	
	
	/**
	 * Calcula a quantidade de dias que o usu�rio est� suspenso em "tempo real", ou seja, n�o � a
	 * quantidade de dias que o usu�rio pegou de suspens�o, mas sim a quantidade de dias que o usu�rio
	 * est� suspenso atualmente.
	 * 
	 * @return A quantidade de dias que o usu�rio ainda est� suspenso. Se n�o tiver suspenso, retorna zero.
	 */
	public int calculaQuantidadeDiasSuspenso(){
		int dias = CirculacaoUtil.calculaDiasEmAtrasoBiblioteca(new Date(), dataFim);
		
		if (dias < 0)
			return 0;
		
		return dias;
	}
	
	/**
	 * Verifica se o usu�rio est� suspenso.
	 *
	 * @return
	 */
	public boolean usuarioEstaSuspenso(){
		return calculaQuantidadeDiasSuspenso() > 0;
	}
	
	/**
	 * 
	 * Cria uma nova suspens�o sempre come�ando a partir de amanh� e com uma quantidade de dias > 0
	 *
	 * @param inicio
	 * @param qtdDiasSuspensao
	 */
	public void criaNovaSuspensao(Date inicio, int qtdDiasSuspensao){
			this.dataInicio = inicio;
			
			Calendar c = Calendar.getInstance();
			c.setTime(inicio);
			c.add(Calendar.DAY_OF_MONTH, qtdDiasSuspensao);
			c.setTime(CalendarUtils.configuraTempoDaData(c.getTime(), 23, 59, 59, 999));
			
			this.dataFim = c.getTime();
	}
	
	
	
	/**
	 * Valida os dados quando uma suspens�o � criada.
	 * 
	 * @see br.ufrn.arq.negocio.validacao.Validatable#validate()
	 */
	public ListaMensagens validate() {
		
		ListaMensagens mensagens = new ListaMensagens();
		
		if (dataInicio == null)
			mensagens.addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Data Inicial");
		
		if (dataFim == null)
			mensagens.addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Data Final");
		
		if (dataInicio != null && dataFim != null)
		if (CalendarUtils.estorouPrazo(dataFim, dataInicio))
			mensagens.addMensagem(MensagensArquitetura.DATA_POSTERIOR_A, "Data Final", new SimpleDateFormat("dd/MM/yyyy").format(dataInicio));
		
		if(manual == true){                  // cadastrando uma suspens�o manual
			if(StringUtils.isEmpty(motivoCadastro))
				mensagens.addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Motivo");
		}
			
		return mensagens;
	}
	
	
	@Override
	public boolean equals (Object obj){
		return EqualsUtil.testEquals(this, obj, "id");
	}
	
	@Override
	public int hashCode (){
		return HashCodeUtil.hashAll(id);
	}
	
	
	// Sets e gets
	
	public Date getDataInicio() {
		return dataInicio;
	}

	public void setDataInicio(Date dataInicio) {
		this.dataInicio = dataInicio;
	}

	public Date getDataFim() {
		return dataFim;
	}

	public void setDataFim(Date dataFim) {
		this.dataFim = dataFim;
	}


	public int getId() {
		return id;
	}


	public void setId(int id) {
		this.id = id;
	}
	
	
}