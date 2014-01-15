/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '26/10/2006'
 *
 */
package br.ufrn.sigaa.prodocente.producao.dominio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.ValidatorUtil;

/**
 * Entidade que registra os tipos de produção intelectual que um docente pode cadastrar no sistema
 * 
 * @author wendell
 *
 */
@Entity
@Table(name = "tipo_producao", schema = "prodocente")
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
public class TipoProducao implements Validatable {

	public static final TipoProducao ARTIGO_PERIODICO_JORNAIS_SIMILARES = new TipoProducao(1);

	public static final TipoProducao CAPITULO_LIVROS = new TipoProducao(2);

	public static final TipoProducao LIVRO = new TipoProducao(3);

	public static final TipoProducao PUBLICACOES_EVENTOS = new TipoProducao(4);

	public static final TipoProducao TEXTO_DIDATICO = new TipoProducao(5);

	public static final TipoProducao TEXTO_DISCUSSAO = new TipoProducao(6);

	public static final TipoProducao AUDIO_VISUAIS = new TipoProducao(7);

	public static final TipoProducao EXPOSICAO_APRESENTACAO_ARTISTICAS = new TipoProducao(8);

	public static final TipoProducao MONTAGENS = new TipoProducao(9);

	public static final TipoProducao PROGRAMACAO_VISUAL = new TipoProducao(10);

	public static final TipoProducao BANCA_CURSO_SELECOES = new TipoProducao(11);

	public static final TipoProducao MAQUETES_PROTOTIPOS_OUTROS = new TipoProducao(12);

	public static final TipoProducao PATENTE = new TipoProducao(13);

	public static final TipoProducao APRESENTACAO_EVENTOS = new TipoProducao(14);

	public static final TipoProducao PREMIO_RECEBIDO = new TipoProducao(15);

	public static final TipoProducao BOLSA_OBTIDA = new TipoProducao(16);

	public static final TipoProducao VISITA_CIENTIFICA= new TipoProducao(17);

	public static final TipoProducao PARTICIPACAO_COMISSAO_ORGANIZACAO_EVENTO = new TipoProducao(18);

	public static final TipoProducao PARTICIPACAO_SOCIEDADE_CIENTIFICA_CULTURAIS = new TipoProducao(19);

	public static final TipoProducao PARTICIPACAO_COLEGIADOS_COMISSOES = new TipoProducao(20);

	public static final TipoProducao BANCA_CONCURSO = new TipoProducao(21);

