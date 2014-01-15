package br.ufrn.sigaa.ouvidoria.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;
import static br.ufrn.arq.util.ValidatorUtil.isNotEmpty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.faces.event.ActionEvent;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dao.UnidadeDAOImpl;
import br.ufrn.arq.dao.dialect.SQLDialect;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.comum.dao.ResponsavelUnidadeDAO;
import br.ufrn.comum.dao.ServidorDAO;
import br.ufrn.comum.dominio.Responsavel;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.comum.dominio.UnidadeGeral;
import br.ufrn.rh.dominio.NivelResponsabilidade;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.ouvidoria.dominio.AcompanhamentoManifestacao;
import br.ufrn.sigaa.ouvidoria.dominio.CategoriaAssuntoManifestacao;
import br.ufrn.sigaa.ouvidoria.dominio.DelegacaoUsuarioResposta;
import br.ufrn.sigaa.ouvidoria.dominio.HistoricoManifestacao;
import br.ufrn.sigaa.ouvidoria.dominio.Manifestacao;

/**
 * Controller abstrato com comportamentos comuns a alguns controllers de {@link Manifestacao}.
 * 
 * @author Bernardo
 *
 */
public abstract class AnaliseManifestacaoAbstractController extends SigaaAbstractController<Manifestacao> {
	
	//Constantes de JSPs
	/** Armazena a p�gina de detalhamento de uma manifesta��o. */
	public static final String JSP_DETALHES_MANIFESTACAO = "/detalhes_manifestacao.jsp";

	//Constantes usadas na listagem de manifesta��es
	/** Indica a opera��o ativa de visualizar um manifesta��o pendente. */
    protected static final int VISUALIZAR_PENDENTE = 1;
    
    /** Indica a opera��o ativa de visualizar um manifesta��o encaminhada. */
    protected static final int VISUALIZAR_ENCAMINHADA = 2;
    
    /** Indica a opera��o ativa de visualizar um manifesta��o buscada. */
    protected static final int VISUALIZAR_BUSCA = 3;
    
    /** Indica a opera��o ativa de responder um manifesta��o pendente. */
    protected static final int RESPONDER_PENDENTE = 4;
    
    /** Indica a opera��o ativa de responder um manifesta��o encaminhada. */
    protected static final int RESPONDER_ENCAMINHADA = 5;
    
    /** Indica a opera��o ativa de encaminhar uma manifesta��o. */
    protected static final int ENCAMINHAR = 6;
    
    /** Indica a opera��o ativa de reencaminhar uma manifesta��o. */
    protected static final int REENCAMINHAR = 7;
    
    /** Indica a opera��o ativa de editar uma manifesta��o. */
    protected static final int EDITAR = 8;
    
    /** Indica a opera��o ativa de alterar o prazo de resposta de uma manifesta��o encaminhada. */
    protected static final int ALTERAR_PRAZO = 9;
    
    /** Indica a opera��o ativa de visualizar um manifesta��o buscada. */
    protected static final int VISUALIZAR_RESPONDIDA = 10;
    
    /** Indica a opera��o ativa de finalizar uma manifesta��o respondida. */
    protected static final int FINALIZAR = 11;
    
    /** Indica a opera��o ativa de solicitar esclarecimentos */
    protected static final int SOLICITAR = 12;

    /** Indica a opera��o ativa de responder esclarecimento */
    protected static final int RESPONDER_ESCLARECIMENTO = 13;
    
    //Dados utilizados na busca
    /** Indica se o filtro de busca pelo n�mero/ano da manifesta��o foi selecionado. */
    protected boolean buscaNumeroAno;

    /** Indica se o filtro de busca pelo per�odo de cadastro da manifesta��o foi selecionado. */
    protected boolean buscaPeriodo;

    /** Indica se o filtro de busca pela categoria de assunto da manifesta��o foi selecionado. */
	protected boolean buscaCategoriaAssunto;
	
	/** Indica se o filtro de busca pelo nome do manifestante. */
    protected boolean buscaNome;

    /** Indica se o filtro de busca pela matr�cula ou SIAPE do manifestante. */
    protected boolean buscaMatriculaSiape;

    /** Indica se o filtro de busca pelo CPF do manifestante. */
	protected boolean buscaCpf;
    
	/** Ano indicado como parametro da busca de manifesta��es. */
    protected Integer ano;
    
    /** N�mero indicado como parametro da busca de manifesta��es. */
    protected String numero;
    
    /** Data de in�cio para o per�odo de cadastro indicado como parametro da busca de manifesta��es. */
    protected Date dataInicial;
    
