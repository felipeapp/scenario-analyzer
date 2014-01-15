/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Created on '27/03/2007'
 *
 */
package br.ufrn.sigaa.prodocente.negocio;

import java.rmi.RemoteException;
import java.util.List;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.ProcessadorCadastro;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.prodocente.relatorios.dominio.GrupoItem;
import br.ufrn.sigaa.prodocente.relatorios.dominio.GrupoRelatorioProdutividade;
import br.ufrn.sigaa.prodocente.relatorios.dominio.RelatorioProdutividade;

/**
 * Processador respons�vel pelo cadastro do relat�rio
 *
 * @author Eric Moura (Eriquim)
 *
 */
public class ProcessadorRelatorioProdutividade extends ProcessadorCadastro {

	@Override
	public Object execute(Movimento movimento) throws NegocioException, ArqException, RemoteException {
		validate(movimento);

		if (movimento.getCodMovimento().equals(SigaaListaComando.CADASTRAR_RELATORIO_PRODUTIVIDADE))
			return criar((MovimentoCadastro) movimento);
		else if (movimento.getCodMovimento().equals(SigaaListaComando.ALTERAR_RELATORIO_PRODUTIVIDADE))
			return alterar((MovimentoCadastro) movimento);
		return null;
	}


	@Override
	protected Object alterar(MovimentoCadastro mov) throws DAOException, NegocioException {
		GenericDAO dao = getGenericDAO(mov);

		RelatorioProdutividadeMov movRel = (RelatorioProdutividadeMov) mov;

		List<GrupoRelatorioProdutividade> gruposRelatorio = ((RelatorioProdutividade) movRel.getObjMovimentado()).getGrupoRelatorioProdutividadeCollection();

//		Testar se n�o existe grupos vazios
		for(GrupoRelatorioProdutividade gruporel: gruposRelatorio){
			if(gruporel.getGrupoItemCollection().isEmpty()){
				NegocioException ne = new NegocioException();
				ne.addErro("N�o � permitido cadastrar relat�rio com grupo vazio! ("+gruporel.getTitulo()+")");
			}
		}

//		Apagando os itens exclu�dos do relat�rio
	    if(!movRel.getGrupoItemExcluidos().isEmpty())
	    	for(GrupoItem grupoItem: movRel.getGrupoItemExcluidos())
	    		dao.remove(grupoItem);

		//Apagando os grupos e itens filhos do grupo que foram exclu�dos - N�o precisa apagar nos filhos, pois j� est� configurado no banco
	    if(!movRel.getGrupoRelatorioProdutividadeExcluidos().isEmpty())
	    	for(GrupoRelatorioProdutividade grupoRelatorioProdutividade: movRel.getGrupoRelatorioProdutividadeExcluidos())
	    		dao.remove(grupoRelatorioProdutividade);

        dao.update(mov.getObjMovimentado());
		dao.close();
		return mov.getObjMovimentado();

	}

}
