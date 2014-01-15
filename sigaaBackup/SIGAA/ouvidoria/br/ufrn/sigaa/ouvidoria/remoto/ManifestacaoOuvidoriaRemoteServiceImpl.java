package br.ufrn.sigaa.ouvidoria.remoto;

import static br.ufrn.arq.util.ValidatorUtil.isNotEmpty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.remoting.RemoteAccessException;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.integracao.dto.ManifestacaoDTO;
import br.ufrn.integracao.interfaces.ManifestacaoOuvidoriaRemoteService;
import br.ufrn.sigaa.ouvidoria.dao.HistoricoManifestacaoDao;
import br.ufrn.sigaa.ouvidoria.dominio.HistoricoManifestacao;

public class ManifestacaoOuvidoriaRemoteServiceImpl implements ManifestacaoOuvidoriaRemoteService {

	@Override
	public List<ManifestacaoDTO> getAllSemRespostaByUnidade(int idUnidade) {
		HistoricoManifestacaoDao dao = null;
		
		try {
			List<ManifestacaoDTO> resultado = new ArrayList<ManifestacaoDTO>();
			
			dao = new HistoricoManifestacaoDao();
			
			Collection<HistoricoManifestacao> historicos = dao.findHistoricosManifetacoesAtrasadasByUnidade(idUnidade);
			
			if(isNotEmpty(historicos)) {
				for (HistoricoManifestacao h : historicos) {
					ManifestacaoDTO dto = new ManifestacaoDTO();
					
					dto.setId(h.getManifestacao().getId());
					dto.setTitulo(h.getManifestacao().getTitulo());
					dto.setNumero(h.getManifestacao().getNumero());
					dto.setAno(h.getManifestacao().getAno());
					dto.setPrazo(h.getPrazoResposta());
					
					resultado.add(dto);
				}
			}
			
			return resultado;
		} 
		catch (DAOException e) {
			throw new RemoteAccessException(e.getMessage());
		} finally {
			if(dao != null)
				dao.close();
		}
	}

}
