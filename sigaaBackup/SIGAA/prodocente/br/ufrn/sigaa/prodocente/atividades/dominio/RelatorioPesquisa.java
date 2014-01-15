/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '15/03/2007'
 *
 */
package br.ufrn.sigaa.prodocente.atividades.dominio;

import java.util.Date;
import java.util.HashMap;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.Formatador;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.pessoa.dominio.Servidor;
import br.ufrn.sigaa.prodocente.producao.dominio.TipoParticipacao;
import br.ufrn.sigaa.prodocente.producao.dominio.TipoRegiao;
import br.ufrn.sigaa.prodocente.relatorios.dominio.ViewAtividadeBuilder;
import br.ufrn.sigaa.projetos.dominio.EntidadeFinanciadora;

/**
 * Entidade que representa as informações de Relatorio Pesquisa
 * 
 * @author Mario Melo
 *
 */
@Deprecated
@Entity
@Table(name = "relatorio",schema="prodocente")
public class RelatorioPesquisa implements Validatable, ViewAtividadeBuilder {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "id_relatorio", nullable = false)
	private int id;

	/**
	 * Ao remover as produções e atividades, as mesmas não serão removidas da base de dados,
	 * apenas o campo ativo será marcado como FALSE
	 */
	@Column(name = "ativo")
	private Boolean ativo;
	
	@JoinColumn(name="id_agencia_financiadora",referencedColumnName="id_entidade_financiadora")
	@ManyToOne
	private EntidadeFinanciadora agencia = new EntidadeFinanciadora();
	
	@JoinColumn(name="id_participacao",referencedColumnName="id_tipo_participacao")
	@ManyToOne
	private TipoParticipacao participacao = new TipoParticipacao();

	@JoinColumn(name="id_servidor",referencedColumnName="id_servidor")
	@ManyToOne
	private Servidor servidor = new Servidor();

	@JoinColumn(name = "id_tipo_relatorio",referencedColumnName = "id_tipo_relatorio")
	@ManyToOne
	private TipoRelatorio tipoRelatorio = new TipoRelatorio();

	@JoinColumn(name="id_tipo_regiao",referencedColumnName="id_tipo_regiao" )
	@ManyToOne
	private TipoRegiao tipoRegiao = new TipoRegiao();

	@Column(name="titulo")
	private String titulo;

	@Column(name = "data_aprovacao")
    @Temporal(TemporalType.DATE)
    private Date dataAprovacao;

	@Column(name="informacao")
	private String informacao;

	@Column(name="destaque")
	private boolean destaque;

	@Column(name="pago")
	private boolean pago;

	@Column(name="relatorio_final")
	private boolean relatorioFinal;

	public TipoRegiao getTipoRegiao() {
		return tipoRegiao;
	}

	public void setTipoRegiao(TipoRegiao tipoRegiao) {
		this.tipoRegiao = tipoRegiao;
	}

	public Servidor getServidor() {
		return servidor;
	}

	public void setServidor(Servidor servidor) {
		this.servidor = servidor;
	}

	public TipoParticipacao getParticipacao() {
		return participacao;
	}

	public void setParticipacao(TipoParticipacao participacao) {
		this.participacao = participacao;
	}

	public ListaMensagens validate() {

		ListaMensagens lista = new ListaMensagens();
		ValidatorUtil.validateRequiredId(getServidor().getId(),"Docente", lista.getMensagens());
		ValidatorUtil.validateRequiredId(getAgencia().getId(), "Agência Financiadora", lista.getMensagens());
		ValidatorUtil.validateRequired(getDataAprovacao(), "Data de Aprovação", lista.getMensagens());
		ValidatorUtil.validateRequiredId(getParticipacao().getId(), "Tipo Participacao", lista.getMensagens());
		ValidatorUtil.validateRequiredId(getTipoRelatorio().getId(), "Tipo Relatório", lista.getMensagens());
		ValidatorUtil.validateRequiredId(getTipoRegiao().getId(), "Tipo Região", lista.getMensagens());

		return lista;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id=id;
	}

	/**
	 * Ao remover as produções e atividades, as mesmas não serão removidas da base de dados,
	 * apenas o campo ativo será marcado como FALSE
	 * @author Edson Anibal (ambar@info.ufrn.br)
	 */
	public Boolean getAtivo() {	return this.ativo; }

	/**
	 * Ao remover as produções e atividades, as mesmas não serão removidas da base de dados,
	 * apenas o campo ativo será marcado como FALSE
	 * @author Edson Anibal (ambar@info.ufrn.br)
	 */
	public void setAtivo(Boolean ativo)	{ this.ativo = ativo; }


	
	public EntidadeFinanciadora getAgencia() {
		return agencia;
	}

	public void setAgencia(EntidadeFinanciadora agencia) {
		this.agencia = agencia;
	}

	public Date getDataAprovacao() {
		return dataAprovacao;
	}

	public void setDataAprovacao(Date dataAprovacao) {
		this.dataAprovacao = dataAprovacao;
	}

	public boolean isDestaque() {
		return destaque;
	}

	public void setDestaque(boolean destaque) {
		this.destaque = destaque;
	}

	public String getInformacao() {
		return informacao;
	}

	public void setInformacao(String informacao) {
		this.informacao = informacao;
	}

	public boolean isPago() {
		return pago;
	}

	public void setPago(boolean pago) {
		this.pago = pago;
	}

	public boolean isRelatorioFinal() {
		return relatorioFinal;
	}

	public void setRelatorioFinal(boolean relatorioFinal) {
		this.relatorioFinal = relatorioFinal;
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public String getItemView() {
		return "  <td>"+titulo+ "</td>" +
			   "  <td style=\"text-align:center\">"+Formatador.getInstance().formatarData(dataAprovacao)+"</td>"+
			   "  <td>"+(pago ? "Sim":"Não" )+"</td>";

	}

	public String getTituloView() {
		return  "    <td>Atividade</td>" +
				"    <td style=\"text-align:center\">Data Publicação</td>" +
				"    <td>Remunerado</td>";
	}

	public HashMap<String, String> getItens() {
		HashMap<String, String> itens = new HashMap<String, String>();
		itens.put("titulo", null);
		itens.put("dataAprovacao", null);
		itens.put("pago", null);
		return itens;
	}

	public TipoRelatorio getTipoRelatorio() {
		return tipoRelatorio;
	}

	public void setTipoRelatorio(TipoRelatorio tipoRelatorio) {
		this.tipoRelatorio = tipoRelatorio;
	}

	public float getQtdBase() {
		return 1;
	}
}
