<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<a4j:keepAlive beanName="consolidarDisciplinaMBean"/>
<h2><ufrn:subSistema/> &gt;Consolidação de Disciplina</h2>

<h:form id="imprimir">
<input type="hidden" name="id" value="${ param['idTurmaSerie'] }"/>
<p align="center" style="margin: 70px 0;">
<span class="title">
<c:if test="${ param['parcial'] == 'true' }">
Consolidação Parcial da Disciplina realizada com Sucesso!
</c:if>
<c:if test="${ param['parcial'] != 'true' }">
Disciplina Consolidada com Sucesso!
</c:if>
</span>
</p>

<table align="center" width="50%">
<tr>
	<td width="50%" align="center" valign="top">
		<h:commandButton action="#{ consolidarDisciplinaMBean.cancelar }" image="/img/consolidacao/nav_right_green.png" 
			alt="Continuar" title="Continuar" immediate="true"/>
	</td>
	<td width="50%" align="center" valign="top">
		<h:commandButton action="#{ consolidarDisciplinaMBean.selecionarTurma }" image="/img/consolidacao/nav_right_green.png" 
			alt="Consolidar Outra Disciplina" title="Consolidar Outra Disciplina" immediate="true"/>
	</td>
</tr>
<tr>
	<td width="50%" align="center" valign="top">
		<h:commandLink action="#{ consolidarDisciplinaMBean.cancelar }" value="Continuar"/>
	</td>
	<td width="50%" align="center" valign="top">
		<h:commandLink action="#{ consolidarDisciplinaMBean.selecionarTurma }" value="Consolidar Outra Disciplina" 
			title="Consolidar Outra Disciplina" immediate="true"/>
	</td>
</tr>
</table>

</h:form>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
