/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas
 * Superintendência de Informática - UFRN
 *
 * Created on Jun 27, 2007
 *
 */
package br.ufrn.sigaa.ensino.negocio;

import static br.ufrn.arq.util.ValidatorUtil.isNotEmpty;

import java.rmi.RemoteException;
import java.util.Date;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.sigaa.arq.dao.ensino.MatriculaComponenteDao;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.dominio.MatriculaImplantada;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.negocio.dominio.ImplantarHistoricoMov;
import br.ufrn.sigaa.pessoa.dominio.Discente;

/**
 * Processador que realiza a persistência de objetos MatriculaComponente
 * presentes na lista getMatriculas() do objeto ImplantarHistoricoMov
 * recebido como parâmetro do método execute.
 *
 * @author Victor Hugo
 *
 */
public class ProcessadorImplantarHistorico extends AbstractProcessador {

	public Object execute(Movimento movimento) throws NegocioException, ArqException,
			RemoteException {

		validate(movimento);

		ImplantarHistoricoMov mov = (ImplantarHistoricoMov) movimento;
		GenericDAO dao = getGenericDAO(mov);

		Discente d = mov.getDiscente();

		/**
		 * Cadastrando as novas matrículas implantadas
		 */
		for( MatriculaComponente mc : mov.getMatriculas() ){
			
			mc.setDataCadastro( new Date() );
			mc.setRegistroEntrada( mov.getUsuarioLogado().getRegistroEntrada() );
			mc.setDiscente(d);

			mc.setNotas(null);
			mc.setRecuperacao(null);

			dao.create(mc);
			
			MatriculaImplantada matImplantada = new MatriculaImplantada(mc);
			dao.create(matImplantada);
			
		}

		
		
		/**
		 * Removendo as que foram selecionadas para remoção
		 */
		MatriculaComponenteDao daoMatricula = getDAO(MatriculaComponenteDao.class, mov);
		if( isNotEmpty( mov.getMatriculasParaRemover() ) ){
			for( MatriculaComponente mc : mov.getMatriculasParaRemover() ){
				MatriculaComponenteHelper.alterarSituacaoMatricula(mc, SituacaoMatricula.EXCLUIDA, mov, daoMatricula);
			}
		}
		
		
		/**
		 * Persistindo as alterações das que já haviam sido implantadas e foram alteradas
		 */
		if( isNotEmpty( mov.getMatriculasParaAlterar() ) ){
			for( MatriculaComponente mc : mov.getMatriculasParaAlterar() ){
				if( mc.isSelected() ){ //se foi alterada
					mc.setNotas(null);
					mc.setRecuperacao(null);					
					dao.update(mc);
				}
			}
		}
		
		//TODO atualizar ultimaAtualizacaoTotais para nulo
		//TODO zerar integrações?
		
		return mov.getMatriculas();
	}

	public void validate(Movimento movimento) throws NegocioException, ArqException {
		ImplantarHistoricoMov mov = (ImplantarHistoricoMov) movimento;
		ListaMensagens erros = new ListaMensagens();
		MatriculaComponenteHelper.validarMatriculaComponente(mov.getDiscente(), mov.getMatriculas(), null, erros);
		checkValidation(erros);
	}

}
