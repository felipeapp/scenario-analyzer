/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 08/10/2007
 * 
 */
package br.ufrn.sigaa.pesquisa.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;
import java.util.Collection;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.projetos.dominio.Edital;
import br.ufrn.sigaa.projetos.dominio.FuncaoMembro;

/**
 * ManagedBean responsável pelo controle e operações referentes as funções dos membros de equipes de pesquisa.
 * 
 * @author Ilueny
 */
@Component("funcaoMembroEquipe")
@Scope("request")
public class FuncaoMembroEquipeMBean extends SigaaAbstractController<FuncaoMembro> {
	
	/**
	 * Construtor padrão
	 */
	public FuncaoMembroEquipeMBean() {
		obj = new FuncaoMembro();
		obj.setCadastrarEm( new ArrayList<String>() );
	}

	public Collection<SelectItem> getAllCombo() {
		return getAll(FuncaoMembro.class, "id", "descricao");
	}

	/**
	 * Método utilizado para informar todos os servidores em um ComboBox
	 * @return
	 */
	public Collection<SelectItem> getAllServidoresCombo() {
		GenericDAO dao = getGenericDAO();
		try {
			if ( isEmpty( getDescricaoTipoEdital() ) )
				return toSelectItems( dao.findByExactField( FuncaoMembro.class, "escopo", FuncaoMembro.ESCOPO_SERVIDOR, "asc", "descricao" ), "id", "descricao" );
			else
				return toSelectItems( dao.findByExactField( FuncaoMembro.class, new String[]{"escopo", getDescricaoTipoEdital() }, 
						new Object[]{FuncaoMembro.ESCOPO_SERVIDOR, Boolean.TRUE}), "id", "descricao" );
		} catch (DAOException e) {
			addMensagemErro("Erro ao carregar funções dos membros.");
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Método utilizado para representar todos os discentes em um ComboBox
	 * @return
	 */
	public Collection<SelectItem> getAllDiscentesCombo() {
		GenericDAO dao = getGenericDAO();
		try {
			if ( isEmpty( getDescricaoTipoEdital() ) )
				return toSelectItems( dao.findByExactField( FuncaoMembro.class, "escopo", FuncaoMembro.ESCOPO_DISCENTE, "asc", "descricao" ), "id", "descricao" );
			else
				return toSelectItems( dao.findByExactField( FuncaoMembro.class, new String[]{"escopo", getDescricaoTipoEdital() }, 
						new Object[]{FuncaoMembro.ESCOPO_DISCENTE, Boolean.TRUE}), "id", "descricao" );
		} catch (DAOException e) {
			addMensagemErro("Erro ao carregar funções dos membros.");
			e.printStackTrace();
		}
		return null;
	}

	/***
	 * Lista somente os tipos de fução que um discente colaborador pode assumir ao entrar no projeto.
	 * @return
	 */
	public Collection<SelectItem> getDiscentesColaboradoresCombo() {	    
		GenericDAO dao = getGenericDAO();
		try {
		    Collection<FuncaoMembro> lista = new ArrayList<FuncaoMembro>();
		    lista.add( dao.findByPrimaryKey(FuncaoMembro.VOLUNTARIO, FuncaoMembro.class));
		    lista.add( dao.findByPrimaryKey(FuncaoMembro.ALUNO_EM_ATIVIDADE_CURRICULAR, FuncaoMembro.class));
		    return toSelectItems( lista, "id", "descricao" );
		} catch (DAOException e) {
		    addMensagemErro("Erro ao carregar funções dos membros.");
		    e.printStackTrace();
		}
		return null;
	}

	/** Serve para setar as informações dos projeto que possuem tal função */
	@Override
	public void beforeCadastrarAndValidate() throws NegocioException,
			SegurancaException, DAOException {
		for (String tipo : obj.getCadastrarEm()) {
			if ( tipo.equals(Character.toString(Edital.ASSOCIADO)))
				obj.setIntegrados(true);
			if (tipo.equals(Character.toString(Edital.PESQUISA)))
				obj.setPesquisa(true);
			if (tipo.equals(Character.toString(Edital.EXTENSAO)))
				obj.setExtensao(true);
			if (tipo.equals(Character.toString(Edital.MONITORIA)))
				obj.setEnsino(true);
		}
	}
	
	/** Utilizado para carregar e marcar os combos os tipos de projeto */
	@Override
	public void afterAtualizar() throws ArqException {
		obj.setCadastrarEm( new ArrayList<String>() );
		if ( obj.isPesquisa() )
			obj.getCadastrarEm().add(Character.toString(Edital.PESQUISA));
		if ( obj.isEnsino() )
			obj.getCadastrarEm().add(Character.toString(Edital.MONITORIA));
		if ( obj.isExtensao() )
			obj.getCadastrarEm().add(Character.toString(Edital.EXTENSAO));
		if ( obj.isIntegrados() )
			obj.getCadastrarEm().add(Character.toString(Edital.ASSOCIADO));
	}
	
	/**
	 * Inicia o cadastro de tipos de Função Membro Equipe.
	 * Chamado por:
	 * <ul><li>/sigaa.war/extensao/menu.jsp</li></ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String iniciarCadastro() throws ArqException {
	    prepareMovimento(ArqListaComando.CADASTRAR);
	    return forward("/extensao/FuncaoMembro/form.jsp");
	}

	/** Utilizado para gerar o tipo de consulta correspondente  */
	public String getDescricaoTipoEdital() {
		switch (getTipoEdital()) {
		case Edital.ASSOCIADO:
			return "integrados";
		case Edital.PESQUISA:
			return "pesquisa";
		case Edital.EXTENSAO:
			return "extensao";
		case Edital.MONITORIA:
			return "ensino";
		default:
			return null;
		}
	}
	
	/** Seta o diretório na qual as views estão presentes */
	@Override
	public String getDirBase() {
		return "/extensao/FuncaoMembro";
	}
	
}