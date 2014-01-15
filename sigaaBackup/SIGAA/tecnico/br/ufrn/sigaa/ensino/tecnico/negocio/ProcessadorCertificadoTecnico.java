/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 14/09/2006
 *
 */
package br.ufrn.sigaa.ensino.tecnico.negocio;

import java.rmi.RemoteException;
import java.util.Collection;
import java.util.HashSet;

import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ensino.tecnico.dominio.Modulo;

/**
 * Processador responsável por validar o Certificado Técnico.
 * @author Leonardo
 *
 */
public class ProcessadorCertificadoTecnico extends AbstractProcessador {
	
	/** constantes usadas para mudar o tipoSituacaoMatricula */
	public final static int EMESPERA = 1;
	public final static int MATRICULADO = 2;
	public final static int CANCELADO = 3;
	public final static int CONCLUIDO = 4;
	public final static int TRANCADO = 5;
	
	/** constantes usadas para mudar o tipoResultado */
	public final static int APROVADO = 1;
	public final static int REPROVADO_POR_NOTA = 2;
	public final static int REPROVADO_POR_FALTA= 3;
	
	/**
	 * Método responsável pela execução do processador do Certificado Técnico.
	 */
	public Object execute(Movimento mov) throws NegocioException,
			ArqException, RemoteException {
		validate(mov);
		Object obj = null;
		if( mov.getCodMovimento() == SigaaListaComando.VALIDAR_CERTIFICADO )
			obj = validarCertificado(mov);
		return obj;
	}
	
	/**
	 * Responsável por validar o Certificado Técnico.
	 * @param mov
	 * @return
	 * @throws NegocioException
	 * @throws DAOException
	 */
	private Object validarCertificado(Movimento mov) throws NegocioException, DAOException {
		Collection<Modulo> colecaoModulosCertificados = new HashSet<Modulo>();
		/*
		 MovimentoCertificadoTecnico movc = (MovimentoCertificadoTecnico) mov;
		DiscenteTecnico td = movc.getTecDiscente();
		
		Collection<Disciplina> disciplinasJaPagas = (Collection<Disciplina>) new HashSet();
		
		for( Modulo tm: td.getEstruturaCurricularTecnica().getTecModulos() ){
			if( !tm.getDisciplinas().isEmpty() ){
				for( Disciplina disciplina: tm.getDisciplinas() ){ //para cada disciplina do modulo
					for( MatriculaDisciplina tmd: td.getMatriculasDisciplinas() ){
						if( disciplina.equals(tmd.getTurma().getDisciplina()) ) //se o discente ja pagou essa disciplina
							if( tmd.getSituacaoMatricula().getIdSituacaoMatricula() == CONCLUIDO && tmd.getTipoResultado().getIdTipoResultado() == APROVADO ) //e foi aprovado nela
								disciplinasJaPagas.add(disciplina); //adiciona disciplina na colecao
					}
				}
				if( disciplinasJaPagas.equals(tm.getDisciplinas()) ) //se as 2 colecoes forem iguais
					colecaoModulosCertificados.add(tm); //o discente tem direito ao certificado naquele modulo
			}
		}
		if( colecaoModulosCertificados.isEmpty() )
			throw new NegocioException("O Discente "+td.getPessoa().getNome()+" nÃ£o possui Certificados");
		*/
		return colecaoModulosCertificados;
	}

	public void validate(Movimento mov) throws NegocioException, ArqException {
		checkRole(SigaaPapeis.ADMINISTRADOR_SIGAA, mov);

	}

}
