<%@ include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>


<script type="text/javascript" src="/shared/javascript/cronograma.js"> </script>

<f:view>

<%@include file="/portais/docente/menu_docente.jsp"%>
<h2><ufrn:subSistema /> > Cadastro do Cronograma do Plano de Trabalho </h2>

	<div class="infoAltRem">
		 <h:graphicImage value="/img/cronograma/remover.gif" style="overflow: visible;"/>: Remover Atividade
		 <a href="#" onclick="javascript:cronograma.adicionarAtividade();"> <html:img src="/sigaa/img/cronograma/adicionar.gif" /> Adicionar Atividade </a>
		 <a href="#" onclick="javascript:cronograma.limpar();"> <html:img src="/sigaa/img/cronograma/limpar.gif"/> Limpar Cronograma </a>	    		    
	</div>





<h:form id="formPlanoTrabalho">

	<table id="cronograma" class="formulario" width="100%">
	<caption> Cronograma de Atividades </caption>
	<thead>
		<tr>
			<th width="30%" rowspan="2"> Atividades desenvolvidas </th>
			<c:forEach items="${planoTrabalhoProjeto.telaCronograma.mesesAno}" var="ano">
			<th colspan="${fn:length(ano.value)}" align="center" class="inicioAno fimAno">
				${ano.key}
			</th>
			</c:forEach>
			<th width="2%" rowspan="2"> </th>
		</tr>
		<tr>
			<c:forEach items="${planoTrabalhoProjeto.telaCronograma.mesesAno}" var="ano">
				<c:forEach items="${ano.value}" var="mes" varStatus="status">
				<c:set var="classeCabecalho" value=""/>
				<c:if test="${status.first}"> <c:set var="classeCabecalho" value="inicioAno"/> </c:if>
				<c:if test="${status.last}"> <c:set var="classeCabecalho" value="fimAno"/> </c:if>

				<th class="${classeCabecalho}"> ${mes}	</th>
				</c:forEach>
			</c:forEach>
		</tr>
	</thead>
	<tbody>
		<c:set var="numeroAtividades" value="${fn:length(planoTrabalhoExtensao.telaCronograma.cronogramas)}" />
		<c:if test="${numeroAtividades == 0}">
			<c:set var="numeroAtividades" value="1"/>
		</c:if>

		<c:set var="valoresCheckboxes" value=",${fn:join(planoTrabalhoProjeto.telaCronograma.calendario, ',')}" />
		<c:forEach begin="1" end="${numeroAtividades}" varStatus="statusAtividades">
		<tr>
			<td>
				<textarea style="width:95%; height: 30px;" name="telaCronograma.atividade" tabindex="${statusAtividades.index}">${planoTrabalhoProjeto.telaCronograma.atividade[statusAtividades.index-1]}</textarea>
			</td>
			<c:forEach items="${planoTrabalhoProjeto.telaCronograma.mesesAno}" var="ano" varStatus="statusCheckboxes">
				<c:forEach items="${ano.value}" var="mes">
					<c:set var="valorCheckbox" value=",${statusAtividades.index-1}_${mes}_${ano.key}" />
					<td align="center">
						<input type="checkbox"
							class="noborder"
							name="telaCronograma.calendario"
							value="${valorCheckbox}"
							<c:if test="${fn:contains(valoresCheckboxes, valorCheckbox)}"> checked="checked" </c:if>
						/>
					</td>
				</c:forEach>
			</c:forEach>
			<td align="right">
				<a href="#" onclick="javascript:cronograma.removerAtividade(this);">
					<img src="/sigaa/img/cronograma/remover.gif" alt="Remover Atividade" title="Remover Atividade"/>
				</a>
			</td>
		</tr>
		</c:forEach>
	</tbody>

	<tfoot>
		<tr>
			<td colspan="${planoTrabalhoProjeto.telaCronograma.numeroMeses + 2}">
					<h:commandButton value="<< Voltar" action="#{planoTrabalhoProjeto.irDadosGerais}"  id="voltarDadosGerais"/>
					<h:commandButton value="Cancelar" action="#{planoTrabalhoProjeto.cancelar}" onclick="#{confirm }" id="cancelar"/>
					<h:commandButton value="Resumo >>" action="#{planoTrabalhoProjeto.submeterCronograma}"  id="cadastrarPlano"/> 
			</td>
		</tr>
	</tfoot>
	
	</table>
</h:form>

</f:view>
<script type="text/javascript">
	var cronograma = new Cronograma('formPlanoTrabalho',
		'${planoTrabalhoProjeto.telaCronograma.mesesString}',
		'${numeroAtividades}'
	);
</script>

<%@ include file="/WEB-INF/jsp/include/rodape.jsp"%>