<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>
<%@page import="br.ufrn.sigaa.arq.util.CheckRoleUtil"%>
<%@page import="br.ufrn.arq.seguranca.SigaaPapeis"%>
<%@ taglib uri="/tags/a4j" prefix="a4j"%>


<f:view>
		<h2><ufrn:subSistema /> &gt; Hist�rico de Altera��es de um T�tulo</h2>
		<br>
		
		<a4j:keepAlive beanName="emiteRelatorioHistoricosMBean"></a4j:keepAlive>
		
		<%-- Pode ser chamad o a partir da pesquisa de T�tulo--%>
		<a4j:keepAlive beanName="pesquisaTituloCatalograficoMBean"></a4j:keepAlive>
		
		<a4j:region>
			<h:form id="form">

				<div class="descricaoOperacao" style="width: 85%">
					<p>Utilize este formul�rio gerar o relat�rio com o hist�rico de altera��es em um t�tulo.</p>
				</div>

				<div
					style="height: 20px; color: #FF0000; font-weight: bold; text-align: center;"><h:outputText
					id="mensagemErro"
					value="#{emiteRelatorioHistoricosMBean.mensagemErroAjax}" /></div>

				<table class="formulario" width="85%">
				
					<caption>Hist�rico de Altera��es de um T�tulo</caption>
				
					<tr style="height: 100px;">
						<td colspan="2" align="justify" >
							<h:outputText escape="false" value="#{emiteRelatorioHistoricosMBean.tituloCatalografico.formatoReferencia}" />
						</td>
					</tr>
					
					<tr>
						<th>Per�odo para a Consulta:</th>
						<td>
							de <t:inputCalendar id="Inicio" value="#{emiteRelatorioHistoricosMBean.dataInicio}" renderAsPopup="true" popupDateFormat="dd/MM/yyyy" onkeypress="return formataData(this,event)" renderPopupButtonAsImage="true" size="10" maxlength="10" />
							at� <t:inputCalendar id="Fim" value="#{emiteRelatorioHistoricosMBean.dataFim}" renderAsPopup="true" popupDateFormat="dd/MM/yyyy" onkeypress="return formataData(this,event)" renderPopupButtonAsImage="true" size="10" maxlength="10" />
						</td>
					</tr>

					<tfoot>
						<tr>
							<td colspan="2" align="center">
								<h:commandButton value="Gerar Relat�rio" action="#{emiteRelatorioHistoricosMBean.gerarRelatorioHistoricoAlteracoesTitulo}" id="acao" />
								<h:commandButton value="Cancelar" action="#{pesquisaTituloCatalograficoMBean.telaPesquisaTitulo}" onclick="#{confirm}" immediate="true" id="cancelar" />
							</td>
						</tr>
					</tfoot>
				</table>
			</h:form>
		</a4j:region>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>