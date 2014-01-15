<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>

	<h2><ufrn:subSistema /> &gt; Relat�rios de Extens�o</h2>

	<h:outputText value="#{autorizacaoDepartamento.create}"/>
	<h:outputText value="#{atividadeExtensao.create}"/>

	<div class="infoAltRem">
	    <h:graphicImage value="/img/view.gif" style="overflow: visible;" styleClass="noborder" />: Visualizar A��o    
	    <h:graphicImage value="/img/extensao/form_green.png" style="overflow: visible;" styleClass="noborder" />: Visualizar Relat�rio	    
	    <h:graphicImage value="/img/seta.gif" style="overflow: visible;" styleClass="noborder" />: Analisar Relat�rio
	</div>

	<h:form>
		<table class="listagem">
			<caption class="listagem">Relat�rios de A��es de Extens�o (${fn:length(relatorioAcaoExtensao.relatoriosPendentesProex)})</caption>
			<thead>
				<tr>
					<th width="5%" style="text-align:center">C�digo</th>
					<th width="40%">T�tulo da A��o</th>
					<th>Tipo</th>
					<th style="text-align:center">Analisado em</th>
					<th></th>
					<th></th>
					<th></th>
				</tr>
			</thead>

			<c:forEach items="#{relatorioAcaoExtensao.relatoriosPendentesProex}" var="item" varStatus="status">
				<tr class="${ status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
					<td style="text-align:center">${item.atividade.codigo}</td>
					<td>${item.atividade.titulo}</td>
					<td>${item.tipoRelatorio.descricao}</td>
					<td style="text-align:center">
						<fmt:formatDate value="${item.dataValidacaoProex}" pattern="dd/MM/yyyy HH:mm:ss" /> 
						<c:if test="${((item.dataValidacaoProex == null) && (item.dataValidacaoDepartamento != null))}">
							N�O ANALISADO
						</c:if>
					</td>

					<td width="3%">
						<h:commandLink title="Visualizar A��o" action="#{atividadeExtensao.view}" style="border: 0;">
							<f:param name="id" value="#{item.atividade.id}" />
							<h:graphicImage url="/img/view.gif" />
						</h:commandLink>
					</td>

					<td width="3%">
						<h:commandLink title="Visualizar Relat�rio" action="#{relatorioAcaoExtensao.view}" style="border: 0;">
							<f:param name="id" value="#{item.id}" />
							<h:graphicImage url="/img/extensao/form_green.png" />
						</h:commandLink>
					</td>

					<td width="3%">
						<h:commandLink title="Analisar Relat�rio" action="#{relatorioAcaoExtensao.analisarRelatorioProex}"
							style="border: 0;" rendered="#{item.proexPodeAnalisar}">
							<f:param name="id" value="#{item.id}" />
							<h:graphicImage url="/img/seta.gif" />
						</h:commandLink>
					</td>
				</tr>
			</c:forEach>

			<c:if test="${empty relatorioAcaoExtensao.relatoriosPendentesProex}">
				<tr>
					<td colspan="4">
						<center>
							<font color="red">N�o h� relat�rios submetidos para an�lise da PROEx</font>
						</center>
					</td>
				</tr>
			</c:if>
			
		</table>
	</h:form>
	
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>