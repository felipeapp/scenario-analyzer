<%@include file="/ava/cabecalho.jsp"%>
<%@taglib prefix="cewolf" uri="/tags/cewolf" %>

<f:view>
<%@include file="/ava/menu.jsp" %>
<h:form>


<fieldset>
<legend>Acesso Turma Virtual</legend>

<table align="center">
   	<c:if test="${relatorioAcessoTurmaVirtualMBean.quantidadeTotalAcessos > 120}">
	  	<tr>
			<td>Selecione um Mês para detalhar: </td>
   		</tr>
	    <c:set var="_mes" />
	   	<c:forEach items="#{relatorioAcessoTurmaVirtualMBean.logLeituraTurmaVirtual}" var="linha" varStatus="status">
		   	<c:set var="mesAtual" value="${linha.mes}"/>
				  <c:if test="${_mes != mesAtual}">
						<tr>
					   		<td>
					   			<h:commandLink id="nomeMes" action="#{relatorioAcessoTurmaVirtualMBean.detalharMes}" 
					   					value="#{linha.nomeMes}" >
					   					<f:param name="mes" value="#{linha.mes}"/>
					   			</h:commandLink>
					   		</td>
						</tr>
					</c:if>
			<c:set var="_mes" value="${mesAtual}"/>
	   </c:forEach>
   </c:if>
   <c:if test="${relatorioAcessoTurmaVirtualMBean.quantidadeTotalAcessos > 60}">
	 	<tr>
			<td colspan="10" style="text-align: center;">Selecione uma Semana para detalhar: </td>
   		</tr> 
	    <c:set var="_semana" />
	    <c:set var="formatSemana"/>
	  <tr  class="semana">
	   	<td width="145px;" />
	   		<c:forEach items="#{relatorioAcessoTurmaVirtualMBean.logLeituraTurmaVirtual}" var="linha" varStatus="status">
		   		<c:set var="semanaAtual" value="${linha.semana}"/>
					  <c:if test="${_semana != semanaAtual}">
							
							<td width="128px;">
					   			<h:commandLink action="#{relatorioAcessoTurmaVirtualMBean.detalharSemana}"
					   					value="#{linha.nomeSemana}" >
					   			<f:param name="mes" value="#{linha.mes}"/>		
					   			<f:param name="semana" value="#{linha.semana}"/>
					   			</h:commandLink>
					   		</td>
					</c:if>
			<c:set var="_semana" value="${semanaAtual}"/>
	    </c:forEach>
 	  </tr>
   </c:if>
   
</table>
	  	
<jsp:useBean id="dados" class="br.ufrn.sigaa.ava.dominio.GraficoAcessoTurma" scope="page" /> 
<br/>
<center>
<cewolf:chart id="AcessoTurma" type="verticalbar3d" xaxislabel="Período de Acessos" yaxislabel="Número de Acesso"> 
	<cewolf:colorpaint color="#D3E1F1"/> 	
	<cewolf:data> 
		<cewolf:producer id="dados"> 
			<cewolf:param name="lista" value="${relatorioAcessoTurmaVirtualMBean.logLeituraTurmaVirtual}"/>
		</cewolf:producer>
	</cewolf:data> 
</cewolf:chart> 
<cewolf:img chartid="AcessoTurma" renderer="/cewolf" width="1500" height="400"/> 
</center>	

</fieldset>

<br /><br />
	<div class = "voltar">
		<a href="javascript:history.go(-1)"> Voltar </a>
	</div>
	
</h:form>

</f:view>
<%@include file="/ava/rodape.jsp"%>