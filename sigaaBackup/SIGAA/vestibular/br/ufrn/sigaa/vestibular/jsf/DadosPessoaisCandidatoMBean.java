/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 23/08/2010
 *
 */
package br.ufrn.sigaa.vestibular.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.comum.dominio.TipoNecessidadeEspecial;
import br.ufrn.sigaa.arq.dao.MunicipioDao;
import br.ufrn.sigaa.arq.dao.vestibular.EscolaInepDao;
import br.ufrn.sigaa.arq.dao.vestibular.PessoaVestibularDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.UnidadeFederativa;
import br.ufrn.sigaa.pessoa.dominio.Endereco;
import br.ufrn.sigaa.pessoa.dominio.Municipio;
import br.ufrn.sigaa.pessoa.dominio.Pais;
import br.ufrn.sigaa.vestibular.dominio.EscolaInep;
import br.ufrn.sigaa.vestibular.dominio.InscricaoVestibular;
import br.ufrn.sigaa.vestibular.dominio.PessoaVestibular;
import br.ufrn.sigaa.vestibular.negocio.MovimentoPessoaVestibular;

/**
 * Controlador responsável por atualizar informações de dados pessoais de candidatos inscritos no
 * vestibular.
 * @author Leonardo Campos
 *
 */
@Component
@Scope("request")
public class DadosPessoaisCandidatoMBean extends SigaaAbstractController<PessoaVestibular> {

	/** Mensagem de erro de validação do CPF. */
	private String erroCPF;
	
	/** Indica se deve exibir o painel para digitação do CPF. */
	private boolean exibirPainel;
	
	/**
	 * Indica se o país selecionado no formulário de dados pessoais é o brasil. Se for false não é pra
	 * renderizar estado e nem município.
	 */
	private boolean brasil = true;
	/** Lista de SelectItem para escolha do município de naturalidade. */
	private Collection<SelectItem> municipiosNaturalidade = new ArrayList<SelectItem>(0);
	/** Lista de SelectItem para escolha do município do endereço. */
	private Collection<SelectItem> municipiosEndereco = new ArrayList<SelectItem>(0);
	/** Mapa de dados da foto */
	private List<SelectItem> tipoNecessidadeCombo;
	/** Municípios de escolas onde o candidato concluiu o ensino médio. */
	private Collection<Municipio> municipiosEscola;
	/** Lista de escolas pré-definidas onde o candidato concluiu o ensino médio. */
	private Collection<EscolaInep> escolasInep;
	
	/** Construtor padrão.  
	 * @throws DAOException */
	public DadosPessoaisCandidatoMBean() throws DAOException {
		initObj();
	}

	/** Inicializa os atributos do controller.
	 * @throws DAOException
	 */
	private void initObj() throws DAOException {
		obj = new PessoaVestibular();
		popularMunicipios();
	}
	
	/**
	 * Popular campos de municípios que serão utilizados no formulário
	 * 
	 * @throws DAOException
	 */
	private void popularMunicipios() throws DAOException {
		// Popular municípios para campo de naturalidade
		int uf = UnidadeFederativa.ID_UF_PADRAO;
		if (obj.getUnidadeFederativa() != null && obj.getUnidadeFederativa().getId() > 0)
			uf = obj.getUnidadeFederativa().getId();
		carregarMunicipiosNaturalidade(uf);	
		
		//Popular municípios para campo de endereço
		uf = UnidadeFederativa.ID_UF_PADRAO;
		if (obj.getEnderecoContato()  != null 
				&& obj.getEnderecoContato().getUnidadeFederativa() != null 
				&& obj.getEnderecoContato().getUnidadeFederativa().getId() > 0) {
			uf = obj.getEnderecoContato().getUnidadeFederativa().getId();
		}
		carregarMunicipiosEndereco(uf);
	}
	
	/**
	 * Checa as permissões de acesso, popula as informações necessárias e encaminha 
	 * o usuário para o início do caso de uso.
	 * <br/>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/vestibular/menus/candidatos.jsp</li>
	 * </ul>
	 * @return
	 * @throws SegurancaException
	 * @throws DAOException 
	 */
	public String iniciar() throws SegurancaException, DAOException{
		checkRole(SigaaPapeis.VESTIBULAR);
		initObj();
		exibirPainel = true;
		return formDadosPessoaisCandidato();
	}

	/** Redireciona o usuário para o formulário de atualização de dados pessoais.<br/>Método não invocado por JSP´s.
	 * @return
	 */
	public String formDadosPessoaisCandidato() {
		return forward("/vestibular/dados_pessoais_candidato.jsp");
	}
	
