

<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/sigaaFunctions" prefix="sf"%>
<%@ taglib uri="/tags/ufrn" prefix="ufrn"%>
<%@ taglib uri="/tags/ajax" prefix="ajax"%>

<%@taglib prefix="f" uri="http://java.sun.com/jsf/core"%>
<%@taglib prefix="h" uri="http://java.sun.com/jsf/html"%>
<%@taglib prefix="t" uri="http://myfaces.apache.org/tomahawk"%>

<%@taglib uri="/tags/rich" prefix="rich"%>
<%@taglib uri="/tags/a4j" prefix="a4j"%>

<%@taglib uri="/tags/jawr" prefix="jwr"%>   
	
<%@ taglib uri="/tags/primefaces-p" prefix="p"%>


<div id="divConfiguracoesTelaCatalogacao" style="width: 40%; height: 35px; margin-left: 60%; text-align: center; padding-bottom: 5px;">
	<table style="width: 100%; text-align: center; border: 1px solid #A6C9E2;">
		<caption style="background-color: #C4D2EB; font-weight: bold; height: 15px;"> Opções </caption>
		<tr>
			<td style="width: 55%;">
				<a4j:commandLink id="cmdLinkAlterarEntreTelas" value="Alterar para Catalogação #{catalogacaoMBean.usarTelaCatalogacaoCompleta ? 'Simplificada': 'Completa' }" 
							actionListener="#{catalogacaoMBean.alterarEntreTelasCatalogacao}" 
							reRender=" divConfiguracoesTelaCatalogacao, cmdLinkAlterarEntreTelas, divDadoCatalogacao, pnlOperacoes"
							oncomplete="#{catalogacaoMBean.exibirPainelLateral ?  'resize_AllTextArea(false);' : 'resize_AllTextArea(true);'}">
					<h:graphicImage value="/img/substituir.png" style="overflow: visible;" title="Alterar Entre Telas" /> 
				</a4j:commandLink>
			</td>
			<td style="width: 35%;">
				<a4j:commandLink value="Configurações" onclick="modelPanelConfiguracoesTela.show();" oncomplete="#{catalogacaoMBean.exibirPainelLateral ?  'resize_AllTextArea(false);' : 'resize_AllTextArea(true);'}"> 
					<h:graphicImage value="/img/configuracoes.png" style="overflow: visible;" /> 
				</a4j:commandLink>
			</td>
		</tr>
	</table>
</div>