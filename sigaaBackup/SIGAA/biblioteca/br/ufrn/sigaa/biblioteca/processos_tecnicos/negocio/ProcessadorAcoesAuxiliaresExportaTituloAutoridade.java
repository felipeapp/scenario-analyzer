/*
 * ProcessadorAcoesAuxiliaresExportaTituloAutoridade.java
 *
 * UNIVERSIDADE FEDERAL DO RIO GRANDE DO NORTE
 * Superintendência de Informática
 * Diretoria de Sistemas
 * Campos Universitário Lagoa Nova
 * Natal - RN - Brasil
 * 
 */
package br.ufrn.sigaa.biblioteca.processos_tecnicos.negocio;

import java.rmi.RemoteException;
import java.util.List;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.ArquivoDeCargaNumeroControleFGV;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.CacheEntidadesMarc;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.EntidadesMarcadasParaExportacao;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.RegistroExportacaoCooperacaoTecnica;

/**
 *
 *     Quando um título ou uma autoridade é exportada existem algumas tarefas que devem ser feitas,
 * como registar que eles foram exportados, para puxar relatórios depois. Para tornar essa tarefas transacional
 * foi criado esse processador 
 *
 * @author jadson
 * @since 08/06/2009
 * @version 1.0 criacao da classe
 *
 */
public class ProcessadorAcoesAuxiliaresExportaTituloAutoridade extends AbstractProcessador{

	public Object execute(Movimento mov) throws NegocioException, ArqException,
			RemoteException {

		GenericDAO dao = null;
		
		MovimentoAcoesAuxiliaresExportaTituloAutoridade movimento 
			= (MovimentoAcoesAuxiliaresExportaTituloAutoridade) mov;
		
		try{
		
			dao = getGenericDAO(movimento);			
			
			// Registra que as entidades foram exportadas para depois gerar relatórios
			for (RegistroExportacaoCooperacaoTecnica registro : movimento.getListaRegistros()) {				
				registro.setRegistroCriacao(mov.getUsuarioLogado().getRegistroEntrada());
				
				dao.create(registro);
			}
			
			// Limpa a lista de entidades salvas pelo sistema para não serem exportadoas + 1 vez
			
			if(movimento.isExportacaoTitulos()){
				for (CacheEntidadesMarc tituloGravado : movimento.getEntidadesExportadas()) {
					
					tituloGravado = dao.refresh(tituloGravado);
					
					List<EntidadesMarcadasParaExportacao> lista = (List<EntidadesMarcadasParaExportacao>) 
						dao.findAllProjection(EntidadesMarcadasParaExportacao.class, new String[] { "id", "idTituloCatalografico" });
					
					for (EntidadesMarcadasParaExportacao entidade : lista) {
						
						if(entidade.getIdTituloCatalografico()!= null && entidade.getIdTituloCatalografico().equals( tituloGravado.getIdTituloCatalografico())){
							dao.remove(entidade);
							break;
						}
						
					}
					
				}
			}
			
			
			if(! movimento.isExportacaoTitulos()){
				for (CacheEntidadesMarc autoridade : movimento.getEntidadesExportadas()) {
					
					autoridade = dao.refresh(autoridade);
					
					List<EntidadesMarcadasParaExportacao> lista = (List<EntidadesMarcadasParaExportacao>) 
						dao.findAllProjection(EntidadesMarcadasParaExportacao.class, new String[] { "id", "idAutoridade" });
					
					for (EntidadesMarcadasParaExportacao entidade : lista) {
						
						if(entidade.getIdAutoridade() != null && entidade.getIdAutoridade().equals( autoridade.getIdAutoridade())){
							dao.remove(entidade);
							break;
						}
						
					}
					
				}
			}
			
			///////  ATUALIZA AS INFORMAÇÕES SOBRE OS NÚMEROS DE CONTROLE USADO PARA EXPORTAR PARA FGV /////////
			for (ArquivoDeCargaNumeroControleFGV arquivo : movimento.getArquivosCarregados()) {
				dao.update(arquivo);
			}
			
			
		}finally{
			if(dao != null) dao.close();
		}
		
		return null;
	}

	public void validate(Movimento mov) throws NegocioException, ArqException {
		// não tem validação
		
	}

}