	/** Submete o CPF digitado no painel. O MBean realiza uma busca por pessoas já cadastradas
	 * que possuam o mesmo CPF e retorna os dados para o formulário.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/vestibular/dados_pessoais_candidato.jsp</li>
	 *	</ul>
	 * @param e
	 * @throws ArqException
	 */
	public void submeterCPF(ActionEvent e) throws ArqException {

		ListaMensagens erros = new ListaMensagens();
		ValidatorUtil.validateCPF_CNPJ(obj.getCpf_cnpj(), "CPF", erros);
		if (!erros.isEmpty()) {
			erroCPF = "Por favor, informe um CPF válido";
			return;
		}
		PessoaVestibularDao dao = getDAO(PessoaVestibularDao.class);
		prepareMovimento(SigaaListaComando.CADASTRAR_PESSOA_VESTIBULAR);
		PessoaVestibular pessoa = dao.findByCPF(obj.getCpf_cnpj());

		preparaAtualizar(pessoa);
		
	}
	
	/** Atualiza os dados pessoais de um candidato.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>/sigaa.war/vestibular/relatorios/seleciona_inscricao_candidato.jsp</li>
	 *	</ul>
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#atualizar()
	 */
	@Override
	public String atualizar() throws ArqException {
		int id = getParameterInt("id", 0);
		InscricaoVestibular inscricao = getGenericDAO().findByPrimaryKey(id, InscricaoVestibular.class);
		if (inscricao == null) {
			addMensagemErro("Não foi possível localizar a inscrição selecionada.");
			return null;
		} else {
			prepareMovimento(SigaaListaComando.CADASTRAR_PESSOA_VESTIBULAR);
			return preparaAtualizar(inscricao.getPessoa());
		}
	}
	
	/** Prepara os dados necessários para a atualização.
	 * @return
	 * @throws DAOException 
	 */
	private String preparaAtualizar(PessoaVestibular pessoa) throws DAOException {
		GenericDAO dao = getGenericDAO();
		// evitando um bug no hibernate, que faz apontar dois atributos distintos para o mesmo objeto na memória
		if (pessoa.getTituloEleitor() != null)
			pessoa.getTituloEleitor().setUnidadeFederativa(new UnidadeFederativa(pessoa.getTituloEleitor().getUnidadeFederativa().getId()));
		if (pessoa.getPais() != null)
			pessoa.setPais(new Pais(pessoa.getPais().getId()));
		if ( !isEmpty(pessoa) ) {
			obj = pessoa;
			obj.prepararDados();
			if(obj.getPais().getId() == Pais.BRASIL)
				setBrasil(true);
			else
				setBrasil(false);
			dao.refresh(obj.getEstadoCivil());
			dao.refresh(obj.getPais());
			dao.refresh(obj.getTituloEleitor().getUnidadeFederativa());
			dao.refresh(obj.getIdentidade().getUnidadeFederativa());
			carregarMunicipiosEndereco(obj.getEnderecoContato().getUnidadeFederativa().getId());
			carregarMunicipiosNaturalidade(obj.getUnidadeFederativa().getId());
			dao.refresh(obj.getMunicipio());
			dao.refresh(obj.getEnderecoContato().getMunicipio());
			if (obj.getUfConclusaoEnsinoMedio() != null){
				Integer idUfEnsinoMedio = obj.getUfConclusaoEnsinoMedio().getId();
				obj.setUfConclusaoEnsinoMedio(new UnidadeFederativa());
				obj.getUfConclusaoEnsinoMedio().setId(idUfEnsinoMedio);
			}
			if(obj.getMunicipio() != null){
				Integer idMunicipio = obj.getMunicipio().getId();
				obj.setMunicipio(new Municipio());
				obj.getMunicipio().setId(idMunicipio);
			}
			if(obj.getEnderecoContato() != null && obj.getEnderecoContato().getMunicipio() != null){
				Integer idMunicipio = obj.getEnderecoContato().getMunicipio().getId();
				obj.getEnderecoContato().setMunicipio(new Municipio());
				obj.getEnderecoContato().getMunicipio().setId(idMunicipio);
			}
			exibirPainel = false;
			return forward("/vestibular/dados_pessoais_candidato.jsp");
		} else {
			addMensagemErro("Selecione um registro válido.");
			return null;
		}
	}
	
