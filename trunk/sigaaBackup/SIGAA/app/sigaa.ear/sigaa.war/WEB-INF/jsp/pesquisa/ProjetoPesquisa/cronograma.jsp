<%@ include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<script type="text/javascript" src="/shared/javascript/ajaxtags/prototype.js"></script>
<script type="text/javascript" src="/shared/javascript/ajaxtags/scriptaculous.js"></script>
<script type="text/javascript" src="${ctx}/javascript/projetos/cronograma.js"></script>

<style>

#container {
	width: 100%;
}
#cabecalho {
	width: 100%;
}
html.background {
	background: #FFF;
}

</style>

<c:choose>
	<c:when test="${ projetoPesquisaForm.renovacao }">
		<h2> <ufrn:subSistema /> &gt; Renovação de Projetos de Pesquisa &gt; Dados da Renovação &gt; Membros &gt; Cronograma </h2>
	</c:when>
	<c:otherwise>
		<h2> <ufrn:steps/> </h2>
	</c:otherwise>
</c:choose>
<ufrn:keepAlive tempo="5"/>

<div class="infoAltRem">
	<a href="#" onclick="javascript:cronograma.adicionarAtividade();"> <html:img src="/sigaa/img/cronograma/adicionar.gif" /> Adicionar Atividade </a>
	<a href="#" onclick="javascript:cronograma.limpar();"> <html:img src="/sigaa/img/cronograma/limpar.gif"/> Limpar Cronograma </a>
	<html:img src="/sigaa/img/cronograma/remover.gif"/>: Remover Atividade
</div>

<html:form action="/pesquisa/projetoPesquisa/criarProjetoPesquisa" method="post" styleId="projetoPesquisaForm">

	<table id="cronograma" class="formulario" width="98%">
	<caption> Cronograma de Atividades </caption>
	<thead>
		<tr>
			<th width="2px;" rowspan="2"> </th>
			<th width="30%" rowspan="2"> Atividade </th>
			<c:forEach items="${projetoPesquisaForm.telaCronograma.mesesAno}" var="ano">
			<th colspan="${fn:length(ano.value)}" align="center" class="inicioAno fimAno">
				${ano.key}
			</th>
			</c:forEach>
			<th width="2%" rowspan="2"> </th>
		</tr>
		<tr>
			<c:forEach items="${projetoPesquisaForm.telaCronograma.mesesAno}" var="ano">
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
		<c:set var="numeroAtividades" value="${fn:length(projetoPesquisaForm.telaCronograma.cronogramas)}" />
		<c:if test="${numeroAtividades == 0}">
			<c:set var="numeroAtividades" value="1"/>
		</c:if>

		<c:set var="valoresCheckboxes" value=",${fn:join(projetoPesquisaForm.telaCronograma.calendario, ',')}" />
		<c:forEach begin="1" end="${numeroAtividades}" varStatus="statusAtividades">
		<tr>
			<td>
				${ statusAtividades.index }
			</td>
			<td>
				<textarea style="width:95%;" rows="2" name="telaCronograma.atividade" tabindex="${statusAtividades.index}">${projetoPesquisaForm.telaCronograma.atividade[statusAtividades.index-1]}</textarea>
			</td>
			<c:forEach items="${projetoPesquisaForm.telaCronograma.mesesAno}" var="ano" varStatus="statusCheckboxes">
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
			<td colspan="${projetoPesquisaForm.telaCronograma.numeroMeses + 3}">
				<c:if test="${ not projetoPesquisaForm.renovacao }">
					<html:button dispatch="gravar" value="Gravar e Continuar"/>
				</c:if>
				<html:button dispatch="telaDocentes">&lt;&lt; Voltar </html:button>
				<html:button dispatch="cancelar" cancelar="true">Cancelar</html:button>
				<html:button dispatch="submeterCronograma">Avançar  &gt;&gt;</html:button>
			</td>
		</tr>
	</tfoot>
	</table>
</html:form>

<script type="text/javascript">
	var cronograma = new Cronograma('projetoPesquisaForm',
		'${projetoPesquisaForm.telaCronograma.mesesString}',
		'${numeroAtividades}'
	);
</script>

<%@ include file="/WEB-INF/jsp/include/rodape.jsp"%>