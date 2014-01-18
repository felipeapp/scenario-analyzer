package br.ufrn.sigaa.extensao.dominio;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dao.CampoAtivo;
import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.seguranca.log.CriadoPor;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.projetos.dominio.MembroComissao;

/**
 *  Uma linha de estenção vincula um edital a uma ou varias conissões de avaliação de proposta de ação de extensão.
 *  Ex. (Pesquisa, Monitoria, Extensão ou Pós Graduação,...).
 * @author Joab.
 *
 */
@SuppressWarnings("serial")
@Entity
@Table(schema = "extensao", name = "edital_extensao_linha_atuacao")
public class EditalExtensaoLinhaAtuacao implements Validatable {
	
	/** Atributo utilizado para identificar a Linha de Atuação do Edital de Extensão.*/
	@Id
    @GeneratedValue(generator="seqGenerator")
    @GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
    		parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
    @Column(name = "id_edital_extensao_linha_atuacao", nullable = false)
	private int id;
	
	/**Descrição resumida da linha de atuação*/
    @Column(name = "descricao")
	private String descricao;
    
    /**Guarda os papeis dos membros da comissao*/
    @Column(name = "expressao_membros_comissao")
	private String expressaoMembrosComissao;
	
	/** Atributo utilizado para representar o Edital de Extensão. */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_edital_extensao")
    private EditalExtensao editalExtensao = new EditalExtensao();
    
    /** Atributo define se a linha de atuação esta ativa. */
    @CampoAtivo
    private boolean ativo = true;
    
    /** Atributo utilizado para representar a data de cadastro do Edital Extensão Linha de Atuação. */
    @CriadoEm
    @Column(name="data_cadastro")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataCadastro;
	
    /** Usuário que cadastrou o edital */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_registro_entrada")
    @CriadoPor
    private RegistroEntrada registroEntrada;
    
	/**Comissões Responsaveis pela linha de atuação.*/
	@Transient
	private Collection<MembroComissao> membrosComissao = new ArrayList<MembroComissao>();
	
	/**retorna uma expressão com as comissões responsaveis pela linha de atuação.(PESQUISA, MONITORIA,...)*/
	public String getExpressaoComissoes(){
		StringBuilder expressao = new StringBuilder();
		String[] papeis = expressaoMembrosComissao.split(",");
		for(int i = 0;i < papeis.length;i++){			
			expressao.append(MembroComissao.getPapelString(Integer.parseInt(papeis[i])));
			if(i+1 < papeis.length)
				expressao.append("; ");
		}
		return expressao.toString();
	}
	
	/**Cria uma string de idPapeis e adiciona no atributo persistido expressaoMembrosComissao*/
	public void addPapeisMembrosComissao(Collection<MembroComissao> membros){
		StringBuilder expressao = new StringBuilder();
		int posicao = 1;
		for(MembroComissao mc: membros){
			expressao.append(mc.getPapel());
			if(posicao++ < membros.size())
				expressao.append(",");
		}
		setExpressaoMembrosComissao(expressao.toString());
	}
	

	@Override
	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();
    	ValidatorUtil.validateRequired(descricao, "Linha Atuação", lista);
    	ValidatorUtil.validateRequired( membrosComissao,"Membro da comissão de avaliação do projeto", lista);
    	
    	return lista;
	}

	/**
     * Método utilizado para verificar se o objeto passado por parâmetro é igual a linha de atuação atual.
     * <br>
     * Método chamado pela(s) seguinte(s) JSP(s):
     * <ul>
     * 		<li>Não é chamdo por JSP(s)</li>
     * </ul>
     * 
     * @param obj
     */
    @Override
    public boolean equals(Object obj) {
		if (this == obj)
		    return true;
		if (obj == null)
		    return false;
		if (getClass() != obj.getClass())
		    return false;
		EditalExtensaoLinhaAtuacao other = (EditalExtensaoLinhaAtuacao) obj;
		if (id != 0 && other.id != 0 && id != other.id)
		    return false;
	
		if (ativo != other.ativo)
		    return false;
		if (editalExtensao == null) {
		    if (other.editalExtensao != null)
			return false;
		} else if (!editalExtensao.equals(other.editalExtensao))
		    return false;
		if (descricao == null) {
		    if (other.descricao != null)
			return false;
		} else if (!descricao.toUpperCase().equals(other.descricao.toUpperCase()))
		    return false;		
		return true;
    }
	
	public int getId() {
		return id;
	}
	
	
	public void setId(int id) {
		this.id = id;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public Collection<MembroComissao> getMembrosComissao() {
		return membrosComissao;
	}

	public void setMembrosComissao(Collection<MembroComissao> membroComissao) {
		this.membrosComissao = membroComissao;
	}

	public EditalExtensao getEditalExtensao() {
		return editalExtensao;
	}

	public void setEditalExtensao(EditalExtensao editalExtensao) {
		this.editalExtensao = editalExtensao;
	}

	public Date getDataCadastro() {
		return dataCadastro;
	}

	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	public RegistroEntrada getRegistroEntrada() {
		return registroEntrada;
	}

	public void setRegistroEntrada(RegistroEntrada registroEntrada) {
		this.registroEntrada = registroEntrada;
	}

	public String getExpressaoMembrosComissao() {
		return expressaoMembrosComissao;
	}

	public void setExpressaoMembrosComissao(String expressaoMembrosComissao) {
		this.expressaoMembrosComissao = expressaoMembrosComissao;
	}
	public boolean isAtivo() {
		return ativo;
	}
	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}    
}
