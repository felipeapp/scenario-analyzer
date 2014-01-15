package br.ufrn.sigaa.extensao.jsf.helper;

import br.ufrn.arq.dao.DAOFactory;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.extensao.AtividadeExtensaoDao;
import br.ufrn.sigaa.dominio.AreaConhecimentoCnpq;
import br.ufrn.sigaa.dominio.ModalidadeEducacao;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.extensao.dominio.AreaTematica;
import br.ufrn.sigaa.extensao.dominio.AtividadeExtensao;
import br.ufrn.sigaa.extensao.dominio.CursoEventoExtensao;
import br.ufrn.sigaa.extensao.dominio.EditalExtensao;
import br.ufrn.sigaa.extensao.dominio.ExecutorFinanceiro;
import br.ufrn.sigaa.extensao.dominio.LocalRealizacao;
import br.ufrn.sigaa.extensao.dominio.ProdutoExtensao;
import br.ufrn.sigaa.extensao.dominio.ProgramaEstrategicoExtensao;
import br.ufrn.sigaa.extensao.dominio.ProjetoExtensao;
import br.ufrn.sigaa.extensao.dominio.TipoAtividadeExtensao;
import br.ufrn.sigaa.extensao.dominio.TipoCursoEventoExtensao;
import br.ufrn.sigaa.extensao.dominio.TipoProduto;
import br.ufrn.sigaa.extensao.jsf.AtividadeExtensaoMBean;
import br.ufrn.sigaa.pesquisa.dominio.GrupoPesquisa;
import br.ufrn.sigaa.pessoa.dominio.Municipio;
import br.ufrn.sigaa.prodocente.producao.dominio.TipoRegiao;
import br.ufrn.sigaa.projetos.dominio.ClassificacaoFinanciadora;
import br.ufrn.sigaa.projetos.dominio.MembroProjeto;

/*******************************************************************************
 * Classe auxiliar para controlar o cadastro de atividades de extensão
 * 
 * @author Ricardo Wendell
 * 
 ******************************************************************************/
public class AtividadeExtensaoHelper {

	/**
	 * Retorna o Managed Bean geral de atividades de extensão
	 * 
	 * @return
	 */
	public static AtividadeExtensaoMBean getAtividadeMBean() {
		return (AtividadeExtensaoMBean) AtividadeExtensaoMBean
		.getMBean("atividadeExtensao");
	}

	/**
	 * Instancia os atributos nulos de uma atividade que serão utilizados nos
	 * formulários de cadastro.
	 * 
	 * @param atividade
	 * @return
	 */
	public static AtividadeExtensao instanciarAtributos(AtividadeExtensao atividade) {

		// Campos comuns às atividades
		if (atividade.getEditalExtensao() == null) {
			atividade.setEditalExtensao(new EditalExtensao());
		}
		if (atividade.getLocalRealizacao() == null) {
			atividade.setLocalRealizacao(new LocalRealizacao());
		}
		if (atividade.getLocalRealizacao().getMunicipio() == null) {
			atividade.getLocalRealizacao().setMunicipio(new Municipio());
		}
		if (atividade.getClassificacaoFinanciadora() == null) {
			atividade.setClassificacaoFinanciadora(new ClassificacaoFinanciadora());
		}
		if (atividade.getMembrosEquipe() != null) {
			atividade.getMembrosEquipe().size();
		}
		if (atividade.getAtividades() != null) {
			atividade.getAtividades().size();
		}
		if (atividade.getAreaTematicaPrincipal() == null) {
			atividade.setAreaTematicaPrincipal( new AreaTematica() );
		}

		//Evitar erro de Target Unreachable
		if (atividade.getAreaConhecimentoCnpq() == null) {
			atividade.setAreaConhecimentoCnpq(new AreaConhecimentoCnpq());
		}
		if (atividade.getTipoRegiao() == null) {
			atividade.setTipoRegiao(new TipoRegiao());
		}	    

		if (atividade.getProgramaEstrategico() == null) {
			atividade.setProgramaEstrategico(new ProgramaEstrategicoExtensao());
		}	    

		if (atividade.getProjeto().getCoordenador() == null) {
			atividade.getProjeto().setCoordenador(new MembroProjeto());
		}
		
		if ( atividade.getExecutorFinanceiro() == null ) {
			atividade.setExecutorFinanceiro(new ExecutorFinanceiro());
		}
		
		// Campos específicos de cada tipo de atividade
		switch (atividade.getTipoAtividadeExtensao().getId()) {
		case TipoAtividadeExtensao.PROJETO:
			if (atividade.getProjetoExtensao() == null) {
				atividade.setProjetoExtensao(new ProjetoExtensao());
			}
			if (atividade.getProjetoExtensao().getGrupoPesquisa() == null) {
				atividade.getProjetoExtensao().setGrupoPesquisa(new GrupoPesquisa());
			}
			break;
		case TipoAtividadeExtensao.PRODUTO:
			if (atividade.getProdutoExtensao() == null) {
				atividade.setProdutoExtensao(new ProdutoExtensao());
			}
			if (atividade.getProdutoExtensao().getTipoProduto() == null) {
				atividade.getProdutoExtensao().setTipoProduto(new TipoProduto());
			}
			
			break;
		case TipoAtividadeExtensao.PROGRAMA:
			if (atividade.getProjeto().getCoordenador() == null) {
				atividade.getProjeto().setCoordenador(new MembroProjeto());
			}
			break;
		case TipoAtividadeExtensao.CURSO:
		case TipoAtividadeExtensao.EVENTO:
			
			// Já no cadastro será necessário informar a unidade orçamentária, caso o curso cobre alguma taxa //
			if (atividade.getProjeto().getUnidadeOrcamentaria() == null) {
				atividade.getProjeto().setUnidadeOrcamentaria(new Unidade());
			}
			
			if (atividade.getCursoEventoExtensao() == null) {
				atividade.setCursoEventoExtensao(new CursoEventoExtensao());
			}
			if (atividade.getCursoEventoExtensao().getModalidadeEducacao() == null) {
				atividade.getCursoEventoExtensao().setModalidadeEducacao(new ModalidadeEducacao());
			}
			if (atividade.getCursoEventoExtensao().getTipoCursoEvento() == null) {
				atividade.getCursoEventoExtensao().setTipoCursoEvento(new TipoCursoEventoExtensao());
			}
		}

		return atividade;
	}
	
