/* 
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 * Criado em: 18/01/2008
 */
package br.ufrn.sigaa.ava.jsf;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ava.dao.TurmaVirtualDao;
import br.ufrn.sigaa.ava.dominio.AcaoAva;
import br.ufrn.sigaa.ava.dominio.DataAvaliacao;
import br.ufrn.sigaa.ava.dominio.EntidadeRegistroAva;
import br.ufrn.sigaa.ava.validacao.EspecificacaoCadastroDataAvaliacao;
import br.ufrn.sigaa.ava.validacao.Notification;
import br.ufrn.sigaa.ava.validacao.Specification;
import br.ufrn.sigaa.ava.validacao.TurmaVirtualException;

/**
 * Managed bean para o cadastro de datas de avaliações.
 * 
 * @author David Pereira
 *
 */
@Component("dataAvaliacao") @Scope("request")
public class DataAvaliacaoMBean extends CadastroTurmaVirtual<DataAvaliacao> {

	/** Id da avaliação que foi selecionada para ser removida sem ser passado o id pela requisição. */
	private int idAvaliacaoSelecionada;
	
	public DataAvaliacaoMBean (){
		object = new DataAvaliacao();
	}
	
	/**
     * Retorna a lista com todas as avaliações da turma.<br/><br/>
	 * 
     * Método não invocado por JSPs. É public por causa da arquitetura.
     */
	@Override
	public List<DataAvaliacao> lista() {
	    return getDAO(TurmaVirtualDao.class).buscarDatasAvaliacao(turma());
	}
	
	/**
     * Cadastra a data da avaliação.<br/><br/>
	 * 
	 * Método chamado pelas seguintes JSPs:
     * <ul>
     * 		<li>/ava/DataAvaliacao/novo.jsp</li>
	 * 		<li>/ava/PlanoEnsino/novo.jsp</li>
     * </ul>
     */
	@Override
	public String cadastrar() throws ArqException {
		
		boolean ok = true;
		if (StringUtils.isEmpty (object.getDescricao())){
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Descrição");
			ok = false;
		}
			
		if (object.getData() == null){
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Data");
			ok = false;
		}
			
		if (StringUtils.isEmpty(object.getHora())){
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Hora");
			ok = false;
		}
		
		if (object.getData() != null){
			List<DataAvaliacao> datasAvaliacao = (List<DataAvaliacao>) getGenericDAO().findByExactField(DataAvaliacao.class, new String[]{"turma.id","ativo"},
					new Object[]{turma().getId(),true});
			
			if (datasAvaliacao != null && !datasAvaliacao.isEmpty()){
				for (DataAvaliacao data : datasAvaliacao){
					if ( data.getData().getTime() == object.getData().getTime() ){
						addMensagemErro("A avaliação \""+object.getDescricao()+ "\" possui a mesma data que a avaliação \"" 
									+data.getDescricao()+ "\". Não pode existir mais de uma avaliação no mesmo dia.");
						ok = false;
					}
				}
			}
		}
		
		if (ok){
			if (cadastrarEm == null){
				cadastrarEm = new ArrayList <String> ();
				cadastrarEm.add("" + turma().getId());
			}
				
			super.cadastrar();
			return redirect("/ava/" + getClasse().getSimpleName() + "/listar.jsf");
		}
		
		return null;
	}
	
	/**
	 * Utilizado para exibir os detalhes de uma data de avaliação.<br/><br/>
	 * 
	 * Método chamado pelas seguintes JSPs:
     * <ul>
     * 		<li>/ava/DataAvaliacao/editar.jsp</li>
	 * 		<li>/ava/DataAvaliacao/listar.jsp</li>
	 * 		<li>/ava/PlanoEnsino/listar.jsp</li>
     * </ul>
	 */
   public String mostrar() throws DAOException {
		if (getParameterInt("id") == null && (object == null || object.getId() == 0)) {
			addMensagemErro("Nenhum objeto para alterar.");
			return null;
		}	   	   
		
		int id = getParameterInt("id",0);
		if (id == 0)
			id = object.getId();			
	   
		object = getGenericDAO().findByPrimaryKey(id, getClasse());
		registrarAcao(object.getDescricao(), EntidadeRegistroAva.AVALIACAO, AcaoAva.ACESSAR, id);
		
        registrarLogAcessoDiscenteTurmaVirtual(getClasse().getName(), id, turma().getId());
       
        if (acessoPublico())
            return forward("/ava/" + getClasse().getSimpleName() + "/mostrarPublico.jsp");
        else
            return forward("/ava/" + getClasse().getSimpleName() + "/mostrar.jsp");
    }
   