	/** Valida se ao CPF do candidato já tem um cadastro associado. <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>/sigaa.war/vestibular/relatorios/seleciona_inscricao_candidato.jsp</li>
	 *	</ul>
	 * @param evt
	 * @throws DAOException
	 */
	public void validaCPFCadastrado(ActionEvent evt) throws DAOException{
		 PessoaVestibularDao dao = getDAO(PessoaVestibularDao.class);
		 PessoaVestibular cadastroAntigo = dao.findByCPF(obj.getCpf_cnpj());
		 if (cadastroAntigo != null && cadastroAntigo.getId() != obj.getId()){
			 if (evt != null)
				 addMensagemErroAjax("O CPF " + obj.getCpf_cnpjString() + " está cadastrado para: " + cadastroAntigo.getNome());
			 else
				 addMensagemErro("O CPF " + obj.getCpf_cnpjString() + " está cadastrado para: " + cadastroAntigo.getNome());
		 }
	 }
	
	/** Chama o processador para atualizar os dados pessoais do candidato.
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/vestibular/dados_pessoais_candidato.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException 
	 */
	public String atualizarDados() throws ArqException{
		
		erros.addAll(obj.validate());
		validaCPFCadastrado(null);

		if(hasErrors())
			return formDadosPessoaisCandidato();
		
		obj.anularAtributosVazios();
		obj.setTipoRedeEnsino(null);
		obj.setRegistroEntrada(getRegistroEntrada());		
		MovimentoPessoaVestibular mov = new MovimentoPessoaVestibular();
		mov.setPessoaVestibular(obj);
		mov.setFoto(null);
		mov.setSenhaAberta(null);
		mov.setEnviaEmail(false);
		mov.setCodMovimento(SigaaListaComando.CADASTRAR_PESSOA_VESTIBULAR);
		
		try {
			execute(mov);
		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens());
			return formDadosPessoaisCandidato();
		}
		addMensagem(MensagensArquitetura.OPERACAO_SUCESSO);
		return cancelar();
		
	}

	/**
	 * Carrega a lista de municípios de naturalidade e endereço.
	 * <br/>Método não invocado por JSP´s.
	 * @throws DAOException
	 */
	public void carregarMunicipios() throws DAOException {
		MunicipioDao dao = getDAO(MunicipioDao.class);
		int uf = UnidadeFederativa.ID_UF_PADRAO;
		if (obj.getUnidadeFederativa() != null && obj.getUnidadeFederativa().getId() > 0)
			uf = obj.getUnidadeFederativa().getId();
		UnidadeFederativa ufNatur = dao.findByPrimaryKey(uf, UnidadeFederativa.class);

		Collection<Municipio> municipios = dao.findByUF(uf);
		municipiosNaturalidade = new ArrayList<SelectItem>(0);
		municipiosNaturalidade.add(new SelectItem(ufNatur.getCapital().getId(), ufNatur.getCapital().getNome()));
		municipiosNaturalidade.addAll(toSelectItems(municipios, "id", "nome"));

		uf = UnidadeFederativa.ID_UF_PADRAO;
		if (obj.getEnderecoContato() != null
				&& obj.getEnderecoContato().getUnidadeFederativa() != null
				&& obj.getEnderecoContato().getUnidadeFederativa()
						.getId() > 0)
			uf = obj.getEnderecoContato().getUnidadeFederativa().getId();
		UnidadeFederativa ufEnd = dao.findByPrimaryKey(uf, UnidadeFederativa.class);

		municipios = dao.findByUF(uf);
		municipiosEndereco = new ArrayList<SelectItem>(0);
		municipiosEndereco.add(new SelectItem(ufEnd.getCapital().getId(), ufEnd.getCapital().getNome()));
		municipiosEndereco.addAll(toSelectItems(municipios, "id", "nome"));
		if (obj.getEnderecoContato() == null) {
			obj.setEnderecoContato(new Endereco());
			obj.getEnderecoContato().setUnidadeFederativa(ufEnd);
			obj.getEnderecoContato().setMunicipio(ufEnd.getCapital());
		}
	}
	
	/**
	 * Carrega a lista de municípios de endereço de uma determinada UF.<br/>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/public/vestibular/acompanhamento/novo_cadastro.jsp</li>
	 * </ul>
	 * 
	 * @param idUf
	 * @throws DAOException
	 */
	public void carregarMunicipiosEndereco(Integer idUf) throws DAOException {
		if (idUf == null) {
			idUf = obj.getEnderecoContato().getUnidadeFederativa().getId();
		}

		MunicipioDao dao = getDAO(MunicipioDao.class);
		try {
			UnidadeFederativa uf = dao.findByPrimaryKey(idUf, UnidadeFederativa.class);
			Collection<Municipio> municipios = dao.findByUF(idUf);
			municipiosEndereco = new ArrayList<SelectItem>(0);
			municipiosEndereco.add(new SelectItem(uf.getCapital().getId(), uf.getCapital().getNome()));
			municipiosEndereco.addAll(toSelectItems(municipios, "id", "nome"));
	
			obj.getEnderecoContato().setUnidadeFederativa(new UnidadeFederativa());
			obj.getEnderecoContato().getUnidadeFederativa().setId(uf.getId());
		} finally {
			dao.close();
		}
	}
	
	/**
	 * Listener responsável por setar o valor da UF de conclusão do Ensino
	 * Médio.<br/>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/public/vestibular/acompanhamento/novo_cadastro.jsp</li>
	 * </ul>
	 * 
	 * @param e
	 * @throws DAOException
	 */
	public void setaMunicipioConclusaoEnsinoMedio(ValueChangeEvent e) throws DAOException {
		String selectId = e.getComponent().getId();
		if (selectId != null && e.getNewValue() != null) {
			Integer idMunicipio = (Integer) e.getNewValue();
			if (idMunicipio != null) {
				EscolaInepDao dao = getDAO(EscolaInepDao.class);
				escolasInep = dao.findByMunicipioUF(idMunicipio, 0);
			}
		}
		obj.setEscolaConclusaoEnsinoMedio(new EscolaInep());
		obj.setNomeEscolaConclusaoEnsinoMedio(null);
	}
	
	/**
	 * Listener responsável por setar o valor do município da escola de conclusão do Ensino
	 * Médio.<br/>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/public/vestibular/acompanhamento/novo_cadastro.jsp</li>
	 * </ul>
	 * 
	 * @param e
	 * @throws DAOException
	 */
	public void setaUfConclusaoEnsinoMedio(ValueChangeEvent e)
			throws DAOException {
		String selectId = e.getComponent().getId();
		if (selectId != null && e.getNewValue() != null) {
			Integer ufId = (Integer) e.getNewValue();
			if (ufId != null) {
				EscolaInepDao dao = getDAO(EscolaInepDao.class);
				municipiosEscola = dao.findMunicipiosByUF(ufId);
				escolasInep = new ArrayList<EscolaInep>();
			}
		}
		obj.setEscolaConclusaoEnsinoMedio(new EscolaInep());
		obj.setNomeEscolaConclusaoEnsinoMedio(null);
	}
	
	/**
	 * Retorna uma coleção de EscolaInep para o recurso de autocompletar do
	 * formulário.<br/>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>sigaa.war/vestibular/dados_pessoais_candidato.jsp</li>
	 * </ul>
	 * 
	 * @param event
	 * @return
	 * @throws DAOException
	 */
	public Collection<EscolaInep> autocompleteEscolaConclusao(Object event)
			throws DAOException {
		String nome = event.toString();
		EscolaInepDao dao = getDAO(EscolaInepDao.class);
		return dao.findByNomeUF(nome, obj.getUfConclusaoEnsinoMedio().getId());
	}
	
	/**
	 * Atribui a escola de conclusão de ensino médio selecionada pelo usuário.<br/>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>sigaa.war/vestibular/dados_pessoais_candidato.jsp</li>
	 * </ul>
	 * 
	 * @param e
	 * @throws DAOException
	 */
	public void setEscolaInep(ActionEvent e) throws DAOException {
		int id = (Integer) e.getComponent().getAttributes().get("idEscola");
		EscolaInepDao dao = getDAO(EscolaInepDao.class);
		try {
			EscolaInep escola = dao.findByPrimaryKey(id, EscolaInep.class);
			if (escola != null) {
				obj.setEscolaConclusaoEnsinoMedio(escola);
				obj.setNomeEscolaConclusaoEnsinoMedio(null);
			}
		} finally {
			dao.close();
		}
	}
	
	/**
	 * Listener responsável por carregar a lista de municípios de naturalidade
	 * ou endereço, caso o valor da UF seja alterado no formulário.<br/>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>sigaa.war/vestibular/dados_pessoais_candidato.jsp</li>
	 * </ul>
	 * 
	 * @param e
	 * @throws DAOException
	 */
	public void carregarMunicipios(ValueChangeEvent e) throws DAOException {
		String selectId = e.getComponent().getId();
		if (selectId != null && e.getNewValue() != null) {
			Integer ufId = (Integer) e.getNewValue();
			if (selectId.toLowerCase().contains("natur")) {
				carregarMunicipiosNaturalidade(ufId);
			} else if (selectId.toLowerCase().contains("end")) {
				carregarMunicipiosEndereco(ufId);
			}
		}
	}
	
	/**
	 * Carrega a lista de municípios de naturalidade de uma determinada UF.<br/>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>sigaa.war/vestibular/dados_pessoais_candidato.jsp</li>
	 * </ul>
	 * 
	 * @param idUf
	 * @throws DAOException
	 */
	public void carregarMunicipiosNaturalidade(Integer idUf)
			throws DAOException {
		if (idUf == null) {
			idUf = obj.getUnidadeFederativa().getId();
		}

		MunicipioDao dao = getDAO(MunicipioDao.class);
		try{
			UnidadeFederativa uf = dao.findByPrimaryKey(idUf, UnidadeFederativa.class);
			Collection<Municipio> municipios = dao.findByUF(idUf);
			municipiosNaturalidade = new ArrayList<SelectItem>(0);
			municipiosNaturalidade.add(new SelectItem(uf.getCapital().getId(),uf.getCapital().getNome()));
			municipiosNaturalidade.addAll(toSelectItems(municipios, "id", "nome"));
	
			obj.setUnidadeFederativa(new UnidadeFederativa());
			obj.getUnidadeFederativa().setId(uf.getId());
			obj.setMunicipio(uf.getCapital());
		} finally {
			dao.close();
		}
	}
	
	
	
	/**
	 * Retorna uma lista de SelectItem de tipos de necessidades especiais.
	 * 
	 * @return
	 * @throws DAOException 
	 */
	public Collection<SelectItem> getAllTipoNecessidadeEspecialCombo() throws DAOException {
		if (tipoNecessidadeCombo == null) {
			tipoNecessidadeCombo = new ArrayList<SelectItem>();
			for (TipoNecessidadeEspecial tipo : getGenericDAO().findAll(TipoNecessidadeEspecial.class, "descricao", "asc")) {
				if (tipo.getId() > 0)
					tipoNecessidadeCombo.add(new SelectItem(tipo.getId(), tipo.getDescricao()));
			}
		}
		return tipoNecessidadeCombo;
	}
	
	public String getErroCPF() {
		return erroCPF;
	}

	public void setErroCPF(String erroCPF) {
		this.erroCPF = erroCPF;
	}

	public boolean isExibirPainel() {
		return exibirPainel;
	}

	public void setExibirPainel(boolean exibirPainel) {
		this.exibirPainel = exibirPainel;
	}

	/**
	 * Verifica o pais selecionado pelo candidato para nacionalidade e ajusta os dados de município e UF no formulário. <br/>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/public/vestibular/acompanhamento/novo_cadastro.jsp</li>
	 * </ul>
	 * 
	 * @param e
	 * @throws DAOException
	 */
	public void alterarPais(ValueChangeEvent e) throws DAOException {
		Integer idPais = (Integer) e.getNewValue();
		brasil = (idPais == Pais.BRASIL);
		if (brasil && obj.getMunicipio() == null) {
			obj.setMunicipio(new Municipio());
			obj.setUnidadeFederativa(new UnidadeFederativa());
		}
	}
	
	public boolean isBrasil() {
		return brasil;
	}

	public void setBrasil(boolean brasil) {
		this.brasil = brasil;
	}

	public Collection<SelectItem> getMunicipiosNaturalidade() {
		return municipiosNaturalidade;
	}

	public void setMunicipiosNaturalidade(
			Collection<SelectItem> municipiosNaturalidade) {
		this.municipiosNaturalidade = municipiosNaturalidade;
	}

	public Collection<SelectItem> getMunicipiosEndereco() {
		return municipiosEndereco;
	}

	public void setMunicipiosEndereco(Collection<SelectItem> municipiosEndereco) {
		this.municipiosEndereco = municipiosEndereco;
	}

	public List<SelectItem> getTipoNecessidadeCombo() {
		return tipoNecessidadeCombo;
	}

	public void setTipoNecessidadeCombo(List<SelectItem> tipoNecessidadeCombo) {
		this.tipoNecessidadeCombo = tipoNecessidadeCombo;
	}

	public Collection<Municipio> getMunicipiosEscola() {
		return municipiosEscola;
	}

	public void setMunicipiosEscola(Collection<Municipio> municipiosEscola) {
		this.municipiosEscola = municipiosEscola;
	}

	public Collection<EscolaInep> getEscolasInep() {
		return escolasInep;
	}

	public void setEscolasInep(Collection<EscolaInep> escolasInep) {
		this.escolasInep = escolasInep;
	}
}
