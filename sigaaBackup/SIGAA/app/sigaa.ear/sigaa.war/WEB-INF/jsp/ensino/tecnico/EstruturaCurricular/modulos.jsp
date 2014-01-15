<%@ include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<h2 class="tituloPagina">
<ufrn:steps /></h2>

<html:form action="/ensino/tecnico/estruturaCurricular/wizard"  method="post"  >
	<html:hidden property="moduloCurricular.estruturaCurricularTecnica.id"
			value="${ estruturaCurTecnicoForm.obj.id }" />
	<table class="formulario" width="90%">
	<caption>Adicionando M�dulos</caption>
	<tbody>
		<tr>
		<th class="obrigatorio">
		M�dulo:
		</th>
		<td>
		<html:select property="moduloCurricular.modulo.id">
		<html:option value="">-- SELECIONE --</html:option>
		<c:forEach items="${modulos}" var="modulo">
			<html:option value="${modulo.id}">${modulo.descricao} - ${modulo.cargaHoraria}hs</html:option>
		</c:forEach>
		</html:select>
		</td>
		<th>
		Per�odo de Oferta:
		</th>
		<td>
		<html:text property="moduloCurricular.periodoOferta" maxlength="2" size="2" onkeyup="formatarInteiro(this)" />
		</td>
		</tr>

	</tbody>

	<tfoot>
		<tr>
		<td colspan="4">
		<html:button dispatch="addModuloCurricular" value="Adicionar"/>
		</td>
		</tr>
	</tfoot>
	</table>
</html:form>

<br>
<c:if test="${ empty estruturaCurTecnicoForm.obj.modulosCurriculares }">
<br><center><i>Nenhum m�dulo adicionado.</i></center>
</c:if>
<c:if test="${ not empty estruturaCurTecnicoForm.obj.modulosCurriculares }">
<table class="listagem">
	<caption class="listagem">M�dulos cadastrados</caption>
	<thead>
	<td width="10%" align="center">C�d.</td>
	<td>Descri��o</td>
	<td width="7%" align="center">C.H.</td>
	<td width="12%" align="center">Pr. Oferta</td>
	<td width="10%" align="center"> </td>
	</thead>

	<tbody>
	<c:forEach items="${estruturaCurTecnicoForm.obj.modulosCurriculares}" var="moduloCurricular">
		<html:form action="/ensino/tecnico/estruturaCurricular/wizard"  method="post"  >
		<html:hidden property="moduloId" value="${moduloCurricular.modulo.id}" />
		<html:hidden property="mcId" value="${moduloCurricular.id}" />
		<tr>
		<td>${moduloCurricular.modulo.codigo}</td>
		<td>${moduloCurricular.modulo.descricao}</td>
		<td align="center">${moduloCurricular.modulo.cargaHoraria}</td>
		<td align="center">${moduloCurricular.periodoOferta}</td>
		<td align="center">
		<html:button dispatch="remModuloCurricular" value="Remover" />
		</td>
		</tr>
		</html:form>
	</c:forEach>

	<tr>
	<td align="right" colspan="2">
	CH dos M�dulos:
	</td>
	<td align="center">
	<c:if test="${ estruturaCurTecnicoForm.obj.cargaHoraria > estruturaCurTecnicoForm.obj.chModulos}">
		<c:set var="cor" value="red" />
	</c:if>
	<span style="color: ${ cor }">
	${ estruturaCurTecnicoForm.obj.chModulos }
	</span>
	</td>
	<td colspan="2"></td>
	</tr>

	<tr>
	<td align="right" colspan="2">
	<b>CH da Estrutura:</b>
	</td>
	<td align="center">
	<b>${ estruturaCurTecnicoForm.obj.cargaHoraria }</b>
	</td>
	<td colspan="2"></td>
	</tr>

</table>
</c:if>

<center>
<br><br>
<html:form action="/ensino/tecnico/estruturaCurricular/wizard"  method="post"  >
	<html:button view="dadosGerais">&lt;&lt; Dados Gerais</html:button>
	<html:button dispatch="cancelar">Cancelar</html:button>
	<html:button dispatch="submeterModulos">Pr�ximo &gt;&gt;</html:button>
</html:form>

<br><br>
<html:img page="/img/required.gif" style="vertical-align: top;"/> <span class="fontePequena"> Campos de preenchimento obrigat�rio. </span>

<br><br>
</center>

<%@ include file="/WEB-INF/jsp/include/rodape.jsp"%>