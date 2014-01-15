<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<style>
	table.listagem thead tr th.nota, table.listagem tbody tr td.nota {
		width: 10%;
		text-align: right;
	}

	table.listagem th.cota, table.listagem td.cota {
		text-align: center;
		width: 8%;
	}

	table.listagem td.centro {
		text-align: left;
	}
</style>

<h2>
	<ufrn:subSistema></ufrn:subSistema> &gt;
	Distribuição de Cotas a Docentes
</h2>

<c:set var="edital" value="${formCotaDocente.obj.edital}"/>
<%@include file="/WEB-INF/jsp/pesquisa/EditalPesquisa/info_edital.jsp"%>

<br />
<table class="listagem" style="width: 100%">
<caption class="tituloTabela"> Distribuição de Cotas  </caption>
	<thead>
		<tr>
			<th> </th>
			<th> Docente </th>
			<th class="centro"> Departamento </th>
			<th class="centro" style="padding-left: 10px;"> Centro </th>
			<th class="nota"> FPPI </th>
			<th class="nota"> Projetos </th>
			<th class="nota" style="padding-right: 10px;"> IFC </th>
			<c:forEach var="c" items="${edital.cotas}">
				<th class="cota"> Cotas ${ c.tipoBolsa.descricaoResumida } </th>
			</c:forEach>
		</tr>
	</thead>

	<tbody>
	<c:forEach var="cota" items="${formCotaDocente.cotas}" varStatus="loop">
	<tr class="${ loop.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
		<th> ${loop.index + 1 }.</th>
		<td> ${cota.docente.nome }</td>
		<td class="centro"> ${cota.docente.unidade.siglaAcademica }</td>
		<td class="centro" style="padding-left: 10px;"> ${cota.docente.unidade.gestora.sigla }</td>
		<td class="nota"> <ufrn:format type="valor" name="cota" property="fppi" /></td>
		<td class="nota"> <ufrn:format type="valor" name="cota" property="mediaProjetos" /></td>
		<td class="nota" style="padding-right: 10px;"> <b><ufrn:format type="valor" name="cota" property="ifc" /></b> </td>
		<c:forEach var="c" items="${cota.cotas}">
			<td class="cota"> ${ c.quantidade } </td>
		</c:forEach>
	</tr>
	</c:forEach>
	</tbody>
	<c:if test="${formCotaDocente.geracao}">
		<tfoot>
			<tr>
				<td colspan="${7 + fn:length(edital.cotas)}" style="text-align: center">
					<html:form action="/pesquisa/distribuirCotasDocentes" method="post">
						<html:button dispatch="persistirDistribuicao" value="Finalizar Distribuição"/>
						<html:button dispatch="iniciarDistribuicao" value="<< Alterar Critérios"/>
						<html:button dispatch="cancelar" value="Cancelar"/>
					</html:form>
				</td>
			</tr>
		</tfoot>
	</c:if>
</table>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>