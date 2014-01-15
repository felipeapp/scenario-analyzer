<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>
<%@ taglib uri="/tags/a4j" prefix="a4j"%>

<f:view>

		<h2><ufrn:subSistema /> &gt; Histórico de Empréstimos de um Material</h2>
		
		<a4j:keepAlive beanName="emiteRelatorioHistoricosMBean"></a4j:keepAlive>
		
		<a4j:region>
			<h:form id="form">

				<div class="descricaoOperacao" style="width: 85%">
					<p> Caro usuário, </p>
					<p> Por padrão os campos de data vêm preenchidos para recuperar os empréstimos do material realizados no último ano.</p>
					<p> Se desejar trazer todos os empréstimos de um material a partir de uma determinada data, deixe o campo "<i>Até</i>" em branco. Caso
				queira recuperar todos os empréstimos até uma determinada data, deixe o campo "<i>De</i>" em banco. </p>
					<p> <strong>Para recuperar o histórico completo de empréstimos de um material, não preencha os campos de data. </strong> </p>
				</div>

				<div style="height: 20px; color: #FF0000; font-weight: bold; text-align: center;">
					<h:outputText id="mensagemErro" value="#{emiteRelatorioHistoricosMBean.mensagemErroAjax}" />
				</div>

				<table class="formulario" width="85%">
					<caption>Histórico de Empréstimos de um Material</caption>
					<tr>
						<th class="obrigatorio">Código de Barras:</th>
						<td>
							<h:inputText id="campoCB" value="#{emiteRelatorioHistoricosMBean.codigoBarras}" />
							<a4j:commandButton style="margin-left:10px;" title="Buscar Material" actionListener="#{emiteRelatorioHistoricosMBean.consultarCodigoBarras}" reRender="form" value="Buscar Material" oncomplete="document.getElementById('form:campoCB').value = ''; document.getElementById('form:campoCB').focus();" />
							<a4j:status>
								<f:facet name="start">
									<h:graphicImage value="/img/indicator.gif" />
								</f:facet>
							</a4j:status>
						</td>
					</tr>
					
					<tr>
						<td colspan="2" align="center"><h:outputText value="#{emiteRelatorioHistoricosMBean.materialInformacional.informacao}" /></td>
					</tr>
					
					<tr>
						<th>Período do Empréstimo:</th>
						<td>
							De <t:inputCalendar id="Inicio" value="#{emiteRelatorioHistoricosMBean.dataInicio}" renderAsPopup="true" popupDateFormat="dd/MM/yyyy" onkeypress="return formataData(this,event)" renderPopupButtonAsImage="true" size="10" maxlength="10" />
							Até <t:inputCalendar id="Fim" value="#{emiteRelatorioHistoricosMBean.dataFim}" renderAsPopup="true" popupDateFormat="dd/MM/yyyy" onkeypress="return formataData(this,event)" renderPopupButtonAsImage="true" size="10" maxlength="10" />
						</td>
					</tr>

					<tfoot>
						<tr>
							<td colspan="2" align="center">
								<h:commandButton value="Gerar Relatório" action="#{emiteRelatorioHistoricosMBean.gerarRelatorioEmprestimos}" id="acao" />
								<h:commandButton value="Cancelar" action="#{emiteRelatorioHistoricosMBean.cancelar}" onclick="#{confirm}" immediate="true" id="cancelar" />
							</td>
						</tr>
					</tfoot>
				</table>
				
				<%@include file="/WEB-INF/jsp/include/mensagem_obrigatoriedade.jsp" %>
			</h:form>
		</a4j:region>


</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>