    /** Data final para o per�odo de cadastro indicado como parametro da busca de manifesta��es. */
    protected Date dataFinal;
    
    /** Categoria de assunto indicada como parametro da busca de manifesta��es. */
    protected CategoriaAssuntoManifestacao categoriaAssuntoManifestacao;
	
	/** Nome do manifestante utilizado como parametro da busca de manifesta��es. */
    protected String nome;

    /** Matr�cula ou SIAPE do manifestante utilizado como parametro da busca de manifesta��es. */
    protected Long matriculaSiape;

    /** CPF do manifestante utilizado como parametro da busca de manifesta��es. */
	protected Long cpf;
    
    /** Lista de manifesta��es trabalhadas no momento. */
    protected Collection<Manifestacao> manifestacoes;
    
    /** Conjunto de hist�ricos cadastrados para a manifesta��o selecionada. */
    protected Collection<HistoricoManifestacao> historicos;
    
    /** Conjunto de c�pias cadastradas para a manifesta��o selecionada. */
    protected Collection<AcompanhamentoManifestacao> copias;
    
    /** Conjunto de c�pias cadastradas para a manifesta��o selecionada. */
    protected Collection<DelegacaoUsuarioResposta> delegacoes;
    
    /** Hist�rico da manifesta��o trabalhado no momento. */
    protected HistoricoManifestacao historico;

    /** Respons�vel pela unidade escolhida. */
    protected Responsavel responsavelUnidade;

    /** Responsabilidades do usu�rio logado. */
	private List<Responsavel> responsabilidades;

	protected AnaliseManifestacaoAbstractController() {
		init();
	}

	/**
	 * Inicia os dados necess�rios para o bean.
	 */
	protected void init() {
		obj = new Manifestacao();
		responsabilidades = new ArrayList<Responsavel>();
	}
    
	/**
	 * Detalha uma manifesta��o listada como pendente.
	 * 
	 * @param evt
	 * @throws DAOException
	 */
	public void detalharManifestacaoPendente(ActionEvent evt) throws DAOException {
		setOperacaoAtiva(VISUALIZAR_PENDENTE);
		
		detalharManifestacao(evt);
    }
    
	/**
	 * Detalha uma manifesta��o buscada.
	 * 
	 * @param evt
	 * @throws DAOException
	 */
    public void detalharManifestacaoBuscada(ActionEvent evt) throws DAOException {
		setOperacaoAtiva(VISUALIZAR_BUSCA);
		
		detalharManifestacao(evt);
    }
    
    /**
     * M�todo abstrato representando o comportamento de detalhar uma manifesta��o selecionada.
     * 
     * @param evt
     * @throws DAOException
     */
    public abstract void detalharManifestacao(ActionEvent evt) throws DAOException;
    
    /**
     * Recupera uma manifesta��o previamente listada de acordo com seu id.
     * 
     * @param id
     * @return
     */
    public Manifestacao getManifestacaoById(int id) {
		Manifestacao manifestacao = new Manifestacao();
		
		if(isNotEmpty(getManifestacoes())) {
			for (Manifestacao m : getManifestacoes()) {
			    if(m.getId() == id) {
					manifestacao = m;
					break;
			    }
			}
		}
		
		return manifestacao;
    }

    /**
     * Marca o hist�rico passado como par�metro como lido.
     * 
     * @param historico
     * @throws DAOException
     */
	protected void marcarHistoricoLido(HistoricoManifestacao historico) throws DAOException {
		GenericDAO dao = getGenericDAO();
		
		try {
		    dao.updateField(HistoricoManifestacao.class, historico.getId(), "lido", SQLDialect.TRUE);
		} finally {
		    dao.close();
		}
    }

	/**
	 * Carrega o respons�vel pela unidade destino da manifesta��o.
	 * 
	 * @param idUnidadeDestino
	 */
	protected void carregarResponsavelUnidade(Integer idUnidadeDestino) {
		ResponsavelUnidadeDAO responsavelDao = getDAO(ResponsavelUnidadeDAO.class);
		ServidorDAO servidorDao = getDAO(ServidorDAO.class, Sistema.COMUM);
		
		try {
			responsavelUnidade = responsavelDao.findResponsavelAtualByUnidade(idUnidadeDestino, NivelResponsabilidade.CHEFE);
				
			// Tratando erro de NullPointer (Lazy)
			if(!ValidatorUtil.isEmpty(responsavelUnidade) && !ValidatorUtil.isEmpty(responsavelUnidade.getServidor())) {
				responsavelUnidade.setServidor(servidorDao.findByPrimaryKey(responsavelUnidade.getServidor().getId()));
			}
		} finally {
			responsavelDao.close();
		    servidorDao.close();
		}
	}
	
