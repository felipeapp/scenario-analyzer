package br.ufrn.sigaa.projetos.jsf;

import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.projetos.dominio.TipoSituacaoAvaliacao;

/**
 * 
 * @author Julio
 * MBean responsável pelo gerenciamento das situações das avaliações
 */
@Component("situacaoAvaliacaoBean")
@Scope("request")
public class SituacaoAvaliacaoMBean extends SigaaAbstractController<TipoSituacaoAvaliacao>{
	
	/** Construtor */
	public SituacaoAvaliacaoMBean(){
		obj = new TipoSituacaoAvaliacao();
	}
	
	/** 
	 * Atualiza o diretório base para navegação de páginas 
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>sigaa.war/projetos/Avaliacoes/SituacaoAvaliacao/lista.jsp</li>
	 * 	<li>sigaa.war/projetos/Avaliacoes/SituacaoAvaliacao/form.jsp</li>
	 * 	<li>sigaa.war/projetos/Avaliacoes/SituacaoAvaliacao/remove.jsp</li>
	 * </ul>
	 * @return
	 */
	@Override
	public String getDirBase() {
		return "/projetos/Avaliacoes/SituacaoAvaliacao";
	}
	
	/** 
	 * Verifica se o usuário é membro do CIEPE
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>sigaa.war/projetos/Avaliacoes/SituacaoAvaliacao/lista.jsp</li>
	 * 	<li>sigaa.war/projetos/Avaliacoes/SituacaoAvaliacao/form.jsp</li>
	 * </ul>
	 * @throws SegurancaException
	 */
	@Override
	public void checkChangeRole() throws SegurancaException {
		checkRole(SigaaPapeis.MEMBRO_COMITE_INTEGRADO);
	}
	
	/**
	 * Realiza validações antes de cadastrar e validar
	 * <br>
	 * <ul>
	 * 	<li>sigaa.war/projetos/Avaliacoes/SituacaoAvaliacao/form.jsp</li>
	 * </ul>
	 * @throws NegocioException 
	 */
	@Override
	public void beforeCadastrarAndValidate() throws NegocioException,
			SegurancaException, DAOException {
		if(!obj.getDescricao().isEmpty()){
			if( (getGenericDAO().findAtivosByExactField(TipoSituacaoAvaliacao.class, "descricao", obj.getDescricao())).size() > 0  ){
				addMensagemErro("Essa situação de avaliação ja está cadastrada.");
			}else{
				String uperCase = obj.getDescricao();
				obj.setDescricao(uperCase.toUpperCase());
			}
		}else addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Descrição");
	}
	
	/**
	 * Redireciona para a tela de listagem
	 * <br>
	 * <ul>
	 * 	<li>sigaa.war/projetos/Avaliacoes/SituacaoAvaliacao/form.jsp</li>
	 * </ul>
	 * @return
	 */
	@Override
	public String forwardCadastrar() {
		return getListPage();
	}
		

	/**
	 * Redireciona para tela de listagem
	 * <br>
	 * <ul>
	 *	<li>sigaa.war/projetos/Avaliacoes/SituacaoAvaliacao/lista.jsp</li>
	 * 	<li>sigaa.war/projetos/Avaliacoes/SituacaoAvaliacao/form.jsp</li>
	 * 	<li>sigaa.war/projetos/Avaliacoes/SituacaoAvaliacao/remove.jsp</li>
	 * </ul>
	 * @return
	 */
	public String toLista(){
		return redirect("/projetos/Avaliacoes/SituacaoAvaliacao/lista.jsf");
	}
	
	/**
	 * Lista todos os tipos de situação
	 * <br>
	 * <ul>
	 * 	<li>sigaa.war/projetos/Avaliacoes/SituacaoAvaliacao/lista.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException
	 */
	public List<TipoSituacaoAvaliacao> getTiposSituacao() throws DAOException{
		return (List<TipoSituacaoAvaliacao>) getGenericDAO().findAllAtivos(TipoSituacaoAvaliacao.class, "id");
	}
}
