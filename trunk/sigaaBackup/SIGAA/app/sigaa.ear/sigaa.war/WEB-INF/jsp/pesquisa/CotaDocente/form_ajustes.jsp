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

	table.listagem td.cota input{
		font-size: 1.2em;
		text-align: center;
	}

	table.listagem td.centro {
		text-align: center;
	}

	#novoDocente {
		background: #F5F5F5;
		border: 1px solid #DEDFE3;
		margin: 0 auto 10px;
		width: 85%;
	}

	#novoDocente td{
		padding: 3px;
	}
</style>

<h2>
	<ufrn:subSistema></ufrn:subSistema> &gt;
	Ajustes na Distribuição de Cotas a Docentes
</h2>

<html:form action="/pesquisa/distribuirCotasDocentes" method="post">

<c:set var="edital" value="${formCotaDocente.obj.edital}"/>

<%@include file="/WEB-INF/jsp/pesquisa/EditalPesquisa/info_edital.jsp"%>

<c:if test="${ formCotaDocente.centro.id > 0 }">
	<h3 style="text-align: center; padding: 15px 5px 7px; font-variant: small-caps;">
		DISTRIBUIÇÃO DE COTAS PARA O(A) ${ formCotaDocente.centro.sigla }
		<small>(PIBIC: ${ totalPibic }, Propesq: ${ totalPropesq })</small>
	</h3>
</c:if>

<table id="novoDocente" class="subFormulario">
	<caption style="text-align: center">Adicionar um novo docente</caption>
	
	<tbody>
	<tr>
		<td  valign="bottom" style="text-align: center">
			<c:set var="idAjax" value="obj.docente.id"/>
			<c:set var="nomeAjax" value="obj.docente.pessoa.nome"/>
			<%@include file="/WEB-INF/jsp/include/ajax/docente.jsp" %>
		</td>
	</tr>
	
	</tbody>
	<tfoot>
	<tr>
		<td align="center">
			<html:button dispatch="adicionarDocente" value="Adicionar à Lista"/>
		</td>
	</tr>
	</tfoot>
</table>


<table class="listagem" style="width: 95%">
	<caption> Distribuição de Cotas </caption>

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
			<td class="cota"><html:text property="cota(${c.tipoBolsa.id}${cota.docente.id})" size="3"/></td>
		</c:forEach>
	</tr>
	</c:forEach>
	</tbody>

	<tfoot>
		<tr>
			<td colspan="${7 + fn:length(edital.cotas)}" style="text-align: center">
				<html:button dispatch="gravarAjustes" value="Salvar Ajustes"/>
				<html:button dispatch="cancelar" value="Cancelar"/>
			</td>
		</tr>
	</tfoot>
</table>
</html:form>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>