	/**
	 * Verifica se o usu�rio logado tem acesso � aba de respons�vel por unidade.
	 * 
	 * @return
	 * @throws DAOException 
	 */
	public boolean isAcessaAbaResponsabilidade() throws DAOException {
		return isUserInRole(SigaaPapeis.CHEFE_UNIDADE) || isNotEmpty(getResponsabilidadesUsuario());
	}
	
	/**
	 * Lista as responsabilidades do usu�rio logado.
	 * 
	 * @return
	 * @throws DAOException
	 */
	public List<Responsavel> getResponsabilidadesUsuario() throws DAOException {
		if(isEmpty(responsabilidades) && isNotEmpty(getServidorUsuario())) {
			UnidadeDAOImpl dao = getDAO(UnidadeDAOImpl.class);
			dao.setSistema( Sistema.SIGAA );
			
			try {
				responsabilidades = (List<Responsavel>) dao.findResponsabilidadeUnidadeByServidor(getServidorUsuario().getId(), NivelResponsabilidade.getNiveis());
			} finally {
				dao.close();
			}
		}
		
		return responsabilidades;
	}
	
	/**
	 * Lista todas as unidades que o usu�rio possui responsabilidade.
	 * 
	 * @return
	 * @throws DAOException
	 */
	public Collection<UnidadeGeral> getAllUnidadesResponsabilidadeUsuario() throws DAOException {
		Collection<UnidadeGeral> unidades = new ArrayList<UnidadeGeral>();
	    
	    for (Responsavel r : getResponsabilidadesUsuario()) {
			unidades.add(r.getUnidade());
		}
	    
	    return unidades;
	}
	
	/**
	 * Verifica se a opera��o ativa � de visualizar uma manifesta��o pendente.
	 * 
	 * @return
	 */
    public boolean isOperacaoVisualizarPendente() {
    	return isOperacaoAtiva(VISUALIZAR_PENDENTE);
    }
    
    /**
	 * Verifica se a opera��o ativa � de visualizar uma manifesta��o encaminhada.
	 * 
	 * @return
	 */
    public boolean isOperacaoVisualizarEncaminhada() {
    	return isOperacaoAtiva(VISUALIZAR_ENCAMINHADA);
    }
    
    /**
	 * Verifica se a opera��o ativa � de visualizar uma manifesta��o buscada.
	 * 
	 * @return
	 */
    public boolean isOperacaoVisualizarBusca() {
    	return isOperacaoAtiva(VISUALIZAR_BUSCA);
    }
    
    /**
	 * Verifica se a opera��o ativa � de responder uma manifesta��o.
	 * 
	 * @return
	 */
    public boolean isOperacaoResponder() {
    	return isOperacaoAtiva(RESPONDER_PENDENTE, RESPONDER_ENCAMINHADA);
    }
    
    /**
	 * Verifica se a opera��o ativa � de encaminhar uma manifesta��o.
	 * 
	 * @return
	 */
    public boolean isOperacaoEncaminhar() {
    	return isOperacaoAtiva(ENCAMINHAR);
    }
    
    /**
	 * Verifica se a opera��o ativa � de reencaminhar uma manifesta��o.
	 * 
	 * @return
	 */
    public boolean isOperacaoReencaminhar() {
    	return isOperacaoAtiva(REENCAMINHAR);
    }
    
    /**
	 * Verifica se a opera��o ativa � de editar uma manifesta��o.
	 * 
	 * @return
	 */
    public boolean isOperacaoEditar() {
    	return isOperacaoAtiva(EDITAR);
    }
    
    /**
	 * Verifica se a opera��o ativa � de alterar o prazo de resposta uma manifesta��o.
	 * 
	 * @return
	 */
    public boolean isOperacaoAlterarPrazo() {
    	return isOperacaoAtiva(ALTERAR_PRAZO);
    }
    
    /**
	 * Verifica se a opera��o ativa � de visualizar uma manifesta��o respondida.
	 * 
	 * @return
	 */
    public boolean isOperacaoVisualizarRespondida() {
    	return isOperacaoAtiva(VISUALIZAR_RESPONDIDA);
    }
    
    /**
	 * Verifica se a opera��o ativa � de finalizar uma manifesta��o.
	 * 
	 * @return
	 */
    public boolean isOperacaoFinalizarManifestacao() {
    	return isOperacaoAtiva(FINALIZAR);
    }
    
    /**
	 * Verifica se a opera��o ativa corresponde a uma opera��o com manifesta��o pendente.
	 * 
	 * @return
	 */
    public boolean isOperacaoPendente() {
    	return isOperacaoAtiva(RESPONDER_PENDENTE, VISUALIZAR_PENDENTE, EDITAR, ENCAMINHAR);
    }
    
