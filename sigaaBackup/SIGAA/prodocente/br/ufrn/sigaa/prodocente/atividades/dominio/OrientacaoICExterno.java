/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '08/04/2009'
 *
 */
package br.ufrn.sigaa.prodocente.atividades.dominio;

import java.util.Date;
import java.util.HashMap;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.dominio.InstituicoesEnsino;
import br.ufrn.sigaa.pessoa.dominio.Servidor;
import br.ufrn.sigaa.prodocente.relatorios.dominio.ViewAtividadeBuilder;

/**
 * Representa uma Orientação de Iniciação Científica realizada fora da Instituição
 * 
 * @author Jean Guerethes
 */

@Entity
@Table(name = "orientacao_externa",schema="prodocente") 
public class OrientacaoICExterno implements Validatable, ViewAtividadeBuilder {

	/** Constantes dos Tipos de bolsa */
	public static final int INSTITUCIONAL = 1;
	public static final int PIBIC = 2;
	public static final int OUTRA = 3;
	
		
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id_orientacao_externa", nullable = false)
    private int id;

	/**
	 * Ao remover as orientações, as mesmas não serão removidas da base de dados,
	 * apenas o campo ativo será marcado como FALSE
	 */
	@Column(name = "ativo")
	private Boolean ativo = true;

    @Column(name = "nome_orientando")
    private String nomeOrientando;
    
    @Column(name = "data_inicio")
    @Temporal(TemporalType.DATE)
    private Date dataInicio;

    @Column(name = "data_fim")
    @Temporal(TemporalType.DATE)
    private Date dataFim;
    
	@Column(name = "tipo_bolsa")
    private int tipoBolsa;
	
	@Column(name = "tipo_bolsa_outra")
	private String tipoBolsaOutra;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_instituicao_ensino")
    private InstituicoesEnsino instituicao;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_orientador")
	private Servidor orientador;
	
	public ListaMensagens validate() {
		ListaMensagens erros = new ListaMensagens();
		ValidatorUtil.validateRequired(nomeOrientando, "Nome do Orientando", erros);
		ValidatorUtil.validateRequired(dataInicio, "Data de Início da Orientação", erros);
		ValidatorUtil.validateRequired(dataFim, "Data do Fim da Orientação", erros);
		ValidatorUtil.validateRequired(tipoBolsa, "Tipo da Bolsa", erros);
		ValidatorUtil.validateRequired(instituicao, "Instituição de Ensino", erros);
		return erros;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getNomeOrientando() {
		return nomeOrientando;
	}

	public void setNomeOrientando(String nomeOrientando) {
		this.nomeOrientando = nomeOrientando;
	}

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

	public int getTipoBolsa() {
		return tipoBolsa;
	}

	public void setTipoBolsa(int tipoBolsa) {
		this.tipoBolsa = tipoBolsa;
	}

	public String getTipoBolsaOutra() {
		return tipoBolsaOutra;
	}

	public void setTipoBolsaOutra(String tipoBolsaOutra) {
		this.tipoBolsaOutra = tipoBolsaOutra;
	}

	public InstituicoesEnsino getInstituicao() {
		return instituicao;
	}

	public void setInstituicao(InstituicoesEnsino instituicao) {
		this.instituicao = instituicao;
	}

	public Boolean getAtivo() {
		return ativo;
	}

	public void setAtivo(Boolean ativo) {
		this.ativo = ativo;
	}

	@Transient
	public String getItemView() {
		return "  <td>"+ getNomeOrientando()+ "</td><td>Externo</td>";
	}

	@Transient
	public String getTituloView() {
		return  "    <td>Nome do Orientando</td><td>Tipo</td>";
	}

	@Transient
	public HashMap<String, String> getItens() {
		HashMap<String, String> itens = new HashMap<String, String>();
		itens.put("nomeOrientando", null);
		itens.put("dataInicio", null);
		itens.put("dataFim", null);
		return itens;
	}
	
	@Transient
	public float getQtdBase() {
		return 1;
	}

	/**
	 * @param orientador the orientador to set
	 */
	public void setOrientador(Servidor orientador) {
		this.orientador = orientador;
	}

	/**
	 * @return the orientador
	 */
	public Servidor getOrientador() {
		return orientador;
	}
  
}
