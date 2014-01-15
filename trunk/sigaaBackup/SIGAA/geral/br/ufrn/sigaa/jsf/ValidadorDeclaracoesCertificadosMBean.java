/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas - SIGAA
 * Superintendência de Informática - UFRN
 *
 * Created on '19/11/2008'
 *
 */
package br.ufrn.sigaa.jsf;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import br.ufrn.arq.seguranca.autenticacao.AutValidator;
import br.ufrn.arq.seguranca.autenticacao.EmissaoDocumentoAutenticado;
import br.ufrn.arq.seguranca.autenticacao.SubTipoDocumentoAutenticado;
import br.ufrn.arq.web.jsf.AbstractController;
import br.ufrn.sigaa.apedagogica.jsf.CertificadoParticipacaoAtividadeAPMBean;
import br.ufrn.sigaa.extensao.jsf.CertificadoAvaliadorExtensaoMBean;
import br.ufrn.sigaa.extensao.jsf.CertificadoExtensaoMBean;
import br.ufrn.sigaa.extensao.jsf.DeclaracaoExtensaoMBean;
import br.ufrn.sigaa.monitoria.jsf.CertificadoMonitorMBean;
import br.ufrn.sigaa.monitoria.jsf.DeclaracaoDiscenteMonitoriaMBean;
import br.ufrn.sigaa.monitoria.jsf.DeclaracaoDocenteMonitoriaMBean;
import br.ufrn.sigaa.pesquisa.jsf.AvaliadorCICMBean;
import br.ufrn.sigaa.pesquisa.jsf.CertificadoCICMBean;
import br.ufrn.sigaa.pesquisa.jsf.DeclaracaoGrupoPesquisaMBean;

/**
 * Validador geral de declarações e certificados que redireciona para os MBeans
 * de validação específicos de acordo com o tipo específico do documento a ser
 * validado.
 * 
 * @author leonardo
 * 
 */
