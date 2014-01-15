/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Created on 16/03/2012
 * 
 */
package br.ufrn.sigaa.biblioteca.processos_tecnicos.negocio;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dao.InventarioAcervoBibliotecaDao;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.InventarioAcervoBiblioteca;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.InventarioMaterialRegistrado;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.MaterialInformacional;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.ResultadoRegistraMateriaisInventario;
import br.ufrn.sigaa.dominio.Usuario;

/**
 *   Processador com a l�gica de neg�cio para registrar materiais em um invent�rio e remover esses registros..
 *
 * @author felipe
 * @since 16/03/2012
 * @version 1.0 criacao da classe
 *
 */
public class ProcessadorRegistraMateriaisInventario extends AbstractProcessador {
	
	
	/**
	 * 
	 * Ver coment�rios da classe pai.<br/>
	 *
	 * @see br.ufrn.arq.negocio.ProcessadorComando#execute(br.ufrn.arq.dominio.Movimento)
	 */
	public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {
		
		long tempo = System.currentTimeMillis();
		
		try{
			validate(mov);
			
			MovimentoRegistraMateriaisInventario movimento = (MovimentoRegistraMateriaisInventario) mov;
			
			if(mov.getCodMovimento() == SigaaListaComando.REGISTRAR_MATERIAIS_INVENTARIO){
				return registrar(movimento);
			}
			
			if(mov.getCodMovimento() == SigaaListaComando.REMOVE_REGISTRO_MATERIAIS_INVENTARIO){
				return removerRegistros(movimento);
			}
		}finally{
			if(mov.getCodMovimento() == SigaaListaComando.REGISTRAR_MATERIAIS_INVENTARIO){
				System.out.println(" <<<<<<<<<<<<<<< Registrar materiais invent�rio demorou   " + (System.currentTimeMillis()-tempo ) + " ms   >>>>>>>>>>>>>>> ");
			}
		}
		return null;
	}

	
	/** Realiza um novo registro no invent�rio */
	private Object registrar(MovimentoRegistraMateriaisInventario movimento) throws DAOException {
		
		GenericDAO dao = null;
		
		try {
			dao = getGenericDAO(movimento);
			

			Usuario usuarioRegistro = (Usuario) movimento.getUsuarioLogado();
			InventarioAcervoBiblioteca inventario = movimento.getInventario();
			List<MaterialInformacional> materiais = movimento.getMaterialList();
			ResultadoRegistraMateriaisInventario resultado = movimento.getResultado();
			
			Date dataRegistro = new Date();
			InventarioMaterialRegistrado itemInventario = null;
			
			for (MaterialInformacional material : materiais) {				
				itemInventario = new InventarioMaterialRegistrado();

				itemInventario.setAtivo(true);
				itemInventario.setDataRegistro(dataRegistro);
				itemInventario.setInventario(inventario);
				itemInventario.setMaterial(material);
				itemInventario.setUsuarioRegistro(usuarioRegistro);
				
				dao.create(itemInventario);	
				
				resultado.addSucesso(material.getCodigoBarras());
			}
			
			return resultado;
		} finally {
			if(dao != null) dao.close();
		}
	}


	
	/** Remove os registros existentes do invent�rio */
	private Object removerRegistros(MovimentoRegistraMateriaisInventario movimento) throws DAOException {
		
		InventarioAcervoBibliotecaDao dao = null;
		
		try {
			dao = getDAO(InventarioAcervoBibliotecaDao.class, movimento);
		
			List<MaterialInformacional> materiais = movimento.getMaterialList();
			InventarioAcervoBiblioteca inventario =  movimento.getInventario();
			
			List<String> codigosDeBarrasRegistrosRemovidos  = new ArrayList<String>();
			
			for (MaterialInformacional material : materiais) {
				boolean registroRemovido = dao.removeRegistroMaterialInventario(material.getId(), inventario.getId());
				
				if(registroRemovido)
					codigosDeBarrasRegistrosRemovidos.add(material.getCodigoBarras());
			}
			
			return codigosDeBarrasRegistrosRemovidos;
			
		} finally {
			if(dao != null) dao.close();
		}
	}




	




	/**
	 * 
	 * Ver coment�rios da classe pai.<br/>
	 *
	 * @see br.ufrn.arq.negocio.ProcessadorComando#validate(br.ufrn.arq.dominio.Movimento)
	 */
	public void validate(Movimento mov) throws NegocioException, ArqException {
		
		MovimentoRegistraMateriaisInventario movimento = (MovimentoRegistraMateriaisInventario) mov;
		
		if(mov.getCodMovimento() == SigaaListaComando.REGISTRAR_MATERIAIS_INVENTARIO){
			validarRegistro(movimento);
		}
		
		if(mov.getCodMovimento() == SigaaListaComando.REMOVE_REGISTRO_MATERIAIS_INVENTARIO){
			validarRemocao(movimento);
		}
		
	}


