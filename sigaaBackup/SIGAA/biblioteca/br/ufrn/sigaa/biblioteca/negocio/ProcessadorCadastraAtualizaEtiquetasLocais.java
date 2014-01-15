/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on  29/04/2009
 */
package br.ufrn.sigaa.biblioteca.negocio;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.sigaa.arq.dao.biblioteca.EtiquetaDao;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.DescritorSubCampo;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Etiqueta;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.ValorIndicador;

/**
 *
 *  <p> Processador que cadastra ou atualiza etiquetas locais. As etiquetas locais são: </p>
 *  <p>Definidas no padrão MARC:<br/>
 *    09X -> números de chamada Locais<br/>
 *    59X -> Notas locais<br/>
 *    69X -> Campos de Assunto Locais<br/>
 * </p>
 *
 * <p>Obs. as etiquetas 9XX são consideradas locais também no sistema, pois o padrão MARC não mensiona 
 * nada sobre elas e a FGV usa as 997, 998 e 999 para intercâmbio de informações.</p>
 *  
 * 
 * <p><strong>Observação: </strong> São as únicas etiquetas que o usuário pode alterar.</p>
 *  
 * @author Jadson
 * @since 26/06/2009
 * @version 1.0 Criação da classe
 *
 */
public class ProcessadorCadastraAtualizaEtiquetasLocais extends AbstractProcessador{
	/**
	 * @see br.ufrn.arq.negocio.ProcessadorComando#execute(br.ufrn.arq.dominio.Movimento)
	 */
	public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {
		
		MovimentoCadastraAtualizaEtiquetasLocais movimento 
				= (MovimentoCadastraAtualizaEtiquetasLocais) mov;
		
		EtiquetaDao dao = null; 
		
		try{
			
			dao = getDAO(EtiquetaDao.class, mov);
			
			if(movimento.isRemovendo()){               // Não Valida as informações para remover a etiqueta.
				dao.update(movimento.getEtiqueta()); 
			}else{
			
				validate(mov);
		
				if(movimento.isAtualizando()){
					
					Etiqueta etiquetaBanco =  dao.findByPrimaryKey(movimento.getEtiqueta().getId(), Etiqueta.class);
					
					// Precisa remover na mão o descritor ou valor do indicador removido, porque o hibernate não está fazendo isso.
					
					List<DescritorSubCampo> descritoresRemovidos = new ArrayList<DescritorSubCampo>();
					List<ValorIndicador> valoresRemovidos = new ArrayList<ValorIndicador>();
					
					verificaValoresRemovidos(valoresRemovidos, movimento.getEtiqueta(), etiquetaBanco);	
					verificaDescritoresRemovidos(descritoresRemovidos, movimento.getEtiqueta(), etiquetaBanco);
					
					dao.detach(etiquetaBanco);
					
					for (DescritorSubCampo descritorRemovido : descritoresRemovidos) {
						dao.remove(descritorRemovido);
					}
					
					for (ValorIndicador valorRemovido : valoresRemovidos) {
						dao.remove(valorRemovido);
					}
					
					
					// IMPORTANTE: colocar os valores que estavam na Lista para o SET que vai ser persistido //
					movimento.getEtiqueta().iniciaDescritoresSubCampoPersistidos();
					movimento.getEtiqueta().iniciaValoresIndicadorPersistidos();
					
					
					dao.update(movimento.getEtiqueta());
					
					
				}else{
					
					// IMPORTANTE: colocar os valores que estavam na Lista para o SET que vai ser persistido //
					movimento.getEtiqueta().iniciaDescritoresSubCampoPersistidos();
					movimento.getEtiqueta().iniciaValoresIndicadorPersistidos();
					
					dao.create(movimento.getEtiqueta());
				}
				
			}
			
		}finally{
			if(dao != null) dao.close();
		}
		
		return null;
	}

