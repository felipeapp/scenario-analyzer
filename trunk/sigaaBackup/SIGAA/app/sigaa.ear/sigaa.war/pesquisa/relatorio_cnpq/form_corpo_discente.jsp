<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<f:view>
	<h2 class="title"><ufrn:subSistema /> &gt; ${ relatoriosCNPQMBean.nomeRelatorioCabecalho }</h2>

	<h:form id="form">
		<c:choose>
			<c:when test="${ relatoriosCNPQMBean.exibirTipoBolsa }">
				<table class="formulario" width="40%">
			</c:when>
			<c:otherwise>
				<table class="formulario" width="100%">
			</c:otherwise>			
		</c:choose>
		
			<caption class="formulario">Selecione ${ relatoriosCNPQMBean.exibirTipoBolsa ? 'o tipo de bolsa' : 'um edital'}</caption>
			
			<c:if test="${ relatoriosCNPQMBean.exibirTipoBolsa }">
				<tr>
					<th class="obrigatorio" width="60%">Tipo de Relatório:</th>
					<td>
						<h:selectOneMenu id="congresso" value="#{relatoriosCNPQMBean.tipoBolsaPesquisa.id}">
							<f:selectItems value="#{tipoBolsaPesquisa.allBolsasRelatorioCombo}" />
						</h:selectOneMenu>
					</td>
				</tr>
			</c:if>
			
			<c:if test="${ relatoriosCNPQMBean.exibirEditalPesquisa }">
				<tr>
					<th class="obrigatorio" width="15%">Edital de Pesquisa:</th>
					<td>
						<h:selectOneMenu id="editalPesquisa" value="#{relatoriosCNPQMBean.editalPesquisa.id}" style="width: 100%">
							<f:selectItems value="#{editalPesquisaMBean.allCombo}" />
						</h:selectOneMenu>
					</td>
				</tr>
			</c:if>

			<tfoot>
				<tr>
					<td colspan="2">
						<h:commandButton value="Buscar" action="#{ relatoriosCNPQMBean.gerarRelatorio }" /> 
						<h:commandButton value="Cancelar" action="#{ relatoriosCNPQMBean.cancelar }" onclick="#{confirm}"/>
					</td>
				</tr>
			</tfoot>
			
		</table>
	</h:form>
	<div class="obrigatorio"> Campos de preenchimento obrigatório. </div>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>