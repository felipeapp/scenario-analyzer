/*
 * ProcessadorRemoverMateriaisAcervo.java
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 18/02/2010
 * Autor: jadson
 */
package br.ufrn.sigaa.biblioteca.processos_tecnicos.negocio;

import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.dao.biblioteca.ExemplarDao;
import br.ufrn.sigaa.arq.dao.biblioteca.FasciculoDao;
import br.ufrn.sigaa.arq.dao.biblioteca.MaterialInformacionalDao;
import br.ufrn.sigaa.arq.dao.biblioteca.SituacaoMaterialInformacionalDao;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.SituacaoMaterialInformacional;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Exemplar;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Fasciculo;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.MateriaisMarcadosParaGerarEtiquetas;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.MaterialInformacional;
import br.ufrn.sigaa.biblioteca.util.BibliotecaUtil;

/**
 *  <p>Processador que contém as regras de negócio para remover os materiais informacionais do acervo.</p>
 *  <p><strong>A remoção de materiais do acervo não gera baixa do patrimônio no sipac, pois geralmente são materiais 
 *  que não existem e foram incluídos no acervo por engano. <br/>
 *  Como os materiais são apenas desativados, os seus códigos de barras são alterado para permitir ao usuário incluir 
 *  um novo material com o mesmo código de barras.</strong></p>
 *	<p>Esse processador é chamado tanto na remoção individual do material, quanto no caso de uso de remoção em massa
 *dos materiais.</p>
 *
 * @author jadson
 *
 */
public class ProcessadorRemoverMateriaisAcervo extends AbstractProcessador{

	/**
	 * 
	 * Ver comentários da classe pai.<br/>
	 *
	 * @see br.ufrn.arq.negocio.ProcessadorComando#execute(br.ufrn.arq.dominio.Movimento)
	 */
	public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {
		
		validate(mov);
		
		GenericDAO dao = null;
		MovimentoRemoverMateriaisAcervo movimento = (MovimentoRemoverMateriaisAcervo) mov;
		
		try{
		
			dao = getGenericDAO(movimento);
		
			for (MaterialInformacional  material : movimento.getMateriais()) {
				
				MaterialInformacional materialBanco = dao.findByPrimaryKey(material.getId(), MaterialInformacional.class);                                                                      //
				material.getSituacao().setId(materialBanco.getSituacao().getId());
				dao.detach(materialBanco);
				
				
				material.setAtivo(false);
				material.setCodigoBarras(material.getCodigoBarras()+"_"+new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()));
				
				dao.update(material);
				
				
				
				//////////////////  Apaga as pendências que ficam quando os materiais são removidos ////////////////
				
				int idTituloMaterial = 0;
				Collection<MateriaisMarcadosParaGerarEtiquetas> materiaisMarcados = new ArrayList<MateriaisMarcadosParaGerarEtiquetas>();
				
				if(material instanceof Exemplar){
					idTituloMaterial = ((Exemplar) material).getTituloCatalografico().getId();
					materiaisMarcados = dao.findByExactField(MateriaisMarcadosParaGerarEtiquetas.class, "idExemplar", material.getId());
				}
					
				if(material instanceof Fasciculo){
					idTituloMaterial = ((Fasciculo) material).getAssinatura().getTituloCatalografico().getId();
					materiaisMarcados = dao.findByExactField(MateriaisMarcadosParaGerarEtiquetas.class, "idFasciculo", material.getId());
				}
				
				BibliotecaUtil.decrementaQuantidadesMateriaisTitulo(dao, idTituloMaterial);
			
				for (MateriaisMarcadosParaGerarEtiquetas materialMarcado : materiaisMarcados) {
					dao.remove(materialMarcado);
				}
				
				////////////////////////////////////////////////////////////////////////////////////////////////////
				
			}
		
		}finally{
			if(dao != null) dao.close();
		}
		
