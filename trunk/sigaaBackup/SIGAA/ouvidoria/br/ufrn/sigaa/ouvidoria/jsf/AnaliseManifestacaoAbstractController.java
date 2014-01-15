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
	/** Armazena a página de detalhamento de uma manifestação. */
	public static final String JSP_DETALHES_MANIFESTACAO = "/detalhes_manifestacao.jsp";

	//Constantes usadas na listagem de manifestações
	/** Indica a operação ativa de visualizar um manifestação pendente. */
    protected static final int VISUALIZAR_PENDENTE = 1;
    
    /** Indica a operação ativa de visualizar um manifestação encaminhada. */
    protected static final int VISUALIZAR_ENCAMINHADA = 2;
    
    /** Indica a operação ativa de visualizar um manifestação buscada. */
    protected static final int VISUALIZAR_BUSCA = 3;
    
    /** Indica a operação ativa de responder um manifestação pendente. */
    protected static final int RESPONDER_PENDENTE = 4;
    
    /** Indica a operação ativa de responder um manifestação encaminhada. */
    protected static final int RESPONDER_ENCAMINHADA = 5;
    
    /** Indica a operação ativa de encaminhar uma manifestação. */
    protected static final int ENCAMINHAR = 6;
    
    /** Indica a operação ativa de reencaminhar uma manifestação. */
    protected static final int REENCAMINHAR = 7;
    
    /** Indica a operação ativa de editar uma manifestação. */
    protected static final int EDITAR = 8;
    
    /** Indica a operação ativa de alterar o prazo de resposta de uma manifestação encaminhada. */
    protected static final int ALTERAR_PRAZO = 9;
    
    /** Indica a operação ativa de visualizar um manifestação buscada. */
    protected static final int VISUALIZAR_RESPONDIDA = 10;
    
    /** Indica a operação ativa de finalizar uma manifestação respondida. */
    protected static final int FINALIZAR = 11;
    
    /** Indica a operação ativa de solicitar esclarecimentos */
    protected static final int SOLICITAR = 12;

    /** Indica a operação ativa de responder esclarecimento */
    protected static final int RESPONDER_ESCLARECIMENTO = 13;
    
    //Dados utilizados na busca
    /** Indica se o filtro de busca pelo número/ano da manifestação foi selecionado. */
    protected boolean buscaNumeroAno;

    /** Indica se o filtro de busca pelo período de cadastro da manifestação foi selecionado. */
    protected boolean buscaPeriodo;

    /** Indica se o filtro de busca pela categoria de assunto da manifestação foi selecionado. */
	protected boolean buscaCategoriaAssunto;
	
	/** Indica se o filtro de busca pelo nome do manifestante. */
    protected boolean buscaNome;

    /** Indica se o filtro de busca pela matrícula ou SIAPE do manifestante. */
    protected boolean buscaMatriculaSiape;

    /** Indica se o filtro de busca pelo CPF do manifestante. */
	protected boolean buscaCpf;
    
	/** Ano indicado como parametro da busca de manifestações. */
    protected Integer ano;
    
    /** Número indicado como parametro da busca de manifestações. */
    protected String numero;
    
    /** Data de início para o período de cadastro indicado como parametro da busca de manifestações. */
    protected Date dataInicial;
    
    /** Data final para o período de cadastro indicado como parametro da busca de manifestações. */
    protected Date dataFinal;
    
    /** Categoria de assunto indicada como parametro da busca de manifestações. */
    protected CategoriaAssuntoManifestacao categoriaAssuntoManifestacao;
	
	/** Nome do manifestante utilizado como parametro da busca de manifestações. */
    protected String nome;

    /** Matrícula ou SIAPE do manifestante utilizado como parametro da busca de manifestações. */
    protected Long matriculaSiape;

    /** CPF do manifestante utilizado como parametro da busca de manifestações. */
	protected Long cpf;
    
    /** Lista de manifestações trabalhadas no momento. */
    protected Collection<Manifestacao> manifestacoes;
    
    /** Conjunto de históricos cadastrados para a manifestação selecionada. */
    protected Collection<HistoricoManifestacao> historicos;
    
    /** Conjunto de cópias cadastradas para a manifestação selecionada. */
    protected Collection<AcompanhamentoManifestacao> copias;
    
    /** Conjunto de cópias cadastradas para a manifestação selecionada. */
    protected Collection<DelegacaoUsuarioResposta> delegacoes;
    
    /** Histórico da manifestação trabalhado no momento. */
    protected HistoricoManifestacao historico;

    /** Responsável pela unidade escolhida. */
    protected Responsavel responsavelUnidade;

    /** Responsabilidades do usuário logado. */
	private List<Responsavel> responsabilidades;

	protected AnaliseManifestacaoAbstractController() {
		init();
	}

	/**
	 * Inicia os dados necessários para o bean.
	 */
	protected void init() {
		obj = new Manifestacao();
		responsabilidades = new ArrayList<Responsavel>();
	}
    
	/**
	 * Detalha uma manifestação listada como pendente.
	 * 
	 * @param evt
	 * @throws DAOException
	 */
	public void detalharManifestacaoPendente(ActionEvent evt) throws DAOException {
		setOperacaoAtiva(VISUALIZAR_PENDENTE);
		
		detalharManifestacao(evt);
    }
    
	/**
	 * Detalha uma manifestação buscada.
	 * 
	 * @param evt
	 * @throws DAOException
	 */
    public void detalharManifestacaoBuscada(ActionEvent evt) throws DAOException {
		setOperacaoAtiva(VISUALIZAR_BUSCA);
		
		detalharManifestacao(evt);
    }
    
    /**
     * Método abstrato representando o comportamento de detalhar uma manifestação selecionada.
     * 
     * @param evt
     * @throws DAOException
     */
    public abstract void detalharManifestacao(ActionEvent evt) throws DAOException;
    
    /**
     * Recupera uma manifestação previamente listada de acordo com seu id.
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
     * Marca o histórico passado como parâmetro como lido.
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
	 * Carrega o responsável pela unidade destino da manifestação.
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
	 * Verifica se o usuário logado tem acesso à aba de responsável por unidade.
	 * 
	 * @return
	 * @throws DAOException 
	 */
	public boolean isAcessaAbaResponsabilidade() throws DAOException {
		return isUserInRole(SigaaPapeis.CHEFE_UNIDADE) || isNotEmpty(getResponsabilidadesUsuario());
	}
	
	/**
	 * Lista as responsabilidades do usuário logado.
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
	 * Lista todas as unidades que o usuário possui responsabilidade.
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
	 * Verifica se a operação ativa é de visualizar uma manifestação pendente.
	 * 
	 * @return
	 */
    public boolean isOperacaoVisualizarPendente() {
    	return isOperacaoAtiva(VISUALIZAR_PENDENTE);
    }
    
    /**
	 * Verifica se a operação ativa é de visualizar uma manifestação encaminhada.
	 * 
	 * @return
	 */
    public boolean isOperacaoVisualizarEncaminhada() {
    	return isOperacaoAtiva(VISUALIZAR_ENCAMINHADA);
    }
    
    /**
	 * Verifica se a operação ativa é de visualizar uma manifestação buscada.
	 * 
	 * @return
	 */
    public boolean isOperacaoVisualizarBusca() {
    	return isOperacaoAtiva(VISUALIZAR_BUSCA);
    }
    
    /**
	 * Verifica se a operação ativa é de responder uma manifestação.
	 * 
	 * @return
	 */
    public boolean isOperacaoResponder() {
    	return isOperacaoAtiva(RESPONDER_PENDENTE, RESPONDER_ENCAMINHADA);
    }
    
    /**
	 * Verifica se a operação ativa é de encaminhar uma manifestação.
	 * 
	 * @return
	 */
    public boolean isOperacaoEncaminhar() {
    	return isOperacaoAtiva(ENCAMINHAR);
    }
    
    /**
	 * Verifica se a operação ativa é de reencaminhar uma manifestação.
	 * 
	 * @return
	 */
    public boolean isOperacaoReencaminhar() {
    	return isOperacaoAtiva(REENCAMINHAR);
    }
    
    /**
	 * Verifica se a operação ativa é de editar uma manifestação.
	 * 
	 * @return
	 */
    public boolean isOperacaoEditar() {
    	return isOperacaoAtiva(EDITAR);
    }
    
    /**
	 * Verifica se a operação ativa é de alterar o prazo de resposta uma manifestação.
	 * 
	 * @return
	 */
    public boolean isOperacaoAlterarPrazo() {
    	return isOperacaoAtiva(ALTERAR_PRAZO);
    }
    
    /**
	 * Verifica se a operação ativa é de visualizar uma manifestação respondida.
	 * 
	 * @return
	 */
    public boolean isOperacaoVisualizarRespondida() {
    	return isOperacaoAtiva(VISUALIZAR_RESPONDIDA);
    }
    
    /**
	 * Verifica se a operação ativa é de finalizar uma manifestação.
	 * 
	 * @return
	 */
    public boolean isOperacaoFinalizarManifestacao() {
    	return isOperacaoAtiva(FINALIZAR);
    }
    
    /**
	 * Verifica se a operação ativa corresponde a uma operação com manifestação pendente.
	 * 
	 * @return
	 */
    public boolean isOperacaoPendente() {
    	return isOperacaoAtiva(RESPONDER_PENDENTE, VISUALIZAR_PENDENTE, EDITAR, ENCAMINHAR);
    }
    
    /**
	 * Verifica se a operação ativa corresponde a uma operação com manifestação encaminhada.
	 * 
	 * @return
	 */
    public boolean isOperacaoEncaminhada() {
    	return isOperacaoAtiva(RESPONDER_ENCAMINHADA, VISUALIZAR_ENCAMINHADA, ALTERAR_PRAZO);
    }
    
    /**
	 * Verifica se a operação ativa corresponde a uma operação de visualização.
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
