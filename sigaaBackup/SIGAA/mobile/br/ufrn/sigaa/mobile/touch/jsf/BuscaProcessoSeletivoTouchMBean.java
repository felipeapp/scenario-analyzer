package br.ufrn.sigaa.mobile.touch.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.ensino.ProcessoSeletivoDao;
import br.ufrn.sigaa.ensino.dominio.ProcessoSeletivo;
import br.ufrn.sigaa.ensino.dominio.StatusProcessoSeletivo;
import br.ufrn.sigaa.jsf.ProcessoSeletivoMBean;
import br.ufrn.sigaa.mobile.commons.SigaaTouchAbstractController;

@Component("buscaProcessoSeletivoTouch") @Scope("request")
public class BuscaProcessoSeletivoTouchMBean extends SigaaTouchAbstractController<ProcessoSeletivo> {
	
	private Collection<ProcessoSeletivo> processos;
	
	private char nivel;
	
	public BuscaProcessoSeletivoTouchMBean(){
		init();
	}
	
	private void init() {
		obj = new ProcessoSeletivo();
	}

	public String iniciarBusca() {
		return forward("/mobile/touch/public/nivel_processo_seletivo.jsf");
	}
	
	public String selecionaNivelBusca() throws DAOException {
		nivel = getParameterChar("nivelProcesso");
		
		ProcessoSeletivoDao processoDao = getDAO(ProcessoSeletivoDao.class);
		
		try {
			if (nivel == NivelEnsino.STRICTO)
				processos = processoDao.findAllVisiveis(nivel, ProcessoSeletivoMBean.NUMERO_DIAS_PASSADOS_PUBLICACAO, StatusProcessoSeletivo.PUBLICADO);
			else
				processos = processoDao.findAllVisiveis(nivel, ProcessoSeletivoMBean.NUMERO_DIAS_PASSADOS_PUBLICACAO, null);
			
			//Ordena os processos seletivos na ordem dos abertos com a data de inscrição mais próxima 
			if(isEmpty(processos)) {
				addMensagemErro("Nenhum Processo Seletivo encontrado para o nível escolhido.");
				return null;
			}
			else {
				Collections.sort((List<ProcessoSeletivo>) processos, new Comparator<ProcessoSeletivo>(){
					public int compare(ProcessoSeletivo p1, ProcessoSeletivo p2) {
						if (p1.isInscricoesAbertas() && !p2.isInscricoesAbertas())
							return -1;
						else if (!p1.isInscricoesAbertas() && p2.isInscricoesAbertas())
							return 1;
						else
							return p2.getEditalProcessoSeletivo().getInicioInscricoes().
									compareTo(p1.getEditalProcessoSeletivo().getInicioInscricoes());
					}
				});
			}
		} finally {
			processoDao.close();
		}

		return forwardListaProcessos();
	}

	public String forwardListaProcessos() {
		return forward("/mobile/touch/public/lista_processos_seletivos.jsf");
	}
	
	public String view() throws ArqException {
		setId();
		
		if (obj.getId() > 0) {
			obj = getGenericDAO().findByPrimaryKey(obj.getId(),ProcessoSeletivo.class);
		} else {
			addMensagemErro("Projeto de Pesquisa não selecionado");
			return null;
		}
		
		return forward("/mobile/touch/public/view_processo_seletivo.jsf");
	}

	public Collection<ProcessoSeletivo> getProcessos() {
		return processos;
	}

	public void setProcessos(Collection<ProcessoSeletivo> processos) {
		this.processos = processos;
	}

	public char getNivel() {
		return nivel;
	}

	public void setNivel(char nivel) {
		this.nivel = nivel;
	}

}
