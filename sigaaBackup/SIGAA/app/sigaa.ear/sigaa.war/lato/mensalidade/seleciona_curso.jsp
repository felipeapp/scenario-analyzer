<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<a4j:keepAlive beanName="relatoriosLato"></a4j:keepAlive>
	<h2><ufrn:subSistema /> > Relat�rio de Mensalidades Pagas </h2>

	<div class="descricaoOperacao">
		<p><b>Caro Usu�rio,</b></p>
		<p>Atrav�s deste formul�rio voc� poder� gerar um relat�rio que lista os discentes de um curso e as mensalidades que foram pagas at� o momento.</p>
	</div>

	<h:form id="form">
		<table class="formulario" width="100%">
			<caption class="listagem">Selecione um Curso</caption>
			<tr>
				<th width="15%" class="obrigatorio">Curso: </th>
				<td>
					<h:selectOneMenu id="curso" value="#{relatoriosLato.idCurso}" style="max-width: 85% !important;">
							<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
							<f:selectItems value="#{relatoriosLato.cursosCombo}"/>
					</h:selectOneMenu>
				</td>
			</tr>
			<tfoot>
				<tr>
					<td colspan="2">
						<h:commandButton id="gerarRelatorio" value="Gerar Relat�rio" action="#{relatoriosLato.gerarRelatorioMensalidadesPagas}" /> 
						<h:commandButton value="Cancelar" action="#{relatoriosLato.cancelar}" id="cancelar" onclick="#{confirm}"/>
					</td>
				</tr>
			</tfoot>
		</table>
		<br />
		<%@include file="/WEB-INF/jsp/include/mensagem_obrigatoriedade.jsp"%>
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>