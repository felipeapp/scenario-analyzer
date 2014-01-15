/*
 * ProcessadorEstornaEmprestimo.java
 *
 * UNIVERSIDADE FEDERAL DO RIO GRANDE DO NORTE
 * Superintendencia de Informatica
 * Diretoria de Sistemas
 * Campos Universitario Lagoa Nova
 * Natal - RN - Brasil
 *
 * Este software eh confidencial e de propriedade intelectual da
 * UFRN - Universidade Federal do Rio Grande no Norte
 * Nao se deve utilizar este produto em desacordo com as normas
 * da referida instituicao.
 */
package br.ufrn.sigaa.biblioteca.circulacao.negocio;

import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.ProcessadorCadastro;
import br.ufrn.sigaa.arq.dao.biblioteca.EmprestimoDao;
import br.ufrn.sigaa.arq.dao.biblioteca.MaterialInformacionalDao;
import br.ufrn.sigaa.arq.dao.biblioteca.ReservaMaterialBibliotecaDao;
import br.ufrn.sigaa.arq.dao.biblioteca.SuspensaoUsuarioBibliotecaDao;
import br.ufrn.sigaa.arq.dao.biblioteca.UsuarioBibliotecaDao;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.Emprestimo;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.SituacaoMaterialInformacional;
import br.ufrn.sigaa.biblioteca.dominio.EnvioEmailBiblioteca;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.MaterialInformacional;
import br.ufrn.sigaa.biblioteca.util.BibliotecaUtil;
import br.ufrn.sigaa.biblioteca.util.ReservaMaterialBibliotecaUtil;
import br.ufrn.sigaa.dominio.Usuario;

/**
 *
 * <p>Classe que cont�m as regras de neg�cio para estornar um empr�stimo.</p>
 * 
 * <p>Estonar um empr�stimo significa colocar a sua situa��o como <code>Emprestimo.CANCELADO</code>, colocar a data do estorno 
 * e desativa-lo para n�o aparecer nos hist�rios dos usu�rios. </p>
 * <p>O  material que estava emprestado volta para a situa��o de  "dispon�vel"</p>
 *
 * @author jadson
 * @since 17/10/2008
 * @version 1.0 cria��o da classe
 *
 */