public class ValidadorDeclaracoesCertificadosMBean extends AbstractController
		implements AutValidator {

	
	/**
	 * 
	 * Usado para verificar o subtipo do documento emitido e chamar o método exibir adequado, de acordo com o MBean que emitiu o documento,
	 * para exibição adequada documento.
	 * 
	 * Não é chamado por JSP's.
	 *    
	 * 
	 */
	
	public void exibir(EmissaoDocumentoAutenticado comprovante,
			HttpServletRequest req, HttpServletResponse res) {

		if (comprovante.getSubTipoDocumento().equals(
				SubTipoDocumentoAutenticado.CERTIFICADO_PARTICIPANTE_MEMBRO_EQUIPE_EXTENSAO)) {
			CertificadoExtensaoMBean bean = getMBean("certificadoExtensaoMBean");
			bean.exibir(comprovante, req, res);
		} else if (comprovante.getSubTipoDocumento().equals(
				SubTipoDocumentoAutenticado.DECLARACAO_PARTICIPANTE_MEMBRO_EQUIPE_EXTENSAO)) {
			DeclaracaoExtensaoMBean bean = getMBean("declaracaoExtensaoMBean");
			bean.exibir(comprovante, req, res);
		}else if (comprovante.getSubTipoDocumento().equals(
					SubTipoDocumentoAutenticado.CERTIFICADO_DISCENTE_EXTENSAO)) {
			CertificadoExtensaoMBean bean = getMBean("certificadoExtensaoMBean");
			bean.exibir(comprovante, req, res);
		} else if (comprovante.getSubTipoDocumento().equals(
				SubTipoDocumentoAutenticado.CERTIFICADO_PARTICIPANTE_CIC)) {
			CertificadoCICMBean bean = getMBean("certificadoCIC");
			bean.exibir(comprovante, req, res);
		} else if (comprovante.getSubTipoDocumento().equals(
				SubTipoDocumentoAutenticado.CERTIFICADO_PARTICIPANTE_AVALIADOR_CIC)) {
			AvaliadorCICMBean bean = getMBean("avaliadorCIC");
			bean.exibir(comprovante, req, res);
		} else if (comprovante.getSubTipoDocumento().equals(
			SubTipoDocumentoAutenticado.CERTIFICADO_PARTICIPANTE_PUBLICO_ALVO_EXTENSAO)) {
			CertificadoExtensaoMBean bean = getMBean("certificadoExtensaoMBean");
			bean.exibir(comprovante, req, res);
		} else if (comprovante.getSubTipoDocumento().equals(
			SubTipoDocumentoAutenticado.DECLARACAO_PARTICIPANTE_PUBLICO_ALVO_EXTENSAO)) {
		    	DeclaracaoExtensaoMBean bean = getMBean("declaracaoExtensaoMBean");
			bean.exibir(comprovante, req, res);
		} else if (comprovante.getSubTipoDocumento().equals(
				SubTipoDocumentoAutenticado.CERTIFICADO_AVALIADOR_EXTENSAO)) {
	    	CertificadoAvaliadorExtensaoMBean bean = getMBean("certificadoAvaliadorExtensao");
	    	bean.exibir(comprovante, req, res);		
		}else if (comprovante.getSubTipoDocumento().equals(
				SubTipoDocumentoAutenticado.CERTIFICADO_PARTICIPANTE_ATUALIZACAO_PEDAGOGICA)) {
			CertificadoParticipacaoAtividadeAPMBean bean = getMBean("certificadoParticipacaoAP");
	    	bean.exibir(comprovante, req, res);		
		}else if (comprovante.getSubTipoDocumento().equals(
				SubTipoDocumentoAutenticado.DECLARACAO_GRUPO_PESQUISA)) {
			DeclaracaoGrupoPesquisaMBean bean = getMBean("declaracaoGrupoPesquisaMBean");
			bean.exibir(comprovante, req, res);		
		}else if (comprovante.getSubTipoDocumento().equals(SubTipoDocumentoAutenticado.CERTIFICADO_PARTICIPANTE_MONITORIA)) {
			CertificadoMonitorMBean bean = getMBean("certificadoMonitor");
			bean.exibir(comprovante, req, res);	
		}else if (comprovante.getSubTipoDocumento().equals(SubTipoDocumentoAutenticado.DECLARACAO_DOCENTE_MONITORIA)) {
			DeclaracaoDocenteMonitoriaMBean bean = getMBean("declaracaoDocenteMonitoria");
			bean.exibir(comprovante, req, res);
		}else if (comprovante.getSubTipoDocumento().equals(SubTipoDocumentoAutenticado.DECLARACAO_DISCENTE_MONITORIA)) {
			DeclaracaoDiscenteMonitoriaMBean bean = getMBean("declaracaoDiscenteMonitoria");
			bean.exibir(comprovante, req, res);
		}
		
	}

	/**
	 * 
	 * Usado para verificar o subtipo do documento emitido e efetuar a validação adequadamente, chamando o método validaDigest
	 * do MBean responsável pela emissão do documento. 
	 * 
	 * Não é chamado por JSP's.
	 * 
	 * 
	 */
	public boolean validaDigest(EmissaoDocumentoAutenticado comprovante) {

		if (comprovante.getSubTipoDocumento().equals(
				SubTipoDocumentoAutenticado.CERTIFICADO_PARTICIPANTE_MEMBRO_EQUIPE_EXTENSAO)) {
			CertificadoExtensaoMBean bean = getMBean("certificadoExtensaoMBean");
			return bean.validaDigest(comprovante);
		} else if (comprovante.getSubTipoDocumento().equals(
				SubTipoDocumentoAutenticado.DECLARACAO_PARTICIPANTE_MEMBRO_EQUIPE_EXTENSAO)) {
			DeclaracaoExtensaoMBean bean = getMBean("declaracaoExtensaoMBean");
			return bean.validaDigest(comprovante);
		} else if (comprovante.getSubTipoDocumento().equals(
				SubTipoDocumentoAutenticado.CERTIFICADO_PARTICIPANTE_CIC)) {
			CertificadoCICMBean bean = getMBean("certificadoCIC");
			return bean.validaDigest(comprovante);
		} else if (comprovante.getSubTipoDocumento().equals(
				SubTipoDocumentoAutenticado.CERTIFICADO_PARTICIPANTE_AVALIADOR_CIC)) {
			AvaliadorCICMBean bean = getMBean("avaliadorCIC");
			return bean.validaDigest(comprovante);
		} else if (comprovante.getSubTipoDocumento().equals(
			SubTipoDocumentoAutenticado.CERTIFICADO_PARTICIPANTE_PUBLICO_ALVO_EXTENSAO)) {
			CertificadoExtensaoMBean bean = getMBean("certificadoExtensaoMBean");
			return bean.validaDigest(comprovante);
		} else if (comprovante.getSubTipoDocumento().equals(
			SubTipoDocumentoAutenticado.DECLARACAO_PARTICIPANTE_PUBLICO_ALVO_EXTENSAO)) {
		    	DeclaracaoExtensaoMBean bean = getMBean("declaracaoExtensaoMBean");
		    	return bean.validaDigest(comprovante);		    	
		} else if (comprovante.getSubTipoDocumento().equals(
				SubTipoDocumentoAutenticado.CERTIFICADO_AVALIADOR_EXTENSAO)) {
			CertificadoAvaliadorExtensaoMBean bean = getMBean("certificadoAvaliadorExtensao");
	    	return bean.validaDigest(comprovante);		
		} else if (comprovante.getSubTipoDocumento().equals(
			SubTipoDocumentoAutenticado.CERTIFICADO_DISCENTE_EXTENSAO)) {
			CertificadoExtensaoMBean bean = getMBean("certificadoExtensaoMBean");
			return bean.validaDigest(comprovante);
		} else if (comprovante.getSubTipoDocumento().equals(
				SubTipoDocumentoAutenticado.DECLARACAO_DOCENTE_MONITORIA)) {
				DeclaracaoDocenteMonitoriaMBean bean = getMBean("declaracaoDocenteMonitoria");
				return bean.validaDigest(comprovante);	
		} else if (comprovante.getSubTipoDocumento().equals(
				SubTipoDocumentoAutenticado.DECLARACAO_DISCENTE_MONITORIA)) {
				DeclaracaoDiscenteMonitoriaMBean bean = getMBean("declaracaoDiscenteMonitoria");
				return bean.validaDigest(comprovante);
		}else if (comprovante.getSubTipoDocumento().equals(
				SubTipoDocumentoAutenticado.CERTIFICADO_PARTICIPANTE_ATUALIZACAO_PEDAGOGICA)) {
	    		CertificadoParticipacaoAtividadeAPMBean bean = getMBean("certificadoParticipacaoAP");
	    		return bean.validaDigest(comprovante);		
		}else if (comprovante.getSubTipoDocumento().equals(
				SubTipoDocumentoAutenticado.DECLARACAO_GRUPO_PESQUISA)) {
			DeclaracaoGrupoPesquisaMBean bean = getMBean("declaracaoGrupoPesquisaMBean");
			return bean.validaDigest(comprovante);		
		}else if (comprovante.getSubTipoDocumento().equals(SubTipoDocumentoAutenticado.CERTIFICADO_PARTICIPANTE_MONITORIA)) {
			CertificadoMonitorMBean bean = getMBean("certificadoMonitor");
			return bean.validaDigest(comprovante);		
		}else if (comprovante.getSubTipoDocumento().equals(SubTipoDocumentoAutenticado.DECLARACAO_DOCENTE_MONITORIA)) {
			DeclaracaoDocenteMonitoriaMBean bean = getMBean("declaracaoDocenteMonitoria");
			return bean.validaDigest(comprovante);		
		}else if (comprovante.getSubTipoDocumento().equals(SubTipoDocumentoAutenticado.DECLARACAO_DISCENTE_MONITORIA)) {
			DeclaracaoDiscenteMonitoriaMBean bean = getMBean("declaracaoDiscenteMonitoria");
			return bean.validaDigest(comprovante);		
		}


		return false;
	}

}
