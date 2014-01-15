/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 17/05/2007
 *
 */
package br.ufrn.sigaa.arq.dominio;

import br.ufrn.sigaa.parametros.dominio.ParametrosExtensao;
import br.ufrn.sigaa.parametros.dominio.ParametrosGerais;
import br.ufrn.sigaa.parametros.dominio.ParametrosGraduacao;
import br.ufrn.sigaa.parametros.dominio.ParametrosLatoSensu;
import br.ufrn.sigaa.parametros.dominio.ParametrosMonitoria;
import br.ufrn.sigaa.parametros.dominio.ParametrosPesquisa;
import br.ufrn.sigaa.parametros.dominio.ParametrosPortalDocente;
import br.ufrn.sigaa.parametros.dominio.ParametrosPortalPublico;
import br.ufrn.sigaa.parametros.dominio.ParametrosProdocente;
import br.ufrn.sigaa.parametros.dominio.ParametrosSAE;
import br.ufrn.sigaa.parametros.dominio.ParametrosStrictoSensu;
import br.ufrn.sigaa.parametros.dominio.ParametrosVestibular;

/**
 * Classe que mant�m um conjunto de constantes de par�metros do sistema, 
 * os c�digos devem estar sincronizados com os valores da tabela public.parametros
 */
public interface ConstantesParametro extends ParametrosExtensao, ParametrosGerais,
	ParametrosGraduacao, ParametrosLatoSensu, ParametrosMonitoria, ParametrosPesquisa,
	ParametrosProdocente, ParametrosStrictoSensu, ParametrosSAE, ParametrosPortalDocente, ParametrosVestibular, ParametrosPortalPublico {

}
