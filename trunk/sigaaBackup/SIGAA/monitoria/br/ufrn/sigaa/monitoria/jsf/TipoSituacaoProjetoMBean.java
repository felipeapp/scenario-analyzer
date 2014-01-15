/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Created on '06/10/2006'
 *
 */
package br.ufrn.sigaa.monitoria.jsf;

import java.util.ArrayList;
import java.util.Collection;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.projetos.dominio.TipoSituacaoProjeto;

/**
 * Classe que controla na view os tipos de situa��o poss�veis de projetos. 
 * 
 * @author UFRN
 *
 */
@Component("tipoSituacaoProjeto")
@Scope("request")
public class TipoSituacaoProjetoMBean extends SigaaAbstractController<TipoSituacaoProjeto> {

	public TipoSituacaoProjetoMBean() {
		obj = new TipoSituacaoProjeto();
	}

	public Collection<SelectItem> getSituacoesProjetoPesquisa() throws DAOException{
		GenericDAO dao = getGenericDAO();
		return toSelectItems(dao.findByExactField(TipoSituacaoProjeto.class, "contexto", "P", "asc", "id" ), "id", "descricao");
	}

	/**
	 * Retorna as situa��es v�lidas dos projetos de pesquisa
	 * @return
	 * @throws DAOException
	 */
	public Collection<SelectItem> getSituacoesProjetoPesquisaValidos() throws DAOException{
		ArrayList<TipoSituacaoProjeto> situacoesValidas = new ArrayList<TipoSituacaoProjeto>();
		situacoesValidas.add(new TipoSituacaoProjeto( TipoSituacaoProjeto.EM_ANDAMENTO, "Em Andamento"));
		situacoesValidas.add(new TipoSituacaoProjeto( TipoSituacaoProjeto.RENOVADO, "Renovado" ));
		situacoesValidas.add(new TipoSituacaoProjeto( TipoSituacaoProjeto.FINALIZADO, "Finalizado" ));
		return toSelectItems(situacoesValidas, "id", "descricao");
	}

	/**
	 * Retorna as situa��es v�lidas de A��es de Associadas
	 * @return
	 * @throws DAOException
	 */
	public Collection<SelectItem> getSituacoesAcoesAssociadasValidas() throws DAOException{
		ArrayList<TipoSituacaoProjeto> situacoesValidas = new ArrayList<TipoSituacaoProjeto>();
		situacoesValidas.add(new TipoSituacaoProjeto( TipoSituacaoProjeto.PROJETO_BASE_CADASTRO_EM_ANDAMENTO, "CADASTRO EM ANDAMENTO"));
		situacoesValidas.add(new TipoSituacaoProjeto( TipoSituacaoProjeto.PROJETO_BASE_CONCLUIDO, "CONCLU�DO" ));
		situacoesValidas.add(new TipoSituacaoProjeto( TipoSituacaoProjeto.PROJETO_BASE_EM_EXECUCAO, "EM EXECU��O" ));
		situacoesValidas.add(new TipoSituacaoProjeto( TipoSituacaoProjeto.PROJETO_BASE_REPROVADO, "N�O RECOMENDADO" ));
		situacoesValidas.add(new TipoSituacaoProjeto( TipoSituacaoProjeto.PROJETO_BASE_SUBMETIDO, "SUBMETIDO" ));
		situacoesValidas.add(new TipoSituacaoProjeto( TipoSituacaoProjeto.PROJETO_BASE_APROVADO_COM_RECURSOS, "APROVADO COM RECURSOS" ));
		situacoesValidas.add(new TipoSituacaoProjeto( TipoSituacaoProjeto.PROJETO_BASE_APROVADO_SEM_RECURSOS, "APROVADO SEM RECURSOS" ));
		
		situacoesValidas.add(new TipoSituacaoProjeto( TipoSituacaoProjeto.PROJETO_BASE_AGUARDANDO_AVALIACAO, "AGUARDANDO AVALIA��O" ));
		situacoesValidas.add(new TipoSituacaoProjeto( TipoSituacaoProjeto.PROJETO_BASE_ANALISANDO_PEDIDO_RECONSIDERACAO, "ANALISANDO RECONSIDERA��O" ));
		situacoesValidas.add(new TipoSituacaoProjeto( TipoSituacaoProjeto.PROJETO_BASE_PEDIDO_RECONSIDERACAO_APROVADO, "RECONSIDERA��O APROVADA" ));
		return toSelectItems(situacoesValidas, "id", "descricao");
	}

	/**
	 * Utilizado para preencher os menus de op��o com as situa��es de a��o v�lidas.
	 * 
	 * Chamado por:
	 * sigaa.war/extensao/Relatorios/busca_relatorio_planejamento.jsp
	 * 
	 * @return
	 * @throws DAOException
	 */
	public Collection<SelectItem> getSituacoesAcaoExtensaoValidas() throws DAOException{
		ArrayList<TipoSituacaoProjeto> situacoesValidas = new ArrayList<TipoSituacaoProjeto>();
		situacoesValidas.add(new TipoSituacaoProjeto( TipoSituacaoProjeto.EXTENSAO_CADASTRO_EM_ANDAMENTO, "CADASTRO EM ANDAMENTO"));
		situacoesValidas.add(new TipoSituacaoProjeto( TipoSituacaoProjeto.EXTENSAO_CONCLUIDO, "CONCLU�DO" ));
		situacoesValidas.add(new TipoSituacaoProjeto( TipoSituacaoProjeto.EXTENSAO_EM_EXECUCAO, "EM EXECU��O" ));
		situacoesValidas.add(new TipoSituacaoProjeto( TipoSituacaoProjeto.EXTENSAO_REPROVADO, "REPROVADO" ));
		situacoesValidas.add(new TipoSituacaoProjeto( TipoSituacaoProjeto.EXTENSAO_SUBMETIDO, "SUBMETIDO" ));
		return toSelectItems(situacoesValidas, "id", "descricao");
	}
	
	/**
	 * Retorna as situa��es v�lidas no momento da aprova��o/reprova��o da proposta.
	 * 
	 * @return
	 * @throws DAOException
	 */
	public Collection<SelectItem> getSituacoesAcoesAssociadasAnalisandoProposta() throws DAOException{
		ArrayList<TipoSituacaoProjeto> situacoesValidas = new ArrayList<TipoSituacaoProjeto>();
		situacoesValidas.add(new TipoSituacaoProjeto( TipoSituacaoProjeto.PROJETO_BASE_APROVADO_COM_RECURSOS, "APROVADO COM RECURSOS" ));
		situacoesValidas.add(new TipoSituacaoProjeto( TipoSituacaoProjeto.PROJETO_BASE_APROVADO_SEM_RECURSOS, "APROVADO SEM RECURSOS" ));
		situacoesValidas.add(new TipoSituacaoProjeto( TipoSituacaoProjeto.PROJETO_BASE_CONCLUIDO, "CONCLU�DO" ));
		situacoesValidas.add(new TipoSituacaoProjeto( TipoSituacaoProjeto.PROJETO_BASE_EM_EXECUCAO, "EM EXECU��O" ));
		situacoesValidas.add(new TipoSituacaoProjeto( TipoSituacaoProjeto.PROJETO_BASE_REPROVADO, "N�O RECOMENDADO" ));
		
		return toSelectItems(situacoesValidas, "id", "descricao");
	}


	
}
