/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas
 * Superintendência de Informática - UFRN
 *
 */
package br.ufrn.sigaa.ensino.negocio;

import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Date;

import br.ufrn.academico.dominio.TipoUnidadeAcademica;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.ensino.SecretariaUnidadeDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ensino.dominio.SecretariaUnidade;
import br.ufrn.sigaa.ensino.negocio.dominio.SecretariaUnidadeMov;

/**
 * Processador que realiza identificação e substituição de secretários de
 * departamento e de programas de pós-graduação
 *
 * @author leonardo
 *
 */
public class ProcessadorSecretariaUnidade extends AbstractProcessador {

	/* (non-Javadoc)
	 * @see br.ufrn.arq.ejb.ProcessadorComando#execute(br.ufrn.arq.dominio.Movimento)
	 */
	public Object execute(Movimento mov) throws NegocioException, ArqException,
			RemoteException {

		validate(mov);

		GenericDAO dao = getGenericDAO(mov);
		SecretariaUnidadeMov secMov = (SecretariaUnidadeMov) mov;
		SecretariaUnidade secretario = secMov.getSecretario();
		SecretariaUnidade secretarioAntigo = secMov.getSecretarioAntigo();

		RegistroEntrada registro = mov.getUsuarioLogado().getRegistroEntrada();

		try {
			if(secMov.getCodMovimento() == SigaaListaComando.IDENTIFICAR_SECRETARIO){
				
				secretario.setInicio(new Date());
				secretario.setUsuarioAtribuidor(registro);
				
				dao.create(secretario);
				
			}else if(secMov.getCodMovimento() == SigaaListaComando.SUBSTITUIR_SECRETARIO){
				
				Date dataFim = secretarioAntigo.getFim();
				secretarioAntigo = dao.findByPrimaryKey(secretarioAntigo.getId(), SecretariaUnidade.class);
				
				secretarioAntigo.setFim(dataFim);
				secretarioAntigo.setUsuarioFinalizador(registro);
				
				secretario.setInicio(new Date());
				secretario.setUsuarioAtribuidor(registro);
				
				dao.update(secretarioAntigo);
				dao.create(secretario);
				
			} else if(secMov.getCodMovimento() == SigaaListaComando.CANCELAR_SECRETARIO){
				
				dao.updateField(SecretariaUnidade.class, secretario.getId(), "ativo", false);
				
			}
		} finally {
			dao.close();
		}
		return mov;
	}

	/* (non-Javadoc)
	 * @see br.ufrn.arq.ejb.ProcessadorComando#validate(br.ufrn.arq.dominio.Movimento)
	 */
	public void validate(Movimento mov) throws NegocioException, ArqException {

		SecretariaUnidadeMov secMov = (SecretariaUnidadeMov) mov;

		if(secMov.getCodMovimento() == SigaaListaComando.CANCELAR_SECRETARIO)
			return;

		ListaMensagens erros = new ListaMensagens();

		erros.addAll(secMov.getSecretario().validate());
		checkValidation(erros);

		SecretariaUnidadeDao dao = getDAO(SecretariaUnidadeDao.class, mov);
		
		try {
			if (secMov.getSecretario().getUnidade() != null) {
				
				if( secMov.getCodMovimento().getId() == SigaaListaComando.IDENTIFICAR_SECRETARIO.getId() &&
						!ValidatorUtil.isEmpty(secMov.getSecretario().getUnidade()) &&
						!ValidatorUtil.isEmpty(secMov.getSecretario().getUsuario())
						) {					
					
					Collection<SecretariaUnidade> secretarias = dao.findByUnidade(secMov.getSecretario().getUnidade().getId(), secMov.getSecretario().getUsuario());
					
					if( !ValidatorUtil.isEmpty(secretarias) ) {
						erros.addErro("Este usuário já está vinculado a esta unidade.");
					}
				
				}
				
				
				// só valida pra secretaria de departamento
				int tipoUnidade = TipoUnidadeAcademica.DEPARTAMENTO;
				String tipo = "departamento";
				if( secMov.getSecretario().isCentro() ){
					tipoUnidade = TipoUnidadeAcademica.CENTRO;
					tipo = "centro";
				}
				else if(secMov.getSecretario().getTipo() == TipoUnidadeAcademica.UNID_ACADEMICA_ESPECIALIZADA){
					tipoUnidade = TipoUnidadeAcademica.UNID_ACADEMICA_ESPECIALIZADA;
					tipo = "unidade acadêmica especializada";
					if(secMov.getSecretario().getServidor().getUnidade() != null &&
							secMov.getSecretario().getUnidade().getId() != secMov.getSecretario().getServidor().getUnidade().getId()){
						throw new NegocioException("Não foi possível atribuir secretaria a esse usuário.<br>" +
								"Ele está vinculado a(o) " + secMov.getSecretario().getServidor().getUnidade().getNome());
					}
				}
				
				Collection<SecretariaUnidade> secs = dao.findByUsuarioTipoAcademico(secMov.getSecretario().getUsuario().getId(),
						tipoUnidade);
				if (secs != null && !secs.isEmpty()) {
					throw new NegocioException("Não foi possível atribuir secretaria de " + tipo + " a esse usuário.<br>" +
							"Ele já está vinculado ao " + secs.iterator().next().getUnidade().getNome());
				}
			} else {
				Collection<SecretariaUnidade> secs = dao.findByUsuarioTipoAcademico(secMov.getSecretario().getUsuario().getId(),
						TipoUnidadeAcademica.COORDENACAO_CURSO);
				if (secs != null && !secs.isEmpty()) {
					for (SecretariaUnidade sec : secs) {
						if (sec.getCurso().getId() == secMov.getSecretario().getCurso().getId()) {
							throw new NegocioException("Não foi possível atribuir secretaria de coordenação a esse usuário.<br>" +
							"Ele já possui um vínculo de secretaria em aberto com esse curso");
						}
					}
				}
			}
			
			checkValidation(erros);
		} finally {
			dao.close();
		}
	}
}