	/** Cont�m as valida��es para o registro no invent�rio 
	 * @throws DAOException */
	private void validarRegistro(MovimentoRegistraMateriaisInventario movimento) throws DAOException {
		
		InventarioAcervoBibliotecaDao dao = null;
		
		try {
			dao = getDAO(InventarioAcervoBibliotecaDao.class, movimento);

			Usuario usuarioRegistro = (Usuario) movimento.getUsuarioLogado();
			
			InventarioAcervoBiblioteca inventario = dao.findInformacoesInventarioParaValidacaoRegistro(movimento.getInventario().getId());
			
			List<String> codigoBarrasList = movimento.getCodigoBarrasList();
			List<MaterialInformacional> materiaisRetorno = movimento.getMaterialList();
			
			ResultadoRegistraMateriaisInventario resultado = movimento.getResultado();
			
			if(! usuarioRegistro.isUserInRole( SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL)) {
				try {
					checkRole(inventario.getBiblioteca().getUnidade(), movimento, 
							SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO, 
							SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO, 
							SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_GERENCIAR_MATERIAIS, 
							SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO, 
							SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO_BIBLIOTECARIO,
							SigaaPapeis.BIBLIOTECA_SETOR_INFO_E_REF, 
							SigaaPapeis.BIBLIOTECA_SETOR_INFO_E_REF_BIBLIOTECARIO,
							SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_LOCAL);
				} catch (SegurancaException se) {
					resultado.addErro("", "O usu�rio(a) sem permiss�o para registrar materiais desse invent�rio");	// Mensagens curtas porque isso � exibido em uma tela pequena do coletor
				}
			}
			
			// Recupera todos de uma �nica vez para otimizar //
			List<MaterialInformacional> materiais = dao.findInformacoesMateriaisValidacaoRegistroInventario(codigoBarrasList);
		
			for (MaterialInformacional material : materiais) {
				
				if (material != null) {
					
					if (material.getBiblioteca().equals(inventario.getBiblioteca())) {
						if (inventario.getColecao() == null || material.getColecao().equals(inventario.getColecao())) {
							
							// materia com a biblioteca  e cole��es iguais a do invent�rio, n�o faz nada
							
						} else {
							resultado.addErro(material.getCodigoBarras(), "Material '" + material.getCodigoBarras() + "' n�o pertence � cole��o do invent�rio."); // Mensagens curtas porque isso � exibido em uma tela pequena do coletor
							continue;
						}
					} else {
						resultado.addErro(material.getCodigoBarras(), "Material '" + material.getCodigoBarras() + "' n�o pertence � biblioteca do invent�rio."); // Mensagens curtas porque isso � exibido em uma tela pequena do coletor
						continue;
					}
					
					boolean jaExiste = dao.existeMaterialInventario(material.getCodigoBarras(), inventario.getId());
					
					if (jaExiste) {
						resultado.addErro(material.getCodigoBarras(), "Material '" + material.getCodigoBarras() + "' j� registrado."); // Mensagens curtas porque isso � exibido em uma tela pequena do coletor
						
						continue;
					}
				}
				
				materiaisRetorno.add(material);
			}
			
			// Verifica quais c�digos de barras digitados par registro n�o existem no acervo //
			
			List<String> codigoBarrasEncontados = new ArrayList<String>();
			
			for (MaterialInformacional materiaisBanco : materiais) {
				codigoBarrasEncontados.add(materiaisBanco.getCodigoBarras());
			}
			
			for (String codigosBarrasEnviado : codigoBarrasList) {
				if ( !codigoBarrasEncontados.contains(codigosBarrasEnviado) ){
					resultado.addErro(codigosBarrasEnviado, "Material  '" + codigosBarrasEnviado + "' n�o existe."); // Mensagens curtas porque isso � exibido em uma tela pequena do coletor
				}
			}
			
			
		} finally {			
			if(dao != null) dao.close();
		}
		
	}

	
	/** Cont�m as valida��es para a remo��o dos registros realizados no invent�rio. 
	 * @throws NegocioException */
	private void validarRemocao(MovimentoRegistraMateriaisInventario movimento) throws NegocioException {
		
		InventarioAcervoBiblioteca inventario =  movimento.getInventario();
		
		if(!inventario.isAberto()){
			throw new NegocioException("O registro realizados para o invent�rio n�o podem ser alterado porque o invent�rio est� fechado.");
		}
		
		if(!  movimento.getUsuarioLogado().isUserInRole( SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL)){
				
			try{
				checkRole(inventario.getBiblioteca().getUnidade(), movimento, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_LOCAL);
			}catch (SegurancaException se) {
				throw new NegocioException("O usu�rio(a): "+ movimento.getUsuarioLogado().getNome()
							+ " n�o tem permiss�o para remover registros de invent�rios da "+inventario.getBiblioteca().getDescricao()+".");
			}
		
		}
	}







}
