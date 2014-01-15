package br.ufrn.sigaa.mobile.resources;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import br.ufrn.arq.dao.DAOFactory;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.mobile.utils.JSONProcessor;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.ava.dao.MaterialTurmaDao;
import br.ufrn.sigaa.ava.dao.TopicoAulaDao;
import br.ufrn.sigaa.ava.dao.TurmaVirtualDao;
import br.ufrn.sigaa.ava.dominio.AbstractMaterialTurma;
import br.ufrn.sigaa.ava.dominio.AcaoAva;
import br.ufrn.sigaa.ava.dominio.ArquivoTurma;
import br.ufrn.sigaa.ava.dominio.EntidadeRegistroAva;
import br.ufrn.sigaa.ava.dominio.IndicacaoReferencia;
import br.ufrn.sigaa.ava.dominio.TopicoAula;
import br.ufrn.sigaa.ava.dominio.VideoTurma;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.mobile.dto.MaterialDTO;
import br.ufrn.sigaa.mobile.dto.TopicoAulaDTO;
import br.ufrn.sigaa.mobile.dto.tipos.TipoMaterial;

/**
 * Resource para operações com tópico de aula das turmas
 * 
 * @author bernardo
 * 
 */
@Path("/topicoaula")
public class TopicoAulaResource extends SigaaGenericResource {

	public TopicoAulaResource() {
	}


	/**
	 * Retorna todos os tópicos de aula relacionados a turma.
	 * 
	 * @param idTurma
	 * @return
	 * 
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAllTopicosAula(@QueryParam("ownerId") final Integer idTurma) throws ArqException, NegocioException {
		Response response = null;
		Collection<TopicoAulaDTO> listaTopicos = new ArrayList<TopicoAulaDTO>();
		
		request.getSession().setAttribute("tid", idTurma);
		
		TopicoAulaDao taDao = DAOFactory.getInstance().getDAO(TopicoAulaDao.class);
		MaterialTurmaDao mDao = DAOFactory.getInstance().getDAO(MaterialTurmaDao.class);
		TurmaVirtualDao tvDao = DAOFactory.getInstance().getDAO(TurmaVirtualDao.class);
		
		try {
			Turma turma = new Turma(idTurma);
			
			registrarAcao(turma, null, EntidadeRegistroAva.TOPICO_AULA, AcaoAva.LISTAR, 0);
			
			List<TopicoAula> listaTopicosVisiveis = taDao.findByTurma(turma);
			
			//Ordenando materiais buscados
			Map<TopicoAula, List<AbstractMaterialTurma>> materiaisBuscados = tvDao.findMateriaisByTurma(turma);				
			Map<TopicoAula, List<AbstractMaterialTurma>> materiais = mDao.findOrdemMaterialTurma(turma, materiaisBuscados);
			
			for (TopicoAula topico : listaTopicosVisiveis) {
				TopicoAulaDTO dto = new TopicoAulaDTO();
				
				if (topico.isVisivel()){
					
					dto.setId(topico.getId());
					dto.setTitulo(topico.getDescricao());
					String conteudo = topico.getConteudo();
					//<!--[if gte mso 9]> <![endif]-->
					dto.setConteudo(StringUtils.stripHtmlTags(
							StringUtils.toAsciiHtml(
									StringUtils.removerComentarios(conteudo != null ? conteudo : ""))));
					Date data = new Date();
					data.setTime(topico.getData().getTime());
					dto.setData(data);
					Date dataFim = new Date();
					dataFim.setTime(topico.getFim().getTime());
					dto.setFim(dataFim);
					dto.setIdTurma(turma.getId());
					List<AbstractMaterialTurma> list = materiais != null ? materiais.get(topico) : null;
					List<MaterialDTO> l = new ArrayList<MaterialDTO>();
					if (list != null && !list.isEmpty()) {
						for (AbstractMaterialTurma m : list) {
							
							
							MaterialDTO material = new MaterialDTO();
							material.setId(m.getId());
							
							String ico = m.getIcone().substring(m.getIcone().lastIndexOf("/")+1, m.getIcone().length());
							material.setIcone(ico);
							
							if (m.isTipoArquivo()) {
								material.setIdArquivo(((ArquivoTurma) m).getArquivo().getIdArquivo());
								material.setChave(UFRNUtils.generateArquivoKey(material.getIdArquivo()));
								material.setTipo(TipoMaterial.ARQUIVO);
								material.setNomeArquivo(((ArquivoTurma) m).getArquivo().getNome());
								material.setExtensao(((ArquivoTurma) m).getArquivo().getExtensao());
							} else if (m.isTipoRotulo()) {
								material.setIdArquivo(m.getMaterial().getId());
								material.setTipo(TipoMaterial.ROTULO);
								material.setIcone("");
							} else if (m.isTipoIndicacao()) {
								material.setSubtipo(((IndicacaoReferencia) m).getTipo());
								material.setDetalhes(((IndicacaoReferencia) m).getDetalhes());
								material.setUrl(((IndicacaoReferencia) m).getUrl());
								material.setTipo(TipoMaterial.INDICACAO);
							} else if (m.isTipoConteudo()) {
								material.setTipo(TipoMaterial.CONTEUDO);
							} else if (m.isTipoVideo()) {
								if(((VideoTurma) m).getLink() != null){
									material.setUrl(((VideoTurma) m).getLink());
								}
								material.setTipo(TipoMaterial.VIDEO);
							} else if (m.isTipoEnquete()) {
								material.setTipo(TipoMaterial.ENQUETE);
							} else if (m.isTipoForum()) {
								material.setTipo(TipoMaterial.FORUM);
							} else if (m.isTipoChat()) {
								material.setTipo(TipoMaterial.CHAT);
							} else if (m.isTipoTarefa()) {
								material.setTipo(TipoMaterial.TAREFA);
							} else if (m.isTipoQuestionario()) {
								material.setTipo(TipoMaterial.QUESTIONARIO);
							} else {
								material.setId(m.getMaterial().getId());
							}
							
							material.setNome(m.getNome());
							l.add(material);
						}
					}
					dto.setMateriais(l);
					listaTopicos.add(dto);
					
				}
			}
			response = Response.ok(JSONProcessor.toJSON(listaTopicos)).build();
		} catch (ArqException e) {
			if(!e.isNotificavel())
				response = montarResponseException(e);
			else
				throw e;
		} catch (NegocioException e) {
			if(!e.isNotificavel())
				response = montarResponseException(e);
			else
				throw e;
		}  finally {
			if (taDao != null)
				taDao.close();
			if (mDao != null)
				mDao.close();
			if (tvDao != null)
				tvDao.close();
		}

		return response;
	}

}
