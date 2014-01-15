/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 01/10/2004
 */
package br.ufrn.comum.dominio;

import br.ufrn.arq.dominio.PersistDB;

/**
 * Classe de domínio que mapeia os papeis. Um papel faz o relacionamento entre
 * subsistema e permissoes.
 * 
 * @author Andre
 * @author David Pereira
 * @author Gleydson Lima
 *  
 */
public class Papel implements PersistDB {

	public static final int AUTORIZACAO_GLOBAL = 1;
	public static final int AUTORIZACAO_LOCAL = 2;
	
    private int id;

    /** Identificador do sistema a que o papel se refere */
    private Sistema sistema;
    
    /** Identificador do subsistem (dentro do sistema) a que o papel se refere */
    private SubSistema subSistema;

    /** Nome do papel */
    private String nome;
    
    /** Descrição textual do papel */
    private String descricao;
    
    /** Se o papel é restrito a alguns usuários */
    private boolean restrito;
    
    /** Se as permissões atribuidas pelo papel serao restritas a apenas uma unidade. */
    private boolean exigeUnidade;
    
    /** SQL para listar as unidades que podem ser utilizadas para o papel. Utilizado caso o atributo exigeUnidade seja true. */
    private String sqlFiltroUnidades;

    /** Indica se o papel tem abrangência local ou global, e se ele deve ser autorizado de forma local (pelo chefe da unidade) ou global (por um gestor de permissões). */
    private int tipoAutorizacao;
    
    /** De quantos em quantos dias o portador deste papel deve trocar de senha. */
    private Integer tempoAlteracaoSenha;
    
    public Papel() {
        
    }
    
    public Papel(int id) {
        this.id = id;
    }
    
    public Papel(int id, SubSistema subsistema) {
    	this(id);
    	this.subSistema = subsistema;
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

    public SubSistema getSubSistema() {
        return subSistema;
    }

    public void setSubSistema(SubSistema subSistemas) {
        this.subSistema = subSistemas;
    }

	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + id;
		return result;
	}

	@Override
	public boolean equals(Object obj) {

        if (obj != null && obj instanceof Papel) {
            if (((Papel) obj).getId() == id) {
                return true;
            }
        }
        return false;
    }

	public Sistema getSistema() {
		return sistema;
	}

	public void setSistema(Sistema sistema) {
		this.sistema = sistema;
	}

	public String getDescricaoCompleta() {
		return sistema.getNome() 
			+ " - " + subSistema.getNome() 
			+ " - " + getNome();
	}
	
	public boolean isRestrito() {
		return restrito;
	}

	public void setRestrito(boolean restrito) {
		this.restrito = restrito;
	}

	public boolean isExigeUnidade() {
		return exigeUnidade;
	}

	public void setExigeUnidade(boolean exigeUnidade) {
		this.exigeUnidade = exigeUnidade;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public int getTipoAutorizacao() {
		return tipoAutorizacao;
	}

	public void setTipoAutorizacao(int tipoAutorizacao) {
		this.tipoAutorizacao = tipoAutorizacao;
	}
	
	@Override
	public String toString() {
		return getDescricaoCompleta();
	}

	public Integer getTempoAlteracaoSenha() {
		return tempoAlteracaoSenha;
	}

	public void setTempoAlteracaoSenha(Integer tempoAlteracaoSenha) {
		this.tempoAlteracaoSenha = tempoAlteracaoSenha;
	}

	public String getSqlFiltroUnidades() {
		return sqlFiltroUnidades;
	}

	public void setSqlFiltroUnidades(String sqlFiltroUnidades) {
		this.sqlFiltroUnidades = sqlFiltroUnidades;
	}
	
}