public class ProcessadorEstornaEmprestimo  extends ProcessadorCadastro{

	
	/**
	 * Ver coment�rio na classe pai.
	 * 
	 * @see br.ufrn.arq.negocio.ProcessadorCadastro#execute(br.ufrn.arq.dominio.Movimento)
	 */
	@Override
	public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {

		SuspensaoUsuarioBibliotecaDao dao = null;
		MaterialInformacionalDao mDao = null;
		UsuarioBibliotecaDao daoUsuario = null;
		EmprestimoDao daoEmprestimo = null;
		ReservaMaterialBibliotecaDao daoReserva = null;
		
		try {
			
			dao = getDAO(SuspensaoUsuarioBibliotecaDao.class, mov);
			mDao = getDAO(MaterialInformacionalDao.class, mov);
			daoUsuario = getDAO(UsuarioBibliotecaDao.class, mov);
			daoEmprestimo = getDAO(EmprestimoDao.class, mov);
			daoReserva = getDAO(ReservaMaterialBibliotecaDao.class, mov);
			
			MovimentoEstornaEmprestimo personalMov = (MovimentoEstornaEmprestimo) mov;
	
			Emprestimo emp =  dao.findByPrimaryKey(personalMov.getIdEmprestimo(), Emprestimo.class); 
	
			if (emp == null)
				throw new DAOException("Erro ao obter o empr�stimo do material!");
	
			if (! emp.getMaterial().getSituacao().isSituacaoEmprestado()){
				throw new NegocioException ("Ocorreu uma inconsist�ncia no sistema: O material com o c�digo de barras "
						+ emp.getMaterial().getCodigoBarras()+" n�o est� emprestado, logo ele n�o deveria pertencer a esse empr�stimo");
			}
			
			if( daoEmprestimo.isMaterialComComunicacaoPerdaAtiva(emp.getMaterial().getId()) ){
				throw new NegocioException ("O empr�stimo do material de c�digo de barras: "+ emp.getMaterial().getCodigoBarras() +" n�o pode ser desfeito, pois existe uma comunica��o de perda do material no sistema.");
			}
			
			MaterialInformacional material = emp.getMaterial();
			
			
			// Se permitir estornar vai bagun�ar a fila de espera //
			if( ReservaMaterialBibliotecaUtil.isSistemaTrabalhaComReservas() ) { // para n�o ter consultas extras ao banco se o sistema n�o utilizar reserva.
				
				int idTitulo = emp.getMaterial().getTituloCatalografico().getId();
				
				if (daoReserva.countReservasAtivasDoTitulo(idTitulo) > 0)
					throw new NegocioException ("Imposs�vel estornar o empr�stimo, pois o T�tulo possui reservas ativas.");
			}
			
			
			
			SituacaoMaterialInformacional situacaoDisponivel = dao.findByExactField(SituacaoMaterialInformacional.class, "situacaoDisponivel", true, true);
			
			// Atualiza a situa��o do material para dispon�vel //
			List<Integer> idMaterial = new ArrayList<Integer>();
			idMaterial.add( material.getId());
			mDao.atualizaSituacaoDeMateriais(idMaterial, situacaoDisponivel.getId());
			
			
			
			// Estorna o empr�stimo
			Date dataEstorno = new Date();
			dao.updateFields(Emprestimo.class, emp.getId(), new String []{"dataEstorno", "usuarioRealizouEstorno", "situacao", "ativo"}, new Object [] {dataEstorno, (Usuario) mov.getUsuarioLogado(), Emprestimo.CANCELADO, false});
	
			enviaEmailAvisoEstorno(daoUsuario, emp, dataEstorno);
			
			
			return null;
			
		} finally {
			if (dao != null) dao.close();
			if (mDao != null) mDao.close();
			if (daoUsuario != null) daoUsuario.close();
			if (daoEmprestimo != null) daoEmprestimo.close();
			if (daoReserva != null) daoReserva.close();
			
		}
	}


	
	
	/**
	 * Ver coment�rio na classe pai.
	 * 
	 * @see br.ufrn.arq.negocio.ProcessadorCadastro#validate(Movimento)
	 */
	@Override
	public void validate(Movimento mov) throws NegocioException, ArqException {
		super.validate(mov);
	}




	/**
	 *   Forma os dados para enviar o email de aviso de estorno.
	 *
	 * @param daoUsuario
	 * @param emp
	 * @param dataEstorno
	 * @param usuarioLocado
	 * @throws DAOException
	 */
	private void enviaEmailAvisoEstorno(UsuarioBibliotecaDao daoUsuario, Emprestimo emp, Date dataEstorno) throws DAOException{
		
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		Object[] informacoesUsuario =  daoUsuario.findNomeEmailUsuarioBiblioteca(emp.getUsuarioBiblioteca());
		
		List<String> listaInformacoesEmail = new ArrayList<String>();
		listaInformacoesEmail.add( " <strong> Data do Empr�stimo: </strong>"+ format.format( emp.getDataEmprestimo()) + " <strong>Material:</strong> " + BibliotecaUtil.obtemDadosMaterialInformacional(emp.getMaterial().getId() ));
		
		String codigoAutenticacao =  BibliotecaUtil.geraNumeroAutenticacaoComprovantes(emp.getId(), dataEstorno);
		
		String assunto = " Confirma��o de Estorno de Empr�stimo ";
		String titulo = " Empr�stimo Estornado ";
		String mensagemUsuario = " O empr�stimo abaixo foi estornado com sucesso: ";
		
		new EnvioEmailBiblioteca().enviaEmail( (String)informacoesUsuario[0], (String)informacoesUsuario[1], assunto, titulo
				, EnvioEmailBiblioteca.AVISO_EMPRESTIMO, mensagemUsuario, null, null, null, listaInformacoesEmail
				, null, null,  codigoAutenticacao, null);
	}
	
}