	/**
	 * @see br.ufrn.arq.negocio.ProcessadorComando#validate(br.ufrn.arq.dominio.Movimento)
	 */
	public void validate(Movimento mov) throws NegocioException, ArqException {
		
		ListaMensagens lista = new ListaMensagens();
		
		MovimentoCadastraAtualizaEtiquetasLocais movimento 
			= (MovimentoCadastraAtualizaEtiquetasLocais) mov;
		
		EtiquetaDao dao = null;
		
		
		if(movimento.isCampoLocal()){ // Se só permite alterar campos locais 
			if( ! movimento.getEtiqueta().isEquetaLocal() ){
				lista.addErro("O campo '"+movimento.getEtiqueta().getTag()+"' não é um campo local MARC. Apenas campos locais podem ser criados/alterados no sistema.");
				checkValidation(lista);
			}
		}
		
		try{
			
			// Teste se o usuário digitou dois descritores de subcampo iguais
			List<Character> codigosSubCampos = new ArrayList<Character>();
			
			if( movimento.getEtiqueta().getDescritorSubCampoList() != null )
				for (DescritorSubCampo descritor : movimento.getEtiqueta().getDescritorSubCampoList()) {
					if( ! codigosSubCampos.contains(descritor.getCodigo())){
						codigosSubCampos.add(descritor.getCodigo());
					}else{
						lista.addErro("Subcampo '"+(descritor.getCodigo() != null ? descritor.getCodigo(): " ")+"' já informado.");
					}
				}
			
			// Teste se o usuário digitou dois valores de indicadores iguais.
			List<ValorIndicador> valoresTemp = new ArrayList<ValorIndicador>();
			
			if( movimento.getEtiqueta().getValoresIndicadorList() != null )
				for (ValorIndicador valor : movimento.getEtiqueta().getValoresIndicadorList() ) {
					
					lista.addAll( valor.validate() );
					
					if(valor.getValor() != null){
						if( ! valoresTemp.contains(valor)){
							valoresTemp.add(valor);
						}else{
							lista.addErro( ( valor.getNumeroIndicador() == ValorIndicador.PRIMEIRO ? " 1º Indicador " : " 2º Indicador ")
									+"'"+valor.getValor()+"'"+" já informado.");
						}
					}
				}
			
			if(! movimento.isAtualizando()){
				
				dao = getDAO(EtiquetaDao.class, movimento);
				Etiqueta e = dao.findEtiquetaPorTagETipoAtiva(movimento.getEtiqueta().getTag(), movimento.getEtiqueta().getTipo());
				if (e != null)
					lista.addErro("O campo \""+e.getTag()+"\" "+ ( e.isEtiquetaBibliografica() ? "Bibliográfico" : "de Autoridades")
							+" já está cadastrado no sistema com a descrição: "+e.getDescricao());
			}
			
			lista.addAll(  movimento.getEtiqueta().validate());
			
			if( movimento.getEtiqueta().getDescritorSubCampoList() != null )
				for (DescritorSubCampo descritor : movimento.getEtiqueta().getDescritorSubCampoList()) {
					lista.addAll( descritor.validate() );
					
					if(movimento.isCampoLocal()){
						lista.addAll( descritor.validateCampoLocal() ); 
					}
					
				}
			
			checkValidation(lista);
		
		}finally{
			if(dao != null) dao.close();
		}
		
	}

	
	/**
	 * Verifica quais descritores foram removidos pelo usuário.
	 */
	private void verificaDescritoresRemovidos(List<DescritorSubCampo> descritoresRemocao, Etiqueta etiqueta, Etiqueta etiquetaBanco) {
		
		for (DescritorSubCampo descritorBanco : etiquetaBanco.getDescritorSubCampo()) {
			
			List<DescritorSubCampo> descritoresMemoria = etiqueta.getDescritorSubCampoList();
			
			boolean contemDescritor = false;
			
			for (DescritorSubCampo descritorSubCampoMemoria : descritoresMemoria) {
				
				
				if(descritorSubCampoMemoria.getId() == descritorBanco.getId()){
					contemDescritor = true;
					break;
				}
			}
			
			if(! contemDescritor ){ // foi removido
				descritoresRemocao.add(descritorBanco);
			}
		}
	}

	/**
	 * Verifica quais valores foram removidos pelo usuário.
	 */
	private void verificaValoresRemovidos(List<ValorIndicador> valoresRemocao, Etiqueta etiqueta, Etiqueta etiquetaBanco) {
		
		for (ValorIndicador valorBanco : etiquetaBanco.getValoresIndicador()) {
			
			List<ValorIndicador> valoresMemoria = etiqueta.getValoresIndicadorList();
			
			boolean contemValor = false;
			
			for (ValorIndicador valorIndicadorMemoria : valoresMemoria) {
				
				if(valorIndicadorMemoria.getId() == valorBanco.getId()){
					contemValor = true;
					break;
				}
			}
			
			if(! contemValor ){ // foi removido
				valoresRemocao.add(valorBanco);
			}
		}
	}
	
}
