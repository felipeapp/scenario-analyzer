<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<h2 class="tituloPagina">
<ufrn:steps/>
</h2>

<html:form action="/ensino/tecnico/criarQualificacao" method="post">
<center>
<br>
<table class="formulario" cellpadding="3" width="550">
<caption>Dados da Qualifica��o</caption>
<tr>
	<th>
	<b>Curso:</b>
	</th>
	<td>
	${qualificacaoForm.obj.cursoTecnico.nome}
	</td>
</tr>
<tr>
	<th>
	<b>Descri��o:</b>
	</th>
	<td>
	${qualificacaoForm.obj.descricao}
	</td>
</tr>
<tr>
	<th>
	<b>Habilita��o:</b>
	</th>
	<td>
	<c:if test="${qualificacaoForm.obj.habilitacao}">Sim</c:if>
	<c:if test="${not qualificacaoForm.obj.habilitacao}">N�o</c:if>
	</td>
</tr>
<tr>
	<th valign="top">
	<b>M�dulos:</b>
	</th>
	<td>
		<c:forEach items="${qualificacaoForm.obj.modulos}" var="modulo">
			${modulo.fullDesc}<br>
		</c:forEach>
		<br><br>
	</td>
</tr>
<tfoot>
<tr>
	<td colspan="2">
	<c:if test="${ param['dispatch'] == 'view' }">
	<html:button dispatch="cancelar" value="Cancelar"/>
	</c:if>
	<c:if test="${ param['dispatch'] == 'resumo' }">
		<html:button dispatch="chamaModelo" value="Confirmar"/>
		&nbsp;&nbsp;
		<html:button dispatch="cancelar" value="Cancelar"/><br>
  		<br>
  		<html:button view="modulos" value="<< Adicionar M�dulos"/>
		<html:button view="dadosBasicos" value="<< << Dados B�sicos da Qualifica��o"/>
	</c:if>
	<c:if test="${ param['dispatch'] == 'prepararRemover' }">
		<html:button dispatch="remover" value="Confirmar Remo��o da Qualifica��o"/>
		&nbsp;&nbsp;
		<html:button dispatch="cancelar" value="Cancelar"/><br>
	</c:if>
	</td>
</tr>
</tfoot>
</table>
</html:form>

<br><br>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
