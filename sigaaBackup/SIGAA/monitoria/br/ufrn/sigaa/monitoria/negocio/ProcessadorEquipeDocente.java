package br.ufrn.sigaa.monitoria.negocio;

import java.rmi.RemoteException;
import java.util.Collection;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.parametrizacao.RepositorioDadosInstitucionais;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.monitoria.dominio.ComponenteCurricularMonitoria;
import br.ufrn.sigaa.monitoria.dominio.EquipeDocente;
import br.ufrn.sigaa.monitoria.dominio.EquipeDocenteComponente;
import br.ufrn.sigaa.monitoria.dominio.Orientacao;


/**
 * Processador responsável por cadastrar, alterar, finalizar e excluir
 * docentes de projetos de monitoria.
 * 
 * @author ilueny santos
 *
 */
public class ProcessadorEquipeDocente extends AbstractProcessador {

	public Object execute(Movimento edMov) throws NegocioException, ArqException, RemoteException {
		
		MovimentoCadastro mov = (MovimentoCadastro) edMov;		
		GenericDAO dao = getGenericDAO(mov);		
		validate(mov);
		try {
		    if( mov.getCodMovimento().equals(SigaaListaComando.CADASTRAR_EQUIPEDOCENTE)){ 
			
				EquipeDocente doc = (EquipeDocente) mov.getObjMovimentado();
				doc.setAtivo(true);
				doc.setExcluido(false);
				//criando novos e atualizando componentes curriculares
				for (ComponenteCurricularMonitoria comp : doc.getProjetoEnsino().getComponentesCurriculares()) {
					dao.update(comp);
					for (EquipeDocenteComponente edc : comp.getDocentesComponentes()) {
						if (edc.getId() != 0) {
							dao.update(edc);
						} else {						
							if ( edc.getEquipeDocente().getId() == 0 ) {
								dao.create(edc.getEquipeDocente());
							}
							dao.create(edc); 
							
							//criando orientações do novo docente
							for (Orientacao ori : edc.getEquipeDocente().getOrientacoes()){
								if (ori.getId() == 0) {
									dao.create(ori);
								}
							}						
						}
					}
				}
				if(doc.getId() > 0)
					dao.update(doc);
				
				return (doc);

			
		    } else if( mov.getCodMovimento().equals(SigaaListaComando.ALTERAR_EQUIPEDOCENTE)){			
				EquipeDocente eqDocente = (EquipeDocente) mov.getObjMovimentado();			
				dao.update(eqDocente);
				return (eqDocente);
			
		    } else if( mov.getCodMovimento().equals(SigaaListaComando.FINALIZAR_EQUIPEDOCENTE)){
				//finalizando o docente do projeto
				EquipeDocente eqDocente = (EquipeDocente) mov.getObjMovimentado();
				eqDocente.setAtivo(true);			
				dao.update(eqDocente);
				return (eqDocente);
			
		    } else if( mov.getCodMovimento().equals(SigaaListaComando.EXCLUIR_EQUIPEDOCENTE)){
				//estabelecendo situação excluído... geralmente por erro de migração			
				EquipeDocente eqDocente = (EquipeDocente) mov.getObjMovimentado();			
				eqDocente.setAtivo(false);
				eqDocente.setExcluido(true);
				eqDocente.setRegistroEntradaExclusao(mov.getUsuarioLogado().getRegistroEntrada());
				dao.updateField(EquipeDocente.class, eqDocente.getId(), "ativo", false);
				dao.updateField(EquipeDocente.class, eqDocente.getId(), "excluido", true);
				Collection<EquipeDocenteComponente> listaDocenteComponente = dao.findByExactField(EquipeDocenteComponente.class, "equipeDocente.id", eqDocente.getId());
			
				//inativando componentes vinculados ao docente.
				if( listaDocenteComponente != null) {
					
					for (EquipeDocenteComponente com : listaDocenteComponente) {
						dao.updateField(EquipeDocenteComponente.class, com.getId(), "ativo", false);
					}
				}
				
				if(eqDocente.getOrientacoesValidas() != null){
					for (Orientacao ori : eqDocente.getOrientacoesValidas()) {
						dao.updateField(Orientacao.class, ori.getId(), "ativo", false);
					}
				}
				
				return (eqDocente);
		    }
		
		    return null;
		    
		}finally {
		    dao.close();
		}
	}

	
	/**
	 * 
	 * 
	 */
	public void validate(Movimento mov) throws NegocioException, ArqException {
	    checkRole(SigaaPapeis.GESTOR_MONITORIA, mov);
	    EquipeDocente equipeDocente = (EquipeDocente) ((MovimentoCadastro)mov).getObjMovimentado();
	    ListaMensagens lista = new ListaMensagens();

	    if( mov.getCodMovimento().equals(SigaaListaComando.CADASTRAR_EQUIPEDOCENTE)){
		// docente deve ser do quadro permanente
		if ((equipeDocente.getServidor() != null) && (equipeDocente.getServidor().isSubstituto())) {
			
			lista.addErro("Somente docentes do quadro permanente da " + RepositorioDadosInstitucionais.getSiglaInstituicao() + " podem ser adicionados ao projeto.");
		}

		// em projetos de monitoria isolados o docente so pode estar em 2 projetos ativos ao mesmo tempo
		if (equipeDocente.getProjetoEnsino().isProjetoIsolado()) {
		    DiscenteMonitoriaValidator.validaMaximoProjetosDocente(equipeDocente, lista);
		}

		// Docente teve relatório de reprovado e não vai (Geladeira)
		// poder participar de novos projetos por 2 anos!
		ProjetoMonitoriaValidator.validaDocenteRelatorioProjetosAntigosNaoAprovados(equipeDocente, lista);

		// 'pendência de relatório!',.... docente não apresentou relatório do sid
		ProjetoMonitoriaValidator.validaDocenteResumoSidProjetosAnteriores(equipeDocente, lista);

	    } else if( mov.getCodMovimento().equals(SigaaListaComando.ALTERAR_EQUIPEDOCENTE)) {	

	    } else if( mov.getCodMovimento().equals(SigaaListaComando.FINALIZAR_EQUIPEDOCENTE)) {
		if (equipeDocente.getDataSaidaProjeto() == null) {
		    lista.addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Data de saída do projeto");
		}

	    } else if( mov.getCodMovimento().equals(SigaaListaComando.EXCLUIR_EQUIPEDOCENTE)) {
		
	    }
	    
	    checkValidation(lista);
	}
}