/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Criado em 10/06/09
 */

package br.ufrn.sigaa.biblioteca.circulacao.negocio;

import java.rmi.RemoteException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.dao.biblioteca.SuspensaoUsuarioBibliotecaDao;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.Emprestimo;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.SuspensaoUsuarioBiblioteca;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.UsuarioBiblioteca;
import br.ufrn.sigaa.biblioteca.util.BibliotecaUtil;
import br.ufrn.sigaa.biblioteca.util.CirculacaoUtil;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.mensagens.MensagensBiblioteca;

/**
 * Processador que estorna suspens�es de usu�rios da biblioteca, adiantando suspens�es futuras a estas.
 * 
 * @author Fred_Castro
 *
 */
public class ProcessadorEstornarSuspensoes extends AbstractProcessador {

	/**
	 * De acordo com a suspens�o a estornar, adianta as datas das suspens�es futuras para o usu�rio desta
	 * suspens�o, diminuindo efetivamente o tempo que o usu�rio ficar� suspenso.
	 */
	public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {

		MovimentoEstornarSuspensoes personalMov = (MovimentoEstornarSuspensoes) mov;
		
		SuspensaoUsuarioBibliotecaDao dao = null;
		
		validate(personalMov);
		
		try {
			dao = getDAO (SuspensaoUsuarioBibliotecaDao.class, personalMov);
			
			// A data de fim da suspens�o que est� sendo estornada passar�
			// a ser o dia de ontem
			SuspensaoUsuarioBiblioteca auxS = new SuspensaoUsuarioBiblioteca();
			Calendar cal = Calendar.getInstance();
			cal.setTime(new Date());
			cal.add(Calendar.DAY_OF_MONTH, -1);
			auxS.setDataFim(cal.getTime());
			Emprestimo auxE = new Emprestimo ();
			
			SuspensaoUsuarioBiblioteca primeira = personalMov.getSuspensoesAEstornar().get(0);
			
			// Pega o usu�rio biblioteca, seja pelo empr�stimo associado ou diretamente, se for uma
			// suspens�o manual
			UsuarioBiblioteca usuarioBiblioteca = null;
			if (! primeira.isManual())
				usuarioBiblioteca = primeira.getEmprestimo().getUsuarioBiblioteca();
			else
				usuarioBiblioteca = primeira.getUsuarioBiblioteca();
			
			auxE.setUsuarioBiblioteca(usuarioBiblioteca);
			auxS.setEmprestimo(auxE);
			
			int diasAVoltar = 0;
			
			UsuarioBiblioteca usuarioBibliotecaCompleto = dao.findByPrimaryKey(usuarioBiblioteca.getId(), UsuarioBiblioteca.class, "pessoa.id", "biblioteca.id");
			
			// Retorna as supens�es futuras ordenadas pela data de fim //
			List <SuspensaoUsuarioBiblioteca> sFuturas = dao.findSuspensoesFuturasDoUsuario(
					usuarioBibliotecaCompleto.getIdentificadorPessoa()
					, usuarioBibliotecaCompleto.getIdentificadorBiblioteca()
					, auxS.getDataFim());
			
			for (SuspensaoUsuarioBiblioteca suspensaoFutura : sFuturas){
				
				if (personalMov.getSuspensoesAEstornar().contains(suspensaoFutura)){
					
					// Se a suspens�o vai ser estornada, estorna a suspens�o e incrementa a quantidade de dias que v�o ser retirados do per�odo de suspens�o do usu�rio
					
					suspensaoFutura.setAtivo(false);
					suspensaoFutura.setMotivoEstorno(personalMov.getMotivo());
					suspensaoFutura.setDataEstorno(new Date());
					suspensaoFutura.setUsuarioEstorno((Usuario) personalMov.getUsuarioLogado());
					
					diasAVoltar += CirculacaoUtil.calculaDiasEmAtrasoBiblioteca(suspensaoFutura.getDataInicio(), suspensaoFutura.getDataFim());
					
				} else {

					// Se a suspens�o n�o vai ser estornada, apenas volta ela a quantidade de dias das supens�es j� estornadas
					
					cal.setTime(suspensaoFutura.getDataInicio());
					cal.add(Calendar.DAY_OF_MONTH, -1*diasAVoltar);
					suspensaoFutura.setDataInicio(cal.getTime());
					
					cal.setTime(suspensaoFutura.getDataFim());
					cal.add(Calendar.DAY_OF_MONTH, -1*diasAVoltar);
					suspensaoFutura.setDataFim(cal.getTime());
				}
				
				dao.update(suspensaoFutura);
			}
			
		} finally {
			if (dao != null) dao.close();
		}
		
		return null;
	}

	public void validate(Movimento mov) throws NegocioException, ArqException {
		
		MovimentoEstornarSuspensoes personalMov = (MovimentoEstornarSuspensoes) mov;
		
		if (personalMov.getSuspensoesAEstornar() == null || personalMov.getSuspensoesAEstornar().isEmpty()){
			NegocioException ne = new NegocioException ();
			ne.getListaMensagens().addMensagem(MensagensBiblioteca.USUARIO_SEM_SUSPENSOES_ATIVAS);
			throw ne;
		}
		
		boolean selecionada = false;
		for (SuspensaoUsuarioBiblioteca s : personalMov.getSuspensoesAEstornar())
			if (s.isSelecionado()){
				selecionada = true;
				break;
			}
		
		if (!selecionada){
			NegocioException ne = new NegocioException();
			ne.getListaMensagens().addMensagem(MensagensBiblioteca.SELECIONE_UMA_SUSPENSAO);
			throw ne;
		}
		
		// Um bibliotec�rio s� pode estornar uma suspens�o da sua pr�pria biblioteca:
		// Obs.: suspens�es manuais n�o s�o ligadas a uma biblioteca
		for ( SuspensaoUsuarioBiblioteca s : personalMov.getSuspensoesAEstornar() )
			if ( s.isSelecionado() ) {
				SuspensaoUsuarioBibliotecaDao dao = null;
				try {
					dao = getDAO(SuspensaoUsuarioBibliotecaDao.class, personalMov);
					
					// Retorna o id da unidade do material do emprestimo da suspens�o para verificar a permiss�o do usu�rio
					Integer idUnidadeSuspensao = dao.findUnidadeDaSuspensao(s.getId() );
					
					if ( idUnidadeSuspensao == null ) // � multa manual
						continue;
					
					
					if(! personalMov.getUsuarioLogado().isUserInRole(SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL)){
					
						
						List<Integer> unidades = BibliotecaUtil.encontraUnidadesPermissaoDoUsuario(personalMov.getUsuarioLogado(),
								SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_SETOR_INFO_E_REF_BIBLIOTECARIO);
					
						if ( ! unidades.contains(idUnidadeSuspensao) ) {
							NegocioException ne = new NegocioException();
							ne.getListaMensagens().addErro("O senhor(a) n�o tem permiss�o para estornar suspens�es de empr�stimos " +
									"feitos em outras bibliotecas.");
							throw ne;
						}
					}
					
				} finally {
					if ( dao != null ) dao.close();
				}
			}
		
	}
}
