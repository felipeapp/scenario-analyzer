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
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.sigaa.pessoa.dominio.Servidor;
import br.ufrn.sigaa.projetos.dominio.OrcamentoDetalhado;
import br.ufrn.sigaa.projetos.dominio.Projeto;

@Entity
@Table(name = "projeto_apoio_novos_pesquisadores", schema = "pesquisa", uniqueConstraints = {})
public class ProjetoApoioNovosPesquisadores implements Validatable {
	
	/** Atributo utilizado para representar o ID do projeto de pesquisa */
	@Id @GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	parameters={ @Parameter(name="hibernate_sequence", value="public.hibernate_sequence") })
	@Column(name = "id_projeto_apoio_novos_pesquisadores")
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

	/** Grupo de Pesquisa ao qual o Projeto de Apoio de Grupo de Pesquisa está vínculado */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_coordenador", unique = false, nullable = true, insertable = true, updatable = true)
	private Servidor coordenador;

	private String integracao;

	@Transient
	private boolean visualizar;

	@Transient
	private boolean concluido;

	@Transient
	private TipoPassoProjetoNovoPesquisador tipoPassosProjeto;
	
	@Transient
	private boolean viculadoGrupoPesquisa;
	
	public ProjetoApoioNovosPesquisadores() {
		editalPesquisa = new EditalPesquisa();
		projeto = new Projeto();
		grupoPesquisa = new GrupoPesquisa();
		coordenador = new Servidor();
	}
	
    public List<OrcamentoDetalhado> getOrcamentosDetalhados() {
    	return (List<OrcamentoDetalhado>) getProjeto().getOrcamento();
    }

    public void setOrcamentosDetalhados(Collection<OrcamentoDetalhado> orcamentosDetalhados) {
    	this.getProjeto().setOrcamento(orcamentosDetalhados);
    }
	
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

	public Servidor getCoordenador() {
		return coordenador;
	}

	public void setCoordenador(Servidor coordenador) {
		this.coordenador = coordenador;
	}

	public String getIntegracao() {
		return integracao;
	}

	public void setIntegracao(String integracao) {
		this.integracao = integracao;
	}

	public boolean isVisualizar() {
		return visualizar;
	}

	public void setVisualizar(boolean visualizar) {
		this.visualizar = visualizar;
	}

	public boolean isConcluido() {
		return concluido;
	}

	public void setConcluido(boolean concluido) {
		this.concluido = concluido;
	}

	public TipoPassoProjetoNovoPesquisador getTipoPassosProjeto() {
		return tipoPassosProjeto;
	}

	public void setTipoPassosProjeto(
			TipoPassoProjetoNovoPesquisador tipoPassosProjeto) {
		this.tipoPassosProjeto = tipoPassosProjeto;
	}

	public boolean isViculadoGrupoPesquisa() {
		return viculadoGrupoPesquisa;
	}

	public void setViculadoGrupoPesquisa(boolean viculadoGrupoPesquisa) {
		this.viculadoGrupoPesquisa = viculadoGrupoPesquisa;
	}

	@Override
	public ListaMensagens validate() {
		return null;
	}
}