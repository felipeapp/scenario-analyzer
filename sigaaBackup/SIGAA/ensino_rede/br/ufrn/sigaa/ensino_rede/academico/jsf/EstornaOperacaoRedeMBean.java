package br.ufrn.sigaa.ensino_rede.academico.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;
import static br.ufrn.arq.util.ValidatorUtil.isNotEmpty;

import java.util.ArrayList;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ensino_rede.academico.dao.MovimentacaoDiscenteAssociadoDao;
import br.ufrn.sigaa.ensino_rede.academico.dominio.MovimentacaoDiscenteAssociado;
import br.ufrn.sigaa.ensino_rede.dominio.DiscenteAssociado;
import br.ufrn.sigaa.ensino_rede.dominio.StatusDiscenteAssociado;
import br.ufrn.sigaa.ensino_rede.jsf.EnsinoRedeAbstractController;
import br.ufrn.sigaa.ensino_rede.jsf.SelecionaDiscente;
import br.ufrn.sigaa.ensino_rede.jsf.SelecionaDiscenteMBean;
/**
 * MBean responsável em estornar as movimentações de Discentes.
 * @author Jeferson Queiroga
 *
 */
@SuppressWarnings("serial")
@Component@Scope("session")
public class EstornaOperacaoRedeMBean extends EnsinoRedeAbstractController<MovimentacaoDiscenteAssociado> implements SelecionaDiscente  {
	
	/** Constante que guarda o Caminho da JSP da listagem*/
	public static final String LISTA_MOVIMENTO = "/ensino_rede/estornar_movimentacao/lista.jsf";
	
	/** Constante que guarda o Caminho da JSP da página da confirmação*/
	public static final String JSP_CONFIRMAR = "/ensino_rede/estornar_movimentacao/confirmar.jsf"; 
	
	/**
	 * Método para instanciar os objetos
	 */
	public void clean(){
		obj = new MovimentacaoDiscenteAssociado();
	}
	
	/**
	 * Método que inica o caso de uso.
	 * <br/>
	 * JSP: /sigaa.war/ensino_rede/portal/menu_coordenador_rede.jsp
	 * @return
	 * @throws DAOException
	 * @throws NegocioException
	 * @throws SegurancaException 
	 */
	public String iniciar() throws DAOException, NegocioException, SegurancaException{
		checkRole(SigaaPapeis.COORDENADOR_GERAL_REDE, SigaaPapeis.COORDENADOR_UNIDADE_REDE);
		clean();
		SelecionaDiscenteMBean mBean = getMBean("selecionaDiscenteMBean");
		mBean.setRequisitor(this);
		mBean.filterByStatus(new ArrayList<StatusDiscenteAssociado>());
							
		if (isCoordenadorUnidadeRede())
			return mBean.executar(getCampusIes());
		else
			return mBean.executar();
	}

	@Override
	public void setDiscente(DiscenteAssociado discente) {
		if( isNotEmpty( discente )){
			obj.setDiscente(discente);
		}
	}

	@Override
	public String selecionaDiscente() throws ArqException {
		MovimentacaoDiscenteAssociadoDao dao = getDAO(MovimentacaoDiscenteAssociadoDao.class);
		setResultadosBusca(  dao.findAtivosByExactField(MovimentacaoDiscenteAssociado.class, "discente.id", obj.getDiscente().getId() ) );
		prepareMovimento(SigaaListaComando.ESTORNAR_MOVIMENTACAO);
		setOperacaoAtiva(SigaaListaComando.ESTORNAR_MOVIMENTACAO.getId());
		
		if( isEmpty( getResultadosBusca() )  ){
			addMensagemErro("O Discente selecionado não possui movimentação para estornar.");
			return null;
		}
		
		return forward(LISTA_MOVIMENTO);
	}
	
	/**
	 * Método para selecionar a movimentação e direcionar para a confirmação do estorno.
	 * <br/>
	 * JSP:/sigaa.war/ensino_rede/estornar_movimentacao/lista.jsp
	 * @return
	 * @throws ArqException 
	 */
	public String selecionarMovimentacao() throws ArqException {
		Integer id = getParameterInt("idMovimentacao");
		obj = getGenericDAO().findByPrimaryKey(id, MovimentacaoDiscenteAssociado.class);
		if(!obj.isAtivo()){
			cancelar();
		}
		
		return forward(JSP_CONFIRMAR);
	}
	
	
	/**
	 * Método para selecionar a movimentação e direcionar para a confirmação do estorno.
	 * <br/>
	 * JSP:/ensino_rede/estornar_movimentacao/confirmar.jsf
	 * @return
	 * @throws ArqException
	 */
	public String cadastrar() throws ArqException{
		
		if( !checkOperacaoAtiva(SigaaListaComando.ESTORNAR_MOVIMENTACAO.getId() )  ){
			cancelar();
		}
		
		MovimentoCadastro mov = new MovimentoCadastro();
		mov.setCodMovimento(SigaaListaComando.ESTORNAR_MOVIMENTACAO);
		mov.setObjMovimentado(obj);
		
		try {
			execute(mov);
			addMensagemInformation("Estorno Realizado com sucesso");
		} catch(NegocioException e) {
			addMensagemErro("Não foi estornar a operação.");
			return null;
		}
		
		removeOperacaoAtiva();
		return redirectJSF(getSubSistema().getLink());
	}
	
	
	public String voltarDiscente(){
		SelecionaDiscenteMBean MBean = getMBean("selecionaDiscenteMBean");
		return MBean.voltar();
	}
	/**
	 * Método que retorna a lista de movimentações
	 * <br/>
	 * JSP: /sigaa.war/ensino_rede/estornar_movimentacao/confirmar.jsp
	 * @return
	 */
	public String voltarMovimentacao(){
		return forward( LISTA_MOVIMENTO );
	}
	
	
}
