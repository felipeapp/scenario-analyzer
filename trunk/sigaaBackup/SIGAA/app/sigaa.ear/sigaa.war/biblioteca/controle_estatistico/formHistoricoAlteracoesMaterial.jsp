<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>
<%@ taglib uri="/tags/a4j" prefix="a4j"%>

<f:view>
	<h2><ufrn:subSistema /> &gt; Hist�rico de Altera��es de um Material</h2>
	<br>
	<a4j:region>
		<h:form id="form">

			<div class="descricaoOperacao" style="width: 85%">
				<p>Utilize este formul�rio gerar o relat�rio com o hist�rico de altera��es em um material.</p>
			</div>

			<table class="formulario" width="85%">
			
				<caption>Hist�rico de Altera��es de um Material</caption>
			
				<tr>
					<th class="obrigatorio">C�digo de Barras:</th>
					<td><h:inputText value="#{emiteRelatorioHistoricosMBean.codigoBarras}" maxlength="20" /></td>
				</tr>
				
				<tr>
					<th>Per�odo para a Consulta:</th>
					<td>
							De <t:inputCalendar id="Inicio" value="#{emiteRelatorioHistoricosMBean.dataInicio}" renderAsPopup="true" popupDateFormat="dd/MM/yyyy" onkeypress="return formataData(this,event)" renderPopupButtonAsImage="true" size="10" maxlength="10" />
							At� <t:inputCalendar id="Fim" value="#{emiteRelatorioHistoricosMBean.dataFim}" renderAsPopup="true" popupDateFormat="dd/MM/yyyy" onkeypress="return formataData(this,event)" renderPopupButtonAsImage="true" size="10" maxlength="10" />
					</td>
				</tr>

				<tfoot>
					<tr>
						<td colspan="2" align="center">
							<h:commandButton value="Gerar Relat�rio" action="#{emiteRelatorioHistoricosMBean.gerarRelatorioHistoricoAlteracoesMaterial}" id="acao" />
							<h:commandButton value="Cancelar" action="#{emiteRelatorioHistoricosMBean.cancelar}" onclick="#{confirm}" immediate="true" id="cancelar" />
						</td>
					</tr>
				</tfoot>
			</table>
			<div class="obrigatorio">Campos de preenchimento obrigat�rio.</div>
		</h:form>
	</a4j:region>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>