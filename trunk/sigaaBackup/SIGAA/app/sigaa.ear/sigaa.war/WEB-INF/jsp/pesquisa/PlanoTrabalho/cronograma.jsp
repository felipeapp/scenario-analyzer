<%@ include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<script type="text/javascript" src="/shared/javascript/ajaxtags/prototype.js"></script>
<script type="text/javascript" src="/shared/javascript/ajaxtags/scriptaculous.js"></script>
<script type="text/javascript" src="/shared/javascript/cronograma.js"> </script>

<h2> <ufrn:steps /></h2>

<div class="descricaoOperacao">
	<h3>
		Caro Professor,
	</h3>
	<p style="margin: 10px 20px; text-indent: 20px;">
	 	Informe no cronograma abaixo a(s) atividade(s) realizada(s) nos meses que compõem o 
	 	período do plano de trabalho.
	</p>
</div>

<div class="infoAltRem" style="text-align: center; width: 100%">
	<a href="#" onclick="javascript:cronograma.adicionarAtividade();"> <html:img src="/sigaa/img/cronograma/adicionar.gif" /> Adicionar Atividade </a>
	<a href="#" onclick="javascript:cronograma.limpar();"> <html:img src="/sigaa/img/cronograma/limpar.gif"/> Limpar Cronograma </a>
	<html:img src="/sigaa/img/cronograma/remover.gif"/> Remover Atividade 
</div>

<html:form action="/pesquisa/planoTrabalho/wizard" method="post" styleId="formPlanoTrabalho">

	<table id="cronograma" class="formulario" width="100%">
	<caption> Cronograma de Atividades </caption>
	<thead>
		<tr>
			<th width="30%" rowspan="2"> Atividade </th>
			<c:forEach items="${formPlanoTrabalho.telaCronograma.mesesAno}" var="ano">
			<th colspan="${fn:length(ano.value)}" align="center" class="inicioAno fimAno">
				${ano.key}
			</th>
			</c:forEach>
			<th width="2%" rowspan="2"> </th>
		</tr>
		<tr>
			<c:forEach items="${formPlanoTrabalho.telaCronograma.mesesAno}" var="ano">
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
		<c:set var="numeroAtividades" value="${fn:length(formPlanoTrabalho.telaCronograma.cronogramas)}" />
		<c:if test="${numeroAtividades == 0}">
			<c:set var="numeroAtividades" value="1"/>
		</c:if>

		<c:set var="valoresCheckboxes" value=",${fn:join(formPlanoTrabalho.telaCronograma.calendario, ',')}" />
		<c:forEach begin="1" end="${numeroAtividades}" varStatus="statusAtividades">
		<tr>
			<td>
				<textarea style="width:95%; height: 30px;" name="telaCronograma.atividade" tabindex="${statusAtividades.index}">${formPlanoTrabalho.telaCronograma.atividade[statusAtividades.index-1]}</textarea>
			</td>
			<c:forEach items="${formPlanoTrabalho.telaCronograma.mesesAno}" var="ano" varStatus="statusCheckboxes">
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
			<td colspan="${formPlanoTrabalho.telaCronograma.numeroMeses + 2}">
				<html:button view="dadosGerais">&lt;&lt; Dados Gerais</html:button>
				<html:button dispatch="cancelar" cancelar="true">Cancelar</html:button>
				<html:button dispatch="submeterCronograma">Visualizar Plano de Trabalho &gt;&gt;</html:button>
			</td>
		</tr>
	</tfoot>
	</table>
</html:form>

<script type="text/javascript">
	var cronograma = new Cronograma('formPlanoTrabalho',
		'${formPlanoTrabalho.telaCronograma.mesesString}',
		'${numeroAtividades}'
	);
</script>

<%@ include file="/WEB-INF/jsp/include/rodape.jsp"%>