	/**
	 * Inicia a edição da Data Avaliação.
	 * <br/><br/> 
	 * Método chamado pelas seguintes JSPs:
     * <ul>
     * 		<li>/ava/DataAvaliacao/editar.jsp</li>
	 * 		<li>/ava/DataAvaliacao/listar.jsp</li>
     * </ul>
	 */
   public String editar(){
		if (getParameterInt("id") == null && (object == null || object.getId() == 0)) {
			addMensagemErro("Nenhum objeto para alterar.");
			return null;
		}	   	   
		
		int id = getParameterInt("id",0);
		if (id == 0)
			id = object.getId();
		
		try {
			object = getGenericDAO().findByPrimaryKey(id, getClasse());
			instanciarAposEditar();
			prepare(SigaaListaComando.ATUALIZAR_AVA);
		} catch (Exception e) {
			throw new TurmaVirtualException(e);
		} 
		return forward("/ava/" + getClasse().getSimpleName() + "/editar.jsp");	   
   }
	
	@Override
	public Specification getEspecificacaoCadastro() {
		return new EspecificacaoCadastroDataAvaliacao();
	}
	
	/**
	 * Indica para qual página o usuário deve seguir após cadastrar.<br/><br/>
	 * 
     * Método não invocado por JSPs. É public por causa da arquitetura.
	 */
	@Override
	public String forwardCadastrar (){
		return "/ava/" + getClasse().getSimpleName() + "/novo.jsf";
	}
	
	/**
     * Remove a data da avaliação.<br/><br/>
	 * 
	 * Método chamado pelas seguintes JSPs:
     * <ul>
     * 		<li>/ava/DataAvaliacao/lsitar.jsp</li>
     * </ul>
     */
	@Override
	public String remover() {
		try {
			if (idAvaliacaoSelecionada > 0){
				object = new DataAvaliacao();
				object.setId(idAvaliacaoSelecionada);
			} else
				object = getGenericDAO().findByPrimaryKey(getParameterInt("id"), getClasse());
			
			prepare(SigaaListaComando.REMOVER_DATA_AVALIACAO);
			
			Notification notification = null;
			if ( object != null ) {
				registrarAcao(object.getDescricao(), EntidadeRegistroAva.AVALIACAO, AcaoAva.INICIAR_REMOCAO, object.getId());
				notification = execute(SigaaListaComando.REMOVER_DATA_AVALIACAO, object, getEspecificacaoRemocao());	
			}

			if (notification != null && notification.hasMessages())
				return notifyView(notification);
			
			registrarAcao(object.getDescricao(), EntidadeRegistroAva.AVALIACAO, AcaoAva.REMOVER, object.getId());
			listagem = null;		
			addMensagem(MensagensArquitetura.OPERACAO_SUCESSO);
			
		} catch (DAOException e) {
			throw new TurmaVirtualException(e);
		}

		return redirect("/ava/" + getClasse().getSimpleName() + "/listar.jsf");
	}
	
	@Override
	public void aposPersistir (){
		TurmaVirtualMBean tBean = getMBean("turmaVirtual");
		tBean.setDatasAvaliacoes(null);
	}
	
	public int getIdAvaliacaoSelecionada() {
		return idAvaliacaoSelecionada;
	}

	public void setIdAvaliacaoSelecionada(int idAvaliacaoSelecionada) {
		this.idAvaliacaoSelecionada = idAvaliacaoSelecionada;
	}
}