	public static final TipoProducao OUTROS = new TipoProducao(99);

/*
	public static final TipoProducao ARTIGO_EM_PERIODICO = new TipoProducao(1);

	public static final TipoProducao LIVRO = new TipoProducao(2);

	public static final TipoProducao TRABALHO_EM_ANAIS = new TipoProducao(3);

	public static final TipoProducao TRADUCAO = new TipoProducao(4);

	public static final TipoProducao PARTITURA_MUSICAL = new TipoProducao(5);

	public static final TipoProducao ARTIGO_EM_JORNAL_REVISTA = new TipoProducao(6);

	public static final TipoProducao APRESENTACAO_OBRA_ARTISTICA = new TipoProducao(21);

	public static final TipoProducao ARRANJO_MUSICAL = new TipoProducao(22);

	public static final TipoProducao COMPOSICAO_MUSICAL = new TipoProducao(23);

	public static final TipoProducao CURSO_CURTA_DURACAO = new TipoProducao(24);

	public static final TipoProducao OBRAS_ARTE_VISUAIS = new TipoProducao(26);

	public static final TipoProducao SONOPLASTIA = new TipoProducao(27);

	public static final TipoProducao SERVICOS_TECNICOS = new TipoProducao(41);

	public static final TipoProducao CARTAS_MAPAS_SIMILARES = new TipoProducao(42);

	public static final TipoProducao DESENVOLVIMENTO_APLICATIVO = new TipoProducao(44);

	public static final TipoProducao DESENVOLVIMENTO_MATERIAL_DIDATICO_INSTITUICIONAL = new TipoProducao(45);

	public static final TipoProducao DESENVOLVIMENTO_PRODUTO= new TipoProducao(46);

	public static final TipoProducao DESENVOLVIMENTO_TECNICA = new TipoProducao(47);

	public static final TipoProducao EDITORIA = new TipoProducao(48);

	public static final TipoProducao MANUTENCAO_OBRA_ARTISTICA = new TipoProducao(49);

	public static final TipoProducao MAQUETE = new TipoProducao(50);

	public static final TipoProducao ORGANIZACAO_EVENTO = new TipoProducao(51);

	public static final TipoProducao PROGRAMA_RADIO_TV = new TipoProducao(52);

	public static final TipoProducao RELATORIO_PESQUISA = new TipoProducao(53);

	public static final TipoProducao APRESENTACAO_TRABALHO = new TipoProducao(71);

	public static final TipoProducao OUTRO = new TipoProducao(99);

	public static final TipoProducao TEXTO_DIDATICO = new TipoProducao(101);

	public static final TipoProducao AUDIO_VISUAL = new TipoProducao(101);

	public static final TipoProducao ARTIGO = new TipoProducao(102);

	public static final TipoProducao CAPITULO = new TipoProducao(102);

	public static final TipoProducao CONGRESSO = new TipoProducao(103);

	public static final TipoProducao PATENTE = new TipoProducao(103);

	public static final TipoProducao MONTAGEM = new TipoProducao(104);

	public static final TipoProducao PROGRAMACAO_VISUAL = new TipoProducao(105);

	public static final TipoProducao TEXTO_DISCUSSAO = new TipoProducao(106);

	public static final TipoProducao PRODUCAO_TECNOLOGICA = new TipoProducao(106);

	public static final TipoProducao BANCA_CONCURSO = new TipoProducao(107);

	public static final TipoProducao BANCA_CURSO = new TipoProducao(108);

	public static final TipoProducao EXPOSICAO = new TipoProducao(109);

	public static final TipoProducao VISITA_CIENTIFICA = new TipoProducao(110);

	public static final TipoProducao PARTICIPACAO_SOCIEDADE = new TipoProducao(111);

	public static final TipoProducao PARTICIPACAO_COLEGIADO = new TipoProducao(112);

	public static final TipoProducao BOLSA_OBTIDA = new TipoProducao(113);

	public static final TipoProducao PREMIO_RECEBIDO = new TipoProducao(113);

*/

	@Id @GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	parameters={ @Parameter(name="hibernate_sequence", value="public.hibernate_sequence") })
	@Column(name = "id_tipo_producao", nullable = false)
	private int id;

	private String descricao;

    @Transient
    private boolean selecionado; //utilizado para selecionar os checkboxes
    
    /** String que representa o nome do MBean que implementa o CRUD de Produção. **/
    @Column(name = "nome_mbean")
	private String nomeMBean;

	public TipoProducao(int id) {
		this.id = id;
	}

	public TipoProducao(int id, String descricao) {
		this.id = id;
		this.descricao = descricao;
	}

	public TipoProducao() {
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Integer getIdObject() {
		return id;
	}

	public void setIdObject(Integer idObject) {
		if ( idObject != null ) {
			id = idObject;
		}
	}

	@Override
	public boolean equals(Object obj) {
		if( obj instanceof TipoProducao){
			TipoProducao temp = (TipoProducao) obj;
			if(getId()==temp.getId())
				return true;
			else
				return false;
		}else
			return false;

	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return descricao;
	}
	
	/*
	 * Campo Obrigatorio: Descricao
	 */

	public ListaMensagens validate(){
		
		ListaMensagens lista = new ListaMensagens();

		ValidatorUtil.validateRequired(getDescricao(),"Descrição", lista);

		return lista;
	}

	public boolean isSelecionado() {
		return selecionado;
	}

	public void setSelecionado(boolean selecionado) {
		this.selecionado = selecionado;
	}

	public String getNomeMBean() {
		return nomeMBean;
	}

	public void setNomeMBean(String nomeMBean) {
		this.nomeMBean = nomeMBean;
	}

}
