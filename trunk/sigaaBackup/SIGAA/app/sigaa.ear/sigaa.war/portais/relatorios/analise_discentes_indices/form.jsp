<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<a4j:keepAlive beanName="relatorioAnaliseDiscentesPorIndiceMBean"/>
 	<h2><ufrn:subSistema /> > Análise de Discentes por Índice Acadêmico</h2>

	<h:form id="form">
		<table align="center" class="formulario" width="90%">
			<caption>Dados do Relatório</caption>

			<tr>
				<th width="30%" class="obrigatorio">Ano-Período de Entrada:</th>
				<td>
					<h:inputText value="#{relatorioAnaliseDiscentesPorIndiceMBean.ano}" id="ano" onkeyup="return(formatarInteiro(this))" size="4" maxlength="4" converter="#{ intConverter }" /> .
					<h:inputText value="#{relatorioAnaliseDiscentesPorIndiceMBean.periodo}" id="periodo" onkeyup="return(formatarInteiro(this))" size="1" maxlength="1" converter="#{ intConverter }" />
				</td>
			</tr>
			<tr>
				<th width="30%" class="obrigatorio">Curso:</th>
				<td>
					<h:selectOneMenu value="#{relatorioAnaliseDiscentesPorIndiceMBean.curso.id}" id="curso" style="width:600px;">
						<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
						<f:selectItems value="#{curso.allCursoGraduacaoCombo}" />
					</h:selectOneMenu>
				</td>
			</tr>			
			<tr>
				<th width="30%">Nível Atual:</th>
				<td>					
					<h:inputText value="#{relatorioAnaliseDiscentesPorIndiceMBean.nivel}" id="nivel" onkeyup="return(formatarInteiro(this))" size="4" maxlength="2" converter="#{ intConverter }" />					
				</td>
			</tr>			
			<tr>
				<th width="30%" class="obrigatorio">Índice Acadêmico:</th>
				<td>
					<h:selectOneMenu value="#{relatorioAnaliseDiscentesPorIndiceMBean.indice.id}" id="indice">
						<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
						<f:selectItems value="#{relatorioAnaliseDiscentesPorIndiceMBean.allIndicesGraduacao}" />
					</h:selectOneMenu>
				</td>
			</tr>							
			<tfoot>
				<tr>
					<td colspan="2" align="center">
						<h:commandButton value="Gerar Relatório" id="gerarRelatorio" action="#{relatorioAnaliseDiscentesPorIndiceMBean.gerarRelatorio}" />
						<h:commandButton value="Cancelar" id="cancelar"	action="#{relatorioAnaliseDiscentesPorIndiceMBean.cancelar}" onclick="#{confirm}" immediate="true"/>
					</td>
				</tr>
			</tfoot>
		</table>
	</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
