/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 01/10/2004
 */
package br.ufrn.comum.dominio;

import java.util.Date;

import br.ufrn.arq.dominio.PersistDB;


/**
 * Classe de domínio que mapeia as permissões. Uma permissão faz o relacionamento entre um usuário e um papel.
 *
 * @author Andre
 * @author David Pereira
 * @author Gleydson Lima
 *
 */
public class Permissao implements PersistDB{

    protected int id;

    /** Papel associado a permissão */
    protected Papel papel;

    /** Usuário que possui a permissão */
    private UsuarioGeral usuario;

    /** Unidade para a qual a permissão é válida. Utilizada apenas em papeis cuja coluna exige_unidade for true. */
    private UnidadeGeral unidadePapel;

    /** Dados de quem cadastrou a permissão*/
    private UsuarioGeral usuarioCadastro;

    /** Data da criação da permissão */
    protected Date dataCadastro;

    /** Se for um papel temporário, é a data em que a permissão deixa de ser válida */
    protected Date dataExpiracao;

    /** Sistema da permissão */
    private int sistema;

    /** Número do chamado que ocasionou a criação da permissão */
    private Integer numeroChamado;

    /** Motivos pelos quais o usuário solicitou a permissão. O que ele deseja utilizar no sistema e por que. */
    private String motivo;

    /** Se a permissão já foi autorizada pelo gestor de permissões. */
    private boolean autorizada;

    /** Designação associada a permissão */
    private Integer designacao;
    
    /** Date de expiração da Designação associada a permissão */
    protected Date dataExpiracaoDesignacao;
    
    public Permissao() {

    }

    public Permissao(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Papel getPapel() {
        return papel;
    }

    public void setPapel(Papel papel) {
        this.papel = papel;
    }

    public UsuarioGeral getUsuario() {
        return usuario;
    }

    public void setUsuario(UsuarioGeral usuario) {
        this.usuario = usuario;
    }

	public Date getDataCadastro() {
		return dataCadastro;
	}

	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	public UsuarioGeral getUsuarioCadastro() {
		return usuarioCadastro;
	}

	public void setUsuarioCadastro(UsuarioGeral usuarioCadastro) {
		this.usuarioCadastro = usuarioCadastro;
	}

	public Date getDataExpiracao() {
		return dataExpiracao;
	}

	public void setDataExpiracao(Date dataExpiracao) {
		this.dataExpiracao = dataExpiracao;
	}

	public int getSistema() {
		return sistema;
	}

	public void setSistema(int sistema) {
		this.sistema = sistema;
	}

	public UnidadeGeral getUnidadePapel() {
		return unidadePapel;
	}

	public void setUnidadePapel(UnidadeGeral unidadePapel) {
		this.unidadePapel = unidadePapel;
	}

	public Integer getNumeroChamado() {
		return numeroChamado;
	}

	public void setNumeroChamado(Integer numeroChamado) {
		this.numeroChamado = numeroChamado;
	}

	public String getMotivo() {
		return motivo;
	}

	public void setMotivo(String motivo) {
		this.motivo = motivo;
	}

	public boolean isAutorizada() {
		return autorizada;
	}

	public void setAutorizada(boolean autorizada) {
		this.autorizada = autorizada;
	}
	
	public boolean isExpirada() {
		return dataExpiracao != null && dataExpiracao.before(new Date());
	}

	/**
	 * Verifica se esta permissão é válida para a unidade especificada.
	 * @param unidade
	 * @return
	 */
	public boolean permiteUnidade(UnidadeGeral unidade) {
		return unidadePapel == null || (unidadePapel != null && unidadePapel.getId() == unidade.getId());
	}

	/**
	 * Verifica se esta permissão é válida para a unidade especificada, considerando sua hierarquia.
	 * @param unidade
	 * @return
	 */
	public boolean permiteUnidadeHierarquia(UnidadeGeral unidade) {
		if(unidadePapel == null)
			return true;
		else if(unidade == null || unidade.getHierarquiaOrganizacional() == null)
			return false;
		else
			return unidade.getHierarquiaOrganizacional().contains("."+unidadePapel.getId()+".");
	}

	public Integer getDesignacao() {
		return designacao;
	}

	public void setDesignacao(Integer designacao) {
		this.designacao = designacao;
	}

	public Date getDataExpiracaoDesignacao() {
		return dataExpiracaoDesignacao;
	}

	public void setDataExpiracaoDesignacao(Date dataExpiracaoDesignacao) {
		this.dataExpiracaoDesignacao = dataExpiracaoDesignacao;
	}
}
