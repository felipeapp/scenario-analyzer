<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<%@include file="/stricto/menu_coordenador.jsp" %>
<h2><ufrn:subSistema /> > Alterar Limites de Orientandos de Docentes do Programa</h2>

<div class="descricaoOperacao">
	<p>Caro Usu�rio,</p>
	<p>	Esta opera��o destina-se alterar o n�mero m�ximo de orientandos dos docentes de p�s-gradua��o.
	Abaixo encontra-se a lista dos docentes. Entre com o n�mero m�ximo de orientandos de cada docente. </p>
	<p>	Se o campo tiver o valor 0 (zero) indica que o docente n�o pode ter orientando. </p>
	<p>	Se o campo n�o tiver nenhum valor indica que n�o h� limite de orientando para este docente. </p>
</div>

<h:form>
<h:messages showDetail="true"/>


<center>
	<div class="infoAltRem">
		MORM: M�ximo de Orientandos Regulares Mestrado&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		MORD: M�ximo de Orientandos Regulares Doutorado
		<br/>
		MOEM: M�ximo de Orientandos Especiais Mestrado&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		MOED: M�ximo de Orientandos Especiais Doutorado		
	</div>
</center>

<t:dataTable var="equipe" value="#{equipePrograma.equipeDoPrograma}" styleClass="listagem" rowClasses="linhaPar,linhaImpar">
	<f:facet name="caption">
	<f:verbatim>Docentes do Programa ${fn:length(equipePrograma.equipeDoPrograma)} docentes</f:verbatim></f:facet>

	<h:column>
		<f:facet name="header">
			<f:verbatim>Docente</f:verbatim>
		</f:facet>
		<h:outputText value="#{equipe.nome}"/>
	</h:column>
	<h:column>
		<f:facet name="header">
			<f:verbatim>V�nculo</f:verbatim>
		</f:facet>
		<h:outputText value="#{equipe.vinculo}"/>
	</h:column>
	<h:column>
		<f:facet name="header">
			<f:verbatim>N�vel</f:verbatim>
		</f:facet>
		<h:outputText value="#{equipe.nivel}"/>
	</h:column>

	<h:column>
		<f:facet name="header">
			<f:verbatim>Mest.</f:verbatim>
		</f:facet>
		<h:outputText value="#{equipe.mestrado ? 'Sim' : 'N�o'}"/>
	</h:column>

	<h:column>
		<f:facet name="header">
			<f:verbatim>Dout.</f:verbatim>
		</f:facet>
		<h:outputText value="#{equipe.doutorado ? 'Sim' : 'N�o'}"/>
	</h:column>

	
	<h:column>
		<f:facet name="header">
			<f:verbatim>MORM</f:verbatim>
		</f:facet>
		<h:inputText value="#{equipe.maxOrientandoRegularMestrado}" size="2" maxlength="2" onkeyup="return formatarInteiro(this);" id="maxOrientandoRegularMestrado"/>
	</h:column>
	<h:column>
		<f:facet name="header">
			<f:verbatim>MORD</f:verbatim>
		</f:facet>
		<h:inputText value="#{equipe.maxOrientandoRegularDoutorado}" size="2" maxlength="2" onkeyup="return formatarInteiro(this);" id="maxOrientandoRegularDoutorado"/>
	</h:column>
	<h:column>
		<f:facet name="header">
			<f:verbatim>MOEM</f:verbatim>
		</f:facet>
		<h:inputText value="#{equipe.maxOrientandoEspecialMestrado}" maxlength="2" size="2" onkeyup="return formatarInteiro(this);" id="maxOrientandoEspecialMestrado"/>
	</h:column>
	<h:column>
		<f:facet name="header">
			<f:verbatim>MOED</f:verbatim>
		</f:facet>
		<h:inputText value="#{equipe.maxOrientandoEspecialDoutorado}" maxlength="2" size="2" onkeyup="return formatarInteiro(this);" id="maxOrientandoEspecialDoutorado"/>
	</h:column>	
</t:dataTable>

<br/>
<p align="center">
	<h:commandButton value="Gravar Limites" action="#{equipePrograma.cadastrarLimitesOrientandos}" id="botaoGravarLimites"/>
</p>
</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>