		return null;
	}

	
	
	/**
	 * 
	 * Ver comentários da classe pai.<br/>
	 *
	 * @see br.ufrn.arq.negocio.ProcessadorComando#validate(br.ufrn.arq.dominio.Movimento)
	 */
	public void validate(Movimento mov) throws NegocioException, ArqException {
	
		ListaMensagens lista = new ListaMensagens();
		
		MovimentoRemoverMateriaisAcervo movimento = (MovimentoRemoverMateriaisAcervo) mov;
		
		MaterialInformacionalDao dao = null;
		ExemplarDao exeplarDao = null;
		FasciculoDao fasciculoDao = null;
		SituacaoMaterialInformacionalDao situacaoDao = null;
		
		try{
			
			dao = getDAO(MaterialInformacionalDao.class, movimento);
			exeplarDao = getDAO(ExemplarDao.class, mov);
			fasciculoDao = getDAO(FasciculoDao.class, mov);
			situacaoDao = getDAO(SituacaoMaterialInformacionalDao.class, mov);
			
			SituacaoMaterialInformacional situacaoEmprestado = dao.findByExactField(
					SituacaoMaterialInformacional.class, "situacaoEmprestado", true, true);
			
			// Valida as informações de cada material passado
			for (MaterialInformacional  material : movimento.getMateriais()) {
				
				if(material instanceof Exemplar){
					if( dao.materialEstaPendenteDeTransferenciaEntreBibliotecas(material.getId()))
						lista.addErro("O Exemplar não pode ser removido, pois ele está pendente de transferênica.");
					
					if(material.getSituacao().isSituacaoEmprestado())
						lista.addErro("O Exemplar não pode ser removido, pois ele está emprestado.");
				}
				
				if(material instanceof Fasciculo){
					
					if( dao.materialEstaPendenteDeTransferenciaEntreBibliotecas(material.getId()))
						lista.addErro("O Fascículo não pode ser removido, pois ele está pendente de transferênica.");
					
					if(material.getSituacao().isSituacaoEmprestado())
						lista.addErro("O Fascículo não pode ser removido, pois ele está emprestado.");
					
				}
				
				
				if(! movimento.getUsuarioLogado().isUserInRole( SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL)){
					
					try{
						checkRole(material.getBiblioteca().getUnidade() , movimento, SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_GERENCIAR_MATERIAIS);
					}catch (SegurancaException se) {
						lista.addErro("O usuário(a): "+ mov.getUsuarioLogado().getNome()
									+ " não tem permissão para alterar o material da biblioteca. "+material.getBiblioteca().getDescricao());
					}
				
				}
				
				SituacaoMaterialInformacional situacaoNoBanco = situacaoDao.findSituacaoAtualMaterial(material.getId());
				
				if(situacaoNoBanco.getId() == situacaoEmprestado.getId() ){
					lista.addErro(" Exemplar não pode ser removido, pois ele está "+situacaoEmprestado.getDescricao());
				}
				
				
				dao.detach(situacaoNoBanco);
				
				
                // VERIFICAÇÃO DE MATERIAL POSSUI ANEXOS OU SUPLEMENTOS PARA PODER REMOVER //				
				if(material instanceof Exemplar){
					Long qtdAnexos = exeplarDao.countAnexosAtivosDoExemplar(material.getId());
					
					if(qtdAnexos > 0)
						lista.addErro("O exemplar não pode ser removido, pois ele possui anexos.");
				}else
					if(material instanceof Fasciculo){
						Long qtdSuplementos = fasciculoDao.countSuplementosAtivosDoFasciculo(material.getId());
						
						if(qtdSuplementos > 0)
							lista.addErro("O Fascículo não pode ser removido, pois ele possui suplementos.");
					}
			}
			
		}finally{
			checkValidation(lista);
			if(dao != null) dao.close();
			if(situacaoDao != null) situacaoDao.close();
			if(exeplarDao != null) exeplarDao.close();
			if(fasciculoDao != null) fasciculoDao.close();
			
		}
		
	}

}