	/**
	 * Método utilizado na geração do código de uma ação de extensão.
	 * 
	 * @param acao
	 * @param dao
	 * @return
	 * @throws DAOException
	 */
	public static int gerarCodigoAcaoExtensao(AtividadeExtensao acao, AtividadeExtensaoDao dao) throws DAOException {
	    	int result = 0;
		int ano = acao.getAno();
		int tipoAtividade = acao.getTipoAtividadeExtensao().getId();
		
		// Retorna a próxima seq. disponível
		int next = dao.findNextSequencia(ano, tipoAtividade);
		if (next == 0){
			next++;
			// cria nova seq. iniciando em 1
			dao.novaSequencia(ano, tipoAtividade);
		}					
		result = new Integer(next);
		
		// Atualiza o banco com o valor da próxima seq. disponível.
		dao.updateNextSequencia(ano, tipoAtividade, ++next);
		
		return result;
	}
	
	/**
	 * Determina se a atividade de extensão é vinculada a algum programa de extensão.
	 * 
	 * @param atividadeExtensao
	 * @throws DAOException
	 */
	public static void determinaVinculoProgramaExtensao(AtividadeExtensao atividadeExtensao) throws DAOException {
		AtividadeExtensaoDao dao = DAOFactory.getInstance().getDAO(AtividadeExtensaoDao.class);
 		try {
 			AtividadeExtensao atividadeBD = dao.findByPrimaryKey(atividadeExtensao.getId(), AtividadeExtensao.class);
 			
 			//Excluir
			boolean vinculoPrograma = false;
			// Verifica se a atividade removida é vinculada a algum outro programa de extensão.
			// Caso não seja vinculado a nenhum programa, seu atributo 'vinculoProgramaExtensao' deve ser FALSE
			for (AtividadeExtensao atividade : atividadeBD.getAtividadesPai()) {
				if (atividade.getTipoAtividadeExtensao().isPrograma() && !atividade.equals(AtividadeExtensaoHelper.getAtividadeMBean().getObj())) {
					vinculoPrograma = true;
					break;
				}
			}
			if (!vinculoPrograma)
				dao.updateField(AtividadeExtensao.class, atividadeExtensao.getId(), "vinculoProgramaExtensao", Boolean.FALSE);

			//Cadastrar
			for (AtividadeExtensao atividade : AtividadeExtensaoHelper.getAtividadeMBean().getObj().getAtividades())
				dao.updateField(AtividadeExtensao.class, atividade.getId(), "vinculoProgramaExtensao", Boolean.TRUE);

		} finally {
			dao.close();
		}
	}
	
	/**
	 * Retorna o número de membros do projeto de determinada categoria.
	 * @param categoria
	 * @return
	 * @throws DAOException
	 */
	public static int getQtdeMembrosProjetoByCategoria(Integer categoria) throws DAOException{
		int numMembros = 0;
		for (MembroProjeto membro : AtividadeExtensaoHelper.getAtividadeMBean().getMembrosEquipe()) {
			if (membro.getCategoriaMembro().getId() == categoria)
				numMembros++;
		}

		return numMembros;
	}

}