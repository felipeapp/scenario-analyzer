 /*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Created on 18/01/2008
 *
 */
package br.ufrn.sigaa.ava.negocio;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.ava.dao.TopicoAulaDao;
import br.ufrn.sigaa.ava.dao.TurmaVirtualDao;
import br.ufrn.sigaa.ava.dominio.AbstractMaterialTurma;
import br.ufrn.sigaa.ava.dominio.ArquivoTurma;
import br.ufrn.sigaa.ava.dominio.ConteudoTurma;
import br.ufrn.sigaa.ava.dominio.ImportacaoDadosTurmasAnteriores;
import br.ufrn.sigaa.ava.dominio.IndicacaoReferencia;
import br.ufrn.sigaa.ava.dominio.TopicoAula;
import br.ufrn.sigaa.ava.dominio.VideoTurma;
import br.ufrn.sigaa.ava.validacao.Specification;
import br.ufrn.sigaa.ensino.dominio.PlanoCurso;

/**
 * Processador para importa��o de dados de uma turma virtual para outra.
 * 
 * @author David Pereira
 *
 */
public class ProcessadorImportacaoDadosTurmas extends AbstractProcessador {

	public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {
		MovimentoCadastroAva cMov = (MovimentoCadastroAva) mov;
		ImportacaoDadosTurmasAnteriores importacao = (ImportacaoDadosTurmasAnteriores) cMov.getObjMovimentado();
		
		TopicoAulaDao dao = null;
		TurmaVirtualDao turmaVirtualDao = null;
		
		try {
			
			dao = getDAO(TopicoAulaDao.class, mov);
			TopicoAula copiaTopicoAula = null; 
			// Caso esteja importanto materiais sem t�pico, cadastra os materiais no primeiro t�pico
			// da turma para onde os dados est�o sendo importados.
			TopicoAula primeiroTopico =  dao.findPrimeiroTopicoTurma(importacao.getTurmaAtual().getId());
			List<TopicoAula> topicosAula = null;
			List <PersistDB> objetosASalvar = new ArrayList <PersistDB> ();
			
			// Se o material n�o possui t�pico de aula e est� utilizando o primeiro t�pico da turma
			boolean materialSemTopico = false;
			Specification specification = cMov.getSpecification();
			
			if (importacao.isTopicosDeAula()) {
				topicosAula =  cMov.getTopicosDeAula();
			}
			
			// Se o usu�rio selecionou importar t�picos de aulas
			if (importacao.isTopicosDeAula() && topicosAula != null) {
				for (TopicoAula topico : topicosAula) {
					
					if (topico.isSelecionado()){					
						copiaTopicoAula = (TopicoAula) UFRNUtils.deepCopy(topico);
							
						if (primeiroTopico == null)
							primeiroTopico = copiaTopicoAula;
						
						if (topicosAula != null && copiaTopicoAula.getTopicoPai() != null)	{
							
							int i = objetosASalvar.indexOf(topico.getTopicoPai());
							if ( i != -1 )
								copiaTopicoAula.setTopicoPai((TopicoAula)objetosASalvar.get(i));
							else
								copiaTopicoAula.setTopicoPai(null);
						}
						//copiaTopicoAula.setId(0);
						copiaTopicoAula.adicionarDocenteTurma(null);
						copiaTopicoAula.setTurma(importacao.getTurmaAtual());
												
						//dao.create(copiaTopicoAula);
						objetosASalvar.add(copiaTopicoAula);
					}
				}
			}
			
			// Se o usu�rio selecionou importar conte�dos
			if (importacao.isConteudos()) {
				
				turmaVirtualDao = getDAO(TurmaVirtualDao.class, mov);
				List<ConteudoTurma> listaConteudos = turmaVirtualDao.findConteudoTurma( importacao.getTurmaAnterior() );
				
				for (ConteudoTurma conteudo : listaConteudos) {
					
					ConteudoTurma copiaConteudo = (ConteudoTurma) UFRNUtils.deepCopy(conteudo);
					
					// Faz a c�pia apontar para a c�pia certa da aula.
					if (topicosAula != null)
					{	
						int i = objetosASalvar.indexOf(copiaConteudo.getAula());
						// O conte�do deve estar associado a um t�pico de aula. 
						if ( i != -1 )
							copiaConteudo.setAula((TopicoAula)objetosASalvar.get(i));
						else {
							copiaConteudo.setAula(primeiroTopico);
							materialSemTopico = true;
						}	
					}
					
					//dao.create(copiaConteudo);
					objetosASalvar.add(copiaConteudo);
				}
			}
			
			// Se o usu�rio selecionou importar arquivos
			if (importacao.isArquivos()) {
				
				turmaVirtualDao = getDAO(TurmaVirtualDao.class, mov);
				List<ArquivoTurma> listaArquivos = turmaVirtualDao.findArquivosByTurma(importacao.getTurmaAnterior().getId());
				
				for (ArquivoTurma arquivo : listaArquivos) {
					
					ArquivoTurma copiaArquivo = (ArquivoTurma) UFRNUtils.deepCopy(arquivo);
					
					// Faz a c�pia apontar para a c�pia certa da aula.
					if (topicosAula != null)
					{
						int i = objetosASalvar.indexOf(copiaArquivo.getAula());
						// O arquivo deve estar associado a um t�pico de aula. 
						if ( i != -1 )
							copiaArquivo.setAula((TopicoAula)objetosASalvar.get(i));
						else {
							copiaArquivo.setAula(primeiroTopico);
							materialSemTopico = true;
						}	
					}
						
					copiaArquivo.setArquivo( arquivo.getArquivo() );
					copiaArquivo.setDescricao( arquivo.getArquivo().getNome() );
					if (copiaTopicoAula != null)
						copiaArquivo.setUsuarioCadastro(copiaTopicoAula.getUsuario());

					//dao.create(copiaArquivo);
					objetosASalvar.add(copiaArquivo);
				}
			}
			
			// Se o usu�rio selecionou importar refer�ncias.
			if (importacao.isReferencias()) {
				
				turmaVirtualDao = getDAO(TurmaVirtualDao.class, mov);
				List<IndicacaoReferencia> listaReferencia = cMov.isParaPlanoCurso() ? cMov.getReferencias() : turmaVirtualDao.findReferenciasTurma(importacao.getTurmaAnterior());
				
				for (IndicacaoReferencia referencia : listaReferencia) {
					
					if (!cMov.isParaPlanoCurso() || referencia.isSelecionada()) {
						IndicacaoReferencia indicacaoReferencia = (IndicacaoReferencia) UFRNUtils.deepCopy(referencia);
						
						// Faz a c�pia apontar para a c�pia certa da aula.
						if (topicosAula != null)
						{	
							int i = objetosASalvar.indexOf(indicacaoReferencia.getAula());
							// O referencia deve estar associada a um t�pico de aula. 
							if ( i != -1 )
								indicacaoReferencia.setAula((TopicoAula)objetosASalvar.get(i));
							else
								indicacaoReferencia.setAula(null);
						} else
							indicacaoReferencia.setAula(null);
											
						indicacaoReferencia.setDescricao( referencia.getDescricao() );
						indicacaoReferencia.setDetalhes( referencia.getDetalhes() );
						indicacaoReferencia.setTipoIndicacao( referencia.getTipoIndicacao() );
						
						if (copiaTopicoAula != null){
							indicacaoReferencia.setUsuarioCadastro(copiaTopicoAula.getUsuario());
							indicacaoReferencia.setTurma(copiaTopicoAula.getTurma());
						} else {
							indicacaoReferencia.setTurma(importacao.getTurmaAtual());
							indicacaoReferencia.setUsuarioCadastro(importacao.getUsuario());
						}
						
						indicacaoReferencia.setUrl(referencia.getUrl());
						
						indicacaoReferencia.setAno(referencia.getAno());
						indicacaoReferencia.setAutor(referencia.getAutor());
						indicacaoReferencia.setEdicao(referencia.getEdicao());
						indicacaoReferencia.setEditora(referencia.getEditora());
						indicacaoReferencia.setTitulo(referencia.getTitulo());
						indicacaoReferencia.setTituloCatalografico(referencia.getTituloCatalografico());
						
						objetosASalvar.add(indicacaoReferencia);
					}
				}
				
			}
			
			// Se o usu�rio selecionou importar videos
			if (importacao.isVideos()) {
				
				turmaVirtualDao = getDAO(TurmaVirtualDao.class, mov);
				List<VideoTurma> listaVideos = turmaVirtualDao.findVideosTurma(importacao.getTurmaAnterior());
				
				for (VideoTurma video : listaVideos) {
					
					VideoTurma copiaVideo = (VideoTurma) UFRNUtils.deepCopy(video);
					
					// Faz a c�pia apontar para a c�pia certa da aula.
					if (topicosAula != null)
					{
						int i = objetosASalvar.indexOf(copiaVideo.getAula());
						// O arquivo deve estar associado a um t�pico de aula. 
						if ( i != -1 )
							copiaVideo.setTopicoAula((TopicoAula)objetosASalvar.get(i));
						else {
							copiaVideo.setTopicoAula(primeiroTopico);
							materialSemTopico = true;
						}	
					}
						
					if (copiaTopicoAula != null)
						copiaVideo.setUsuario(copiaTopicoAula.getUsuario());

					copiaVideo.setTurma(importacao.getTurmaAtual());
					//dao.create(copiaArquivo);
					objetosASalvar.add(copiaVideo);
				}
			}
			
			// Se o usu�rio selecionou importar plano de curso.
			if (importacao.isPlanoCurso()) {
				PlanoCurso plano = dao.findByExactField(PlanoCurso.class, "turma.id", importacao.getTurmaAnterior().getId(), true);
				
				if ( plano != null && !ValidatorUtil.isEmpty(plano.getMetodologia()) && !ValidatorUtil.isEmpty(plano.getProcedimentoAvalicao()) ){
					
					PlanoCurso planoAtual = dao.findByExactField(PlanoCurso.class, "turma.id", importacao.getTurmaAtual().getId(), true);
					
					if ( planoAtual == null ){
						planoAtual = (PlanoCurso) UFRNUtils.deepCopy(plano);
						planoAtual.setId(0);
						planoAtual.setFinalizado(false);
						planoAtual.setHorarioAtendimento(null);
						planoAtual.setTurma(importacao.getTurmaAtual());
					}
					planoAtual.setMetodologia(plano.getMetodologia());
					planoAtual.setProcedimentoAvalicao(plano.getProcedimentoAvalicao());

					objetosASalvar.add(planoAtual);
				}	
			}

			if (objetosASalvar.isEmpty())
				specification.getNotification().addError("A turma n�o possui os materiais selecionados para serem importados!");
			
			// Salva todos os objetos selecionados para importa��o.
			for (PersistDB o : objetosASalvar){
				if ( o instanceof PlanoCurso ) 
					dao.createOrUpdate(o);
				else{
					o.setId(0);
					TopicoAula topicoAula = null;
					if (o instanceof AbstractMaterialTurma) {
						MaterialTurmaHelper.instanciaMaterial((AbstractMaterialTurma) o);
						
						topicoAula = ((AbstractMaterialTurma) o).getAula();
						if ( topicoAula == null && !(o instanceof IndicacaoReferencia)){
							throw new NegocioException("S� � poss�vel importar um Arquivo, Conte�do ou V�deo, se a turma tiver pelo menos um t�pico de aula." +
									" Por favor, cadastre um t�pico de aula ou selecione pelo menos um t�pico para importa��o.");
						} else if ( materialSemTopico ){
							specification.getNotification().getMessages().addWarning("Como houve a importa��o de conte�dos e arquivos " +
									"que n�o estavam associados a t�picos de aula, o sistema associou automaticamente estes materiais ao primeiro t�pico " +
									"de aula da turma");
						}
						else if ( topicoAula != null )	
							topicoAula = dao.findByPrimaryKey(topicoAula.getId(), TopicoAula.class);
						
						MaterialTurmaHelper.definirNovoMaterialParaTopico((AbstractMaterialTurma) o, topicoAula, cMov.getTurma());
					}
					
					dao.create(o);
					
					if (o instanceof AbstractMaterialTurma) 
						MaterialTurmaHelper.atualizarMaterial(dao, (AbstractMaterialTurma) o, true);
				}	
					
			}
				
		
		}catch (ArqException e) {
			throw new ArqException(e);
		}finally
		{
			if (dao != null)
				dao.close();

			if (turmaVirtualDao != null)
				turmaVirtualDao.close();
		}
		
		
		return null;
	}

	public void validate(Movimento mov) throws NegocioException, ArqException {
		
	}

}