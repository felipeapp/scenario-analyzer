package br.ufrn.sigaa.pesquisa.dominio;

import java.util.Collection;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.projetos.dominio.OrcamentoDetalhado;
import br.ufrn.sigaa.projetos.dominio.Projeto;

/**
 * Classe que representa um projeto de apoio a um grupo de pesquisa.
 * 
 * @author Jean Guerethes
 */
@Entity
@Table(name = "projeto_apoio_grupo_pesquisa", schema = "pesquisa", uniqueConstraints = {})
public class ProjetoApoioGrupoPesquisa implements Validatable {

	/** Atributo utilizado para representar o ID do projeto de pesquisa */
	@Id @GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	parameters={ @Parameter(name="hibernate_sequence", value="public.hibernate_sequence") })
	@Column(name = "id_projeto_apoio_grupo_pesquisa")
	private int id;
	
	/** Atributo utilizado para representar o edital do projeto de Apoio ao Grupo de Pesquisa */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_edital_pesquisa", unique = false, nullable = true, insertable = true, updatable = true)
	private EditalPesquisa editalPesquisa;

	/** Projeto ao qual o Projeto de Apoio está vínculado */
	@ManyToOne(cascade = {CascadeType.ALL}, fetch = FetchType.LAZY)
	@JoinColumn(name = "id_projeto", unique = false, nullable = true, insertable = true, updatable = true)
	private Projeto projeto;

	/** Grupo de Pesquisa ao qual o Projeto de Apoio de Grupo de Pesquisa está vínculado */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_grupo_pesquisa", unique = false, nullable = true, insertable = true, updatable = true)
	private GrupoPesquisa grupoPesquisa;

	/** Campo que armazena a informação de qual tipo de integração há entre os projetos.*/
	private String integracao;

	/** Construtor Padrão */
	public ProjetoApoioGrupoPesquisa() {
		editalPesquisa = new EditalPesquisa();
		grupoPesquisa = new GrupoPesquisa();
		projeto = new Projeto();
	}
	
    public List<OrcamentoDetalhado> getOrcamentosDetalhados() {
    	return (List<OrcamentoDetalhado>) getProjeto().getOrcamento();
    }

    /**
     * Set o orçamento detalhado no orçamento presente na classe Projeto.
     * @param orcamentosDetalhados
     */
    public void setOrcamentosDetalhados(Collection<OrcamentoDetalhado> orcamentosDetalhados) {
    	this.getProjeto().setOrcamento(orcamentosDetalhados);
    }
	
    /**
     * Retornar se o orcamento detalhado foi adicionado ou não ao orçamento do projeto.
     * @param orcamentoDetalhado
     * @return
     */
    public boolean addOrcamentoDetalhado(OrcamentoDetalhado orcamentoDetalhado) {
    	getProjeto().setApoioGrupoPesquisa(true);
    	orcamentoDetalhado.setProjeto(getProjeto());
    	return getProjeto().getOrcamento().add(orcamentoDetalhado);
    }
    
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public EditalPesquisa getEditalPesquisa() {
		return editalPesquisa;
	}

	public void setEditalPesquisa(EditalPesquisa editalPesquisa) {
		this.editalPesquisa = editalPesquisa;
	}

	public Projeto getProjeto() {
		return projeto;
	}

	public void setProjeto(Projeto projeto) {
		this.projeto = projeto;
	}

	public GrupoPesquisa getGrupoPesquisa() {
		return grupoPesquisa;
	}

	public void setGrupoPesquisa(GrupoPesquisa grupoPesquisa) {
		this.grupoPesquisa = grupoPesquisa;
	}

	public String getIntegracao() {
		return integracao;
	}

	public void setIntegracao(String integracao) {
		this.integracao = integracao;
	}

	@Override
	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();
		
		ValidatorUtil.validateRequired(projeto.getTitulo(), "Título", lista);
		ValidatorUtil.validateRequiredId(editalPesquisa.getId(), "Edital de Pesquisa", lista);
		ValidatorUtil.validateRequired(getProjeto().getJustificativa(), "Justificativa dos Recursos Solicitados", lista);

		if ( getOrcamentosDetalhados().isEmpty() )
			lista.addErro("É necessário solicitar pelo menos um item.");
		
		return lista;
	}
	
}