    /**
	 * Verifica se a opera��o ativa corresponde a uma opera��o com manifesta��o encaminhada.
	 * 
	 * @return
	 */
    public boolean isOperacaoEncaminhada() {
    	return isOperacaoAtiva(RESPONDER_ENCAMINHADA, VISUALIZAR_ENCAMINHADA, ALTERAR_PRAZO);
    }
    
    /**
	 * Verifica se a opera��o ativa corresponde a uma opera��o de visualiza��o.
	 * 
	 * @return
	 */
    public boolean isOperacaoVisualizacao() {
    	return isOperacaoVisualizarBusca() || isOperacaoVisualizarEncaminhada() || isOperacaoVisualizarPendente();
    }

	public boolean isBuscaNumeroAno() {
		return buscaNumeroAno;
	}

	public void setBuscaNumeroAno(boolean buscaNumeroAno) {
		this.buscaNumeroAno = buscaNumeroAno;
	}

	public boolean isBuscaPeriodo() {
		return buscaPeriodo;
	}

	public void setBuscaPeriodo(boolean buscaPeriodo) {
		this.buscaPeriodo = buscaPeriodo;
	}

	public boolean isBuscaCategoriaAssunto() {
		return buscaCategoriaAssunto;
	}

	public void setBuscaCategoriaAssunto(boolean buscaCategoriaAssunto) {
		this.buscaCategoriaAssunto = buscaCategoriaAssunto;
	}

	public boolean isBuscaNome() {
		return buscaNome;
	}

	public void setBuscaNome(boolean buscaNome) {
		this.buscaNome = buscaNome;
	}

	public boolean isBuscaMatriculaSiape() {
		return buscaMatriculaSiape;
	}

	public void setBuscaMatriculaSiape(boolean buscaMatriculaSiape) {
		this.buscaMatriculaSiape = buscaMatriculaSiape;
	}

	public boolean isBuscaCpf() {
		return buscaCpf;
	}

	public void setBuscaCpf(boolean buscaCpf) {
		this.buscaCpf = buscaCpf;
	}

	public Integer getAno() {
		return ano;
	}

	public void setAno(Integer ano) {
		this.ano = ano;
	}

	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	public Date getDataInicial() {
		return dataInicial;
	}

	public void setDataInicial(Date dataInicial) {
		this.dataInicial = dataInicial;
	}

	public Date getDataFinal() {
		return dataFinal;
	}

	public void setDataFinal(Date dataFinal) {
		this.dataFinal = dataFinal;
	}

	public CategoriaAssuntoManifestacao getCategoriaAssuntoManifestacao() {
		return categoriaAssuntoManifestacao;
	}

	public void setCategoriaAssuntoManifestacao(CategoriaAssuntoManifestacao categoriaAssuntoManifestacao) {
		this.categoriaAssuntoManifestacao = categoriaAssuntoManifestacao;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public Long getMatriculaSiape() {
		return matriculaSiape;
	}

	public void setMatriculaSiape(Long matriculaSiape) {
		this.matriculaSiape = matriculaSiape;
	}

	public Long getCpf() {
		return cpf;
	}

	public void setCpf(Long cpf) {
		this.cpf = cpf;
	}

	public Collection<Manifestacao> getManifestacoes() {
		return manifestacoes;
	}

	public void setManifestacoes(Collection<Manifestacao> manifestacoes) {
		this.manifestacoes = manifestacoes;
	}

	public Collection<HistoricoManifestacao> getHistoricos() {
		return historicos;
	}

	public void setHistoricos(Collection<HistoricoManifestacao> historicos) {
		this.historicos = historicos;
	}

	public Collection<AcompanhamentoManifestacao> getCopias() {
		return copias;
	}

	public void setCopias(Collection<AcompanhamentoManifestacao> copias) {
		this.copias = copias;
	}

	public Collection<DelegacaoUsuarioResposta> getDelegacoes() {
		return delegacoes;
	}

	public void setDelegacoes(Collection<DelegacaoUsuarioResposta> delegacoes) {
		this.delegacoes = delegacoes;
	}

	public HistoricoManifestacao getHistorico() {
		return historico;
	}

	public void setHistorico(HistoricoManifestacao historico) {
		this.historico = historico;
	}

	public Responsavel getResponsavelUnidade() {
		return responsavelUnidade;
	}

	public void setResponsavelUnidade(Responsavel responsavelUnidade) {
		this.responsavelUnidade = responsavelUnidade;
	}
    
}
