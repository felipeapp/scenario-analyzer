<%@include file="/ava/cabecalho.jsp"%>
<%@taglib prefix="cewolf" uri="/tags/cewolf" %>
<f:view>
<%@include file="/ava/menu.jsp" %>
<h:form>


<fieldset>
<legend>Estatísticas da Turma</legend>

<jsp:useBean id="dados" class="br.ufrn.sigaa.ava.jsf.EstatisticasTurma" scope="page" /> 
<br/>
<center>
<cewolf:chart id="EstatisticasTurma" type="pie"> 
	<cewolf:colorpaint color="#D3E1F1"/> 
	<cewolf:data> 
		<cewolf:producer id="dados"> 
			<cewolf:param name="idTurma" value="${ turmaVirtual.turma.id }"/>
		</cewolf:producer>
	</cewolf:data> 
</cewolf:chart> 
<cewolf:img chartid="EstatisticasTurma" renderer="/cewolf" width="650" height="400"/> 
</center>

</fieldset>

</h:form>

</f:view>
<%@include file="/ava/rodape.jsp"%>